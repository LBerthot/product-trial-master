<script setup lang="ts">
    const props = withDefaults(
        defineProps<{
            modelValue: boolean;
            activeLabel: string;
            inactiveLabel: string;
            disabled?: boolean;
        }>(),
        {
            disabled: false,
        }
    );

    const emit = defineEmits<{
        (e: 'update:modelValue', value: boolean): void;
        (e: 'toggle', value: boolean): void;
    }>();

    function onToggle() {
        if (props.disabled) return;
        const next = !props.modelValue;
        emit('update:modelValue', next);
        emit('toggle', next);
    }
</script>

<template>
    <button
        class="toggle"
        type="button"
        :disabled="props.disabled"
        :aria-pressed="props.modelValue"
        @click="onToggle"
    >
        <span class="option" :class="{ selected: props.modelValue }">
            {{ props.modelValue ? props.activeLabel : props.inactiveLabel }}
        </span>
    </button>
</template>

<style scoped>
    .toggle {
        background-color: var(--ac-accent);
    }
</style>