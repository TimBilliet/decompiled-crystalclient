package mapwriter;

import java.util.*;

public class CircularHashMap<K, V> {
  private final Map<K, Node> nodeMap = new HashMap<>();
  
  private Node headNode = null;
  
  private Node currentNode = null;
  
  public class Node implements Map.Entry<K, V> {
    private final K key;
    
    private V value;
    
    private Node next;
    
    private Node prev;
    
    Node(K key, V value) {
      this.key = key;
      this.value = value;
      this.next = this;
      this.prev = this;
    }
    
    public K getKey() {
      return this.key;
    }
    
    public V getValue() {
      return this.value;
    }
    
    public V setValue(V value) {
      V oldValue = this.value;
      this.value = value;
      return oldValue;
    }
  }
  
  public V put(K key, V value) {
    Node node = this.nodeMap.get(key);
    if (node == null) {
      node = new Node(key, value);
      this.nodeMap.put(key, node);
      if (this.headNode == null) {
        node.next = node;
        node.prev = node;
      } else {
        node.next = this.headNode.next;
        node.prev = this.headNode;
        this.headNode.next.prev = node;
        this.headNode.next = node;
      } 
      if (this.currentNode == null)
        this.currentNode = node; 
      this.headNode = node;
    } else {
      node.value = value;
    } 
    return value;
  }
  
  public V remove(Object key) {
    Node node = this.nodeMap.get(key);
    V value = null;
    if (node != null) {
      if (this.headNode == node) {
        this.headNode = node.next;
        if (this.headNode == node)
          this.headNode = null; 
      } 
      if (this.currentNode == node) {
        this.currentNode = node.next;
        if (this.currentNode == node)
          this.currentNode = null; 
      } 
      node.prev.next = node.next;
      node.next.prev = node.prev;
      node.next = null;
      node.prev = null;
      value = node.value;
      this.nodeMap.remove(key);
    } 
    return value;
  }
  
  public void clear() {
    for (Node node : this.nodeMap.values()) {
      node.next = null;
      node.prev = null;
    } 
    this.nodeMap.clear();
    this.headNode = null;
    this.currentNode = null;
  }
  
  public boolean containsKey(Object key) {
    return this.nodeMap.containsKey(key);
  }
  
  public int size() {
    return this.nodeMap.size();
  }
  
  public Set<K> keySet() {
    return this.nodeMap.keySet();
  }
  
  public Collection<V> values() {
    Collection<V> list = new ArrayList<>();
    for (Node node : this.nodeMap.values())
      list.add(node.value); 
    return list;
  }
  
  public Collection<Map.Entry<K, V>> entrySet() {
    return new ArrayList<>(this.nodeMap.values());
  }
  
  public V get(Object key) {
    Node node = this.nodeMap.get(key);
    return (node != null) ? node.value : null;
  }
  
  public boolean isEmpty() {
    return this.nodeMap.isEmpty();
  }
  
  public Map.Entry<K, V> getNextEntry() {
    if (this.currentNode != null)
      this.currentNode = this.currentNode.next; 
    return this.currentNode;
  }
  
  public Map.Entry<K, V> getPrevEntry() {
    if (this.currentNode != null)
      this.currentNode = this.currentNode.prev; 
    return this.currentNode;
  }
  
  public void rewind() {
    this.currentNode = (this.headNode != null) ? this.headNode.next : null;
  }
  
  public boolean setPosition(K key) {
    Node node = this.nodeMap.get(key);
    if (node != null)
      this.currentNode = node; 
    return (node != null);
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mapwriter\CircularHashMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */