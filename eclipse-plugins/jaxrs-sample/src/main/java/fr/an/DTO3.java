package fr.an;

public class DTO3 {

	private int field1;
	
	private int field2GetterOnly;
	
	private int field3SetterOnly;
	
	private int field4NotAccessors;

	public int getField1() {
		return field1;
	}

	public void setField1(int field1) {
		this.field1 = field1;
	}

	public int getField2GetterOnly() {
		return field2GetterOnly;
	}

	public void setField3SetterOnly(int field3SetterOnly) {
		this.field3SetterOnly = field3SetterOnly;
	}
	
	
}
