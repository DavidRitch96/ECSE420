package ca.mcgill.ecse420.a3;


public class TestBoundedQueue {

  public static final int NUM_THREADS = 3;
  
  public static void main (String[] args) {
    BoundedQueue list = new BoundedQueue();
    
    Test[] tests = new Test[NUM_THREADS];
    
    for (int i=0; i< NUM_THREADS; i++) {
      tests[i] = new Test(list, i);
    }
    
    Thread[] threads = new Thread[NUM_THREADS];
    
    // Assigning and running all the threads
    for (int i = 0; i < threads.length; i++) {
      threads[i]=new Thread(tests[i]);
      threads[i].start();
    }
  }
  
  
  public static class Test implements Runnable {
    private BoundedQueue list;
    private int threadNum;
    
    // Constructor
    public Test(BoundedQueue list, int threadNum){
      this.list = list;
      this.threadNum = threadNum;
    }
    
    
    @Override
    public void run() {
      while (true) {
        if (((Math.random())*2)>1) {
          enqueue();
        } else {
          dequeue();
        }
      }
    }

    
    private void enqueue() {
      Integer item = (int) ((Math.random())*100);

      list.enqueue(item);

    }
    
    private void dequeue() {

      list.dequeue();

    }
    
  }
}

