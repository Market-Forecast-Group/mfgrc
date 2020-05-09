package com.mfg.tea.db;

import java.io.PrintWriter;
import java.util.Map.Entry;

import org.mapdb.Atomic.Integer;
import org.mapdb.Atomic.Long;

/**
 * A simple console dumper which dumps the content of a given db to a console in
 * a pretty print way, much like the Dumper package in perl.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 *
 */
public class ConsoleDbDumper implements IDBDumperListener {

	private final PrintWriter _writer;
	private String _currentComposite;

	public ConsoleDbDumper(PrintWriter aWriter) {
		_writer = aWriter;
	}

	@Override
	public void dumpBegin() {
		/*
		 * initialization of the stream, if necessary.
		 */
	}

	@Override
	public void dumpEnd() {
		/*
		 * The dump has finished, clean up!
		 */
	}

	@Override
	public void dumpUnknownObject(String key, Object value) {
		_writer.println("key=" + key);
		_writer.println("val= " + value.toString());
	}

	@Override
	public void dumpAtomicInteger(String key, Integer value) {
		_writer.println("AtomicInteger: " + key + " value: " + value);
	}

	@Override
	public void dumpAtomicLong(String key, Long value) {
		_writer.println("AtomicLong: " + key + " value: " + value);
	}

	@Override
	public void beginSet(String aSetName) {
		assert (_currentComposite == null);
		_writer.println("Set " + aSetName + "{");
		_currentComposite = aSetName;
	}

	@Override
	public void endSet() {
		_writer.println("} // end of set " + _currentComposite);
		_currentComposite = null;
	}

	@Override
	public void newSetEntry(Object setEntry) {
		// just a indentation.
		_writer.println("  " + setEntry.toString() + ",");

	}

	@Override
	public void beginMap(String aMapName) {
		assert (_currentComposite == null);
		_writer.println("Map " + aMapName + "{");
		_currentComposite = aMapName;
	}

	@Override
	public void endMap() {
		assert (_currentComposite != null);
		_writer.println("} // end of map " + _currentComposite);
		_currentComposite = null;
	}

	@Override
	public void newMapEntry(Entry<?, ?> mapEntry) {
		_writer.println("  " + "(" + mapEntry.getKey() + " -> "
				+ mapEntry.getValue() + ")");

	}
}
