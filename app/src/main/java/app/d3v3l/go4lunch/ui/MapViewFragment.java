package app.d3v3l.go4lunch.ui;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

import static app.d3v3l.go4lunch.Utils.LocationUtils.getBoundsFromLatLng;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.maps.android.SphericalUtil;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.d3v3l.go4lunch.R;
import app.d3v3l.go4lunch.Utils.PlaceCalls;
import app.d3v3l.go4lunch.databinding.ActivityHomeBinding;
import app.d3v3l.go4lunch.databinding.FragmentMapViewBinding;
import app.d3v3l.go4lunch.model.GoogleApiPlaces.placesNearBySearch.Container;
import app.d3v3l.go4lunch.model.GoogleApiPlaces.placesNearBySearch.Photo;
import app.d3v3l.go4lunch.model.GoogleApiPlaces.placesNearBySearch.Result;
import app.d3v3l.go4lunch.model.Restaurant;

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
    private List<Restaurant> mRestaurants = new ArrayList<>();
    private final int AUTOCOMPLETE_REQUEST_CODE = 0;

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionSearch:
                //onSearchCalled();
                return true;
            default:
                return false;
        }
    }

    /**
     * For SearchView
     *
     * @param //menu
     * @param //inflater
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

                googleMapGlobal.clear();

                if (newText.isEmpty()) {
                    displayAutoCompleteResults(mRestaurants);
                } else {
                    if (newText.length() >= 1) {
                        List<Restaurant> mNewRestaurants = new ArrayList<>();
                        for (Restaurant resto : mRestaurants) {
                            if (resto.getName().toLowerCase().contains(newText.toLowerCase()) || resto.getAddress().toLowerCase().contains(newText.toLowerCase())) {
                                mNewRestaurants.add(resto);
                            }
                        }
                        displayAutoCompleteResults(mNewRestaurants);
                    }
                }
                return true;
            }
        });
    }


    /*public void onSearchCalled() {
        // Set the fields to specify which types of place data to return.
        List<Place.Field> fields = Arrays.asList(Place.Field.ID);
        // Start the autocomplete intent.
        ArrayList<String> typeOfSearch = new ArrayList<String>();
        typeOfSearch.add("restaurant");
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY, fields)
                .setTypesFilter(typeOfSearch)
                .setLocationBias(getBoundsFromLatLng(myLocation, 50000))
                .build(getActivity());
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Intent intent = new Intent(getActivity(), RestaurantDetailsActivity.class);
                intent.putExtra("PLACEID", place.getId());
                startActivity(intent);

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }*/



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
        PlaceCalls.fetchRestaurants(this, myLocation.latitude + "," + myLocation.longitude);
    }

    @Override
    public void onResponse(@Nullable Container places) {

        // Access a Firestore instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DecimalFormat df = new DecimalFormat("#.#");
        df.setRoundingMode(RoundingMode.HALF_UP);

        mRestaurants.clear();

        for (Result result : places.getResults()) {

            Map<String, Object> place = new HashMap<>();
            place.put("place_id", result.getPlaceId());
            place.put("name", result.getName());
            place.put("address", result.getVicinity());
            place.put("latitude", result.getGeometry().getLocation().getLat());
            place.put("longitude", result.getGeometry().getLocation().getLng());

            Photo p = new Photo();
            this.mRestaurants.add(new Restaurant(result.getPlaceId(), result.getName(), result.getVicinity(), result.getGeometry().getLocation().getLat(),result.getGeometry().getLocation().getLng(),result.getDistanceFromUser(),p));

            Double distance = SphericalUtil.computeDistanceBetween(myLocation,new LatLng(result.getGeometry().getLocation().getLat(),result.getGeometry().getLocation().getLng()));
            String distanceSimplified;
            if (distance>=1000) {
                distance = distance /1000;
                distanceSimplified = df.format(distance) + " km";
            } else {
                distanceSimplified = distance.intValue() + " m";
            }

            LatLng coords = new LatLng(result.getGeometry().getLocation().getLat(), result.getGeometry().getLocation().getLng());

            MarkerOptions markerOptions = new MarkerOptions()
                    .position(coords)
                    .title(result.getName())
                    .snippet("Distance : " + distanceSimplified)
                    .anchor(0.5f, 1);

            Marker marker = googleMapGlobal.addMarker(markerOptions);
            marker.setTag(result.getPlaceId());


            //TODO voir onClickListener sur le marker
            // https://www.geeksforgeeks.org/how-to-add-onclicklistener-to-marker-on-google-maps-in-android/

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

        // adding on click listener to marker of google maps.
        /*googleMapGlobal.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {



                // on marker click we are getting the title of our marker
                // which is clicked and displaying it in a toast message.
                //String markerName = marker.getTitle();
                //Toast.makeText(getActivity(), "Clicked location is " + markerName, Toast.LENGTH_SHORT).show();
                return true;
            }
        });*/

        googleMapGlobal.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(@NonNull Marker marker) {
                Intent intent = new Intent(getActivity(), RestaurantDetailsActivity.class);
                intent.putExtra("PLACEID", marker.getTag().toString());
                startActivity(intent);
            }
        });

    }


    public void displayAutoCompleteResults(List<Restaurant> restaurants) {

        DecimalFormat df = new DecimalFormat("#.#");
        df.setRoundingMode(RoundingMode.HALF_UP);

        for (Restaurant resto : restaurants) {

            Map<String, Object> place = new HashMap<>();
            place.put("place_id", resto.getPlaceId());
            place.put("name", resto.getName());
            place.put("address", resto.getAddress());
            place.put("latitude", resto.getLatitude());
            place.put("longitude", resto.getLongitude());

            Double distance = SphericalUtil.computeDistanceBetween(myLocation,new LatLng(resto.getLatitude(),resto.getLongitude()));
            String distanceSimplified;
            if (distance>=1000) {
                distance = distance /1000;
                distanceSimplified = df.format(distance) + " km";
            } else {
                distanceSimplified = distance.intValue() + " m";
            }

            LatLng coords = new LatLng(resto.getLatitude(), resto.getLongitude());

            MarkerOptions markerOptions = new MarkerOptions()
                    .position(coords)
                    .title(resto.getName())
                    .snippet("Distance : " + distanceSimplified)
                    .anchor(0.5f, 1);

            Marker marker = googleMapGlobal.addMarker(markerOptions);
            marker.setTag(resto.getPlaceId());

        }
        googleMapGlobal.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(myLocation,10,0f,0f)));


        googleMapGlobal.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(@NonNull Marker marker) {
                Intent intent = new Intent(getActivity(), RestaurantDetailsActivity.class);
                intent.putExtra("PLACEID", marker.getTag().toString());
                startActivity(intent);
            }
        });














    }



    @Override
    public void onFailure() {
        Log.d("HttpRequest", "FAILURE");
    }

}