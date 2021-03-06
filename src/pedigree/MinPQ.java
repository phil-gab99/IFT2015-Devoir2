package pedigree;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.NoSuchElementException;

/**
 * The class {@link MinPQ} defines a generic min priority queue of comparable
 * objects.
 * 
 * @version 1.0 2021-mm-dd
 * @author Philippe Gabriel
 */

public class MinPQ<T extends Comparable<T>> {
    
    static final int DEFAULT_CAPACITY = 10;
    
    private Object[] pq;
    private int n;
    
    /**
     * The constructor method {@link #MinPQ(int)} initializes the priority
     * queue with a starting given capacity.
     *
     * @param capacity Starting capacity of priority queue
     */
    
    public MinPQ(int capacity) {
        
        n = 0;
        pq = new Object[capacity];
    }
    
    /**
     * The constructor method {@link #MinPQ()} initializes the priority queue
     * with default capacity.
     */
    
    public MinPQ() {
        
        this(DEFAULT_CAPACITY);
    }
    
    @SuppressWarnings("unchecked")
    private T pq(int index) {
        
        return (T)pq[index];
    }
    
    public List<T> toList() {
        
        List<T> list = new ArrayList<>(n);
        
        for (int i = 1; i <= n; i++) {
            
            list.add(pq(i));
        }
        
        return list;
    }
    
    /**
     * The method {@link #isEmpty()} indicates whether the priority queue is
     * empty or not.
     * 
     * @return {@code true} if this priority queue is empty<li>{@code false}
     * otherwise</li>
     */
    
    public boolean isEmpty() {
        
        return n == 0;
    }
    
    /**
     * The method {@link #insert(T)} adds a new {@link T} type object to the
     * priority queue.
     *
     * @param v {@link T} type to add onto priority queue
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
     * The method {@link #size()} retrieves the size of the priority queue.
     * 
     * @return The number of elements in the priority queue
     */
    
    public int size() {
        
        return n;
    }
    
    /**
     * The method {@link #delMin()} retrieves and removes the highest priority
     * element of this priority queue
     *
     * @return The highest priority element of this priority queue
     * @throws NoSuchElementException if priority queue is empty
     */
    
    public T delMin() {
        
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
     * The method {@link #peek()} retrieves the minimum element of the priority
     * queue.
     *
     * @return The highest priority element of this priority queue
     * @throws NoSuchElementException if priority queue is empty
     */
    
    public T peek() throws NoSuchElementException {
        
        if (isEmpty()) {
            
            throw new NoSuchElementException("Priority queue underflow");
        }
        
        return pq(1);
    }
    
    /**
     * The method {@link #resize(int)} resizes the priority queue to the given
     * capacity.
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
     * The helper method {@link #swim(int)} correctly positions an element
     * up through the binary heap structure to preserve the min-heap property.
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
     * The helper method {@link #sink(int)} correctly positions an element
     * down through the binary heap to preserve the min-heap property.
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
     * The helper method {@link #greater(int, int)} compares two {@link Event}s
     * at the given indeces and determines whether the first is greater than
     * the second.
     *
     * @param i Index of first {@link Event}
     * @param j Index of second {@link Event}
     * @return {@code true} if the {@link Event} at index {@code i} is greater
     * than the {@link Event} at index {@code j}<li>{@code false} otherwise
     * </li>
     * @see java.lang.Comparable
     */
    
    private boolean greater(int i, int j) {
        
        // System.out.println(pq(i) + " " + pq(j));
        return pq(i).compareTo(pq(j)) > 0;
    }
    
    /**
     * The helper method {@link #swap(int, int)} positionnally swaps two
     * elements in the priority queue at the given indeces
     */
    
    private void swap(int i, int j) {
        
        T temp = pq(i);
        pq[i] = pq(j);
        pq[j] = temp;
    }
}