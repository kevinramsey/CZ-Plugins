package com.melissadata.kettle.report;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Tracks report statistics
 */
public class ReportStats {
	private class Stat {
		int	count;

		Stat(int count) {
			this.count = count;
		}

		@Override
		public String toString() {
			return Integer.toString(count);
		}
	}

	/**
	 * Creates a report statistic tracker for given keys
	 *
	 * @param keys
	 * @return
	 */
	public static ReportStats create(String[] keys) {
		ReportStats reportStats = new ReportStats();
		for (String key : keys) {
			reportStats.set(key, 0);
		}
		return reportStats;
	}
	private Map<String, Stat>	stats	= new HashMap<String, Stat>();

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
	 * Returns a specific statistic
	 *
	 * @param key
	 * @return
	 */
	public synchronized int get(String key) {
		Stat stat = stats.get(key);
		if (stat != null) { return stat.count; }
		return 0;
	}

	/**
	 * @return The set of keys managed by this structure
	 */
	public Set<String> getKeys() {
		return stats.keySet();
	}

	/**
	 * Increment a single tracked statistic
	 *
	 * @param key
	 */
	public synchronized void inc(String key) {
		Stat stat = stats.get(key);
		if (stat == null) {
			stat = new Stat(0);
			stats.put(key, stat);
		}
		stat.count++;
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
	 * Remove tracking for a specific statistic
	 *
	 * @param key
	 */
	public synchronized void remove(String key) {
		stats.remove(key);
	}

	/**
	 * Sets a specific statistic for a given key
	 *
	 * @param key
	 * @param count
	 */
	public synchronized void set(String key, int count) {
		stats.put(key, new Stat(count));
	}
}
