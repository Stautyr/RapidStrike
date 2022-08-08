package com.example.teamnames2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    ListView rapidStrike;
    String teamList[] = {"RS1", "RS2", "RS3", "RS4", "RS5", "RS6", "RS7", "RS8", "RS9"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rapidStrike = (ListView)findViewById(R.id.contactList);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.contacts, R.id.teamName, teamList);
        rapidStrike.setAdapter(arrayAdapter);
    }
}