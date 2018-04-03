
;   Cloud Only installer for ContactZone
;   Author: John Miller
;   Date: 03-2-2012

#define MyAppName "ContactZone"
;#define MyAppVersion "6.1.2"
#define revision "1"
#define MyAppPublisher "Melissa Data Corp"
#define MyAppURL "http://www.melissadata.com"
#define MyAppExeName "Spoon.bat"
#define StartParam "-file="


[Setup]
; NOTE: The value of AppId uniquely identifies this application.
; Do not use the same AppId value in installers for other applications.
; (To generate a new GUID, click Tools | Generate GUID inside the IDE.)
AppId=mdCZ
AppName={#MyAppName}
AppVersion={#MyAppVersion}
;AppVerName={#MyAppName} {#MyAppVersion}
AppPublisher={#MyAppPublisher}
AppPublisherURL={#MyAppURL}
AppSupportURL={#MyAppURL}
AppUpdatesURL={#MyAppURL}
DefaultDirName={pf}\Melissa Data\DQT\
DisableDirPage=no
DefaultGroupName=Contact Zone
DisableProgramGroupPage=yes
OutputDir={#OUTPUT_DIR}
OutputBaseFilename={#MyAppName}-cloud-{#VERSION_NUM}
Compression=lzma
SolidCompression=yes
; Make it so the defalut is not appended to the path name
AppendDefaultDirName=yes
; Suppress the warning when install directory already exists
DirExistsWarning=no
UsePreviousAppDir=no
SetupLogging=yes
UpdateUninstallLogAppName=yes
;PrivilegesRequired=lowest

LicenseFile={#RESOURCES_DIR}\License.txt
InfoAfterFile={#RESOURCES_DIR}\PostInstallMessage.txt
WizardImageFile={#RESOURCES_DIR}\welcome-CZ-2017.bmp
WizardSmallImageFile={#RESOURCES_DIR}\logo-contact-zone-studio-update.bmp
SetupIconFile={#RESOURCES_DIR}\contact-zone-icon.ico

ArchitecturesInstallIn64BitMode=x64 ia64

; The name of the code signing tool 
; SignTool=signer

; Enable the creation of file associations
ChangesAssociations=yes

[Types]
Name: "full"; Description: "Plugins and Data Files"
Name: "plugin"; Description: "Plugins Only"; 
Name: "data"; Description: "Data Files Only";
 

[Components]
;Name: "Plugins"; Description: "Pentaho Plugins"; Types: full plugin; 
;Name: "DataFiles"; Description: "Data Files"; Types: full data

                                                                                                                           
[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[Registry]
;Create file association for .ktr and .kjb files  
Root: HKCR; Subkey: ".ktr"; ValueType: string; ValueName: ""; ValueData: "MDCZ"; Flags: uninsdeletevalue
Root: HKCR; Subkey: ".kjb"; ValueType: string; ValueName: ""; ValueData: "MDCZ"; Flags: uninsdeletevalue
Root: HKCR; Subkey: "MDCZ"; ValueType: string; ValueName: ""; ValueData: "ContactZone"; Flags: uninsdeletekey
Root: HKCR; Subkey: "MDCZ\DefaultIcon"; ValueType: string; ValueName: ""; ValueData: "{app}\ContactZone\spoon.ico,0"; Flags: uninsdeletekey
Root: HKCR; Subkey: "MDCZ\shell\open\command"; ValueType: string; ValueName: ""; ValueData:  """{app}\ContactZone\Spoon.bat"" {#StartParam} ""%1"""; Flags: uninsdeletekey    



[Files]
; pre built Rules 
Source: "{#RULES_DIR}\*.xml"; DestDir:"{app}\ContactZone\mdKettle\MD\Rules"; Flags: ignoreversion recursesubdirs createallsubdirs; Permissions: users-modify
; mdProps
Source: "{#PROPERTIES_DIR}\mdProps.prop"; DestDir:"{app}\ContactZone\mdKettle"; Flags: onlyifdoesntexist ignoreversion recursesubdirs createallsubdirs; Permissions: users-modify
Source: "{#PROPERTIES_DIR}\mdProps.prop"; DestDir:"{app}\ContactZone\mdKettle\tmp"; Flags: ignoreversion recursesubdirs createallsubdirs; Permissions: users-modify
Source: "{#PROPERTIES_DIR}\contact_zone.prp"; DestDir:"{app}\ContactZone\ui"; Flags: ignoreversion recursesubdirs createallsubdirs; Permissions: users-modify

; data-integration
Source: "{#APP_FILES}\*"; Excludes: ".svn"; DestDir: "{app}\ContactZone"; Flags: ignoreversion recursesubdirs createallsubdirs
; Dlls
;-------  Source: "{#DLL_DIR}\*"; DestDir: "{app}\ContactZone\mdKettle\MD"; Flags: ignoreversion recursesubdirs createallsubdirs
; master Reports
;-------  Source: "{#REPORTS_DIR}\reports\*.*"; DestDir: "{app}\ContactZone\mdKettle\MD\libext\reporting"; Flags: ignoreversion recursesubdirs createallsubdirs 
; JNDI property files
;-------  Source: "{#REPORTS_DIR}\JNDI\cz.properties"; DestDir: "{app}\ContactZone\simple-jndi"; Flags: ignoreversion
;-------  Source: "{#REPORTS_DIR}\JNDI\ga.properties"; DestDir: "{app}\ContactZone\simple-jndi"; Flags: ignoreversion

; cz.lib and MDSettings
;Source: "{#CZ_LIB_DIR}\bin\*.jar"; DestDir: "{app}\ContactZone\lib"; Flags: ignoreversion
;Source: "{#CZ_LIB_DIR}\lib-md\*.jar"; DestDir: "{app}\ContactZone\lib"; Flags: ignoreversion
;Source: "{#MDSETTINGS_DIR}\bin\*.jar"; DestDir: "{app}\ContactZone\lib"; Flags: ignoreversion

; MDCheck 
;Source: "{#MDCHECK_DIR}\bin\*.jar"; DestDir: "{app}\ContactZone\plugins\MDCheck"; Flags: ignoreversion
; MDGlobal Verify
;Source: "{#GLOBAL_VERIFY_DIR}\bin\*.jar"; DestDir: "{app}\ContactZone\plugins\MDGlobalVerify"; Flags: ignoreversion
; MDPersonator 
;Source: "{#PERSONATOR_DIR}\bin\*.jar"; DestDir: "{app}\ContactZone\plugins\MDPersonator"; Flags: ignoreversion
;Source: "{#PERSONATOR_DIR}\outGrp.txt"; DestDir: "{app}\ContactZone\plugins\MDPersonator"; Flags: ignoreversion 
;MDBusinessCoder
;Source: "{#BUS_CODER_DIR}\bin\*.jar"; DestDir: "{app}\ContactZone\plugins\MDBusinessCoder"; Flags: ignoreversion 
;MDProfiler
;Source: "{#PROFILER_DIR}\bin\*.jar"; DestDir: "{app}\ContactZone\plugins\MDProfiler"; Flags: ignoreversion

;MDPresort
;Source: "{#PRESORT_DIR}\bin\*.jar"; DestDir: "{app}\ContactZone\plugins\MDPresort"; Flags: ignoreversion
;Source: "{#PRESORT_DIR}\presortExec\*.*"; DestDir:"{%USERPROFILE}\.kettle\presort"; Flags: ignoreversion recursesubdirs createallsubdirs; Permissions: users-modify

;MDCleanser
;Source: "{#CLEANSER_DIR}\bin\*.jar"; DestDir: "{app}\ContactZone\plugins\MDCleanser"; Flags: ignoreversion
;MDPropertyService
;Source: "{#PROPERTY_SERVICE_DIR}\bin\*.jar"; DestDir: "{app}\ContactZone\plugins\MDPropertyService"; Flags: ignoreversion

; Name Object Files
; (Name object is local only starting in version 2.4.0 of the MDCheck plugin)
;-------  Source: "{#DATA_DIR}\Name\*.*"; Excludes:"*.cfg "; DestDir: "{app}\Data"; Flags: ignoreversion; Permissions: users-modify

; Address
;-------  Source: "{#DATA_DIR}\Address\*.*"; Excludes:" *.dbf, dph*, ews.txt, mdAddr.lic, lcd*, mdCanada*, mdLACS*, mdS* , month*, mdRBDI.dat, mdAddrKey.db"; DestDir: "{app}\Data"; Flags: ignoreversion; Permissions: users-modify


; phone
;-------  Source: "{#DATA_DIR}\Phone\*.*"; Excludes:"mdGlobalPhone.dat, mdAddr.dat"; DestDir: "{app}\Data"; Flags: ignoreversion; Permissions: users-modify

; email
;-------  Source: "{#DATA_DIR}\Email\*.*"; DestDir: "{app}\Data"; Flags: ignoreversion; Permissions: users-modify

; cleanser
;-------  Source: "{#DATA_DIR}\Cleanser\*.*"; DestDir: "{app}\Data"; Flags: ignoreversion; Permissions: users-modify
; profiler
;-------  Source: "{#DATA_DIR}\Profiler\*.*"; DestDir: "{app}\Data"; Flags: ignoreversion; Permissions: users-modify
; presort
;Source: "{#DATA_DIR}\Presort\*.*"; DestDir: "{app}\Data"; Flags: ignoreversion; Permissions: users-modify

; MatchUp Files
;-------  Source: "{#MU_FILES_DIR}\*.*"; Excludes:"*.mc, *.cfg "; DestDir:"{app}\ContactZone\mdKettle\matchup"; Flags: ignoreversion recursesubdirs createallsubdirs; Permissions: users-modify
;-------  Source: "{#MUGLOBAL_FILES_DIR}\*.*"; Excludes:"*.mc, *.cfg "; DestDir:"{app}\ContactZone\mdKettle\matchup.global"; Flags: ignoreversion recursesubdirs createallsubdirs; Permissions: users-modify
;-------  Source: "{#DLL_DIR}\64_bit\mdMatchUp.dll"; DestDir:"{app}\ContactZone\mdKettle\matchup"; Flags: ignoreversion recursesubdirs createallsubdirs; Permissions: users-modify
;-------  Source: "{#DLL_DIR}\64_bit\mdMatchUp.dll"; DestDir:"{app}\ContactZone\mdKettle\matchup.global"; Flags: ignoreversion recursesubdirs createallsubdirs; Permissions: users-modify


; Presort files
;Source: "MDPresort2.4.0\bin\*.jar"; DestDir: "{app}\ContactZone\plugins\steps\MDPresort"; Flags: ignoreversion
;Source: "mdPresortFiles\mdPresort.dll"; DestDir: "{app}\ContactZone\plugins\steps\MDPresort"; Flags: ignoreversion
;Source: "mdPresortFiles\mdPresort.lib"; DestDir: "{app}\ContactZone\plugins\steps\MDPresort"; Flags: ignoreversion
;Source: "mdPresortFiles\mdPresortExec.exe"; DestDir: "{app}\ContactZone\plugins\steps\MDPresort"; Flags: ignoreversion
;Source: "Data\presort\*.*"; DestDir: "{app}\Data"; Flags: ignoreversion; Permissions: users-modify


; Files for writing paths
Source: "{#RESOURCES_DIR}\internal_installer_executables\*"; Excludes: ".svn"; DestDir: "{app}\ContactZone"; Flags: ignoreversion 

; Files that should not be overwritten on users system
Source:"{#MU_FILES_DIR}\mdMatchup*.mc"; DestDir: "{app}\ContactZone\mdKettle\matchup\"; Flags: onlyifdoesntexist; Permissions: users-modify
Source:"{#MU_FILES_DIR}\mdMatchup.cfg"; DestDir: "{app}\ContactZone\mdKettle\matchup\"; Flags: onlyifdoesntexist; Permissions: users-modify
Source:"{#MUGLOBAL_FILES_DIR}\mdMatchup*.mc"; DestDir: "{app}\ContactZone\mdKettle\matchup.global\"; Flags: onlyifdoesntexist; Permissions: users-modify
Source:"{#MUGLOBAL_FILES_DIR}\mdMatchup.cfg"; DestDir: "{app}\ContactZone\mdKettle\matchup.global\"; Flags: onlyifdoesntexist; Permissions: users-modify
Source:"{#DATA_DIR}\Name\mdName.cfg"; DestDir: "{app}\Data\"; Flags: onlyifdoesntexist; Permissions: users-modify


; Documentation 
Source: "{#DOCUMENTATION_DIR}\*"; DestDir: "{app}\ContactZone\Documentation"; Flags: ignoreversion


; Sample files
;Source: "Production_Samples\*"; Excludes: ".svn"; DestDir: "{userdocs}\Melissa Data\Contact Zone\samples"; Flags: onlyifdoesntexist ignoreversion recursesubdirs createallsubdirs; Permissions: users-modify
;Source: "{#SAMPLES_DIR}\*"; Excludes: ".svn"; DestDir: "{app}\ContactZone\samples\MDSamples\"; Flags: onlyifdoesntexist ignoreversion recursesubdirs createallsubdirs; Permissions: users-modify

;VC++ runtime files
Source: "{#RESOURCES_DIR}\VCppRuntime\vcredist_x86.exe"; DestDir: {tmp};

Source: "{#RESOURCES_DIR}\VCppRuntime\vcredist_x64.exe"; DestDir: {tmp}; Check: IsWin64

; NOTE: Don't use "Flags: ignoreversion" on any shared system files

[Icons]
Name: "{group}\{#MyAppName}"; Filename: "{app}\ContactZone\{#MyAppExeName}"; IconFilename:"{app}\ContactZone\spoon.ico"
Name: "{commondesktop}\{#MyAppName}"; Filename: "{app}\ContactZone\{#MyAppExeName}"; IconFilename:"{app}\ContactZone\spoon.ico"
;Name: "{group}\Samples"; Filename: "{userdocs}\Melissa Data\Contact Zone\samples"; IconFilename:"{app}\ContactZone\spoon.ico"
Name: "{group}\Help"; Filename: "http://www.melissadata.com/webhelp/contactzone/index.htm"; IconFilename:"{app}\ContactZone\spoon.ico"
Name: "{group}\Uninstall {#MyAppName}"; Filename: "{uninstallexe}"; IconFilename:"{app}\ContactZone\spoon.ico" 


[Run]
;Filename: "{app}\ContactZone\license.bat"; Parameters: "{app}\ContactZone\mdKettle\tmp\mdProps.prop {code:WriteAppDataPathFile|{app}\ContactZone\czdatapath.txt}";   Flags: runhidden
;Filename: "{app}\ContactZone\license.bat"; Parameters: "{app}\ContactZone\mdKettle\tmp\mdProps.prop {code:Write_psexe_PathFile|{app}\ContactZone\psexepath.txt}"; Flags: runhidden

Filename: "{app}\ContactZone\license.bat"; Parameters: "{app}\ContactZone\mdKettle\mdProps.prop {code:WriteAppDataPathFile|{app}\ContactZone\czdatapath.txt}";  Flags: runhidden
;Filename: "{app}\ContactZone\license.bat"; Parameters: "{app}\ContactZone\mdKettle\mdProps.prop {code:Write_psexe_PathFile|{app}\ContactZone\psexepath.txt}"; Flags: runhidden

Filename: "{app}\ContactZone\license.bat"; Parameters: "{app}\ContactZone\mdKettle\mdProps.prop {code:GetLicense|{app}\ContactZone\licenseString.txt}"; Flags: runhidden
;Filename: "{app}\ContactZone\license.bat"; Parameters: "{app}\ContactZone\mdKettle\mdProps.prop {code:GetPSLicense|{app}\ContactZone\PSlicenseString.txt}"; Flags: runhidden
;Filename: "{app}\ContactZone\license.bat"; Parameters: "{app}\ContactZone\mdKettle\mdProps.prop {code:GetPresortPath|{app}\ContactZone\PSPathString.txt}"; Flags: runhidden
Filename: "{app}\ContactZone\license.bat"; Parameters: "{app}\ContactZone\mdKettle\mdProps.prop {code:GetPath|{app}\ContactZone\PathString.txt}"; Flags: runhidden
Filename: "{app}\ContactZone\license.bat"; Parameters: "{app}\ContactZone\mdKettle\mdProps.prop {code:GetDQTPath|{app}\ContactZone\DQTPathString.txt}"; Flags: runhidden
Filename: "{app}\ContactZone\license.bat"; Parameters: "{app}\ContactZone\mdKettle\mdProps.prop {code:CreateReportingPath|{app}\ContactZone\CVReportingPathString.txt}"; Flags: runhidden
Filename: "{app}\ContactZone\license.bat"; Parameters: "{app}\ContactZone\mdKettle\mdProps.prop {code:CreateGAReportingPath|{app}\ContactZone\GAReportingPathString.txt}"; Flags: runhidden
Filename: "{app}\ContactZone\combine.exe"; Parameters: "/a ""{app}\ContactZone\simple-jndi\ga.properties"" ""{app}\ContactZone\GAReportingPathString.txt"""; Flags: runhidden
Filename: "{app}\ContactZone\combine.exe"; Parameters: "/a ""{app}\ContactZone\simple-jndi\cz.properties"" ""{app}\ContactZone\CVReportingPathString.txt"""; Flags: runhidden
Filename: "{app}\ContactZone\combine.exe"; Parameters: "/a ""{app}\ContactZone\mdKettle\tmp\mdProps.prop"" ""{app}\ContactZone\DQTPathString.txt"""; Flags: runhidden
Filename: "{app}\ContactZone\combine.exe"; Parameters: "/a ""{app}\ContactZone\mdKettle\tmp\mdProps.prop"" ""{app}\ContactZone\PSPathString.txt"""; Flags: runhidden
Filename: "{app}\ContactZone\combine.exe"; Parameters: "/a ""{app}\ContactZone\mdKettle\tmp\mdProps.prop"" ""{app}\ContactZone\PSlicenseString.txt"""; Flags: runhidden
Filename: "{app}\ContactZone\combine.exe"; Parameters: "/a ""{app}\ContactZone\mdKettle\tmp\mdProps.prop"" ""{app}\ContactZone\PathString.txt"""; Flags: runhidden
Filename: "{app}\ContactZone\combine.exe"; Parameters: "/a ""{app}\ContactZone\mdKettle\tmp\mdProps.prop"" ""{app}\ContactZone\licenseString.txt"""; AfterInstall: MyAfterInstall; Flags: runhidden
Filename: "{app}\ContactZone\combine.exe"; Parameters: "/a ""{app}\ContactZone\mdKettle\tmp\mdProps.prop"" ""{app}\ContactZone\mdKettle\tmp\mdProps.prop"""; Flags: runhidden

Filename: "{app}\ContactZone\combine.exe"; Parameters: "/a ""{app}\ContactZone\mdKettle\mdProps.prop"" ""{app}\ContactZone\DQTPathString.txt"""; Flags: runhidden
Filename: "{app}\ContactZone\combine.exe"; Parameters: "/a ""{app}\ContactZone\mdKettle\mdProps.prop"" ""{app}\ContactZone\PSPathString.txt"""; Flags: runhidden
Filename: "{app}\ContactZone\combine.exe"; Parameters: "/a ""{app}\ContactZone\mdKettle\mdProps.prop"" ""{app}\ContactZone\PSlicenseString.txt"""; Flags: runhidden
Filename: "{app}\ContactZone\combine.exe"; Parameters: "/a ""{app}\ContactZone\mdKettle\mdProps.prop"" ""{app}\ContactZone\PathString.txt"""; Flags: runhidden
Filename: "{app}\ContactZone\combine.exe"; Parameters: "/a ""{app}\ContactZone\mdKettle\mdProps.prop"" ""{app}\ContactZone\licenseString.txt"""; AfterInstall: MyAfterInstall; Flags: runhidden
;Filename: "{app}\ContactZone\combine.exe"; Parameters: "/a ""{app}\ContactZone\mdKettle\tmp\mdProps.prop"" ""{app}\ContactZone\mdKettle\tmp\mdProps.prop"""; Flags: runhidden



;Install C++ runtime redistributables
;Filename: {tmp}\vcredist_x86.exe; Parameters: "/passive /norestart   /c:""msiexec /q:a /i vcredist.msi"" "; StatusMsg: Installing 2012 RunTime... ; Flags: runhidden unchecked ;
Filename: {tmp}\vcredist_x64.exe; Parameters: "/passive /norestart  /c:""msiexec /q:a /i vcredist.msi"" "; StatusMsg: Installing 2012 RunTime... ; Flags: runhidden unchecked ;  Check: IsWin64

Filename: "{app}\ContactZone\Spoon.bat"; Description: {cm:LaunchProgram,{cm:AppName}}; Flags: nowait postinstall skipifsilent

[InstallDelete]

Type: files; Name:"{app}\ContactZone\plugins\MDBusinessCoder\*.jar"
Type: files; Name:"{app}\ContactZone\plugins\MDCheck\*.jar"
Type: files; Name:"{app}\ContactZone\plugins\MDCleanser\*.jar"
Type: files; Name: "{app}\ContactZone\plugins\MDGlobalVerify\*.jar"
Type: files; Name: "{app}\ContactZone\plugins\MDPersonator\MDPersonator*.jar"
Type: files; Name:"{app}\ContactZone\plugins\MDProfiler\*.jar"
Type: files; Name:"{app}\ContactZone\plugins\MDPropertyService\*.jar"
;Type: files; Name:"{app}\ContactZone\plugins\MDPresort\*.jar"
;Type: filesandordirs; Name:"{%USERPROFILE}\.kettle\presort\*.*"

;files from old installer
Type: filesandordirs; Name:"{app}\ContactZone\plugins\steps\MDBusinessCoder"
Type: filesandordirs; Name:"{app}\ContactZone\plugins\steps\MDCheck"
Type: filesandordirs; Name:"{app}\ContactZone\plugins\steps\MDCleanser"
Type: filesandordirs; Name: "{app}\ContactZone\plugins\steps\MDGlobalVerify"
Type: filesandordirs; Name: "{app}\ContactZone\plugins\steps\MDGlobalAddress"
Type: filesandordirs; Name: "{app}\ContactZone\plugins\steps\MDPersonator"
Type: filesandordirs; Name:"{app}\ContactZone\plugins\steps\MDProfiler"
Type: filesandordirs; Name:"{app}\ContactZone\plugins\steps\MDPropertyService"
Type: filesandordirs; Name:"{app}\ContactZone\plugins\steps\MDPresort"

Type: filesandordirs; Name:"{app}\ContactZone\plugins\steps\S3CsvInput"
Type: filesandordirs; Name:"{app}\ContactZone\plugins\steps\ShapeFileReader3"
Type: filesandordirs; Name:"{app}\ContactZone\plugins\steps\DummyPlugin"
Type: filesandordirs; Name:"{app}\ContactZone\plugins\versioncheck"
Type: files; Name:"{app}\ContactZone\plugins\hour-partitioner.jar"
Type: filesandordirs; Name:"{commonappdata}\ContactZone"
Type: filesandordirs; Name:"{app}\ContactZone\mdKettle"
Type: filesandordirs; Name:"{app}\ContactZone\md"
Type: filesandordirs; Name:"{app}\ContactZone\libext"
Type: filesandordirs; Name:"{app}\ContactZone\Data Integration 32-bit.app"
Type: filesandordirs; Name:"{app}\ContactZone\Data Integration 64-bit.app"
Type: filesandordirs; Name:"{app}\ContactZone\launcher"
Type: filesandordirs; Name: "{app}\ContactZone\ui"
Type: files; Name: "{app}\ContactZone\lib\*.jar"
Type: files; Name: "{app}\ContactZone\lib\*.zip"
Type: files; Name: "{app}\ContactZone\libswt\*.jar"
Type: files; Name: "{app}\ContactZone\libswt\*.zip"
Type: files; Name:"{%USERPROFILE}\.kettle\tmp\*.jar"
;Type: files; Name: "{app}\uninst000.exe"

[UninstallDelete]
Type: files; Name:"{app}\ContactZone\licenseString.txt"
Type: files; Name:"{app}\ContactZone\PSlicenseString.txt"
Type: files; Name:"{app}\ContactZone\PathString.txt"
Type: files; Name:"{app}\ContactZone\PSPathString.txt"
Type: files; Name:"{app}\ContactZone\DQTPathString.txt"
Type: files; Name:"{app}\ContactZone\CVReportingPathString.txt"
Type: files; Name:"{app}\ContactZone\GAReportingPathString.txt"
Type: files; Name:"{app}\ContactZone\czdatapath.txt"
Type: files; Name:"{app}\ContactZone\psexepath.txt"
Type: files; Name:"{app}\ContactZone\combine.exe"
Type: filesandordirs; Name:"{app}\ContactZone\**\*"

Type: filesandordirs; Name:"{%USERPROFILE}\.kettle\tmp"
Type: filesandordirs; Name:"{%USERPROFILE}\.kettle\matchup\*.*"
Type: filesandordirs; Name:"{%USERPROFILE}\.kettle\matchup.global\*.*"
Type: filesandordirs; Name:"{%USERPROFILE}\.kettle\MD\*.*"
Type: filesandordirs; Name:"{%USERPROFILE}\.kettle\reports"
Type: files; Name:"{%USERPROFILE}\.kettle\md*"
Type: dirifempty; Name:"{%USERPROFILE}\.kettle\tmp"
Type: dirifempty; Name:"{%USERPROFILE}\.kettle\matchup"
Type: dirifempty; Name:"{%USERPROFILE}\.kettle\matchup.global"
Type: dirifempty; Name:"{%USERPROFILE}\.kettle\MD"
;Type: filesandordirs; Name:"{%USERPROFILE|{userdocs}}\mdFiles"
;Type: filesandordirs; Name:"{commonappdata}\ContactZone"

[Messages]
ConfirmUninstall=Are you sure you want to completely remove %1 and all of its samples and data?
SelectDirBrowseLabel=To change the location of your install please select a location below

[CustomMessages]
AppName=ContactZone
LaunchProgram=Start ContactZone after installation?

[Code]
function InitializeSetup(): Boolean;
var
 ErrorCode: Integer;
 JavaInstalled : Boolean;
 Result1 : Boolean;
 Result2 : Boolean;
 Versions: TArrayOfString;
 I: Integer;
// S: String;

 begin   // b1
 if RegGetSubkeyNames(HKLM, 'SOFTWARE\JavaSoft\Java Runtime Environment', Versions) then

 begin

  for I := 0 to GetArrayLength(Versions)-1 do
 
   if JavaInstalled = true then
   begin
    //do nothing
   end else
   begin
    if ( Versions[I][2]='.' ) and ( ( StrToInt(Versions[I][1]) > 1 ) or ( ( StrToInt(Versions[I][1]) = 1 ) and ( StrToInt(Versions[I][3]) >= 6 ) ) ) then
    begin
     JavaInstalled := true;
//       JavaInstalled := false;
    end else
    begin
     JavaInstalled := false;
    end;
   end;
 end else
 begin
  JavaInstalled := false;
 end;



 if JavaInstalled then
  begin
  Result := true;
  end else
    begin
      Result1 := MsgBox('This tool requires Java Runtime Environment version 1.6 or newer to run. Please download and install the JRE and run this setup again. Do you want to download it now?',
      mbConfirmation, MB_YESNO) = idYes;
      if Result1 = false then
        begin
          Result:=false;
          // Ask if they want install without Java
          Result2 := MsgBox('You choose not to install Java.  Would you like to install ContactZone without installing Java?',
          mbConfirmation, MB_YESNO) = idYes;
          if Result2 = true then
            begin
              Result:= true;
            end;   
    
  end else
  begin
   Result:=false;
   ShellExec('open','http://www.java.com/en/download/manual.jsp','','',SW_SHOWNORMAL,ewNoWait,ErrorCode);
  end;
 end;


    
    
end; //e1



{ This is the code for the custom wizard page }
{ The license input screen is maintained in   }
{ this code                                   }

var LicensePage: TInputQueryWizardPage;
var DataDirPage: TInputDirWizardPage;
var templic: String;
var templic2: String;

{ Create custom wizard pages, set initial values: }


procedure InitializeWizard;

begin
  
  LicensePage := CreateInputQueryPage(wpSelectDir,
		'License Information', 'Were you given a license key?',
		'If you were e-mailed a license key, you may paste it in the area below.'#13#13 +
		'If you don''t have it handy, you will be prompted for it later, when you use the '+
		'components.');
	LicensePage.Add('License key:', False);

   begin
  //    if RegKeyExists(HKEY_CLASSES_ROOT, 'CZlic') then
        // if key is in new location
        if RegKeyExists(HKEY_CURRENT_USER, 'Software\Melissa Data\CZlic') then
          begin
            // The key exists in new location
            // RegQueryStringValue(HKEY_CLASSES_ROOT, 'CZlic', 'licval', templic);
            RegQueryStringValue(HKEY_CURRENT_USER, 'Software\Melissa Data\CZlic', 'licval', templic);
            LicensePage.Values[0] := templic; 
          end
            else
              begin
                // if key exists in old location
                if RegKeyExists(HKEY_CLASSES_ROOT, 'CZlic') then
                  begin
                    RegQueryStringValue(HKEY_CLASSES_ROOT, 'CZlic', 'licval', templic);
                    LicensePage.Values[0] := templic;
                  end
                    else
                      // else proceed normally
                      begin
                      LicensePage.Values[0] := GetPreviousData('LicenseKey', '');
                      end;
              end; // end else
   end;
end;


{ These two functions help us automatically set a license if the user specified one during the }
{   installation.                                                                              }
function LicenseProvided(): Boolean;
begin
	if Length(LicensePage.Values[0])>0 then
		Result := True
	else
		Result := False;
end;

function SetVersion(): Boolean;

var S : String; 
var ver : String;
var bld : String;
var path : String;

begin
  path := ExpandConstant('{app}\ContactZone\ui\contact_zone.prp');
  ver := '{#SetupSetting("OutputBaseFilename")}';
  bld := Copy(ver,19, Length(ver)-18);
  S := 'Version=' + bld;

  Log('Writing Version to ' + path + '  -  ' + S);
  
 SaveStringToFile(path,S, false);
 Result := True; 

end;

function GetLicense(Param: String): String;

var S : String; 


begin
	S := 'RESET.license=';
  
  SaveStringToFile(Param, S + LicensePage.Values[0], false);
  Result := LicensePage.Values[0]; 

end;


function GetDQTPath(Param: String): String;

var S : String; 
var S2 : String;
var S3 : String;
var S4 : String;
var count : Integer;

begin
	S := 'RESET.data_path=';
  S2 := '\Data';
  S3 := '#Primary DQT Data Path';

  S4 := S+ExpandConstant('{app}')+S2+#13#10;

  count := StringChangeEx(S4,'\','\\',True);
//  SaveStringToFile(Param, S3+ #13#10 + S + ExpandConstant('{userdocs}')+S2+#13#10, false);
//  SaveStringToFile(Param, S3+ #13#10 + S + ExpandConstant('{%USERPROFILE|{userdocs}}') +S2+#13#10, false);

  Log('Get DQT path : ' + Param + ' - ' + S3+#13#10+S4);
  SaveStringToFile(Param, S3+#13#10+S4, false);
  Result := LicensePage.Values[0]; 

end;


function CreateReportingPath(Param: String): String;


var S : String; 
var S2 : String;
var S3 : String;
var single : String;
var double : String;
var count : Integer;
begin
	
  single := '\';
  double := '\\';

  S := 'ContactStats/url=jdbc:sqlite:';
  S2 := '\.kettle\ContactStats.db';

  S3 := S+ExpandConstant('{%USERPROFILE}')+S2+#13#10;
  count := StringChangeEx(S3,single,double,True);
  SaveStringToFile(Param, S3, false);
  Result := LicensePage.Values[0]; 

end;

function CreateGAReportingPath(Param: String): String;


var S : String; 
var S2 : String;
var S3 : String;
var single : String;
var double : String;
var count : Integer;
begin
	
  single := '\';
  double := '\\';

  S := 'GlobalAddressStats/url=jdbc:sqlite:';
  S2 := '\.kettle\reports\GlobalAddressStats.db';

 
  S3 := S+ExpandConstant('{%USERPROFILE}')+S2+#13#10;
  count := StringChangeEx(S3,single,double,True);
  SaveStringToFile(Param, S3, false);
  Result := LicensePage.Values[0]; 

end;


function GetPath(Param: String): String;

var S : String; 
var S2 : String;
var S3 : String;
var single : String;
var double : String;
var count : Integer;
begin
	
  single := '\';
  double := '\\';

  S := 'RESET.data_path_mu=';
  S2 := '\.kettle\matchup';

 
  S3 := S+ExpandConstant('{%USERPROFILE}')+S2+#13#10;
  count := StringChangeEx(S3,single,double,True);
  SaveStringToFile(Param, S3, false);
  Result := LicensePage.Values[0]; 

end;


function GetPresortPath(Param: String): String;

var S : String; 
var S2 : String;
var S3 : String;
var S4 : String;
var S5 : String;
var S6 : String;
var count : Integer;

begin
	S := 'RESET.presort_datapath=';
  S2 := '\Data';
  S3 := '#Presort Data Path and license';
  S5 := 'presort_report_path=';

  S6 := S5+ExpandConstant('{userdocs}')+#13#10;
  S4 := S+ExpandConstant('{app}')+S2+#13#10;
  count := StringChangeEx(S6,'\','\\',True);
  count := StringChangeEx(S4,'\','\\',True);
  SaveStringToFile(Param, S3+#13#10+S4, false);
  SaveStringToFile(Param, #13#10+S6, true);
  
  Result := LicensePage.Values[0]; 

end;


function WriteAppDataPathFile(Param: String): String;

var S : String;
var S2 : String; 
var S3 : String;

begin
	
  //S := ExpandConstant('{%USERPROFILE}');
  //{app}\ContactZone\mdKettle
  S := ExpandConstant('{%app}');
  S2 := '\.kettle'
  S3 := '\ContactZone\mdKettle'
  Log('Saving AppDataPath : ' + S + ' - ' + S3 )
  SaveStringToFile(Param, S+S3, false);
  Result := LicensePage.Values[0]; 

end;

function Write_psexe_PathFile(Param: String): String;

var S : String;
var S2 : String; 

begin
	
  S := ExpandConstant('{app}');
  S2 := '\ContactZone\plugins\MDPresort'
  //Log('Saving to file : ' + S + ' - ' + S2 )
  SaveStringToFile(Param, S+S2, false);
  Result := LicensePage.Values[0]; 

end;



function GetPSLicense(Param: String): String;

var S : String; 


begin
	S := 'RESET.presort_license=';
  
  SaveStringToFile(Param, S + LicensePage.Values[0] +#13#10, false);
  Result := LicensePage.Values[0]; 

end;

function GetDataDir(Param: String): String;

begin
	
  Result := DataDirPage.Values[0]; 

end;

 

procedure MyAfterInstall();
begin
  
  // Writes the current license value to the registry.
  
  // MsgBox('Trying to write reg entry ' , mbInformation, MB_OK);

  LicensePage.Values[0]
    if RegWriteStringValue(HKEY_CURRENT_USER, 'Software\Melissa Data\CZlic', 'licval', LicensePage.Values[0]) then
    //if RegWriteStringValue(HKEY_CLASSES_ROOT, 'CZlic', 'licval', LicensePage.Values[0]) then
    begin
      // do nothing special
    end;
    SetVersion();

end;

