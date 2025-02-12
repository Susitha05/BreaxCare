package com.example.breaxcare.views.checkuphandler;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.breaxcare.R;
import com.example.breaxcare.views.BaseActivity;

import java.util.ArrayList;

public class QuestionnaireActivity extends BaseActivity {

    private TextView questionView;
    private ImageView imageFrame;
    private RadioGroup optionsGroup;
    private ArrayList<String> answers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire);

        Button btnBack = findViewById(R.id.btn_back);
        questionView = findViewById(R.id.question);
        imageFrame = findViewById(R.id.imageframe);
        optionsGroup = findViewById(R.id.rediogroup);


        String[] quest = {"Have you experienced any pain or discomfort in your breasts?",
                "Have you observed any nipple discharge, other than breast milk, or changes in the nipple?",
                "Have you ever been pregnant? If yes, at what age was your first full-term pregnancy?",
                "Do you smoke or consume alcohol?",
                "Do you exercise or engage in physical activity?"};

        ArrayList<Integer> pic = new ArrayList<>();
        pic.add(R.drawable.breast_);
        pic.add(R.drawable.image1);
        pic.add(R.drawable.image2);
        pic.add(R.drawable.image3);
        pic.add(R.drawable.breast_self_exam_ill_vector);

        final int[] count = {0};

        btnBack.setOnClickListener(v -> onBackPressed());

        findViewById(R.id.next).setOnClickListener(v -> {
            if(count[0] != quest.length) {
                if (validation()) {
                    answers.add(getSelectedAnswer());
                    if (count[0] < quest.length) {
                        questionView.setText(quest[count[0]]);
                        if (count[0] < pic.size()) {
                            imageFrame.setImageResource(pic.get(count[0]));
                        } else {
                            imageFrame.setImageResource(pic.get(1));
                        }
                        count[0]++;
                    }
                    optionsGroup.clearCheck();
                } else {
                    Toast.makeText(this, "Please give an answer.", Toast.LENGTH_SHORT).show();
                }
            } else {
                int yess = 0, nos = 0;
                String label = "";
                for (String answer : answers) {
                    if ("Yes".equals(answer)) {
                        yess++;
                    } else if ("No".equals(answer)) {
                        nos++;
                    }
                }
                if (yess > 3) {
                    label = "Risk";
                } else if (nos > 3) {
                    label = "Check";
                } else {
                    label = "Normal";
                }

                Intent intent = new Intent(QuestionnaireActivity.this, AnswerActivity.class);
                intent.putExtra("label", label);
                startActivity(intent);
            }
        });
    }

    public boolean validation() {
        int checkedId = optionsGroup.getCheckedRadioButtonId();
        return checkedId != -1;
    }

    private String getSelectedAnswer() {
        int checkedId = optionsGroup.getCheckedRadioButtonId();
        RadioButton selectedButton = findViewById(checkedId);
        return selectedButton.getText().toString();
    }
}
