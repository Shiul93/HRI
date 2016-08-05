package com.mytechia.robobo.framework.hri.sound.noteDetection;

/**
 * Created by luis on 30/7/16.
 */
public interface INoteListener {

    void onNoteDetected(Note note);

    void onNoteEnd(Note note, long time);

    void onNewNote(Note note);
}
