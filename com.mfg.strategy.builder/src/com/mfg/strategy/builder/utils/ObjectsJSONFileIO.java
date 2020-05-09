/*
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 *
 * $Id: $
 */
/**
 * @author <a href="mailto:gardero@gmail.com">Enrique Matos</a>, MFG
 *
 * @version $Revision: $ $Date: $
 */

package com.mfg.strategy.builder.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;

import com.mfg.utils.GenericIdentifier;
import com.mfg.utils.XmlIdentifier;

public class ObjectsJSONFileIO {

	@SuppressWarnings("unchecked")
	public <T> T readInstance(String fileName) {
		File file = new File(fileName);
		String aXML = "";
		try {
			aXML = new String(Files.readAllBytes(file.toPath()));
		} catch (IOException ex1) {
			ex1.printStackTrace();
			return null;
		}
		Thread currentThread = Thread.currentThread();
		ClassLoader oldLoader = currentThread.getContextClassLoader();
		try
		{
			currentThread.setContextClassLoader(this.getClass().getClassLoader());
			return (T) XmlIdentifier.createFromString(aXML);
		} finally
		{
			currentThread.setContextClassLoader(oldLoader);
		}

	}


	@Deprecated
	public <T> T readInstanceFromJSONFile(String fileName) {
		File file = new File(fileName);
		String aJSON = "";
		try {
			aJSON = new String(Files.readAllBytes(file.toPath()));
		} catch (IOException ex1) {
			ex1.printStackTrace();
			return null;
		}
		return readModelFromJSON(aJSON);

	}


	@SuppressWarnings("unchecked")
	public <T> T readModelFromJSON(String json) {
		Thread currentThread = Thread.currentThread();
		ClassLoader oldLoader = currentThread.getContextClassLoader();
		try
		{
			currentThread.setContextClassLoader(this.getClass().getClassLoader());
			return (T) GenericIdentifier.createFromString(json);
		} finally
		{
			currentThread.setContextClassLoader(oldLoader);
		}
	}


	public static <T> void writeInstance(T aInstance, String fileName) {
		File file = new File(fileName);
		try {
			if (!file.exists()) {
				file.getParentFile().mkdirs();
				file.createNewFile();
			}

			String text = "";
			if (aInstance instanceof XmlIdentifier) {
				text = ((XmlIdentifier) aInstance).serializeToString();
			} else if (aInstance instanceof GenericIdentifier) {
				text = ((GenericIdentifier) aInstance).toJSONString();
			}
			try (BufferedWriter wr = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(fileName)))) {
				wr.write(text);
				wr.close();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	
	private ObjectsJSONFileIO() {
		super();
	}

	private static ObjectsJSONFileIO instance = null;


	public static ObjectsJSONFileIO getInstance() {
		return (instance == null) ? (instance = new ObjectsJSONFileIO()) : instance;
	}

}
