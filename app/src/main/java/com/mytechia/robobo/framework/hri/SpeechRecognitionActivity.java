package com.mytechia.robobo.framework.hri;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.TextureView;
import android.widget.ImageView;
import android.widget.TextView;

import com.mytechia.robobo.framework.RoboboManager;
import com.mytechia.robobo.framework.exception.ModuleNotFoundException;
import com.mytechia.robobo.framework.hri.speech.recognition.ISpeechRecognitionListener;
import com.mytechia.robobo.framework.hri.speech.recognition.ISpeechRecognitionModule;
import com.mytechia.robobo.framework.hri.vision.basicCamera.Frame;
import com.mytechia.robobo.framework.service.RoboboServiceHelper;

public class SpeechRecognitionActivity extends Activity implements ISpeechRecognitionListener{

    private static final String TAG="MainActivity";


    private RoboboServiceHelper roboboHelper;
    private RoboboManager roboboManager;


    private ISpeechRecognitionModule speechRecognitionModule;

    private TextView textView = null;
    private SurfaceView surfaceView = null;
    private ImageView imageView = null;
    private TextureView textureView = null;
    private Frame actualFrame ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_robobo_custom_main);
        this.textView = (TextView) findViewById(R.id.textView);
       // this.surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        this.imageView = (ImageView) findViewById(R.id.imageView);

//        this.textureView = (TextureView) findViewById(R.id.textureView);
        roboboHelper = new RoboboServiceHelper(this, new RoboboServiceHelper.Listener() {
            @Override
            public void onRoboboManagerStarted(RoboboManager robobo) {

                //the robobo service and manager have been started up
                roboboManager = robobo;

                //dismiss the wait dialog


                //start the "custom" robobo application
                startRoboboApplication();

            }

            @Override
            public void onError(String errorMsg) {

                final String error = errorMsg;


            }

        });

        //start & bind the Robobo service
        Bundle options = new Bundle();
        roboboHelper.bindRoboboService(options);
    }
    private void startRoboboApplication() {

        try {
            this.speechRecognitionModule = this.roboboManager.getModuleInstance(ISpeechRecognitionModule.class);
        } catch (ModuleNotFoundException e) {
            e.printStackTrace();
        }


        speechRecognitionModule.suscribe(this);

    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {

        return super.onTouchEvent(event);
    }


    @Override
    protected void onDestroy() {


        super.onDestroy();

        //we unbind and (maybe) stop the Robobo service on exit
        if (roboboHelper != null) {
            roboboHelper.unbindRoboboService();
        }

    }




    @Override
    public void phraseRecognized(final String phrase, Long timestamp) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {


                textView.setText(phrase);

            }
        });
    }

    @Override
    public void onModuleStart() {

        speechRecognitionModule.addPhrase("front");
        speechRecognitionModule.addPhrase("back");
        speechRecognitionModule.addPhrase("right");
        speechRecognitionModule.addPhrase("left");
        speechRecognitionModule.addPhrase("rob");

        speechRecognitionModule.updatePhrases();

        speechRecognitionModule.setGrammarSearch("MovSearch","movements.gram");

    }
}
