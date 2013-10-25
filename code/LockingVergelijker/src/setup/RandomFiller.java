package setup;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;

import model.Account;
import model.Holder;

public class RandomFiller {
	// PARAMETER SECTION
	public static final int BASIC_BALANCE			 = 100000;
	private static final int NUM_HOLDERS 		  	 = 10000;
	private static final int NUM_ACCOUNTS 		  	 = 13000;
	private static final double BALANCE_PARETO_ALPHA = 2.0;
	
	// DATA FOR DATABASE
	private static Holder[] holders = new Holder[NUM_HOLDERS];
	private static Account[] accounts = new Account[NUM_ACCOUNTS];
	
	public static void main (String args[]) throws Exception {
		new RandomFiller();
    }
	
	public RandomFiller() throws Exception {
		System.out.println("Generating random data");
		// Create data
		generateRandom();
		
		System.out.println("Setting up database connection");
		// Set up DB connection
        Properties props;
        java.sql.Connection conn;
        java.sql.Driver d = (java.sql.Driver)Class.forName("solid.jdbc.SolidDriver").newInstance();
        String sCon = "jdbc:solid://localhost:1964/dba/dba";
        props = new Properties();
        props.put("StatementCache","32");
        conn = java.sql.DriverManager.getConnection(sCon, props);
        // Execute all in one transaction
        conn.setAutoCommit(false);
        
        // Recreate schema
        SchemaCreator.createSchema();
        
        // Insert Holders & Accounts into DB
        System.out.println("Busy inserting Holders");
        insertHolders(conn);
        conn.commit();
        System.out.println("Busy inserting Accounts");
        insertAccounts(conn);
        conn.commit();
        System.out.println("Setting tables to optimistic");
        setOptimistic(conn);
        conn.commit();
        
        // Close connection
        conn.close();
        
        System.out.println("Finished");	
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
			int holderId = (int)(Math.random()*(NUM_HOLDERS-1));
			accounts[i] = new Account(i, balance[i], holderId);
		}
	}
	
	private static void insertHolders(Connection conn){
		// Build query String
		String queryString = Holder.getInsertStmtPrefix();
		queryString += holders[0].getInsertStmtInner();
		for(int i=1;i<NUM_HOLDERS;i++){
			queryString += ", " + holders[i].getInsertStmtInner();
		}
		
		// Execute query
		try{
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(queryString);
			stmt.close();
		} catch(SQLException e){
			System.err.println("ERROR IN STATEMENT: "+e.getMessage());
			System.err.println("query: "+ queryString);
		}
	}
	
	private static void setOptimistic(Connection conn){
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
			System.err.println("ERROR IN STATEMENT: "+e.getMessage());
		}
	}
	
	private static void insertAccounts(Connection conn){
		// Build query String
		String queryString = Account.getInsertStmtPrefix();
		queryString += accounts[0].getInsertStmtInner();
		for(int i=1;i<NUM_ACCOUNTS;i++){
			queryString += ", " + accounts[i].getInsertStmtInner();
		}
		
		// Execute query
		try{
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(queryString);
			stmt.close();
		} catch(SQLException e){
			System.err.println("ERROR IN STATEMENT: "+e.getMessage());
		}
	}
}
