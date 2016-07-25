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
import android.widget.ImageView;
import android.widget.TextView;

import com.mytechia.robobo.framework.RoboboManager;
import com.mytechia.robobo.framework.exception.ModuleNotFoundException;
import com.mytechia.robobo.framework.hri.sound.clapDetection.IClapDetectionModule;
import com.mytechia.robobo.framework.hri.sound.clapDetection.IClapListener;
import com.mytechia.robobo.framework.hri.speech.production.ISpeechProductionModule;
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

public class MainActivity extends Activity implements ITouchListener,ICameraListener,IFaceListener,IClapListener{

    private static final String TAG="MainActivity";


    private RoboboServiceHelper roboboHelper;
    private RoboboManager roboboManager;
    private ISpeechProductionModule speechProductionModule;
    private ITouchModule touchModule;
    private ICameraModule cameraModule;
    private IFaceDetectionModule faceModule;
    private ISpeechRecognitionModule speechRecognitionModule = null;
    private IClapDetectionModule clapModule = null;
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


            this.speechProductionModule= this.roboboManager.getModuleInstance(ISpeechProductionModule.class);
            this.touchModule = this.roboboManager.getModuleInstance(ITouchModule.class);
            this.cameraModule = this.roboboManager.getModuleInstance(ICameraModule.class);
            this.faceModule = this.roboboManager.getModuleInstance(IFaceDetectionModule.class);
            this.clapModule =this.roboboManager.getModuleInstance(IClapDetectionModule.class);
            //this.speechRecognitionModule = this.roboboManager.getModuleInstance(ISpeechRecognitionModule.class);


            //Log.d(TAG,speechProductionModule.toString());
            //speechRecognitionModule.suscribe(this);
            cameraModule.passTextureView(textureView);
            touchModule.suscribe(this);
            cameraModule.suscribe(this);
            faceModule.suscribe(this);
            clapModule.suscribe(this);






        } catch (ModuleNotFoundException e) {

        }





    }

   /* @Override
    public void onModuleStart() {
        speechRecognitionModule.addPhrase("up");
        speechRecognitionModule.addPhrase("now");
        speechRecognitionModule.addPhrase("test");
        speechRecognitionModule.addPhrase("down");
        speechRecognitionModule.addPhrase("robot");
        speechRecognitionModule.updatePhrases();
    }*/

   /* @Override
    protected void onStop() {
        if (speechRecognitionModule!=null&&speechRecognitionModule.hasStarted())
            speechRecognitionModule.pauseRecognition();
            Log.d(TAG,"ONSTOP");
        super.onStop();
    }

    @Override
    protected void onResume() {
        if (speechRecognitionModule!=null&&speechRecognitionModule.hasStarted())
            speechRecognitionModule.resumeRecognition();
        Log.d(TAG,"ONRESUME");
        super.onResume();
    }*/
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        touchModule.feedTouchEvent(event);
        return super.onTouchEvent(event);
    }


    @Override
    protected void onDestroy() {

        speechProductionModule.sayText("Shutdown!",ISpeechProductionModule.PRIORITY_HIGH);
        super.onDestroy();

        //we unbind and (maybe) stop the Robobo service on exit
        if (roboboHelper != null) {
            roboboHelper.unbindRoboboService();
        }

    }


    /*@Override
    public void phraseRecognized(String phrase, Long timestamp) {
        //speechProductionModule.sayText(phrase,ISpeechProductionModule.PRIORITY_HIGH);
        Log.d(TAG,"Phrase recognized: "+phrase);
        textView.setText(phrase);

    }*/

    @Override
    public void tap(Integer x, Integer y) {
        Log.d(TAG,"TAP");
        cameraModule.foto();
        speechProductionModule.sayText("TAP",ISpeechProductionModule.PRIORITY_HIGH);
        textView.setText(String.format("TAP: (%d,%d)",x,y));
    }

    @Override
    public void touch(Integer x, Integer y) {
        Log.d(TAG,"TOUCH");
        speechProductionModule.sayText("Touch",ISpeechProductionModule.PRIORITY_HIGH);
        textView.setText(String.format("TAP: (%d,%d)",x,y));
    }

    @Override
    public void fling(TouchGestureDirection dir) {
        speechProductionModule.sayText("Fling",ISpeechProductionModule.PRIORITY_HIGH);
        Log.d(TAG,"FLING");
        textView.setText("FLING: "+dir.toString());
    }

    @Override
    public void caress(TouchGestureDirection dir) {
        speechProductionModule.sayText("Caress",ISpeechProductionModule.PRIORITY_HIGH);
        Log.d(TAG,"CARESS");
        textView.setText("CARESS: "+dir.toString());
    }

    @Override
    public void onNewFrame(final Frame frame) {

        //Canvas canvas = new Canvas();
        float left = 500;
        float top = 500;
//        canvas.drawBitmap(frame.getBitmap(),left,top,null);
//        surfaceView.draw(canvas);
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//
//            imageView.setImageBitmap(frame.getBitmap());
//
//
//            }
//        });

        actualFrame = frame;

        Log.d(TAG,"New Frame");

    }

    @Override
    public void onFaceDetected(final PointF faceCoords) {
        Log.d(TAG, "FACE DETECTED");
        Bitmap bm = Bitmap.createBitmap(actualFrame.getBitmap());
        final Bitmap mutableBitmap = bm.copy(Bitmap.Config.ARGB_8888, true);

        final Canvas canvas = new Canvas(mutableBitmap);

        Paint paint = new Paint();
        Paint paintred = new Paint();
        paintred.setColor(Color.RED);
        paint.setAntiAlias(true);
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);
        //paint.setColor(Color.GREEN);

        canvas.drawCircle((int)faceCoords.x,(int)faceCoords.y,(float)100,paint);
        //paint.setColor(Color.TRANSPARENT);
        canvas.drawCircle((int)faceCoords.x,(int)faceCoords.y,(float)5,paint);

        canvas.drawCircle((int)faceCoords.x,(int)faceCoords.y,(float)4,paintred);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                imageView.setImageBitmap(mutableBitmap);
                textView.setText(String.format("FACE: (%f,%f)",faceCoords.x,faceCoords.y));


            }
        });


    }

    @Override
    public void onClap() {
        speechProductionModule.sayText("CLAP!!",ISpeechProductionModule.PRIORITY_HIGH);
        runOnUiThread(new Runnable() {

            @Override
            public void run() {


                textView.setText("CLAP!!");

            }
        });

    }
}
