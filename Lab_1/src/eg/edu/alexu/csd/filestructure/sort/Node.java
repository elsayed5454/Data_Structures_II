package eg.edu.alexu.csd.filestructure.sort;


public class Node implements INode {

    private int index;

    public Node(int newIndex) {
        index = newIndex;
    }

    @Override
    public INode getLeftChild() {
        return new Node(index * 2);
    }

    @Override
    public INode getRightChild() {
        return new Node(index * 2 + 1);
    }

    @Override
    public INode getParent() {
        return new Node(index / 2);
    }

    @Override
    public Comparable getValue() {
        return index;
    }

    @Override
    public void setValue(Comparable value) {
        index = (int) value;
    }

}
