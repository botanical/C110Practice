package com.example.jennifertran.cse110practice;

import java.util.ArrayList;
import java.util.HashMap;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class ELATest extends BaseExpandableListAdapter {

    private Context _context;
    private ArrayList<ELAEntry> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, ArrayList<ELAEntry>> _listDataChild;

    public ELATest(Context context, ArrayList<ELAEntry> listDataHeader,
                                 HashMap<String, ArrayList<ELAEntry>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition).TextEntry).get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

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
/*
        convertView.setBackgroundColor(entry.color);

        txtListChild.setText(entry.TextEntry);
        if(entry.image != "") {
            txtListChild.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            if (entry.image == "xmark") {
                txtListChild.setCompoundDrawablesWithIntrinsicBounds(R.drawable.xmark, 0, 0, 0);
            } else if (entry.image == "checkmark") {
                txtListChild.setCompoundDrawablesWithIntrinsicBounds(R.drawable.checkmark, 0, 0, 0);
            } else {
                txtListChild.setCompoundDrawablesWithIntrinsicBounds(R.drawable.questionmark, 0, 0, 0);
            }
        }
*/
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition).TextEntry).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

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

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}
