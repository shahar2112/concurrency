package bank;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Bank
{
	boolean didSucceed = false;
	Account from;
	Account to;
	Lock bankLock;
	int amount;
	List<Account> accounts;
	public static final int MAX_ACOUNT =10;
	
	public Bank(List<Account> accounts)
	{
		bankLock = new ReentrantLock();
		this.accounts = accounts;
	}
	
	public boolean transfer(Account from, Account to, int amount)
	{
		bankLock.lock();
		if(from.getBalance() >= amount)
		{
			from.setBalance(-amount);
			to.setBalance(+amount);
			didSucceed = true;
		}
		 System.out.println("after transfer: "
				 + from.getDetails() + " "+ to.getDetails() + " The total amount is: " +
				 this.getTotalBalance());
		bankLock.unlock();
		return didSucceed;
	}
	
	public int getTotalBalance()
	{
		bankLock.lock();
		int total = 0;
	
		for(Account a : accounts)
		{
			total += a.getBalance();
		}
		bankLock.unlock();
		return total;
	}
}

