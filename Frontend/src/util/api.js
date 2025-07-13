import axios from 'axios';

export const fetchChatResponse = async (message) => {
  try {
    const token = localStorage.getItem('token'); // Or sessionStorage, or your auth state
    const encodedMessage = encodeURIComponent(message);
    console.log(token);
    const response = await axios.get(
      `${process.env.REACT_APP_LOCALHOST}/api/qna/ask/${encodedMessage}`,
      {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      }
    );
    console.log(response.data);
    return response.data;
  } catch (error) {
    console.error('API Error:', error);
    return { reply: 'Sorry, something went wrong.' };
  }
};