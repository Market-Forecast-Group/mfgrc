package com.mfg.widget.probabilities;

import com.mfg.utils.ui.table.IMfgTableModel;

public class T1Model implements IMfgTableModel {

	static final String[] _COLUMNS = new String[] { "Upper(<=)", "Matches",
			"Matches%", "Reached", "Prob" };

	/**
	 * 
	 */
	private T1Computer fT1Computer;

	public T1Model() {
		super();
	}

	public T1Model(T1Computer aT1Computer) {
		fT1Computer = aT1Computer;
	}

	// private static final long serialVersionUID = 1L;

	@SuppressWarnings("boxing")
	public Object getValueAt(int row, int column) {
		switch (column) {
		case 0:
			return fT1Computer.upperbounds[row];
		case 1:
			return fT1Computer.elements[row];
		case 2:
			return T1Computer.StepDefinition.round(fT1Computer.getPercent(row));
		case 3:
			return fT1Computer.winnerElements[row];
		default:
			return T1Computer.StepDefinition.round(fT1Computer
					.getProbability(row));
		}
	}

	@Override
	public int getRowCount() {
		if (fT1Computer == null || fT1Computer.elements == null)
			return 0;
		return fT1Computer.upperbounds.length;
	}

	public static int getColumnCount() {
		return _COLUMNS.length;
	}

	/**
	 * @param row
	 * @param column
	 */
	public static boolean isCellEditable(int row, int column) {
		return false;
	}

	public boolean reachedMinMatches(int index) {
		return fT1Computer.getPercent(index) >= fT1Computer.minPercent;
	}

	public boolean bestIndex(int index) {
		return index == fT1Computer.bindex;
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
		return fT1Computer.getPercent(aRow) >= fT1Computer.getMinPercent();
	}

	public T1Computer getT1Computer() {
		return fT1Computer;
	}

	public void setT1Computer(T1Computer aT1Computer) {
		fT1Computer = aT1Computer;
	}

	@Override
	public int getHighLight(int aRow, int aColumn) {
		int bidx = fT1Computer.getBindex();
		return bidx == aRow ? 1 : 0;
	}

}
