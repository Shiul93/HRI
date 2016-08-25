package com.mytechia.robobo.framework.hri;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.TextureView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mytechia.robobo.framework.RoboboManager;
import com.mytechia.robobo.framework.exception.ModuleNotFoundException;
import com.mytechia.robobo.framework.hri.messaging.IMessagingModule;
import com.mytechia.robobo.framework.hri.sound.noteDetection.Note;
import com.mytechia.robobo.framework.hri.vision.basicCamera.Frame;
import com.mytechia.robobo.framework.hri.vision.basicCamera.ICameraListener;
import com.mytechia.robobo.framework.hri.vision.basicCamera.ICameraModule;
import com.mytechia.robobo.framework.hri.vision.colorDetection.IColorDetectionModule;
import com.mytechia.robobo.framework.hri.vision.colorDetection.IColorListener;
import com.mytechia.robobo.framework.hri.vision.faceDetection.IFaceDetectionModule;
import com.mytechia.robobo.framework.service.RoboboServiceHelper;

/**
 * Created by luis on 10/8/16.
 */
public class ColorDetectActivity extends Activity implements ICameraListener, IColorListener {
    private static final String TAG="MainActivity";


    private RoboboServiceHelper roboboHelper;
    private RoboboManager roboboManager;


    private ICameraModule camModule;
    private IColorDetectionModule colorModule;


    private RelativeLayout rellayout = null;
    private TextView textView = null;
    private SurfaceView surfaceView = null;
    private ImageView imageView = null;
    private TextureView textureView = null;
    private Frame actualFrame ;
    private Note prevNote;
    private Frame lastFrame;
    private boolean paused = true;
    private long lastDetection = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_robobo_custom_main);
        this.textView = (TextView) findViewById(R.id.textView);
        this.rellayout = (RelativeLayout) findViewById(R.id.rellayout);
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

            this.camModule = this.roboboManager.getModuleInstance(ICameraModule.class);
            this.colorModule = this.roboboManager.getModuleInstance(IColorDetectionModule.class);

        } catch (ModuleNotFoundException e) {
            e.printStackTrace();
        }


        camModule.suscribe(this);
        colorModule.suscribe(this);

        camModule.signalInit();



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
    public void onNewFrame(final Frame frame) {
        lastFrame = frame;
        //TODO CAMBIAR ESTO ROLLO LISTENERS
        colorModule.processFrame(frame.getBitmap());



    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        paused = true;
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        paused = false;
    }

    @Override
    public void onNewColor(final int colorrgb,final int nearest_color,final int x, final int y, final int height, final int width, final Bitmap borders) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {



            }
        });
        Bitmap bm = borders;
        final Bitmap mutableBitmap = bm.copy(Bitmap.Config.ARGB_8888, true);

        final Canvas canvas = new Canvas(mutableBitmap);

        Paint paint = new Paint();
        Paint paintred = new Paint();
        paintred.setColor(Color.RED);
        paint.setAntiAlias(true);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);



        //paint.setColor(Color.GREEN);

        canvas.drawRect(new Rect(x,y,x+width,y+height),paint);


        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                imageView.setImageBitmap(mutableBitmap);

                rellayout.setBackgroundColor(nearest_color);

            }
        });


    }
}
