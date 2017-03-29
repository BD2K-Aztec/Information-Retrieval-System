package edu.ucla.cs.scai.aztec.similarity;

import edu.ucla.cs.scai.aztec.AztecEntry;
import edu.ucla.cs.scai.aztec.AztecEntryProviderFromJsonFile;
import edu.ucla.cs.scai.aztec.summarization.KeywordsBuilder;
import edu.ucla.cs.scai.aztec.summarization.RankedString;
import edu.ucla.cs.scai.aztec.textexpansion.TextParser;
import net.sf.extjwnl.JWNLException;
import org.apache.poi.ss.formula.functions.Countif;
import org.apache.poi.ss.formula.functions.Rank;
import sun.awt.image.ImageWatched;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
/**
 * Created by Xinxin on 3/14/2017.
 */
public class IndentityAttrBuilder {
    TextParser textparser;
    Tokenizer tokenizer;
    HashMap<String,LinkedList<RankedString>> idinfotf = new HashMap<>();
    public IndentityAttrBuilder() throws Exception{
        tokenizer = new Tokenizer();
        textparser = new TextParser();
    }
    public HashMap<String, LinkedList<RankedString>> buildIDTF(Collection<AztecEntry> entries, String outputPath) throws JWNLException,IOException {
        HashMap<String, LinkedList<RankedString>> res = new HashMap<>();
        PrintWriter pw = new PrintWriter(new FileOutputStream("src/main/data/wrongauthor.txt"));
        for(AztecEntry entry: entries) {
            ArrayList<String> authors = entry.getAuthors();
            ArrayList<String> institutions = entry.getInstitutions();
            String name = entry.getName();
            LinkedList<RankedString> idattr_tf = new LinkedList<>();
            if(authors!=null && authors.size()>0) {
                for (String author : authors) {
                    LinkedList<String> parsed_author = textparser.docParser(author);
                    for (String p : parsed_author) {
                        RankedString ele_score = new RankedString(p, 10.0); //if the user is searching names, it can be matched on name, we will give them priority
                        idattr_tf.add(ele_score);
                    }
                }
            }
            else{
                if(authors!=null) {
                    pw.write(authors.toString() + "\n");
                }
            }
            if(institutions!=null && institutions.size()>0) {
                for (String institution : institutions) {
                    LinkedList<String> parsed_inst = textparser.docParser(institution);
                    for (String p : parsed_inst) {
                        RankedString ele_score = new RankedString(p, 10.0); //if the user is searching names, it can be matched on name, we will give them priority
                        idattr_tf.add(ele_score);
                    }
                }
            }
            if(name!=null) {
                LinkedList<String> parsed_name = textparser.docParser(name);
                for (String p : parsed_name) {
                    RankedString ele_score = new RankedString(p, 10.0); //if the user is searching names, it can be matched on name, we will give them priority
                    idattr_tf.add(ele_score);
                }
            }
            res.put(entry.getId(),idattr_tf);
        }
        System.out.println("Writing keywords to file " + outputPath);
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(outputPath))) {
            out.writeObject(res);
        } catch (Exception ex) {
            Logger.getLogger(IndentityAttrBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
        return res;
    }
    public static void main(String[] args) throws Exception{
        String outfile = "src/main/data/IDdata.data";
        IndentityAttrBuilder IAB = new IndentityAttrBuilder();
        PrintWriter pw = new PrintWriter(new FileOutputStream("src/main/data/idattr.txt"));
        String entriesPath = System.getProperty("entries.path", "src/main/data/newData.json");
        ArrayList<AztecEntry> entryArray = new AztecEntryProviderFromJsonFile(entriesPath).load();
        HashMap<String, LinkedList<RankedString>> res = IAB.buildIDTF(entryArray,outfile);
        for(String id: res.keySet()){
            pw.write(id+":");
            LinkedList<RankedString> tmp = res.get(id);
            for(RankedString rs:tmp){
                pw.write(rs.toString()+" ");
            }
            pw.write("\n");
        }
        pw.close();
    }
}
