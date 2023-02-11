package ca.lcit22f.rishivarma853.lab_rishivarma_c0880853_android;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Geocoder geocoder;
    //private MarkerOptions markerOptions;
    private static final int REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private ArrayList<String> addressesList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        geocoder = new Geocoder(this, Locale.getDefault());
        fusedClient = LocationServices.getFusedLocationProviderClient(this);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if(IntentModel.ListToMap.addNewPlace) {
            if (!hasLocationPermission())
                requestLocationPermission();
            else
                startUpdateLocation();
            Location location = getLastKnownLocation();
            if(location != null) {
                LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.addMarker(new MarkerOptions().position(userLocation).title("You're here"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
            }
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(@NonNull LatLng latLng) {
                    if(hasLocationPermission()) {
                        stopUpdateLocation();
                    }
                }
            });
        }
        else if(IntentModel.ListToMap.updatePlace) {
            LatLng latLng = new LatLng(Double.parseDouble(IntentModel.ListToMap.Place.latitude),
                    Double.parseDouble(IntentModel.ListToMap.Place.longitude));
            mMap.addMarker(new MarkerOptions().position(latLng).title(IntentModel.ListToMap.Place.address));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
        }
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                if(IntentModel.ListToMap.addNewPlace) {
                    mMap.clear();
                    Marker marker = mMap.addMarker(new MarkerOptions().position(latLng));
                    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                    List<Address> addresses;
                    String markerAddress = "";
                    try {
                        addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                        if (addresses.size() > 0) {
                            markerAddress = addresses.get(0).getAddressLine(0);
                            marker.setTitle(markerAddress);
                            addressesList.add(markerAddress);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(MapsActivity.this,"Place Is Pinned", Toast.LENGTH_SHORT).show();
                    IntentModel.MapToList.clear();
                    IntentModel.MapToList.addedNewPlace = true;
                    IntentModel.MapToList.Place.address = markerAddress;
                    IntentModel.MapToList.Place.latitude = String.valueOf(latLng.latitude);
                    IntentModel.MapToList.Place.longitude = String.valueOf(latLng.longitude);
                    IntentModel.ListToMap.clear();
                }
                else if(IntentModel.ListToMap.updatePlace) {
                    mMap.clear();
                    Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).title("Loading address..."));
                    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                    List<Address> addresses;
                    String markerAddress = "";
                    try {
                        addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                        if (addresses.size() > 0) {
                            markerAddress = addresses.get(0).getAddressLine(0);
                            marker.setTitle(markerAddress);
                            addressesList.add(markerAddress);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(MapsActivity.this,"Place Is Pinned", Toast.LENGTH_SHORT).show();
                    IntentModel.MapToList.clear();
                    IntentModel.MapToList.updatedPlace = true;
                    IntentModel.MapToList.Place.id = IntentModel.ListToMap.Place.id;
                    IntentModel.MapToList.Place.address = markerAddress;
                    IntentModel.MapToList.Place.latitude = String.valueOf(latLng.latitude);
                    IntentModel.MapToList.Place.longitude = String.valueOf(latLng.longitude);
                    IntentModel.ListToMap.clear();
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        startUpdateLocation();
        String message = "";
        if(IntentModel.MapToList.addedNewPlace) {
            message = "New Favourite Place Added";
        } else if(IntentModel.MapToList.updatedPlace) {
            message = "Favourite Place Updated";
        }
        Toast.makeText(MapsActivity.this, message, Toast.LENGTH_LONG).show();
    }
    private Location getLastKnownLocation() {
        final Location[] location = {null};
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        fusedClient.getLastLocation().addOnSuccessListener(loc -> {
            if (loc != null) {
                LatLng latLng = new LatLng(loc.getLatitude(), loc.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                location[0] = loc;
            }
        });
        return location[0];
    }
    private void startUpdateLocation() {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(2000);
        locationRequest.setFastestInterval(1000);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                mMap.clear();
                if (locationResult != null) {
                    Location location = locationResult.getLastLocation();
                    LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(userLocation).title("your location!"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
                }
            }
        };
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }
    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }
    private boolean hasLocationPermission() {
        return ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
    private void stopUpdateLocation() {
        if (fusedClient != null) {
            fusedClient.removeLocationUpdates(locationCallback);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setMessage("The permission is mandatory")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
                            }
                        }).create().show();
            } else
                startUpdateLocation();
        }
    }
}