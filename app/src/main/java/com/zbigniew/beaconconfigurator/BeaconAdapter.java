package com.zbigniew.beaconconfigurator;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.kontakt.sdk.android.common.profile.IEddystoneDevice;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zbigniew on 01/12/15.
 */
public class BeaconAdapter extends ArrayAdapter<IEddystoneDevice> {

    Context context;
    int layoutResourceId;
    List<IEddystoneDevice> data;

    public BeaconAdapter(Context context, int layoutResourceId, List<IEddystoneDevice> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        BeaconHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new BeaconHolder();
            //holder.imgIcon = (ImageView)row.findViewById(R.id.imgIcon);
            holder.txtTitle = (TextView)row.findViewById(R.id.beaconProfile);

            row.setTag(holder);
        }
        else
        {
            holder = (BeaconHolder)row.getTag();
        }

        IEddystoneDevice device = data.get(position);
        //Weather weather = data[position];
        holder.txtTitle.setText(device.getUrl());

        return row;
    }

    static class BeaconHolder
    {
        TextView txtTitle;
    }

}
