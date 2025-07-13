import logo from './logo.svg';
import './App.css';
import { Routes, Route, useLocation } from 'react-router-dom';
import Login from './pages/login';
import Signup from './pages/Signup';
import './css/Main.css';
import { jwtDecode } from 'jwt-decode'; 
import { ToastContainer, toast, Bounce } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import { useEffect } from 'react';
import SafeRoute from './component/SafeRoute';
import Home from './pages/home';
import Document from './component/Documents';
import Users from './component/Users';
import Header from './component/Header';
import Footer from './component/footer';
import { useState } from 'react';
function App() {
  const [username, setUsername] = useState('');
  const [role, setRole] = useState('');
  
  const location = useLocation();
  useEffect(() => {
    const fetchProfile = async () => {
      const token = localStorage.getItem('token');
      if (!token) {
        setUsername('');
        setRole('');
        return;
      }
  
      try {
        const decoded = jwtDecode(token);
        const res = await fetch(`${process.env.REACT_APP_LOCALHOST}/api/user/${decoded.sub}`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
  
        if (!res.ok) throw new Error('Unauthorized');
        const data = await res.json();
        setUsername(data.username);
        setRole(data.role?.toUpperCase());
      } catch (error) {
        console.error('Failed to load profile');
        setUsername('');
        setRole('');
      }
    };
  
    fetchProfile();
  }, [location.pathname]); // 🔁 Re-run on every route change
  return (
  
    <div className="App">
        <ToastContainer
        position="top-right"
        autoClose={5000}
        hideProgressBar={false}
        newestOnTop={false}
        closeOnClick
        rtl={false}
        pauseOnFocusLoss
        draggable
        pauseOnHover
        theme="light"
        transition={Bounce}
      />


    
      <Header username={username} role={role} />
   

      <Routes>
        
        <Route path='/users' element={<SafeRoute><Users/></SafeRoute>}/>
        <Route path='/document' element={<SafeRoute><Document/></SafeRoute>}/>
        <Route path="/Login" element={<Login />} />
        <Route path="/signup" element={<Signup />} />
        <Route path="/home"element={<SafeRoute><Home/> </SafeRoute>}/> 

      </Routes>
      <Footer/>
      
    
    
    </div>
  );
  
}

export default App;
