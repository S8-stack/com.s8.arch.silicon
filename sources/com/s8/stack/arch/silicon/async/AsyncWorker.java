package com.s8.stack.arch.silicon.async;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


/**
 * 
 * @author pierreconvert
 *
 */
public class AsyncWorker {

	public final static int DEFAULT_CAPACITY = 1048576;
	
	
	private final static AsyncTask ZERO = new AsyncTask() {
		
		@Override
		public void run() {
		}
		
		@Override
		public MthProfile profile() {
			return null; // is not read
		}

		@Override
		public String describe() {
			return "ZERO";
		}
	};

	
	
	private final int slot;
	

	private final BlockingQueue<AsyncTask> tasks;

	
	/**
	 * Note that all primitive vars (less than 32 bits) are atomically set.
	 * volatile keyword ensure consistency among threads.
	 */
	private volatile boolean isRunning;

	public AsyncWorker(int slot) {
		super();
		this.slot = slot;
		this.tasks = new ArrayBlockingQueue<>(DEFAULT_CAPACITY);
	}
	
	public AsyncWorker(int slot, int capacity) {
		super();
		this.slot = slot;
		this.tasks = new ArrayBlockingQueue<>(capacity);
	}
	
	
	public int getSlot() {
		return slot;
	}



	public void start() {
		
		// start processing unit flag
		isRunning = true;
		
		// create thread
		Thread thread = new Thread(new Kernel());	
		
		// ignition
		thread.start();
	}



	private class Kernel implements Runnable {

		public void run() {

			AsyncTask task;
			
			while(isRunning) {

				try {
					
					// take next task...
					task = tasks.take();

					// ... and run it
					task.run();
				} 
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			
			/*
			 * Allows graceful exit
			 */
			// pull as much as possible...
			while((task = tasks.poll()) != null) {
				
				// ... and run it
				task.run();
			}
		}
	}

	/**
	 * 
	 */
	public void stop() {
		tasks.add(ZERO);
		isRunning = false;
	}

	
	/**
	 * 
	 * @param runnable
	 */
	public boolean pushTask(AsyncTask runnable) {
		
		/**
		 * When using queues that may impose insertion restrictions (for example
		 * capacity bounds), method offer is generally preferable to method
		 * Collection.add(E), which can fail to insert an element only by throwing an
		 * exception.
		 * 
		 */
		return tasks.offer(runnable);
	}
}
