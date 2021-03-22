package com.example.eyepedia.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;
import com.example.eyepedia.R;



public class SubActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);
        Intent intent = getIntent();
        String Name = intent.getStringExtra("Name");
        TextView textview = (TextView)findViewById(R.id.textView);
        textview.setText(Name);
    }
}