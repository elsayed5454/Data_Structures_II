package eg.edu.alexu.csd.filestructure.sort;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class Heap<T extends Comparable<T>> implements IHeap<T> {

    private ArrayList<T> arr;

    public Heap() {
        arr = new ArrayList<>();
        arr.add(null);
    }

    @Override
    public INode<T> getRoot() {

        // The tree is 1 indexed
        return new Node<>( 1);
    }

    @Override
    public int size() {
        return arr.size();
    }

    @Override
    public void heapify(INode<T> node) {

        // Getting left child index and right child index
        int index = (int)(Object)node.getValue();
        int leftIndex = index * 2;
        int rightIndex = index * 2 + 1;

        // Assigning the node index as index of maximum element
        int max = index;

        // Comparing the left and right children nodes to the parent
        if(leftIndex < size() && arr.get(leftIndex).compareTo(arr.get(max)) > 0) {
            max = leftIndex;
        }
        if(rightIndex < size() && arr.get(rightIndex).compareTo(arr.get(max)) > 0) {
            max = rightIndex;
        }

        // Swapping the maximum element with the parent element
        if(max != index) {
            Collections.swap(arr, index, max);
            heapify(new Node<>(max));
        }
    }

    @Override
    public T extract() {

        // Swapping the root and the last element in the tree
        Collections.swap(arr, 1, size() - 1);
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
        Collections.swap(arr, 1, size() - 1);
        heapify(getRoot());
    }

    @Override
    public void build(Collection<T> unordered) {

        arr = new ArrayList<>(unordered);
        for(int i = (size() - 1) / 2; i >= 1; i--) {
            heapify(new Node<>(i));
        }
    }

}
