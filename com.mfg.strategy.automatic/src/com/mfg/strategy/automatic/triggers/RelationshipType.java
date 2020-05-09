
package com.mfg.strategy.automatic.triggers;

/**
 * the type of relationship that is being checked in the relationship trigger.
 * <p>
 * Possible values are:
 * <ul>
 * <li> {@code Contrarian}, for an up swing the relationship is contrarian iff TH<sub>0</sub> >= TH<sub>-1</sub>, and for a down swing iff
 * TH<sub>0</sub> <= TH<sub>-1</sub>.
 * <li> {@code NonContrarian}, represents that the TH relation is not Contrarian.
 * <li> {@code None}, represents no check of relation.
 * </ul>
 * 
 * @author gardero
 * 
 */
public enum RelationshipType {
	/**
	 * for an up swing the relationship is contrarian iff TH<sub>0</sub> >= TH<sub>-1</sub>, and for a down swing iff TH<sub>0</sub> <=
	 * TH<sub>-1</sub>.
	 */
	Contrarian {
		@Override
		public boolean check(boolean aPriceDecreasing, double aTH0, double aTHm1) {
			if (!aPriceDecreasing)
				return aTH0 >= aTHm1;
			return aTH0 <= aTHm1;
		}
	},
	/**
	 * represents that the TH relation is not Contrarian.
	 */
	NonContrarian {
		@Override
		public boolean check(boolean aPriceDecreasing, double aTH0, double aTHm1) {
			return !Contrarian.check(aPriceDecreasing, aTH0, aTHm1);
		}
	},
	/**
	 * represents no check of relation.
	 */
	None;

	/**
	 * checks if this relation is present.
	 * 
	 * @param aPriceDecreasing
	 *            if the price is decreasing or not.
	 * @param aTH0
	 *            the TH<sub>0</sub>.
	 * @param aTHm1
	 *            the TH<sub>-1</sub>
	 * @return true is this relationship is present.
	 */
	@SuppressWarnings("static-method")//It's overwritten in this class.
	public boolean check(boolean aPriceDecreasing, double aTH0, double aTHm1) {
		return true;
	}
}
