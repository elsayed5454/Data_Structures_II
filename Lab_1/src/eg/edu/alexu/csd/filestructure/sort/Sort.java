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
        if(unordered!=null) {
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
    }

    @Override
    public void sortFast(ArrayList<T> unordered) {

    }
}
