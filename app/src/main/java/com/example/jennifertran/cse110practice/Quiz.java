package com.example.jennifertran.cse110practice;

import java.util.ArrayList;

/*
 * Name: Quiz
 * Parent Activity: SubjectNavActivity
 * Purpose: The quiz object which represents the entirity of the quiz being taken in
 * QuizActivity.
 * Children Activity: ResultActivity
 */
public class Quiz {
    //The list of questions the quiz contains
    private ArrayList<Question> questionList;
    //The answers to the questions in the quiz.
    private ArrayList<String> answers = new ArrayList<>();
    //Title of the quiz shown in the subjectNav
    private String title;
    //Questions and cols of quiz. Cols is used to create a uniform structure in the quiz
    //In case questions have different amounts of options.
    private int numQuestions;
    private int numCols;
    //The current viewed question
    private Question currentQuestion;



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

    public void addQuestion(Question q, int id){
        // Adds question at index
        questionList.add(id+1, q);
        numQuestions++;
        answers.add(q.getAnswer());

        for (int i = 0; i < numQuestions; i++) {
            questionList.get(i).setId(i);
        }
    }


    /* Method name: deleteQuestion
     * Parameter(s): Question object named q
     * Description: this method will delete the current question from the quiz
     *              and make updates to the database accordingly
     */
    public void deleteQuestion(int id) {
        questionList.remove(id);
        answers.remove(id);
        numQuestions--;
        for ( int i = 0; i < numQuestions; i++) {
            questionList.get(i).setId(i);
        }
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
    //setters
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
    public void setNumCols(int numCols) { this.numCols = numCols; }

    //An extremely important method. This method updates each question to have the correct
    //number of columns so that they can be passed into the database correctly.
    public void updateNumColsOfQuestions(int numCols) {
        for(Question q : questionList)
        {
            setNumCols(numCols);
        }
    }

    //A method that turns all of the questions to strings and then prints said strings.
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
