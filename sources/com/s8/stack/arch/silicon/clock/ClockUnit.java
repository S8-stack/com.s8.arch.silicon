package com.s8.stack.arch.silicon.clock;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import com.s8.stack.arch.silicon.SiliconEngine;

/**
 * 
 * @author pierreconvert
 *
 */
public class ClockUnit {

	private final SiliconEngine engine;
	
	private final AtomicBoolean isRunning;
	
	private final ConcurrentLinkedQueue<ClockTask> incoming;
	
	private final long tick;
	
	public ClockUnit(SiliconEngine engine, long tick) {
		this.engine = engine;
		isRunning = new AtomicBoolean();
		incoming = new ConcurrentLinkedQueue<>();
		this.tick = tick;
	}
	
	
	/**
	 * 
	 * @param task
	 */
	public void pushTask(ClockTask task) {
		incoming.add(task);
	}
	
	
	public void start() {
		isRunning.set(true);
		new Thread(new Runnable() {
			
			@Override
			public void run() {

				int position = 0, rPosition, capacity = 4;
				long t = 0;
				

				ClockTask task;
				ClockTask[] tasks = new ClockTask[capacity], rollover;
				
				while(isRunning.get()) {
					
					
					/** adding incoming tasks */
					while((task = incoming.poll()) != null) {
						
						// extend if necessary
						if(position == capacity) {
							ClockTask[] extended = new ClockTask[2*capacity];
							for(int i=0; i<capacity; i++) { extended[i] = tasks[i]; }
							tasks = extended;
							capacity *= 2;
						}
						
						// add task
						tasks[position] = task;
					}
					
					
					rPosition = 0;
					rollover = new ClockTask[capacity];
					for(int i=0; i<position; i++) {
						if((task = tasks[i]) != null) {
							boolean isContinued = task.trigger(t, engine);
							if(isContinued) {
								rollover[rPosition++] = task;
							}
						}
					}
					
					// rolling-over
					tasks = rollover;
					position = rPosition;
					
					
					// sleep
					try {
						Thread.sleep(tick);
					}
					catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

}
