import axios from 'axios'

export const http = axios.create({
  baseURL: '/api',
})

http.interceptors.request.use((config) => {
  const token = localStorage.getItem('auth.token');
  if (token) {
    config.headers = config.headers ?? {};
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});