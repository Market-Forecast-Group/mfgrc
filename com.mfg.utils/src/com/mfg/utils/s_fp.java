package com.mfg.utils;

//import static com.marketforecastgroup.proc.src.s_sys.*;
//import com.marketforecastgroup.proc.inc.i_fp.Field_fp;

import static com.mfg.utils.Utils.debug_var;
import static com.mfg.utils.Utils.exit_ue;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.mfg.utils.i_fp.Field_fp;

/**
 * This module stores the functions to deal with generic "fingerprints" of java
 * objects, which are built using java reflection.
 */
public final class s_fp {

	/**
	 * Only a module, don't create me.
	 */
	private s_fp() {
	}

	private enum TEST_ENUM {
		AAAAA, BBBBB, CCCCC
	}

	static class super_of_test {
		@SuppressWarnings("unused")
		private long answer = 42;
		@SuppressWarnings("unused")
		private static int super_static_int = 33;
	}

	@SuppressWarnings("unused")
	static class class_test extends super_of_test {

		private boolean bbb = true;
		private TEST_ENUM tef = TEST_ENUM.CCCCC;

		public String ss = "SS";

		private int values[] = new int[5];

		{
			for (int i = 0; i < values.length; ++i) {
				values[i] = i;
			}
		}
	}

	@SuppressWarnings({ "unused" })
	public static void test_fp() {
		// class_test ct = new class_test();
		// long ll = get_raw_fp(ct);
		// long ll = get_raw_fp(new Integer(33));

		// int arr[] = new int[3];
		// arr[0] = 11;
		// arr[1] = 2;
		// arr[2] = 7;
		// long ll = get_raw_fp(arr);

		// TEST_ENUM tef = TEST_ENUM.CCCCC;
		// long ll = get_raw_fp(tef);

		long ll, ll1;
		ll = 0;
		ll1 = 0;

		// long ll = get_raw_fp(new String("lino"));
		// long ll1 = get_raw_fp(new String("lino"));

		// if (ll != ll1) {
		// exit_s("PROBLEMS");
		// }

		Hashtable<Integer, String> ht1 = new Hashtable<Integer, String>();
		ht1.put(new Integer(33), "hello");
		ht1.put(new Integer(42), "answer");

		ll = get_raw_fp(ht1);

		@SuppressWarnings("unchecked")
		Hashtable<Integer, String> ht2 = (Hashtable<Integer, String>) ht1
				.clone();

		ht2.remove(new Integer(42));
		ht2.put(new Integer(42), "answer");
		// ht2.put(new Integer(192), "answeri");

		Field_fp ffp1 = get_detail_fp(ht1);
		Field_fp ffp2 = get_detail_fp(ht2);
		print_field_fp(ffp2);

		// debug_var(108887, "The answer is " , ll, " and " , ffp.fingerprint);

		Field_fp diff = get_first_difference(ffp1, ffp2);

		debug_var(334476, "The difference should be null = ", diff);
	}

	public static Field_fp get_detail_fp(Object o) {
		Field_fp ffp = new Field_fp();
		try {
			long fp = _get_raw_fp_ex(o, new HashSet<>(), ffp);
			assert (fp == ffp.fingerprint);
		} catch (Exception e) {
			exit_ue(e);
		}

		return ffp;
	}

	public static long get_raw_fp(Object o) {
		try {
			return _get_raw_fp_ex(o, new HashSet<>(), null);
		} catch (Exception e) {
			exit_ue(e);
		}
		return -1;
	}

	public static Field_fp get_first_difference(Field_fp fp1, Field_fp fp2) {
		return get_first_difference_rec(fp1, fp2, "/");
	}

	/**
	 * @return the first difference between two detail fingerprints. The
	 *         different is in the first "leaf". The difference is from the
	 *         first object.
	 */
	@SuppressWarnings("boxing")
	public static Field_fp get_first_difference_rec(Field_fp fp1, Field_fp fp2,
			String prefix) {
		if (fp1.fingerprint == fp2.fingerprint) {
			// end of recursion, we don't have to go down
			// debug_var(657502, prefix , " end of recursion, fp1 ",
			// fp1.fingerprint , " fp2 ",
			// fp2.fingerprint);
			return null;
		}

		if (fp1.detail.size() == 0) {

			// I have a leaf...
			if (fp2.detail.size() != 0) {
				debug_var(155226, prefix,
						" End of recursion, I have one leaf and one not");
				return fp1;
			}

			if (fp1.fingerprint != fp2.fingerprint) {
				debug_var(312380, prefix, " two diff leaves fp1 ",
						fp1.fingerprint, " fp2 ", fp2.fingerprint);
				return fp1;
			}
			// debug_var(527805, prefix , " Two equal leaf-fingerprints");
			return null;
		}

		Field_fp diff = null;
		Field_fp first_diff = null;

		for (String f1 : fp1.detail.keySet()) {
			// debug_var(783448, prefix, " diff for key [", f1 , "]");

			Field_fp fd1 = fp1.detail.get(f1);
			Field_fp fd2 = fp2.detail.get(f1);

			if (fd2 == null) {
				debug_var(446427, prefix + f1, " Null fd2 , fd1 has fp= ",
						fd1.fingerprint);
				diff = fd1;
			} else {
				diff = get_first_difference_rec(fd1, fd2, prefix + f1 + "/");
			}

			if (diff != null) {
				// debug_var(20629, prefix, " breaking with a difference!");
				first_diff = diff;
				break; // end of loop
			}
		}

		return first_diff;
	}

	@SuppressWarnings("boxing")
	public static void print_field_fp(Field_fp afp) {
		print_field_fp_lev(afp, "/");
		debug_var(513962, "fingerprint TOTAL IS ", afp.fingerprint);
	}

	@SuppressWarnings("boxing")
	private static void print_field_fp_lev(Field_fp afp, String prefix) {
		String path;
		for (String st : afp.detail.keySet()) {
			Field_fp ffp = afp.detail.get(st);
			path = prefix + st + "/";
			print_field_fp_lev(ffp, path);
			debug_var(396272, "[", path, "]", " fp = ", ffp.fingerprint);
		}
	}

	private static long _get_raw_fp_for_class(Class<?> cl, Object o,
			HashSet<Object> obs, Field_fp detail) throws IllegalAccessException {
		// debug_var(931271, "fp for object of class " , cl , " --START");

		long fp = 0;
		Field[] flds = cl.getDeclaredFields();

		for (Field fd : flds) {
			fd.setAccessible(true);
			// debug_var(813386, "fp for field " , fd.getName());

			long fpf = 0;

			if (Modifier.isTransient(fd.getModifiers())) {
				// no transient data
			} else if (fd.isSynthetic()) {
				// debug_var(57994, "no fp, is synthetic");
			} else if (Modifier.isStatic(fd.getModifiers())) {
				// no fingerprint for static fields...
				// debug_var(714000, "no fp for static " , fd.getName());
			} else {
				Object ob = fd.get(o);
				Field_fp detail_f = null;
				if (detail != null) {
					detail_f = new Field_fp();
					detail.detail.put(fd.getName(), detail_f);
				}
				fpf = _get_raw_fp_ex(ob, obs, detail_f);
			}

			if (fpf == 0) {
				fpf = -1;
			}
			fp += fpf;
			// if (fpf != 0){
			// debug_var(813386, "fp for field " , fd.getName(), " end = ", fpf
			// , " subtotal " , fp);
			// }
		}

		// debug_var(352887, "fp for object of class " , cl , " -- end : ", fp);
		return fp;

	}

	private static long _get_raw_fp_enum(Object ob, Field_fp detail) {
		long fpe = -1;
		Object ec[] = ob.getClass().getEnumConstants();

		int i;
		for (i = 0; i < ec.length; ++i) {
			if (ob.equals(ec[i])) {
				// debug_var(512713, "found correspondence for " , ec[i]);
				fpe = i;
				break;
			}
		}

		if (fpe < 0) {
			assert (false) : ("should not happen, not found correspondence for " + ob);
		}

		if (detail != null) {
			detail.fingerprint = fpe;
		}

		return fpe;
	}

	/**
	 * The array is an object!
	 */
	private static long _get_raw_fp_array(Object ob, HashSet<Object> obs,
			Field_fp detail) throws IllegalAccessException {
		long fpa = 0;
		int length = Array.getLength(ob);
		// debug_var(491870, "The object is ... ", ob, " length " , length);

		for (int i = 0; i < length; ++i) {

			Object arr_el = Array.get(ob, i);
			Field_fp detail_f = null;
			if (detail != null) {
				detail_f = new Field_fp();
				detail.detail.put(new Integer(i).toString(), detail_f);
			}
			long fpae = _get_raw_fp_ex(arr_el, obs, detail_f);
			// debug_var(452744, "Fp for array element ", i, " is " , fpae);
			fpa += fpae;
		}

		return fpa;
	}

	/**
	 * @param detail
	 *            can be null, in this case we don't have detail...
	 * @return the raw fingerprint of an object using reflection.
	 */
	@SuppressWarnings("boxing")
	private static long _get_raw_fp_ex(Object o, HashSet<Object> obs,
			Field_fp detail) throws IllegalAccessException {

		long res = 0;

		if (o != null) {
			// check primitive types!
			if (o instanceof Integer) {
				res = (Integer) o;
			} else if (o instanceof Boolean) {
				boolean bb = (Boolean) o;
				res = bb ? 1 : -1;
			} else if (o instanceof Long) {
				res = (Long) o;
			} else if (o instanceof Character) {
				res = ((Character) o).charValue();
			} else if (o instanceof Float) {
				res = Double.doubleToRawLongBits((Float) o);
			} else if (o instanceof Double) {
				res = Double.doubleToRawLongBits((Double) o);
			} else if (o instanceof Byte) {
				res = (Byte) o;
			} else if (o instanceof Short) {
				res = (Short) o;
			} else if (!obs.contains(o)) {

				obs.add(o);

				// First check if the object is a primitive one!
				if (o.getClass().isArray()) {
					// debug_var(482857, "I have an array!");
					res = _get_raw_fp_array(o, obs, detail);
				} else if (o.getClass().isEnum()) {
					// debug_var(470870, "I have an enum");
					res = _get_raw_fp_enum(o, detail);
				} else if (o instanceof Map) {
					// the fingerprint of the hashtable is computed differently
					Map<?, ?> ht = (Map<?, ?>) o;
					res = _get_hash_table_fingerprint(ht, obs, detail);
				} else if (o instanceof String) {
					res = o.hashCode();
					// the hash code of strings is "normal", equal strings have
					// equal hash codes.
				} else if (o instanceof List) {
					List<?> al = (List<?>) o;
					res = _get_array_list_fingerprint(al, obs, detail);
				} else {
					// A normal object!
					Class<?> cl = o.getClass();
					do {
						// debug_var(293952, "Getting fp for class ",
						// cl.getName(), " object ", o);
						res += _get_raw_fp_for_class(cl, o, obs, detail);
						cl = cl.getSuperclass();
					} while (cl != null);
				}

			}
			// else
			// {
			// debug_var(382374, "Object ", o, " is already done!");
			// }
		} else {

			// debug_var(921343, "Giving me a null pointer! detail size: ",
			// detail != null ? detail.detail.size() : " 0 detail ");
		}

		if (detail != null) {
			detail.fingerprint = res;
		}
		return res;
	}

	private static long _get_array_list_fingerprint(List<?> al,
			HashSet<Object> obs, Field_fp detail) throws IllegalAccessException {
		// debug_var(325752, "Array list start");
		long res = 0;
		int i = 0;
		for (Object ael : al) {
			Field_fp detail_f = null;
			String key_string = "[" + new Integer(i++).toString() + "]";
			if (detail != null) {
				detail_f = new Field_fp();
				detail.detail.put(key_string, detail_f);
			}
			long res_el = _get_raw_fp_ex(ael, obs, detail_f);
			// debug_var(309880, key_string, " ", ael ," of ", ael.getClass() ,
			// " = " , res_el);
			res += res_el;
		}
		// debug_var(325752, "Array list end ", res);
		return res;
	}

	private static long _get_hash_table_fingerprint(Map<?, ?> ht,
			HashSet<Object> obs, Field_fp detail) throws IllegalAccessException {

		// debug_var(708543, "hash table fingerprint START");
		long res = 0;
		for (Object o : ht.keySet()) {

			Field_fp detail_f = null;
			if (detail != null) {
				detail_f = new Field_fp();
				String key_string = "key-" + o.toString();
				detail.detail.put(key_string, detail_f);
				// debug_var(933861, "fp for " , key_string);

			}
			res += _get_raw_fp_ex(o, obs, detail_f);

			Object val = ht.get(o);

			if (detail != null) {
				detail_f = new Field_fp();
				String value_string = "valueof[" + o.toString() + "]";
				detail.detail.put(value_string, detail_f);
				// debug_var(596639, "fp for ", value_string);
			}
			res += _get_raw_fp_ex(val, obs, detail_f);
		}

		// debug_var(365778, "HASH TABLE FINGERPRINT END " , res);
		return res;
	}
}