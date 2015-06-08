function wicketsend(data) {
    Wicket.Ajax.ajax({
        "u": "./?1-1.IBehaviorListener.2-component",
        "c": "component",
        "ep": [
            {"name": "param1", "value": "value1"},
            {"name": "param2", "value": "value2"}]
    });
}
var ReactWicketMixin = {
    componentWillMount: function () {
        this.sendToWicket = function (action, data) {
            Wicket.Ajax.ajax({
                "u": this.props.wicketurl,
                "c": this.props.wicketcomponent,
                "ep": [
                    {"name": action, "value": JSON.stringify(data)}
                ]
            });
        }
    },
    getInitialState: function () {
        return this.props;
    },
    componentWillReceiveProps: function (props) {
        this.setState(props);
    }
};

module.exports = ReactWicketMixin;