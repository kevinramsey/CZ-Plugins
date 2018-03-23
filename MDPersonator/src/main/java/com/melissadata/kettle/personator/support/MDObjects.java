package com.melissadata.kettle.personator.support;

import java.io.File;

import com.melissadata.kettle.personator.MDPersonator;




/**
 * Allows for more controlled loading of Melissa Data object libraries
 */
public class MDObjects {

	private static final String osProp = System.getProperty("os.name"); 
	private static final String osArch = System.getProperty("os.arch"); 

	public static class MDObjectException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = 92471452889847592L;
		
		public MDObjectException(String msg, Throwable cause) {
			super(msg, cause);
		}
	}

	/**
	 * Make sure the libraries for an object exist and are loaded
	 * 
	 * @param name
	 * @throws MDObjectException 
	 */
	public static void checkLibrary(String name) throws MDObjectException {
		
		// Base object directory
		File dir = new File(osProp.contains("Windows") ? "md" : "md/Linux");
		
		// architecture
		dir = new File(dir, (osArch.contains("64") ? "64_bit" : "32_bit"));
				
		// operating system suffix
		String suffix = osProp.contains("Windows") ? ".dll" : ".so"; 

		// operating system prefix
		String prefix = osProp.contains("Windows") ? "" : "lib"; 
		
		// primary and wrapper libraries
		File mainLib = new File(dir, prefix + name + suffix);
		
		// Load them
		try {
			System.load(mainLib.getAbsolutePath());
		} catch (UnsatisfiedLinkError e) {
			String msg = MDPersonator.getErrorString("BadDll", name);
			throw new MDObjectException(MDPersonator.getErrorString("InitializeService", msg), e);
		}
	}
}
