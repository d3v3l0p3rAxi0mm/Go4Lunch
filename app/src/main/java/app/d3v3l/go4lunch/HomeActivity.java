package app.d3v3l.go4lunch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseUser;

import app.d3v3l.go4lunch.databinding.ActivityHomeBinding;
import app.d3v3l.go4lunch.databinding.ActivityHomeNavHeaderBinding;
import app.d3v3l.go4lunch.manager.UserManager;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // resto https://cloud.google.com/firestore/docs/manage-data/add-data?hl=fr

    // Doc carto https://developers.google.com/maps/documentation/android-sdk/map?hl=fr



    private ActivityHomeBinding b;
    private ActivityHomeNavHeaderBinding bNav;
    private UserManager userManager = null;

    private NavigationView headerNavView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityHomeBinding.inflate(getLayoutInflater());
        //bNav = ActivityHomeNavHeaderBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        userManager = UserManager.getInstance();
        configureToolBar();
        configureDrawerLayout();
        configureNavigationView();
        loadMapViewFragment();
        updateUIWithUserData();

        // conditions of bottomNav clicks
        b.bottomNavigation.setOnItemSelectedListener(item -> {
            int idRessource = item.getItemId();
            if (idRessource == R.id.listViewButton) {
                loadListViewFragment();
            } else if (idRessource == R.id.workmatesButton) {
                loadWorkmatesFragment();
            } else {
                loadMapViewFragment();
            }
            return true;
        });

        b.activityHomeNavView.setNavigationItemSelectedListener(item -> {
            int idRessource = item.getItemId();
            if (idRessource == R.id.activity_home_drawer_logout) {
                userManager.signOut(this).addOnSuccessListener(aVoid -> { finish(); });
            }
            return true;
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUIWithUserData();
    }

    private void updateUIWithUserData(){
        if(userManager.isCurrentUserLogged()){
            FirebaseUser user = userManager.getCurrentUser();
            setTextUserData(user);
        }
    }



    private void configureToolBar(){
        setSupportActionBar(b.activityHomeToolbar);
    }

    private void configureDrawerLayout(){
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, b.activityHomeDrawerLayout, b.activityHomeToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        b.activityHomeDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void configureNavigationView(){
        b.activityHomeNavView.setNavigationItemSelectedListener(this);
    }
/*
    navHeaderView= navigationView.inflateHeaderView(R.layout.nav_header_main);
    tvHeaderName= (TextView) navHeaderView.findViewById(R.id.tvHeaderName);
    tvHeaderName.setText("Saly");

 */

    private void loadMapViewFragment() {
        FragmentManager manager = ((AppCompatActivity) b.activityHomeFrameLayout.getContext()).getSupportFragmentManager();
        MapViewFragment mapViewFragment = MapViewFragment.newInstance();
        manager.beginTransaction().replace(R.id.activity_home_frame_layout, mapViewFragment).commit();
    }

    private void loadListViewFragment() {
        FragmentManager manager = ((AppCompatActivity) b.activityHomeFrameLayout.getContext()).getSupportFragmentManager();
        ListViewFragment listViewFragment = ListViewFragment.newInstance();
        manager.beginTransaction().replace(R.id.activity_home_frame_layout, listViewFragment).commit();
    }

    private void loadWorkmatesFragment() {
        FragmentManager manager = ((AppCompatActivity) b.activityHomeFrameLayout.getContext()).getSupportFragmentManager();
        WorkmatesFragment workmatesFragment = WorkmatesFragment.newInstance();
        manager.beginTransaction().replace(R.id.activity_home_frame_layout, workmatesFragment).commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    private void setTextUserData(FirebaseUser user){

        if (userManager.getCurrentUser() != null) {

            //b.activityHomeDrawerLayout.setDrawerTitle(0,user.getDisplayName());

            String name = TextUtils.isEmpty(user.getDisplayName()) ? getString(R.string.info_no_username_found) : user.getDisplayName();
            //bNav.menuDrawerName.setText(name);
            TextView textView = b.activityHomeNavView.getHeaderView(0).findViewById(R.id.menuDrawerName);
            textView.setText(name);


        }

    }
}