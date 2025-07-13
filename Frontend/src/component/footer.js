import React from 'react';
import '../css/style.css';

const Footer = () => (
  <footer className="app-footer">
    <p>&copy; {new Date().getFullYear()} Sonata Software. All rights reserved.</p>
  </footer>
);

export default Footer;