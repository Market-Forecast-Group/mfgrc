/*
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 * 
 * @author <a href="mailto:boniatillo@gmail.com">Arian Fornaris</a>, MFG
 * 
 * @version $Revision$: $Date$:
 * $Id$:
 */
package com.mfg.dm.speedControl;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.wb.swt.ResourceManager;
import org.eclipse.wb.swt.SWTResourceManager;

/**
 * @author arian
 * 
 */
public class SpeedComposite3 extends Composite {

	private final class StepButtonListener extends MouseAdapter implements
			KeyListener {
		private Thread th;
		private boolean _pressing;

		public StepButtonListener() {
			// TODO Auto-generated constructor stub
		}

		@Override
		public void mouseDown(MouseEvent e) {
			down();
		}

		@Override
		public void mouseUp(MouseEvent e) {
			up();
		}

		@Override
		public void keyPressed(KeyEvent e) {
			if (!_pressing) {
				_pressing = true;
				if (e.character == ' ') {
					down();
				}
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
			_pressing = false;
			if (e.character == ' ') {
				up();
			}
		}

		void up() {
			th.interrupt();
			_model.setState(DataSpeedControlState.PAUSED);
		}

		void down() {
			synchronized (_model) {
				_model.notifyAll();
				_model.setState(DataSpeedControlState.STEP);
				_model.setState(DataSpeedControlState.PAUSED);
			}
			th = new Thread() {
				@Override
				public void run() {
					try {
						sleep(500);
						_model.setState(DataSpeedControlState.PLAYING);
					} catch (InterruptedException e) {
						//
					}
				}
			};
			th.start();
		}

	}

	final FormToolkit toolkit = new FormToolkit(Display.getCurrent());
	private final Text _runToTimeText;
	private final Text _delayText;
	DataSpeedModel _model;
	private final PropertyChangeListener modelListener;
	private final Button _playButton;
	private final Button _stopButton;
	private final Button _pauseButton;
	private final Button _stepButton;
	private final Button _runToTimeButton;
	private final Button _lessDelayButton;
	private final Button _moreDelayButton;
	private final Button _ffButton;
	Runnable _stopVeto;
	private boolean _enabledSpeedButtons;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	@SuppressWarnings("unused")
	public SpeedComposite3(Composite parent, int style) {
		super(parent, style);
		_enabledSpeedButtons = true;
		_model = DataSpeedModel.INITIAL_MODEL;
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
		setLayout(new RowLayout(SWT.HORIZONTAL));

		Composite composite = toolkit.createComposite(this, SWT.NONE);
		toolkit.paintBordersFor(composite);
		composite.setLayout(new GridLayout(7, false));

		_playButton = toolkit.createButton(composite, "", SWT.NONE);
		GridData gd_playButton = new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 1, 1);
		gd_playButton.widthHint = 50;
		_playButton.setLayoutData(gd_playButton);
		_playButton.setToolTipText("Play");
		_playButton.setImage(ResourceManager.getPluginImage("com.mfg.dm",
				"icons/play.gif"));

		_pauseButton = toolkit.createButton(composite, "", SWT.NONE);
		_pauseButton.setToolTipText("Pause");
		_pauseButton.setImage(ResourceManager.getPluginImage("com.mfg.dm",
				"icons/pause.gif"));

		_stepButton = toolkit.createButton(composite, "", SWT.NONE);
		_stepButton.setToolTipText("Step");
		_stepButton.setImage(ResourceManager.getPluginImage("com.mfg.dm",
				"icons/step.gif"));

		_stopButton = toolkit.createButton(composite, "", SWT.NONE);
		_stopButton.setToolTipText("Stop");
		_stopButton.setImage(ResourceManager.getPluginImage("com.mfg.dm",
				"icons/stop.gif"));

		_lessDelayButton = toolkit.createButton(composite, "", SWT.NONE);
		_lessDelayButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				long delay = (long) _model.getDelay();
				if (delay == 0) {
					delay = 1;
				} else {
					delay *= 2;
				}
				_model.setDelay(delay);
			}
		});
		_lessDelayButton.setToolTipText("Slowdown");
		_lessDelayButton.setImage(ResourceManager.getPluginImage("com.mfg.dm",
				"icons/down.gif"));

		_moreDelayButton = toolkit.createButton(composite, "", SWT.NONE);
		_moreDelayButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				long delay = (long) _model.getDelay();
				delay /= 2;
				_model.setDelay(delay);
			}
		});
		_moreDelayButton.setToolTipText("Faster");
		_moreDelayButton.setImage(ResourceManager.getPluginImage("com.mfg.dm",
				"icons/up.gif"));

		_ffButton = toolkit.createButton(composite, "", SWT.NONE);
		_ffButton.setFont(SWTResourceManager.getFont("Tahoma", 8, SWT.ITALIC));
		_ffButton.setToolTipText("Run at Full Speed!!!");
		_ffButton.setImage(ResourceManager.getPluginImage("com.mfg.dm",
				"icons/fullspeed.gif"));
		_ffButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				_model.setState(DataSpeedControlState.FAST_FORWARDING);
			}
		});
		_stopButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					if (_stopVeto != null) {
						_stopVeto.run();
					}
					_model.setState(DataSpeedControlState.STOPPED);
				} catch (IllegalArgumentException ex) {
					MessageDialog.openInformation(getShell(), "Stop",
							ex.getMessage());
				}
			}
		});
		// stepButton.addSelectionListener(new SelectionAdapter() {
		// @Override
		// public void widgetSelected(SelectionEvent e) {
		// boolean firstTime = model.getState() !=
		// DataSpeedControlState.STEPPING;
		// model.setState(DataSpeedControlState.STEPPING);
		// if (!firstTime) {
		// model.firePropertyChange(DataSpeedModel.PROP_STATE);
		// }
		// }
		// });

		StepButtonListener stepListener = new StepButtonListener();
		_stepButton.addMouseListener(stepListener);
		_stepButton.addKeyListener(stepListener);

		_pauseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				_model.setState(DataSpeedControlState.PAUSED);
			}
		});
		_playButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DataSpeedControlState state = isSpeedAtMaximum() ? DataSpeedControlState.FAST_FORWARDING
						: DataSpeedControlState.PLAYING;
				_model.setState(state);
			}
		});

		Composite composite_2 = new Composite(this, SWT.NONE);
		toolkit.adapt(composite_2);
		toolkit.paintBordersFor(composite_2);
		GridLayout gl_composite_2 = new GridLayout(2, false);
		gl_composite_2.marginTop = 3;
		composite_2.setLayout(gl_composite_2);

		Label lblSpeed = toolkit.createLabel(composite_2,
				"Price to Price Delay (ms)", SWT.NONE);
		_delayText = toolkit.createText(composite_2, "New Text", SWT.NONE);
		_delayText.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (Character.isDigit(e.character)) {
					handleChangedDelayText();
				}
			}
		});
		_delayText.setText("");

		Composite composite_1 = toolkit.createComposite(this, SWT.NONE);
		toolkit.paintBordersFor(composite_1);
		composite_1.setLayout(new GridLayout(3, false));

		Label lblRunToTime = toolkit.createLabel(composite_1, "Run to Time",
				SWT.NONE);

		_runToTimeText = toolkit.createText(composite_1, "New Text", SWT.NONE);
		_runToTimeText.setText("");

		_runToTimeButton = toolkit.createButton(composite_1, "", SWT.NONE);
		_runToTimeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleSetTimeToRun();
			}
		});
		_runToTimeButton.setToolTipText("Run To Time");
		_runToTimeButton.setImage(ResourceManager.getPluginImage("com.mfg.dm",
				"icons/runtotime.gif"));
		afterCreateWidgets();

	}

	/**
	 * @return the stopVeto
	 */
	public Runnable getStopVeto() {
		return _stopVeto;
	}

	/**
	 * Set a runnable that will check if the process an be stopped. In case the
	 * stop cannot be pressed, it will send a {@link IllegalArgumentException}.
	 * 
	 * @param stopVeto
	 *            the stopVeto to set
	 */
	public void setStopVeto(Runnable stopVeto) {
		this._stopVeto = stopVeto;
	}

	protected void handleSetTimeToRun() {
		String text = _runToTimeText.getText().trim();
		if (text.length() == 0) {
			_model.setTimeToRun(0);
		} else {
			try {
				long n = Long.parseLong(text);
				_model.setTimeToRun(n);
				_model.setState(DataSpeedControlState.PLAYING);
			} catch (NumberFormatException e) {
				//
			}
		}

	}

	private void afterCreateWidgets() {
		updateFrom_Model_state();
	}

	void handleModelPropertyChanged(final PropertyChangeEvent evt) {
		if (evt.getSource() == _model) {
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					if (evt.getPropertyName() == DataSpeedModel.PROP_DELAY) {
						updateFrom_Model_delay();
					}
					if (evt.getPropertyName() == DataSpeedModel.PROP_TIME_TO_RUN) {
						updateFrom_Model_timeToRun();
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

	protected void updateFrom_Model_timeToRun() {
		_runToTimeText.setText(Long.toString(_model.getTimeToRun()));
		_runToTimeText.setSelection(_runToTimeText.getText().length());
	}

	void updateFrom_Model_state() {
		DataSpeedControlState state = _model.getState();

		if (state == DataSpeedControlState.PLAYING
				|| state == DataSpeedControlState.FAST_FORWARDING
				|| state == DataSpeedControlState.STOPPED) {
			synchronized (_model) {
				_model.notifyAll();
			}
		}

		if (!isDisposed()) {
			updateButtons(state);
		}

	}

	void updateButtons(DataSpeedControlState state) {
		// update buttons
		_playButton.setEnabled(state.isEnabledPlayButton());
		_moreDelayButton.setEnabled(state.isEnabledMoreOrLessButton()
				&& _enabledSpeedButtons);
		_lessDelayButton.setEnabled(state.isEnabledMoreOrLessButton()
				&& _enabledSpeedButtons);
		_delayText.setEnabled(state.isEnabledMoreOrLessButton()
				&& _enabledSpeedButtons);
		_ffButton.setEnabled(state.isEnabledFFButton() && _enabledSpeedButtons);
		_stepButton.setEnabled(state.isEnabledStepButton()
				&& _enabledSpeedButtons);
		_pauseButton.setEnabled(state.isEnabledPauseButton()
				&& _enabledSpeedButtons);
		_stopButton.setEnabled(state.isEnabledStopButton());
		_runToTimeButton.setEnabled(state.isEnabledStopButton()
				&& _enabledSpeedButtons);
		_runToTimeText.setEnabled(state.isEnabledStopButton()
				&& _enabledSpeedButtons);

		if (state == DataSpeedControlState.PAUSED) {
			_stepButton.forceFocus();
		}
	}

	/**
	 * @param dataSpeedModel
	 */
	public void setModel(DataSpeedModel model) {
		Assert.isNotNull(model);

		if (this._model != null) {
			this._model.removePropertyChangeListener(modelListener);
		}

		this._model = model;

		this._model.addPropertyChangeListener(modelListener);

		updateFrom_Model_delay();
		updateFrom_Model_state();
	}

	/**
	 * @return the model
	 */
	public DataSpeedModel getModel() {
		return _model;
	}

	/**
	 * @return the btnPlay
	 */
	public Button getPlayButton() {
		return _playButton;
	}

	void updateFrom_Model_delay() {
		if (isDisposed()) {
			return;
		}

		DataSpeedControlState state = _model.getState();

		long selection = (long) _model.getDelay();
		_delayText.setText(Long.toString(selection));
		_delayText.setSelection(_delayText.getText().length());

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

		_model.setState(nextState);
	}

	boolean isSpeedAtMaximum() {
		return _model.getDelay() == 0;
	}

	void handleChangedDelayText() {
		try {
			long n = Long.parseLong(_delayText.getText());
			if (n >= 0) {
				_model.setDelay(n);
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

	public void setEnableSpeedButtons(boolean enabledSpeedButtons) {
		_enabledSpeedButtons = enabledSpeedButtons;
		updateButtons(getModel().getState());
	}

	public boolean isEnabledSpeedButtons() {
		return _enabledSpeedButtons;
	}
}
