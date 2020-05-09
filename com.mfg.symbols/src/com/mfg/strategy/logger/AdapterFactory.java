package com.mfg.strategy.logger;

import org.eclipse.core.runtime.IAdapterFactory;

import com.mfg.logger.ILogRecord;
import com.mfg.utils.ui.IObjectSplitter;

@SuppressWarnings("rawtypes")
public class AdapterFactory implements IAdapterFactory {

	private static Class[] adapterList = { IObjectSplitter.class };

	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (adapterType == IObjectSplitter.class) {
			if (adaptableObject instanceof ILogRecord) {
				Object msg = ((ILogRecord) adaptableObject).getMessage();
				if (msg instanceof TradeMessageWrapper) {
					return new IObjectSplitter() {

						@Override
						public Object[] splitObject(Object obj) {
							TradeMessageWrapper wrapper = (TradeMessageWrapper) ((ILogRecord) obj)
									.getMessage();
							return new Object[] { Long.valueOf(wrapper.getFakeTime()),
									Double.valueOf(wrapper.getPrice()), Long.valueOf(wrapper.getEquity()),
									Double.valueOf(wrapper.getLongCapital()),
									Double.valueOf(wrapper.getShortCapital()),
									Integer.valueOf(wrapper.getLongQuantity()),
									Integer.valueOf(wrapper.getShortQuantity()),
									wrapper.getType(), wrapper.getEvent(),
									wrapper.getSource(), Integer.valueOf(wrapper.getOrderID()) };
						}
					};
				}
			}
		}
		return null;
	}

	@Override
	public Class[] getAdapterList() {
		return adapterList;
	}

}
