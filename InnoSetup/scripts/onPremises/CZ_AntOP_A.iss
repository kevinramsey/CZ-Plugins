;   On premise installer for ContactZone
;   Author: John Miller                           
;   Date: 01-30-2012

#define MyAppName "ContactZone"
;#define MyAppVersion "6.1.2"
#define MyAppPublisher "Melissa Corp"
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
;DefaultDirName={pf}\Melissa Data\ContactZone
DefaultDirName={pf}\Melissa Data\DQT\
DisableDirPage=no
DefaultGroupName=Contact Zone
DisableProgramGroupPage=yes
;OutputDir=E:\iso20130318
OutputDir={#OUTPUT_DIR}\{#ISO_DIR}
;OutputBaseFilename=ContactZone_4.1.0_OPb02
OutputBaseFilename={#MyAppName}-OP-{#VERSION_NUM}
Compression=lzma
SolidCompression=yes
; Make it so the defalut is not appended to the path name
AppendDefaultDirName=yes
; Suppress the warning when install directory already exists
DirExistsWarning=no
UsePreviousAppDir=no
SetupLogging=yes
;PrivilegesRequired=lowest

UpdateUninstallLogAppName=yes

;LicenseFile=C:\InstallFiles\Installer_Files_3.1.0\Resources\CZ_Specific\License.txt
;InfoAfterFile=C:\InstallFiles\Installer_Files_3.1.0\Resources\CZ_Specific\PostInstallMessage.txt
;WizardImageFile=C:\InstallFiles\Installer_Files_3.1.0\Resources\CZ_Specific\updated_images\welcome-CZ-32bit.bmp
;WizardSmallImageFile=C:\InstallFiles\Installer_Files_3.1.0\Resources\CZ_Specific\updated_images\logo-contact-zone-studio-update.bmp
;SetupIconFile=C:\InstallFiles\Installer_Files_3.1.0\Resources\CZ_Specific\updated_images\contact-zone-icon.ico

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
Name: "full"; Description: "ContactZone and Data Files";
Name: "cz"; Description: "ContactZone Only"; 
Name: "data"; Description: "Data Files Only";

[Components]
Name: "ContactZone"; Description: "ContactZone"; Types: full cz data;  
Name: "DataFiles"; Description: "Data Files"; Types: full data


[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[Registry]
;Create file association for .ktr files
Root: HKCR; Subkey: ".ktr"; ValueType: string; ValueName: ""; ValueData: "MDCZ"; Flags: uninsdeletevalue
Root: HKCR; Subkey: ".kjb"; ValueType: string; ValueName: ""; ValueData: "MDCZ"; Flags: uninsdeletevalue
Root: HKCR; Subkey: "MDCZ"; ValueType: string; ValueName: ""; ValueData: "ContactZone"; Flags: uninsdeletekey
Root: HKCR; Subkey: "MDCZ\DefaultIcon"; ValueType: string; ValueName: ""; ValueData: "{app}\ContactZone\spoon.ico,0"; Flags: uninsdeletekey
Root: HKCR; Subkey: "MDCZ\shell\open\command"; ValueType: string; ValueName: ""; ValueData:  """{app}\ContactZone\Spoon.bat"" {#StartParam} ""%1"""; Flags: uninsdeletekey    

[Tasks]


[Files]
;ContactZone main directory
Source: "{src}\Windows\*"; DestDir: "{app}\ContactZone"; Components: ContactZone; Flags: external ignoreversion recursesubdirs createallsubdirs; Permissions: users-modify

; Rules files
Source: "{src}\misc\rules\*"; DestDir: "{%HOMEPATH}\.kettle\MD\Rules"; Flags: external ignoreversion recursesubdirs createallsubdirs; Permissions: users-modify

;Property files
Source: "{src}\misc\properties\mdProps.prop"; DestDir:"{%HOMEPATH}\.kettle"; Flags: external onlyifdoesntexist ignoreversion recursesubdirs createallsubdirs; Permissions: users-modify
Source: "{src}\misc\properties\mdProps.prop"; DestDir:"{%HOMEPATH}\.kettle\tmp"; Flags: external ignoreversion recursesubdirs createallsubdirs; Permissions: users-modify
Source: "{src}\misc\properties\contact_zone.prp"; DestDir:"{app}\ContactZone\ui"; Flags: external ignoreversion recursesubdirs createallsubdirs; Permissions: users-modify

;Report templates
Source: "{src}\misc\reports\*.*"; DestDir: "{%HOMEPATH}\.kettle\MD\libext\reporting"; Flags: external ignoreversion recursesubdirs createallsubdirs

; JNDI property files
Source: "{src}\misc\JNDI\cz.properties"; DestDir: "{app}\ContactZone\simple-jndi"; Flags: external ignoreversion
Source: "{src}\misc\JNDI\ga.properties"; DestDir: "{app}\ContactZone\simple-jndi"; Flags: external ignoreversion


;Matchup Files
Source: "{src}\Matchup\mdMatchup.dat";  DestDir: "{%HOMEPATH}\.kettle\matchup"; Flags: external ignoreversion recursesubdirs createallsubdirs; Permissions: users-modify
Source: "{src}\Matchup\MatchUpEditor.exe";  DestDir: "{%HOMEPATH}\.kettle\matchup"; Flags: external ignoreversion recursesubdirs createallsubdirs; Permissions: users-modify
Source: "{src}\misc\DLLs\64_bit\mdMatchUp.dll"; DestDir: "{%HOMEPATH}\.kettle\matchup"; Flags: external ignoreversion recursesubdirs createallsubdirs; Permissions: users-modify

Source: "{src}\Matchup\Global\*.*"; DestDir: "{%HOMEPATH}\.kettle\matchup.global"; Flags: external ignoreversion recursesubdirs createallsubdirs; Permissions: users-modify
Source: "{src}\misc\DLLs\64_bit\mdMatchUp.dll"; DestDir: "{%HOMEPATH}\.kettle\matchup.global"; Flags: external ignoreversion recursesubdirs createallsubdirs; Permissions: users-modify

; Presort
;Source: "{src}\misc\presort\*.*"; DestDir: "{%HOMEPATH}\.kettle\presort"; Flags: external ignoreversion recursesubdirs createallsubdirs; Permissions: users-modify


; Dlls
Source: "{src}\misc\DLLs\*"; DestDir: "{%HOMEPATH}\.kettle\MD"; Flags: external ignoreversion recursesubdirs createallsubdirs

; Used for writing license string and paths
Source: "{#RESOURCES_DIR}\internal_installer_executables\*"; DestDir: "{app}\ContactZone"; Flags: ignoreversion

; Files that should not be overwritten on users system
Source:"{src}\Matchup\mdMatchup.mc"; DestDir: "{%HOMEPATH}\.kettle\matchup\"; Flags: external onlyifdoesntexist; Permissions: users-modify
Source:"{src}\Matchup\mdMatchup.cfg"; DestDir: "{%HOMEPATH}\.kettle\matchup\"; Flags: external onlyifdoesntexist; Permissions: users-modify

Source:"{src}\Matchup\Global\mdMatchup.mc"; DestDir: "{%HOMEPATH}\.kettle\matchup.global\"; Flags: external onlyifdoesntexist; Permissions: users-modify
Source:"{src}\Matchup\Global\mdMatchup.cfg"; DestDir: "{%HOMEPATH}\.kettle\matchup.global\"; Flags: external onlyifdoesntexist; Permissions: users-modify

Source:"{src}\Data\Name\mdName.cfg"; DestDir: "{app}\Data"; Flags: external onlyifdoesntexist; Permissions: users-modify
Source:"{src}\Data\Email\mdEmail.cfg"; DestDir: "{app}\Data"; Flags: external onlyifdoesntexist; Permissions: users-modify

; Documentation is stored under the DQC_PentahoPDI directory
Source: "{src}\Documentation\*"; DestDir: "{app}\ContactZone\Documentation"; Flags: external ignoreversion

; Sample files
;
;May need to move these from the installer source to the external source {src} location
;Source: "{src}\Samples\*"; DestDir: "{userdocs}\Melissa Data\Contact Zone\samples"; Flags: external onlyifdoesntexist ignoreversion recursesubdirs createallsubdirs; Permissions: users-modify
Source: "{src}\Samples\*"; DestDir: "{app}\ContactZone\samples\MDSamples"; Flags: external recursesubdirs createallsubdirs ignoreversion; Permissions: users-modify

;VC++ runtime files
Source: "{src}\misc\VCppRuntime\vcredist_x86.exe"; DestDir: {tmp};  Flags: external
Source: "{src}\misc\VCppRuntime\vcredist_x64.exe"; DestDir: {tmp};  Flags: external; Check: IsWin64

; The data files are referenced as external and need to reside in the locations listed below on the install media

Source: "{src}\Data\Address\*.*"; DestDir: {app}\Data; Components: DataFiles; Flags:  external ignoreversion recursesubdirs createallsubdirs
Source: "{src}\Data\Cleanser\*.*"; DestDir: {app}\Data; Components: DataFiles; Flags:  external ignoreversion recursesubdirs createallsubdirs
Source: "{src}\Data\Email\mdEmail.dat"; DestDir: {app}\Data; Components: DataFiles; Flags:  external ignoreversion recursesubdirs createallsubdirs
Source: "{src}\Data\Geocode\*.*"; DestDir: {app}\Data; Components: DataFiles; Flags:  external ignoreversion recursesubdirs createallsubdirs
Source: "{src}\Data\Profiler\*.*"; DestDir: {app}\Data; Components: DataFiles; Flags:  external ignoreversion recursesubdirs createallsubdirs
Source: "{src}\Data\Name\*.*"; DestDir: {app}\Data; Components: DataFiles; Flags:  external ignoreversion recursesubdirs createallsubdirs
Source: "{src}\Data\Phone\*.*"; DestDir: {app}\Data; Components: DataFiles; Flags:  external ignoreversion recursesubdirs createallsubdirs
;Source: "{src}\Data\Presort\*.*"; DestDir: {app}\Data; Components: DataFiles; Flags:  external ignoreversion recursesubdirs createallsubdirs
Source: "{src}\Data\IpLocator\*.*"; DestDir: {app}\Data; Components: DataFiles; Flags:  external ignoreversion recursesubdirs createallsubdirs
; NOTE: Don't use "Flags: ignoreversion" on any shared system files

[Icons]

Name: "{group}\{#MyAppName}"; Filename: "{app}\ContactZone\{#MyAppExeName}"; IconFilename:"{app}\ContactZone\spoon.ico"
Name: "{commondesktop}\{#MyAppName}"; Filename: "{app}\ContactZone\{#MyAppExeName}"; IconFilename:"{app}\ContactZone\spoon.ico"
;Name: "{group}\Samples"; Filename: "{userdocs}\Melissa Data\Contact Zone\samples"; IconFilename:"{app}\ContactZone\spoon.ico"
Name: "{group}\Help"; Filename: "http://www.melissadata.com/webhelp/contactzone/index.htm"; IconFilename:"{app}\ContactZone\spoon.ico"
Name: "{group}\Uninstall ContactZone"; Filename: "{app}\{uninstallexe}"; IconFilename:"{app}\ContactZone\spoon.ico" 

[Run]
; newer definitions from cloud installer
Filename: "{app}\ContactZone\license.bat"; Parameters: "{%HOMEPATH}\.kettle\tmp\mdProps.prop {code:WriteAppDataPathFile|{app}\ContactZone\czdatapath.txt}"; Flags: runhidden
Filename: "{app}\ContactZone\license.bat"; Parameters: "{%HOMEPATH}\.kettle\tmp\mdProps.prop {code:Write_psexe_PathFile|{app}\ContactZone\psexepath.txt}"; Flags: runhidden
Filename: "{app}\ContactZone\license.bat"; Parameters: "{%HOMEPATH}\.kettle\tmp\mdProps.prop {code:GetLicense|{app}\ContactZone\licenseString.txt}"; Flags: runhidden
Filename: "{app}\ContactZone\license.bat"; Parameters: "{%HOMEPATH}\.kettle\tmp\mdProps.prop {code:GetPSLicense|{app}\ContactZone\PSlicenseString.txt}"; Flags: runhidden
Filename: "{app}\ContactZone\license.bat"; Parameters: "{%HOMEPATH}\.kettle\tmp\mdProps.prop {code:GetPresortPath|{app}\ContactZone\PSPathString.txt}"; Flags: runhidden
Filename: "{app}\ContactZone\license.bat"; Parameters: "{%HOMEPATH}\.kettle\tmp\mdProps.prop {code:GetPath|{app}\ContactZone\PathString.txt}"; Flags: runhidden
Filename: "{app}\ContactZone\license.bat"; Parameters: "{%HOMEPATH}\.kettle\tmp\mdProps.prop {code:GetDQTPath|{app}\ContactZone\DQTPathString.txt}"; Flags: runhidden
Filename: "{app}\ContactZone\license.bat"; Parameters: "{%HOMEPATH}\.kettle\tmp\mdProps.prop {code:CreateReportingPath|{app}\ContactZone\CVReportingPathString.txt}"; Flags: runhidden
Filename: "{app}\ContactZone\license.bat"; Parameters: "{%HOMEPATH}\.kettle\tmp\mdProps.prop {code:CreateGAReportingPath|{app}\ContactZone\GAReportingPathString.txt}"; Flags: runhidden

Filename: "{app}\ContactZone\combine.exe"; Parameters: "/a ""{app}\ContactZone\simple-jndi\ga.properties"" ""{app}\ContactZone\GAReportingPathString.txt"""; Flags: runhidden
Filename: "{app}\ContactZone\combine.exe"; Parameters: "/a ""{app}\ContactZone\simple-jndi\cz.properties"" ""{app}\ContactZone\CVReportingPathString.txt"""; Flags: runhidden
Filename: "{app}\ContactZone\combine.exe"; Parameters: "/a ""{%HOMEPATH}\.kettle\tmp\mdProps.prop"" ""{app}\ContactZone\DQTPathString.txt"""; Flags: runhidden
Filename: "{app}\ContactZone\combine.exe"; Parameters: "/a ""{%HOMEPATH}\.kettle\tmp\mdProps.prop"" ""{app}\ContactZone\PSPathString.txt"""; Flags: runhidden
Filename: "{app}\ContactZone\combine.exe"; Parameters: "/a ""{%HOMEPATH}\.kettle\tmp\mdProps.prop"" ""{app}\ContactZone\PSlicenseString.txt"""; Flags: runhidden
Filename: "{app}\ContactZone\combine.exe"; Parameters: "/a ""{%HOMEPATH}\.kettle\tmp\mdProps.prop"" ""{app}\ContactZone\PathString.txt"""; Flags: runhidden 
Filename: "{app}\ContactZone\combine.exe"; Parameters: "/a ""{%HOMEPATH}\.kettle\tmp\mdProps.prop"" ""{app}\ContactZone\licenseString.txt"""; AfterInstall: MyAfterInstall; Flags: runhidden
Filename: "{app}\ContactZone\combine.exe"; Parameters: "/a ""{%HOMEPATH}\.kettle\tmp\mdProps.prop"" ""{%HOMEPATH}\.kettle\tmp\mdProps.prop"""; Flags: runhidden

;Install C++ runtime redistributables
Filename: {tmp}\vcredist_x86.exe; Parameters: "/passive /norestart   /c:""msiexec /q:a /i vcredist.msi"" "; StatusMsg: Installing 2012 RunTime... ; Flags: runhidden unchecked ;
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


;Clear up from older install if any
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
Type: filesandordirs; Name:"{app}\ContactZone\md"
Type: filesandordirs; Name:"{app}\ContactZone\libext"
Type: filesandordirs; Name:"{app}\ContactZone\Data Integration 32-bit.app"
Type: filesandordirs; Name:"{app}\ContactZone\Data Integration 64-bit.app"
Type: filesandordirs; Name:"{app}\ContactZone\launcher"
;Type: files; Name: "{app}\ContactZone\plugins\MDPresort\MDPresort*.jar"
Type: filesandordirs; Name: "{app}\ContactZone\ui"
Type: files; Name: "{app}\ContactZone\lib\*.jar"
Type: files; Name: "{app}\ContactZone\lib\*.zip"
Type: files; Name: "{app}\ContactZone\libswt\*.jar"
Type: files; Name: "{app}\ContactZone\libswt\*.zip"
Type: files; Name:"{%HOMEPATH}\.kettle\tmp\*.jar"
;Type: files; Name: "{app}\unins000.exe"

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
;Type: filesandordirs; Name:"{app}\ContactZone\**\*"

Type: filesandordirs; Name:"{%HOMEPATH}\.kettle\tmp"
Type: filesandordirs; Name:"{%HOMEPATH}\.kettle\matchup"
Type: filesandordirs; Name:"{%HOMEPATH}\.kettle\MD"
Type: filesandordirs; Name:"{%HOMEPATH}\.kettle\reports"
Type: files; Name:"{%HOMEPATH}\.kettle\md*"


[Messages]
;SelectDirBrowseLabel=Installing to other directories (like C:\Program Files�) may cause permission problems for users not running as Administrator
SelectDirBrowseLabel=To change the location of your install please select a location below
ConfirmUninstall=Are you sure you want to completely remove %1 and all of its samples and data?

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




function LicenseProvided(): Boolean;
begin
	if Length(LicensePage.Values[0])>0 then
		Result := True
	else
		Result := False;
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
//  S2 := '\mdFiles\Data';
   S2 := '\Data';
  S3 := '#Primary DQT Data Path';

//  S4 := S+ExpandConstant('{commonappdata}')+S2+#13#10;
S4 := S+ExpandConstant('{app}')+S2+#13#10;

  count := StringChangeEx(S4,'\','\\',True);
//  SaveStringToFile(Param, S3+ #13#10 + S + ExpandConstant('{userdocs}')+S2+#13#10, false);
//  SaveStringToFile(Param, S3+ #13#10 + S + ExpandConstant('{%USERPROFILE|{userdocs}}') +S2+#13#10, false);
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

 
  S3 := S+ExpandConstant('{%HOMEPATH}')+S2+#13#10;
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

 
  S3 := S+ExpandConstant('{%HOMEPATH}')+S2+#13#10;
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

  S6 := S5+ExpandConstant('{%HOMEPATH}')+#13#10;
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

begin
	
  S := ExpandConstant('{%HOMEPATH}');
  S2 := '\.kettle'
  SaveStringToFile(Param, S+S2, false);
  Result := LicensePage.Values[0]; 

end;

function Write_psexe_PathFile(Param: String): String;

var S : String;
var S2 : String; 

begin
	
  S := ExpandConstant('{app}');
  S2 := '\ContactZone\plugins\MDPresort'
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


function NextButtonClickXX(PageId: Integer): Boolean;
var
 FindRec: TFindRec;
 isAdmin: String;
 isPower: String;
begin
    Result := True;

    if IsAdminLoggedOn then
    begin
        isAdmin := 'Admin User';
    end
      else
    begin
        isAdmin := 'NO';
    end

     if IsPowerUserLoggedOn then
    begin
        isPower := 'Power User';
    end
      else
    begin
        isPower := 'NO Powerr';
    end
    

//    isAdmin := IsAdminLoggedOn();
//    isPower := IsPowerUserLoggedOn();

     if (PageId = wpSelectDir) then 
      begin
      //  Log(' - - Looking For : plugins');
          MsgBox('Are you an administrator ?? ' + isAdmin + #13#10#13#10 + 'Are you an Power ?? ' + ExpandConstant('{%TEMP}'), mbError, MB_OK);
          Result := True;
        end
     else
      begin
       // Log(' - -NOT Found : ');
     //   MsgBox('YourApp does not seem to be installed in that folder.  Please select the correct folder.', mbError, MB_OK);
        Result := True;
       // exit;
      end
    end;
//end;

 

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

end;