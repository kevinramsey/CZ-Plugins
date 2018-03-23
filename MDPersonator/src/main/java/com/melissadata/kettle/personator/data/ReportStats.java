package com.melissadata.kettle.personator.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Tracks report statistics
 */
public class ReportStats {
	
	private class Stat {
		String key;
		int count;

		Stat(String key, int count) {
			this.key = key;
			this.count = count;
		}
		
		@Override
		public String toString() {
			return key + "=" + count;
		}
	}

	private Map<String, Stat> stats = new HashMap<String, Stat>();

	/**
	 * Creates a report statistic tracker for given keys
	 * 
	 * @param keys
	 * @return
	 */
	public static ReportStats create(String[] keys) {
		ReportStats reportStats = new ReportStats();
		for (int i = 0; i < keys.length; i++)
			reportStats.set(keys[i], 0);
		return reportStats;
	}

	/**
	 * Increment a single tracked statistic
	 * 
	 * @param key
	 */
	public synchronized void inc(String key) {
		Stat stat = stats.get(key);
		if (stat != null)
			stat.count++;
		else
			stat = new Stat(key, 1);
		stats.put(key, stat);
	}

	/**
	 * Add all the stats from one tracker to this tracker
	 * 
	 * @param other
	 */
	public synchronized void putAll(ReportStats other) {
		stats.putAll(other.stats);
	}

	/**
	 * Sets a specific statistic for a given key
	 * 
	 * @param key
	 * @param count
	 */
	public synchronized void set(String key, int count) {
		stats.put(key, new Stat(key, count));
	}

	/**
	 * Returns a specific statistic
	 * 
	 * @param key
	 * @return
	 */
	public synchronized int get(String key) {
		Stat stat = stats.get(key);
		if (stat != null)
			return stat.count;
		return 0;
	}

	/**
	 * Remove tracking for a specific statistic
	 * 
	 * @param key
	 */
	public synchronized void remove(String key) {
		stats.remove(key);
	}

	/**
	 * See if we are tracking a particular key
	 * 
	 * @param key
	 * @return
	 */
	public synchronized boolean containsKey(Object key) {
		return stats.containsKey(key);
	}

	/**
	 * @return The set of keys managed by this structure
	 */
	public Set<String> getKeys() {
		return stats.keySet();
	}

}
