import React from 'react';
import ReactDOM from 'react-dom';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import * as ChatActions from './chatActionGenerators.jsx';
import ChatLogin from './ChatLogin';
import ChatContacts from './ChatContacts';

function mapStateToProps(state) {
    return {
        messages: state.messages,
        isConnected : state.messages.status
    };
}

function mapDispatchToProps(dispatch) {
    return {
        actions: bindActionCreators(ChatActions, dispatch)
    };
}


class Chat extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            recipient: 'some recipient'
        };
    }


    handleSendPost(){
        const messageNode = ReactDOM.findDOMNode(this.refs.message);
        let text = messageNode.value.trim();

        if(!text){
            return;
        }
        this.props.actions.postMessage(text, this.state.recipient);
        messageNode.value = '';
    }

    handleKeyPress(e) {
        if (e.key === 'Enter') {
            this.handleSendPost();
        }
    }

    renderInputBox() {
        return (
            <div className="form-group">
                <input
                    onKeyPress={this.handleKeyPress.bind(this)}
                    className="form-control"
                    type="text"
                    ref="message"
                    placeholder="message"/>
            </div>
        );
    }

    renderMessages(){
        console.log('render messages props', this.props);
        return (
            <div>
                <div>
                    <ul className="chat-message-list">
                        {
                            this.props.messages.conversation.map(msg =>
                                <li className="list-group-item">{msg.message}</li>
                            )
                        }
                    </ul>
                </div>
                { this.props.isConnected ? this.renderInputBox() : "" }
            </div>
        );
    }

    render(){
        return (
            <div>
                <div className="row">
                    <div className="col-md-2">
                        <ChatLogin /> <br/>
                        <ChatContacts />
                    </div>
                    <div className="col-md-10">
                        { this.renderMessages() }
                    </div>
                </div>
            </div>
        );
    }

}

export default connect(mapStateToProps, mapDispatchToProps)(Chat);