package com.example.cm.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cm.R;
import com.example.cm.controller.MainController;

public class MainActivity extends AppCompatActivity {

    private TextView mText;
    private Button mBtnFence;
    private Button mBtnSnapshot;
    private MainController mainController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById();
        mainController = new MainController(MainActivity.this);

        mBtnSnapshot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainController.snapshot();
            }
        });

        mBtnFence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainController.fenceEnabled();
            }
        });
    }

    public void showText(final String text) {
        mText.post(new Runnable() {
            @Override
            public void run() {
                mText.setText(text);
            }
        });
    }

    private void findViewById(){
        mText = findViewById(R.id.text);
        mBtnFence = findViewById(R.id.btnFence);
        mBtnSnapshot = findViewById(R.id.btnSnapshot);
    }
}