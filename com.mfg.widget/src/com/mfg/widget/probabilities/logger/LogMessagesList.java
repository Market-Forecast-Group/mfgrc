package com.mfg.widget.probabilities.logger;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.garret.perst.Link;

public class LogMessagesList<T> implements List<T> {

	Link<T> messages;

	@Override
	public int size() {
		return 0;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public boolean contains(Object aO) {
		return false;
	}

	@Override
	public Iterator<T> iterator() {
		return null;
	}

	@Override
	public Object[] toArray() {
		return null;
	}

	@SuppressWarnings("hiding")
	@Override
	public <T> T[] toArray(T[] aA) {
		return null;
	}

	@Override
	public boolean add(T aE) {
		return false;
	}

	@Override
	public boolean remove(Object aO) {
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> aC) {
		return false;
	}

	@Override
	public boolean addAll(Collection<? extends T> aC) {
		return false;
	}

	@Override
	public boolean addAll(int aIndex, Collection<? extends T> aC) {
		return false;
	}

	@Override
	public boolean removeAll(Collection<?> aC) {
		return false;
	}

	@Override
	public boolean retainAll(Collection<?> aC) {
		return false;
	}

	@Override
	public void clear() {
		//
	}

	@Override
	public T get(int aIndex) {
		return null;
	}

	@Override
	public T set(int aIndex, T aElement) {
		return null;
	}

	@Override
	public void add(int aIndex, T aElement) {
		//
	}

	@Override
	public T remove(int aIndex) {
		return null;
	}

	@Override
	public int indexOf(Object aO) {
		return 0;
	}

	@Override
	public int lastIndexOf(Object aO) {
		return 0;
	}

	@Override
	public ListIterator<T> listIterator() {
		return null;
	}

	@Override
	public ListIterator<T> listIterator(int aIndex) {
		return null;
	}

	@Override
	public List<T> subList(int aFromIndex, int aToIndex) {
		return null;
	}

}
