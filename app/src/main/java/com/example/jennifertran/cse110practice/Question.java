package com.example.jennifertran.cse110practice;


import android.widget.RadioButton;

import java.util.ArrayList;

public class Question {
    private int id;
    private String question;
    private ArrayList<String> options;
    private ArrayList<RadioButton> radioButtons;
    private String answer;
    private int marked;
    private boolean viewed;


    public Question()
    {
        id=0;
        question="";
        options= new ArrayList<>();
        answer="";
        marked=0;
        viewed=false;
    }
    public Question(String question, ArrayList<String> options,
                    String answer, int marked, boolean viewed) {
        this.question = question;
        this.options = options;
        this.answer = answer;
        this.marked = marked;
        this.viewed = viewed;
    }
    public int getId()
    {
        return id;
    }
    public String getQuestion() {
        return this.question;
    }
    public ArrayList<String> getOptions() {return this.options; }
    public ArrayList<RadioButton> getRadioButtons() {return this.radioButtons; }
    public String getAnswer() {
        return answer;
    }
    public int getMarked() {
        return marked;
    }
    public boolean getViewed() { return viewed; }
    public void setId(int id)
    {
        this.id=id;
    }
    public void setQuestion(String question) {
        this.question = question;
    }
    public void setOptions(ArrayList<String> options) {
        this.options = options;
    }
    public void setRadioButtons(ArrayList<RadioButton> radioButtons){
        this.radioButtons = radioButtons;
    }
    public void setAnswer(String answer) {
        this.answer = answer;
    }
    public void setMarked(int marked) {
        this.marked = marked;
    }
    public void setViewed(boolean viewed) { this.viewed = viewed; }
    static public Question arrayListToQuestion(ArrayList<String> row)
    {
        //TODO add final variables corresponding to indexes
        Question rowq = new Question();
        rowq.setId(Integer.valueOf(row.get(0)));
        rowq.setQuestion(row.get(1));
        rowq.setAnswer(row.get(2));
        ArrayList<String> options = new ArrayList<>();
        for(int i = (2 + 1); i < row.size()-1; i++){ // -1 to skip last column 'marked'
            options.add(row.get(i));
        }
        rowq.setOptions(options);
        rowq.setMarked(-1); //default marked value == -1
        rowq.setViewed(false);
        return rowq;
    }
    public String toString (){

        return "[ "+ this.getId()+", "+this.getQuestion()+", "+this.getAnswer()+", " +
                this.options.toString() + ", " + this.getMarked()+ "," + this.getViewed() + " ]";
    }


}
