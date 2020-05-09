package org.mfg.opengl.chart;

import org.mfg.opengl.IGLConstants;

public class Settings implements IGLConstants {
	private SnappingMode _snappingMode;
	private float[] _bgColor;
	private float[] _gridColor;
	private float[] _textColor;
	private float[] _crosshairColor;
	private int _gridStippleFactor;
	private int _crosshairStippleFactor;
	private short _gridStipplePattern;
	private short _crosshairStipplePattern;
	private float _crosshairWidth;
	private float _gridWidth;

	public Settings() {
		_snappingMode = SnappingMode.DO_NOT_SNAP;
		_bgColor = COLOR_BLACK;
		_gridColor = COLOR_GRAY;
		_textColor = COLOR_WHITE;
		_crosshairColor = COLOR_CYAN;
		_gridStippleFactor = STIPPLE_FACTOR_3;
		_crosshairStippleFactor = STIPPLE_FACTOR_1;
		_gridStipplePattern = _crosshairStipplePattern = STIPPLE_PATTERN;
		_gridWidth = _crosshairWidth = 1.5f;
	}

	@Override
	public Settings clone() {
		Settings clone = new Settings();
		clone._snappingMode = _snappingMode;
		clone._bgColor = _bgColor;
		clone._gridColor = _gridColor;
		clone._textColor = _textColor;
		clone._crosshairColor = _crosshairColor;
		clone._gridStippleFactor = _gridStippleFactor;
		clone._crosshairStippleFactor = _crosshairStippleFactor;
		clone._gridStipplePattern = _gridStipplePattern;
		clone._crosshairStipplePattern = _crosshairStipplePattern;
		clone._gridWidth = _gridWidth;
		return clone;
	}

	public float[] getBgColor() {
		return _bgColor;
	}

	public void setBgColor(float[] bgColor) {
		this._bgColor = bgColor;
	}

	public float[] getGridColor() {
		return _gridColor;
	}

	public void setGridColor(float[] gridColor) {
		this._gridColor = gridColor;
	}

	public float[] getTextColor() {
		return _textColor;
	}

	public void setTextColor(float[] textColor) {
		this._textColor = textColor;
	}

	public float[] getCrosshairColor() {
		return _crosshairColor;
	}

	public void setCrosshairColor(float[] crosshairColor) {
		this._crosshairColor = crosshairColor;
	}

	public int getGridStippleFactor() {
		return _gridStippleFactor;
	}

	public void setGridStippleFactor(int gridStippleFactor) {
		this._gridStippleFactor = gridStippleFactor;
	}

	public int getCrosshairStippleFactor() {
		return _crosshairStippleFactor;
	}

	public void setCrosshairStippleFactor(int crosshairStippleFactor) {
		this._crosshairStippleFactor = crosshairStippleFactor;
	}

	public short getGridStipplePattern() {
		return _gridStipplePattern;
	}

	public void setGridStipplePattern(short gridStipplePattern) {
		_gridStipplePattern = gridStipplePattern;
	}

	public short getCrosshairStipplePattern() {
		return _crosshairStipplePattern;
	}

	public void setCrosshairStipplePattern(short crosshairStipplePattern) {
		this._crosshairStipplePattern = crosshairStipplePattern;
	}

	public float getCrosshairWidth() {
		return _crosshairWidth;
	}

	public void setCrosshairWidth(float crosshairWidth) {
		this._crosshairWidth = crosshairWidth;
	}

	public float getGridWidth() {
		return _gridWidth;
	}

	public void setGridWidth(float gridWidth) {
		this._gridWidth = gridWidth;
	}

	public SnappingMode getSnappingMode() {
		return _snappingMode;
	}

	public void setSnappingMode(SnappingMode snappingMode) {
		_snappingMode = snappingMode;
	}
}
