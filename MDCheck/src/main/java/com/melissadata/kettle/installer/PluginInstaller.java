package com.melissadata.kettle.installer;

import com.melissadata.cz.MDProps;
import com.melissadata.cz.ui.MDAbstractDialog;
import com.melissadata.cz.ui.MDDialogParent;
import com.melissadata.kettle.AdvancedConfigurationMeta;
import com.melissadata.kettle.MDCheckDialog;
import com.melissadata.kettle.MDCheckMeta;
import com.melissadata.kettle.support.MDCheckHelper;
import org.apache.commons.io.FileUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.Props;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.ui.spoon.Spoon;

import java.io.*;
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
	public class dataPathDialog extends MDAbstractDialog {
		private MDCheckHelper helper;

		public dataPathDialog(MDDialogParent dialog, int flags) {
			super(dialog, flags);
		}

		@Override
		public void ok() {
			MDProps.setProperty(TAG_LOCAL_DATA_PATH, dataDir.toString());
			if (createDataDir(dataDir)) {
				dispose();
			} else {
				MessageDialog.openError(getShell(), getString("DirectoryDialogError.Title"), getString("DirectoryDialogError.Label", dataDir.toString(), dataErrorMsg));
			}
		}

		@Override
		protected void createContents(Composite parent, Object arg1) {
			helper = new MDCheckHelper((MDCheckDialog) dialog);
			parent.getShell().setText(getString("DataDialog.Title"));
			// Create the composite that will hold the contents of the tab
			Composite wComp = new Composite(parent, SWT.NONE);
			helper.setLook(wComp);
			FormLayout fl = new FormLayout();
			fl.marginWidth = 3;
			fl.marginHeight = 3;
			wComp.setLayout(fl);
			// Description line
			Label description = new Label(wComp, SWT.LEFT | SWT.WRAP);
			description.setText(getString("DataDialog.Label"));
			helper.setLook(description);
			FormData fd = new FormData();
			fd.left = new FormAttachment(0, 0);
			fd.top = new FormAttachment(0, 0);
			description.setLayoutData(fd);
			final Text dirTxt = new Text(wComp, SWT.BORDER);
			fd = new FormData();
			fd.left = new FormAttachment(0, 0);
			fd.right = new FormAttachment(100, helper.margin);
			fd.top = new FormAttachment(description, helper.margin);
			dirTxt.setLayoutData(fd);
			dirTxt.setText(dataDir.toString());
			dirTxt.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent arg0) {
					dataDir = new File(dirTxt.getText());
				}
			});
			Button button = new Button(wComp, SWT.PUSH);
			fd = new FormData();
			fd.left = new FormAttachment(0, 0);
			fd.right = new FormAttachment(25, helper.margin);
			fd.top = new FormAttachment(dirTxt, helper.margin);
			button.setLayoutData(fd);
			button.setText("Browse...");
			button.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent event) {
					DirectoryDialog dlg = new DirectoryDialog(helper.getShell());
					dlg.setFilterPath(dirTxt.getText());
					dlg.setText(getString("DirectoryDialog.Title"));
					dlg.setMessage(getString("DirectoryDialog.Label"));
					String dir = dlg.open();
					if (dir != null) {
						dirTxt.setText(dir);
						dataDir = new File(dirTxt.getText());
					}
				}
			});
			// Fit the composite within its container (the scrolled composite)
			fd = new FormData();
			fd.left = new FormAttachment(0, 0);
			fd.top = new FormAttachment(0, 0);
			fd.right = new FormAttachment(100, 0);
			fd.bottom = new FormAttachment(100, 0);
			wComp.setLayoutData(fd);
			wComp.pack();
		}

		@Override
		protected boolean getData(Object arg0) throws KettleException {
			return false;
		}

		@Override
		protected String getHelpURLKey() {
			return null;
		}

		@Override
		protected Class<?> getPackage() {
			return PKG;
		}

		@Override
		protected String getStringPrefix() {
			return "PluginInStaller";
		}

		@Override
		protected void init(Object arg0) {
			// nothing to init
		}
	}

	private static Class<?>		PKG					= MDCheckMeta.class;
	private static final String	TAG_LOCAL_DATA_PATH	= "data_path";
	private String				fileSep				= Const.FILE_SEPARATOR;
	private static final String	TAG_MDCHECK_VERSION	= "md_contactverify_version";
	private File				srcDir;
	private File				jndiDir				= new File(Const.JNDI_DIRECTORY);
	private File				kettleDir			= new File(Const.getKettleDirectory());
	private File				dataDir				= null;
	private String				version				= "0";
	// private String build = "";
	private boolean				update;
	private boolean				installed;
	private String				dataErrorMsg		= "";
	private LogChannelInterface log;

	public PluginInstaller(boolean isContactZone, LogChannelInterface log) {

		this.log = log;
		if(isContactZone){
			log.logBasic("ContactZone MDCheck Installed");

		} else {
			log.logBasic("Pentaho Install MDCheck ");
			getSrcDir();
			setDataDirDefault();
		}
	}


	public void dirChooser(Display display) {
		final Shell shell = new Shell(display);
		shell.setText("Directory Browser");
		shell.setLayout(new GridLayout(6, true));
		new Label(shell, SWT.NONE).setText("Directory:");
		final Text dirTxt = new Text(shell, SWT.BORDER);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 4;
		dirTxt.setLayoutData(data);
		dirTxt.setText(dataDir.toString());
		// TODO put text in messages
		Button button = new Button(shell, SWT.PUSH);
		button.setText("Browse...");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				DirectoryDialog dlg = new DirectoryDialog(shell);
				dlg.setFilterPath(dirTxt.getText());
				dlg.setText("SWT's DirectoryDialog");
				dlg.setMessage("Select a directory");
				String dir = dlg.open();
				if (dir != null) {
					dirTxt.setText(dir);
					dataDir = new File(dirTxt.getText());
				}
			}
		});
		shell.pack();
		shell.open();
		MDProps.setProperty(TAG_LOCAL_DATA_PATH, dataDir.toString());
	}
	
	public void checkCZInstall(String currentVersion) {
		getVersionFromManifest();
	//	System.out.println("Current = " + currentVersion + "   From manifest = " + version);
		if (!currentVersion.equals(version)) {
			updateCZ();			
		}
	}

	private void updateCZ() {
		//System.out.println("Updated to: " + version);
		Props.getInstance().setProperty(TAG_MDCHECK_VERSION, version);
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

	//	System.out.println("Contact Verify components : " + "Installed  = " + installed  + " Update  = " + update );
		if (!installed) {
			log.logBasic("INSTALLING Melissa Contact Verify components : " + "Version = " + version);
		} else if (update) {
			log.logBasic("Updating Melissa Contact Verify components : " + "Version = " + version);
		} else if (installed && !update){
			log.logBasic("Contact Verify components : " + "Version = " + version);
		}
		if (!installed) {
			log.logBasic("Installing MDCheck -");
			try {
				loadClassFiles();
				// loadReportinClasses();
			} catch (Exception e) {
				log.logError("Error loading class files: " + e.getMessage());
				throw new KettleException("Failed to load external class files: " + e.getMessage());
			}
			copyRequiredFiles();
			Props.getInstance().setProperty(TAG_MDCHECK_VERSION, version);
			Props.getInstance().saveProps();
		} else {
			if (update) {
				log.logBasic("Updating MDCheck -");
				try {
					loadClassFiles();
					// loadReportinClasses();
				} catch (Exception e) {
					log.logError("Error loading class files: " + e.getMessage());
					throw new KettleException("Failed to load external class files: " + e.getMessage());
				}
				copyRequiredFiles();
				// this is writing to the .spoonrc file in the .kettle dir
				Props.getInstance().setProperty(TAG_MDCHECK_VERSION, version);
				Props.getInstance().saveProps();
			}
		}
	}

	public boolean showDataDialog() {
		boolean toShow = false;
		String dPath = MDProps.getProperty(TAG_LOCAL_DATA_PATH, "");
		if (Const.isEmpty(dPath)) {
			toShow = true;
		} else {
			dataDir = new File(dPath);
			createDataDir(dataDir);
		}
		return toShow;
	}
	

	
//	Code determines which Operation System is being used and copies correct files

	private void copyRequiredFiles() throws KettleException {
		log.logBasic(" Copy required files.  Update = " + update);
		// Get the needed jars and files from MDCheck folder
		File srcMD             = new File(srcDir, "MD");
		File srcJNDI           = new File(srcDir, "JNDI");
		File srcKettle         = new File(srcDir, "kettle");
		File srcKettleMU       = new File(srcDir, "kettle" + fileSep + "matchup");
		File srcKettleMUglobal = new File(srcDir, "kettle" + fileSep + "matchup.global");
		File srcLibext         = new File(srcMD, "libext");
		File srcReports        = new File(srcDir, "MDReporting");
		File srcSamples        = new File(srcDir, "mdSamples");
		
		// Check to make sure they all exist
		if(!srcMD.exists() || !srcMD.isDirectory()){
			throw new KettleException("Error: Missing directory " + srcMD);
		}
		if(!srcJNDI.exists() || !srcJNDI.isDirectory()){
			throw new KettleException("Error: Missing directory " + srcJNDI);
		}
		if(!srcKettle.exists() || !srcKettle.isDirectory()){
			throw new KettleException("Error: Missing directory " + srcKettle);
		}
		if(!srcKettleMU.exists() || !srcKettleMU.isDirectory()){
			throw new KettleException("Error: Missing directory " + srcKettleMU);
		}
		if(!srcKettleMUglobal.exists() || !srcKettleMUglobal.isDirectory()){
			throw new KettleException("Error: Missing directory " + srcKettleMUglobal);
		}
		if(!srcLibext.exists() || !srcLibext.isDirectory()){
			throw new KettleException("Error: Missing directory " + srcLibext);
		}
		if(!srcReports.exists() || !srcReports.isDirectory()){
			throw new KettleException("Error: Missing directory " + srcLibext);
		}
		
		
		Collection<File> cJNDI = FileUtils.listFiles(srcJNDI, new String[] { "properties" }, false);
		Collection<File> cKettle = FileUtils.listFiles(srcKettle, new String[] { "prop" }, false);
		Collection<File> cReports = FileUtils.listFiles(srcReports, new String[] { "prpt" }, false);
		Collection<File> c32_bit = null;
		Collection<File> c64_bit = null;
		if (Const.isLinux()) {
			c32_bit = FileUtils.listFiles(new File(srcMD, "32_bit"), new String[] { "so" }, false);
			c64_bit = FileUtils.listFiles(new File(srcMD, "64_bit"), new String[] { "so" }, false);
		} else if (Const.isWindows()) {
			c32_bit = FileUtils.listFiles(new File(srcMD, "32_bit"), new String[] { "dll" }, false);
			c64_bit = FileUtils.listFiles(new File(srcMD, "64_bit"), new String[] { "dll" }, false);
		} else {
			// MAC OS 
			 //c32_bit = FileUtils.listFiles(new File(srcMD, "mac" + fileSep + "32_bit"), new String[] { "so", "jnilib" }, false);
			 c64_bit = FileUtils.listFiles(new File(srcMD, "mac" + fileSep + "64_bit"), new String[] { "so", "jnilib"  }, false);
		}

		if(/*c32_bit.isEmpty() ||*/ c64_bit.isEmpty()){
			throw new KettleException("Error: Missing Library Files in " + srcMD);
		}

		File md_dir       = new File(kettleDir, "MD");
		File libext_dir   = new File(md_dir + fileSep + "libext");
		File dest32       = new File(kettleDir, "MD" + fileSep + "32_bit");
		File dest64       = new File(kettleDir, "MD" + fileSep + "64_bit");
		File destSamples  = new File(jndiDir.getParent() + fileSep + "samples" + fileSep + "mdSamples");
		File destReports  = new File(libext_dir, "reporting");
		File destMU       = new File(kettleDir, "matchup");
		File destMUglobal = new File(kettleDir, "matchup.global");
		File muDll        = null;
		if(Const.isWindows()) {
			muDll = new File(srcMD, "64_bit" + fileSep + "mdMatchUp.dll");
		}
		// Samples
		if(srcSamples.exists() && srcSamples.isDirectory()){
			log.logBasic("Copy Samples from : " + srcSamples + " to " + destSamples);
			try {
				FileUtils.copyDirectory(srcSamples, destSamples, false);
			} catch (IOException e) {
				log.logError("IO error copying sample files. " + e.getMessage());
			}
		} else {
			log.logError("Warning: Unable to copy samples. " + srcSamples + " Does not exist or is not a directory.");
		}
		// Read ME
		try {
			FileUtils.copyFileToDirectory(new File(srcDir + fileSep + "MD_Linux_ReadMe.txt"), new File(jndiDir.getParent()), false);
		} catch (IOException e) {
			log.logError("Warning Unable to copy ReadME : " + e.getMessage());
		}

		// Dll-so

		File tmp = new File(kettleDir, "tmp" + fileSep + "32_bit");
		if(c32_bit != null) {
			log.logBasic("Copy 32_bit objects - ");
			for (File file : c32_bit) {
				try {
					log.logBasic("Copy 32_bit : " + file.getName() + " to " + dest32.getAbsolutePath());
					FileUtils.copyFileToDirectory(file, dest32, false);
				} catch (IOException e) {
					log.logBasic(file.getName() + " in use copy to tmp " + tmp.getAbsolutePath());
					try {
						FileUtils.copyFileToDirectory(file, tmp, false);
					} catch (IOException e1) {
						log.logError("Error Copying " + file + " : " + e.getMessage());
					}
				}
			}
		}
		tmp = new File(kettleDir, "tmp" + fileSep + "64_bit");
		log.logBasic("Copy 64_bit objects -");
		for (File file : c64_bit) {
			try {
				log.logBasic("Copy 64_bit : " + file.getName() + " to " + dest64.getAbsolutePath());
				FileUtils.copyFileToDirectory(file, dest64, false);
			} catch (IOException e) {
				log.logBasic(file.getName() + " in use copy to tmp " + tmp.getAbsolutePath());
				try {
					FileUtils.copyFileToDirectory(file, tmp, false);
				} catch (IOException e1) {
					log.logError("Unable to copy " + file + " : " + e.getMessage());
				}
			}
		}
		// czlib MDSettings
		tmp = new File(kettleDir, "tmp" + fileSep + "libext");
		try {
			if (libext_dir.exists()) {
			//	System.out.println(dir_libext.getAbsolutePath() + " is already here so delete it");
				FileUtils.deleteDirectory(libext_dir);
			}

			log.logBasic("Copy external libraries : " + srcLibext.getAbsolutePath() + " to " + md_dir + fileSep + "libext");
			FileUtils.copyDirectory(srcLibext, new File(md_dir + fileSep + "libext"));
		} catch (IOException e) {
			log.logBasic(srcLibext + " in use copy to tmp " + tmp.getAbsolutePath());
			try {
				FileUtils.copyDirectory(srcLibext, tmp, false);
			} catch (IOException e1) {
				log.logError("Unable to copy " + srcLibext + " : " + e.getMessage());
			}
		}
		// Reporting prpt files
		log.logBasic("Copy Reports  to " + destReports.getAbsolutePath());
		for (File file : cReports) {
			try {
				FileUtils.copyFileToDirectory(file, destReports, false);
			} catch (IOException e) {

				log.logError("Unable to copy " + file + " : " + e.getMessage());
			}
		}
		// MATCH UP
		try {
			if (!update) {
				log.logBasic("Copy matchup files from : " + srcKettleMU + " to " + destMU );
				FileUtils.copyDirectory(srcKettleMU, destMU, false);
				log.logBasic("Copy matchup.global files from : " + srcKettleMUglobal + " to " + destMUglobal );
				FileUtils.copyDirectory(srcKettleMUglobal, destMUglobal, false);
			} else {
				log.logBasic("Updating matchUp files - ");
				Collection<File> cMU = FileUtils.listFiles(srcKettleMU, new String[] { "exe", "dat", "cfg", "dll" }, false);
				for (File file : cMU) {
					log.logBasic("Updating matchUp file: " + file.getAbsolutePath());
					FileUtils.copyFileToDirectory(file, destMU, false);
				}

				Collection<File> cMUglobal = FileUtils.listFiles(srcKettleMUglobal, new String[] { "exe", "dat", "cfg", "dll", "3db", "hto", "kof", "tths", "vof" }, false);
				log.logBasic("Updating matchUp.global files - ");
				for (File file : cMUglobal) {
					log.logBasic("Updating matchUp.global file: " + file.getAbsolutePath());
					FileUtils.copyFileToDirectory(file, destMUglobal, false);
				}
			}
			if(Const.isWindows() && (muDll != null)) {
				log.logBasic("Copy matchup dll - " + muDll );
				FileUtils.copyFileToDirectory(muDll, destMU, false);
				FileUtils.copyFileToDirectory(muDll, destMUglobal, false);
			}
		} catch (IOException e) {
			log.logError("IO error copying files. " + e.getMessage());
		}
		// Reporting DB props
		log.logBasic("Writing reporting paths to JNDI " );
		for (File file : cJNDI) {
			try {
				FileUtils.copyFileToDirectory(file, jndiDir, false);
			} catch (IOException e) {
				log.logError("IO error copying file. " + e.getMessage());
			}
			try {
				if (file.getName().contentEquals("ga.properties")) {
					String czJniPath = jndiDir.toString() + fileSep + "ga.properties";
					String dbPath = "";
					if (Const.isWindows()) {
						dbPath = "GlobalAddressStats/url=jdbc:sqlite:" + kettleDir.toString() + "\\GlobalAddressStats.db";
						dbPath = "\n" + dbPath.replaceAll("\\\\", "\\\\\\\\");
					} else {
						dbPath = "GlobalAddressStats/url=jdbc:sqlite:" + kettleDir.toString() + fileSep + "GlobalAddressStats.db";
						dbPath = "\n" + dbPath;
					}
					FileWriter fileWritter = new FileWriter(czJniPath, true);
					BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
					bufferWritter.write(dbPath);
					bufferWritter.close();
				} else if (file.getName().contentEquals("cz.properties")) {
					String czJniPath = jndiDir.toString() + fileSep + "cz.properties";
					String dbPath = "";
					if (Const.isWindows()) {
						dbPath = "ContactStats/url=jdbc:sqlite:" + kettleDir.toString() + "\\ContactStats.db";
						dbPath = "\n" + dbPath.replaceAll("\\\\", "\\\\\\\\");
					} else {
						dbPath = "ContactStats/url=jdbc:sqlite:" + kettleDir.toString() + fileSep + "ContactStats.db";
						dbPath = "\n" + dbPath;
					}
					FileWriter fileWritter = new FileWriter(czJniPath, true);
					BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
					bufferWritter.write(dbPath);
					bufferWritter.close();
				}
			} catch (IOException e) {
				log.logError("Error writing JNDI db paths. " + e.getMessage());
			}
		}
		try {
			log.logBasic("Writing mdProps.prop file");
			if (!update) {
				for (File file : cKettle) {
					if (!file.exists()) {
						log.logBasic("Copy mdProps.prop to " + kettleDir );
						FileUtils.copyFileToDirectory(file, kettleDir, false);
					}
					log.logBasic("Copy mdProps.prop to " + kettleDir + fileSep + "tmp" );
					FileUtils.copyFileToDirectory(file, new File(kettleDir, "tmp"), false);
				}
			} else {
				for (File file : cKettle) {
					log.logBasic("Copy update mdProps.prop to " + kettleDir + fileSep + "tmp" );
					FileUtils.copyFileToDirectory(file, new File(kettleDir, "tmp"), false);
				}
			}
		} catch (IOException e) {
			log.logError("IO error copying files. " + e.getMessage());
		}
	}

	private boolean createDataDir(File dataDir) {
		File mdData = new File(srcDir, fileSep + "MDdata");
		try {
			FileUtils.copyDirectory(mdData, dataDir);
			return true;
		} catch (IOException e) {
			System.out.println("Error copying data " + e.getMessage());
			dataErrorMsg = e.getMessage();
			return false;
		}
	}

	private void getSrcDir() {
		String decodedPath = "";
		File ssdd = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
		try {
			decodedPath = URLDecoder.decode(ssdd.toString(), "UTF-8");
			decodedPath = decodedPath.substring(0, decodedPath.lastIndexOf(fileSep));
			AdvancedConfigurationMeta.setTemplatePath(decodedPath + fileSep + "MDReporting");
		} catch (UnsupportedEncodingException e) {
			System.out.println("Error getting source dir: " + e.getMessage());
		}

		srcDir = new File(decodedPath);
		log.logBasic("MDCheck source files location : " + srcDir.getAbsolutePath());
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

	private void setDataDirDefault() {
		if (Const.isWindows()) {
			File[] roots = File.listRoots();
			dataDir = new File(roots[0], fileSep + "melissaData" + fileSep + "DQT" + fileSep + "data");
		} else {
			dataDir = new File(Const.USER_HOME_DIRECTORY + fileSep + "MDdata");
		}
	}
}
