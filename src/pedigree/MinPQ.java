package pedigree;

import java.util.Comparator;
import java.util.NoSuchElementException;

/**
 * The class {@link MinPQ} defines a generic min priority queue of
 * {@link Comparable} objects.
 * 
 * @param T The generic type of elements on this priority queue
 * @version 1.11.27 2021-03-28
 * @author Philippe Gabriel
 */

public class MinPQ<T extends Comparable<T>> {
    
    static final int DEFAULT_CAPACITY = 1;
    
    private Object[] pq;
    private int n;
    private Comparator<T> comparator;
    
    /**
     * Initializes the priority queue with default capacity using given
     * comparator.
     *
     * @param comparator Natural given order of elements
     */
    
    public MinPQ(Comparator<T> comparator) {
        
        pq = new Object[DEFAULT_CAPACITY];
        n = 0;
        this.comparator = comparator;
    }
    
    /**
     * Initializes the priority queue with default capacity and no comparator.
     */
    
    public MinPQ() {
        
        this(null);
    }
    
    /**
     * Indicates whether the priority queue is empty or not.
     * 
     * @return <ul><li>{@code true} if this priority queue is empty</li><li>
     * {@code false} otherwise</li></ul>
     */
    
    public boolean isEmpty() {
        
        return n == 0;
    }
    
    /**
     * Adds a new {@link T} type object to the priority queue.
     *
     * @param v Element to add onto priority queue
     */
    
    public void insert(T v) {
        
        // Doubling capacity if necessary
        if (n == pq.length - 1) {
            
            resize(2 * pq.length);
        }
        
        pq[++n] = v;
        swim(n);
    }
    
    /**
     * Retrieves the size of the priority queue.
     * 
     * @return The number of elements in the priority queue
     */
    
    public int size() {
        
        return n;
    }
    
    /**
     * Retrieves and removes the minimum element of this priority queue.
     *
     * @return The highest priority element of this priority queue
     * @throws NoSuchElementException if priority queue is empty
     */
    
    public T delMin() throws NoSuchElementException {
        
        T min = peek();
        
        swap(1, n--);
        sink(1);
        pq[n + 1] = null;
        
        if (n > 0 && n == (pq.length - 1) / 4) {
            
            resize(pq.length / 2);
        }
        
        return min;
    }
    
    /**
     * Retrieves the minimum element of the priority queue.
     *
     * @return The highest priority element of this priority queue
     * @throws NoSuchElementException if priority queue is empty
     */
    
    public T peek() {
        
        if (isEmpty()) {
            
            throw new NoSuchElementException("Priority queue underflow");
        }
        
        return pq(1);
    }
    
    /**
     * Determines whether the passed element is within the queue or not.
     *
     * @param e Element to search for
     * @return <ul><li>{@code true} if the element is within the queue</li><li>
     * {@code false} otherwise
     */
    
    public boolean contains(T e) {
        
        for (Object o : pq) {
            
            if (e == o) {
                
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Resizes the priority queue to the given capacity.
     *
     * @param capacity New capacity of the priority queue
     */
    
    private void resize(int capacity) {
        
        Object[] temp = new Object[capacity];
        
        for (int i = 1; i <= n; i++) {
            
            temp[i] = pq(i);
        }
        
        pq = temp;
    }
    
    /**
     * Correctly positions an element up through the binary heap structure to
     * preserve the min-heap property.
     *
     * @param i Index of the {@link Event} to position
     */
    
    private void swim(int i) {
        
        while (i > 1 && greater(i / 2, i)) {
            
            swap(i, i / 2);
            i /= 2;
        }
    }
    
    /**
     * Correctly positions an element down through the binary heap structure to
     * preserve the min-heap property.
     *
     * @param i Index of the element to position
     */
    
    private void sink(int i) {
        
        while (2 * i <= n) {
            
            int j = 2 * i;
            
            // Selecting the min child node
            if (j < n && greater(j, j + 1)) {
                
                j++;
            }
            
            if (!greater(i, j)) {
                
                break;
            }
            
            swap(i, j);
            i = j;
        }
    }
    
    /**
     * Compares two elements at the given indeces and determines whether the
     * first is greater than the second.
     *
     * @param i Index of first {@link Event}
     * @param j Index of second {@link Event}
     * @return <ul><li>{@code true} if the element at index {@code i} is
     * greater than the element at index {@code j}</li><li>{@code false}
     * otherwise</li></ul>
     * @see java.lang.Comparable
     */
    
    private boolean greater(int i, int j) {
        
        if (comparator == null) {
        
            return pq(i).compareTo(pq(j)) > 0;
        } else {
            
            return comparator.compare(pq(i), pq(j)) > 0;
        }
    }
    
    /**
     * Positionnally swaps two elements in the priority queue at the given
     * indeces.
     *
     * @param i Index of first element
     * @param j Index of second element
     */
    
    private void swap(int i, int j) {
        
        T temp = pq(i);
        pq[i] = pq(j);
        pq[j] = temp;
    }
    
    /**
     * Accessor method which retrieves an element of type T in the priority
     * queue at a given index. Note that unchecked warnings are suppressed
     * to allow this procedure since generics check for type errors at
     * compile-time and lack the information at runtime.
     *
     * @param index Index at which element of interest is
     * @return The element of type T at the given index
     * @see the java.util.ArrayList source code for annotation idea
     */
    
    @SuppressWarnings("unchecked")
    private T pq(int index) {
        
        return (T)pq[index];
    }
}