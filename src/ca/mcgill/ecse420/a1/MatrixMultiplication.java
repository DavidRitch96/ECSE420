package ca.mcgill.ecse420.a1;

import static java.util.concurrent.Executors.newFixedThreadPool;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class MatrixMultiplication {

    //private static final int NUMBER_THREADS = 10;
    private static final int MATRIX_SIZE = 2000;
    
    

    public static void main(String[] args) throws IOException {

            // Generate two random matrices, same size
            
            
            BufferedWriter writer = new BufferedWriter(new FileWriter("VariedMatrixSize.csv"));
            writer.write("matrixSize,msSequential,msParallel(n=8)");
            writer.newLine();
            for (int i : new int[] {100, 200, 500, 1000, 2000, 4000}) {
            	double[][] a = generateRandomMatrix(i, i);
                double[][] b = generateRandomMatrix(i, i);
            	System.out.println(i);
            	writer.write(i+","+timeMatrixMultiplication(false, a, b, 0)+","+timeMatrixMultiplication(true, a, b, 8));
            	writer.newLine();
            }
            writer.close();
            //System.out.println("Sequential: " + timeMatrixMultiplication(false, a, b));
            //System.out.println("Parallel: " + timeMatrixMultiplication(true, a, b));
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
                //dot product of a[i] and b[][j]
                for (int k = 0; k < a[0].length; k++)
                    result[i][j] += (a[i][k] * b[k][j]);
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

            //constructor
            public DotProductTask(int row, int col) {
                this.row = row;
                this.col = col;
            }

            public void run() {
                //dot product of a[i] and b[][j]
                for (int k = 0; k < a[0].length; k++)
                    result[row][col] += (a[row][k] * b[k][col]);
            }
        }
        
        ExecutorService executor = newFixedThreadPool(threads);
        for(int i = 0; i < result.length; i++) {
            for (int j = 0; j < result[0].length; j++) {
                executor.execute(new DotProductTask(i, j));
            }
        }
        executor.shutdown();
        try {
			executor.awaitTermination(120, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
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
    public static long timeMatrixMultiplication(Boolean isParallel, double[][] a, double[][] b, int threads) {
    	long startTime = System.currentTimeMillis();
    	if (isParallel)
    		parallelMultiplyMatrix(a, b, threads); //this blocks until the computation is complete
    	else
    		sequentialMultiplyMatrix(a, b); //this also blocks until the computation is complete
    	long endTime = System.currentTimeMillis();
    	
    	return endTime - startTime;
    }

}