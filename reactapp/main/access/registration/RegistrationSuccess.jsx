import React from 'react';
import { connect } from 'react-redux';

export const RegistrationSuccess = React.createClass({
  render() {
    return (
        <div className="container">
          <div className="row main">
            <div className="col-md-4 col-md-offset-4">
              <h1 className="title">Registration Successful</h1>
              <p>Registration successful. Please check your email (including junk and spam folders) for the activation link.</p>
            </div>
          </div>
        </div>
    );
  }
});

export default connect()(RegistrationSuccess);
