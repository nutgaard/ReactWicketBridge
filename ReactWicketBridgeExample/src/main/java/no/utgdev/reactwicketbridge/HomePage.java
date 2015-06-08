package no.utgdev.reactwicketbridge;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class HomePage extends WebPage {
    private static final long serialVersionUID = 1L;


    public HomePage(final PageParameters parameters) {
        super(parameters);
        add(new ReactTestComponent("testpanel1"));
        add(new ReactTestComponent("testpanel2"));
    }


}
