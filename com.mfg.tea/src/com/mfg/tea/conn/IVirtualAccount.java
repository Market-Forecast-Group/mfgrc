package com.mfg.tea.conn;


/**
 * The IVirtualAccount interface is used to obtain information from a virtual
 * account. We don't use the in-sample/out-sample division as before, because
 * now the patterns don't have this division.
 * 
 * 
 * <p>
 * An account is like a "normal" account in double entry terminology, but it is
 * linked to a certain "material" entity, which is called a financial
 * instrument.
 * 
 * <p>
 * Each account has its own "equity": that means that every transaction is
 * actually a double entry transaction between a "good" account (thing) and an
 * implicitely know equity account.
 * 
 * <p>
 * Multi tea actually contains a simple double entry accounting system where
 * goods are bought and sold with transactions (the exact nature of transaction
 * is not relevant here, there is a broker, there is a market, we know it, but
 * we do not enforce it).
 * 
 * 
 * <p>
 * In the "old" application we had different kind of accounts, normal accounts
 * and leaf accounts. That was good, but maybe now it is not so useful.
 * 
 * <p>
 * The fact is that the stock accounts can have parent stock accounts of the
 * <b>same</b> kind. It is like having a local warehouse of pens, a regional
 * warehouse of pens, a national warehouse of pens. We all count "pens" but each
 * regional warehouse has its own history, has recorded its own transactions,
 * etc.
 * 
 * <p>
 * What I do not yet have decided is if the economical accounts form a different
 * tree. If I have two parallel trees. I think not, but this has to be carefully
 * designed.
 * 
 * 
 */
public interface IVirtualAccount {

	/**
	 * Every virtual account, in theory, can have many children.
	 */
	public int getNumberOfChildren();

	/**
	 * @return the child at a particular index.
	 */
	public IVirtualAccount getChildAt(int index);

	// /**
	// * @return the total statistics. Short + Long
	// */
	// public AccountStats getTotalStatistics();
	//
	// /**
	// * @return the long statistics.
	// */
	// public AccountStats getLongStatistics();
	//
	// /**
	// * @return the short statistics of this account
	// */
	// public AccountStats getShortStatistics();

	/**
	 * @return a clone of this object, useful to have a separate counting for
	 *         in-sample, out-sample statistics.
	 * 
	 *         <p>
	 *         If this is already a snapshot it simply returns itself.
	 */
	public IVirtualAccount snapshot();

}
