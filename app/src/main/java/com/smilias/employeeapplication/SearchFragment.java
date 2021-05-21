package com.smilias.employeeapplication;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.smilias.employeeapplication.databinding.FragmentSearchBinding;

import java.util.ArrayList;
import java.util.List;


public class SearchFragment extends Fragment {


    private FragmentSearchBinding binding;
    private List<String> attributeList=new ArrayList<>();
    private List<String> employeeList;
    private SQLiteDatabase db;
    private String itemApplied=null;

    public SearchFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        db = getActivity().openOrCreateDatabase("myDb", Context.MODE_PRIVATE,null);
        db.execSQL("CREATE TABLE IF NOT EXISTS Attribute(attribute TEXT PRIMARY KEY,name TEXT, employees TEXT)");

        Cursor cursor = db.rawQuery("SELECT * FROM Attribute",null);
        if (cursor.getCount()>0){
            while (cursor.moveToNext()){
                attributeList.add(cursor.getString(0)+":"+cursor.getString(1));
            }
        }
        if (!(attributeList.isEmpty())){
            ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, attributeList);
            adapter.setDropDownViewResource(
                    android.R.layout
                            .simple_spinner_dropdown_item);
            binding.spinnerSearchAttribute.setAdapter(adapter);
            binding.spinnerSearchAttribute.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    itemApplied=binding.spinnerSearchAttribute.getSelectedItem().toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }

        binding.btnSearch.setOnClickListener(view1 -> {
            searchEmployee(itemApplied);
        });
        return view;
    }

    private void searchEmployee(String item) { //Ψάχνει τη βάση για τους εργαζομένους με το επιλεγμένο attributes τους βάζει σε μία λίστα και τους φορτώνει στο listView
        this.employeeList=new ArrayList<>();
        if (item!=null){
            String[] newItem=item.split(":");
            String employeeList = null;

            Cursor c=db.rawQuery("SELECT employees FROM Attribute WHERE attribute=?", new String[]{newItem[0]});

            if (c.getCount() > 0) {
                while (c.moveToNext()) {
                    employeeList = c.getString(0);
                }
            }
            if (employeeList!=null) {
                String[] myArray = employeeList.split("-");
                for (String x : myArray) {
                    if (!(this.employeeList.contains(x)) && !(x.equals(""))) this.employeeList.add(x);
                }
                ArrayAdapter<String> adapter=new ArrayAdapter<>(getActivity(),R.layout.support_simple_spinner_dropdown_item,this.employeeList);
                binding.listViewSearchEmployee.setAdapter(adapter);
                binding.listViewSearchEmployee.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        String name=binding.listViewSearchEmployee.getItemAtPosition(i).toString();
                        Intent intent=new Intent(getActivity(),MapsActivity.class);
                        intent.putExtra("empName", name);
                        startActivity(intent);
                    }
                });
            }
        }
    }
}