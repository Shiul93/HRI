package com.mytechia.robobo.framework.hri.sound.noteDetection.TarsosDSP;

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
    private double minThreshold = 0.1;
    private double maxThreshold = 0.9;

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

        double doubleNoteIndex = freqToNote(freq);
        int noteindex = (int) Math.round(doubleNoteIndex);


        double diffValue = Math.abs(Math.abs(doubleNoteIndex)-Math.abs(Math.round(doubleNoteIndex)));
        Log.d(TAG,"Diff: "+diffValue+" Index: "+doubleNoteIndex);
        if ((diffValue<minThreshold)||(diffValue>maxThreshold)) {
            Log.d(TAG, "Index: " + doubleNoteIndex + " freq: " + freq);
            Note note = null;
            for (Note note1 : Note.values()) {
                if (note1.index == noteindex) {
                    notifyNote(note1);
                }
            }
        }
    }
    //endregion
}
