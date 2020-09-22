package bank;

import java.util.List;

public class TransferThread implements Runnable
{	
	private Bank bank;
	private List<Account> accounts;
	private static final int AMOUNT = 10;
	
	public TransferThread(List<Account> accounts, Bank bank)
	{
		this.bank = bank;
		this.accounts = accounts;
	}
	
	@Override
	public void run() 
	{
		while(true)
		{
			Account from = accounts.get((int) (Math.random() * 10 %accounts.size()));
			Account to = accounts.get((int) (Math.random() * 10 %accounts.size()));
			if(from == to)
			{
				continue;
			}
			  
		  System.out.println("transfering from: " + from.getDetails() + " To: " +
		  to.getDetails()); 
		  bank.transfer(from, to, AMOUNT);
		  try 
		  {
			 Thread.sleep(10);
		  } catch (InterruptedException e)
		  {
			  e.printStackTrace();
		  }
		}	
	}
}


