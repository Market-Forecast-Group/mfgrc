package com.mfg.connector.dfs;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IStartup;

import com.mfg.common.DFSException;
import com.mfg.dfs.conn.IDFS;
import com.mfg.utils.Utils;
import com.mfg.utils.jobs.IMFGJob;

public class DFSStartup implements IStartup {

	@Override
	public void earlyStartup() {
		Job job = new Job("Starting DFS") {
			@Override
			public boolean belongsTo(Object family) {
				return super.belongsTo(family) || family == IMFGJob.class;
			}

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					long t = System.currentTimeMillis();
					Utils.debug_id(34559, "Starting loading DFS");
					DFSPlugin plugin = DFSPlugin.getDefault();

					final IDFS dfs = plugin.getDataProvider().getDfs();
					Utils.debug_id(34558,
							"DFS is ready " + (System.currentTimeMillis() - t)
									+ "ms");

					t = System.currentTimeMillis();
					List<IDFSRunnable> dfsRunnables = plugin.getDfsRunnables();

					synchronized (dfsRunnables) {
						int size = dfsRunnables.size();
						if (size > 0) {
							ExecutorService pool = Executors
									.newFixedThreadPool(size);
							for (IDFSRunnable run : dfsRunnables) {
								final IDFSRunnable run2 = run;
								pool.execute(new Runnable() {

									@Override
									public void run() {
										try {
											run2.run(dfs);
										} catch (DFSException e) {
											e.printStackTrace();
										}
									}
								});

							}
						}
					}
					plugin.setDfs(dfs);
					return Status.OK_STATUS;
				} catch (Exception e) {
					e.printStackTrace();
					return new Status(IStatus.ERROR, DFSPlugin.PLUGIN_ID,
							e.getMessage(), e);
				}
			}
		};
		job.schedule();
	}
}
