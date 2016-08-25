package com.mytechia.robobo.framework.hri.sound.noteGeneration;

import com.mytechia.robobo.framework.IModule;

/**
 * Created by luis on 24/8/16.
 */
public interface INoteGeneratorModule extends IModule {
    void suscribe(INotePlayListener listener);
    void unsuscribe(INotePlayListener listener);
    void playNote(Note note,int timems);
    void addNoteToSequence(Note note,int timems);
    void playSequence();

}
