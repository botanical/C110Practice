package com.example.jennifertran.cse110practice;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by JenniferTran on 11/16/15.
 */
public class FragmentNavigationAdapter extends ArrayAdapter<FragmentNavigationTitle> {

    Context context;
    int layoutResourceId;
    FragmentNavigationTitle data[] = null;

    public FragmentNavigationAdapter(Context context, int layoutResourceId, FragmentNavigationTitle[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        NavHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new NavHolder();
            holder.v_imgIcon = (ImageView)row.findViewById(R.id.v_imgIcon);
            holder.a_imgIcon = (ImageView)row.findViewById(R.id.a_imgIcon);
            holder.txtTitle = (TextView)row.findViewById(R.id.txtTitle);

            row.setTag(holder);
        }
        else
        {
            holder = (NavHolder)row.getTag();
        }

        FragmentNavigationTitle navigationTitle = data[position];
        holder.txtTitle.setText(navigationTitle.title);
        holder.v_imgIcon.setImageResource(navigationTitle.viewed_icon);
        holder.a_imgIcon.setImageResource(navigationTitle.answered_icon);


        return row;
    }

    static class NavHolder
    {
        ImageView a_imgIcon;
        ImageView v_imgIcon;
        TextView txtTitle;
    }
}