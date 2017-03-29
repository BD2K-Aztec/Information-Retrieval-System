package edu.ucla.cs.scai.aztec.keyphrase;

import edu.ucla.cs.scai.aztec.AztecEntry;
import edu.ucla.cs.scai.aztec.AztecEntryProviderFromJsonFile;
import net.sf.extjwnl.JWNLException;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.*;


/**
 * Created by xinxin on 3/13/17.
 */
public class VocabularyGenerator {
    static ArrayList<AztecEntry> entryArray;
    public void loadJson(String infile) throws Exception{
        entryArray = new AztecEntryProviderFromJsonFile(infile).load();
    }
    public void getName(String att, String outfile) throws Exception {

        PrintWriter pw = new PrintWriter(new FileOutputStream(outfile));
        List<String> attr = new ArrayList<>();
        for(AztecEntry entry:entryArray){

            attr.add(entry.getName());
        }

    }
}
