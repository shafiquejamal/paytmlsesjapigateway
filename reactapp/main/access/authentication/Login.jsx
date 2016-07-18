import React from 'react';
import * as Redux from 'react-redux';
import { reduxForm } from 'redux-form';
import { Link } from 'react-router';

import { REGISTER_LINK, REGISTER_TEXT } from '../../../routes';

export const Login = React.createClass({
  onSubmit: function() {

  },
  render() {
    const {fields: {emailOrUsername, password}, handleSubmit} = this.props;
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
                      <form className="form-horizontal" onSubmit={handleSubmit(this.onSubmit)}>

                          <div className="form-group">
                              <label htmlFor="emailOrUsername" className="control-label">Your Email</label>
                              <div className="cols-sm-10">
                                  <div className={`input-group ${emailOrUsername.touched && emailOrUsername.invalid ? 'has-danger' : ''}`}>
                                      <span className="input-group-addon"><i className="fa fa-envelope fa" aria-hidden="true"></i></span>
                                      <input type="text" className="form-control" name="emailOrUsername" id="emailOrUsername"  placeholder="Enter your Email or Username"  {...emailOrUsername} />
                                  </div>
                                  <div className="text-help">
                                    {emailOrUsername.touched ? emailOrUsername.error : ''}
                                  </div>
                              </div>
                          </div>

                          <div className="form-group">
                              <label htmlFor="password" className="control-label">Password</label>
                              <div className="cols-sm-10">
                                  <div className={`input-group ${password.touched && password.invalid ? 'has-danger' : ''}`}>
                                      <span className="input-group-addon"><i className="fa fa-lock fa-lg" aria-hidden="true"></i></span>
                                      <input type="password" className="form-control" name="password" id="password"  placeholder="Enter your Password" {...password}/>
                                  </div>
                                  <div className="text-help">
                                    {password.touched ? password.error : ''}
                                  </div>
                              </div>
                          </div>

                          <div className="form-group ">
                              <button type="button" className="btn btn-primary btn-lg btn-block login-button">Login</button>
                          </div>
                          <div className="login-register">
                              <Link to={REGISTER_LINK}>{REGISTER_TEXT}</Link>
                          </div>
                      </form>
                  </div>
              </div>
          </div>
      </div>
    );

  }
});

function validate(values) {
  const errors = {};

  if (!values.emailOrUsername) {
    errors.emailOrUsername = 'Enter your email address or username';
  }

  if (!values.password) {
    errors.password = 'Enter a password';
  }
  return errors;
}

export default reduxForm({
  form: 'LoginForm',
  fields: ['emailOrUsername', 'password'],
  validate
}, (state) => {
  return state;
}, null)(Login);
