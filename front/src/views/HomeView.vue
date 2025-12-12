<script setup lang="ts">
import { useCounterStore } from '../stores/counter'
const counter = useCounterStore()
</script>

<template>
    <h1>Home</h1>
    <p>Count: {{ counter.count }}</p>
    <button type="button" @click="counter.increment">Increment</button>
</template>