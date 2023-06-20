package app.d3v3l.go4lunch.Utils;

import android.util.Log;

import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;

import app.d3v3l.go4lunch.model.GoogleApiPlaces.placeDetails.DetailsContainer;
import app.d3v3l.go4lunch.model.GoogleApiPlaces.placesNearBySearch.Container;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaceCalls {

    // 1 - Creating a callback
    public interface Callbacks {
        void onResponse(@Nullable Container places);
        void onFailure();
    }

    public interface PlaceDetailsCallbacks {
        void onDetailsResponse(@Nullable DetailsContainer places);
        void onDetailsFailure();
    }

    // Public method to start fetching places with given parameters
    public static void fetchRestaurants(Callbacks callbacks, String location){

        // Weak reference to callback (avoid memory leaks)
        final WeakReference<Callbacks> callbacksWeakReference = new WeakReference<>(callbacks);

        // Get a Retrofit instance and the related endpoints
        PlaceService placeService = PlaceService.retrofit.create(PlaceService.class);

        // Create the call on GoogleMaps API
        Call<Container> call = placeService.getPlaces(location);
        // Start the call
        call.enqueue(new Callback<Container>() {

            @Override
            public void onResponse(Call<Container> call, Response<Container> response) {
                // Call the proper callback used in controller
                if (callbacksWeakReference.get() != null) callbacksWeakReference.get().onResponse(response.body());
            }

            @Override
            public void onFailure(Call<Container> call, Throwable t) {
                // Call the proper callback used in controller
                Log.d("CallBack", callbacksWeakReference.get().toString());
                if (callbacksWeakReference.get() != null) callbacksWeakReference.get().onFailure();
            }
        });
    }

    // Public method to start fetching places with given parameters
    public static void fetchRestaurantDetail(PlaceDetailsCallbacks placeDetailCallbacks, String placeId){

        // Weak reference to callback (avoid memory leaks)
        final WeakReference<PlaceDetailsCallbacks> detailsCallbacksWeakReference = new WeakReference<>(placeDetailCallbacks);

        // Get a Retrofit instance and the related endpoints
        PlaceService detailsPlaceService = PlaceService.retrofit.create(PlaceService.class);

        // Create the call on GoogleMaps API
        Call<DetailsContainer> call = detailsPlaceService.getPlaceDetails(placeId);
        // Start the call
        call.enqueue(new Callback<DetailsContainer>() {

            @Override
            public void onResponse(Call<DetailsContainer> call, Response<DetailsContainer> response) {
                // Call the proper callback used in controller
                if (detailsCallbacksWeakReference.get() != null) detailsCallbacksWeakReference.get().onDetailsResponse(response.body());
            }

            @Override
            public void onFailure(Call<DetailsContainer> call, Throwable t) {
                // Call the proper callback used in controller
                Log.d("DetailsCallBack", detailsCallbacksWeakReference.get().toString());
                if (detailsCallbacksWeakReference.get() != null) detailsCallbacksWeakReference.get().onDetailsFailure();
            }
        });
    }
}