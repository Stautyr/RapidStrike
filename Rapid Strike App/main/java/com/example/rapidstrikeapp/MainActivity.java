package com.example.rapidstrikeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button comm = findViewById(R.id.Comm);
        comm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                Intent intent = new Intent(MainActivity.this, ViewComm.class);
                startActivity(intent);
            }
        });

        final Button carrymod = findViewById(R.id.CarryModule);
        carrymod.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                Intent intent = new Intent(MainActivity.this, ViewCarry.class);
                startActivity(intent);
            }
        });

        final Button helpbtn = findViewById(R.id.Help);
        helpbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                Intent intent = new Intent(MainActivity.this, ViewHelp.class);
                startActivity(intent);
            }
        });
    }
}
