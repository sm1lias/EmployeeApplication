package com.smilias.employeeapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.common.util.ArrayUtils;
import com.smilias.employeeapplication.databinding.ActivityEditEmployeesBinding;

import java.util.ArrayList;
import java.util.List;

public class EditEmployeesActivity extends AppCompatActivity {
    private SQLiteDatabase db;
    private Employee employee;
    private ActivityEditEmployeesBinding binding;
    private List<String> myAttrList=new ArrayList<>();
    private List<String> employeeAttrList=new ArrayList<>();
    private String itemDatabase;
    private String itemApplied;
    private boolean result;
    private String previousName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditEmployeesBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        String name = getIntent().getExtras().getString("employeeName", null); //Παίρνουμε από το προηγούμενο screen το όνομα που πατήθηκε

        db = openOrCreateDatabase("myDb", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS Attribute(attribute TEXT PRIMARY KEY,name TEXT, employees TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS Employee(name TEXT,birthDate TEXT, hasCar TEXT, address TEXT)");

        Cursor cursor = db.rawQuery("SELECT * FROM Employee WHERE name=?",new String[]{name}); //Παίρνουμε από τη βάση τον εργαζόμενο σύμφωνα με το όνομα
        if (cursor.getCount() > 0) {
            employee=new Employee();

            while (cursor.moveToNext()) {
                employee.setName(cursor.getString(0));
                employee.setBirthDate(cursor.getString(1));
                employee.setHasCar(Boolean.parseBoolean(cursor.getString(2)));
                employee.setAddress(cursor.getString(3));
            }

        }
        binding.editName.setText(employee.getName());
        binding.editBirthDate.setText(employee.getBirthDate());
        binding.editAddress.setText(employee.getAddress());

        if (employee.getHasCar()) {
            result=true;
            binding.rbtnYes.setChecked(true);
        } else {
            result=false;
            binding.rbtnNo.setChecked(true);
        }

        Cursor cursor3 = db.rawQuery("SELECT * FROM Attribute", null); // Φτιάχνουμε τη λίστα για το spinner
        if (cursor3.getCount() > 0) {
            while (cursor3.moveToNext()) {
                String check=cursor3.getString(2);
                if (check!=null) {
                    String[] arrayCheck=check.split("-");
                    for (String x: arrayCheck){
                        if (x.equals(employee.getName())) employeeAttrList.add(cursor3.getString(0)+":"+cursor3.getString(1));
                    }
                }


            }
        }
        if (!(employeeAttrList.isEmpty())) {
            ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, employeeAttrList);
            adapter.setDropDownViewResource(
                    android.R.layout
                            .simple_spinner_dropdown_item);
            binding.spinnerEmployeeAttributes.setAdapter(adapter);
            binding.spinnerEmployeeAttributes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    itemApplied=binding.spinnerEmployeeAttributes.getSelectedItem().toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }

        Cursor cursor2 = db.rawQuery("SELECT attribute,name FROM Attribute", null); // Φτιάχνουμε τη λίστα για το spinner
        if (cursor2.getCount() > 0) {
            while (cursor2.moveToNext()) {
                String isIn=cursor2.getString(0)+":"+cursor2.getString(1);
                if(employeeAttrList.contains(isIn)) continue;
                myAttrList.add(cursor2.getString(0)+":"+cursor2.getString(1));
            }
        }
        if (!(myAttrList.isEmpty())) {
            ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, myAttrList);
            adapter.setDropDownViewResource(
                    android.R.layout
                            .simple_spinner_dropdown_item);
            binding.spinnerDatabaseAttributes.setAdapter(adapter);
            binding.spinnerDatabaseAttributes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    itemDatabase=binding.spinnerDatabaseAttributes.getSelectedItem().toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }

        binding.rbGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.rbtnNo) result = false;
                else result = true;
            }
        });

        binding.btnAddAttributeEmployee.setOnClickListener(view1 -> {
            if (itemDatabase != null) addAttributeEmployee(itemDatabase);
        });

        binding.btnDeleteAttributeFromEmployee.setOnClickListener(view2 ->{
            if (itemApplied!=null) deleteAttributeEmployee(itemApplied);
        });

        binding.btnFinishEdit.setOnClickListener(view3 ->{
            finishEdit();
        });

        binding.btnDeleteEmp.setOnClickListener(view4 ->{
            deleteEmployee();
        });



    }
    private void goToMainActivity(){
        Intent intent=new Intent(EditEmployeesActivity.this, MainActivity.class);
        intent.putExtra("fragment", "employeeFragment");
        startActivity(intent);
    }

    private void deleteEmployee() { // Διαγράφει τον εργαζόμενο από τους πίνακες Employee και Attribute όπου είχε attribute
        try {
            String employeeList = null;
            String[] newItem;
            db.execSQL("DELETE FROM Employee WHERE name=?", new String[]{employee.getName()});
            if (!(this.employeeAttrList.isEmpty())) {
                for (String x : this.employeeAttrList) {
                    newItem = x.split(":");
                    Cursor c = db.rawQuery("SELECT employees FROM Attribute WHERE attribute=?", new String[]{newItem[0]});

                    if (c.getCount() > 0) {
                        while (c.moveToNext()) {
                            Toast.makeText(this, c.getString(0), Toast.LENGTH_SHORT).show();
                            employeeList = c.getString(0);
                        }
                    }
                    if (employeeList != null) {
                        String[] myArray = employeeList.split("-");
                        int i = 0;
                        for (String s : myArray) {
                            if (s.equals(employee.getName())) {
                                myArray[i] = null;
                            }
                            i++;
                        }
                        String newValue = null;
                        for (String s : myArray) {
                            if (s != null) {
                                newValue = "-" + s;
                            }
                        }
                        ContentValues newEmployees = new ContentValues();
                        newEmployees.put("employees", newValue);
                        db.update("Attribute", newEmployees, "attribute" + "= ?", new String[]{newItem[0]});


                    }
                }
            }
            goToMainActivity();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void deleteAttributeEmployee(String itemApplied) { // Διαγράφουμε το attribute από τον εργαζόμενο, παίρνουμε από
        // τη βάση τους εργαζομένους που έχουν το συγκεκριμένo attribute και τη στέλνουμε χωρίς τον εργαζόμενό μας
        String[] newItem=itemApplied.split(":");
        String employeeList = null;


        Cursor c=db.rawQuery("SELECT employees FROM Attribute WHERE attribute=?", new String[]{newItem[0]});

        if (c.getCount() > 0) {
            while (c.moveToNext()) {
                Toast.makeText(this, c.getString(0), Toast.LENGTH_SHORT).show();
                employeeList = c.getString(0);
            }
        }
        if (employeeList!=null){
            String[] myArray=employeeList.split("-");
            int i=0;
            for (String x: myArray){
                if (x.equals(employee.getName())){
                    myArray[i]=null;
                }
                i++;
            }
            String newValue=null;
            for (String x:myArray){
                if (x!=null){
                    newValue="-"+x;
                }
            }
            ContentValues newEmployees = new ContentValues();
            newEmployees.put("employees", newValue);
            db.update("Attribute", newEmployees, "attribute" + "= ?", new String[] {newItem[0]});
        }

        finish();
        startActivity(getIntent());



    }

    private void addAttributeEmployee(String item) { // Προθέτουμε το attribute στον εργαζόμενο, παίρνουμε από
        // τη βάση τους εργαζομένους που έχουν το συγκεκριμένo attribute και τη στέλνουμε μαζί με τον εργαζόμενό μας
        String[] newItem=item.split(":");
        String employeeList = null;

        Cursor c=db.rawQuery("SELECT employees FROM Attribute WHERE attribute=?", new String[]{newItem[0]});

        if (c.getCount() > 0) {
            while (c.moveToNext()) {
                employeeList = c.getString(0);
            }
        }
        boolean isIn=false;
        if (employeeList!=null) {
            String[] myArray = employeeList.split("-");
            for (String x : myArray) {
                if (x.equals(employee.getName())) {
                    isIn = true;
                }
            }
        }
        if (!isIn) {
            ContentValues newEmployees = new ContentValues();
            newEmployees.put("employees", employeeList+employee.getName() + "-");
            db.update("Attribute", newEmployees, "attribute" + "= ?", new String[]{newItem[0]});

            finish();
            startActivity(getIntent());
        }
    }

    private void initialise() {
        previousName=employee.getName();
        employee.setName(binding.editName.getText().toString());
        employee.setBirthDate(binding.editBirthDate.getText().toString());
        employee.setAddress(binding.editAddress.getText().toString());
        employee.setHasCar(result);
    }

    private void finishEdit(){ // Αν πληρούνται τε κριτήρια κάνει  update τη βάση
        initialise();
        if (employee.getName().isEmpty()) {
            binding.editName.setError("Please give a name");
            binding.editName.requestFocus();
            return;
        }
//        else {
//            Cursor cursor = db.rawQuery("SELECT name FROM Employee WHERE name=?", new String[]{employee.getName()});
//            if (cursor.getCount() > 0) {
//                Toast.makeText(this, "Employee with this name already exists", Toast.LENGTH_SHORT).show();
//                return;
//            }
//        }

        if (!(employee.getBirthDate().matches("^([0-2][0-9]|(3)[0-1])(\\/)(((0)[0-9])|((1)[0-2]))(\\/)\\d{4}$"))) {
            binding.editBirthDate.setError("Give a valid date");
            binding.editBirthDate.requestFocus();
            return;
        }

        if (!(employee.getAddress().matches("^[-+]?([1-8]?\\d(\\.\\d+)?|90(\\.0+)?),\\s*[-+]?(180(\\.0+)?|((1[0-7]\\d)|([1-9]?\\d))(\\.\\d+)?)$"))) {
            binding.editAddress.setError("Give a valid coordinates");
            binding.editAddress.requestFocus();
            return;
        }

        ContentValues cv = new ContentValues();
        cv.put("name",employee.getName());
        cv.put("birthDate",employee.getBirthDate());
        cv.put("hasCar",Boolean.toString(employee.getHasCar()));
        cv.put("address",employee.getAddress());
        db.update("Employee", cv, "name = ?", new String[]{previousName});
        Intent intent=new Intent(EditEmployeesActivity.this, MainActivity.class);
        intent.putExtra("fragment", "employeeFragment");
        startActivity(intent);
    }
}