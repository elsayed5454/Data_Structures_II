package eg.edu.alexu.csd.filestructure.redblacktree;

import javax.management.RuntimeErrorException;

public class RedBlackTree<T extends Comparable<T>, V> implements IRedBlackTree<T, V> {

    private INode<T, V> root;
    // Null node with black color
    private final INode<T, V> nil = new Node<>(true);

    public RedBlackTree() {
        root = nil;
    }

    @Override
    public INode<T, V> getRoot() {
        return root;
    }

    @Override
    public boolean isEmpty() {
        return root.isNull();
    }

    @Override
    public void clear() {
        root = nil;
    }

    @Override
    public V search(T key) {
        if (key == null) {
            throw new RuntimeErrorException(new Error());
        }

        INode<T, V> node = findNode(key);
        if (!node.isNull() && key.compareTo(node.getKey()) == 0) {
            return node.getValue();
        }
        else {
            return null;
        }
    }

    @Override
    public boolean contains(T key) {
        if (key == null) {
            throw new RuntimeErrorException(new Error());
        }

        INode<T, V> node = findNode(key);
        return !node.isNull() && key.compareTo(node.getKey()) == 0;
    }

    @Override
    public void insert(T key, V value) {
        if (key == null || value == null) {
            throw new RuntimeErrorException(new Error());
        }

        // Create new node with Null node as its 2 children
        INode<T, V> newNode = new Node<>(key, value);
        newNode.setLeftChild(nil);
        newNode.setRightChild(nil);

        // Case for first node entered which will be root
        if (root == nil) {
            root = newNode;
            root.setColor(INode.BLACK);     // Root is always black in red-black trees
            root.setParent(nil);            // Setting Null node as its parent
        }
        else {
            INode<T, V> temp = findNode(key);   // Find the parent node of the new node

            // Update value if key already exists
            if (key.compareTo(temp.getKey()) == 0){
                temp.setValue(value);
                return;
            }
            newNode.setParent(temp);            // Setting links between the new node and its parent
            if (key.compareTo(temp.getKey()) < 0) {
                temp.setLeftChild(newNode);
            }
            else {
                temp.setRightChild(newNode);
            }
            redRed(newNode);    // Fixing the problem of two consecutive red nodes
        }
    }

    @Override
    public boolean delete(T key) {
        if (key == null) {
            throw new RuntimeErrorException(new Error());
        }

        // Check for node existence
        INode<T, V> node = findNode(key);
        if (node.isNull()) {
            return false;
        }

        INode<T, V> nodeReplace, temp = node;
        boolean tempColor = temp.getColor();

        if (node.getLeftChild() == nil) {
            nodeReplace = node.getRightChild();
            removeAndReplace(node, node.getRightChild());
        }
        else if (node.getRightChild() == nil) {
            nodeReplace = node.getLeftChild();
            removeAndReplace(node, node.getLeftChild());
        }
        else {
            temp = findMin(node.getRightChild());
            tempColor = temp.getColor();
            nodeReplace = temp.getRightChild();
            if (temp.getParent() == node) {
                nodeReplace.setParent(temp);
            }
            else {
                removeAndReplace(temp, temp.getRightChild());
                temp.setRightChild(node.getRightChild());
                temp.getRightChild().setParent(temp);
            }
            removeAndReplace(node, temp);
            temp.setLeftChild(node.getLeftChild());
            temp.getLeftChild().setParent(temp);
            temp.setColor(node.getColor());
        }

        if (tempColor == INode.BLACK) {
            doubleBlack(nodeReplace);
        }
        return true;
    }

    // Fixes the problem of two consecutive red nodes
    void redRed(INode<T, V> node) {

        while (node.getParent() != root && node.getParent().getColor() == INode.RED) {
            INode<T, V> parent = node.getParent(), grandParent = parent.getParent();
            if (parent == grandParent.getLeftChild()) {
                INode<T, V> uncle = grandParent.getRightChild();
                if (uncle.getColor() == INode.RED) {
                    parent.setColor(INode.BLACK);
                    uncle.setColor(INode.BLACK);
                    grandParent.setColor(INode.RED);
                    node = grandParent;
                }
                else {
                    if (node == parent.getRightChild()) {
                        node = parent;
                        leftRotate(node);
                        parent = node.getParent();
                    }
                    parent.setColor(INode.BLACK);
                    grandParent.setColor(INode.RED);
                    rightRotate(grandParent);
                }
            }
            else {
                INode<T, V> uncle = grandParent.getLeftChild();
                if (uncle.getColor() == INode.RED) {
                    parent.setColor(INode.BLACK);
                    uncle.setColor(INode.BLACK);
                    grandParent.setColor(INode.RED);
                    node = grandParent;
                }
                else {
                    if (node == parent.getLeftChild()) {
                        node = parent;
                        rightRotate(node);
                        parent = node.getParent();
                    }
                    parent.setColor(INode.BLACK);
                    grandParent.setColor(INode.RED);
                    leftRotate(grandParent);
                }
            }
        }
        root.setColor(INode.BLACK);
    }

    // Fix double black problem at a node
    void doubleBlack(INode<T, V> n) {

        while (n != root && n != null && n.getColor() == INode.BLACK) {

            if (n == n.getParent().getLeftChild()) {
                INode<T, V> b = n.getParent().getRightChild();
                if (b.getColor() == INode.RED) {
                    b.setColor(INode.BLACK);
                    n.getParent().setColor(INode.RED);
                    leftRotate(n.getParent());
                    b = n.getParent().getRightChild();
                }
                if (b.getLeftChild() != null && b.getLeftChild().getColor() == INode.BLACK
                    && b.getRightChild() != null && b.getRightChild().getColor() == INode.BLACK) {
                    b.setColor(INode.RED);
                    n = n.getParent();
                }
                else if (b != nil) {
                    if (b.getRightChild() != null && b.getRightChild().getColor() == INode.BLACK) {
                        b.getLeftChild().setColor(INode.BLACK);
                        b.setColor(INode.RED);
                        rightRotate(b);
                        b = n.getParent().getRightChild();
                    }
                    b.setColor(n.getParent().getColor());
                    n.getParent().setColor(INode.BLACK);
                    b.getRightChild().setColor(INode.BLACK);
                    leftRotate(n.getParent());
                    n = root;
                }
                else {
                    n.setColor(INode.BLACK);
                    n = n.getParent();
                }
            }
            else {
                INode<T, V> b = n.getParent().getLeftChild();
                if (b.getColor() == INode.RED) {
                    b.setColor(INode.BLACK);
                    n.getParent().setColor(INode.RED);
                    rightRotate(n.getParent());
                    b = n.getParent().getLeftChild();
                }
                if (b.getLeftChild() != null && b.getLeftChild().getColor() == INode.BLACK
                    && b.getRightChild() != null && b.getRightChild().getColor() == INode.BLACK) {
                    b.setColor(INode.RED);
                    n = n.getParent();
                }
                else if (b != nil) {
                    if (b.getLeftChild() != null && b.getLeftChild().getColor() == INode.BLACK) {
                        b.getRightChild().setColor(INode.BLACK);
                        b.setColor(INode.RED);
                        leftRotate(b);
                        b = n.getParent().getLeftChild();
                    }
                    b.setColor(n.getParent().getColor());
                    n.getParent().setColor(INode.BLACK);
                    b.getLeftChild().setColor(INode.BLACK);
                    rightRotate(n.getParent());
                    n = root;
                }
                else {
                    n.setColor(INode.BLACK);
                    n = n.getParent();
                }
            }
        }
    }

    // Left rotate a node
    private void leftRotate(INode<T, V> node) {
        INode<T, V> temp = node.getRightChild();
        temp.setParent(node.getParent());

        node.setRightChild(temp.getLeftChild());
        if (node.getRightChild() != nil) {
            node.getRightChild().setParent(node);
        }

        temp.setLeftChild(node);
        node.setParent(temp);

        if (temp.getParent() != nil) {
            if (node == temp.getParent().getLeftChild()) {
                temp.getParent().setLeftChild(temp);
            }
            else {
                temp.getParent().setRightChild(temp);
            }
        }
        // Change root if current node is root
        else {
            root = temp;
        }
    }

    // Right rotate a node
    private void rightRotate(INode<T, V> node) {
        INode<T, V> temp = node.getLeftChild();
        temp.setParent(node.getParent());

        node.setLeftChild(temp.getRightChild());
        if (node.getLeftChild() != nil) {
            node.getLeftChild().setParent(node);
        }

        temp.setRightChild(node);
        node.setParent(temp);

        if (temp.getParent() != nil) {
            if (node == temp.getParent().getLeftChild()) {
                temp.getParent().setLeftChild(temp);
            }
            else {
                temp.getParent().setRightChild(temp);
            }
        }
        // Change root if current node is root
        else {
            root = temp;
        }
    }

    private INode<T, V> findNode(T key) {
        INode<T, V> temp = root;
        while (temp != nil) {
            if (key.compareTo(temp.getKey()) > 0) {
                if (temp.getRightChild().isNull()) {
                    break;
                }
                else {
                    temp = temp.getRightChild();
                }
            }
            else if (key.compareTo(temp.getKey()) < 0) {
                if (temp.getLeftChild().isNull()) {
                    break;
                }
                else {
                    temp = temp.getLeftChild();
                }
            }
            else {
                break;
            }
        }
        return temp;
    }

    private INode<T, V> findMin(INode<T, V> node) {
        while (!node.getLeftChild().isNull()) {
            node = node.getLeftChild();
        }
        return node;
    }

    private void removeAndReplace(INode<T, V> node, INode<T, V> newNode) {
    if (node.getParent().isNull()) {
        root = newNode;
    }
    else if (node == node.getParent().getLeftChild()) {
        node.getParent().setLeftChild(newNode);
    }
    else {
        node.getParent().setRightChild(newNode);
    }
    newNode.setParent(node.getParent());
    }

}
