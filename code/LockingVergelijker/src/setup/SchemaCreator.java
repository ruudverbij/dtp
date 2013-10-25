package setup;

import java.sql.SQLException;
import java.util.Properties;

public class SchemaCreator {
    public static void main (String args[]) throws Exception
    {
    	createSchema(Boolean.valueOf(args[0]));
    }
    
    protected static void createSchema(boolean printDebug) throws Exception{
        Properties props;
        java.sql.Connection conn;
        java.sql.Statement stmt;
            
        // Register driver
        java.sql.Driver d = (java.sql.Driver)Class.forName("solid.jdbc.SolidDriver").newInstance();
        String sCon = "jdbc:solid://localhost:1964/dba/dba";

        props = new Properties();
        props.put("StatementCache","32");

        // Set up connection
        if(printDebug)
			System.out.println("Attempting to connect :" + sCon);
        conn = java.sql.DriverManager.getConnection(sCon, props);
        conn.setAutoCommit(true);
        
        if(printDebug)
			System.out.println("SolidDriver succesfully connected.");
        
        String dQuery1 = "DROP TABLE ACCOUNT ";
        String dQuery2 = "DROP TABLE HOLDER ";
        
        String cQuery1 = "CREATE TABLE Holder ("
			    		+ " HolderPk int,"
			    		+ " FirstName varchar(60),"
			    		+ " LastName varchar(60),"
			    		+ " PRIMARY KEY (HolderPk)"
			    		+ ") STORE disk";
        
        String cQuery2 = "CREATE TABLE Account ("
        				+ " AccountPk int,"
        				+ " Balance bigint,"
        				+ " HolderFk int,"
        				+ " PRIMARY KEY (AccountPk)"
        				+ ") STORE disk";

        stmt= conn.createStatement();
        
        try{
        	stmt.executeUpdate(dQuery1);
        	stmt.executeUpdate(dQuery2);
        }catch(SQLException e){
        	System.err.println(e.getMessage());
        }
        try{
        	stmt.executeUpdate(cQuery1);
        	stmt.executeUpdate(cQuery2);
        }catch(SQLException e){
        	System.err.println(e.getMessage());
        }
                
        // Close
        stmt.close();
        conn.close();        

    }

}
