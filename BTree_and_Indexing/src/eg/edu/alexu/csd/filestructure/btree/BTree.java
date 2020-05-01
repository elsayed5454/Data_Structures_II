package eg.edu.alexu.csd.filestructure.btree;

import javax.management.RuntimeErrorException;
import java.util.ArrayList;
import java.util.List;

public class BTree<K extends Comparable<K>, V> implements IBTree<K, V> {

    private final int minimumDegree;
    private IBTreeNode<K, V> root;
    private final int MIN_KEYS;
    private final int MAX_KEYS;

    public BTree(int minimumDegree) {
        if (minimumDegree == 0 || minimumDegree == 1){
            throw new RuntimeErrorException(new Error());
        }
        this.minimumDegree = minimumDegree;
        MIN_KEYS = minimumDegree - 1; //min number of keys allowed in one node
        MAX_KEYS = minimumDegree * 2 - 1; //max number of keys allowed in one node
    }

    @Override
    public int getMinimumDegree() {
        return this.minimumDegree;
    }

    @Override
    public IBTreeNode<K, V> getRoot() {
        return root;
    }

    @Override
    public void insert(K key, V value) {
        if (key == null || value == null){ //invalid input
            throw new RuntimeErrorException(new Error());
        }
        if (search(key) != null){ //already exists
            return;
        }
        if (this.root == null){ //tree is empty
            IBTreeNode<K, V> newRoot = new BTreeNode<>();
            List<K> keys = new ArrayList<>();
            List<V> values = new ArrayList<>();
            keys.add(key);
            values.add(value);
            List<IBTreeNode<K, V>> children = new ArrayList<>();
            newRoot.setKeys(keys);
            newRoot.setValues(values);
            newRoot.setNumOfKeys(keys.size());
            newRoot.setChildren(children);
            newRoot.setLeaf(true); //the root of the tree is also a leaf
            this.root = newRoot;
            return;
        }
        IBTreeNode<K, V> insertInto = findNodeInsert(this.root, key); //find the node to insert into
        List<K> keys = insertInto.getKeys();
        int i;
        for (i = 0 ; i < insertInto.getNumOfKeys(); i++){ //find the right to place to insert into inside the node
            if (key.compareTo(keys.get(i)) < 0){
                break;
            }
        }
        keys.add(i, key);
        insertInto.getValues().add(i, value);
        insertInto.setNumOfKeys(keys.size());
    }

    @Override
    public V search(K key) {
        if (key == null) { //invalid input
            throw new RuntimeErrorException(new Error());
        }
        IBTreeNode<K, V> found = findNodeSearch(this.root, key); //find the node that contains the key
        if (found == null) { //not found
            return null;
        }
        if (found.getKeys().contains(key)){
            return found.getValues().get(found.getKeys().indexOf(key)); //return suitable value according to the key
        }
        return null; //not found in the leaf
    }

    @Override
    public boolean delete(K key) {

        // if key is not found in tree return false
        if (search(key) == null) return false;

        // Recursively iterating through the B-tree while preserving its properties
        delete(this.root, key);

        // if the root has no keys then the new root is the old root's only child
        if (this.root.getKeys().isEmpty() && !this.root.isLeaf())
            this.root = this.root.getChildren().get(0);

        return true;
    }

    private IBTreeNode<K, V> findNodeSearch(IBTreeNode<K, V> node, K key) {
        while (node != null && !node.isLeaf()) {
            List<K> keys = node.getKeys();
            for (int i = 0; i < keys.size(); i++) {
                K k = keys.get(i);
                if (key.compareTo(k) == 0) { //key found
                    return node;
                } else if (key.compareTo(k) < 0) { //search in the left child
                    List<IBTreeNode<K, V>> children = node.getChildren();
                    node = children.get(i);
                    break;
                } else if (i == keys.size() - 1) { //search in the right child
                    List<IBTreeNode<K, V>> children = node.getChildren();
                    node = children.get(i + 1);
                    break;
                }
            }
        }

        // additional check for key in node if this node is leaf
        if (node != null && node.isLeaf() && isKeyInCurrNode(node, key)) {
            return node;
        }
        else {
            return null;
        }
    }

    private IBTreeNode<K, V> findNodeInsert(IBTreeNode<K, V> node, K key) {
        if (node.getNumOfKeys() == MAX_KEYS){ //the root is full and needs to be split
            splitRoot(node);
            node = this.root;
        }
        while (!node.isLeaf()) {
            List<K> keys = node.getKeys();
            for (int i = 0; i < keys.size(); i++){
                K k = keys.get(i);
                if (key.compareTo(k) < 0) { //traverse through left child
                    List<IBTreeNode<K, V>> children = node.getChildren();
                    IBTreeNode<K, V> child = children.get(i);
                    if (child.getNumOfKeys() == MAX_KEYS){ //split if left child is full
                        child = split(node, child, key);
                    }
                    node = child;
                    break;
                } else if (i == keys.size() - 1) { //traverse throught the most right child
                    List<IBTreeNode<K, V>> children = node.getChildren();
                    IBTreeNode<K, V> child = children.get(i+1);
                    if (child.getNumOfKeys() == MAX_KEYS){ //split if the most right child is full
                       child = split(node, child, key);
                    }
                    node = child;
                    break;
                }
            }
        }
        return node;
    }

    private void splitRoot (IBTreeNode<K, V> node){
        List<K> keys = node.getKeys();
        List<V> values = node.getValues();
        List<IBTreeNode<K, V>> children = node.getChildren();

        IBTreeNode<K, V> newRoot = new BTreeNode<>();
        IBTreeNode<K, V> leftSplit = new BTreeNode<>();
        IBTreeNode<K, V> rightSplit = new BTreeNode<>();

        List<K> newKeys = new ArrayList<>();
        List<V> newValues = new ArrayList<>();
        List<IBTreeNode<K, V>> newChildren = new ArrayList<>();

        if(node.isLeaf()){
            leftSplit.setLeaf(true);
            rightSplit.setLeaf(true);
        }
        int i;
        for ( i = 0; i < node.getNumOfKeys() / 2; i++){ //left side of the split
            newKeys.add(keys.get(i));
            newValues.add(values.get(i));
            if(!node.isLeaf()){
                newChildren.add(children.get(i));
            }
        }
        if(!node.isLeaf()){ //assign the new leaves
            newChildren.add(children.get(i));
        }
        leftSplit.setKeys(newKeys);
        leftSplit.setValues(newValues);
        leftSplit.setChildren(newChildren);
        leftSplit.setNumOfKeys(newKeys.size());

        newKeys = new ArrayList<>();
        newValues = new ArrayList<>();
        newChildren = new ArrayList<>();

        newKeys.add(keys.get(i));  //the middle item of the split
        newValues.add(values.get(i));
        newChildren.add(leftSplit);
        newChildren.add(rightSplit);
        newRoot.setKeys(newKeys);
        newRoot.setValues(newValues);
        newRoot.setChildren(newChildren);
        newRoot.setNumOfKeys(newKeys.size());

        newKeys = new ArrayList<>();
        newValues = new ArrayList<>();
        newChildren = new ArrayList<>();

        i++;
        for (; i < node.getNumOfKeys(); i++){ //the right side of the split
            newKeys.add(keys.get(i));
            newValues.add(values.get(i));
            if(!node.isLeaf()){
                newChildren.add(children.get(i));
            }
        }
        if(!node.isLeaf()){
            newChildren.add(children.get(i));
        }
        rightSplit.setKeys(newKeys);
        rightSplit.setValues(newValues);
        rightSplit.setChildren(newChildren);
        rightSplit.setNumOfKeys(newKeys.size());
        this.root = newRoot;
    }

    private IBTreeNode<K, V> split (IBTreeNode<K, V> node, IBTreeNode<K, V> child, K key){
        IBTreeNode<K, V> leftSplit = new BTreeNode<>();
        IBTreeNode<K, V> rightSplit = new BTreeNode<>();

        List<K> keys = child.getKeys();
        List<V> values = child.getValues();
        List<IBTreeNode<K, V>> children = child.getChildren();

        List<K> newKeys = new ArrayList<>();
        List<V> newValues = new ArrayList<>();
        List<IBTreeNode<K, V>> newChildren = new ArrayList<>();

        if (child.isLeaf()){ //assign the new leaves
            leftSplit.setLeaf(true);
            rightSplit.setLeaf(true);
        }
        int i;
        for ( i = 0; i < child.getNumOfKeys() / 2; i++){ //left side of the split
            newKeys.add(keys.get(i));
            newValues.add(values.get(i));
            if(!child.isLeaf()){
                newChildren.add(children.get(i));
            }
        }
        if(!child.isLeaf()){
            newChildren.add(children.get(i));
        }
        leftSplit.setKeys(newKeys);
        leftSplit.setValues(newValues);
        leftSplit.setChildren(newChildren);
        leftSplit.setNumOfKeys(newKeys.size());

        newKeys = node.getKeys(); //middle item of the split
        newValues = node.getValues();
        newChildren = node.getChildren();
        int j;
        K midKey = keys.get(i);
        for ( j = 0 ; j < newKeys.size(); j++){
            if (keys.get(i).compareTo(newKeys.get(j)) < 0){
                break;
            }
        }
        newKeys.add(j,midKey);
        newValues.add(j,values.get(i));
        newChildren.remove(j);
        newChildren.add(j,leftSplit);
        newChildren.add(j+1,rightSplit);
        node.setKeys(newKeys);
        node.setValues(newValues);
        node.setChildren(newChildren);
        node.setNumOfKeys(newKeys.size());

        newKeys = new ArrayList<>();
        newValues = new ArrayList<>();
        newChildren = new ArrayList<>();

        i++;
        for (; i < child.getNumOfKeys() ; i++){ //right side of the split
            newKeys.add(keys.get(i));
            newValues.add(values.get(i));
            if(!child.isLeaf()){
                newChildren.add(children.get(i));
            }
        }
        if(!child.isLeaf()){
            newChildren.add(children.get(i));
        }
        rightSplit.setKeys(newKeys);
        rightSplit.setValues(newValues);
        rightSplit.setChildren(newChildren);
        rightSplit.setNumOfKeys(newKeys.size());

        if (key.compareTo(midKey) > 0){
            return rightSplit;
        }
        return leftSplit;
    }

    private void delete(IBTreeNode<K, V> x, K key) {
        if (x.isLeaf()) {   // case I: if the node is leaf then remove the key and the value
            List<K> xKeys = x.getKeys();
            List<V> xValues = x.getValues();

            int index = xKeys.indexOf(key); // index of value in node's values
            xKeys.remove(key);
            xValues.remove(index);

            x.setKeys(xKeys);
            x.setValues(xValues);
            return;
        }

        // if the node is internal node and key is found in it
        if (isKeyInCurrNode(x, key)) {
            internalFoundDelete(x, key);
            return;
        }

        // if the node is internal node and key is not found in it
        internalNotFoundDelete(x, key);
    }

    // case II: if the node is internal node and key is found in it
    private void internalFoundDelete(IBTreeNode<K, V> x, K key) {

        int index = x.getKeys().indexOf(key);   // index of value which equivalent to key

        // get the predecessor child to current node
        IBTreeNode<K, V> predChild = getPredChild(x, key);

        // Check if the predecessor child has additional keys to compensate the deleted one from the parent node
        if (predChild.getNumOfKeys() >= minimumDegree) {
            K predKey = getPredKey(predChild);
            V predValue = getPredValue(predChild);

            // delete the predecessor key from tree before replacing the original deleted node by it
            delete(predChild, predKey);
            List<K> xKeys = x.getKeys();
            List<V> xValues = x.getValues();

            xKeys.set(index, predKey);    // replacing the deleted key by its predecessor
            xValues.set(index, predValue);  // replacing the deleted value by its predecessor

            x.setKeys(xKeys);
            x.setValues(xValues);
            return;
        }

        // get the successor child to current node
        IBTreeNode<K, V> succChild = getSuccChild(x, key);

        // Check if the successor child has additional keys to compensate the deleted one from the parent node
        if (succChild.getNumOfKeys() >= minimumDegree) {
            K succKey = getSuccKey(succChild);
            V succValue = getSuccValue(succChild);

            // delete the successor key from tree before replacing the original deleted node by it
            delete(succChild, succKey);
            List<K> xKeys = x.getKeys();
            List<V> xValues = x.getValues();

            xKeys.set(index, succKey);  // replacing the deleted key by its successor
            xValues.set(index, succValue);  // replacing the deleted value by its successor

            x.setKeys(xKeys);
            x.setValues(xValues);
            return;
        }

        // if none has additional keys then merge them with the target key to be deleted
        merge(x, key, predChild, succChild);
        delete(predChild, key);     // recursively move down to the merged node to delete the target key
    }

    // case III: if the node is internal node and key is not found in it
    private void internalNotFoundDelete(IBTreeNode<K, V> x, K key) {

        // get the subtree in which the key must be there to check its number of keys
        IBTreeNode<K, V> subtree = getSubtree(x, key);

        // if the subtree number of keys is small then it needs refactoring
        if (subtree.getNumOfKeys() < minimumDegree) {

            // the left or right sibling can lend the subtree some keys if it has additional ones
            IBTreeNode<K, V> sibling = getImmediateSibling(x, subtree);

            if (sibling != null) {  // if such sibling exists
                moveKeys(x, subtree, sibling);
                delete(subtree, key);
            }
            else {  // No sibling has additional keys

                int index = getSiblingIndex(x, subtree);    // get index of any sibling
                List<IBTreeNode<K, V>> xChildren = x.getChildren();

                // check if this sibling is right or left sibling
                boolean isRightSibling = index > 0 && xChildren.get(index - 1) == subtree;
                sibling = xChildren.get(index);

                // according to the position of the sibling, the merge of sibling, subtree and median key differs
                K medianKey;
                if (isRightSibling) {
                    medianKey = x.getKeys().get(index - 1);
                    merge(x, medianKey, subtree, sibling);
                    delete(subtree, key);   // recursively move down to the merged node to delete the target key
                }
                else {
                    medianKey = x.getKeys().get(index);
                    merge(x, medianKey, sibling, subtree);
                    delete(sibling, key);
                }
            }
        }

        // recursively move down to the merged node to delete the target key
        else {
            delete(subtree, key);
        }
    }

    // check if the key is found in this node
    boolean isKeyInCurrNode(IBTreeNode<K, V> x, K key) {
        List<K> keys = x.getKeys();
        for (K k : keys) {
            if (key.compareTo(k) == 0)
                return true;
        }
        return false;
    }

    // get the predecessor child to this node
    IBTreeNode<K, V> getPredChild(IBTreeNode<K, V> x, K key) {
        List<K> keys = x.getKeys();
        for (int i = 0; i < keys.size(); ++i) {
            if (key.compareTo(keys.get(i)) == 0)
                return x.getChildren().get(i);
        }
        return null;
    }

    // get the predecessor key by searching for the rightmost key in the this subtree
    K getPredKey(IBTreeNode<K, V> predChild) {
        int lastIndex;
        if (predChild.isLeaf()) {
            lastIndex = predChild.getKeys().size() - 1;
            return predChild.getKeys().get(lastIndex);
        }

        lastIndex = predChild.getChildren().size() - 1;
        return getPredKey(predChild.getChildren().get(lastIndex));
    }

    // get the predecessor value by searching for the rightmost key in the this subtree
    V getPredValue(IBTreeNode<K, V> predChild) {
        int lastIndex;
        if (predChild.isLeaf()) {
            lastIndex = predChild.getValues().size() - 1;
            return predChild.getValues().get(lastIndex - 1);
        }

        lastIndex = predChild.getChildren().size() - 1;
        return getPredValue(predChild.getChildren().get(lastIndex));
    }

    // get the successor child to this node
    IBTreeNode<K, V> getSuccChild(IBTreeNode<K, V> x, K key) {
        List<K> keys = x.getKeys();
        for (int i = 0; i < keys.size(); ++i) {
            if (key.compareTo(keys.get(i)) == 0)
                return x.getChildren().get(i + 1);
        }
        return null;
    }

    // get the successor key by searching for the leftmost key in the this subtree
    K getSuccKey(IBTreeNode<K, V> succChild) {
        int firstIndex = 0;
        if (succChild.isLeaf()) {
            return succChild.getKeys().get(firstIndex);
        }

        return getSuccKey(succChild.getChildren().get(firstIndex));
    }

    // get the successor value by searching for the leftmost key in the this subtree
    V getSuccValue(IBTreeNode<K, V> succChild) {
        int firstIndex = 0;
        if (succChild.isLeaf()) {
            return succChild.getValues().get(firstIndex);
        }

        return getSuccValue(succChild.getChildren().get(firstIndex));
    }

    // merge the addend node to the addedTo nodes with the key from parent
    void merge(IBTreeNode<K, V> x, K key, IBTreeNode<K, V> addedTo, IBTreeNode<K, V> addend) {
        int index;

        // add key and value from parent to addedTo node keys
        List<K> keysOfAddedTo = addedTo.getKeys();
        index = x.getKeys().indexOf(key);
        List<V> valuesOfAddedTo = addedTo.getValues();
        keysOfAddedTo.add(key);
        valuesOfAddedTo.add(x.getValues().get(index));

        // then add the addend node keys
        keysOfAddedTo.addAll(addend.getKeys());
        valuesOfAddedTo.addAll(addend.getValues());
        addedTo.setKeys(keysOfAddedTo);
        addedTo.setValues(valuesOfAddedTo);

        // add the children pointers if the nodes are internal nodes
        if (!addedTo.isLeaf()) {
            List<IBTreeNode<K, V>> addendChildren = addend.getChildren();
            List<IBTreeNode<K, V>> addedToChildren = addedTo.getChildren();
            addedToChildren.addAll(addendChildren);
            addedTo.setChildren(addedToChildren);
        }

        // after that, remove the key from the parent
        List<K> keysOfX = x.getKeys();
        List<V> valuesOfX = x.getValues();
        index = keysOfX.indexOf(key);   // used in removing pointer to addend node
        keysOfX.remove(key);
        valuesOfX.remove(index);
        x.setKeys(keysOfX);
        x.setValues(valuesOfX);

        // finally, remove the pointer to the addend node from parent
        List<IBTreeNode<K, V>> childrenOfX = x.getChildren();
        childrenOfX.remove(childrenOfX.get(index + 1));
        x.setChildren(childrenOfX);
    }

    // get subtree in which key must exists
    IBTreeNode<K, V> getSubtree(IBTreeNode<K, V> x, K key) {
        List<K> keys = x.getKeys();
        for (int i = 0; i < keys.size(); ++i) {
            K k = keys.get(i);

            if (key.compareTo(k) <= 0) {
                return x.getChildren().get(i);
            }
            if (i == keys.size() - 1) {
                return x.getChildren().get(i + 1);
            }
        }
        return null;
    }

    // get a sibling with additional number of keys
    IBTreeNode<K, V> getImmediateSibling(IBTreeNode<K, V> x, IBTreeNode<K, V> subtree) {
        List<IBTreeNode<K, V>> xChildren = x.getChildren();
        for (int i = 0; i < xChildren.size(); ++i) {
            if (xChildren.get(i) == subtree) {

                // left sibling with additional keys
                if (i > 0 && xChildren.get(i - 1).getNumOfKeys() >= minimumDegree) {
                    return xChildren.get(i - 1);
                }
                // right sibling with additional keys
                else if (i != xChildren.size() - 1 && xChildren.get(i + 1).getNumOfKeys() >= minimumDegree) {
                    return xChildren.get(i + 1);
                }
            }
        }
        return null;
    }

    // move key from parent to subtree to increase its number of keys to be equal to the minimum degree
    // then replace the moved key in parent by an additional key in sibling
    void moveKeys(IBTreeNode<K, V> x, IBTreeNode<K, V> subtree, IBTreeNode<K, V> sibling) {

        int index = 0;  // used to store the index of key in parent to be replaced
        boolean isRightSibling = false; // used to know the position of sibling

        List<IBTreeNode<K, V>> xChildren = x.getChildren();
        for (int i = 0; i < xChildren.size(); ++i) {
            if (xChildren.get(i) == subtree) {
                index = i;
                isRightSibling = i != xChildren.size() - 1 && xChildren.get(i + 1) == sibling;
                break;
            }
        }

        if (isRightSibling) {

            // move key and value from parent to subtree
            List<K> xKeys = x.getKeys();
            List<V> xValues = x.getValues();
            K xKeyToBeMovedDown = xKeys.get(index);
            V xValueToBeMovedDown = xValues.get(index);

            // add moved down key and value then setting the new keys to subtree
            List<K> subtreeKeys = subtree.getKeys();
            List<V> subtreeValues = subtree.getValues();
            subtreeKeys.add(xKeyToBeMovedDown);
            subtreeValues.add(xValueToBeMovedDown);
            subtree.setKeys(subtreeKeys);
            subtree.setValues(subtreeValues);

            // then replace the moved key and value in parent by an additional key in sibling
            List<K> siblingKeys = sibling.getKeys();
            List<V> siblingValues = sibling.getValues();
            K siblingKeyToBeMovedUp = siblingKeys.remove(0);
            V siblingValueToBeMovedUp = siblingValues.remove(0);
            xKeys.set(index, siblingKeyToBeMovedUp);
            xValues.set(index, siblingValueToBeMovedUp);
            x.setKeys(xKeys);
            x.setValues(xValues);
            sibling.setKeys(siblingKeys);
            sibling.setValues(siblingValues);

            // finally moving the appropriate child pointer from sibling to subtree, if they are internal nodes
            if (!subtree.isLeaf()) {
                List<IBTreeNode<K, V>> siblingChildren = sibling.getChildren();
                IBTreeNode<K, V> childPt = siblingChildren.remove(0);
                List<IBTreeNode<K, V>> subtreeChildren = subtree.getChildren();
                subtreeChildren.add(childPt);
                sibling.setChildren(siblingChildren);
                subtree.setChildren(subtreeChildren);
            }
        }
        else {  // left sibling

            // move key and value from parent to subtree
            List<K> xKeys = x.getKeys();
            List<V> xValues = x.getValues();
            K xKeyToBeMovedDown = xKeys.get(index - 1);
            V xValueToBeMovedDown = xValues.get(index - 1);

            // add moved down key and value then setting the new keys to subtree
            List<K> subtreeKeys = subtree.getKeys();
            List<V> subtreeValues = subtree.getValues();
            subtreeKeys.add(0, xKeyToBeMovedDown);
            subtreeValues.add(0, xValueToBeMovedDown);
            subtree.setKeys(subtreeKeys);
            subtree.setValues(subtreeValues);

            // then replace the moved key and value in parent by an additional key in sibling
            List<K> siblingKeys = sibling.getKeys();
            List<V> siblingValues = sibling.getValues();
            K siblingKeyToBeMovedUp = siblingKeys.remove(siblingKeys.size() - 1);
            V siblingValueToBeMovedUp = siblingValues.remove(siblingValues.size() - 1);
            xKeys.set(index - 1, siblingKeyToBeMovedUp);
            xValues.set(index - 1, siblingValueToBeMovedUp);
            x.setKeys(xKeys);
            x.setValues(xValues);
            sibling.setKeys(siblingKeys);
            sibling.setValues(siblingValues);

            // finally moving the appropriate child pointer from sibling to subtree, if they are internal nodes
            if (!subtree.isLeaf()) {
                List<IBTreeNode<K, V>> siblingChildren = sibling.getChildren();
                IBTreeNode<K, V> childPt = siblingChildren.remove(siblingChildren.size() - 1);
                List<IBTreeNode<K, V>> subtreeChildren = subtree.getChildren();
                subtreeChildren.add(0, childPt);
                sibling.setChildren(siblingChildren);
                subtree.setChildren(subtreeChildren);
            }
        }
    }

    // get index of any sibling to this node
    int getSiblingIndex(IBTreeNode<K, V> x, IBTreeNode<K, V> subtree) {
        List<IBTreeNode<K, V>> xChildren = x.getChildren();
        for (int i = 0; i < xChildren.size(); ++i) {
            if (xChildren.get(i) == subtree) {
                if (i != xChildren.size() - 1) return i + 1;
                else return i - 1;
            }
        }
        return 0;
    }
}
