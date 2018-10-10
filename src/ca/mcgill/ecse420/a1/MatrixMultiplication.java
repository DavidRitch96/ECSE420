package ca.mcgill.ecse420.a1;

import static java.util.concurrent.Executors.newFixedThreadPool;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class MatrixMultiplication {
  private static final int MATRIX_SIZE = 1000;
  
  public static void main(String[] args) throws IOException {
    // Generate two random matrices, same size
	double[][] a = generateRandomMatrix(MATRIX_SIZE, MATRIX_SIZE);
	double[][] b = generateRandomMatrix(MATRIX_SIZE, MATRIX_SIZE);
	        
	// PRINT GENERATED MATRICES
	//System.out.print("a =");
	//printMatrix(a);
	//System.out.print("b =");
	//printMatrix(b);
	        
	// PART 1.1 SEQUENTIAL MATRIX MULTIPLICATION
	System.out.println("Part 1.1: Sequential Matrix Multiplication");
	System.out.println("Matrix row/column size: " + MATRIX_SIZE);
	//System.out.print("a * b =");
	// TO PRINT RESULT, UNCOMMENT ABOVE AND CHANGE 5TH ARG BELOW TO TRUE.
	System.out.println("multiplication took " 
	  + timeMatrixMultiplication(false, a, b, 0, false) + " ms.\n");
	
	        
	// PART 1.2 PARALLEL MATRIX MULTIPLICATION
	System.out.println("Part 1.2: Parallel Matrix Multiplication");
	System.out.println("Matrix row/column size: " + MATRIX_SIZE);
	System.out.println("Number of threads: 10");
	//System.out.print("a * b =");
	// TO PRINT RESULT, UNCOMMENT ABOVE AND CHANGE 5TH ARG BELOW TO TRUE.
	System.out.println("multiplication took " 
	  + timeMatrixMultiplication(true, a, b, 10, false) + " ms.\n");
    
	
	// PART 1.4 VARYING NUMBER OF THREADS
	System.out.println("Part 1.4 Varying Number of Threads");
	System.out.println("Matrix row/column size: 2000");
	a = generateRandomMatrix(2000, 2000); 
	b = generateRandomMatrix(2000, 2000);
	BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out));
	
	// UNCOMMENT BELOW to write to file instead of stdout
	//writer = new BufferedWriter(new FileWriter("VariedThreads.csv"));
    writer.write("n,ms");
    writer.newLine();
    writer.flush();
	for (int i = 1; i <= 10; i++) {
		writer.write(i+","+timeMatrixMultiplication(true, a, b, i, false));
    	writer.newLine();
    	writer.flush();
	}
	System.out.println();
	        
	        
	// PART 1.5 VARYING MATRIX SIZE PARALLEL VS SEQUENTIAL
	System.out.println("Part 1.5 Varying Matrix Size");
	System.out.println("Number of threads for parallel: 8");
	// UNCOMMENT BELOW to write to file instead of stdout
	//writer = new BufferedWriter(new FileWriter("VariedMatrixSize.csv"));
    writer.write("matrixSize,msSequential,msParallel(n=8)");
    writer.newLine();
    writer.flush();
    for (int i : new int[] {100, 200, 500, 1000, 2000, 4000}) {
      a = generateRandomMatrix(i, i);
      b = generateRandomMatrix(i, i);
      writer.write(i+","+timeMatrixMultiplication(false, a, b, 0, false)+","
          +timeMatrixMultiplication(true, a, b, 8, false));
      writer.newLine();
      writer.flush();
    }
    writer.close();
  }

  /**
   * Returns the result of a sequential matrix multiplication
   * The two matrices are randomly generated
   * @param a is the first matrix
   * @param b is the second matrix
   * @return the result of the multiplication
   * */
  public static double[][] sequentialMultiplyMatrix(double[][] a, double[][] b) {
    double[][] result = new double[a.length][b[0].length];
    for(int i = 0; i < result.length; i++) {
      for (int j = 0; j < result[0].length; j++) {
        // dot product of a[i] and b[][j]
        for (int k = 0; k < a[0].length; k++) {
          result[i][j] += (a[i][k] * b[k][j]);
        }
      }
    }
    return result;
  }

  /**
   * Returns the result of a concurrent matrix multiplication
   * The two matrices are randomly generated
   * @param a is the first matrix
   * @param b is the second matrix
   * @return the result of the multiplication
   * */
  public static double[][] parallelMultiplyMatrix(double[][] a, double[][] b, int threads) {
    double[][] result = new double[a.length][b[0].length];
    
    class DotProductTask implements Runnable {
      int row, col;

      // constructor
      public DotProductTask(int row, int col) {
        this.row = row;
        this.col = col;
      }

      public void run() {
        // dot product of a[i] and b[][j]
        for (int k = 0; k < a[0].length; k++) {
          result[row][col] += (a[row][k] * b[k][col]);
        }
      }
    }
    
    // each task is responsible for computing one cell of the product matrix
    ExecutorService executor = newFixedThreadPool(threads);
    for(int i = 0; i < result.length; i++) {
      for (int j = 0; j < result[0].length; j++) {
        executor.execute(new DotProductTask(i, j));
      }
    }
    
    // shut down the thread pool manager and wait for the last task to complete
    executor.shutdown();
    try {
      executor.awaitTermination(120, TimeUnit.SECONDS);
	} catch (InterruptedException e) {
	  e.printStackTrace();
	}
    return result;
  }

  /**
   * Populates a matrix of given size with randomly generated integers between 0-10.
   * @param numRows number of rows
   * @param numCols number of cols
   * @return matrix
   */
  private static double[][] generateRandomMatrix (int numRows, int numCols) {
    double matrix[][] = new double[numRows][numCols];
    for (int row = 0 ; row < numRows ; row++ ) {
      for (int col = 0 ; col < numCols ; col++ ) {
        matrix[row][col] = (double) ((int) (Math.random() * 10.0));
      }
    }
  return matrix;
  }

  public static void printMatrix(double[][] m) {
    System.out.println();
    for (double[] row : m) {
      System.out.print("|");
      for (double cell : row) {
        System.out.print(cell + "\t");
      }
      System.out.println("|");
    }
    System.out.println();
  }
    
  /**
   * Times the computation of the multiplication of matrices a and b
   * @param isParallel - true if the computation is to be parallelized, false otherwise
   * @param a - the matrix multiplicand
   * @param b - the matrix multiplier
   * @return the time taken to complete the multiplication, in milliseconds
   */
  public static long timeMatrixMultiplication(Boolean isParallel, double[][] a, double[][] b,
        int threads, Boolean printResult) {
  	long startTime = System.currentTimeMillis();
  	double product[][];
  	if (isParallel) {
  	  product = parallelMultiplyMatrix(a, b, threads); // this blocks until computation is complete
  	} else {
  	  product = sequentialMultiplyMatrix(a, b); // this also blocks until computation is complete
  	}
  	long endTime = System.currentTimeMillis();
  	if (printResult) {
  	  printMatrix(product);
  	}
  	return endTime - startTime;
  }
}