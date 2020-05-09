package com.mfg.utils.concurrent;

import java.util.ArrayDeque;
import java.util.ArrayList;

/**
 * An {@link ArrayList} which is a lazy writer, because all the mutative
 * operations happens at the next call to {@link #iterateCode(RunnableItem)}.
 * 
 * <p>
 * The list is not synchronized, only the removing and adding lists are
 * synchronized.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public class LazyWriteArrayList<E> {

	/**
	 * an object through which some code can be run, the code is generic and
	 * takes as a unique argument an item of the collection.
	 * 
	 * <p>
	 * It is like a visitor pattern.
	 * 
	 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
	 * 
	 * @param <E>
	 */
	public abstract static class RunnableItem<E> {
		public abstract void run(E aItem);
	}

	/**
	 * This is the normal list. It is modified only at the start of the
	 * #iterator method, we may add it in the front or in the back.
	 */
	private ArrayDeque<E> _list = new ArrayDeque<>();

	private ArrayList<E> _addendumList = new ArrayList<>();

	private ArrayList<E> _addendumHead = new ArrayList<>();

	private ArrayList<E> _removingList = new ArrayList<>();

	private void _synchLists() {
		/*
		 * When this method is entered the _list has already been synchronized.
		 */
		synchronized (_addendumList) {
			for (E item : _addendumList) {
				if (!_list.contains(item))
					_list.add(item);
			}
			_addendumList.clear();
		}
		synchronized (_addendumHead) {
			for (E item : _addendumHead) {
				if (!_list.contains(item))
					_list.addFirst(item);
			}
			_addendumHead.clear();
		}
		synchronized (_removingList) {
			for (E item : _removingList) {
				_list.remove(item);
			}
			_removingList.clear();
		}

	}

	/**
	 * iterates the {@link Runnable} over all the elements of the sequence.
	 * 
	 * <p>
	 * This is equivalent to have
	 * 
	 * <code>
	 * for (item : _list){
	 *  item.aCode();
	 * }
	 * </code>
	 * 
	 * @param aCode
	 */
	public void iterateCode(RunnableItem<E> aCode) {
		synchronized (_list) {
			_synchLists();
			for (E item : _list) {
				aCode.run(item);
			}
		}

	}

	public void add(E item) {
		synchronized (_addendumList) {
			_addendumList.add(item);
		}
	}

	public void remove(E item) {
		synchronized (_removingList) {
			_removingList.add(item);
		}
	}

	/**
	 * Adds the item to the head of the queue.
	 * 
	 * @param item
	 */
	public void addFirst(E item) {
		synchronized (_addendumHead) {
			_addendumHead.add(item);
		}
	}

	public int size() {
		synchronized (_list) {
			return _list.size() + _addendumHead.size() + _addendumList.size()
					- _removingList.size();
		}
	}
}
