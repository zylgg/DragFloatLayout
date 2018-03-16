package com.example.zyl.dragfloatlayout;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tfhr02.myapplication.R;

public class ScrollingActivity extends AppCompatActivity {

    private static final String TAG = "ScrollingActivity";
    private MyViewGroup mvg;
    private Toolbar toolbar1, toolbar2;
    private ScrollView nsv;
    private TextView tv_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        mvg = (MyViewGroup) findViewById(R.id.mvg);
        tv_content= (TextView) findViewById(R.id.tv_content);

        toolbar1 = (Toolbar) findViewById(R.id.toolbar);
        toolbar2 = (Toolbar) findViewById(R.id.toolbar2);
        nsv = (ScrollView) findViewById(R.id.nsv);

        setSupportActionBar(toolbar1);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initListener();
    }

    @SuppressLint("NewApi")
    private void initListener() {
        mvg.setOnMyViewGroupScrollListener(new MyViewGroup.OnMyViewGroupScrollListener() {
            @Override
            public void translationY(float ratio) {
                toolbar2.setTranslationY(toolbar2.getHeight() * ratio);
                if (ratio == 1) {
                    Toast.makeText(ScrollingActivity.this, "完全展开了！", Toast.LENGTH_SHORT).show();
                    nsv.setBackgroundColor(getColor(R.color.green));
                } else {
                    nsv.setBackgroundColor(getColor(R.color.white));
                }
            }
        });
        toolbar2.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mvg.scrollHeader();
                nsv.setBackgroundColor(getColor(R.color.white));
            }
        });
    }
}
