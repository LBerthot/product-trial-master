<script setup lang="ts">
    import { ref } from 'vue';
    import { useAuthStore } from '../stores/authStore';
    import { useRouter } from 'vue-router';
    
    const email = ref('admin@admin.com');
    const password = ref('');
    const loading = ref(false);
    const error = ref<string | null>(null);
    const router = useRouter();
    const authStore = useAuthStore();

    async function onSubmit() {
        loading.value = true;
        error.value = null;

        try {
            await authStore.login(email.value, password.value);
            router.push('/products');
        } catch (e: any) {
            const status = e?.response?.status;
            if (status === 401) {
                error.value = "Identifiants invalides";
            } else {
                const message = e?.response?.data?.message;
                error.value = `Erreur API (${status ?? "unknown"}): ${message ?? e?.message ?? "unknown"}`;
            }
        } finally {
            loading.value = false;
        }
    }
</script>

<template>
    <h1>Login</h1>
    <form @submit.prevent="onSubmit">
        <label>
            Email
            <input type="email" v-model="email" placeholder="Email" />
        </label>
        <label>
            Password
            <input type="password" v-model="password" placeholder="Password" />
        </label>
        <button type="submit" :disabled="loading">
             {{ loading ? 'Connexion...' : 'Se connecter' }}
        </button>
    </form>
    <p v-if="error" class="error">{{ error }}</p>
</template>

<style scoped>
    .error {
        color: red;
    }
    .success {
        color: green;
    }
</style>