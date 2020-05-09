package com.mfg.ui;

import static java.lang.System.out;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import com.mfg.ui.editors.IEditable;

/**
 * The activator class controls the plug-in life cycle
 */
public class UIPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.mfg.ui"; //$NON-NLS-1$

	// The shared instance
	private static UIPlugin plugin;
	public static final String SOUND_ARRIVED_TH_SCALE = "arrived-th-scale.wav";
	public static String SOUND_BUSHORN = "bushorn.wav";
	public static String SOUND_LASER = "laser.wav";
	public static String SOUND_INTERRUPTED = "interrupted.wav";
	public static String SOUND_TRAIN = "train.wav";
	public static String SOUND_ALERT = "alert.wav";
	public static String SOUND_PHONE = "phone.wav";
	public static String SOUND_CHORD = "chord.wav";
	public static String SOUND_TINK1 = "tink1.wav";
	public static String SOUND_VIBRO1 = "vibro1.wav";
	public static String SOUND_BUZZER = "buzzer.wav";
	public static String SOUND_RETURN = "return.wav";
	public static String SOUND_EMM_CTZ_EXIT = "emm_ctz_exit.wav";
	public static String SOUND_THUMP1 = "thump1.wav";
	public static String SOUND_ALARM = "alarm.wav";
	public static String SOUND_POP = "pop.wav";
	public static String SOUND_RINGIN = "ringin.wav";
	public static String SOUND_CHIMES = "chimes.wav";
	public static String SOUND_CASHREG = "cashreg.wav";
	public static String SOUND_DRIP = "drip.wav";
	public static String SOUND_REMINDER = "reminder.wav";
	public static String SOUND_DOORBELL = "doorbell.wav";
	public static String SOUND_DING = "ding.wav";
	public static String SOUND_BEEP = "beep.wav";
	public static String SOUND_BUZZ = "buzz.wav";
	public static String SOUND_BLIP = "blip.wav";
	public static String SOUND_GOTAMESSAGE = "gotamessage.wav";
	public static String SOUND_STONE1 = "stone1.wav";
	public static String SOUND_AUTOPLAY = "autoplay.wav";
	public static String SOUND_ALERTTARGET = "alerttarget.wav";
	public static String SOUND_EMM_CTZ_ENTRY = "emm_ctz_entry.wav";
	public static String SOUND_WARNING = "warning.wav";
	public static String SOUND_ATTENTION = "attention.wav";
	public static String SOUND_NEW = "new.wav";
	public static String SOUND_COWBELL = "cowbell.wav";
	public static String SOUND_CHIME_DOWN = "chime down.wav";

	public static String[] SOUNDS = {

	SOUND_BUSHORN, SOUND_LASER, SOUND_INTERRUPTED, SOUND_TRAIN, SOUND_ALERT,
			SOUND_PHONE, SOUND_CHORD, SOUND_TINK1, SOUND_VIBRO1, SOUND_BUZZER,
			SOUND_RETURN, SOUND_EMM_CTZ_EXIT, SOUND_THUMP1, SOUND_ALARM,
			SOUND_POP, SOUND_RINGIN, SOUND_CHIMES, SOUND_CASHREG, SOUND_DRIP,
			SOUND_REMINDER, SOUND_DOORBELL, SOUND_DING, SOUND_BEEP, SOUND_BUZZ,
			SOUND_BLIP, SOUND_GOTAMESSAGE, SOUND_STONE1, SOUND_AUTOPLAY,
			SOUND_ALERTTARGET, SOUND_EMM_CTZ_ENTRY, SOUND_WARNING,
			SOUND_ATTENTION, SOUND_NEW, SOUND_COWBELL, SOUND_CHIME_DOWN, };

	private Timer _timer;
	private List<TimerTask> _tasks = new ArrayList<>();

	public synchronized void playSound(final String... names) {
		if (_timer == null) {
			_timer = new Timer();
		}
		for (TimerTask t : _tasks) {
			t.cancel();
		}
		_tasks.clear();
		_timer.purge();
		TimerTask task = new TimerTask() {

			@Override
			public void run() {
				try {
					byte[] buffer = new byte[4096];
					for (String name : names) {
						if (name == null) {
							continue;
						}
						URL url = getURL(name);
						try (AudioInputStream is = AudioSystem
								.getAudioInputStream(url)) {
							AudioFormat format = is.getFormat();
							try (SourceDataLine line = AudioSystem
									.getSourceDataLine(format)) {
								line.open(format);
								line.start();
								while (is.available() > 0) {
									int len = is.read(buffer);
									line.write(buffer, 0, len);
								}
								line.drain();
								line.close();
							}
						}
					}
				} catch (Exception e) {
					//
				}
			}
		};
		_timer.schedule(task, 0);
		_tasks.add(task);
	}

	/**
	 * The constructor
	 */
	public UIPlugin() {
	}

	public URL getURL(String name) throws MalformedURLException {
		Bundle bundle = getBundle();
		URL url;
		if (bundle == null) {
			url = new File("sounds/" + name).toURI().toURL();
		} else {
			url = bundle.getResource("sounds/" + name);
		}
		return url;
	}

	@SuppressWarnings("static-method")
	public void stopSound(String name) {
		// TODO: not implemented yet
		out.println("stop " + name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static UIPlugin getDefault() {
		return plugin;
	}

	public static IEditorPart openEditor(final Object obj)
			throws PartInitException {
		IEditable editable = (IEditable) Platform.getAdapterManager()
				.getAdapter(obj, IEditable.class);
		IEditorPart editor = null;
		if (editable != null) {
			editor = editable.openEditor();
			PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getActivePage().activate(editor);
		}
		return editor;
	}

	public Image getBundledImage(String path) {
		Image image = getImageRegistry().get(path);
		if (image == null) {
			getImageRegistry().put(path, getBundledImageDescriptor(path));
			image = getImageRegistry().get(path);
		}
		return image;
	}

	private static ImageDescriptor getBundledImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
}
