package eg.edu.alexu.csd.filestructure.sort;


public class Node<T extends Comparable<T>> implements INode<T> {

    private Integer index;

    public Node(int newIndex) { index = newIndex; }

    @Override
    public INode<T> getLeftChild() { return new Node<T>( index * 2); }

    @Override
    public INode<T> getRightChild() {
        return new Node<T>(index * 2 + 1);
    }

    @Override
    public INode<T> getParent() {
        return new Node<T>(index / 2);
    }

    @Override
    public T getValue() { return (T) index; }

    @Override
    public void setValue(T value) {
        index = (int) (Object)value;
    }

}
