package com.example.ride;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
    //Tablica z koordynatami lat i lng trasy
    ArrayList<LatLng> coordList  = new ArrayList<LatLng>();

    //Przekazanie danych z Jsona do stringa
    public class DownloadTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... urls) {
            String json = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();

                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);
                int data = reader.read();
                //-1 koniec stringa - end of file
                while(data != -1){
                    char letter = (char) data;
                    json += letter;
                    data = reader.read();
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return json;
        }

        //Wyciągniecie danych z Jsona odnośnie samej trasy
        @Override
        protected void onPostExecute(String json) {
            super.onPostExecute(json);

            try {
                //przekształcanie na tekst z jsona
                JSONObject jsonObject = new JSONObject(json);
                //"[" - use getJSONArray
                //"{" - use getJSONObject
                JSONArray routesArray = jsonObject.getJSONArray("routes");
                JSONObject arrayObject = routesArray.getJSONObject(0);
                JSONArray legsArray = arrayObject.getJSONArray("legs");
                JSONObject legsObject = legsArray.getJSONObject(0);
                JSONArray stepArray = legsObject.getJSONArray("steps");

                for(int i = 0; i < stepArray.length(); i++){
                    JSONObject stepsObject = stepArray.getJSONObject(i);
                    JSONObject startLocation = stepsObject.getJSONObject("start_location");
                    JSONObject endLocation= stepsObject.getJSONObject("end_location");

                    Double punktStartowyLat = startLocation.getDouble("lat");
                    Double punktStartowyLng = startLocation.getDouble("lng");
                    Double punktKoncowyLat = endLocation.getDouble("lat");
                    Double punktKoncowyLng = endLocation.getDouble("lng");

                    //Log.i("punkt startowy lat: ",Double.toString(punktStartowyLat));
                    //Log.i("punkt startowy lng: ",Double.toString(punktStartowyLng));
                    //Log.i("punkt koncowy lat: ",Double.toString(punktKoncowyLat));
                    //Log.i("punkt koncowy lng: ",Double.toString(punktKoncowyLng));

                    for (int j = 0; j < stepArray.length(); j++) {
                        coordList.add(new LatLng(punktStartowyLat,punktStartowyLng));
                        coordList.add(new LatLng(punktKoncowyLat,punktKoncowyLng));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    //Metoda zezwalająca na dostęp do usług lokalizacyjnych
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 20000, 0, locationListener);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Pobieranie danych o trasie z direction API
        getRoute("Chylonia, Gdynia", "Rezerwat przyrody Cisowa, Gdynia");

        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void getRoute(String startRoute, String endRoute){

        //Pobieranie danych o trasie z direction API
        DownloadTask task = new DownloadTask();
        task.execute("https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=" + startRoute +
                "&destination=" + endRoute +
                //"&waypoints=Łężyce, 84-207" +
                "&mode=bicycling" +
                "&key=AIzaSyDAqU8VuZm3-D8hzdd9Uk_pXrvb9h0skI8");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Ustawienie typu mapy np. teren, satelita (terrain, satellite)
        //mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                // Toast.makeText(MapsActivity.this, location.toString(), Toast.LENGTH_LONG).show();
                // Add a marker in myLocation and move the camera
                //mMap.clear();

                //Metoda ustawia marker na naszej lokalizacji
                LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.addMarker(
                        new MarkerOptions()
                                .position(myLocation)
                                .title("Tutaj jestem")
                                //Change colour of marker
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
                );

                //Metoda rysuje trasę na podstawie danych z Jsona
                List<LatLng> points = coordList; // list of latlng
                for (int i = 0; i < points.size() - 1; i++) {
                    LatLng src = points.get(i);
                    LatLng dest = points.get(i + 1);

                    // mMap is the Map Object
                    Polyline line = mMap.addPolyline(
                            new PolylineOptions().add(
                                    new LatLng(src.latitude, src.longitude),
                                    new LatLng(dest.latitude,dest.longitude)
                            ).width(4).color(Color.BLUE).geodesic(true)
                    );
                }
                //Metoda rysuje trasę(linie)
//                mMap.addPolyline(new PolylineOptions()
//                        .clickable(true)
//                        .add(
//                                new LatLng(54.5413882, 18.4728713),
//                                new LatLng(54.54018360000001, 18.4723871)
//                                ));
//                newLatLngZoom - create zoom in your map, 0 - 20

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }
        };

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 20000, 0, locationListener);
        }
    }
}