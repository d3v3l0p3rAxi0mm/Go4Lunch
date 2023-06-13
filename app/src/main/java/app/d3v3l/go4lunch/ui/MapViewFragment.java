package app.d3v3l.go4lunch.ui;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.maps.android.SphericalUtil;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import app.d3v3l.go4lunch.R;
import app.d3v3l.go4lunch.Utils.PlaceCalls;
import app.d3v3l.go4lunch.databinding.ActivityHomeBinding;
import app.d3v3l.go4lunch.databinding.FragmentMapViewBinding;
import app.d3v3l.go4lunch.model.GoogleApiPlaces.placesNearBySearch.Container;
import app.d3v3l.go4lunch.model.GoogleApiPlaces.placesNearBySearch.Result;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapViewFragment extends Fragment implements PlaceCalls.Callbacks {

    private ActivityHomeBinding bHome;
    private FragmentMapViewBinding b;
    private GoogleMap googleMapGlobal;
    private LatLng myLocation;

    private FusedLocationProviderClient fusedLocationProviderClient;

    public MapViewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     * @return A new instance of fragment MapViewFragment.
     */
    public static MapViewFragment newInstance() {
        MapViewFragment fragment = new MapViewFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        SearchMyPositionThenPlacesNearby();
    }

    @SuppressLint("MissingPermission")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        b = FragmentMapViewBinding.inflate(getLayoutInflater());

        // Initialize map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                googleMapGlobal = googleMap;
                googleMapGlobal.setMyLocationEnabled(true);
            }
        });
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        //for SearchView
        setHasOptionsMenu(true);
        //change title of toolbar
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("I'm Hungry");


        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null){
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(false);
        }

        return b.getRoot();
    }


    /**
     * For SearchView
     *
     * @param menu
     * @param inflater
     */
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.activity_home_topmenu, menu);
        MenuItem item = menu.findItem(R.id.actionSearch);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_IF_ROOM);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    //executeHttpRequestWithRetrofit();
                }
                //executeHttpRequestWithRetrofitAutocomplete(newText);
                return true;
            }
        });
    }


    private void SearchMyPositionThenPlacesNearby() {
        @SuppressLint("MissingPermission")
        Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
        locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                executeHttpRequestInMapWithRetrofit();
            }
        });
    }

    // Execute HTTP request and update UI
    private void executeHttpRequestInMapWithRetrofit(){
        Log.d("MyLocation in MapView", myLocation.toString());
        PlaceCalls.fetchRestaurants(this, myLocation.latitude + "," + myLocation.longitude);
    }

    @Override
    public void onResponse(@Nullable Container places) {

        // Access a Firestore instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DecimalFormat df = new DecimalFormat("#.#");
        df.setRoundingMode(RoundingMode.HALF_UP);

        for (Result result : places.getResults()) {

            Map<String, Object> place = new HashMap<>();
            place.put("place_id", result.getPlaceId());
            place.put("name", result.getName());
            place.put("address", result.getVicinity());
            place.put("latitude", result.getGeometry().getLocation().getLat());
            place.put("longitude", result.getGeometry().getLocation().getLng());

            Double distance = SphericalUtil.computeDistanceBetween(myLocation,new LatLng(result.getGeometry().getLocation().getLat(),result.getGeometry().getLocation().getLng()));
            String distanceSimplified;
            if (distance>=1000) {
                distance = distance /1000;
                distanceSimplified = df.format(distance) + " km";
            } else {
                distanceSimplified = distance.intValue() + " m";
            }

            LatLng coords = new LatLng(result.getGeometry().getLocation().getLat(), result.getGeometry().getLocation().getLng());

            googleMapGlobal.addMarker(new MarkerOptions()
                    .position(coords)
                    .title(result.getName())
                    .snippet("Distance : " + distanceSimplified)
                    .anchor(0.5f, 1));

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
        googleMapGlobal.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(myLocation,10,0f,0f)));

    }


    @Override
    public void onFailure() {
        Log.d("HttpRequest", "FAILURE");
    }

}