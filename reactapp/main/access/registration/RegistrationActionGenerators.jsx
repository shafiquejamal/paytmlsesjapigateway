import axios from 'axios';
import { ROOT_URL } from '../../ConfigurationPaths';

const USERNAME_CHECK_ENDPOINT = '/username';
const EMAIL_CHECK_ENDPOINT = '/email';
const REGISTER_ENDPOINT = '/register';
const ACTIVATE_ENDPOINT = '/activate';
const RESEND_ACTIVATION_LINK_ENDPOINT = '/resend-activation-link';

export const resendActivationLink = (email) => {
  return function(dispatch, getState) {
    return axios.post(`${ROOT_URL}${RESEND_ACTIVATION_LINK_ENDPOINT}`, {email});
  }
}

export const checkAvailable = (endpoint, value) => {
  return function(dispatch, getState) {
    return axios.get(`${ROOT_URL}${endpoint}/${value}`).then(
      (response) => {
        return response.data.status;
      },
      (response) => {
      }
    );
  }
};

export const registerUser = (email, username, password) => {
  return function(dispatch, getState) {
    return axios.post(`${ROOT_URL}${REGISTER_ENDPOINT}`, {email, username, password});
    }
};

export const startActivatingUser = (email, code) => {
  return function(dispatch, getState) {
    return axios.post(`${ROOT_URL}${ACTIVATE_ENDPOINT}`, {email, code});
  }
};
