<template>
  <v-card flat border>
    <v-card-title class="d-flex align-center">
      <v-icon class="mr-2">mdi-lock-reset</v-icon>
      Alterar Senha
    </v-card-title>
    <v-card-text>
      <v-form ref="form" @submit.prevent="submitPasswordChange">
        <v-text-field
            v-model="formData.currentPassword"
            label="Senha Atual"
            type="password"
            variant="outlined"
            class="mb-3"
            :rules="[rules.required]"
            prepend-inner-icon="mdi-lock"
            :error-messages="errors.currentPassword"
        ></v-text-field>

        <v-text-field
            v-model="formData.newPassword"
            label="Nova Senha"
            type="password"
            variant="outlined"
            class="mb-3"
            :rules="[rules.required, rules.minLength, rules.password]"
            prepend-inner-icon="mdi-lock-plus"
            :error-messages="errors.newPassword"
        ></v-text-field>

        <v-text-field
            v-model="formData.confirmPassword"
            label="Confirmar Nova Senha"
            type="password"
            variant="outlined"
            class="mb-3"
            :rules="[rules.required, rules.passwordMatch]"
            prepend-inner-icon="mdi-lock-check"
            :error-messages="errors.confirmPassword"
        ></v-text-field>

        <v-card
            v-if="formData.newPassword"
            variant="outlined"
            class="mb-4"
        >
          <v-card-text class="pb-2">
            <div class="text-body-2 mb-2">Força da senha:</div>
            <v-progress-linear
                :model-value="passwordStrength.score * 25"
                :color="passwordStrength.color"
                height="8"
                rounded
                class="mb-2"
            ></v-progress-linear>
            <div class="text-caption">{{ passwordStrength.text }}</div>

            <div class="mt-3">
              <div class="text-caption mb-1">Requisitos:</div>
              <v-chip
                  v-for="requirement in passwordRequirements"
                  :key="requirement.text"
                  size="small"
                  :color="requirement.met ? 'success' : 'grey'"
                  variant="outlined"
                  class="mr-1 mb-1"
              >
                <v-icon :icon="requirement.met ? 'mdi-check' : 'mdi-close'" size="small" class="mr-1"></v-icon>
                {{ requirement.text }}
              </v-chip>
            </div>
          </v-card-text>
        </v-card>

        <div class="d-flex justify-end">
          <v-btn
              type="submit"
              color="primary"
              :loading="isLoading"
              :disabled="isLoading || !isFormValid"
              prepend-icon="mdi-content-save"
          >
            Alterar Senha
          </v-btn>
        </div>
      </v-form>
    </v-card-text>
  </v-card>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue';
import { useAuthStore } from '@/stores/auth';
import { useUiStore } from '@/stores/ui';
import * as userService from '@/services/userService';

const authStore = useAuthStore();
const uiStore = useUiStore();

const form = ref();
const isLoading = ref(false);

const formData = reactive({
  currentPassword: '',
  newPassword: '',
  confirmPassword: '',
});

const errors = reactive<{
  currentPassword: string[];
  newPassword: string[];
  confirmPassword: string[];
}>({
  currentPassword: [],
  newPassword: [],
  confirmPassword: [],
});

const rules = {
  required: (v: string) => !!v || 'Este campo é obrigatório',
  minLength: (v: string) => v.length >= 8 || 'A senha deve ter no mínimo 8 caracteres',
  password: (v: string) => {
    const pattern = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]/;
    return pattern.test(v) || 'A senha deve conter ao menos: 1 minúscula, 1 maiúscula, 1 número e 1 caractere especial';
  },
  passwordMatch: (v: string) => v === formData.newPassword || 'As senhas não coincidem',
};

const passwordStrength = computed(() => {
  const password = formData.newPassword;
  if (!password) return { score: 0, text: '', color: 'grey' };

  let score = 0;

  if (password.length >= 8) score++;
  if (password.length >= 12) score++;

  if (/[a-z]/.test(password)) score++;
  if (/[A-Z]/.test(password)) score++;
  if (/\d/.test(password)) score++;
  if (/[@$!%*?&]/.test(password)) score++;

  if (score >= 4) score++;

  const levels = [
    { text: 'Muito Fraca', color: 'red' },
    { text: 'Fraca', color: 'orange' },
    { text: 'Regular', color: 'yellow' },
    { text: 'Boa', color: 'light-green' },
    { text: 'Forte', color: 'green' },
  ];

  const level = Math.min(Math.floor(score * levels.length / 7), levels.length - 1);

  return {
    score: Math.max(score, 1),
    text: levels[level].text,
    color: levels[level].color,
  };
});

const passwordRequirements = computed(() => {
  const password = formData.newPassword;
  return [
    { text: 'Min. 8 caracteres', met: password.length >= 8 },
    { text: 'Letra minúscula', met: /[a-z]/.test(password) },
    { text: 'Letra maiúscula', met: /[A-Z]/.test(password) },
    { text: 'Número', met: /\d/.test(password) },
    { text: 'Caractere especial', met: /[@$!%*?&]/.test(password) },
  ];
});

const isFormValid = computed(() => {
  return formData.currentPassword &&
      formData.newPassword &&
      formData.confirmPassword &&
      formData.newPassword === formData.confirmPassword &&
      passwordStrength.value.score >= 3;
});

const clearErrors = () => {
  errors.currentPassword = [];
  errors.newPassword = [];
  errors.confirmPassword = [];
};

const submitPasswordChange = async () => {
  if (!authStore.user) return;

  const { valid } = await form.value.validate();
  if (!valid) return;

  isLoading.value = true;
  clearErrors();

  try {
    await userService.updateUserPassword(
        authStore.user.id,
        [formData.currentPassword, formData.newPassword]
    );

    formData.currentPassword = '';
    formData.newPassword = '';
    formData.confirmPassword = '';

    form.value.reset();

    uiStore.showAlert({
      message: 'Senha alterada com sucesso!',
      type: 'success'
    });
  } catch (error: any) {
    console.error('Erro ao alterar senha:', error);

    if (error.response?.status === 400) {
      errors.currentPassword = ['Senha atual incorreta'];
    } else {
      uiStore.showAlert({
        message: 'Não foi possível alterar a senha. Tente novamente.',
        type: 'error'
      });
    }
  } finally {
    isLoading.value = false;
  }
};
</script>