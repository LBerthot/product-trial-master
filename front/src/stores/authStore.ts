import { defineStore } from 'pinia';
import { ref } from 'vue';
import {login as loginApi} from '../api/authApi';
import { jwtDecode } from 'jwt-decode';
import { getAccount } from '../api/accountApi';

const AUTH_TOKEN_STORAGE_KEY = 'auth.token';

export const useAuthStore = defineStore('auth', () => {
    const token = ref<string | null>(null);
    const userId = ref<number | null>(null);
    const email = ref<string | null>(null);
    const isAuthenticated = () => Boolean(token.value);
    const isAdmin = () => { return email.value === 'admin@admin.com' };
    
    async function login(email: string, password: string) {
        const data = await loginApi(email, password);
        token.value = data.token;
        const decoded = jwtDecode(token.value);
        userId.value = Number(decoded.sub); //TODO: the userID in backend is uth.getName() 
        localStorage.setItem(AUTH_TOKEN_STORAGE_KEY, data.token);

        await getEmailFromToken();
    }
    
    function logout() {
        token.value = null;
        userId.value = null;
        email.value = null;
        localStorage.removeItem(AUTH_TOKEN_STORAGE_KEY);
    }
    async function restoreFromLocalStorage() {
        const storedToken = localStorage.getItem(AUTH_TOKEN_STORAGE_KEY);
        if (!storedToken) {
            return;
        }
        token.value = storedToken;
        
        try {
            const decoded = jwtDecode(storedToken);
            userId.value = Number(decoded.sub);
            await getEmailFromToken();
        } catch {
            logout();
        }
    }
    
    async function getEmailFromToken() {
        const account = await getAccount(userId.value!);
        email.value = account.email;
    }

    return {token, isAuthenticated, isAdmin, login, logout, restoreFromLocalStorage}
});