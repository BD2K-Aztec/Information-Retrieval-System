package edu.ucla.cs.scai.aztec;

import java.util.ArrayList;

/**
 * The interface of AztecEntryProvider
 *
 * @author Giuseppe M. Mazzeo "mazzeo@cs.ucla.edu"
 * @author Xinxin Huang "xinxinh@gmail.com"
 * @author Zeyu Li "zyli@cs.ucla.edu"
 */
public interface AztecEntryProvider {

    public ArrayList<AztecEntry> load() throws Exception;

}
