package app.d3v3l.go4lunch.Utils;

import android.util.Log;

import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;

import app.d3v3l.go4lunch.model.placeDetails.PlaceDetailsContainer;
import app.d3v3l.go4lunch.model.placesByTextSearch.PlacesByTextSearchContainer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaceCalls {

    // 1 - Creating a callback
    public interface CallbacksAllPlaces {
        void onResponse(@Nullable PlacesByTextSearchContainer places);
        void onFailure();
    }
    public interface CallbacksPlaceDetails {
        void onResponse(@Nullable PlaceDetailsContainer place);
        void onFailure();
    }

    // Public method to start fetching places with given parameters
    public static void fetchRestaurants(CallbacksAllPlaces callbacksAllPlaces, String query, String location){

        // Weak reference to callback (avoid memory leaks)
        final WeakReference<CallbacksAllPlaces> callbacksWeakReference = new WeakReference<>(callbacksAllPlaces);

        // Get a Retrofit instance and the related endpoints
        PlaceService placeService = PlaceService.retrofit.create(PlaceService.class);

        // Create the call on GoogleMaps API
        Call<PlacesByTextSearchContainer> call = placeService.getPlaces(query, location);
        // Start the call
        call.enqueue(new Callback<PlacesByTextSearchContainer>() {

            @Override
            public void onResponse(Call<PlacesByTextSearchContainer> call, Response<PlacesByTextSearchContainer> response) {
                // Call the proper callback used in controller
                Log.d("CallBack", "PlacesByTextSearchContainer onSuccess");
                if (callbacksWeakReference.get() != null) callbacksWeakReference.get().onResponse(response.body());
            }

            @Override
            public void onFailure(Call<PlacesByTextSearchContainer> call, Throwable t) {
                // Call the proper callback used in controller
                Log.d("CallBack", "PlacesByTextSearchContainer onFailure");
                if (callbacksWeakReference.get() != null) callbacksWeakReference.get().onFailure();
            }
        });
    }

    // Public method to start fetching places with given parameters
    public static void fetchDetailsRestaurant(CallbacksPlaceDetails callbacksPlaceDetails, String place_id){

        // Weak reference to callback (avoid memory leaks)
        final WeakReference<CallbacksPlaceDetails> callbacksWeakReference = new WeakReference<>(callbacksPlaceDetails);

        // Get a Retrofit instance and the related endpoints
        PlaceService placeService = PlaceService.retrofit.create(PlaceService.class);

        // Create the call on GoogleMaps API
        Call<PlaceDetailsContainer> call = placeService.getPlaceDetails(place_id);
        // Start the call
        call.enqueue(new Callback<PlaceDetailsContainer>() {

            @Override
            public void onResponse(Call<PlaceDetailsContainer> call, Response<PlaceDetailsContainer> response) {
                // Call the proper callback used in controller
                Log.d("CallBack", "PlaceDetailsContainer onSuccess");
                if (callbacksWeakReference.get() != null) callbacksWeakReference.get().onResponse(response.body());
            }

            @Override
            public void onFailure(Call<PlaceDetailsContainer> call, Throwable t) {
                // Call the proper callback used in controller
                Log.d("CallBack", "PlaceDetailsContainer onFailure");
                if (callbacksWeakReference.get() != null) callbacksWeakReference.get().onFailure();
            }
        });
    }
}