import axios from 'axios';
import validator from 'validator';

import { ROOT_URL } from '../../ConfigurationPaths';

const LOGIN_ENDPOINT = '/authenticate';
const REQUEST_PASSWORD_RESET_ENDPOINT = '/send-password-reset-link';
const PASSWORD_RESET_ENDPOINT = '/reset-password';
export const LOGIN_USER = 'LOGIN_USER';
export const LOGOUT_USER = 'LOGOUT_USER';
const LOGOUT_ALL_DEVICES_ENDPOINT = '/logout-all-devices';

export const startResettingPassword = function(email, code, newPassword) {
  return (dispatch, getState) => {
    return axios.post(`${ROOT_URL}${PASSWORD_RESET_ENDPOINT}`,
      {email, code, newPassword});
  }
};

export const sendPasswordResetLink = function(email) {
  return (dispatch, getState) => {
    return axios.post(`${ROOT_URL}${REQUEST_PASSWORD_RESET_ENDPOINT}`, {email});
  }
};

export const startLoggingInUser = function(emailOrUsername, password) {
  var loginCredentials = {};
  return (dispatch, getState) => {
    if (validator.isEmail(emailOrUsername)) {
      loginCredentials = {
        email: emailOrUsername,
        password: password
      };
    } else {
      loginCredentials = {
        username: emailOrUsername,
        password: password
      };
    }

    return axios.post(`${ROOT_URL}${LOGIN_ENDPOINT}`, loginCredentials).then(
      (response) => {
        if (response.data.token) {
          dispatch(loginUser(response.data.email, response.data.username));
          localStorage.setItem('token',response.data.token);
          localStorage.setItem('email',response.data.email);
          localStorage.setItem('username',response.data.username);
        }
        return response;
      },
      (response) => {
        return response;
      }
    );
  }
};

export const startLoggingOutAllDevices = function() {
  return (dispatch, getState) => {
    axios.post(`${ROOT_URL}${LOGOUT_ALL_DEVICES_ENDPOINT}`,
      { no: 'data'},
      { headers: { authorization: "Bearer " + localStorage.getItem('token') }});
    dispatch(startLoggingOutUser());
  }
};

export const startLoggingOutUser = function() {
  return (dispatch, getState) => {
    localStorage.clear();
    localStorage.removeItem('token');
    localStorage.removeItem('email');
    localStorage.removeItem('username');
    localStorage.removeItem('chatContacts');
    localStorage.removeItem('chatMessages');
    dispatch(logoutUser());
  };
};

export const loginUser = (email, username) => {
  return {
    type: LOGIN_USER,
    email,
    username
  }
};

export const logoutUser = () => {
  return {
    type: LOGOUT_USER
  }
};
