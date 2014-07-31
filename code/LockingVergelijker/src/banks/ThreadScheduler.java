package banks;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import model.Account;
import model.Transaction;
import setup.RandomFiller;

public class ThreadScheduler {
        
	// Connection
	private java.sql.Connection conn;
	private ArrayList<Transaction> transactions;
	private ArrayList<Account> accounts;
	private int numberOfBanks;
	private int numberOfTransactionsPerBank;
	private boolean printDebug;
	
	// args[0] = #banks
	// args[1] = #transactions per bank
	// args[2] = true V false: run randomfiller.
	// args[3] = true V false: output debug info
	public static void main (String args[]) throws Exception {
		if(Boolean.valueOf(args[2]))
			new RandomFiller(Boolean.valueOf(args[3]));
    	new ThreadScheduler(Integer.parseInt(args[0]),Integer.parseInt(args[1]),Boolean.valueOf(args[3]));
    }
	
	public ThreadScheduler(int numberOfBanks, int numberOfTransactionsPerBank, boolean printDebug) throws Exception {
		this.numberOfBanks = numberOfBanks;
		this.numberOfTransactionsPerBank = numberOfTransactionsPerBank;
		this.printDebug = printDebug;
		
		// setup connection
		if(printDebug)
			System.out.println("Setup connection");
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
		// run with optimistic concurrency control
		distributeAndRunTransactions("optimistic");
		// run with pessimistic concurrency control
		distributeAndRunTransactions("pessimistic");
		
		try {
			conn.close();
		} catch(Exception ie) {
			System.err.println("SQLException: " + ie.getMessage());
		}
		
		
	}
	
	private void setupConnection() throws Exception {
        Properties props;
        java.sql.Driver d = (java.sql.Driver)Class.forName("solid.jdbc.SolidDriver").newInstance();
        String sCon = "jdbc:solid://localhost:2315/dba/dba";
        props = new Properties();
        props.put("StatementCache","32");
        conn = java.sql.DriverManager.getConnection(sCon, props);
	}

	private void setPessimistic() throws Exception {
        String queryString1 = "alter table account set pessimistic";
		String queryString2 = "alter table holder set pessimistic";
		
		try{
			Statement stmt1 = conn.createStatement();
			stmt1.executeUpdate(queryString1);
			stmt1.close();
			Statement stmt2 = conn.createStatement();
			stmt2.executeUpdate(queryString2);
			stmt2.close();
		} catch(SQLException e){
			System.err.println("ERROR IN STATEMENT(pes): "+e.getMessage());
			e.printStackTrace();
		}

	}
	
	private void setOptimistic() throws Exception {
        String queryString1 = "alter table account set optimistic";
		String queryString2 = "alter table holder set optimistic";
		
		try{
			Statement stmt1 = conn.createStatement();
			stmt1.executeUpdate(queryString1);
			stmt1.close();
			Statement stmt2 = conn.createStatement();
			stmt2.executeUpdate(queryString2);
			stmt2.close();
		} catch(SQLException e){
			System.err.println("ERROR IN STATEMENT(opt): "+e.getMessage());
			e.printStackTrace();
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
	
	private void distributeAndRunTransactions(String cc) throws Exception {
		if(cc.equals("optimistic"))
			setOptimistic();
		else if(cc.equals("pessimistic"))
			setPessimistic();
		ExecutorService service = Executors.newCachedThreadPool();
		ArrayList<Bank> banks = new ArrayList<Bank>();
		ArrayList<Future<Long[]>> futures = new ArrayList<Future<Long[]>>();
		
		try {
			for(int i = 0; i < numberOfBanks; i++) {
				ArrayList<Transaction> bankList = new ArrayList<Transaction>();
				for(int j = i*numberOfTransactionsPerBank; j < (i+1)*numberOfTransactionsPerBank; j++)
					bankList.add(transactions.get(j));
				banks.add(new Bank(bankList,i,printDebug));
			}	
		
			if(printDebug)
				System.out.println("Now going to run the banks");
			for(int i = 0; i < banks.size(); i++){
				Future<Long[]> task = service.submit(banks.get(i));
				futures.add(task);
			}
			for(Future<Long[]> task:futures){
				Long[] measurement = task.get();
				System.out.println("" + numberOfBanks +","+ numberOfTransactionsPerBank +","+ cc +","+ measurement[0] +","+ measurement[1]);
			}
		} catch(Exception ie){System.err.println("Exception: " + ie.getMessage());}
	}
}