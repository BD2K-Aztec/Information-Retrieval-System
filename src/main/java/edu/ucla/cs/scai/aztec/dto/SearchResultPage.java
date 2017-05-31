/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucla.cs.scai.aztec.dto;

import edu.ucla.cs.scai.aztec.AztecEntry;
import java.util.ArrayList;

/**
 * The Page of the Search Results
 *
 * @author Giuseppe M. Mazzeo "mazzeo@cs.ucla.edu"
 * @author Xinxin Huang "xinxinh@gmail.com"
 * @author Zeyu Li "zyli@cs.ucla.edu"
 */
public class SearchResultPage {

    private ArrayList<AztecEntry> entries;
    private int resultCount;

    /**
     * Constructor class.
     *
     * @param entries     All the AztecEntries of search result.
     * @param resultCount Number of all the returned entries.
     */
    public SearchResultPage(ArrayList<AztecEntry> entries, int resultCount) {
        this.entries = entries;
        this.resultCount = resultCount;
    }

    /**
     * Get all the searched entries.
     *
     * @return All the searched entries.
     */
    public ArrayList<AztecEntry> getEntries() {
        return entries;
    }

    /**
     * Set the entries.
     *
     * @param entries The entries to be set.
     */
    public void setEntries(ArrayList<AztecEntry> entries) {
        this.entries = entries;
    }

    /**
     * Get the number of the result.
     *
     * @return the number of the result.
     */
    public int getResultCount() {
        return resultCount;
    }

    /**
     * Set the number of the result.
     *
     * @param resultCount the number of searched result to be set.
     */
    public void setResultCount(int resultCount) {
        this.resultCount = resultCount;
    }
}
