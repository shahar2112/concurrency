package il.co.ilrd.Concurrency;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

class ThreadEx extends Thread
{
	int i;
	
	@Override
	public void run()
	{
		while(!Thread.interrupted())
		{
			System.out.println("Extending Thread thread is running " + i++);
		}
		System.out.println("Extending thread stopped");
	}
}


class Threadrun implements Runnable
{
	int i;
	
	@Override
	public void run() 
	{
		while(!Thread.interrupted())
		{
			System.out.println("Implementing runnable thread is running " + i++);
		}
		System.out.println("Implementing thread stopped");
	}
}



class Ex2 implements Runnable
{
	static int counter;
	static AtomicInteger counter2 = new AtomicInteger(0);
	static final ReentrantLock lock = new ReentrantLock();
	
	@Override
	public void run() 
	{
		for(int i = 0; i < 1000000; i++)
		{
			regular();
			//synchro();
			//synchroBlock();
			//atomic();
			//reentrant();
		}
	}

	void regular() 
	{
		++counter;
	}
	
	static synchronized void synchro() 
	{
		++counter;
	}
	
	void synchroBlock() 
	{
		synchronized(Ex2.class)
		{

			++counter;

		}
	}
	
	void atomic() 
	{
		counter2.incrementAndGet();

	}
	
	void reentrant() 
	{
		lock.lock();
		++counter;
		lock.unlock();
	}
}



