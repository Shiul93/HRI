package com.mytechia.robobo.framework.hri.sound.emotionSound;

import com.mytechia.robobo.framework.IModule;

/**
 * Created by luis on 23/8/16.
 */
public interface IEmotionSoundModule extends IModule {

    int ALERT_SOUND = 0;
    int CLAPS_SOUND = 1;
    int BOOOOO_SOUND = 2;
    int LAUGH_SOUND = 3;
    int ALARM_SOUND = 4;
    int RIMSHOT_SOUND = 5;

    /**
     * Plays a prefixed emotion sound
     * @param Sound  The sound to be played
     */
    void playSound(int Sound);
}
