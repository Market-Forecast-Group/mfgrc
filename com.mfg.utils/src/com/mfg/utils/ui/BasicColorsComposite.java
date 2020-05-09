package com.mfg.utils.ui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;

public class BasicColorsComposite extends Composite {

	public static final String PROP_COLOR = "color";
	private RGB _color;
	protected Control _selected;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public BasicColorsComposite(Composite parent, int style) {
		super(parent, style);
		GridLayout gridLayout = new GridLayout(31, true);
		gridLayout.verticalSpacing = 2;
		gridLayout.horizontalSpacing = 2;
		setLayout(gridLayout);

		Label label = new Label(this, SWT.BORDER);
		label.setBackground(SWTResourceManager.getColor(255, 255, 255));
		GridData gd_label = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
		gd_label.widthHint = 10;
		gd_label.heightHint = 10;
		gd_label.minimumWidth = 5;
		gd_label.minimumHeight = 5;
		label.setLayoutData(gd_label);

		Label label_1 = new Label(this, SWT.BORDER);
		label_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1,
				1));
		label_1.setBackground(SWTResourceManager.getColor(204, 255, 255));

		Label label_2 = new Label(this, SWT.BORDER);
		label_2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1,
				1));
		label_2.setBackground(SWTResourceManager.getColor(204, 204, 255));

		Label label_3 = new Label(this, SWT.BORDER);
		label_3.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1,
				1));
		label_3.setBackground(SWTResourceManager.getColor(204, 204, 255));

		Label label_4 = new Label(this, SWT.BORDER);
		label_4.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1,
				1));
		label_4.setBackground(SWTResourceManager.getColor(204, 204, 255));

		Label label_5 = new Label(this, SWT.BORDER);
		label_5.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1,
				1));
		label_5.setBackground(SWTResourceManager.getColor(204, 204, 255));

		Label label_6 = new Label(this, SWT.BORDER);
		label_6.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1,
				1));
		label_6.setBackground(SWTResourceManager.getColor(204, 204, 255));

		Label label_7 = new Label(this, SWT.BORDER);
		label_7.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1,
				1));
		label_7.setBackground(SWTResourceManager.getColor(204, 204, 255));

		Label label_48 = new Label(this, SWT.BORDER);
		label_48.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_48.setBackground(SWTResourceManager.getColor(204, 204, 255));

		Label label_49 = new Label(this, SWT.BORDER);
		label_49.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_49.setBackground(SWTResourceManager.getColor(204, 204, 255));

		Label label_50 = new Label(this, SWT.BORDER);
		label_50.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_50.setBackground(SWTResourceManager.getColor(204, 204, 255));

		Label label_51 = new Label(this, SWT.BORDER);
		label_51.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_51.setBackground(SWTResourceManager.getColor(255, 204, 255));

		Label label_52 = new Label(this, SWT.BORDER);
		label_52.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_52.setBackground(SWTResourceManager.getColor(255, 204, 204));

		Label label_53 = new Label(this, SWT.BORDER);
		label_53.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_53.setBackground(SWTResourceManager.getColor(255, 204, 204));

		Label label_54 = new Label(this, SWT.BORDER);
		label_54.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_54.setBackground(SWTResourceManager.getColor(255, 204, 204));

		Label label_55 = new Label(this, SWT.BORDER);
		label_55.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_55.setBackground(SWTResourceManager.getColor(255, 204, 204));

		Label label_56 = new Label(this, SWT.BORDER);
		label_56.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_56.setBackground(SWTResourceManager.getColor(255, 204, 204));

		Label label_57 = new Label(this, SWT.BORDER);
		label_57.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_57.setBackground(SWTResourceManager.getColor(255, 204, 204));

		Label label_58 = new Label(this, SWT.BORDER);
		label_58.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_58.setBackground(SWTResourceManager.getColor(255, 204, 204));

		Label label_59 = new Label(this, SWT.BORDER);
		label_59.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_59.setBackground(SWTResourceManager.getColor(255, 204, 204));

		Label label_60 = new Label(this, SWT.BORDER);
		label_60.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_60.setBackground(SWTResourceManager.getColor(255, 204, 204));

		Label label_61 = new Label(this, SWT.BORDER);
		label_61.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_61.setBackground(SWTResourceManager.getColor(255, 255, 204));

		Label label_62 = new Label(this, SWT.BORDER);
		label_62.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_62.setBackground(SWTResourceManager.getColor(204, 255, 204));

		Label label_63 = new Label(this, SWT.BORDER);
		label_63.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_63.setBackground(SWTResourceManager.getColor(204, 255, 204));

		Label label_64 = new Label(this, SWT.BORDER);
		label_64.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_64.setBackground(SWTResourceManager.getColor(204, 255, 204));

		Label label_65 = new Label(this, SWT.BORDER);
		label_65.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_65.setBackground(SWTResourceManager.getColor(204, 255, 204));

		Label label_66 = new Label(this, SWT.BORDER);
		label_66.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_66.setBackground(SWTResourceManager.getColor(204, 255, 204));

		Label label_67 = new Label(this, SWT.BORDER);
		label_67.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_67.setBackground(SWTResourceManager.getColor(204, 255, 204));

		Label label_68 = new Label(this, SWT.BORDER);
		label_68.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_68.setBackground(SWTResourceManager.getColor(204, 255, 204));

		Label label_69 = new Label(this, SWT.BORDER);
		label_69.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_69.setBackground(SWTResourceManager.getColor(204, 255, 204));

		Label label_70 = new Label(this, SWT.BORDER);
		label_70.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_70.setBackground(SWTResourceManager.getColor(204, 255, 204));

		Label label_8 = new Label(this, SWT.BORDER);
		label_8.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1,
				1));
		label_8.setBackground(SWTResourceManager.getColor(204, 204, 204));

		Label label_13 = new Label(this, SWT.BORDER);
		label_13.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_13.setBackground(SWTResourceManager.getColor(153, 255, 255));

		Label label_22 = new Label(this, SWT.BORDER);
		label_22.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_22.setBackground(SWTResourceManager.getColor(153, 204, 255));

		Label label_23 = new Label(this, SWT.BORDER);
		label_23.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_23.setBackground(SWTResourceManager.getColor(153, 153, 255));

		Label label_32 = new Label(this, SWT.BORDER);
		label_32.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_32.setBackground(SWTResourceManager.getColor(153, 153, 255));

		Label label_33 = new Label(this, SWT.BORDER);
		label_33.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_33.setBackground(SWTResourceManager.getColor(153, 153, 255));

		Label label_42 = new Label(this, SWT.BORDER);
		label_42.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_42.setBackground(SWTResourceManager.getColor(153, 153, 255));

		Label label_43 = new Label(this, SWT.BORDER);
		label_43.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_43.setBackground(SWTResourceManager.getColor(153, 153, 255));

		Label label_71 = new Label(this, SWT.BORDER);
		label_71.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_71.setBackground(SWTResourceManager.getColor(153, 153, 255));

		Label label_72 = new Label(this, SWT.BORDER);
		label_72.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_72.setBackground(SWTResourceManager.getColor(153, 153, 255));

		Label label_73 = new Label(this, SWT.BORDER);
		label_73.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_73.setBackground(SWTResourceManager.getColor(204, 153, 255));

		Label label_74 = new Label(this, SWT.BORDER);
		label_74.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_74.setBackground(SWTResourceManager.getColor(255, 153, 255));

		Label label_75 = new Label(this, SWT.BORDER);
		label_75.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_75.setBackground(SWTResourceManager.getColor(255, 153, 204));

		Label label_76 = new Label(this, SWT.BORDER);
		label_76.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_76.setBackground(SWTResourceManager.getColor(255, 153, 153));

		Label label_77 = new Label(this, SWT.BORDER);
		label_77.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_77.setBackground(SWTResourceManager.getColor(255, 153, 153));

		Label label_78 = new Label(this, SWT.BORDER);
		label_78.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_78.setBackground(SWTResourceManager.getColor(255, 153, 153));

		Label label_79 = new Label(this, SWT.BORDER);
		label_79.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_79.setBackground(SWTResourceManager.getColor(255, 153, 153));

		Label label_80 = new Label(this, SWT.BORDER);
		label_80.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_80.setBackground(SWTResourceManager.getColor(255, 153, 153));

		Label label_81 = new Label(this, SWT.BORDER);
		label_81.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_81.setBackground(SWTResourceManager.getColor(255, 153, 153));

		Label label_82 = new Label(this, SWT.BORDER);
		label_82.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_82.setBackground(SWTResourceManager.getColor(255, 153, 153));

		Label label_83 = new Label(this, SWT.BORDER);
		label_83.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_83.setBackground(SWTResourceManager.getColor(255, 204, 153));

		Label label_84 = new Label(this, SWT.BORDER);
		label_84.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_84.setBackground(SWTResourceManager.getColor(255, 255, 153));

		Label label_85 = new Label(this, SWT.BORDER);
		label_85.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_85.setBackground(SWTResourceManager.getColor(204, 255, 153));

		Label label_86 = new Label(this, SWT.BORDER);
		label_86.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_86.setBackground(SWTResourceManager.getColor(153, 255, 153));

		Label label_87 = new Label(this, SWT.BORDER);
		label_87.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_87.setBackground(SWTResourceManager.getColor(153, 255, 153));

		Label label_88 = new Label(this, SWT.BORDER);
		label_88.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_88.setBackground(SWTResourceManager.getColor(153, 255, 153));

		Label label_89 = new Label(this, SWT.BORDER);
		label_89.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_89.setBackground(SWTResourceManager.getColor(153, 255, 153));

		Label label_90 = new Label(this, SWT.BORDER);
		label_90.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_90.setBackground(SWTResourceManager.getColor(153, 255, 153));

		Label label_91 = new Label(this, SWT.BORDER);
		label_91.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_91.setBackground(SWTResourceManager.getColor(153, 255, 153));

		Label label_92 = new Label(this, SWT.BORDER);
		label_92.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_92.setBackground(SWTResourceManager.getColor(153, 255, 153));

		Label label_93 = new Label(this, SWT.BORDER);
		label_93.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_93.setBackground(SWTResourceManager.getColor(153, 255, 204));

		Label label_9 = new Label(this, SWT.BORDER);
		label_9.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1,
				1));
		label_9.setBackground(SWTResourceManager.getColor(204, 204, 204));

		Label label_14 = new Label(this, SWT.BORDER);
		label_14.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_14.setBackground(SWTResourceManager.getColor(102, 255, 255));

		Label label_21 = new Label(this, SWT.BORDER);
		label_21.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_21.setBackground(SWTResourceManager.getColor(102, 204, 255));

		Label label_24 = new Label(this, SWT.BORDER);
		label_24.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_24.setBackground(SWTResourceManager.getColor(102, 153, 255));

		Label label_31 = new Label(this, SWT.BORDER);
		label_31.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_31.setBackground(SWTResourceManager.getColor(102, 102, 255));

		Label label_34 = new Label(this, SWT.BORDER);
		label_34.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_34.setBackground(SWTResourceManager.getColor(102, 102, 255));

		Label label_41 = new Label(this, SWT.BORDER);
		label_41.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_41.setBackground(SWTResourceManager.getColor(102, 102, 255));

		Label label_44 = new Label(this, SWT.BORDER);
		label_44.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_44.setBackground(SWTResourceManager.getColor(102, 102, 255));

		Label label_94 = new Label(this, SWT.BORDER);
		label_94.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_94.setBackground(SWTResourceManager.getColor(102, 102, 255));

		Label label_95 = new Label(this, SWT.BORDER);
		label_95.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_95.setBackground(SWTResourceManager.getColor(153, 102, 255));

		Label label_96 = new Label(this, SWT.BORDER);
		label_96.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_96.setBackground(SWTResourceManager.getColor(204, 102, 255));

		Label label_97 = new Label(this, SWT.BORDER);
		label_97.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_97.setBackground(SWTResourceManager.getColor(255, 102, 204));

		Label label_98 = new Label(this, SWT.BORDER);
		label_98.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_98.setBackground(SWTResourceManager.getColor(255, 102, 153));

		Label label_99 = new Label(this, SWT.BORDER);
		label_99.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_99.setBackground(SWTResourceManager.getColor(255, 102, 153));

		Label label_100 = new Label(this, SWT.BORDER);
		label_100.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_100.setBackground(SWTResourceManager.getColor(255, 102, 102));

		Label label_101 = new Label(this, SWT.BORDER);
		label_101.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_101.setBackground(SWTResourceManager.getColor(255, 102, 102));

		Label label_102 = new Label(this, SWT.BORDER);
		label_102.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_102.setBackground(SWTResourceManager.getColor(255, 102, 102));

		Label label_103 = new Label(this, SWT.BORDER);
		label_103.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_103.setBackground(SWTResourceManager.getColor(255, 102, 102));

		Label label_104 = new Label(this, SWT.BORDER);
		label_104.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_104.setBackground(SWTResourceManager.getColor(255, 102, 102));

		Label label_105 = new Label(this, SWT.BORDER);
		label_105.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_105.setBackground(SWTResourceManager.getColor(255, 153, 102));

		Label label_106 = new Label(this, SWT.BORDER);
		label_106.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_106.setBackground(SWTResourceManager.getColor(255, 204, 102));

		Label label_107 = new Label(this, SWT.BORDER);
		label_107.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_107.setBackground(SWTResourceManager.getColor(255, 255, 102));

		Label label_108 = new Label(this, SWT.BORDER);
		label_108.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_108.setBackground(SWTResourceManager.getColor(204, 255, 102));

		Label label_109 = new Label(this, SWT.BORDER);
		label_109.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_109.setBackground(SWTResourceManager.getColor(153, 255, 102));

		Label label_110 = new Label(this, SWT.BORDER);
		label_110.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_110.setBackground(SWTResourceManager.getColor(102, 255, 102));

		Label label_111 = new Label(this, SWT.BORDER);
		label_111.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_111.setBackground(SWTResourceManager.getColor(102, 255, 102));

		Label label_112 = new Label(this, SWT.BORDER);
		label_112.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_112.setBackground(SWTResourceManager.getColor(102, 255, 102));

		Label label_113 = new Label(this, SWT.BORDER);
		label_113.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_113.setBackground(SWTResourceManager.getColor(102, 255, 102));

		Label label_114 = new Label(this, SWT.BORDER);
		label_114.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_114.setBackground(SWTResourceManager.getColor(102, 255, 102));

		Label label_115 = new Label(this, SWT.BORDER);
		label_115.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_115.setBackground(SWTResourceManager.getColor(102, 255, 153));

		Label label_116 = new Label(this, SWT.BORDER);
		label_116.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_116.setBackground(SWTResourceManager.getColor(102, 255, 204));

		Label label_10 = new Label(this, SWT.BORDER);
		label_10.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_10.setBackground(SWTResourceManager.getColor(153, 153, 153));

		Label label_15 = new Label(this, SWT.BORDER);
		label_15.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_15.setBackground(SWTResourceManager.getColor(51, 255, 255));

		Label label_20 = new Label(this, SWT.BORDER);
		label_20.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_20.setBackground(SWTResourceManager.getColor(51, 204, 255));

		Label label_25 = new Label(this, SWT.BORDER);
		label_25.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_25.setBackground(SWTResourceManager.getColor(51, 153, 255));

		Label label_30 = new Label(this, SWT.BORDER);
		label_30.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_30.setBackground(SWTResourceManager.getColor(51, 102, 255));

		Label label_35 = new Label(this, SWT.BORDER);
		label_35.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_35.setBackground(SWTResourceManager.getColor(51, 51, 255));

		Label label_40 = new Label(this, SWT.BORDER);
		label_40.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_40.setBackground(SWTResourceManager.getColor(51, 51, 255));

		Label label_45 = new Label(this, SWT.BORDER);
		label_45.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_45.setBackground(SWTResourceManager.getColor(51, 51, 255));

		Label label_117 = new Label(this, SWT.BORDER);
		label_117.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_117.setBackground(SWTResourceManager.getColor(102, 51, 255));

		Label label_118 = new Label(this, SWT.BORDER);
		label_118.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_118.setBackground(SWTResourceManager.getColor(153, 51, 255));

		Label label_119 = new Label(this, SWT.BORDER);
		label_119.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_119.setBackground(SWTResourceManager.getColor(204, 51, 255));

		Label label_120 = new Label(this, SWT.BORDER);
		label_120.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_120.setBackground(SWTResourceManager.getColor(255, 51, 255));

		Label label_121 = new Label(this, SWT.BORDER);
		label_121.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_121.setBackground(SWTResourceManager.getColor(255, 51, 204));

		Label label_122 = new Label(this, SWT.BORDER);
		label_122.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_122.setBackground(SWTResourceManager.getColor(255, 51, 153));

		Label label_123 = new Label(this, SWT.BORDER);
		label_123.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_123.setBackground(SWTResourceManager.getColor(255, 51, 102));

		Label label_124 = new Label(this, SWT.BORDER);
		label_124.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_124.setBackground(SWTResourceManager.getColor(255, 51, 51));

		Label label_125 = new Label(this, SWT.BORDER);
		label_125.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_125.setBackground(SWTResourceManager.getColor(255, 51, 51));

		Label label_126 = new Label(this, SWT.BORDER);
		label_126.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_126.setBackground(SWTResourceManager.getColor(255, 51, 51));

		Label label_127 = new Label(this, SWT.BORDER);
		label_127.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_127.setBackground(SWTResourceManager.getColor(255, 102, 51));

		Label label_128 = new Label(this, SWT.BORDER);
		label_128.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_128.setBackground(SWTResourceManager.getColor(255, 153, 51));

		Label label_129 = new Label(this, SWT.BORDER);
		label_129.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_129.setBackground(SWTResourceManager.getColor(255, 204, 51));

		Label label_130 = new Label(this, SWT.BORDER);
		label_130.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_130.setBackground(SWTResourceManager.getColor(255, 255, 51));

		Label label_131 = new Label(this, SWT.BORDER);
		label_131.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_131.setBackground(SWTResourceManager.getColor(204, 255, 51));

		Label label_132 = new Label(this, SWT.BORDER);
		label_132.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_132.setBackground(SWTResourceManager.getColor(153, 255, 51));

		Label label_133 = new Label(this, SWT.BORDER);
		label_133.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_133.setBackground(SWTResourceManager.getColor(102, 255, 51));

		Label label_134 = new Label(this, SWT.BORDER);
		label_134.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_134.setBackground(SWTResourceManager.getColor(51, 255, 51));

		Label label_135 = new Label(this, SWT.BORDER);
		label_135.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_135.setBackground(SWTResourceManager.getColor(51, 255, 51));

		Label label_136 = new Label(this, SWT.BORDER);
		label_136.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_136.setBackground(SWTResourceManager.getColor(51, 255, 51));

		Label label_137 = new Label(this, SWT.BORDER);
		label_137.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_137.setBackground(SWTResourceManager.getColor(51, 255, 102));

		Label label_138 = new Label(this, SWT.BORDER);
		label_138.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_138.setBackground(SWTResourceManager.getColor(51, 255, 153));

		Label label_139 = new Label(this, SWT.BORDER);
		label_139.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_139.setBackground(SWTResourceManager.getColor(51, 255, 204));

		Label label_11 = new Label(this, SWT.BORDER);
		label_11.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_11.setBackground(SWTResourceManager.getColor(153, 153, 153));

		Label label_16 = new Label(this, SWT.BORDER);
		label_16.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_16.setBackground(SWTResourceManager.getColor(0, 255, 255));

		Label label_19 = new Label(this, SWT.BORDER);
		label_19.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_19.setBackground(SWTResourceManager.getColor(0, 204, 255));

		Label label_26 = new Label(this, SWT.BORDER);
		label_26.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_26.setBackground(SWTResourceManager.getColor(0, 153, 255));

		Label label_29 = new Label(this, SWT.BORDER);
		label_29.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_29.setBackground(SWTResourceManager.getColor(0, 102, 255));

		Label label_36 = new Label(this, SWT.BORDER);
		label_36.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_36.setBackground(SWTResourceManager.getColor(0, 51, 255));

		Label label_39 = new Label(this, SWT.BORDER);
		label_39.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_39.setBackground(SWTResourceManager.getColor(0, 0, 255));

		Label label_46 = new Label(this, SWT.BORDER);
		label_46.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_46.setBackground(SWTResourceManager.getColor(51, 0, 255));

		Label label_140 = new Label(this, SWT.BORDER);
		label_140.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_140.setBackground(SWTResourceManager.getColor(102, 0, 255));

		Label label_141 = new Label(this, SWT.BORDER);
		label_141.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_141.setBackground(SWTResourceManager.getColor(153, 0, 255));

		Label label_142 = new Label(this, SWT.BORDER);
		label_142.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_142.setBackground(SWTResourceManager.getColor(204, 0, 255));

		Label label_143 = new Label(this, SWT.BORDER);
		label_143.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_143.setBackground(SWTResourceManager.getColor(255, 0, 255));

		Label label_144 = new Label(this, SWT.BORDER);
		label_144.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_144.setBackground(SWTResourceManager.getColor(255, 0, 204));

		Label label_145 = new Label(this, SWT.BORDER);
		label_145.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_145.setBackground(SWTResourceManager.getColor(255, 0, 153));

		Label label_146 = new Label(this, SWT.BORDER);
		label_146.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_146.setBackground(SWTResourceManager.getColor(255, 0, 102));

		Label label_147 = new Label(this, SWT.BORDER);
		label_147.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_147.setBackground(SWTResourceManager.getColor(255, 0, 51));

		Label label_148 = new Label(this, SWT.BORDER);
		label_148.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_148.setBackground(SWTResourceManager.getColor(255, 0, 0));

		Label label_149 = new Label(this, SWT.BORDER);
		label_149.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_149.setBackground(SWTResourceManager.getColor(255, 51, 0));

		Label label_150 = new Label(this, SWT.BORDER);
		label_150.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_150.setBackground(SWTResourceManager.getColor(255, 102, 0));

		Label label_151 = new Label(this, SWT.BORDER);
		label_151.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_151.setBackground(SWTResourceManager.getColor(255, 153, 0));

		Label label_152 = new Label(this, SWT.BORDER);
		label_152.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_152.setBackground(SWTResourceManager.getColor(255, 204, 0));

		Label label_153 = new Label(this, SWT.BORDER);
		label_153.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_153.setBackground(SWTResourceManager.getColor(255, 255, 0));

		Label label_154 = new Label(this, SWT.BORDER);
		label_154.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_154.setBackground(SWTResourceManager.getColor(204, 255, 0));

		Label label_155 = new Label(this, SWT.BORDER);
		label_155.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_155.setBackground(SWTResourceManager.getColor(153, 255, 0));

		Label label_156 = new Label(this, SWT.BORDER);
		label_156.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_156.setBackground(SWTResourceManager.getColor(102, 255, 0));

		Label label_157 = new Label(this, SWT.BORDER);
		label_157.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_157.setBackground(SWTResourceManager.getColor(51, 255, 0));

		Label label_158 = new Label(this, SWT.BORDER);
		label_158.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_158.setBackground(SWTResourceManager.getColor(0, 255, 0));

		Label label_159 = new Label(this, SWT.BORDER);
		label_159.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_159.setBackground(SWTResourceManager.getColor(0, 255, 51));

		Label label_160 = new Label(this, SWT.BORDER);
		label_160.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_160.setBackground(SWTResourceManager.getColor(0, 255, 102));

		Label label_161 = new Label(this, SWT.BORDER);
		label_161.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_161.setBackground(SWTResourceManager.getColor(0, 255, 153));

		Label label_162 = new Label(this, SWT.BORDER);
		label_162.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_162.setBackground(SWTResourceManager.getColor(0, 255, 204));

		Label label_12 = new Label(this, SWT.BORDER);
		label_12.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_12.setBackground(SWTResourceManager.getColor(102, 102, 102));

		Label label_17 = new Label(this, SWT.BORDER);
		label_17.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_17.setBackground(SWTResourceManager.getColor(0, 204, 204));

		Label label_18 = new Label(this, SWT.BORDER);
		label_18.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_18.setBackground(SWTResourceManager.getColor(0, 204, 204));

		Label label_27 = new Label(this, SWT.BORDER);
		label_27.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_27.setBackground(SWTResourceManager.getColor(0, 153, 204));

		Label label_28 = new Label(this, SWT.BORDER);
		label_28.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_28.setBackground(SWTResourceManager.getColor(0, 102, 204));

		Label label_37 = new Label(this, SWT.BORDER);
		label_37.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_37.setBackground(SWTResourceManager.getColor(0, 51, 204));

		Label label_38 = new Label(this, SWT.BORDER);
		label_38.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_38.setBackground(SWTResourceManager.getColor(0, 0, 204));

		Label label_163 = new Label(this, SWT.BORDER);
		label_163.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_163.setBackground(SWTResourceManager.getColor(51, 0, 204));

		Label label_164 = new Label(this, SWT.BORDER);
		label_164.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_164.setBackground(SWTResourceManager.getColor(102, 0, 204));

		Label label_165 = new Label(this, SWT.BORDER);
		label_165.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_165.setBackground(SWTResourceManager.getColor(153, 0, 204));

		Label label_166 = new Label(this, SWT.BORDER);
		label_166.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_166.setBackground(SWTResourceManager.getColor(204, 0, 204));

		Label label_167 = new Label(this, SWT.BORDER);
		label_167.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_167.setBackground(SWTResourceManager.getColor(204, 0, 204));

		Label label_168 = new Label(this, SWT.BORDER);
		label_168.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_168.setBackground(SWTResourceManager.getColor(204, 0, 204));

		Label label_169 = new Label(this, SWT.BORDER);
		label_169.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_169.setBackground(SWTResourceManager.getColor(204, 0, 153));

		Label label_170 = new Label(this, SWT.BORDER);
		label_170.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_170.setBackground(SWTResourceManager.getColor(204, 0, 102));

		Label label_171 = new Label(this, SWT.BORDER);
		label_171.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_171.setBackground(SWTResourceManager.getColor(204, 0, 51));

		Label label_172 = new Label(this, SWT.BORDER);
		label_172.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_172.setBackground(SWTResourceManager.getColor(204, 0, 0));

		Label label_173 = new Label(this, SWT.BORDER);
		label_173.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_173.setBackground(SWTResourceManager.getColor(204, 51, 0));

		Label label_174 = new Label(this, SWT.BORDER);
		label_174.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_174.setBackground(SWTResourceManager.getColor(204, 102, 0));

		Label label_175 = new Label(this, SWT.BORDER);
		label_175.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_175.setBackground(SWTResourceManager.getColor(204, 153, 0));

		Label label_176 = new Label(this, SWT.BORDER);
		label_176.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_176.setBackground(SWTResourceManager.getColor(204, 204, 0));

		Label label_177 = new Label(this, SWT.BORDER);
		label_177.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_177.setBackground(SWTResourceManager.getColor(204, 204, 0));

		Label label_178 = new Label(this, SWT.BORDER);
		label_178.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_178.setBackground(SWTResourceManager.getColor(204, 204, 0));

		Label label_179 = new Label(this, SWT.BORDER);
		label_179.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_179.setBackground(SWTResourceManager.getColor(153, 204, 0));

		Label label_180 = new Label(this, SWT.BORDER);
		label_180.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_180.setBackground(SWTResourceManager.getColor(102, 204, 0));

		Label label_181 = new Label(this, SWT.BORDER);
		label_181.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_181.setBackground(SWTResourceManager.getColor(51, 204, 0));

		Label label_182 = new Label(this, SWT.BORDER);
		label_182.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_182.setBackground(SWTResourceManager.getColor(0, 204, 0));

		Label label_183 = new Label(this, SWT.BORDER);
		label_183.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_183.setBackground(SWTResourceManager.getColor(0, 204, 51));

		Label label_184 = new Label(this, SWT.BORDER);
		label_184.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_184.setBackground(SWTResourceManager.getColor(0, 204, 102));

		Label label_185 = new Label(this, SWT.BORDER);
		label_185.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_185.setBackground(SWTResourceManager.getColor(0, 204, 153));

		Label label_47 = new Label(this, SWT.BORDER);
		label_47.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_47.setBackground(SWTResourceManager.getColor(0, 204, 204));

		Label label_186 = new Label(this, SWT.BORDER);
		label_186.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_186.setBackground(SWTResourceManager.getColor(102, 102, 102));

		Label label_190 = new Label(this, SWT.BORDER);
		label_190.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_190.setBackground(SWTResourceManager.getColor(0, 153, 153));

		Label label_191 = new Label(this, SWT.BORDER);
		label_191.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_191.setBackground(SWTResourceManager.getColor(0, 153, 153));

		Label label_198 = new Label(this, SWT.BORDER);
		label_198.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_198.setBackground(SWTResourceManager.getColor(0, 153, 153));

		Label label_200 = new Label(this, SWT.BORDER);
		label_200.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_200.setBackground(SWTResourceManager.getColor(0, 102, 153));

		Label label_202 = new Label(this, SWT.BORDER);
		label_202.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_202.setBackground(SWTResourceManager.getColor(0, 51, 153));

		Label label_204 = new Label(this, SWT.BORDER);
		label_204.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_204.setBackground(SWTResourceManager.getColor(0, 0, 153));

		Label label_206 = new Label(this, SWT.BORDER);
		label_206.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_206.setBackground(SWTResourceManager.getColor(51, 0, 153));

		Label label_208 = new Label(this, SWT.BORDER);
		label_208.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_208.setBackground(SWTResourceManager.getColor(102, 0, 153));

		Label label_210 = new Label(this, SWT.BORDER);
		label_210.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_210.setBackground(SWTResourceManager.getColor(153, 0, 153));

		Label label_212 = new Label(this, SWT.BORDER);
		label_212.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_212.setBackground(SWTResourceManager.getColor(153, 0, 153));

		Label label_214 = new Label(this, SWT.BORDER);
		label_214.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_214.setBackground(SWTResourceManager.getColor(153, 0, 153));

		Label label_216 = new Label(this, SWT.BORDER);
		label_216.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_216.setBackground(SWTResourceManager.getColor(153, 0, 153));

		Label label_218 = new Label(this, SWT.BORDER);
		label_218.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_218.setBackground(SWTResourceManager.getColor(153, 0, 153));

		Label label_220 = new Label(this, SWT.BORDER);
		label_220.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_220.setBackground(SWTResourceManager.getColor(153, 0, 102));

		Label label_222 = new Label(this, SWT.BORDER);
		label_222.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_222.setBackground(SWTResourceManager.getColor(153, 0, 51));

		Label label_224 = new Label(this, SWT.BORDER);
		label_224.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_224.setBackground(SWTResourceManager.getColor(153, 0, 0));

		Label label_226 = new Label(this, SWT.BORDER);
		label_226.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_226.setBackground(SWTResourceManager.getColor(153, 51, 0));

		Label label_228 = new Label(this, SWT.BORDER);
		label_228.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_228.setBackground(SWTResourceManager.getColor(153, 102, 0));

		Label label_230 = new Label(this, SWT.BORDER);
		label_230.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_230.setBackground(SWTResourceManager.getColor(153, 153, 0));

		Label label_232 = new Label(this, SWT.BORDER);
		label_232.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_232.setBackground(SWTResourceManager.getColor(153, 153, 0));

		Label label_234 = new Label(this, SWT.BORDER);
		label_234.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_234.setBackground(SWTResourceManager.getColor(153, 153, 0));

		Label label_236 = new Label(this, SWT.BORDER);
		label_236.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_236.setBackground(SWTResourceManager.getColor(153, 153, 0));

		Label label_238 = new Label(this, SWT.BORDER);
		label_238.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_238.setBackground(SWTResourceManager.getColor(153, 153, 0));

		Label label_240 = new Label(this, SWT.BORDER);
		label_240.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_240.setBackground(SWTResourceManager.getColor(102, 153, 0));

		Label label_242 = new Label(this, SWT.BORDER);
		label_242.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_242.setBackground(SWTResourceManager.getColor(51, 153, 0));

		Label label_244 = new Label(this, SWT.BORDER);
		label_244.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_244.setBackground(SWTResourceManager.getColor(0, 153, 0));

		Label label_246 = new Label(this, SWT.BORDER);
		label_246.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_246.setBackground(SWTResourceManager.getColor(0, 153, 51));

		Label label_248 = new Label(this, SWT.BORDER);
		label_248.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_248.setBackground(SWTResourceManager.getColor(0, 153, 102));

		Label label_250 = new Label(this, SWT.BORDER);
		label_250.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_250.setBackground(SWTResourceManager.getColor(0, 153, 153));

		Label label_252 = new Label(this, SWT.BORDER);
		label_252.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_252.setBackground(SWTResourceManager.getColor(0, 153, 153));

		Label label_187 = new Label(this, SWT.BORDER);
		label_187.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_187.setBackground(SWTResourceManager.getColor(51, 51, 51));

		Label label_192 = new Label(this, SWT.BORDER);
		label_192.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_192.setBackground(SWTResourceManager.getColor(0, 102, 102));

		Label label_197 = new Label(this, SWT.BORDER);
		label_197.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_197.setBackground(SWTResourceManager.getColor(0, 102, 102));

		Label label_199 = new Label(this, SWT.BORDER);
		label_199.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_199.setBackground(SWTResourceManager.getColor(0, 102, 102));

		Label label_201 = new Label(this, SWT.BORDER);
		label_201.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_201.setBackground(SWTResourceManager.getColor(0, 102, 102));

		Label label_203 = new Label(this, SWT.BORDER);
		label_203.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_203.setBackground(SWTResourceManager.getColor(0, 51, 102));

		Label label_205 = new Label(this, SWT.BORDER);
		label_205.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_205.setBackground(SWTResourceManager.getColor(0, 0, 102));

		Label label_207 = new Label(this, SWT.BORDER);
		label_207.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_207.setBackground(SWTResourceManager.getColor(51, 0, 102));

		Label label_209 = new Label(this, SWT.BORDER);
		label_209.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_209.setBackground(SWTResourceManager.getColor(102, 0, 102));

		Label label_211 = new Label(this, SWT.BORDER);
		label_211.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_211.setBackground(SWTResourceManager.getColor(102, 0, 102));

		Label label_213 = new Label(this, SWT.BORDER);
		label_213.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_213.setBackground(SWTResourceManager.getColor(102, 0, 102));

		Label label_215 = new Label(this, SWT.BORDER);
		label_215.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_215.setBackground(SWTResourceManager.getColor(102, 0, 102));

		Label label_217 = new Label(this, SWT.BORDER);
		label_217.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_217.setBackground(SWTResourceManager.getColor(102, 0, 102));

		Label label_219 = new Label(this, SWT.BORDER);
		label_219.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_219.setBackground(SWTResourceManager.getColor(102, 0, 102));

		Label label_221 = new Label(this, SWT.BORDER);
		label_221.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_221.setBackground(SWTResourceManager.getColor(102, 0, 102));

		Label label_223 = new Label(this, SWT.BORDER);
		label_223.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_223.setBackground(SWTResourceManager.getColor(102, 0, 51));

		Label label_225 = new Label(this, SWT.BORDER);
		label_225.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_225.setBackground(SWTResourceManager.getColor(102, 0, 0));

		Label label_227 = new Label(this, SWT.BORDER);
		label_227.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_227.setBackground(SWTResourceManager.getColor(102, 51, 0));

		Label label_229 = new Label(this, SWT.BORDER);
		label_229.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_229.setBackground(SWTResourceManager.getColor(102, 102, 0));

		Label label_231 = new Label(this, SWT.BORDER);
		label_231.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_231.setBackground(SWTResourceManager.getColor(102, 102, 0));

		Label label_233 = new Label(this, SWT.BORDER);
		label_233.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_233.setBackground(SWTResourceManager.getColor(102, 102, 0));

		Label label_235 = new Label(this, SWT.BORDER);
		label_235.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_235.setBackground(SWTResourceManager.getColor(102, 102, 0));

		Label label_237 = new Label(this, SWT.BORDER);
		label_237.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_237.setBackground(SWTResourceManager.getColor(102, 102, 0));

		Label label_239 = new Label(this, SWT.BORDER);
		label_239.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_239.setBackground(SWTResourceManager.getColor(102, 102, 0));

		Label label_241 = new Label(this, SWT.BORDER);
		label_241.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_241.setBackground(SWTResourceManager.getColor(102, 102, 0));

		Label label_243 = new Label(this, SWT.BORDER);
		label_243.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_243.setBackground(SWTResourceManager.getColor(51, 102, 0));

		Label label_245 = new Label(this, SWT.BORDER);
		label_245.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_245.setBackground(SWTResourceManager.getColor(0, 102, 0));

		Label label_247 = new Label(this, SWT.BORDER);
		label_247.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_247.setBackground(SWTResourceManager.getColor(0, 102, 51));

		Label label_249 = new Label(this, SWT.BORDER);
		label_249.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_249.setBackground(SWTResourceManager.getColor(0, 102, 102));

		Label label_251 = new Label(this, SWT.BORDER);
		label_251.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_251.setBackground(SWTResourceManager.getColor(0, 102, 102));

		Label label_253 = new Label(this, SWT.BORDER);
		label_253.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_253.setBackground(SWTResourceManager.getColor(0, 102, 102));

		Label label_188 = new Label(this, SWT.BORDER);
		label_188.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_188.setBackground(SWTResourceManager.getColor(0, 0, 0));

		Label label_193 = new Label(this, SWT.BORDER);
		label_193.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_193.setBackground(SWTResourceManager.getColor(0, 51, 51));

		Label label_196 = new Label(this, SWT.BORDER);
		label_196.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_196.setBackground(SWTResourceManager.getColor(0, 51, 51));

		Label label_254 = new Label(this, SWT.BORDER);
		label_254.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_254.setBackground(SWTResourceManager.getColor(0, 51, 51));

		Label label_255 = new Label(this, SWT.BORDER);
		label_255.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_255.setBackground(SWTResourceManager.getColor(0, 51, 51));

		Label label_256 = new Label(this, SWT.BORDER);
		label_256.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_256.setBackground(SWTResourceManager.getColor(0, 51, 51));

		Label label_257 = new Label(this, SWT.BORDER);
		label_257.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_257.setBackground(SWTResourceManager.getColor(0, 0, 51));

		Label label_258 = new Label(this, SWT.BORDER);
		label_258.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_258.setBackground(SWTResourceManager.getColor(51, 0, 51));

		Label label_259 = new Label(this, SWT.BORDER);
		label_259.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_259.setBackground(SWTResourceManager.getColor(51, 0, 51));

		Label label_260 = new Label(this, SWT.BORDER);
		label_260.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_260.setBackground(SWTResourceManager.getColor(51, 0, 51));

		Label label_261 = new Label(this, SWT.BORDER);
		label_261.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_261.setBackground(SWTResourceManager.getColor(51, 0, 51));

		Label label_262 = new Label(this, SWT.BORDER);
		label_262.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_262.setBackground(SWTResourceManager.getColor(51, 0, 51));

		Label label_263 = new Label(this, SWT.BORDER);
		label_263.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_263.setBackground(SWTResourceManager.getColor(51, 0, 51));

		Label label_264 = new Label(this, SWT.BORDER);
		label_264.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_264.setBackground(SWTResourceManager.getColor(51, 0, 51));

		Label label_265 = new Label(this, SWT.BORDER);
		label_265.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_265.setBackground(SWTResourceManager.getColor(51, 0, 51));

		Label label_266 = new Label(this, SWT.BORDER);
		label_266.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_266.setBackground(SWTResourceManager.getColor(51, 0, 51));

		Label label_267 = new Label(this, SWT.BORDER);
		label_267.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_267.setBackground(SWTResourceManager.getColor(51, 0, 0));

		Label label_268 = new Label(this, SWT.BORDER);
		label_268.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_268.setBackground(SWTResourceManager.getColor(51, 51, 0));

		Label label_269 = new Label(this, SWT.BORDER);
		label_269.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_269.setBackground(SWTResourceManager.getColor(51, 51, 0));

		Label label_270 = new Label(this, SWT.BORDER);
		label_270.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_270.setBackground(SWTResourceManager.getColor(51, 51, 0));

		Label label_271 = new Label(this, SWT.BORDER);
		label_271.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_271.setBackground(SWTResourceManager.getColor(51, 51, 0));

		Label label_272 = new Label(this, SWT.BORDER);
		label_272.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_272.setBackground(SWTResourceManager.getColor(51, 51, 0));

		Label label_273 = new Label(this, SWT.BORDER);
		label_273.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_273.setBackground(SWTResourceManager.getColor(51, 51, 0));

		Label label_274 = new Label(this, SWT.BORDER);
		label_274.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_274.setBackground(SWTResourceManager.getColor(51, 51, 0));

		Label label_275 = new Label(this, SWT.BORDER);
		label_275.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_275.setBackground(SWTResourceManager.getColor(51, 51, 0));

		Label label_276 = new Label(this, SWT.BORDER);
		label_276.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_276.setBackground(SWTResourceManager.getColor(0, 51, 0));

		Label label_277 = new Label(this, SWT.BORDER);
		label_277.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_277.setBackground(SWTResourceManager.getColor(0, 51, 51));

		Label label_278 = new Label(this, SWT.BORDER);
		label_278.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_278.setBackground(SWTResourceManager.getColor(0, 51, 51));

		Label label_279 = new Label(this, SWT.BORDER);
		label_279.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_279.setBackground(SWTResourceManager.getColor(0, 51, 51));

		Label label_280 = new Label(this, SWT.BORDER);
		label_280.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_280.setBackground(SWTResourceManager.getColor(0, 51, 51));

		Label label_281 = new Label(this, SWT.BORDER);
		label_281.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
				1, 1));
		label_281.setBackground(SWTResourceManager.getColor(51, 51, 51));

		afterCreateWidgets();

	}

	private void afterCreateWidgets() {
		addPaintListener(new PaintListener() {

			@Override
			public void paintControl(PaintEvent e) {
				e.gc.setForeground(getDisplay().getSystemColor(
						SWT.COLOR_DARK_GRAY));
				for (final Control c : getChildren()) {
					if (_selected == c) {
						Rectangle b = c.getBounds();
						e.gc.setLineDash(new int[] { 2, 2 });
						e.gc.drawRectangle(b.x - 2, b.y - 2, b.width + 2,
								b.height + 2);
						break;
					}
				}
			}
		});
		for (final Control c : getChildren()) {
			c.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDown(MouseEvent e) {
					labelSelected(c);
				}
			});
		}
		_selected = getChildren()[0];
		setColor(_selected.getBackground().getRGB());
	}

	void labelSelected(final Control c) {
		_selected = c;
		setColor(c.getBackground().getRGB());
	}

	public void selectColor(RGB rgb) {
		for (Control c : getChildren()) {
			if (rgb.equals(c.getBackground().getRGB())) {
				_selected = c;
				_color = rgb;
				break;
			}
		}
		getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {
				redraw();
			}
		});
	}

	public void setColor(RGB color) {
		_color = color;
		firePropertyChange(PROP_COLOR);
		getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {
				redraw();
			}
		});
	}

	public RGB getColor() {
		return _color;
	}

	private transient final PropertyChangeSupport support = new PropertyChangeSupport(
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

	public void firePropertyChange(String property) {
		support.firePropertyChange(property, true, false);
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
