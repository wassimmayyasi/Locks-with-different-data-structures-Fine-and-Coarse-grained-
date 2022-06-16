
package data_structures.implementation;

import data_structures.Sorted;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class CoarseGrainedTree<T extends Comparable<T>> implements Sorted<T> {

    private Node rootNode;
    private ReentrantLock lock = new ReentrantLock();

    private class Node {
        private Node leftChild;
        private Node rightChild;
        T data;

        Node(T data) {
            this.data = data;

            leftChild = null;
            rightChild = null;
        }
    }

    public void add(T t) {

        lock.lock();
        try {

            if(rootNode == null) 
                rootNode = new Node(t);
            
            else {
                Node currNode = rootNode;
                Node currNodeParent = null;

                while (currNode != null) { 
                //traverse through the tree to find the exact position the new added node will be in
                    currNodeParent = currNode;


                    if(t.compareTo(currNode.data) < 0) //go left child if the added node is smaller
                        currNode = currNode.leftChild;
                    
                    else  //if equal or greater, go right child
                        currNode = currNode.rightChild;
                    

                }

                Node addedNode = new Node(t);
                //this part to assign the node to its position, depend on left or right side
                if (t.compareTo(currNodeParent.data) < 0) 
                    currNodeParent.leftChild = addedNode;
                
                else 
                    currNodeParent.rightChild = 

addedNode;
        
            }
        } finally {
            lock.unlock();
        }

    }

    public void remove(T t) {

        lock.lock();
        try {

           if(rootNode != null) { //if not empty we do it
                
                Node currNode = rootNode; //starting at root
                Node currNodeParent = null;

                while (currNode != null && 

currNode.data.compareTo(t) != 0) { //find the node to be removed

                    currNodeParent = currNode;
                    if(t.compareTo(currNode.data) < 0) 
                        currNode = currNode.leftChild;
                    
                    else 
                        currNode = currNode.rightChild;
                    
                }

                if(currNode.leftChild == null && currNode.rightChild == null) { //LEAF NODE IS BEING REMOVED
                    
                    if(currNode == rootNode)  //root is the one and only element in tree
                        rootNode = null;
                    
                    else if(currNode == 

currNodeParent.leftChild) //a left node of parent being removed
                        currNodeParent.leftChild = null;
                    
                    else  //right node of parent being removed
                        currNodeParent.rightChild = null;
                    
                }

                else if(currNode.leftChild == null) { //node being removed has a right child
                   
                    if(currNode == rootNode) 
                        rootNode = currNode.rightChild;
                    
                    else if(currNode == currNodeParent.leftChild) 
                        currNodeParent.leftChild = currNode.rightChild;
                    
                    else 

                        currNodeParent.rightChild = currNode.rightChild;
                    
                }

                else if(currNode.rightChild == null) {    //node being removed has a left child
                    
                    if(currNode == rootNode) 
                        rootNode = currNode.leftChild;
                    
                    else if(currNode == currNodeParent.leftChild) 
                        currNodeParent.leftChild = currNode.leftChild;
                    
                    else 
                        currNodeParent.rightChild = currNode.leftChild;
                    
                }
                

                else { //node bieng removed has 2 children
                    Node newSuccessor = getDeletedNodesSuccessor(currNode);
                
                    if(currNode == rootNode) 
                        rootNode = newSuccessor;
                    
                    else if(currNode == currNodeParent.leftChild)
                        currNodeParent.leftChild = newSuccessor;
                    
                    else 
                        currNodeParent.rightChild = newSuccessor;

                    newSuccessor.leftChild = currNode.leftChild;
                }   

            }


        } finally {
            lock.unlock();
        }

    }    

    private Node getDeletedNodesSuccessor(Node deletedNode) {

        Node temporarySuccessor = deletedNode;
        Node temporarySucessorsParent = null;
        Node currNode = deletedNode.rightChild;
        //first we go the right child of the deleted node
        while(currNode != null) {
            //then we start going to the left children of the right child to reach the optimal sucessor
            temporarySucessorsParent = 

temporarySuccessor;
            temporarySuccessor = currNode;
            currNode = currNode.leftChild;

        }

        if(temporarySuccessor != deletedNode.rightChild) {
            //change the sucessor's previous parent its left to the successors right (if it has one)
            temporarySucessorsParent.leftChild = temporarySuccessor.rightChild;
            //change the successor's right child to replace the deleted node
            temporarySuccessor.rightChild = deletedNode.rightChild;
        }

        return temporarySuccessor;
    }

    // functions dealing with ArrayList conversion
    public ArrayList<T> toArrayList() {

    ArrayList<T> arrayList = new ArrayList<T>();
        addToList(rootNode, arrayList);
        return arrayList;
    }

    private void addToList(Node root, ArrayList<T> list) {

    if(root != null) {

    addToList(root.leftChild, list);
    list.add(root.data);
    addToList(root.rightChild, list);
    }
    }
}
