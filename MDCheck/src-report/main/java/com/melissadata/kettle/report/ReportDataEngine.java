package com.melissadata.kettle.report;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import com.melissadata.kettle.MDCheckMeta;
import com.melissadata.kettle.MDCheckStepData;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.i18n.BaseMessages;

import com.melissadata.cz.CZUtil;
import com.melissadata.kettle.cv.geocode.GeoCoderMeta.AddrKeySource;
/**
 * Handles all the database transactions to be used in the reporting.  
 *
 */
public class ReportDataEngine {
	// for version
	private static Class<?> PKG = MDCheckMeta.class;

	// summary text strings
	private static String SUMMARY_VIEW_ADDRESS = "Address";
	private static String SUMMARY_VIEW_GEOCODER = "Geocoder";
	private static String SUMMARY_VIEW_PHONE = "Phone";
	private static String SUMMARY_VIEW_NAME = "Name";
	private static String SUMMARY_VIEW_EMAIL = "Email";

	private static String COMPONENT_ADDRESS = "Addresses";
	private static String COMPONENT_GEOCODER = "Geocoding";
	private static String COMPONENT_PHONE = "Phone";
	private static String COMPONENT_NAME = "Name Parse";
	private static String COMPONENT_EMAIL = "Email";
	
	// overall text strings
	private static String OVERALL_VIEW_OVERVIEW = "overview";
	private static String OVERALL_VIEW_ADDRESS_DELIVERABLE = "AD";
	private static String OVERALL_VIEW_ADDRESS_CHANGE = "AC";
	private static String OVERALL_VIEW_ADDRESS_ERROR = "AE";
	private static String OVERALL_VIEW_PHONE = "PR";
	private static String OVERALL_VIEW_EMAIL = "ER";
	private static String OVERALL_VIEW_NAME = "NR";

	private static String TYPE_ADDRESS = "Address";
	private static String TYPE_NAME = "Name";
	private static String TYPE_PHONE = "Phone";
	private static String TYPE_EMAIL = "Email";
	private static String TYPE_GEOCODE = "Geocode";
	
	private static String REPORT_ADDRESS = "Address";
	private static String REPORT_ADDRESS_DELIVERABILITY = "Address Deliverability Report";
	private static String REPORT_ADDRESS_CHANGE = "Address Change Report";
	private static String REPORT_ADDRESS_ERROR = "Address Error Report";
	private static String REPORT_PHONE = "Phone";
	private static String REPORT_PHONE_REPORT = "Phone Report";
	private static String REPORT_EMAIL = "Email";
	private static String REPORT_EMAIL_REPORT = "Email Report";
	private static String REPORT_NAME = "Name";
	private static String REPORT_NAME_REPORT = "Name Report";
	private static String REPORT_GEOCODE = "Geocode";
	
	private static String STAT_TOTAL_ADDRESS = "Total verified records";
	private static String STAT_TOTAL_EMAIL = "Total email records";
	private static String STAT_TOTAL_PHONE = "Total phone records";
	private static String STAT_TOTAL_NAME = "Total name records";
	private static String STAT_TOTAL_COMPANY_NAME = "Total company name records";
	private static String STAT_TOTAL_GEO = "Total geocode records";

	// sql connection strings
	private static final String CONTACT_STATS_FILE = "ContactStats.db";
//	private static final String SMART_MOVER_STATS_FILE = "SmartMoverStats.db";
	private static final String JDBC_DRIVER = "org.sqlite.JDBC";
	
	// formatting
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM-dd-yy HH:mm:ss");	
	private static final DecimalFormat LONG_NUMBER_FORMAT = new DecimalFormat("###,###,###");
	private static final DecimalFormat PERCENTAGE_FORMAT = new DecimalFormat("#.#");
	
	private ReportStats numAddrOverview;
	private ReportStats numPhoneOverview;
	private ReportStats numGeoOverview;
	private ReportStats numNameOverview;
	private ReportStats numEmailOverview;

	private ReportStats numAddrFormula;
	private ReportStats numPhoneFormula;
	private ReportStats numGeoFormula;
	private ReportStats numNameFormula;
	private ReportStats numEmailFormula;

	private int numGeoErrors;

	private int numLines; 

	public int numNameBlanks;
	public int numCompanyNameBlanks;
	public int numPhoneErrors;
	public int numEmailBlanks;
	public int numValidDomain;
	
	private int totalAddrErrors;
	private int totalAddrChanges;
	private int totalAddr;
	private int totalPhone;
	private int totalEmail;
	private int totalName;
	private int totalCompanyName;
	private int totalGeo;
	
	private int                 totalLines;
	
	public  long                runTime;
	
	public  String[]            geoOverviewReportCStat;
	public  String[]            nameOverviewFields;
	public  String[]            nameFormulaFields;
	
	public  String[]            emailFormulaFields ;
	public  String[]            emailOverviewReportCStat;

	
	public  String[]            phoneFormulaFields;
	public  String[]            phoneOverviewReportCStat;

	public  String[]            addrValidationReportCStat;
	public  String[]            addrOverviewReportCStat;
	public  String[]            addrChangeReportCStat;

	public  String[]            errorReportCStat;
	
	private MDCheckStepData     data;
	private LogChannelInterface logger;

	public ReportDataEngine(MDCheckMeta meta, LogChannelInterface logger){
		this.data = meta.getData();
		this.logger = logger;
		numGeoErrors = 0;


		numLines= 0; 

		numNameBlanks= 0;
		numPhoneErrors= 0;
		numEmailBlanks= 0;
		numValidDomain= 0;
		
		totalAddrErrors= 0;
		totalAddrChanges= 0;
		totalAddr= 0;
		totalPhone= 0;
		totalEmail= 0;
		totalName= 0;
		totalCompanyName = 0;
		totalGeo= 0;
		
		totalLines= 0;

		runTime = 0;
	}

	/**
	 * @param filename
	 * @return A connection to a database with the given name
	 * @throws SQLException 
	 * @throws IOException 
	 */
	private static Connection getConnection(String filename) throws SQLException, IOException {
		// Make sure the driver is loaded
	    try {
			Class.forName(JDBC_DRIVER);
		} catch (ClassNotFoundException e) {
			// This should not happen
			throw new RuntimeException("Unexpected error while loading JDBC driver", e);
		}
	    // Get a connection to the database
		String url = "jdbc:sqlite:" + new File(CZUtil.getCZWorkDirectory(),filename);
		Connection connection = DriverManager.getConnection(url);
		connection.setAutoCommit(false);
		return connection; 
	}

	/**
	 * Called to commit the changes for a connection
	 * @param connection
	 * @throws SQLException
	 */
	private static void safeCommit(Connection connection) throws SQLException {
		connection.commit();
	}

	/**
	 * Called to rollback the changes on a connection
	 * 
	 * @param connection
	 */
	private static void safeRollback(Connection connection) {
		if (connection != null)
			try {
				connection.rollback();
			} catch (SQLException e) {
				// Rollbacks occur within the context of exception handling, 
				// so we just log this exception and ignore it
				e.printStackTrace(System.err);
			}
	}

	/**
	 * Called to close a connection safely
	 * 
	 * @param connection
	 */
	private static void safeClose(Connection connection) {
		try {
			if (connection != null)
				connection.close();
		} catch (SQLException e) {
			// Close may occur within the context of exception handling, 
			// so we just log this exception and ignore it
			e.printStackTrace(System.err);
		}
	}
	
	/**
	 * Generates the database statistics to be used later on with reporting.
	 * 
	 * @param resultCodes
	 * @throws KettleException
	 */
	public boolean generateStatDatabase(ReportStats resultStats, boolean update) throws KettleException {
		// Construct the overview stats
		setupOverviews(resultStats);
		boolean runCompleted = true;
		
		Connection connection = null;
		try {
			// Get connection to database
			connection = getConnection(CONTACT_STATS_FILE);
			
			// Update/Initialize the overview stats
			if (update)
				updateOverallStats(connection);
			else
				writeOverallStats(connection);
			
			updateSummaryStats(connection);
	
			if (!update) {

				PreparedStatement insertStmnt = connection.prepareStatement("insert into resultcodes values (?,?,?,?,?);");

				String currentDate = DATE_FORMAT.format(new Date());
				
				insertStmnt.setString(1, "datetime");
				insertStmnt.setString(2, "datetime");
				insertStmnt.setString(3, "datetime");
				insertStmnt.setString(4, "datetime");
				insertStmnt.setString(5, currentDate);
				
				insertStmnt.addBatch();
				
				// insert version info
				String version = BaseMessages.getString(PKG, "MDCheckDialog.VersionPrefix");

				Class<?> clazz = this.getClass();
				String className = clazz.getSimpleName() + ".class";
				String classPath = clazz.getResource(className).toString();
//				String buildNum = classPath.substring(classPath.indexOf("Build") + 5, classPath.indexOf(".jar") - 1);
				if (!classPath.startsWith("jar")) {
					version = BaseMessages.getString(PKG, "MDCheckDialog.VersionNotFound");
				} else {
					String manifestPath = classPath.substring(0, classPath.lastIndexOf("!") + 1) + "/META-INF/MANIFEST.MF";
					try {
						Manifest manifest = new Manifest(new URL(manifestPath).openStream());
						String implVersion = manifest.getMainAttributes().getValue(Attributes.Name.IMPLEMENTATION_VERSION);
						if (implVersion == null)
							version = BaseMessages.getString(PKG, "MDCheckDialog.VersionNotFound");
						else
							version = BaseMessages.getString(PKG, "MDCheckDialog.VersionPrefix") + " " + implVersion;
						
					} catch (IOException e) {
						version = BaseMessages.getString(PKG, "MDCheckDialog.VersionNotFound");
					}
				}
				
				insertStmnt.setString(1, "version");
				insertStmnt.setString(2, "version");
				insertStmnt.setString(3, "version");
				insertStmnt.setString(4, "version");
				insertStmnt.setString(5, version);
				
				insertStmnt.addBatch();

				insertStmnt.executeBatch();

				insertStmnt.close();
			}
			
			if (data.getAddressVerify() != null && data.getAddressVerify().isEnabled()){						
				updateResultStats(connection, "AS", "Addr", resultStats);
				updateResultStats(connection, "AC", "Addr", resultStats);
				updateResultStats(connection, "AE", "Addr", resultStats);
			}
			if (data.getPhoneVerify() != null && data.getPhoneVerify().isEnabled()){
				
				updateResultStats(connection, "PS", "Phone",resultStats);
				updateResultStats(connection, "PE", "Phone",resultStats);
			}
			if (data.getEmailVerify() != null && data.getEmailVerify().isEnabled()){
				
				updateResultStats(connection, "ES", "Email", resultStats);
				updateResultStats(connection, "EE", "Email", resultStats);
			}
			if (data.getNameParse() != null && (data.getNameParse().isEnabled() || data.getNameParse().isCompanyEnabled())){
				
				updateResultStats(connection, "NS", "Name", resultStats);
				updateResultStats(connection, "NE", "Name", resultStats);
			}
			if (data.getGeoCoder() != null && data.getGeoCoder().getAddrKeySource() != AddrKeySource.None && data.getGeoCoder().isEnabled()){
	
				updateResultStats(connection, "GS", "Geo", resultStats);
				updateResultStats(connection, "GE", "Geo", resultStats);
			}

			safeCommit(connection);

		} catch (SQLException e) {
			runCompleted = false;
			safeRollback(connection);
			logger.logBasic("Error with inserting the time into the result code table" + e.toString());
			
		} catch (IOException e) {
			runCompleted = false;
			safeRollback(connection);
			logger.logBasic("Error with inserting the time into the result code table" + e.toString());
			
		} catch (Exception e) {
			runCompleted = false;
			safeRollback(connection);
			logger.logBasic("Unkown Exception " + e.toString());
			
		} finally {
			safeClose(connection);
		}
		return runCompleted;
	}
	
	/**
	 * Construct the overview objects to be used later. 
	 * 
	 * @param resultStats
	 */
	private void setupOverviews(ReportStats resultStats) {
		numNameOverview = new ReportStats();
		numNameFormula = new ReportStats();		
		numPhoneFormula = new ReportStats();
		numGeoFormula = new ReportStats();
		numEmailFormula = new ReportStats();
		numGeoErrors = 0;
		numAddrFormula.set(addrValidationReportCStat[3], 0);
		numAddrFormula.set(addrValidationReportCStat[4], 0);
		numAddrFormula.set(addrValidationReportCStat[5], 0);
		numAddrFormula.set(addrValidationReportCStat[6], 0);
		
		if (resultStats.containsKey("GE01"))
			numGeoErrors = resultStats.get("GE01");

		if (resultStats.containsKey("GE02"))
			numGeoErrors = resultStats.get("GE02");

		if (resultStats.containsKey("GE05"))
			numGeoErrors = resultStats.get("GE05");

		if (resultStats.containsKey("AS03"))
			updateStat(numAddrFormula, addrValidationReportCStat[3], resultStats.get("AS03"));
		
		if (resultStats.containsKey("AS09"))
			updateStat(numAddrFormula, addrValidationReportCStat[4], resultStats.get("AS09"));

		if (resultStats.containsKey("AE06"))
			updateStat(numAddrFormula, addrValidationReportCStat[5], resultStats.get("AE06"));
		
		if (resultStats.containsKey("AS16"))
			updateStat(numAddrFormula, addrValidationReportCStat[6], resultStats.get("AS16"));
		
		if (resultStats.containsKey("AS17"))
			updateStat(numAddrFormula, addrValidationReportCStat[7], resultStats.get("AS17"));

		if (resultStats.containsKey("NS01")) {
			updateStat(numNameOverview, nameOverviewFields[0], resultStats.get("NS01"));
			updateStat(numNameFormula, nameFormulaFields[0], resultStats.get("NS01"));
		}
		
		if (resultStats.containsKey("NS02")) {
			updateStat(numNameOverview, nameOverviewFields[1], resultStats.get("NS02"));
			updateStat(numNameFormula, nameFormulaFields[1], resultStats.get("NS02"));
		}
		
		if (resultStats.containsKey("NE99")) {
			updateStat(numNameOverview, nameOverviewFields[3], resultStats.get("NE99"));
			updateStat(numNameFormula, nameFormulaFields[3], resultStats.get("NE99"));
		}
		
		if (resultStats.containsKey("NS99")) {
			updateStat(numNameOverview, nameOverviewFields[4], resultStats.get("NS99"));
			updateStat(numNameFormula, nameFormulaFields[4], resultStats.get("NS99"));
		}
		
		if (resultStats.containsKey("PE02"))
			updateStat(numPhoneFormula, phoneFormulaFields[2], resultStats.get("PE02"));
		
		if (resultStats.containsKey("PS01"))
			updateStat(numPhoneFormula, phoneFormulaFields[0], resultStats.get("PS01"));
		
		if (resultStats.containsKey("PS02"))
			updateStat(numPhoneFormula, phoneFormulaFields[1], resultStats.get("PS02"));
		
		if (resultStats.containsKey("ES04"))
			updateStat(numEmailFormula, emailFormulaFields[1], resultStats.get("ES04"));
		
		if (resultStats.containsKey("ES02"))
			updateStat(numEmailFormula, emailFormulaFields[2], resultStats.get("ES02"));
		
		if (resultStats.containsKey("ES03"))
			updateStat(numEmailFormula, emailFormulaFields[3], resultStats.get("ES03"));
		
		numNameOverview.set(nameOverviewFields[5], numCompanyNameBlanks);
		numNameFormula.set(nameFormulaFields[5], numCompanyNameBlanks);
		
		numNameOverview.set(nameOverviewFields[2], numNameBlanks);
		numNameFormula.set(nameFormulaFields[2], numNameBlanks);
		
		numPhoneFormula.set(phoneFormulaFields[3], numPhoneErrors);

		numEmailFormula.set(emailFormulaFields[4], numEmailBlanks);
		
		numEmailFormula.set(emailFormulaFields[0], numValidDomain);
		
		int errorTotal = numAddrFormula.get(data.getMeta().errorReportCStat[0])
						- numAddrFormula.get(data.getMeta().errorReportCStat[1])
						- numAddrFormula.get(data.getMeta().errorReportCStat[3]);
		numAddrOverview.set(data.getMeta().addrOverviewReportCStat[6], errorTotal);
	}
	
	/**
	 * helper to increment a map variable by one
	 * 
	 * @param stats
	 * @param key
	 * @param add
	 */
	private static void updateStat(ReportStats stats, String key, int add) {
		int newStat = stats.get(key) + add;
		stats.set(key, newStat);
	}
	
	/**
	 * Clears and creates the database in sqlite that will be used to hold our reporting data. 
	 */
	public boolean clearPreviousDatabases() throws KettleException {
		// access database
        Connection connection = null;
        boolean clearCompleted = true;
		try {

    	    // Clear contact stats db
		    File delFile = new File(CZUtil.getCZWorkDirectory(),/* "reports" + Const.FILE_SEPARATOR +*/ CONTACT_STATS_FILE);
		    delFile.delete();

			connection = getConnection(CONTACT_STATS_FILE);
	        Statement stmnt = connection.createStatement();
	        
	        stmnt.executeUpdate("drop table if exists resultcodes;");
	        stmnt = connection.createStatement();
	        stmnt.executeUpdate("create table resultcodes (component,code,description,numreturn, percentage);");

	        stmnt = connection.createStatement();
	        stmnt.executeUpdate("drop table if exists overall;");
	        stmnt = connection.createStatement();
	        stmnt.executeUpdate("create table overall (view,type,report, stat, amount, percentage);");

	        stmnt = connection.createStatement();
	        stmnt.executeUpdate("drop table if exists summary;");
	        stmnt = connection.createStatement();
	        stmnt.executeUpdate("create table summary (view,component,ok,okpercent,errors,errorpercent,empty,emptypercent);");

	        safeCommit(connection);

//	        // Clear smart mover stats db (TODO: not fully implemented)
//	        connection = getConnection(SMART_MOVER_STATS_FILE);
//	        stat = connection.createStatement();
//	        stat.executeUpdate("drop table if exists stats;");
//	        stat.executeUpdate("create table stats (desc,value);");
//	        connection.close(); 
//	        
//	        connection.commit();
	        
		} catch (SQLException e) {
			logger.logBasic("There was a SQL Exception clearing previous stats database" + e.toString());
			clearCompleted = false;
			safeRollback(connection);
			//throw new KettleException("Could not open database for stats");
        	
        } catch (IOException e) {
			logger.logBasic("There was a file IO issue clearing previous stats database" + e.toString());
			clearCompleted = false;
			
			safeRollback(connection);
			//throw new KettleException("Could not open database for stats");
		}
        finally {
        	safeClose(connection);
        }
		return clearCompleted;
	}
	
	/**
	 * add a batch for the summary table
	 * 
	 * @param insertStmnt
	 * @param view
	 * @param component
	 * @param ok
	 * @param errors
	 * @param empty
	 * @param divisor
	 * @throws KettleException 
	 */
	private static void addSqlBatchSummary(PreparedStatement insertStmnt, String view, String component, int ok,
			int errors, int empty, int divisor) throws KettleException {

		try {
			insertStmnt.setString(1, view);
			insertStmnt.setString(2, component);
			insertStmnt.setString(3, safeFormat(ok));
			insertStmnt.setString(4, safeFormatPercentage(ok, divisor));
			insertStmnt.setString(5, safeFormat(errors));
			insertStmnt.setString(6, safeFormatPercentage(errors, divisor));
			insertStmnt.setString(7, safeFormat(empty));
			insertStmnt.setString(8, safeFormatPercentage(empty, divisor));

			insertStmnt.addBatch();
			
		} catch (SQLException e) {
			throw new KettleException("Problem adding to the summary table", e);
		}

	}

	/**
	 * Return a formatted number in string format if it's not empty, if it is then return 0.  
	 * 
	 * @param inputNumber
	 * @return
	 */
	private static String safeFormat(int inputNumber) {
		if (inputNumber > 0)
			return LONG_NUMBER_FORMAT.format(inputNumber);
		return "0";
	}

	/**
	 * Return a formatted number in string format if it's not empty, if it is then return 0.  
	 * 
	 * @param numerator
	 * @param divisor
	 * @return
	 */
	private static String safeFormatPercentage(int numerator, int divisor) {
		if (numerator <= 0)
			return "0%";
		if (numerator > divisor)
			return "100%";
		return Double.valueOf(PERCENTAGE_FORMAT.format(((double) (numerator) / divisor) * 100)).toString() + "%";
	}

	/**
	 * Called to get summary stats 
	 * 
	 * @param view
	 * @param component
	 * @param connection
	 * @return
	 * @throws KettleException 
	 */
	private static ReportStats getSummaryStats(String view, String component, Connection connection) throws KettleException {
		
		try {
			PreparedStatement selectStmnt = connection.prepareStatement("select ok,errors,empty from summary where view = ? and component = ?;");

			// update address stats
			selectStmnt.setString(1, view);
			selectStmnt.setString(2, component);

			ResultSet rs = selectStmnt.executeQuery();

			String okNum = "0";
			String errorsNum = "0";
			String emptyNum = "0";

			if (rs.next()) {
				okNum = rs.getString("ok");
				errorsNum = rs.getString("errors");
				emptyNum = rs.getString("empty");
			}

			okNum = okNum.replaceAll(",", "");
			errorsNum = errorsNum.replaceAll(",", "");
			emptyNum = emptyNum.replaceAll(",", "");

			ReportStats summaryStats = new ReportStats();
			summaryStats.set("ok", new Integer(okNum));
			summaryStats.set("errors", new Integer(errorsNum));
			summaryStats.set("empty", new Integer(emptyNum));

			selectStmnt.close();

			return summaryStats;
			
		} catch (SQLException e) {
			throw new KettleException("Problem retrieving summary stats", e);
		}
	}

	/**
	 * updates the summary stats table with this threads stats
	 * 
	 * @param connection
	 * @throws KettleException 
	 */
	private void updateSummaryStats(Connection connection) throws KettleException {
		
		try {
			PreparedStatement insertStmnt = connection.prepareStatement("insert into summary values (?,?,?,?,?,?,?,?);");
			PreparedStatement deleteStmnt = connection.prepareStatement("delete from summary where view = ?;");

			int okNumInt = 0;
			int errorsNumInt = 0;
			int emptyNumInt = 0;

			// update the address summary stat
			
			ReportStats summaryStats = getSummaryStats(SUMMARY_VIEW_ADDRESS, COMPONENT_ADDRESS, connection);
			okNumInt = summaryStats.get("ok");
			errorsNumInt = summaryStats.get("errors");
			emptyNumInt = summaryStats.get("empty");

			okNumInt += numAddrOverview.get(addrOverviewReportCStat[0]);
			okNumInt += numAddrOverview.get(addrOverviewReportCStat[1]);

			errorsNumInt += numAddrOverview.get(addrOverviewReportCStat[2]);
			errorsNumInt += numAddrOverview.get(addrOverviewReportCStat[3]);
			errorsNumInt += numAddrOverview.get(addrOverviewReportCStat[4]);
			errorsNumInt += numAddrOverview.get(addrOverviewReportCStat[6]);

			emptyNumInt += numAddrOverview.get(addrOverviewReportCStat[5]);

			deleteStmnt.setString(1, SUMMARY_VIEW_ADDRESS);
			deleteStmnt.executeUpdate();
   
			addSqlBatchSummary(insertStmnt, SUMMARY_VIEW_ADDRESS, COMPONENT_ADDRESS, okNumInt, errorsNumInt, emptyNumInt, totalLines);

			// update geocoder stats

			summaryStats = getSummaryStats(SUMMARY_VIEW_GEOCODER, COMPONENT_GEOCODER, connection);
			okNumInt = summaryStats.get("ok");
			errorsNumInt = summaryStats.get("errors");
			emptyNumInt = summaryStats.get("empty");

			okNumInt += numGeoOverview.get(geoOverviewReportCStat[0]);
			okNumInt += numGeoOverview.get(geoOverviewReportCStat[1]);
			okNumInt += numGeoOverview.get(geoOverviewReportCStat[2]);

			emptyNumInt += numGeoErrors;

			deleteStmnt.setString(1, SUMMARY_VIEW_GEOCODER);
			deleteStmnt.executeUpdate();

			addSqlBatchSummary(insertStmnt, SUMMARY_VIEW_GEOCODER, COMPONENT_GEOCODER, okNumInt, 0, emptyNumInt, totalLines);

			// update Phone stats

			summaryStats = getSummaryStats(SUMMARY_VIEW_PHONE, COMPONENT_PHONE, connection);
			okNumInt = summaryStats.get("ok");
			errorsNumInt = summaryStats.get("errors");
			emptyNumInt = summaryStats.get("empty");

			okNumInt += numPhoneOverview.get(phoneOverviewReportCStat[0]);
			emptyNumInt += numPhoneOverview.get(phoneOverviewReportCStat[2]);
			errorsNumInt += numPhoneOverview.get(phoneOverviewReportCStat[3]);

			deleteStmnt.setString(1, SUMMARY_VIEW_PHONE);
			deleteStmnt.executeUpdate();

			addSqlBatchSummary(insertStmnt, SUMMARY_VIEW_PHONE, COMPONENT_PHONE, okNumInt, errorsNumInt, emptyNumInt, totalLines);

			// update email stats
			summaryStats = getSummaryStats(SUMMARY_VIEW_EMAIL, COMPONENT_EMAIL, connection);
			okNumInt = summaryStats.get("ok");
			errorsNumInt = summaryStats.get("errors");
			emptyNumInt = summaryStats.get("empty");

			okNumInt += numEmailOverview.get(emailOverviewReportCStat[0]);
			emptyNumInt += numEmailOverview.get(emailOverviewReportCStat[3]);
			errorsNumInt += numEmailOverview.get(emailOverviewReportCStat[4]);

			deleteStmnt.setString(1, SUMMARY_VIEW_EMAIL);
			deleteStmnt.executeUpdate();

			addSqlBatchSummary(insertStmnt, SUMMARY_VIEW_EMAIL, COMPONENT_EMAIL, okNumInt, errorsNumInt, emptyNumInt, totalLines);

			// update name object stats
			summaryStats = getSummaryStats(SUMMARY_VIEW_NAME, COMPONENT_NAME, connection);
			okNumInt = summaryStats.get("ok");
			errorsNumInt = summaryStats.get("errors");
			emptyNumInt = summaryStats.get("empty");

			if (data.getNameParse().isEnabled()) {
				okNumInt += numNameOverview.get(nameOverviewFields[0]);
				emptyNumInt += numNameOverview.get(nameOverviewFields[2]);
				errorsNumInt += numNameOverview.get(nameOverviewFields[1]);
			} else {
				okNumInt += numNameOverview.get(nameOverviewFields[4]);
				okNumInt += numNameOverview.get(nameOverviewFields[3]);
				emptyNumInt += numNameOverview.get(nameOverviewFields[5]);
				errorsNumInt = 0;
			}
			deleteStmnt.setString(1, SUMMARY_VIEW_NAME);
			deleteStmnt.executeUpdate();

			addSqlBatchSummary(insertStmnt, SUMMARY_VIEW_NAME, COMPONENT_NAME, okNumInt, errorsNumInt, emptyNumInt, totalLines);

			// close delete stmt out
			deleteStmnt.close();

			// commit the batch adds
			insertStmnt.executeBatch();
			
		} catch (SQLException e) {
			throw new KettleException("Problem updating summary stats", e);
		}
	}
	
	/**
	 * creates a batch for the overview table with the incoming fields
	 * 
	 * @param insertStmnt
	 * @param view
	 * @param type
	 * @param report
	 * @param stat
	 * @param amount
	 * @param divisor
	 * @throws KettleException 
	 */
	private static void addSqlBatchOverall(PreparedStatement insertStmnt, String view, String type, String report,
			String stat, int amount, int divisor) throws KettleException {

		try {
			insertStmnt.setString(1, view);
			insertStmnt.setString(2, type);
			insertStmnt.setString(3, report);
			insertStmnt.setString(4, stat);
			insertStmnt.setString(5, safeFormat(amount));
			insertStmnt.setString(6, safeFormatPercentage(amount, divisor));
			
			insertStmnt.addBatch();
			
		} catch (SQLException e) {
			throw new KettleException("Problem adding to overall stats", e);
		}
	}
	
	/**
	 * 
	 * @param insertStmnt
	 * @param view
	 * @param type
	 * @param report
	 * @param stat
	 * @param amount
	 * @throws KettleException 
	 */
	private static void addSqlBatchOverallTotal(PreparedStatement insertStmnt, String view, String type, String report,
			String stat, int amount) throws KettleException {

		try {
			insertStmnt.setString(1, view);
			insertStmnt.setString(2, type);
			insertStmnt.setString(3, report);
			insertStmnt.setString(4, stat);
			insertStmnt.setString(5, safeFormat(amount));
			insertStmnt.setString(6, "");

			insertStmnt.addBatch();
			
		} catch (SQLException e) {
			throw new KettleException("Problem adding to total overall stats", e);
		}
	}
	
	//TODO: combine this with update and simplify
	/**
	 * write out the stats needed in the overall stats table
	 * 
	 * @param Connection
	 * @throws KettleException 
	 */
	private void writeOverallStats(Connection connection) throws KettleException {
		try {
			PreparedStatement insertStmnt = connection.prepareStatement("insert into overall values (?,?,?,?,?,?);");

			Date now = new Date();
			String currentDate = DATE_FORMAT.format(now);

			insertStmnt.setString(1, "datetime");
			insertStmnt.setString(2, "datetime");
			insertStmnt.setString(3, "datetime");
			insertStmnt.setString(4, "datetime");
			insertStmnt.setString(5, currentDate);
			insertStmnt.setString(6, "");
			insertStmnt.addBatch();

			long time = runTime / 1000;
			String seconds = Integer.toString((int) (time % 60));
			String minutes = Integer.toString((int) ((time % 3600) / 60));
			String hours = Integer.toString((int) (time / 3600));
			if (seconds.length() < 2)
				seconds = "0" + seconds;
			if (minutes.length() < 2)
				minutes = "0" + minutes;
			if (hours.length() < 2)
				hours = "0" + hours;
			insertStmnt.setString(1, "elaptime");
			insertStmnt.setString(2, "elaptime");
			insertStmnt.setString(3, "elaptime");
			insertStmnt.setString(4, "elaptime");
			insertStmnt.setString(5, hours + ":" + minutes + ":" + seconds);
			insertStmnt.setString(6, "");
			insertStmnt.addBatch();

			insertStmnt.setString(1, "recCount");
			insertStmnt.setString(2, "recCount");
			insertStmnt.setString(3, "recCount");
			insertStmnt.setString(4, "recCount");
			insertStmnt.setString(5, safeFormat(numLines));
			insertStmnt.setString(6, "");
			insertStmnt.addBatch();

			totalLines = numLines;

			insertStmnt.setString(1, "JobName");
			insertStmnt.setString(2, "JobName");
			insertStmnt.setString(3, "JobName");
			insertStmnt.setString(4, "JobName");
			insertStmnt.setString(5, data.getReportMeta().getOutputReportName(data.getMeta().getParentStepMeta().getParentTransMeta()));
			insertStmnt.setString(6, "");
			insertStmnt.addBatch();

			// create the address overall stats

			totalAddr = numLines - numAddrOverview.get(addrOverviewReportCStat[5]);
			addSqlBatchOverallTotal(insertStmnt, OVERALL_VIEW_OVERVIEW, TYPE_ADDRESS, REPORT_ADDRESS, STAT_TOTAL_ADDRESS, 
									data.getAddressVerify().isEnabled() ? totalAddr : 0);

			for (int i = 0; i < addrOverviewReportCStat.length; i++)
				addSqlBatchOverall(insertStmnt, OVERALL_VIEW_OVERVIEW, TYPE_ADDRESS, REPORT_ADDRESS, addrOverviewReportCStat[i], 
									numAddrOverview.get(addrOverviewReportCStat[i]),
									(i != 5) ? totalAddr : numLines);

			for (int i = 0; i < addrValidationReportCStat.length; i++)
				addSqlBatchOverall(insertStmnt, OVERALL_VIEW_ADDRESS_DELIVERABLE, TYPE_ADDRESS, REPORT_ADDRESS_DELIVERABILITY, addrValidationReportCStat[i], 
									numAddrFormula.get(addrValidationReportCStat[i]), totalAddr);

			totalAddrChanges = numAddrFormula.get(addrChangeReportCStat[1]);

			addSqlBatchOverallTotal(insertStmnt, OVERALL_VIEW_ADDRESS_CHANGE, TYPE_ADDRESS, REPORT_ADDRESS_CHANGE, addrChangeReportCStat[0], 
									numAddrFormula.get(addrChangeReportCStat[0]));
			addSqlBatchOverallTotal(insertStmnt, OVERALL_VIEW_ADDRESS_CHANGE, TYPE_ADDRESS, REPORT_ADDRESS_CHANGE, addrChangeReportCStat[1], 
									totalAddrChanges);

			for (int i = 2; i < addrChangeReportCStat.length; i++)
				addSqlBatchOverall(insertStmnt, OVERALL_VIEW_ADDRESS_CHANGE, TYPE_ADDRESS, REPORT_ADDRESS_CHANGE, addrChangeReportCStat[i], 
									numAddrFormula.get(addrChangeReportCStat[i]), totalAddrChanges);

			totalAddrErrors = numAddrFormula.get(data.getMeta().errorReportCStat[0]);

			addSqlBatchOverallTotal(insertStmnt, OVERALL_VIEW_ADDRESS_ERROR, TYPE_ADDRESS, REPORT_ADDRESS_ERROR, errorReportCStat[0], 
									totalAddrErrors);

			for (int i = 1; i < errorReportCStat.length; i++)
				addSqlBatchOverall(insertStmnt, OVERALL_VIEW_ADDRESS_ERROR, TYPE_ADDRESS, REPORT_ADDRESS_ERROR, errorReportCStat[i], 
									numAddrFormula.get(errorReportCStat[i]), totalAddrErrors);

			// create the phone overview stats

			totalPhone = numLines - numPhoneOverview.get(phoneOverviewReportCStat[2]);
			int amount = data.getPhoneVerify().isEnabled() ? totalPhone : 0;
			addSqlBatchOverallTotal(insertStmnt, OVERALL_VIEW_PHONE, TYPE_PHONE, REPORT_PHONE_REPORT, STAT_TOTAL_PHONE, amount);
			addSqlBatchOverallTotal(insertStmnt, OVERALL_VIEW_OVERVIEW, TYPE_PHONE, REPORT_PHONE, STAT_TOTAL_PHONE, amount);

			for (int i = 0; i < phoneOverviewReportCStat.length; i++)
				addSqlBatchOverall(insertStmnt, OVERALL_VIEW_OVERVIEW, TYPE_PHONE, REPORT_PHONE, phoneOverviewReportCStat[i],
									numPhoneOverview.get(phoneOverviewReportCStat[i]), (i != 2) ? totalPhone : numLines);

			for (int i = 0; i < phoneFormulaFields.length; i++)
				addSqlBatchOverall(insertStmnt, OVERALL_VIEW_PHONE, TYPE_PHONE, REPORT_PHONE_REPORT, phoneFormulaFields[i],
									numPhoneFormula.get(phoneFormulaFields[i]), (i != 2) ? totalPhone : numLines);

			// create the email overall stats

			totalEmail = numLines - numEmailOverview.get(emailOverviewReportCStat[3]);
			amount = data.getEmailVerify().isEnabled() ? totalEmail : 0;
			addSqlBatchOverallTotal(insertStmnt, OVERALL_VIEW_OVERVIEW, TYPE_EMAIL, REPORT_EMAIL, STAT_TOTAL_EMAIL, amount);
			addSqlBatchOverallTotal(insertStmnt, OVERALL_VIEW_EMAIL, TYPE_EMAIL, REPORT_EMAIL_REPORT, STAT_TOTAL_EMAIL, amount);

			for (int i = 0; i < emailOverviewReportCStat.length; i++)
				addSqlBatchOverall(insertStmnt, OVERALL_VIEW_OVERVIEW, TYPE_EMAIL, REPORT_EMAIL, emailOverviewReportCStat[i],
									numEmailOverview.get(emailOverviewReportCStat[i]), (i != 3) ? totalEmail : numLines);

			for (int i = 0; i < emailFormulaFields.length; i++)
				addSqlBatchOverall(insertStmnt, OVERALL_VIEW_EMAIL, TYPE_EMAIL, REPORT_EMAIL_REPORT, emailFormulaFields[i],
									numEmailFormula.get(emailFormulaFields[i]), (i != 4) ? totalEmail : numLines);

			// create the name overview stats

			totalName = numLines - numNameOverview.get(nameOverviewFields[2]);
			totalCompanyName = numLines - numNameOverview.get(nameOverviewFields[5]);

			amount = data.getNameParse().isEnabled() ? totalName : 0;
			addSqlBatchOverallTotal(insertStmnt, OVERALL_VIEW_OVERVIEW, TYPE_NAME, REPORT_NAME, STAT_TOTAL_NAME, amount);
			addSqlBatchOverallTotal(insertStmnt, OVERALL_VIEW_NAME, TYPE_NAME, REPORT_NAME_REPORT, STAT_TOTAL_NAME, amount);

			amount = data.getNameParse().isCompanyEnabled() ? totalCompanyName : 0;
			addSqlBatchOverallTotal(insertStmnt, OVERALL_VIEW_OVERVIEW, TYPE_NAME, REPORT_NAME, STAT_TOTAL_COMPANY_NAME, amount);
			addSqlBatchOverallTotal(insertStmnt, OVERALL_VIEW_NAME, TYPE_NAME, REPORT_NAME_REPORT, STAT_TOTAL_COMPANY_NAME, amount);

			for (int i = 0; i < nameOverviewFields.length; i++) {
				if (i < 2)
					amount = totalName;
				else if (i == 2 || i == 5)
					amount = numLines;
				else
					amount = totalCompanyName;
				addSqlBatchOverall(insertStmnt, OVERALL_VIEW_OVERVIEW, TYPE_NAME, REPORT_NAME, nameOverviewFields[i],
									numNameOverview.get(nameOverviewFields[i]), amount);
			}

			for (int i = 0; i < nameFormulaFields.length; i++) {
				if (i < 2)
					amount = totalName;
				else if (i == 2 || i == 5)
					amount = numLines;
				else
					amount = totalCompanyName;
				addSqlBatchOverall(insertStmnt, OVERALL_VIEW_NAME, TYPE_NAME, REPORT_NAME_REPORT, nameFormulaFields[i],
									numNameFormula.get(nameFormulaFields[i]), amount);
			}

			// create the geo overview stats

			totalGeo = numLines - numGeoOverview.get(geoOverviewReportCStat[3]);

			addSqlBatchOverallTotal(insertStmnt, OVERALL_VIEW_OVERVIEW, TYPE_GEOCODE, REPORT_GEOCODE, STAT_TOTAL_GEO, 
									data.getGeoCoder().isEnabled() ? totalGeo : 0);

			for (int i = 0; i < geoOverviewReportCStat.length; i++)
				addSqlBatchOverall(insertStmnt, OVERALL_VIEW_OVERVIEW, TYPE_GEOCODE, REPORT_GEOCODE, geoOverviewReportCStat[i], 
									numGeoOverview.get(geoOverviewReportCStat[i]), (i != 3) ? totalGeo : numLines);

			insertStmnt.executeBatch();
			
		} catch (SQLException e) {
			throw new KettleException("Problem writing out the overall stats", e);
		}
	}

	/**
	 * update a stat in the overall table that needs a percentage and is not a total
	 * 
	 * @param selectStmnt
	 * @param updateStmnt
	 * @param updatePercentStmnt
	 * @param stat
	 * @param report
	 * @param addAmount
	 * @param divisor
	 * @throws KettleException 
	 */
	private static void updateSqlOverallStat(PreparedStatement selectStmnt, PreparedStatement updateStmnt,
			PreparedStatement updatePercentStmnt, String stat, String report, int addAmount, int divisor) throws KettleException {
		
		try {
			selectStmnt.setString(1, stat);
			selectStmnt.setString(2, report);
			ResultSet rs = selectStmnt.executeQuery();

			String amountStr = "0";
			if (rs.next())
				amountStr = rs.getString("amount");
			amountStr = amountStr.replaceAll(",", "");
			int amount = new Integer(amountStr) + addAmount;

			updateStmnt.setString(1, safeFormat(amount));
			updateStmnt.setString(2, stat);
			updateStmnt.setString(3, report);
			
			updateStmnt.executeUpdate();

			updatePercentStmnt.setString(1, safeFormatPercentage(amount, divisor));
			updatePercentStmnt.setString(2, stat);
			updatePercentStmnt.setString(3, report);
			
			updatePercentStmnt.executeUpdate();

		} catch (SQLException e) {
			throw new KettleException("Problem updating the overall stats", e);
		}
	}

	/**
	 * helper to handle updating a total in the overall table
	 * 
	 * @param selectStmnt
	 * @param updateStmnt
	 * @param stat
	 * @param report
	 * @param numBlanks
	 * @param addAmount
	 * @return
	 * @throws KettleException 
	 */
	private static int addSqlTotalOverall(PreparedStatement selectStmnt, PreparedStatement updateStmnt, String stat,
			String report, int numBlanks, int addAmount) throws KettleException {

		try {
			selectStmnt.setString(1, stat);
			selectStmnt.setString(2, report);
			ResultSet rs = selectStmnt.executeQuery();
			
			String amountStr = "0";
			if (rs.next())
				amountStr = rs.getString("amount");
			amountStr = amountStr.replaceAll(",", "");
			int amount = new Integer(amountStr) + addAmount- numBlanks;

			updateStmnt.setString(1, safeFormat(amount));
			updateStmnt.setString(2, stat);
			updateStmnt.setString(3, report);
			
			updateStmnt.executeUpdate();
			
			return amount;

		} catch (SQLException e) {
			throw new KettleException("Problem adding to the overall total stats", e);
		}
	}

	/**
	 * updates all the columns in the overall table with new data
	 * 
	 * @param connection
	 * @throws KettleException 
	 */
	private void updateOverallStats(Connection connection) throws KettleException {
		
		try {

			PreparedStatement selectStmnt = connection.prepareStatement("select amount from overall where stat = ? and report = ?;");
			PreparedStatement updateStmnt = connection.prepareStatement("update overall set amount = ? where stat = ? and report = ?;");
			PreparedStatement updatePercentStmnt = connection.prepareStatement("update overall set percentage = ? where stat = ? and report = ?;");

			// update the total record count
			selectStmnt.setString(1, "recCount");
			selectStmnt.setString(2, "recCount");
			ResultSet rs = selectStmnt.executeQuery();

			String amountStr = "0";
			if (rs.next())
				amountStr = rs.getString("amount");
			amountStr = amountStr.replaceAll(",", "");
			int amount = new Integer(amountStr) + numLines;
			totalLines = amount;

			updateStmnt.setString(1, safeFormat(amount));
			updateStmnt.setString(2, "recCount");
			updateStmnt.setString(3, "recCount");
			updateStmnt.executeUpdate();

			// handle address total
			totalAddr = addSqlTotalOverall(selectStmnt, updateStmnt, STAT_TOTAL_ADDRESS, REPORT_ADDRESS,
											numAddrOverview.get(addrOverviewReportCStat[5]), 
											data.getAddressVerify().isEnabled() ? numLines : 0);

			// handle address overview stats			
			for (int i = 0; i < addrOverviewReportCStat.length; i++)
				updateSqlOverallStat(selectStmnt, updateStmnt, updatePercentStmnt, addrOverviewReportCStat[i], REPORT_ADDRESS, 
										numAddrOverview.get(addrOverviewReportCStat[i]), (i != 5) ? totalAddr : totalLines);
			
			// handle validation segment stats
			for (int i = 0; i < addrValidationReportCStat.length; i++)
				updateSqlOverallStat(selectStmnt, updateStmnt, updatePercentStmnt, addrValidationReportCStat[i], REPORT_ADDRESS_DELIVERABILITY, 
										numAddrFormula.get(addrValidationReportCStat[i]), totalAddr);

			// handle changes
			totalAddrChanges = addSqlTotalOverall(selectStmnt, updateStmnt, addrChangeReportCStat[0], REPORT_ADDRESS_CHANGE, 
													0, numAddrFormula.get(addrChangeReportCStat[0]));

			for (int i = 2; i < addrChangeReportCStat.length; i++)
				updateSqlOverallStat(selectStmnt, updateStmnt, updatePercentStmnt, addrChangeReportCStat[i], REPORT_ADDRESS_CHANGE, 
										numAddrFormula.get(addrChangeReportCStat[i]), totalAddrChanges);

			// handle errors
			totalAddrErrors = addSqlTotalOverall(selectStmnt, updateStmnt, errorReportCStat[0], REPORT_ADDRESS_ERROR,
													0, numAddrFormula.get(data.getMeta().errorReportCStat[0]));

			for (int i = 1; i < errorReportCStat.length; i++)
				updateSqlOverallStat(selectStmnt, updateStmnt, updatePercentStmnt, errorReportCStat[i], REPORT_ADDRESS_ERROR, 
													numAddrFormula.get(errorReportCStat[i]), totalAddrErrors);

			// update phone total
			totalPhone = addSqlTotalOverall(selectStmnt, updateStmnt, STAT_TOTAL_PHONE, REPORT_PHONE,
											numPhoneOverview.get(phoneOverviewReportCStat[2]), 
											data.getPhoneVerify().isEnabled() ? numLines : 0);

			// update phone overview entries
			for (int i = 0; i < phoneOverviewReportCStat.length; i++)
				updateSqlOverallStat(selectStmnt, updateStmnt, updatePercentStmnt, phoneOverviewReportCStat[i], REPORT_PHONE, 
										numPhoneOverview.get(phoneOverviewReportCStat[i]), 
										(i != 2) ? totalPhone : totalLines);
			
			totalPhone = addSqlTotalOverall(selectStmnt, updateStmnt, STAT_TOTAL_PHONE, REPORT_PHONE_REPORT,
											numPhoneOverview.get(phoneOverviewReportCStat[2]), 
											data.getPhoneVerify().isEnabled() ? numLines : 0);
			
			// update complex stats for the phone reporting

			for (int i = 0; i < phoneFormulaFields.length; i++)
				updateSqlOverallStat(selectStmnt, updateStmnt, updatePercentStmnt, phoneFormulaFields[i], REPORT_PHONE_REPORT, 
										numPhoneFormula.get(phoneFormulaFields[i]), (i != 2) ? totalPhone : totalLines);

			// update the email total line
			totalEmail = addSqlTotalOverall(selectStmnt, updateStmnt, STAT_TOTAL_EMAIL, REPORT_EMAIL,
											numEmailOverview.get(emailOverviewReportCStat[2]), 
											data.getEmailVerify().isEnabled() ? numLines : 0);

			// update the email overview lines
			for (int i = 0; i < emailOverviewReportCStat.length; i++)
				updateSqlOverallStat(selectStmnt, updateStmnt, updatePercentStmnt, emailOverviewReportCStat[i], REPORT_EMAIL, 
										numEmailOverview.get(emailOverviewReportCStat[i]), (i != 3) ? totalEmail : totalLines);

			totalEmail = addSqlTotalOverall(selectStmnt, updateStmnt, STAT_TOTAL_EMAIL, REPORT_EMAIL_REPORT,
											numEmailOverview.get(emailOverviewReportCStat[2]), 
											data.getEmailVerify().isEnabled() ? numLines : 0);

			// update the complex formulas that are for email

			for (int i = 0; i < (emailFormulaFields.length - 1); i++)
				updateSqlOverallStat(selectStmnt, updateStmnt, updatePercentStmnt, emailFormulaFields[i], REPORT_EMAIL_REPORT, 
										numEmailFormula.get(emailFormulaFields[i]), totalEmail);
			
			updateSqlOverallStat(selectStmnt, updateStmnt, updatePercentStmnt, emailFormulaFields[4], REPORT_EMAIL_REPORT, 
									numEmailFormula.get(emailFormulaFields[4]), totalLines);

			// update the name total

			totalName = addSqlTotalOverall(selectStmnt, updateStmnt, STAT_TOTAL_NAME, REPORT_NAME,
											numNameOverview.get(nameOverviewFields[2]), 
											data.getNameParse().isEnabled() ? numLines : 0);

			totalCompanyName = addSqlTotalOverall(selectStmnt, updateStmnt, STAT_TOTAL_COMPANY_NAME, REPORT_NAME,
													numNameOverview.get(nameOverviewFields[5]), 
													data.getNameParse().isCompanyEnabled() ? numLines : 0);

			// update the overview fields for the name object
			for (int i = 0; i < nameOverviewFields.length; i++) {
				if (i < 2)
					amount = totalName;
				else if (i == 2 || i == 5)
					amount = totalLines;
				else
					amount = totalCompanyName;

				updateSqlOverallStat(selectStmnt, updateStmnt, updatePercentStmnt, nameOverviewFields[i], REPORT_NAME, 
										numNameOverview.get(nameOverviewFields[i]), amount);
			}

			totalName = addSqlTotalOverall(selectStmnt, updateStmnt, STAT_TOTAL_NAME, REPORT_NAME_REPORT,
											numNameOverview.get(nameOverviewFields[2]), 
											data.getNameParse().isEnabled() ? numLines : 0);

			totalCompanyName = addSqlTotalOverall(selectStmnt, updateStmnt, STAT_TOTAL_COMPANY_NAME, REPORT_NAME_REPORT, 
											numNameOverview.get(nameOverviewFields[5]), 
											data.getNameParse().isCompanyEnabled() ? numLines : 0);

			// update any complex formula fields for the name object
			for (int i = 0; i < nameFormulaFields.length; i++) {
				if (i < 2)
					amount = totalName;
				else if (i == 2 || i == 5)
					amount = totalLines;
				else
					amount = totalCompanyName;

				updateSqlOverallStat(selectStmnt, updateStmnt, updatePercentStmnt, nameFormulaFields[i], REPORT_NAME_REPORT, 
										numNameFormula.get(nameFormulaFields[i]), amount);
			}

			// update the geocoder total
			totalGeo = addSqlTotalOverall(selectStmnt, updateStmnt, STAT_TOTAL_GEO, REPORT_GEOCODE,
											numGeoOverview.get(geoOverviewReportCStat[3]), 
											data.getGeoCoder().isEnabled() ? numLines : 0);

			// update all the geocoder stats that are complex
			for (int i = 0; i < geoOverviewReportCStat.length; i++)
				updateSqlOverallStat(selectStmnt, updateStmnt, updatePercentStmnt, geoOverviewReportCStat[i], REPORT_GEOCODE, 
										numGeoOverview.get(geoOverviewReportCStat[i]), (i != 3) ? totalGeo : totalLines);

		} catch (SQLException e) {
			throw new KettleException("Problem updating the overall stats", e);
		}
	}

	/**
	 * Called to update the detailed percentage values
	 * 
	 * @param connection 
	 * @throws KettleException 
	 */
	private void updateDetailPercentages(Connection connection) throws KettleException {

		try {
			PreparedStatement selectStmnt = connection.prepareStatement("select code,numreturn from resultcodes where code not in ( 'datetime','version' );");
			ResultSet rs = selectStmnt.executeQuery();

			while (rs.next()) {
				String numReturnStr = "0";
				numReturnStr = rs.getString("numreturn");
				numReturnStr = numReturnStr.replaceAll(",", "");
				int numReturn = new Integer(numReturnStr);
				
				String code = rs.getString("code");
				
				PreparedStatement updatePercentStmnt = connection.prepareStatement("update resultcodes set percentage = ? where code = ?;");
				
				if (code.contains("AS") && !(code.contains("AS13") || code.contains("AS14") || code.contains("AS15"))) {
					updatePercentStmnt.setString(1, safeFormatPercentage(numReturn, totalAddr));
					
				} else if (code.contains("AC") || code.contains("AS13") || code.contains("AS14") || code.contains("AS15")) {
					updatePercentStmnt.setString(1, safeFormatPercentage(numReturn, totalAddrChanges));
					
				} else if (code.contains("AE")) {
					updatePercentStmnt.setString(1, safeFormatPercentage(numReturn, totalAddrErrors));
					
				} else if (code.contains("PE02")) {
					updatePercentStmnt.setString(1, safeFormatPercentage(numReturn, totalLines));
					
				} else if (code.contains("PS") || code.contains("PE")) {
					updatePercentStmnt.setString(1, safeFormatPercentage(numReturn, totalPhone));
					
				} else if (code.contains("NS") || code.contains("NE")) {
					if (code.equals("NS99") || code.equals("NE99"))
						updatePercentStmnt.setString(1, safeFormatPercentage(numReturn, totalCompanyName));
					else
						updatePercentStmnt.setString(1, safeFormatPercentage(numReturn, totalName));
					
				} else if (code.contains("GS") || code.contains("GE")) {
					updatePercentStmnt.setString(1, safeFormatPercentage(numReturn, totalGeo));
					
				} else if (code.contains("ES") || code.contains("EE")) {
					updatePercentStmnt.setString(1, safeFormatPercentage(numReturn, totalEmail));
					
				}

				updatePercentStmnt.setString(2, code);
				updatePercentStmnt.executeUpdate();
			}

		} catch (SQLException e) {
			throw new KettleException("Problem updating detail percentages");
		}
	}
	
	/**
	 *  Updates statistics in database for the reporting
	 * 
	 * @param prefix
	 * @param category
	 * @param resultStats
	 * @throws KettleException 
	 */
	public void updateResultStats(Connection connection, String prefix, String category, ReportStats resultStats) throws KettleException {
		if (resultStats == null)
			return;
		
		// Intersect defined result codes with the result stats passed in
		Map<String, String> resultCodes = data.getResultCodes(category, prefix);
		ReportStats stats = new ReportStats();
		for (String code : resultCodes.keySet())
			stats.set(code, resultStats.get(code));

		// Roll GS02 into GS03, remove GS02
		if (stats.containsKey("GS02")) {
			int newCount = stats.get("GS02");
			newCount += stats.get("GS03");
			stats.remove("GS02");
			stats.set("GS03", newCount);
		}
		
		// access database
		for (String key : stats.getKeys()) {
			int stat = stats.get(key);
			if (stat < 0) 
				continue;
			
			try {
				PreparedStatement selectStmnt = connection.prepareStatement("select numreturn from resultcodes where code = ?;");
				selectStmnt.setString(1, key);
				ResultSet rs = selectStmnt.executeQuery();
				
				String numReturnStr = "0";
				if (rs.next()) {
					numReturnStr = rs.getString("numreturn");
					numReturnStr = numReturnStr.replaceAll(",", "");
					int numReturn = new Integer(numReturnStr);
					numReturn += stat;
					
					selectStmnt.close();

					PreparedStatement updateStmnt = connection.prepareStatement("update resultcodes set numreturn = ? where code = ?;");

					updateStmnt.setString(1, LONG_NUMBER_FORMAT.format(numReturn));
					updateStmnt.setString(2, key);
					updateStmnt.executeUpdate();

					updateStmnt.close();

				} else {
					PreparedStatement insertStmnt = connection.prepareStatement("insert into resultcodes values (?,?,?,?,?);");
					
					insertStmnt.setString(1, category);
					insertStmnt.setString(2, key);
					insertStmnt.setString(3, resultCodes.get(key));
					insertStmnt.setString(4, LONG_NUMBER_FORMAT.format(stat));
					
					if (key.contains("AS") && !(key.contains("AS13") || key.contains("AS14") || key.contains("AS15"))) {
						insertStmnt.setString(5, safeFormatPercentage(stat, totalAddr));
						
					} else if (key.contains("AC") || key.contains("AS13") || key.contains("AS14") || key.contains("AS15")) {
						insertStmnt.setString(5, safeFormatPercentage(stat, totalAddrChanges));
						
					} else if (key.contains("AE")) {
						insertStmnt.setString(5, safeFormatPercentage(stat, totalAddrErrors));
						
					} else if (key.contains("PE02")) {
						insertStmnt.setString(5, safeFormatPercentage(stat, totalLines));
						
					} else if (key.contains("PS") || key.contains("PE")) {
						insertStmnt.setString(5, safeFormatPercentage(stat, totalPhone));
						
					} else if (key.contains("NS") || key.contains("NE")) {
						if (key.equals("NS99") || key.equals("NE99"))
							insertStmnt.setString(5, safeFormatPercentage(stat, totalCompanyName));
						else
							insertStmnt.setString(5, safeFormatPercentage(stat, totalName));
						
					} else if (key.contains("GS") || key.contains("GE")) {
						insertStmnt.setString(5, safeFormatPercentage(stat, totalGeo));
						
					} else if (key.contains("ES") || key.contains("EE")) {
						insertStmnt.setString(5, safeFormatPercentage(stat, totalEmail));
						
					}

					insertStmnt.addBatch();
					
					selectStmnt.close();
					
					insertStmnt.executeBatch();
				}

			} catch (SQLException e) {
				throw new KettleException("Problem updating result stats", e);
			}
		}
		
		updateDetailPercentages(connection);
	}

	public int getNumLines() {
		return numLines;
	}
	public void setNumLines(int numLines) {
		this.numLines = numLines;
	}
	public ReportStats getNumAddrOverview() {
		return numAddrOverview;
	}
	public void setNumAddrOverview(ReportStats numAddrOverview) {
		this.numAddrOverview = numAddrOverview;
	}
	public ReportStats getNumPhoneOverview() {
		return numPhoneOverview;
	}
	public void setNumPhoneOverview(ReportStats numPhoneOverview) {
		this.numPhoneOverview = numPhoneOverview;
	}
	public ReportStats getNumGeoOverview() {
		return numGeoOverview;
	}
	public void setNumGeoOverview(ReportStats numGeoOverview) {
		this.numGeoOverview = numGeoOverview;
	}
	public ReportStats getNumNameOverview() {
		return numNameOverview;
	}
	public void setNumNameOverview(ReportStats numNameOverview) {
		this.numNameOverview = numNameOverview;
	}
	public ReportStats getNumEmailOverview() {
		return numEmailOverview;
	}
	public void setNumEmailOverview(ReportStats numEmailOverview) {
		this.numEmailOverview = numEmailOverview;
	}
	public ReportStats getNumAddrFormula() {
		return numAddrFormula;
	}
	public void setNumAddrFormula(ReportStats numAddrFormula) {
		this.numAddrFormula = numAddrFormula;
	}
	public ReportStats getNumPhoneFormula() {
		return numPhoneFormula;
	}
	public void setNumPhoneFormula(ReportStats numPhoneFormula) {
		this.numPhoneFormula = numPhoneFormula;
	}
	public ReportStats getNumGeoFormula() {
		return numGeoFormula;
	}
	public void setNumGeoFormula(ReportStats numGeoFormula) {
		this.numGeoFormula = numGeoFormula;
	}
	public ReportStats getNumNameFormula() {
		return numNameFormula;
	}
	public void setNumNameFormula(ReportStats numNameFormula) {
		this.numNameFormula = numNameFormula;
	}
	public ReportStats getNumEmailFormula() {
		return numEmailFormula;
	}
	public void setNumEmailFormula(ReportStats numEmailFormula) {
		this.numEmailFormula = numEmailFormula;
	}


}
