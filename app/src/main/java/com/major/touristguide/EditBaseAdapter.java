package com.major.touristguide;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class EditBaseAdapter extends BaseAdapter {

    Context context;
    public static List<EditItem> editItems;

    public EditBaseAdapter(Context context, List<EditItem > editItem)
    {
        this.context = context;
        this.editItems = editItem;
    }

    private class ViewHolder {
        TextView txtTitle;
        TextView txtDesc;
        CheckBox checkBox;
    }

    @Override
    public int getCount() {
        return editItems.size();
    }

    @Override
    public Object getItem(int position) {
        return editItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return editItems.indexOf(getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       final ViewHolder holder;

        LayoutInflater mInflater = (LayoutInflater)
                context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.edit_item, null);
            holder = new ViewHolder();
            holder.txtDesc = (TextView) convertView.findViewById(R.id.desc);
            holder.txtTitle = (TextView) convertView.findViewById(R.id.title);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        EditItem editItem = (EditItem) getItem(position);

        holder.txtDesc.setText(editItem.getDesc());
        holder.txtTitle.setText(editItem.getTitle());
        holder.checkBox.setChecked(editItem.isSelected());
        holder.checkBox.setTag(R.integer.btnplusview, convertView);
        holder.checkBox.setTag(position);

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View tempview = (View) holder.checkBox.getTag(R.integer.btnplusview);
                TextView tv = (TextView) tempview.findViewById(R.id.title);
                Integer pos = (Integer)  holder.checkBox.getTag();


                if(editItems.get(pos).isSelected()){
                    editItems.get(pos).setSelected(false);
                    Toast.makeText(context, editItems.get(pos).getTitle() +"will be removed!", Toast.LENGTH_SHORT).show();
                }else {
                    editItems.get(pos).setSelected(true);
                    Toast.makeText(context, editItems.get(pos).getTitle() +"will be added again!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return convertView;
    }
}
