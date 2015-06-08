var React = require('react/addons');
var ReactWicketMixin = require('./ReactWicketMixin');

var Header = React.createClass({
        mixins: [ReactWicketMixin],
        getDefaultProps: function () {
            return {
                header: 'This headers',
                counter: 0
            };
        },
        increment: function () {
            this.setState({
                counter: this.state.counter + 1
            });
            this.sendToWicket("increment");
        },
        senddata: function(){
            this.sendToWicket("brukerdata", {username: 'admin', text: 'min melding'});
        },
        render: function () {
            return (
                <div>
                    <h1>{this.state.header} - {this.state.counter}</h1>
                    <button onClick={this.senddata}>Mer data</button>
                </div>
            );
        }
    }
);

module.exports = Header;