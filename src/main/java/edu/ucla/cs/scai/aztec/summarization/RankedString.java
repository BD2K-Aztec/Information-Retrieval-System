/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucla.cs.scai.aztec.summarization;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * The class of the ranked string
 * @author Giuseppe M. Mazzeo <mazzeo@cs.ucla.edu>
 */
public class RankedString implements Externalizable, Comparable<RankedString> {

    String string;
    double rank;

    public RankedString() {

    }

    /**
     * Constructor
     * @param string the content of the string
     * @param rank the rank of the string
     */
    public RankedString(String string, double rank) {
        this.string = string;
        this.rank = rank;
    }

    /**
     * Get the string of the rankedstring
     * @return the content of the string
     */
    public String getString() {
        return string;
    }

    /**
     * Set the string of the ranked string
     * @param string the string to be set
     */
    public void setString(String string) {
        this.string = string;
    }

    /**
     * Getter of the rank
     * @return the rank of a ranked string
     */
    public double getRank() {
        return rank;
    }

    /**
     * Set the rank of a ranked string
     * @param rank the rank of a ranked string
     */
    public void setRank(double rank) {
        this.rank = rank;
    }

    /**
     * The comparison of 'this' rank of another rank of 'o'
     * @param o the other object to compare rank with
     * @return the comparison of this and o rank
     */
    @Override
    public int compareTo(RankedString o) {
        return Double.compare(o.rank, rank);
    }

    /**
     * write the object to external files
     * @param out the output object to use.
     * @throws IOException
     */
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(string);
        out.writeDouble(rank);
    }

    /**
     * read from external resources
     * @param in the input object to use
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        string = (String) in.readObject();
        rank = in.readDouble();
    }

    /**
     *  Convert to string.
     * @return the string that is converted.
     */
    @Override
    public String toString() {
        return string + " " + rank;
    }

}
