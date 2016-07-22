import React from 'react';
import * as Redux from 'react-redux';
import { Link, hashHistory } from 'react-router';

import { REGISTER_LINK, REGISTER_TEXT, RESET_PASSWORD_LINK, RESET_PASSWORD_TEXT, MANAGE_ACCOUNT_LINK } from '../../../routes';
import { startLoggingInUser } from './authenticationActionGenerators';

export const Login = React.createClass({
  getInitialState() {
    return {
      loginError: ''
    }
  },
  onLogin() {
    const { dispatch } = this.props;
    const { emailOrUsername, password } = this.refs;
    dispatch(startLoggingInUser(emailOrUsername.value, password.value)).then(
      (response) => {
        if (response.data.token) {
          hashHistory.push(MANAGE_ACCOUNT_LINK);
        } else {
          this.setState({
            loginError: 'There was a problem logging you in. Please check your login credentials and try again.'
          });
        }
      },
      (response) => {
        this.setState({
          loginError: 'There was a problem logging you in. Please contact the admin if you wish to proceed.'
        });
      }
    );
  },
  render() {
    return (
      <div className="container">
          <div className="row main">
              <div className="col-md-4 col-md-offset-4">
                  <div className="panel-heading">
                      <div className="panel-title text-center">
                          <h1 className="title">Login</h1>
                          <hr />
                      </div>
                  </div>
                  <div className="main-login main-center">
                      <form className="form-horizontal">
                          <div className="text-help">
                            {this.state.loginError}
                          </div>
                          <div className="form-group">
                              <label htmlFor="emailOrUsername" className="control-label">Your Email</label>
                              <div className="cols-sm-10">
                                  <div className={`input-group`}>
                                      <span className="input-group-addon"><i className="fa fa-envelope fa" aria-hidden="true"></i></span>
                                      <input type="text" className="form-control" name="emailOrUsername" id="emailOrUsername" ref="emailOrUsername"  placeholder="Enter your Email or Username" />
                                  </div>
                                  <div className="text-help">

                                  </div>
                              </div>
                          </div>

                          <div className="form-group">
                              <label htmlFor="password" className="control-label">Password</label>
                              <div className="cols-sm-10">
                                  <div className={`input-group`}>
                                      <span className="input-group-addon"><i className="fa fa-lock fa-lg" aria-hidden="true"></i></span>
                                      <input type="password" className="form-control" name="password" id="password" ref="password" placeholder="Enter your Password" />
                                  </div>
                                  <div className="text-help">

                                  </div>
                              </div>
                          </div>

                          <div className="form-group ">
                              <button type="button" className="btn btn-primary btn-lg btn-block login-button" onClick={this.onLogin}>Login</button>
                          </div>
                          <div className="login-register">
                              <Link to={REGISTER_LINK}>{REGISTER_TEXT}</Link> <Link to={RESET_PASSWORD_LINK}>{RESET_PASSWORD_TEXT}</Link>
                          </div>
                      </form>
                  </div>
              </div>
          </div>
      </div>
    );

  }
});

export default Redux.connect((state) => { return state; })(Login);
