package edu.ucla.cs.scai.aztec.textexpansion;
import java.util.*;

/**
 * The class of TreeNode Used in MeshTreeHierarchy.
 *
 * @author Xinxin Huang "xinxinh@gmail.com" on 9/27/2016.
 */
public class TreeNode {
    TreeNode parent;
    String ID;
    ArrayList<String> term;
    HashMap<String, TreeNode> children;

    /**
     * Constructor method
     */
    public TreeNode(){
        this.children = new HashMap<>();
    }


    /**
     * Add a new child in to the TreeNode
     * @param id the id of the Node
     * @param node the node
     */
    public void addChildren(String id,TreeNode node){
        this.children.put(id,node);
    }

    /**
     * Get the children of a node.
     * @return the List of children of the nodes.
     */
    public HashMap<String, TreeNode> getChildren(){
        return this.children;
    }

    /**
     * Set the node of a tree.
     * @param ID the id of the node.
     * @param term the terms in the node.
     */
    public void setTreeNode(String ID, ArrayList<String> term){
        this.ID = ID;
        this.term = term;
    }
}
