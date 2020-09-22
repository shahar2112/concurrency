package bank;

public class Account 
{
	private int id;
	private int balance = 0;
	private String name;
	
	public Account(String name, int id)
	{
		this.name = name;
		this.id = id;
	}
	
	public void setBalance(int amount)
	{
		this.balance += amount;
	}
	
	public int getBalance()
	{
		return balance;
	}
	
	public String getDetails()
	{
		return "Name: " + this.name + ", Balance: " + this.balance;
	}
}
