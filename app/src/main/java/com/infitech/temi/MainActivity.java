package com.infitech.temi;

import com.robotemi.sdk.Robot;
import com.robotemi.sdk.TtsRequest;
import com.robotemi.sdk.listeners.OnRobotReadyListener;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.infitech.temi.R;

public class MainActivity extends AppCompatActivity
		implements Robot.ConversationViewAttachesListener, Robot.AsrListener, OnRobotReadyListener {

	private Robot robot;
	private String lastQuestion;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		try {
			robot = Robot.getInstance(); // get an instance of the robot in order to begin using its features.
		} catch (Exception ex) {
			Log.e("21 Create Exception", ex + "");
		}
	}

	@Override
	public void onRobotReady(boolean b) {
		Robot.getInstance().hideTopBar();
	}

	@Override
	public void onConversationAttaches(boolean isAttached) {
		// Do something as soon as the conversation is displayed.
		Log.e("Temi onConversation", "isAttached:" + isAttached);

		if (!isAttached) {

		}
	}

	@Override
	public void onAsrResult(final @NonNull String asrResult) {

		if (asrResult.equalsIgnoreCase("Hello")) {
			speak("Hello, I'm Temi, a robot assistant");
			ask("Could you please confirm if your name is Mr. Roger Woods?");

			lastQuestion = "NAME";
		}

		if (lastQuestion.equals("NAME")) {
			if (asrResult.equalsIgnoreCase("Yes") || asrResult.equalsIgnoreCase("Confirm")) {
				speak("Thank you for the confirmation");
				speak("Mr. Roger Woods, it's time for your afternoon medicine");
				askMedicine();
			} else if (asrResult.equalsIgnoreCase("No")) {
				callNurse();
			}
		}

		if (lastQuestion.equals("MEDICINES")) {
			if (asrResult.equalsIgnoreCase("Yes") || asrResult.equalsIgnoreCase("Confirm")) {
				ask("How are you feeling today?");
				lastQuestion = "FEELING";
			} else if (asrResult.equalsIgnoreCase("No")) {
				askMedicine();
			}
		}

		if (lastQuestion.equals("FEELING")) {
			if (asrResult.equalsIgnoreCase("I'm fine") || asrResult.equalsIgnoreCase("Fine")) {
				speak("Great to hear that");
				speak("Please use the nurse call button if you need any assitance. Thank you");
			} else if (asrResult.equalsIgnoreCase("No") || asrResult.equalsIgnoreCase("No")) {
				callNurse();
			} else if (asrResult.equalsIgnoreCase("Not great") || asrResult.equalsIgnoreCase("Not great, but okay")
					|| asrResult.equalsIgnoreCase("Okay")) {
				ask("Should I call a nurse for assistance?");
				lastQuestion = "NURSE";
			}
		}

		if (lastQuestion.equals("NURSE")) {
			if (asrResult.equalsIgnoreCase("Yes") || asrResult.equalsIgnoreCase("Confirm")) {
				callNurse();
			} else if (asrResult.equalsIgnoreCase("No")) {
				speak("Please use the nurse call button if you need any assitance. Thank you");
			}
		}
	}
	private void askMedicine() {
		speak("Please take 1 Panadol with a glass of water adn 5ml of Adol Oral Suspension");
		ask("Have you taken your medicines?");
		lastQuestion = "MEDICINES";
	}

	private void callNurse() {
		speak("I have called the Nurse for Assistance. They will be here shortly");
		lastQuestion = "";

		robot.finishConversation();
	}

    private void speak(String text) {
        robot.speak(TtsRequest.create(text, true));
        TextView textView = findViewById(R.id.textView);
        textView.setText(text);
    }

    private void ask(String text) {
        robot.askQuestion(text);
        TextView textView = findViewById(R.id.textView);
        textView.setText(text);
    }


}