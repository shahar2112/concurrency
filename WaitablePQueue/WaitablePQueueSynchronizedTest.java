package il.co.ilrd.WaitablePQueue;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WaitablePQueueSynchronizedTest {

	WaitablePQueueSynchronized<Integer> WaitablePQueueSynchronized;
	Runnable runnable;
	Thread thread_arr[];
	
	@BeforeEach
	void BeforeEach()
	{
		WaitablePQueueSynchronized = new WaitablePQueueSynchronized<Integer>();
		thread_arr = new Thread[100];
	}
	
	@Test
	void testAdd() throws InterruptedException 
	{
		for(int i = 0; i < 100; ++i)
		{		
			Integer k;
			k = i;
			thread_arr[i] = new Thread(runnable = () -> { WaitablePQueueSynchronized.add(k); });
			thread_arr[i].start();
		}
		
		for(int i = 0; i < 100; ++i)
		{		
			thread_arr[i].join();
		}
		
		
	assertEquals(100, WaitablePQueueSynchronized.priqueu.size());
	}

	@Test
	void testRemove() throws InterruptedException
	{
		for(int i = 0; i < 5; ++i)
		{		
			Integer k;
			k = i;
			thread_arr[i] = new Thread(runnable = () -> { WaitablePQueueSynchronized.add(k); });
			thread_arr[i].start();
		}
		
		for(int i = 0; i < 5; ++i)
		{		
			thread_arr[i].join();
		}
		
		assertEquals(5, WaitablePQueueSynchronized.priqueu.size());
		
		for(int i = 0; i < 5; ++i)
		{		
			Integer k;
			k = i;
			thread_arr[i] = new Thread(runnable = () -> { try {
				WaitablePQueueSynchronized.remove(k);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} });
			thread_arr[i].start();
		}
		
		for(int i = 0; i < 5; ++i)
		{		
			thread_arr[i].join();
		}

		
		assertEquals(0, WaitablePQueueSynchronized.priqueu.size());
	}

	
	@Test
	void testDequeue() throws InterruptedException
	{
		for(int i = 0; i < 5; ++i)
		{		
			thread_arr[i] = new Thread(runnable = () -> { try {
				WaitablePQueueSynchronized.dequeue();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} });
			thread_arr[i].start();
		}
		
		for(int i = 5; i < 55; ++i)
		{		
			Integer k;
			k = i;
			thread_arr[i] = new Thread(runnable = () -> { WaitablePQueueSynchronized.add(k); });
			thread_arr[i].start();
		}
		
		for(int i = 0; i < 55; ++i)
		{		
			thread_arr[i].join();
		}

		assertEquals(10, WaitablePQueueSynchronized.priqueu.peek());
		assertEquals(45, WaitablePQueueSynchronized.priqueu.size());
	}

}
