package com.example.jennifertran.cse110practice;

/**
 * Created by JenniferTran on 11/16/15.
 */
public class FragmentNavigationTitle {
    public int answered_icon;
    public int viewed_icon;
    public String title;

    public FragmentNavigationTitle(){
            super();
        }

        public FragmentNavigationTitle(int a_icon, int v_icon, String title) {
            super();
            this.answered_icon = a_icon;
            this.viewed_icon = v_icon;
            this.title = title;
        }

}
