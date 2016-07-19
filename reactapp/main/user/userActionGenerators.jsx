import axios from 'axios';

import { ROOT_URL } from 'configuration';

export const CHANGE_PASSWORD_ENDPOINT = '/change-password ';

export const startChangingPassword = function(currentPassword, newPassword) {
  return (dispatch, getState) => {
    return axios.post(`${ROOT_URL}${CHANGE_PASSWORD_ENDPOINT}`,
      {currentPassword, newPassword},
      { headers: { authorization: localStorage.getItem('token') }});
  };
};
