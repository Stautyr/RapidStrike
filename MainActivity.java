package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    DatabaseHelper db;
    EditText editText;
    Button btnAdd, btnView, btnView2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new DatabaseHelper(this);

        editText = (EditText)findViewById(R.id.editText);
        btnAdd = (Button)findViewById(R.id.btnAdd);
        btnView = (Button)findViewById(R.id.btnView);
        btnView2 = (Button)findViewById(R.id.btnView2);
        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ViewDB.class);
                startActivity(intent);
            }
        });
        btnAdd.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String newEntry = editText.getText().toString();
                        if(editText.length() != 0) {
                            AddData(newEntry);
                            editText.setText("");
                        }else
                            Toast.makeText(MainActivity.this, "Data Not Inserted", Toast.LENGTH_LONG).show();
                    }
                }
        );
    }
    public void AddData(String newEntry) {
         {
                    boolean isInserted = db.insertData(newEntry);
                    if(isInserted == true)
                        Toast.makeText(MainActivity.this, "Data Inserted", Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(MainActivity.this, "Data Not Inserted", Toast.LENGTH_LONG).show();
                }
            }


}
