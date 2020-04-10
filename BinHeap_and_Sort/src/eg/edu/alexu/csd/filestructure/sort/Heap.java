package eg.edu.alexu.csd.filestructure.sort;

import java.util.ArrayList;
import java.util.Collection;

public class Heap<T extends Comparable<T>> implements IHeap<T> {

    private ArrayList<Node<T>> arr;

    public Heap() {

        arr = new ArrayList<>();
    }

    @Override
    public INode<T> getRoot() {

        if (size() != 0) {
            return arr.get(0);
        }
        return null;
    }

    @Override
    public int size() {
        return arr.size();
    }

    @Override
    public void heapify(INode<T> node) {

        if (node == null) {
            return;
        }

        // Getting left child and right child
        INode<T> leftChild = node.getLeftChild();
        INode<T> rightChild = node.getRightChild();

        // Assigning the node value as the node with largest element
        INode<T> maxNode = node;

        // Comparing the left and right children nodes to the parent
        if (leftChild != null) {
            if (leftChild.getValue().compareTo(maxNode.getValue()) > 0) {
                maxNode = leftChild;
            }
        }

        if (rightChild != null) {
            if (rightChild.getValue().compareTo(maxNode.getValue()) > 0) {
                maxNode = rightChild;
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

        if (arr.isEmpty()) {
            return null;
        }

        // Swapping the root value and the last node value
        T rootValue = getRoot().getValue();
        T tmp = arr.get(size() - 1).getValue();
        getRoot().setValue(tmp);

        // Setting the removed child to null in parent node
        Node<T> parent = (Node<T>) arr.get(size() - 1).getParent();

        // If the removed node is the root
        if (parent != null) {

            if (parent.getRightChild() != null) {
                parent.setRightChild(null);

            } else {
                parent.setLeftChild(null);
            }
        }

        // Removing the last node then heapify the tree
        arr.remove(size() - 1);
        heapify(getRoot());

        return rootValue;
    }

    @Override
    public void insert(T element) {

        if (element == null) {
            return;
        }

        Node<T> newNode = new Node<>(element);
        arr.add(newNode);

        // If the new node is not the only node
        if (size() != 1) {

            // Setting links between new node and its parent
            int i = size() - 1;
            Node<T> parent = arr.get((i - 1) / 2);
            newNode.setParent(parent);
            if (parent.getLeftChild() == null) {
                parent.setLeftChild(newNode);
            } else {
                parent.setRightChild(newNode);
            }

            // Swapping new node value with parent value
            Node<T> node = newNode;
            while (parent != null && node.getValue().compareTo(parent.getValue()) > 0) {

                T tmp = parent.getValue();
                parent.setValue(node.getValue());
                node.setValue(tmp);

                node = parent;
                parent = (Node<T>) parent.getParent();
            }
        }
    }

    @Override
    public void build(Collection<T> unordered) {

        if (unordered == null || unordered.isEmpty()) {
            return;
        }

        ArrayList<T> unorder = new ArrayList<>(unordered);

        for (T t : unorder) {
            arr.add(new Node<>(t));
        }

        for (int i = 0; i < arr.size(); i++) {

            if (i * 2 + 1 < arr.size()) {
                arr.get(i).setLeftChild(arr.get(i * 2 + 1));
            }
            if (i * 2 + 2 < arr.size()) {
                arr.get(i).setRightChild(arr.get(i * 2 + 2));
            }
            if (i != 0) {
                arr.get(i).setParent(arr.get((i - 1) / 2));
            }
        }

        if (!arr.isEmpty()) {
            for (int i = (arr.size() - 1) / 2; i >= 0; i--) {
                heapify(arr.get(i));
            }
        }
    }

    public ArrayList<Node<T>> getArr() {
        return arr;
    }

}
