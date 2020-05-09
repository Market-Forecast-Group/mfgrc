package com.mfg.dfs.conn;

import java.util.HashMap;

import com.mfg.common.DFSException;
import com.mfg.common.ISymbolListener;
import com.mfg.common.IDataSource;
import com.mfg.dm.TickDataRequest;
import com.mfg.utils.U;

/**
 * The base class for the two versions of service: local and proxy.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public abstract class BaseService implements IDFS {

	{
		/*
		 * Unnamed constructor, just to set the instance for all the data
		 * sources which will be created in this process.
		 */
		BaseDataSource.setService(this);
	}

	/**
	 * This map holds all the data sources which are created in this process.
	 * 
	 * <p>
	 * Remember that there is only one data DFS object per process space.
	 */
	private HashMap<String, IDataSource> _dataSources = new HashMap<>();

	/**
	 * It lists all the remote data sources connections. Each connection has a
	 * integer which has the number of estabilished connections. When it reaches
	 * zero the actual disconnection is done.
	 * 
	 * <p>
	 * Probably there must be another class which is a provider for remote data.
	 * tbd
	 * <p>
	 * Key = virtual symbol id, value = reference count (?)
	 */
	private HashMap<String, Integer> _remoteDataSourcesConnections = new HashMap<>();

	/**
	 * tries to connect to an existing remote data source.
	 * 
	 * <p>
	 * The implementation of the connection depends on the fact if this is a
	 * local or remote server. The data source may be not existing on the
	 * server, in that case an exception is thrown.
	 * 
	 * @param dataSourceId
	 * @param aListener
	 * @throws DFSException
	 */
	protected abstract void _connectToExistingRemoteDataSource(
			String dataSourceId, ISymbolListener aListener)
			throws DFSException;

	/**
	 * Actually creates the data source either a proxy or a local data source
	 * (connected to a virtual symbol).
	 * 
	 * @param request
	 * @param aListener
	 * @return
	 * @throws DFSException
	 */
	protected abstract IDataSource _createDataSourceImpl(
			TickDataRequest request, ISymbolListener aListener)
			throws DFSException;

	/**
	 * disconnects from a remote data source.
	 * 
	 * @param dataSourceId
	 * @param aListener
	 * @throws DFSException
	 */
	protected abstract void _disconnectFromRemoteDataSource(
			String dataSourceId, ISymbolListener aListener)
			throws DFSException;

	@SuppressWarnings("boxing")
	@Override
	public final void connectToExistingDataSource(String dataSourceId,
			ISymbolListener aListener) throws DFSException {
		/*
		 * I have to distinguish the case between a data source which has been
		 * created by this service or a data source which has been created in
		 * another realm.
		 * 
		 * If the data source has been created in this service then the listener
		 * is simply added to the data source, otherwise an abstract connection
		 * method is called.
		 */

		IDataSource dataSource;
		synchronized (_dataSources) {
			dataSource = _dataSources.get(dataSourceId);
		}

		if (dataSource != null) {
			/*
			 * Ok, the data source is existing and it is local, add the listener
			 * to it.
			 */
			dataSource.addListener(aListener, true);
		} else {
			synchronized (_remoteDataSourcesConnections) {
				if (_remoteDataSourcesConnections.containsKey(dataSourceId)) {
					/*
					 * To support it you should create another class which holds
					 * an array of listeners...
					 */
					throw new UnsupportedOperationException();
				}
			}
			_connectToExistingRemoteDataSource(dataSourceId, aListener);
			synchronized (_remoteDataSourcesConnections) {
				_remoteDataSourcesConnections.put(dataSourceId, 1);
			}

		}

	}

	@Override
	public final IDataSource createDataSource(TickDataRequest request,
			ISymbolListener aListener) throws DFSException {

		/*
		 * this method is final here because it needs to update the map.
		 */
		IDataSource ds = _createDataSourceImpl(request, aListener);

		synchronized (_dataSources) {
			_dataSources.put(ds.getId(), ds);
		}

		return ds;
	}

	@SuppressWarnings("boxing")
	@Override
	public final void disconnectFromExistingDataSource(String dataSourceId,
			ISymbolListener aListener) throws DFSException {
		/*
		 * Also in this case the disconnection depends on the status of the data
		 * source, if it is local or remote.
		 */

		IDataSource dataSource;
		synchronized (_dataSources) {
			dataSource = _dataSources.get(dataSourceId);
		}

		if (dataSource != null) {
			dataSource.removeListener(aListener);
		} else {

			boolean disconnection = false;
			synchronized (_remoteDataSourcesConnections) {
				Integer ref = _remoteDataSourcesConnections.get(dataSourceId);
				/*
				 * If ref is null that means that the data source was local and
				 * has been stopped before this call, so nothing should be done
				 * remotely.
				 */
				if (ref != null) {
					if (ref == 1) {
						disconnection = true;
						_remoteDataSourcesConnections.remove(dataSourceId);
					} else {
						throw new UnsupportedOperationException();
					}
				}
			}
			if (disconnection) {
				_disconnectFromRemoteDataSource(dataSourceId, aListener);
			}

		}

	}

	@SuppressWarnings("boxing")
	public void removeStoppedDataSource(IDataSource baseDataSource) {
		synchronized (_dataSources) {
			IDataSource ds = _dataSources.remove(baseDataSource.getId());
			U.debug_var(938495, this.getClass(),
					" service, I stop the data source ",
					baseDataSource.getId(), " size is now ",
					_dataSources.size());
			assert (ds == baseDataSource); // the same object
		}

	}

}
