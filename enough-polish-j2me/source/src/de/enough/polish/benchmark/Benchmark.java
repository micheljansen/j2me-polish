package de.enough.polish.benchmark;

import de.enough.polish.util.HashMap;

/**
 * A benchmark class to time processes in an application.
 * 
 * @author Andre Schmidt
 *
 */
public class Benchmark {
	/**
	 * the benchmarks
	 */
	static HashMap benchmarks = new HashMap();
	
	/**
	 * the time the benchmark started
	 */
	long time = 0;

	/**
	 * the key to identify the benchmark
	 */
	String key;
	
	
	/**
	 * Starts a benchmark with the given key
	 * @param key the key
	 */
	public static void start(String key)
	{
		Benchmark benchmark = new Benchmark();
		
		benchmark.setTime(System.currentTimeMillis());
		
		benchmarks.put(key, benchmark);
	
		//#debug benchmark
		 System.out.println(key + " : started");
	}
	
	/**
	 * Finishes a benchmark with the given key
	 * @param key the keys
	 */
	public static void finish(String key)
	{
		Benchmark benchmark = (Benchmark)benchmarks.get(key);
		
		if(benchmark == null)
		{
			return;
		}
		
		long time = System.currentTimeMillis() - benchmark.getTime();
		String formattedTime = getFormattedTime(time);

		//#debug benchmark
		System.out.println(key + " : finished : " + formattedTime + " seconds");
		
		benchmarks.remove(key);
	}
	
	/**
	 * Prints an info
	 * @param info the info
	 */
	public static void info(String info)
	{
		//#debug benchmark
		System.out.println(info);
	}

	/**
	 * Returns the start time 
	 * @return the start time
	 */
	long getTime() {
		return this.time;
	}

	/**
	 * Sets the start time
	 * @param time the start time
	 */
	void setTime(long time) {
		this.time = time;
	}
	
	/**
	 * Returns a formatted time string in seconds 
	 * @param time the time in milliseconds
	 * @return the formatted time string
	 */
	final static String getFormattedTime(long time)
	{
		long seconds = time / 1000;
		
		long milliseconds = time % 1000;
		String millisecondsStr = "" + milliseconds;
		while(millisecondsStr.length() != 3)
		{
			millisecondsStr = "0" + millisecondsStr;
		}
		
		return seconds + "," + millisecondsStr;
	}
}
