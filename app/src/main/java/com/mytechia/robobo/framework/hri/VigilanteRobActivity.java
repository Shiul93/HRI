package com.mytechia.robobo.framework.hri;

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
import com.mytechia.robobo.framework.hri.messaging.IMessagingModule;
import com.mytechia.robobo.framework.hri.speech.recognition.ISpeechRecognitionListener;
import com.mytechia.robobo.framework.hri.speech.recognition.ISpeechRecognitionModule;
import com.mytechia.robobo.framework.hri.vision.basicCamera.Frame;
import com.mytechia.robobo.framework.hri.vision.basicCamera.ICameraListener;
import com.mytechia.robobo.framework.hri.vision.basicCamera.ICameraModule;
import com.mytechia.robobo.framework.hri.vision.faceDetection.IFaceDetectionModule;
import com.mytechia.robobo.framework.hri.vision.faceDetection.IFaceListener;
import com.mytechia.robobo.framework.service.RoboboServiceHelper;
import com.mytechia.robobo.rob.BluetoothRobInterfaceModule;
import com.mytechia.robobo.rob.movement.IRobMovementModule;
import com.mytechia.robobo.rob.util.RoboboDeviceSelectionDialog;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by luis on 3/8/16.
 */
public class VigilanteRobActivity extends Activity implements IFaceListener,ICameraListener {

    private static final String TAG="MainActivity";


    private RoboboServiceHelper roboboHelper;
    private RoboboManager roboboManager;

    private ProgressDialog waitDialog;


    private ICameraModule cameraModule;
    private IFaceDetectionModule faceDetector;
    private IMessagingModule msgModule;

    private IRobMovementModule movementModule;

    private TextView textView = null;
    private SurfaceView surfaceView = null;
    private ImageView imageView = null;
    private TextureView textureView = null;
    private Frame actualFrame ;
    Timer timer;
    TimerTask sweepTask;
    private boolean direction = false;

    private long lastDetection = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_robobo_custom_main);
        this.textView = (TextView) findViewById(R.id.textView);
        // this.surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        this.imageView = (ImageView) findViewById(R.id.imageView);


        showRoboboDeviceSelectionDialog();
    }
    private void startRoboboApplication() {

        try {

            this.cameraModule = this.roboboManager.getModuleInstance(ICameraModule.class);
            this.faceDetector = this.roboboManager.getModuleInstance(IFaceDetectionModule.class);
            this.movementModule = this.roboboManager.getModuleInstance(IRobMovementModule.class);
            this.msgModule = this.roboboManager.getModuleInstance(IMessagingModule.class);

        } catch (ModuleNotFoundException e) {
            e.printStackTrace();
        }
        cameraModule.suscribe(this);
        faceDetector.suscribe(this);
Log.d(TAG, "NO SE COMPILAR OOOOO:::::");

       timer = new Timer();
        sweepTask = new SweepTask();
        timer.scheduleAtFixedRate(sweepTask,0,5000);


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
                waitDialog = ProgressDialog.show(VigilanteRobActivity.this,
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
            msgModule.sendMessage("FACE DETECTED", "lfllamas93@gmail.com", actualFrame.getBitmap());
        }}

    @Override
    public void onNewFrame(Frame frame) {
        actualFrame = frame;
    }

    class SweepTask extends TimerTask {

        @Override
        public void run() {
           if (direction){
               try {
                   movementModule.movePan(270);
                   direction = !direction;
               } catch (InternalErrorException e) {
                   e.printStackTrace();
               }
           }else{
               try {
                   movementModule.movePan(90);
                   direction = !direction;
               } catch (InternalErrorException e) {
                   e.printStackTrace();
               }
           }
        }

    }


}
