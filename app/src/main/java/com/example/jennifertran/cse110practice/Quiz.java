package com.example.jennifertran.cse110practice;

import java.util.ArrayList;

/**
 * Created by emd_000 on 11/14/2015.
 */
public class Quiz {
    private ArrayList<Question> questionList;
    private ArrayList<String> answers = new ArrayList<>();
    private String title;
    private int numQuestions;


    public Quiz(String title, ArrayList<Question> questionList, int numQuestions){
        setQuestions(questionList);
        setTitle(title);
        setNumQuestions(numQuestions);
        generateAnswers();

    }
    /* generateAnswers()
       Purpose: Pull answers from this Quiz's questions and put into this Quiz's list of 'answers'
       Parameter: None
       Return: None
       Mutates: ArrayList<String> this.answers
     */
    private void generateAnswers()
    {
        for(int i = 0; i < numQuestions; ++i){
            answers.add(questionList.get(i).getAnswer());
        }
    }

    public ArrayList<Question> getQuestions()
    {
        return this.questionList;
    }
    public void setQuestions(ArrayList<Question> questionList)
    {
        this.questionList = questionList;
    }
    public String getTitle()
    {
        return this.title;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public int getNumQuestions()
    {
        return this.numQuestions;
    }
    public void setNumQuestions(int numQuestions)
    {
        this.numQuestions = numQuestions;
    }
    public ArrayList<String> getAnswers()
    {
        return this.answers;
    }
    public void setAnswers(ArrayList<String> answers)
    {
        this.answers = answers;
    }
    public String toString()
    {
        String quizStr = "{ ";
        for (int i = 0; i < questionList.size() -1; i++) {
             quizStr += questionList.get(i).toString()+", ";
        }
        quizStr += questionList.get(questionList.size()-1).toString()+" }";
        return quizStr;

    }

}
