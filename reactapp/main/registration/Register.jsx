import React from 'react';
import * as Redux from 'react-redux';
import { reduxForm } from 'redux-form';
import { getFoo } from './RegistrationActionGenerators';

// http://bootsnipp.com/snippets/featured/register-page
export const Register = React.createClass({
    getInitialState: function() {
      return {
        isUsernameIsAvailable: false
      };
    },
    checkUsernameAvailable: function(e) {
      var that = this;
      getFoo(this.refs.username.value).then(
        function (response) {
          that.setState({
            isUsernameIsAvailable: response
          });
        },
        function (error) {
          console.log('error', error);
        }
      );
    },
    onSubmit: function() {

    },
    render() {
        const {fields: {email, username, password, confirm}, handleSubmit} = this.props;
        return (
            <div className="container">
                <div className="row main">
                    <div className="col-md-4 col-md-offset-4">
                        <div className="panel-heading">
                            <div className="panel-title text-center">
                                <h1 className="title">Company Name</h1>
                                <hr />
                            </div>
                        </div>
                        <div className="main-login main-center">
                            <form className="form-horizontal" onSubmit={handleSubmit(this.onSubmit)}>

                                <div className="form-group">
                                    <label htmlFor="email" className="control-label">Your Email</label>
                                    <div className="cols-sm-10">
                                        <div className={`input-group ${email.touched && email.invalid ? 'has-danger' : ''}`}>
                                            <span className="input-group-addon"><i className="fa fa-envelope fa" aria-hidden="true"></i></span>
                                            <input type="text" className="form-control" name="email" id="email"  placeholder="Enter your Email"  />
                                        </div>
                                        <div className="text-help">
                                          {email.touched ? email.error : ''} {this.state.isUsernameIsAvailable ? 'true' : 'false'}
                                        </div>
                                    </div>
                                </div>

                                <div className="form-group">
                                    <label htmlFor="username" className="control-label">Username</label>
                                    <div className="cols-sm-10">
                                        <div className={`input-group ${username.touched && username.invalid ? 'has-danger' : ''}`}>
                                            <span className="input-group-addon"><i className="fa fa-users fa" aria-hidden="true"></i></span>
                                            <input type="text" className="form-control" name="username" id="username" ref="username" placeholder="Choose a Username" onChange={this.checkUsernameAvailable}/>
                                        </div>
                                        <div className="text-help">
                                          {username.touched ? username.error : ''}
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

                                <div className="form-group">
                                    <label htmlFor="confirm" className="control-label">Confirm Password</label>
                                    <div className="cols-sm-10">
                                        <div className={`input-group ${confirm.touched && confirm.invalid ? 'has-danger' : ''}`}>
                                            <span className="input-group-addon"><i className="fa fa-lock fa-lg" aria-hidden="true"></i></span>
                                            <input type="password" className="form-control" name="confirm" id="confirm"  placeholder="Confirm your Password" {...confirm}/>
                                        </div>
                                        <div className="text-help">
                                          {confirm.touched ? confirm.error : ''}
                                        </div>
                                    </div>
                                </div>

                                <div className="form-group ">
                                    <button type="button" className="btn btn-primary btn-lg btn-block login-button">Register</button>
                                </div>
                                <div className="login-register">
                                    <Link to={LOGIN_LINK}>{LOGIN_TEXT}</Link>
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

  if (!values.email) {
    errors.email = 'Enter your email address';
  }

  if (!values.username) {
    errors.username = 'Enter a username (can be the same as your email)';
  }

  if (!values.password) {
    errors.password = 'Enter a password';
  }

  if (!values.confirm) {
    errors.confirm = 'Confirm your password';
  }

  if (values.confirm !== values.password) {
    errors.confirm = 'Passwords do not match'
  }
  return errors;
}

export default reduxForm({
  form: 'RegisterForm',
  fields: ['email', 'username', 'password', 'confirm'],
  validate
}, (state) => {
  return state;
}, null)(Register);
