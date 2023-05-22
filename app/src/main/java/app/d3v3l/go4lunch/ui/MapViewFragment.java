package app.d3v3l.go4lunch.ui;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;

import app.d3v3l.go4lunch.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapViewFragment extends Fragment {

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_map_view, container, false);
        Context context = view.getContext();

        // Initialize map fragment
        SupportMapFragment mapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                googleMap.setMyLocationEnabled(true);
                // When map is loaded
                googleMap.moveCamera(CameraUpdateFactory.zoomBy(15));


                // For positioning a marker on the map
                LatLng myPlace = new LatLng(46.660079946981696,2.297249870034013);
                googleMap.addMarker(new MarkerOptions()
                        .position(myPlace)
                        .title("Home"));
                //googleMap.moveCamera(CameraUpdateFactory.newLatLng(myPlace));

                LatLng resto = new LatLng(46.6424049,2.283553);
                googleMap.addMarker(new MarkerOptions()
                        .position(resto)
                        .title("resto"));

                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(myPlace,10,0f,0f)));

                Double a = SphericalUtil.computeDistanceBetween(myPlace,new LatLng(46.6424049,2.283553));
                Toast.makeText(getActivity(), "Distance = " + a + "m", Toast.LENGTH_LONG).show();

                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(@NonNull LatLng latLng) {
                        // When clicked on map
                        // Initialize marker options
                        MarkerOptions markerOptions=new MarkerOptions();
                        // Set position of marker
                        markerOptions.position(latLng);
                        // Set title of marker
                        markerOptions.title(latLng.latitude+" : "+latLng.longitude);
                        // Remove all marker
                        googleMap.clear();
                        // Animating to zoom the marker
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
                        // Add marker on map
                        googleMap.addMarker(markerOptions);
                    }
                });
            }
        });

        return view;

    }

    /*
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Add a marker in my Place,
        // and move the map's camera to the same location.
        LatLng myPlace = new LatLng(47.0802, 2.3927);
        googleMap.addMarker(new MarkerOptions()
                .position(myPlace)
                .title("My Place"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(myPlace));
    }
    */



}