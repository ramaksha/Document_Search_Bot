import { jwtDecode } from 'jwt-decode';

export const getAuthHeader = () => {
  const token = localStorage.getItem('token');

  if (!token) return null;

  try {
    const decoded = jwtDecode(token);
    const isExpired = decoded.exp * 1000 < Date.now();

    if (isExpired) {
      localStorage.removeItem('token');
      return null;
    }

    return {
      Authorization: `Bearer ${token}`
    };
  } catch (error) {
    console.error('Invalid token:', error);
    localStorage.removeItem('token');
    return null;
  }
};
