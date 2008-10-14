;JaCaVi v1.0 Windows Installation script
;
;using NSIS 2.40 (http://nsis.sourceforge.net/)

;To Do:
; - store start menu folder and uninstall the right one, not the default one
; - ask user whether to keep modifications (cars, tiles, tracks, etc.)

;--------------------------------
;Constants

  !define APPLICATION_LONG "Java Carrera Visualization"
  !define APPLICATION_SHORT "JaCaVi"
  !define APPLICATION_VERSION "1.0"
  !define APPLICATION_DESCRIPTION "JaCaVi is a ..."
  
  !define REGKEY_INSTDIR "Software\${APPLICATION_SHORT}"

;--------------------------------
;Include Modern UI

  !include "MUI2.nsh"

;--------------------------------
;General

  ;Name and file
  Name "${APPLICATION_SHORT} v${APPLICATION_VERSION}"
  OutFile "${APPLICATION_SHORT}_v${APPLICATION_VERSION}.exe"

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
  !insertmacro MUI_UNPAGE_CONFIRM
  !insertmacro MUI_UNPAGE_INSTFILES
  !insertmacro MUI_UNPAGE_FINISH

;--------------------------------
;Languages
 
  !insertmacro MUI_LANGUAGE "English"

;--------------------------------
;Installer Sections

Section "!JaCaVi Core" SectionCore

  SetOutPath "$INSTDIR"
  
  ;Files to install
  File /r bin\*
  File readme.txt
  
  ;Store installation folder
  WriteRegStr HKCU "${REGKEY_INSTDIR}" "" $INSTDIR
  
  ;Create uninstaller
  WriteUninstaller "$INSTDIR\Uninstall.exe"
  
  
  !insertmacro MUI_STARTMENU_WRITE_BEGIN Application
    
    ;Create shortcuts
    CreateDirectory "$SMPROGRAMS\$StartMenuFolder"
    CreateShortCut "$SMPROGRAMS\$StartMenuFolder\Uninstall.lnk" "$INSTDIR\Uninstall.exe"
    CreateShortCut "$SMPROGRAMS\$StartMenuFolder\JaCaVi.lnk" "$INSTDIR\JaCaVi.exe" "" "$INSTDIR\JaCaVi.ico"
  
  !insertmacro MUI_STARTMENU_WRITE_END

SectionEnd

Section "Sample driving agents" SectionAgents

  AddSize 20

  SetOutPath "$INSTDIR"
  
  ;ADD YOUR OWN FILES HERE...

SectionEnd

;--------------------------------
;Descriptions

!insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
  !insertmacro MUI_DESCRIPTION_TEXT ${SectionCore} "The JaCaVi executable and the core files."
  !insertmacro MUI_DESCRIPTION_TEXT ${SectionAgents} "Some sample driving agents written in Jython and Groovy."
!insertmacro MUI_FUNCTION_DESCRIPTION_END

;  ;Language strings
;  LangString DESC_SecDummy ${LANG_ENGLISH} "A test section."
;
;  ;Assign language strings to sections
;  !insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
;    !insertmacro MUI_DESCRIPTION_TEXT ${SecDummy} $(DESC_SecDummy)
;  !insertmacro MUI_FUNCTION_DESCRIPTION_END
 
;--------------------------------
;Uninstaller Section

Section "Uninstall"

  RMDir /r "$INSTDIR\configuration"
  RMDir /r "$INSTDIR\plugins"
  ;FIXME: really delete workspace?
  RMDir /r "$INSTDIR\workspace"
  Delete "$INSTDIR\.eclipseproduct"
  Delete "$INSTDIR\JaCaVi.exe"
  Delete "$INSTDIR\jacavi.ico"
  Delete "$INSTDIR\JaCaVi.ini"
  Delete "$INSTDIR\jacavi.log"
  Delete "$INSTDIR\readme.txt"

  Delete "$INSTDIR\Uninstall.exe"

  RMDir "$INSTDIR"
  
  !insertmacro MUI_STARTMENU_GETFOLDER Application $StartMenuFolder

  Delete "$SMPROGRAMS\$StartMenuFolder\Uninstall.lnk"
  Delete "$SMPROGRAMS\$StartMenuFolder\JaCaVi.lnk"
  RMDir "$SMPROGRAMS\$StartMenuFolder"
  
  DeleteRegKey /ifempty HKCU "${REGKEY_INSTDIR}"

SectionEnd