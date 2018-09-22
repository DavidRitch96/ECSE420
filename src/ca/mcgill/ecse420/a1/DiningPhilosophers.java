package ca.mcgill.ecse420.a1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

public class DiningPhilosophers {
	
	public static void main(String[] args) {

		int numberOfPhilosophers = 5;
		Philosopher[] philosophers = new Philosopher[numberOfPhilosophers];
		Object[] chopsticks = new Object[numberOfPhilosophers];
		Thread[] threads = new Thread[numberOfPhilosophers];
		
		for (int i = 0; i < threads.length; i++) {
			threads[i]=new Thread(philosophers[i]);
			threads[i].start();
		}
	}

	public static class Philosopher implements Runnable {

		

		@Override
		public void run() {
			System.out.println("hello");
		}


	}

}
