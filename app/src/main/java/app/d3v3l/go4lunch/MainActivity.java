package app.d3v3l.go4lunch;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.snackbar.Snackbar;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import app.d3v3l.go4lunch.databinding.ActivityMainBinding;
import app.d3v3l.go4lunch.manager.UserManager;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding b;
    private static final int RC_SIGN_IN = 123;
    private final UserManager userManager = UserManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        setupListeners();
    }


    private void setupListeners(){
        // Email Authentification
        b.ActivityMainButtonSignInWithEmmail.setOnClickListener(view -> {
            if(userManager.isCurrentUserLogged()){
                startHomeActivity();
            }else{
                startSignInActivity("email");
            }
        });
        // Google Authentification
        b.ActivityMainButtonSignInWithGoogle.setOnClickListener(v -> {
            if(userManager.isCurrentUserLogged()){
                startHomeActivity();
            }else{
                startSignInActivity("google");
            }
        });

    }

    private void startSignInActivity(String authType){

        List<AuthUI.IdpConfig> providers;
        if (Objects.equals(authType, "email")) {
            providers = Collections.singletonList(new AuthUI.IdpConfig.EmailBuilder().build());
        } else if (Objects.equals(authType, "google")) {
            providers = Collections.singletonList(new AuthUI.IdpConfig.GoogleBuilder().build());
        } else {
            providers = null;
        }

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTheme(R.style.Theme_Go4Lunch)
                        .setAvailableProviders(providers)
                        .setIsSmartLockEnabled(false, true)
                        .setLogo(R.drawable.baseline_fingerprint_24)
                        .build(),
                RC_SIGN_IN);
    }

    private void startHomeActivity() {
        Intent myIntent = new Intent(MainActivity.this, HomeActivity.class);
        MainActivity.this.startActivity(myIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.handleResponseAfterSignIn(requestCode, resultCode, data);
    }

    // Show Snack Bar with a message
    private void showSnackBar( String message){
        Snackbar.make(b.mainLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    // Method that handles response after SignIn Activity close
    private void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data){
        IdpResponse response = IdpResponse.fromResultIntent(data);
        if (requestCode == RC_SIGN_IN) {
            // SUCCESS
            if (resultCode == RESULT_OK) {
                //userManager.createUser();
                startHomeActivity();
                //showSnackBar(getString(R.string.connection_succeed));
            } else {
                // ERRORS
                if (response == null) {
                    showSnackBar(getString(R.string.error_authentication_canceled));
                } else if (response.getError()!= null) {
                    if(response.getError().getErrorCode() == ErrorCodes.NO_NETWORK){
                        showSnackBar(getString(R.string.error_no_internet));
                    } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                        showSnackBar(getString(R.string.error_unknown_error));
                    }
                }
            }
        }
    }



}