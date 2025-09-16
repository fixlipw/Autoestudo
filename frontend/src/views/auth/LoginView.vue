<template>
  <v-container>
    <v-row justify="center">
      <v-col cols="12" sm="8" md="6">
        <v-card class="elevation-12">
          <v-toolbar color="primary" dark flat>
            <v-toolbar-title>Login</v-toolbar-title>
          </v-toolbar>
          <v-card-text>
            <v-form @submit.prevent="submitLogin">
              <v-text-field
                  v-model="username"
                  label="Nome de usuário"
                  name="username"
                  prepend-icon="mdi-account"
                  type="text"
                  required
              ></v-text-field>

              <v-text-field
                  v-model="password"
                  label="Senha"
                  name="password"
                  prepend-icon="mdi-lock"
                  type="password"
                  required
              ></v-text-field>

              <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn
                    type="submit"
                    color="primary"
                    :loading="uiStore.isLoading"
                    :disabled="uiStore.isLoading"
                >
                  Entrar
                </v-btn>
              </v-card-actions>
              <div class="text-center mt-4">
                Não tem uma conta? <router-link to="/register">Crie uma aqui</router-link>
              </div>
            </v-form>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useAuthStore } from '@/stores/auth';
import { useUiStore } from '@/stores/ui';

const username = ref('');
const password = ref('');

const authStore = useAuthStore();
const uiStore = useUiStore();

const submitLogin = async () => {
  if (!username.value || !password.value) {
    uiStore.showAlert({ message: 'Por favor, preencha todos os campos.', type: 'warning' });
    return;
  }

  try {
    await authStore.handleLogin({
      username: username.value,
      password: password.value,
    });
  } catch (error) {
    console.error("Erro no componente de login:", error);
  }
};
</script>

<!--<template>-->
<!--  <v-container>-->
<!--    <v-row justify="center">-->
<!--      <v-col cols="12" sm="8" md="6">-->
<!--        <v-card class="elevation-12">-->
<!--          <v-toolbar color="primary" dark flat>-->
<!--            <v-toolbar-title>Login</v-toolbar-title>-->
<!--          </v-toolbar>-->
<!--          <v-card-text>-->
<!--            <v-form @submit.prevent="submitLogin">-->
<!--              <v-text-field-->
<!--                  v-model="form.username.value"-->
<!--                  label="Nome de usuário"-->
<!--                  name="username"-->
<!--                  prepend-icon="mdi-account"-->
<!--                  type="text"-->
<!--                  required-->
<!--                  :error="form.username.touched && !form.username.valid"-->
<!--                  :error-messages="form.username.errors"-->
<!--                  @blur="validateField('username')"-->
<!--              ></v-text-field>-->

<!--              <v-text-field-->
<!--                  v-model="form.password.value"-->
<!--                  label="Senha"-->
<!--                  name="password"-->
<!--                  prepend-icon="mdi-lock"-->
<!--                  type="password"-->
<!--                  required-->
<!--                  :error="form.password.touched && !form.password.valid"-->
<!--                  :error-messages="form.password.errors"-->
<!--                  @blur="validateField('password')"-->
<!--              ></v-text-field>-->

<!--              <v-card-actions>-->
<!--                <v-spacer></v-spacer>-->
<!--                <v-btn-->
<!--                    type="submit"-->
<!--                    color="primary"-->
<!--                    :loading="uiStore.isLoading"-->
<!--                    :disabled="uiStore.isLoading"-->
<!--                >-->
<!--                  Entrar-->
<!--                </v-btn>-->
<!--              </v-card-actions>-->
<!--              <div class="text-center mt-4">-->
<!--                Não tem uma conta? <router-link to="/register">Crie uma aqui</router-link>-->
<!--              </div>-->
<!--            </v-form>-->
<!--          </v-card-text>-->
<!--        </v-card>-->
<!--      </v-col>-->
<!--    </v-row>-->
<!--  </v-container>-->
<!--</template>-->

<!--<script setup lang="ts">-->
<!--import { useAuthStore } from '@/stores/auth';-->
<!--import { useUiStore } from '@/stores/ui';-->
<!--import { useValidation } from '@/composables/useValidation';-->

<!--const authStore = useAuthStore();-->
<!--const uiStore = useUiStore();-->

<!--const {-->
<!--  form,-->
<!--  addField,-->
<!--  validateField,-->
<!--  validateForm,-->
<!--  rules-->
<!--} = useValidation();-->

<!--addField('username', '', [rules.required(), rules.username()]);-->
<!--addField('password', '', [rules.required(), rules.password()]);-->

<!--const submitLogin = async () => {-->
<!--  if (!validateForm()) {-->
<!--    uiStore.showAlert({ message: 'Por favor, corrija os erros do formulário.', type: 'warning' });-->
<!--    return;-->
<!--  }-->
<!--  try {-->
<!--    await authStore.handleLogin({-->
<!--      username: form.username.value,-->
<!--      password: form.password.value,-->
<!--    });-->
<!--  } catch (error) {-->
<!--    console.error("Erro no componente de login:", error);-->
<!--  }-->
<!--};-->
<!--</script>-->