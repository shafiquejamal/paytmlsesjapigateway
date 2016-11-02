import React from 'react';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import * as ChatActions from './chatActionGenerators';

function mapStateToProps(state) {
    return {
        isConnected: state.messages.status
    };
}

function mapDispatchToProps(dispatch) {
    return {
        actions: bindActionCreators(ChatActions, dispatch)
    };
}

class ChatLogin extends React.Component {

    handleClick() {
        this.props.isConnected ? this.props.actions.disconnect() : this.props.actions.connect()
    }

    renderBtn() {
        if (this.props.isConnected) {
            return <button className="btn btn-danger" type="button" onClick={() => this.handleClick()}>Go offline</button>
        } else {
            return <button className="btn btn-success" type="button" onClick={() => this.handleClick()}>Go online</button>
        }
    }

    render(){
        return (
            this.renderBtn()
        );
    }

}




export default connect(mapStateToProps, mapDispatchToProps)(ChatLogin);