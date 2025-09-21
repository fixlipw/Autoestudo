<template>
  <v-container>
    <v-row justify="center">
      <v-col cols="12" sm="8" md="6">
        <v-card class="elevation-12">
          <v-toolbar color="primary" dark flat>
            <v-toolbar-title>Criar Conta</v-toolbar-title>
          </v-toolbar>
          <v-card-text>
            <v-form @submit.prevent="submitRegister">
              <v-text-field
                  v-model="form.first_name.value"
                  label="Nome"
                  prepend-icon="mdi-account"
                  type="text"
                  :error="form.first_name.touched && !form.first_name.valid"
                  :error-messages="form.first_name.errors"
                  @blur="validateField('first_name')"
              ></v-text-field>

              <v-text-field
                  v-model="form.last_name.value"
                  label="Sobrenome"
                  prepend-icon="mdi-account-outline"
                  type="text"
                  :error="form.last_name.touched && !form.last_name.valid"
                  :error-messages="form.last_name.errors"
                  @blur="validateField('last_name')"
              ></v-text-field>

              <v-text-field
                  v-model="form.username.value"
                  label="Nome de usuário"
                  prepend-icon="mdi-account-circle"
                  type="text"
                  required
                  :error="form.username.touched && !form.username.valid"
                  :error-messages="form.username.errors"
                  @blur="validateField('username')"
              ></v-text-field>

              <v-text-field
                  v-model="form.email.value"
                  label="E-mail"
                  prepend-icon="mdi-email"
                  type="email"
                  required
                  :error="form.email.touched && !form.email.valid"
                  :error-messages="form.email.errors"
                  @blur="validateField('email')"
              ></v-text-field>

              <v-text-field
                  v-model="form.password.value"
                  label="Senha"
                  name="password"
                  prepend-icon="mdi-lock"
                  :type="showPassword ? 'text' : 'password'"
                  required
                  :error="form.password.touched && !form.password.valid"
                  :error-messages="form.password.errors"
                  @blur="validateField('password')"
                  :append-inner-icon="showPassword ? 'mdi-eye-off' : 'mdi-eye'"
                  @click:append-inner="showPassword = !showPassword"
              />

              <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn
                    type="submit"
                    color="primary"
                    :loading="uiStore.isLoading"
                    :disabled="uiStore.isLoading"
                >
                  Registrar
                </v-btn>
              </v-card-actions>
              <div class="text-center mt-4">
                Já tem uma conta? <router-link to="/login">Faça login</router-link>
              </div>
            </v-form>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup lang="ts">
import { useAuthStore } from '@/stores/auth';
import { useUiStore } from '@/stores/ui';
import type { RegisterPayload } from '@/types/auth';
import { useValidation } from '@/composables/useValidation';
import {ref} from "vue";

const showPassword = ref(false);

const authStore = useAuthStore();
const uiStore = useUiStore();

const {
  form,
  addField,
  validateField,
  validateForm,
  rules
} = useValidation();

addField('first_name', '', [rules.minLength(2, 'Nome deve ter pelo menos 2 caracteres')]);
addField('last_name', '', [rules.minLength(2, 'Sobrenome deve ter pelo menos 2 caracteres')]);
addField('username', '', [rules.required(), rules.username()]);
addField('email', '', [rules.required(), rules.email()]);
addField('password', '', [rules.required(), rules.password()]);

const submitRegister = async () => {
  if (!validateForm()) {
    uiStore.showAlert({ message: 'Por favor, corrija os erros do formulário.', type: 'warning' });
    return;
  }
  try {
    await authStore.handleRegister({
      username: form.username.value,
      email: form.email.value,
      password: form.password.value,
      first_name: form.first_name.value,
      last_name: form.last_name.value,
    } as RegisterPayload);
    uiStore.showAlert({
      message: 'Registro realizado com sucesso! Aguarde a ativação da sua conta por um administrador.',
      type: 'success',
    });
  } catch (error) {
    console.error("Erro no componente de registro:", error);
  }
};
</script>