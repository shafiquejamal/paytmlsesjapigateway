import React from 'react';
import * as Redux from 'react-redux';
import { Link, hashHistory } from 'react-router';

import { sendPasswordResetLink } from './authenticationActionGenerators';

export const ResetPassword = React.createClass({
  getInitialState() {
    return {
      error: '',
      emailError: ''
    }
  },
  onSendPasswordResetLink(e) {
    e.preventDefault();
    const { dispatch } = this.props;
    console.log(this.state);
    const email = this.refs.email.value;
    dispatch(sendPasswordResetLink(email)).then(
      (response) => {
        console.log('success', response);
      },
      (response) => {
        console.log('failure', response);
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
                          <h1 className="title">Send Password Reset Link</h1>
                          <hr />
                      </div>
                  </div>
                  <div className="main-login main-center">
                      <form className="form-horizontal">
                          <div className="text-help">
                            {this.state.error}
                          </div>
                          <div className="form-group">
                              <label htmlFor="email" className="control-label">Your Email</label>
                              <div className="cols-sm-10">
                                  <div className={`input-group`}>
                                      <span className="input-group-addon"><i className="fa fa-envelope fa" aria-hidden="true"></i></span>
                                      <input type="text" className="form-control" name="email" id="email" ref="email"  placeholder="Enter your email address" />
                                  </div>
                                  <div className="text-help">
                                    {this.state.emailError}
                                  </div>
                              </div>
                          </div>

                          <div className="form-group ">
                              <button type="button" className="btn btn-primary btn-lg btn-block login-button" onClick={this.onSendPasswordResetLink}>Send Password Reset Link</button>
                          </div>
                      </form>
                  </div>
              </div>
          </div>
      </div>
    );

  }
});

export default Redux.connect((state) => { return state; })(ResetPassword);
