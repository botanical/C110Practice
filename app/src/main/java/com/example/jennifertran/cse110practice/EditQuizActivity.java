package com.example.jennifertran.cse110practice;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class EditQuizActivity extends AppCompatActivity {

    TextView textQuestion;
    View submit, back_button;
    Button next_button;
    RadioButton answer;
    String username;

    RadioGroup grp;
    ViewGroup editGrp;
    DbHelperQuiz db;
    int marked;
    int question_id = 0;
    String colName = "marked";
    ProgressDialog pDialog;
    String loginUrl;

    /* Adding member variables, strings, and booleans for fragments */
    private ListView mDrawerList;
    //private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    String open_drawer = "Question Navigation";
    String title;
    Quiz quiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_quiz);
        loginUrl = getApplicationContext().getString(R.string.queryUrl);

        Intent intentReceived = getIntent();
        title = intentReceived.getStringExtra("title");
        username = intentReceived.getStringExtra("username");

        /* Get list of columns from previous activity where the quiz was updated */
        String colsString  = intentReceived.getStringExtra("columns");
        ArrayList<String> cols = new ArrayList<>();
        try{
            JSONArray columns = new JSONArray(colsString);
            for(int i = 0; i < columns.length(); i++)
                cols.add(columns.getString(i));
        }catch(Exception e){
            e.printStackTrace();
        }
        /*******************************  Initialize Quiz Object ****************************/

        db =  new DbHelperQuiz(this,title,cols);
        quiz = new Quiz(title, db.getQuestionsAsQuestionArray(), db.rowcount());

        /*^^^^^^^^^^^^^^^^^^^^^^^^^^^^^  Initialize Quiz Object ^^^^^^^^^^^^^^^^^^^^^^^^^^^^*/

        /******************************** Create Hamburger  *********************************/
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mDrawerList = (ListView)findViewById(R.id.navList);  /* Set ListView for Fragment */

        addDrawerItems();         /* Add drawer items */
        setupDrawer();
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        /*^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ Create Hamburger  ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^*/

        /***************************** Initialize Radio Buttons *****************************/


        grp = (RadioGroup)findViewById(R.id.radioGroup1);
        grp.setClickable(true);
        grp.setFocusable(true);
        grp.setFocusableInTouchMode(true);
        grp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                RadioButton r = (RadioButton) findViewById(checkedId);
                if (r == null)
                    return;
                quiz.getCurrentQuestion().setMarked(checkedId);
                //Set new quiz answer
                quiz.getCurrentQuestion().setAnswer(r.getText().toString());
                /* Update drawer item icons when radio button is clicked */
                addDrawerItems();
            }
        });

        editGrp = (RadioGroup)findViewById(R.id.editGroup);

        for(Question q : quiz.getQuestions())
        {
            EditText qField = new EditText(this);
            qField.setHint(q.getQuestion());
            qField.setOnFocusChangeListener( new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    EditQuizActivity.this.tempSubmitEdit();
                }
            });
            q.setQuestionField(qField);
            ArrayList<EditText> fields = new ArrayList<>();
            ArrayList<RadioButton> btns = new ArrayList<>();
            ArrayList<String> opts = q.getOptions();
            for(int i = 0; i < opts.size(); i++)
            {
                if(opts.get(i) != null && (!opts.get(i).equals(""))) {
                    EditText f = new EditText(this);
                    f.setHint(opts.get(i));
                    f.setId(View.generateViewId());

                    /* Each EditText should submit when a user clicks out of it */
                    f.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            EditQuizActivity.this.tempSubmitEdit();
                        }
                    });
                    fields.add(f);

                    RadioButton b = new RadioButton(this);
                    b.setText(opts.get(i));
                    b.setId(View.generateViewId()); //Generate id for the radioButton
                    if(b.getText().toString().equals(q.getAnswer()))
                    {
                        q.setMarked(b.getId());
                    }
                    btns.add(b);
                }
            }
            q.setRadioButtons(btns);
            q.setTextFields(fields);
        }
        System.out.println("QUIZ: "+quiz);

        /* Set Radio Buttons for first page */
        ArrayList<RadioButton> btns = quiz.getQuestions().get(0).getRadioButtons();
        ArrayList<EditText> fields = quiz.getQuestions().get(0).getTextFields();
        grp.addView(quiz.getCurrentQuestion().getQuestionField());
        for(int i = 0; i < btns.size(); i++) {
            grp.addView(btns.get(i));
            editGrp.addView(fields.get(i));
        }
        grp.check(quiz.getCurrentQuestion().getMarked());

        /*^^^^^^^^^^^^^^^^^^^^^^^^^^^ Initialize Radio Buttons ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^*/




        // Set up question on page
        textQuestion = (TextView)findViewById(R.id.textView1);
        next_button = (Button)findViewById(R.id.button_next);
        submit = findViewById(R.id.button_submit);
        if(quiz.getNumQuestions() == 1)
            submit.setVisibility(View.VISIBLE);
        else
            submit.setVisibility(View.GONE);
        back_button = findViewById(R.id.button_back);
        back_button.setVisibility(View.GONE);


        // Set the questions on the page
        setQuestionView();
        /* Update drawer icons to set viewed icon */
        quiz.getCurrentQuestion().setViewed(true);
        addDrawerItems();
        // Set question to viewed

        // Call listener to check for next page request
        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next();
            }
        });

        findViewById(R.id.button_back).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                back();
            }
        });

        findViewById(R.id.button_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //RadioGroup grp = (RadioGroup) findViewById(R.id.radioGroup1);
                // Save the user's answer
                answer = (RadioButton) findViewById(grp.getCheckedRadioButtonId());

                int btnId = quiz.getCurrentQuestion().getMarked();
                if(btnId == -1)
                {
                    Toast.makeText(EditQuizActivity.this,
                            "Select which option should be the correct answer.", Toast.LENGTH_LONG).show();
                }
                else
                    saveQuiz();
            }
        });
    }
    public void submit() {
        Intent openAdminActivity= new Intent(EditQuizActivity.this, AdminActivity.class);
        openAdminActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(openAdminActivity);
        finish();
    }

//TODO startup page instances don't delete themselves properly and so show up when using back button
    public void next() {
        int btnId = quiz.getCurrentQuestion().getMarked();
        if(btnId == -1)
        {
            Toast.makeText(EditQuizActivity.this,
                    "Select which option should be the correct answer.", Toast.LENGTH_LONG).show();
        }
        else {
            question_id++;
            goToQuestion(question_id);
        }
    }
    public void back() {
        int btnId = quiz.getCurrentQuestion().getMarked();
        if(btnId == -1)
        {
            Toast.makeText(EditQuizActivity.this,
                    "Select which option should be the correct answer.", Toast.LENGTH_LONG).show();
        }else {
            question_id--;
            goToQuestion(question_id);
        }
    }
    public void goToQuestion(int num)
    {
        question_id = num ;

        quiz.setCurrentQuestion(quiz.getQuestions().get(question_id));
        grp.removeAllViews();
        editGrp.removeAllViews();

        ArrayList<RadioButton> btns = quiz.getCurrentQuestion().getRadioButtons();
        ArrayList<EditText> fields = quiz.getCurrentQuestion().getTextFields();
        grp.addView(quiz.getCurrentQuestion().getQuestionField());
        for(int i = 0; i < btns.size(); i++) {
            grp.addView(btns.get(i));
            editGrp.addView(fields.get(i));
        }
        grp.check(quiz.getCurrentQuestion().getMarked());


        if (quiz.getNumQuestions() == 1)
        {
            back_button.setVisibility(View.GONE);
            next_button.setVisibility(View.GONE);
            submit.setVisibility(View.VISIBLE);
        }
        else if (question_id == 0) {
            back_button.setVisibility(View.GONE);
            submit.setVisibility(View.GONE);
            next_button.setVisibility(View.VISIBLE);
        }else if (question_id == quiz.getNumQuestions()-1) { //numofq used to be 4
            submit.setVisibility(View.VISIBLE);
            next_button.setVisibility(View.GONE);
            back_button.setVisibility(View.VISIBLE);
        }else{
            submit.setVisibility(View.GONE);
            next_button.setVisibility(View.VISIBLE);
            back_button.setVisibility(View.VISIBLE);
        }

        setQuestionView();
        if(quiz.getCurrentQuestion().getMarked() != -1 )
            grp.check(quiz.getCurrentQuestion().getMarked());
        quiz.getCurrentQuestion().setViewed(true);
        addDrawerItems();
    }

    /* Change question objects that are local to this activity, but don't save to
     * local or remote databases
     */
    public void tempSubmitEdit(){
        //Set questionAnswer to answer of the currently marked answer.
        int btnId = quiz.getCurrentQuestion().getMarked();
        if(btnId == -1)
        {
            Toast.makeText(EditQuizActivity.this,
                    "Select which option should be the correct answer.", Toast.LENGTH_LONG).show();
        }
        else {
            RadioButton markedRad = (RadioButton) findViewById(quiz.getCurrentQuestion().getMarked());
            quiz.getCurrentQuestion().setAnswer(markedRad.getText().toString());

            //Set new Question title
            String newQuestion = quiz.getCurrentQuestion().getQuestionField().getText().toString();
            if (!newQuestion.equals("")) {
                quiz.getCurrentQuestion().setQuestion(newQuestion);
                textQuestion.setText(newQuestion);
            }

            //Set new Question radioButtons
            ArrayList<RadioButton> r = quiz.getCurrentQuestion().getRadioButtons();
            ArrayList<String> butText = new ArrayList<String>();

            for (int i = 0; i < r.size(); i++) {
                butText.add(r.get(i).getText().toString());
            }
            for (int i = 0; i < r.size(); i++) {
                String newButtonText = quiz.getCurrentQuestion().getTextFields().get(i).getText().toString();
                if (!newButtonText.equals("")) {
                    r.get(i).setText(newButtonText);
                    butText.set(i, newButtonText);
                }
            }
            quiz.getCurrentQuestion().setOptions(butText);
        }



    }

    public void saveQuiz(){
        new QuizSaver().execute();
    }

    @Override
    public void onBackPressed() {
        submit();
        //saveQuiz();
    }


    private void setQuestionView()
    {
        textQuestion.setText(quiz.getCurrentQuestion().getQuestion());
        String qid = "Question: " + String.valueOf(question_id + 1) + "/" + String.valueOf(quiz.getNumQuestions());
        getSupportActionBar().setTitle(qid);
    }


    /* Helper method called by onCreate to add drawer items to Drawer */
    private void addDrawerItems() {

        ArrayList<String> row;
        Question currQuestion;
        ArrayList<Question> questionList = quiz.getQuestions();
        FragmentNavigationAdapter mAdapter;
        FragmentNavigationTitle navTitle[] = new FragmentNavigationTitle[questionList.size()];


        for (int i = 0; i < questionList.size(); i++) {
            currQuestion = questionList.get(i);
            //Add 1 to zero indexed question number
            //questionNums[i] = "Question " + String.valueOf(currQuestion.getId()+1);


            if (currQuestion.getViewed() == false && (currQuestion.getMarked() == -1)) {
                navTitle[i] = new FragmentNavigationTitle(R.drawable.ic_unanswered_question_24px,
                        R.drawable.ic_unviewed_question_24px,
                        "Question " + String.valueOf(currQuestion.getId() + 1));
            }
            else if (currQuestion.getViewed() == false && (currQuestion.getMarked() != -1)){
                navTitle[i] = new FragmentNavigationTitle(R.drawable.ic_answered_question_24px,
                        R.drawable.ic_unviewed_question_24px,
                        "Question " + String.valueOf(currQuestion.getId() + 1));
            }
            else if (currQuestion.getViewed() == true && (currQuestion.getMarked() == -1)){
                navTitle[i] = new FragmentNavigationTitle(R.drawable.ic_unanswered_question_24px,
                        R.drawable.ic_viewed_question_24px,
                        "Question " + String.valueOf(currQuestion.getId() + 1));
            }

            else if (currQuestion.getViewed() == true && (currQuestion.getMarked() != -1)){
                navTitle[i] = new FragmentNavigationTitle(R.drawable.ic_answered_question_24px,
                        R.drawable.ic_viewed_question_24px,
                        "Question " + String.valueOf(currQuestion.getId() + 1));
            }
        }

        mAdapter =
                new FragmentNavigationAdapter(this, R.layout.fragment_navigation_titles, navTitle);

        //mAdapter = new ArrayAdapter<>(this, R.layout.fragment_navigation_titles, questionNums);

        mDrawerList.setAdapter(mAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                goToQuestion(position);
                addDrawerItems();
            }
        });
    }
    /* Helper method called by onCreate to set up drawer items to Drawer */
    private void setupDrawer() {

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if(getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(open_drawer);
                }
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                if(getSupportActionBar() != null) {
                    getSupportActionBar().setTitle("Question: " + String.valueOf(question_id + 1)
                            + "/" + String.valueOf(quiz.getNumQuestions()));
                }
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }
    /* Method is used for Fragment. Syncs the indicator to match current state */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }
    /* Method is used for Fragment. Makes smooth transitioning for orientation change */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    /* Used for Inflating Activity Bar if Items are present */
    @Override
    public boolean onCreateOptionsMenu(Menu item) {
        getMenuInflater().inflate(R.menu.activity_edit_quiz, item);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        // Activate the navigation drawer tonoggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Add new question
        switch (item.getItemId()) {
            case R.id.action_add_question:
                addNewQuestion();
                return true;
            case R.id.action_delete_question:
                deleteQuestion(quiz.getCurrentQuestion().getId());
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
    /* Method name: deleteQuestion
       * Parameter(s): Question object named q
       * Description: this method will delete the current question from the quiz
       *              and make updates to the database accordingly
       */
    public void deleteQuestion(int id) {
        if (id == 0 && quiz.getNumQuestions() == 1) {
            addNewQuestion();
        } else if (id == 0) {
            quiz.deleteQuestion(id);
            goToQuestion(id);
        } else {
            quiz.deleteQuestion(id);
            goToQuestion(id-1);
        }
    }
    /* Method name: addNewQuestion()
     * Parameter(s): none
     * Description: This method will add a new question to the quiz and
     *              make updates to the database accordingly.
     */
    public void addNewQuestion(){
        Question q = new Question();
        q.setId(quiz.getNumQuestions()); //set new question id to last question id plus 1
        // Add Radio Buttons to new Question. Default is 2



        ArrayList<RadioButton> rads = new ArrayList<>();
        RadioButton r = new RadioButton(this);
        r.setText("Add an Option!");

        r.setId(View.generateViewId());
        rads.add(r);
        r = new RadioButton(this);
        r.setText("Add an Option!");
        r.setId(View.generateViewId());
        rads.add(r);
        q.setRadioButtons(rads);

        //Add text fields to new Question to match radio buttons.

        EditText question = new EditText(this);
        question.setHint("Add a new Question!");
        question.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                EditQuizActivity.this.tempSubmitEdit();
            }
        });

        q.setQuestionField(question);
        
        ArrayList<EditText> textFields = new ArrayList<>();
        for(int i = 0; i < rads.size(); i++)
        {
            EditText e = new EditText(this);
            e.setHint(q.getOptions().get(i));
            e.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    EditQuizActivity.this.tempSubmitEdit();
                }
            });
            textFields.add(e);
        }
        q.setTextFields(textFields);
        quiz.addQuestion(q, quiz.getCurrentQuestion().getId());
        goToQuestion(quiz.getNumQuestions()-1); //go to last question.
        //Add new question to quiz && list of currentquestions

    }


    /*********** Hide keyboard and unfocus currently focused EditText ********************/
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View mEditText = getCurrentFocus();
            if (mEditText != null) {
                Rect outRect = new Rect();
                mEditText.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    mEditText.clearFocus();
                    //
                    // Hide keyboard
                    //
                    InputMethodManager imm = (InputMethodManager) mEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }
    class QuizSaver extends AsyncTask<String,String,String> {

        protected void onPreExecute(){
            super.onPreExecute();
            pDialog = new ProgressDialog(EditQuizActivity.this);
            pDialog.setMessage("Attempting To Save Quiz");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        //Delete the current table
        //Insert the current quiz
        @Override
        protected String doInBackground(String... params) {
            RemoteDBHelper remDb = new RemoteDBHelper();
            ArrayList<Question> questions = quiz.getQuestions();
            System.out.println("QUESTIONS "+ questions);
            //Delete old quiz
            String delete = "DELETE FROM `"+title+"`";
            remDb.queryRemote(getApplicationContext().getString(R.string.remotePass),
                    delete, loginUrl);

            //Replace with new quiz
            String sql = "INSERT INTO "+"`"+title+"` VALUES ";
            for(Question q : questions)
            {
                String tmp = sql;
                tmp += q.toString();
               String table = remDb.queryRemote(getApplicationContext().getString(R.string.remotePass),
                       tmp, loginUrl);
            }



            /* Testing what I would write to query solutionMulitiplication And Division Table
             *
             * String solution = "SELECT 'solution' FROM 'solutionMultiplication And Division'
              * WHERE id = " + question_id;
              * remDb.queryRemote(getApplicationContext().getString(R.string.remotePass),
              *   solution, loginUrl);
             */


            return null;
        }

        protected void onPostExecute(String message){
            if(pDialog != null && pDialog.isShowing())
                pDialog.dismiss();
            submit();
        }
    }
}

