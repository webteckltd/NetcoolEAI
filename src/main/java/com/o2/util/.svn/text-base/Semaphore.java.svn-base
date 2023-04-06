/*
 * Created on 2004ª~12ñ‹10ñ‰
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.o2.util;

/**
 * @author Raymond Leung
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Semaphore {
    private int counter;

    public Semaphore() {
        this(0);
    }

    public Semaphore(int i) {
        if (i < 0) throw new IllegalArgumentException(i + " < 0");
        counter = i;
    }

    /**
     * Increments internal counter, possibly awakening a thread
     * wait()ing in acquire().
     */
    public synchronized void release() {
		if (counter == 0) {
			this.notify();
		}
		counter++;

	}

    /**
	 * Decrements internal counter, blocking if the counter is already zero.
	 * 
	 * @exception InterruptedException
	 *                passed from this.wait().
	 */
    public synchronized void acquire() throws InterruptedException {
        while (counter == 0) {
            this.wait();
        }
        counter--;
        
    }
    
}


