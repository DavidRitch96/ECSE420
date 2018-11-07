package ca.mcgill.ecse420.a2;

import static java.util.concurrent.Executors.newFixedThreadPool;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

public class BakeryAndFilterLocksTests {

  final int N = 4;
  int balance = 0;
  BakeryAndFilterLocksTests account;
  
  public static void main(String[] args) throws Exception {
    // new instance of our tests
    BakeryAndFilterLocksTests tests = new BakeryAndFilterLocksTests();
    
    tests.setUp();
    tests.testBakeryLock();
    
    tests.setUp();
    tests.testFilterLock();
  }
  
  public int getBalance() {
    return balance;
  }

  public void setBalance(int balance) {
    this.balance = balance;
  }
  
  
  // methods for testing
  void setUp() {
    account = new BakeryAndFilterLocksTests();
  }

  void testBakeryLock() {
    
    // new lock
    Lock myBakeryLock = new BakeryLock(N);
    ExecutorService executor = newFixedThreadPool(N);
    
    // create a bunch of increment tasks
    for (int i = 0; i < N; i++) {
      executor.execute(new IncrementBalanceTask(account, 1, myBakeryLock));
    }
    
    // wait for executor to shut down
    executor.shutdown();
    try {
      executor.awaitTermination(5, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    
    // validate output
    if (N == account.getBalance()) {
      System.out.println("BakeryLock Test Passed!");
    } else {
      System.err.println("BakeryLock Test Failed! Expected: " + N + " Actual: "
          +account.getBalance());
    }
    
  }

  
  void testFilterLock() {
    
    // new lock
    Lock myFilterLock = new FilterLock(N);
    ExecutorService executor = newFixedThreadPool(N);
    
    // create a bunch of increment tasks
    for (int i = 0; i < N; i++) {
      executor.execute(new IncrementBalanceTask(account, 1, myFilterLock));
    }
    
    // wait for executor to shut down
    executor.shutdown();
    try {
      executor.awaitTermination(5, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    
    // validate output
    if (N == account.getBalance()) {
      System.out.println("FilterLock Test Passed!");
    } else {
      System.err.println("FilterLock Test Failed! Expected: " + N + " Actual: " 
          + account.getBalance());
    }
  }
}

class IncrementBalanceTask implements Runnable {
  BakeryAndFilterLocksTests account;
  int transaction;
  Lock lock;
  
  public IncrementBalanceTask(BakeryAndFilterLocksTests account, int transaction, Lock lock) {
    this.account = account;
    this.transaction = transaction;
    this.lock = lock;
  }
  
  public void run() {
    // acquire lock
    lock.lock();
    
    // get opening balance
    int openingBalance = account.getBalance();
    System.out.println("Thread "
        + Thread.currentThread().getId()+" reads Balance = "+openingBalance);
    System.out.flush();
    
    // simulate doing some important checks before finalizing transaction
    try {
      Thread.sleep(20);
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    // calculate and set closing balance
    int closingBalance = openingBalance + transaction;
    account.setBalance(closingBalance);
    System.out.println("Thread "
        +Thread.currentThread().getId()+" closes with balance = "+closingBalance);
    System.out.flush();
    
    // release lock
    lock.unlock();
  }
}
