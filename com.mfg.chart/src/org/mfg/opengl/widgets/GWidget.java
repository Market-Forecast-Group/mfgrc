/**
 *
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 *
 * $Id: $
 *
 * @author <a href="mailto:boniatillo@gmail.com">Arian Fornaris</a>, MFG
 *
 * @version $Revision: $ $Date: $
 */
/**
 *
 */

package org.mfg.opengl.widgets;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;
import javax.media.opengl.GL2GL3;

import org.mfg.opengl.IGLConstants;

/**
 * @author arian
 * 
 */
@SuppressWarnings("boxing")
public class GWidget implements IGLConstants, IGWidget {
	private final GWidget _parent;
	private List<GWidget> _children;

	private int _x;
	private int _y;
	private int _width;
	private int _height;
	private Integer _bestWidth;
	private Integer _bestHeight;
	private GWBorder _border;
	private GWLayout _layout;
	private boolean _inited;
	private IGWColorModel _colorModel;
	private IGWVisibilityModel _visibilityModel;

	public GWidget(final GWidget parent) {
		this(parent, null);
	}

	public GWidget(final GWidget parent, final GWLayout layout) {
		this._parent = parent;
		_children = new ArrayList<>();

		if (parent == null) {
			if (!(this instanceof GWRoot)) {
				throw new IllegalArgumentException(
						"Only GWRoot widgets can be create with a null parent");
			}
		} else {
			parent._children.add(this);
		}

		_visibilityModel = new GWVisibilityModel(true);
		_border = new GWBorder();
		_inited = false;
		_width = 100;
		_height = 50;
		_colorModel = new GWColorModel();
		this._layout = layout;
	}

	/**
	 * @param gl
	 */
	public void init(final GL2 gl) {
		//
	}

	public GWRoot getRoot() {
		return (GWRoot) (_parent instanceof GWRoot ? _parent : _parent
				.getRoot());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mfg.opengl.widgets.IGWidget#getColorModel()
	 */
	@Override
	public IGWColorModel getColorModel() {
		return _colorModel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mfg.opengl.widgets.IGWidget#setColorModel(org.mfg.opengl.widgets.
	 * IGWColorModel)
	 */
	@Override
	public void setColorModel(final IGWColorModel colorModel) {
		this._colorModel = colorModel;
	}

	public IGWVisibilityModel getVisibilityModel() {
		return _visibilityModel;
	}

	/**
	 * @param visibilityModel
	 *            the visibilityModel to set
	 */
	@Override
	public void setVisibilityModel(final IGWVisibilityModel visibilityModel) {
		this._visibilityModel = visibilityModel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mfg.opengl.widgets.IGWidget#isVisible()
	 */
	@Override
	public boolean isVisible() {
		return getVisibilityModel().isVisible();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mfg.opengl.widgets.IGWidget#getX()
	 */
	@Override
	public int getX() {
		return _x;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mfg.opengl.widgets.IGWidget#setX(int)
	 */
	@Override
	public void setX(final int x) {
		this._x = x;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mfg.opengl.widgets.IGWidget#getY()
	 */
	@Override
	public int getY() {
		return _y;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mfg.opengl.widgets.IGWidget#setY(int)
	 */
	@Override
	public void setY(final int y) {
		this._y = y;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mfg.opengl.widgets.IGWidget#getWidth()
	 */
	@Override
	public int getWidth() {
		return _width;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mfg.opengl.widgets.IGWidget#getRight()
	 */
	@Override
	public int getRight() {
		return getX() + getWidth();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mfg.opengl.widgets.IGWidget#getLeft()
	 */
	@Override
	public int getLeft() {
		return getX();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mfg.opengl.widgets.IGWidget#getUp()
	 */
	@Override
	public int getUp() {
		return getY();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mfg.opengl.widgets.IGWidget#getBottom()
	 */
	@Override
	public int getBottom() {
		return getY() + getHeight();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mfg.opengl.widgets.IGWidget#setWidth(int)
	 */
	@Override
	public void setWidth(final int width) {
		this._width = width;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mfg.opengl.widgets.IGWidget#getBestWidth()
	 */
	@Override
	public int getBestWidth() {
		return _bestWidth == null ? getWidth() : _bestWidth;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mfg.opengl.widgets.IGWidget#setBestWidth(java.lang.Integer)
	 */
	@Override
	public void setBestWidth(final Integer bestWidth) {
		this._bestWidth = bestWidth;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mfg.opengl.widgets.IGWidget#getBestHeight()
	 */
	@Override
	public int getBestHeight() {
		return _bestHeight == null ? getHeight() : _bestHeight;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mfg.opengl.widgets.IGWidget#setBestHeight(java.lang.Integer)
	 */
	@Override
	public void setBestHeight(final Integer bestHeight) {
		this._bestHeight = bestHeight;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mfg.opengl.widgets.IGWidget#getLayout()
	 */
	@Override
	public GWLayout getLayout() {
		return _layout;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mfg.opengl.widgets.IGWidget#setLocation(int, int)
	 */
	@Override
	public void setLocation(final int x, final int y) {
		setX(x);
		setY(y);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mfg.opengl.widgets.IGWidget#setLayout(org.mfg.opengl.widgets.GWLayout
	 * )
	 */
	@Override
	public void setLayout(final GWLayout layout) {
		this._layout = layout;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mfg.opengl.widgets.IGWidget#getBorder()
	 */
	@Override
	public GWBorder getBorder() {
		return _border;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mfg.opengl.widgets.IGWidget#setBorder(org.mfg.opengl.widgets.GWBorder
	 * )
	 */
	@Override
	public void setBorder(final GWBorder border) {
		this._border = border;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mfg.opengl.widgets.IGWidget#setBounds(int, int, int, int)
	 */
	@Override
	public void setBounds(final int x, final int y, final int width,
			final int heigh) {
		setX(x);
		setY(y);
		setWidth(width);
		setHeight(heigh);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mfg.opengl.widgets.IGWidget#setSize(int, int)
	 */
	@Override
	public void setSize(final int width, final int height) {
		setWidth(width);
		setHeight(height);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mfg.opengl.widgets.IGWidget#getBackground()
	 */
	@Override
	public float[] getBackground() {
		return _colorModel == null ? null : _colorModel.getBackground();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mfg.opengl.widgets.IGWidget#getForeground()
	 */
	@Override
	public float[] getForeground() {
		return _colorModel == null ? null : _colorModel.getForeground();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mfg.opengl.widgets.IGWidget#getForegroundToPaint()
	 */
	@Override
	public float[] getForegroundToPaint() {
		return getForeground();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mfg.opengl.widgets.IGWidget#getBackgroundToPaint()
	 */
	@Override
	public float[] getBackgroundToPaint() {
		return getBackground();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mfg.opengl.widgets.IGWidget#getHeight()
	 */
	@Override
	public int getHeight() {
		return _height;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mfg.opengl.widgets.IGWidget#setHeight(int)
	 */
	@Override
	public void setHeight(final int height) {
		this._height = height;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mfg.opengl.widgets.IGWidget#doLayout()
	 */
	@Override
	public void doLayout() {
		if (_layout == null) {
			for (final IGWidget child : _children) {
				child.doLayout();
			}
		} else {
			_layout.layoutWidget(this);
		}
		setWidth(getBestWidth());
		setHeight(getBestHeight());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mfg.opengl.widgets.IGWidget#getParent()
	 */
	@Override
	public IGWidget getParent() {
		return _parent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mfg.opengl.widgets.IGWidget#getChildren()
	 */
	@Override
	public List<GWidget> getChildren() {
		return _children;
	}

	@Override
	public void clearChildren() {
		_children = new ArrayList<>();
	}

	public void mouseClicked(final int x, final int y) {
		for (final GWidget w : _children) {
			if (w.contains(x, y)) {
				w.mouseClicked(x, y);
				break;
			}
		}
	}

	@Override
	public String getTooltip(int x, int y) {
		for (IGWidget w : _children) {
			if (w.contains(x, y)) {
				String tip = w.getTooltip(x, y);
				return tip;
			}
		}
		return null;
	}

	@Override
	public boolean contains(final int x, final int y) {
		if (!isVisible()) {
			return false;
		}
		for (GWidget w : _children) {
			if (w.contains(x, y)) {
				return true;
			}
		}
		return x >= getX() && x <= getRight() && y >= getY()
				&& y <= getBottom();
	}

	protected boolean childrenContain(int x, int y) {
		for (IGWidget w : _children) {
			if (w.contains(x, y)) {
				return true;
			}
		}
		return false;
	}

	public void paint(final GL2 gl) {
		if (isVisible()) {
			initIfNeeded(gl);
			paintWidget(gl);
			paintBorder(gl);
			paintChildren(gl);
		}
	}

	protected void initIfNeeded(final GL2 gl) {
		if (!_inited) {
			_inited = true;
			init(gl);
		}
	}

	/**
	 * @param gl
	 */
	protected void paintBorder(final GL2 gl) {
		_border.paint(gl, this);
	}

	protected void paintChildren(final GL2 gl) {
		for (final GWidget w : _children) {
			w.paint(gl);
		}
	}

	/**
	 * @param gl
	 * @param screenWidth
	 * @param scr
	 */
	protected void paintWidget(final GL2 gl) {
		if (getColorModel() != null) {
			final float[] bg = getBackgroundToPaint();
			if (bg != null) {
				int x = getX();
				int y = getY();
				int r = getRight();
				int b = getBottom();
				int l = getLeft();

				gl.glColor4fv(bg, 0);
				gl.glBegin(GL2GL3.GL_QUADS);
				gl.glVertex2i(x, y);
				gl.glVertex2i(r, y);
				gl.glVertex2i(r, b);
				gl.glVertex2i(l, b);
				gl.glEnd();
			}
		}
	}

	@Override
	public void reshaped(int width, int height) {
		for (IGWidget w : _children) {
			w.reshaped(width, height);
		}
	}

}
