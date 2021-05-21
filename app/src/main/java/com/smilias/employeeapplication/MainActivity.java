package com.smilias.employeeapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.smilias.employeeapplication.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private SQLiteDatabase db;
    private Fragment selectedFragment = new AttributesFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        String intentFragment = null; //Τσεκάρουμε αν ερχόμαστε από συγκεκρίμενα activities για να ανοίξουμε το EmployeesFragment
        try {
            intentFragment = getIntent().getExtras().getString("fragment", null);
            if (intentFragment.equals("employeeFragment"))
                selectedFragment = new EmployeesFragment();
        } catch (Exception e) {
            e.printStackTrace();
        }

        db = openOrCreateDatabase("myDb", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS Attribute(attribute TEXT PRIMARY KEY,name TEXT, employees TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS Employee(name TEXT,birthDate TEXT, hasCar TEXT, address TEXT)");

        binding.bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        if (intentFragment != null) {
            binding.bottomNavigationView.setSelectedItemId(R.id.menuEmployees);
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.myFragment, selectedFragment).commit();

    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            // By using switch we can easily get
            // the selected fragment
            // by using there id.

            switch (item.getItemId()) {
                case R.id.menuAttributes:
                    selectedFragment = new AttributesFragment();
                    break;
                case R.id.menuEmployees:
                    selectedFragment = new EmployeesFragment();
                    break;
                case R.id.menuSearch:
                    selectedFragment = new SearchFragment();
                    break;
                default:
                    break;
            }
            // It will help to replace the
            // one fragment to other.
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.myFragment, selectedFragment)
                    .commit();
            return true;
        }
    };
}