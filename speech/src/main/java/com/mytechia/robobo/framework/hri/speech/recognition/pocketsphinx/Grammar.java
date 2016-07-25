package com.mytechia.robobo.framework.hri.speech.recognition.pocketsphinx;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.StringTokenizer;

/**
 * Created by luis on 22/7/16.
 */
public class Grammar {
private Dictionary<Token,Token> dictionary = new Hashtable<>();

    public class Token{
        private boolean pub;
        private String key;
        private boolean tok;
        private String elements;

        public Token(String key,String elements, boolean pub){
            this.pub = pub;
            this.key = key;
            this.elements = elements;

        }


    }
}
