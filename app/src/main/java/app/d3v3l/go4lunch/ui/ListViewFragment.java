package app.d3v3l.go4lunch.ui;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

import static app.d3v3l.go4lunch.Utils.LocationUtils.getBoundsFromLatLng;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.maps.android.SphericalUtil;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import app.d3v3l.go4lunch.R;
import app.d3v3l.go4lunch.Utils.PlaceCalls;
import app.d3v3l.go4lunch.databinding.FragmentListViewBinding;
import app.d3v3l.go4lunch.databinding.FragmentMapViewBinding;
import app.d3v3l.go4lunch.model.GoogleApiPlaces.placesNearBySearch.Container;
import app.d3v3l.go4lunch.model.GoogleApiPlaces.placesNearBySearch.Photo;
import app.d3v3l.go4lunch.model.GoogleApiPlaces.placesNearBySearch.Result;
import app.d3v3l.go4lunch.model.Restaurant;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListViewFragment extends Fragment implements PlaceCalls.Callbacks {

    private FragmentListViewBinding b;
    private ListRestaurantAdapter listRestaurantAdapter;
    private RecyclerView mRecyclerView;
    private LatLng myLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private List<Restaurant> mRestaurants = new ArrayList<>();
    private final int AUTOCOMPLETE_REQUEST_CODE = 0;

    public ListViewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     * @return A new instance of fragment ListViewFragment.
     */
    public static ListViewFragment newInstance() {
        ListViewFragment fragment = new ListViewFragment();
        return fragment;
    }



    @Override
    public void onResume() {
        super.onResume();
        //mRecyclerView.setAdapter(new ListRestaurantAdapter(mRestaurants));
        //mRecyclerView.getAdapter().notifyDataSetChanged();
        SearchMyPositionThenPlacesNearby();
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        b = FragmentListViewBinding.inflate(getLayoutInflater());
        configureRecyclerView(b.getRoot());

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        //for SearchView
        setHasOptionsMenu(true);
        //change title of toolbar
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("I'm Hungry");

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

/*    public void onSearchCalled() {
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
    }*/

/*
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


    // Configure RecyclerView
    private void configureRecyclerView(View view){
        mRecyclerView = (RecyclerView) view;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
    }


    private void SearchMyPositionThenPlacesNearby() {
        Log.d("XPX", "Enter in SearchMyPositionThenPlacesNearby");
        @SuppressLint("MissingPermission")
        Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
        locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                executeHttpRequestInListViewWithRetrofit();
            }
        });
    }



    // Execute HTTP request and update UI
    private void executeHttpRequestInListViewWithRetrofit(){
        Log.d("XPX", myLocation.toString());
        PlaceCalls.fetchRestaurants(this, myLocation.latitude + "," + myLocation.longitude);
    }

    @Override
    public void onResponse(@Nullable Container places) {

        DecimalFormat df = new DecimalFormat("#.#");
        df.setRoundingMode(RoundingMode.HALF_UP);
        mRestaurants.clear();

        for (Result result : places.getResults()) {
            Double distance = SphericalUtil.computeDistanceBetween(myLocation,new LatLng(result.getGeometry().getLocation().getLat(),result.getGeometry().getLocation().getLng()));
            String distanceSimplified;
            if (distance>=1000) {
                distance = distance /1000;
                distanceSimplified = df.format(distance) + " km";
            } else {
                distanceSimplified = distance.intValue() + " m";
            }


            List<Photo> photos = result.getPhotos();
            Photo photo;
            if (photos == null) {
                photo = null;
            } else {
                photo = result.getPhotos().get(0);
            }

            Restaurant savedResultInList = new Restaurant(result.getPlaceId(), result.getName(), result.getVicinity(), result.getGeometry().getLocation().getLat(), result.getGeometry().getLocation().getLng(), distanceSimplified, photo);
            mRestaurants.add(savedResultInList);
        }
        mRecyclerView.setAdapter(new ListRestaurantAdapter(mRestaurants));

    }


    @Override
    public void onFailure() {
        Log.d("HttpRequest", "FAILURE");
    }

}