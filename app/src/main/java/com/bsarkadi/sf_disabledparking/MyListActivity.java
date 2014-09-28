package com.bsarkadi.sf_disabledparking;

/**
 * Activity to display the list
 */

import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;

import android.widget.Button;

import android.widget.ListView;


import java.util.ArrayList;




public class MyListActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent i = getIntent();
        ArrayList<Result> arlist = (ArrayList<Result>) i.getSerializableExtra("array_list");
        UserAdapter adapter = new UserAdapter(this, arlist);
        final ListView listView = (ListView) findViewById(R.id.list);

        listView.setAdapter(adapter);

        Button btnFind = (Button) findViewById(R.id.btnFind);
        btnFind.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                finish();
            }
        });
    }

}