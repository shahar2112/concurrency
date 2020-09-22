package il.co.ilrd.ThreadSort;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class FileConvertor 
{
	private ArrayList<String> lines;
	private String[] dict_array;
	
	public FileConvertor(String filePath)
	{	
		convert(filePath);
	}
	
	private void convert(String filePath)
	{
		try 
		{
         lines = (ArrayList<String>) Files.readAllLines(Paths.get(filePath));
         this.dict_array = lines.toArray(new String[0]);

        } 
		catch (IOException ex) 
		{
           ex.printStackTrace();
        }
	}
	
	public String[] getFileArray()
	{
		return this.dict_array;
	}
}
