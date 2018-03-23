package com.melissadata.kettle.personator;

import org.apache.commons.io.FileUtils;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.Props;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.ui.spoon.Spoon;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class PluginInstaller {
	private static final String TAG_PERSONATOR_VERSION = "md_personator_version";
	private static final String TAG_MDLICENSE_START    = "mdLicense";
	private              String fileSep                = Const.FILE_SEPARATOR;
	private File srcDir;
	private File                kettleDir = new File(Const.getKettleDirectory());
	private File                jndiDir   = new File(Const.JNDI_DIRECTORY);
	private String              version   = "0";
	private boolean             update    = false;
	private boolean             installed = false;
	private LogChannelInterface log       = null;

	public PluginInstaller(boolean isContactZone, LogChannelInterface log) {
		this.log = log;
		if(isContactZone){
			log.logBasic("ContactZone MDPersonator Installed");
		} else {
			log.logBasic("Pentaho Install MDPersonator ");
			getSrcDir();
		}
	}

	private void copyRequiredFiles() {
		log.logBasic(" Copy required files.  Update = " + update);
		// Get the needed jars and files from MDCheck folder
		File srcMD = new File(srcDir, "MD");
		File srcKettle = new File(srcDir, "kettle");
		File srcLibext = new File(srcMD, "libext");
		File srcSamples = new File(srcDir, "mdSamples");
		Collection<File> cPropsFile = FileUtils.listFiles(srcKettle, new String[] { "prop" }, false);
		Collection<File> c32_bit = null;
		Collection<File> c64_bit = null;
		if (Const.isLinux()) {
			c32_bit = FileUtils.listFiles(new File(srcMD, "32_bit"), new String[] { "so" }, false);
			c64_bit = FileUtils.listFiles(new File(srcMD, "64_bit"), new String[] { "so" }, false);
		} else if (Const.isWindows()){
			c32_bit = FileUtils.listFiles(new File(srcMD, "32_bit"), new String[] { "dll" }, false);
			c64_bit = FileUtils.listFiles(new File(srcMD, "64_bit"), new String[] { "dll" }, false);
		} else {
			 //c32_bit = FileUtils.listFiles(new File(srcMD, "mac" + fileSep + "32_bit"), new String[] { "so", "jnilib" }, false);
			 c64_bit = FileUtils.listFiles(new File(srcMD, "mac" + fileSep + "64_bit"), new String[] { "so", "jnilib"  }, false);
		}
		File md_dir = new File(kettleDir, "MD");
		File dest32 = new File(kettleDir, "MD" + fileSep + "32_bit");
		File dest64 = new File(kettleDir, "MD" + fileSep + "64_bit");
		File destSamples = new File(jndiDir.getParent() + fileSep + "samples" + fileSep + "mdSamples");
		// Samples
		try {
			log.logBasic("Copy Samples from : " + srcSamples + " to " + destSamples);
			FileUtils.copyDirectory(srcSamples, destSamples, false);
		} catch (IOException e) {
			log.logError("IO error copying sample files. " + e.getMessage());
		}
		// READ ME
		try {
			FileUtils.copyFileToDirectory(new File(srcDir + fileSep + "MD_Linux_ReadMe.txt"), new File(jndiDir.getParent()), false);
		} catch (IOException e) {
			log.logError("Unable to copy ReadME : " + e.getMessage());
		}
		// dll / so
		File tmp = new File(kettleDir, "tmp" + fileSep + "32_bit");
		if(c32_bit != null) {
			log.logBasic("Copy 32_bit objects - ");
			for (File file : c32_bit) {
				try {
					log.logBasic("Copy 32_bit : " + file.getName() + " to " + dest32.getAbsolutePath());
					FileUtils.copyFileToDirectory(file, dest32, false);
				} catch (IOException e) {
					try {
						log.logBasic(file.getName() + " in use copy to tmp " + tmp.getAbsolutePath());
						FileUtils.copyFileToDirectory(file, tmp, false);
					} catch (IOException e1) {
						log.logError("Error Copying " + file + " : " + e.getMessage());
					}
				}
			}
		}
		tmp = new File(kettleDir, "tmp" + fileSep + "64_bit");
		log.logBasic("Copy 64_bit files -");
		for (File file : c64_bit) {
			//if (file.getName().startsWith(TAG_MDLICENSE_START)) {
				try {
					log.logBasic("Copy 64_bit : " + file.getName() + " to " + dest64.getAbsolutePath());
					FileUtils.copyFileToDirectory(file, dest64, false);
				} catch (IOException e) {
					try {
						log.logBasic(file.getName() + " in use copy to tmp " + tmp.getAbsolutePath());
						FileUtils.copyFileToDirectory(file, tmp, false);
					} catch (IOException e1) {
						log.logError("Unable to copy " + file + " : " + e.getMessage());
					}
				}
			//}
		}
		// libext cz.lib, MDSettings
		File dir_libext = new File(md_dir + fileSep + "libext");
		tmp = new File(kettleDir, "tmp" + fileSep + "libext");
		try {
			if (dir_libext.exists()) {
				FileUtils.deleteDirectory(dir_libext);
			}
			log.logBasic("Copy external libraries : " + srcLibext.getAbsolutePath() + " to " + md_dir + fileSep + "libext");
			FileUtils.copyDirectory(srcLibext, new File(md_dir + fileSep + "libext"));
		} catch (IOException e) {
			try {
				log.logBasic(srcLibext + " in use copy to tmp " + tmp.getAbsolutePath());
				FileUtils.copyDirectory(srcLibext, tmp, false);
			} catch (IOException e1) {
				log.logError("Unable to copy " + srcLibext + " : " + e.getMessage());
			}
		}
		try {
			if (!update) {
				log.logBasic("Copy mdProps.prop");
				for (File propFile : cPropsFile) {
					if (!propFile.exists()) {
						FileUtils.copyFileToDirectory(propFile, kettleDir, false);
					}
					FileUtils.copyFileToDirectory(propFile, new File(kettleDir, "tmp"), false);
				}
			} else {
				log.logBasic("Updating mdProps.prop");
				for (File file : cPropsFile) {
					FileUtils.copyFileToDirectory(file, new File(kettleDir, "tmp"), false);
				}
			}
		} catch (IOException e) {
			log.logError("IO error copying file. " + e.getMessage());
		}
	}
	
	public void checkCZInstall(String currentVersion) {
		getVersionFromManifest();
		//System.out.println("Current = " + currentVersion + "   From manifest = " + version);
		if (!currentVersion.equals(version)) {
			updateCZ();			
		}
	}

	private void updateCZ() {
	//	System.out.println("Updated to: " + version);
		Props.getInstance().setProperty(TAG_PERSONATOR_VERSION, version);
		Props.getInstance().saveProps();
	}

	private String getVersionFromManifest() {
		Class<?> clazz = this.getClass();
		String className = clazz.getSimpleName() + ".class";
		String classPath = clazz.getResource(className).toString();
		//String buildNum = classPath.substring(classPath.indexOf("Build") + 5, classPath.indexOf(".jar") - 1);
		if (!classPath.startsWith("jar")) {
			version = "0";//BaseMessages.getString(PKG, "MDCheckDialog.VersionNotFound");
		} else {
			String manifestPath = classPath.substring(0, classPath.lastIndexOf("!") + 1) + "/META-INF/MANIFEST.MF";
			try {
				Manifest manifest = new Manifest(new URL(manifestPath).openStream());
				String implVersion = manifest.getMainAttributes().getValue(Attributes.Name.IMPLEMENTATION_VERSION);
				if (implVersion == null) {
					version = "0";
				} else {
					version = implVersion;
				}
			} catch (Exception e) {
				version = "0";
			}
		}
		return version;
	}

	public void doInstall(boolean isInstalled, boolean isUpdate, String currentVersion) throws KettleException {
		installed = isInstalled;
		update = isUpdate;
		version = currentVersion;

		if (isUpdate) {
			log.logBasic("Updating MDPersonator Plugin to  " + version);
		} else {
			log.logBasic("Installing MDPersonator Plugin " + version);
		}

		if (!installed) {
			log.logBasic("Installing -");
			try {
				loadClassFiles();
			} catch (Exception e) {
				log.logError("Error loading class files: " + e.getMessage());
				throw new KettleException("Failed to load external class files: " + e.getMessage());
			}
			copyRequiredFiles();
			Props.getInstance().setProperty(TAG_PERSONATOR_VERSION, version);
			Props.getInstance().saveProps();
		} else {
			
			if (update) {
				log.logBasic("Updating -");
				try {
					loadClassFiles();
					// loadReportinClasses();
				} catch (Exception e) {
					log.logError("Error loading class files: " + e.getMessage());
					throw new KettleException("Failed to load external class files: " + e.getMessage());
				}
				copyRequiredFiles();
				Props.getInstance().setProperty(TAG_PERSONATOR_VERSION, version);
				Props.getInstance().saveProps();
			}
		}
	}

	private void getSrcDir() {
		String decodedPath = "";
		File ssdd = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
		try {
			decodedPath = URLDecoder.decode(ssdd.toString(), "UTF-8");
			decodedPath = decodedPath.substring(0, decodedPath.lastIndexOf(fileSep));
		} catch (UnsupportedEncodingException e) {
			log.logError("Error getting source dir: " + e.getMessage());
		}
		srcDir = new File(decodedPath);
	}

	private void loadClassFiles() throws SecurityException, NoSuchMethodException, IllegalArgumentException, MalformedURLException, IllegalAccessException, InvocationTargetException {
		boolean loadExt = true;
		File lib = new File(srcDir, "MD" + fileSep + "libext");
		if (lib.exists()) {
			URLClassLoader spoonLoader = (URLClassLoader) Spoon.class.getClassLoader();// (URLClassLoader)ClassLoader.getSystemClassLoader();
			URL[] urls = spoonLoader.getURLs();
			for (URL url : urls) {
				if (url.toString().contains("MDSettings")) {
					loadExt = false;
				}
			}
			if (loadExt) {
				log.logBasic("Loading external class files -");
				Class<URLClassLoader> sysClass = URLClassLoader.class;
				Method sysMethod = sysClass.getDeclaredMethod("addURL", new Class[] { URL.class });
				sysMethod.setAccessible(true);
				for (File file : lib.listFiles()) {
					sysMethod.invoke(spoonLoader, new Object[] { file.toURI().toURL() });
					log.logBasic("Loading external class file : " + file.getAbsolutePath());
				}
			} else {
				log.logBasic("MD classes already loaded -");
			}
		}
	}
}
