**************************************************************

Thank you for installing Melissa Data DQT.

FOR LINUX USERS:

In order to allow Melissa Datas native objects to be used by Pentaho 
it will be nessary to edit the spoon.sh, kitchen.sh, pan.sh and carte.sh files.

-------------------------------------------------------------------------------------


FOR 32bit linux

In the spoon.sh file

Change LD_LIBRARY_PATH=${MOZILLA_FIVE_HOME}:${LD_LIBRARY_PATH}

to LD_LIBRARY_PATH=${MOZILLA_FIVE_HOME}:${LD_LIBRARY_PATH}:$HOME/.kettle/MD/32_bit

also add 

LIBPATH=$HOME/.kettle/MD/32_bit

just below

# **************************************************
# ** Platform specific libraries ...              **
# **************************************************

LIBPATH="NONE"

In the pan.sh, carte.sh and kitchen.sh files

Add

LIBPATH=$HOME/.kettle/MD/32_bit
LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$HOME/.kettle/MD/32_bit
export LD_LIBRARY_PATH LIBPATH

just below

# ******************************************************************
# ** Set java runtime options                                     **
# ** Change 512m to higher values in case you run out of memory   **
# ** or set the PENTAHO_DI_JAVA_OPTIONS environment variable      **
# ** (JAVAMAXMEM is there for compatibility reasons)              **
# ******************************************************************

----------------------------------------------------------------------------------

FOR 64bit linux

In the spoon.sh file

Change LD_LIBRARY_PATH=${MOZILLA_FIVE_HOME}:${LD_LIBRARY_PATH}

to LD_LIBRARY_PATH=${MOZILLA_FIVE_HOME}:${LD_LIBRARY_PATH}:$HOME/.kettle/MD/64_bit

also add 

LIBPATH=$HOME/.kettle/MD/64_bit

just below

# **************************************************
# ** Platform specific libraries ...              **
# **************************************************

LIBPATH="NONE"





In the pan.sh, carte.sh and kitchen.sh files

Add

LIBPATH=$HOME/.kettle/MD/64_bit
LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$HOME/.kettle/MD/64_bit
export LD_LIBRARY_PATH LIBPATH

just below

# ******************************************************************
# ** Set java runtime options                                     **
# ** Change 512m to higher values in case you run out of memory   **
# ** or set the PENTAHO_DI_JAVA_OPTIONS environment variable      **
# ** (JAVAMAXMEM is there for compatibility reasons)              **
# ******************************************************************