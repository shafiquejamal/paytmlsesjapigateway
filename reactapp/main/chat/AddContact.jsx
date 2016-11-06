import React, { Component } from 'react';
import ReactDOM from 'react-dom';
import { connect } from 'react-redux';
import * as ChatActions from './../socket/socketActionGenerators.jsx';
import { bindActionCreators } from 'redux';
import { checkAvailable } from '../access/registration/RegistrationActionGenerators';

function mapDispatchToProps(dispatch) {
    return {
        actions: bindActionCreators( {...ChatActions, checkAvailable}, dispatch)
    };
}

class AddContact extends Component {

    constructor(props) {
        super(props);
        this.state = {
            errorMessage: '',
            successMessage: '',
            userAlreadyAdded: ''
        };
    }


    handleAddContact(){
        const messageNode = ReactDOM.findDOMNode(this.refs.contactToAdd);
        let text = messageNode.value.trim();
        if(!text){
            return;
        }
        this.props.actions.addContact(text);
        messageNode.value = '';
    }



    handleInputChange(e) {
        const that = this;
        const inputValue = e.target.value;
        if (inputValue !== '') {
            that.props.actions.checkAvailable('/username', inputValue).then(
                function (response) {
                    const usernameIsRegistered = response === false;
                    if (usernameIsRegistered) {
                        const contacts = that.props.contacts.contacts;
                        if (contacts.indexOf(inputValue) > -1 || inputValue === that.props.auth.username) {
                            that.setState({
                                successMessage: '',
                                errorMessage: '',
                                userAlreadyAdded: 'This user is already in your list of contacts.'
                            });
                        } else {
                            that.setState({
                                successMessage: 'Press enter to add ' +  inputValue + '.',
                                errorMessage: '',
                                userAlreadyAdded: ''
                            });
                        }
                    } else {
                        that.setState({
                            errorMessage: 'This username is not registered',
                            successMessage: '',
                            userAlreadyAdded: ''
                        });
                    }
                },
                function (error) { }
            );
        }
    }

    handleKeyPress(e) {
        if (e.key === 'Enter' && this.state.successMessage !== '') {
            this.handleAddContact();
        }
    }

    render() {
        return (
            <div className="form-group">
                <input
                    onKeyPress={event => this.handleKeyPress(event)}
                    onChange={event => this.handleInputChange(event)}
                    className="form-control"
                    type="text"
                    ref="contactToAdd"
                    placeholder="Enter username of contact to add"/>
                <div className="text-help">
                    {this.state.errorMessage}
                </div>
                <div className="user-exists">
                    {this.state.successMessage}
                </div>
                <div className="user-exists">
                    {this.state.userAlreadyAdded}
                </div>
            </div>

        );

    }

}

export default connect((state) => {
    return state;
}, mapDispatchToProps)(AddContact);