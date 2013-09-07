package model;

public class Transaction {
	private int accountNr1;
	private int accountNr2;
	private Long transactionBalance; 
	
	public Transaction(int accountNr1, int accountNr2, Long transactionBalance){
		this.setAccountNr1(accountNr1);
		this.setAccountNr2(accountNr2);
		this.setTransactionBalance(transactionBalance);
	}

	public int getAccountNr1() {
		return accountNr1;
	}

	public void setAccountNr1(int accountNr1) {
		this.accountNr1 = accountNr1;
	}

	public int getAccountNr2() {
		return accountNr2;
	}

	public void setAccountNr2(int accountNr2) {
		this.accountNr2 = accountNr2;
	}

	public Long getTransactionBalance() {
		return transactionBalance;
	}

	public void setTransactionBalance(Long transactionBalance) {
		this.transactionBalance = transactionBalance;
	}
	
	public String getQuery1() {
		return "UPDATE Account SET Balance = (SELECT Balance FROM Account WHERE AccountPk = "+getAccountNr1()+")-"+getTransactionBalance()+" WHERE AccountPk = "+getAccountNr1();
	}
	
	public String getQuery2() {
		return "UPDATE Account SET Balance = (SELECT Balance FROM Account WHERE AccountPk = "+getAccountNr2()+")+"+getTransactionBalance()+" WHERE AccountPk = "+getAccountNr2();
	}
	
	public String toString() {
		return "from:"+accountNr1+",to:"+accountNr2+",diff:"+transactionBalance;
	}
}