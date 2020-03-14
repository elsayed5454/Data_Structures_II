package eg.edu.alexu.csd.filestructure.sort;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class Heap<T extends Comparable<T>> implements IHeap<T> {

    private ArrayList<INode<T>> arr;

    public Heap() {
        arr = new ArrayList<>();
    }

    @Override
    public INode<T> getRoot() {

        return arr.get(0);
    }

    @Override
    public int size() {
        return arr.size();
    }

    @Override
    public void heapify(INode<T> node) {

        // Getting left child and right child
        INode<T> leftChild = node.getLeftChild();
        INode<T> rightChild = node.getRightChild();

        // Assigning the node value as the node with largest element
        INode<T> maxNode = node;

        // Comparing the left and right children nodes to the parent
        if (leftChild != null && leftChild.getValue().compareTo(maxNode.getValue()) > 0) {
            maxNode = leftChild;
        }
        if (rightChild != null && rightChild.getValue().compareTo(maxNode.getValue()) > 0) {
            maxNode = rightChild;
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

        // Swapping the root and the last element in the tree
        Collections.swap(arr, 1, size());
        T root = arr.get(size() - 1);

        // Removing the last element then heapify the tree
        arr.remove(size() - 1);
        heapify(getRoot());
        return root;
    }

    @Override
    public void insert(T element) {

        // Add the element to the tree then heapify it
        arr.add(element);
        Collections.swap(arr, 1, size());
        heapify(getRoot());
    }

    @Override
    public void build(Collection<T> unordered) {

        ArrayList<T> temp = new ArrayList<>(unordered);
        INode<T> root = createNode(temp, 0);
        arr.add(root);

        int i = 0;
        while(i < temp.size()) {

            INode<T> parent = arr.get(i);
            if(parent.getLeftChild() != null) {
                arr.add(parent.getLeftChild());
            }
            if(parent.getRightChild() != null) {
                arr.add(parent.getRightChild());
            }
            i++;
        }

        for (i = (size()-1) / 2; i >= 0; i--) {
            heapify(arr.get(i));
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
                    createNode(unordered, index * 2 + 1), createNode(unordered, index * 2 + 2));
        }
    }

}
