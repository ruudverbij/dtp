package model;

public class Account {
	private int accountPk;
	private double balance;
	private int holderFk;
	
	public Account(int accountPk, double balance, int holderFk){
		this.setAccountPk(accountPk);
		this.setBalance(balance);
		this.setHolderFk(holderFk);
	}

	public int getAccountPk() {
		return accountPk;
	}

	public void setAccountPk(int accountPk) {
		this.accountPk = accountPk;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public int getHolderFk() {
		return holderFk;
	}

	public void setHolderFk(int holderFk) {
		this.holderFk = holderFk;
	}

}
