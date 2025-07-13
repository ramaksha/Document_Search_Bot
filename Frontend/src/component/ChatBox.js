import React, { useState,useEffect } from 'react';
import { fetchChatResponse } from '../util/api';
import '../css/ChatBox.css'; // Add styles here

const ChatBox = () => {
    const [messages, setMessages] = useState([]);
    const [input, setInput] = useState('');
    const [loading, setLoading] = useState(false);
  
    const sendMessage = async () => {
      if (!input.trim()) return;
  
      const userMessage = { sender: 'user', text: input };
      setMessages((prev) => [...prev, userMessage]);
      setInput('');
      setLoading(true);
  
      try {
        const response = await fetchChatResponse(input);
        console.log(response);
        const botReply = response  || '⚠️ No reply received.';
        console.log(botReply)
        const botMessage = { sender: 'bot', text: botReply };
        console.log(botMessage)
        setMessages((prev) => [...prev, botMessage]);
      } catch (error) {
        console.error('Chat error:', error);
        setMessages((prev) => [
          ...prev,
          { sender: 'bot', text: '❌ Failed to get response from server.' },
        ]);
      } finally {
        setLoading(false);
      }
    };
  
    useEffect(() => {
      const chatDiv = document.querySelector('.chat-messages');
      if (chatDiv) {
        chatDiv.scrollTop = chatDiv.scrollHeight;
      }
    }, [messages]);
  
    return (
      <div className="chat-container">
        <div className="chat-messages">
        <pre>{messages.map((msg, idx) => (
           <div key={idx} className={`message ${msg.sender}`}>
           {msg.text}
         </div>
         
          ))}</pre>
          {loading && <div className="message bot">Typing...</div>}
        </div>
        <div className="chat-input">
          <input
            type="text"
            value={input}
            onChange={(e) => setInput(e.target.value)}
            placeholder="Type your message..."
            onKeyDown={(e) => e.key === 'Enter' && sendMessage()}
          />
          <button onClick={sendMessage} disabled={loading}>
            Send
          </button>
        </div>
      </div>
    );
  };
  
  export default ChatBox;
  