package com.example.jennifertran.cse110practice;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/*
 * Name: FragmentNavigationAdapter
 * Parent Activity: None
 * Purpose: The purpose of this class is to create a hamburger to be used in the quiz to
 * navigate to certain questions (as well as tell users which questions they have answered).
 * Children Activity: None
 */
public class FragmentNavigationAdapter extends ArrayAdapter<FragmentNavigationTitle> {

    //Context of the hamburger
    Context context;
    //id for layout
    int layoutResourceId;
    //Title of the fragment we create.
    FragmentNavigationTitle data[] = null;

    public FragmentNavigationAdapter(Context context, int layoutResourceId, FragmentNavigationTitle[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    //If the view is called we inflate the hamburger, which shows the answered and visited flags.
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        NavHolder holder = null;
        //get default navholders if we have no info
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
        //get actual image values if we have any row values.
        else
        {
            holder = (NavHolder)row.getTag();
        }

        //set navholders
        FragmentNavigationTitle navigationTitle = data[position];
        holder.txtTitle.setText(navigationTitle.title);
        holder.v_imgIcon.setImageResource(navigationTitle.viewed_icon);
        holder.a_imgIcon.setImageResource(navigationTitle.answered_icon);


        return row;
    }

    //Navholders simply contain two images and text.
    static class NavHolder
    {
        ImageView a_imgIcon;
        ImageView v_imgIcon;
        TextView txtTitle;
    }
}