package app.d3v3l.go4lunch.ui;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import app.d3v3l.go4lunch.R;
import app.d3v3l.go4lunch.databinding.ActivityMainBinding;
import app.d3v3l.go4lunch.databinding.ActivityPermissionBinding;

public class PermissionActivity extends AppCompatActivity {

    private ActivityPermissionBinding b;
    final String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    private ActivityResultContracts.RequestMultiplePermissions multiplePermissionsContract;
    private ActivityResultLauncher<String[]> multiplePermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityPermissionBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        multiplePermissionsContract = new ActivityResultContracts.RequestMultiplePermissions();
        multiplePermissionLauncher = registerForActivityResult(multiplePermissionsContract, isGranted -> {
            if (isGranted.containsValue(false)) {
                Log.d("XPERMISSIONS", "Permissions was not granted >> close application");
                b.ActivityPermissionTextviewExplanation.setVisibility(View.GONE);
                b.ActivityPermissionTextviewAlert.setVisibility(View.VISIBLE);
                b.ActivityPermissionButtonCloseApplication.setVisibility(View.VISIBLE);
            } else {
                goToLoginPage();
            }
        });

        AutoCheckPermissions();
        b.ActivityPermissionButtonCloseApplication.setOnClickListener(v -> {
            finishAndRemoveTask();
        });

    }

    private void AutoCheckPermissions() {
        if (hasPermissions(PERMISSIONS)) {
            goToLoginPage();
        } else {
            b.ActivityPermissionButtonAllowGeolocation.setOnClickListener(v -> {
                askPermissions(multiplePermissionLauncher);
            });
        }
    }

    private void askPermissions(ActivityResultLauncher<String[]> multiplePermissionLauncher) {
        if (!hasPermissions(PERMISSIONS)) {
            multiplePermissionLauncher.launch(PERMISSIONS);
        } else {
            Log.d("XPERMISSIONS", "All permissions are already granted");
            goToLoginPage();
        }
    }

    private boolean hasPermissions(String[] permissions) {
        if (permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    Log.d("XPERMISSIONS--", "Permission is not granted: ");
                    return false;
                }
                Log.d("XPERMISSIONS", "Permission already granted: ");
            }
            return true;
        }
        return false;
    }

    private void goToLoginPage() {
        Intent myIntent = new Intent(PermissionActivity.this, MainActivity.class);
        PermissionActivity.this.startActivity(myIntent);
    }


}