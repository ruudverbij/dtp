package banks;

import setup.RandomFiller;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;
import java.util.ArrayList;

import model.Account;
import model.Holder;
import model.Transaction;

public class ThreadScheduler {
        
	// Connection
	private java.sql.Connection conn;
	private ArrayList<Transaction> transactions;
	private ArrayList<Account> accounts;
	private int numberOfBanks;
	private int numberOfTransactionsPerBank;
	private boolean printDebug;
	private boolean optimistic;
	
	// args[0] = #banks
	// args[1] = #transactions per bank
	// args[2] = true V false: run randomfiller.
	// args[3] = true V false: output debug info
	// args[4] = true V false: optimistic?
	public static void main (String args[]) throws Exception {
		if(Boolean.valueOf(args[2]))
			new RandomFiller(Boolean.valueOf(args[3]));
    	new ThreadScheduler(Integer.parseInt(args[0]),Integer.parseInt(args[1]),Boolean.valueOf(args[3]),Boolean.valueOf(args[4]));
    }
	
	public ThreadScheduler(int numberOfBanks, int numberOfTransactionsPerBank, boolean printDebug, boolean optimistic) throws Exception {
		this.numberOfBanks = numberOfBanks;
		this.numberOfTransactionsPerBank = numberOfTransactionsPerBank;
		this.printDebug = printDebug;
		this.optimistic = optimistic;
		
		// first, setup connection
		if(printDebug)
			System.out.println("Setup DB connection");
		setupConnection();
		
		// next, collect all accounts
		if(printDebug)
			System.out.println("Get all accounts");
		getAllAccounts();
		
		// next, generate all transactions
		if(printDebug)
			System.out.println("Generate all transaction");
		generateTransactions();
		
		// finally, distribute all the transactions to the banks
		if(printDebug)
			System.out.println("Distribute transactions");
		distributeTransactions();
	}
	
	private void setupConnection() throws Exception {
		// setup connection
        Properties props;
        java.sql.Driver d = (java.sql.Driver)Class.forName("solid.jdbc.SolidDriver").newInstance();
        String sCon = "jdbc:solid://localhost:2315/dba/dba";
        props = new Properties();
        props.put("StatementCache","32");
        conn = java.sql.DriverManager.getConnection(sCon, props);
		
		String queryString1 = (optimistic) ? "alter table account set optimistic" : "alter table account set pessimistic";
		String queryString2 = (optimistic) ? "alter table holder set optimistic" : "alter table holder set pessimistic";
		
		try{
			Statement stmt1 = conn.createStatement();
			stmt1.executeUpdate(queryString1);
			stmt1.close();
			Statement stmt2 = conn.createStatement();
			stmt2.executeUpdate(queryString2);
			stmt2.close();
		} catch(SQLException e){
			System.err.println("ERROR IN STATEMENT: "+e.getMessage());
		}
		
	}
	
	private void getAllAccounts() throws Exception {
		String sQuery = "SELECT AccountPk, Balance, HolderFk FROM Account";
		
		java.sql.Statement stmt = conn.createStatement();
        java.sql.ResultSet result = stmt.executeQuery(sQuery);
		
		java.sql.ResultSetMetaData meta = result.getMetaData();
        int cols = meta.getColumnCount();
		
		accounts = new ArrayList<Account>();
		
        while(result.next())
        {
			int accountNr = result.getInt(1);
			long balance = result.getLong(2);
			int holderFk = result.getInt(3);
			
			accounts.add(new Account(accountNr, balance, holderFk));
        }
		
		stmt.close();
		try {
			conn.close();
		} catch(Exception ie) {
			System.err.println("SQLException: " + ie.getMessage());
		}
	}
	
	private void generateTransactions(){
		int totalNumberOfTransactions = numberOfBanks * numberOfTransactionsPerBank;
		int numberOfAccounts = accounts.size();
		
		transactions = new ArrayList<Transaction>();
		
		for(int i = 0; i < totalNumberOfTransactions; i++) {
			transactions.add(
				new Transaction(
					(int)Math.round(Math.random()*numberOfAccounts-1),
					(int)Math.round(Math.random()*numberOfAccounts-1),
					Math.round(Math.random()*setup.RandomFiller.BASIC_BALANCE)));
		}
	}
	
	private void distributeTransactions() {
	
		ArrayList<Bank> banks = new ArrayList<Bank>();
		try {
			for(int i = 0; i < numberOfBanks; i++) {
				ArrayList<Transaction> bankList = new ArrayList<Transaction>();
				for(int j = i*numberOfTransactionsPerBank; j < (i+1)*numberOfTransactionsPerBank; j++)
					bankList.add(transactions.get(j));
				banks.add(new Bank(bankList,i,printDebug));
			}	
		
			if(printDebug)
				System.out.println("Now going to run the banks");
			for(int i = 0; i < banks.size(); i++)
				banks.get(i).start();			
		} catch(Exception ie){System.err.println("Exception: " + ie.getMessage());}
	}
}