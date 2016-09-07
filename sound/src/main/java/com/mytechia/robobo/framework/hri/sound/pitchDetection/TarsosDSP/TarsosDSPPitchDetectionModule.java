package com.mytechia.robobo.framework.hri.sound.pitchDetection.TarsosDSP;

import android.content.res.AssetManager;
import android.util.Log;

import com.mytechia.commons.framework.exception.InternalErrorException;
import com.mytechia.robobo.framework.RoboboManager;
import com.mytechia.robobo.framework.hri.sound.pitchDetection.APitchDetectionModule;
import com.mytechia.robobo.framework.hri.sound.soundDispatcherModule.ISoundDispatcherModule;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

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

    private float samplerate = 44100;
    private int buffersize = 2048;
    private int overlap = 0;


    //endregion

    //region IModule methods
    @Override
    public void startup(RoboboManager manager) throws InternalErrorException {
        Properties properties = new Properties();
        AssetManager assetManager = manager.getApplicationContext().getAssets();

        try {
            InputStream inputStream = assetManager.open("sound.properties");
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        samplerate = Integer.parseInt(properties.getProperty("samplerate"));
        buffersize = Integer.parseInt(properties.getProperty("buffersize"));
        overlap = Integer.parseInt(properties.getProperty("overlap"));
        Log.d(TAG,"Properties loaded: "+samplerate+" "+buffersize+" "+overlap);

        dispatcherModule = manager.getModuleInstance(ISoundDispatcherModule.class);

        algo = PitchProcessor.PitchEstimationAlgorithm.YIN;



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
                        //Si se deja de detectar mandar un -1 a modo de cierre
                        previous = false;
                        notifyPitch(-1);
                    }
                }
            }
        };

        pitchProcessor =new PitchProcessor(algo, samplerate, buffersize,handler);

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
