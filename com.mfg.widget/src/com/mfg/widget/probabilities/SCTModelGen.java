package com.mfg.widget.probabilities;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Set;

import javax.xml.bind.annotation.XmlTransient;

import com.mfg.interfaces.probabilities.SCTProbabilityKey;
import com.mfg.utils.StepDefinition;
import com.mfg.utils.ui.table.IMfgTableModel;

public class SCTModelGen implements IMfgTableModel {

	private HashMap<SCTProbabilityKey, SCTProbabilitySet> sctsMap;
	private SCTProbabilityKey[] list;

	public SCTModelGen() {
		super();
		sctsMap = new HashMap<>();
		list = new SCTProbabilityKey[0];
	}

	public SCTModelGen(HashMap<SCTProbabilityKey, SCTProbabilitySet> aSctsMap) {
		// super(_COLUMNS, 0);
		this.sctsMap = aSctsMap;
		Set<SCTProbabilityKey> keySet = aSctsMap.keySet();
		list = keySet.toArray(new SCTProbabilityKey[] {});
		Arrays.sort(list, new Comparator<SCTProbabilityKey>() {
			@Override
			public int compare(SCTProbabilityKey aO1, SCTProbabilityKey aO2) {
				int r = (int) Math.signum(aO1.getScale() - aO2.getScale());
				if (r == 0)
					r = (int) Math.signum(aO1.getSctouches()
							- aO2.getSctouches());
				if (r == 0)
					r = (int) Math.signum(aO1.getBaseScaleCluster()
							- aO2.getBaseScaleCluster());
				return r;
			}
		});

	}

	// private static final long serialVersionUID = 1L;

	// @Override
	@SuppressWarnings("boxing")
	public Object getValueAt(int row, int column) {
		SCTProbabilityKey k = list[row];
		SCTProbabilitySet set = sctsMap.get(k);
		switch (column) {
		case 0:
			return k.getScale();
		case 1:
			return k.getSctouches();
		case 2:
			return k.getBaseScaleCluster();
		case 3:
			return set.getSwingsCount() - set.getReachedNewHHLL();
		case 4:
			return set.getSwingsCount();
		case 5:
			return st.round(set.getNewTHProbability());
		default:
			return st.round(set.getNewHHLLProbability());
		}
	}

	@Override
	public int getRowCount() {
		if (list == null)
			return 0;
		return list.length;
	}

	// @Override
	public static int getColumnCount() {
		return _COLUMNS.length;
	}

	// @Override
	public static boolean isCellEditable(@SuppressWarnings("unused") int row,
			@SuppressWarnings("unused") int column) {
		return false;
	}

	StepDefinition st = new StepDefinition(0.01);
	private static final String[] _COLUMNS = new String[] { "Scale", "SCT",
			"BSC", "TH Count", "Sw Count", "TH Prob", "HHLL Prob" };

	public int getScale(int row) {
		return list[row].getScale();
	}

	public int getBaseClusterID(int row) {
		return list[row].getBaseScaleCluster();
	}

	public int getSCTouch(int row) {
		return list[row].getSctouches();
	}

	@Override
	public String[] getColumnNames() {
		return _COLUMNS;
	}

	@Override
	public Object getContent(int aRow, int aColumn) {
		return getValueAt(aRow, aColumn);
	}

	@Override
	public boolean isEnabled(int aRow, int aColumn) {
		SCTProbabilityKey k = list[aRow];
		switch (aColumn) {
		case 1:
			return k.getSctouches() != 0;
		case 2:
			return k.getBaseScaleCluster() != 0;
		}
		return true;
	}

	@XmlTransient
	public HashMap<SCTProbabilityKey, SCTProbabilitySet> getSctsMap() {
		return sctsMap;
	}

	public void setSctsMap(
			HashMap<SCTProbabilityKey, SCTProbabilitySet> aSctsMap) {
		sctsMap = aSctsMap;
	}

	public SCTProbabilityKey[] getList() {
		return list;
	}

	public void setList(SCTProbabilityKey[] aList) {
		list = aList;
	}

	@Override
	public int getHighLight(int aRow, int aColumn) {
		return 0;
	}

}
