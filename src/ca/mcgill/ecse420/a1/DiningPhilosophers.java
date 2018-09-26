package ca.mcgill.ecse420.a1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DiningPhilosophers {
	public static final int NUM_PHILOSOPHERS = 5;

	
	public static void main(String[] args) {

		
		Philosopher[] philosophers = new Philosopher[NUM_PHILOSOPHERS];
		ReentrantLock[] chopsticks = new ReentrantLock[NUM_PHILOSOPHERS];
		
		//Assigning the locks
		for (int i = 0; i < chopsticks.length; i++) {
			chopsticks[i]=new ReentrantLock();
		}
		
		//Creating each philosopher
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


		//constructor
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
			leftChop.lock();
			if (rightChop.tryLock()){
				System.out.println("Philosopher "+i+" is eating.");
				try {
					Thread.sleep((long)(Math.random() * 1000));
				} catch(InterruptedException e) {
				    System.out.println("Eat thread interrupted");
				}
				leftChop.unlock();
				rightChop.unlock();
			} else {
				leftChop.unlock();
				
			}
			
		}
		
		public void think() {
			try {
				Thread.sleep((long)(2+Math.random() * 1000));
			} catch(InterruptedException e) {
			    System.out.println("Think thread interrupted");
			}
		}


	}

}


/*
 * 
 * left chopstick is always locked first
 * then the philosopher tries to lock the righ chopstick
 * 
 * solutions for deadlock
 * -Releasing all resources if the eat operation cannot be started
 * 		Before eating, the left chopstick is always locked first. the, philosopher tries to lock the right chopstick. If cant lock right, release left and sleep for random amount of time.
 * 
 * Making starvation free
 * -solution-> each philosopher must wait a while before eating again
 * -another soln-> each philosopher takes care of his neighbors. If neighbors are waiting, he will wake them when he is fininshed eating
 * 
 * -another soln-> using javas built in thing
 *  
 *  
 *  4.1
 *  
 *  Sn = (1/ (s+((1-s)/n))), lim Sn n->inf = ?
 *  
 *  4.2/4.3
 *  
 *  Sn = (1/ (s+((1-s)/n))), Sn' = (1/ ((s/k)+((1-(s/k))/n)))*/
