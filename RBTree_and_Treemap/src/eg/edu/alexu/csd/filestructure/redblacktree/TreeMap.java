package eg.edu.alexu.csd.filestructure.redblacktree;

import com.sun.corba.se.spi.protocol.InitialServerRequestDispatcher;

import javax.management.RuntimeErrorException;
import java.util.*;


public class TreeMap<T extends Comparable<T>, V> implements ITreeMap<T, V> {

    final class MyEntry<K, V> implements Map.Entry<K, V> {
        private final K key;
        private V value;

        public MyEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            V old = this.value;
            this.value = value;
            return old;
        }
    }



    IRedBlackTree<T, V> map= new RedBlackTree();
    // Null node with black color
    private final INode<T, V> nil = new Node<>(true);
    ArrayList<Map.Entry<T,V>> list= new ArrayList<Map.Entry<T,V>>();


    void InorderTraversal(INode<T, V> node)
    {
        if ((node.getKey()==null)&&(node.getValue()==null)&&(node.getLeftChild()==null)&&(node.getRightChild()==null)) {

            return;
        }

        /* first recur on left child */
        InorderTraversal(node.getLeftChild());

        /* add the data of node to set */
        list.add(new AbstractMap.SimpleEntry<>((T)node.getKey(),(V)node.getValue()));

        /* now recur on right child */
        InorderTraversal(node.getRightChild());
    }


    private boolean isLeftChild (INode<T, V> child){
        if(child.getParent()!=null&&child.getParent()!=nil){
            if(child.getParent().getLeftChild()==child){
                return true;
            }
        }
        return false;
    }
    private boolean isRightChild (INode<T, V> child){
        if(child.getParent()!=null&&child.getParent()!=nil){
            if(child.getParent().getRightChild()==child){
                return true;
            }
        }
        return false;
    }
    private INode<T, V> findNode(T key) {
        INode<T, V> temp = map.getRoot();
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




    @Override
    public Map.Entry<T, V> ceilingEntry(T key) {
        if (key == null) {
            throw new RuntimeErrorException(new Error());
        }
        INode<T, V> temp= findNode(key);
        if(temp==null||temp==nil){
            return null;
        }
        if(temp.getKey().compareTo(key)<=0){
            return  new MyEntry<T, V>(temp.getKey(), temp.getValue());
        }
        while(temp!=null&&temp!=nil){
            if(isLeftChild(temp)){
                return new MyEntry<T, V>(temp.getParent().getKey(), temp.getParent().getValue()) ;
            }
            temp=temp.getParent();
        }
        return null;
    }

    @Override
    public T ceilingKey(T key) {
        if (key == null) {
            throw new RuntimeErrorException(new Error());
        }
        if(!(ceilingEntry(key)==null)){
            return ceilingEntry(key).getKey();
        }
        return null;
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public boolean containsKey(T key) {
        return map.contains(key);
    }

    @Override
    public boolean containsValue(V value) {
        if (value == null) {
            throw new RuntimeErrorException(new Error());
        }
        return values().contains(value);
    }

    @Override
    public Set<Map.Entry<T, V>> entrySet() {
        InorderTraversal(map.getRoot());
        Set<Map.Entry<T, V>> orderedSet=new LinkedHashSet<>(list);
        Iterator<Map.Entry<T, V>> itr1 = orderedSet.iterator();
        itr1.next();
        System.out.println(itr1);
        return orderedSet;
    }

    @Override
    public Map.Entry<T, V> firstEntry() {
        INode<T,V> t=map.getRoot();
        if(t==null||t==nil||map.isEmpty()){
            return null;
        }
        while(t.getLeftChild().getKey()!=null&&t.getLeftChild().getKey()!=nil&&t.getLeftChild().getValue()!=null&&t.getLeftChild().getValue()!=nil){
            t=t.getLeftChild();
        }
        return new MyEntry<T, V>(t.getKey(),t.getValue());
    }

    @Override
    public T firstKey() {
        if(firstEntry()==null) {
            return null;
        }
        return firstEntry().getKey();
    }

    @Override
    public Map.Entry<T, V> floorEntry(T key) {
        if (key == null) {
            throw new RuntimeErrorException(new Error());
        }
        INode<T, V> temp= findNode(key);
        if(temp==null||temp==nil){
            return null;
        }
        if(temp.getKey().compareTo(key)>=0){
            return  new MyEntry<T, V>(temp.getKey(), temp.getValue());
        }
        while(temp!=null&&temp!=nil){
            if(isRightChild(temp)){
                return new MyEntry<T, V>(temp.getParent().getKey(), temp.getParent().getValue()) ;
            }
            temp=temp.getParent();
        }
        return null;
    }

    @Override
    public T floorKey(T key) {
        if (key == null) {
            throw new RuntimeErrorException(new Error());
        }
        if(!(floorEntry(key)==null)){
            return floorEntry(key).getKey();
        }
        return null;
    }

    @Override
    public V get(T key) {
        if (key == null) {
            throw new RuntimeErrorException(new Error());
        }

        return map.search(key);
    }

    @Override
    public ArrayList<Map.Entry<T, V>> headMap(T toKey) {
        if (toKey == null) {
            throw new RuntimeErrorException(new Error());
        }
        list.clear();
        InorderTraversal(map.getRoot());
        if(!list.contains(toKey)){
            return new ArrayList<>();
        }
        ArrayList<Map.Entry<T, V>> sList = new ArrayList<Map.Entry<T, V>> (list.subList(0,list.indexOf(toKey)-1));
        return sList;
    }

    @Override
    public ArrayList<Map.Entry<T, V>> headMap(T toKey, boolean inclusive) {
        if(inclusive){
            if (toKey == null) {
                throw new RuntimeErrorException(new Error());
            }
            list.clear();
            InorderTraversal(map.getRoot());
            if(!list.contains(toKey)){
                return new ArrayList<>();
            }
            ArrayList<Map.Entry<T, V>> sList = new ArrayList<Map.Entry<T, V>> (list.subList(0,list.indexOf(toKey)));
            return sList;
        }
        return headMap(toKey);
    }

    @Override
    public Set<T> keySet() {
        InorderTraversal(map.getRoot());
        ArrayList<T> keyList= new ArrayList<T>();
        for( int i=0 ; i< list.size();i++){
            keyList.add(list.get(i).getKey());
        }
        Set<T> keySet=new LinkedHashSet<T>(keyList);
        return keySet;
    }

    @Override
    public Map.Entry<T, V> lastEntry() {
        INode<T,V> t=map.getRoot();
        if(t==null||t==nil||map.isEmpty()){
            return null;
        }
        while(t.getRightChild().getKey()!=null&&t.getRightChild().getKey()!=nil&&t.getRightChild().getValue()!=null&&t.getRightChild().getValue()!=nil){
            t=t.getRightChild();
        }
        return new MyEntry<T, V>(t.getKey(),t.getValue());
    }

    @Override
    public T lastKey() {
        if(lastEntry()==null) {
            return null;
        }
        return lastEntry().getKey();
    }

    @Override
    public Map.Entry<T, V> pollFirstEntry() {
        if(map.isEmpty()){
            return null;
        }
        Map.Entry<T,V> temp=firstEntry();
        T tempKey=firstKey();
        remove(tempKey);
        return temp;
    }

    @Override
    public Map.Entry<T, V> pollLastEntry() {
        if(map.isEmpty()){
            return null;
        }
        Map.Entry<T,V> temp=lastEntry();
        T tempKey=lastKey();
        remove(tempKey);
        return temp;
    }

    @Override
    public void put(T key, V value) {
        map.insert(key,value);
    }

    @Override
    public void putAll(Map<T, V> map) {

        if (map == null) {
            throw new RuntimeErrorException(new Error());
        }

        for (Map.Entry<T,V> entry : map.entrySet()){
            put(entry.getKey(),entry.getValue());
        }

    }

    @Override
    public boolean remove(T key) {

        if(!containsKey(key)){
            return false;
        }
        return map.delete(key);

    }

    @Override
    public int size() {
        list.clear();
        InorderTraversal(map.getRoot());
        return list.size();
    }

    @Override
    public Collection<V> values() {
        list.clear();
        InorderTraversal(map.getRoot());
        ArrayList<V> valueList= new ArrayList<V>();
        for( int i=0 ; i< list.size();i++){
            valueList.add(list.get(i).getValue());
        }
        return valueList;
    }
}
