package ca.mcgill.ecse420.a3;

import static java.util.concurrent.Executors.newFixedThreadPool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class MatrixVectorMultiplier {

  public static void main(String[] args) throws InterruptedException, ExecutionException {
    final int N = 500;
    double[][] M = generateSquareMatrix(N);
    double[] v = generateVector(N);
    
    
//    System.out.println(Arrays.deepToString(M));
//    System.out.println(Arrays.toString(v));
//    System.out.println(Arrays.toString(multiplyParallel(M, v)));
    System.out.println("N = " + N);
    System.out.println("Multiplying sequentially...");
    long startTime = System.nanoTime();
    multiplySequentially(M, v);
    long endTime = System.nanoTime();
    System.out.println("Took " + (endTime - startTime) + " ns.");
    
    System.out.println("Multiplying in parallel...");
    startTime = System.nanoTime();
    multiplyParallel(M, v);
    endTime = System.nanoTime();
    System.out.println("Took " + (endTime - startTime) + " ns.");

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
      // System.out.print(i + " of " + a.length + "\r");
    }
    return r;
  }
  
  public static double[] multiplyParallel(double[][] a, double[] x) throws InterruptedException, ExecutionException {
    double[] r = new double[x.length];
    
    
    
    // each task is responsible for computing one element of the product vector
    ExecutorService executor = Executors.newCachedThreadPool(); // newFixedThreadPool(r.length * r.length);
    ArrayList<Future<Double>> futures;
    futures = new ArrayList<Future<Double>>();
//    CompletionService<Double> completionService = new ExecutorCompletionService<Double>(executor);
    for(int i = 0; i < r.length; i++) {
      futures.add(executor.submit(new DotProductTask(a[i], x, 0, x.length, executor)));
    }
    
    // shut down the thread pool manager and wait for the last task to complete
//    executor.shutdown();
//    try {
//      executor.awaitTermination(120, TimeUnit.SECONDS);
//    } catch (InterruptedException e) {
//      e.printStackTrace();
//    }
    
    for (int i = 0; i < r.length; i++) {
      // System.out.println("getting futures at " + i);
      r[i] = futures.get(i).get();
    }
    executor.shutdown();
    return r;
  }
  
  //computes the dot product of a[row] and x, from start to finish.
  static class DotProductTask implements Callable<Double> {
    double[] a, b;
    int start, end;
    ExecutorService exc;

    // constructor
    public DotProductTask(double[] a, double[] b, int start, int end, ExecutorService exc) {
      this.a = a;
      this.b = b;
      this.start = start;
      this.end = end;
      this.exc = exc;
    }

    public Double call() throws InterruptedException, ExecutionException {
      // dot product of a[row] and x
      long id = Thread.currentThread().getId();
      
      // base case
      if (end - start == 1) {
        return a[start] * b[start];
      } else {
        // recursive case, split the dot product into 2 dot product tasks
        int mid = (start + end) / 2;
//        double[] a1 = Arrays.copyOfRange(a, 0, mid);
//        double[] a2 = Arrays.copyOfRange(a, mid, a.length);
//        double[] b1 = Arrays.copyOfRange(b, 0, mid);
//        double[] b2 = Arrays.copyOfRange(b, mid, b.length);
        
        Future<Double> fut1 = exc.submit(new DotProductTask(a, b, start, mid, exc));
        Future<Double> fut2 = exc.submit(new DotProductTask(a, b, mid, end, exc));
        
        return fut1.get() + fut2.get();
      }
    }
  }
}
