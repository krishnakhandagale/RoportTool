
package com.electivechaos.claimsadjuster.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.electivechaos.claimsadjuster.R;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Handler;

import static android.os.Looper.getMainLooper;

public class PlaceArrayAdapter extends ArrayAdapter<PlaceArrayAdapter.PlaceAutocomplete> implements Filterable {
    private static final String TAG = "PlaceArrayAdapter";
    private GoogleApiClient mGoogleApiClient;
    //    private AutocompleteFilter mPlaceFilter;
    private LatLngBounds mBounds;
    private ArrayList<PlaceAutocomplete> mResultList;
    private int mLayoutResourceId;
    private Context mContext;
    private PlacesClient mPlacesClient;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(new LatLng(37.398160, -122.180831), new LatLng(37.430620, -121.972090));
    RectangularBounds bounds = RectangularBounds.newInstance(
            new LatLng(-33.880490, 151.184363),
            new LatLng(-33.858754, 151.229596));


    public PlaceArrayAdapter(Context context, int resource, LatLngBounds bounds) {
        super(context, resource);
        mBounds = bounds;
        mLayoutResourceId = resource;
        mContext = context;
        if (!Places.isInitialized()) {
            Places.initialize(mContext, getContext().getString(R.string.api_key));
        }

        mPlacesClient = Places.createClient(mContext);
    }


    @Override
    public int getCount() {
        return mResultList.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        try {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(mLayoutResourceId, parent, false);
            }

            PlaceAutocomplete objectItem = mResultList.get(position);

            TextView textViewItem = convertView.findViewById(R.id.location_short);
            textViewItem.setText(objectItem.primaryText);
            TextView textViewItemLong = convertView.findViewById(R.id.location_long);
            textViewItemLong.setText(objectItem.fulltext);

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }

    @Override
    public PlaceAutocomplete getItem(int position) {
        return mResultList.get(position);
    }



    private ArrayList<PlaceAutocomplete> getPredictions(CharSequence constraint) {
        final ArrayList<PlaceAutocomplete> resultList = new ArrayList<>();
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                // similar to previous mBounds
                // but you have to use Rectangular bounds (Check reference link)
//                    .setLocationRestriction(RectangularBounds.newInstance(BOUNDS_MOUNTAIN_VIEW))

//                    .setLocationBias(bounds)
//              .setLocationRestriction(RectangularBounds.newInstance(BOUNDS_MOUNTAIN_VIEW))
//                    .setCountry("au")
                .setSessionToken(token)
                .setQuery(constraint.toString()) // similar to previous constraint
//                .setTypeFilter(TypeFilter.ADDRESS) // similar to mPlaceFilter
                .build();
        Task<FindAutocompletePredictionsResponse> autocompletePredictions = mPlacesClient.findAutocompletePredictions(request);

        // This method should have been called off the main UI thread. Block and wait for at most
        // 60s for a result from the API.
        try {
            Tasks.await(autocompletePredictions, 60, TimeUnit.SECONDS);
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            e.printStackTrace();
        }
        if (autocompletePredictions.isSuccessful()) {
            FindAutocompletePredictionsResponse findAutocompletePredictionsResponse = autocompletePredictions.getResult();
            if (findAutocompletePredictionsResponse != null)
                for (AutocompletePrediction prediction : findAutocompletePredictionsResponse.getAutocompletePredictions()) {
                    Log.i(TAG, prediction.getPlaceId());
                    resultList.add(new PlaceAutocomplete(prediction.getPlaceId(), prediction.getPrimaryText(new StyleSpan(Typeface.BOLD)).toString(), prediction.getFullText(new StyleSpan(Typeface.BOLD)).toString()));
                }

            return resultList;
        } else {
            return resultList;
        }

    }

    /**
     * Returns the filter for the current set of autocomplete results.
     */
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                // Skip the autocomplete query if no constraints are given.
                if (constraint != null) {
                    // Query the autocomplete API for the (constraint) search string.
//                    mResultList = searchPlaces(constraint);
                    mResultList = getPredictions(constraint);
                    if (mResultList != null) {
                        // The API successfully returned results.
                        results.values = mResultList;
                        results.count = mResultList.size();
                    }
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    // The API returned at least one result, update the data.
                    notifyDataSetChanged();
                } else {
                    // The API did not return any results, invalidate the data set.
                    //notifyDataSetInvalidated();
                }
            }
        };

    }

    static public class PlaceAutocomplete {

        public CharSequence placeId;
        public CharSequence primaryText;
        public CharSequence fulltext;

        PlaceAutocomplete(CharSequence placeId, CharSequence fulltext, CharSequence primaryText) {
            this.placeId = placeId;
            this.primaryText = primaryText;
            this.fulltext = fulltext;
        }

        @Override
        public String toString() {
            return this.fulltext.toString();
        }
    }
}
