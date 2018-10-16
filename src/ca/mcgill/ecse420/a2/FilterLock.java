package ca.mcgill.ecse420.a2;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class FilterLock implements Lock {

  @Override
  public void lock() {
    // TODO Auto-generated method stub
    
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
    // TODO Auto-generated method stub
    
  }

}
