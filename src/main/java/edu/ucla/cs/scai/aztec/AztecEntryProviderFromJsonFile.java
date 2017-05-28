package edu.ucla.cs.scai.aztec;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Loading Aztec Entries from Json File
 *
 * @author Giuseppe M. Mazzeo "mazzeo@cs.ucla.edu"
 * @author Xinxin Huang "xinxinh@gmail.com"
 * @author Zeyu Li "zyli@cs.ucla.edu"
 */
public class AztecEntryProviderFromJsonFile implements AztecEntryProvider {

    private String fileName;

    public AztecEntryProviderFromJsonFile(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Load Json to AztecEntries.
     *
     * @return The docs of EntryWrapper1.
     * @throws Exception Exception when cannot read files
     */
    @Override
    public ArrayList<AztecEntry> load() throws Exception {
        StringBuilder json = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF8"))) {

            String l;
            while ((l = in.readLine()) != null) {
                json.append(l).append(" ");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        // Gson converts an Java Object to Json or Vise-Versa. Depends on toJson or fromJson
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
        EntryWrapper1 w = gson.fromJson(json.toString(), EntryWrapper1.class); // TODO: why 2 EntryWrapper?
        System.out.println("Loaded "+w.getdocs().size()+" entries");
        return w.getdocs();
    }

//    public static void main(String[] args) throws Exception {
//        System.out.println("Entries path system property: "+System.getProperty("entries.path"));
//        String entriesPath = System.getProperty("entries.path", "/home/massimo/Downloads/solrResources.json");
//        ArrayList<AztecEntry> entries=new AztecEntryProviderFromJsonFile(entriesPath).load();
//        System.out.println(entries.size());
//    }
    // TODO: Write test case of Class "AztecEntryProviderFromJsonFile".
}
