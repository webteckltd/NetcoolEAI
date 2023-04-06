package com.o2.techm.netcool.eai.manonsite.mrs;


import java.util.ArrayList;
import java.util.List;




public class RequestContent {
	 protected List<Item> item;

	public List<Item> getItem() {
		return item;
	}

	public void setItem(List<Item> item) {
		this.item = item;
	}

	@Override
	public String toString() {
		return "RequestContent [item=" + item + "]";
	}
	
	public synchronized void  addItem(Item item1){
		if (null !=this.item){
			this.item.add(item1);
		}else{
			this.item  =  new ArrayList<Item>();
			this.item.add(item1);
		}
		
	}
	
	 
	 
}