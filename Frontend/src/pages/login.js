import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useNavigate, Link } from 'react-router-dom';
import { getAuthHeader } from '../util/aurthheader';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

const Login = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate();

  // 🔍 Redirect if valid token exists
  useEffect(() => {
    const headers = getAuthHeader();
    if (headers) {
      navigate('/home');
    }
  }, [navigate]);

  // 🔐 Handle login form submit
  const handleLogin = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.post(
        `${process.env.REACT_APP_LOCALHOST}/public/login`,
        { username, password }
      );

      localStorage.setItem('token', response.data); // Adjust if response includes `token` object
      navigate('/home');
    } catch (err) {
      console.error('❌ Login failed:', err);
      toast.error('Invalid username or password.');
    }
  };

  return (
    <div className="container login-container">
      <div className="row justify-content-center">
        <div className="col-md-6 col-lg-4">
          <div className="card shadow p-4">
            <h3 className="text-center mb-4">Login</h3>
            {error && <div className="alert alert-danger">{error}</div>}

            <form onSubmit={handleLogin}>
              <div className="mb-3">
                <input
                  type="text"
                  className="form-control"
                  placeholder="Enter UserName"
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  required
                />
              </div>
              <div className="mb-3">
                <input
                  type="password"
                  className="form-control"
                  placeholder="Enter Password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  required
                />
              </div>
              <button type="submit" className="btn btn-primary w-100">
                Login
              </button>
            </form>

            <p className="mt-3 text-center">
              Don't have an account? <Link to="/signup">Sign up here</Link>
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Login;