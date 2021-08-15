package com.infitech.temi;

import com.robotemi.sdk.Robot;
import com.robotemi.sdk.TtsRequest;
import com.robotemi.sdk.listeners.OnRobotReadyListener;

import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity implements Robot.AsrListener, Robot.WakeupWordListener, OnRobotReadyListener {

    private Robot robot;
    private String lastQuestion;
    private TextView txtQuestion;
    private TextView txtAnswer1;
    private TextView txtAnswer2;

    /**
     * Setting up all the event listeners
     */
    @Override
    protected void onStart() {
        super.onStart();
        robot.addOnRobotReadyListener(this);
        robot.addAsrListener(this);
        robot.addWakeupWordListener(this);
    }


    @Override
    public void onWakeupWord(@NotNull String wakeupWord, int direction) {
        onAsrResult("Hello");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtQuestion = findViewById(R.id.question);
        txtAnswer1 = findViewById(R.id.answer1);
        txtAnswer2 = findViewById(R.id.answer2);

        try {
            robot = Robot.getInstance(); // get an instance of the robot in order to begin using its features.s
        } catch (Exception ex) {
            Log.e("21 Create Exception", ex + "");
        }
    }

    /**
     * Places this application in the top bar for a quick access shortcut.
     */
    @Override
    public void onRobotReady(boolean isReady) {
        if (isReady) {
            try {
                final ActivityInfo activityInfo = getPackageManager().getActivityInfo(getComponentName(), PackageManager.GET_META_DATA);
                // Robot.getInstance().onStart() method may change the visibility of top bar.
                robot.onStart(activityInfo);
                robot.requestToBeKioskApp();
                robot.showTopBar();

                txtQuestion.setText("Say " + robot.getWakeupWord());
            } catch (PackageManager.NameNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void startQuestion() {
        setQuestionAnswers("Hello, I'm Temi, a robot assistant. Could you please confirm if your name is Mr. Roger Woods?", "Yes", "No");

        txtAnswer1.setOnClickListener(view -> {
            speak("Thank you for the confirmation. Mr. Roger Woods, it's time for your afternoon medicine");
            askMedicine();
        });
        txtAnswer2.setOnClickListener(view -> {
            callNurse();
        });
    }


    @Override
    public void onAsrResult(final @NonNull String asrResult) {
        if (asrResult.equalsIgnoreCase("Hello")) {
            setQuestionAnswers("Hello, I'm Temi, a robot assistant. Could you please confirm if your name is Mr. Roger Woods?", "Yes", "No");
            lastQuestion = "NAME";
        }

        if (lastQuestion.equals("NAME")) {
            if (asrResult.equalsIgnoreCase("Yes")) {
                speak("Thank you for the confirmation. Mr. Roger Woods, it's time for your afternoon medicine");
                askMedicine();
            } else if (asrResult.equalsIgnoreCase("No")) {
                callNurse();
            }
        }

        if (lastQuestion.equals("MEDICINES")) {
            if (asrResult.equalsIgnoreCase("Yes")) {
                setQuestionAnswers("How are you feeling today?", "I'm fine", "Not great");
                lastQuestion = "FEELING";
            } else if (asrResult.equalsIgnoreCase("No")) {
                askMedicine();
            }
        }

        if (lastQuestion.equals("FEELING")) {
            if (asrResult.equalsIgnoreCase("I'm fine")) {
                speak("Great to hear that! Please use the nurse call button if you need any assistance. Thank you");
            } else if (asrResult.equalsIgnoreCase("Not great")) {
                setQuestionAnswers("Should I call a nurse for assistance?", "Yes", "No");
                lastQuestion = "NURSE";
            }
        }

        if (lastQuestion.equals("NURSE")) {
            if (asrResult.equalsIgnoreCase("Yes")) {
                callNurse();
            } else if (asrResult.equalsIgnoreCase("No")) {
                speak("Please use the nurse call button if you need any assistance. Thank you");
            }
        }
    }

    private void askMedicine() {
        setQuestionAnswers("Please take 1 Panadol with a glass of water adn 5ml of Adol Oral Suspension. Have you taken your medicines?", "Yes", "No");
        lastQuestion = "MEDICINES";
    }

    private void callNurse() {
        setQuestionAnswers("I have called the Nurse for Assistance. They will be here shortly", null, null);
        lastQuestion = "I have called the Nurse for Assistance. They will be here shortly";
    }

    private void speak(String text) {
        robot.speak(TtsRequest.create(text, false));
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        txtQuestion.setText(text);
    }

    private void setQuestionAnswers(String question, String answer1, String answer2) {
        speak(question);

        txtQuestion.setText(question);

        if (txtAnswer1 == null) {
			txtAnswer1.setVisibility(View.INVISIBLE);
			txtAnswer1.setText("");
			txtAnswer1.setOnClickListener(view -> {});
		} else {
			txtAnswer1.setVisibility(View.VISIBLE);
            txtAnswer1.setText(answer1);
            txtAnswer1.setOnClickListener(view -> {
                onAsrResult(answer1);
            });
        }

		if (txtAnswer2 == null) {
			txtAnswer2.setVisibility(View.INVISIBLE);
			txtAnswer2.setText("");
			txtAnswer2.setOnClickListener(view -> {});
		} else {
			txtAnswer2.setVisibility(View.VISIBLE);
			txtAnswer2.setText(answer2);
			txtAnswer2.setOnClickListener(view -> {
				onAsrResult(answer2);
			});
		}
    }

}