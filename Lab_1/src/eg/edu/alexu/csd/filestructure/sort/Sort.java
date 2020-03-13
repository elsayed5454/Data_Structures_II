package eg.edu.alexu.csd.filestructure.sort;

import java.util.ArrayList;
import java.util.Collections;

public class Sort<T extends Comparable<T>> implements ISort<T>{
    @Override
    public IHeap<T> heapSort(ArrayList<T> unordered) {
        Heap<T> ordered = new Heap<>();
        ordered.build(unordered);
        Heap<T> cloned = new Heap<>();
        for(int i = 1; i < ordered.size(); i++) {
            cloned.insert(ordered.extract());
        }
        return cloned;
    }

    @Override
    public void sortSlow(ArrayList<T> unordered) {

        // Bubble sort
        int n = unordered.size();
        for (int i = 0; i < n-1; i++) {
            for (int j = 0; j < n-i-1; j++) {
                if (unordered.get(j).compareTo(unordered.get(j + 1)) > 0) {

                    // Swap index j and j + 1
                    Collections.swap(unordered, j, j + 1);
                }
            }
        }
    }

    @Override
    public void sortFast(ArrayList<T> unordered) {

        // Merge sort
        sort(unordered, 0, unordered.size() - 1);
    }

    // Main function that sorts arr[l..r] using
    // merge()
    private void sort(ArrayList<T> unordered, int l, int r) {
        if (l < r)
        {
            // Find the middle point
            int mid = (l+r)/2;

            // Sort first and second halves
            sort(unordered, l, mid);
            sort(unordered , mid + 1, r);

            // Merge the sorted halves
            merge(unordered, l, mid, r);
        }
    }

    private void merge(ArrayList<T> unordered, int l, int m, int r) {

        // Find sizes of two subarrays to be merged
        int n1 = m - l + 1;
        int n2 = r - m;

        /* Create temp arrays */
        int L[] = new int [n1];
        int R[] = new int [n2];

        /*Copy data to temp arrays*/
        for (int i=0; i<n1; ++i)
            L[i] = arr[l + i];
        for (int j=0; j<n2; ++j)
            R[j] = arr[m + 1+ j];


        /* Merge the temp arrays */

        // Initial indexes of first and second subarrays
        int i = 0, j = 0;

        // Initial index of merged subarry array
        int k = l;
        while (i < n1 && j < n2)
        {
            if (L[i] <= R[j])
            {
                arr[k] = L[i];
                i++;
            }
            else
            {
                arr[k] = R[j];
                j++;
            }
            k++;
        }

        /* Copy remaining elements of L[] if any */
        while (i < n1)
        {
            arr[k] = L[i];
            i++;
            k++;
        }

        /* Copy remaining elements of R[] if any */
        while (j < n2)
        {
            arr[k] = R[j];
            j++;
            k++;
        }
    }
}
