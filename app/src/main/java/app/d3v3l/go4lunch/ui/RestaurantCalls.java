package app.d3v3l.go4lunch.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import app.d3v3l.go4lunch.Utils.PlaceCalls;
import app.d3v3l.go4lunch.databinding.ActivityRestaurantCallsBinding;
import app.d3v3l.go4lunch.model.placeDetails.PlaceDetailsContainer;
import app.d3v3l.go4lunch.model.placeDetails.PlaceWithDetails;
import app.d3v3l.go4lunch.model.placesByTextSearch.PlacesByTextSearchContainer;
import app.d3v3l.go4lunch.model.placesByTextSearch.Place;

public class RestaurantCalls extends AppCompatActivity implements PlaceCalls.CallbacksAllPlaces, PlaceCalls.CallbacksPlaceDetails {

    private ActivityRestaurantCallsBinding b;
    private StringBuilder stringBuilder = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityRestaurantCallsBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        HttpRequestAllPlaces();
    }

    // Execute HTTP request
    private void HttpRequestAllPlaces(){
        updateUIWhenStartingHTTPRequest();
        String myLocation = "46.660079946981696,2.297249870034013";
        PlaceCalls.fetchRestaurants(this, "restaurants", myLocation);
    }

    // Execute HTTP request
    private void HttpRequestDetailsPlace(String placeId){
        PlaceCalls.fetchDetailsRestaurant(this, placeId);
    }

    @Override
    public void onResponse(@Nullable PlacesByTextSearchContainer places) {
        for (Place result : places.getResults()) {
            HttpRequestDetailsPlace(result.getPlaceId());
        }
        b.jsonReturn.setText(stringBuilder.toString());
        updateUIWhenStopingHTTPRequest(stringBuilder.toString());
    }

    @Override
    public void onResponse(@Nullable PlaceDetailsContainer place) {
        PlaceWithDetails result = place.getResult();
        stringBuilder.append("-" + result.getPlaceId() + "\n");
        stringBuilder.append("-- " + result.getName() + "\n");
        stringBuilder.append("-- " + result.getFormattedAddress() + "\n");
        stringBuilder.append("-- Lat. " + result.getGeometry().getLocation().getLat() + "\n");
        stringBuilder.append("-- Lng. " + result.getGeometry().getLocation().getLng() + "\n");



    // Access a Firestore instance
    //FirebaseFirestore db = FirebaseFirestore.getInstance();
    //Result restaurant = new Result("");
    //db.collection("restaurant").document("LA").set(city);


    }

    @Override
    public void onFailure() {
        Log.d("HttpRequest", "FAILURE");
    }

    // ------------------
    //  UPDATE UI
    // ------------------

    private void updateUIWhenStartingHTTPRequest(){
        b.progessBar.setVisibility(View.VISIBLE);
        b.bowlForWait.setVisibility(View.VISIBLE);
    }

    private void updateUIWhenStopingHTTPRequest(String response){
        b.progessBar.setVisibility(View.GONE);
        b.bowlForWait.setVisibility(View.GONE);
    }




}