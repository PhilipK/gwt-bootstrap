package com.geekvigarista.gwt.bootstrap.client.ui.base;

import com.geekvigarista.gwt.bootstrap.client.ui.resources.Span;
import com.google.gwt.user.client.ui.Widget;

/**
 * Set Span Sizes helper.
 * 
 * @author Carlos Alexandro Becker
 * @since 23/01/2012
 */
public class SizeHelper {

	public static void setSize(Span spanSize, Widget widget) {

		assert spanSize != null : "span size is null.";
		assert widget != null : "widget should not be null";

		for (Span s : Span.values()) {
			widget.removeStyleName(s.getSize());
		}
		widget.addStyleName(spanSize.getSize());
	}
}