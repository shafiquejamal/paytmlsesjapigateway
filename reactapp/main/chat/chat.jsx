import React from 'react';
import ReactDOM from 'react-dom';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import * as ChatActions from './chatActionGenerators.jsx';
import ChatLogin from './ChatLogin';
import ChatContacts from './ChatContacts';
import ChatMessage from './ChatMessage';

function mapStateToProps(state) {
    return {
        messages: state.messages,
        isConnected : state.messages.status,
        activeContact: state.contacts.activeContact,
        auth: state.auth
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
    }


    handleSendPost(){
        const messageNode = ReactDOM.findDOMNode(this.refs.message);
        let text = messageNode.value.trim();

        if(!text){
            return;
        }
        this.props.actions.postMessage(text, this.props.activeContact);
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
        console.log('this.props', this.props);
        return (
            <div>
                <div>
                    <ul className="list-group chat-message-list">
                        {
                            this.props.messages.conversation.filter(msg => msg.message.to === this.props.activeContact || msg.message.from === this.props.activeContact).map(msg =>
                                <ChatMessage
                                    key={msg.message.time}
                                    msg={msg}
                                    chatMessageToFromClass={ msg.message.to === this.props.auth.username ? "bubble-left" : "bubble-right"}/>)
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