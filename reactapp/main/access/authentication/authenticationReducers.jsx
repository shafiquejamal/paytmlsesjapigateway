import { LOGIN_USER, LOGOUT_USER } from './authenticationActionGenerators';

export const authenticationReducer = (state = {}, action) => {
  console.log('authenticationReducer', 'reducing...')
  switch (action.type) {
    case LOGIN_USER:
      console.log('LOGIN_USER', state);
     return {
       ...state,
       authenticated: true
     };
     break;
    case LOGOUT_USER:
      console.log('LOGOUT_USER', state);
      return {
        ...state,
        authenticated: false
      }
     break;
    default:
      return state;
  }
}
