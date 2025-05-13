import axios from 'axios';

const API_URL = 'http://localhost:8080/auth/';

// Configure axios defaults
axios.defaults.withCredentials = true;
axios.defaults.headers.common['Content-Type'] = 'application/json';

class AuthService {
  async login(email, password) {
    try {
      console.log('Attempting login with:', { email });
      const response = await axios.post(API_URL + 'login', {
        email,
        password
      });
      
      console.log('Login response:', response.data);
      
      if (response.data.statusCode === 200 && response.data.token) {
        // Store the complete response data
        const userData = {
          token: response.data.token,
          refreshToken: response.data.refreshToken,
          expirationTime: response.data.expirationTime,
          user: response.data.user,
          message: response.data.message
        };
        localStorage.setItem('user', JSON.stringify(userData));
        return userData;
      } else {
        throw new Error(response.data.message || 'Login failed');
      }
    } catch (error) {
      console.error('Login error:', {
        message: error.message,
        response: error.response?.data,
        status: error.response?.status
      });
      
      // Handle specific error cases
      if (error.response?.status === 401) {
        throw new Error('Invalid email or password');
      } else if (error.response?.status === 404) {
        throw new Error('Account not found');
      } else if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      } else {
        throw new Error('Login failed. Please try again.');
      }
    }
  }

  logout() {
    localStorage.removeItem('user');
  }

  async register(username, email, password) {
    try {
      console.log('Sending register request:', { username, email, password });
      const response = await axios.post(API_URL + 'register', {
        username,
        email,
        password
      });
      console.log('Register response:', response.data);
      return response.data;
    } catch (error) {
      console.error('Register error:', {
          message: error.message,
        response: error.response?.data,
        status: error.response?.status
      });
      throw error;
    }
  }

  getCurrentUser() {
    const user = localStorage.getItem('user');
    if (user) {
      return JSON.parse(user);
  }
    return null;
  }

  isAuthenticated() {
    const user = this.getCurrentUser();
    return user !== null;
  }
}

export default new AuthService(); 