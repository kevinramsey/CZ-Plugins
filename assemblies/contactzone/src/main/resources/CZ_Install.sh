#!/bin/bash
isoDir=$(pwd);
printf "\nThank you for Installing ContactZone\n";

if type -p java; then
    _java=java
elif [[ -n "$JAVA_HOME" ]] && [[ -x "$JAVA_HOME/bin/java" ]];  then
    _java="$JAVA_HOME/bin/java"
else
    printf "We were unable to find Java on this machine.\n"
    printf "Please Install Java 1.5 or greater before continuing.\n"
    exit;
fi

if [[ "$_java" ]]; then
    version=$("$_java" -version 2>&1 | awk -F '"' '/version/ {print $2}')
    if [[ "$version" < "1.6" ]]; then
	printf "Current Java version $version\n"
	printf "Java 1.6 or greater is required.\n"
    	printf "Please Install Java 1.5 or greater before continuing.\n"
	exit;
    fi
fi


 ARCH=`uname -m`
		case $ARCH in
			x86_64)
				LIBPATH=$isoDir/Windows/libswt/linux/x86_64/
				;;

			i[3-6]86)
				LIBPATH=$isoDir/Windows/libswt/linux/x86/
				;;


			*)	
				echo "I'm sorry, this Linux platform [$ARCH] is not yet supported!"
				exit
				;;
		esac
		

cd misc/Linux;

ff=$(pwd);

rawPath=$(java -cp CZLinInstall.jar:$ff/*:$isoDir/Windows/lib/*:$isoDir/Windows/lib/*:$isoDir/Windows/libswt/*:$LIBPATH/* src.CZInstall $isoDir $USER);
status=$?;
wait;

if [[ "$status" -ne 0 ]]; then
printf "\nInstall Cancelled\n"
exit;
fi

spoonPath=${rawPath//[$'\t\r\n']}

chmod +x $spoonPath/*.sh;

workDir="$HOME/.kettle";
printf "Stats Path $workDir \n";

printf "Spoon - $spoonPath \n";
cd "$spoonPath";
#write reporting props
printf "ContactStats/url=jdbc:sqlite:$workDir/ContactStats.db" >> simple-jndi/cz.properties
printf "GlobalAddressStats/url=jdbc:sqlite:$workDir/GlobalAddressStats.db" >> simple-jndi/ga.properties



if [[ "$USER" == "root" ]]; then
	#chmod +x  /usr/share/applications/ContactZone.desktop;
	chmod +x  /usr/share/ContactZone/.CZ_Launcher.sh;
else
	#chmod +x  ~/.local/share/applications/ContactZone.desktop;
	chmod +x  ~/Desktop/ContactZone.desktop;
	chmod +x  ~/.local/share/ContactZone/.CZ_Launcher.sh;
fi

if [ -e $spoonPath/StartCZ.st ];
then

./spoon.sh;
rm $spoonPath/StartCZ.st;

fi






