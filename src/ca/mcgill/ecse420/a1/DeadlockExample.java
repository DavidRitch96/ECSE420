package ca.mcgill.ecse420.a1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.*;

public class DeadlockExample{
	
	public static Lock lock1 = new ReentrantLock();
	public static Lock lock2 = new ReentrantLock();
	
	public static void main (String[] args) {

		DealockThread1 a = new DealockThread1();
		DealockThread2 b = new DealockThread2();
		
		Thread threadA = new Thread(a);
		Thread threadB = new Thread(b);
		
		threadA.start();
		threadB.start();
	}
	
	public static class DealockThread1 implements Runnable{
		
		public void run() {
			lock1.lock();
			lock2.lock();
					
			System.out.println("hello");
	
		}
	}
	
	public static class DealockThread2 implements Runnable{
		
		public void run() {
			lock2.lock();
			lock1.lock();
					
			System.out.println("hi");
	
		}
	}
	
	
}