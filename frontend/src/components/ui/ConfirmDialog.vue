<template>
  <v-dialog v-model="confirmDialog.show" max-width="500px" persistent>
    <v-card>
      <v-card-title class="text-h5">{{ confirmDialog.title }}</v-card-title>
      <v-card-text>{{ confirmDialog.message }}</v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn color="blue-darken-1" variant="text" @click="cancel">
          Cancelar
        </v-btn>
        <v-btn color="red-darken-1" variant="text" @click="confirm">
          Confirmar
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { useUiStore } from '@/stores/ui';
import { computed } from 'vue';

const uiStore = useUiStore();

const confirmDialog = computed(() => uiStore.confirmDialog);

const cancel = () => {
  uiStore.resolveConfirmDialog(false);
};

const confirm = () => {
  uiStore.resolveConfirmDialog(true);
};
</script>
