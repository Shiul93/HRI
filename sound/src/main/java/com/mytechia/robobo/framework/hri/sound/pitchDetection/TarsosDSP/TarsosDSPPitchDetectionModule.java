package com.mytechia.robobo.framework.hri.sound.pitchDetection.TarsosDSP;

import android.util.Log;

import com.mytechia.commons.framework.exception.InternalErrorException;
import com.mytechia.robobo.framework.RoboboManager;
import com.mytechia.robobo.framework.hri.sound.pitchDetection.APitchDetectionModule;
import com.mytechia.robobo.framework.hri.sound.soundDispatcherModule.ISoundDispatcherModule;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;

/**
 * Created by luis on 26/7/16.
 */
public class TarsosDSPPitchDetectionModule extends APitchDetectionModule{

    //region VAR

    private PitchProcessor.PitchEstimationAlgorithm algo;
    private PitchProcessor pitchProcessor;
    private ISoundDispatcherModule dispatcherModule;
    private String TAG = "PitchDetectionModule";
    private boolean previous = false;
    //endregion

    //region IModule methods
    @Override
    public void startup(RoboboManager manager) throws InternalErrorException {
        dispatcherModule = manager.getModuleInstance(ISoundDispatcherModule.class);

        algo = PitchProcessor.PitchEstimationAlgorithm.YIN;

        float sampleRate = 44100;
        int bufferSize = 2048;
        int overlap = 0;

        PitchDetectionHandler handler = new PitchDetectionHandler() {
            @Override
            public void handlePitch(PitchDetectionResult pitchDetectionResult,
                                    AudioEvent audioEvent) {
                double pitch = pitchDetectionResult.getPitch();
                //Log.d(TAG,(audioEvent.getTimeStamp() + " " +pitch));

                if (pitch>0){
                    previous = true;
                    notifyPitch(pitch*2);
                }else {
                    if (previous){
                        previous = false;
                        notifyPitch(-1);
                    }
                }
            }
        };

        pitchProcessor =new PitchProcessor(algo, sampleRate, bufferSize,handler);

        dispatcherModule.addProcessor(pitchProcessor);


    }

    @Override
    public void shutdown() throws InternalErrorException {
        dispatcherModule.removeProcessor(pitchProcessor);
        pitchProcessor.processingFinished();

    }

    @Override
    public String getModuleInfo() {
        return "Pitch detection module";
    }

    @Override
    public String getModuleVersion() {
        return "v0.1";
    }
    //endRegion
}