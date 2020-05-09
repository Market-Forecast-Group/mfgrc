package com.marketforecastgroup.dfsa.ui.commands;

import static com.mfg.utils.Utils.debug_id;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

import com.marketforecastgroup.dfsa.ui.NewSymbolDialog;
import com.marketforecastgroup.dfsa.ui.views.SymbolsNavigator;
import com.mfg.common.DFSException;
import com.mfg.common.DfsSymbol;
import com.mfg.connector.dfs.DFSPlugin;
import com.mfg.connector.dfs.IDFSRunnable;
import com.mfg.dfs.conn.DfsCacheRepo;
import com.mfg.dfs.conn.IDFS;
import com.mfg.dfs.data.DfsSymbolStatus;
import com.mfg.dfs.misc.Service;
import com.mfg.ui.UIPlugin;
import com.mfg.utils.PartUtils;

public class CreateNewSymbolHandler extends AbstractHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		if (DFSPlugin.getDefault().isDFSReady()) {
			try {
				DFSPlugin.getDefault().runWithDFS(new IDFSRunnable() {

					@Override
					public void run(IDFS dfs) {
						try {
							openDialog(event, dfs);
						} catch (DFSException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void notReady() {
						// nothing
					}
				});
			} catch (DFSException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		} else {
			MessageDialog.openInformation(HandlerUtil.getActiveShell(event),
					"DFS", "DFS is not ready yet. Try later.");
		}

		return null;
	}

	static void openDialog(ExecutionEvent event, IDFS dfs) throws DFSException {
		String name = null;

		NewSymbolDialog dlg = new NewSymbolDialog(
				HandlerUtil.getActiveShell(event));

		final ArrayList<DfsSymbol> symbols = dfs.getSymbolsList().symbols;

		dlg.setName("@ES " + (symbols.size() + 1));
		dlg.setTickSize(0.25);
		dlg.setTickValue(12.5);
		dlg.setCurrency(NewSymbolDialog.USD);
		dlg.setTimeZone(NewSymbolDialog.TZ_AMERICA_NEW_YORK);
		dlg.setNameValidator(new IInputValidator() {

			@Override
			public String isValid(String newText) {
				for (DfsSymbol s : symbols) {
					if (s.prefix.equals(newText)) {
						return "That name already exists.";
					}
				}
				return null;
			}
		});

		if (dlg.open() == Window.OK) {
			name = dlg.getName();
			if (name != null && dfs instanceof Service) {
				Service service = (Service) dfs;

				// get data from dialog
				BigDecimal bigTickSize = BigDecimal.valueOf(dlg.getTickSize());
				int tickSize = bigTickSize.unscaledValue().intValue();
				int tickSizeScale = bigTickSize.scale();
				BigDecimal bigTickValue = BigDecimal
						.valueOf(dlg.getTickValue());
				int tickValue = bigTickValue.unscaledValue().intValue();

				// Fix the tick value with the tick size scale. If the tick size
				// scale is bigger, then the tick value should be multiplied by
				// 10 the times of the difference.
				int tickValueScale = bigTickValue.scale();
				while (tickValueScale > 0) {
					tickValue *= 10;
					tickValueScale--;
				}

				debug_id(46665, "Use tick value " + tickValue);

				// add to the cache
				DfsCacheRepo cache = service.getModel().getCache();
				DfsSymbol s = cache.addSymbol(name, "Complete name", tickSize,
						tickSizeScale, tickValue, dlg.getTimeZone(),
						dlg.getCurrency(), DfsSymbol.TYPE_FUTURES);

				// check if the symbol was added successfully
				DfsSymbolStatus status = dfs.getStatusForSymbol(s.prefix);

				if (status == null) {
					MessageDialog.openError(HandlerUtil.getActiveShell(event),
							"Open Symbol", "There is not a status for prefix "
									+ s.prefix);
				} else {

					// run the scheduler if the user select to do that. this
					// should make the symbol to appear as ready in the
					// navigator, after the scheduler stops
					if (dlg.isRunSchedulerNow()) {
						dfs.manualScheduling();
					}

					try {
						UIPlugin.openEditor(s);
					} catch (PartInitException e) {
						e.printStackTrace();
						throw new RuntimeException(e);
					}
				}

				// TODO:
				// yes, we do this but yet it does not have any effect,
				// we should show in the content provider the "collecting"
				// symbols, this is an issue yet
				List<SymbolsNavigator> views = PartUtils
						.getOpenViews(SymbolsNavigator.ID);
				for (SymbolsNavigator view : views) {
					view.getCommonViewer().refresh();
				}
			}
		}
	}
}
