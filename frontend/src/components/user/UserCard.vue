<template>
  <v-card class="mt-6">
    <v-card-title class="text-h5 font-weight-bold">
      <span> {{ user.fullName || user.username }} </span>
    </v-card-title>

    <v-card-subtitle class="pb-2">
      <span> {{ user.bio }} </span>
    </v-card-subtitle>

    <v-card-text>
      <p><strong>Usuário:</strong> {{ user.username }}</p>
      <p><strong>E-mail:</strong> {{ user.email }}</p>
      <p><strong>Criado em:</strong> {{ formatedDate }}</p>
    </v-card-text>
  </v-card>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import type { User } from '@/types/User';

const props = defineProps<{
  user: User;
}>();

const formatedDate = computed(() => {
  const dateValue = props.user.created_at;
  if (!dateValue) return 'Data desconhecida';
  return new Date(dateValue).toLocaleDateString('pt-BR', {
    day: '2-digit',
    month: 'long',
    year: 'numeric',
  });
});

</script>

<style scoped>

</style>