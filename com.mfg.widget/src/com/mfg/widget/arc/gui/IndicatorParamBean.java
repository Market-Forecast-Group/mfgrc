package com.mfg.widget.arc.gui;

import javax.xml.bind.annotation.XmlRootElement;

import com.mfg.interfaces.symbols.AbstractIndicatorParamBean;
import com.mfg.utils.PropertiesEx;
import com.mfg.widget.interfaces.IWidgetParameters;

/**
 * 
 * @author root
 */
@XmlRootElement(name = "indicatorSettings")
public class IndicatorParamBean extends AbstractIndicatorParamBean implements
		IWidgetParameters {
	/**
     * 
     */
	private static final long serialVersionUID = -9220825600291998978L;
	private transient PropertiesEx fProperties;

	public IndicatorParamBean() {
	}

	@Override
	public String getWidgetAlgorithm() {
		return "Channel";
	}

	/**
	 * Sets an extended set of properties for the indicator.
	 * 
	 * <p>
	 * This object usually it is read from a file *.properties located in the
	 * workspace opened by the application (at the time of writing it is called
	 * mfg.properties).
	 * 
	 * <p>
	 * The indicator uses this object as a last resort to know the value of a
	 * property. This object is usually used for least used, experimental
	 * properties.
	 * 
	 * @param properties
	 *            the properties to be set.
	 */
	public void setProperties(PropertiesEx properties) {
		fProperties = properties;
	}

	/**
	 * returns the extended set of properties. If the object is not set a new,
	 * empty property object is created.
	 * 
	 * @return a new property object.
	 */
	public PropertiesEx getProperties() {
		if (fProperties == null) {
			fProperties = new PropertiesEx();
		}
		return fProperties;
	}

}
