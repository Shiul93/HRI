package com.mytechia.robobo.framework.hri.speech.recognition;
import com.mytechia.robobo.framework.IModule;
/**
 * Created by luis on 5/4/16.
 */
public interface ISpeechRecognitionModule extends IModule {
    /**
     * Adds a phrase to the collection
     * @param phrase The phrase to be added
     */
    void addPhrase(String phrase);
    /**
     * Removes a phrase from the collection
     * @param phrase The phrase to be removed
     */
    void removePhrase(String phrase);
    /**
     * Updates the pocketsphinx search with the contents of the recognizable phrases collection.
     * Should be called after addPhrase() and removePhrase()
     */
    void updatePhrases();
    /**
     * Clear all the phrases in the recognizer
     */
    void cleanPhrases();
    void suscribe(ISpeechRecognitionListener listener);
    void unsuscribe(ISpeechRecognitionListener listener);

}
