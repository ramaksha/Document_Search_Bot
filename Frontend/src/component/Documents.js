import React, { useEffect, useState } from 'react';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import '../css/Document.css';

const Document = () => {
  const [files, setFiles] = useState([]);
  const token = localStorage.getItem("token");

  useEffect(() => {
    fetchFiles();
  }, []);

  const fetchFiles = async () => {
    try {
      const token = localStorage.getItem("token");
      const res = await fetch(process.env.REACT_APP_LOCALHOST + '/api/document', {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      if (!res.ok) throw new Error('Fetch failed');
      const data = await res.json();
      setFiles(data);
    } catch (err) {
      toast.error('Failed to load files');
    }
  };
  

  const deleteFile = async (filename) => {
    try {
      const token = localStorage.getItem("token");
      const res = await fetch(`${process.env.REACT_APP_LOCALHOST}/api/document/${encodeURIComponent(filename)}`, {
        method: 'DELETE',
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      if (res.ok) {
        toast.success(`Deleted: ${filename}`);
        fetchFiles();
      } else {
        toast.error(`Failed to delete: ${filename}`);
      }
    } catch {
      toast.error('Delete request failed');
    }
  };

  const uploadFile = async (e) => {
    const file = e.target.files[0];
    if (!file) return;
  
    const formData = new FormData();
    formData.append('file', file);
  
   try {
  const token = localStorage.getItem("token");
  const res = await fetch(process.env.REACT_APP_LOCALHOST + '/api/document', {
    method: 'POST',
    headers: {
      Authorization: `Bearer ${token}`,
    },
    body: formData,
  });

  if (res.ok) {
    toast.success(`Uploaded: ${file.name}`);
    fetchFiles();
  } else {
    const errorData = await res.json(); // 🔥 Parse the response JSON
    console.error("❌ Upload failed:", errorData.details); // ✅ Print error message
    toast.error(errorData.details || 'Upload failed');
  }

} catch (err) {
  console.error("🚨 Request error:", err.details);
  toast.error('Upload request failed');
}
  };
  return (
    <div className="document-container">
      <h2>📄 Uploaded Files</h2>

      <div className="file-list">
        {files.map((file) => (
          <div className="file-item" key={file}>
            <span>{file}</span>
            <button className="delete-btn" onClick={() => deleteFile(file)}>Delete</button>
          </div>
        ))}
      </div>

      <div className="upload-area">
        <input type="file" id="fileInput" onChange={uploadFile} />
      </div>

      <ToastContainer position="bottom-center" />
    </div>
  );
};

export default Document;