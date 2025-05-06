import axios from 'axios';

const API_URL = 'http://localhost:8080/auth/';

// Configurare axios pentru a gestiona CORS
axios.defaults.withCredentials = true;

class AuthService {
  async login(email, password) {
    try {
      const response = await axios.post(API_URL + 'login', {
        email,
        password
      });
      
      if (response.data.token) {
        localStorage.setItem('user', JSON.stringify(response.data));
        return response.data;
      } else {
        throw new Error(response.data.message || 'Login failed');
      }
    } catch (error) {
      throw error.response?.data || error.message;
    }
  }

  logout() {
    localStorage.removeItem('user');
  }

  async register(username, email, password) {
    try {
      console.log('Attempting to register with:', { username, email }); // pentru debug
      
      const response = await axios.post(API_URL + 'register', {
        username,
        email,
        password
      });
      
      console.log('Register response:', response.data); // pentru debug
      
      return response.data;
    } catch (error) {
      console.error('Registration error:', error.response || error); // pentru debug
      
      if (error.response) {
        // Eroare de la server
        throw {
          message: error.response.data.message || 'Registration failed',
          statusCode: error.response.status
        };
      } else if (error.request) {
        // Eroare de rețea
        throw {
          message: 'Network error - please check your connection',
          statusCode: 0
        };
      } else {
        // Altă eroare
        throw {
          message: error.message,
          statusCode: 0
        };
      }
    }
  }

  getCurrentUser() {
    return JSON.parse(localStorage.getItem('user'));
  }

  getToken() {
    const user = this.getCurrentUser();
    return user?.token;
  }
}

export default new AuthService(); 