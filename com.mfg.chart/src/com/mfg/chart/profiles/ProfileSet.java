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

import static java.lang.System.out;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author arian
 * 
 */
@XmlRootElement(name = "ChartProfilesSet")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProfileSet {
	public final static String DEFAULT_PROFILE_NAME = "Default";

	private final List<Profile> profiles = new ArrayList<>();

	public ProfileSet() {
	}

	public void addProfile(final Profile profile) {
		profiles.add(profile);
	}

	public void removeProfile(final Profile profile) {
		profiles.remove(profile);
	}

	public Profile findProfile(final String name) {
		for (final Profile p : profiles) {
			if (p.getName().equals(name)) {
				return p;
			}
		}
		return null;
	}

	public static ProfileSet fromXML(final String xml) throws JAXBException {
		final JAXBContext c = JAXBContext.newInstance(ProfileSet.class);
		final Unmarshaller um = c.createUnmarshaller();
		final ProfileSet set = (ProfileSet) um.unmarshal(new StringReader(xml));
		return set;
	}

	public String toXML() throws JAXBException {
		final JAXBContext c = JAXBContext.newInstance(ProfileSet.class);
		final Marshaller ma = c.createMarshaller();
		ma.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		final StringWriter writer = new StringWriter();
		ma.marshal(this, writer);
		return writer.toString();
	}

	public static void main(final String[] args) throws JAXBException {
		final Profile profile = new Profile("Profile 1");
		profile.putInt("pepe.juana", 10);
		profile.putInt("pepe.juana", 11);
		profile.putFloatArray("yeya", new float[] { 1, 2, 3 });
		final ProfileSet set = new ProfileSet();
		set.addProfile(profile);
		out.println(set.toXML());
	}

	/**
	 * @return
	 */
	public String newProfileName() {
		int i = 1;
		while (true) {
			final String name = "Profile " + i;
			if (!existsProfile(name)) {
				return name;
			}
			i++;
		}
	}

	/**
	 * @param newText
	 * @return
	 */
	public boolean existsProfile(final String name) {
		for (final Profile p : profiles) {
			if (p.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return
	 */
	public Profile[] toArray() {
		return profiles.toArray(new Profile[profiles.size()]);
	}
}
