package com.melissadata.kettle.businesscoder.support;

import com.melissadata.cz.MDProps;
import com.melissadata.cz.ui.MDAbstractDialog;
import com.melissadata.cz.ui.MDDialogParent;
import com.melissadata.kettle.businesscoder.MDBusinessCoderDialog;
import com.melissadata.kettle.businesscoder.MDBusinessCoderMeta;
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
	private static final String              TAG_LOCAL_DATA_PATH       = "data_path";
	private static final String              TAG_BUSINESSCODER_VERSION = "md_businesscoder_version";
	private static       Class<?>            PKG                       = MDBusinessCoderMeta.class;
	private              String              fileSep                   = Const.FILE_SEPARATOR;
	private              File                srcDir                    = null;
	private              File                jndiDir                   = new File(Const.JNDI_DIRECTORY);
	private              File                kettleDir                 = new File(Const.getKettleDirectory());
	private              File                dataDir                   = null;
	private              String              version                   = "";
	private              boolean             update                    = false;
	private              boolean             installed                 = false;
	private              String              dataErrorMsg              = "";
	private              LogChannelInterface log                       = null;

	public PluginInstaller(boolean isContactZone, LogChannelInterface log) {
		this.log = log;
		if (isContactZone) {
			log.logDebug("ContactZone MDBusinessCoder Installed");
		} else {
			log.logDebug("Pentaho Install MDBusinessCoder ");
			getSrcDir();
			setDataDirDefault();
		}
	}

	// TODO put text in messages
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
		Button button = new Button(shell, SWT.PUSH);
		button.setText("Browse...");
		button.addSelectionListener(new SelectionAdapter() {
			@Override public void widgetSelected(SelectionEvent event) {
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
		if (!currentVersion.equals(version)) {
			updateCZ();
		}
	}

	private void updateCZ() {
		Props.getInstance().setProperty(TAG_BUSINESSCODER_VERSION, version);
		Props.getInstance().saveProps();
	}

	private String getVersionFromManifest() {
		Class<?> clazz = this.getClass();
		String className = clazz.getSimpleName() + ".class";
		String classPath = clazz.getResource(className).toString();
		if (!classPath.startsWith("jar")) {
			version = "0";
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
			log.logDebug("Updating MDBusinessCoder Plugin to  " + version);
		} else {
			log.logDebug("Installing MDBusinessCoder Plugin " + version);
		}
		if (!installed) {
			log.logDebug("Installing -");
			try {
				loadClassFiles();
			} catch (Exception e) {
				log.logError("Error loading class files: " + e.getMessage());
				throw new KettleException("Failed to load external class files: " + e.getMessage());
			}
			copyRequiredFiles();
			Props.getInstance().setProperty(TAG_BUSINESSCODER_VERSION, version);
			Props.getInstance().saveProps();
		} else if (update) {
			log.logDebug("Updating -");
			try {
				loadClassFiles();
			} catch (Exception e) {
				log.logError("Error loading class files: " + e.getMessage());
				throw new KettleException("Failed to load external class files: " + e.getMessage());
			}
			copyRequiredFiles();
			Props.getInstance().setProperty(TAG_BUSINESSCODER_VERSION, version);
			Props.getInstance().saveProps();
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

	private void copyRequiredFiles() {
		log.logDebug(" Copy required files.  Update = " + update);
		// Get the needed jars and files from MDCheck folder
		File srcMD = new File(srcDir, "MD");
		File srcKettle = new File(srcDir, "kettle");
		File srcLibext = new File(srcMD, "libext");
		File srcSamples = new File(srcDir, "mdSamples");
		Collection<File> cPropFiles = FileUtils.listFiles(srcKettle, new String[] { "prop" }, false);
		Collection<File> c32_bit = null;
		Collection<File> c64_bit = null;
		if (Const.isLinux()) {
			c32_bit = FileUtils.listFiles(new File(srcMD, "32_bit"), new String[] { "so" }, false);
			c64_bit = FileUtils.listFiles(new File(srcMD, "64_bit"), new String[] { "so" }, false);
		} else if (Const.isWindows()) {
			c32_bit = FileUtils.listFiles(new File(srcMD, "32_bit"), new String[] { "dll" }, false);
			c64_bit = FileUtils.listFiles(new File(srcMD, "64_bit"), new String[] { "dll" }, false);
		} else {
			//c32_bit = FileUtils.listFiles(new File(srcMD, "mac" + fileSep + "32_bit"), new String[] { "so", "jnilib" }, false);
			c64_bit = FileUtils.listFiles(new File(srcMD, "mac" + fileSep + "64_bit"), new String[] { "so", "jnilib" }, false);
		}
		File md_dir = new File(kettleDir, "MD");
		File dest32 = new File(kettleDir, "MD" + fileSep + "32_bit");
		File dest64 = new File(kettleDir, "MD" + fileSep + "64_bit");
		File destSamples = new File(jndiDir.getParent() + fileSep + "samples" + fileSep + "mdSamples");
		// Copy over the needed files
		// MD Sample transforms
		try {
			log.logDebug("Copy Samples from : " + srcSamples + " to " + destSamples);
			FileUtils.copyDirectory(srcSamples, destSamples, false);
		} catch (IOException e) {
			log.logError("IO error copying sample files. " + e.getMessage());
		}
		// Linux readme text file
		try {
			FileUtils.copyFileToDirectory(new File(srcDir + fileSep + "MD_Linux_ReadMe.txt"), new File(jndiDir.getParent()), false);
		} catch (IOException e) {
			log.logError("Unable to copy Readme.txt : " + e.getMessage());
		}
		// dll / so
		File tmp = new File(kettleDir, "tmp" + fileSep + "32_bit");
		if(c32_bit != null) {
			log.logDebug("Copy 32_bit objects - ");
			for (File file : c32_bit) {
				try {
					log.logDebug("Copy 32_bit : " + file.getName() + " to " + dest32.getAbsolutePath());
					FileUtils.copyFileToDirectory(file, dest32, false);
				} catch (IOException e) {
					try {
						log.logDebug(file.getName() + " in use copy to tmp " + tmp.getAbsolutePath());
						FileUtils.copyFileToDirectory(file, tmp, false);
					} catch (IOException e1) {
						log.logError("Error Copying " + file + " : " + e.getMessage());
					}
				}
			}
		}
		tmp = new File(kettleDir, "tmp" + fileSep + "64_bit");
		log.logDebug("Copy 64_bit files -");
		for (File file : c64_bit) {
			try {
				log.logDebug("Copy 64_bit : " + file.getName() + " to " + dest64.getAbsolutePath());
				FileUtils.copyFileToDirectory(file, dest64, false);
			} catch (IOException e) {
				try {
					log.logDebug(file.getName() + " in use copy to tmp " + tmp.getAbsolutePath());
					FileUtils.copyFileToDirectory(file, tmp, false);
				} catch (IOException e1) {
					log.logError("Unable to copy " + file + " : " + e.getMessage());
				}
			}
		}
		// cz.lib, MDSettings
		File dir_libext = new File(md_dir + fileSep + "libext");
		tmp = new File(kettleDir, "tmp" + fileSep + "libext");
		try {
			if (dir_libext.exists()) {
				FileUtils.deleteDirectory(dir_libext);
			}
			log.logDebug("Copy external libraries : " + srcLibext.getAbsolutePath() + " to " + md_dir + fileSep + "libext");
			FileUtils.copyDirectory(srcLibext, new File(md_dir + fileSep + "libext"));
		} catch (IOException e) {
			try {
				log.logDebug(srcLibext + " in use copy to tmp " + tmp.getAbsolutePath());
				FileUtils.copyDirectory(srcLibext, tmp, false);
			} catch (IOException e1) {
				log.logError("Unable to copy " + srcLibext + " : " + e.getMessage());
			}
		}
		// props file
		try {
			if (!update) {
				log.logDebug("Copy mdProps.prop");
				for (File propFile : cPropFiles) {
					if (!propFile.exists()) {
						FileUtils.copyFileToDirectory(propFile, kettleDir, false);
					}
					FileUtils.copyFileToDirectory(propFile, new File(kettleDir, "tmp"), false);
				}
			} else {
				log.logDebug("Updating mdProps.prop");
				for (File file : cPropFiles) {
					FileUtils.copyFileToDirectory(file, new File(kettleDir, "tmp"), false);
				}
			}
		} catch (IOException e) {
			log.logError("IO error copying mdProps.prop. " + e.getMessage());
		}
	}

	private boolean createDataDir(File dataDir) {
		File mdData = new File(srcDir, fileSep + "MDdata");
		try {
			FileUtils.copyDirectory(mdData, dataDir);
			return true;
		} catch (IOException e) {
			log.logError("Error copying data " + e.getMessage());
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
		} catch (UnsupportedEncodingException e) {
			log.logError("Error getting source dir: " + e.getMessage());
		}
		srcDir = new File(decodedPath);
		log.logDebug("MDBusinessCoder source files location : " + srcDir.getAbsolutePath());
	}

	private void loadClassFiles() throws SecurityException, NoSuchMethodException, IllegalArgumentException, MalformedURLException, IllegalAccessException, InvocationTargetException {
		boolean loadExt = true;
		File libext_dir = new File(srcDir, "MD" + fileSep + "libext");
		if (libext_dir.exists()) {
			URLClassLoader spoonLoader = (URLClassLoader) Spoon.class.getClassLoader();// (URLClassLoader)ClassLoader.getSystemClassLoader();
			URL[] urls = spoonLoader.getURLs();
			// check if already loaded
			for (URL url : urls) {
				if (url.toString().contains("MDSupport")) {
					loadExt = false;
				}
			}
			// not Loaded so load
			if (loadExt) {
				log.logDebug("Loading external class files -");
				Class<URLClassLoader> sysClass = URLClassLoader.class;
				Method sysMethod = sysClass.getDeclaredMethod("addURL", new Class[] { URL.class });
				sysMethod.setAccessible(true);
				for (File file : libext_dir.listFiles()) {
					log.logDebug("Loading external class file : " + file.getAbsolutePath());
					sysMethod.invoke(spoonLoader, new Object[] { file.toURI().toURL() });
				}
			} else {
				log.logDebug("MD classes already loaded -");
			}
		} else {
			log.logError("Missing directory cant load externam MD classes: " + libext_dir.getAbsolutePath());
		}
	}

	private void setDataDirDefault() {
		if (Const.isWindows()) {
			File[] roots = File.listRoots();
			dataDir = new File(roots[0], fileSep + "melissaData" + fileSep + "DQT" + fileSep + "data");
		} else {
			dataDir = new File(Const.getDIHomeDirectory() + fileSep + "MDdata");
		}
	}

	public class dataPathDialog extends MDAbstractDialog {
		private MDBusinessCoderHelper helper;

		public dataPathDialog(MDDialogParent dialog, int flags) {
			super(dialog, flags);
		}

		@Override public void ok() {
			MDProps.setProperty(TAG_LOCAL_DATA_PATH, dataDir.toString());
			if (createDataDir(dataDir)) {
				dispose();
			} else {
				MessageDialog.openError(getShell(), getString("DirectoryDialogError.Title"), getString("DirectoryDialogError.Label", dataDir.toString(), dataErrorMsg));
			}
		}

		@Override protected void createContents(Composite parent, Object arg1) {
			helper = new MDBusinessCoderHelper((MDBusinessCoderDialog) dialog);
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
				@Override public void modifyText(ModifyEvent arg0) {
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
				@Override public void widgetSelected(SelectionEvent event) {
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

		@Override protected boolean getData(Object arg0) throws KettleException {
			return false;
		}

		@Override protected String getHelpURLKey() {
			return null;
		}

		@Override protected Class<?> getPackage() {
			return PKG;
		}

		@Override protected String getStringPrefix() {
			return "PluginInStaller";
		}

		@Override protected void init(Object arg0) {
			// nothing to init
		}
	}
}
