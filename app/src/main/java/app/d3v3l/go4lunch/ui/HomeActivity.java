package app.d3v3l.go4lunch.ui;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;

import app.d3v3l.go4lunch.BuildConfig;
import app.d3v3l.go4lunch.R;
import app.d3v3l.go4lunch.databinding.ActivityHomeBinding;
import app.d3v3l.go4lunch.manager.UserManager;

public class HomeActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActivityHomeBinding b;
    private UserManager userManager = null;
    private GoogleMap mMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityHomeBinding.inflate(getLayoutInflater());

        setContentView(b.getRoot());
        userManager = UserManager.getInstance();
        configureToolBar();
        configureDrawerLayout();
        configureNavigationView();
        updateUIWithUserData();
        loadMapViewFragment();

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
            else if (idRessource== R.id.activity_home_drawer_favorite) {
                Toast.makeText(getApplicationContext(), "This feature is coming soon !!!", Toast.LENGTH_SHORT).show();
            }
            else if (idRessource== R.id.activity_home_drawer_your_lunch) {


                FirebaseFirestore db = FirebaseFirestore.getInstance();
                String uid = userManager.getCurrentUser().getUid();
                Date date_of_today = new Date();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                String stringDate= format.format(date_of_today);

                DocumentReference docRef = db.collection("history").document(stringDate).collection("users").document(uid);
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        String placeId = null;
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                placeId = document.getData().get("placeId").toString();
                                extracted(placeId);
                            } else {
                                Toast.makeText(getApplicationContext(), "You haven't decided for your lunch yet !", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });



            } else if (idRessource == R.id.activity_home_drawer_settings) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
            return true;
        });

        /**
         * Initialize Places. For simplicity, the API key is hard-coded. In a production
         * environment we recommend using a secure mechanism to manage API keys.
         */
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), BuildConfig.MAPS_API_KEY);
        }
        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);

    }

    private void extracted(String placeId) {
        Intent intent = new Intent(this, RestaurantDetailsActivity.class);
        intent.putExtra("PLACEID", placeId);
        startActivity(intent);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
    }

    private void updateUIWithUserData(){
        if(userManager.isCurrentUserLogged()){
            FirebaseUser user = userManager.getCurrentUser();
            setInfoUserData(user);
        }

    }

    private void configureToolBar(){
        Toolbar toolbar = b.activityHomeToolbar;
        setSupportActionBar(toolbar);
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