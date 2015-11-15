package com.example.jennifertran.cse110practice;


public class Question {
    private int ID;
    private String QUESTION;
    private String OPTA;
    private String OPTB;
    private String OPTC;
    private String ANSWER;
    private String MARKED;

    public Question()
    {
        ID=0;
        QUESTION="";
        OPTA="";
        OPTB="";
        OPTC="";
        ANSWER="";
        MARKED="";
    }
    public Question(String question, String A, String B, String C,
                    String answer, String marked) {
        QUESTION = question;
        OPTA = A;
        OPTB = B;
        OPTC = C;
        ANSWER = answer;
    }
    public int getID()
    {
        return ID;
    }
    public String getQUESTION() {
        return QUESTION;
    }
    public String getOPTA() {
        return OPTA;
    }
    public String getOPTB() {
        return OPTB;
    }
    public String getOPTC() {
        return OPTC;
    }
    public String getANSWER() {
        return ANSWER;
    }
    public String getMARKED() {
        return MARKED;
    }
    public void setID(int id)
    {
        ID=id;
    }
    public void setQUESTION(String question) {
        QUESTION = question;
    }
    public void setOPTA(String A) {
        OPTA = A;
    }
    public void setOPTB(String B) {
        OPTB = B;
    }
    public void setOPTC(String C) {
        OPTC = C;
    }
    public void setANSWER(String answer) {
        ANSWER = answer;
    }
    public void setMARKED(String marked) {
        MARKED = marked;
    }

}
