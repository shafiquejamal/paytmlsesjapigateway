import React from 'react';
import { Link } from 'react-router';
import * as Redux from 'react-redux';

import { REGISTER_LINK, REGISTER_TEXT, LOGIN_LINK, LOGIN_TEXT, LOGOUT_LINK, LOGOUT_TEXT, MANAGE_ACCOUNT_LINK, MANAGE_ACCOUNT_TEXT } from '../routes';

export const Template = React.createClass({
    renderLinks() {
      const { authenticated } = this.props.auth;
      if (!authenticated) {
        return (
          [
            <li className="nav-item" key="REGISTER_TEXT">
                <Link to={REGISTER_LINK} className="nav-link">{REGISTER_TEXT}</Link>
            </li>,
            <li className="nav-item" key="LOGIN_TEXT">
                <Link to={LOGIN_LINK} className="nav-link">{LOGIN_TEXT}</Link>
            </li>
          ]
        );
      } else {
        return (
          [
            <li className="nav-item" key="MANAGE_ACCOUNT_TEXT">
                <Link to={MANAGE_ACCOUNT_LINK} className="nav-link">{MANAGE_ACCOUNT_TEXT}</Link>
            </li>,
            <li className="nav-item" key="LOGOUT_TEXT">
                <Link to={LOGOUT_LINK} className="nav-link">{LOGOUT_TEXT}</Link>
            </li>
          ]
        );
      }
    },
    render() {
        return (
            <div>
                <nav className="navbar navbar-static-top navbar-dark bg-inverse">
                    <a className="navbar-brand" href="#">Project name</a>
                    <ul className="nav navbar-nav">
                      <li className="nav-item">
                          <Link className="nav-link" to="#">Home</Link>
                      </li>
                      {this.renderLinks()}
                    </ul>

                </nav>

                {this.props.children}

                <div className="container">
                    <hr/>

                        <footer className="col-md-12">
                            <p>&copy; Company 2015</p>
                        </footer>
                </div>

            </div>
        );
    }
});

export default Redux.connect((state) => {return state;})(Template);
