package il.co.ilrd.Concurrency;

import java.util.LinkedList;
import java.util.concurrent.Semaphore;

public class Ex3_2 
{
	LinkedList<Integer> list;
	Semaphore sem;
	
	public Ex3_2()
	{
		this.list = new LinkedList<Integer>();
		this.sem = new Semaphore(0);
	}
	
	class Part1producer implements Runnable
	{
		@Override
		public void run()
		{
			while(true)
			{	
				synchronized (Ex3_2.this.list)
				{
					list.add(5);
					System.out.println(Thread.currentThread().getName() + " adding to list");
				}
			}
		}
	}
	
	
	class Part1consumer implements Runnable
	{
		@Override
		public void run()
		{
			while(true)
			{
				synchronized (Ex3_2.this.list) 
				{
					if(!list.isEmpty())
					{
						list.remove();
						System.out.println(Thread.currentThread().getName() + " removing from list");
					}
				}
			}
		}
	}
	
	
	class Part2producer implements Runnable
	{
		@Override
		public void run()
		{
			while(true)
			{
				synchronized (Ex3_2.this.list)
				{
					list.add(5);
					System.out.println(Thread.currentThread().getName() + " adding to list");
					sem.release();
				}
			}
		}
	}
	
	class Part2consumer implements Runnable
	{
		@Override
		public void run()
		{
			while(true)
			{
				sem.acquireUninterruptibly();
				synchronized (Ex3_2.this.list)
				{	
					list.remove();
					System.out.println(Thread.currentThread().getName() + " removing from list");
				}
			}
		}
	}

}
