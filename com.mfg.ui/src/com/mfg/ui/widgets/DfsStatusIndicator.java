package com.mfg.ui.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchWindow;

import com.mfg.ui.UIPlugin;
import com.mfg.utils.IMarketConnectionStatusListener;
import com.mfg.utils.IMarketConnectionStatusListener.EConnectionStatus;
import com.mfg.utils.IMarketConnectionStatusListener.ETypeOfData;

@SuppressWarnings("restriction")
public class DfsStatusIndicator {
	private static DfsStatusIndicator instance;

	CLabel dfsStatusImgLabel;
	private CLabel dfsStatusTxtLabel;

	// private CLabel iqFeedHistTxtStatus;
	// CLabel iqFeedHistImgStatus;

	private CLabel iqFeedRtTxtStatus;
	CLabel iqFeedRtImgStatus;

	EConnectionStatus _lastDfsStatus = EConnectionStatus.CONNECTING;
	EConnectionStatus _lastIqHistStatus = EConnectionStatus.CONNECTING;
	EConnectionStatus _lastIqRtStatus = EConnectionStatus.CONNECTING;

	public static DfsStatusIndicator getInstance() {
		if (instance == null) {
			instance = new DfsStatusIndicator();
		}
		return instance;
	}

	private DfsStatusIndicator() {
	}

	public void onDFSConnectionStatusChange(final ETypeOfData aDataType,
			final EConnectionStatus aStatus) {

		// debug_var(891930, "[[[[ data type ", aDataType , " status is ",
		// aStatus);

		// if (labelToUpdate != null) {
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {

				CLabel labelToUpdate = null;

				switch (aDataType) {
				case DFS_PROXY:
					labelToUpdate = dfsStatusImgLabel;
					_lastDfsStatus = aStatus;
					break;
				case HISTORICAL:
					return;
					// labelToUpdate = iqFeedHistImgStatus;
					// _lastIqHistStatus = aStatus;
					// break;
				case REAL_TIME:
					labelToUpdate = iqFeedRtImgStatus;
					_lastIqRtStatus = aStatus;
					break;
				default:
					assert (false); // wtf?
					return;
				}

				updateLabel(labelToUpdate, aStatus);

				WorkbenchWindow win = (WorkbenchWindow) PlatformUI
						.getWorkbench().getActiveWorkbenchWindow();
				if (win != null) {
					win.getStatusLineManager().update(true);
				}
			}
		});

		// }
	}

	static void updateLabel(CLabel aImgLabel,
			IMarketConnectionStatusListener.EConnectionStatus aStatus) {

		if (aStatus != null && aImgLabel != null && !aImgLabel.isDisposed()) {
			switch (aStatus) {
			case CONNECTED:
				// aTxtLabel.setText("Data Bridge Connected");
				aImgLabel.setImage(UIPlugin.getDefault().getBundledImage(
						"icons/connected.png"));
				break;
			case CONNECTING:
				// aTxtLabel.setText("Data Bridge Connecting...");
				aImgLabel.setImage(UIPlugin.getDefault().getBundledImage(
						"icons/connecting.png"));
				break;
			case DISCONNECTED:
				// aTxtLabel.setText("Data Bridge Disconnected");
				aImgLabel.setImage(UIPlugin.getDefault().getBundledImage(
						"icons/disconnected.png"));
				break;

			default:
				break;
			}
		}
	}

	public Control createControl(@SuppressWarnings("unused") boolean isDfsEmbedded, Composite parent) {
		dfsStatusTxtLabel = new CLabel(parent, SWT.CENTER);
		dfsStatusImgLabel = new CLabel(parent, SWT.CENTER);

		// iqFeedHistTxtStatus = new CLabel(parent, SWT.CENTER);
		// iqFeedHistImgStatus = new CLabel(parent, SWT.CENTER);

		iqFeedRtTxtStatus = new CLabel(parent, SWT.CENTER);
		iqFeedRtImgStatus = new CLabel(parent, SWT.CENTER);

		dfsStatusTxtLabel.setText("DFS Status:");
		// iqFeedHistTxtStatus.setText("Hist. Data: ");
		iqFeedRtTxtStatus.setText("RT Data:");

		updateLabel(dfsStatusImgLabel, _lastDfsStatus);
		// updateLabel(iqFeedHistImgStatus, _lastIqHistStatus);
		updateLabel(iqFeedRtImgStatus, _lastIqRtStatus);

		// iqFeedHistImgStatus.setVisible(false);
		// iqFeedHistTxtStatus.setVisible(false);

		/**
		 * dfs embedded is always connected!
		 */
		// Giulio says to show always both lights. (commented by Arian)
		// if (isDfsEmbedded) {
		// dfsStatusTxtLabel.setVisible(false);
		// dfsStatusImgLabel.setVisible(false);
		// }

		return dfsStatusImgLabel;
	}

}
