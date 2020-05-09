# name the installer
!define VERSION "1.0.0"

OutFile "MFG_Setup.exe"
Name "MFG System"

RequestExecutionLevel user

InstallDir "$PROFILE\MFG System"

Section
	CreateDirectory $INSTDIR
	SetOutPath $INSTDIR
	
	File /r MFG

	WriteUninstaller "$INSTDIR\uninstall.exe"
	CreateDirectory "$SMPROGRAMS\MFG System"
	CreateShortCut "$SMPROGRAMS\MFG System\MFG.lnk" "$INSTDIR\MFG\MFG.exe"
	; no update for now
	; CreateShortCut "$SMPROGRAMS\MFG System\Update.lnk" "$INSTDIR\MFG\update.exe"
	CreateShortCut "$SMPROGRAMS\MFG System\Uninstall.lnk" "$INSTDIR\uninstall.exe"
SectionEnd

Section "uninstall"
	RMDir /r "$SMPROGRAMS\MFG System"
	RMDir /r "$INSTDIR"
SectionEnd


/** 

TODO:

- Ask for install dir. 
- Launch MFG
- Uninstaller
- Shortcuts to MFG, DFS, MFG Update
- Check if MFG was installed before, then run the MFG Update.

 */