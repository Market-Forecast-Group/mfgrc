; Script generated by the HM NIS Edit Script Wizard.
!define MULTIUSER_INSTALLMODE_COMMANDLINE
!include "MultiUser.nsh"


; HM NIS Edit Wizard helper defines
!define PRODUCT_NAME "MFG System Check For Updates"
!define PRODUCT_VERSION "1.0"
!define PRODUCT_PUBLISHER "Market Forecast Group"
!define PRODUCT_WEB_SITE "http://www.marketforecastgroup.com"
!define PRODUCT_ROOT_KEY "SHCTX"
!define PRODUCT_KEY "Software\MFG_System"

; MUI 1.67 compatible ------
!include "MUI.nsh"

; MUI Settings
!define MUI_ABORTWARNING
!define MUI_ICON "${NSISDIR}\Contrib\Graphics\Icons\modern-install.ico"

; Welcome page
!insertmacro MUI_PAGE_WELCOME
; License page
!insertmacro MUI_PAGE_LICENSE "..\_resources\lic.txt"
; Instfiles page
!insertmacro MUI_PAGE_INSTFILES
; Language files
!insertmacro MUI_LANGUAGE "English"

; MUI end ------

Name "${PRODUCT_NAME} ${PRODUCT_VERSION}"
OutFile "check-update.exe"
InstallDir "C:\MFG System"
ShowInstDetails show
RequestExecutionLevel user

Section "MainSection" SEC01
  SetOutPath "$INSTDIR"
  SetOverwrite on
  ;ReadRegStr $0 PRODUCT_ROOT_KEY "SHCTX" "${PRODUCT_KEY}" "Version"
  inetc::get /caption "Update info download" /popup "MFG Update Server" "ftp://mfg:positiveMfg99_@ftp.marketforecastgroup.com/MFGBuild/Setup/info.ini" "$INSTDIR\info.ini"
  ReadINIStr $0 $INSTDIR\info.ini Version BuildVersion
  MessageBox MB_OK $0
  inetc::get /caption "Update download" /popup "MFG Update Server" "ftp://mfg:positiveMfg99_@ftp.marketforecastgroup.com/MFGBuild/Setup/update.exe" "$INSTDIR\update.exe"

  Exec "$INSTDIR\update.exe"
  
  SetAutoClose true
SectionEnd