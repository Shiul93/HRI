package com.mytechia.robobo.framework.hri.speech.recognition.pocketsphinx;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Debug;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;

import com.mytechia.commons.framework.exception.InternalErrorException;
import com.mytechia.robobo.framework.RoboboManager;
import com.mytechia.robobo.framework.hri.speech.R;
import com.mytechia.robobo.framework.hri.speech.recognition.ASpeechRecognitionModule;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.HashSet;


import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;

import static edu.cmu.pocketsphinx.SpeechRecognizerSetup.defaultSetup;


/**
 * Created by luis on 5/4/16.
 */
public class PocketSphinxSpeechRecognitionModule extends ASpeechRecognitionModule implements RecognitionListener {

    //region VAR
    private SpeechRecognizer recognizer;
    private static final String PHRASEFILENAME = "phrases.gram";
    private String TAG = "SpeechRecognitionModule";
    private static final String KEYWORDSEARCH = "KWSEARCH";

    private String threshold = " /1e-1/\n";

    private static final String MOV_SEARCH = "MOVSEARCH";

    private AbstractCollection<String> recognizablePhrases;
    private static final Integer HASHSETSIZE = 128;
    private File phraseFile;
    private File assetsDir;

    private String hyp = "";

    private HashSet<String> searches = new HashSet<String>();

    private Context context;

    private String currentSearch;

    private Boolean hasStarted = false;
    private Boolean paused = false;


    //endregion

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

    @Override
    public void pauseRecognition(){
        paused = true;
        recognizer.stop();
    }


    @Override
    public void resumeRecognition(){
        paused = false;
        recognizer.startListening(currentSearch);//, timeout);
    }


    @Override
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
            writer.print("");
            //Iterates over all the current phrases and adds them to the file
            for (String phrase:recognizablePhrases){
                Log.d(TAG,"Adding phrase: "+phrase);
                writer.append(phrase+threshold);

            }
            //Set the keyword search with the new file
            writer.close();
            recognizer.addKeywordSearch(KEYWORDSEARCH,phraseFile);
        } catch (FileNotFoundException e) {
            Log.e("PS_SpeechRecognition", "Phrase file not initialized");
            e.printStackTrace();
        }
        try {
            Log.d(TAG,phraseFile.list()[1]);
        }catch (NullPointerException npe){
            Log.d(TAG, "null array");
        }

        currentSearch = KEYWORDSEARCH;
        recognizer.startListening(KEYWORDSEARCH);//,timeout);



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
    public void startup(final RoboboManager roboboManager) throws InternalErrorException {
        Log.d(TAG,"Startup Recognition Module");
        //Create a new hashset for phrases
        recognizablePhrases = new HashSet<String>(HASHSETSIZE);
        searches.add(KEYWORDSEARCH);
        currentSearch = KEYWORDSEARCH;

        context = roboboManager.getApplicationContext();
        //Get current directory for the app
        File appRootDir = roboboManager.getApplicationContext().getFilesDir();
        //Create a new text file for storing the phrases
        phraseFile = new File(Environment.getExternalStorageDirectory(),PHRASEFILENAME);




        // Recognizer initialization is a time-consuming and it involves IO,
        // so we execute it in async task

        new AsyncTask<Void, Void, Exception>() {
            @Override
            protected Exception doInBackground(Void... params) {
                try {
                    Log.d(TAG, "AT/----------");
                    Log.d(TAG, "AT/Start AsyncTask");
                    Assets assets = new Assets(roboboManager.getApplicationContext());

                    Log.d(TAG,"AT/ "+assets.toString());
                    assetsDir = assets.syncAssets();
                    setupRecognizer(assetsDir);


                } catch (IOException e) {
                    return e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Exception result) {
                if (result != null) {
                    //throw new InternalErrorException("Could not start recognizer");
                    Log.d(TAG,"AT/Could not start recognizer");
                    Log.d(TAG,"AT/Exception: "+result.toString());
                    Log.d(TAG,"AT/End");
                } else {

                    Log.d(TAG,"AT/Starting keyword listener");
                    //Update search and start listening
                    //updatePhrases();
                    Log.d(TAG,"AT/End");
                    Log.d(TAG, "AT/----------");
                    hasStarted = true;
                    notifyStartup();
                }
            }
        }.execute();


    }



    @Override
    public void shutdown() throws InternalErrorException {
        if (recognizer != null){
        //Cancel the listening
        recognizer.cancel();
        //Shutdown the recognizer
        recognizer.shutdown();
        //Delete the phrase file
        phraseFile.delete();
        }

    }

    @Override
    public String getModuleInfo() {
        return "PocketSphinx Voice recognition module";
    }

    @Override
    public String getModuleVersion() {
        return "v0.1";
    }

    @Override
    public Boolean hasStarted(){
        return hasStarted;
    }


    private void setupRecognizer(File assetsDir) throws IOException {
        Log.d(TAG, "Setting up recognizer");
        // The recognizer can be configured to perform multiple searches
        // of different kind and switch between them



        recognizer = defaultSetup()
                .setAcousticModel(new File(assetsDir, "en-us-ptm"))
                .setDictionary(new File(assetsDir, "cmudict-en-us.dict"))

                        // To disable logging of raw audio comment out this call (takes a lot of space on the device)
                .setRawLogDir(assetsDir)
                .setFloat("-vad_threshold", 3.0)
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
        recognizer.stop();
    }

    @Override
    public void onPartialResult(Hypothesis hypothesis) {

        String text = "null";
        if (hypothesis != null){text = hypothesis.getHypstr();}
        if (hypothesis == null || text.equals("null"))
            return;

        hyp = text;
        Log.d(TAG,"Recognized part "+text);


    }

    @Override
    public void onResult(Hypothesis hypothesis) {
        if ((hypothesis != null)&&(hyp.equals(hypothesis.getHypstr()))) {

            String text = hypothesis.getHypstr();
            //TODO Filtrar por probabilidad

            Log.d(TAG,"Recognized "+text+" Prob: "+hypothesis.getBestScore());
            long time = System.currentTimeMillis();
            notifyPhrase(text,time);
        }
        else{Log.d(TAG,"Recognized nothing");}

        if (! paused){
            recognizer.startListening(currentSearch);//, timeout);
        }

    }


    @Override
    public void onError(Exception e) {

    }

    @Override
    public void onTimeout() {

    }

    //TODO Permitir varias busquedas diferentes?
    public void setGrammarSearch(String searchName, String grammarFileName){

//        if (!searches.contains(searchName)) {
            searches.add(searchName);
            File languageModel = new File(assetsDir, grammarFileName);
            recognizer.stop();
            recognizer.addGrammarSearch(searchName, languageModel);
            recognizer.startListening(searchName);
            currentSearch = searchName;
//        }else{
//            recognizer.stop();
//            recognizer.startListening(searchName);
//            currentSearch = searchName;
//        }

    }

    public void setKeywordSearch(){
        recognizer.stop();
        currentSearch = KEYWORDSEARCH;
        recognizer.startListening(KEYWORDSEARCH);
    }
}