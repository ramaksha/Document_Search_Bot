import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useNavigate, Link } from 'react-router-dom';
import { getAuthHeader } from '../util/aurthheader';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

const Signup = () => {
  const [form, setForm] = useState({ username: '', email: '', password: '' });
  const [error, setError] = useState('');
  const navigate = useNavigate();

  // 🔐 Redirect to /home if a valid token already exists
  useEffect(() => {
    try {
      const headers = getAuthHeader();
      if (headers) {
        navigate('/home');
      }
    } catch (err) {
      console.warn('Token check failed — staying on signup page.');
    }
  }, [navigate]);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSignup = async (e) => {
    e.preventDefault();
    const { username, email, password } = form;

    if (!username || !email || !password) {
      setError('All fields are required');
      return;
    }

    try {
     var res= await axios.post(process.env.REACT_APP_LOCALHOST+'/public/signup', form);
     if(res){
        toast.success("User Registered Successfully");
      
     }
      
    } catch (err) {
      toast.error('Signup failed. Try a different username or email.');
    }
  };

  return (
    <div className="container signup-container">
      <div className="row justify-content-center">
        <div className="col-md-6 col-lg-4">
          <div className="card shadow p-4">
            <h3 className="text-center mb-4">Sign Up</h3>
            {error && <div className="alert alert-danger">{error}</div>}
            <form onSubmit={handleSignup}>
              <div className="mb-3">
                <input
                  type="text"
                  name="username"
                  className="form-control"
                  placeholder="Enter Username"
                  value={form.username}
                  onChange={handleChange}
                  required
                />
              </div>
              <div className="mb-3">
                <input
                  type="email"
                  name="email"
                  className="form-control"
                  placeholder="Enter Email"
                  value={form.email}
                  onChange={handleChange}
                  required
                />
              </div>
              <div className="mb-3">
                <input
                  type="password"
                  name="password"
                  className="form-control"
                  placeholder="Enter Password"
                  value={form.password}
                  onChange={handleChange}
                  required
                />
              </div>
              <button type="submit" className="btn btn-success w-100">
                Create Account
              </button>
            </form>
            <p className="mt-3 text-center">
              Already have an account? <Link to="/login">Login here</Link>
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Signup;