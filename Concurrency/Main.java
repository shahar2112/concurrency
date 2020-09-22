package il.co.ilrd.Concurrency;
import java.util.concurrent.TimeUnit;

public class Main {

	static int num_of_threads = 5;
	
	public static void main(String[] args) throws InterruptedException {

		//ex1();
		//ex2();
		//ex3_sem();
		//ex3_ato();
		//ex3_part1();
		//ex3_part2();
		ex3_part3();
	}
	
	public static void ex1() throws InterruptedException
	{
		ThreadEx ex1thread = new ThreadEx();
		ex1thread.start();
		
		Threadrun ex1thread2 = new Threadrun();
		Thread t2 = new Thread(ex1thread2);
		t2.start();
		
		TimeUnit.SECONDS.sleep(1);
		ex1thread.interrupt();
		t2.interrupt();
		
	}
	
	
	public static void ex2() throws InterruptedException
	{
		Ex2 ex2thread = new Ex2();
		Thread t1 = new Thread(ex2thread);
		Thread t2 = new Thread(ex2thread);
		
		long startTime = System.nanoTime();

		t1.start();
		t2.start();
		t1.join();
		t2.join();
		
		long endTime = System.nanoTime();
		long timeElapsed = endTime - startTime;
		System.out.println("Execution time in milliseconds : " + timeElapsed / 1000000);

		System.out.println("counter is " + Ex2.counter);
		//System.out.println("counter is " + Ex2.counter2.get());
	}
	
	
	
	public static void ex3_sem() throws InterruptedException
	{
		Ex3_1 outerEx3_1 = new Ex3_1();
		
		Ex3_1.Ex3ping thread1 = outerEx3_1.new Ex3ping();
		Ex3_1.Ex3pong thread2 = outerEx3_1.new Ex3pong();
		
		Thread t1 = new Thread(thread1);
		Thread t2 = new Thread(thread2);

		t1.start();
		t2.start();
		t1.join();
		t2.join();
		
	}
	
	
	
	public static void ex3_ato() throws InterruptedException
	{
		Ex3_1 outerEx3_1 = new Ex3_1();
		
		Ex3_1.Ex3pingAtomic ato_thread1 = outerEx3_1.new Ex3pingAtomic();
		Ex3_1.Ex3pongAtomic ato_thread2 = outerEx3_1.new Ex3pongAtomic();
		
		Thread ato_t1 = new Thread(ato_thread1);
		Thread ato_t2 = new Thread(ato_thread2);
		
		ato_t1.start();
		ato_t2.start();
		ato_t1.join();
		ato_t2.join();
	}
	
	
	public static void ex3_part1() throws InterruptedException
	{
		Ex3_2 outerEx3_2 = new Ex3_2();
		
		Ex3_2.Part1consumer thread1 = outerEx3_2.new Part1consumer();
		Ex3_2.Part1producer thread2 = outerEx3_2.new Part1producer();
		
		for(int i = 0 ; i < num_of_threads; ++i)
		{
			Thread t1 = new Thread(thread1);
			Thread t2 = new Thread(thread2);
			
			t1.start();
			t2.start();
			t1.join();
			t2.join();
		}
		
	}
	
	
	public static void ex3_part2() throws InterruptedException
	{
		Ex3_2 outerEx3_2 = new Ex3_2();
		
		Ex3_2.Part2consumer thread1 = outerEx3_2.new Part2consumer();
		Ex3_2.Part2producer thread2 = outerEx3_2.new Part2producer();
		
		for(int i = 0 ; i < num_of_threads; ++i)
		{
			Thread t1 = new Thread(thread1);
			Thread t2 = new Thread(thread2);
			
			t1.start();
			t2.start();
			t1.join();
			t2.join();
		}
		
	}
	
	
	public static void ex3_part3() throws InterruptedException
	{
		Ex3_3 outerEx3_3 = new Ex3_3();
		
		Ex3_3.consumers thread1 = outerEx3_3.new consumers();
		Ex3_3.producer thread2 = outerEx3_3.new producer();
		Thread[] consthreads = new Thread[num_of_threads];
		
		Thread t2 = new Thread(thread2);
		t2.start();
		
		for(int i = 0 ; i < num_of_threads; ++i)
		{
			consthreads[i] = new Thread(thread1);
			consthreads[i].start();
		}
		
		
		for(int i = 0 ; i < num_of_threads; ++i)
		{
			consthreads[i].join();
		}
		t2.join();
		
	}
	

	
	

}
