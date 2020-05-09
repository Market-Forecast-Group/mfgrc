/**
 * 
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 * 
 * $Id: $
 *
 * @author <a href="mailto:boniatillo@gmail.com">Arian Fornaris</a>, MFG
 * 
 * @version $Revision: $ $Date: $
 */
/**
 * 
 */

package com.mfg.chart.model;

/**
 * @author arian
 * 
 */
public interface IChannelModel {
	IChannelModel EMPTY = new IChannelModel() {

		@Override
		public IChannelCollection getChannels(int dataLayer, long lowerTime,
				long upperTime) {
			return IChannelCollection.EMPTY;
		}

		@Override
		public IChannel2Collection getChannels2(int dataLayer, long lowerTime,
				long upperTime) {
			return IChannel2Collection.EMPTY;
		}
	};

	public IChannelCollection getChannels(int dataLayer, long lowerTime,
			long upperTime);

	public IChannel2Collection getChannels2(int dataLayer, long lowerTime,
			long upperTime);

}
