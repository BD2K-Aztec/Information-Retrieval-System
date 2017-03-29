package edu.ucla.cs.scai.aztec.utils;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import edu.ucla.cs.scai.aztec.AztecEntry;
import edu.ucla.cs.scai.aztec.AztecEntryProviderFromJsonFile;
import edu.ucla.cs.scai.aztec.EntryWrapper1;
import edu.ucla.cs.scai.aztec.similarity.CachedData;
import edu.ucla.cs.scai.aztec.summarization.RankedString;
import edu.ucla.cs.scai.aztec.keyphrase.Tokenizer;
import net.sf.extjwnl.JWNLException;

import javax.json.Json;
import javax.tools.FileObject;
import java.awt.*;
import java.io.*;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Xinxin on 10/12/2016.
 */
public class EntryAnalysis {
    HashMap<String, AztecEntry> entryMap = new HashMap<>();
    ArrayList<AztecEntry> entryArray;
    Tokenizer tokenizer;
    static HashMap<String, LinkedList<RankedString>> keywords;
    public EntryAnalysis() throws FileNotFoundException,JWNLException {
        tokenizer = new Tokenizer();
    }

    public void loadEntry(String filepath){
        System.out.println("Entries path system property: " + System.getProperty("entries.path"));
        String entriesPath = System.getProperty("entries.path", filepath);
        try {
            entryArray = new AztecEntryProviderFromJsonFile(entriesPath).load();
            for (AztecEntry e : entryArray) {
                entryMap.put(e.getId(), e);
            }
        } catch (Exception ex) {
            Logger.getLogger(CachedData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void loadEntry2(String filepath){
        System.out.println("Entries path system property: " + System.getProperty("entries.path"));
        String entriesPath = System.getProperty("entries.path", filepath);
        try {
            entryArray = new AztecEntryProviderFromJsonFile(entriesPath).load();
            for (AztecEntry e : entryArray) {
                String nid = Integer.toString(Integer.valueOf(e.getId())+6074);
                e.setId(nid);
                entryMap.put(nid, e);
            }
        } catch (Exception ex) {
            Logger.getLogger(CachedData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void dataFilter() throws IOException,JsonSyntaxException{
        HashSet<String> name = new HashSet<>();
        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        ArrayList<AztecEntry> finaljson = new ArrayList<AztecEntry>();
        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream("src/main/data/newData_author.json"), StandardCharsets.UTF_8);
        for(AztecEntry e:entryMap.values()){
            if(e.getDescription() != null){
                String des = e.getDescription().replace("\\p{P}","");
                if(des.trim().split("\\s+").length>15) {
                    if(name.add(e.getName())) {
                        finaljson.add(e);
                    }
                }
            }
        }
        Gson gson2 = new Gson();
        JsonElement element = gson2.toJsonTree(finaljson, new TypeToken<ArrayList<AztecEntry>>() {}.getType());
        JsonArray docs = element.getAsJsonArray();
        JsonObject inner = new JsonObject();
        inner.add("docs",docs);
        JsonObject jsonoutter = new JsonObject();
        jsonoutter.add("response",inner);
        gson.toJson(jsonoutter,writer);
        writer.close();

    }
    public void ProcessAuthor() throws IOException{
        PrintWriter pw = new PrintWriter(new FileOutputStream("src/main/data/authorList.txt"));
        Set<String> author_set = new HashSet<>();
        for (AztecEntry e: entryArray){
            ArrayList<String> authors = e.getAuthors();
            if(authors!=null && authors.size()>0) {
                for (String author : authors) {
                    LinkedList<String> parsed_author = tokenizer.tokenize(author);
                    author_set.add((String.join("_",parsed_author)));
                }
            }
        }
        for(String aut: author_set){
            pw.write(aut+"\n");
        }
        pw.close();
    }
    public void ProcessInstuitation() throws IOException{
        PrintWriter pw = new PrintWriter(new FileOutputStream("src/main/data/instList.txt"));
        Set<String> inst_set = new HashSet<>();
        for (AztecEntry e: entryArray){
            ArrayList<String> insts = e.getInstitutions();
            if(insts!=null && insts.size()>0) {
                for (String inst : insts) {
                    LinkedList<String> parsed_inst = tokenizer.tokenize(inst);
                    inst_set.add((String.join("_",parsed_inst)));
                }
            }
        }
        for(String ins: inst_set){
            pw.write(ins+"\n");
        }
        pw.close();

    }


    public void BasicInfo(){
        Integer N= entryMap.size();
        System.out.print("number: ");
        System.out.println(N);
        Integer avgdocLength = 0;
        Integer avgtagNum = 0;
        Integer cnotag = 0;
        Integer cnoDom = 0;
        Integer cnodes = 0;
        Integer numdex_10 = 0;
        Integer tagnum = 0;
        for(AztecEntry e:entryArray){
            if(e.getDescription() == null){
                cnodes +=1;
            }
            else{
                String des = e.getDescription().replace("\\p{P}","");
                Integer num = des.trim().split("\\s+").length;
                avgdocLength += num;

                if(num>=40){
                    numdex_10+=1;
                }
            }
            if(e.getTags() == null){
                cnotag+=1;
            }
            else{
                avgtagNum += e.getTags().size();
                if(e.getTags().size()<=1){
                    tagnum+=1;
                }
            }
            if(e.getDomains() == null){
                cnoDom+=1;
            }
        }
        Double davgdocLength = avgdocLength*1.0/N;
        Double davgtagNum = avgtagNum*1.0/N;
        System.out.print("average des length ");
        System.out.println(davgdocLength);
        System.out.print("average tag num ");
        System.out.println(davgtagNum);
        System.out.print("no des num ");
        System.out.println(cnodes);
        System.out.print("no tag num ");
        System.out.println(cnotag);
        System.out.print("no dom num ");
        System.out.println(cnoDom);
        System.out.println(numdex_10);
        System.out.println(tagnum);
    }
    public void printentry() throws IOException{
        BufferedReader reader = new BufferedReader(new FileReader("src/main/data/search_result_v9_2/Top30_list.txt"));
        String line;
        while((line = reader.readLine())!=null){
            Integer count = 1;
            String[] id_eid = line.trim().split(":");
            String ID = id_eid[0];
            PrintWriter writer = new PrintWriter(new FileOutputStream("src/main/data/search_result_v9_2/Top30_des_"+ID+".txt"));
            String eid = id_eid[1].trim();
            String[] list_id = eid.split(" ");
            for(String entryid:list_id){
                writer.print(count);
                if(entryMap.get(entryid)!=null) {
                    if ((entryMap.get(entryid).getDescription() != null) && (entryMap.get(entryid).getName() != null)) {
                        writer.println("\t" + entryMap.get(entryid).getId() + "\t" + entryMap.get(entryid).getName() + "\t" + entryMap.get(entryid).getDescription());
                        if (entryMap.get(entryid).getTags() != null) {
                            writer.println("\t" + entryMap.get(entryid).getTags());
                        }
                    } else {
                        System.out.println(ID);
                        System.out.println(entryid);
                    }

                    if (count % 10 == 0) {
                        writer.println("-------------------");
                    }
                    count += 1;
                }
                else{
                    System.out.println(entryid);
                }
            }
            writer.close();
        }
    }

    public static void main(String[] args) throws IOException,JWNLException{
        EntryAnalysis EA = new EntryAnalysis();
        EA.loadEntry("src/main/data/newData.json");
//        EA.ProcessAuthor();
        EA.ProcessInstuitation();
//        String file1="src/main/data/solrResources.json";
//        EA.loadEntry(file1);
//        String file2 = "src/main/data/data.json";
//        EA.loadEntry2(file2);
//        EA.dataFilter();
//        EntryAnalysis EA2 = new EntryAnalysis();
//        EA2.loadEntry("src/main/data/newData_author.json");
//        EA2.BasicInfo();
    }
}
