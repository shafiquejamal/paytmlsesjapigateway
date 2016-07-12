import React, { Component } from 'react';
import { Link } from 'react-router';

export default class Template extends Component {
    render() {
        return (
            <div>
                <Link to="/example1">Go to Example1</Link>
                ---
                <Link to="/example2">Go to Example2</Link>
                {this.props.children}
            </div>
        );
    }
}
