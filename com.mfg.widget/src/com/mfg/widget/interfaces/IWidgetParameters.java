package com.mfg.widget.interfaces;

import com.mfg.utils.IIdentifiable;


/**
The widget parameters is an object which stores the "fingerprint"
of a widget... all the parameters which this a widget uses to do
its computations.

The widget parameters are cloneable and are, of course, persistent.
*/
public interface IWidgetParameters extends IIdentifiable {

 /**
    @return the string which identifies this widget. From this
    object a new widget can be created.
  */
 public String getWidgetAlgorithm();
 
}
