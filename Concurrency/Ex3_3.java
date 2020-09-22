package il.co.ilrd.Concurrency;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class Ex3_3
{
	private ArrayList<String> arrayList;
	private Semaphore sem;
    private final Lock lock;
    private final Condition cond;
	
	public Ex3_3()
	{
		this.arrayList = new ArrayList<String>();
		this.sem = new Semaphore(0);
		this.lock = new ReentrantLock();
		this.cond = lock.newCondition();
		
	}
	
	class producer implements Runnable
	{
		@Override
		public void run()
		{
			while(true)
			{
				
				for(int i = 0; i < Main.num_of_threads ; ++i)
				{
					sem.acquireUninterruptibly();
				}
				
				
				synchronized (Ex3_3.this.arrayList) 
				{
					arrayList.add("Hello");
					System.out.println( "\n--> " + Thread.currentThread().getName() + " I am writing " + arrayList.get(0));
				}
				
				lock.lock();
				cond.signalAll();
				lock.unlock();
				
				
				for(int i = 0; i < Main.num_of_threads ; ++i)
				{
					sem.acquireUninterruptibly();
				}
			
			}
		}
	}
	
	class consumers implements Runnable
	{
		@Override
		public void run()
		{
			while(true)
			{
				lock.lock();
				sem.release();
				
				try {
					cond.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				synchronized (Ex3_3.this.arrayList) 
				{
					System.out.println(Thread.currentThread().getName() + " I am reading " +
							arrayList.get(0) + " for the " + arrayList.size()+ " time");
				}
				sem.release();
				lock.unlock();
			}
			
			
		}
		
	}
	
	
	
}
