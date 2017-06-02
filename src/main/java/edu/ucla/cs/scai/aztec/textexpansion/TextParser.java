package edu.ucla.cs.scai.aztec.textexpansion;

import edu.ucla.cs.scai.aztec.keyphrase.Tokenizer;
import net.sf.extjwnl.JWNLException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Class for Text Parser.
 *
 * @author Xinxin Huang "xinxinh@gmail.com" (8/3/2016)
 * @author Zeyu Li "zyli@cs.ucla.edu"
 */
public class TextParser{
    private final static HashSet<String> phraseList = new HashSet<>();
    private final static HashSet<String> stopWords = new HashSet<>();
    private static Integer maxPhrase = 5;
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
        stopWords.addAll(Arrays.asList(s.split("\\\n")));
        stopWords.add("");
    }
//    public TextParser() throws JWNLException, FileNotFoundException{
//        Tokenizer token = new Tokenizer();
//    }

    /**
     * Constructor.
     *
     * @throws IOException
     */
    public TextParser() throws IOException{
        this.loadData("src/main/data/phraseList_Chi_newdata");
    }

    /**
     * Load file to phraseList.
     *
     * @param inFile the filename from which to load data.
     * @throws IOException when cannot find file.
     */
    // TODO: Maybe change it to try-catch statement someday.
    public void loadData(String inFile) throws IOException{
        BufferedReader reader = new BufferedReader(new FileReader(inFile));
        String line;
        while((line = reader.readLine()) != null){
            phraseList.add(line.trim());
        }
        reader.close();
    }


    /**
     * queryParser, old version?
     *
     * Leave it private first.
     */
//    private LinkedList<String> queryParser_old(String text) throws JWNLException, IOException{
//        LinkedList<String> unitList = new LinkedList<>();
//        //String infile = "src/main/data/phraseList_20.txt";
//        //TextParser parser = new TextParser();
//        Tokenizer token = new Tokenizer();
//        //this.loadData(infile);
//        LinkedList<String> words = token.tokenize(text);
//        String phrase;
//        if(words.size()>1) {
//            Integer final_idx = words.size() - 2;
//            Integer i = 0;
//            while(i < final_idx) {
//                phrase = words.get(i)+"_"+words.get(i+1)+"_"+words.get(i+2); // first check three words phrase
//                if(phraseList.contains(phrase)){
//                    unitList.add((phrase));
//                    i += 3; //skip all following words contained in phrase
//                }
//                else{
//                    phrase = words.get(i)+"_"+words.get(i+1);
//                    if (phraseList.contains(phrase)){
//                        unitList.add(phrase);
//                        i += 2;
//                    }
//                    else{
//                        unitList.add(words.get(i));
//                        i++;
//                    }
//                }
//            }
//            while(i<final_idx+1) { // check the last few words.
//                phrase = words.get(i) + "_" + words.get(i + 1);
//                if (phraseList.contains(phrase)) {
//                    unitList.add(phrase);
//                    i += 2;
//                } else { // if not phrase, add as two separate words
//                    unitList.add(words.get(i));
//                    i++;
//                }
//            }
//            while(i<final_idx+2){
//                unitList.add(words.get(i));
//                i++;
//            }
//
//        }
//        else{
//            unitList.add(words.get(0));
//        }
//        return  unitList;
//    }


    /**
     * Scan the input string with different window size to detect all the phrases.
     * @param text The input string to be scanned.
     * @return the list of detected phrases and words.
     * @throws JWNLException
     * @throws IOException
     */
    public LinkedList<String> queryParser(String text) throws JWNLException, IOException{
        Integer windowSize = maxPhrase;
        LinkedList<String> unitList = new LinkedList<>();
        Tokenizer tokenizer = new Tokenizer();
        LinkedList<String> words = tokenizer.tokenize(text);
        ArrayList<String> nextWords = new ArrayList<>();
        ArrayList<String> thisWords = new ArrayList<>();
        thisWords.addAll(words);

        for( ; windowSize >= 1; windowSize--){
            Integer startPos = 0;
            Integer endPos = startPos + windowSize -1;
            while(endPos < thisWords.size()){
                List<String> subPhraseList = thisWords.subList(startPos, endPos +1);
                String subPhrase = String.join("_", subPhraseList);
                if(phraseList.contains(subPhrase)){
                    nextWords.add(subPhrase);
                    startPos = endPos +1;
                }
                else{
                    nextWords.add(thisWords.get(startPos));
                    startPos++;
                }
                endPos = startPos + windowSize -1;
            }
            if(startPos < thisWords.size()) {
                nextWords.addAll(thisWords.subList(startPos, thisWords.size()));
            }
            thisWords = new ArrayList<>(nextWords);
            nextWords = new ArrayList<>();
//            windowSize--;
        }
        unitList.addAll(thisWords);
//        if(words.size()>1) {
//            Integer final_idx = words.size() - 2;
//            Integer i = 0;
//            while(i < final_idx) {
//                phrase = words.get(i)+"_"+words.get(i+1)+"_"+words.get(i+2); // first check three words phrase
//                if(phraseList.contains(phrase)){
//                    unitList.add((phrase));
//                    i += 3; //skip all following words contained in phrase
//                }
//                else{
//                    phrase = words.get(i)+"_"+words.get(i+1);
//                    if (phraseList.contains(phrase)){
//                        unitList.add(phrase);
//                        i += 2;
//                    }
//                    else{
//                        unitList.add(words.get(i));
//                        i++;
//                    }
//                }
//            }
//            while(i<final_idx+1) { // check the last few words.
//                phrase = words.get(i) + "_" + words.get(i + 1);
//                if (phraseList.contains(phrase)) {
//                    unitList.add(phrase);
//                    i += 2;
//                } else { // if not phrase, add as two separate words
//                    unitList.add(words.get(i));
//                    i++;
//                }
//            }
//            while(i<final_idx+2){
//                unitList.add(words.get(i));
//                i++;
//            }
//
//        }
//        else{
//            unitList.add(words.get(0));
//        }
        return  unitList;
    }

    /**
     * Remove all the punctuation units and stop words.
     * @param doc the Doc to work on.
     * @return the list of strings that has the punctuations and stop words removed.
     * @throws JWNLException
     * @throws IOException
     */
    public LinkedList<String> docParser(String doc) throws JWNLException, IOException {
        LinkedList<String> unitList = new LinkedList<>();
        LinkedList<String> tmpUnitList = this.queryParser(doc);
        for (String unit: tmpUnitList) // remove single punctuation unit
            if((!(Pattern.matches("(?!-_)\\p{Punct}", unit))) && (!stopWords.contains(unit)))
                unitList.add(unit);
        return  unitList;
    }
    public static void main(String[] args) throws JWNLException, IOException{
        String test = "metabolomics database";
        TextParser TP = new TextParser();
        LinkedList<String> units = TP.docParser(test);
        System.out.print(units);
    }
}
