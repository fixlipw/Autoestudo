<template>
  <v-card flat border>
    <v-card-title>Editar Perfil</v-card-title>
    <v-card-text>
      <v-form @submit.prevent="submitUpdate">
        <v-text-field
            v-model="formData.first_name"
            label="Nome"
            variant="outlined"
            class="mb-3"
        ></v-text-field>
        <v-text-field
            v-model="formData.last_name"
            label="Sobrenome"
            variant="outlined"
            class="mb-3"
        ></v-text-field>
        <v-text-field
            v-model="formData.email"
            label="E-mail"
            type="email"
            variant="outlined"
            class="mb-3"
            :rules="[rules.email]"
        ></v-text-field>
        <v-textarea
            v-model="formData.bio"
            label="Biografia"
            variant="outlined"
            rows="3"
        ></v-textarea>
        <div class="d-flex justify-end mt-4">
          <v-btn
              type="submit"
              color="primary"
              :loading="uiStore.isLoading"
              :disabled="uiStore.isLoading"
              prepend-icon="mdi-content-save"
          >
            Salvar Alterações
          </v-btn>
        </div>
      </v-form>
    </v-card-text>
  </v-card>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';
import { useAuthStore } from '@/stores/auth';
import { useUiStore } from '@/stores/ui';
import * as userService from '@/services/userService';
import type { UserUpdatePayload } from '@/services/userService';

const authStore = useAuthStore();
const uiStore = useUiStore();

const formData = ref<Partial<UserUpdatePayload>>({});

const rules = {
  email: (v: string) => /.+@.+\..+/.test(v) || 'E-mail deve ser válido',
};

watch(() => authStore.user, (currentUser) => {
  if (currentUser) {
    formData.value = {
      first_name: currentUser.first_name,
      last_name: currentUser.last_name,
      email: currentUser.email,
      bio: currentUser.bio,
    };
  }
}, { immediate: true });

const submitUpdate = async () => {
  if (!authStore.user) return;
  uiStore.setLoading(true);
  try {
    const updatedUser = await userService.updateUser(authStore.user.id, formData.value);
    authStore.user = { ...authStore.user, ...updatedUser };
    localStorage.setItem('user', JSON.stringify(authStore.user));
    uiStore.showAlert({ message: 'Perfil atualizado com sucesso!', type: 'success' });
  } catch (error) {
    console.error('Erro ao atualizar perfil:', error);
    uiStore.showAlert({ message: 'Não foi possível atualizar o perfil.', type: 'error' });
  } finally {
    uiStore.setLoading(false);
  }
};
</script>