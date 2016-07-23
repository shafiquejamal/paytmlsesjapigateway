import React from 'react';
import * as Redux from 'react-redux';
import { Link, hashHistory } from 'react-router';

import { resendActivationLink } from './RegistrationActionGenerators';

export const ResendActivation = React.createClass({
  getInitialState() {
    return {
      error: '',
      emailError: '',
      linkSentMessage: ''
    }
  },
  onSendPasswordResetLink(e) {
    e.preventDefault();
    const { dispatch } = this.props;
    const email = this.refs.email.value;
    dispatch(resendActivationLink(email)).then(
      (response) => {
        this.setState({
          linkSentMessage: 'If the email is registered and the user account has not been activated, then the password reset link was re-sent - please check your email.',
          error: ''
        });
      },
      (response) => {
        this.setState({error: 'There was an error re-sending the activation link. Please contact the admin to continue.', linkSentMessage: ''});
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
                          <h1 className="title">Re-send Activation Link</h1>
                          <hr />
                      </div>
                  </div>
                  <div className="main-login main-center">
                      <form className="form-horizontal">
                          <div className="text-help">
                            {this.state.error}
                          </div>
                          <div className="text-link-sent">
                            {this.state.linkSentMessage}
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
                              <button type="button" className="btn btn-primary btn-lg btn-block login-button" onClick={this.onSendPasswordResetLink}>Re-send Activation Link</button>
                          </div>
                      </form>
                  </div>
              </div>
          </div>
      </div>
    );

  }
});

export default Redux.connect((state) => { return state; })(ResendActivation);
