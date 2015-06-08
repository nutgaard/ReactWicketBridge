package no.utgdev.reactwicketbridge;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;

import java.util.*;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;
import static no.utgdev.reactwicketbridge.SerializeUtils.deserialize;
import static org.apache.wicket.markup.head.OnDomReadyHeaderItem.forScript;

public class ReactComponentPanel extends MarkupContainer {
    public static final String NS = "window.NS.";
    public static final String REACT = NS + "React";
    public static final String COMPONENTS = NS + "Components";
    public static final String INITIALIZED = NS + "InitializedComponents";

    private final transient Map<String, List<CallbackWrapper>> callbacks = new HashMap<>();

    public ReactComponentPanel(String wicketId, String componentName) {
        this(wicketId, componentName, new HashMap<>());
    }

    public ReactComponentPanel(String wicketId, final String componentName, final Map<String, Object> props) {
        super(wicketId);
        setOutputMarkupId(true);
        setOutputMarkupPlaceholderTag(true);

        add(new AbstractDefaultAjaxBehavior() {
            @Override
            protected void respond(AjaxRequestTarget target) {
                WebRequest request = ((WebRequest) RequestCycle.get().getRequest());
                Set<String> paramnames = request.getQueryParameters().getParameterNames();

                for (String paramname : paramnames) {
                    String data = request.getQueryParameters().getParameterValue(paramname).toString();
                    ofNullable(callbacks.get(paramname)).orElse(new ArrayList<>())
                            .stream()
                            .forEach((callbackwrapper) -> {
                                if (data == null || data.isEmpty() || callbackwrapper.type == Void.class) {
                                    callbackwrapper.callback.onCallback(target, null);
                                } else {
                                    Object object = deserialize(data, callbackwrapper.type);
                                    callbackwrapper.callback.onCallback(target, object);
                                }
                            });
                }
            }

            @Override
            public void renderHead(Component component, IHeaderResponse response) {
                Map<String, Object> augmentedprops = augmentedProps(props, getCallbackUrl());
                response.render(forScript(initializeScript(componentName, augmentedprops)));
                super.renderHead(component, response);
            }
        });
    }

    private Map<String, Object> augmentedProps(Map<String, Object> props, CharSequence callbackUrl) {
        props.put("wicketurl", callbackUrl);
        props.put("wicketcomponent", getMarkupId());
        return props;
    }

    public void setState(Map<String, Object> state) {
        call("setState", state);
    }

    public void call(String method, Object... args) {
        String arguments = asList(args)
                .stream()
                .map(SerializeUtils::serialize)
                .reduce((String s, String t) -> s + ", " + t)
                .orElse("");

        send(callScript(method, arguments));
    }

    public <T> void addCallback(String action, Class<T> type, ReactComponentCallback<T> callback) {
        List<CallbackWrapper> callbackList = ofNullable(callbacks.get("action"))
                .orElse(new ArrayList<>());
        CallbackWrapper<T> wrapper = new CallbackWrapper<>(type, callback);
        callbackList.add(wrapper);

        callbacks.put(action, callbackList);
    }

    private void send(final String js) {
        getTarget().ifPresent((AjaxRequestTarget target) -> target.appendJavaScript(js));
    }

    private Optional<AjaxRequestTarget> getTarget() {
        AjaxRequestTarget target = RequestCycle.get().find(AjaxRequestTarget.class);
        if (target == null) {
            return Optional.empty();
        }
        return Optional.of(target);
    }

    public String initializeScript(String componentName, Map<String, Object> props) {
        return createScript(componentName, props) + renderScript();
    }

    private String renderScript() {
        return format("%s.%s = %s.render(%s.%s, document.getElementById('%s'));", INITIALIZED, this.getMarkupId(), REACT, INITIALIZED, this.getMarkupId(), this.getMarkupId());
    }

    private String createScript(String componentName, Map<String, Object> props) {
        String json = SerializeUtils.serialize(props);
        return format("%s.%s = %s.createElement(%s.%s, %s);", INITIALIZED, this.getMarkupId(), REACT, COMPONENTS, componentName, json);
    }

    private String callScript(String methodName, String arguments) {
        return format("%s.%s.%s(%s);", INITIALIZED, this.getMarkupId(), methodName, arguments);
    }

    private static class CallbackWrapper<T> {
        public final Class<T> type;
        public final ReactComponentCallback<T> callback;

        public CallbackWrapper(Class<T> type, ReactComponentCallback<T> callback) {
            this.type = type;
            this.callback = callback;
        }
    }
}
