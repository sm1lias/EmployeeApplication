package com.smilias.employeeapplication;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
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

import com.smilias.employeeapplication.databinding.FragmentAttributesBinding;

import java.util.ArrayList;
import java.util.List;

public class AttributesFragment extends Fragment {
    private List<String> myAttributes = new ArrayList<>();
    private FragmentAttributesBinding binding;
    private SQLiteDatabase db;

    public AttributesFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAttributesBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        db = getActivity().openOrCreateDatabase("myDb", Context.MODE_PRIVATE,null);
        db.execSQL("CREATE TABLE IF NOT EXISTS Attribute(attribute TEXT PRIMARY KEY,name TEXT, employees TEXT)");

        binding.btnAddAttribute.setOnClickListener(view1 -> {
            addingAttribute();
        });



        Cursor cursor = db.rawQuery("SELECT attribute, name FROM Attribute",null); //Ψάχνουμε τη βάση αν έχει attributes και τα προσθέτουμε σε μια λίστα την οποία την φορτώνουμε στο listView
        if (cursor.getCount()>0){
            while (cursor.moveToNext()){
                myAttributes.add(cursor.getString(0)+":"+cursor.getString(1));
            }
            ArrayAdapter<String> adapter=new ArrayAdapter<>(getActivity(),R.layout.support_simple_spinner_dropdown_item,myAttributes);
            binding.attributesListView.setAdapter(adapter);
            binding.attributesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String item=binding.attributesListView.getItemAtPosition(i).toString();
                    deletingAttribute(item);
                }
            });
        }




        return view;

    }
    private void addingAttribute(){ // Ανοίγει ένα παράθυρο όπου ο χρήστης μπορεί να προσθέσει ένα attribute αν δεν υπάρχει ήδη
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Create an attribute");


        final EditText input = new EditText(getActivity());

        input.setHint("attribute:value");
        builder.setView(input);


        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String value = input.getText().toString();
                if (value.isEmpty() || !(value.matches("[a-zA-Z]+:{1}+[a-zA-Z]+"))){
                    Toast.makeText(getActivity(), "Nothing added", Toast.LENGTH_SHORT).show();
                } else {
                    String [] values=value.split(":");
                    Cursor cursor = db.rawQuery("SELECT attribute FROM Attribute WHERE attribute=?", new String[]{values[0]});
                    if (cursor.getCount() > 0) {
                        Toast.makeText(getActivity(), "This attribute already exists", Toast.LENGTH_SHORT).show();
                    } else {
                        db.execSQL("INSERT INTO Attribute  VALUES('" + values[0] + "','" + values[1] + "','" + "" + "')");
                        uiReset();
                    }

                }

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void deletingAttribute(String item){ // Διαγράφεται το attribute από τη λίστα ή το κάνουμε μετονομασία εφόσον δώσουμε το ίδιο με άλλο όνομα
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Give a value to rename the attribute or delete it");


        final EditText input = new EditText(getActivity());

        input.setHint(item);
        builder.setView(input);
        String attr[]=item.split(":");


        builder.setPositiveButton("Rename", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String value = input.getText().toString();
                if (value.isEmpty() || !(value.matches("[a-zA-Z]+:{1}+[a-zA-Z]+"))){
                    Toast.makeText(getActivity(), "Nothing changed", Toast.LENGTH_SHORT).show();
                } else {
                    String[] values=value.split(":");
                    ContentValues newName = new ContentValues();
                    newName.put("name", values[1]);
                    db.update("Attribute", newName, "attribute" + "= ?", new String[] {attr[0]});
                    uiReset();
                }

            }
        });
        builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                db.execSQL("DELETE FROM Attribute WHERE attribute=?",new String[]{attr[0]});
                myAttributes.remove(item);
                uiReset();
            }
        });

        builder.show();
    }

    private void uiReset(){
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.myFragment, new AttributesFragment(), "findThisFragment")
                .commit();
    }
}