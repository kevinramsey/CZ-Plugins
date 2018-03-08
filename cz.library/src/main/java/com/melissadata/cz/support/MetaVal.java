package com.melissadata.cz.support;

public class MetaVal {
	public String metaValue;
	public String webTag;
	public int metaSize;
	/**
	 * Keeps track of meta value, web tag, and size
	 * 
	 * @param metaValue
	 * @param webTag
	 * @param metaSize
	 */
	public MetaVal(String metaValue,String webTag, int metaSize) {
		super();
		this.metaValue = metaValue;
		this.webTag = webTag;
		this.metaSize = metaSize;
	}
	
}
