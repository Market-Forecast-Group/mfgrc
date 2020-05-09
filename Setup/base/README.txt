This is the base directory.

The base directory contains the core files of a version.
This is used to generate the update patches.

Folder structure:

MFG/ -- The base files of MFG, it is the Eclipse RCP core files (less the configuration folder).

The JRE, updater.exe, etc.. are part of a version, but they are not include in this folder because
they are never patched.

The configuration is not include here cause it contains runtime information. However, a setup.exe should contain a fresh configuration folder.