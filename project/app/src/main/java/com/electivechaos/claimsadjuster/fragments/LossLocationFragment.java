package com.electivechaos.claimsadjuster.fragments;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.electivechaos.claimsadjuster.Constants;
import com.electivechaos.claimsadjuster.R;
import com.electivechaos.claimsadjuster.adapters.PlaceArrayAdapter;
import com.electivechaos.claimsadjuster.application.App;
import com.electivechaos.claimsadjuster.interfaces.LossLocationDataInterface;
import com.electivechaos.claimsadjuster.utils.CommonUtils;
import com.electivechaos.claimsadjuster.utils.PermissionUtilities;
import com.electivechaos.claimsadjuster.utils.SingleShotLocationProvider;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by krishna on 2/26/18.
 */

public class LossLocationFragment extends Fragment implements OnMapReadyCallback {

    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(new LatLng(37.398160, -122.180831), new LatLng(37.430620, -121.972090));
    private GoogleMap googleMap;
    private View lossLocationParentLayout;
    private PlaceArrayAdapter mPlaceArrayAdapter;
    private AutoCompleteTextView mAutocompleteTextView;
    private LossLocationDataInterface lossLocationDataInterface;
    private String locationLat = "";
    private String locationLong = "";
    private String addressLine = "";
    private MarkerOptions a = new MarkerOptions().position(new LatLng(50, 6));
    private Marker mGoogleMapMarker = null;
    private CheckBox txtCurrentLocation, txtSetLocation;
    private float zoom = 15f;
    private SupportMapFragment mMapFragment;
    private AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    setAddress(placeId);
                }
            }).start();

        }
    };

    private void setAddress(String placeId) {
        // Specify the fields to return (in this example all fields are returned).
        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
        FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);
        Task<FetchPlaceResponse> placeTask = Places.createClient(getActivity()).fetchPlace(request);

        try {
            Tasks.await(placeTask, 60, TimeUnit.SECONDS);
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            e.printStackTrace();
        }


        placeTask.addOnSuccessListener(
                (response) -> {
                    String addressEntered = response.getPlace().getAddress();
                    LatLng latLng = response.getPlace().getLatLng();
                    getCurrentAddress(latLng);
                    mAutocompleteTextView.setText(addressEntered);
                    mAutocompleteTextView.dismissDropDown();
                });

        placeTask.addOnFailureListener(
                (exception) -> {
                    exception.printStackTrace();
                    Snackbar.make(lossLocationParentLayout, "Something went wrong", Snackbar.LENGTH_LONG).show();
                });


    }


    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            locationLat = bundle.get("locationLat").toString();
            locationLong = bundle.get("locationLong").toString();
            addressLine = bundle.get("addressLine").toString();
        }


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.loss_location_fragment, container, false);
        lossLocationParentLayout = rootView.findViewById(R.id.lossLocationParentLayout);

        mMapFragment= (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if(mMapFragment != null)
        mMapFragment.getMapAsync(this);

        //Initialize places
        if (!Places.isInitialized()) {
            Places.initialize(App.getContext(), getString(R.string.api_key));
        }
        mMapFragment.onResume();
        mMapFragment.onCreate(savedInstanceState);

        txtCurrentLocation = rootView.findViewById(R.id.txtCurrentLocation);
        txtSetLocation = rootView.findViewById(R.id.txtSetLocation);

        mAutocompleteTextView = rootView.findViewById(R.id.place_autocomplete_text_view);
        mAutocompleteTextView.setText(addressLine);
        mAutocompleteTextView.setThreshold(2);
        mAutocompleteTextView.setOnItemClickListener(mAutocompleteClickListener);
        mPlaceArrayAdapter = new PlaceArrayAdapter(getActivity(), R.layout.places_autocomplete_item, BOUNDS_MOUNTAIN_VIEW);
        mAutocompleteTextView.setAdapter(mPlaceArrayAdapter);

        if (TextUtils.isEmpty(addressLine)) {
            txtSetLocation.setText("Pin Location");
        }

        txtCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap mMap) {
                        googleMap = mMap;

                        if (CommonUtils.getGoogleMap(getActivity()).equalsIgnoreCase(Constants.MAP_TYPE_ID_ROADMAP)) {
                            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        } else if (CommonUtils.getGoogleMap(getActivity()).equalsIgnoreCase(Constants.MAP_TYPE_ID_SATELLITE)) {
                            googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                        }
                        googleMap.clear();
                        mGoogleMapMarker = googleMap.addMarker(a);

                        boolean result = PermissionUtilities.checkPermission(getActivity(), LossLocationFragment.this, PermissionUtilities.MY_APP_LOCATION_PERMISSIONS);
                        if (result) {
                            setCurrentLocation();
                        } else {
                            PermissionUtilities.checkPermission(getActivity(), LossLocationFragment.this, PermissionUtilities.MY_APP_LOCATION_PERMISSIONS);
                        }

                    }
                });
            }
        });


        txtSetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtSetLocation.isChecked()) {
                    lossLocationDataInterface.setLocationLat(locationLat);
                    lossLocationDataInterface.setLocationLong(locationLong);
                    lossLocationDataInterface.setAddressLine(addressLine);
                    txtSetLocation.setText("Unpin Location");
                } else {
                    lossLocationDataInterface.setLocationLat("");
                    lossLocationDataInterface.setLocationLong("");
                    lossLocationDataInterface.setAddressLine("");
                    txtSetLocation.setText("Pin Location");
                }

            }
        });


        return rootView;
    }

    private void getGPSLOcation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            SingleShotLocationProvider.requestSingleUpdate(App.getContext(), LossLocationFragment.this,
                    location -> {
                        LatLng currentLocation = new LatLng(location.latitude, location.longitude);
                        Log.d("Location", "my location is " + location.toString());
                        if (googleMap != null) {
                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLocation, zoom);
                            googleMap.animateCamera(cameraUpdate);

                            locationLat = String.valueOf(currentLocation.latitude);
                            locationLong = String.valueOf(currentLocation.longitude);

                            getCurrentAddress(currentLocation);

                            CameraPosition cameraPosition = new CameraPosition.Builder().target(currentLocation).zoom(15).build();
                            if (cameraPosition != null) {
                                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                            }

                            googleMap.getUiSettings().setMyLocationButtonEnabled(false);

                            googleMap.setOnMapLoadedCallback(() -> googleMap.snapshot(bitmap -> {
                                lossLocationDataInterface.setMapSnapshot(bitmap);
                                googleMap.setOnMapLoadedCallback(null);
                            }));
                        }

                        lossLocationDataInterface.setLocationLat(locationLat);
                        lossLocationDataInterface.setLocationLong(locationLong);
                        lossLocationDataInterface.setAddressLine(addressLine);

                        txtSetLocation.setChecked(false);
                        txtSetLocation.setText("Pin Location");
                    });
        }
    }

    public void getCurrentAddress(LatLng currentLatLng) {
        if (currentLatLng != null) {
            Geocoder geocoder;
            List<Address> addresses = null;
            geocoder = new Geocoder(App.getContext(), Locale.getDefault());
            try {
                addresses = geocoder.getFromLocation(currentLatLng.latitude, currentLatLng.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                if (addresses != null && addresses.size() > 0) {
                    String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    mAutocompleteTextView.setText(address);
                    addressLine = address;
                    mGoogleMapMarker.setPosition(currentLatLng);
                    mGoogleMapMarker.setTitle("Location");
                    mGoogleMapMarker.setSnippet(address);
                    locationLat = String.valueOf(currentLatLng.latitude);
                    locationLong = String.valueOf(currentLatLng.longitude);
                    if (googleMap != null) {
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, zoom));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, zoom));
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
                    lossLocationDataInterface.setLocationLat(locationLat);
                    lossLocationDataInterface.setLocationLong(locationLong);
                    lossLocationDataInterface.setAddressLine(addressLine);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Prompts the user for permission to use the device location.
     */

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PermissionUtilities.MY_APP_LOCATION_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showCurrentPlace();
                }
                break;
            }
            default:
        }

    }

    private void setCurrentLocation() {
        if (googleMap == null) {
            return;
        }
        getGPSLOcation();
    }

    private void showCurrentPlace() {
        if (googleMap == null) {
            return;
        }

        if (locationLat.isEmpty()) {
            getGPSLOcation();
        } else {
            LatLng latLng = new LatLng(Double.parseDouble(locationLat), Double.parseDouble(locationLong));
            mGoogleMapMarker.setPosition(latLng);
            mGoogleMapMarker.setTitle("Location");
            mGoogleMapMarker.setSnippet(addressLine);

            CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(15).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
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

            lossLocationDataInterface.setLocationLat(locationLat);
            lossLocationDataInterface.setLocationLong(locationLong);
            lossLocationDataInterface.setAddressLine(addressLine);
            txtSetLocation.setChecked(true);

        }


    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapFragment.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
        mMapFragment.onPause();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapFragment.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapFragment.onLowMemory();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            lossLocationDataInterface = (LossLocationDataInterface) getActivity();
        } catch (ClassCastException exception) {
            exception.printStackTrace();
        }

    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        googleMap.addMarker(new MarkerOptions()
                .position(sydney)
                .title("Marker in Sydney"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        if (CommonUtils.getGoogleMap(getActivity()).equalsIgnoreCase(Constants.MAP_TYPE_ID_ROADMAP)) {
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        } else if (CommonUtils.getGoogleMap(getActivity()).equalsIgnoreCase(Constants.MAP_TYPE_ID_SATELLITE)) {
            googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        }

        googleMap.clear();
        mGoogleMapMarker = googleMap.addMarker(a);

        boolean result = PermissionUtilities.checkPermission(getActivity(), LossLocationFragment.this, PermissionUtilities.MY_APP_LOCATION_PERMISSIONS);
        if (result) {
            showCurrentPlace();

        } else {
            PermissionUtilities.checkPermission(getActivity(), LossLocationFragment.this, PermissionUtilities.MY_APP_LOCATION_PERMISSIONS);
        }
    }

}
