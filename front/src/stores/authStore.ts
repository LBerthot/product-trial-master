import { defineStore } from 'pinia'
import { ref } from 'vue'
import {login as loginApi} from '../api/authApi'

const AUTH_TOKEN_STORAGE_KEY = 'auth.token'

export const useAuthStore = defineStore('auth', () => {
    const token = ref<string | null>(null);
    const isAuthenticated = () => Boolean(token.value);
    
    async function login(email: string, password: string) {
        const data = await loginApi(email, password);
        token.value = data.token;
        localStorage.setItem(AUTH_TOKEN_STORAGE_KEY, data.token);
    }
    
    function logout() {
        token.value = null;
        localStorage.removeItem(AUTH_TOKEN_STORAGE_KEY);
    }
    function restoreFromLocalStorage() {
        token.value = localStorage.getItem(AUTH_TOKEN_STORAGE_KEY);
    }

    return {token, isAuthenticated, login, logout, restoreFromLocalStorage}
});