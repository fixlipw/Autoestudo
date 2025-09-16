<template>
  <v-snackbar
      v-model="alert.show"
      :color="alert.type"
      :timeout="4000"
      location="top right"
      multi-line
  >
    {{ alert.message }}
    <template v-slot:actions>
      <v-btn
          icon="mdi-close"
          variant="text"
          @click="hideAlert"
      ></v-btn>
    </template>
  </v-snackbar>
</template>

<script setup lang="ts">
import { useUiStore } from '@/stores/ui';
import { computed } from 'vue';

const uiStore = useUiStore();

const alert = computed(() => uiStore.alert);
const hideAlert = uiStore.hideAlert;
</script>
