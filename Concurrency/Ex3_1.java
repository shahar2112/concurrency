package il.co.ilrd.Concurrency;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Ex3_1
{
	static Semaphore Sem1 = new Semaphore(1);
	static Semaphore Sem2 = new Semaphore(0);
	static AtomicInteger flag = new AtomicInteger(0);

	
class Ex3ping implements Runnable
{
	
	@Override
	public void run() 
{	
		while(true)
		{
			Sem1.acquireUninterruptibly();
			try {
				System.out.println("-->" + Thread.currentThread().getName() + " is printing PING");
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Sem2.release();
		}
	}
	
	
}




class Ex3pong implements Runnable
{
	
	@Override
	public void run() 
	{	
		while(true)
		{
			Sem2.acquireUninterruptibly();
			try {
				System.out.println(Thread.currentThread().getName() + " is printing PONG <--");
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Sem1.release();
		}
	}
}





class Ex3pingAtomic implements Runnable
{
	@Override
	public void run() 
	{	
		while(true)
		{ 
			if(flag.get() == 0)
			{	
				try {
					System.out.println("-->" + Thread.currentThread().getName() + " is printing PING");
					TimeUnit.SECONDS.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
					flag.set(1);
			}
		}
	}

}




class Ex3pongAtomic implements Runnable
{
	
	@Override
	public void run() 
	{	
		while(true)
		{
			if(flag.get() == 1)
			{
			try {
				System.out.println(Thread.currentThread().getName() + " is printing PONG <--");
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
				flag.set(0);
			}
		}
	}
}

}
