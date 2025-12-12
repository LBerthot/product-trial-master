<script setup lang="ts">
    import { ref } from 'vue'
    import { http } from '../api/http'
    
    const email = ref('admin@admin.com')
    const password = ref('')
    const loading = ref(false)
    const error = ref<string | null>(null)
    const token = ref<string | null>(null)

    async function onSubmit() {
        loading.value = true
        error.value = null
        token.value = null

        try {
            const response = await http.post('/token', {
                email: email.value,
                password: password.value
            })
            token.value = response.data.token
        } catch (e: any) {
            const status = e?.response?.status
            const message = e?.response?.data?.message
            error.value = `Erreur API (${status ?? 'unknown'}): ${message ?? e?.message ?? 'unknown'}`
        } finally {
            loading.value = false
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
    <p v-if="token" class="success">{{ token }}</p>
</template>

<style scoped>
    .error {
        color: red;
    }
    .success {
        color: green;
    }
</style>