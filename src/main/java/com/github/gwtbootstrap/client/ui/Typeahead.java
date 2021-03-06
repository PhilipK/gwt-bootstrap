package com.github.gwtbootstrap.client.ui;

import java.util.Collection;

import com.github.gwtbootstrap.client.ui.base.MarkupWidget;
import com.github.gwtbootstrap.client.ui.base.TextBoxBase;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle.Callback;
import com.google.gwt.user.client.ui.SuggestOracle.Request;
import com.google.gwt.user.client.ui.SuggestOracle.Response;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.Widget;

public class Typeahead extends MarkupWidget {

    public interface UpdaterCallback {
        String onSelection(Suggestion selectedSuggestion);
    }
    
    private int displayItems = 8;

    private int minLength = 1;

    private final SuggestOracle oracle;
    private Collection<? extends Suggestion> suggestions;

    private UpdaterCallback updaterCallback;

    /**
     * Constructor for {@link Typeahead}. Creates a {@link MultiWordSuggestOracle} to use with this
     */
    public Typeahead() {
        this(new MultiWordSuggestOracle());
    }

    /**
     * Constructor for {@link Typeahead}.
     *
     * @param oracle the oracle for this <code>Typeahead</code>
     */
    public Typeahead(SuggestOracle oracle) {
        this.oracle = oracle;
        this.updaterCallback = createDefaultUpdaterCallback();
    }

    private UpdaterCallback createDefaultUpdaterCallback() {
        return new UpdaterCallback() {
            @Override
            public String onSelection(Suggestion selectedSuggestion) {
                return selectedSuggestion.getReplacementString();
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setWidget(Widget w) {

        if (!(w instanceof TextBoxBase || w instanceof com.google.gwt.user.client.ui.TextBoxBase)) {
            throw new IllegalArgumentException("Typeahead should be set TextBoxBase childs");
        }

        super.setWidget(w);
    }

    @Override
    public Widget asWidget() {

        if (widget != null) {
            Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                @Override
                public void execute() {
                    reconfigure();
                }
            });

        }

        return super.asWidget();
    }

    /**
     * reconfigure setting.
     */
    public void reconfigure() {

        if (widget == null) {
            return;
        }

        removeDataIfExists(widget.getElement());

        configure(widget.getElement(), displayItems, minLength);
    }

    /**
     * Get the max number of items to display in the dropdown.
     *
     * @return the max number of items to display in the dropdown
     */
    public int getDisplayItemCount() {
        return displayItems;
    }

    /**
     * Set max number of items to display in the dropdown.
     *
     * @param displayItems the max number of items to display in the dropdown
     */
    public void setDisplayItemCount(int displayItems) {
        this.displayItems = displayItems;
    }

    /**
     * Get the minimum character length needed before triggering autocomplete suggestions.
     *
     * @return the minimum character length needed before triggering autocomplete suggestions
     */
    public int getMinLength() {
        return minLength;
    }

    /**
     * Set the minimum character length needed before triggering autocomplete suggestions.
     *
     * @param minLength The minimum character length needed before triggering autocomplete suggestions.
     */
    public void setMinLength(int minLength) {
        this.minLength = minLength;
    }

    public void setUpdaterCallback(UpdaterCallback updaterCallback) {
        this.updaterCallback = updaterCallback;
    }

    /**
     * Get suggest oracle
     *
     * @return oracle
     */
    public SuggestOracle getSuggestOracle() {
        return oracle;
    }

    private void query(String query, final JavaScriptObject process) {
        Callback callback = new Callback() {
            @Override
            public void onSuggestionsReady(Request request, Response response) {
                callback(process, response);
            }
        };

        if (query != null && !query.isEmpty()) {
            oracle.requestSuggestions(new Request(query, displayItems), callback);
        } else {
            oracle.requestDefaultSuggestions(new Request(), callback);
        }
    }

    private void callback(final JavaScriptObject process, Response response) {
        suggestions = response.getSuggestions();

        JsArrayString jsArrayString = JavaScriptObject.createArray().cast();

        for (Suggestion suggestion : suggestions) {
            jsArrayString.push(suggestion.getDisplayString());
        }
        callProcess(jsArrayString, process);
    }

    private String updater(String item) {
        for (Suggestion suggestion : suggestions) {
            if (suggestion.getDisplayString().equals(item)) {
                return this.updaterCallback.onSelection(suggestion);
            }
        }

        return item;
    }
    
    //@formatter:off
    private native void callProcess(JsArrayString items ,JavaScriptObject process) /*-{
        process(items);
    }-*/;

    private native void removeDataIfExists(Element element) /*-{
        var $this = $wnd.jQuery(element);
        $this.data("typeahead") && $this.removeData("typeahead");
    }-*/;
    
    public native void configure(Element element,int items, int minLength) /*-{
        var that = this;
        $wnd.jQuery(element).typeahead({
            "source" : function(query , process) {
                that.@com.github.gwtbootstrap.client.ui.Typeahead::query(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(query , process);
            },
            "items" : items,
            "minLength" : minLength,
            "updater" : function(item) {
                return that.@com.github.gwtbootstrap.client.ui.Typeahead::updater(Ljava/lang/String;)(item);
            },
            "highlighter" : function(item) {
                return item;
            }
        });
    }-*/;
    //@formatter:on
}
