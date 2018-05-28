package com.electivechaos.claimsadjuster.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;

import com.electivechaos.claimsadjuster.R;
import com.electivechaos.claimsadjuster.adapters.PlaceArrayAdapter;
import com.electivechaos.claimsadjuster.interfaces.LossLocationDataInterface;
import com.electivechaos.claimsadjuster.utils.CommonUtils;
import com.electivechaos.claimsadjuster.utils.PermissionUtilities;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;

/**
 * Created by krishna on 2/26/18.
 */

public class LossLocationFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private MapView mMapView;
    private GoogleMap googleMap;
    private View  lossLocationParentLayout;


    private PlaceDetectionClient mPlaceDetectionClient;


    private GoogleApiClient mGoogleApiClient;
    private PlaceArrayAdapter mPlaceArrayAdapter;
    private AutoCompleteTextView mAutocompleteTextView;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));
    private LossLocationDataInterface lossLocationDataInterface;

    private String locationLat = "";
    private String locationLong = "";
    private String addressLine = "";

    private MarkerOptions a = new MarkerOptions().position(new LatLng(50,6));
    private Marker mGoogleMapMarker = null;

    @Override
    public void onStart() {
        super.onStart();

        if(mGoogleApiClient != null && !mGoogleApiClient.isConnected()){
            mGoogleApiClient.connect();
        }
    }

    private synchronized void buildGoogleAPIClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Places.GEO_DATA_API)
                .build();
        mGoogleApiClient.connect();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();

        if(bundle != null){
            locationLat = bundle.get("locationLat").toString();
            locationLong = bundle.get("locationLong").toString();
            addressLine = bundle.get("addressLine").toString();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.loss_location_fragment, container, false);
        mMapView = rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        lossLocationParentLayout = rootView.findViewById(R.id.lossLocationParentLayout);
        mMapView.onResume();

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                mGoogleMapMarker = googleMap.addMarker(a);

                boolean result= PermissionUtilities.checkPermission(getActivity(),LossLocationFragment.this,PermissionUtilities.MY_APP_LOCATION_PERMISSIONS);
                if(result){
                buildGoogleAPIClient();


                mPlaceDetectionClient = Places.getPlaceDetectionClient(getActivity());


                showCurrentPlace();

                }else{
                    PermissionUtilities.checkPermission(getActivity(),LossLocationFragment.this,PermissionUtilities.MY_APP_LOCATION_PERMISSIONS);
                }

            }
        });



        mAutocompleteTextView = rootView.findViewById(R.id.place_autocomplete_text_view);
        mAutocompleteTextView.setText(addressLine);
        mAutocompleteTextView.setThreshold(2);
        mAutocompleteTextView.setOnItemClickListener(mAutocompleteClickListener);
        mPlaceArrayAdapter = new PlaceArrayAdapter(getActivity(), R.layout.places_autocomplete_item, BOUNDS_MOUNTAIN_VIEW, null);
        mAutocompleteTextView.setAdapter(mPlaceArrayAdapter);

        mAutocompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                lossLocationDataInterface.setLocationLat("");
                lossLocationDataInterface.setLocationLong("");
                lossLocationDataInterface.setAddressLine(s.toString());
            }
        });


        return rootView;
    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                return;
            }

            LatLng currentLocation = places.get(0).getLatLng();

            lossLocationDataInterface.setLocationLat(String.valueOf(currentLocation.latitude));

            lossLocationDataInterface.setLocationLong(String.valueOf(currentLocation.longitude));

            lossLocationDataInterface.setAddressLine(places.get(0).getAddress().toString());

            mGoogleMapMarker.setPosition(currentLocation);
            mGoogleMapMarker.setTitle("Location");
            mGoogleMapMarker.setSnippet(places.get(0).getAddress().toString());

            CameraPosition cameraPosition = new CameraPosition.Builder().target(currentLocation).zoom(20).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    googleMap.snapshot(new GoogleMap.SnapshotReadyCallback() {
                        @Override
                        public void onSnapshotReady(Bitmap bitmap) {
                            lossLocationDataInterface.setMapSnapshot(bitmap);
                            googleMap.setOnMapLoadedCallback(null);
                        }
                    });
                }
            });
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);


        }
    };


    /**
     * Prompts the user for permission to use the device location.
     */

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PermissionUtilities.MY_APP_LOCATION_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    buildGoogleAPIClient();
                    mPlaceDetectionClient = Places.getPlaceDetectionClient(getActivity());
                    showCurrentPlace();
                }
            }
        }

    }



    private void showCurrentPlace() {

        if (googleMap == null) {
            return;
        }

        if(locationLat.isEmpty()){
            @SuppressLint("MissingPermission") final Task<PlaceLikelihoodBufferResponse> placeResult =
                    mPlaceDetectionClient.getCurrentPlace(null);
            placeResult.addOnCompleteListener
                    (new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
                        @Override
                        public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();

                                LatLng currentLocation = likelyPlaces.get(0).getPlace().getLatLng();

                                lossLocationDataInterface.setLocationLat(String.valueOf(currentLocation.latitude));

                                lossLocationDataInterface.setLocationLong(String.valueOf(currentLocation.longitude));

                                lossLocationDataInterface.setAddressLine(likelyPlaces.get(0).getPlace().getAddress().toString());
                                mAutocompleteTextView.setText(likelyPlaces.get(0).getPlace().getAddress().toString());
                                mGoogleMapMarker.setPosition(currentLocation);
                                mGoogleMapMarker.setTitle("Location");
                                mGoogleMapMarker.setSnippet(likelyPlaces.get(0).getPlace().getAddress().toString());

                                CameraPosition cameraPosition = new CameraPosition.Builder().target(currentLocation).zoom(20).build();
                                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                                likelyPlaces.release();

                                googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                                    @Override
                                    public void onMapLoaded() {
                                        googleMap.snapshot(new GoogleMap.SnapshotReadyCallback() {
                                            @Override
                                            public void onSnapshotReady(Bitmap bitmap) {
                                                lossLocationDataInterface.setMapSnapshot(bitmap);
                                                googleMap.setOnMapLoadedCallback(null);
                                            }
                                        });
                                    }
                                });


                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                }
            });
        }else{
            LatLng currentLocation = new LatLng(Double.parseDouble(locationLat), Double.parseDouble(locationLong));
            mGoogleMapMarker.setPosition(currentLocation);
            mGoogleMapMarker.setTitle("Location");
            mGoogleMapMarker.setSnippet(addressLine);

            CameraPosition cameraPosition = new CameraPosition.Builder().target(currentLocation).zoom(20).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);

        }


    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        CommonUtils.showSnackbarMessage("Failed to connect to google api.", true, true,lossLocationParentLayout,getActivity());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(mGoogleApiClient != null){
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mGoogleApiClient != null){
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
        if(mGoogleApiClient != null){
            mGoogleApiClient.disconnect();
        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        if(mGoogleApiClient != null){
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mPlaceArrayAdapter.setGoogleApiClient(null);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
            try {
                lossLocationDataInterface = (LossLocationDataInterface)getActivity();
            }catch (ClassCastException exception){
                exception.printStackTrace();
            }

    }
}
