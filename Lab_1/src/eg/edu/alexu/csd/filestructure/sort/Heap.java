package eg.edu.alexu.csd.filestructure.sort;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class Heap<T extends Comparable<T>> implements IHeap<T> {

    private ArrayList<INode<T>> arr;
    int heapSize;
    int counter =0;
    public Heap() {

        arr = new ArrayList<>();
        heapSize=0;
    }

    @Override
    public INode<T> getRoot() {

        return arr.get(0);
    }

    @Override
    public int size() {
        return heapSize;
    }

    @Override
    public void heapify(INode<T> node) {

        // Getting left child and right child
        INode<T> leftChild = node.getLeftChild();
        INode<T> rightChild = node.getRightChild();
        counter++;

        // Assigning the node value as the node with largest element
        INode<T> maxNode = node;


        // Comparing the left and right children nodes to the parent
        if (leftChild != null ) {
            System.out.println(counter+"   leftchild   "+leftChild.getValue() );
            if( leftChild.getValue().compareTo(maxNode.getValue()) > 0) {
                maxNode = leftChild;

            }
        }
        if (rightChild != null ) {
            System.out.println(counter+"   rightchild   "+rightChild.getValue() );
            if(rightChild.getValue()!=null) {
                if (rightChild.getValue().compareTo(maxNode.getValue()) > 0) {
                    maxNode = rightChild;

                }
            }
        }

        // Swapping the maximum element with the parent element
        if (maxNode != node) {
            T temp = node.getValue();
            node.setValue(maxNode.getValue());
            maxNode.setValue(temp);
            heapify(maxNode);
        }
    }

    @Override
    public T extract() {
        if(!arr.isEmpty()) {
            System.out.println("extract");
            // Swapping the root and the last element in the tree
            T temp = getRoot().getValue();
            if(getRoot()==null){System.out.println("nulllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllll");}
            getRoot().setValue(arr.get(heapSize - 1).getValue());
            arr.get(heapSize - 1).setValue(temp);
            if (heapSize > 1) {
                if ((heapSize - 1) % 2 == 0) {
                    Node<T> nod= new Node<T>(arr.get((heapSize - 2)/2).getValue(), arr.get(((heapSize - 2)/2)/2), arr.get((heapSize - 2)/2).getLeftChild());
                    nod.setValue(arr.get((heapSize - 2)/2).getValue());
                    arr.set((heapSize - 2) / 2,nod);
                } else {
                    Node<T> nodt=new Node<T>(arr.get((heapSize - 1)/2).getValue(), arr.get(((heapSize - 1)/2)/2));
                    nodt.setValue(arr.get((heapSize - 1)/2).getValue());
                    arr.set((heapSize - 1) / 2,nodt);
                }
            }
            heapSize = heapSize - 1;
            // Removing the last element then heapify the tree
            heapify(getRoot());
            if(counter>0){
                counter=0;
            }
            System.out.println("root after extract heapify"+getRoot().getValue());
            return temp;
        }
        else {
            return null;
        }
    }

    @Override
    public void insert(T element) {
        System.out.println(element);
        Node newNode;
        System.out.println("array size"+arr.size()+"heap size"+heapSize);

        if((arr.size()==0||heapSize==0)&&!(arr.size()<0)){
            newNode = new Node<T>(element);
            newNode.setValue(element);
        }
        else {
            newNode = new Node<T>(element, arr.get(heapSize - 1));
            newNode.setValue(element);
        }
        arr.add(heapSize,newNode);
        if(heapSize>=1) {
            if ((heapSize - 1) % 2 == 0) {

                Node<T> tmp = new Node<T>(arr.get(heapSize / 2).getValue(), arr.get(heapSize / 2).getParent(), newNode);
                tmp.setValue(arr.get(heapSize / 2).getValue());
                arr.set(heapSize / 2, tmp);

            } else {
                Node<T> tmpt = new Node<T>(arr.get(heapSize / 2).getValue(), arr.get(heapSize / 2).getParent(), arr.get(heapSize / 2).getLeftChild(), newNode);
                tmpt.setValue(arr.get(heapSize / 2).getValue());
                arr.set(heapSize / 2, tmpt);
            }
        }
        heapSize=heapSize+1;
        heapify(getRoot());
        if(counter>0){
            counter=0;
        }
        System.out.println("root after insert heapify"+getRoot().getValue());
    }

    @Override
    public void build(Collection<T> unordered) {
        if(unordered!=null&&!unordered.isEmpty()) {
            ArrayList<T> temp = new ArrayList<>(unordered);
            INode<T> root = createNode(temp, 0);
            arr.add(root);

            int i = 0;
            while (i < temp.size()) {

                INode<T> parent = arr.get(i);
                if (parent.getLeftChild() != null) {
                    arr.add(parent.getLeftChild());
                }
                if (parent.getRightChild() != null) {
                    arr.add(parent.getRightChild());
                }
                i++;
            }
            if(!arr.isEmpty()) {
                for (i = (size() - 1) / 2; i >= 0; i--) {
                    heapify(arr.get(i));
                }
            }
        }
    }

    private INode<T> createNode(ArrayList<T> unordered, int index) {

        // Check if index is out of bounds
        if(index >= unordered.size()) {
            return null;
        }

        // Create nodes recursively
        if(index == 0) {
            return new Node<>(unordered.get(index), null, createNode(unordered, index * 2 + 1),
                    createNode(unordered, index * 2 + 2));
        }
        else {
            return new Node<>(unordered.get(index), createNode(unordered, (index - 1) / 2),
                    createNode(unordered, index * 2+1 ), createNode(unordered, index * 2 + 2));
        }
    }

}
