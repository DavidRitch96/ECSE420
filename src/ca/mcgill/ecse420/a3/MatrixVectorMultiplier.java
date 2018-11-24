package ca.mcgill.ecse420.a3;

import static java.util.concurrent.Executors.newFixedThreadPool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class MatrixVectorMultiplier {

  public static void main(String[] args) throws InterruptedException, ExecutionException {
    double[][] M = new double[3][3];
    for (int i = 0; i < M.length; i++) {
      for (int j = 0; j < M[0].length; j++) {
        M[i][j] = i + j;
      }
    }
    
    double[] v = {3.0, 2.0, 1.0};
    
    System.out.println(Arrays.deepToString(M));
    System.out.println(Arrays.toString(v));
    System.out.println(Arrays.toString(multiplyParallel(M, v)));

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
    
    
    
    // each task is responsible for computing one element of the product vector
    ExecutorService executor = newFixedThreadPool(10);
    ArrayList<Future<Double>> futures;
    futures = new ArrayList<Future<Double>>();
//    CompletionService<Double> completionService = new ExecutorCompletionService<Double>(executor);
    for(int i = 0; i < r.length; i++) {
      futures.add(executor.submit(new DotProductTask(a[i], x, executor)));
    }
    
    // shut down the thread pool manager and wait for the last task to complete
//    executor.shutdown();
//    try {
//      executor.awaitTermination(120, TimeUnit.SECONDS);
//    } catch (InterruptedException e) {
//      e.printStackTrace();
//    }
    
    for (int i = 0; i < r.length; i++) {
      System.out.println("getting futures at " + i);
      r[i] = futures.get(i).get();
    }
    return r;
  }
  
  //computes the dot product of a[row] and x, from start to finish.
 static class DotProductTask implements Callable<Double> {
    double[] a, b;
    ExecutorService exc;

    // constructor
    public DotProductTask(double[] a, double[] b, ExecutorService exc) {
      this.a = a;
      this.b = b;
      this.exc = exc;
    }

    public Double call() throws InterruptedException, ExecutionException {
      // dot product of a[row] and x
      long id = Thread.currentThread().getId();
      
      // base case
      if (a.length == 1) {
        return a[0] * b[0];
      } else {
        int mid = a.length / 2;
        double[] a1 = Arrays.copyOfRange(a, 0, mid);
        double[] a2 = Arrays.copyOfRange(a, mid + 1, a.length - 1);
        double[] b1 = Arrays.copyOfRange(b, 0, mid);
        double[] b2 = Arrays.copyOfRange(b, mid + 1, b.length - 1);
        
        Future<Double> fut1 = exc.submit(new DotProductTask(a1, b1, exc));
        Future<Double> fut2 = exc.submit(new DotProductTask(a2, b2, exc));
        
        return fut1.get() + fut2.get();
      }
      
      
    }
  }
  

}
