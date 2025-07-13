import React from 'react';
import { Navigate } from 'react-router-dom';
import { getAuthHeader } from '../util/aurthheader';
const SafeRoute = ({ children }) => {
  const authHeader = getAuthHeader();

  if (!authHeader) {
    return <Navigate to="/login" replace />;
  }

  return children;
};

export default SafeRoute;
