package com.mfg.tea.conn;

import com.mfg.tea.accounting.MixedInventoriesFolder;

/**
 * This interface is used by the local tea and the stub tea because they share
 * the possibility to have a mixed folder.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
interface IServerSideTea {

	// /**
	// * After the tea has been registered this method tells the tea the mixed
	// * folder to use.
	// *
	// * @param aFolder
	// */
	// void setMixedFolder(MixedInventoriesFolder aFolder);

	/**
	 * @return the mixed inventory folder.
	 */
	MixedInventoriesFolder getMixedFolder();

}
