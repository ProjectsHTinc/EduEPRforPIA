package com.edu.erp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edu.erp.R;


/**
 * Created by Admin on 03-04-2017.
 */

public class NavDrawerAdapter extends ArrayAdapter<String> {

    int resource;
    Context context;
    String[] items;

    //Initialize adapter
    public NavDrawerAdapter(Context context, int resource, String[] items) {
        super(context, resource, items);
        this.resource = resource;
        this.context = context;
        this.items = items;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LinearLayout offerView = null;

        if (convertView == null) {
            offerView = new LinearLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater vi;
            vi = (LayoutInflater) getContext().getSystemService(inflater);
            vi.inflate(resource, offerView, true);
        } else {
            offerView = (LinearLayout) convertView;
        }
        TextView txt2 = (TextView) offerView.findViewById(R.id.nav_Drawer_textItem);
        txt2.setText(items[position]);
        txt2.setTextColor(context.getResources().getColor(R.color.black));

        return offerView;
    }
}