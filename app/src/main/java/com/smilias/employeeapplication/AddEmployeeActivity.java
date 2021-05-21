package com.smilias.employeeapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.smilias.employeeapplication.databinding.ActivityAddEmployeeBinding;

public class AddEmployeeActivity extends AppCompatActivity {

    private ActivityAddEmployeeBinding binding;
    private Employee employee;
    private boolean result=true;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddEmployeeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        db = openOrCreateDatabase("myDb", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS Employee(name TEXT,birthDate TEXT, hasCar TEXT, address TEXT)");

        binding.btnCreateEmployee.setOnClickListener(view1 -> createEmployee());

        binding.rbGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.rbtnNo) result = false;
                else result = true;
            }
        });


    }

    private void initialise() {
        employee = new Employee();
        employee.setName(binding.editTextName.getText().toString());
        employee.setBirthDate(binding.editTextDate.getText().toString());
        employee.setAddress(binding.editTextAddress.getText().toString());
        employee.setHasCar(result);
    }


    private void createEmployee() { //Δημουργούμαι το employee αν ισχύουν κάποια κριτήρια αλλιώς πετάει error και ζητάει focus, αν είναι οκ γίνεται insert στη βάση
        initialise();
        if (employee.getName().isEmpty()) {
            binding.editTextName.setError("Please give a name");
            binding.editTextName.requestFocus();
            return;
        } else {
            Cursor cursor = db.rawQuery("SELECT name FROM Employee WHERE name=?", new String[]{employee.getName()});
            if (cursor.getCount() > 0) {
                Toast.makeText(this, "Employee with this name already exists", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (!(employee.getBirthDate().matches("^([0-2][0-9]|(3)[0-1])(\\/)(((0)[0-9])|((1)[0-2]))(\\/)\\d{4}$"))) {
            binding.editTextDate.setError("Give a valid date");
            binding.editTextDate.requestFocus();
            return;
        }

        if (!(employee.getAddress().matches("^[-+]?([1-8]?\\d(\\.\\d+)?|90(\\.0+)?),\\s*[-+]?(180(\\.0+)?|((1[0-7]\\d)|([1-9]?\\d))(\\.\\d+)?)$"))) {
            binding.editTextAddress.setError("Give a valid coordinates");
            binding.editTextAddress.requestFocus();
            return;
        }
        db.execSQL("INSERT INTO Employee  VALUES('" + employee.getName() + "','" + employee.getBirthDate() + "','" + employee.getHasCar() + "','" + employee.getAddress() + "')");
        Intent intent=new Intent(AddEmployeeActivity.this,MainActivity.class);
        intent.putExtra("fragment", "employeeFragment");
        startActivity(intent);
    }

}