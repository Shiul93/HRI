package com.mytechia.robobo.framework.simonexamplerobobo;

/**
 * Created by luis on 24/8/16.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

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
import com.mytechia.robobo.framework.service.RoboboServiceHelper;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends Activity implements INoteListener, INotePlayListener, IClapListener {

    private RoboboServiceHelper roboboHelper;
    private RoboboManager robobo;

    private String TAG = "MUSICEXAMPLE";

    private IEmotionModule emotionModule;
    private ISpeechProductionModule speechModule;
    private INoteDetectionModule noteDetectionModule;
    private INoteGeneratorModule noteGeneratorModule;
    private IEmotionSoundModule emotionSoundModule;
    private ISoundDispatcherModule soundDispatcherModule;
    private IClapDetectionModule clapDetectionModule;


    private static Random r = new Random();

    private List<Note> notes = new ArrayList<>();
    private LinkedList<Note> checkNotes = new LinkedList<>();


    private boolean ready = false;
    private boolean playing = false;
    private boolean playingnotes = false;
    private boolean listening = false;

    Timer timer = new Timer();
    TimerTask speechTask;

    private boolean seeingLight = false;


    //region Listeners

    //region ActivityListeners
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        roboboHelper = new RoboboServiceHelper(this, new RoboboApplication());
        roboboHelper.bindRoboboService(new Bundle());

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        roboboHelper.unbindRoboboService();
        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG,"TOUCH");
        speechModule.sayText("Comenzando juego",0);
        return super.onTouchEvent(event);
    }

    //endregion

    //region Notelisteners


    @Override
    public void onNoteDetected(com.mytechia.robobo.framework.hri.sound.noteDetection.Note note) {

    }

    @Override
    public void onNoteEnd(com.mytechia.robobo.framework.hri.sound.noteDetection.Note note, long time) {

        Log.d(TAG, "NOTEEND!!!!!!! Playing: "+playing+" playingnotes "+playingnotes+" listening "+listening);

        if ((time>200)&&(playing)&&(!playingnotes)&&(listening)) {
            try {
                Log.d(TAG,"CheckResult");
                boolean checkresult = checkNote(convertNote(note));
                if (!checkresult){
                    Log.d(TAG,"GameOver");
                    gameOver();
                }
            }catch (NoSuchElementException e){
                Log.d(TAG,"EndList");
                listening = false;
                continueGame();
            }


        }

    }

    @Override
    public void onNewNote(com.mytechia.robobo.framework.hri.sound.noteDetection.Note note) {

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

    //region ClapListener


    @Override
    public void onClap(double time) {
        if ((ready)&&(!playing)){
            Log.d(TAG,"PLAYING");
            playing = true;
            startGame();
        }
        if ((!ready)&&(!playing)){
            Log.d(TAG,"READY");
            ready = true;
            speechModule.sayText("Ready",0);
        }


    }

    //endregion



    //endregion

    //region Noteconverters
    private com.mytechia.robobo.framework.hri.sound.noteDetection.Note convertNote(Note note){
        com.mytechia.robobo.framework.hri.sound.noteDetection.Note returnvalue = null;
        for (com.mytechia.robobo.framework.hri.sound.noteDetection.Note n :com.mytechia.robobo.framework.hri.sound.noteDetection.Note.values()){
            if (n.index==note.index){
                returnvalue = n;
            }
        }
        return returnvalue;
    }

    private  Note convertNote(com.mytechia.robobo.framework.hri.sound.noteDetection.Note note){
        Note returnvalue = null;
        for (Note n :Note.values()){
            if (n.index==note.index){
                returnvalue = n;
            }
        }
        return returnvalue;
    }
//endregion

    //region App Initialization things


    private class RoboboApplication implements RoboboServiceHelper.Listener {

        @Override
        public void onRoboboManagerStarted(RoboboManager roboboManager) {
            robobo = roboboManager;
            startApplication();
        }

        @Override
        public void onError(String s) {
            Log.e("ROBOBO-APP", s);
        }
    }


    protected void startApplication() {

        roboboHelper.launchDisplayActivity(WebGLEmotionDisplayActivity.class);





        try {

            soundDispatcherModule=
                    robobo.getModuleInstance(ISoundDispatcherModule.class);
            emotionModule =
                    robobo.getModuleInstance(IEmotionModule.class);
            speechModule =
                    robobo.getModuleInstance(ISpeechProductionModule.class);
            noteDetectionModule=
                    robobo.getModuleInstance(INoteDetectionModule.class);
            noteGeneratorModule=
                    robobo.getModuleInstance(INoteGeneratorModule.class);
            emotionSoundModule=
                    robobo.getModuleInstance(IEmotionSoundModule.class);
            clapDetectionModule=
                    robobo.getModuleInstance(IClapDetectionModule.class);




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

        clapDetectionModule.suscribe(this);
        noteGeneratorModule.suscribe(this);
        noteDetectionModule.suscribe(this);
        soundDispatcherModule.runDispatcher();





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


}
