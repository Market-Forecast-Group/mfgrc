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

package com.mfg.strategy.builder.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.parts.ScrollableThumbnail;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.KeyHandler;
import org.eclipse.gef.KeyStroke;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.MouseWheelHandler;
import org.eclipse.gef.MouseWheelZoomHandler;
import org.eclipse.gef.dnd.TemplateTransferDragSourceListener;
import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.actions.ZoomInAction;
import org.eclipse.gef.ui.actions.ZoomOutAction;
import org.eclipse.gef.ui.parts.ContentOutlinePage;
import org.eclipse.gef.ui.parts.GraphicalEditorWithPalette;
import org.eclipse.gef.ui.parts.TreeViewer;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import com.mfg.persist.interfaces.PersistInterfacesPlugin;
import com.mfg.strategy.builder.commands.CopyNodeAction;
import com.mfg.strategy.builder.commands.ExpandCollapseAction;
import com.mfg.strategy.builder.commands.ICommandIds;
import com.mfg.strategy.builder.commands.PasteNodeAction;
import com.mfg.strategy.builder.commands.RotateAction;
import com.mfg.strategy.builder.commands.RunAction;
import com.mfg.strategy.builder.commands.StrategyTemplateTransferDropTargetListener;
import com.mfg.strategy.builder.model.EventModelNode;
import com.mfg.strategy.builder.model.EventsCanvasModel;
import com.mfg.strategy.builder.model.StrategyInfo;
import com.mfg.strategy.builder.part.AppContextMenuProvider;
import com.mfg.strategy.builder.part.AppTreeEditPartFactory;
import com.mfg.strategy.builder.part.StrategyEditPartFactory;
import com.mfg.strategy.builder.persistence.StrategyBuilderStorage;
import com.mfg.strategy.builder.utils.ObjectsJSONFileIO;
import com.mfg.ui.editors.StorageObjectEditorInput;
import com.mfg.utils.Utils;

public class StrategyBuilderEditor extends GraphicalEditorWithPalette {

	public static final String ID = "com.mfg.strategy.builder.ui.StrategyBuilderEditor";

	public static final String KEY_NAME = "info.name";

	ScrollableThumbnail thumbnail;
	DisposeListener disposeListener;

	private EventModelNode model;
	KeyHandler keyHandler;

	private boolean iamDirty;

	@Override
	public ActionRegistry getActionRegistry() {
		return super.getActionRegistry();
	}

	public class OutlinePage extends ContentOutlinePage {
		private SashForm sash;


		public OutlinePage() {
			super(new TreeViewer());
		}


		// Use of inherit methods.
		@SuppressWarnings("synthetic-access")
		@Override
		public void createControl(Composite parent) {
			sash = new SashForm(parent, SWT.VERTICAL);

			getViewer().createControl(sash);

			getViewer().setEditDomain(getEditDomain());
			getViewer().setEditPartFactory(new AppTreeEditPartFactory());
			getViewer().setContents(model);
			// ((TreeViewer)getViewer()).
			getSelectionSynchronizer().addViewer(getViewer());

			// Creation de la miniature.
			Canvas canvas = new Canvas(sash, SWT.BORDER);
			LightweightSystem lws = new LightweightSystem(canvas);

			thumbnail = new ScrollableThumbnail((Viewport) ((ScalableRootEditPart) getGraphicalViewer().getRootEditPart()).getFigure());
			thumbnail.setSource(((ScalableRootEditPart) getGraphicalViewer().getRootEditPart()).getLayer(LayerConstants.PRINTABLE_LAYERS));

			lws.setContents(thumbnail);

			disposeListener = new DisposeListener() {
				@Override
				public void widgetDisposed(DisposeEvent e) {
					if (thumbnail != null) {
						thumbnail.deactivate();
						thumbnail = null;
					}
				}
			};
			getGraphicalViewer().getControl().addDisposeListener(disposeListener);
			IActionBars bars = getSite().getActionBars();
			ActionRegistry ar = getActionRegistry();
			// ...
			// bars.setGlobalActionHandler(ActionFactory.COPY.getId(), ar.getAction(ActionFactory.COPY.getId()));
			bars.setGlobalActionHandler(ActionFactory.PASTE.getId(), ar.getAction(ActionFactory.PASTE.getId()));
			bars.setGlobalActionHandler(ActionFactory.SAVE.getId(), getActionRegistry().getAction(ActionFactory.SAVE.getId()));
			bars.setGlobalActionHandler(ActionFactory.SAVE_AS.getId(), getActionRegistry().getAction(ActionFactory.SAVE_AS.getId()));
			initializeToolBar();
		}


		private void initializeToolBar() {
			// IToolBarManager tbm = getSite().getActionBars().getToolBarManager();
		}


		@Override
		public void init(IPageSite pageSite) {
			super.init(pageSite);

			IActionBars bars = getSite().getActionBars();

			bars.setGlobalActionHandler(ActionFactory.UNDO.getId(), getActionRegistry().getAction(ActionFactory.UNDO.getId()));
			bars.setGlobalActionHandler(ActionFactory.REDO.getId(), getActionRegistry().getAction(ActionFactory.REDO.getId()));
			bars.setGlobalActionHandler(ActionFactory.DELETE.getId(), getActionRegistry().getAction(ActionFactory.DELETE.getId()));
			bars.setGlobalActionHandler(ActionFactory.SAVE.getId(), getActionRegistry().getAction(ActionFactory.SAVE.getId()));
			bars.setGlobalActionHandler(ActionFactory.SAVE_AS.getId(), getActionRegistry().getAction(ActionFactory.SAVE_AS.getId()));
			bars.setGlobalActionHandler(ICommandIds.CMD_ROTATE, getActionRegistry().getAction(ICommandIds.CMD_ROTATE));
			bars.setGlobalActionHandler(ICommandIds.CMD_RUN, getActionRegistry().getAction(ICommandIds.CMD_RUN));
			// bars.setGlobalActionHandler(ICommandIds.CMD_OPEN,
			// getActionRegistry().getAction(ICommandIds.CMD_OPEN));
			bars.setGlobalActionHandler(ICommandIds.CMD_EXPANDCOLLAPSE, getActionRegistry().getAction(ICommandIds.CMD_EXPANDCOLLAPSE));

			// bars.setGlobalActionHandler(
			// ActionFactory.COPY.getId(),
			// ar.getAction(ActionFactory.COPY.getId()));
			// bars.setGlobalActionHandler(
			// ActionFactory.PASTE.getId(),
			// ar.getAction(ActionFactory.PASTE.getId()));

			bars.updateActionBars();

			getViewer().setKeyHandler(keyHandler);

			ContextMenuProvider provider = new AppContextMenuProvider(getViewer(), getActionRegistry());
			getViewer().setContextMenu(provider);

		}


		@Override
		public Control getControl() {
			return sash;
		}


		@SuppressWarnings("synthetic-access")
		// Used of inherit methods.
		@Override
		public void dispose() {
			getSelectionSynchronizer().removeViewer(getViewer());
			if (getGraphicalViewer().getControl() != null && !getGraphicalViewer().getControl().isDisposed())
				getGraphicalViewer().getControl().removeDisposeListener(disposeListener);
			super.dispose();
		}
	}


	public StrategyBuilderEditor() {
		setEditDomain(new DefaultEditDomain(this));
	}


	@Override
	public void createActions() {
		super.createActions();
		ActionRegistry registry = getActionRegistry();
		IAction action = new RotateAction(this);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());

		action = new RunAction(this);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());

		action = new ExpandCollapseAction(this);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());

		action = new CopyNodeAction(this);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());

		// action = saveNodeAction = new SaveNodeAction();
		IWorkbenchWindow w = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		action = ActionFactory.SAVE.create(w);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());
		action = ActionFactory.SAVE_AS.create(w);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());

		action = new PasteNodeAction(this);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());
	}


	@Override
	protected void configureGraphicalViewer() {
		double[] zoomLevels;
		ArrayList<String> zoomContributions;
		super.configureGraphicalViewer();
		GraphicalViewer viewer = getGraphicalViewer();
		viewer.setEditPartFactory(new StrategyEditPartFactory());
		ScalableRootEditPart rootEditPart = new ScalableRootEditPart();
		viewer.setRootEditPart(rootEditPart);

		ZoomManager manager = rootEditPart.getZoomManager();
		getActionRegistry().registerAction(new ZoomInAction(manager));
		getActionRegistry().registerAction(new ZoomOutAction(manager));

		zoomLevels = new double[] { 1.0, 2.0, 3.0, 4.0, 5.0, 10.0, 20.0 };
		manager.setZoomLevels(zoomLevels);

		zoomContributions = new ArrayList<>();
		zoomContributions.add(ZoomManager.FIT_ALL);
		zoomContributions.add(ZoomManager.FIT_HEIGHT);
		zoomContributions.add(ZoomManager.FIT_WIDTH);
		manager.setZoomLevelContributions(zoomContributions);

		KeyHandler kh = keyHandler = new KeyHandler();

		kh.put(KeyStroke.getPressed(SWT.DEL, 127, 0), getActionRegistry().getAction(ActionFactory.DELETE.getId()));

		kh.put(KeyStroke.getPressed('+', SWT.KEYPAD_ADD, 0), getActionRegistry().getAction(GEFActionConstants.ZOOM_IN));

		kh.put(KeyStroke.getPressed('-', SWT.KEYPAD_SUBTRACT, 0), getActionRegistry().getAction(GEFActionConstants.ZOOM_OUT));

		kh.put(KeyStroke.getPressed('r', 'r', 0), getActionRegistry().getAction(ICommandIds.CMD_ROTATE));

		kh.put(KeyStroke.getPressed('e', 'e', 0), getActionRegistry().getAction(ICommandIds.CMD_EXPANDCOLLAPSE));

		viewer.setProperty(MouseWheelHandler.KeyGenerator.getKey(SWT.NONE), MouseWheelZoomHandler.SINGLETON);

		viewer.setKeyHandler(kh);

		ContextMenuProvider provider = new AppContextMenuProvider(viewer, getActionRegistry());
		viewer.setContextMenu(provider);

		IActionBars bars = getEditorSite().getActionBars();
		// bars.setGlobalActionHandler(ActionFactory.SAVE.getId(), saveNodeAction);
		// bars.setGlobalActionHandler(ActionFactory.SAVE_AS.getId(), saveAsNodeAction);
		bars.updateActionBars();

	}


	@Override
	public Object getAdapter(Class type) {
		if (type == ZoomManager.class)
			return ((ScalableRootEditPart) getGraphicalViewer().getRootEditPart()).getZoomManager();
		if (type == IContentOutlinePage.class) {
			return new OutlinePage();
		}
		return super.getAdapter(type);
	}


	@SuppressWarnings("unchecked")
	@Override
	public StorageObjectEditorInput<StrategyInfo> getEditorInput() {
		return (StorageObjectEditorInput<StrategyInfo>) super.getEditorInput();
	}


	@Override
	protected void initializeGraphicalViewer() {
		GraphicalViewer viewer = getGraphicalViewer();
		StorageObjectEditorInput<StrategyInfo> editorInput = getEditorInput();
		StrategyInfo info = editorInput.getStorageObject();
		model = ObjectsJSONFileIO.getInstance().readModelFromJSON(info.getPatternJSON());
		viewer.setContents(model);
		setPartName(getEditorInput().getStorageObject().getName());
		viewer.addDropTargetListener(new StrategyTemplateTransferDropTargetListener(viewer));
		PlatformUI.getWorkbench().getHelpSystem().setHelp(viewer.getControl(), "com.mfg.help.strategyBuilder");
		model.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent aEvt) {
				setDirty(true);
			}
		});
	}


	protected void setDirty(boolean aB) {
		iamDirty = aB;
		firePropertyChange(PROP_DIRTY);
	}


	@Override
	public boolean isDirty() {
		return iamDirty;
	}

	boolean controlon = false;


	@Override
	protected void initializePaletteViewer() {
		super.initializePaletteViewer();
		getPaletteViewer().addDragSourceListener(new TemplateTransferDragSourceListener(getPaletteViewer()));
		getPaletteViewer().addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent aEvent) {
				// TODO Auto-generated method stub
				Utils.debug_var(12345, "ME " + controlon);
			}
		});
		// getPaletteViewer().setKeyHandler(keyHandler);
	}


	@Override
	public void doSave(IProgressMonitor aMonitor) {
		getCommandStack().markSaveLocation();

		EventsCanvasModel canvasModel = (EventsCanvasModel) model;
		StrategyBuilderStorage storage = (StrategyBuilderStorage) getEditorInput().getStorage();
		StrategyInfo info = getEditorInput().getStorageObject();
		info.setPatternJSON(canvasModel.toJSONString());
		try {
			storage.save(info, new File(PersistInterfacesPlugin.getDefault().getCurrentWorkspacePath()));
			setDirty(false);
			// firePropertyChange(PROP_DIRTY);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}


	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}


	@Override
	protected PaletteRoot getPaletteRoot() {
		PaletteRoot root = new StrategyPaletteRoot();
		return root;
	}

}
