package app.d3v3l.go4lunch.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseUser;

import app.d3v3l.go4lunch.R;
import app.d3v3l.go4lunch.databinding.ActivityHomeBinding;
import app.d3v3l.go4lunch.manager.UserManager;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActivityHomeBinding b;
    private UserManager userManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityHomeBinding.inflate(getLayoutInflater());
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
            // Case when user click on Logout Button
            if (idRessource == R.id.activity_home_drawer_logout) {
                userManager.signOut(this).addOnSuccessListener(aVoid -> finish());
            }
            return true;
        });

    }


    private void updateUIWithUserData(){
        if(userManager.isCurrentUserLogged()){
            FirebaseUser user = userManager.getCurrentUser();
            setInfoUserData(user);
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

    private void loadMapViewFragment() {
        Fragment mapFragment = new MapViewFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(b.activityHomeFrameLayout.getId(), mapFragment)
                .commit();
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


    private void setInfoUserData(FirebaseUser user){
        if (userManager.getCurrentUser() != null) {
            String name = TextUtils.isEmpty(user.getDisplayName()) ? getString(R.string.info_no_username_found) : user.getDisplayName();
            TextView nameView = b.activityHomeNavView.getHeaderView(0).findViewById(R.id.menuDrawerName);
            nameView.setText(name);

            String email = TextUtils.isEmpty(user.getEmail()) ? getString(R.string.info_no_email_found) : user.getEmail();
            TextView emailView = b.activityHomeNavView.getHeaderView(0).findViewById(R.id.menuDrawerEmail);
            emailView.setText(email);

            ImageView pictureView = b.activityHomeNavView.getHeaderView(0).findViewById(R.id.menuDrawerAvatar);
            if(user.getPhotoUrl() != null){
                Glide.with(this)
                        .load(user.getPhotoUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(pictureView);
            }
        }

    }
}