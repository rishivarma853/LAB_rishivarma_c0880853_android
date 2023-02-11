package ca.lcit22f.rishivarma853.lab_rishivarma_c0880853_android;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private ListView list_FavouritePlaces;
    Button btnAddFavouritePlace;
    private static final String SHARED_PREF_NAME = "places_list";
    private static final String ADDRESSES = "addresses";

    private static final String LATITUDES = "latitudes";

    private static final String LONGITUDES = "longitudes";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list_FavouritePlaces = findViewById(R.id.list_favourite_places);
        btnAddFavouritePlace = findViewById(R.id.btn_add_favourite_place);

        FavouritePlaces.initialize();
        IntentModel.ListToMap.clear();
        IntentModel.MapToList.clear();
        loadAddressesFromSharedPreferences();
        loadLatitudesFromSharedPreferences();
        loadLongitudesFromSharedPreferences();
        updateList();

        btnAddFavouritePlace.setOnClickListener(v -> {
            IntentModel.ListToMap.clear();
            IntentModel.ListToMap.addNewPlace = true;
            startActivity(new Intent(MainActivity.this, MapsActivity.class));
        });

        list_FavouritePlaces.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                IntentModel.ListToMap.clear();
                IntentModel.ListToMap.updatePlace = true;
                IntentModel.ListToMap.Place.id = i;
                IntentModel.ListToMap.Place.address = FavouritePlaces.addresses.get(i);
                IntentModel.ListToMap.Place.latitude = FavouritePlaces.latitudes.get(i);
                IntentModel.ListToMap.Place.longitude = FavouritePlaces.latitudes.get(i);
                IntentModel.MapToList.clear();
                startActivity(new Intent(MainActivity.this, MapsActivity.class));
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        if(IntentModel.MapToList.addedNewPlace) {
            FavouritePlaces.addresses.add(IntentModel.MapToList.Place.address);
            FavouritePlaces.latitudes.add(IntentModel.MapToList.Place.latitude);
            FavouritePlaces.longitudes.add(IntentModel.MapToList.Place.longitude);
            IntentModel.MapToList.clear();
        }
        else if(IntentModel.MapToList.updatedPlace) {
            int id = IntentModel.MapToList.Place.id;
            FavouritePlaces.addresses.set(id, IntentModel.MapToList.Place.address);
            FavouritePlaces.latitudes.set(id, IntentModel.MapToList.Place.latitude);
            FavouritePlaces.longitudes.set(id, IntentModel.MapToList.Place.longitude);
            IntentModel.MapToList.clear();
        }
        updateList();
    }

    @Override
    public void onStop() {
        super.onStop();
        saveAddressesToSharedPreferences();
        saveLatitudesToSharedPreferences();
        saveLongitudesToSharedPreferences();
    }

    private void loadAddressesFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        Set<String> addressesSet = sharedPreferences.getStringSet(ADDRESSES, null);
        if (addressesSet != null) {
            FavouritePlaces.addresses = new ArrayList<>(addressesSet);
        } else {
            FavouritePlaces.addresses = new ArrayList<>();
        }
    }

    private void loadLatitudesFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        Set<String> latitudesSet = sharedPreferences.getStringSet(LATITUDES, null);
        if (latitudesSet != null) {
            FavouritePlaces.latitudes = new ArrayList<>(latitudesSet);
        } else {
            FavouritePlaces.latitudes = new ArrayList<>();
        }
    }

    private void loadLongitudesFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        Set<String> longitudesSet = sharedPreferences.getStringSet(LONGITUDES, null);
        if (longitudesSet != null) {
            FavouritePlaces.longitudes = new ArrayList<>(longitudesSet);
        } else {
            FavouritePlaces.longitudes = new ArrayList<>();
        }
    }

    private void saveAddressesToSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> addressesSet = new HashSet<>(FavouritePlaces.addresses);
        editor.putStringSet(ADDRESSES, addressesSet);
        editor.commit();
    }
    private void saveLatitudesToSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> latitudesSet = new HashSet<>(FavouritePlaces.latitudes);
        editor.putStringSet(ADDRESSES, latitudesSet);
        editor.commit();
    }
    private void saveLongitudesToSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> longitudesSet = new HashSet<>(FavouritePlaces.longitudes);
        editor.putStringSet(ADDRESSES, longitudesSet);
        editor.commit();
    }
    private void updateList() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1,
                        FavouritePlaces.addresses);
                list_FavouritePlaces.setAdapter(adapter);
            }
        });
    }
}

class FavouritePlaces {
    public static ArrayList<String> addresses;
    public static ArrayList<String> latitudes;
    public static ArrayList<String> longitudes;

    public static void initialize() {
        addresses = new ArrayList<>();
        latitudes = new ArrayList<>();
        longitudes = new ArrayList<>();
    }



}