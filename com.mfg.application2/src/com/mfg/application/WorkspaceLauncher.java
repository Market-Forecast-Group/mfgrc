package com.mfg.application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.AppResourceManager;

public class WorkspaceLauncher extends TitleAreaDialog {

	public static final String WS_IDENTIFIER = ".rcp_workspace";

	private static final String KEY_WORKSPACE_ROOT_DIR = "wsRootDir";
	private static final String KEY_REMEMBER_WORKSPACE = "wsRemember";
	private static final String KEY_LAST_USED_WORKSPACES = "wsLastUsedWorkspaces";

	private static Preferences PREFERENCES = Preferences
			.userNodeForPackage(WorkspaceLauncher.class);

	private static final String STR_MSG = "Your workspace is where settings and various important files will be stored.";
	private static final String STR_INFO = "Please select a directory that will be the workspace root";
	private static final String STR_ERROR = "You must set a directory";

	Combo workspacePathCombo;
	private List<String> lastUsedWorkspaces;
	private Button rememberWorkspaceButton;

	private static final String SPLIT_CHAR = "#";

	private static final int MAX_HISTORY = 20;

	private final boolean switchWorkspace;

	private String selectedWorkspaceRootLocation;

	public WorkspaceLauncher(boolean switchWorkspace1, Image wizardImage) {
		super(Display.getDefault().getActiveShell());
		this.switchWorkspace = switchWorkspace1;
		Image pluginImage = wizardImage;
		pluginImage = AppResourceManager.getPluginImage("com.mfg.application",
				"icons/wks_icon1.jpg");
		if (pluginImage != null) {
			setTitleImage(pluginImage);
		}
	}

	@Override
	protected void configureShell(Shell newShell) {
		newShell.setImage(AppResourceManager.getPluginImage(
				"com.mfg.application", "icons/wks_icon2.ico"));
		super.configureShell(newShell);
		if (switchWorkspace) {
			newShell.setText("Switch Workspace");
		} else {
			newShell.setText("Workspace Selection");
		}
	}

	public static boolean isRememberWorkspace() {
		return PREFERENCES.getBoolean(KEY_REMEMBER_WORKSPACE, false);
	}

	public static void setRememberWorkspace(final boolean remember) {
		PREFERENCES.putBoolean(KEY_REMEMBER_WORKSPACE, remember);
	}

	public static String getLastSetWorkspaceDirectory() {
		return PREFERENCES.get(KEY_WORKSPACE_ROOT_DIR, null);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		setTitleImage(AppResourceManager.getPluginImage("com.mfg.application",
				"icons/wks_icon1.jpg"));

		setTitle("Workspace Launcher");
		setMessage(STR_MSG);
		try {
			Composite inner = new Composite(parent, SWT.NONE);
			inner.setLayoutData(new GridData(GridData.FILL_VERTICAL
					| GridData.VERTICAL_ALIGN_END | GridData.GRAB_HORIZONTAL));
			inner.setLayout(new GridLayout(3, false));
			new Label(inner, SWT.NONE).setText("");
			new Label(inner, SWT.NONE).setText("");
			new Label(inner, SWT.NONE).setText("");

			CLabel lblWorkspace = new CLabel(inner, SWT.NONE);
			lblWorkspace.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false,
					false, 1, 1));
			lblWorkspace.setText("Workspace");
			workspacePathCombo = new Combo(inner, SWT.BORDER);
			GridData gd__workspacePathCombo = new GridData(SWT.FILL, SWT.FILL,
					false, false, 1, 1);
			gd__workspacePathCombo.widthHint = 390;
			workspacePathCombo.setLayoutData(gd__workspacePathCombo);
			workspacePathCombo.setVisibleItemCount(30);
			String wsRoot = PREFERENCES.get(KEY_WORKSPACE_ROOT_DIR, "");
			if (wsRoot == null || wsRoot.length() == 0) {
				wsRoot = getWorkspacePathSuggestion();
			}
			workspacePathCombo.setText(wsRoot == null ? "" : wsRoot);

			Button browse = new Button(inner, SWT.PUSH);
			GridData gd_browse = new GridData(SWT.LEFT, SWT.CENTER, false,
					false, 1, 1);
			gd_browse.widthHint = 100;
			browse.setLayoutData(gd_browse);
			browse.setText("Browse...");
			browse.addListener(SWT.Selection, new Listener() {

				@SuppressWarnings("synthetic-access")
				@Override
				public void handleEvent(Event event) {
					DirectoryDialog dd = new DirectoryDialog(getParentShell());
					dd.setText("Select Workspace Root");
					dd.setMessage(STR_INFO);
					dd.setFilterPath(workspacePathCombo.getText());
					String pick = dd.open();
					if (pick == null
							&& workspacePathCombo.getText().length() == 0) {
						setMessage(STR_ERROR, IMessageProvider.ERROR);
					} else {
						setMessage(STR_MSG);
						workspacePathCombo.setText(pick);
					}
				}

			});
			new Label(inner, SWT.NONE).setText("");

			String lastUsed = PREFERENCES.get(KEY_LAST_USED_WORKSPACES, "");
			lastUsedWorkspaces = new ArrayList<>();
			if (lastUsed != null) {
				String[] all = lastUsed.split(SPLIT_CHAR);
				for (String str : all)
					lastUsedWorkspaces.add(str);
			}
			for (String last : lastUsedWorkspaces)
				workspacePathCombo.add(last);
			new Label(inner, SWT.NONE).setText("");
			new Label(inner, SWT.NONE).setText("");
			new Label(inner, SWT.NONE).setText("");

			new Label(inner, SWT.NONE).setText("");
			new Label(inner, SWT.NONE).setText("");
			new Label(inner, SWT.NONE).setText("");

			new Label(inner, SWT.NONE).setText("");
			new Label(inner, SWT.NONE).setText("");

			rememberWorkspaceButton = new Button(inner, SWT.CHECK);
			GridData gd__RememberWorkspaceButton = new GridData(SWT.LEFT,
					SWT.CENTER, false, false, 2, 1);
			gd__RememberWorkspaceButton.heightHint = 26;
			rememberWorkspaceButton.setLayoutData(gd__RememberWorkspaceButton);
			rememberWorkspaceButton
					.setText("Use this as the default and do not ask again");
			rememberWorkspaceButton.setSelection(PREFERENCES.getBoolean(
					KEY_REMEMBER_WORKSPACE, false));
			new Label(inner, SWT.NONE).setText("");

			return inner;
		} catch (Exception err) {
			err.printStackTrace();
			return null;
		}
	}

	public String getSelectedWorkspaceLocation() {
		return selectedWorkspaceRootLocation;
	}

	private static String getWorkspacePathSuggestion() {
		StringBuffer buf = new StringBuffer();

		String uHome = System.getProperty("user.home");
		if (uHome == null) {
			uHome = "c:" + File.separator + "temp";
		}

		buf.append(uHome);
		buf.append(File.separator);
		buf.append("MFG");
		buf.append("_Workspace");

		return buf.toString();
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		final Image image = AppResourceManager.getPluginImage(
				"com.mfg.application", "icons/wks_icon2.ico");
		Button clone = createButton(parent, IDialogConstants.IGNORE_ID,
				"Clone", false);
		clone.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				try {
					Display.getDefault().getActiveShell().setImage(image);
					String txt = workspacePathCombo.getText();
					File workspaceDirectory = new File(txt);
					if (!workspaceDirectory.exists()) {
						MessageDialog
								.openError(
										Display.getDefault().getActiveShell(),
										"Error",
										"The currently entered workspace path does not exist. Please enter a valid path.");
						return;
					}

					if (!workspaceDirectory.canRead()) {
						MessageDialog
								.openError(
										Display.getDefault().getActiveShell(),
										"Error",
										"The currently entered workspace path is not readable. Please check file system permissions.");
						return;
					}

					// check for workspace file (empty indicator that it's a
					// workspace)
					File wsFile = new File(txt + File.separator + WS_IDENTIFIER);
					if (!wsFile.exists()) {
						MessageDialog
								.openError(Display.getDefault()
										.getActiveShell(), "Error",
										"The currently entered workspace path does not contain a valid workspace.");
						return;
					}

					DirectoryDialog dd = new DirectoryDialog(Display
							.getDefault().getActiveShell());
					dd.setFilterPath(txt);
					String directory = dd.open();
					if (directory == null) {
						return;
					}

					File targetDirectory = new File(directory);
					if (targetDirectory.getAbsolutePath().equals(
							workspaceDirectory.getAbsolutePath())) {
						MessageDialog.openError(Display.getDefault()
								.getActiveShell(), "Error",
								"Source and target workspaces are the same");
						return;
					}

					// recursive check, if new directory is a subdirectory of
					// our workspace, that's a big no-no or we'll
					// create directories forever
					if (isTargetSubdirOfDir(workspaceDirectory, targetDirectory)) {
						MessageDialog
								.openError(Display.getDefault()
										.getActiveShell(), "Error",
										"Target folder is a subdirectory of the current workspace");
						return;
					}

					try {
						copyFiles(workspaceDirectory, targetDirectory);
					} catch (Exception err) {
						MessageDialog.openError(Display.getDefault()
								.getActiveShell(), "Error",
								"There was an error cloning the workspace: "
										+ err.getMessage());
						return;
					}

					boolean setActive = MessageDialog
							.openConfirm(Display.getDefault().getActiveShell(),
									"Workspace Cloned",
									"Would you like to set the newly cloned workspace to be the active one?");
					if (setActive) {
						workspacePathCombo.setText(directory);
					}
				} catch (Exception err) {
					MessageDialog
							.openError(Display.getDefault().getActiveShell(),
									"Error",
									"There was an internal error, please check the logs");
					err.printStackTrace();
				}
			}
		});
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);

	}

	// checks whether a target directory is a subdirectory of ourselves
	boolean isTargetSubdirOfDir(File source, File target) {
		List<File> subdirs = new ArrayList<>();
		getAllSubdirectoriesOf(source, subdirs);
		return subdirs.contains(target);
	}

	// helper for above
	private void getAllSubdirectoriesOf(File target, List<File> buffer) {
		File[] files = target.listFiles();
		if (files == null || files.length == 0)
			return;

		for (File f : files) {
			if (f.isDirectory()) {
				buffer.add(f);
				getAllSubdirectoriesOf(f, buffer);
			}
		}
	}

	/**
	 * This function will copy files or directories from one location to
	 * another. note that the source and the destination must be mutually
	 * exclusive. This function can not be used to copy a directory to a sub
	 * directory of itself. The function will also have problems if the
	 * destination files already exist.
	 * 
	 * @param src
	 *            -- A File object that represents the source for the copy
	 * @param dest
	 *            -- A File object that represents the destination for the copy.
	 * @throws IOException
	 *             if unable to copy.
	 */
	public static void copyFiles(File src, File dest) throws IOException {
		// Check to ensure that the source is valid...
		if (!src.exists()) {
			throw new IOException("Can not find source: "
					+ src.getAbsolutePath());
		} else if (!src.canRead()) { // check to ensure we have rights to the
										// source...
			throw new IOException("Cannot read: " + src.getAbsolutePath()
					+ ". Check file permissions.");
		}
		// is this a directory copy?
		if (src.isDirectory()) {
			if (!dest.exists()) { // does the destination already exist?
				// if not we need to make it exist if possible (note this is
				// mkdirs not mkdir)
				if (!dest.mkdirs()) {
					throw new IOException("Could not create direcotry: "
							+ dest.getAbsolutePath());
				}
			}
			// get a listing of files...
			String list[] = src.list();
			// copy all the files in the list.
			for (int i = 0; i < list.length; i++) {
				File dest1 = new File(dest, list[i]);
				File src1 = new File(src, list[i]);
				copyFiles(src1, dest1);
			}
		} else {
			// This was not a directory, so lets just copy the file
			byte[] buffer = new byte[4096]; // Buffer 4K at a time (you can
											// change this).
			int bytesRead;
			try (FileInputStream fin = new FileInputStream(src);
					FileOutputStream fout = new FileOutputStream(dest)) {
				// while bytesRead indicates a successful read, lets write...
				while ((bytesRead = fin.read(buffer)) >= 0) {
					fout.write(buffer, 0, bytesRead);
				}
			} catch (IOException e) { // Error copying file...
				IOException wrapper = new IOException("Unable to copy file: "
						+ src.getAbsolutePath() + "to" + dest.getAbsolutePath());
				wrapper.initCause(e);
				wrapper.setStackTrace(e.getStackTrace());
				throw wrapper;
			}
		}
	}

	@Override
	protected void okPressed() {
		String str = workspacePathCombo.getText();

		if (str.length() == 0) {
			setMessage(STR_ERROR, IMessageProvider.ERROR);
			return;
		}

		String ret = checkWorkspaceDirectory(getParentShell(), str, true, true);
		if (ret != null) {
			setMessage(ret, IMessageProvider.ERROR);
			return;
		}

		// save it so we can show it in combo later
		lastUsedWorkspaces.remove(str);

		if (!lastUsedWorkspaces.contains(str)) {
			lastUsedWorkspaces.add(0, str);
		}

		// deal with the max history
		if (lastUsedWorkspaces.size() > MAX_HISTORY) {
			List<String> remove = new ArrayList<>();
			for (int i = MAX_HISTORY; i < lastUsedWorkspaces.size(); i++) {
				remove.add(lastUsedWorkspaces.get(i));
			}

			lastUsedWorkspaces.removeAll(remove);
		}

		// create a string concatenation of all our last used workspaces
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < lastUsedWorkspaces.size(); i++) {
			buf.append(lastUsedWorkspaces.get(i));
			if (i != lastUsedWorkspaces.size() - 1) {
				buf.append(SPLIT_CHAR);
			}
		}

		// save them onto our preferences
		PREFERENCES.putBoolean(KEY_REMEMBER_WORKSPACE,
				rememberWorkspaceButton.getSelection());
		PREFERENCES.put(KEY_LAST_USED_WORKSPACES, buf.toString());

		// now create it
		boolean ok = checkAndCreateWorkspaceRoot(str);
		if (!ok) {
			setMessage("The workspace could not be created, please check the error log");
			return;
		}

		// here we set the location so that we can later fetch it again
		selectedWorkspaceRootLocation = str;

		// and on our preferences as well
		PREFERENCES.put(KEY_WORKSPACE_ROOT_DIR, str);

		super.okPressed();
	}

	/**
	 * Ensures a workspace directory is OK in regards of reading/writing, etc.
	 * This method will get called externally as well.
	 * 
	 * @param parentShell
	 *            Shell parent shell
	 * @param workspaceLocation
	 *            Directory the user wants to use
	 * @param askCreate
	 *            Whether to ask if to create the workspace or not in this
	 *            location if it does not exist already
	 * @param fromDialog
	 *            Whether this method was called from our dialog or from
	 *            somewhere else just to check a location
	 * @return null if everything is ok, or an error message if not
	 */
	public static String checkWorkspaceDirectory(Shell parentShell,
			String workspaceLocation, boolean askCreate, boolean fromDialog) {
		File f = new File(workspaceLocation);
		if (!f.exists()) {
			if (askCreate) {
				boolean create = MessageDialog
						.openConfirm(parentShell, "New Directory",
								"The directory does not exist. Would you like to create it?");
				if (create) {
					try {
						f.mkdirs();
						File wsDot = new File(workspaceLocation
								+ File.separator + WS_IDENTIFIER);
						wsDot.createNewFile();
					} catch (Exception err) {
						return "Error creating directories, please check folder permissions";
					}
				}

				if (!f.exists()) {
					return "The selected directory does not exist";
				}
			}
		}

		if (!f.canRead()) {
			return "The selected directory is not readable";
		}

		if (!f.isDirectory()) {
			return "The selected path is not a directory";
		}

		File wsTest = new File(workspaceLocation + File.separator
				+ WS_IDENTIFIER);
		if (fromDialog) {
			if (!wsTest.exists()) {
				boolean create = MessageDialog
						.openConfirm(
								parentShell,
								"New Workspace",
								"The directory '"
										+ wsTest.getAbsolutePath()
										+ "' is not set to be a workspace. Do note that files will be created directly under the specified directory and it is suggested you create a directory that has a name that represents your workspace. \n\nWould you like to create a workspace in the selected location?");
				if (create) {
					try {
						f.mkdirs();
						File wsDot = new File(workspaceLocation
								+ File.separator + WS_IDENTIFIER);
						wsDot.createNewFile();
					} catch (Exception err) {
						return "Error creating directories, please check folder permissions";
					}
				} else {
					return "Please select a directory for your workspace";
				}

				if (!wsTest.exists()) {
					return "The selected directory does not exist";
				}

				return null;
			}
		} else {
			if (!wsTest.exists()) {
				return "The selected directory is not a workspace directory";
			}
		}

		return null;
	}

	/**
	 * Checks to see if a workspace exists at a given directory string, and if
	 * not, creates it. Also puts our identifying file inside that workspace.
	 * 
	 * @param wsRoot
	 *            Workspace root directory as string
	 * @return true if all checks and creations succeeded, false if there was a
	 *         problem
	 */
	public static boolean checkAndCreateWorkspaceRoot(String wsRoot) {
		try {
			File fRoot = new File(wsRoot);
			if (!fRoot.exists())
				return false;

			File dotFile = new File(wsRoot + File.separator
					+ WorkspaceLauncher.WS_IDENTIFIER);
			if (!dotFile.exists() && !dotFile.createNewFile())
				return false;

			return true;
		} catch (Exception err) {
			// as it might need to go to some other error log too
			err.printStackTrace();
			return false;
		}
	}

}