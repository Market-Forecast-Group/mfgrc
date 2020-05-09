package com.mfg.widget.probabilities;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import com.mfg.interfaces.ISimpleLogMessage;
import com.mfg.interfaces.probabilities.IProbabilitiesFilter;
import com.mfg.interfaces.probabilities.ProbabilitiesKey;
import com.mfg.logger.ILogFilter;
import com.mfg.logger.ILogRecord;
import com.mfg.logger.ILogger;
import com.mfg.widget.ProbabilitiesManager;
import com.mfg.widget.WidgetPlugin;
import com.mfg.widget.probabilities.logger.LoggerUtils;

public class ProbababilitiesFilter extends ViewerFilter implements
		IProbabilitiesFilter, ILogFilter {

	private static final String SCALE = "SCALE";
	private static final String PPID = "PATTERN ID";
	private static final String PCID = "CLUSTER ID";
	private static final String DIR = "DIR";
	private static final String ALLSWINGS = "SHOW ALL SWINGS";
	private static final String VERSION = "VERSION";
	private static final String BCID = "BASE CLUSTER ID";
	private static final String SCTIDX = "SC TOUCH INDEX";
	private int scale = 0;
	private int PID = 0;
	private int CID = 0;
	private CNCDir dir = CNCDir.Both;
	private boolean allswings = true;
	private ProbVer version = ProbVer.Targets;
	@SuppressWarnings("boxing")
	private Integer sctIndex = 0;
	@SuppressWarnings("boxing")
	private Integer bcid = 0;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mfg.widget.probabilities.IProbabilitiesFilter#aceptsKey(com.mfg.widget
	 * .probabilities.ProbabilitiesKey)
	 */
	@Override
	public boolean aceptsKey(ProbabilitiesKey k) {
		return (scale == 0 || (scale == k.getScale() /*
													 * && scale == k
													 * .getBaseScale()
													 */))
				&& (PID == 0 || PID == k.getPatternID())
				&& aceptsCID(k.getClusterID()) && aceptsDir(k.isContrarian());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mfg.widget.probabilities.IProbabilitiesFilter#aceptsProbVer(com.mfg
	 * .widget.probabilities.ProbababilitiesFilter.ProbVer)
	 */
	@Override
	public boolean aceptsProbVer(ProbVer ver) {
		return version == ProbVer.ALL || version.equals(ver)
				|| ver == ProbVer.ALL;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.widget.probabilities.IProbabilitiesFilter#getScale()
	 */
	@Override
	public int getScale() {
		return scale;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.widget.probabilities.IProbabilitiesFilter#aceptsScale(int)
	 */
	@Override
	public boolean aceptsScale(int ascale) {
		return (scale == 0 || scale == ascale);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.widget.probabilities.IProbabilitiesFilter#setScale(int)
	 */
	@SuppressWarnings("boxing")
	@Override
	public void setScale(int aScale) {
		Object oldVal = scale;
		scale = aScale;
		firePropertyChange(SCALE, oldVal, aScale);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.widget.probabilities.IProbabilitiesFilter#getPID()
	 */
	@Override
	public int getPID() {
		return PID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.widget.probabilities.IProbabilitiesFilter#setPID(int)
	 */
	@SuppressWarnings("boxing")
	@Override
	public void setPID(int aPID) {
		Object oldVal = PID;
		PID = aPID;
		firePropertyChange(PPID, oldVal, aPID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.widget.probabilities.IProbabilitiesFilter#aceptsPID(int)
	 */
	@Override
	public boolean aceptsPID(int aPID) {
		return (allswings || PID == 0 || PID == aPID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.widget.probabilities.IProbabilitiesFilter#getCID()
	 */
	@Override
	public int getCID() {
		return CID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.widget.probabilities.IProbabilitiesFilter#setCID(int)
	 */
	@SuppressWarnings("boxing")
	@Override
	public void setCID(int aCID) {
		Object oldVal = CID;
		CID = aCID;
		firePropertyChange(PCID, oldVal, aCID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.widget.probabilities.IProbabilitiesFilter#aceptsCID(int)
	 */
	@Override
	public boolean aceptsCID(int aCID) {
		return (CID == 0 || CID == aCID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.widget.probabilities.IProbabilitiesFilter#aceptsBCID(int)
	 */
	@SuppressWarnings("boxing")
	@Override
	public boolean aceptsBCID(int aBCID) {
		return (bcid == 0 || bcid == aBCID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.widget.probabilities.IProbabilitiesFilter#aceptsSCT(int)
	 */
	@SuppressWarnings("boxing")
	@Override
	public boolean aceptsSCT(int aSCT) {
		return (sctIndex == 0 || sctIndex == aSCT);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.widget.probabilities.IProbabilitiesFilter#getDir()
	 */
	@Override
	public CNCDir getDir() {
		return dir;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mfg.widget.probabilities.IProbabilitiesFilter#setDir(com.mfg.widget
	 * .probabilities.ProbababilitiesFilter.CNCDir)
	 */
	@Override
	public void setDir(CNCDir aDir) {
		Object oldVal = dir;
		dir = aDir;
		firePropertyChange(DIR, oldVal, aDir);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.widget.probabilities.IProbabilitiesFilter#aceptsDir(boolean)
	 */
	@Override
	public boolean aceptsDir(boolean contrarian) {
		return (allswings || dir == CNCDir.Both || (dir == CNCDir.Contr) == contrarian);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.widget.probabilities.IProbabilitiesFilter#isSimpleMode()
	 */
	@Override
	public boolean isAllswings() {
		return allswings;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mfg.widget.probabilities.IProbabilitiesFilter#setSimpleMode(boolean)
	 */
	@SuppressWarnings("boxing")
	@Override
	public void setAllswings(boolean aallswings) {
		Object oldVal = allswings;
		allswings = aallswings;
		firePropertyChange(ALLSWINGS, oldVal, aallswings);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.widget.probabilities.IProbabilitiesFilter#getVersion()
	 */
	@Override
	public ProbVer getVersion() {
		return version;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mfg.widget.probabilities.IProbabilitiesFilter#setVersion(com.mfg.
	 * widget.probabilities.ProbababilitiesFilter.ProbVer)
	 */
	@Override
	public void setVersion(ProbVer aSelectedItem) {
		Object oldVal = version;
		version = aSelectedItem;
		firePropertyChange(VERSION, oldVal, aSelectedItem);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mfg.widget.probabilities.IProbabilitiesFilter#setSCTIndex(java.lang
	 * .Integer)
	 */
	@Override
	public void setSCTIndex(Integer aInteger) {
		Object oldVal = sctIndex;
		sctIndex = aInteger;
		firePropertyChange(SCTIDX, oldVal, aInteger);
	}

	@SuppressWarnings("boxing")
	@Override
	public int getSCTIndex() {
		return sctIndex;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mfg.widget.probabilities.IProbabilitiesFilter#setBCID(java.lang.Integer
	 * )
	 */
	@Override
	public void setBCID(Integer aInteger) {
		Object oldVal = bcid;
		bcid = aInteger;
		firePropertyChange(BCID, oldVal, aInteger);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.widget.probabilities.IProbabilitiesFilter#getBCID()
	 */
	@SuppressWarnings("boxing")
	@Override
	public int getBCID() {
		return bcid;
	}

	@Override
	public boolean select(Viewer aViewer, Object aParentElement, Object aElement) {
		ISimpleLogMessage msg = (ISimpleLogMessage) aElement;
		boolean res = !((!allswings && msg.getCategory().equals(
				ISimpleLogMessage.CATEGORY_TARGET)) || !msg.passFilter(this));
		return res;
	}

	@Override
	public void apply() {
		ProbabilitiesManager man = WidgetPlugin.getDefault()
				.getProbabilitiesManager();
		man.getLogManager().refreshViews();
		ILogger logger = LoggerUtils.defaultLogger();
		logger.setFilters((ILogFilter) man.getProbabilitiesLogFilter());
		System.out.println("refreshing log");
	}

	@Override
	public boolean accept(ILogRecord aRecord) {
		ISimpleLogMessage msg = (ISimpleLogMessage) (aRecord.getMessage());
		boolean res = msg.passFilter(this);
		return res;
	}

	@Override
	public void setProbabilityKey(ProbabilitiesKey key) {
		setPID(key.getPatternID());
		setScale(key.getScale());
		setCID(key.getClusterID());
		setDir(key.isContrarian() ? CNCDir.Contr : CNCDir.NContr);
		setVersion(ProbVer.Targets);
		setAllswings(false);
	}

	@SuppressWarnings("boxing")
	@Override
	public String toString() {
		return "Filter [scale=" + scale + ", PID=" + PID + ", dir=" + dir
				+ (CID > 0 ? ", CID=" + CID : "") + ", allswings=" + allswings
				+ ", version=" + version
				+ (sctIndex > 0 ? ", sctIndex=" + sctIndex : "")
				+ (bcid > 0 ? ", bcid=" + bcid : "") + "]";
	}

	private transient PropertyChangeSupport support = new PropertyChangeSupport(
			this);

	public void addPropertyChangeListener(PropertyChangeListener l) {
		support.addPropertyChangeListener(l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		support.removePropertyChangeListener(l);
	}

	public void addPropertyChangeListener(String property,
			PropertyChangeListener l) {
		support.addPropertyChangeListener(property, l);
	}

	public void removePropertyChangeListener(String property,
			PropertyChangeListener l) {
		support.removePropertyChangeListener(property, l);
	}

	public void firePropertyChange(String property, Object oldVal, Object newVal) {
		support.firePropertyChange(property, oldVal, newVal);
	}

}
