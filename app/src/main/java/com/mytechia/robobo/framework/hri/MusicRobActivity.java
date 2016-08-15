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
import com.mytechia.robobo.framework.hri.speech.recognition.ISpeechRecognitionListener;
import com.mytechia.robobo.framework.hri.speech.recognition.ISpeechRecognitionModule;
import com.mytechia.robobo.framework.hri.vision.basicCamera.Frame;
import com.mytechia.robobo.framework.service.RoboboServiceHelper;
import com.mytechia.robobo.rob.BluetoothRobInterfaceModule;
import com.mytechia.robobo.rob.movement.IRobMovementModule;
import com.mytechia.robobo.rob.util.RoboboDeviceSelectionDialog;

import java.sql.Time;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by luis on 3/8/16.
 */
public class MusicRobActivity extends Activity implements IPitchListener,INoteListener {

    private static final String TAG="MusicActivity";


    private RoboboServiceHelper roboboHelper;
    private RoboboManager roboboManager;

    private ProgressDialog waitDialog;




    private IRobMovementModule movementModule;
    private ISoundDispatcherModule dispatcher;
    private INoteDetectionModule noteDetectionModule;
    private IPitchDetectionModule pitchModule;

    private TextView textView = null;
    private SurfaceView surfaceView = null;
    private ImageView imageView = null;
    private TextureView textureView = null;
    private Frame actualFrame ;

    private RelativeLayout rellayout = null;

    private ArrayList<String> scale = new ArrayList<>(5);
    private String triggerNote = "A";
    private boolean recording = false;
    private boolean executing = false;

    private Timer timer;
    private TimerTask waitTask;

    private LinkedList<NoteCommand> commandList;
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

        commandList = new LinkedList<>();
        scale.add("A");
        scale.add("C");
        scale.add("D");
        scale.add("E");
        scale.add("G");


        try {
            this.dispatcher = this.roboboManager.getModuleInstance(ISoundDispatcherModule.class);
            this.noteDetectionModule = this.roboboManager.getModuleInstance(INoteDetectionModule.class);
            this.movementModule = this.roboboManager.getModuleInstance(IRobMovementModule.class);
            this.pitchModule = this.roboboManager.getModuleInstance(IPitchDetectionModule.class);

        } catch (ModuleNotFoundException e) {
            e.printStackTrace();
        }
        pitchModule.suscribe(this);
        noteDetectionModule.suscribe(this);

        dispatcher.runDispatcher();



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





    public void executeCommands(LinkedList<NoteCommand> commands) {
        try {

            NoteCommand command = commands.pop();
            waitTask = new WaitTask();
            Log.d(TAG, "Executing command "+command.toString());


            if (command.note.note ==( "C")){
                movementModule.moveForwardsTime((short)60,command.duration*2);
                timer.schedule(waitTask,(command.duration*2)+100);
            }
            if (command.note.note ==( "D")){
                movementModule.moveBackwardsTime((short)60,command.duration);
                timer.schedule(waitTask,command.duration+100);
            }
            if (command.note.note == "E"){
                movementModule.turnLeftTime((short)60,(command.duration*2));
                timer.schedule(waitTask,(command.duration*2)+100);
            }
            if (command.note.note ==( "G")){
                movementModule.turnRightTime((short)60,(command.duration*2));
                timer.schedule(waitTask,(command.duration*2)+100);

            }

        } catch (InternalErrorException e){

        } catch (NoSuchElementException e){
            executing = false;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    rellayout.setBackgroundColor(Color.RED);


                }
            });
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
                waitDialog = ProgressDialog.show(MusicRobActivity.this,
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
    public void onNoteDetected(Note note) {
    }

    @Override
    public void onNoteEnd(Note note, long time) {
        Log.d(TAG,"Note: "+note.note);
        if (!executing) {
            if (note.note == (triggerNote)) {
                if ((recording)&&(time>100)) {
                    recording = false;
                    executing = true;
                    Log.d(TAG,"executing");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            rellayout.setBackgroundColor(Color.BLUE);


                        }
                    });
                    executeCommands(commandList);
                } else {
                    Log.d(TAG,"recording");
                    recording = true;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                           rellayout.setBackgroundColor(Color.GREEN);


                        }
                    });
                }
            } else {
                if (recording) {
                    if (scale.contains(note.note)) {
                        Log.d(TAG,"adding command");
                        commandList.add(new NoteCommand(note, time));
                    } else {
                        //Bad note
                    }
                }
            }

        }
    }

    @Override
    public void onNewNote(Note note) {

    }

    @Override
    public void onPitchdetected(double freq) {

    }

    private class WaitTask extends TimerTask{



        @Override
        public void run() {
            executeCommands(commandList);
        }
    }
    private class NoteCommand{
        public Note note;
        public  long duration;
        public NoteCommand(Note note, long duration){
            this.note = note;
            this.duration=duration;
        }
    }
}
