package app.d3v3l.go4lunch.ui.permissions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;

import app.d3v3l.go4lunch.databinding.ActivityLocationPermissionBinding;

public class LocationPermissionActivity extends AppCompatActivity {

    private ActivityLocationPermissionBinding b;
    private final String permissionAccessFineLocation = Manifest.permission.ACCESS_FINE_LOCATION;
    private final String permissionAccessCoarseLocation = Manifest.permission.ACCESS_COARSE_LOCATION;
    final String[] PERMISSIONS_LOCATION = {permissionAccessFineLocation,permissionAccessCoarseLocation};
    private final int REQUEST_LOCATION_PERMISSION_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityLocationPermissionBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        b.ActivityPermissionButtonAllowGeolocation.setOnClickListener(v -> {
            ActivityCompat.requestPermissions(this, PERMISSIONS_LOCATION, REQUEST_LOCATION_PERMISSION_CODE);
        });

        b.ActivityPermissionButtonParameters.setOnClickListener(v -> {
            goToParameters();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkForPermissions();
    }

    private void checkForPermissions() {
        if (hasPermissions(PERMISSIONS_LOCATION)) {
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        int nbOfPermission = 0;
        int nbNotShowRationale = 0;
        int nbShowRationale = 0;
        if (requestCode == REQUEST_LOCATION_PERMISSION_CODE) {
            // for each permission check if the user granted/denied them
            for (int i = 0, len = permissions.length; i < len; i++) {
                String permission = permissions[i];
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    // user rejected the permission
                    boolean showRationale = shouldShowRequestPermissionRationale(permission);
                    if (!showRationale) {
                        nbNotShowRationale++;

                    } else if (permissionAccessFineLocation.equals(permission) || permissionAccessCoarseLocation.equals(permission)) {
                        nbShowRationale++;
                    }
                } else {
                    nbOfPermission++;
                }
            }
        }
        if (nbOfPermission == permissions.length) {
            finish();
        } else {
            if (nbNotShowRationale > 0) {
                b.ActivityPermissionTextviewExplanation.setVisibility(View.GONE);
                b.ActivityPermissionTextviewAlertNeverAskAgain.setVisibility(View.VISIBLE);
                b.ActivityPermissionTextviewAlert.setVisibility(View.GONE);
                b.ActivityPermissionButtonAllowGeolocation.setVisibility(View.GONE);
                b.ActivityPermissionButtonParameters.setVisibility(View.VISIBLE);
            } else {
                if (nbShowRationale > 0) {
                    b.ActivityPermissionTextviewExplanation.setVisibility(View.GONE);
                    b.ActivityPermissionTextviewAlertNeverAskAgain.setVisibility(View.GONE);
                    b.ActivityPermissionTextviewAlert.setVisibility(View.VISIBLE);
                    b.ActivityPermissionButtonAllowGeolocation.setVisibility(View.VISIBLE);
                    b.ActivityPermissionButtonParameters.setVisibility(View.GONE);
                }
            }
        }



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

    private void goToParameters() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, REQUEST_LOCATION_PERMISSION_CODE);
    }

}