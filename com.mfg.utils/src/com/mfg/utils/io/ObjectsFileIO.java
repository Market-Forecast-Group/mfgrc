package com.mfg.utils.io;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ObjectsFileIO {

	public static Object readInstance(String fileName) {
		try (ObjectInputStream oin = new ObjectInputStream(new FileInputStream(
				fileName))) {
			Object res = oin.readObject();
			return res;
		} catch (Exception e) {
			System.out.println(e.getCause());
			e.printStackTrace();
		}
		return null;
	}

	public static void writeInstance(Object instance1, String fileName) {
		try (ObjectOutputStream oout = new ObjectOutputStream(
				new FileOutputStream(fileName))) {

			oout.writeObject(instance1);
			oout.flush();
			oout.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private ObjectsFileIO() {
		super();
	}

	private static ObjectsFileIO instance = null;

	public static ObjectsFileIO getInstance() {
		return (instance == null) ? (instance = new ObjectsFileIO()) : instance;
	}

}
