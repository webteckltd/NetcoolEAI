/*
 * Created on Dec 14, 2004
 *
 */
package com.o2.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author rleung
 *
 */
public class WorkQueue {
	private static Logger log = LoggerFactory.getLogger(WorkQueue.class);
	private Semaphore availableWorkSemaphore; 
	private LinkedList queue;
	
	public WorkQueue (){
		this.queue = new LinkedList();
		this.availableWorkSemaphore = new Semaphore(0);
	}
	public void enqueueWork(Object work) {
		synchronized (queue){
			queue.addLast(work);
			
		}
		availableWorkSemaphore.release();
	}
	
	public void enqueueWorkFirst(Object work) {
		synchronized (queue){
			queue.addFirst(work);
			
		}
		availableWorkSemaphore.release();
	}
	public Object dequeueWork() throws InterruptedException 
	{ 
		boolean acquired = false;
		while (!acquired){
			availableWorkSemaphore.acquire();
			acquired = true;
		}
		synchronized(queue){
			return queue.removeFirst();
		}
		
	}
	
	public int size()
	{
		synchronized(queue){
			return queue.size();
		}
	}
	 
	public int binarySearch(Object key, Comparator c)
	{
		return Collections.binarySearch(queue,key,c);
	}
}
