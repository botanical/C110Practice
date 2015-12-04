package com.example.jennifertran.cse110practice;

/*
 * Name: ResultActivity
 * Parent Activity: None
 * Purpose: Instead of the standard strings the ExpandableListAdapter provides, this class
 * adds the increased functionality of adding the abiltiy to change the color and image of the
 * entry as well as the text.
 * Children Activity: None
 */
public class ELAEntry {
    public int color = 0xFFFFFF; //Black by default
    public String image = ""; //Image can take four values, check/Xmark/incomplete/non-existant.
    public String TextEntry = "";//The text of the entry.
}
