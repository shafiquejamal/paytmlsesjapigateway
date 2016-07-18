// import axios from 'axios';
//
// export const CHECK_USERNAME_AVAILABLE = 'CHECK_USERNAME_AVAILABLE';
//
// export const ROOT_URL = 'http://localhost:9000/'
// export const USRENAME_CHECK_ENDPOINT = 'username'

// export const startCheckUsernameAvailableActionGenerator = (username) => {
//   return function (dispatch, getState) {
//     axios.get(`${ROOT_URL}${USRENAME_CHECK_ENDPOINT}/${username}`).then(
//       (response) => {
//         dispatch(checkUsernameAvailableActionGenerator(response.data.status));
//       },
//       (response) => {
//         console.log('api failure:', response);
//       });
//   };
// };


// export const checkUsernameAvailableActionGenerator = (apiResponse) => {
//   return {
//     type: CHECK_USERNAME_AVAILABLE,
//     isUsernameIsAvailable: apiResponse
//   }
// }

import axios from 'axios';

export const ROOT_URL = 'http://localhost:9000/';
export const USRENAME_CHECK_ENDPOINT = 'username';

export const getFoo = function(username) {
    return axios.get(`${ROOT_URL}${USRENAME_CHECK_ENDPOINT}/${username}`).then(
      (response) => {
        console.log('success', response);
        console.log('response.data.status', response.data.status);
        return response.data.status;
      },
      (response) => {
        console.log('failure', response);
      }
    )
}
