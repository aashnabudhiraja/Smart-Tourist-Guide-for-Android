package com.major.touristguide;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.major.touristguide.models.PlaceItem;
import com.major.touristguide.models.RowItem;

import java.util.List;

public class CustomAdapter extends BaseAdapter {


    Context context;
    List<PlaceItem> placeItems;

    public CustomAdapter(Context context, List<PlaceItem> items) {
        this.context = context;
        this.placeItems = items;
    }

    /*private view holder class*/
    private class ViewHolder {
        TextView txtPlaceName;
        TextView txtRating;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        CustomAdapter.ViewHolder holder = null;

        LayoutInflater mInflater = (LayoutInflater)
                context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item, null);
            holder = new CustomAdapter.ViewHolder();
            //holder.txtRating = (TextView) convertView.findViewById(R.id.desc);
            holder.txtPlaceName = (TextView) convertView.findViewById(R.id.title);
            holder.txtRating = (TextView) convertView.findViewById(R.id.icon);
            convertView.setTag(holder);
        }
        else {
            holder = (CustomAdapter.ViewHolder) convertView.getTag();
        }

        PlaceItem placeItem = (PlaceItem) getItem(position);

        holder.txtRating.setText(placeItem.getRating());
        holder.txtPlaceName.setText(placeItem.getPlaceName());
       // holder.textView.setText(rowItem.getTiming());

        return convertView;
    }

    @Override
    public int getCount() {
        return placeItems.size();
    }

    @Override
    public Object getItem(int position) {
        return placeItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return placeItems.indexOf(getItem(position));
    }
}
