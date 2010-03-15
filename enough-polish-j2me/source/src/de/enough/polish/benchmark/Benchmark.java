package de.enough.polish.benchmark;

import de.enough.polish.util.HashMap;
import de.enough.polish.util.Iterator;

/**
 * A benchmark class to time processes in an application.
 *
 * @author Ovidiu Iliescu
 * @author Andre Schmidt
 * 
 */
public class Benchmark {

    /**
     * the benchmarks
     */
    static HashMap benchmarks;
    /**
     * the time the benchmark started
     */
    long time = 0;
    /**
     * the key to identify the benchmark
     */
    String key;
    //---
    // Smart Timer related code follows
    //---
    private static long lastTime = 0;
    private static HashMap totalExecutionTimes;
    private static HashMap tempExecutionTimes;
    private static HashMap timerDescriptions;
    private static boolean outputResults = true;
    private static long checkInterval = 5000;

    static {
        //#mdebug benchmark
        totalExecutionTimes = new HashMap();
        tempExecutionTimes = new HashMap();
        timerDescriptions = new HashMap();
        benchmarks = new HashMap();
        //#enddebug
    }

    /**
     * Starts a benchmark with the given key
     *
     * @param key
     *            the key
     */
    public static void start(String key) {
        //#mdebug benchmark
        Benchmark benchmark = (Benchmark) benchmarks.get(key);

        if (benchmark == null) {
            benchmark = new Benchmark();
        }

        benchmark.setTime(System.currentTimeMillis());

        benchmarks.put(key, benchmark);

        System.out.println(key + " : started");
        //#enddebug
    }

    public static void stop(String key, String name) {
        //#mdebug benchmark
        Benchmark benchmark = (Benchmark) benchmarks.get(key);

        if (benchmark == null) {
            return;
        }

        long time = System.currentTimeMillis() - benchmark.getTime();
        String formattedTime = getFormattedTime(time);

        System.out.println(key + " : " + name + ": " + formattedTime + " seconds");
        //#enddebug
    }

    /**
     * Returns the start time
     *
     * @return the start time
     */
    long getTime() {
        return this.time;
    }

    /**
     * Sets the start time
     *
     * @param time
     *            the start time
     */
    void setTime(long time) {
        this.time = time;
    }

    /**
     * Returns a formatted time string in seconds
     *
     * @param time
     *            the time in milliseconds
     * @return the formatted time string
     */
    final static String getFormattedTime(long time) {
        long seconds = time / 1000;

        long milliseconds = time % 1000;
        String millisecondsStr = "" + milliseconds;
        while (millisecondsStr.length() != 3) {
            millisecondsStr = "0" + millisecondsStr;
        }

        return seconds + "," + millisecondsStr;
    }

    /**
     * Reset all smart timers
     */
    private static void resetSmartTimers() {
        //#mdebug benchmark

        Iterator temp = totalExecutionTimes.keysIterator();
        Object key;
        while (temp.hasNext()) {
            key = temp.next();
            totalExecutionTimes.put(key, new Long(0));
            tempExecutionTimes.put(key, new Long(0));
        }
        lastTime = System.currentTimeMillis();

        //#enddebug
    }

    /**
     * Output smart timer information to console
     */
    private static void outputSmartTimers() {
        //#mdebug benchmark

        Iterator temp = totalExecutionTimes.keysIterator();
        Object key;
        String tempStr;
        while (temp.hasNext()) {
            key = temp.next();
            if (((Long) totalExecutionTimes.get(key)).longValue() > 0) {
                if (timerDescriptions.get(key) == null) {
                    tempStr = "\"" + key.toString() + "\"";
                } else {
                    tempStr = "\"" + timerDescriptions.get(key) + "\"";
                }

                if (outputResults) {
                    System.out.println("Timer " + tempStr + " has value " + ((Long) totalExecutionTimes.get(key)).toString());
                }
            }
        }

        //#enddebug
    }

    /**
     * Set the description text of a certain smart timer
     * @param timerName
     * @param description
     */
    public static void setSmartTimerDescription(Object timerName, String description) {
        //#mdebug benchmark

        timerDescriptions.put(timerName, description);

        //#enddebug
    }

    /**
     * Start the clock on a certain smart timer
     * @param name
     */
    public static void startSmartTimer(Object name) {
        //#mdebug benchmark

        tempExecutionTimes.put(name, new Long(System.currentTimeMillis()));

        //#enddebug
    }

    /**
     * Increment a certain timer by 1. Useful for measuring how many times a certain piece of code was executed.
     * @param name
     */
    public static void incrementSmartTimer(Object name) {
        //#mdebug benchmark

        Long val = (Long) totalExecutionTimes.get(name);
        if (val == null) {
            totalExecutionTimes.put(name, new Long(0));
        } else {
            totalExecutionTimes.put(name, new Long(val.longValue() + 1));
        }

        //#enddebug
    }

    /**
     * Pause the clock on a certain smart timer
     * @param name
     */
    public static void pauseSmartTimer(Object name) {
        //#mdebug benchmark

        // If timer is paused without being started first, we must ignore the rest of the method
        Long val = (Long) tempExecutionTimes.get(name);
        if (val == null) {
            return;
        }
        if (val.longValue() == 0) {
            return;
        }

        Long soFar = ((Long) totalExecutionTimes.get(name));
        long timeSpentSoFar = 0;
        if (soFar != null) {
            timeSpentSoFar = soFar.longValue();
        }
        totalExecutionTimes.put(name, new Long(timeSpentSoFar + System.currentTimeMillis() - val.longValue()));
        tempExecutionTimes.put(name, new Long(0));

        //#enddebug
    }

    /**
     * Check if enough time has elapsed since this method was last called. If enough time has passed, output the smart timer values.
     */
    public static void check() {
        //#mdebug benchmark

        check(checkInterval);

        //#enddebug
    }

    /*
     * Sets the default "time elapsed interval" for the check() method.
     */
    public static void setSmartTimerCheckInterval(long value) {
        //#mdebug benchmark

        checkInterval = value;

        //#enddebug
    }

    /**
     * Check if enough time has elapsed since this method was last called. If enough time has passed, output the smart timer values.
     * @param desiredElapsedTime
     */
    public static void check(long desiredElapsedTime) {
        //#mdebug benchmark

        long currentTime = System.currentTimeMillis();
        if (currentTime - desiredElapsedTime > lastTime) {
            if (outputResults) {
                System.out.println("---");
                System.out.println("> TIME ELAPSED : " + (currentTime - lastTime));
            }
            outputSmartTimers();
            resetSmartTimers();
        }

        //#enddebug
    }

    /*
     * Prevents smart timers from outputing their values
     */
    public static void haltOutput() {
        //#mdebug benchmark

        outputResults = false;

        //#enddebug
    }

    /*
     * Resumes output for smart timers
     */
    public static void resumeOutput() {
        //#mdebug benchmark

        outputResults = true;

        //#enddebug
    }
}
