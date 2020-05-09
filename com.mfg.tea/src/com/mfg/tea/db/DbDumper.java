package com.mfg.tea.db;

import java.io.PrintWriter;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableSet;
import java.util.Set;

import org.mapdb.Atomic;
import org.mapdb.BTreeKeySerializer;
import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Fun;
import org.mapdb.HTreeMap;
import org.mapdb.Hasher;
import org.mapdb.Serializer;

/**
 * Utility class (it does not have a public constructor) which contains only an
 * utility method to dump the content of a {@link DB} database and to give dump
 * events to a {@link IDBDumperListener} object.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 *
 */
public class DbDumper {

	/**
	 * 
	 * dumps the content of the database given in text mode using the supplied
	 * dump listener.
	 * 
	 * @param aDb
	 *            the database to be dumped, it must be already opened (also in
	 *            read only mode). No assumptions are made on the content of the
	 *            db, the dumper will try to fail back to sensible defaults in
	 *            all cases.
	 * 
	 * @param aListener
	 *            the listener used to manage physically the dump. In this
	 *            method we have only the logic to traverse the DB.
	 */
	public static void dump(DB aDb, IDBDumperListener aListener) {
		aListener.dumpBegin();

		// gets all objects.
		Map<String, Object> all = aDb.getAll();

		for (Entry<String, Object> entry : all.entrySet()) {

			String entryType = entry.getValue().getClass().toString();
			if (entryType.endsWith("Atomic$Integer")) {
				aListener.dumpAtomicInteger(entry.getKey(),
						(org.mapdb.Atomic.Integer) entry.getValue());
			} else if (entryType.endsWith("Atomic$Long")) {
				aListener.dumpAtomicLong(entry.getKey(),
						(org.mapdb.Atomic.Long) entry.getValue());
			} else if (entryType.endsWith("KeySet")) {
				aListener.beginSet(entry.getKey());
				Set<?> set = (Set<?>) entry.getValue();
				for (Object setEntry : set) {
					aListener.newSetEntry(setEntry);
				}
				aListener.endSet();
			} else if (entryType.endsWith("Map")) {
				aListener.beginMap(entry.getKey());

				Map<?, ?> map = (Map<?, ?>) entry.getValue();
				for (Entry<?, ?> mapEntry : map.entrySet()) {
					aListener.newMapEntry(mapEntry);
				}

				aListener.endMap();
			} else {
				// fallback... dump the object as a simple toString.
				aListener.dumpUnknownObject(entry.getKey(), entry.getValue()
						.getClass().toString());
			}
		}

		aListener.dumpEnd();
	}

	/**
	 * Tests the dumper with a test db.
	 * 
	 * @param args
	 */
	@SuppressWarnings("boxing")
	public static void main(String args[]) {
		DB db = DBMaker.newMemoryDB().make();

		/*
		 * Create a simple set
		 */
		NavigableSet<Fun.Tuple2<String, Integer>> multiMap;

		multiMap = db.createTreeSet("test2")
				.serializer(BTreeKeySerializer.TUPLE2).make();

		multiMap.add(new Fun.Tuple2<>("aa", 1));
		multiMap.add(new Fun.Tuple2<>("aa", 2));
		multiMap.add(new Fun.Tuple2<>("aa", 34));
		multiMap.add(new Fun.Tuple2<>("bb", 1));
		multiMap.add(new Fun.Tuple2<>("aa", 77));

		// an atomic integer
		Atomic.Integer testInt = db.getAtomicInteger("testAtomicInteger");
		testInt.set(295);

		// an atomic long
		Atomic.Long testLong = db.getAtomicLong("testAtomicLong");
		testLong.set(99);

		// A simple set of integers.
		Set<Integer> hashSet = db.createHashSet("hashSetTestInt")
				.serializer(Serializer.INTEGER).make();
		hashSet.add(5);
		hashSet.add(921);
		hashSet.add(25);

		// a simple tree map
		BTreeMap<String, String> stStMap = db.createTreeMap("st-st-treemap")
				.keySerializer(BTreeKeySerializer.STRING)
				.valueSerializer(Serializer.STRING).make();

		stStMap.put("1984", "orwell");
		stStMap.put("anna karenina", "tolstoj");

		// an integer hash map with string as values and int as keys.
		HTreeMap<Integer, String> intStrMap = db.createHashMap("hashIntMap")
				.keySerializer(Serializer.INTEGER).hasher(Hasher.BASIC)
				.valueSerializer(Serializer.STRING).make();

		intStrMap.put(55, "stringto55");
		intStrMap.put(24, "24String");

		DbDumper.dump(db,
				new ConsoleDbDumper(new PrintWriter(System.out, true)));

		db.close();
	}

}
