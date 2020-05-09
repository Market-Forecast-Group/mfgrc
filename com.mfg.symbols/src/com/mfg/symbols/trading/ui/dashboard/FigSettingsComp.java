package com.mfg.symbols.trading.ui.dashboard;

import org.eclipse.nebula.visualization.widgets.figures.AbstractMarkedWidgetFigure;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;

import com.mfg.utils.ui.ColorChooserButton;

import org.eclipse.swt.layout.GridData;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;

public class FigSettingsComp extends Composite {
	private ColorChooserButton _bg;
	private ColorChooserButton _fg;
	private Composite _composite;
	private Composite _composite_1;
	private Button _loloCheck;
	private ColorChooserButton _loloColor;
	private Text _loloLevel;
	private Button _loCheck;
	private ColorChooserButton _loColor;
	private Text _loLevel;
	private Button _hiCheck;
	private ColorChooserButton _hiColor;
	private Text _hiLevel;
	private Button _hihiCheck;
	private ColorChooserButton _hihiColor;
	private Text _hihiLevel;
	private Button _logScale;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public FigSettingsComp(Composite parent, int style) {
		super(parent, style);
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		setLayout(gridLayout);

		_composite = new Composite(this, SWT.NONE);
		_composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		_composite.setLayout(new GridLayout(2, false));

		Label lblForeground = new Label(_composite, SWT.NONE);
		GridData gd_lblForeground = new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 1, 1);
		gd_lblForeground.widthHint = 100;
		lblForeground.setLayoutData(gd_lblForeground);
		lblForeground.setText("Foreground");

		_fg = new ColorChooserButton(_composite, SWT.NONE);
		GridData gd_fg = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_fg.heightHint = 30;
		gd_fg.widthHint = 50;
		_fg.setLayoutData(gd_fg);

		Label lblBackground = new Label(_composite, SWT.NONE);
		lblBackground.setText("Background");

		_bg = new ColorChooserButton(_composite, SWT.NONE);
		GridData gd_bg = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_bg.heightHint = 30;
		gd_bg.widthHint = 50;
		_bg.setLayoutData(gd_bg);

		_logScale = new Button(_composite, SWT.CHECK);
		_logScale.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 2, 1));
		_logScale.setText("Log Scale");

		_composite_1 = new Composite(this, SWT.NONE);
		_composite_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		_composite_1.setLayout(new GridLayout(4, false));

		_hihiCheck = new Button(_composite_1, SWT.CHECK);
		GridData gd_hihi = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1,
				1);
		gd_hihi.widthHint = 100;
		_hihiCheck.setLayoutData(gd_hihi);
		_hihiCheck.setText("HIHI");

		_hihiColor = new ColorChooserButton(_composite_1, SWT.NONE);
		GridData gd_hihiColor = new GridData(SWT.LEFT, SWT.FILL, false,
				false, 1, 1);
		gd_hihiColor.widthHint = 50;
		_hihiColor.setLayoutData(gd_hihiColor);

		Label hihiLevelLabel = new Label(_composite_1, SWT.NONE);
		hihiLevelLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		hihiLevelLabel.setText("Level");

		_hihiLevel = new Text(_composite_1, SWT.BORDER);
		GridData gd_hihiLevel = new GridData(SWT.LEFT, SWT.CENTER, true, false,
				1, 1);
		gd_hihiLevel.widthHint = 80;
		_hihiLevel.setLayoutData(gd_hihiLevel);

		_hiCheck = new Button(_composite_1, SWT.CHECK);
		GridData gd_hi = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_hi.widthHint = 100;
		_hiCheck.setLayoutData(gd_hi);
		_hiCheck.setText("HI");

		_hiColor = new ColorChooserButton(_composite_1, SWT.NONE);
		GridData gd_hiColor = new GridData(SWT.LEFT, SWT.FILL, false, false,
				1, 1);
		gd_hiColor.widthHint = 50;
		_hiColor.setLayoutData(gd_hiColor);

		Label hiLevelLabel = new Label(_composite_1, SWT.NONE);
		hiLevelLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		hiLevelLabel.setText("Level");

		_hiLevel = new Text(_composite_1, SWT.BORDER);
		GridData gd_hiLevel = new GridData(SWT.LEFT, SWT.CENTER, true, false,
				1, 1);
		gd_hiLevel.widthHint = 80;
		_hiLevel.setLayoutData(gd_hiLevel);

		_loCheck = new Button(_composite_1, SWT.CHECK);
		GridData gd_lo = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lo.widthHint = 100;
		_loCheck.setLayoutData(gd_lo);
		_loCheck.setText("LO");

		_loColor = new ColorChooserButton(_composite_1, SWT.NONE);
		GridData gd_loColor = new GridData(SWT.LEFT, SWT.FILL, false, false,
				1, 1);
		gd_loColor.widthHint = 50;
		_loColor.setLayoutData(gd_loColor);

		Label loLevelLabel = new Label(_composite_1, SWT.NONE);
		loLevelLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		loLevelLabel.setText("Level");

		_loLevel = new Text(_composite_1, SWT.BORDER);
		GridData gd_loLevel = new GridData(SWT.LEFT, SWT.CENTER, true, false,
				1, 1);
		gd_loLevel.widthHint = 80;
		_loLevel.setLayoutData(gd_loLevel);

		_loloCheck = new Button(_composite_1, SWT.CHECK);
		GridData gd_lolo = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1,
				1);
		gd_lolo.widthHint = 100;
		_loloCheck.setLayoutData(gd_lolo);
		_loloCheck.setText("LOLO");

		_loloColor = new ColorChooserButton(_composite_1, SWT.NONE);
		GridData gd_loloColor = new GridData(SWT.LEFT, SWT.FILL, false,
				false, 1, 1);
		gd_loloColor.widthHint = 50;
		_loloColor.setLayoutData(gd_loloColor);

		Label loloLevelLabel = new Label(_composite_1, SWT.NONE);
		loloLevelLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		loloLevelLabel.setText("Level");

		_loloLevel = new Text(_composite_1, SWT.BORDER);
		GridData gd_loloLevel = new GridData(SWT.LEFT, SWT.CENTER, true, false,
				1, 1);
		gd_loloLevel.widthHint = 80;
		_loloLevel.setLayoutData(gd_loloLevel);

	}

	public void saveTo(AbstractMarkedWidgetFigure fig) {
		fig.setBackgroundColor(SWTResourceManager.getColor(_bg.getColor()));
		fig.setForegroundColor(SWTResourceManager.getColor(_fg.getColor()));
		fig.setLogScale(_logScale.getSelection());

		fig.setHihiColor(SWTResourceManager.getColor(_hihiColor.getColor()));
		fig.setHiColor(SWTResourceManager.getColor(_hiColor.getColor()));
		fig.setLoColor(SWTResourceManager.getColor(_loColor.getColor()));
		fig.setLoloColor(SWTResourceManager.getColor(_loloColor.getColor()));

		fig.setShowHihi(_hihiCheck.getSelection());
		fig.setShowHi(_hiCheck.getSelection());
		fig.setShowLo(_loCheck.getSelection());
		fig.setShowLolo(_loloCheck.getSelection());

		fig.setHihiLevel(Double.parseDouble(_hihiLevel.getText()));
		fig.setHiLevel(Double.parseDouble(_hiLevel.getText()));
		fig.setLoLevel(Double.parseDouble(_loLevel.getText()));
		fig.setLoloLevel(Double.parseDouble(_loloLevel.getText()));
	}

	public void updateFrom(AbstractMarkedWidgetFigure fig) {
		_bg.setColor(fig.getBackgroundColor().getRGB());
		_fg.setColor(fig.getForegroundColor().getRGB());
		_logScale.setSelection(fig.isLogScale());

		_hihiColor.setColor(fig.getHihiColor().getRGB());
		_hiColor.setColor(fig.getHiColor().getRGB());
		_loColor.setColor(fig.getLoColor().getRGB());
		_loloColor.setColor(fig.getLoloColor().getRGB());

		_hihiCheck.setSelection(fig.isShowHihi());
		_hiCheck.setSelection(fig.isShowHi());
		_loCheck.setSelection(fig.isShowLo());
		_loloCheck.setSelection(fig.isShowLolo());

		_hihiLevel.setText(Double.toString(fig.getHihiLevel()));
		_hiLevel.setText(Double.toString(fig.getHiLevel()));
		_loLevel.setText(Double.toString(fig.getLoLevel()));
		_loloLevel.setText(Double.toString(fig.getLoloLevel()));
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
