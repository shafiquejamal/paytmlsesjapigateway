import React from 'react';
import { connect } from 'react-redux';
import { hashHistory } from 'react-router';

import { ACTIVATION_FAILED_LINK } from '../../../routes';
import { startActivatingUser } from './RegistrationActionGenerators';


export const Activate = React.createClass({
  componentWillMount() {
    const { dispatch } = this.props;
    const { email, code } = this.props.location.query;
    dispatch(startActivatingUser(email, code)).then(
      (response) => {
        if (response.data.error === "this user is blocked") {
          hashHistory.push(ACTIVATION_FAILED_LINK);
        }
      },
      (response) => {
        hashHistory.push(ACTIVATION_FAILED_LINK);
      }
    );
  },
  render() {
    return (
        <div className="container">
          <div className="row main">
            <div className="col-md-4 col-md-offset-4">
              <h1 className="title">Activation Successful</h1>
              <p>Please log in to your account to continue.</p>
            </div>
          </div>
        </div>
    );
  }
});

export default connect()(Activate);
