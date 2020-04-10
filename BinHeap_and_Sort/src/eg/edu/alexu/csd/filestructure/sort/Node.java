package eg.edu.alexu.csd.filestructure.sort;


public class Node<T extends Comparable<T>> implements INode<T> {

    private T value;
    private INode<T> parent, leftChild, rightChild;

    public Node(T newValue, INode<T> newParent, INode<T> newLeftChild, INode<T> newRightChild) {

        value = newValue;
        parent = newParent;
        leftChild = newLeftChild;
        rightChild = newRightChild;
    }

    public Node(T newValue) {
        this(newValue, null, null, null);
    }

    @Override
    public INode<T> getLeftChild() {
        return leftChild;
    }

    @Override
    public INode<T> getRightChild() {
        return rightChild;
    }

    @Override
    public INode<T> getParent() {
        return parent;
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public void setValue(T newValue) {
        value = newValue;
    }

    public void setParent(INode<T> newParent) {
        parent = newParent;
    }

    public void setLeftChild(INode<T> newLeftChild) {
        leftChild = newLeftChild;
    }

    public void setRightChild(INode<T> newRightChild) {
        rightChild = newRightChild;
    }

}
