package app.d3v3l.go4lunch.ui;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import app.d3v3l.go4lunch.R;
import app.d3v3l.go4lunch.Utils.PlaceCalls;
import app.d3v3l.go4lunch.databinding.FragmentApiFirstCallBinding;
import app.d3v3l.go4lunch.model.GoogleApiPlaces.placesSearchByText.PlaceSearchByText;
import app.d3v3l.go4lunch.model.GoogleApiPlaces.placesSearchByText.Result;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ApiFirstCallFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ApiFirstCallFragment extends Fragment implements PlaceCalls.Callbacks {

    private FragmentApiFirstCallBinding b;

    public ApiFirstCallFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */

    public static ApiFirstCallFragment newInstance() {
        ApiFirstCallFragment fragment = new ApiFirstCallFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        b = FragmentApiFirstCallBinding.inflate(getLayoutInflater());
        return b.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        executeHttpRequestWithRetrofit();
    }

    // Execute HTTP request and update UI
    private void executeHttpRequestWithRetrofit(){
        updateUIWhenStartingHTTPRequest();
        String myLocation = "46.66007,2.29724";
        PlaceCalls.fetchRestaurants(this, myLocation);
    }

    @Override
    public void onResponse(@Nullable PlaceSearchByText places) {
        Log.d("HttpRequest", "SUCCESS");
        Log.d("HttpRequest", String.valueOf(places.getResults().size()));
        StringBuilder stringBuilder = new StringBuilder();
        for (Result result : places.getResults()) {
            Log.d("placeId", result.getPlaceId());
            stringBuilder.append("    ID.        " + result.getPlaceId() + "\n");
            stringBuilder.append("      name.    " + result.getName() + "\n");
            stringBuilder.append("      address. " + result.getFormattedAddress() + "\n");
            stringBuilder.append("      lat.     " + result.getGeometry().getLocation().getLat() + "\n");
            stringBuilder.append("      lng.     " + result.getGeometry().getLocation().getLng() + "\n");
            stringBuilder.append("      photos.  " + result.getPhotos() + "\n");
        }
        b.jsonReturnFragment.setText(stringBuilder.toString());

        // Access a Firestore instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        for (Result result : places.getResults()) {

            Map<String, Object> place = new HashMap<>();
            place.put("place_id", result.getPlaceId());
            place.put("name", result.getName());
            place.put("formatted_address", result.getFormattedAddress());
            place.put("latitude", result.getGeometry().getLocation().getLat());
            place.put("longitude", result.getGeometry().getLocation().getLng());

            db.collection("restaurants").document(result.getPlaceId())
                    .set(place)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot successfully written!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error writing document", e);
                        }
                    });

        }
        updateUIWhenStopingHTTPRequest();

    }

    @Override
    public void onFailure() {
        Log.d("HttpRequest", "FAILURE");
    }

    // ------------------
    //  UPDATE UI
    // ------------------

    private void updateUIWhenStartingHTTPRequest(){
        b.progessBarFragment.setVisibility(View.VISIBLE);
        b.bowlForWaitFragment.setVisibility(View.VISIBLE);
    }

    private void updateUIWhenStopingHTTPRequest(){
        b.progessBarFragment.setVisibility(View.GONE);
        b.bowlForWaitFragment.setVisibility(View.GONE);
    }

}