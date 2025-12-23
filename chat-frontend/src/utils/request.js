import axios from 'axios'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截器
request.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
}, error => {
  return Promise.reject(error)
})

// 响应拦截器
request.interceptors.response.use(response => {
  return response.data
}, error => {
  if (error.response && error.response.status === 401) {
    localStorage.removeItem('token')
    window.location.href = '/login'
  }
  // 确保 error 对象有正确的结构供外层使用
  if (error.response && error.response.data) {
    return Promise.reject(error.response.data)
  }
  return Promise.reject(error)
})

export default request
