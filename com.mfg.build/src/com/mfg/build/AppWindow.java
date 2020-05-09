package com.mfg.build;

import static java.lang.System.out;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

public class AppWindow extends JFrame {
	private static final long serialVersionUID = 1L;

	private JPanel _contentPane;
	JTextArea _textArea;

	private JFileChooser _chooser;

	private JMenuBar _menuBar;

	private JMenu _mnProducts;

	private ImageIcon _productIcon;

	JCheckBoxMenuItem _autoScrollingItem;

	UpdateProductAction _updateProductAction;

	private JPanel _toolbar;

	private static AppWindow _instance;

	public static AppWindow getInstance() {
		return _instance;
	}

	/**
	 * Launch the application.
	 * 
	 * @throws URISyntaxException
	 * @throws MonitorException
	 */
	public static void main(String[] args) throws URISyntaxException {
		try {
			if (UIManager.getSystemLookAndFeelClassName().contains("indow")) {
				UIManager.setLookAndFeel(UIManager
						.getSystemLookAndFeelClassName());
			} else {
				UIManager.setLookAndFeel(NimbusLookAndFeel.class.getName());
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					AppWindow frame = new AppWindow();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public AppWindow() {
		super("MFG Build v0.26");
		_instance = this;
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setIconImages(Arrays.asList(getBuildIcon(16), getBuildIcon(32),
				getBuildIcon(120)));
		setSize(800, 600);
		setLocation(100, 100);

		_toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
		_toolbar.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

		createMenuBar();

		_contentPane = new JPanel();
		_contentPane.setLayout(new BorderLayout(0, 0));
		_textArea = new JTextArea();
		_textArea.setEditable(false);
		_textArea.setLineWrap(true);
		_textArea.setWrapStyleWord(true);
		_textArea.setFont(Font.decode(Font.MONOSPACED));
		_contentPane.add(_toolbar, BorderLayout.NORTH);
		_contentPane.add(new JScrollPane(_textArea), BorderLayout.CENTER);
		setContentPane(_contentPane);

		redirectSystemStreams();

		showWelcome();

		startCheckUpdateThread();
	}

	private void startCheckUpdateThread() {
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					try {
						Config cfg = Config.getInstance();
						if (CheckNewSourceVersionAction.check(cfg)) {
							if (JOptionPane
									.showConfirmDialog(
											AppWindow.this,
											"There is a new source version. Do you want to update the product?",
											"New Version",
											JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
								_updateProductAction.execute(cfg);
							}
						}
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					try {
						Thread.sleep(TimeUnit.MINUTES.toMillis(10));
					} catch (InterruptedException e) {
						// nothing
					}
				}
			}
		});
		thread.setDaemon(true);
		thread.start();
	}

	private BufferedImage getBuildIcon(int size) {
		try {
			String name = "mfg-build-icon-" + size + ".png";
			return ImageIO.read(getClass().getResourceAsStream(name));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	ImageIcon getProductIcon() {
		if (_productIcon == null) {
			try {
				String name = "mfg-icon.png";
				_productIcon = new ImageIcon(ImageIO.read(getClass()
						.getResourceAsStream(name)));
				return _productIcon;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return _productIcon;
	}

	private static void showWelcome() {
		out.println("\nWelcome to MFG Build\n");

		out.println("F5 \tRun MFG product.");
		out.println("F6 \tRun DFS product.");
		out.println("F8 \tUpdate MFG product.");
		out.println("F2 \tSave the log to a file.");
		out.println("\n");

		out.println("os.name: " + System.getProperty("os.name"));
		out.println("os.arch: " + System.getProperty("os.arch"));
		out.println("user.home: " + System.getProperty("user.home"));
		out.println("java.home: " + System.getProperty("java.home"));
		out.println("\n");
		out.println("Recognized " + (Config.isWindows() ? "win" : "linux-")
				+ "-" + Config.getArch() + "\n\n");
	}

	private void createMenuBar() {
		_menuBar = new JMenuBar();
		setJMenuBar(_menuBar);

		menuFile();
		menuEdit();
		menuProducts();
		menuDeveloper();
		menuLicense();
	}

	private void menuLicense() {
		JMenu menu = new JMenu("License");
		menu.add(new AbstractAction("Generate Do Not Expire License Key") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					String key = Encrypter.encrypt("DoNotExpire");

					out.println("\n\n\nGenerated \"Do Not Expire\" key:\n");
					out.println(key);
					out.println();
					out.println("(The key was copied into the clipboard)\n\n");

					StringSelection stringSelection = new StringSelection(key);
					Clipboard clpbrd = Toolkit.getDefaultToolkit()
							.getSystemClipboard();
					clpbrd.setContents(stringSelection, null);

				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});

		menu.add(new AbstractAction("Generate Expiration License Key") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				LocalDate date = LocalDate.now();
				date = date.plus(1, ChronoUnit.YEARS);
				String data = (String) JOptionPane
						.showInputDialog(
								getInstance(),
								"<html>Write the expiration date (day-month-year)<br><i>(By default it takes the next year)</i></html>",
								"title",
								JOptionPane.QUESTION_MESSAGE,
								null,
								null,
								date.getDayOfMonth() + "-"
										+ date.getMonthValue() + "-"
										+ date.getYear());
				if (data != null) {
					try {
						String key = Encrypter.encrypt(data);

						out.println("\n\n\nGenerated expiration key (" + data
								+ "):\n");
						out.println(key);
						out.println();
						out.println("(The key was copied into the clipboard)\n\n");

						StringSelection stringSelection = new StringSelection(
								key);
						Clipboard clpbrd = Toolkit.getDefaultToolkit()
								.getSystemClipboard();
						clpbrd.setContents(stringSelection, null);

					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		});

		_menuBar.add(menu);
	}

	@SuppressWarnings("unchecked")
	public void menuProducts() {
		if (_mnProducts == null) {
			_mnProducts = new JMenu("Products");
			_menuBar.add(_mnProducts);
		}
		_mnProducts.removeAll();
		_toolbar.removeAll();

		_updateProductAction = new UpdateProductAction();
		_mnProducts.add(_updateProductAction).setAccelerator(
				KeyStroke.getKeyStroke("F8"));

		_mnProducts.add(new JSeparator());

		_mnProducts.add(new BuildReleaseAction(true));
		_mnProducts.add(new BuildReleaseAction(false));

		_mnProducts.add(new JSeparator());

		List<Path> dfsExecList = new ArrayList<>();
		List<Path> mfgExecList = new ArrayList<>();
		Path product = Config.getInstance().getDir().resolve("products");
		{
			final List<Path> execList = new ArrayList<>();
			File[] listFiles = product.toFile().listFiles();
			if (listFiles != null) {
				for (File f : listFiles) {
					Path p = f.toPath();
					if (p.toString().contains("DFS")) {
						Path exe = p.resolve("DFS.exe");
						if (Files.exists(exe)) {
							execList.add(exe);
						} else {
							out.println("Warning: " + exe + " does not exist.");
						}
					} else {
						Path exe = p.resolve("MFG.exe");
						if (Files.exists(exe)) {
							execList.add(exe);
						} else {
							out.println("Warning: " + exe + " does not exist.");
						}
					}
				}
			}
			Collections.sort(execList, new Comparator<Path>() {

				@Override
				public int compare(Path o1, Path o2) {
					try {
						return -Files.getLastModifiedTime(o1.getParent())
								.compareTo(
										Files.getLastModifiedTime(o2
												.getParent()));
					} catch (IOException e) {
						Utils.error(e);
						return 0;
					}
				}
			});

			for (Path p : execList) {
				if (p.toString().contains("DFS")) {
					dfsExecList.add(p);
				} else {
					mfgExecList.add(p);
				}
			}
		}

		for (final List<Path> list : new List[] { mfgExecList, dfsExecList }) {
			boolean isMFG = list == mfgExecList;
			String productName = isMFG ? "MFG" : "DFS";

			if (!isMFG) {
				_mnProducts.add(new JSeparator());
			}

			JMenuItem item = addLaunchItem(productName, list.isEmpty() ? null
					: list.get(0), _mnProducts);
			item.setText("Run " + productName + " Product");

			if (isMFG) {
				item.setAccelerator(KeyStroke.getKeyStroke("F5"));
			} else {
				item.setAccelerator(KeyStroke.getKeyStroke("F6"));
			}
			AbstractAction action = (AbstractAction) item.getAction();
			action.putValue(Action.SHORT_DESCRIPTION, item.getText());
			action.putValue(Action.NAME, item.getText());
			JButton btn = new JButton(item.getAction());
			btn.setText(isMFG ? "<html>MFG <small>(F5)</small></html>"
					: "<html>DFS <small>(F6)</small></html>");
			_toolbar.add(btn);

			JMenu productMenu = new JMenu(productName);
			_mnProducts.add(productMenu);

			for (final Path exe : list) {
				int i = list.indexOf(exe);
				final String name = (list.size() - i) + " - "
						+ BuildAction.getProductLauncherName(productName, exe);
				JMenu menu = new JMenu(name);
				productMenu.add(menu);

				addLaunchItem(productName, exe, menu);

				final Path logfile = exe.getParent().resolve(
						exe.getFileName() + ".log");
				menu.add(new JSeparator());
				if (Files.exists(logfile)) {
					menu.add(new AbstractAction("See Log File") {

						private static final long serialVersionUID = 1L;

						@Override
						public void actionPerformed(ActionEvent e) {
							try {
								_textArea.append("\nPrinting " + logfile
										+ "...\n\n");
								byte[] bytes = Files.readAllBytes(logfile);
								_textArea.append(new String(bytes));
							} catch (IOException e1) {
								Utils.error(e1);
							}
						}
					});
				}
				menu.add(new AbstractAction("Open Location") {

					private static final long serialVersionUID = 1L;

					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							Desktop.getDesktop().open(exe.getParent().toFile());
						} catch (IOException e1) {
							Utils.error(e1);
						}
					}
				});
			}

			if (list.size() > 1) {
				productMenu.add(new JSeparator());
				productMenu.add(new AbstractAction("Delete Older Products") {

					private static final long serialVersionUID = 1L;

					@Override
					public void actionPerformed(ActionEvent e) {
						if (JOptionPane.showConfirmDialog(getContentPane(),
								"Do you want to delete older products?",
								"Delete", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
							out.println("\nDeleting " + (list.size() - 1)
									+ " products...");
							for (int i = 1; i < list.size(); i++) {
								try {
									Path dir = list.get(i).getParent();
									out.println("Deleting " + dir + "...");
									BuildAction.deleteTree(dir);
								} catch (IOException e1) {
									Utils.error(e1);
								}
							}
							menuProducts();
						}
					}
				});
			}
		}

		_toolbar.doLayout();
	}

	private JMenuItem addLaunchItem(final String productName, final Path exe,
			JMenu menu) {
		return menu.add(new AbstractAction("Run " + productName) {

			private static final long serialVersionUID = 1L;
			{
				putValue(SMALL_ICON, getProductIcon());
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (exe == null) {
						if (JOptionPane
								.showConfirmDialog(
										getContentPane(),
										"There is not a product to run, do you want to build it?",
										"Run", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
							new UpdateProductAction().actionPerformed(e);
						}
					} else {
						BuildAction.launchProduct(exe);
					}
				} catch (Exception e1) {
					Utils.error(e1);
				}
			}

		});
	}

	private void menuDeveloper() {
		JMenu mnDev = new JMenu("Developer");
		_menuBar.add(mnDev);
		mnDev.add(new CheckRepoStatusAction());
		mnDev.add(new UpdateRepoAction());
		mnDev.add(new HgCommandAction());
		mnDev.add(new JSeparator());
		mnDev.add(new DownloadSourceZipAction());
		mnDev.add(new CheckNewSourceVersionAction());
		mnDev.add(new DownloadMFGDepsAction());
		mnDev.add(new InstallEclipseAction());
		mnDev.add(new JSeparator());
		mnDev.add(new ListRunningJVMAction());
		mnDev.add(new JSeparator());
		mnDev.add(new PatchDialogAction());
	}

	private void menuEdit() {
		JMenu mnEdit = new JMenu("Edit");
		_menuBar.add(mnEdit);
		mnEdit.add(new AbstractAction("Copy") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				if (_textArea.getSelectedText() == null) {
					_textArea.selectAll();
					_textArea.copy();
					_textArea.select(0, 0);
				} else {
					_textArea.copy();
				}
			}
		});
		mnEdit.add(new AbstractAction("Clear Console") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				_textArea.setText("");
			}
		});
		_autoScrollingItem = new JCheckBoxMenuItem("Auto Scroll");
		_autoScrollingItem.setSelected(true);
		mnEdit.add(_autoScrollingItem);

		mnEdit.add(new JCheckBoxMenuItem(new AbstractAction("Wrap Text") {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				_textArea.setLineWrap(!_textArea.getLineWrap());
			}
		})).setSelected(true);
		mnEdit.add(new JSeparator());
		mnEdit.add(new PrintSystemPropertiesAction());
		mnEdit.add(new OpenSettingsAction());
	}

	private void menuFile() {
		JMenu mnFile = new JMenu("File");
		_menuBar.add(mnFile);
		mnFile.add(new AbstractAction("Save To File...") {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				saveToFile(_textArea);
			}
		}).setAccelerator(KeyStroke.getKeyStroke("F2"));
		mnFile.add(new JSeparator());
		mnFile.add(new AbstractAction("Exit") {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
				System.exit(0);
			}
		});
	}

	protected void saveToFile(JTextArea textArea) {
		if (_chooser == null) {
			_chooser = new JFileChooser();
			_chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			_chooser.setDialogType(JFileChooser.SAVE_DIALOG);
		}
		if (_chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			try {
				File file = _chooser.getSelectedFile();
				if (file.exists()) {
					if (JOptionPane.showConfirmDialog(this,
							"Do you want to override this file?") == JOptionPane.YES_OPTION) {
						return;
					}
				} else {
					Files.createFile(file.toPath());
				}
				Files.write(file.toPath(), textArea.getText().getBytes());
			} catch (Exception e) {
				Utils.error(e);
			}
		}
	}

	void updateTextPane(final String text) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				Document doc = _textArea.getDocument();
				try {
					doc.insertString(doc.getLength(), text, null);
					if (doc.getLength() > 80000) {
						doc.remove(0, 40000);
					}
				} catch (BadLocationException e) {
					throw new RuntimeException(e);
				}
				if (_autoScrollingItem.isSelected()) {
					_textArea.setCaretPosition(doc.getLength() - 1);
				}
			}
		});
	}

	private void redirectSystemStreams() {
		OutputStream os = new OutputStream() {
			@Override
			public void write(final int b) throws IOException {
				updateTextPane(String.valueOf((char) b));
			}

			@Override
			public void write(byte[] b, int off, int len) throws IOException {
				updateTextPane(new String(b, off, len));
			}

			@Override
			public void write(byte[] b) throws IOException {
				write(b, 0, b.length);
			}
		};

		System.setOut(new PrintStream(os, true));
		System.setErr(new PrintStream(os, true));
	}
}
