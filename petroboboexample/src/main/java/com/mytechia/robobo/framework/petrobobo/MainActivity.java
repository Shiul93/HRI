package com.mytechia.robobo.framework.petrobobo;

/**
 * Created by luis on 24/8/16.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

import com.mytechia.commons.framework.exception.InternalErrorException;
import com.mytechia.robobo.framework.RoboboManager;
import com.mytechia.robobo.framework.exception.ModuleNotFoundException;
import com.mytechia.robobo.framework.hri.emotion.Emotion;
import com.mytechia.robobo.framework.hri.emotion.IEmotionModule;
import com.mytechia.robobo.framework.hri.emotion.webgl.WebGLEmotionDisplayActivity;
import com.mytechia.robobo.framework.hri.sound.clapDetection.IClapDetectionModule;
import com.mytechia.robobo.framework.hri.sound.clapDetection.IClapListener;
import com.mytechia.robobo.framework.hri.sound.emotionSound.IEmotionSoundModule;
import com.mytechia.robobo.framework.hri.sound.noteDetection.INoteDetectionModule;
import com.mytechia.robobo.framework.hri.sound.noteDetection.INoteListener;
import com.mytechia.robobo.framework.hri.sound.noteGeneration.INoteGeneratorModule;
import com.mytechia.robobo.framework.hri.sound.noteGeneration.INotePlayListener;
import com.mytechia.robobo.framework.hri.sound.noteGeneration.Note;
import com.mytechia.robobo.framework.hri.sound.soundDispatcherModule.ISoundDispatcherModule;
import com.mytechia.robobo.framework.hri.speech.production.ISpeechProductionModule;
import com.mytechia.robobo.framework.hri.speech.recognition.ISpeechRecognitionListener;
import com.mytechia.robobo.framework.hri.speech.recognition.ISpeechRecognitionModule;
import com.mytechia.robobo.framework.hri.touch.ITouchListener;
import com.mytechia.robobo.framework.hri.touch.ITouchModule;
import com.mytechia.robobo.framework.hri.touch.TouchGestureDirection;
import com.mytechia.robobo.framework.hri.vision.basicCamera.ICameraModule;
import com.mytechia.robobo.framework.hri.vision.colorDetection.IColorDetectionModule;
import com.mytechia.robobo.framework.hri.vision.colorDetection.IColorListener;
import com.mytechia.robobo.framework.hri.vision.faceDetection.IFaceDetectionModule;
import com.mytechia.robobo.framework.hri.vision.faceDetection.IFaceListener;
import com.mytechia.robobo.framework.service.RoboboServiceHelper;
import com.mytechia.robobo.rob.BluetoothRobInterfaceModule;
import com.mytechia.robobo.rob.IRobInterfaceModule;
import com.mytechia.robobo.rob.movement.IRobMovementModule;
import com.mytechia.robobo.rob.util.RoboboDeviceSelectionDialog;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends Activity implements  INotePlayListener, ITouchListener, ISpeechRecognitionListener, IFaceListener, IColorListener {

    private RoboboServiceHelper roboboHelper;
    private RoboboManager robobo;

    private ProgressDialog waitDialog;

    private String TAG = "PETROBOBO";

    private IEmotionModule emotionModule;
    private ISpeechProductionModule speechModule;

    private INoteGeneratorModule noteGeneratorModule;
    private IEmotionSoundModule emotionSoundModule;
    private ITouchModule touchModule;
    private ISpeechRecognitionModule recognitionModule;
    private IFaceDetectionModule faceDetectionModule;
    private ICameraModule cameraModule;
    private IColorDetectionModule colorDetectionModule;

    private IRobInterfaceModule interfaceModule;
    private IRobMovementModule movementModule;


    private static Random r = new Random();

    private List<Note> notes = new ArrayList<>();
    private LinkedList<Note> checkNotes = new LinkedList<>();


    private boolean ready = false;
    private boolean playing = false;
    private boolean playingnotes = false;
    private boolean listening = false;

    Timer timer = new Timer();
    TimerTask speechTask;
    Timer timermovement = new Timer();
    TimerTask movTask;
    int negatecount = 0;

    private boolean seeingLight = false;


    //region Listeners

    //region ActivityListeners
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        roboboHelper = new RoboboServiceHelper(this, new RoboboApplication());
//        roboboHelper.bindRoboboService(new Bundle());
        showRoboboDeviceSelectionDialog();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        roboboHelper.unbindRoboboService();
        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        touchModule.feedTouchEvent(event);
        return super.onTouchEvent(event);
    }

    //endregion



    //region notePlayer Listeners

    @Override
    public void onNotePlayEnd() {

    }

    @Override
    public void onSequencePlayEnd() {
        emotionModule.setCurrentEmotion(Emotion.SURPRISED);
        Log.d("TAG","PLAYING = FALSE");
        playingnotes = false;

        listening = true;
        checkNotes = new LinkedList<>(notes);
        Log.d("TAG", checkNotes.toString());

    }

    //endregion



    //region ColorListener
    @Override
    public void onNewColor(int colorrgb, int nearest_color) {

    }
    //endregion

    //region FaceListener
    @Override
    public void onFaceDetected(PointF faceCoords, float eyesDistance) {

    }
    //endregion


    //region SpeechListener
    @Override
    public void phraseRecognized(String phrase, Long timestamp) {

    }

    @Override
    public void onModuleStart() {

    }
    //endregion

    //region TouchListener
    @Override
    public void tap(Integer x, Integer y) {
        noteGeneratorModule.playNote(Note.A4,250);
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
    //endregion



//endregion

    //region App Initialization things





    protected void startRoboboApplication() {

        //roboboHelper.launchDisplayActivity(WebGLEmotionDisplayActivity.class);

        try {

            emotionModule =
                    robobo.getModuleInstance(IEmotionModule.class);
            speechModule =
                    robobo.getModuleInstance(ISpeechProductionModule.class);

            noteGeneratorModule=
                    robobo.getModuleInstance(INoteGeneratorModule.class);
            emotionSoundModule=
                    robobo.getModuleInstance(IEmotionSoundModule.class);

            movementModule=
                    robobo.getModuleInstance(IRobMovementModule.class);
            interfaceModule=
                    robobo.getModuleInstance(IRobInterfaceModule.class);
            cameraModule=
                    robobo.getModuleInstance(ICameraModule.class);
            colorDetectionModule=
                    robobo.getModuleInstance(IColorDetectionModule.class);
            faceDetectionModule=
                    robobo.getModuleInstance(IFaceDetectionModule.class);
            recognitionModule=
                    robobo.getModuleInstance(ISpeechRecognitionModule.class);
            touchModule=
                    robobo.getModuleInstance(ITouchModule.class);
        }

        catch(ModuleNotFoundException e) {
            final Exception ex = e;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showErrorDialog(ex.getMessage());
                }
            });
        }

        colorDetectionModule.suscribe(this);
        faceDetectionModule.suscribe(this);
        noteGeneratorModule.suscribe(this);
        touchModule.suscribe(this);


        try {
            interfaceModule.getRobInterface().resetPanTiltOffset();
        } catch (InternalErrorException e) {
            e.printStackTrace();
        }


    }

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
            public void onRoboboManagerStarted(RoboboManager robobom) {

                //the robobo service and manager have been started up
                robobo = robobom;

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




    /** Shows an error dialog with the message 'msg'
     *
     * @param msg the message to be shown in the error dialog
     */
    protected void showErrorDialog(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(com.mytechia.robobo.framework.R.string.title_error_dialog).
                setMessage(msg);
        builder.setPositiveButton(com.mytechia.robobo.framework.R.string.ok_msg, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                System.exit(0);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    //endregion

    //region Game Methods

    boolean checkNote(Note note)throws NoSuchElementException {

        Note actualnote = checkNotes.pop();

        if (actualnote.note.equals(note.note)){
            if (checkNotes.isEmpty()){
                throw  new NoSuchElementException();
            }
            return true;
        }else {
            return false;
        }
    }

    private static int generateRandom(int min, int max) {
        // max - min + 1 will create a number in the range of min and max, including max. If you don´t want to include it, just delete the +1.
        // adding min to it will finally create the number in the range between min and max
        return r.nextInt(max-min+1) + min;
    }

    Note generateRandomNote(){
        int foo = generateRandom(0,4);
        int octave = generateRandom(5,6);
        String nota = "";

        switch (foo){
            case 0:
                nota = "A";
                break;
            case 1:
                nota = "C";
                break;
            case 2:
                nota = "D";
                break;
            case 3:
                nota = "E";
                break;
            case 4:
                nota = "G";
                break;
        }

        for(Note n:Note.values()){
            if ((n.note.equals(nota))&&(n.octave==octave)){
                return n;

            }
        }

        return Note.A4;

    }

    void playNoteSequence(){
        int i;
        emotionModule.setCurrentEmotion(Emotion.SMYLING);
        for (i=0;i<notes.size();i++){
            noteGeneratorModule.addNoteToSequence(notes.get(i),750);
        }
        playingnotes = true;
        noteGeneratorModule.playSequence();
    }

    void startGame(){
        playing = true;
        notes = new ArrayList<Note>();
        notes.add(generateRandomNote());
        sayPhrase();

        //playNoteSequence();

    }

    void continueGame(){
        notes.add(generateRandomNote());
        sayPhrase();
        //playNoteSequence();
    }

    void gameOver(){
        playing=false;
        ready = false;
        playingnotes = false;
        emotionSoundModule.playSound(IEmotionSoundModule.RIMSHOT_SOUND);
        emotionModule.setTemporalEmotion(Emotion.SAD,3000,Emotion.NORMAL);
        movTask = new NegateClass();
        timermovement.schedule(movTask,100);

    }


    void sayPhrase(){
        int phrase = generateRandom(0,5);
        switch (phrase){
            case 0:
                speechModule.sayText("Check this out!",0);
                break;
            case 1:
                speechModule.sayText("Try this!",0);
                break;
            case 2:
                speechModule.sayText("Follow me if you can!",0);
                break;
            case 3:
                speechModule.sayText("Play this!",0);
                break;
            case 4:
                speechModule.sayText("Repeat after me!",0);
                break;
        }
        speechTask = new WaitForSpeech();
        timer.schedule(speechTask,2000);
    }


    private class WaitForSpeech extends TimerTask {



        @Override
        public void run() {
            playNoteSequence();
        }
    }

    //endregion

    //region Movement Things

    private class NegateClass extends TimerTask{

        @Override
        public void run() {
            try {
                switch (negatecount) {
                    case 0:
                        negatecount++;

                        movementModule.movePan((short)9,165);
                        movTask = new NegateClass();
                        timermovement.schedule(movTask,300);


                        break;
                    case 1:
                        negatecount++;
                        movementModule.movePan((short)9,195);
                        movTask = new NegateClass();
                        timermovement.schedule(movTask,600);
                        break;
                    case 2:
                        negatecount++;

                        movementModule.movePan((short)9,165);
                        movTask = new NegateClass();
                        timermovement.schedule(movTask,600);


                        break;
                    case 3:
                        negatecount++;
                        movementModule.movePan((short)9,195);
                        movTask = new NegateClass();
                        timermovement.schedule(movTask,600);
                        break;
                    case 4:
                        negatecount++;
                        movementModule.movePan((short)9,180);
                        movTask = new NegateClass();
                        timermovement.schedule(movTask,300);

                        break;
                    case 5:
                        negatecount=0;

                        break;

                }
            } catch (InternalErrorException e) {
                e.printStackTrace();
            }
        }
    }

    private class AsienteClass extends TimerTask{

        @Override
        public void run() {
            try {
                switch (negatecount) {
                    case 0:
                        Log.d(TAG, "NegateCount: "+negatecount);
                        negatecount++;

                        movementModule.moveTilt((short)9,95);
                        movTask = new AsienteClass();
                        timermovement.schedule(movTask,300);


                        break;
                    case 1:
                        Log.d(TAG, "NegateCount: "+negatecount);
                        negatecount++;
                        movementModule.moveTilt((short)9,80);
                        movTask = new AsienteClass();
                        timermovement.schedule(movTask,600);
                        break;
                    case 2:
                        Log.d(TAG, "NegateCount: "+negatecount);
                        negatecount++;

                        movementModule.moveTilt((short)9,95);
                        movTask = new AsienteClass();
                        timermovement.schedule(movTask,600);


                        break;
                    case 3:
                        Log.d(TAG, "NegateCount: "+negatecount);
                        negatecount++;
                        movementModule.moveTilt((short)9,80);
                        movTask = new AsienteClass();
                        timermovement.schedule(movTask,600);
                        break;
                    case 4:
                        Log.d(TAG, "NegateCount: "+negatecount);
                        negatecount++;
                        movementModule.moveTilt((short)9,90);
                        movTask = new AsienteClass();
                        timermovement.schedule(movTask,500);

                        break;
                    case 5:
                        Log.d(TAG, "NegateCount: "+negatecount);
                        negatecount=0;
                        continueGame();
                        break;

                }
            } catch (InternalErrorException e) {
                e.printStackTrace();
            }
        }
    }



    //endregion


}
