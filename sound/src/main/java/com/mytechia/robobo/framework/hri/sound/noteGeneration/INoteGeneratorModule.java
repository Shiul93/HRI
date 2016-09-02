package com.mytechia.robobo.framework.hri.sound.noteGeneration;

import com.mytechia.robobo.framework.IModule;

/**
 * Created by luis on 24/8/16.
 */
public interface INoteGeneratorModule extends IModule {
    void suscribe(INotePlayListener listener);
    void unsuscribe(INotePlayListener listener);

    /**
     * Plays a note
     * @param note the note to be played
     * @param timems the duration of the note
     */
    void playNote(Note note,int timems);

    /**
     * Add a note to the internal sequence
     * @param note the note to be added to the sequence
     * @param timems the duration of the note
     */
    void addNoteToSequence(Note note,int timems);

    /**
     * Plays the internal sequence
     */
    void playSequence();

}
