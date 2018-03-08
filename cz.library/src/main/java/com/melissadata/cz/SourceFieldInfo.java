package com.melissadata.cz;

import java.util.HashSet;
import java.util.Set;

/**
 * Class used to hold source field name and usage
 */
public class SourceFieldInfo {

	private String name;
	private Set<String> usage;

	public SourceFieldInfo(String name) {
		this.name = name;

		usage = new HashSet<String>();
	}

	public String getName() {
		return name;
	}

	public void addUsage(String usageID) {
		usage.add(usageID);
	}

	public void removeUsage(String usageID) {
		usage.remove(usageID);
	}
	
	public String getUsage() {
		StringBuffer s = new StringBuffer();
		String sep = "";
		for (String use : usage) {
			s.append(sep).append(use);
			sep = ",";
		}
		return s.toString();
	}

	@Override
	public String toString() {
		return getName() + ":" + getUsage();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SourceFieldInfo other = (SourceFieldInfo) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	
}
