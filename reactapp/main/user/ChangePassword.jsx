import React, { Component } from 'react';
import * as Redux from 'react-redux';
import { Link, hashHistory } from 'react-router';

import { PASSWORD_CHANGE_SUCCESSFUL_LINK, LOGIN_LINK } from '../../routes';
import { startChangingPassword } from './userActionGenerators';
import { startLoggingOutUser } from '../access/authentication/authenticationActionGenerators';

export const ChangePassword = React.createClass({
    getInitialState: function() {
      return {
        changePasswordError: '',
        currentpasswordError: '',
        newpasswordError: '',
        confirmError: ''
      };
    },
    checkPassword: function(e) {
      const inputValue = e.target.value;
      const checkVariable = e.target.getAttribute('data-check').toLowerCase();
      const errorVariable = checkVariable + 'Error';
      this.setState({
          [errorVariable]: inputValue === '' ? 'This field is required' : ''
      });
      if ((checkVariable === 'confirm' || checkVariable === 'newpassword' ) && this.refs.newpassword.value !== this.refs.confirm.value) {
        this.setState({
          confirmError: 'Passwords do not match'
        });
      } else {
        this.setState({
          confirmError: ''
        });
      }
    },
    onChangePassword() {
      const {currentpasswordError, newpasswordError, confirmError} = this.state;
      const { dispatch } = this.props;
      if (currentpasswordError === '' && newpasswordError === '' && confirmError === '' ) {
        dispatch(startChangingPassword(this.refs.currentpassword.value, this.refs.newpassword.value)).then(
          (response) => {
            if (response.data.status !== 'success') {
              this.setState({
                changePasswordError: 'Your password could not be changed. Please check the current and new passwords and try again.'
              });
            } else {
              hashHistory.push(PASSWORD_CHANGE_SUCCESSFUL_LINK);
            }
          },
          (response) => {
            if (response.status === 401) {
              dispatch(startLoggingOutUser());
              hashHistory.push(LOGIN_LINK);
            } else {
              this.setState({
                changePasswordError: 'Sorry, your password could not be changed. Please contact the admin.'
              });
            }
          });
      }
    },
    render() {
        return (
            <div className="container">
                <div className="row main">
                    <div className="col-md-4 col-md-offset-4">
                        <div className="panel-heading">
                            <div className="panel-title text-center">
                                <h1 className="title">Change Password</h1>
                                <hr />
                            </div>
                        </div>
                        <div className="main-login main-center">
                            <form className="form-horizontal">
                              <div className="text-help">
                                {this.state.changePasswordError}
                              </div>
                                <div className="form-group">
                                    <label htmlFor="currentpassword" className="control-label">Current Password</label>
                                    <div className="cols-sm-10">
                                        <div className={`input-group ${this.state.currentpasswordError !== '' ? 'has-danger' : ''}`}>
                                            <span className="input-group-addon"><i className="fa fa-users fa" aria-hidden="true"></i></span>
                                            <input type="password" className="form-control" name="currentpassword" id="currentpassword" ref="currentpassword" placeholder="Enter your current password" data-check="currentpassword" onBlur={this.checkPassword} onChange={this.checkPassword}/>
                                        </div>
                                        <div className="text-help">
                                          {this.state.currentpasswordError}
                                        </div>
                                    </div>
                                </div>

                                <div className="form-group">
                                    <label htmlFor="newpassword" className="control-label">New Password</label>
                                    <div className="cols-sm-10">
                                        <div className={`input-group ${this.state.newpasswordError !== '' ? 'has-danger' : ''}`}>
                                            <span className="input-group-addon"><i className="fa fa-lock fa-lg" aria-hidden="true"></i></span>
                                            <input type="password" className="form-control" name="newpassword" id="newpassword" ref="newpassword" placeholder="Enter your new password" data-check="newpassword" ref="newpassword" onBlur={this.checkPassword} onChange={this.checkPassword} />
                                        </div>
                                        <div className="text-help">
                                          {this.state.newpasswordError}
                                        </div>
                                    </div>
                                </div>

                                <div className="form-group">
                                    <label htmlFor="confirm" className="control-label">Confirm New Password</label>
                                    <div className="cols-sm-10">
                                        <div className={`input-group ${this.state.confirmError !== ''  ? 'has-danger' : ''}`}>
                                            <span className="input-group-addon"><i className="fa fa-lock fa-lg" aria-hidden="true"></i></span>
                                            <input type="password" className="form-control" name="confirm" id="confirm"  placeholder="Confirm your new password" data-check="confirm" ref="confirm" onBlur={this.checkPassword} onChange={this.checkPassword} />
                                        </div>
                                        <div className="text-help">
                                          {this.state.confirmError}
                                        </div>
                                    </div>
                                </div>

                                <div className="form-group ">
                                    <button type="button" className="btn btn-primary btn-lg btn-block login-button" onClick={this.onChangePassword}>Change Password</button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        );
    }
});

export default Redux.connect((state) => {
  return state;
})(ChangePassword);
