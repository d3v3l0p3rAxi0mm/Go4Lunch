package app.d3v3l.go4lunch.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.List;

import app.d3v3l.go4lunch.BuildConfig;
import app.d3v3l.go4lunch.Utils.PlaceCalls;
import app.d3v3l.go4lunch.databinding.ActivityRestaurantCallsBinding;
import app.d3v3l.go4lunch.model.Location;
import app.d3v3l.go4lunch.model.Place;
import app.d3v3l.go4lunch.model.Result;

public class RestaurantCalls extends AppCompatActivity implements PlaceCalls.Callbacks {

    private ActivityRestaurantCallsBinding b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityRestaurantCallsBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        executeHttpRequestWithRetrofit();
    }

    // Execute HTTP request and update UI
    private void executeHttpRequestWithRetrofit(){
        updateUIWhenStartingHTTPRequest();
        //String myLocation = "45,3";
        PlaceCalls.fetchRestaurants(this, "restaurants");
    }

    @Override
    public void onResponse(@Nullable List<Place> places) {
        Log.d("HttpRequest", "SUCCESS");
        updateUIWithListOfUsers(places);
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

    private void updateUIWithListOfUsers(List<Place> places){
        StringBuilder stringBuilder = new StringBuilder();
        for (Place place : places){
            for (Result result : place.getResults()) {
                stringBuilder.append("-" + result.getName() + "\n");
            }
        }
        b.jsonReturn.setText(stringBuilder.toString());
        updateUIWhenStopingHTTPRequest(stringBuilder.toString());
    }


}