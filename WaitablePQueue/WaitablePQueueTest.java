package il.co.ilrd.WaitablePQueue;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WaitablePQueueTest {

	WaitablePQueue<Integer> waitablePQueue;
	Runnable runnable;
	Thread thread_arr[];
	
	@BeforeEach
	void BeforeEach()
	{
		waitablePQueue = new WaitablePQueue<Integer>();
		thread_arr = new Thread[100];
	}
	
	@Test
	void testAdd() throws InterruptedException
	{
		
		for(int i = 0; i < 100; ++i)
		{		
			Integer k;
			k = i;
			thread_arr[i] = new Thread(runnable = () -> { waitablePQueue.add(k); });
			thread_arr[i].start();
		}
		
		for(int i = 0; i < 100; ++i)
		{		
			thread_arr[i].join();
		}
		
		
	assertEquals(100, waitablePQueue.priqueu.size());
	}

	@Test
	void testRemove() throws InterruptedException 
	{
		
		for(int i = 0; i < 5; ++i)
		{		
			Integer k;
			k = i;
			thread_arr[i] = new Thread(runnable = () -> { waitablePQueue.add(k); });
			thread_arr[i].start();
		}
		
		for(int i = 0; i < 5; ++i)
		{		
			thread_arr[i].join();
		}
		
		assertEquals(5, waitablePQueue.priqueu.size());
		
		for(int i = 0; i < 5; ++i)
		{		
			Integer k;
			k = i;
			thread_arr[i] = new Thread(runnable = () -> { try {
				waitablePQueue.remove(k);
			} catch (InterruptedException e) {

				e.printStackTrace();
			} });
			thread_arr[i].start();
		}
		
		for(int i = 0; i < 5; ++i)
		{		
			thread_arr[i].join();
		}

		
		assertEquals(0, waitablePQueue.priqueu.size());
	}

	
	@Test
	void testDequeue() throws InterruptedException 
	{
		for(int i = 0; i < 5; ++i)
		{		
			thread_arr[i] = new Thread(runnable = () -> { try {
				waitablePQueue.dequeue();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} });
			thread_arr[i].start();
		}
		
		for(int i = 5; i < 55; ++i)
		{		
			Integer k;
			k = i;
			thread_arr[i] = new Thread(runnable = () -> { waitablePQueue.add(k); });
			thread_arr[i].start();
		}
		
		for(int i = 0; i < 55; ++i)
		{		
			thread_arr[i].join();
		}

		assertEquals(10, waitablePQueue.priqueu.peek());
		assertEquals(45, waitablePQueue.priqueu.size());
	}

}
