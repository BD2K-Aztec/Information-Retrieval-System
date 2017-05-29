package edu.ucla.cs.scai.aztec;

/**
 * Output Possible Duplicates of AztecEntries.
 *
 * @author Giuseppe M. Mazzeo "mazzeo@cs.ucla.edu"
 */
public class PossibleDuplicates {

    private AztecEntry e1, e2;
    private String reason;

    public PossibleDuplicates(AztecEntry e1, AztecEntry e2, String reason) {
        this.e1 = e1;
        this.e2 = e2;
        this.reason = reason;
    }

    public AztecEntry getE1() {
        return e1;
    }

    public void setE1(AztecEntry e1) {
        this.e1 = e1;
    }

    public AztecEntry getE2() {
        return e2;
    }

    public void setE2(AztecEntry e2) {
        this.e2 = e2;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public String toString() {
        return e1.getName() + " (id=" + e1.getId()+ ") could be a duplicate of " + e2.getName()+ " (id=" + e2.getId()+ ") - reason: " + reason;
    }

}
