package com.mytechia.robobo.framework.surveillanceroboboexample;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.TextureView;
import android.widget.ImageView;
import android.widget.TextView;

import com.mytechia.commons.framework.exception.InternalErrorException;
import com.mytechia.robobo.framework.RoboboManager;
import com.mytechia.robobo.framework.exception.ModuleNotFoundException;
import com.mytechia.robobo.framework.hri.emotion.Emotion;
import com.mytechia.robobo.framework.hri.emotion.IEmotionModule;
import com.mytechia.robobo.framework.hri.emotion.webgl.WebGLEmotionDisplayActivity;
import com.mytechia.robobo.framework.hri.messaging.IMessagingModule;
import com.mytechia.robobo.framework.hri.sound.clapDetection.IClapDetectionModule;
import com.mytechia.robobo.framework.hri.sound.clapDetection.IClapListener;
import com.mytechia.robobo.framework.hri.sound.emotionSound.IEmotionSoundModule;
import com.mytechia.robobo.framework.hri.sound.soundDispatcherModule.ISoundDispatcherModule;
import com.mytechia.robobo.framework.hri.speech.production.ISpeechProductionModule;
import com.mytechia.robobo.framework.hri.vision.basicCamera.Frame;
import com.mytechia.robobo.framework.hri.vision.basicCamera.ICameraListener;
import com.mytechia.robobo.framework.hri.vision.basicCamera.ICameraModule;
import com.mytechia.robobo.framework.hri.vision.faceDetection.IFaceDetectionModule;
import com.mytechia.robobo.framework.hri.vision.faceDetection.IFaceListener;
import com.mytechia.robobo.framework.service.RoboboServiceHelper;
import com.mytechia.robobo.rob.BluetoothRobInterfaceModule;
import com.mytechia.robobo.rob.IRob;
import com.mytechia.robobo.rob.IRobInterfaceModule;
import com.mytechia.robobo.rob.LEDsModeEnum;
import com.mytechia.robobo.rob.movement.IRobMovementModule;
import com.mytechia.robobo.rob.util.RoboboDeviceSelectionDialog;
import com.mytechia.robobo.util.Color;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by luis on 3/8/16.
 */
public class MainActivity extends Activity implements IFaceListener,ICameraListener,IClapListener {

    private static final String TAG="MainActivity";


    private RoboboServiceHelper roboboHelper;
    private RoboboManager roboboManager;

    private ProgressDialog waitDialog;


    private ICameraModule cameraModule;
    private IFaceDetectionModule faceDetector;
    private IMessagingModule msgModule;


    private IEmotionModule emotionModule;
    private ISpeechProductionModule speechModule;
    private IEmotionSoundModule emotionSoundModule;
    private ISoundDispatcherModule soundDispatcherModule;
    private IClapDetectionModule clapDetectionModule;

    private IRobInterfaceModule interfaceModule;
    private IRobMovementModule movementModule;

    private TextView textView = null;
    private SurfaceView surfaceView = null;
    private ImageView imageView = null;
    private TextureView textureView = null;

    private IRob iRob;
    private Frame actualFrame ;
    Timer sweepTimer = new Timer();
    Timer policeTimer = new Timer();
    Timer stopTimer = new Timer();
    TimerTask stopTask;
    TimerTask sweepTask;
    TimerTask policeTask;
    private boolean direction = false;
    private boolean lights = false;

    private long lastDetection = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        showRoboboDeviceSelectionDialog();
    }
    private void startRoboboApplication() {

        roboboHelper.launchDisplayActivity(WebGLEmotionDisplayActivity.class);

        try {
            Log.d(TAG,"MARCA 0");

            soundDispatcherModule=
                    roboboManager.getModuleInstance(ISoundDispatcherModule.class);
            emotionModule =
                    roboboManager.getModuleInstance(IEmotionModule.class);
            speechModule =
                    roboboManager.getModuleInstance(ISpeechProductionModule.class);
            emotionSoundModule=
                    roboboManager.getModuleInstance(IEmotionSoundModule.class);
            clapDetectionModule=
                    roboboManager.getModuleInstance(IClapDetectionModule.class);
            interfaceModule=
                    roboboManager.getModuleInstance(IRobInterfaceModule.class);
            cameraModule=
                    roboboManager.getModuleInstance(ICameraModule.class);
            faceDetector=
                    roboboManager.getModuleInstance(IFaceDetectionModule.class);
            movementModule=
                    roboboManager.getModuleInstance(IRobMovementModule.class);
            msgModule=
                    roboboManager.getModuleInstance(IMessagingModule.class);

        } catch (ModuleNotFoundException e) {
            e.printStackTrace();
        }
        Log.d(TAG,"MARCA 1");
        cameraModule.suscribe(this);
        Log.d(TAG,"MARCA 2");
        faceDetector.suscribe(this);
        Log.d(TAG,"MARCA 3");
        clapDetectionModule.suscribe(this);
        Log.d(TAG,"MARCA 4");


        Log.d(TAG,"MARCA 5");
        iRob =interfaceModule.getRobInterface();
        Log.d(TAG,"MARCA 6");

        try {
            movementModule.moveTilt((short)5,90);
        } catch (InternalErrorException e) {
            e.printStackTrace();
        }


//        sweepTimer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                Log.d(TAG,"Starting sonund dispatcher");
//                soundDispatcherModule.runDispatcher();
//            }
//        },5000);


        soundDispatcherModule.runDispatcher();



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






    /** Shows a Robobo device selection dialog, suscribes to device selection
     * events to "wait" until the user selects a device, and then starts
     * the Robobo Framework using the RoboboHelper inside an AsyncTask to
     * not freeze the UI code.
     */
    private void showRoboboDeviceSelectionDialog() {

        RoboboDeviceSelectionDialog dialog = new RoboboDeviceSelectionDialog();
        dialog.setListener(new RoboboDeviceSelectionDialog.Listener() {
            @Override
            public void roboboSelected(String roboboName) {

                final String roboboBluetoothName = roboboName;

                //start the framework in background
                AsyncTask<Void, Void, Void> launchRoboboService =
                        new AsyncTask<Void, Void, Void>() {
                            @Override
                            protected Void doInBackground(Void... params) {
                                launchAndConnectRoboboService(roboboBluetoothName);
                                return null;
                            }
                        };
                launchRoboboService.execute();

            }

            @Override
            public void selectionCancelled() {

            }

            @Override
            public void bluetoothIsDisabled() {
                finish();
            }

        });
        dialog.show(getFragmentManager(),"BLUETOOTH-DIALOG");

    }


    private void launchAndConnectRoboboService(String roboboBluetoothName) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //wait to dialog shown during the startup of the framework and the bluetooth connection
                waitDialog = ProgressDialog.show(MainActivity.this,
                        "Conectando","conectando", true);
            }
        });


        //we use the RoboboServiceHelper class to manage the startup and binding
        //of the Robobo Manager service and Robobo modules
        roboboHelper = new RoboboServiceHelper(this, new RoboboServiceHelper.Listener() {
            @Override
            public void onRoboboManagerStarted(RoboboManager robobo) {

                //the robobo service and manager have been started up
                roboboManager = robobo;

                //dismiss the wait dialog
                waitDialog.dismiss();

                //start the "custom" robobo application



                sweepTask = new SweepTask();
                sweepTimer.scheduleAtFixedRate(sweepTask,2000,5000);




                startRoboboApplication();

            }

            @Override
            public void onError(String errorMsg) {

                final String error = errorMsg;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        //dismiss the wait dialog
                        waitDialog.dismiss();

                        //show an error dialog


                    }
                });

            }

        });

        //start & bind the Robobo service
        Bundle options = new Bundle();
        options.putString(BluetoothRobInterfaceModule.ROBOBO_BT_NAME_OPTION, roboboBluetoothName);
        roboboHelper.bindRoboboService(options);

    }

    @Override
    public void onFaceDetected(PointF faceCoords, float eyesDistance) {
        if ((System.currentTimeMillis()-lastDetection)>10000) {
            lastDetection = System.currentTimeMillis();
            policeTask = new PoliceTask();
            policeTimer.scheduleAtFixedRate(policeTask,2000,200);
            emotionModule.setTemporalEmotion(Emotion.ANGRY,10000,Emotion.NORMAL);
            stopTimer.schedule(new StopTask(),10000);
            emotionSoundModule.playSound(IEmotionSoundModule.ALERT_SOUND);
            //msgModule.sendMessage("FACE DETECTED", "lfllamas93@gmail.com", actualFrame.getBitmap());
        }}

    @Override
    public void onNewFrame(Frame frame) {
        actualFrame = frame;
    }

    @Override
    public void onClap(double time) {



    }


    class SweepTask extends TimerTask {

        @Override
        public void run() {
            Log.d(TAG,"SweepTask");
            if (direction){
                try {
                    movementModule.movePan((short)5,120);
                    direction = !direction;
                } catch (InternalErrorException e) {
                    e.printStackTrace();
                }
            }else{
                try {
                    movementModule.movePan((short)5,240);
                    direction = !direction;
                } catch (InternalErrorException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    class PoliceTask extends TimerTask {
        Color red = new Color(255,0,0);
        Color blue = new Color(0,0,255);
        @Override
        public void run() {
            Log.d(TAG,"SweepTask");
            if (lights){
                try {


                    iRob.setLEDColor(0, red);
                    iRob.setLEDColor(1, red);
                    iRob.setLEDColor(2, red);
                    iRob.setLEDColor(3, red);
                    iRob.setLEDColor(4, red);
                    iRob.setLEDColor(5, red);

                    iRob.setLEDColor(6, blue);

                    iRob.setLEDColor(7, red);

                    iRob.setLEDColor(8, blue);

                    iRob.setLEDColor(9, red);


                    lights = !lights;
                } catch (InternalErrorException e) {
                    e.printStackTrace();
                }
            }else{
                try {


                    iRob.setLEDColor(0, blue);
                    iRob.setLEDColor(1, blue);
                    iRob.setLEDColor(2, blue);
                    iRob.setLEDColor(3, blue);
                    iRob.setLEDColor(4, blue);
                    iRob.setLEDColor(5, blue);

                    iRob.setLEDColor(6, red);

                    iRob.setLEDColor(7, blue);

                    iRob.setLEDColor(8, red);

                    iRob.setLEDColor(9, blue);

                    iRob.setLEDColor(10, red);

                    iRob.setLEDColor(11, blue);

                    iRob.setLEDColor(12, red);

                    iRob.setLEDColor(13, blue);

                    iRob.setLEDColor(14, red);




                    lights = !lights;
                } catch (InternalErrorException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    class StopTask extends TimerTask{
        Color green = new Color(0,255,0);
        @Override
        public void run() {
            policeTimer.cancel();
            policeTimer = new Timer();
            try {
                for (int i=0;i<10;i++){
                    iRob.setLEDColor(i, green);
                }
            } catch (InternalErrorException e) {
                e.printStackTrace();
            }
        }
    }


}
