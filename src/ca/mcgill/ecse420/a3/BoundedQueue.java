package ca.mcgill.ecse420.a3;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BoundedQueue<T> {
  
  public static final int CAPACITY = 8;
  
  Lock enqLock, deqLock;
  Condition notEmptyCondition, notFullCondition;
  AtomicInteger size;
  Object[] arr;
  int index;

  public BoundedQueue() {
    arr = new Object [CAPACITY];  //All values initialized to 0
    size = new AtomicInteger(0);
    index = 0;
    enqLock = new ReentrantLock();
    notFullCondition= enqLock.newCondition();
    deqLock = new ReentrantLock();
    notEmptyCondition= deqLock.newCondition();
  }
  
  
  public boolean enqueue(T item) throws InterruptedException {
    enqLock.lock();
    
    try {
      if (size.get()>=arr.length) {
        System.out.println("Array is full!");
      }
      while (size.get()>=arr.length){
        notFullCondition.await();
      }
      
      arr[index]=item;
      index++;
      
      
    } finally {
      enqLock.unlock();
    }
    
    deqLock.lock();
    notEmptyCondition.signalAll();
    deqLock.unlock();
    
    return true;
  }
  
  public boolean dequeue() throws InterruptedException {
    
    deqLock.lock();
    
    try {
      if(size.get()==0) {
        System.out.println("Array is empty!");
      }
      while (size.get()==0){
        notEmptyCondition.await();
      } 
      
      System.out.println("Dequeuing "+arr[0]+".");
      arr[0]=0;
        
      for (int i=1; i<arr.length; i++) {
        arr[i-1]=arr[i];  
        if (i==index) {
         arr[i]=0;          
         break;
        }
      }
      
      index--;
      
    } finally {
      deqLock.unlock();
    }
    

    enqLock.lock();
    notFullCondition.signalAll();
    enqLock.unlock();
    
    return true;
  }

}
