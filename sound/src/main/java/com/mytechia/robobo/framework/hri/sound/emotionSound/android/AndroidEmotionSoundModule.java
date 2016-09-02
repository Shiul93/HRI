package com.mytechia.robobo.framework.hri.sound.emotionSound.android;

import android.content.Context;
import android.media.MediaPlayer;

import com.mytechia.commons.framework.exception.InternalErrorException;
import com.mytechia.robobo.framework.RoboboManager;
import com.mytechia.robobo.framework.hri.sound.R;
import com.mytechia.robobo.framework.hri.sound.emotionSound.IEmotionSoundModule;
import com.mytechia.robobo.framework.hri.sound.soundDispatcherModule.ISoundDispatcherModule;

import java.util.TimerTask;

/**
 * Created by luis on 23/8/16.
 */
public class AndroidEmotionSoundModule implements IEmotionSoundModule {

    private MediaPlayer mediaPlayer;
    private Context context;

    @Override
    public void playSound(int sound) {
        switch (sound){
            case ALERT_SOUND:

                mediaPlayer = MediaPlayer.create(context, R.raw.alerta);
                mediaPlayer.start();
                break;
            case CLAPS_SOUND:

                mediaPlayer = MediaPlayer.create(context, R.raw.clapping);
                mediaPlayer.start();
                break;
            case BOOOOO_SOUND:

                mediaPlayer = MediaPlayer.create(context, R.raw.boo);
                mediaPlayer.start();
                break;
            case LAUGH_SOUND:

                mediaPlayer = MediaPlayer.create(context, R.raw.laugh);
                mediaPlayer.start();
                break;
            case ALARM_SOUND:

                mediaPlayer = MediaPlayer.create(context, R.raw.alarm);
                mediaPlayer.start();
                break;
            case RIMSHOT_SOUND:

                mediaPlayer = MediaPlayer.create(context, R.raw.rimshot);
                mediaPlayer.start();
                break;
        }

    }

    @Override
    public void startup(RoboboManager manager) throws InternalErrorException {

        context = manager.getApplicationContext();

    }

    @Override
    public void shutdown() throws InternalErrorException {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }

    }

    @Override
    public String getModuleInfo() {
        return "Emotion Sound Module";
    }

    @Override
    public String getModuleVersion() {
        return "v0.1";
    }



}
