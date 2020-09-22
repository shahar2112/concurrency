package il.co.ilrd.ThreadPool;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import il.co.ilrd.WaitablePQueue.WaitablePQueue;

public class ThreadPool implements Executor
{
	private WaitablePQueue<ThreadTask<?>> taskQueue;
	private ArrayList<WorkerThread> threadArray;
	private Semaphore shutDownSem;
	private Semaphore pauseSem;
	private int numOfThreads;
	private boolean isShutDown;
	private boolean isPaused;
	//In order to preserve abstraction - fake priorities for special tasks
	private final static int SUPER_HIGH_PRIORITY = 10;
	private final static int SUPER_LOW_PRIORITY = -10;
	
	//constructor
	public ThreadPool(int numOfThreads)
	{
		checkValidNumOfThreads(numOfThreads);
		
		taskQueue = new WaitablePQueue<ThreadTask<?>>();
		threadArray = new ArrayList<WorkerThread>(numOfThreads);
		shutDownSem = new Semaphore(0);
		pauseSem = new Semaphore(0);
		this.numOfThreads = numOfThreads;
		isShutDown = false;
		isPaused = false;
		
		threadCreator();
	}
	
	
	@Override
	//submit and execute methods adds and executes a task to queue. 
	//submit returns a future
	public void execute(Runnable command) 
	{
		Callable<?> callcommand = Executors.callable(command, null);
		submit(callcommand, Priority.NORMAL.ordinal());
	}
	
	public <E> Future<E> submit(Runnable command, Priority priority)
	{
		Callable<E> callcommand = Executors.callable(command, null);
		return submit(callcommand, priority.ordinal());
	}
	
	public <E> Future<E> submit(Callable<E> command)
	{
		return submit(command, Priority.NORMAL.ordinal());
	}
	
	public <E> Future<E> submit(Callable<E> command, Priority priority)
	{
		return submit(command, priority.ordinal());
	}
	
	
	private <E> Future<E> submit(Callable<E> command, int priority)
	{
		if(!isShutDown)
		{	
			ThreadTask<E> task = new ThreadTask<E>(command, priority);
			taskQueue.add(task);
			
			return task.futureTask;
		}
		throw new UnsupportedOperationException("not allowed to submit after shutdown");
	}
	
	
	//stops trying to dequeue tasks from the queue
	public <E> void pause()
	{
		Callable<E> pauseCommand = () -> { pauseSem.acquire(); 
		return null;};

		for(int i = 0; i < this.numOfThreads; ++i)
		{			
			submit(pauseCommand, SUPER_HIGH_PRIORITY);
		}
		
		isPaused = true;
	}
	
	
	//resume dequeue tasks from the queue
	public void resume()
	{
		if(isPaused)
		{			
			pauseSem.release(numOfThreads);
		}
		
		isPaused = false;
	}
	
	
	//set the number of threads
	public void setNumOfThreads(int newNumOfThreads)
	{
		int threadsRange = newNumOfThreads - numOfThreads;
		
		checkValidNumOfThreads(newNumOfThreads);

		if(threadsRange > 0)
		{
			addThreads(threadsRange);
		}
		else
		{	
			removeThreads(Math.abs(threadsRange));
		}
	}
	

	

	/* Previously submitted tasks are executed, but no new tasks will be accepted.
	 * this method does not wait for previously submitted task to complete execution.
	 * use awaitTermination to do that. */
	public <E> void shutDown()
	{
		int numOfShutDownTasks = this.numOfThreads;
		if(isPaused)
		{
			resume();
		}
		Callable<E> shutDownCommand = () -> 
		{
			safeRemove();
			shutDownSem.release();
			return null;
		};
		for(int i = 0; i < numOfShutDownTasks; ++i)
		{	
			submit(shutDownCommand, SUPER_LOW_PRIORITY);
		}
		isShutDown = true;
	}
	
	
	// Blocks until all tasks completed after calling shutdown or the timeout occurs.
	public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException
	{
		return shutDownSem.tryAcquire(numOfThreads, timeout, unit);
	}

	
	public int getNumOfThreads()
	{
		return this.numOfThreads;
	}
	
	/*********************************/
	private class WorkerThread extends Thread
	{
		private boolean isStoppedRunning;
		
		@Override
		public void run()
		{
			while(!isStoppedRunning)
			{
				try {
					ThreadPool.this.taskQueue.dequeue().run();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	
	/*********************************/
	private class ThreadTask<E> implements Comparable<ThreadTask<E>>
	{
		private int priority;
		private Callable<E> callable;
		private FutureTaskImp futureTask;
		private Semaphore finishTaskSem;
		private boolean isTaskDone;
		private boolean isTaskCancelled;
		
		ThreadTask(Callable<E> callableObj, int priority)
		{
			this.priority = priority;
			this.callable = callableObj;
			this.finishTaskSem = new Semaphore(0);
			futureTask = new FutureTaskImp();
			isTaskDone = false;
			isTaskCancelled = false;
		}
		
		
		private void run() throws Exception 
		{
			if(!isTaskCancelled)
			{
				this.futureTask.retVal = this.callable.call();
				finishTaskSem.release();
				isTaskDone = true;
			}
		}
	
		
		@Override
		public int compareTo(ThreadTask<E> task) 
		{
			return task.priority - this.priority;
		}
		
		
		/********************/
		class FutureTaskImp implements Future<E>
		{
			E retVal;
			
			@Override
			public boolean cancel(boolean mayInterruptIfRunning) 
			{
				if (ThreadTask.this.isTaskDone || ThreadTask.this.isTaskCancelled)
				{
					return false;
				}
				ThreadTask.this.isTaskCancelled = true;
				
				return true;
			}
	
			//returns the futuretask return value
			@Override
			public E get() throws InterruptedException, ExecutionException
			{
				checkIfCancled();
				ThreadTask.this.finishTaskSem.acquire();
				ThreadTask.this.finishTaskSem.release(); //for the option to call "get" method a few times
	
				return retVal;
			}
	

			@Override
			public E get(long timeout, TimeUnit unit) throws
			InterruptedException, ExecutionException, TimeoutException
			{
				checkIfCancled();
				boolean didAquire = ThreadTask.this.finishTaskSem.tryAcquire(timeout, unit);
				
				if(!didAquire)
				{				
					throw new TimeoutException();
				}
				
				ThreadTask.this.finishTaskSem.release();
				
				return retVal;
			}
	
			
			@Override
			public boolean isCancelled()
			{
				return ThreadTask.this.isTaskCancelled;
			}
	
			
			@Override
			public boolean isDone() 
			{
				return ThreadTask.this.isTaskDone;
			}	
			
			
			private void checkIfCancled() 
			{
				if(isTaskCancelled)
				{
					throw new CancellationException("This task was canceled");
				}
			}
		}
	}

/*********************************/
	public enum Priority
	{
		LOW,
		NORMAL,
		HIGH,
	}

/**********Utility functions***********/
	private void threadCreator()
	{
		for(int i = 0; i < numOfThreads; ++i)
		{
			threadArray.add(i, new WorkerThread());
			threadArray.get(i).start();
		}
	}
	
	
	private void addThreads(int threadsRange)
	{	
		for(int i = 0; i < threadsRange; ++i)
		{
			threadArray.add(new WorkerThread());
			threadArray.get(threadArray.size() -1).start();
		}
		
		numOfThreads += threadsRange;
	}
	
	
	private <E> void removeThreads(int threadsRange)
	{
		Callable<E> isStoppedRunningCommand = () -> 
		{  
			safeRemove();
			return null;
		};
		for(int i = 0; i < threadsRange; ++i)
		{		
			submit(isStoppedRunningCommand, SUPER_HIGH_PRIORITY);
		}
	}
	
	
	private void safeRemove()
	{
		synchronized (threadArray) 
		{ 
			int index = threadArray.indexOf(WorkerThread.currentThread());
			threadArray.get(index).isStoppedRunning = true;
			threadArray.remove(index);
		}
		--this.numOfThreads;
	}
	
	
	private void checkValidNumOfThreads(int numOfThreads)
	{
		if(numOfThreads <= 0)
		{
			throw new IllegalArgumentException("choose a number greater than 0");
		}
	}
}

