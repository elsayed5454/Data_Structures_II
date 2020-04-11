package eg.edu.alexu.csd.filestructure.redblacktree;

public class RedBlackTree<T extends Comparable<T>, V> implements IRedBlackTree<T, V> {

    INode<T, V> root;
    int size;

    public RedBlackTree() {
        root = null;
        size = 0;
    }

    @Override
    public INode<T, V> getRoot() {
        return root;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public void clear() {

    }

    @Override
    public V search(T key) {
        return null;
    }

    @Override
    public boolean contains(T key) {
        return false;
    }

    @Override
    public void insert(T key, V value) {

    }

    @Override
    public boolean delete(T key) {
        return false;
    }
}
