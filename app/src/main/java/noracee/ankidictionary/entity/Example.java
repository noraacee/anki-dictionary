package noracee.ankidictionary.entity;

/**
 * An example sentence in both Japanese and English
 */

public class Example {
    private static final String SEPARATOR = "::";

    private String english;
    private String japanese;

    public Example(String japanese, String english) {
        this.japanese = japanese;
        this.english = english;
    }

    @Override
    public String toString() {
        return japanese + SEPARATOR + english;
    }

    /**
     * Retrieves the Japanese translation for this example
     * @return Japanese translation
     */
    public String getJapanese() { return japanese; }

    /**
     * Retrieves the English translation for this example
     * @return English translation
     */
    public String getEnglish() { return english; }
}
