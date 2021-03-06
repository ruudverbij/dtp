package banks;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.Callable;

import model.Transaction;


public class Bank implements Callable<Long[]> {
        
	// Connection
	private java.sql.Connection conn;
	private ArrayList<Transaction> transactions;
	private ArrayList<Transaction> revokedList;
    private Statement stmt;
	private int numberOfExceptions;
	private int bankNumber;
	private boolean printDebug = false;
	
	public Bank(ArrayList<Transaction> transactions, int bankNumber, boolean printDebug) throws Exception {
		// setup connection
        Properties props;
        java.sql.Driver d = (java.sql.Driver)Class.forName("solid.jdbc.SolidDriver").newInstance();
        String sCon = "jdbc:solid://localhost:2315/dba/dba";
        props = new Properties();
        props.put("StatementCache","32");
        conn = java.sql.DriverManager.getConnection(sCon, props);
		// set autocommit to false, hereby creating procedures
        conn.setAutoCommit(false);	

		// set the bank-specific items
		this.printDebug = printDebug;
		this.transactions = transactions;
		this.bankNumber = bankNumber;
		numberOfExceptions = 0;
	}
	
	private void runTransactions(ArrayList<Transaction> transactions){
		// reset revokedList
		revokedList = new ArrayList<Transaction>();
		boolean revoked = false;
		
		// run all transactions
		for(int i = 0; i < transactions.size(); i++) {
			String sQuery1 = transactions.get(i).getQuery1();
			String sQuery2 = transactions.get(i).getQuery2();
		
			try {
				stmt = conn.createStatement();
				stmt.executeUpdate(sQuery1);
				stmt.close();
			} catch(SQLException ex) {
				// System.err.println("SQLException 3:"+ex.getMessage());
				// would output:
				// SQLException 3:[Solid JDBC 6.5.0.0 Build 0010] SOLID Database Error 10006: Concurrency conflict, two transactions updated or deleted the same row
				if(ex.getMessage().equals("[Solid JDBC 6.5.0.0 Build 0010] SOLID Database Error 10006: Concurrency conflict, two transactions updated or deleted the same row"))
					numberOfExceptions++;
				else if(ex.getMessage().equals("[Solid JDBC 6.5.0.0 Build 0010] SOLID Database Error 10031: Transaction detected a deadlock, transaction is rolled back"))
					numberOfExceptions++;
				else
					System.out.println(ex.getMessage());
				// add transaction to revokedList
				revokedList.add(transactions.get(i));
				revoked = true;
			}	
			
			if(!revoked) {
				try {
					stmt = conn.createStatement();
					stmt.executeUpdate(sQuery2);
					stmt.close();
				} catch(SQLException ex) {
					if(ex.getMessage().equals("[Solid JDBC 6.5.0.0 Build 0010] SOLID Database Error 10006: Concurrency conflict, two transactions updated or deleted the same row"))
						numberOfExceptions++;
					else if(ex.getMessage().equals("[Solid JDBC 6.5.0.0 Build 0010] SOLID Database Error 10031: Transaction detected a deadlock, transaction is rolled back"))
						numberOfExceptions++;
					else
						System.out.println(ex.getMessage());
					// add transaction to revokedList
					revokedList.add(transactions.get(i));
					revoked = true;
				}	
			}
            if(!revoked){
                try{
                    conn.commit();
                }catch (Exception e){
                    System.err.println("DEZE EXCEPTIE ZOU NOOIT MOETEN GEBEUREN: "+e.getMessage());
                }
            }
		}
	}
	
	public void loopForRevoked(ArrayList<Transaction> transactions) {
		// run all transactions (and rerun revoked transactions)
		runTransactions(transactions);
	}
	
	@Override
	public Long[] call() throws Exception {
		if(printDebug)
			System.out.println("Bank "+bankNumber+" started with "+transactions.size()+" transactions");
		// start time measurement
		long startTime = System.nanoTime();
		
		loopForRevoked(this.transactions);
		while(revokedList.size() > 0) {
			loopForRevoked(revokedList);
		}
		
        // Close connection
		try {
			conn.close();
		} catch(Exception ie){System.err.println("SQLException: " + ie.getMessage());}
        
		// close time measurement
		long timeElapsed = System.nanoTime() - startTime;
		
		// print results
		if(printDebug)
			System.out.println("Bank "+bankNumber+" finished with "+numberOfExceptions+" revokes in "+timeElapsed+" nanoseconds.");
		
		Long data[] = new Long[] {(long) numberOfExceptions, timeElapsed};
		return data;
	}
}