package com.inc.mountzoft.goldencompass;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static java.lang.String.valueOf;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        PlaceAutocompleteFragment places= (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        places.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {

                String address = valueOf(place.getAddress());
                getLocationFromAddress(address);
            }

            @Override
            public void onError(Status status) {

                Toast.makeText(getApplicationContext(),status.toString(),Toast.LENGTH_SHORT).show();

            }
        });



    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }
    public void getAddressFunction(View view) {
        final EditText latitudeEditText = (EditText) findViewById(R.id.latitude);
        String latitudeInput = latitudeEditText.getText().toString();
        final EditText longitudeEditText = (EditText) findViewById(R.id.longitude);
        String longitudeInput = longitudeEditText.getText().toString();

        double latitudeInputDecimal, longitudeInputDecimal;

        if(latitudeInput.isEmpty())
            Toast.makeText(MapsActivity.this, "Latitude field is empty !", Toast.LENGTH_SHORT).show();
        else if(longitudeInput.isEmpty())
            Toast.makeText(MapsActivity.this, "Longitude field is empty !", Toast.LENGTH_SHORT).show();
        else {
            latitudeInputDecimal = Double.valueOf(latitudeInput);
            longitudeInputDecimal = Double.valueOf(longitudeInput);
            if (latitudeInputDecimal > 82 || latitudeInputDecimal < -90) {
                Toast.makeText(MapsActivity.this, "Latitude invalid", Toast.LENGTH_SHORT).show();
            } else if (longitudeInputDecimal > 180 || longitudeInputDecimal < -180) {
                Toast.makeText(MapsActivity.this, "Longitude invalid", Toast.LENGTH_SHORT).show();
            } else{
                mMap.clear();
                LatLng location = new LatLng(latitudeInputDecimal, longitudeInputDecimal);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(11.0f));

                Circle circle = mMap.addCircle(new CircleOptions()
                    .center(new LatLng(latitudeInputDecimal, longitudeInputDecimal))
                    .radius(5000)
                    .strokeColor(Color.GREEN)
                    .fillColor(0x3500ff00));
                try {
                    Geocoder geocoder;
                    List<Address> addresses;
                    geocoder = new Geocoder(this, Locale.getDefault());

                    addresses = geocoder.getFromLocation(latitudeInputDecimal, longitudeInputDecimal, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                    String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    String city = addresses.get(0).getLocality();
                    String country = addresses.get(0).getCountryName();

                    TextView instantAddress = (TextView) this.findViewById(R.id.address);
                    instantAddress.setText(address + ", " + country);

                   mMap.addMarker(new MarkerOptions().position(location).title(address));

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void getLocationFromAddress(String strAddress) {

        Geocoder coder = new Geocoder(this);
        List<Address> address;

        try {
            address = coder.getFromLocationName(strAddress,5);
            if (address==null) {
                return;
            }
            Address adderssLocation=address.get(0);
            double latitudeInputDecimal = adderssLocation.getLatitude();
            double longitudeInputDecimal = adderssLocation.getLongitude();


            mMap.clear();
            LatLng location = new LatLng(latitudeInputDecimal, longitudeInputDecimal);
            mMap.addMarker(new MarkerOptions().position(location).title(strAddress));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
            mMap.animateCamera( CameraUpdateFactory.zoomTo( 11.0f ) );

            Circle circle = mMap.addCircle(new CircleOptions()
                    .center(new LatLng(latitudeInputDecimal, longitudeInputDecimal))
                    .radius(5000)
                    .strokeColor(Color.GREEN)
                    .fillColor(0x3500ff00));
            TextView instantAddress = (TextView) this.findViewById(R.id.address);
            instantAddress.setText(strAddress+"\nLatitude : "+String.valueOf(latitudeInputDecimal)+"\n Longitude : "+String.valueOf(longitudeInputDecimal));

        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
