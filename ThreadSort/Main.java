package il.co.ilrd.ThreadSort;

import java.awt.List;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

import org.junit.experimental.max.MaxCore;
import org.junit.jupiter.api.io.TempDir;
import org.omg.CORBA.PUBLIC_MEMBER;
import org.omg.IOP.MultipleComponentProfileHelper;

import il.co.ilrd.ThreadSort.Dictionary.sortingThreads;

public class Main {

	public static void main(String[] args) throws InterruptedException 
	{
		FileConvertor convertor = new FileConvertor("/home/bla/shahar-maimon/java/myProjects/src/il/co/ilrd/ThreadSort/words_to_sort");
		Dictionary dict = new Dictionary(convertor.getFileArray());
		
		dict.printFileArr();
		System.out.println("-->That was a Print of the file before sorting");
		
		TimeUnit.SECONDS.sleep(3);
		dict.unsort();
		dict.printFileArr();
		System.out.println("->That was a print after unsorting file");
		
		TimeUnit.SECONDS.sleep(3);
		dict.bucketSort();
		dict.printFileArr();;
		System.out.println("-->That was a print after my sort :)");

	}

}

	