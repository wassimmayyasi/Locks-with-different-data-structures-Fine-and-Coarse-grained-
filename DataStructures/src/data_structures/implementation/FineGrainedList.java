
package data_structures.implementation;

import data_structures.Sorted;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class FineGrainedList<T extends Comparable<T>> implements Sorted<T> {

    private Node headNode = new Node(null); //starts the list with a null node

    private class Node {

        private T data;
        private Node nxtNode;
        private ReentrantLock lock; //individual node locks

        Node(T data) {


            this.data = data;
            nxtNode = null;
            lock = new ReentrantLock();

        }
    }

    public void add(T t) {
        //locks headNode to start with fine-grained locking
        headNode.lock.lock();

        Node prevNode = headNode;
        try {

            Node currNode = prevNode.nxtNode;

            if (currNode != null) {

                currNode.lock.lock(); //lock only if a currNode exists


                try {

                    while (currNode.data.compareTo(t) < 0) { 
                    //find the position it needs to be in
                        prevNode.lock.unlock(); //keep only 2 locks at a time, unlock the previous one
                        prevNode = currNode;
                        currNode = currNode.nxtNode;
                        if (currNode == null)
                            break;

                        currNode.lock.lock(); //lock next node

                    }

                    // place node in the right spot
                    Node node = new Node(t);
                    prevNode.nxtNode = node;
                    node.nxtNode = currNode;



                } finally {

                    if (currNode != null)
                        currNode.lock.unlock();
                }
            } else { //if no nodes in the list, creates one after the null created above

                Node node = new Node(t);
                prevNode.nxtNode = node;
                node.nxtNode = currNode;

            }



        } finally {
            prevNode.lock.unlock();
        }


    }

    public void remove(T t) {

            Node prevNode = null, currNode;

            headNode.lock.lock(); //again starts with locking the head

            try {

                prevNode = headNode;
                currNode = prevNode.nxtNode;

                currNode.lock.lock(); //lock next node after head

                try {

                    while (currNode.data.compareTo(t) < 0) {

                        //loop til you either go past the node we want removed, or when we're at it
                        prevNode.lock.unlock(); //unlock previous node to keep only 2 locked node at a time
                        prevNode = currNode;
                        currNode = currNode.nxtNode;
                        currNode.lock.lock(); //and lock the next one

                    }

                    if(currNode.data.compareTo(t) == 0) {
                        //if the node to be removed exists, we "remove" it
                        currNode = prevNode.nxtNode;
                        prevNode.nxtNode = currNode.nxtNode;

                    }

                } finally { 
                    currNode.lock.unlock();
                }
                // unlock the node we're currently at and the one before it
            } finally {
                prevNode.lock.unlock();
            }

    }


    public ArrayList<T> toArrayList() {

        ArrayList<T> arrayList = new ArrayList<T>();
        Node currNode = headNode.nxtNode; //because headnode will always be null
        //to allow this code to work, this adaption was done
        while(currNode != null) {

            arrayList.add(currNode.data);

            currNode = currNode.nxtNode;
        }

        return arrayList;
    }

}
