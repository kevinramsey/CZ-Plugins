Setting up to run MD data quality products on hadoop.


Depending on which component is being used there are three things that will be needed to run our components on
a Hadoop cluster.


The properties file.  mdProps.prop    This copy of the props file file will need to be edited so that any data path
settings point to the appropriate locations on the Hadoop cluster.

The native objects .so files.  Depending on which components you may only need the license object or all the objects.  
These will always come in pairs  as in mdXXX.so and mdXXXJavaWrapper.

The data files again depending on which components are being used will determine which data files are needed.



File Locations.
	The mdProps.prop file and object files will be on the Controller .

	Because of the large size it is recommended that a copy of the data files be kept on each node.


The "User Defined" tab of the  MapReduce job entry. On this tab there are two variables that need to be set.

	mapred.cache.files				<full path to file>#<File Name>
	This variable is used to tell Hadoop to copy files to the cache on the nodes.

mapred.job.classpath.files      <full path to file> 
This is the setting that adds the path to our native objects  to the LD_LIBRARY_PATH


	
This is just that basics.  Hadoop is complex and ther is more than one way to set things up.  The examples below
seem to be the simplest.




HADOOP SETUP

1) Create a new dir to hold native objects on the HDFS.  I use HUE as this needs to be on HDFS not just
on the machine. The location and name I use is /DQT.

2) Upload the appropriate object files (64 or 32 bit   .so for linux .dll windows) to the newly created location.


3) Upload a copy of your mdProps.prop file to the newly created location.
This copy of the props file file will need to be edited so that "data_path=<cluster data location>"     (see step 4)




3) In order for the files to be picked up and added to the correct paths you will need to add some setting in the "User Defined" 
tab of the  MapReduce job entry.

-------------------------------------------------------------------------------------------------------------------------
Name 							Value

mapred.cache.files				<full path to file>#<File Name>, add the rest seperated by ","... 

			EXAMPLE:	/DQT/libmdLicense.so#libmdLicense.so,/DQT/libmdLicenseJavaWrapper.so#libmdLicenseJavaWrapper.so,/DQT/libmdName.so#libmdName.so,/DQT/libmdNameJavaWrapper.so#libmdNameJavaWrapper.so,/DQT/libmdAddr.so#libmdAddr.so,/DQT/libmdAddrJavaWrapper.so#libmdAddrJavaWrapper.so,/DQT/libmdEmail.so#libmdEmail.so,/DQT/libmdEmailJavaWrapper.so#libmdEmailJavaWrapper.so,/DQT/libmdPhone.so#libmdPhone.so,/DQT/libmdPhoneJavaWrapper.so#libmdPhoneJavaWrapper.so,/DQT/libmdGeo.so#libmdGeo.so,/DQT/libmdGeoJavaWrapper.so#libmdGeoJavaWrapper.so

Note: Any files added to here are copied to the local cache folder for for the given job.  The first part is the actual 
file location and name the part after the “# “ is the link name that will be created by hadoop.  				
-------------------------------------------------------------------------------------------------------------------------
Name 							Value

mapred.job.classpath.files      <full path to file>,<full path to file>.. and so on

			EXAMPLE :	/DQT/libmdLicense.so,/DQT/libmdLicenseJavaWrapper.so,/DQT/libmdName.so,/DQT/libmdNameJavaWrapper.so,/DQT/libmdAddr.so,/DQT/libmdAddrJavaWrapper.so,/DQT/libmdEmail.so,/DQT/libmdEmailJavaWrapper.so,/DQT/libmdGeo.so,/DQT/libmdGeoJavaWrapper.so,/DQT/libmdPhone.so,/DQT/libmdPhoneJavaWrapper.so

Note: This is the setting that adds it to the LD_LIBRARY_PATH
				
-------------------------------------------------------------------------------------------------------------------------

4) On each node there needs to be a copy of the data.  create a directory /hadoop/yarn/local/DQT/data.  

/hadoop/yarn/local/usercache/<user>/...   is the location that hadoop copies the jar and other files to run the jobs, so
/hadoop/yarn/local/DQT/data should be accessible by all users.


				
				
				