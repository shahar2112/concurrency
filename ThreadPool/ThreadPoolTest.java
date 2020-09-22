package il.co.ilrd.ThreadPool;

import static org.junit.jupiter.api.Assertions.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import il.co.ilrd.ThreadPool.ThreadPool.Priority;


class ThreadPoolTest 
{
	ThreadPool myThreadPool;
	final static int NUMOFTHREADS = 5;
	
	@BeforeEach
	void testThreadPool() 
	{
		myThreadPool = new ThreadPool(NUMOFTHREADS);
	}

	@Test
	void testExecute() 
	{
		Runnable testCommand = () -> { System.out.println("testExecute");};
		myThreadPool.execute(testCommand);
	}

	
	@Test
	void testSubmitRunnablePriority() 
	{
		myThreadPool.pause();
		
		Runnable testCommand = () -> { System.out.println("testSubmitRunnable - Low");};
		myThreadPool.submit(testCommand, Priority.LOW);
		
		Runnable test2Command = () -> { System.out.println("testSubmitRunnable - High");};
		myThreadPool.submit(test2Command, Priority.HIGH);
		
		myThreadPool.resume();
	}

	
	@Test
	void testSubmitCallableOfE() 
	{	
		Callable<String> testCommand = () -> { System.out.println("testSubmitCallable"); return null;};
		myThreadPool.submit(testCommand);		
	}

	
	@Test
	void testSubmitCallableOfEPriority() throws InterruptedException 
	{
		myThreadPool.pause();
		
		Callable<String> test2Command = () -> { System.out.println("testSubmitCallable - Low"); return null;};
		myThreadPool.submit(test2Command, Priority.LOW);
		
		Callable<String> testCommand = () -> { System.out.println("testSubmitCallable - High"); return null;};
		myThreadPool.submit(testCommand, Priority.HIGH);
		
		myThreadPool.setNumOfThreads(1);
		
		Callable<String> test3Command = () -> { System.out.println("testSubmitCallable - Norm"); return null;};
		myThreadPool.submit(test3Command, Priority.NORMAL);
		
		myThreadPool.resume();
	}

	
	@Test
	void testPause()
	{
		myThreadPool.pause();
		
		Callable<String> testCommand = () -> { System.out.println("should not print this"); return null;};
		myThreadPool.submit(testCommand, Priority.HIGH);
		
		Callable<String> test2Command = () -> { System.out.println("should not print this"); return null;};
		myThreadPool.submit(test2Command, Priority.LOW);
		
		Callable<String> test3Command = () -> { System.out.println("should not print this"); return null;};
		myThreadPool.submit(test3Command, Priority.NORMAL);
	}
	

	@Test
	void testResume() 
	{
		myThreadPool.pause();
		
		Callable<String> test2Command = () -> { System.out.println("testinglow"); return null;};
		myThreadPool.submit(test2Command, Priority.LOW);
		
		Callable<String> testCommand = () -> { System.out.println("testinghigh"); return null;};
		myThreadPool.submit(testCommand, Priority.HIGH);
		
		myThreadPool.resume();
		
		Callable<String> test3Command = () -> { System.out.println("testingNorm"); return null;};
		myThreadPool.submit(test3Command, Priority.NORMAL);	
	}
	

	@Test
	void testSetNumOfThreads() throws InterruptedException 
	{
		assertEquals(NUMOFTHREADS, myThreadPool.getNumOfThreads());
		myThreadPool.setNumOfThreads(8);
		Thread.sleep(200);
		assertEquals(8, myThreadPool.getNumOfThreads());

		myThreadPool.setNumOfThreads(2);
		Thread.sleep(200);
		assertEquals(2, myThreadPool.getNumOfThreads());
		
		 assertThrows(IllegalArgumentException.class, () -> {
			 myThreadPool.setNumOfThreads(-2);
		    });
		
	}
	

	@Test
	void testShutDown() throws InterruptedException, ExecutionException 
	{
		Future<?>[] tasks = new Future<?>[10];
		
		for(int i = 0; i < 10; ++i)
		{
			final int k = i;
			Callable<Integer> testCommand = () -> { return k;};
			tasks[i] = myThreadPool.submit(testCommand, Priority.LOW);
		}
		
		myThreadPool.shutDown();
		myThreadPool.awaitTermination(20, TimeUnit.SECONDS);
		
		Callable<String> testCommand = () -> { System.out.println("not suppose to print"); return "hi";};
		;
		
		 assertThrows(UnsupportedOperationException.class, () -> {
			 myThreadPool.submit(testCommand, Priority.LOW);
		    });

		 assertEquals(0, myThreadPool.getNumOfThreads());
	}
	
	
	
	@Test
	void testCancel() 
	{
		myThreadPool.pause();
		
		Callable<String> testCommand = () -> { System.out.println("testCancel - Low"); return "hi";};
		myThreadPool.submit(testCommand, Priority.LOW);
		
		Callable<Void> test2Command = () -> { System.out.println("testCancel - taskToCancle"); return null;};
		Future<Void> task2 = myThreadPool.submit(test2Command, Priority.HIGH);
		
		Callable<Integer> test3Command = () -> { System.out.println("testCancel - Norm"); return 123;};
		myThreadPool.submit(test3Command, Priority.NORMAL);
		
		task2.cancel(false);
		myThreadPool.resume();
	}
	
	
	@Test
	void testGet() throws InterruptedException, ExecutionException
	{
		Callable<Integer> testCommand = () -> { return 123;};
		Future<Integer> task = myThreadPool.submit(testCommand);
		
		assertEquals(123, task.get());
		assertEquals(123, task.get());
	}
	

	@Test
	void testGetTimeOut() throws InterruptedException, ExecutionException, TimeoutException
	{
		Callable<Integer> testCommand = () -> { return 123456;};
		Future<Integer> task = myThreadPool.submit(testCommand);
		
		assertEquals(123456, task.get(100, TimeUnit.MILLISECONDS));
		
		Callable<Integer> test2Command = () -> { Thread.sleep(500) ;return 123456;};
		Future<Integer> task2 = myThreadPool.submit(test2Command);
		
		 assertThrows(TimeoutException.class, () -> {
			 task2.get(100, TimeUnit.MILLISECONDS);
		    });
	}

	
	@Test
	void testIsCancelled()
	{
		Callable<Integer> testCommand = () -> { return 123456;};
		Future<Integer> task = myThreadPool.submit(testCommand);
		
		task.cancel(true);
		assertTrue(task.isCancelled());
	}
	
	
	@Test
	void testIsDone() throws InterruptedException
	{
		myThreadPool.pause();
		Callable<Integer> testCommand = () -> { return 123456;};
		Future<Integer> task = myThreadPool.submit(testCommand);
		
		assertFalse(task.isDone());
		
		myThreadPool.resume();
		Thread.sleep(100);
		assertTrue(task.isDone());
	}

}



