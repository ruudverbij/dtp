package model;

public class Holder {
	private int holderPk;
	private String firstName;
	private String lastName;
	
	public Holder(int holderPk, String firstName, String lastName){
		this.setHolderPk(holderPk);
		this.setFirstName(firstName);
		this.setLastName(lastName);
	}

	public int getHolderPk() {
		return holderPk;
	}

	public void setHolderPk(int holderPk) {
		this.holderPk = holderPk;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public String getInsertStmt() {
		return "INSERT INTO Holder VALUES ("+holderPk+", '"+firstName+"', '"+lastName+"');";
	}
}
