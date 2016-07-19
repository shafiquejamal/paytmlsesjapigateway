import React from 'react';
import * as Redux from 'react-redux';

export const PasswordChangeSuccessful = React.createClass({
  render() {
    return (
      <div className="container">
          <div className="row main">
              <div className="col-md-4 col-md-offset-4">
                <h1 className="Password Change Successful"></h1>
                <p>Password successfully changed.</p>
              </div>
          </div>
      </div>
    );
  }
});

export default Redux.connect((state) => { return state; })(PasswordChangeSuccessful)
