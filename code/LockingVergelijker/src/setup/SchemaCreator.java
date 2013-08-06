package setup;

import java.sql.SQLException;
import java.util.Properties;

public class SchemaCreator {
    public static void main (String args[]) throws Exception
    {
        Properties props;
        java.sql.Connection conn;
        java.sql.Statement stmt;
            
        // this is the recommended way for registering Drivers
        java.sql.Driver d = (java.sql.Driver)Class.forName("solid.jdbc.SolidDriver").newInstance();
        String sCon = "jdbc:solid://localhost:1964/dba/dba";

        props = new Properties();
        props.put("StatementCache","32");

        // next, the connection is attempted
        System.out.println("Attempting to connect :" + sCon);
        conn = java.sql.DriverManager.getConnection(sCon, props);

        System.out.println("SolidDriver succesfully connected.");
        
        String dQuery1 = "DROP TABLE HOLDER ";
        String dQuery2 = "DROP TABLE ACCOUNT ";
        
        String cQuery1 = "CREATE TABLE Holder ("
			    		+ "HolderPk int,"
			    		+ " FirstName varchar(60),"
			    		+ " LastName varchar(60),"
			    		+ " PRIMARY KEY (HolderPk)"
			    		+ ") ";
        
        String cQuery2 = "CREATE TABLE Account ("
        				+ " AccountPk int,"
        				+ " Balance int,"
        				+ " HolderFk int,"
        				+ " PRIMARY KEY (AccountPk),"
        				+ " FOREIGN KEY (HolderFK) REFERENCES Holder(HolderPk)"
        				+ ")";

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
        // and we close
        stmt.close();
        conn.close();        
    }

}
