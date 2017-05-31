package edu.ucla.cs.scai.aztec.keyphrase;

import edu.ucla.cs.scai.aztec.textexpansion.TextParser;
import net.sf.extjwnl.JWNLException;

import java.io.*;
import java.util.*;

/**
 * Parse sentence into the required format of Word2Vector.
 * Not Used in Search Engine.
 *
 * @author Xinxin Huang "xinxinh@gmail.com" 7/29/16
 * @author Zeyu Li "zyli@cs.ucla.edu" 5/30/2017
 */
public class ParseSentence {
    private final static HashSet<String> phraseList = new HashSet<>();

    /**
     * From infine load data into phraseList, line by line after trim.
     *
     * @param infile The filename from which we load data.
     * @throws IOException when cannot find the file
     */
    public void loadData(String infile) throws IOException{
        try(BufferedReader reader = new BufferedReader(new FileReader(infile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                phraseList.add(line.trim());
            }
        } catch (IOException ioEx){
            ioEx.printStackTrace();
        }
    }

    /**
     * writeData from "list" to the file "outfile"
     *
     * @param list The list strings to output.
     * @param outfile The output filename.
     * @throws IOException when cannot create the file
     */
    public void writeData(HashSet<String> list, String outfile) throws IOException{
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(outfile))) {
            Iterator it = list.iterator();
            if (it.hasNext()) {
                writer.write(it.next() + "\n");
            }
        }catch(IOException ioEx){
            ioEx.printStackTrace();
        }
        //writer.close();
    }

    public static void main(String[] args) throws IOException, JWNLException{
        TextParser TP = new TextParser();
        String infile = "/home/xinxin/AztecData/phraseList_Chi_newdata_solr";
        String senfile = "/home/xinxin/AztecData/abstract_removeurl_newdata_solr.txt";
        String outfile = "/home/xinxin/AztecData/parsedSentence_for_train_Chi_newdata_solr.txt";
        PrintWriter outString = new PrintWriter(outfile);
        ParseSentence parser = new ParseSentence();
        parser.loadData(infile);
        BufferedReader br = new BufferedReader(new FileReader(senfile));
        String line;
        String parserSen;
        Integer linecount = 0;
        while((line = br.readLine()) != null){
            StringBuilder parsedSen = new StringBuilder();
            for( String unit: TP.queryParser(line.trim())){
                parsedSen.append(" ").append(unit);
            }
//            String parsedSen = line.toLowerCase();
//            String[] sens = line.trim().replaceAll("[^A-Za-z0-9\\-\\s+]","\\|").toLowerCase().split("\\|");
//            for(Integer i=0;i<sens.length;i++){
//                //String sen = sens[i].replaceAll("-"," ");
//                String[] words = sens[i].trim().split("\\s+");
//                if (words.length>1){                            // if it possible to contain a phrase
//                    for (Integer j = 0;j<words.length-1;j++) {
//                        String phrase = words[j] + "_" + words[j + 1];
//                        if(phraseList.contains(phrase)){
//                            parsedSen = parsedSen.replace(words[j]+" "+words[j+1],phrase); // automatically merged phrases
//                            //System.out.println("parsed");
//                        }
//                    }
//                }
//            }
            outString.println(parsedSen);
            linecount++;
            if(linecount%1000 == 0){
                System.out.println(linecount/1000);
            }
        }

    }
}
