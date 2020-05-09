package com.mfg.build;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileFilter;

public class ConfigDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private String[] _keys;
	private HashMap<String, Component> _keyFieldMap;
	private boolean _accepted;
	private static Map<String, String> _keyLabel;
	private static Map<String, Class<?>> _keyType;

	static {
		_keyLabel = new HashMap<>();
		_keyLabel.put(Config.K_MERCURIAL_EXEC_PATH,
				"Mercurial Executable (hg.exe) Path");
		_keyLabel.put(Config.K_MFG_REPO_PATH, "MFG Repository Directory Path");
		_keyLabel.put(Config.K_DEBUG_INFO_AND_ASSERTIONS,
				"Enable Debug Info and Assertions");

		// by default, it is a File
		_keyType = new HashMap<>();
		_keyType.put(Config.K_DEBUG_INFO_AND_ASSERTIONS, Boolean.class);
	}

	/**
	 * Create the dialog.
	 */
	public ConfigDialog(String... keys) {
		super(AppWindow.getInstance(), true);
		_keys = keys;
		setTitle("Settings");
		setSize(600, 300);
		setLocationRelativeTo(getParent());
		getContentPane().setLayout(new BorderLayout(5, 5));

		_keyFieldMap = new HashMap<>();
		Config cfg = Config.getInstance();

		JPanel centerPanel = new JPanel(new GridBagLayout());
		getContentPane().add(centerPanel, BorderLayout.CENTER);
		centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(5, 5, 5, 5);
		c.anchor = GridBagConstraints.LINE_START;
		c.gridy = 1;
		c.weighty = 0;
		for (final String key : _keys) {
			Class<?> type = _keyType.get(key);
			Component comp;

			c.gridx = 0;
			c.fill = GridBagConstraints.NONE;
			c.weightx = 0;
			centerPanel.add(new JLabel(_keyLabel.get(key)), c);

			c.gridx++;
			c.fill = GridBagConstraints.BOTH;
			c.weightx = 1;

			if (type == Boolean.class) {
				JCheckBox check = new JCheckBox();
				check.setSelected(Boolean.parseBoolean(cfg.get(key)));
				comp = check;
			} else {
				JTextField field = new JTextField();
				field.setText(cfg.get(key));
				comp = field;
			}
			centerPanel.add(comp, c);
			_keyFieldMap.put(key, comp);

			c.gridx++;
			c.fill = GridBagConstraints.NONE;
			c.weightx = 0;

			if (type == null) {
				AbstractAction a = new AbstractAction("Browse") {

					private static final long serialVersionUID = 1L;

					@Override
					public void actionPerformed(ActionEvent e) {
						browseFile(key);
					}
				};
				comp = new JButton(a);
			} else {
				comp = new JLabel("");
			}
			centerPanel.add(comp, c);
			c.gridy++;
		}
		c.weightx = 0;
		c.weighty = 1;
		centerPanel.add(new JLabel(""), c);

		{
			JPanel buttonPane = new JPanel();
			buttonPane.setBorder(BorderFactory
					.createEmptyBorder(10, 10, 10, 10));
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				buttonPane.add(new JButton(new AbstractAction("Cancel") {

					private static final long serialVersionUID = 1L;

					@Override
					public void actionPerformed(ActionEvent e) {
						cancelPressed();
					}
				}));
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						okPressed();
					}
				});
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
		}

	}

	protected void cancelPressed() {
		_accepted = false;
		setVisible(false);
	}

	protected void okPressed() {
		Config cfg = Config.getInstance();
		for (String key : _keys) {
			Component comp = _keyFieldMap.get(key);
			String value;
			if (comp instanceof JCheckBox) {
				value = Boolean.toString(((JCheckBox) comp).isSelected());
			} else {
				value = ((JTextField) comp).getText();
			}
			cfg.put(key, value);
		}
		cfg.save();

		if (cfg.isValid(_keys)) {
			_accepted = true;
			setVisible(false);
		}
	}

	static class ExecFileFilter extends FileFilter {
		private String _description;
		private String _execName;

		public ExecFileFilter(String execName, String description) {
			super();
			_execName = execName;
			_description = description;
		}

		@Override
		public boolean accept(File pathname) {
			Path p = pathname.toPath();
			return Files.isDirectory(p)
					|| p.getFileName().toString().equals(_execName);
		}

		@Override
		public String getDescription() {
			return _description;
		}
	}

	protected void browseFile(String key) {
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogType(JFileChooser.OPEN_DIALOG);

		switch (key) {
		case Config.K_MFG_REPO_PATH:
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			break;
		case Config.K_MERCURIAL_EXEC_PATH:
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			chooser.setFileFilter(new ExecFileFilter(Config
					.getMercurialExecName(), "Mecurial Executable ("
					+ Config.getMercurialExecName() + ")"));
			break;
		}

		if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			File dir = chooser.getSelectedFile();
			JTextField field = (JTextField) _keyFieldMap.get(key); 
			field.setText(dir.getAbsolutePath());
		}
	}

	public static boolean open(String[] keys) {
		if (keys != null) {
			ConfigDialog dlg = new ConfigDialog(keys);
			dlg.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			dlg.setVisible(true);
			return dlg._accepted;
		}
		return true;
	}
}
