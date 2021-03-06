package no.utgdev.reactwicketbridge;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.panel.Panel;

import java.util.HashMap;

public class ReactTestComponent extends Panel {

    private int counter = 0;
    private User user = null;

    public ReactTestComponent(String id) {
        super(id);

        ReactComponentPanel reactpanel = new ReactComponentPanel("react", "Header", createState());

        reactpanel.addCallback("increment", Void.class, (target, ignore) -> counter++);
        reactpanel.addCallback("userdata", User.class, (target, user) -> {
            this.user = user;
            reactpanel.setState(createState());
        });

        AjaxLink serverlink = new AjaxLink("server") {
            @Override
            public void onClick(AjaxRequestTarget ajaxRequestTarget) {
                counter++;
                reactpanel.setState(createState());
            }
        };
        AjaxLink clientlink = new AjaxLink("client") {
            @Override
            public void onClick(AjaxRequestTarget ajaxRequestTarget) {
                reactpanel.call("increment");
            }
        };
        add(reactpanel, serverlink, clientlink);
    }

    private HashMap<String, Object> createState() {
        return new HashMap<String, Object>() {{
            put("header", "From wicket");
            put("counter", counter);
            put("wicketdata", user);
        }};
    }
}
