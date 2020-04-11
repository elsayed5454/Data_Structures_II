package eg.edu.alexu.csd.filestructure.redblacktree;

public class Node<T extends Comparable<T>, V> implements INode<T, V> {

    @Override
    public void setParent(INode<T, V> parent) {

    }

    @Override
    public INode<T, V> getParent() {
        return null;
    }

    @Override
    public void setLeftChild(INode<T, V> leftChild) {

    }

    @Override
    public INode<T, V> getLeftChild() {
        return null;
    }

    @Override
    public void setRightChild(INode<T, V> rightChild) {

    }

    @Override
    public INode<T, V> getRightChild() {
        return null;
    }

    @Override
    public T getKey() {
        return null;
    }

    @Override
    public void setKey(T key) {

    }

    @Override
    public V getValue() {
        return null;
    }

    @Override
    public void setValue(V value) {

    }

    @Override
    public boolean getColor() {
        return false;
    }

    @Override
    public void setColor(boolean color) {

    }

    @Override
    public boolean isNull() {
        return false;
    }
}
