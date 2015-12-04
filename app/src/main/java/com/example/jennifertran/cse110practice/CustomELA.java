package com.example.jennifertran.cse110practice;

import java.util.ArrayList;
import java.util.HashMap;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;

/*
 * Name: CustomELA
 * Parent Activity: None
 * Purpose: The purpose of the CustomELA (as opposed to the ExpandableListAdapter class) is to
 * provide a complex implementation of an ELA where the user has access to
 * more of the variables of the ELA.  An ela is simply a listview in which we have parent nodes
 * that can be expanded to show sublists under each parent node.
 * Children Activity: None
 */

public class CustomELA extends BaseExpandableListAdapter {

    private Context _context;
    private ArrayList<ELAEntry> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, ArrayList<ELAEntry>> _listDataChild;

    //We now have the list of parent/child list passed in the constructor
    //Note that instead of strings we have ELAEntries
    public CustomELA(Context context, ArrayList<ELAEntry> listDataHeader,
                     HashMap<String, ArrayList<ELAEntry>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }

    //returns the child object from the parent/child index.
    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition).TextEntry).get(childPosititon);
    }

    //returns the childID.
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    //Sets the text (to a readable size) for the child view.
    //Functionally the childview of the customELA acts exactly the same as the regular
    //expandable list view because we are not intereted in editing the fields of the
    //child
    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        ELAEntry entry = (ELAEntry) getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item, null);
        }

        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.lblListItem);
        txtListChild.setText(entry.TextEntry);
        txtListChild.setTextSize(20);
        return convertView;
    }

    //Gives you you the amount of children in a parent node.
    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition).TextEntry).size();
    }

    //Necessary function to extend BaseELA
    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    //Provides the total number of parent nodes in the ELA
    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    //Necessary function to extend BaseELA
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    //The more interesting function, this method
    //takes and sets the text as usual, but also sets
    //the background color to the color of the entry,
    //and sets the image to one of the three predefined images
    //Although we could have made code to draw any image
    //passed in the image field, we learned that drawing
    //images in such a way slowed down machines considerably, and
    //thus decided to simply pass in a sting to decide what image we needed to draw.
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        ELAEntry entry = (ELAEntry) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }

        TextView txtListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        convertView.setBackgroundColor(entry.color);

        txtListHeader.setText(entry.TextEntry);
        if(entry.image != "") {
            txtListHeader.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            if (entry.image == "xmark") {
                txtListHeader.setCompoundDrawablesWithIntrinsicBounds(R.drawable.xmark2, 0, 0, 0);
            } else if (entry.image == "checkmark") {
                txtListHeader.setCompoundDrawablesWithIntrinsicBounds(R.drawable.check3, 0, 0, 0);
            } else {
                txtListHeader.setCompoundDrawablesWithIntrinsicBounds(R.drawable.quesmark2, 0, 0, 0);
            }
        }
        return convertView;
    }


    //Necessary function to extend BaseELA
    @Override
    public boolean hasStableIds() {
        return false;
    }

    //Necessary function to extend BaseELA
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}
