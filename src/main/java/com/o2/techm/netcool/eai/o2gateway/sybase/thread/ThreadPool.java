/*
 * Created on Dec 14, 2004
 */
package com.o2.techm.netcool.eai.o2gateway.sybase.thread;

import java.util.ArrayList;

/**
 * @author rleung
 */
public class ThreadPool extends ThreadGroup{
	protected ArrayList threads = new ArrayList();
	public ThreadPool(String name){
		super(name);
	}
	public void addThread(Thread thread){
		threads.add(thread);
	}
	public void start() {
		for (int i = 0; i < threads.size(); i++){
			((Thread)threads.get(i)).start();
		}
		
	}
	public void terminateAll() 
	{
		for (int i = 0; i < threads.size(); i++){
		    Thread thread = (Thread) threads.get(i); 
			if (thread instanceof Terminatable) {
				((Terminatable)thread).terminate();
				thread.interrupt(); 
			}			
		}
	}
	public StringBuffer monitorAll() 
	{
		StringBuffer str=new StringBuffer(". " + threads.size() + " IDUC Thread(s) created and running. ");
		int j=0;
		for (int i = 0; i < threads.size(); i++){
		    Thread thread = (Thread) threads.get(i); 
		     if(thread != null && !thread.isAlive())
		     {
        		str.append(" : ").append(thread.getName()).append(" is dead. ");
        		j++;
		     }		     	     
		}
		if(j > 0)
	     	str.append(j).append(" thread(s) are dead");
		else
			str.append(" All IDUC threads are alive");
		return str;
	}
}
