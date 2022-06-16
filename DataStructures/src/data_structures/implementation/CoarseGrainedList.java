
package data_structures.implementation;

import data_structures.Sorted;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class CoarseGrainedList<T extends Comparable<T>> implements Sorted<T> {

    private Node headNode = new Node(null); //starts the list with a null node
    private ReentrantLock lock = new ReentrantLock(); //create lock for coarse-grained locking

    private class Node {

        private T data;
        private Node nxtNode;

        Node(T data) {
            this.data = data;
            nxtNode = null;
        }
    }

    public void add(T t) {

        Node prevNode = headNode;

        lock.lock(); //simple lock the entire thing for coarse grained
        try {

            Node currNode = prevNode.nxtNode;

            if(currNode != null) { //if not empty

                while (currNode.data.compareTo(t) < 0) {//find the position we will add it in

                    prevNode = currNode;

                    currNode = currNode.nxtNode;

                    if (currNode == null)
                        break;

                }
                //add it in
                Node node = new Node(t);
                prevNode.nxtNode = node;
                node.nxtNode = currNode;
            }
            else { //if empty, we put it after the "null" we have about

                Node node = new Node(t);
                prevNode.nxtNode = node;
                node.nxtNode = currNode;
                
            }  

        } finally {
            lock.unlock(); //unlock when done

        }
    }

    public void remove(T t) {

        lock.lock(); //again, lock entire thing
        try {

            Node prevNode = headNode;
            Node currNode = prevNode.nxtNode;
            
            while (currNode.data.compareTo(t) < 0) { 
                //iterate till you go past or you are at the node to be removed
                prevNode = currNode;
                currNode = currNode.nxtNode;
            }

            if(currNode.data.compareTo(t) == 0) {
                //remove it
                currNode = prevNode.nxtNode;

                prevNode.nxtNode = currNode.nxtNode;
            }
        } finally {
            lock.unlock(); //and unlock when done
        }
    }

    public ArrayList<T> toArrayList() {
        ArrayList<T> arrayList = new ArrayList<T>();
        Node currNode = headNode.nxtNode;//because headnode will always be null
        //to allow this code to work, this adaption was done
        while(currNode != null) {

            arrayList.add(currNode.data);
            currNode = currNode.nxtNode;
        }

        return arrayList;
    }


}
