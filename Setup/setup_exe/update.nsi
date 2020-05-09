; Script generated by the HM NIS Edit Script Wizard.
!define MULTIUSER_INSTALLMODE_COMMANDLINE
!include "MultiUser.nsh"


; HM NIS Edit Wizard helper defines
!define PRODUCT_NAME "MFG System Update"
!define PRODUCT_VERSION "1.0"
!define PRODUCT_PUBLISHER "Market Forecast Group"
!define PRODUCT_WEB_SITE "http://www.marketforecastgroup.com"
!define PRODUCT_UNINST_KEY "Software\Microsoft\Windows\CurrentVersion\Uninstall\${PRODUCT_NAME}"
!define PRODUCT_UNINST_ROOT_KEY "SHCTX"
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

; Finish page
!define MUI_FINISHPAGE_RUN "$INSTDIR\MFG\MFG.exe"
!insertmacro MUI_PAGE_FINISH
; Language files
!insertmacro MUI_LANGUAGE "English"

; MUI end ------

Name "${PRODUCT_NAME} ${PRODUCT_VERSION}"
OutFile "MFGUpdate.exe"
InstallDir "C:\MFG System"
ShowInstDetails show
RequestExecutionLevel user

Section "MainSection" SEC01
  SetOutPath "$INSTDIR"
  SetOverwrite on

  ; Remove MFG plugins
  Delete "MFG\plugins\com.mfg.*"
  Delete "MFG\plugins\org.mfg.*"
  Delete "MFG\plugins\com.marketforecastgroup.*"
  Delete "MFG\plugins\com.marketforescastgroup.*"
  
  ; Remove MFG features
  ; Only delete the first mfg feature it founds, for now it is ok.
  FindFirst $0 $1 "MFG\features\com.mfg.*"
  RMDir /r "$INSTDIR\MFG\features\$1"
  FindClose $0

  ; Install new files here
  
SectionEnd