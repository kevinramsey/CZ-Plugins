package com.melissadata.kettle.report;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Map;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;

import com.melissadata.kettle.AdvancedConfigurationMeta;
import com.melissadata.kettle.MDCheckMeta;
import org.eclipse.swt.widgets.Display;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.ui.spoon.Spoon;
import org.pentaho.di.ui.spoon.TabMapEntry;
import org.pentaho.di.ui.spoon.TabMapEntry.ObjectType;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.PdfReportUtil;
import org.pentaho.reporting.engine.classic.core.modules.output.table.csv.CSVReportUtil;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlReportUtil;
import org.pentaho.reporting.engine.classic.core.modules.output.table.rtf.RTFReportUtil;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xls.ExcelReportUtil;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.fonts.LibFontBoot;
import org.pentaho.reporting.libraries.resourceloader.LibLoaderBoot;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import com.melissadata.cz.CZUtil;
import com.melissadata.cz.MDProps;

/**
 * Runs the pentaho reports for the various configured reports.
 */
public class ReportRunner {
	private static final String			OUTPUT_REPORT_JOB_SUMMARY_NAME			= "JobSummary.html";
	private static final String			OUTPUT_REPORT_JOB_SUMMARY_MASTER_NAME	= "JobSummary.prpt";
	private static final String			OUTPUT_REPORT_OVERVIEW_NAME				= "overviewmaster.html";
	private static final String			OUTPUT_REPORT_OVERVIEW_MASTER_NAME		= "overviewmaster.prpt";
	private static final String			OUTPUT_REPORT_OVERVIEW_SUFFIX_NAME		= "Overview";
	private static final String			OUTPUT_REPORT_PREFIX_NAME				= "ContactZone";
	private SortedMap<String, File>   templateFiles;
	private SortedMap<String, String> templateDescriptions;
	private MDCheckMeta               meta;
	private BaseStep                  step;
	private TransMeta                 transMeta;

	public ReportRunner(MDCheckMeta meta, BaseStep step) {
		this.meta = meta;
		this.step = step;
		transMeta = step.getTransMeta();
	}

	/**
	 * Pulls the description of the report template from the property file.
	 */
	private String getTemplateDesc(int index) {
		// Load report descriptions the first time
		if (templateDescriptions == null) {
			templateDescriptions = new TreeMap<String, String>();
			Properties props = MDProps.getProperties();
			for (Map.Entry<?, ?> entry : props.entrySet()) {
				String key = (String) entry.getKey();
				String value = (String) entry.getValue();
				// FIXME: Better mechanism for determining defined reports
				if (key.startsWith("ReportDesc"))
					templateDescriptions.put(key, value);
			}
		}
		// Because this map is sorted we can do an indexed lookup on it.
		Object[] keys = templateDescriptions.keySet().toArray();
		String description = templateDescriptions.get(keys[index]);
		return description;
	}

	/**
	 * Pulls the report template from the property file.
	 */
	private File getTemplateFile(int index) {
		// Load report templates the first time
		if (templateFiles == null) {
			templateFiles = new TreeMap<String, File>();
			for (Map.Entry<?, ?> entry : MDProps.getProperties().entrySet()) {
				String key = (String) entry.getKey();
				String value = (String) entry.getValue();
				// FIXME: Different mechanism for finding report templates
				if (key.startsWith("ReportFile")) {
					// Strip off directory information and force to template
					// directory
					File f = new File(value);
					value = f.getName();
					f = new File(getTemplateDirectory(), value);
					templateFiles.put(key, f);
				}
			}
		}
		// Because this map is sorted we can do an indexed lookup on it.
		Object[] keys = templateFiles.keySet().toArray();
		File template = templateFiles.get(keys[index]);
		return template;
	}

	/**
	 * Pops up the url in a new tab in the spoon tabs.
	 */
	private void popupWeb(final String windowName, final String url) {
		if (!MDCheckMeta.isSpoon())
			return;
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				try {
					TabMapEntry tab = Spoon.getInstance().delegates.tabs.findTabMapEntry(windowName, ObjectType.BROWSER);
					if (tab == null) {
						Spoon.getInstance().addSpoonBrowser(windowName, url);
					} else {
						Spoon.getInstance().delegates.tabs.removeTab(tab);
						Spoon.getInstance().addSpoonBrowser(windowName, url);
					}
					TabMapEntry dshTab = Spoon.getInstance().delegates.tabs.findTabMapEntry("Job Summary", ObjectType.BROWSER);
					int idx = Spoon.getInstance().tabfolder.indexOf(dshTab.getTabItem());
					// keep the focus on the graph
					Spoon.getInstance().tabfolder.setSelected(idx);
				} catch (Exception e) {
					meta.logError("Problem displaying report tab", e);
				}
			}
		});
	}


	/**
	 * Generate the reports for an MDCheck step.
	 * 
	 * @throws KettleException
	 */
	public void generateReports() throws KettleException {
		try {
			// Create the work directory
			initWorkDirectory();
			// Get the summary report template and output file
			File templateFile = new File(getTemplateDirectory(), OUTPUT_REPORT_JOB_SUMMARY_MASTER_NAME);
			File outputFile = new File(getWorkDirectory(), OUTPUT_REPORT_JOB_SUMMARY_NAME);
			// Generate the summary report
			processReport(templateFile, outputFile, "HTML");
			// Display the summary report (always?)
			popupWeb("Job Summary", outputFile.toURI().toURL().toString());
			// Run other reports, if configured
			ReportingMeta reportMeta = meta.getData().getReportMeta();
			if (reportMeta.isOptGlobalReports()) {
				// Find the overview report template
				templateFile = new File(getTemplateDirectory(), OUTPUT_REPORT_OVERVIEW_MASTER_NAME);
				// Determine the output file name and type
				String outputType = "HTML";
				if (reportMeta.isOptToFile()) {
					// Determine output file name
					String reportName = reportMeta.getOutputReportName(transMeta);
					String reportPrefix = OUTPUT_REPORT_PREFIX_NAME;
					if (reportName != null && reportName.length() > 0)
						reportPrefix = reportName.replaceAll(" ", "_");
					String reportFilename = reportPrefix + "_" + OUTPUT_REPORT_OVERVIEW_SUFFIX_NAME + "_" + getReportDate() + ".pdf";
					outputFile = new File(reportMeta.getOutputReportDirname(transMeta), reportFilename);
					// Files are always output as PDFs
					outputType = "PDF";
				} else {
					outputFile = new File(getWorkDirectory(), OUTPUT_REPORT_OVERVIEW_NAME);
				}
				// Generate the report
				processReport(templateFile, outputFile, outputType);
				// Display the report if not saved to a file
				if (!reportMeta.isOptToFile())
					popupWeb("Overview", outputFile.toURI().toURL().toString());
				// Run sub-reports, if any
				boolean[] subReports = reportMeta.getOptsSubReports();
				for (int i = 0; i < subReports.length; i++) {
					if (subReports[i]) {
						// Get the sub-report template
						templateFile = getTemplateFile(i);
						// Determine the output file name and type
						if (!reportMeta.isOptToFile()) {
							// If not saving to file then generate a temporary
							// file in the work directory
							String reportFilename = templateFile.getName();
							reportFilename = reportFilename.replace(".prpt", ".html");
							outputFile = new File(getWorkDirectory(), reportFilename);
						} else {
							// If saving to file then determine the name of the
							// file
							String reportName = reportMeta.getOutputReportName(transMeta);
							String reportPrefix = OUTPUT_REPORT_PREFIX_NAME;
							if (reportName != null && reportName.length() > 0)
								reportPrefix = reportName.replaceAll(" ", "_");
							String reportFileName = reportPrefix + "_" + getTemplateDesc(i).replaceAll(" ", "") + "_" + getReportDate() + ".pdf";
							outputFile = new File(reportMeta.getOutputReportDirname(transMeta));
							outputFile = new File(outputFile, reportFileName);
							outputType = "PDF";
						}
						// Generate the report
						processReport(templateFile, outputFile, outputType);
						// Display the report if not saved to a file
						if (!reportMeta.isOptToFile())
							popupWeb(getTemplateDesc(i), outputFile.toURI().toURL().toString());
					}
				}
			}
		} catch (MalformedURLException e) {
			throw new KettleException("Problem generating reports", e);
		} catch (Exception ex) {
			throw new KettleException("Problem generating reports", ex);
		}
	}

	private String getReportDate() {
		String date = "";
		Calendar cal = Calendar.getInstance();
		date = Integer.toString(cal.get(Calendar.MONTH) + 1) + "_" + Integer.toString(cal.get(Calendar.DAY_OF_MONTH)) + "_" + Integer.toString(cal.get(Calendar.YEAR) - 2000) + "_" + Integer.toString(cal.get(Calendar.HOUR_OF_DAY))
				+ getMinuet(cal.get(Calendar.MINUTE));
		return date;
	}

	private String getMinuet(int mins) {
		String minuets = "";
		minuets = mins > 9 ? Integer.toString(mins) : "0" + Integer.toString(mins);
		return minuets;
	}

	/**
	 * Initializes the Pentaho Reporting System
	 * 
	 * @param referenceClass
	 * @param templateFile
	 * @return
	 * @throws KettleException
	 */
	public MasterReport init(Class<?> referenceClass, File templateFile) throws KettleException {
		bootReporting(referenceClass);
		MasterReport masterReport = loadReportTemplate(templateFile);
		return masterReport;
	}

	/**
	 * Initialize the work directory
	 * 
	 * @throws KettleException
	 */
	private void initWorkDirectory() throws KettleException {
		File workDir = getWorkDirectory();
		if (!workDir.exists())
			workDir.mkdirs();
		String[] children = workDir.list();
		if (children != null) {
			for (String filename : children) {
				File delFile = new File(workDir, filename);
				delFile.delete();
			}
		}
	}

	/**
	 * @return The location of the template directory
	 */
	private File getTemplateDirectory() {
		// The template directory is under the application directory
		File template;
		template = new File(new File(Const.getKettleDirectory()), "MD" + Const.FILE_SEPARATOR + "libext" + Const.FILE_SEPARATOR + "reporting");
		if (template.exists()) {
			return template;
		} else {
			template = new File(AdvancedConfigurationMeta.getTemplatePath());
			return template;
		}
	}

	/**
	 * @return The location of the work directory
	 * @throws KettleException
	 */
	private File getWorkDirectory() throws KettleException {
		// The work directory is under the overall CZ work directory
		try {
			return new File(CZUtil.getCZWorkDirectory(), "reports");
		} catch (IOException e) {
			throw new KettleException("Problem getting report work directory", e);
		}
	}

	/**
	 * Boot the Pentaho reporting engine
	 * 
	 * @param referenceClass
	 * @throws KettleException
	 */
	private static void bootReporting(Class<?> referenceClass) throws KettleException {
		if (!ClassicEngineBoot.getInstance().isBootDone()) {
			ObjectUtilities.setClassLoader(referenceClass.getClassLoader());
			ObjectUtilities.setClassLoaderSource(ObjectUtilities.CLASS_CONTEXT);
			LibLoaderBoot.getInstance().start();
			LibFontBoot.getInstance().start();
			ClassicEngineBoot.getInstance().start();
			Exception exception = ClassicEngineBoot.getInstance().getBootFailureReason();
			if (exception != null)
				throw new KettleException("Pentaho Reports Boot Error", exception);
		}
	}

	/**
	 * Load the report template
	 * 
	 * @param templateFile
	 * @throws KettleException
	 */
	private static MasterReport loadReportTemplate(File templateFile) throws KettleException {
		ResourceManager manager = new ResourceManager();
		manager.registerDefaults();
		// FileObject templateObject = KettleVFS.getFileObject(templateFile.getAbsolutePath());
		// File templateObject = KettleVFS.getFileObject(templateFile.getAbsolutePath());
		try {
			URL url = templateFile.toURI().toURL();
			// URL url = new URL(templateObject.getName().getURI());
			Resource resource = manager.createDirectly(url, MasterReport.class);
			MasterReport report = (MasterReport) resource.getResource();
			return report;
		} catch (MalformedURLException e) {
			throw new KettleException("Problem loading the master report", e);
		} catch (ResourceException e) {
			throw new KettleException("Problem loading the master report", e);
		}
	}

	/**
	 * Process a loaded report and saves the output to input specified.
	 * 
	 * @param templateFile
	 * @param outputFile
	 * @param outputType
	 */
	public void processReport(File templateFile, File outputFile, String outputType) throws KettleException {
		try {
			// Initialize the reporting system
			MasterReport masterReport = init(getClass(), templateFile);
			// Create based on report type
			String outputPath = outputFile.getAbsolutePath();
			if (outputType.equals("PDF"))
				PdfReportUtil.createPDF(masterReport, outputFile);
			else if (outputType.equals("HTML"))
				HtmlReportUtil.createDirectoryHTML(masterReport, outputPath);
			else if (outputType.equals("StreamHTML"))
				HtmlReportUtil.createStreamHTML(masterReport, outputPath);
			else if (outputType.equals("CSV"))
				CSVReportUtil.createCSV(masterReport, outputPath);
			else if (outputType.equals("XLS"))
				ExcelReportUtil.createXLS(masterReport, outputPath);
			else if (outputType.equals("RTF"))
				RTFReportUtil.createRTF(masterReport, outputPath);
			// TODO commented out as of Pentaho 6.0
			// Save a reference to the created file
			// FileObject outputObject = KettleVFS.getFileObject(outputPath, step.getTransMeta());
			// File outputObject = new File(outputPath);
// ResultFile resultFile = new ResultFile(ResultFile.FILE_TYPE_GENERAL, KettleVFS.getFileObject(outputPath/*,
// step.getTransMeta()*/), step.getTransMeta().getName(), step.getStepname());
// resultFile.setComment("This file was created with a Pentaho Reporting Output step");
// step.addResultFile(resultFile);
		} catch (ReportProcessingException e) {
			throw new KettleException("Problem generating report", e);
		} catch (IOException e) {
			throw new KettleException("Problem generating report", e);
		}
	}
}
