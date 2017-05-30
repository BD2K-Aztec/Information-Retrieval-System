package edu.ucla.cs.scai.aztec.keyphrase;

import java.io.*;
import java.util.*;
import java.lang.*;

/**
 * A filter to get all high quality phrases
 * Input: possible phrases and frequency, word frequency.
 * Output: high quality phrases.
 *
 * @author Xinxin Huang "xinxinh@gmail.com"
 */
public class PhraseFilter {
    private Map<String, Integer> wordCount = new HashMap<String, Integer>();
    private Map<String, Integer> phraseCount = new HashMap<String, Integer>();
    private Map<String, Integer> trigramCount = new HashMap<String, Integer>();
    private Map<String, Double> phraseProb = new HashMap<String, Double>();
    private LinkedList<String> trigramSet = new LinkedList<>();
    private Integer wordthreshold  = 4;
    private Double t_threshold = 0.0;
    private final static HashSet<String> stopwords = new HashSet<>();
    static {
        String s = "a\n"
                + "about\n"
                + "above\n"
                + "after\n"
                + "again\n"
                + "against\n"
                + "all\n"
                + "also\n"
                + "am\n"
                + "an\n"
                + "and\n"
                + "any\n"
                + "are\n"
                + "aren't\n"
                + "as\n"
                + "at\n"
                + "be\n"
                + "because\n"
                + "been\n"
                + "before\n"
                + "being\n"
                + "below\n"
                + "between\n"
                + "both\n"
                + "but\n"
                + "by\n"
                + "can\n"
                + "can't\n"
                + "cannot\n"
                + "could\n"
                + "couldn't\n"
                + "did\n"
                + "didn't\n"
                + "do\n"
                + "does\n"
                + "doesn't\n"
                + "doing\n"
                + "don't\n"
                + "down\n"
                + "during\n"
                + "each\n"
                + "few\n"
                + "for\n"
                + "from\n"
                + "further\n"
                + "had\n"
                + "hadn't\n"
                + "has\n"
                + "hasn't\n"
                + "have\n"
                + "haven't\n"
                + "having\n"
                + "he\n"
                + "he'd\n"
                + "he'll\n"
                + "he's\n"
                + "her\n"
                + "here\n"
                + "here's\n"
                + "hers\n"
                + "herself\n"
                + "him\n"
                + "himself\n"
                + "his\n"
                + "how\n"
                + "how's\n"
                + "i\n"
                + "i'd\n"
                + "i'll\n"
                + "i'm\n"
                + "i've\n"
                + "if\n"
                + "in\n"
                + "into\n"
                + "is\n"
                + "isn't\n"
                + "it\n"
                + "it's\n"
                + "its\n"
                + "itself\n"
                + "let's\n"
                + "me\n"
                + "many\n"
                + "make\n"
                + "more\n"
                + "most\n"
                + "mustn't\n"
                + "my\n"
                + "myself\n"
                + "no\n"
                + "nor\n"
                + "not\n"
                + "of\n"
                + "off\n"
                + "on\n"
                + "once\n"
                + "only\n"
                + "or\n"
                + "other\n"
                + "ought\n"
                + "our\n"
                + "ours	ourselves\n"
                + "out\n"
                + "over\n"
                + "own\n"
                + "same\n"
                + "shan't\n"
                + "she\n"
                + "she'd\n"
                + "she'll\n"
                + "she's\n"
                + "should\n"
                + "shouldn't\n"
                + "so\n"
                + "some\n"
                + "such\n"
                + "than\n"
                + "that\n"
                + "that's\n"
                + "the\n"
                + "their\n"
                + "theirs\n"
                + "them\n"
                + "themselves\n"
                + "then\n"
                + "there\n"
                + "there's\n"
                + "these\n"
                + "they\n"
                + "they'd\n"
                + "they'll\n"
                + "they're\n"
                + "they've\n"
                + "this\n"
                + "those\n"
                + "through\n"
                + "to\n"
                + "too\n"
                + "under\n"
                + "until\n"
                + "up\n"
                + "very\n"
                + "was\n"
                + "wasn't\n"
                + "we\n"
                + "we'd\n"
                + "we'll\n"
                + "we're\n"
                + "we've\n"
                + "were\n"
                + "weren't\n"
                + "what\n"
                + "what's\n"
                + "when\n"
                + "when's\n"
                + "where\n"
                + "whereas\n"
                + "where's\n"
                + "which\n"
                + "while\n"
                + "who\n"
                + "who's\n"
                + "whom\n"
                + "why\n"
                + "why's\n"
                + "with\n"
                + "won't\n"
                + "would\n"
                + "wouldn't\n"
                + "you\n"
                + "you'd\n"
                + "you'll\n"
                + "you're\n"
                + "you've\n"
                + "your\n"
                + "yours\n"
                + "yourself\n"
                + "yourselves\n'";
        stopwords.addAll(Arrays.asList(s.split("\\\n")));
        stopwords.add("");
    }

    public Double[] confidenceSupport(String phrase){
//        Double prob = 0.0;
        Double[] confSup = {0.0, 0.0};
        String words[] = phrase.split("_");
        try {
            Integer phraseNum = phraseCount.get(phrase);
            Integer word1Num = wordCount.get(words[0]);
            //Integer word2Num = wordCount.get(words[1]);
//            prob = (double) (phraseNum) / (double)(word1Num);
            confSup[0] = (double) phraseNum / word1Num;
            confSup[1] = (double)(phraseNum);
        }catch(NullPointerException npEX){
            npEX.printStackTrace();
            System.out.println("I can't find word: "+phrase);
        }
        return confSup;
    }

    // TODO: What's the difference of confidence 1 to 4?
    public Double[] confidenceSupport_2(String phrase){
//        Double prob = 0.0;
        Double[] confSup = new Double[2];
        String[] words = phrase.split("_");
        try {
            Integer phraseNum = phraseCount.get(phrase);
            //Integer word1Num = wordCount.get(words[0]);
            Integer word2Num = wordCount.get(words[1]);
//            prob = (double) phraseNum / word2Num;
            confSup[0] = (double) phraseNum / word2Num;
            confSup[1] = (double) phraseNum;
        }catch(NullPointerException npEX){
            npEX.printStackTrace();
            System.out.println("I can't find word: "+phrase);
        }
        return confSup;
    }

    public Double[] confidenceSupport_3(String phrase){
//        Double prob = 0.0;
        Double[] confSup = {0.0,0.0};
        String words[] = phrase.split("_");
        Integer phraseNum = phraseCount.get(phrase);
        try {
            Integer word1Num = wordCount.get(words[0]);
            Integer word2Num = wordCount.get(words[1]);
            //Integer minNum = Math.min(word1Num,word2Num);
//            prob = (double) (phraseNum) / (double) (Math.min(word1Num, word2Num));
            confSup[0] = (double) phraseNum / Math.min(word1Num, word2Num);
            confSup[1] = (double) phraseNum;
        }catch(NullPointerException npEX){
            npEX.printStackTrace();
            System.out.println("I can't find word: "+phrase);
        }
        return confSup;
    }

    public Double[] confidenceSupport_4(String phrase){
//        Double prob = 0.0;
        Double[] confSup = {0.0,0.0};
        String words[] = phrase.split("_");
        try {
            Integer phraseNum = trigramCount.get(phrase);
            Integer prephraseNum = phraseCount.get(words[0]+"_"+words[1]);
            Integer sufphraseNum = phraseCount.get(words[1]+"_"+words[2]);
//            prob = (double) (phraseNum) / (double)Math.min(prephraseNum,sufphraseNum);
            confSup[0] = (double) phraseNum / Math.min(prephraseNum,sufphraseNum);
            confSup[1] = (double) phraseNum;
        }catch(NullPointerException npEX){
            npEX.printStackTrace();
            System.out.println("I can't find word: "+phrase);
        }
        return confSup;
    }

    /**
     * Compute the T-test value of a phrase.
     *
     * @param phrase the phrase to compute T-test value of.
     * @return tValueSup // TODO: what are the two entries of tValueSup?
     */
    public Double[] t_test(String phrase){
        Double phraseProb = 0.0; // TODO: what are phraseProb and nullHypoMean?
        Double nullHypoMean = 0.0;
        Double[] tValueSup = {0.0, 0.0};
        String words[] = phrase.split("_");
        try{
            Integer phraseNum = phraseCount.get(phrase);
            Integer word1Num = wordCount.get(words[0]);
            Integer word2Num = wordCount.get(words[1]);
            phraseProb = (double) phraseNum/ phraseCount.size();
            nullHypoMean = (double) (word1Num * word2Num) / (wordCount.size() * wordCount.size());
            tValueSup[0] = Math.abs(phraseProb - nullHypoMean)/(Math.sqrt(phraseProb/wordCount.size()));
            tValueSup[1] = (double) phraseNum;
        }catch(NullPointerException npEX){
            npEX.printStackTrace();
            System.out.println("I can't find word: "+phrase);
        }
        return tValueSup;
    }

    /**
     * Check whether the phrase contains a stop word.
     *
     * @param phrase the phrase to be chekced
     * @return <b>true</b> if phrase contains a stop word. <b>false</b> if it doesn't.
     */
    public boolean containsStopWords(String phrase){
        String[] words = phrase.split("_");
        for (String word:words)
            if (stopwords.contains(word))
                return true;
        return false;
    }

    /**
     * Check if any word in the phrase is completely comprised by numbers.
     * @param phrase the phrase to be checked
     * @return <b>true</b> if any word in the phrase is completely comprised by numbers. <b>false</b> if not.
     */
    public boolean ifNotCharacter(String phrase){
        String[] words = phrase.split("_");
        for(String word:words)
            if((word).matches("^[0-9,.]+$"))
                return true;
        return false;
    }

    /**
     * //TODO: What does this function do?
     *
     * @param phrase
     * @return
     */
    public boolean hasSpecialChar(String phrase){ // if contains return true now: hope if all contains return true
        String words[] = phrase.split("_");
        for (String word:words){
            if(word.replaceAll("[^A-Za-z0-9]"," ").trim().isEmpty()){
                return true;
            }
        }
        return false;
    }

    /**
     * Check whether and Phrase is valid. If it get false from *containsStopWords*
     * *ifNotCharacter* and *hasSpecialChar*, then it is valid.
     * @param phrase the phrase to be checked.
     * @return <b>true</b> if all conditions are satisfied. <b>false</b> Vise-Versa.
     */
    public boolean validPhrase(String phrase){
        PhraseFilter pf = new PhraseFilter();
        return !(pf.ifNotCharacter(phrase) ||  pf.containsStopWords(phrase) || pf.hasSpecialChar(phrase));
//        if(!(pf.ifNotCharacter(phrase) || pf.containsStopWords(phrase) || pf.hasSpecialChar(phrase))){
//            return true;
//        }
//        else{
//            return false;
//        }
    }

    /**
     * Fill in `wordCount` and `phraseCount`, get distinct phrase set, output valid and invalid phrases.
     * @throws IOException When cannot find file or cannot create file.
     * @throws NullPointerException When cannot find the entry in set.
     */
    public void selectPhrase() throws IOException, NullPointerException {
        Double thold2Low = 0.025;
        Double thold2High = 1.0;
        Double minSup2 = 20.0;

        // TODO: Cannot find these files
        String wordFreqFile = "src/main/data/wordFrequency";
        String phraseFreqFile = "src/main/data/phrase2Frequency";
        Properties properties = new Properties(); // TODO: always using the `properties`, can be simplifed

        // Loading wordCount and phraseCount
        try{
            properties.load(new FileInputStream(wordFreqFile));
            for (String key : properties.stringPropertyNames())
                wordCount.put(key, Integer.valueOf(properties.get(key).toString()));
            properties.clear();

            properties.load(new FileInputStream(phraseFreqFile));
            for (String key : properties.stringPropertyNames())
                phraseCount.put(key, Integer.valueOf(properties.get(key).toString()));
            properties.clear();
        }catch(NullPointerException | IOException e){
            e.printStackTrace();
            System.out.println("I can't read the file");
        }

        // Detecting invalid phrases
        Integer linecount = 0;
        try(PrintWriter outInvalidPhrase = new PrintWriter("src/main/data/invalid_phrase.txt");
            PrintWriter outPhrase = new PrintWriter("src/main/data/phrase2List_20.txt")){
            // TODO: Cannot find these files either
            for ( String phrase : phraseCount.keySet() ) {
                if (validPhrase(phrase)){
                    Double[] confSup = confidenceSupport_3(phrase);
                    if(confSup[0] >= thold2Low
                       && confSup[0] < thold2High
                       && confSup[1] >= minSup2){
                        phraseProb.put(phrase, confSup[0]);
                        outPhrase.println(phrase);
                    }
                }
                else{
                    outInvalidPhrase.println(phrase);
                }
            }
            System.out.println(linecount);
            //Prints the Number of Distinct words found in the files read
            System.out.println(phraseProb.size() + " distinct phrases:");
        }catch(IOException e) {
            e.printStackTrace();
            System.out.println("I can't write test file");
        }

        // Output all the valid phrases
        for (Map.Entry<String,Double> entry : phraseProb.entrySet()) {
            properties.put(entry.getKey(), Double.toString(entry.getValue()));
        }

        // TODO: change variable name of this FileOutputStream
        try(FileOutputStream fos = new FileOutputStream(new File("src/main/data/phraseProbability_sup_20_c_min_0.025_max_1"))){
            properties.store(fos, null);
        }catch (NullPointerException | IOException e) {
            e.printStackTrace();
            System.out.println("I can't write the file:" + e);
        }
        properties.clear();

        // Output all the phraseProbabilities
        for (String key : phraseProb.keySet()) {
            properties.put(key, Double.toString(phraseCount.get(key)));
        }
        try(FileOutputStream fos=new FileOutputStream(new File("src/main/data/phraseFrequency_sup_20_c_min_0.025_max_1"))){
            properties.store(fos, null);
        }catch (NullPointerException | IOException e) {
            e.printStackTrace();
            System.out.println("I can't write the file:" + e);
        }
    }


    /**
     * Merge all the bigrams to get trigrams
     *
     * @throws IOException when cannot open files
     */
    public void mergePhrase() throws IOException{
        Map<String,HashSet<String>> bigramSet = new HashMap<>();

        String infile = "src/main/data/phrase2List_20.txt";
        BufferedReader br = new BufferedReader(new FileReader(infile));

        // Load all bigrams into bigramSet
        String line;
        while ((line = br.readLine()) != null) {
            String[] words = line.split("_");
            HashSet<String> set = bigramSet.get(words[0]);
            if(set == null)
                set = new HashSet<>();
            set.add(words[1]);
            bigramSet.put(words[0], set);
        }

        // Merge bigrams to get trigrams
        // Key + Word + RightWord make a new trigram.
        HashSet<String> keySet = new HashSet<String>(bigramSet.keySet());
        for(String key: keySet){
            HashSet<String> set = bigramSet.get(key);
            for(String word:set){
                if(keySet.contains(word)){
                    for (String rightWord: bigramSet.get(word)) {
                        String tmpTrigram = key + "_" + word + "_"+ rightWord;
                        trigramSet.add(tmpTrigram);
                    }
                }
            }
        }
    }

    /**
     * Select and output all trigrams.
     *
     * @throws IOException when cannot open files
     */
    public void selectPhrase3() throws IOException{
        Double thold3 = 0.0;
        Double minSup = 20.0;
        String infile = "src/main/data/phrase3Frequency";
        PrintWriter outInvalidTrigram = new PrintWriter("src/main/data/invalid_phrase_3.txt");
        PrintWriter outTrigram = new PrintWriter("src/main/data/phrase3List_20");
        Properties properties = new Properties();
        try{
            properties.load(new FileInputStream(infile));

            for (String key : properties.stringPropertyNames()) {
                trigramCount.put(key, Integer.valueOf(properties.get(key).toString()));
            }
            properties.clear();
        }catch(NullPointerException | IOException e){
            e.printStackTrace();
            System.out.println("I can't read the file");
        }
        for ( String phrase : trigramSet) {
            if (validPhrase(phrase)) {
                Double[] confSup = confidenceSupport_4(phrase);
                if(confSup[0]> thold3 && confSup[1]> minSup){
                    phraseProb.put(phrase, confSup[0]);
                    outTrigram.println(phrase + " " + Double.toString(confSup[1]));
                }
            } else {
                outInvalidTrigram.println(phrase);
            }
        }
        outTrigram.close();
    }

    /**
     * Output all Phrases to files
     *
     * @throws IOException
     */
    // TODO: Lacking a try-catch statement
    public void writePhraseList() throws IOException{
        String outfile = "src/main/data/phraseList_20.txt";
        PrintWriter outString = new PrintWriter(outfile);
        for(String key:phraseProb.keySet()){
            outString.println(key);
        }
        outString.close();
    }

    /**
     * Write properties to files
     *
     * @throws IOException
     */
    public void writeProperties() throws IOException{
        Properties properties = new Properties();
        for (Map.Entry<String,Double> entry : phraseProb.entrySet()) {
            properties.put(entry.getKey(), Double.toString(entry.getValue()));
        }
        try{
            File fileOne=new File("src/main/data/phraseProbability_all_20");
            FileOutputStream fos=new FileOutputStream(fileOne);
            properties.store(fos, null);
            fos.close();
        }catch (NullPointerException | IOException e) {
            e.printStackTrace();
            System.out.println("I can't write the file:" + e);
        }
    }

    public static void main(String[] args) throws IOException{
        PhraseFilter PF = new PhraseFilter();
        System.out.println("Selecting bigram phrases");
        PF.selectPhrase();
        PF.wordCount.clear();
        System.out.println("Generate possible trigram phrases for bigram phrases");
        PF.mergePhrase();
        System.out.println("Selecting trigram phrases");
        PF.selectPhrase3();
        System.out.println("Writing all high quality phrases into file");
        PF.writePhraseList();
        PF.writeProperties();

    }
}
