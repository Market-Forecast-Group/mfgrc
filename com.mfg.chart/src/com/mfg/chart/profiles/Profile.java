/*
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 *
 * @author <a href="mailto:boniatillo@gmail.com">Arian Fornaris</a>, MFG
 *
 * @version $Revision$: $Date$:
 * $Id$:
 */

package com.mfg.chart.profiles;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author arian
 * 
 */
@XmlRootElement(name = "ChartProfile")
@XmlAccessorType(XmlAccessType.FIELD)
public class Profile implements Cloneable, Serializable {

	private static final long serialVersionUID = 1L;

	@XmlAccessorType(XmlAccessType.FIELD)
	public static class IntValue implements Serializable {
		private static final long serialVersionUID = 1L;
		public String key;
		public int value;
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	public static class FloatValue implements Serializable {
		private static final long serialVersionUID = 1L;
		public String key;
		public float value;
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	public static class FloatArrayValue implements Serializable {
		private static final long serialVersionUID = 1L;
		public String key;
		@XmlList
		public float[] value;
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	public static class StringValue implements Serializable {
		private static final long serialVersionUID = 1L;
		public String key;
		public String value;
	}

	private final List<IntValue> intValues = new ArrayList<>();
	private final List<FloatValue> floatValues = new ArrayList<>();
	private final List<FloatArrayValue> floatArrayValues = new ArrayList<>();
	private final List<StringValue> strValues = new ArrayList<>();
	private String name;

	public Profile() {
		// JAXB
	}

	public Profile(final String name1) {
		this();
		this.name = name1;
	}

	public List<IntValue> getIntValues() {
		return intValues;
	}

	public List<FloatArrayValue> getFloatArrayValues() {
		return floatArrayValues;
	}

	public List<FloatValue> getFloatValues() {
		return floatValues;
	}

	public List<StringValue> getStringValues() {
		return strValues;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public synchronized Profile clone() {
		final ByteArrayOutputStream o = new ByteArrayOutputStream();
		try (final ObjectOutputStream out = new ObjectOutputStream(o)) {
			out.writeObject(this);
			final ObjectInputStream in = new ObjectInputStream(
					new ByteArrayInputStream(o.toByteArray()));
			return (Profile) in.readObject();
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	public boolean sameSettings(Profile other) {
		boolean b1 = intValues.size() == other.intValues.size();
		boolean b2 = strValues.size() == other.strValues.size();
		boolean b3 = floatArrayValues.size() == other.floatArrayValues.size();
		boolean b4 = floatValues.size() == other.floatValues.size();

		if (!b1 || !b2 || !b3 || !b4) {
			return false;
		}

		{
			ArrayList<IntValue> l1 = new ArrayList<>(intValues);
			Comparator<IntValue> cmp = new Comparator<IntValue>() {

				@Override
				public int compare(IntValue o1, IntValue o2) {
					return o1.key.compareTo(o2.key);
				}
			};
			Collections.sort(l1, cmp);
			ArrayList<IntValue> l2 = new ArrayList<>(other.intValues);
			Collections.sort(l2, cmp);

			for (int i = 0; i < l1.size(); i++) {
				if (!l1.get(i).key.equals(l2.get(i).key)
						|| !(l1.get(i).value == l2.get(i).value)) {
					return false;
				}
			}
		}

		{
			ArrayList<StringValue> l1 = new ArrayList<>(strValues);
			Comparator<StringValue> cmp = new Comparator<StringValue>() {

				@Override
				public int compare(StringValue o1, StringValue o2) {
					return o1.key.compareTo(o2.key);
				}
			};
			Collections.sort(l1, cmp);
			ArrayList<StringValue> l2 = new ArrayList<>(other.strValues);
			Collections.sort(l2, cmp);

			for (int i = 0; i < l1.size(); i++) {
				if (!l1.get(i).key.equals(l2.get(i).key)
						|| !(l1.get(i).value.equals(l2.get(i).value))) {
					return false;
				}
			}
		}

		{
			ArrayList<FloatValue> l1 = new ArrayList<>(floatValues);
			Comparator<FloatValue> cmp = new Comparator<FloatValue>() {

				@Override
				public int compare(FloatValue o1, FloatValue o2) {
					return o1.key.compareTo(o2.key);
				}
			};
			Collections.sort(l1, cmp);
			ArrayList<FloatValue> l2 = new ArrayList<>(other.floatValues);
			Collections.sort(l2, cmp);

			for (int i = 0; i < l1.size(); i++) {
				if (!l1.get(i).key.equals(l2.get(i).key)
						|| !(l1.get(i).value == l2.get(i).value)) {
					return false;
				}
			}
		}

		{
			ArrayList<FloatArrayValue> l1 = new ArrayList<>(floatArrayValues);
			Comparator<FloatArrayValue> cmp = new Comparator<FloatArrayValue>() {

				@Override
				public int compare(FloatArrayValue o1, FloatArrayValue o2) {
					return o1.key.compareTo(o2.key);
				}
			};
			Collections.sort(l1, cmp);
			ArrayList<FloatArrayValue> l2 = new ArrayList<>(
					other.floatArrayValues);
			Collections.sort(l2, cmp);

			for (int i = 0; i < l1.size(); i++) {
				String k1 = l1.get(i).key;
				String k2 = l2.get(i).key;
				if (!k1.equals(k2) || !sameFloatArray(l1.get(i), l2.get(i))) {
					return false;
				}
			}
		}

		return true;
	}

	private static boolean sameFloatArray(FloatArrayValue v1, FloatArrayValue v2) {
		if (v1.value.length != v2.value.length) {
			return false;
		}
		for (int i = 0; i < v1.value.length; i++) {
			String a = round(v1, i);
			String b = round(v2, i);
			if (!a.equals(b)) {
				return false;
			}
		}
		return true;
	}

	private static String round(FloatArrayValue v1, int i) {
		String str = Float.toString(v1.value[i]);
		return str.substring(0, Math.min(4, str.length()));
	}

	public static Profile fromXML(String xml) throws JAXBException {
		final JAXBContext c = JAXBContext.newInstance(Profile.class);
		final Unmarshaller um = c.createUnmarshaller();
		Object obj = um.unmarshal(new StringReader(xml));
		return (Profile) obj;
	}

	public String toXML() throws JAXBException {
		final JAXBContext c = JAXBContext.newInstance(Profile.class);
		final Marshaller ma = c.createMarshaller();
		ma.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		final StringWriter writer = new StringWriter();
		ma.marshal(this, writer);
		return writer.toString();
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name1
	 *            the name to set
	 */
	public void setName(final String name1) {
		this.name = name1;
	}

	public synchronized void putFloatArray(final String key, final float[] array) {
		for (final FloatArrayValue v : floatArrayValues) {
			if (v.key.equals(key)) {
				v.value = array;
				return;
			}
		}
		final FloatArrayValue prop = new FloatArrayValue();
		prop.key = key;
		prop.value = array;
		floatArrayValues.add(prop);
	}

	public synchronized float[] getFloatArray(final String key,
			final float[] defaultArray) {
		for (final FloatArrayValue v : floatArrayValues) {
			if (v.key.equals(key)) {
				return v.value;
			}
		}
		return defaultArray;
	}

	public synchronized void putBoolean(final String key, final boolean b) {
		putInt(key, b ? 1 : 0);
	}

	public synchronized boolean getBoolean(final String key,
			final boolean defaultBool) {
		return getInt(key, defaultBool ? 1 : 0) == 1;
	}

	public synchronized boolean containsKey(String key) {

		for (IntValue v : intValues) {
			if (v.key.equals(key)) {
				return true;
			}
		}
		for (FloatArrayValue v : floatArrayValues) {
			if (v.key.equals(key)) {
				return true;
			}
		}
		for (FloatValue v : floatValues) {
			if (v.key.equals(key)) {
				return true;
			}
		}
		for (StringValue v : strValues) {
			if (v.key.equals(key)) {
				return true;
			}
		}

		return false;
	}

	public void removeKey(String key) {
		Object del = null;
		for (IntValue v : intValues) {
			if (v.key.equals(key)) {
				del = v;
				break;
			}
		}
		if (del != null) {
			intValues.remove(del);
		}

		del = null;
		for (FloatValue v : floatValues) {
			if (v.key.equals(key)) {
				del = v;
				break;
			}
		}
		if (del != null) {
			floatArrayValues.remove(del);
		}

		del = null;
		for (FloatValue v : floatValues) {
			if (v.key.equals(key)) {
				del = v;
				break;
			}
		}
		if (del != null) {
			floatValues.remove(del);
		}

		del = null;
		for (StringValue v : strValues) {
			if (v.key.equals(key)) {
				del = v;
				break;
			}
		}
		if (del != null) {
			strValues.remove(del);
		}
	}

	public synchronized void putInt(final String key, final int i) {
		for (final IntValue v : intValues) {
			if (v.key.equals(key)) {
				v.value = i;
				return;
			}
		}
		final IntValue prop = new IntValue();
		prop.key = key;
		prop.value = i;
		intValues.add(prop);
	}

	public synchronized int getInt(final String key, final int defaultInt) {
		for (final IntValue v : intValues) {
			if (v.key.equals(key)) {
				return v.value;
			}
		}

		return defaultInt;
	}

	public synchronized void putFloat(final String key, final float f) {
		for (final FloatValue v : floatValues) {
			if (v.key.equals(key)) {
				v.value = f;
				return;
			}
		}
		final FloatValue prop = new FloatValue();
		prop.key = key;
		prop.value = f;
		floatValues.add(prop);
	}

	public synchronized float getFloat(final String key,
			final float defaultFloat) {
		for (final FloatValue v : floatValues) {
			if (v.key.equals(key)) {
				return v.value;
			}
		}

		return defaultFloat;
	}

	public synchronized void putString(final String key, final String str) {
		for (final StringValue v : strValues) {
			if (v.key.equals(key)) {
				v.value = str;
				return;
			}
		}
		final StringValue prop = new StringValue();
		prop.key = key;
		prop.value = str;
		strValues.add(prop);
	}

	public synchronized String getString(final String key,
			final String defaultStr) {
		for (final StringValue v : strValues) {
			if (v.key.equals(key)) {
				return v.value;
			}
		}
		return defaultStr;
	}

	public synchronized void clear() {
		this.floatArrayValues.clear();
		this.floatValues.clear();
		this.intValues.clear();
		this.strValues.clear();
	}

	@Override
	public String toString() {
		return getName() + ":" + super.toString();
	}

	/**
	 * @param currentProfile
	 */
	public void updateFrom(Profile profile) {
		floatArrayValues.clear();
		floatArrayValues.addAll(profile.floatArrayValues);

		floatValues.clear();
		floatValues.addAll(profile.floatValues);

		intValues.clear();
		intValues.addAll(profile.intValues);

		strValues.clear();
		strValues.addAll(profile.strValues);
	}

	public boolean isEmpty() {
		return floatArrayValues.isEmpty() && floatValues.isEmpty()
				&& intValues.isEmpty() && strValues.isEmpty();
	}
}
