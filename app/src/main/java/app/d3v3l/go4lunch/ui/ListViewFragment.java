package app.d3v3l.go4lunch.ui;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.SphericalUtil;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import app.d3v3l.go4lunch.R;
import app.d3v3l.go4lunch.Utils.PlaceCalls;
import app.d3v3l.go4lunch.databinding.FragmentListViewBinding;
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
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.iamhungry);

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
                    mRecyclerView.setAdapter(new ListRestaurantAdapter(mRestaurants));
                } else {
                    if (newText.length() >= 1) {
                        List<Restaurant> mNewRestaurants = new ArrayList<>();
                        Log.d(TAG, "search pris en compte");
                        for (Restaurant resto : mRestaurants) {
                            if (resto.getName().toLowerCase().contains(newText.toLowerCase()) || resto.getAddress().toLowerCase().contains(newText.toLowerCase())) {
                                mNewRestaurants.add(resto);
                            }
                        }
                        mRecyclerView.setAdapter(new ListRestaurantAdapter(mNewRestaurants));
                    }
                }
                return true;
            }
        });
    }

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
            String photo;
            if (photos == null) {
                photo = "default";
            } else {
                photo = result.getPhotos().get(0).getPhotoReference();
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