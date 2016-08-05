package com.mytechia.robobo.framework.hri.sound.noteDetection.TarsosDSP;

import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;

import com.mytechia.commons.framework.exception.InternalErrorException;
import com.mytechia.robobo.framework.RoboboManager;
import com.mytechia.robobo.framework.hri.sound.noteDetection.ANoteDetectionModule;
import com.mytechia.robobo.framework.hri.sound.noteDetection.Note;
import com.mytechia.robobo.framework.hri.sound.pitchDetection.IPitchDetectionModule;
import com.mytechia.robobo.framework.hri.sound.pitchDetection.IPitchListener;

/**
 * Created by luis on 30/7/16.
 */
public class TarsosDSPNoteDetectionModule extends ANoteDetectionModule implements IPitchListener{

    private IPitchDetectionModule pitchDetectionModule;

    private String TAG = "NoteModule";

    //The thresholds for considering a pitch a well tuned note
    private double minThreshold = 0.1;
    private double maxThreshold = 0.9;

    //Start and end of the note play
    private long startTime;
    private long endTime;

    private Note lastNote;

    //region IModule methods
    @Override
    public void startup(RoboboManager manager) throws InternalErrorException {
        pitchDetectionModule = manager.getModuleInstance(IPitchDetectionModule.class);
        pitchDetectionModule.suscribe(this);
    }

    @Override
    public void shutdown() throws InternalErrorException {

    }

    @Override
    public String getModuleInfo() {
        return "TarsosDsp Notedetection Module";
    }

    @Override
    public String getModuleVersion() {
        return "v0.1";
    }


    //endregion

    //region PitchListener methods
    @Override
    public void onPitchdetected(double freq) {
        //If freq == -1 and the last note is not null, that means that note stopped playing
        if (freq==-1){

            if (lastNote!=null){
                endTime = System.currentTimeMillis();
                notifyNoteEnd(lastNote,endTime-startTime);
                //Log.d(TAG,"ENDNOTE "+lastNote.toString()+" Time elapsed:"+(endTime-startTime)+" ms");
                lastNote = null;
            }


        }else{

            double doubleNoteIndex = freqToNote(freq);
            int noteindex = (int) Math.round(doubleNoteIndex);

            //The difference to the nearest note index
            double diffValue = Math.abs(Math.abs(doubleNoteIndex)-Math.abs(Math.round(doubleNoteIndex)));

            //Log.d(TAG,"Diff: "+diffValue+" Index: "+doubleNoteIndex);
            //Filter by threshold
            if ((diffValue<minThreshold)||(diffValue>maxThreshold)) {
                //Log.d(TAG, "Index: " + doubleNoteIndex + " freq: " + freq);
                Note note = null;
                for (Note note1 : Note.values()) {
                    //Get the note from the index
                    if (note1.index == noteindex) {
                        if(note1 != lastNote){

                            if (lastNote!=null){
                                endTime = System.currentTimeMillis();
                                notifyNoteEnd(lastNote,endTime-startTime);
                                Log.d(TAG,"ENDNOTE "+lastNote.toString()+" Time elapsed:"+(endTime-startTime)+" ms");
                            }
                            Log.d(TAG,"NEWNOTE "+note1.toString());
                            notifyNewNote(note1);
                            startTime = System.currentTimeMillis();

                            lastNote = note1;
                        }
                        notifyNote(note1);
                    }
                }
            }else{
                //TODO Probar esto
                endTime = System.currentTimeMillis();
                notifyNoteEnd(lastNote,endTime-startTime);
                lastNote = null;

            }
        }
    }
    //endregion
}
