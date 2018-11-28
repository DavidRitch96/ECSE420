package ca.mcgill.ecse420.a3;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BoundedQueue {
  
  public static final int CAPACITY = 4;
  
  Lock enqLock, deqLock;
  Condition notEmptyCondition, notFullCondition;
  AtomicInteger size;
  AtomicInteger index;
  Object[] arr;

  public BoundedQueue() {
    arr = new Object [CAPACITY];  //All values initialized to 0
    size = new AtomicInteger(0);
    enqLock = new ReentrantLock();
    notFullCondition= enqLock.newCondition();
    deqLock = new ReentrantLock();
    notEmptyCondition= deqLock.newCondition();
  }
  
  
  public <T> void enqueue(T item) {
    enqLock.lock();
    
    try {
      System.out.print("Trying to enqueue "+item+": ");

      if (CAPACITY==size.get()) {
        System.out.println("Array is full!");
        return;
      }
      //while (size.get()>=arr.length){
     //   notFullCondition.await();
     // }
      

      arr[size.get()]=item;
      size.incrementAndGet();
      System.out.println("Success!");
      System.out.println();

      
      
      
    } finally {
      enqLock.unlock();
      
      /*Once we get to this point, we know we have at least one item in the array
       * Therefore, we can signal dequeue in case there are any waiting
       */
     // deqLock.lock();
     // try {
     //   notEmptyCondition.signalAll();
     // } finally {
     //   deqLock.unlock();
     // }
      
      
    }
   

  }
  
  public <T> void dequeue() {
    
    deqLock.lock();
    
    try {
      if(size.get()==0) {
        System.out.println("Array is empty!");
        return;
      }
      //while (size.get()==0){  
     //   notEmptyCondition.await();
     // } 
      
      System.out.println("Dequeuing "+arr[0]+".");
      System.out.println();
      arr[0]=0;
        
      for (int i=1; i<arr.length; i++) {
        arr[i-1]=arr[i];  
        if (i==size.get() || i+1==arr.length) {
         arr[i]=null; 
         size.decrementAndGet();

         
         break;
        }
      }
  
      
    } finally {
      
      deqLock.unlock();
      
      /*Once we get to this point, we know we have at least one free spot in the array
       * Therefore, we can signal enqueue in case there are any waiting
       */
    //  enqLock.lock();
    //  try {
    //    notFullCondition.signalAll();
    //  } finally {
    //    enqLock.unlock();
    //  }
    }
    

  }
  

}
