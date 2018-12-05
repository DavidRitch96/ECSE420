package ca.mcgill.ecse420.a3;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MatrixVectorMultiplier {

  public static void main(String[] args) throws InterruptedException, ExecutionException {
    final int N = 2000;
    double[][] M = generateSquareMatrix(N);
    double[] v = generateVector(N);
    
    // sequential
    System.out.println("N = " + N);
    System.out.println("Multiplying sequentially...");
    long startTime = System.nanoTime();
    multiplySequentially(M, v);
    long endTime = System.nanoTime();
    System.out.println("Took " + (endTime - startTime)/1000000 + " ms.");
    
    //parallel
    System.out.println("Multiplying in parallel...");
    startTime = System.nanoTime();
    multiplyParallel(M, v);
    endTime = System.nanoTime();
    System.out.println("Took " + (endTime - startTime)/1000000 + " ms.");

  }
  
  private static double[][] generateSquareMatrix(int N) {
    double matrix[][] = new double[N][N];
    for (int row = 0 ; row < N ; row++ ) {
      for (int col = 0 ; col < N ; col++ ) {
        matrix[row][col] = (double) ((int) (Math.random() * 10.0));
      }
    }
    return matrix;
  }
  
  private static double[] generateVector(int N) {
    double vector[] = new double[N];
    for (int i = 0 ; i < N ; i++ ) {
      vector[i] = (double) ((int) (Math.random() * 10.0));
    }
    return vector;
  }
  
  public static double[] multiplySequentially(double[][] a, double[] x) {
    double[] r = new double[a.length];
    for (int i = 0; i < a.length; i++) { // for each row
      for (int j = 0; j < x.length; j++) { // for each column
        r[i] += a[i][j] * x[j];
      }
    }
    return r;
  }
  
  public static double[] multiplyParallel(double[][] a, double[] x) throws InterruptedException, ExecutionException {
    double[] r = new double[x.length];
    
    // to lock elements in the output array while we update them
    Lock[] locks = new ReentrantLock[x.length];
    for (int i = 0; i < x.length; i++) {
      locks[i] = new ReentrantLock();
    }
    
    // to keep track of when we're done
    CountDownLatch latch = new CountDownLatch(x.length*x.length);
    
    
    // main task class
    class TinyTask implements Runnable {
      int rowstart, rowend, colstart, colend;
      ExecutorService exc;

      public TinyTask(int rowstart, int rowend, int colstart, int colend, ExecutorService exc) {
        super();
        this.rowstart = rowstart;
        this.rowend = rowend;
        this.colstart = colstart;
        this.colend = colend;
        this.exc = exc;
      }
      
      public void run() {
        if (colend - colstart <= 1 && rowend - rowstart <= 1) { // base case
          
          // lock the value we want to update. update, then unlock
          locks[colstart].lock();
          r[colstart] += a[rowstart][colstart]*x[colstart];
          locks[colstart].unlock();
          
          // decrement the latch
          latch.countDown();
        } else if (colend - colstart <= 1) { // split up a row
          int mid = (rowend + rowstart)/2;
          exc.submit(new TinyTask(rowstart, mid, colstart, colend, exc));
          exc.submit(new TinyTask(mid, rowend, colstart, colend, exc));
        } else { // split up columns
          int mid = (colend + colstart)/2;
          exc.submit(new TinyTask(rowstart, rowend, colstart, mid, exc));
          exc.submit(new TinyTask(rowstart, rowend, mid, colend, exc));
        }
      }
    }
    
    ExecutorService exc = Executors.newCachedThreadPool();
    exc.execute(new TinyTask(0, x.length, 0, x.length, exc));
    
    // wait for latch, shut down the executor, then return.
    try {
      latch.await();
      exc.shutdown();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return r;
  }
}
