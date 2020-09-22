package il.co.ilrd.WaitablePQueue;

import java.util.Comparator;
import java.util.PriorityQueue;

public class WaitablePQueueSynchronized <E>
{
	public PriorityQueue<E> priqueu;
	
	public WaitablePQueueSynchronized()
	{
		priqueu = new PriorityQueue<E>();
	}
	
	
	public WaitablePQueueSynchronized(Comparator<? super E> comp)
	{
		priqueu = new PriorityQueue<E>(comp);
	}
	
	
	public void add(E e) 
	{
		synchronized (priqueu)
		{
			priqueu.add(e);
			priqueu.notifyAll();
		}
	}
	
	
	public boolean remove(E e) throws InterruptedException
	{
		synchronized (priqueu)
		{			
			return priqueu.remove(e);
		}
		
	}
	
	
	public E dequeue() throws InterruptedException 
	{
		E removed_element;
		
		synchronized (priqueu)
		{
			while(priqueu.isEmpty())
			{				
				priqueu.wait();
			}
			removed_element = priqueu.poll();
		}
		
		return removed_element;
	}
}
