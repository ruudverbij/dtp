package setup;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;

import model.Account;
import model.Holder;

public class RandomFiller {
	// PARAMETER SECTION
	private static final int BASIC_BALANCE			 = 100000;
	private static final int NUM_HOLDERS 		  	 = 10000;
	private static final int NUM_ACCOUNTS 		  	 = 13000;
	private static final double BALANCE_PARETO_ALPHA = 3.0;
	private static final double HOLDER_PARETO_ALPHA  = 3.0;
	
	// DATA FOR DATABASE
	private static Holder[] holders = new Holder[NUM_HOLDERS];
	private static Account[] accounts = new Account[NUM_ACCOUNTS];
	
	public static void main (String args[]) throws Exception
    {
		// Create data
		generateRandom();

		// Set up DB connection
        Properties props;
        java.sql.Connection conn;
        java.sql.Statement stmt;  
        java.sql.Driver d = (java.sql.Driver)Class.forName("solid.jdbc.SolidDriver").newInstance();
        String sCon = "jdbc:solid://localhost:1964/dba/dba";
        props = new Properties();
        props.put("StatementCache","32");
        conn = java.sql.DriverManager.getConnection(sCon, props);
        stmt= conn.createStatement();
        
        // insert Holders & Accounts into DB
        insertHolders(stmt);
        insertAccounts(stmt);
        
        // Close statement & connection
        stmt.close();
        conn.close();		
    }
	
	private static void generateRandom(){
		// GENERATE NAMES OF HOLDERS
		String[] firstName  = new String[NUM_HOLDERS];
		String[] lastName	= new String[NUM_HOLDERS];
		for(int i=0;i<NUM_HOLDERS;i++){
			// TODO: Placeholder naam Henk de Boer vervangen
			firstName[i] = "Henk";
			lastName[i] = "de Boer";
		}
		
		// GENERATE HOLDERS
		for(int i=0;i<NUM_HOLDERS;i++){
			holders[i] = new Holder(i, firstName[i], lastName[i]);
		}
		
		// GENERATE PARETO-DISTRIBUTED BALANCES
		Long[] balance = new Long[NUM_ACCOUNTS];
		for(int i=0;i<NUM_ACCOUNTS;i++){
			balance[i] = new Long(Math.round(StdRandom.pareto(BALANCE_PARETO_ALPHA)*BASIC_BALANCE)) ;
			
		}
		Arrays.sort(balance, Collections.reverseOrder());
		
		// GENERATE ACCOUNT INSTANCES
		for(int i=0;i<NUM_ACCOUNTS;i++){
			int holderId = (int) Math.round((StdRandom.pareto(HOLDER_PARETO_ALPHA)*NUM_HOLDERS));
			accounts[i] = new Account(i, balance[i], holderId);
		}
	}
	
	private static void insertHolders(Statement stmt){
		for(int i=0;i<NUM_HOLDERS;i++){
			try{
				stmt.executeQuery(holders[i].getInsertStmt());
			}catch(SQLException e){
				System.err.println("ERROR WHILE INSERTING: "+e.getMessage());
				System.err.println("Cannot insert: "+holders[i].getInsertStmt());
			}
		}
	}
	
	private static void insertAccounts(Statement stmt){
		for(int i=0;i<NUM_ACCOUNTS;i++){
			try{
				stmt.executeQuery(accounts[i].getInsertStmt());
			}catch(SQLException e){
				System.err.println("ERROR WHILE INSERTING: "+e.getMessage());
				System.err.println("Cannot insert: "+holders[i].getInsertStmt());
			}
		}
	}
}
