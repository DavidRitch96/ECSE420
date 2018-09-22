package ca.mcgill.ecse420.a1;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static java.util.concurrent.Executors.newFixedThreadPool;
import java.util.concurrent.TimeUnit;

public class MatrixMultiplication {

    private static final int NUMBER_THREADS = 10;
    private static final int MATRIX_SIZE = 2000;
    
    

    public static void main(String[] args) {

            // Generate two random matrices, same size
            double[][] a = generateRandomMatrix(111, 22);
            double[][] b = generateRandomMatrix(22, 123);
            //double[][] rslt = sequentialMultiplyMatrix(a, b);
            double[][] rslt = parallelMultiplyMatrix(a, b);
            
            printMatrix(a);
            printMatrix(b);
            printMatrix(rslt);
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
    public static double[][] parallelMultiplyMatrix(double[][] a, double[][] b) {
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
        
        ExecutorService executor = newFixedThreadPool(NUMBER_THREADS);
        for(int i = 0; i < result.length; i++) {
            for (int j = 0; j < result[0].length; j++) {
                executor.execute(new DotProductTask(i, j));
            }
        }
        executor.shutdown();
        //executor.awaitTermination(,);
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

}