package club.lovemo.questionbank.lovemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import club.lovemo.questionbank.R;

/**
 * Created by John .
 */




public class MainActivity extends AppCompatActivity {
    public Button main_question_bank;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        main_question_bank = findViewById(R.id.main_question_bank);
        main_question_bank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, QuestionListActivity.class));
            }
        });
    }
}
