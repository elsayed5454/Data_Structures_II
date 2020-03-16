package eg.edu.alexu.csd.filestructure.sort;

import java.util.ArrayList;
import java.util.Collections;

public class Sort<T extends Comparable<T>> implements ISort<T> {

    @Override
    public IHeap<T> heapSort(ArrayList<T> unordered) {

        Heap<T> unorder = new Heap<>();
        unorder.build(unordered);

        // Initialize array list
        ArrayList<T> initialArr = new ArrayList<>();
        for (int i = 0; i < unorder.size(); i++) {
            initialArr.add((T) (Integer) 0);
        }
        Heap<T> orderHeap = new Heap<>();
        orderHeap.build(initialArr);

        // Put the ordered nodes in the array list
        ArrayList<Node<T>> orderArr = orderHeap.getArr();
        for (int i = unorder.size() - 1; i >= 0; i--) {
            orderArr.get(i).setValue(unorder.extract());
        }
        return orderHeap;
    }

    @Override
    public void sortSlow(ArrayList<T> unordered) {

        if (unordered == null) {
            return;
        }

        // Bubble sort
        int n = unordered.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (unordered.get(j).compareTo(unordered.get(j + 1)) > 0) {

                    // Swap index j and j + 1
                    Collections.swap(unordered, j, j + 1);
                }
            }
        }

    }

    @Override
    public void sortFast(ArrayList<T> unordered) {

        if (unordered == null) {
            return;
        }
        mergeSort(unordered, 0, unordered.size() - 1, new ArrayList<>(unordered));
    }

    private void mergeSort(ArrayList<T> unordered, int l, int r, ArrayList<T> cloned) {

        if (l < r) {

            int mid = l + (r - l) / 2;
            mergeSort(unordered, l, mid, cloned);
            mergeSort(unordered, mid + 1, r, cloned);
            merge(unordered, l, mid, r, cloned);
        }
    }

    private void merge(ArrayList<T> unordered, int l, int mid, int r, ArrayList<T> cloned) {

        int low = l;
        int high = mid + 1;

        for (int i = l; i <= r; i++) {
            cloned.set(i, unordered.get(i));
        }

        while (low <= mid && high <= r) {

            if(cloned.get(low).compareTo(cloned.get(high)) < 0) {
                unordered.set(l, cloned.get(low));
                low++;
            }
            else {
                unordered.set(l, cloned.get(high));
                high++;
            }
            l++;
        }

        while (low <= mid) {
            unordered.set(l, cloned.get(low));
            l++;
            low++;
        }
    }
}
