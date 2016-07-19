import axios from 'axios';
import validator from 'validator';

import { ROOT_URL } from 'configuration';

export const LOGIN_ENDPOINT = '/authenticate';
export const LOGIN_USER = 'LOGIN_USER';
export const LOGOUT_USER = 'LOGOUT_USER';

export const startLoggingInUser = function(emailOrUsername, password) {
  var loginCredentials = {}
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
    };
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

export const startLoggingOutUser = function() {
  return (dispatch, getState) => {
    localStorage.removeItem('token');
    localStorage.removeItem('email');
    localStorage.removeItem('username');
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
    type: LOGOUT_USER,
  }
};
