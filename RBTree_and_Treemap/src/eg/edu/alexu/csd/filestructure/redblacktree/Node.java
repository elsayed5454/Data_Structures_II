package eg.edu.alexu.csd.filestructure.redblacktree;

public class Node<T extends Comparable<T>, V> implements INode<T, V> {

    private T key;
    private V value;
    private INode<T, V> parent, leftChild, rightChild;
    private boolean color;

    public Node(T newKey, V newValue) {
        key = newKey;
        value = newValue;
        parent = leftChild = rightChild = null;
        color = RED;
    }

    @Override
    public void setParent(INode<T, V> newParent) {
        parent = newParent;
    }

    @Override
    public INode<T, V> getParent() {
        return parent;
    }

    @Override
    public void setLeftChild(INode<T, V> newLeftChild) {
        leftChild = newLeftChild;
    }

    @Override
    public INode<T, V> getLeftChild() {
        return leftChild;
    }

    @Override
    public void setRightChild(INode<T, V> newRightChild) {
        rightChild = newRightChild;
    }

    @Override
    public INode<T, V> getRightChild() {
        return rightChild;
    }

    @Override
    public T getKey() {
        return key;
    }

    @Override
    public void setKey(T newKey) {
        key = newKey;
    }

    @Override
    public V getValue() {
        return value;
    }

    @Override
    public void setValue(V newValue) {
        value = newValue;
    }

    @Override
    public boolean getColor() {
        return color;
    }

    @Override
    public void setColor(boolean newColor) {
        color = newColor;
    }

    // Todo
    @Override
    public boolean isNull() {
        return false;
    }
}
