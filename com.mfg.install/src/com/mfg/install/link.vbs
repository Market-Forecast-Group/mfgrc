set shell = CreateObject("Wscript.shell")
set link = shell.CreateShortcut("$target$")
link.TargetPath = "$source$"
link.IconLocation = "$icon$"
link.Save()