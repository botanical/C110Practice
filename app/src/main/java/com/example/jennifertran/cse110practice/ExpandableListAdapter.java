package com.example.jennifertran.cse110practice;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;


/*
 * Name: ExpandableListAdapter
 * Parent Activity: None
 * Purpose: The purpose of the ExpandableListAdapter (as opposed to the customELA class) is to
 * provide a simple implementation of an ELA where only strings are needed. This is due in part
 * to different segments of code requiring different types of ELAs and partially due to the fact
 * that half way through the project we developed the customELA and refactoring the existing code
 * was deemed both too risky and not where our efforts would be best placed. An ela is simply
 * a listview in which we have parent nodes that can be expanded to show sublists under each parent
 * node.
 * Children Activity: None
 */

public class ExpandableListAdapter extends BaseExpandableListAdapter {


    private Context _context;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<String>> _listDataChild;

    //We now have the list of parent/child list passed in the constructor
    public ExpandableListAdapter(Context context, List<String> listDataHeader,
                                 HashMap<String, List<String>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }
    //Simple code to add a node to the parent list.
    public void addHeader(String header){
        _listDataHeader.add(header);
    }

    //returns the child object from the parent/child index.
    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    //returns the childID.
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    //Sets the text (to a readable size) for the child view.
    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item, null);
        }

        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.lblListItem);

        txtListChild.setText(childText);
        txtListChild.setTextSize(25);
        return convertView;
    }

    //Simple method for getting the number of children at a given index
    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    //Returns the parent node from a given index
    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }


    //Returns the size of the parent list
    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    //Useless function.
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    //Sets the text/text type for the parent nodes.
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    //Useless function
    @Override
    public boolean hasStableIds() {
        return false;
    }

    //Returns if children are able to be selected in the ELA, always true for our purposes.
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}

