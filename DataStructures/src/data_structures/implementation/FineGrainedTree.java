
package data_structures.implementation;

import data_structures.Sorted;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class FineGrainedTree<T extends Comparable<T>> implements Sorted<T> {

    private Node rootNode;
    ReentrantLock headLock = new ReentrantLock();

    private class Node {

        private Node leftChild;
        private Node rightChild;
        T data;
        ReentrantLock lock; //individual node locks

        
        Node(T data) {

            this.data = data;
            leftChild = null;
            rightChild = null;
            lock = new ReentrantLock();

        }
    }

    public void add(T t) {

        headLock.lock();
        Node currNode = null;
        Node currNodeParent = null;

        try {

            if(rootNode == null) {

                rootNode = new Node(t);

                return;
            } 
        } finally {
            headLock.unlock();
        }
            
        currNode = rootNode;
        currNode.lock.lock();
        try {

        while (currNode != null) { 
            //traverse thro the tree to find the exact position the new added node will be in
                currNodeParent = currNode;
                if(t.compareTo(currNode.data) < 0) //go left child if the added node is smaller
                    currNode = currNode.leftChild;

                else //if equal or greater, go right child
                    currNode = currNode.rightChild;

                if(currNode != null) { //we lock next node, and unlock previous, only keeping 2 locked at a time
                currNode.lock.lock();
                currNodeParent.lock.unlock();
                }

            }

Node addedNode = new Node(t);
            //this part to assign the node to its position, depend on left or right side
            if (t.compareTo(currNodeParent.data) < 0) 
                currNodeParent.leftChild = addedNode;
            
            else 
                currNodeParent.rightChild = addedNode;
            

        } finally {
            //we unlock anything still locked
        if(currNode != currNodeParent && currNode != null) {
        currNode.lock.unlock();
        currNodeParent.lock.unlock();
        } 
            else 
        currNodeParent.lock.unlock();
        
        }
   
    }

public void remove(T t) {
    
    headLock.lock(); //lock in case of a head node removal or empty
    Node currNode;
    Node currNodeParent = null;

    try {


        if(rootNode == null) //if empty we leave
            return;

        rootNode.lock.lock();//else we lock and move on
        currNode = rootNode;

        try {
            //search for the node to be removed
            while (currNode != null && currNode.data.compareTo(t) != 0) { // find the node to be removed
                
                if (currNodeParent != null)
                    currNodeParent.lock.unlock();//in case of not the first node, it unlocks the previous
                    //to only keep 2 locked nodes at a time
                currNodeParent = currNode;
                if(t.compareTo(currNode.data) < 0)

                    currNode = currNode.leftChild;
                //traversing to the next node (left or right) depending if node searched for is greater or less than
                else
                    currNode = currNode.rightChild;
                
                if(currNode != null) 
                    currNode.lock.lock();//locks next node

            }

            if(currNode == null) //couldn't find
                return;
            
            if(currNode.leftChild == null && currNode.rightChild == null) { //LEAF NODE IS BEING REMOVED
                
                if(currNode == rootNode)  //root is the one and only element in tree

                    rootNode = null;

                else if(currNode == currNodeParent.leftChild)  //a left node of parent being removed
                    currNodeParent.leftChild = null;

                else  //right node of parent being removed
                    currNodeParent.rightChild = null;

            }

            else if(currNode.leftChild == null) { //node being removed has a right child
               
                if(currNode == rootNode)
                    rootNode = currNode.rightChild;

                else if(currNode == currNodeParent.leftChild)
                    currNodeParent.leftChild = 

currNode.rightChild;

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

            else { //node being removed has 2 children
                
                Node newSuccessor = getDeletedNodesSuccessor(currNode);

                if(currNode == rootNode)
                    rootNode = newSuccessor;

                else if(currNode == currNodeParent.leftChild)
                    currNodeParent.leftChild = newSuccessor;

                else
                    currNodeParent.rightChild = newSuccessor;

                newSuccessor.leftChild = 

currNode.leftChild;
            }

        } finally {
            //unlocks all nodes currently locked
            if(currNode != null) 
                currNode.lock.unlock();
            
            if(currNode != currNodeParent && currNodeParent != null)
                currNodeParent.lock.unlock();
        }
    } finally {
        headLock.unlock();
    }
}

    private Node getDeletedNodesSuccessor(Node deletedNode) {
        
        Node temporarySuccessor = 

deletedNode;
        Node temporarySucessorsParent = null;
        Node currNode = deletedNode.rightChild;
        //first we go the right child of the deleted node
        while(currNode != null) {
            //then we start going to the left children of the right child to reach the optimal sucessor
            temporarySucessorsParent = temporarySuccessor;
            temporarySuccessor = currNode;
            currNode = currNode.leftChild;

        }

        if(temporarySuccessor != deletedNode.rightChild) {
            //change the sucessor's previous parent its left to the successors right (if it has one)
            temporarySucessorsParent.leftChild = 

temporarySuccessor.rightChild;
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
