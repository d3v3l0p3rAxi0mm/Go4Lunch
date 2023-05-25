package app.d3v3l.go4lunch.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import app.d3v3l.go4lunch.ui.permissions.LocationPermissionActivity;


public abstract class BaseActivity extends AppCompatActivity {

    private final String permissionAccessFineLocation = Manifest.permission.ACCESS_FINE_LOCATION;
    private final String permissionAccessCoarseLocation = Manifest.permission.ACCESS_COARSE_LOCATION;
    final String[] PERMISSIONS_LOCATION = {permissionAccessFineLocation, permissionAccessCoarseLocation};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkForPermissions();
    }

    private void checkForPermissions() {
        if (!hasPermissions(PERMISSIONS_LOCATION)) {
            Intent myIntent = new Intent(BaseActivity.this, LocationPermissionActivity.class);
            BaseActivity.this.startActivity(myIntent);
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


}