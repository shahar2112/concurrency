package bank;

import java.util.ArrayList;
import java.util.List;

public class Main 
{
	public static void main(String[] args) 
	{
		List<Account> accountsList = new ArrayList<>();
		
		for(int i = 0; i < Bank.MAX_ACOUNT; ++i)
		{
			String name = "account" + i;
			Account newaccount = new Account(name, i);
			newaccount.setBalance(100);
			accountsList.add(newaccount);
		}
		
		Bank bank = new Bank(accountsList);
		
		TransferThread transfer = new TransferThread(accountsList, bank);
		for(int i = 0; i < Bank.MAX_ACOUNT; i++)
		{			
			Thread thread = new Thread(transfer);
			thread.start();
		}
	    
	}
	
	
}
