package eg.edu.alexu.csd.filestructure.redblacktree;

import javax.management.RuntimeErrorException;

public class RedBlackTree<T extends Comparable<T>, V> implements IRedBlackTree<T, V> {

    INode<T, V> root;

    public RedBlackTree() {
        root = null;
    }

    @Override
    public INode<T, V> getRoot() {
        return root;
    }

    @Override
    public boolean isEmpty() {
        return root == null;
    }

    @Override
    public void clear() {
        root = null;
    }

    @Override
    public V search(T key) {

        if (key == null) {
            throw new RuntimeErrorException(new Error());
        }

        INode<T, V> node = findNode(root, key);
        if (node != null && node.getKey() == key) {
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
        INode<T, V> node = findNode(root, key);
        return node != null && node.getKey() == key;
    }

    @Override
    public void insert(T key, V value) {

        if (key == null || value == null) {
            throw new RuntimeErrorException(new Error());
        }

        INode<T, V> newNode = new Node<>(key, value);

        if (root == null) {
            newNode.setColor(INode.BLACK);
            root = newNode;
        }
        else {
            INode<T, V> temp = findNode(root, key);

            // Update value if key exists
            if (temp.getKey() == key){
                temp.setValue(value);
                return;
            }

            newNode.setParent(temp);
            if (key.compareTo(temp.getKey()) < 0) {
                temp.setLeftChild(newNode);
            }
            else {
                temp.setRightChild(newNode);
            }
            redRed(newNode);
        }
    }

    @Override
    public boolean delete(T key) {

        if (key == null) {
            throw new RuntimeErrorException(new Error());
        }

        // Check for node existence
        INode<T, V> node = findNode(root, key);
        if (node == null) {
            return false;
        }

        // Find replacement for the deleted node
        INode<T, V> nodeReplace = findReplace(node);

        // When both the target node and its replacement are black
        boolean bothBlack = ((nodeReplace == null || nodeReplace.getColor() == INode.BLACK) &&
                            (node.getColor() == INode.BLACK));
        INode<T, V> parent = node.getParent();

        // When the node is leaf then it has no replacement
        if (nodeReplace == null) {

            // When the target node is the root, just make the root null
            if (node == root) {
                root = null;
            }
            else {

                // Fix double black at the target node
                if (bothBlack) {
                    doubleBlack(node);
                }
                else {

                    // Todo Add comment
                    INode<T, V> brother = getBrother(node);
                    if (brother != null) {
                        brother.setColor(INode.RED);
                    }
                }

                // Delete node from tree
                detachNodeFromParent(node);
            }
            return true;
        }

        // When node has 1 child
        if (node.getLeftChild() == null || node.getRightChild() == null) {

            // Check if it's root
            if (node == root) {
                root.setKey(nodeReplace.getKey());
                root.setValue(nodeReplace.getValue());
                detachNodeFromParent(nodeReplace);
            }
            else {

                // Delete node from tree and replace it
                if (isLeftChild(node)) {
                    parent.setLeftChild(nodeReplace);
                }
                else {
                    parent.setRightChild(nodeReplace);
                }
                nodeReplace.setParent(parent);

                // Fix double black at the replacement node
                if (bothBlack) {
                    doubleBlack(nodeReplace);
                }

                // Replacement node is red, so change it to black
                else {
                    nodeReplace.setColor(INode.BLACK);
                }
            }
            return true;
        }

        // Node has 2 children, so swap values with replacement
        swap(node, nodeReplace);
        delete(nodeReplace.getKey());
        return true;
    }

    void redRed(INode<T, V> node) {

        if (node == root) {
            node.setColor(INode.BLACK);
            return;
        }

        INode<T, V> parent = node.getParent(), grandParent = node.getParent().getParent();
        INode<T, V> uncle = getBrother(node.getParent());

        if (parent.getColor() != INode.BLACK) {
            if(uncle != null && uncle.getColor() == INode.RED){

                parent.setColor(INode.BLACK);
                uncle.setColor(INode.BLACK);
                grandParent.setColor(INode.RED);
                redRed(grandParent);
            }
            else {
                if(isLeftChild(parent)){
                    // Left right case
                    if (isLeftChild(node)) {
                        swapColors(parent, grandParent);
                    }
                    else {
                        leftRotate(parent);
                        swapColors(node, grandParent);
                    }
                    rightRotate(grandParent);
                }
                else {
                    // Right left case
                    if (isLeftChild(node)) {
                        rightRotate(parent);
                        swapColors(node, grandParent);
                    }
                    else {
                        swapColors(parent, grandParent);
                    }
                    leftRotate(grandParent);
                }
            }
        }
    }

    // Fix double black problem at a node
    void doubleBlack(INode<T, V> node) {

        // Base case
        if (node == root) {
            return;
        }

        INode<T, V> brother = getBrother(node), parent = node.getParent();

        // When no brothers, double black moves up
        if (brother == null) {
            doubleBlack(parent);
        }
        else {

            // When brother is red, rotate parent around brother
            if (brother.getColor() == INode.RED) {
                parent.setColor(INode.RED);
                brother.setColor(INode.BLACK);

                if (isLeftChild(brother)) {
                    rightRotate(parent);
                }
                else {
                    leftRotate(parent);
                }
                doubleBlack(node);
            }
            // So brother is black and cases differ according to its children color
            else {
                if (hasRedChild(brother)) {
                    // Check if brother has at least 1 red child
                    if (brother.getLeftChild() != null && brother.getLeftChild().getColor() == INode.RED) {
                        // Left left case
                        if (isLeftChild(brother)) {
                            brother.getLeftChild().setColor(brother.getColor());
                            brother.setColor(parent.getColor());
                            rightRotate(parent);
                        }
                        // Right left case
                        else {
                            brother.getLeftChild().setColor(parent.getColor());
                            rightRotate(brother);
                            leftRotate(parent);
                        }
                    }
                    else {
                        // Left right case
                        if (isLeftChild(brother)) {
                            brother.getRightChild().setColor(parent.getColor());
                            leftRotate(brother);
                            rightRotate(parent);
                        }
                        // Right right case
                        else {
                            brother.getRightChild().setColor(brother.getColor());
                            brother.setColor(parent.getColor());
                            leftRotate(parent);
                        }
                    }
                    parent.setColor(INode.BLACK);
                }
                // Brother has 2 black children
                else {
                    brother.setColor(INode.RED);
                    if (parent.getColor() == INode.BLACK) {
                        doubleBlack(parent);
                    }
                    else {
                        parent.setColor(INode.BLACK);
                    }
                }
            }
        }
    }

    // Left rotate a node
    private void leftRotate(INode<T, V> node) {

        // The right child will be the new parent
        INode<T, V> newParent = node.getRightChild();

        // Change root if current node is root
        if (node == root) {
            root = newParent;
        }

        if (node.getParent() != null) {
            if (isLeftChild(node)) {
                node.getParent().setLeftChild(newParent);
            }
            else {
                node.getParent().setRightChild(newParent);
            }
        }
        newParent.setParent(node.getParent());
        node.setParent(newParent);

        node.setRightChild(newParent.getLeftChild());

        if (newParent.getLeftChild() != null) {
            newParent.getLeftChild().setParent(node);
        }
        newParent.setLeftChild(node);
    }

    // Right rotate a node
    private void rightRotate(INode<T, V> node) {

        // The right child will be the new parent
        INode<T, V> newParent = node.getLeftChild();

        // Change root if current node is root
        if (node == root) {
            root = newParent;
        }

        if (node.getParent() != null) {
            if (isLeftChild(node)) {
                node.getParent().setLeftChild(newParent);
            }
            else {
                node.getParent().setRightChild(newParent);
            }
        }
        newParent.setParent(node.getParent());
        node.setParent(newParent);

        node.setLeftChild(newParent.getRightChild());

        if (newParent.getRightChild() != null) {
            newParent.getRightChild().setParent(node);
        }
        newParent.setRightChild(node);
    }

    private INode<T, V> findNode(INode<T, V> root, T key) {

        INode<T, V> tmp = root;
        while (tmp != null) {
            if (key.compareTo(tmp.getKey()) > 0) {
                if (tmp.getRightChild() == null) {
                    break;
                }
                else {
                    tmp = tmp.getRightChild();
                }
            }
            else if (key.compareTo(tmp.getKey()) < 0) {
                if (tmp.getLeftChild() == null) {
                    break;
                }
                else {
                    tmp = tmp.getLeftChild();
                }
            }
            else {
                break;
            }
        }
        return tmp;
    }

    private INode<T, V> findReplace(INode<T, V> node) {

        if (node.getLeftChild() != null && node.getRightChild() != null) {
            INode<T, V> tmp = node.getRightChild();

            while (tmp.getLeftChild() != null) {
                tmp = tmp.getLeftChild();
            }
            return tmp;
        }

        if (node.getLeftChild() == null && node.getRightChild() == null) {
            return null;
        }

        if (node.getLeftChild() != null) {
            return node.getLeftChild();
        }
        else {
            return node.getRightChild();
        }
    }

    private INode<T, V> getBrother(INode<T, V> node) {

        if (node.getParent() == null) return null;

        if (node.getParent().getLeftChild() == node) {
            return node.getParent().getRightChild();
        }
        else {
            return node.getParent().getLeftChild();
        }
    }

    private void detachNodeFromParent(INode<T, V> node) {

        if (node.getParent().getLeftChild() == node) {
            node.getParent().setLeftChild(null);
        }
        else {
            node.getParent().setRightChild(null);
        }
    }

    private boolean isLeftChild(INode<T, V> node) {

        return node.getParent().getLeftChild() == node;
    }

    private void swap(INode<T, V> node, INode<T, V> nodeReplace) {

        T tmpKey = nodeReplace.getKey();
        V tmpVal = nodeReplace.getValue();
        nodeReplace.setKey(node.getKey());
        nodeReplace.setValue(node.getValue());
        node.setKey(tmpKey);
        node.setValue(tmpVal);
    }

    private void swapColors(INode<T, V> x, INode<T, V> y) {
        boolean tmp = x.getColor();
        x.setColor(y.getColor());
        y.setColor(tmp);
    }

    private boolean hasRedChild(INode<T, V> node) {

        return (node.getLeftChild() != null && node.getLeftChild().getColor() == INode.RED) ||
                (node.getRightChild() != null && node.getRightChild().getColor() == INode.RED);
    }

}
