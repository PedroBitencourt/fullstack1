import axios from "axios";

export const http = axios.create({
  baseURL: "http://localhost:8080",
  headers: {
    "Content-Type": "application/json",
  },
  timeout: 15000,
});

/**
 * (Opcional) Interceptor de erro
 * Centraliza log/tratamento
 */
http.interceptors.response.use(
  (response) => response,
  (error) => {
    console.error("HTTP error:", error?.response?.data || error.message);
    return Promise.reject(error);
  }
);
