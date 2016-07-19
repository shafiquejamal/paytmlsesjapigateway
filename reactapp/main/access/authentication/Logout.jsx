import React from 'react';
import * as Redux from 'react-redux';
import { startLoggingOutUser } from './authenticationActionGenerators';

export const Logout = React.createClass({
  componentWillMount() {
    const { dispatch } = this.props;
    dispatch(startLoggingOutUser());
  },
  render() {
    return (
      <div className="container">
          <div className="row main">
              <div className="col-md-4 col-md-offset-4">
                <h1 className="title">Logged Out</h1>
                <p>You are now logged out.</p>
              </div>
          </div>
      </div>
    );
  }
});

export default Redux.connect((state) => { return state; })(Logout)
