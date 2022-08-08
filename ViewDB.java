package com.example.myapplication;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ViewDB extends AppCompatActivity {

    DatabaseHelper db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview);

        ListView listView = (ListView) findViewById(R.id.listView);
        db = new DatabaseHelper(this);

        ArrayList<String> thislist = new ArrayList<>();
        Cursor data = db.getListContents();

        if(data.getCount() == 0){
            Toast.makeText(this, "db is empty", Toast.LENGTH_SHORT).show();
        } else
            while(data.moveToNext()){
                thislist.add(data.getString(0));
                ListAdapter listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,thislist);
                listView.setAdapter(listAdapter);
            }
    }
}
