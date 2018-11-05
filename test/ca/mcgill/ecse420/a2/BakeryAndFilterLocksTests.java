package ca.mcgill.ecse420.a2;

import static java.util.concurrent.Executors.newFixedThreadPool;
import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BakeryAndFilterLocksTests {

  final int N = 9;
  int balance = 0;
  BakeryAndFilterLocksTests account;
  
  public int getBalance() {
    return balance;
  }

  public void setBalance(int balance) {
    this.balance = balance;
  }

  
  // TEST METHODS
  @BeforeEach
  void setUp() throws Exception {
    account = new BakeryAndFilterLocksTests();
  }

  @AfterEach
  void tearDown() throws Exception {
  }

  @Test
  void testBakeryLock() {
    
    Lock myBakeryLock = new BakeryLock(N);
    ExecutorService executor = newFixedThreadPool(N);
    
    
    for (int i = 0; i < N; i++) {
      executor.execute(new IncrementBalanceTask(account, 1, myBakeryLock));
    }
    
    executor.shutdown();
    try {
      executor.awaitTermination(5, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    assertEquals(N, account.getBalance());
    
  }

  @Test
  void testFilterLock() {
    
    Lock myFilterLock = new FilterLock(N);
    ExecutorService executor = newFixedThreadPool(N);
    
    
    for (int i = 0; i < N; i++) {
      executor.execute(new IncrementBalanceTask(account, 1, myFilterLock));
    }
    
    executor.shutdown();
    try {
      executor.awaitTermination(5, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    assertEquals(N, account.getBalance());
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
