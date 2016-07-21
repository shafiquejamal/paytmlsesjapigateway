import React from 'react';
import { Route, IndexRoute } from 'react-router';

import Template from './main/Template';
import LoginOrRegister from './main/LoginOrRegister';
import Register from './main/access/registration/Register';
import RegistrationSuccess from './main/access/registration/RegistrationSuccess';
import Activate from './main/access/registration/Activate';
import ActivationFailed from './main/access/registration/ActivationFailed';
import Login from './main/access/authentication/Login';
import Logout from './main/access/authentication/Logout';
import ResetPassword from './main/access/authentication/ResetPassword';
import ManageAccount from './main/user/ManageAccount';
import ChangePassword from './main/user/ChangePassword';
import PasswordChangeSuccessful from './main/user/PasswordChangeSuccessful';

export const REGISTER_LINK = "/register";
export const REGISTER_TEXT = "Register";
export const LOGIN_LINK = "/login";
export const LOGIN_TEXT = "Login";
export const LOGOUT_LINK = "/logout";
export const LOGOUT_TEXT = "Logout";
export const REGISTRATION_SUCCESS_LINK = "/registration-success";
export const MANAGE_ACCOUNT_LINK = "/manage-account";
export const MANAGE_ACCOUNT_TEXT = "My account";
export const CHANGE_PASSWORD_LINK = "/change-password";
export const CHANGE_PASSWORD_TEXT = "Change Password";
export const PASSWORD_CHANGE_SUCCESSFUL_LINK = "/password-change-successful";
export const ACTIVATE_LINK = "/activate";
export const ACTIVATION_FAILED_LINK = "/activation-failed";
export const RESET_PASSWORD_LINK = "/reset-password";
export const RESET_PASSWORD_TEXT = "Forgot your password?";

const requireLoggedIn = (nextState, replace, next) => {
  if (!localStorage.getItem('token')) {
    replace(LOGIN_LINK);
  }
  next();
}

const requireLoggedOut = (nextState, replace, next) => {
  if (localStorage.getItem('token')) {
    replace(LOGOUT_LINK);
  }
  next();
}

export default (
<Route path="/" component={Template}>
    <IndexRoute component={LoginOrRegister} />
    <Route path={REGISTER_LINK} component={Register} />
    <Route path={REGISTRATION_SUCCESS_LINK} component={RegistrationSuccess} />
    <Route path={LOGIN_LINK} component={Login} onEnter={requireLoggedOut} />
    <Route path={LOGOUT_LINK} component={Logout} />
    <Route path={MANAGE_ACCOUNT_LINK} component={ManageAccount} onEnter={requireLoggedIn} />
    <Route path={CHANGE_PASSWORD_LINK} component={ChangePassword} onEnter={requireLoggedIn} />
    <Route path={PASSWORD_CHANGE_SUCCESSFUL_LINK} component={PasswordChangeSuccessful} onEnter={requireLoggedIn} />
    <Route path={ACTIVATE_LINK} component={Activate} />
    <Route path={ACTIVATION_FAILED_LINK} component={ActivationFailed} />
    <Route path={RESET_PASSWORD_LINK} component={ResetPassword} />
</Route>
);
