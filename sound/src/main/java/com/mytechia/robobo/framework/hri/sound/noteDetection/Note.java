package com.mytechia.robobo.framework.hri.sound.noteDetection;

/**
 * Created by luis on 26/7/16.
 */



public enum Note {
    C3(3,-21),
    Cs3(3,-20),
    D3(3,-19),
    Ds3(3,-18),
    E3(3,-17),
    F3(3,-16),
    Fs3(3,-15),
    G3(3,-14),
    Gs3(3,-13),
    A3(3,-12),
    As3(3,-11),
    B3(3,-10),
    C4(4,-9),
    Cs4(4,-8),
    D4(4,-7),
    Ds4(4,-6),
    E4(4,-5),
    F4(4,-4),
    Fs4(4,-3),
    G4(4,-2),
    Gs4(4,-1),
    A4(4,0),
    As4(4,1),
    B4(4,2),
    C5(5,3),
    Cs5(5,4),
    D5(5,5),
    Ds5(5,6),
    E5(5,7),
    F5(5,8),
    Fs5(5,9),
    G5(5,10),
    Gs5(5,11),
    A5(5,12),
    As5(5,13),
    B5(5,14),
    C6(6,15),
    Cs6(6,16),
    D6(6,17),
    Ds6(6,18),
    E6(6,19),
    F6(6,20),
    Fs6(6,21),
    G6(6,22),
    Gs6(6,23),
    A6(6,24),
    As6(4,25),
    B6(4,26);


public int index;
public int octave;

Note(int octave,int index){
    this.octave = octave;
    this.index = index;
}
}


