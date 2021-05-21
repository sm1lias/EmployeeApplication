package com.smilias.employeeapplication;

import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String name;
    private SQLiteDatabase db;
    private List<Employee> employeeList = new ArrayList<>();
    private Employee employee;
    private List<LatLng> points;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        name = getIntent().getExtras().getString("empName", null);

        db = openOrCreateDatabase("myDb", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS Employee(name TEXT,birthDate TEXT, hasCar TEXT, address TEXT)");

        fillList();

    }

    private void fillList() {
        Cursor cursor = db.rawQuery("SELECT * FROM Employee", null); // Παίρνει όλους τους εργαζόμενους και τους βάζει σε μια λίστα
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                employee = new Employee();
                employee.setName(cursor.getString(0));
                employee.setBirthDate(cursor.getString(1));
                employee.setHasCar(Boolean.parseBoolean(cursor.getString(2)));
                employee.setAddress(cursor.getString(3));
                employeeList.add(employee);
            }
        }
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
        double lat;
        double lon;
        String[] array;
        LatLng coordinates;

        for (Employee x : this.employeeList) { // Παίρνει τις συντεταγμένες από τους εργαζομένους και φτιάχνει τα σημάδια στον χάρτη
            array = x.getAddress().split(",");
            lat = Double.parseDouble(array[0].trim());
            lon = Double.parseDouble(array[1].trim());
            coordinates = new LatLng(lat, lon);
            if (x.getName().equals(name)) {
                mMap.addMarker(new MarkerOptions()
                        .position(coordinates)
                        .title(x.getName())
                        .icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(coordinates));
            } else
                mMap.addMarker(new MarkerOptions().position(coordinates).title(x.getName()));
        }
        createPoints();
        for (int i = 0; i < points.size() - 1; i++) { //Ενώνουμε τα σημάδια με γραμμές
            LatLng src = points.get(i);
            LatLng dest = points.get(i + 1);

            // mMap is the Map Object
            Polyline line = mMap.addPolyline(
                    new PolylineOptions().add(
                            new LatLng(src.latitude, src.longitude),
                            new LatLng(dest.latitude,dest.longitude)
                    ).width(2).color(Color.BLUE).geodesic(true)
            );
        }

    }

    private void createPoints() { // Φτιάχνουμε μια λίστα με τις συντεταγμένες
        points = new ArrayList<>();

        String[] array;
        double lat;
        double lon;
        LatLng coordinates;

        for (Employee emp : this.employeeList) {
            array = emp.getAddress().split(",");
            lat = Double.parseDouble(array[0].trim());
            lon = Double.parseDouble(array[1].trim());
            coordinates = new LatLng(lat, lon);
            this.points.add(coordinates);
        }
    }
}