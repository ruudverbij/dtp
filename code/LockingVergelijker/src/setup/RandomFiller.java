package setup;

import java.math.BigDecimal;

public class RandomFiller {
	// PARAMETER SECTION
	private static final int BASIC_BALANCE			 = 100000;
	private static final int NUM_HOLDERS 		  	 = 10000;
	private static final int NUM_ACCOUNTS 		  	 = 13000;
	private static final double ACCOUNT_PARETO_ALPHA = 3.0;
	
	public static void main (String args[]) throws Exception
    {
		// GENERATE PARETO-DISTRIBUTED BALANCES
		BigDecimal[] balance = new BigDecimal[NUM_ACCOUNTS];
		for(int i=0;i<NUM_ACCOUNTS;i++){
			balance[i] = new BigDecimal(String.valueOf(StdRandom.pareto(ACCOUNT_PARETO_ALPHA)*BASIC_BALANCE)).setScale(2, BigDecimal.ROUND_HALF_UP) ;
			System.out.println(balance[i]);
		}
		
		// GENERATE HOLDERS
		String[] firstName  = new String[NUM_HOLDERS];
		String[] lastName	= new String[NUM_HOLDERS];
				
    }
}
