package noracee.ankidictionary.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * Contains a parsed dictionary query
 */

public class Vocabulary {
    // Index of respective fields in the Anki note
    private static final int INDEX_KANJI   = 0;
    private static final int INDEX_READING = 1;
    private static final int INDEX_DEFS    = 2;
    private static final int INDEX_POS     = 3;
    private static final int INDEX_EXAMPLE = 4;

    // Number of fields in the Anki note
    private static final int SIZE_FIELDS   = 5;

    // Delimiter to separate entities
    private static final String DELIMITER     = "::";
    private static final String DELIMITER_TAG = "　";

    private int defCount;
    private int posCount;

    private String category;
    private String query;
    private String raw;

    private String[] fields;

    private HashSet<String> tags;
    private List<Example> examples;

    public Vocabulary(String query, String raw) {
        this.query = query;
        this.raw = raw;

        defCount = 0;
        posCount = 0;

        examples = null;
        fields = new String[SIZE_FIELDS];
        tags = new HashSet<>();
    }

    /**
     * Retrieves all the fields of the Anki note as a String array
     * @return String array of all the fields
     */
    public String[] getFields() {
        for (int i = 0; i < fields.length; i++) {
            if (fields[i] == null)
                fields[i] = "";
        }

        return fields;
    }

    /**
     * Retrieves the category to which this vocabulary belongs to
     * @return category
     */
    public String getCategory() {
        return category;
    }

    /**
     * Sets the category to which this vocabulary belongs to
     * @param category category
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Retrieves the defintions in this vocabulary
     * @return definitions in this vocabulary
     */
    public String getDefinitions() {
        return fields[INDEX_DEFS];
    }

    /**
     * Adds a definition set to this vocabulary
     * @param definition definition set
     */
    public void addDefinition(String definition) {
        if (definition == null || definition.isEmpty())
            return;

        if (fields[INDEX_DEFS] == null)
            setDefinitions(definition);
        else
            fields[INDEX_DEFS] += DELIMITER + definition;

        defCount++;
    }

    /**
     * Sets the entire definition set of this vocabulary
     * @param defs entire definition set
     */
    public void setDefinitions(String defs) {
        fields[INDEX_DEFS] = defs;
    }

    /**
     * Retrieves the example set for this vocabulary
     * @return example
     */
    public String getExample() {
        return fields[INDEX_EXAMPLE];
    }

    /**
     * Retrieves the list of {@link Example Example} associated with this vocabulary
     * @return list of {@link Example Example}
     */
    public List<Example> getExamples() {
        return examples;
    }

    /**
     * Adds an {@link Example Example} to the list
     * @param example {@link Example Example} to add
     */
    public void addExample(Example example) {
        if (examples == null)
            examples = new ArrayList<>();examples.add(example);
    }

    /**
     * Sets the example that will be used in the note for Anki
     * @param index index of the {@link Example Example} in the list
     */
    public void setExample(int index) {
        fields[INDEX_EXAMPLE] = examples.get(index).toString();
    }

    /**
     * Sets the example that will be used in the note for Anki
     * @param example example as a String
     */
    public void setExample(String example) {
        fields[INDEX_EXAMPLE] = example;
    }

    /**
     * Retrieves the kanji writing of this vocabulary
     * @return kanji writing
     */
    public String getKanji() {
        return fields[INDEX_KANJI];
    }

    /**
     * Adds a kanji writing to this vocabulary
     * @param kanji kanji writing
     */
    public void addKanji(String kanji) {
        if (fields[INDEX_KANJI] == null)
            setKanji(kanji);
        else
            fields[INDEX_KANJI] += DELIMITER + kanji;
    }

    /**
     * Sets the kanji writing of this vocabulary
     * @param kanji kanji writing
     */
    public void setKanji(String kanji) {
        fields[INDEX_KANJI] = kanji;
    }

    /**
     * Retrieves the parts of speech in this vocabulary
     * @return parts of speech
     */
    public String getPartsOfSpeech() {
        if (fields[INDEX_POS] == null)
            return "";
        return fields[INDEX_POS];
    }

    /**
     * Adds a part of speech set to this vocabulary
     * @param pos part of speech set
     */
    public void addPartsOfSpeech(String pos) {
        if (pos == null|| pos.isEmpty())
            return;

        if (fields[INDEX_POS] == null) {
            setPartsOfSpeech(pos);
            posCount++;
        } else {
            /*
            In cases where there are more definition sets than part of speech sets, add the
            appropriate number of delimiters
             */
            StringBuilder sb = new StringBuilder(fields[INDEX_POS]);
            while (posCount < defCount) {
                 sb.append(DELIMITER);
                 posCount++;
            }

            sb.append(pos);
            fields[INDEX_POS] = sb.toString();
        }
    }

    /**
     * Sets the entire part of speech set of this vocabulary
     * @param pos entire part of speech set
     */
    public void setPartsOfSpeech(String pos) {
        fields[INDEX_POS] = pos;
    }

    /**
     * Retrieves the query used to search for this vocabulary
     * @return query
     */
    public String getQuery() { return query; }

    /**
     * Retrieves the raw response received from the servers
     * @return raw response
     */
    public String getRaw() {
        return raw;
    }

    /**
     * Retrieves the hiragana or katakana reading of this vocabulary
     * @return hiragana or katakana reading
     */
    public String getReading() {
        if (fields[INDEX_READING] == null)
            return fields[INDEX_KANJI];
        return fields[INDEX_READING];
    }

    /**
     * Adds a reading to this vocabulary
     * @param reading reading
     */
    public void addReading(String reading) {
        if (fields[INDEX_READING] == null)
            setReading(reading);
        else
            fields[INDEX_READING] += DELIMITER + reading;
    }

    /**
     * Sets the hiragana or katakana reading of this vocabulary
     * @param reading hiragana or katakana reading
     */
    public void setReading(String reading) {
        fields[INDEX_READING] = reading;
    }

    /**
     * Retrieves the complete tags set for this vocabulary including the category
     * @return complete tags set
     */
    public HashSet<String> getTags() {
        tags.add(category);
        return tags;
    }

    /**
     * Retrieves the tags for this vocabulary as a String
     * @return tags as a String
     */
    public String getTagsString() {
        StringBuilder sb = new StringBuilder();

        // checks if tag is the first in the set, otherwise append delimiter
        boolean first = true;
        for (String tag : tags) {
            if (first) {
                sb.append(tag);
                first = false;
            } else {
                sb.append(DELIMITER_TAG).append(tag);
            }
        }
        return sb.toString().trim();
    }

    /**
     * Adds a tag to this vocabulary
     * @param tag tag
     */
    public void addTag(String tag) {
        if (tag == null || tag.isEmpty())
            return;

        tags.add(tag);
    }

    /**
     * Sets the entire tag set for this vocabulary
     * @param tagsRaw entire tag set as a String
     */
    public void setTags(String tagsRaw) {
        String[] tagsArray = tagsRaw.split(DELIMITER_TAG);

        tags.clear();
        Collections.addAll(tags, tagsArray);
    }
}
