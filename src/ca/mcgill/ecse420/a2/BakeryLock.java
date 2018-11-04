package ca.mcgill.ecse420.a2;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class BakeryLock implements Lock {

  private AtomicBoolean[] flag;
  private AtomicInteger[] label;
  final int n;
  
  public BakeryLock(int n) {
    this.n = n;
    flag = new AtomicBoolean[n];
    label = new AtomicInteger[n];
    for (int i = 0; i < n; i++) {
      flag[i] = new AtomicBoolean();
      label[i] = new AtomicInteger();
    }
  }
  
  @Override
  public void lock() {
    int i = (int) Thread.currentThread().getId() % n;
    flag[i].set(true);
    
    // find largest value in array
    int max = Integer.MIN_VALUE;
    for (AtomicInteger ai : label) {
      if (ai.get() > max) {
        max = ai.get();
      }
    }
    
    label[i].set(max + 1);
    for (int k = 0; k < n; k++) {
      while ((k != i) && flag[k].get() && ((label[k].get() < label[i].get())
          || ((label[k].get() == label[i].get()) && k < i))) {
        //wait in spin 
      }
    }

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

  @Override
  public void unlock() {
    int i = (int) Thread.currentThread().getId() % n;
    flag[i].set(false);

  }

}
