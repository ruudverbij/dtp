package model;

public class Account {
	private int accountPk;
	private Long balance;
	private int holderFk;
	
	public Account(int accountPk, Long balance, int holderFk){
		this.setAccountPk(accountPk);
		this.setBalance(balance);
		this.setHolderFk(holderFk);
	}
	
	public Account(int accountPk, Long balance){
		this.setAccountPk(accountPk);
		this.setBalance(balance);
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

	public void setBalance(Long balance) {
		this.balance = balance;
	}

	public int getHolderFk() {
		return holderFk;
	}

	public void setHolderFk(int holderFk) {
		this.holderFk = holderFk;
	}

	public String getInsertStmt() {
		return "INSERT INTO Account VALUES ("+accountPk+", "+balance+", "+holderFk+");";
	}
}
