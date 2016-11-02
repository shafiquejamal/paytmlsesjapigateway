import React from 'react';
import { Link } from 'react-router';
import * as Redux from 'react-redux';

import { REGISTER_LINK, REGISTER_TEXT, LOGIN_LINK, LOGIN_TEXT, LOGOUT_LINK, LOGOUT_TEXT, MANAGE_ACCOUNT_LINK, MANAGE_ACCOUNT_TEXT, CHAT_LINK, CHAT_TEXT } from '../routes';

export const Template = React.createClass({
    renderLinks() {
      const { authenticated, username } = this.props.auth;
      if (!authenticated) {
        return (
          <div><ul className="nav pull-right">
                <li key="REGISTER_TEXT">
                    <Link to={REGISTER_LINK} className="nav-link">{REGISTER_TEXT}</Link>
                </li>
                <li key="LOGIN_TEXT">
                    <Link to={LOGIN_LINK} className="nav-link">{LOGIN_TEXT}</Link>
                </li>
          </ul></div>
        );
      } else {
        return (
            <div>
                <ul className="nav">
                    <li key="CHAT_TEXT">
                      <Link to={CHAT_LINK} className="nav-link">{CHAT_TEXT}</Link>
                    </li>
                </ul>
                <ul className="nav pull-right">
                    <li key="MANAGE_ACCOUNT_TEXT">
                      <Link to={MANAGE_ACCOUNT_LINK} className="nav-link">{username}</Link>
                    </li>
                    <li key="LOGOUT_TEXT">
                      <Link to={LOGOUT_LINK} className="nav-link">{LOGOUT_TEXT}</Link>
                    </li>
                </ul>
            </div>
        );
      }
    },
    render() {
        return (
            <div>
                <nav role="navigation" className="navbar navbar-static-top navbar-dark bg-inverse">
                    <a className="navbar-brand" href="#">Project name</a>
                    {this.renderLinks()}
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
