/*
*
* CREDIT FOR PART OF PROGRAM
* WEBSITE USED:
* https://www.w3schools.blog/android-sqlite-example-with-spinner
*
*
* */
package com.example.rapidstrikeapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class ViewTeam extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Spinner spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teamsel_page);
        spinner = findViewById(R.id.spinner);
        Button btnsubmit = findViewById(R.id.btnsubmit);
        Button btnAdd = findViewById(R.id.btnadd);
        final EditText inputTeam = findViewById(R.id.input_label);

        spinner.setOnItemSelectedListener(this);

        loadSpinnerData();

        btnsubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String team = spinner.getSelectedItem().toString();
                Toast.makeText(ViewTeam.this, team, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ViewTeam.this, MainActivity.class);
                startActivity(intent);
            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String team = inputTeam.getText().toString();

                if (team.trim().length() > 0) {
                    DBHelper db = new DBHelper(getApplicationContext());
                    db.insertTeam(team);

                    inputTeam.setText("");

                    InputMethodManager imm = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(inputTeam.getWindowToken(), 0);
                    loadSpinnerData();
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter team name",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadSpinnerData() {
        DBHelper db = new DBHelper(getApplicationContext());
        List<String> teams = db.getAllTeams();

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, teams);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(dataAdapter);
    }
    
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            String team = adapterView.getItemAtPosition(i).toString();

            Toast.makeText(adapterView.getContext(), "Selected: " + team, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
