package com.s8.stack.arch.silicon.async;

/**
 * <h1>MthP1Task</h1>
 * <p>Non-blocking task with almost deterministic run time</p> 
 * @author pierreconvert
 *
 */
public interface AsyncTask {
	

	
	/**
	 * 
	 * @return
	 */
	public String describe();
	
	
	/**
	 * 
	 * @return
	 */
	public MthProfile profile();
	
	
	/**
	 * no recursive call allowed
	 */
	public void run();
	
}
