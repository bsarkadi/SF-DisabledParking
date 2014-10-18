package com.bsarkadi.sf_disabledparking;

/**
 * Activity to display the list
 */

import android.app.Activity;

import android.app.Fragment;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.widget.Button;

import android.widget.ListView;


import java.util.ArrayList;

import android.util.Log;




public class MyFragment extends Fragment {
    private static final String TAG = "MyFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle extras = getArguments();
        Log.i(TAG, extras.getParcelableArrayList("arraylist").toString());

        ArrayList <Result> arlist = extras.getParcelableArrayList("arraylist");

        UserAdapter adapter = new UserAdapter(getActivity(), arlist);
        ListView listView = (ListView) getActivity().findViewById(R.id.list);

        listView.setAdapter(adapter);


        return super.onCreateView(inflater, container, savedInstanceState);


    }
}