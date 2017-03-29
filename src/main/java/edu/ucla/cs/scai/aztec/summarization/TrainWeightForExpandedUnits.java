package edu.ucla.cs.scai.aztec.summarization;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xinxin on 2/8/17.
 */
public class TrainWeightForExpandedUnits {
    private final static Map<String, List<RankedString>> similarity = new HashMap<>();
    public void loadMap(String infile) throws IOException { // input format -- Key: simUnit1,score simUnit2,score simUnit3,score ....
        BufferedReader reader = new BufferedReader(new FileReader(infile));
        String line;
        while((line = reader.readLine()) != null){
            List<RankedString> simPair = new ArrayList<>();
            String key = line.trim().split(":")[0].trim();
            String simString = line.trim().split(":")[1].trim();
            String[] simList = simString.split(" ");
            for(String pair: simList){
                String unit = pair.split(",")[0].trim();
                Double score = Double.parseDouble(pair.split(",")[1].trim());
                simPair.add(new RankedString(unit,score));
            }
            similarity.put(key,simPair);
            //simPair.clear();
        }
        reader.close();
    }
    public TrainWeightForExpandedUnits() throws IOException{
        this.loadMap("src/main/data/SimilarityFile.txt");
    }
    public void LearnWeight(){

    }
    // function first expand the document and retrieval first 10 documents -- return list of list
    // function calculate the langueage model score for the 10 documents--return list of 10
    // function calculate the NGCD based on the origial rank and language model score -- return the objective value
    // how to find the gradient??
}
