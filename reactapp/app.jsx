import React from 'react';
import ReactDOM from 'react-dom';
import { Provider } from 'react-redux';
import { Router, hashHistory } from 'react-router';
import promise from 'redux-promise';
import WSInstance from './main/WS';
import * as ChatActions from './main/chat/chatActionGenerators';
import * as ActionTypes from './main/chat/chatActionTypes';
import { WS_ROOT_URL } from './main/ConfigurationPaths';
import { socketConfiguration } from './main/socketConfiguration';

import { updateMessagesApplicationLoad } from './main/chat/chatMessagesActionGenerators';
import { getContactsApplicationLoad } from './main/chat/chatContactsActionGenerators.jsx';

import routes from './routes';
import { LOGIN_USER } from './main/access/authentication/authenticationActionGenerators'

var store = require('configureStore').configure();

const token = localStorage.getItem('token');
const email = localStorage.getItem('email');
const username = localStorage.getItem('username');
if (token) {
  store.dispatch({
    type: LOGIN_USER,
    email,
    username
  })
}

const messages = JSON.parse(localStorage.getItem('chatMessages'));
if (messages) {
    store.dispatch(updateMessagesApplicationLoad(messages));
}

const testContacts = ['shafique', 'shafiquep', 'shafiqueksg'];
localStorage.setItem('chatContacts', JSON.stringify(testContacts));
const chatContacts = JSON.parse(localStorage.getItem('chatContacts'));
if (chatContacts) {
    store.dispatch(getContactsApplicationLoad(chatContacts));
}

require('style!css!sass!applicationStyles');

ReactDOM.render(
    <Provider store={store}>
        <Router history={hashHistory} routes={routes} />
    </Provider>,
    document.getElementById('app')
);

const sock = socketConfiguration(store);

store.subscribe(() => sock.wsListener());