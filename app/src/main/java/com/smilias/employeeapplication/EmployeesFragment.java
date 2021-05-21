package com.smilias.employeeapplication;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.smilias.employeeapplication.databinding.FragmentEmployeesBinding;

import java.util.ArrayList;
import java.util.List;

public class EmployeesFragment extends Fragment {
    private FragmentEmployeesBinding binding;
    private List<String> employeeslist = new ArrayList<>();
    private SQLiteDatabase db;

    public EmployeesFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEmployeesBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        db = getActivity().openOrCreateDatabase("myDb", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS Employee(name TEXT,birthDate TEXT, hasCar TEXT, address TEXT)");

        binding.btnAddEmployee.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), AddEmployeeActivity.class);
            startActivity(intent);
        });

        Cursor cursor = db.rawQuery("SELECT name FROM Employee", null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                employeeslist.add(cursor.getString(0));
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, employeeslist);
            binding.employeesListView.setAdapter(adapter);
            binding.employeesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String item = binding.employeesListView.getItemAtPosition(i).toString();
                    Intent intent = new Intent(getActivity(), EditEmployeesActivity.class);
                    intent.putExtra("employeeName", item);
                    startActivity(intent);
                }
            });
        }
        return view;
    }


    private void uiReset() {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.myFragment, new EmployeesFragment(), "findThisFragment")
                .commit();
    }
}