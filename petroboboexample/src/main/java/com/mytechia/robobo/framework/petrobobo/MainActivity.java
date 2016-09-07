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
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.MotionEvent;

import com.mytechia.commons.framework.exception.InternalErrorException;
import com.mytechia.robobo.framework.RoboboManager;
import com.mytechia.robobo.framework.exception.ModuleNotFoundException;
import com.mytechia.robobo.framework.hri.emotion.Emotion;
import com.mytechia.robobo.framework.hri.emotion.IEmotionModule;
import com.mytechia.robobo.framework.hri.emotion.ITouchEventListener;
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
import com.mytechia.robobo.rob.IRob;
import com.mytechia.robobo.rob.IRobInterfaceModule;
import com.mytechia.robobo.rob.MoveMTMode;
import com.mytechia.robobo.rob.movement.IRobMovementModule;
import com.mytechia.robobo.rob.util.RoboboDeviceSelectionDialog;
import com.mytechia.robobo.util.Color;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends Activity implements  INotePlayListener, ITouchListener, ISpeechRecognitionListener, IFaceListener, IColorListener, ITouchEventListener {

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

    private IRob iRob;

    private static Random r = new Random();

    private List<Note> notes = new ArrayList<>();
    private LinkedList<Note> checkNotes = new LinkedList<>();


    private boolean ready = false;
    private boolean playing = false;
    private boolean playingnotes = false;
    private boolean listening = false;

    private int waterlevel = 5;
    private int foodlevel = 5;

    Timer timer = new Timer();
    TimerTask speechTask;
    Timer timermovement = new Timer();
    TimerTask movTask;

    Timer eventTimer;
    TimerTask eventTask;
    int negatecount = 0;

    private boolean firstface = true;

    private int nextEvent = 10000;


    private boolean wantspetting = true;

    private int caresscount = 0;

    private int caresslevel = 50;

    private int expectedColor;


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

        Log.d(TAG,"Expected color " + expectedColor);
        if (expectedColor == nearest_color){
            speechModule.sayText("Thank You!", ISpeechProductionModule.PRIORITY_LOW);
            if ((expectedColor == android.graphics.Color.GREEN)||(expectedColor == android.graphics.Color.RED)){
                foodlevel=5;
            }else{
                waterlevel= 5;
            }
            colorDetectionModule.pauseDetection();
        }else{
            speechModule.sayText("I did not ask for this!", ISpeechProductionModule.PRIORITY_HIGH);
        }
    }
    //endregion

    //region FaceListener
    @Override
    public void onFaceDetected(PointF faceCoords, float eyesDistance) {
        Log.d(TAG,"Eyes distance: "+eyesDistance);
        if (eyesDistance>120){
            emotionModule.setTemporalEmotion(Emotion.EMBARRASED,2000,Emotion.NORMAL);
            speechModule.sayText("You are too close!",ISpeechProductionModule.PRIORITY_HIGH);
            try {
                movementModule.moveBackwardsTime((short)20,2000);

            } catch (InternalErrorException e) {
                e.printStackTrace();
            }
        }else if (eyesDistance<20){
            emotionModule.setTemporalEmotion(Emotion.SURPRISED,2000,Emotion.NORMAL);
            speechModule.sayText("You are too far! Come here!",ISpeechProductionModule.PRIORITY_HIGH);

        }else{
            if (firstface){
                firstface = false;
                emotionModule.setTemporalEmotion(Emotion.HAPPY,5000,Emotion.NORMAL);
                speechModule.sayText("Hey there! Im robobo!", ISpeechProductionModule.PRIORITY_HIGH);

            }
        }
    }
    //endregion


    //region SpeechListener
    @Override
    public void phraseRecognized(String phrase, Long timestamp) {
        if (phrase.equals("Hi")) {

        }
        else if (phrase.equals("whats up")) {

        }
        else if (phrase.equals("here comes the food")) {
            expectedColor = android.graphics.Color.GREEN;

            colorDetectionModule.startDetection();

        }
        else if (phrase.equals("here comes the drink")) {
            expectedColor = android.graphics.Color.BLUE;
            colorDetectionModule.startDetection();

        }
        else if (phrase.equals("Hi")) {

        }else if (phrase.equals("Hi")) {

        }

    }

    @Override
    public void onModuleStart() {
        Log.d(TAG,"ONMODULESTART SPEECH)");
        recognitionModule.setGrammarSearch("voicecontrolsearch","voicecontrol.gram");
    }
    //endregion

    //region TouchListener
    @Override
    public void tap(Integer x, Integer y) {
        Log.d(TAG,"X: "+x+" Y: "+y);
        if ((x>230)&&(x<900)&&(y>425)&&(y<1025)){
            emotionModule.setTemporalEmotion(Emotion.ANGRY,2000,Emotion.NORMAL);
            speechModule.sayText("Ouch! Don't poke my eye!",ISpeechProductionModule.PRIORITY_HIGH);

        }else if ((x>250)&&(x<800)&&(y>1285)&&(y<1500)){
            emotionModule.setTemporalEmotion(Emotion.ANGRY,2000,Emotion.NORMAL);
            speechModule.sayText("Dont put your finger in my mouth!",ISpeechProductionModule.PRIORITY_HIGH);

        }else{
            emotionModule.setTemporalEmotion(Emotion.LAUGHING,2000,Emotion.NORMAL);
            speechModule.sayText("That tickles!",ISpeechProductionModule.PRIORITY_HIGH);
        }
    }

    @Override
    public void touch(Integer x, Integer y) {
        try {
            movementModule.moveTilt((short)5,90);
        } catch (InternalErrorException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void fling(TouchGestureDirection dir, double angle, long time, double distance) {
        try {


            switch (dir) {
                case UP:
                    movementModule.moveTilt((short)5,80);
                    break;
                case DOWN:
                    movementModule.moveTilt((short)5,100);
                    break;
                case LEFT:
                    iRob.moveMT(MoveMTMode.FORWARD_REVERSE,(short)50,720,(short)50,720);
                    break;
                case RIGHT:
                    iRob.moveMT(MoveMTMode.REVERSE_FORWARD,(short)50,720,(short)50,720);
                    break;
            }
        }catch (InternalErrorException e){

        }
    }

    @Override
    public void caress(TouchGestureDirection dir) {
        if (wantspetting){
            caresscount++;
            if (caresscount>=caresslevel){
                wantspetting = false;
                caresscount = 0;
                speechModule.sayText("Thanks for the petting!", ISpeechProductionModule.PRIORITY_HIGH);
                emotionModule.setTemporalEmotion(Emotion.IN_LOVE, 3000, Emotion.NORMAL);
            }
        }

    }
    //endregion



//endregion

    //region App Initialization things





    protected void startRoboboApplication() {

        roboboHelper.launchDisplayActivity(WebGLEmotionDisplayActivity.class);

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

        emotionModule.subscribeTouchListener(this);
        colorDetectionModule.suscribe(this);
        faceDetectionModule.suscribe(this);
        noteGeneratorModule.suscribe(this);
        touchModule.suscribe(this);

        recognitionModule.suscribe(this);
        colorDetectionModule.pauseDetection();
        iRob = interfaceModule.getRobInterface();

        eventTimer = new Timer();
        eventTimer.schedule(new EventTask(),nextEvent);


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

    void playNoteSequence(int numberofnotes){
        int i;
        emotionModule.setCurrentEmotion(Emotion.SMYLING);
        for (i=0;i<numberofnotes;i++){
            noteGeneratorModule.addNoteToSequence(generateRandomNote(),200);
        }
        playingnotes = true;
        noteGeneratorModule.playSequence();
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

    @Override
    public void onScreenTouchEvent(MotionEvent event) {
        touchModule.feedTouchEvent(event);
    }


    private class WaitForSpeech extends TimerTask {



        @Override
        public void run() {
            playNoteSequence(5);
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

                        break;

                }
            } catch (InternalErrorException e) {
                e.printStackTrace();
            }
        }
    }

    private class EventTask extends TimerTask{

        @Override
        public void run() {
            int option = generateRandom(0,6);
            Log.d(TAG,"Event: "+option);

            switch (option){
                case 0:
                    emotionModule.setTemporalEmotion(Emotion.SMYLING,2500,Emotion.NORMAL);
                    playNoteSequence(10);
                    break;
                case 1:
                    emotionModule.setTemporalEmotion(Emotion.HAPPY,2500,Emotion.NORMAL);
                    break;
                case 2:
                    try {
                        iRob.moveMT(MoveMTMode.REVERSE_FORWARD,(short)50,720,(short)50,720);
                    } catch (InternalErrorException e) {
                        e.printStackTrace();
                    }
                    break;
                case 3:
                    try {
                        iRob.moveMT(MoveMTMode.FORWARD_REVERSE,(short)50,720,(short)50,720);
                    } catch (InternalErrorException e) {
                        e.printStackTrace();
                    }
                    break;
                case 4:
                    if (foodlevel>0) {
                        foodlevel = foodlevel - 1;
                    }
                    break;
                case 5:
                    if (waterlevel>0) {
                        waterlevel = waterlevel -1;
                    }
                    break;
                case 6:
                    speechModule.sayText("Pet me!",ISpeechProductionModule.PRIORITY_HIGH);
                    wantspetting = true;

                    break;
                case 7:
                    break;
                case 8:
                    break;
                case 9:
                    break;
                case 10:
                    break;
            }


            checkLevels();
            eventTimer = new Timer();
            eventTask = new EventTask();
            eventTimer.schedule(eventTask,generateRandom(30000,50000));
        }
    }


    private void checkLevels(){
        if (waterlevel<2){
            speechModule.sayText("Im thirsty",ISpeechProductionModule.PRIORITY_LOW);
        }
        if (foodlevel<2){
            speechModule.sayText("Im hungry",ISpeechProductionModule.PRIORITY_LOW);
        }
    }

    //endregion


}
