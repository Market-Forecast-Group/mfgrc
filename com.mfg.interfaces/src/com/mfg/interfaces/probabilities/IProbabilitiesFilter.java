package com.mfg.interfaces.probabilities;

public interface IProbabilitiesFilter {

	public enum CNCDir {
		Contr, NContr, Both;
	}

	public enum ProbVer {
		Targets, SCT, ALL;
	}

	public abstract boolean aceptsKey(ProbabilitiesKey k);

	public abstract boolean aceptsProbVer(ProbVer ver);

	public abstract int getScale();

	public abstract boolean aceptsScale(int ascale);

	public abstract void setScale(int aScale);

	public abstract int getPID();

	public abstract void setPID(int aPID);

	public abstract boolean aceptsPID(int aPID);

	public abstract int getCID();

	public abstract void setCID(int aCID);

	public abstract boolean aceptsCID(int aCID);

	public abstract boolean aceptsBCID(int aBCID);

	public abstract boolean aceptsSCT(int aSCT);

	public abstract CNCDir getDir();

	public abstract void setDir(CNCDir aDir);

	public abstract boolean aceptsDir(boolean contrarian);

	public abstract boolean isAllswings();

	public abstract void setAllswings(boolean aSimpleMode);

	public abstract ProbVer getVersion();

	public abstract void setVersion(ProbVer aSelectedItem);

	public abstract void setSCTIndex(Integer aInteger);

	public abstract int getSCTIndex();

	public abstract void setBCID(Integer aInteger);

	public abstract int getBCID();

	public abstract void apply();

	public abstract void setProbabilityKey(ProbabilitiesKey aSelectedTargetKey);

}
