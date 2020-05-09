package com.mfg.tea.db;

import java.util.Map.Entry;

import org.mapdb.Atomic;
import org.mapdb.Atomic.Long;

/**
 * An interface used to listen to dump events in the {@link DbDumper} class.
 * 
 * <p>
 * Concrete objects implementing this interface will either create a DOM in
 * memory and flush it at the end of the dump, or stream it to a physical medium
 * (display or file) as soon as new data is given by the dumper itself.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 *
 */
public interface IDBDumperListener {

	/**
	 * called when the dump is about to begin.
	 */
	public void dumpBegin();

	/**
	 * Called when the dump has ended. The listener is able to do some cleanup.
	 */
	public void dumpEnd();

	/**
	 * There is an unknown object in db, please dump it as you can.
	 * 
	 * @param key
	 *            the key with which the object is known.
	 * @param value
	 *            the unknown object.
	 */
	public void dumpUnknownObject(String key, Object value);

	/**
	 * dumps the given atomic integer.
	 * 
	 * @param key
	 *            the name of the atomic integer
	 * 
	 * @param value
	 *            the atomic integer to be dumped.
	 */
	public void dumpAtomicInteger(String key, Atomic.Integer value);

	/**
	 * dumps the given atomic long.
	 * 
	 * @param key
	 *            the name of the atomic long
	 * 
	 * @param value
	 *            the atomic integer to be dumped.
	 */
	public void dumpAtomicLong(String key, Long value);

	/**
	 * tells the dumper that a set with the given name begins here.
	 * 
	 * @param aSetName
	 *            the name of the set.
	 */
	public void beginSet(String aSetName);

	/**
	 * tells the dumper that the previous set which has started with
	 * {@link #beginSet(String)} has finished.
	 */
	public void endSet();

	/**
	 * called for each entry in this set.
	 * 
	 * @param setEntry
	 */
	public void newSetEntry(Object setEntry);

	/**
	 * tells the dumper that a new map begins with the given name
	 * 
	 * @param aMapName
	 */
	public void beginMap(String aMapName);

	/**
	 * tells the dumper that the given map has ended.
	 */
	public void endMap();

	/**
	 * gets a new map entry from the dumper, the map has been already
	 * initialized with {@link #beginMap(String)}
	 * 
	 * @param mapEntry
	 *            the entry in this map.
	 */
	public void newMapEntry(Entry<?, ?> mapEntry);

}
