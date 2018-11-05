package ca.mcgill.ecse420.a2;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.lang.Thread;

public class FilterLock implements Lock {

  // declare
  private AtomicInteger[] level;
  private AtomicInteger[] victim;
  final int n;

  public FilterLock(int n) {
    // initialize
    this.n = n;
    level = new AtomicInteger[n];
    victim = new AtomicInteger[n];
    for (int i = 0; i < n; i++) {
      level[i] = new AtomicInteger();
      victim[i] = new AtomicInteger();
    }
  }
  
  @Override
  public void lock() {
    int i = (int) Thread.currentThread().getId() % n;
    for (int L = 1; L < n; L++) {
      level[i].set(L);
      victim[L].set(i);
      for (int j = 0; j < n; j++ ) {
        while ((j != i)
            && (level[j].get() >= L
            && victim[L].get() == i)) {
          // wait in spin
        }
      }
    }
  }
  
  @Override
  public void unlock() {
    int i = (int) Thread.currentThread().getId() % n;
    level[i].set(0);
  }

  @Override
  public void lockInterruptibly() throws InterruptedException {
    // TODO Auto-generated method stub
  }

  @Override
  public Condition newCondition() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean tryLock() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean tryLock(long arg0, TimeUnit arg1) throws InterruptedException {
    // TODO Auto-generated method stub
    return false;
  }
}
