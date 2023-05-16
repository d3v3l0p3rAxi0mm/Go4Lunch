package app.d3v3l.go4lunch.Utils;

import android.util.Log;

import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;
import java.util.List;

import app.d3v3l.go4lunch.model.Place;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaceCalls {

    // 1 - Creating a callback
    public interface Callbacks {
        void onResponse(@Nullable Place places);
        void onFailure();
    }

    // Public method to start fetching places with given parameters
    public static void fetchRestaurants(Callbacks callbacks, String query, String location, int radius){

        // Weak reference to callback (avoid memory leaks)
        final WeakReference<Callbacks> callbacksWeakReference = new WeakReference<>(callbacks);

        // Get a Retrofit instance and the related endpoints
        PlaceService placeService = PlaceService.retrofit.create(PlaceService.class);

        // Create the call on GoogleMaps API
        Call<Place> call = placeService.getPlaces(query, location, radius);
        // Start the call
        call.enqueue(new Callback<Place>() {

            @Override
            public void onResponse(Call<Place> call, Response<Place> response) {
                // Call the proper callback used in controller
                if (callbacksWeakReference.get() != null) callbacksWeakReference.get().onResponse(response.body());
            }

            @Override
            public void onFailure(Call<Place> call, Throwable t) {
                // Call the proper callback used in controller
                Log.d("CallBack", callbacksWeakReference.get().toString());
                if (callbacksWeakReference.get() != null) callbacksWeakReference.get().onFailure();
            }
        });
    }
}