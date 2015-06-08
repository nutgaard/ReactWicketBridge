var React = require('react/addons');
var ReactWicketMixin = require('./ReactWicketMixin');

var Header = React.createClass({
        mixins: [ReactWicketMixin],
        getDefaultProps: function () {
            return {
                header: 'This headers',
                counter: 0,
                wicketdata: {
                    text: ''
                }
            };
        },
        increment: function () {
            this.setState({
                counter: this.state.counter + 1
            });
            this.sendToWicket("increment");
        },
        senddata: function () {
            this.sendToWicket("userdata", {
                username: 'admin',
                text: 'message sent from react, to wicket server and then back again...'
            });
        },
        render: function () {
            var wicketmessage = this.state.wicketdata ? this.state.wicketdata.text : '';

            return (
                <div>
                    <h1>{this.state.header} - {this.state.counter}</h1>
                    <button onClick={this.senddata}>Send til wicket</button>
                    <p>{wicketmessage}</p>
                </div>
            );
        }
    }
);

module.exports = Header;