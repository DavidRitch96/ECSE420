package ca.mcgill.ecse420.a1;

import java.util.concurrent.locks.ReentrantLock;

public class DiningPhilosophersWithDeadlock {
  public static final int NUM_PHILOSOPHERS = 5;

  public static void main(String[] args) {
    Philosopher[] philosophers = new Philosopher[NUM_PHILOSOPHERS];
    ReentrantLock[] chopsticks = new ReentrantLock[NUM_PHILOSOPHERS];
		
    // Assigning the locks
    for (int i = 0; i < chopsticks.length; i++) {
      chopsticks[i]=new ReentrantLock();
    }
		
    // Creating each philosopher
    for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
      philosophers[i] = new Philosopher(i, chopsticks[i], chopsticks[(i + 1) % NUM_PHILOSOPHERS]);
    }
		
    Thread[] threads = new Thread[NUM_PHILOSOPHERS];
		
    for (int i = 0; i < threads.length; i++) {
      threads[i]=new Thread(philosophers[i]);
      threads[i].start();
    }
  }

  public static class Philosopher implements Runnable {
    private int i;
    private ReentrantLock leftChop;
    private ReentrantLock rightChop;

    // Constructor
    public Philosopher(int i, ReentrantLock leftChop, ReentrantLock rightChop) {
      this.i=i;
      this.leftChop=leftChop;
      this.rightChop=rightChop;
    }
		
    @Override
    public void run() {
      while (true) {
        eat();
        think();
      }
    }
		
    public void eat() {
      System.out.println("Philosopher "+i+" is waiting for left chopstick.");
      leftChop.lock();
      System.out.println("Philosopher "+i+" picked up left chopstick.");
      System.out.println("Philosopher "+i+" is waiting for right chopstick.");
      rightChop.lock();
      System.out.println("Philosopher "+i+" picked up right chopstick.");
      System.out.println("Philosopher "+i+" is eating.");
      leftChop.unlock();
      rightChop.unlock();
    }
		
    public void think() {
      System.out.println("Philosopher "+i+" is thinking.");
    }
  }
}