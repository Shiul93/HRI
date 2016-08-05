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

    /**
     * Pauses the recognition
     */
    void pauseRecognition();

    /**
     * Resumes the keyword search
     */
    void resumeRecognition();

    /**
     * Checks if the recognizer has started
     * @return True if it has started
     */
    Boolean hasStarted();

    /**
     * Switches to the default keyword search
     */
    void setKeywordSearch();

    /**
     * Switches to a grammar based search
     * @param searchName Id of the search
     * @param grammarFileName Name of the file containing the grammar, with extension,
     *                        located on assets/sync
     */
    void setGrammarSearch(String searchName, String grammarFileName);

    void suscribe(ISpeechRecognitionListener listener);
    void unsuscribe(ISpeechRecognitionListener listener);

}
