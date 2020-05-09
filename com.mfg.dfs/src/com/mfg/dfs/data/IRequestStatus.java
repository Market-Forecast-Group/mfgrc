package com.mfg.dfs.data;

import java.io.Serializable;

import com.mfg.common.RequestParams;
import com.mfg.utils.socket.IPushSource;

/**
 * The interface used to query the status for all the requests.
 * 
 * @author Sergio
 * 
 */
public interface IRequestStatus extends Serializable, IPushSource {

	/**
	 * returns the number of bars given so far.
	 * 
	 * @return
	 */
	public int numBarsGiven();

	/**
	 * returns the parameters associated to this request.
	 * 
	 * @return
	 */
	public RequestParams getRequest();

}
