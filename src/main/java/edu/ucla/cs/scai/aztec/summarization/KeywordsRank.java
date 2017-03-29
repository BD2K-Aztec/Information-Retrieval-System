package edu.ucla.cs.scai.aztec.summarization;

import com.mysql.fabric.xmlrpc.base.MethodCall;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.ling.tokensregex.types.Expressions;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.util.CollectionFactory;
import edu.stanford.nlp.util.SystemUtils;
import edu.stanford.nlp.util.TypesafeMap;
import edu.ucla.cs.scai.aztec.similarity.Tokenizer;
import edu.ucla.cs.scai.aztec.similarity.WeightedAbs;
import edu.ucla.cs.scai.aztec.textexpansion.TextParser;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import net.sf.extjwnl.JWNLException;
import sun.awt.image.ImageWatched;

/**
 *
 * @author Giuseppe M. Mazzeo <mazzeo@cs.ucla.edu>
 */
public class KeywordsRank {

    WeightedEdgeGraph g;
    HashMap<Integer, String> keywords = new HashMap<>();
    HashMap<String, Integer> keywordIds = new HashMap<>();
    double[] rank;
    Integer[] ordered;

    /*build dependency graph based on the grammar structure
    calculate min distance between each pair of node*/
    public void dependencyGraph(List<String> input) throws JWNLException, FileNotFoundException {
        LexicalizedParser lp = LexicalizedParser.loadModel();
        TreebankLanguagePack tlp = lp.getOp().langpack();
        GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
        Tokenizer tk = new Tokenizer();
        //List<CoreLabel> tokens = tk.originTokenizer(sen);
        int idx1,idx2;
        int n = 0;
        String[] tokens = input.toArray(new String[input.size()]);
        for (String t : tokens) {
            System.out.println(t);
            keywords.put(n, t);
            keywordIds.put(t, n);
            n++;
        }
        Tree tree = lp.apply(Sentence.toWordList(tokens));
        GrammaticalStructure gs = gsf.newGrammaticalStructure(tree);
        Collection tdl = gs.typedDependenciesCCprocessed();
        System.out.println(tdl);
//        int n = 0;
//        for (CoreLabel t : tokens) {
//            System.out.println(t.lemma());
//            keywords.put(n, t.lemma());
//            keywordIds.put(t.lemma(), n);
//            n++;
//        }
//        Tree tree = lp.parse(tokens);
//        GrammaticalStructure gs = gsf.newGrammaticalStructure(tree);
//        Collection tdl = gs.typedDependenciesCCprocessed();
//        System.out.println(tdl);
        g = new WeightedEdgeGraph(n);
        for(Iterator<TypedDependency> it = tdl.iterator(); it.hasNext();) {
            TypedDependency dc = it.next();
            //dc.dep();
            if (dc.reln().toString() == "root" || dc.reln().toString() == "cc"||dc.reln().toString() == "case") {
                continue;
            }
            idx1 = keywordIds.get(dc.gov().value());
            idx2 = keywordIds.get(dc.dep().value());
            g.setWeight(idx1, idx2, 1.0); // unweighted undirected graph
        }
        double[][] dis = g. computeShortestDis();
//        for(int i = 0;i<g.weights.length;i++){
//            for(int j =0;j<n;j++){
//                System.out.print(dis[i][j] == Integer.MAX_VALUE? -1: dis[i][j]);
//                System.out.print(' ');
//            }
//            System.out.println();
//        }
        for(int i = 0;i<g.weights.length;i++){
            System.out.println(Arrays.toString(g.weights[i]));
        }
        rank = g.computeNodeRank(0.85, 1, 0.001);
        ordered = new Integer[n];
        for (int i = 0; i < n; i++) {
            ordered[i] = i;
        }
        Arrays.sort(ordered, new RankComparator(rank));
        Arrays.sort(rank);
        System.out.println(Arrays.toString(rank));
        System.out.println(Arrays.toString(ordered));

//            SemanticGraph dependencies = new SemanticGraph(tdl);
//            dependencies.prettyPrint();


    }

    public KeywordsRank(){

    }

    public KeywordsRank(String text, int... windowSizes) throws JWNLException, FileNotFoundException, IOException {
        //Tokenizer tokenizer = new Tokenizer();
        TextParser TP = new TextParser();
        //LinkedList<String> tokens = tokenizer.tokenize(text);
        LinkedList<String> tokens = TP.queryParser(text);
        HashSet<String> distinctTokens = new HashSet<>(tokens);
        int n = 0;
        for (String t : distinctTokens) {
            keywords.put(n, t);
            keywordIds.put(t, n);
            n++;
        }
        g = new WeightedEdgeGraph(n);
        for (int windowSize : windowSizes) {
            if (windowSize > tokens.size()) {
                windowSize = tokens.size();
            }
            LinkedList<Integer> window = new LinkedList<>();
            Iterator<String> it = tokens.iterator();
            //init window
            int i = 0;
            for (; i < windowSize; i++) {
                String w = it.next();
                int idw = keywordIds.get(w);
                int j = 0;
                for (int idw2 : window) {
                    g.addWeight(idw, idw2, 1.0 / (i - j));
                    j++;
                }
                window.addLast(idw);
            }
            //advance window
            while (it.hasNext()) {
                String w = it.next();
                int idw = keywordIds.get(w);
                window.removeFirst();
                int j = i - windowSize + 1; //i is increase from previous iteration
                for (int idw2 : window) {
                    g.addWeight(idw, idw2, 1.0 / (i - j));
                    j++;
                }                
                window.addLast(idw);
                i++;
            }
        }
        rank = g.computeNodeRank(0.85, 1, 0.001);
        ordered = new Integer[n];
        for (int i = 0; i < n; i++) {
            ordered[i] = i;
        }
        Arrays.sort(ordered, new RankComparator(rank));
        Arrays.sort(rank);
        System.out.println(Arrays.toString(rank));
    }

    //returns the keywords with the top-k rank
    public List<String> topKeywords(Integer k) {
        if (k == null) {
            return topKeywords();
        }
        if (k > keywords.size()) {
            k = keywords.size();
        }
        LinkedList<String> res = new LinkedList<>();
        for (int i = 0; i < k; i++) {
            res.add(keywords.get(ordered[i]));
        }
        return res;
    }

    public List<String> topKeywords() {
        LinkedList<String> res = new LinkedList<>();
        double minRank = rank[ordered[0]] * 0.9;
        int i = 0;
        while (i < ordered.length && rank[ordered[i]] >= minRank) {
            res.add(keywords.get(ordered[i]));
            i++;
        }
        return res;
    }

    public List<RankedString> topRankedKeywords(Integer k) {
        if (k == null) {
            return topRankedKeywords();
        }
        if (k > keywords.size()) {
            k = keywords.size();
        }
        LinkedList<RankedString> res = new LinkedList<>();
        for (int i = 0; i < k; i++) {
            res.add(new RankedString(keywords.get(ordered[i]), rank[ordered[i]]));
        }
        return res;

    }

    public List<RankedString> topRankedKeywords() {
        LinkedList<RankedString> res = new LinkedList<>();
        double minRank = (rank[ordered[0]]-rank[ordered[ordered.length-1]]) * 0.2+rank[ordered[ordered.length-1]];// change from 0.9 to 0.6
        int i = 0;
        while (i < ordered.length && rank[ordered[i]] >= minRank) {
            res.add(new RankedString(keywords.get(ordered[i]), rank[ordered[i]]));
            i++;
        }
        return res;
    }

    class RankComparator implements Comparator<Integer> {

        double[] rank;

        public RankComparator(double[] rank) {
            this.rank = rank;
        }

        @Override
        public int compare(Integer o1, Integer o2) {
            return Double.compare(rank[o2], rank[o1]);
        }
    }

    public static void main(String[] args) throws JWNLException, FileNotFoundException, IOException{
//        String input = "We introduce Sailfish, a computational method for quantifying the abundance of previously annotated RNA isoforms from RNA-seq data. "
//                +"Because Sailfish entirely avoids mapping reads, a time-consuming step in all current methods, "
//                +"it provides quantification estimates much faster than do existing approaches (typically 20 times faster) without loss of accuracy. "
//                +"By facilitating frequent reanalysis of data and reducing the need to optimize parameters, "
//                +"Sailfish exemplifies the potential of lightweight algorithms for efficiently processing sequencing reads.\n";
         String input = "Readseq reads and converts biosequences between a selection of common biological sequence formats, including EMBL, GenBank and fasta sequence formats. ";
//         String input = "Prediction of interacting protein residues by identifying co-evolving pairs of aminoacids from an alignment of protein sequences.";
//          String input = "Genomic technologies allow laboratories to produce large-scale data sets, either through the use of next-generation sequencing or microarray platforms. To explore these data sets and obtain maximum value from the data, researchers view their results alongside all the known features of a given reference genome. To study transcriptional changes that occur under a given condition, researchers search for regions of the genome that are differentially expressed between different experimental conditions. In order to identify these regions several algorithms have been developed over the years, along with some bioinformatic platforms that enable their use. However, currently available applications for comparative microarray analysis exclusively focus on changes in gene expression within known transcribed regions of predicted protein-coding genes, the changes that occur in non-predictable genetic elements, such as non-coding RNAs. Here, we present a web application for the visualization of strand-specific tiling microarray or next-generation sequencing data that allows customized detection of differentially expressed regions all along the genome in an unspecific manner, that allows identification of all RNA sequences, predictable or not. Availability and implementation: The web application is freely accessible at";
//        String[] text = new String[]{"In this paper we present SYST, a question_answering (QA) system over RDF cubes.",
//                "The system first tags chunks of text with elements of the knowledge base, and then leverages the well-defined structure of data cubes to create the SPARQL query from the tags.",
//                "For each class of questions with the same structure a SPARQL template is defined.",
//                "The correct template is chosen by using a set of regular-expression-like regex-like patterns, based on both syntactical and semantic features of the tokens extracted from the question.",
//                "Preliminary results are encouraging and suggest a number of improvements.",
//                "SYST can currently provide a correct answer to 51 of the 100 questions of the training set."};
//        List<String> input = new LinkedList<>();
//        for (String s : text) {
//            input.add(s);
//        }
        TextParser tp = new TextParser();
        List<String> tokens = tp.queryParser(input);
        System.out.println(tokens.toString());
        KeywordsRank tr = new KeywordsRank();
        tr.dependencyGraph(tokens);
        List<String> res = tr.topKeywords(16);
        System.out.println(res);
        KeywordsRank tr2 = new KeywordsRank(input,5);
        List<String> res2 = tr2.topKeywords(17);
        System.out.println(res2);
//        KeywordsRank kr = new KeywordsRank(test, 10);
//        List<RankedString> kw = kr.topRankedKeywords(20);
//        for (RankedString s : kw) {
//            System.out.println(s);
//        }
    }
}
