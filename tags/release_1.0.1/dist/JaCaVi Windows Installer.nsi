;JaCaVi v1.0.0 Windows Installation script
;
;compile using NSIS 2.40 (http://nsis.sourceforge.net/) with Modern UI 2 installed

;--------------------------------
;Constants

  !define APPLICATION_LONG "Java Carrera Visualization"
  !define APPLICATION_SHORT "JaCaVi"
  !define APPLICATION_VERSION "1.0.0"
  !define APPLICATION_DESCRIPTION "JaCaVi is a slot car control and visualization application.$\r$\n$\r$\nPlease visit http://www.jacavi.de/ for more in-depth information and updates."
  
  !define REGKEY_INSTDIR "Software\${APPLICATION_SHORT}"

  !ifdef OUTPUT_FILE
    !define APPLICATION_INSTALLER_FILENAME "${OUTPUT_FILE}"
  !else
    !define APPLICATION_INSTALLER_FILENAME "${APPLICATION_SHORT}_v${APPLICATION_VERSION}_manual_build.exe"
  !endif

;--------------------------------
;Include Modern UI

  !include "MUI2.nsh"

;--------------------------------
;General

  ;Name and file
  Name "${APPLICATION_SHORT} v${APPLICATION_VERSION}"
  OutFile "${APPLICATION_INSTALLER_FILENAME}"

  ;Default installation folder
  InstallDir "$PROGRAMFILES\${APPLICATION_SHORT} ${APPLICATION_VERSION}"
  
  ;Get installation folder from registry if available
  InstallDirRegKey HKCU "${REGKEY_INSTDIR}" ""

  ;Request application privileges for Windows Vista
  RequestExecutionLevel user

;--------------------------------
;Interface Configuration

  !define MUI_HEADERIMAGE
  !define MUI_HEADERIMAGE_BITMAP "header.bmp"
  !define MUI_ABORTWARNING
  !define MUI_WELCOMEFINISHPAGE_BITMAP "welcome.bmp"
  !define MUI_UNWELCOMEFINISHPAGE_BITMAP "welcome.bmp"
  !define MUI_FINISHPAGE_NOAUTOCLOSE
  !define MUI_UNFINISHPAGE_NOAUTOCLOSE

;--------------------------------
;Variables

  Var StartMenuFolder

;--------------------------------
;Pages

  ;Welcome page
  !define MUI_WELCOMEPAGE_TEXT "This wizard will guide you through the installation of ${APPLICATION_SHORT} v${APPLICATION_VERSION}.$\n$\n${APPLICATION_DESCRIPTION}$\n$\nClick Next to continue."
  !insertmacro MUI_PAGE_WELCOME

  ;Components page
  !insertmacro MUI_PAGE_COMPONENTS
  
  ;Directory page
  !insertmacro MUI_PAGE_DIRECTORY
  
  ;Start Menu Folder Configuration Page
  !define MUI_STARTMENUPAGE_REGISTRY_ROOT "HKCU" 
  !define MUI_STARTMENUPAGE_REGISTRY_KEY "${REGKEY_INSTDIR}" 
  !define MUI_STARTMENUPAGE_REGISTRY_VALUENAME "Start Menu Folder"
  !insertmacro MUI_PAGE_STARTMENU Application $StartMenuFolder
  
  ;Files page
  !insertmacro MUI_PAGE_INSTFILES
  
  ;Finish page
  !define MUI_FINISHPAGE_RUN "$INSTDIR\JaCaVi.exe"
  !define MUI_FINISHPAGE_SHOWREADME "$INSTDIR\readme.txt"
  !define MUI_FINISHPAGE_LINK "Visit the JaCaVi website for updates and news"
  !define MUI_FINISHPAGE_LINK_LOCATION "http://www.jacavi.de/"
  !insertmacro MUI_PAGE_FINISH

;--------------------------------
;Uninstaller Pages
  
  !insertmacro MUI_UNPAGE_WELCOME
  !insertmacro MUI_UNPAGE_COMPONENTS
  !insertmacro MUI_UNPAGE_CONFIRM
  !insertmacro MUI_UNPAGE_INSTFILES
  !insertmacro MUI_UNPAGE_FINISH

;--------------------------------
;Languages
 
  !insertmacro MUI_LANGUAGE "English"

;--------------------------------
;Installer Sections

Section "!JaCaVi Core" SectionCore

  SectionIn RO    ; make this section mandatory
  SetOutPath "$INSTDIR"
  
  ; files to install
  File bin\win32.win32.x86\jacavi\*
  File /r bin\win32.win32.x86\jacavi\configuration
  File /r bin\win32.win32.x86\jacavi\plugins
  
  ; data files
  CreateDirectory $INSTDIR\agents
  File /r bin\win32.win32.x86\jacavi\cars
  File /r bin\win32.win32.x86\jacavi\tiles
  File /r bin\win32.win32.x86\jacavi\tracks
  
  ;Store installation folder
  WriteRegStr HKCU "${REGKEY_INSTDIR}" "" $INSTDIR
  
  ;Create uninstaller
  WriteUninstaller "$INSTDIR\Uninstall.exe"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${APPLICATION_SHORT}" "DisplayName" "${APPLICATION_SHORT} v${APPLICATION_VERSION}"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${APPLICATION_SHORT}" "UninstallString" "$\"$INSTDIR\Uninstall.exe$\""
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${APPLICATION_SHORT}" "DisplayVersion" "${APPLICATION_VERSION}"
  WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${APPLICATION_SHORT}" "NoModify" "1"
  WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${APPLICATION_SHORT}" "NoRepair" "1"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${APPLICATION_SHORT}" "Publisher" "JaCaVi Team"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${APPLICATION_SHORT}" "URLUpdateInfo" "http://www.jacavi.de/"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${APPLICATION_SHORT}" "URLInfoAbout" "http://www.jacavi.de/"
  
  !insertmacro MUI_STARTMENU_WRITE_BEGIN Application
    
    ;Create shortcuts
    CreateDirectory "$SMPROGRAMS\$StartMenuFolder"
    CreateShortCut "$SMPROGRAMS\$StartMenuFolder\Uninstall.lnk" "$INSTDIR\Uninstall.exe"
    CreateShortCut "$SMPROGRAMS\$StartMenuFolder\${APPLICATION_SHORT} v${APPLICATION_VERSION}.lnk" "$INSTDIR\JaCaVi.exe"
  
  !insertmacro MUI_STARTMENU_WRITE_END

SectionEnd

Section "Sample driving agents" SectionAgents

  SetOutPath "$INSTDIR"

  File /r bin\win32.win32.x86\jacavi\agents

SectionEnd

;--------------------------------
;Descriptions

!insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
  !insertmacro MUI_DESCRIPTION_TEXT ${SectionCore} "The JaCaVi executable and the core files."
  !insertmacro MUI_DESCRIPTION_TEXT ${SectionAgents} "Some sample driving agents written in Jython and Groovy."
!insertmacro MUI_FUNCTION_DESCRIPTION_END

;--------------------------------
;Uninstaller Section

Section "!un.JaCaVi Core" UnsectionCore

  SectionIn RO    ; make this section mandatory

  RMDir /r "$INSTDIR\configuration"
  RMDir /r "$INSTDIR\plugins"
  RMDir /r "$INSTDIR\workspace"
  RMDir /r "$INSTDIR\libs"
  Delete "$INSTDIR\.eclipseproduct"
  Delete "$INSTDIR\JaCaVi.exe"
  Delete "$INSTDIR\JaCaVi.ini"
  Delete "$INSTDIR\jacavi.log"
  Delete "$INSTDIR\readme.txt"

  !insertmacro MUI_STARTMENU_GETFOLDER Application $StartMenuFolder

  Delete "$SMPROGRAMS\$StartMenuFolder\Uninstall.lnk"
  Delete "$SMPROGRAMS\$StartMenuFolder\${APPLICATION_SHORT} v${APPLICATION_VERSION}.lnk"
  RMDir "$SMPROGRAMS\$StartMenuFolder"
  
  DeleteRegKey /ifempty HKCU "${REGKEY_INSTDIR}"

SectionEnd

Section /o "un.User Data" UnsectionUserData

  RMDir /r "$INSTDIR\agents"
  RMDir /r "$INSTDIR\cars"
  RMDir /r "$INSTDIR\tiles"
  RMDir /r "$INSTDIR\tracks"

SectionEnd

Section "-un.Uninstall"

  Delete "$INSTDIR\Uninstall.exe"
  DeleteRegKey HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${APPLICATION_SHORT}"
 
  RMDir "$INSTDIR"
  
SectionEnd

;--------------------------------
;Descriptions

!insertmacro MUI_UNFUNCTION_DESCRIPTION_BEGIN
  !insertmacro MUI_DESCRIPTION_TEXT ${UnsectionCore} "The JaCaVi executable and the core files."
  !insertmacro MUI_DESCRIPTION_TEXT ${UnsectionUserData} "Data that could have been modified by the user. (/agents, /cars, /tiles and /tracks)"
!insertmacro MUI_UNFUNCTION_DESCRIPTION_END
