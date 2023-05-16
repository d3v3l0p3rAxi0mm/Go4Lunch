package app.d3v3l.go4lunch.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        String myLocation = "45,3";
        PlaceCalls.fetchRestaurants(this, "restaurants", myLocation, 3000);
    }

    @Override
    public void onResponse(@Nullable Place places) {
        Log.d("HttpRequest", "SUCCESS");
        updateUIWithListOfUsers(places);

        // Access a Firestore instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();


        /*
        Result restaurant = new Result("");
        db.collection("restaurant").document("LA").set(city);
         */



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

    private void updateUIWithListOfUsers(Place places){
        StringBuilder stringBuilder = new StringBuilder();
            for (Result result : places.getResults()) {
                stringBuilder.append("-" + result.getName() + "\n");
                stringBuilder.append("--" + result.getFormattedAddress() + "\n");
                stringBuilder.append("-- lat. " + result.getGeometry().getLocation().getLat() + "\n");
                stringBuilder.append("-- lng. " + result.getGeometry().getLocation().getLng() + "\n");
            }
        b.jsonReturn.setText(stringBuilder.toString());
        updateUIWhenStopingHTTPRequest(stringBuilder.toString());
    }


}