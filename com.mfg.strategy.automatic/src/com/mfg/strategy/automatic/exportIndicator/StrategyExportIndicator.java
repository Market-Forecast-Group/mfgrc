package com.mfg.strategy.automatic.exportIndicator;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import com.mfg.common.QueueTick;
import com.mfg.interfaces.indicator.IIndicator;
import com.mfg.interfaces.indicator.Pivot;
import com.mfg.interfaces.trading.StrategyType;
import com.mfg.strategy.FinalStrategy;
import com.mfg.utils.StepDefinition;
import com.mfg.utils.Utils;
import com.mfg.widget.probabilities.IIndicatorRunner;

public class StrategyExportIndicator extends FinalStrategy implements
		IIndicatorRunner {

	private IndicatorExportingConfiguration configuration;
	private IIndicator widget;
	private List<IndicatorRecord> recordsCache;
	private PrintStream output;
	private File outputFile;

	public StrategyExportIndicator(
			IndicatorExportingConfiguration aConfiguration) {
		this.configuration = aConfiguration;

	}

	@Override
	public void begin(int aTickSize) {
		super.begin(aTickSize);
		widget = getIndicator();
		widget.getChscalelevels();
		recordsCache = new ArrayList<>();
		outputFile = new File(configuration.getFileName());
		try {
			if (!outputFile.exists()) {
				outputFile.createNewFile();
			}
			output = new PrintStream(outputFile);
			output.println(configuration.getColumnNames());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void checkPivots() {
		for (ScaleIndicatorExportingConfiguration e : configuration
				.getScaleIndicatorExportingConfigurations()) {
			if (e.isPivotIncluded()) {
				if (widget.isThereANewPivot(e.getScale())) {
					Pivot pivot = widget.getLastPivot(0, e.getScale());
					for (Iterator<IndicatorRecord> iterator = recordsCache
							.iterator(); iterator.hasNext();) {
						IndicatorRecord element = iterator.next();
						if (element.getTime() <= pivot.getPivotTime()) {
							if (element.getTime() == pivot.getPivotTime())
								element.setPivotData("1");
							iterator.remove();
							output.println(element.toLine());
						} else
							break;
					}
				}
			}
		}
	}

	public void flushAndClose() {
		for (Iterator<IndicatorRecord> iterator = recordsCache.iterator(); iterator
				.hasNext();) {
			IndicatorRecord element = iterator.next();
			output.println(element.toLine());
		}
		output.close();
		Utils.debug_var(12345, "wrote to file");
	}

	@Override
	public StrategyType getStrategyType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isStopped() {
		// TODO Auto-generated method stub
		return false;
	}

	// @Override
	// public int getMultiplicity() {
	// // TODO Auto-generated method stub
	// return 0;
	// }

	// @Override
	// public void newExecution(IOrderExec anExec) {
	// // T ODO Auto-generated method stub
	//
	// }

	@Override
	public void newTickImpl(QueueTick aTick) {
		// super.newTick(tick);
		IndicatorRecord indicatorRecord = new IndicatorRecord(
				aTick.getFakeTime());
		configuration.fillRecord(indicatorRecord, widget);
		recordsCache.add(indicatorRecord);
		checkPivots();
	}

	@Override
	public void onNewTick(QueueTick qt) {
		indicator.onNewTick(qt);
		newTick(qt);
	}

	@Override
	public void onStarting(int aTick, int scale) {
		setTick(new StepDefinition(scale, aTick));
		indicator.onStarting(aTick, scale);
		begin(aTick);
	}

	@Override
	public void onStopping() {
		indicator.onStopping();
		flushAndClose();
	}

	@Override
	public void onTemporaryTick(QueueTick qt) {
		// DO NOTHING
	}

	@Override
	public void onVolumeUpdate(int fakeTime, int volume) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onWarmUpFinished() {
		endWarmUp();
	}

	@Override
	public void preWarmUpFinishedEvent(IProgressMonitor aMonitor) {
		// DO NOTHING
	}

	@Override
	public void realTimeQueueAlertDown(int currentSize) {
		// empty
	}

	@Override
	public void realTimeQueueAlertUp(int currentSize) {
		// empty

	}

	@Override
	public void setStopped(boolean aStopped) {
		// TODO Auto-generated method stub

	}

	@Override
	public void stop() {
		flushAndClose();
	}

	@Override
	public void stopTrading() {
		// TODO Auto-generated method stub

	}

}
