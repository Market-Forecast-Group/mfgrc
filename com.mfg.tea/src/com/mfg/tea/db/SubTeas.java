package com.mfg.tea.db;

import com.mfg.tea.db.Db.OBJECTS;

/**
 * This is the package (as it is not instantiated) which controls the presence
 * of the subteas in the system.
 * 
 * <p>
 * Each subtea has an identifier, and to each subtea the system assigns an
 * integer which is the sub tea id which is then used to associate to this
 * subtea a list of trading sessions.
 * 
 * <p>
 * The class is not meant to be called from the outside.
 * 
 * 
 * tea_33 = test
 * 
 * tea_39 = 299
 * 
 * 
 * in this way I have the tea set inside the map. My idea is to have the map
 * always self contained.
 * 
 * The SubTeas are the roots of the tree, so they have not a direct
 * relationship, I can also be prepared to an open set.
 * 
 * teaSet_test = 1
 * 
 * 
 * then the trading session
 * 
 * ts_295.subTea = 1
 * 
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
class SubTeas {

	private static final String TRADING_SESSIONS_ARRAY = "trSes";
	private static final String CREATED_FIELD = "created";
	// private static Atomic.Integer _stid;
	private static Db _instance;

	/**
	 * The method will start a trading session for this teaId.
	 * 
	 * 
	 * @param aTeaId
	 *            the id which has connected. This is a human readable string,
	 *            like "Tommy", this means that the MFG that has connected has
	 *            requested the virtual tea named "Tommy".
	 */
	public static int newSubTeaConnected(String aTeaId) {

		String subTeaRootParentKey = Db.OBJECTS.ROOT_OBJECT.getPrefix()
				+ aTeaId;

		String id = _instance._teaMap.get(subTeaRootParentKey);
		if (id != null) {
			return Integer.parseInt(id);
		}

		/*
		 * The root has not a id, because it is a singleton.
		 */
		int newTeaId = DbKeyFieldHelper.getNewObjectId(OBJECTS.SUB_TEA_OBJECT);
		_instance._teaMap.put(subTeaRootParentKey, Integer.toString(newTeaId));

		/*
		 * Then I put the values of this subtea, I can have for example the
		 * first creation of it
		 */
		String now = Db.currentTimeAsDbString();

		DbKeyFieldHelper.putFieldValue(OBJECTS.SUB_TEA_OBJECT, newTeaId,
				CREATED_FIELD, now);
		return newTeaId;
	}

	public static void initialize(Db aInstance) {
		_instance = aInstance;
	}

	public static void newTradingSession(int subTeaId, int ts) {
		DbKeyFieldHelper.putArrayValue(Db.OBJECTS.SUB_TEA_OBJECT, subTeaId,
				TRADING_SESSIONS_ARRAY, Integer.toString(ts));

	}

}
