import React from 'react';

const ChatMessage = ({msg, chatMessageToFromClass}) => {
    return (
        <li className={`list-group-item chat-message ${chatMessageToFromClass}`}>
            <div className="message-text">{msg.message.text}</div>
            <div className="message-time">{msg.message.time}</div>
        </li>
    );

};

export default ChatMessage;