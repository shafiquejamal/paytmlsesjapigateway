import React, { Component } from 'react';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import { updateContacts } from './chatContactsActionGenerators'
import { selectContact } from './chatContactsActionGenerators'
import AddContact from './AddContact'

class ChatContacts extends Component {

    renderAddContact() {
        return <AddContact />
    }

    renderList() {
        if (this.props.contacts.contacts) {
            return this.props.contacts.contacts.sort().filter(contact => contact !== this.props.username).map((contact) => {
                const activeContact = this.props.contacts.activeContact === contact ? "activeContact" : "";
                return (
                    <li
                        key={contact}
                        onClick={() => this.props.selectContact(contact) }
                        className={`list-group-item ${activeContact}`}>{contact}
                    </li>
                );
            });
        }
    }

    render() {
        return (
            <div>
                { this.props.isConnected ? this.renderAddContact() : "" }
                <ul className="list-group">
                    { this.renderList() }
                </ul>
            </div>
        );
    }

}

function mapStateToProps(state) {
    return {
        contacts: state.contacts,
        username: state.auth.username,
        isConnected : state.messages.status
    };
}

// Anything returned from this function will end up as props on the ChatContacts container.
function mapDispatchToProps(dispatch) {
    // Whenever updateContacts is called, the result should be passed to all of our reducers
    return bindActionCreators({ updateContacts, selectContact }, dispatch)
}

// promote booklist from a component to a container
export default connect(mapStateToProps, mapDispatchToProps)(ChatContacts);