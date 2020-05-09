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

package com.mfg.logger.memory;

import static java.lang.System.out;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.mfg.logger.ILogFilter;
import com.mfg.logger.ILogReader;
import com.mfg.logger.ILogRecord;

public class MemoryLogReader implements ILogReader {
	private List<ILogRecord> memory;
	private List<ILogRecord> filteredList;

	protected MemoryLogReader() {
	}

	public MemoryLogReader(List<ILogRecord> aMemory) {
		setMemory(aMemory);
	}

	public List<ILogRecord> getMemory() {
		return memory;
	}

	public void setMemory(List<ILogRecord> aMemory) {
		if (aMemory != null) {
			this.memory = aMemory;
			this.filteredList = aMemory;
		}
	}

	@Override
	public int getRecordCount() {
		return filteredList.size();
	}

	@Override
	public ILogRecord read(int pos) {
		return filteredList.get(pos);
	}

	@Override
	public List<ILogRecord> read() {
		return filteredList;
	}

	@Override
	public List<ILogRecord> read(int start, int end) {
		return filteredList.subList(start, end);
	}

	@Override
	public void setFilters(ILogFilter... filters) {
		long m = System.currentTimeMillis();
		try {
			if (filters == null) {
				filteredList = memory;
			} else {
				filteredList = Collections
						.synchronizedList(new ArrayList<ILogRecord>());
				for (int i = 0; i < memory.size(); i++) {
					ILogRecord r = memory.get(i);
					boolean accept = true;
					for (ILogFilter f : filters) {
						if (!f.accept(r)) {
							accept = false;
							break;
						}
					}
					if (accept) {
						filteredList.add(r);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		out.println("set filter delayed " + (System.currentTimeMillis() - m)
				/ 1000.0 + " for a memory of " + memory.size() + " items");
	}
}
