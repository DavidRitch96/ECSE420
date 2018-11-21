package ca.mcgill.ecse420.a3;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FineGrainedMethod<T> {
  
  private class Node<T> {
      private T item;
      int key;
      private Node<T> next;
      private Lock lock = new ReentrantLock();
    
      private Node (T item) {
          this.item = item;
          this.key = item.hashCode();
      }
  }
  
  private Node<Integer> head;
  
  public FineGrainedMethod() { //initialize our list with items 0 and 100
    head = new Node<Integer>(0);
    head.next = new Node<Integer>(10);
  }


  //add method
  /*Use similar method to remove in the notes to traverse
   * in the notes. If we find the item, we don't add it, but if we don't,
   * we add it at the right index.
   */
  public boolean add(T item) {
      Node pred = null;
      Node curr = null;
      int key = item.hashCode();
      try {
        pred = head;
        pred.lock.lock();
        curr = pred.next;
        curr.lock.lock();
        
        while (curr.key <= key) {
          if (curr.key == key) {
            return false;   //duplicate found
        }
            pred.lock.unlock();
            pred = curr;
            curr = curr.next;
            curr.lock.lock();
        }

        Node<T> node = new Node(item);
        pred.next = node;
        node.next = curr;
        return true;

      } finally {
          curr.lock.unlock();
          pred.lock.unlock();
      }
  }
  
  //remove method
  public boolean remove(T item) {
      Node pred = null;
      Node curr = null;
      int key = item.hashCode();
      try {
          pred = head;
          pred.lock.lock();
          curr = pred.next;
          curr.lock.lock();

          while (curr.key <= key) {
            if (item == curr.item) {
              pred.next = curr.next;
              return true;
          }
              pred.lock.unlock();
              pred = curr;
              curr = curr.next;
              curr.lock.lock();
          }
          return false;
      } finally {
          curr.lock.unlock();
          pred.lock.unlock();
      }
  }
  
  //contains method
  /*The contains method is the same as the remove method, 
   * except when we find the item, we return true instead of removing
   * it.
   */
  public boolean contains(T item) {
      Node pred = null;
      Node curr = null;
      int key = item.hashCode();
      try {
          pred = head;
          pred.lock.lock();
          curr = pred.next;
          curr.lock.lock();

          while (curr.key <= key) {
            if (item == curr.item) {
              return true;
          }
            pred.lock.unlock();
            pred = curr;
            curr = curr.next;
            curr.lock.lock();
          }
          return false;
      } finally {
          curr.lock.unlock();
          pred.lock.unlock();
      }
  }
  
}
