package app.d3v3l.go4lunch.ui;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.Objects;

import app.d3v3l.go4lunch.R;
import app.d3v3l.go4lunch.databinding.ActivityMainBinding;
import app.d3v3l.go4lunch.databinding.ActivityPermissionBinding;

public class PermissionActivity extends AppCompatActivity {

    private ActivityPermissionBinding b;
    private final String permissionAccessFineLocation = Manifest.permission.ACCESS_FINE_LOCATION;
    private final String permissionAccessCoarseLocation = Manifest.permission.ACCESS_COARSE_LOCATION;
    final String[] PERMISSIONS = {permissionAccessFineLocation,permissionAccessCoarseLocation};
    private final int REQUEST_LOCATION_PERMISSION_CODE = 1;
    //private final int AUTO_REQUEST_LOCATION_PERMISSION_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityPermissionBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        AutoCheckPermissions();

        b.ActivityPermissionButtonCloseApplication.setOnClickListener(v -> {
            finishAndRemoveTask();
        });
        b.ActivityPermissionButtonAllowGeolocation.setOnClickListener(v -> {
            askPermissions();
        });
        b.ActivityPermissionButtonParameters.setOnClickListener(v -> {
            goToParameters();
        });

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Intent myIntent = new Intent(PermissionActivity.this, PermissionActivity.class);
        PermissionActivity.this.startActivity(myIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        int numberOfPermission = 0;
        if (requestCode == REQUEST_LOCATION_PERMISSION_CODE) {
            // for each permission check if the user granted/denied them
            for (int i = 0, len = permissions.length; i < len; i++) {
                String permission = permissions[i];
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    // user rejected the permission
                    boolean showRationale = shouldShowRequestPermissionRationale(permission);
                    if (!showRationale) {
                        Log.d("XPE","user also CHECKED never ask again");
                        // user CHECKED "never ask again"
                        b.ActivityPermissionTextviewExplanation.setVisibility(View.GONE);
                        b.ActivityPermissionTextviewAlertNeverAskAgain.setVisibility(View.VISIBLE);
                        b.ActivityPermissionTextviewAlert.setVisibility(View.GONE);
                        b.ActivityPermissionButtonAllowGeolocation.setVisibility(View.GONE);
                        b.ActivityPermissionButtonCloseApplication.setVisibility(View.VISIBLE);
                        b.ActivityPermissionButtonParameters.setVisibility(View.VISIBLE);
                    } else if (permissionAccessFineLocation.equals(permission) || permissionAccessCoarseLocation.equals(permission)) {
                        Log.d("XPE_permissionAccessFineLocation","user did NOT check never ask again");
                        // user did NOT check "never ask again"
                        b.ActivityPermissionTextviewExplanation.setVisibility(View.GONE);
                        b.ActivityPermissionTextviewAlertNeverAskAgain.setVisibility(View.GONE);
                        b.ActivityPermissionTextviewAlert.setVisibility(View.VISIBLE);
                        b.ActivityPermissionButtonAllowGeolocation.setVisibility(View.VISIBLE);
                        b.ActivityPermissionButtonCloseApplication.setVisibility(View.VISIBLE);
                        b.ActivityPermissionButtonParameters.setVisibility(View.GONE);

                    }
                } else {
                    numberOfPermission++;
                }
            }
        }

        if (numberOfPermission == permissions.length) {
            goToLoginPage();
        }
    }

    private void AutoCheckPermissions() {
        boolean hasPermission = hasPermissions(PERMISSIONS);
        if (hasPermission) {
            goToLoginPage();
        }
    }

    private void askPermissions() {
        ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_LOCATION_PERMISSION_CODE);
    }

    private boolean hasPermissions(String[] permissions) {
        int numberOfPermission = 0;
        if (permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
                    numberOfPermission++;
                }
            }
        }
        if (numberOfPermission == permissions.length) {
            return true;
        } else {
            return false;
        }

    }

    private void goToLoginPage() {
        Intent myIntent = new Intent(PermissionActivity.this, MainActivity.class);
        PermissionActivity.this.startActivity(myIntent);
    }

    private void goToParameters() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, REQUEST_LOCATION_PERMISSION_CODE);
    }


}