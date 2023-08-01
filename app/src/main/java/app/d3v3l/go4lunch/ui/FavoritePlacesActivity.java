package app.d3v3l.go4lunch.ui;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.android.SphericalUtil;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import app.d3v3l.go4lunch.R;
import app.d3v3l.go4lunch.databinding.ActivityFavoritePlacesBinding;
import app.d3v3l.go4lunch.manager.UserManager;
import app.d3v3l.go4lunch.model.Restaurant;

public class FavoritePlacesActivity extends AppCompatActivity {

    private ActivityFavoritePlacesBinding b;
    private RecyclerView mRecyclerView;
    private List<Restaurant> mRestaurants = new ArrayList<>();
    private LatLng myLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private UserManager userManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userManager = UserManager.getInstance();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        SearchMyPosition();

        b = ActivityFavoritePlacesBinding.inflate(getLayoutInflater());
        configureRecyclerView(b.getRoot());

        setContentView(b.getRoot());

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_favorite_place_toolbar);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("restaurants")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            mRestaurants.clear();
                            DecimalFormat df = new DecimalFormat("#.#");
                            df.setRoundingMode(RoundingMode.HALF_UP);

                            for (QueryDocumentSnapshot documentLevel0 : task.getResult()) {

                                db.collection("restaurants").document(Objects.requireNonNull(documentLevel0.getData().get("place_id")).toString()).collection("likeUser")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {

                                                    for (QueryDocumentSnapshot document : task.getResult()) {

                                                        if (document.getId().equals(userManager.getCurrentUser().getUid())) {

                                                            if (Objects.equals(document.getData().get("place_in_favorite_for_user"), "true")) {

                                                                Double distance = SphericalUtil.computeDistanceBetween(myLocation,new LatLng(Double.parseDouble(Objects.requireNonNull(documentLevel0.getData().get("latitude")).toString()),Double.parseDouble(Objects.requireNonNull(documentLevel0.getData().get("longitude")).toString())));
                                                                String distanceSimplified;
                                                                if (distance>=1000) {
                                                                    distance = distance /1000;
                                                                    distanceSimplified = df.format(distance) + " km";
                                                                } else {
                                                                    distanceSimplified = distance.intValue() + " m";
                                                                }

                                                                Restaurant r = new Restaurant(
                                                                        Objects.requireNonNull(documentLevel0.getData().get("place_id")).toString(),
                                                                        Objects.requireNonNull(documentLevel0.getData().get("name")).toString(),
                                                                        Objects.requireNonNull(documentLevel0.getData().get("address")).toString(),
                                                                        Double.valueOf(Objects.requireNonNull(documentLevel0.getData().get("latitude")).toString()),
                                                                        Double.valueOf(Objects.requireNonNull(documentLevel0.getData().get("longitude")).toString()),
                                                                        distanceSimplified,
                                                                        Objects.requireNonNull(documentLevel0.getData().get("photo")).toString()
                                                                );


                                                                mRestaurants.add(r);

                                                            }

                                                        }

                                                    }
                                                    mRecyclerView.setAdapter(new ListRestaurantAdapter(mRestaurants));



                                                }
                                            }
                                        });

                            }

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    private void SearchMyPosition() {
        @SuppressLint("MissingPermission")
        Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
        locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                myLocation = new LatLng(location.getLatitude(), location.getLongitude());
            }
        });
    }

    // Configure RecyclerView
    private void configureRecyclerView(View view){
        mRecyclerView = (RecyclerView) view.findViewById(R.id.list_restaurants);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

}