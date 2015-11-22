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
    private int numCols;
    private Question currentQuestion;
    private int currentQuestionIndex;



    public Quiz(String title, ArrayList<Question> questionList, int numQuestions){
        setQuestions(questionList);
        setTitle(title);
        setNumQuestions(numQuestions);
        generateAnswers();
        currentQuestion = questionList.get(0); //currentQuestion is by default the first question

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

    public void addQuestion(Question q){
        questionList.add(q);
        numQuestions++;
        answers.add(q.getAnswer());
    }

   //Setters
    public void setQuestions(ArrayList<Question> questionList)
    {
        this.questionList = questionList;


        int max = 0;
        for(Question q : questionList)
        {
            int tmp = q.getOptions().size();
            if(tmp > max)
                max = tmp;
        }
        numCols = max;

        for (Question q : questionList) {
            q.setNumCols(this.numCols);
        }
    }
    public void setTitle(String title){ this.title = title; }
    public void setNumQuestions(int numQuestions) { this.numQuestions = numQuestions; }
    public void setCurrentQuestion(Question currentQuestion){ this.currentQuestion = currentQuestion; }
    public void setAnswers(ArrayList<String> answers)
    {
        this.answers = answers;
    }

    //Getters
    public ArrayList<Question> getQuestions() { return this.questionList; }
    public String getTitle() { return this.title; }
    public int getNumQuestions() { return this.numQuestions; }
    public Question getCurrentQuestion() { return this.currentQuestion; }
    public ArrayList<String> getAnswers() { return this.answers; }


    public int getNumCols() { return this.numCols; }


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
