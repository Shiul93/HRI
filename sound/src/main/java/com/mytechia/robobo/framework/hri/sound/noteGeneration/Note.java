package com.mytechia.robobo.framework.hri.sound.noteGeneration;

/**
 * Created by luis on 26/7/16.
 */



public enum Note {
    C3(3,-21,"C"),
    Cs3(3,-20,"Cs"),
    D3(3,-19,"D"),
    Ds3(3,-18,"Ds"),
    E3(3,-17,"E"),
    F3(3,-16,"F"),
    Fs3(3,-15,"Fs"),
    G3(3,-14,"G"),
    Gs3(3,-13,"Gs"),
    A3(3,-12,"A"),
    As3(3,-11,"As"),
    B3(3,-10,"B"),
    C4(4,-9,"C"),
    Cs4(4,-8,"Cs"),
    D4(4,-7,"D"),
    Ds4(4,-6,"Ds"),
    E4(4,-5,"E"),
    F4(4,-4,"F"),
    Fs4(4,-3,"Fs"),
    G4(4,-2,"G"),
    Gs4(4,-1,"Gs"),
    A4(4,0,"A"),
    As4(4,1,"As"),
    B4(4,2,"B"),
    C5(5,3,"C"),
    Cs5(5,4,"Cs"),
    D5(5,5,"D"),
    Ds5(5,6,"Ds"),
    E5(5,7,"E"),
    F5(5,8,"F"),
    Fs5(5,9,"Fs"),
    G5(5,10,"G"),
    Gs5(5,11,"Gs"),
    A5(5,12,"A"),
    As5(5,13,"As"),
    B5(5,14,"B"),
    C6(6,15,"C"),
    Cs6(6,16,"Cs"),
    D6(6,17,"D"),
    Ds6(6,18,"Ds"),
    E6(6,19,"E"),
    F6(6,20,"F"),
    Fs6(6,21,"Fs"),
    G6(6,22,"G"),
    Gs6(6,23,"Gs"),
    A6(6,24,"A"),
    As6(6,25,"As"),
    B6(6,26,"B");


public int index;
public int octave;
    public String note;

Note(int octave, int index, String note){
    this.octave = octave;
    this.index = index;
    this.note = note;
}
}


