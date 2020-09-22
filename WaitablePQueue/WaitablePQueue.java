package il.co.ilrd.WaitablePQueue;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

public class WaitablePQueue<E> 
{
	public PriorityQueue<E> priqueu;
	private Semaphore sem;
	private ReentrantLock mutex;
	
	public WaitablePQueue()
	{
		priqueu = new PriorityQueue<E>();
		sem = new Semaphore(0);
		mutex = new ReentrantLock();
	}
	
	public WaitablePQueue(Comparator<? super E> comp)
	{
		priqueu = new PriorityQueue<E>(comp);
		sem = new Semaphore(0);
		mutex = new ReentrantLock();
	}
	
	public void add(E e) 
	{
		mutex.lock();
		priqueu.add(e);
		sem.release();
		mutex.unlock();
	}
	
	
	public boolean remove(E e) throws InterruptedException
	{
		mutex.lock();
		sem.acquire();
		
		boolean didremove = priqueu.remove(e);
		
		if(didremove == false)
		{
			sem.release();
		}
		mutex.unlock();
		
		return didremove;
	}
	
	
	public E dequeue() throws InterruptedException 
	{
		E removed_element;
		
		sem.acquire();
		mutex.lock();
		removed_element = priqueu.poll();
		mutex.unlock();
		
		return removed_element;
	}
	
}





