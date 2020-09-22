package il.co.ilrd.ThreadSort;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Dictionary
{
	 private ArrayList<ArrayList<String>> arrayListBuckets;
	 private String[] file_array;
	 final static int NUM_OF_LETTERS = 52;
	
	 public Dictionary(String[] file_array)
	 {
		 this.file_array = file_array;
		 arrayListBuckets = new ArrayList<ArrayList<String>>(NUM_OF_LETTERS);
		 
	 }
	
	public void unsort()
	{
		Random rand = new Random();
		
		for(int i = 0; i < file_array.length; ++i)
		{
			int rand_index = rand.nextInt(file_array.length);
			 String tempString = file_array[i];
			 file_array[i] = file_array[rand_index];
			 file_array[rand_index] = tempString;
		}
	}
	
	public void printFileArr()
	{
        for (String array : file_array)
        {
            System.out.println(array);
        }

	}
	
	private void splitToBuckets()
	{
		int index = 0;
		
		for(int i = 0; i < NUM_OF_LETTERS; ++i)
		{
			arrayListBuckets.add(i, new ArrayList<String>());
		}
		
		for(String array : file_array)
		{
			if(array.charAt(0) < 'a')
			{
				index = array.charAt(0) - 65; //for cap locks ASCI
				arrayListBuckets.get(index).add(array);
			}
			else
			{
				index = array.charAt(0) - 71; // for small letters ASCI
				arrayListBuckets.get(index).add(array);
			}
		}
	}
	
	private void CreateThreads() throws InterruptedException
	{
		Thread[] threads = new Thread[NUM_OF_LETTERS];
		
		for(int i = 0; i < NUM_OF_LETTERS; ++i)
		{
			sortingThreads sortthread = new sortingThreads(i);
			threads[i] = new Thread(sortthread);
			threads[i].start();
		}
		
		for(int i = 0; i < threads.length; ++i)
		{
			threads[i].join();
		}
	}	
		
		private void returnToDictArray()
		{
			int i = 0;
			for(int j = 0 ; j < NUM_OF_LETTERS; ++j)
			{
				for(int k = 0; k < arrayListBuckets.get(j).size(); k++)
				{
					
					file_array[i] =  arrayListBuckets.get(j).get(k);
					i++;
				}
			}
			
		}
		
		public void bucketSort() throws InterruptedException
		{
			splitToBuckets();
			CreateThreads();
			returnToDictArray();
		}

		
	
	class sortingThreads implements Runnable
	{
		private int index_to_sort = 0;
		 
		public sortingThreads(int index_to_sort)
		{
			this.index_to_sort = index_to_sort;
		}
		
		@Override
		public void run()
		{
			Collections.sort(arrayListBuckets.get(index_to_sort));
		}
	}
}
