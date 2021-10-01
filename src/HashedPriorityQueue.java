
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.function.Function;

/** Combines a hash map with a priority queue.
 * This ensures O(1) time complexity when we want to lookup if something exists in the priority queue.
 * the key of the hash map should be extractable from the Value (Aka E); that is why a keyGetter Function<E, K> is needed.
 */
public class HashedPriorityQueue<K, E> {
    private HashMap<K, E> hashMap;
    private PriorityQueue<E> priorityQueue;
    private Function<E, K> keyGetter;
    
    public HashedPriorityQueue(Comparator<E> comparator, Function<E, K> keyGetter) {
        hashMap = new HashMap<>();
        priorityQueue = new PriorityQueue<>(comparator);
        this.keyGetter = keyGetter;
    }

    /**
    * Adds the value to the priority queue.
    * Extracts key from value using keyGetter and then stores key, value in hash map 
    */
    public void putAdd(E value) {
        hashMap.put(keyGetter.apply(value), value);
        priorityQueue.add(value);
    }

    public E get(Object key) {
        return hashMap.get(key);
    }

    public K getKeyFromValue(E value) {
        return keyGetter.apply(value);
    }

    public boolean containsKey(K key) {
        return hashMap.containsKey(key);
    }

    public E remove() {
        E removed = priorityQueue.remove();
        hashMap.remove(keyGetter.apply(removed));
        return removed;
    }

    public boolean remove(K key) {
        return priorityQueue.remove(hashMap.remove(key));
        
    }

    public boolean isEmpty() {
        return hashMap.isEmpty();
    }

}
