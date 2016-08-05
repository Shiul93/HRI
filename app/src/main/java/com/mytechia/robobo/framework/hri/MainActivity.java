package com.mytechia.robobo.framework.hri;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mytechia.robobo.framework.RoboboManager;
import com.mytechia.robobo.framework.exception.ModuleNotFoundException;

import com.mytechia.robobo.framework.hri.messaging.IMessagingModule;
import com.mytechia.robobo.framework.hri.sound.clapDetection.IClapDetectionModule;
import com.mytechia.robobo.framework.hri.sound.clapDetection.IClapListener;
import com.mytechia.robobo.framework.hri.sound.noteDetection.INoteDetectionModule;
import com.mytechia.robobo.framework.hri.sound.noteDetection.INoteListener;
import com.mytechia.robobo.framework.hri.sound.noteDetection.Note;
import com.mytechia.robobo.framework.hri.sound.pitchDetection.IPitchDetectionModule;
import com.mytechia.robobo.framework.hri.sound.soundDispatcherModule.ISoundDispatcherModule;
import com.mytechia.robobo.framework.hri.speech.production.ISpeechProductionModule;
import com.mytechia.robobo.framework.hri.speech.recognition.ISpeechRecognitionListener;
import com.mytechia.robobo.framework.hri.speech.recognition.ISpeechRecognitionModule;
import com.mytechia.robobo.framework.hri.touch.ITouchListener;
import com.mytechia.robobo.framework.hri.touch.ITouchModule;
import com.mytechia.robobo.framework.hri.touch.TouchGestureDirection;
import com.mytechia.robobo.framework.hri.vision.basicCamera.ICameraListener;
import com.mytechia.robobo.framework.hri.vision.basicCamera.ICameraModule;
import com.mytechia.robobo.framework.hri.vision.basicCamera.android.Frame;
import com.mytechia.robobo.framework.hri.vision.faceDetection.IFaceDetectionModule;
import com.mytechia.robobo.framework.hri.vision.faceDetection.IFaceListener;
import com.mytechia.robobo.framework.service.RoboboServiceHelper;

public class MainActivity extends Activity implements INoteListener{

    private static final String TAG="MainActivity";


    private RoboboServiceHelper roboboHelper;
    private RoboboManager roboboManager;


    private ISoundDispatcherModule dispatcherModule;
    private IPitchDetectionModule pitchModule;
    private INoteDetectionModule noteDetectionModule;
    private IMessagingModule msgModule;

    private TextView textView = null;
    private SurfaceView surfaceView = null;
    private ImageView imageView = null;
    private TextureView textureView = null;
    private Frame actualFrame ;
    private Note prevNote;
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
            this.dispatcherModule = this.roboboManager.getModuleInstance(ISoundDispatcherModule.class);
            this.noteDetectionModule = this.roboboManager.getModuleInstance(INoteDetectionModule.class);
            this.msgModule = this.roboboManager.getModuleInstance(IMessagingModule.class);
        } catch (ModuleNotFoundException e) {
            e.printStackTrace();
        }

        noteDetectionModule.suscribe(this);
        dispatcherModule.runDispatcher();



    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //msgModule.sendMessage("TEST","lfllamas93@gmail.com");
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
    public void onNoteDetected(final Note note) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {


                textView.setText(note.toString());

            }
        });

    }

    @Override
    public void onNoteEnd(final Note note,final long time) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {


                textView.setText(note.toString()+" END Time: "+time);

            }
        });

    }

    @Override
    public void onNewNote(final Note note) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {


                textView.setText(note.toString()+" NEW");

            }
        });

    }
}
