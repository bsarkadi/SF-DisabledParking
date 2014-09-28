package com.bsarkadi.sf_disabledparking;

import android.content.Context;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Adapter customization
 */
public class UserAdapter extends ArrayAdapter<Result> {
    public UserAdapter(Context context, ArrayList<Result> users) {
        super(context,0,users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Result item = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listactivity, parent, false);
        }
        // Lookup view for data population
        TextView listitem = (TextView) convertView.findViewById(R.id.listitem);

        // Populate the data into the template view using the data object
        listitem.setText(Html.fromHtml(item.getRecord()));
        listitem.setMovementMethod(new ScrollingMovementMethod());
        listitem.setMovementMethod(LinkMovementMethod.getInstance());
        listitem.setClickable(true);
        // Return the completed view to render on screen
        return convertView;
    }
}
