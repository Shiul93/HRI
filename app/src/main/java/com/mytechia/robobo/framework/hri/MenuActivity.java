package com.mytechia.robobo.framework.hri;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MenuActivity extends AppCompatActivity {
String TAG = "MENU_ACTIVITY";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        final Button button = (Button) findViewById(R.id.button);
        button.setText("Face Message Activity");

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG,"CLICK");
                Intent intent = new Intent(getApplicationContext(), FaceMessageActivity.class);
                startActivity(intent);

            }
        });

        final Button button1 = (Button) findViewById(R.id.button2);
        button1.setText("Color Detection Activity");
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG,"ColorDetectionActivity");
                Intent intent = new Intent(getApplicationContext(),ColorDetectActivity.class);
                startActivity(intent);
            }
        });

        final Button button2 = (Button) findViewById(R.id.button3);
        button2.setText("Speech Rob Activity");
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG,"Speech Rob Activity");
                Intent intent = new Intent(getApplicationContext(),SpeechRobActivity.class);
                startActivity(intent);
            }
        });

        final Button button3 = (Button) findViewById(R.id.button4);
        button3.setText("Vigilante Rob Activity");
        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG,"Vigilante Rob Activity");
                Intent intent = new Intent(getApplicationContext(),VigilanteRobActivity.class);
                startActivity(intent);
            }
        });

        final Button button4 = (Button) findViewById(R.id.button5);
        button4.setText("Music Rob Activity");
        button4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG,"Music Rob Activity");
                Intent intent = new Intent(getApplicationContext(),MusicRobActivity.class);
                startActivity(intent);
            }
        });



    }
}
