
MacOS Users

It will be nessary to change the dynamic lib path in libmdLicenseJavaWrapper.jnilib


From command line run. 

cd  /Users/<user_name>/.kettle/MD/64_bit

Run:
otool -L   libmdLicenseJavaWrapper.jnilib 

Example:

$ otool -L libmdLicenseJavaWrapper.jnilib \

libmdLicenseJavaWrapper.jnilib:\
        ./libmdLicenseJavaWrapper.jnilib (compatibility version 0.0.0, current version 0.0.0)\
        libmdLicense.so (compatibility version 0.0.0, current version 0.0.0)\
        /usr/local/opt/gcc/lib/gcc/5/libstdc++.6.dylib (compatibility version 7.0.0, current version 7.21.0)\
        /usr/lib/libSystem.B.dylib (compatibility version 1.0.0, current version 1226.10.1)\
        /usr/local/lib/gcc/5/libgcc_s.1.dylib (compatibility version 1.0.0, current version 1.0.0)\



use results to get path to libmdLicense.so and run install_name_tool -change <original path>  <new path> libmdLicenseJavaWrapper.jnilib


if there is no path to libmdLicense.so as above just enter libmdLicense.so for <new path>

Examples:


install_name_tool -change  libmdLicense.so /Users/<userName>/.kettle/MD/64_bit/libmdLicense.so libmdLicenseJavaWrapper.jnilib

or

install_name_tool -change  <Path from otool>/libmdLicense.so /Users/<userName>/.kettle/MD/64_bit/libmdLicense.so libmdLicenseJavaWrapper.jnilib



 