package com.example.venkateshwar.tod;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

public class TipListActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tip_list);

        mToolbar = (Toolbar) findViewById(R.id.nav_actionbar);
        mToolbar.setTitle("My Tips");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(myIntent, 0);
        return true;
    }

    void getAll() {
        StringBuffer s1 = new StringBuffer();
        int i = 0;

//        for (Learning str : arrayList.getArrayList()) {
//            if(str!=null) {
//                s1.append(i + 1 + ". " + str.getData() + "\n");
//                i++;
//            }
//        }
//        textView.setText(s1);
    }
}
