package co.crystaldev.client.util.type;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;

public class GlueList<T> extends AbstractList<T> implements List<T>, Cloneable, Serializable {
    transient Node<T> first;

    transient Node<T> last;

    int size;

    int initialCapacity;

    private static final int DEFAULT_CAPACITY = 10;

    private static final int MAX_ARRAY_SIZE = 2147483639;

    public GlueList() {
        Node<T> initNode = new Node<>(null, null, 0, 10);
        this.first = initNode;
        this.last = initNode;
    }

    public GlueList(int initialCapacity) {
        this.initialCapacity = Math.min(initialCapacity, 2147483639);
        Node<T> initNode = new Node<>(null, null, 0, initialCapacity);
        this.first = initNode;
        this.last = initNode;
    }

    public GlueList(Collection<? extends T> c) {
        Objects.requireNonNull(c);
        Object[] arr = c.toArray();
        int len = arr.length;
        if (len != 0) {
            Node<T> initNode = new Node<>(null, null, 0, len);
            this.first = initNode;
            this.last = initNode;
            System.arraycopy(arr, 0, this.last.elementData, 0, len);
            this.last.elementDataPointer += len;
        } else {
            Node<T> initNode = new Node<>(null, null, 0, 10);
            this.first = initNode;
            this.last = initNode;
        }
        this.modCount++;
        this.size += len;
    }

    public boolean add(T element) {
        Node<T> l = this.last;
        if (l.isAddable()) {
            l.add(element);
        } else {
            Node<T> newNode = new Node<>(l, null, this.size);
            newNode.add(element);
            this.last = newNode;
            l.next = this.last;
        }
        this.modCount++;
        this.size++;
        return true;
    }

    public void add(int index, T element) {
        rangeCheckForAdd(index);
        Node<T> node = getNodeForAdd(index);
        if (node == null) {
            Node<T> l = this.last;
            Node<T> newNode = new Node<>(l, null, this.size);
            this.last = newNode;
            l.next = this.last;
            node = newNode;
        }
        if (node == this.last && node.elementData.length - node.elementDataPointer > 0) {
            int nodeArrIndex = index - node.startingIndex;
            System.arraycopy(node.elementData, nodeArrIndex, node.elementData, nodeArrIndex + 1, node.elementDataPointer - nodeArrIndex);
            node.elementData[nodeArrIndex] = element;
            if (nodeArrIndex > 0)
                System.arraycopy(node.elementData, 0, node.elementData, 0, nodeArrIndex);
            node.elementDataPointer++;
        } else {
            int newLen = node.elementData.length + 1;
            T[] newElementData = (T[]) new Object[newLen];
            int nodeArrIndex = index - node.startingIndex;
            System.arraycopy(node.elementData, nodeArrIndex, newElementData, nodeArrIndex + 1, node.elementDataPointer - nodeArrIndex);
            newElementData[nodeArrIndex] = element;
            if (nodeArrIndex > 0)
                System.arraycopy(node.elementData, 0, newElementData, 0, nodeArrIndex);
            node.elementData = newElementData;
            node.endingIndex++;
            node.elementDataPointer++;
        }
        updateNodesAfterAdd(node);
        this.modCount++;
        this.size++;
    }

    private void rangeCheckForAdd(int index) {
        if (index > this.size || index < 0)
            throw new ArrayIndexOutOfBoundsException(index);
    }

    private void updateNodesAfterAdd(Node<T> nodeFrom) {
        for (Node<T> node = nodeFrom.next; node != null; node = node.next) {
            node.startingIndex++;
            node.endingIndex++;
        }
    }

    public boolean addAll(Collection<? extends T> c) {
        Objects.requireNonNull(c);
        Object[] collection = c.toArray();
        int len = collection.length;
        if (len == 0)
            return false;
        if (this.size == 0) {
            if (this.initialCapacity >= len) {
                System.arraycopy(collection, 0, this.last.elementData, 0, len);
            } else {
                this.last.elementData = Arrays.copyOf((T[]) collection, len);
                this.last.endingIndex = len - 1;
            }
            this.last.elementDataPointer += len;
            this.modCount++;
            this.size += len;
            return true;
        }
        int elementDataLen = this.last.elementData.length;
        int elementSize = this.last.elementDataPointer;
        int remainedStorage = elementDataLen - elementSize;
        if (remainedStorage == 0) {
            Node<T> l = this.last;
            int newLen = this.size >>> 1;
            int initialLen = (len > newLen) ? len : newLen;
            Node<T> newNode = new Node<>(l, null, this.size, initialLen);
            System.arraycopy(collection, 0, newNode.elementData, 0, len);
            newNode.elementDataPointer += len;
            this.last = newNode;
            l.next = this.last;
            this.modCount++;
            this.size += len;
            return true;
        }
        if (len <= remainedStorage) {
            System.arraycopy(collection, 0, this.last.elementData, elementSize, len);
            this.last.elementDataPointer += len;
            this.modCount++;
            this.size += len;
            return true;
        }
        if (len > remainedStorage) {
            System.arraycopy(collection, 0, this.last.elementData, elementSize, remainedStorage);
            this.last.elementDataPointer += remainedStorage;
            this.size += remainedStorage;
            int newLen = this.size >>> 1;
            int remainedDataLen = len - remainedStorage;
            int initialLen = (newLen > remainedDataLen) ? newLen : remainedDataLen;
            Node<T> l = this.last;
            Node<T> newNode = new Node<>(l, null, this.size, initialLen);
            System.arraycopy(collection, remainedStorage, newNode.elementData, 0, remainedDataLen);
            newNode.elementDataPointer += remainedDataLen;
            this.last = newNode;
            l.next = this.last;
            this.modCount++;
            this.size += remainedDataLen;
            return true;
        }
        return false;
    }

    public T set(int index, T element) {
        rangeCheck(index);
        Node<T> node = getNode(index);
        int nodeArrIndex = index - node.startingIndex;
        T oldValue = node.elementData[nodeArrIndex];
        node.elementData[nodeArrIndex] = element;
        return oldValue;
    }

    public T get(int index) {
        rangeCheck(index);
        Node<T> node = getNode(index);
        return node.elementData[index - node.startingIndex];
    }

    public int indexOf(Object o) {
        int index = 0;
        if (o == null) {
            for (Node<T> node = this.first; node != null; node = node.next) {
                for (int i = 0; i < node.elementDataPointer; i++) {
                    if (node.elementData[i] == null)
                        return index;
                    index++;
                }
            }
        } else {
            for (Node<T> node = this.first; node != null; node = node.next) {
                for (int i = 0; i < node.elementDataPointer; i++) {
                    if (o.equals(node.elementData[i]))
                        return index;
                    index++;
                }
            }
        }
        return -1;
    }

    public int lastIndexOf(Object o) {
        int index = this.size - 1;
        if (o == null) {
            for (Node<T> node = this.last; node != null; node = node.pre) {
                for (int i = node.elementDataPointer - 1; i >= 0; i--) {
                    if (node.elementData[i] == null)
                        return index;
                    index--;
                }
            }
        } else {
            for (Node<T> node = this.last; node != null; node = node.pre) {
                for (int i = node.elementDataPointer - 1; i >= 0; i--) {
                    if (o.equals(node.elementData[i]))
                        return index;
                    index--;
                }
            }
        }
        return -1;
    }

    public boolean contains(Object o) {
        return (indexOf(o) != -1);
    }

    public T remove(int index) {
        Node<T> node;
        rangeCheck(index);
        if (this.size == 2 && this.first != this.last) {
            Node<T> newNode = new Node<>(null, null, 0, 2);
            newNode.add(this.first.elementData[0]);
            newNode.add(this.last.elementData[0]);
            node = this.first = this.last = newNode;
        } else {
            node = getNode(index);
        }
        T[] elementData = node.elementData;
        int elementSize = node.elementDataPointer;
        int nodeArrIndex = index - node.startingIndex;
        T oldValue = elementData[nodeArrIndex];
        int numMoved = elementSize - nodeArrIndex - 1;
        if (numMoved > 0)
            System.arraycopy(node.elementData, nodeArrIndex + 1, node.elementData, nodeArrIndex, numMoved);
        if (this.first == this.last || node == this.last) {
            node.elementData[elementSize - 1] = null;
        } else {
            node.elementData = Arrays.copyOf(node.elementData, elementSize - 1);
            node.endingIndex = (--node.endingIndex < 0) ? 0 : node.endingIndex;
        }
        node.elementDataPointer--;
        updateNodesAfterRemove(node);
        if (node.elementDataPointer == 0 && this.first != this.last) {
            Node<T> next = node.next;
            Node<T> prev = node.pre;
            if (prev == null) {
                this.first = next;
            } else {
                prev.next = next;
                node.pre = null;
            }
            if (next == null) {
                this.last = prev;
            } else {
                next.pre = prev;
                node.next = null;
            }
            node.elementData = null;
        }
        this.size--;
        this.modCount++;
        return oldValue;
    }

    public boolean removeAll(Collection<?> c) {
        Objects.requireNonNull(c);
        Object[] arr = c.toArray();
        if (arr.length == 0)
            return false;
        boolean isModified = false;
        for (Object o : arr)
            isModified |= remove(o);
        return isModified;
    }

    public boolean retainAll(Collection<?> c) {
        Objects.requireNonNull(c);
        Object[] arr = c.toArray();
        if (arr.length == 0)
            return false;
        boolean isModified = false;
        Object[] elements = toArray();
        for (Object element : elements) {
            if (!c.contains(element))
                isModified |= remove(element);
        }
        return isModified;
    }

    public boolean remove(Object o) {
        int index = indexOf(o);
        if (index != -1) {
            remove(index);
            return true;
        }
        return false;
    }

    private void updateNodesAfterRemove(Node<T> fromNode) {
        for (Node<T> node = fromNode.next; node != null; node = node.next) {
            node.startingIndex = (--node.startingIndex < 0) ? 0 : node.startingIndex;
            node.endingIndex = (--node.endingIndex < 0) ? 0 : node.endingIndex;
        }
    }

    private Node<T> getNode(int index) {
        int firstStartingIndex = this.first.startingIndex;
        int firstEndingIndex = this.first.endingIndex;
        int firstMinDistance = Math.min(Math.abs(index - firstStartingIndex), Math.abs(index - firstEndingIndex));
        int lastStartingIndex = this.last.startingIndex;
        int lastEndingIndex = this.last.endingIndex;
        int lastMinDistance = Math.min(Math.abs(index - lastStartingIndex), Math.abs(index - lastEndingIndex));
        if (firstMinDistance <= lastMinDistance) {
            Node<T> node1 = this.first;
            while (true) {
                if (node1.startingIndex <= index && index <= node1.endingIndex)
                    return node1;
                node1 = node1.next;
            }
        }
        Node<T> node = this.last;
        while (true) {
            if (node.startingIndex <= index && index <= node.endingIndex)
                return node;
            node = node.pre;
        }
    }

    private Node<T> getNodeForAdd(int index) {
        if (index == this.size && (this.last.startingIndex > index || index > this.last.endingIndex))
            return null;
        return getNode(index);
    }

    private void rangeCheck(int index) {
        if (index >= this.size || index < 0)
            throw new ArrayIndexOutOfBoundsException(index);
    }

    public void clear() {
        for (Node<T> node = this.first; node != null; ) {
            Node<T> next = node.next;
            node.next = null;
            node.pre = null;
            node.elementData = null;
            node = next;
        }
        this.first = this.last = null;
        int capacity = Math.min(2147483639, Math.max(this.size, Math.max(this.initialCapacity, 10)));
        Node<T> initNode = new Node<>(null, null, 0, capacity);
        this.initialCapacity = capacity;
        this.first = initNode;
        this.last = initNode;
        this.modCount++;
        this.size = 0;
    }

    public void trimToSize() {
        int pointer = this.last.elementDataPointer;
        int arrLen = this.last.elementData.length;
        if (pointer < arrLen && arrLen > 2)
            if (pointer < 2) {
                this.last.elementData = Arrays.copyOf(this.last.elementData, 2);
                this.last.endingIndex -= arrLen - 2;
            } else {
                this.last.elementData = Arrays.copyOf(this.last.elementData, pointer);
                this.last.endingIndex -= arrLen - pointer;
            }
    }

    public List<T> subList(int fromIndex, int toIndex) {
        return super.subList(fromIndex, toIndex);
    }

    public Object[] toArray() {
        Object[] objects = new Object[this.size];
        int i = 0;
        for (Node<T> node = this.first; node != null; node = node.next) {
            int len = node.elementDataPointer;
            if (len > 0)
                System.arraycopy(node.elementData, 0, objects, i, len);
            i += len;
        }
        return objects;
    }

    public <T> T[] toArray(T[] a) {
        return (T[]) Arrays.<Object, Object>copyOf(toArray(), this.size, (Class) a.getClass());
    }

    public boolean isEmpty() {
        return (this.size == 0);
    }

    public Iterator<T> iterator() {
        return new Itr();
    }

    private class Itr implements Iterator<T> {
        GlueList.Node<T> node = GlueList.this.first;

        int i = 0;

        int j = 0;

        int lastReturn = -1;

        int expectedModCount = GlueList.this.modCount;

        int elementDataPointer = this.node.elementDataPointer;

        public boolean hasNext() {
            return (this.j != GlueList.this.size);
        }

        public T next() {
            checkForComodification();
            if (this.j >= GlueList.this.size)
                throw new NoSuchElementException();
            if (this.j >= GlueList.this.last.endingIndex + 1)
                throw new ConcurrentModificationException();
            if (this.j == 0) {
                this.node = GlueList.this.first;
                this.elementDataPointer = this.node.elementDataPointer;
                this.i = 0;
            }
            T val = this.node.elementData[this.i++];
            if (this.i >= this.elementDataPointer) {
                this.node = this.node.next;
                this.i = 0;
                this.elementDataPointer = (this.node != null) ? this.node.elementDataPointer : 0;
            }
            this.lastReturn = this.j++;
            return val;
        }

        public void remove() {
            if (this.lastReturn < 0)
                throw new IllegalStateException();
            checkForComodification();
            try {
                GlueList.this.remove(this.lastReturn);
                this.j = this.lastReturn;
                this.lastReturn = -1;
                this.i = (--this.i < 0) ? 0 : this.i;
                this.elementDataPointer = (this.node != null) ? this.node.elementDataPointer : 0;
                this.expectedModCount = GlueList.this.modCount;
            } catch (IndexOutOfBoundsException e) {
                throw new ConcurrentModificationException();
            }
        }

        void checkForComodification() {
            if (GlueList.this.modCount != this.expectedModCount)
                throw new ConcurrentModificationException();
        }

        private Itr() {
        }
    }

    public ListIterator<T> listIterator(int index) {
        checkPositionIndex(index);
        return new ListItr(index);
    }

    private void checkPositionIndex(int index) {
        if (index < 0 || index > this.size)
            throw new ArrayIndexOutOfBoundsException(index);
    }

    public ListIterator<T> listIterator() {
        return new ListItr(0);
    }

    private class ListItr extends Itr implements ListIterator<T> {
        public ListItr(int index) {
            this.node = (index == GlueList.this.size) ? GlueList.this.last : GlueList.this.getNode(index);
            this.j = index;
            this.i = index - this.node.startingIndex;
            this.elementDataPointer = this.node.elementDataPointer;
        }

        public boolean hasPrevious() {
            return (this.j != 0);
        }

        public T previous() {
            checkForComodification();
            int temp = this.j - 1;
            if (temp < 0)
                throw new NoSuchElementException();
            if (temp >= GlueList.this.last.endingIndex + 1)
                throw new ConcurrentModificationException();
            if (this.j == GlueList.this.size) {
                this.node = GlueList.this.last;
                this.elementDataPointer = this.node.elementDataPointer;
                this.i = this.elementDataPointer;
            }
            int index = this.j - this.node.startingIndex;
            if (index == 0) {
                this.node = this.node.pre;
                this.elementDataPointer = this.node.elementDataPointer;
                this.i = this.elementDataPointer;
            }
            T val = this.node.elementData[--this.i];
            if (this.i < 0) {
                this.node = this.node.pre;
                this.i = (this.node != null) ? this.node.elementDataPointer : 0;
            }
            this.j = temp;
            this.lastReturn = this.j;
            return val;
        }

        public int nextIndex() {
            return this.j;
        }

        public int previousIndex() {
            return this.j - 1;
        }

        public void set(T t) {
            if (this.lastReturn < 0)
                throw new IllegalStateException();
            checkForComodification();
            try {
                GlueList.this.set(this.lastReturn, t);
            } catch (IndexOutOfBoundsException e) {
                throw new ConcurrentModificationException();
            }
        }

        public void add(T t) {
            checkForComodification();
            try {
                int temp = this.j;
                GlueList.this.add(temp, t);
                this.j = temp + 1;
                this.lastReturn = -1;
                this.i++;
                this.elementDataPointer = (this.node != null) ? this.node.elementDataPointer : 0;
                this.expectedModCount = GlueList.this.modCount;
            } catch (IndexOutOfBoundsException e) {
                throw new ConcurrentModificationException();
            }
        }
    }

    public int size() {
        return this.size;
    }

    public Object clone() {
        try {
            GlueList<T> clone = (GlueList<T>) super.clone();
            clone.first = clone.last = null;
            int capacity = Math.min(2147483639, Math.max(clone.size, Math.max(clone.initialCapacity, 10)));
            Node<T> initNode = new Node<>(null, null, 0, capacity);
            clone.initialCapacity = capacity;
            clone.first = clone.last = initNode;
            clone.modCount = 0;
            clone.size = 0;
            for (Node<T> node = this.first; node != null; node = node.next) {
                for (int i = 0; i < node.elementDataPointer; i++)
                    clone.add(node.elementData[i]);
            }
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        int expectedModCount = this.modCount;
        s.defaultWriteObject();
        s.writeInt(this.size);
        for (Node<T> node = this.first; node != null; node = node.next) {
            for (int i = 0; i < node.elementDataPointer; i++)
                s.writeObject(node.elementData[i]);
        }
        if (this.modCount != expectedModCount)
            throw new ConcurrentModificationException();
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        clear();
        s.defaultReadObject();
        int size = s.readInt();
        for (int i = 0; i < size; i++)
            this.last.add((T) s.readObject());
    }

    static class Node<T> {
        Node<T> pre;

        Node<T> next;

        int listSize;

        int startingIndex;

        int endingIndex;

        T[] elementData;

        int elementDataPointer;

        Node(Node<T> pre, Node<T> next, int listSize) {
            this.pre = pre;
            this.next = next;
            this.listSize = listSize;
            this.elementData = (T[]) new Object[listSize >>> 1];
            this.startingIndex = listSize;
            this.endingIndex = listSize + this.elementData.length - 1;
        }

        Node(Node<T> pre, Node<T> next, int listSize, int initialCapacity) {
            this.pre = pre;
            this.next = next;
            this.listSize = listSize;
            this.elementData = createElementData(initialCapacity);
            this.startingIndex = listSize;
            this.endingIndex = listSize + this.elementData.length - 1;
        }

        T[] createElementData(int capacity) {
            if (capacity == 0 || capacity == 1)
                return (T[]) new Object[10];
            if (capacity > 1)
                return (T[]) new Object[capacity];
            throw new IllegalArgumentException("Illegal Capacity: " + capacity);
        }

        boolean isAddable() {
            return (this.elementDataPointer < this.elementData.length);
        }

        void add(T element) {
            this.elementData[this.elementDataPointer++] = element;
        }

        public String toString() {
            return String.format("[sIndex: %d - eIndex: %d | elementDataPointer: %d | elementDataLength: %d]", new Object[]{Integer.valueOf(this.startingIndex), Integer.valueOf(this.endingIndex), Integer.valueOf(this.elementDataPointer), Integer.valueOf(this.elementData.length)});
        }
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\clien\\util\type\GlueList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */