package edu.ucla.cs.scai.aztec.textexpansion;

import java.io.*;
import java.util.*;

import edu.ucla.cs.scai.aztec.keyphrase.Tokenizer;
import edu.ucla.cs.scai.aztec.summarization.RankedString;
import edu.ucla.cs.scai.aztec.utils.StringUtils;
import net.sf.extjwnl.JWNLException;
import org.apache.poi.hssf.record.IndexRecord;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import com.opencsv.CSVReader;
import sun.awt.image.ImageWatched;

/**
 * MetshTreeHierachy Class
 *
 * @author Xinxin Huang "xinxinh@gmail.com" on 9/27/2016.
 */
public class MeshTreeHierarchy {
    public static HashMap<String, LinkedList<TreeNode>> Index = new HashMap<>(); // term, node
    public static HashMap<String, TreeNode> generalTable = new HashMap<>(); // tree_id, node used for building the hierarchy, seems trouble
    Tokenizer tk;
    public static HashSet<String> vocabulary = new HashSet<>(); // store the vocabulary

    /**
     * Constructor Method
     * @throws JWNLException
     * @throws FileNotFoundException
     */
    public MeshTreeHierarchy() throws JWNLException, FileNotFoundException{
        tk = new Tokenizer();
    }

    /**
     * Load vocabulary from files
     * @throws Exception when cannot open files
     */
    public void loadvocab() throws Exception{
        String fvocab = "/home/xinxin/AztecData/Vocabulary_w2v.txt";
        BufferedReader br = new BufferedReader(new FileReader(fvocab));
        String line;
        while((line = br.readLine()) != null){
            vocabulary.add(line.trim());
        }
    }

    /**
     * Build the Hierarchy Mesh Tree.
     * @param infile the input file name
     * @throws IOException
     */
    public void BuildHierarchy(String infile) throws IOException{
        PrintWriter pt = new PrintWriter(new FileOutputStream("/home/xinxin/AztecData/bugdata.txt"));
        PrintWriter pt2 = new PrintWriter(new FileOutputStream("/home/xinxin/AztecData/repeatdata.txt"));

        TreeNode new_tn;
        TreeNode par_tn;
        LinkedList<TreeNode> tnl;
        ArrayList<String> tl = new ArrayList<>();
        TreeNode root = new TreeNode();
        String ID;
        String term;
        String terms;
        String ParentID;
        String treeid = "";
        String[] treepath;
        int rows = 0; // Number of rows
        tl.add("ROOT");
        root.setTreeNode("ROOT",tl);
        CSVReader csvReader = new CSVReader(new InputStreamReader(new FileInputStream(infile),"UTF-16"), '\t', '\"','\0', 1, false, true);
        String[] row = null;
        while((row = csvReader.readNext()) != null) {
            if(rows == 60){
                System.out.println(60);
            }
            tl = new ArrayList<>();
            treeid = row[0];
            treepath = treeid.split("\\.");
            ID = treepath[treepath.length - 1];
            pt2.println(treeid);
            terms = row[2];
            String[] termset = terms.split(",");
            new_tn = new TreeNode();
            for (String t : termset) {
                LinkedList<String> tokens = tk.tokenize(t); //token the term fisrt
                term = String.join("_", tokens);
                tl.add(term);
                tnl = Index.get(term);
                if (tnl == null) {
                    tnl = new LinkedList<>();
                    tnl.add(new_tn);
                } else {
                    tnl.add(new_tn);
                }
                Index.put(term, tnl);
            }
            new_tn.setTreeNode(ID, tl);
            generalTable.put(treeid, new_tn);
            if (treepath.length == 1) {
                root.addChildren(ID, new_tn);
                new_tn.parent = root;
            } else {
                ParentID = String.join(".", Arrays.copyOfRange(treepath, 0, treepath.length - 1));
                par_tn = generalTable.get(ParentID); // could do this only because the hierarchy is organised in order.
                if (par_tn == null) {
                    pt.println(treeid);
                    Integer l = treepath.length - 1;
                    while (l > 0) { // may exist some gap in the hierarchy
                        ParentID = String.join(".", Arrays.copyOfRange(treepath, 0, l - 1));
                        par_tn = generalTable.get(ParentID); // could do this only because the hierarchy is organised in order.
                        if (par_tn != null) {
                            break;
                        }
                        l -= 1;
                    }
                    if (par_tn == null) {
                        par_tn = root;
                    }
                }
                par_tn.addChildren(ID, new_tn);
                new_tn.parent = par_tn;
            }
            rows += 1;
        }
        System.out.println(rows);
        System.out.println(treeid);
        csvReader.close();
        pt.close();
        pt2.close();
    }

    /**
     * Load table to hierarch table.
     * @param infile the filename from which to load data.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void readTable(String infile)throws IOException, ClassNotFoundException{
        System.out.println("Load hierarch on keywords path system property: " + System.getProperty("hierarch.path"));
        String tfidtPath = System.getProperty("hierarch.path", infile);
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(tfidtPath));
        Index = (HashMap<String, LinkedList<TreeNode>>) ois.readObject();
        generalTable= (HashMap<String, TreeNode>) ois.readObject();
        ois.close();
    }


    /**
     * Print table to files.
     * @param outfile The target output file.
     * @throws IOException
     */
    public void writeTable(String outfile) throws IOException{
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(outfile));
        out.writeObject(Index);
        out.writeObject(generalTable);
        out.close();
    }

    /**
     * Get the Hypernym of a specific term
     * @param term the word to get hypernym of.
     * @return all the hypernym of term.
     */
    public LinkedList<RankedString> getHypernym(String term){
        LinkedList<TreeNode> tn_now_list = Index.get(term);
        TreeNode upper;
        LinkedList<RankedString> hyper = new LinkedList<>();
        if(tn_now_list==null) {
            return hyper;
        }
        for (TreeNode tn_now:tn_now_list) {
            Integer distance = 1;
            upper = tn_now.parent;
            while (upper.ID != "ROOT") {
                ArrayList<String> terms = upper.term;
                for (String t : terms) {
                    hyper.add(new RankedString(t, distance));
                }
                upper = upper.parent;
                distance += 1;
            }
        }
        return hyper;
    }

    /**
     * Get the Hyponym of a specific term
     * @param term the word to get hyponym of.
     * @return all the hyponym of term.
     */
    public LinkedList<RankedString> getHyponym(String term){ // using BFS to easily get the distance of the words.
        LinkedList<TreeNode> tn_now_list = Index.get(term);
        LinkedList<RankedString> hypo = new LinkedList<>();
        if(tn_now_list == null){
            return hypo;
        }
        for(TreeNode tn:tn_now_list) { // for each node do the BFS
            Queue queue = new LinkedList<TreeNode>();
            Queue level = new LinkedList<Integer>(); // keep record of the level
            TreeNode child;
            Integer level_now = 0;
            Integer distance = 1;
            queue.add(tn);
            level.add(level_now);
            while (!queue.isEmpty()) {
                TreeNode now = (TreeNode) queue.remove();
                level_now = (Integer) level.remove(); // get value for current node.
                for (String id : now.children.keySet()) {
                    child = now.children.get(id);
                    queue.add(child);
                    for (String t : child.term) {
                        hypo.add(new RankedString(t, level_now + 1));
                    }
                    level.add(level_now + 1);
                }

            }
        }
        return hypo;
    }


    /**
     * Get the Hypo pairs in vocabulary
     *
     * @throws Exception
     */
    public void getHypoPairinVocab() throws Exception{
        HashMap<String,Set<String>> hypomap = new HashMap<>();
        String outfile = "/home/xinxin/AztecData/hypo.txt";
        PrintWriter pt = new PrintWriter(new FileOutputStream(outfile));
        Iterator<String> itr = vocabulary.iterator();
        int success = 0;
        while(itr.hasNext()){
            String term = itr.next();
            LinkedList<RankedString> hypo = getHyponym(term);
            Set<String> tmpres = new HashSet<>();
            if(hypomap.containsKey(term)){
                tmpres = hypomap.get(term);
            }
            for(int i =0;i< hypo.size();i++){
                RankedString ts = hypo.get(i);
                if(ts.getRank() == 1.0  && vocabulary.contains(ts.getString())){
                    tmpres.add(ts.getString());
                }
            }
            hypomap.put(term,tmpres);
        }
        for(String term: hypomap.keySet()){
            for(String shypo: hypomap.get(term)){
                pt.write(term+","+shypo+"\n");
                System.out.println(success++);
            }
        }
        System.out.println(success);
        pt.close();
    }


    /**
     * Get the Hypernym pairs in vocabulary
     *
     * @throws Exception
     */
    public void getHyperPairinVocab() throws Exception{
        HashMap<String,Set<String>> hypermap = new HashMap<>();
        String outfile = "/home/xinxin/AztecData/hyper2.txt"; // hyper2.txt the distance between two terms in graph is 2
        PrintWriter pt = new PrintWriter(new FileOutputStream(outfile));
        Iterator<String> itr = vocabulary.iterator();
        int success = 0;
        while(itr.hasNext()){
            String term = itr.next();
            LinkedList<RankedString> hypo = getHypernym(term);
            Set<String> tmpres = new HashSet<>();
            if(hypermap.containsKey(term)){
                tmpres = hypermap.get(term);
            }
            for(int i =0;i< hypo.size();i++){
                RankedString ts = hypo.get(i);
                if(ts.getRank() == 2.0  && vocabulary.contains(ts.getString())){
                    tmpres.add(ts.getString());
                }
            }
            hypermap.put(term,tmpres);
        }
        for(String term: hypermap.keySet()){
            for(String shypo: hypermap.get(term)){
                pt.write(term+","+shypo+"\n");
                System.out.println(success++);
            }
        }
        System.out.println(success);
        pt.close();
    }


    public static void main(String[] args) throws Exception{
        MeshTreeHierarchy MTH = new MeshTreeHierarchy();
        Tokenizer tkn = new Tokenizer();
        // test the code
        String infile = "/home/xinxin/AztecData/MeshTreeHierarchy.csv";
        MTH.BuildHierarchy(infile);
        MTH.loadvocab();
        System.out.println(vocabulary.size());
        MTH.getHyperPairinVocab();
//        //MTH.writeTable("D:/AztecSearch/Xinxin/data/MeshTreeHierarchy.data");
//        String term = "Digestive System Neoplasms";
//        LinkedList<String> tokens = tkn.tokenize(term); //token the term fisrt
//        term = String.join("_", tokens);
//        LinkedList<RankedString> results = new LinkedList<>();
//        results = MTH.getHypernym(term);
//        System.out.println("Hyper:");
//        for(RankedString rs:results){
//            System.out.print(rs.getString()+" ");
//            System.out.println(rs.getRank());
//        }
//        System.out.println("Hypo:");
//        results = MTH.getHyponym(term);
//        for(RankedString rs:results){
//            System.out.print(rs.getString()+" ");
//            System.out.println(rs.getRank());
//        }
    }
}
