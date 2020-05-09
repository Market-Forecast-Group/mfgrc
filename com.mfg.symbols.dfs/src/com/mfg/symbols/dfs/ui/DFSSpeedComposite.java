package com.mfg.symbols.dfs.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.wb.swt.ResourceManager;

import com.mfg.dm.speedControl.DataSpeedControlState;
import com.mfg.dm.speedControl.DataSpeedModel;

/**
 * @author arian
 * 
 */
public class DFSSpeedComposite extends Composite {

	final FormToolkit toolkit = new FormToolkit(Display.getCurrent());
	private final Text delayText;
	DataSpeedModel model;
	private final PropertyChangeListener modelListener;
	private final Button playButton;
	private final Button pauseButton;
	private final Button lessDelayButton;
	private final Button moreDelayButton;
	private final Composite composite;
	private Runnable stopVeto;
	private final Button stepButton;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public DFSSpeedComposite(Composite parent, int style) {
		super(parent, style);
		model = DataSpeedModel.INITIAL_MODEL;
		modelListener = new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				handleModelPropertyChanged(evt);
			}
		};

		addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				toolkit.dispose();
			}
		});
		toolkit.adapt(this);
		toolkit.paintBordersFor(this);
		setLayout(new GridLayout(4, false));

		composite = toolkit.createComposite(this, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 2, 1));
		toolkit.paintBordersFor(composite);
		composite.setLayout(new GridLayout(5, false));

		playButton = toolkit.createButton(composite, "", SWT.NONE);
		GridData gd_playButton = new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 1, 1);
		gd_playButton.widthHint = 50;
		playButton.setLayoutData(gd_playButton);
		playButton.setToolTipText("Play");
		playButton.setImage(ResourceManager.getPluginImage("com.mfg.dm",
				"icons/play.gif"));

		pauseButton = toolkit.createButton(composite, "", SWT.NONE);
		pauseButton.setToolTipText("Pause");
		pauseButton.setImage(ResourceManager.getPluginImage("com.mfg.dm",
				"icons/pause.gif"));

		stepButton = new Button(composite, SWT.NONE);
		stepButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				model.setState(DataSpeedControlState.STEP);
				model.firePropertyChange(DataSpeedModel.PROP_STATE);
			}
		});
		stepButton.setImage(ResourceManager.getPluginImage("com.mfg.dm",
				"icons/step.gif"));
		toolkit.adapt(stepButton, true, true);

		lessDelayButton = toolkit.createButton(composite, "", SWT.NONE);
		lessDelayButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				double delay = model.getDelay() / 2;
				model.setDelay(Math.max(0.001, delay));
			}
		});
		lessDelayButton.setToolTipText("Slowdown");
		lessDelayButton.setImage(ResourceManager.getPluginImage("com.mfg.dm",
				"icons/down.gif"));

		moreDelayButton = toolkit.createButton(composite, "", SWT.NONE);
		moreDelayButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				double delay = model.getDelay();
				delay *= 2;
				model.setDelay(delay);
			}
		});
		moreDelayButton.setToolTipText("Faster");
		moreDelayButton.setImage(ResourceManager.getPluginImage("com.mfg.dm",
				"icons/up.gif"));

		pauseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				model.setState(DataSpeedControlState.PAUSED);
			}
		});
		playButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DataSpeedControlState state = isSpeedAtMaximum() ? DataSpeedControlState.FAST_FORWARDING
						: DataSpeedControlState.PLAYING;
				model.setState(state);
			}
		});

		toolkit.createLabel(this, "Speed", SWT.NONE);
		delayText = toolkit.createText(this, "New Text", SWT.NONE);
		delayText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));
		delayText.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (Character.isDigit(e.character)) {
					handleChangedDelayText();
				}
			}
		});
		delayText.setText("");
		afterCreateWidgets();

	}

	/**
	 * @return the stopVeto
	 */
	public Runnable getStopVeto() {
		return stopVeto;
	}

	/**
	 * Set a runnable that will check if the process an be stopped. In case the
	 * stop cannot be pressed, it will send a {@link IllegalArgumentException}.
	 * 
	 * @param aStopVeto
	 *            the stopVeto to set
	 */
	public void setStopVeto(Runnable aStopVeto) {
		this.stopVeto = aStopVeto;
	}

	private void afterCreateWidgets() {
		updateFrom_Model_state();
	}

	void handleModelPropertyChanged(final PropertyChangeEvent evt) {
		if (evt.getSource() == model) {
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					if (evt.getPropertyName() == DataSpeedModel.PROP_DELAY) {
						updateFrom_Model_delay();
					}

					if (evt.getPropertyName() == DataSpeedModel.PROP_STATE) {
						updateFrom_Model_state();
					}
				}
			};
			if (PlatformUI.isWorkbenchRunning()) {
				PlatformUI.getWorkbench().getDisplay().asyncExec(runnable);
			} else {
				runnable.run();
			}
		}
	}

	void updateFrom_Model_state() {
		DataSpeedControlState state = model.getState();

		if (state == DataSpeedControlState.PLAYING
				|| state == DataSpeedControlState.FAST_FORWARDING
				|| state == DataSpeedControlState.STOPPED) {
			synchronized (model) {
				model.notifyAll();
			}
		}

		if (!isDisposed()) {
			// update buttons
			playButton.setEnabled(state.isEnabledPlayButton());
			moreDelayButton.setEnabled(state.isEnabledMoreOrLessButton());
			lessDelayButton.setEnabled(state.isEnabledMoreOrLessButton());
			delayText.setEnabled(state.isEnabledMoreOrLessButton());
			stepButton.setEnabled(state.isEnabledStepButton());
			pauseButton.setEnabled(state.isEnabledPauseButton());
		}

	}

	/**
	 * @param dataSpeedModel
	 */
	public void setModel(DataSpeedModel aModel) {
		Assert.isNotNull(aModel);

		if (this.model != null) {
			this.model.removePropertyChangeListener(modelListener);
		}

		this.model = aModel;

		this.model.addPropertyChangeListener(modelListener);

		updateFrom_Model_delay();
		updateFrom_Model_state();
	}

	/**
	 * @return the model
	 */
	public DataSpeedModel getModel() {
		return model;
	}

	/**
	 * @return the btnPlay
	 */
	public Button getPlayButton() {
		return playButton;
	}

	void updateFrom_Model_delay() {
		if (isDisposed()) {
			return;
		}

		DataSpeedControlState state = model.getState();

		double selection = model.getDelay();
		delayText.setText(Double.toString(((int) (selection * 1000)) / 1000.0));
		delayText.setSelection(delayText.getText().length());

		DataSpeedControlState nextState = state;

		if (isSpeedAtMaximum()) {
			if (state.isEnabledPauseButton()) {
				nextState = DataSpeedControlState.FAST_FORWARDING;
			}
		} else {
			if (state == DataSpeedControlState.FAST_FORWARDING) {
				nextState = DataSpeedControlState.PLAYING;
			}
		}

		model.setState(nextState);
	}

	boolean isSpeedAtMaximum() {
		return model.getDelay() == 0;
	}

	void handleChangedDelayText() {
		try {
			double n = Double.parseDouble(delayText.getText());
			if (n >= 0) {
				model.setDelay(n);
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}
}
