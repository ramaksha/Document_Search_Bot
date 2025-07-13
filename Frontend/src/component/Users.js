import React, { useEffect, useState } from 'react';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import '../css/Users.css';

const Users = () => {
  const [users, setUsers] = useState([]);

  useEffect(() => {
    fetchUsers();
  }, []);

  const fetchUsers = async () => {
    const token = localStorage.getItem('token');
    if (!token) return toast.error('No auth token found');

    try {
      const res = await fetch(process.env.REACT_APP_LOCALHOST + '/api/user', {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (!res.ok) throw new Error('Failed to fetch users');
      const data = await res.json();
      setUsers(data);
    } catch (err) {
      toast.error('Error fetching users');
    }
  };

  const toggleRole = async (username) => {
    const token = localStorage.getItem('token');
    if (!token) return toast.error('No auth token found');

    try {
      const res = await fetch(
       `${process.env.REACT_APP_LOCALHOST}/api/user/${encodeURIComponent(username)}`,
        {
          method: 'PUT',
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      if (!res.ok) throw new Error('Failed to update role');
      const updatedUser = await res.json();

      toast.success(`Role updated to ${updatedUser.role}`);
      setUsers((prev) =>
        prev.map((user) =>
          user.username === updatedUser.username ? { ...user, role: updatedUser.role } : user
        )
      );
    } catch (err) {
      toast.error('Server error during role toggle');
    }
  };

  return (
    <div className="users-container">
      <h2>👥 User Management</h2>
      <div className="user-list">
        {users.map((user) => (
          <div key={user.userId} className="user-item">
            <span>
              {user.username} — <strong>{user.role?.toUpperCase() || 'USER'}</strong>
            </span>
            <button onClick={() => toggleRole(user.username)}>Toggle Role</button>
          </div>
        ))}
      </div>
      <ToastContainer position="bottom-center" />
    </div>
  );
};

export default Users;