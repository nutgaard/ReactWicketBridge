package no.utgdev.reactwicketbridge;

import org.apache.wicket.ajax.AjaxRequestTarget;

public interface ReactComponentCallback<T> {
    public void onCallback(AjaxRequestTarget target, T data);
}
