package com.o2.techm.netcool.eai.manonsite.mrs;

public class Item {
	

	 
    protected String key;

    protected String value;
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	@Override
	public String toString() {
		return "Item [key=" + key + ", value=" + value + "]";
	}
    
    

}
