package com.mytechia.robobo.framework.hri;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.TextureView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mytechia.commons.framework.exception.InternalErrorException;
import com.mytechia.robobo.framework.RoboboManager;
import com.mytechia.robobo.framework.exception.ModuleNotFoundException;
import com.mytechia.robobo.framework.hri.sound.noteDetection.INoteDetectionModule;
import com.mytechia.robobo.framework.hri.sound.noteDetection.INoteListener;
import com.mytechia.robobo.framework.hri.sound.noteDetection.Note;
import com.mytechia.robobo.framework.hri.sound.pitchDetection.IPitchDetectionModule;
import com.mytechia.robobo.framework.hri.sound.pitchDetection.IPitchListener;
import com.mytechia.robobo.framework.hri.sound.soundDispatcherModule.ISoundDispatcherModule;
import com.mytechia.robobo.framework.hri.touch.ITouchListener;
import com.mytechia.robobo.framework.hri.touch.ITouchModule;
import com.mytechia.robobo.framework.hri.touch.TouchGestureDirection;
import com.mytechia.robobo.framework.hri.vision.basicCamera.Frame;
import com.mytechia.robobo.framework.service.RoboboServiceHelper;
import com.mytechia.robobo.rob.BluetoothRobInterfaceModule;
import com.mytechia.robobo.rob.movement.IRobMovementModule;
import com.mytechia.robobo.rob.util.RoboboDeviceSelectionDialog;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by luis on 3/8/16.
 */
public class OrganicTouchRobActivity extends Activity implements ITouchListener {

    private static final String TAG="MusicActivity";


    private RoboboServiceHelper roboboHelper;
    private RoboboManager roboboManager;

    private ProgressDialog waitDialog;




    private IRobMovementModule movementModule;
    private ITouchModule touchModule;

    private TextView textView = null;
    private SurfaceView surfaceView = null;
    private ImageView imageView = null;
    private TextureView textureView = null;
    private Frame actualFrame ;

    private RelativeLayout rellayout = null;

    private boolean executing = false;

    private Timer timer;
    private TimerTask waitTask;

    private LinkedList<MovCommand> commandList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_robobo_custom_main);
        this.textView = (TextView) findViewById(R.id.textView);
        this.rellayout = (RelativeLayout) findViewById(R.id.rellayout);
        // this.surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        this.imageView = (ImageView) findViewById(R.id.imageView);


        showRoboboDeviceSelectionDialog();
    }

    private void startRoboboApplication() {
        timer = new Timer();
        waitTask = new WaitTask();



        try {


            this.movementModule = this.roboboManager.getModuleInstance(IRobMovementModule.class);
            this.touchModule = this.roboboManager.getModuleInstance(ITouchModule.class);


        } catch (ModuleNotFoundException e) {
            e.printStackTrace();
        }





    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {

        touchModule.feedTouchEvent(event);
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





    public void executeCommands(LinkedList<MovCommand> commands) {
//        try {
//
//            NoteCommand command = commands.pop();
//            waitTask = new WaitTask();
//            Log.d(TAG, "Executing command "+command.toString());
//
//
//            if (command.note.note ==( "C")){
//                movementModule.moveForwardsTime((short)60,command.duration*2);
//                timer.schedule(waitTask,(command.duration*2)+100);
//            }
//            if (command.note.note ==( "D")){
//                movementModule.moveBackwardsTime((short)60,command.duration);
//                timer.schedule(waitTask,command.duration+100);
//            }
//            if (command.note.note == "E"){
//                movementModule.turnLeftTime((short)60,(command.duration*2));
//                timer.schedule(waitTask,(command.duration*2)+100);
//            }
//            if (command.note.note ==( "G")){
//                movementModule.turnRightTime((short)60,(command.duration*2));
//                timer.schedule(waitTask,(command.duration*2)+100);
//
//            }
//
//        } catch (InternalErrorException e){
//
//        } catch (NoSuchElementException e){
//            executing = false;
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//
//                    rellayout.setBackgroundColor(Color.RED);
//
//
//                }
//            });
//        }

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
                waitDialog = ProgressDialog.show(OrganicTouchRobActivity.this,
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
    public void tap(Integer x, Integer y) {

    }

    @Override
    public void touch(Integer x, Integer y) {

    }

    @Override
    public void fling(TouchGestureDirection dir, double angle, long time, double distance) {


    }

    @Override
    public void caress(TouchGestureDirection dir) {

    }

    private class WaitTask extends TimerTask{



        @Override
        public void run() {
            executeCommands(commandList);
        }

    }

    private class MovCommand{
        private String type;
        private short power;
        private int distance;


        public MovCommand(String type, short power, int distance){
            this.type = type;
            this.power = power;
            this.distance = distance;
        }
        public void execute(){
            try {
                if (type.equals("turn")){

                        movementModule.turnLeftAngle(power,distance);
                }

                if (type.equals("advance")){

                    movementModule.turnLeftAngle(power,distance);
                }

                if (type.equals("pan")){

                    movementModule.movePan(distance);
                }

                if (type.equals("tilt")){

                    movementModule.moveTilt(distance);
                }



            } catch (InternalErrorException e) {
                e.printStackTrace();
            }
        }


    }
}
