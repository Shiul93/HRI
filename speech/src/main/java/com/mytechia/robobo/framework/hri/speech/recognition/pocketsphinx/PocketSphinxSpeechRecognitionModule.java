package com.mytechia.robobo.framework.hri.speech.recognition.pocketsphinx;
import android.content.Context;
import android.util.Log;

import com.mytechia.commons.framework.exception.InternalErrorException;
import com.mytechia.robobo.framework.RoboboManager;
import com.mytechia.robobo.framework.hri.speech.recognition.ASpeechRecognitionModule;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.AbstractCollection;
import java.util.HashSet;





import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;

import static edu.cmu.pocketsphinx.SpeechRecognizerSetup.defaultSetup;


/**
 * Created by luis on 5/4/16.
 */
public class PocketSphinxSpeechRecognitionModule extends ASpeechRecognitionModule implements RecognitionListener {

    private SpeechRecognizer recognizer;
    private static final String PHRASEFILENAME = "phrases.txt";

    private static final String KEYWORDSEARCH = "KWSEARCH";
    private AbstractCollection<String> recognizablePhrases;
    private static final Integer HASHSETSIZE = 128;
    private File phraseFile;

    public  PocketSphinxSpeechRecognitionModule(){
        super();
    }

    @Override
    /**
     * Adds a phrase to the collection
     * @param phrase The phrase to be added
     */
    public void addPhrase(String phrase) {


        recognizablePhrases.add(phrase);

    }

    @Override
    /**
     * Removes a phrase from the collection
     * @param phrase The phrase to be removed
     */
    public void removePhrase(String phrase) {

        if(!recognizablePhrases.remove(phrase)){
            Log.e("PS_SpeechRecognition","Phrase "+phrase+" not found in the recognizable set");
        }

    }

    /**
     * Updates the pocketsphinx search with the contents of the recognizable phrases collection.
     * Should be called after addPhrase() and removePhrase()
     */
    public void updatePhrases(){
        PrintWriter writer = null;
        recognizer.stop();

        try {
            //Deletes the old file
            writer = new PrintWriter(phraseFile);
            writer.print("");
            writer.close();
            writer = new PrintWriter(phraseFile);
            //Iterates over all the current phrases and adds them to the file
            for (String phrase:recognizablePhrases){
                writer.append(phrase+"\n");
            }
            //Set the keyword search with the new file
            recognizer.addKeywordSearch(KEYWORDSEARCH,phraseFile);
        } catch (FileNotFoundException e) {
            Log.e("PS_SpeechRecognition","Phrase file not initialized");
            e.printStackTrace();

        }
        recognizer.startListening(KEYWORDSEARCH);



    }

    @Override
    /**
     * Clear all the phrases in the recognizer
     */
    public void cleanPhrases() {
        //Clear the collection
        recognizablePhrases.clear();
        //Update the recognizer
        updatePhrases();

    }

    @Override
    public void startup(RoboboManager roboboManager) throws InternalErrorException {

        //Create a new hashset for phrases
        recognizablePhrases = new HashSet<String>(HASHSETSIZE);
        //Get current directory for the app
        File appRootDir = roboboManager.getApplicationContext().getFilesDir();
        //Create a new text file for storing the phrases
        phraseFile = new File(appRootDir,PHRASEFILENAME);
        //Update search and start listening
        updatePhrases();

    }

    public void startupTest(Context context) throws InternalErrorException {

        //Create a new hashset for phrases
        //TODO Mirar lo de los assets

        recognizablePhrases = new HashSet<String>(HASHSETSIZE);
        //Assets assets = new Assets(context.this);
        File assetDir = null;
        try {
            //assetDir = assets.syncAssets();
            setupRecognizer(assetDir);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Get current directory for the app
        File appRootDir = context.getFilesDir();
        //Create a new text file for storing the phrases
        phraseFile = new File(appRootDir,PHRASEFILENAME);
        //Update search and start listening
        updatePhrases();

    }

    @Override
    public void shutdown() throws InternalErrorException {
        //Cancel the listening
        recognizer.cancel();
        //Shutdown the recognizer
        recognizer.shutdown();
        //Delete the phrase file
        phraseFile.delete();

    }

    @Override
    public String getModuleInfo() {
        return null;
    }

    @Override
    public String getModuleVersion() {
        return null;
    }


    private void setupRecognizer(File assetsDir) throws IOException {
        // The recognizer can be configured to perform multiple searches
        // of different kind and switch between them



        recognizer = defaultSetup()
                .setAcousticModel(new File(assetsDir, "en-us-ptm"))
                .setDictionary(new File(assetsDir, "cmudict-en-us.dict"))

                        // To disable logging of raw audio comment out this call (takes a lot of space on the device)
                .setRawLogDir(assetsDir)

                        // Threshold to tune for keyphrase to balance between false alarms and misses
                .setKeywordThreshold(1e-45f)

                        // Use context-independent phonetic search, context-dependent is too slow for mobile
                .setBoolean("-allphone_ci", true)

                .getRecognizer();
        recognizer.addListener(this);





    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onEndOfSpeech() {

    }

    @Override
    public void onPartialResult(Hypothesis hypothesis) {

    }

    @Override
    public void onResult(Hypothesis hypothesis) {
        if (hypothesis != null) {
            String text = hypothesis.getHypstr();
            long time = System.currentTimeMillis();
            notifyPhrase(text,time);
        }


    }

    @Override
    public void onError(Exception e) {

    }

    @Override
    public void onTimeout() {

    }
}