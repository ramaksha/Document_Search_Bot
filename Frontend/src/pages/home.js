import React, { useEffect, useState } from 'react';
import ChatBox from '../component/ChatBox';
import { Link } from 'react-router-dom';
import { jwtDecode } from 'jwt-decode';
import Header from '../component/Header';

const Home = () => {
   
  
  
  return (
    
    <div className="home-container">
        
    
      <main style={{ padding: '10px' }}>
     
        <ChatBox />
      </main>
    </div>
  );
};

export default Home;