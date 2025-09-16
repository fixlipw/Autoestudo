<template>
  <v-container>
    <div class="d-flex align-center mb-6">
      <v-icon class="mr-3" size="large" color="primary">mdi-account-circle</v-icon>
      <h1 class="text-h4">Meu Perfil</h1>
    </div>

    <v-row>
      <v-col cols="12" md="6" lg="5">
        <UserUpdateForm />

        <div class="mt-6">
          <PasswordChangeForm />
        </div>
      </v-col>

      <v-col cols="12" md="6" lg="7">
        <v-card flat border class="mb-6">
          <v-card-title class="d-flex align-center">
            <v-icon class="mr-2">mdi-chart-line</v-icon>
            Suas Estatísticas
          </v-card-title>
          <v-card-text>
            <v-row>
              <v-col cols="3">
                <div class="text-center">
                  <div class="text-h4 text-primary font-weight-bold">
                    {{ userStats.totalPosts }}
                  </div>
                  <div class="text-body-2">Posts Totais</div>
                </div>
              </v-col>
              <v-col cols="3">
                <div class="text-center">
                  <div class="text-h4 text-success font-weight-bold">
                    {{ userStats.publishedPosts }}
                  </div>
                  <div class="text-body-2">Publicados</div>
                </div>
              </v-col>
              <v-col cols="3">
                <div class="text-center">
                  <div class="text-h4 text-info font-weight-bold">
                    {{ userStats.comments }}
                  </div>
                  <div class="text-body-2">Comentários</div>
                </div>
              </v-col>
              <v-col cols="3">
                <div class="text-center">
                  <div class="text-h4 text-warning font-weight-bold">
                    {{ authStore.user?.status ? statusUser[authStore.user.status] : 'Desconhecido' }}
                  </div>
                  <div class="text-body-2">Status da Conta</div>
                </div>
              </v-col>
            </v-row>
          </v-card-text>
        </v-card>

        <UserPostsList />

        <v-card flat border class="mt-6">
          <v-card-title class="d-flex align-center">
            <v-icon class="mr-2">mdi-cog</v-icon>
            Configurações da Conta
          </v-card-title>
          <v-card-text>
            <v-list>
              <v-list-item>
                <template v-slot:prepend>
                  <v-icon>mdi-shield-check</v-icon>
                </template>
                <v-list-item-title>Conta verificada</v-list-item-title>
                <v-list-item-subtitle>
                  Sua conta foi verificada em {{ formattedCreationDate }}
                </v-list-item-subtitle>
              </v-list-item>

              <v-list-item>
                <template v-slot:prepend>
                  <v-icon>mdi-crown</v-icon>
                </template>
                <v-list-item-title>Nível da conta</v-list-item-title>
                <v-list-item-subtitle>
                  {{ authStore.user?.role === 'ADMIN' ? 'Administrador' : 'Usuário' }}
                </v-list-item-subtitle>
              </v-list-item>
            </v-list>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useAuthStore } from '@/stores/auth';
import UserUpdateForm from '@/components/user/UserUpdateForm.vue';
import UserPostsList from '@/components/user/UserPostsList.vue';
import PasswordChangeForm from '@/components/user/ChangePasswordForm.vue';
import {UserStatus} from "@/types/enums";

const authStore = useAuthStore();

const formattedCreationDate = computed(() => {
  if (!authStore.user?.created_at) return 'Data desconhecida';
  return new Date(authStore.user.created_at).toLocaleDateString('pt-BR', {
    day: '2-digit',
    month: 'long',
    year: 'numeric',
  });
});

const userStats = computed(() => ({
  totalPosts: authStore.user?.postsCount || 0,
  publishedPosts: authStore.user?.publishedPostsCount || 0,
  comments: authStore.user?.commentsCount || 0,
}));

const statusUser: Record<UserStatus, string> = {
  ACTIVE: 'Ativo',
  INACTIVE: 'Inativo',
  SUSPENDED: 'Suspenso',
  PENDING: 'Pendente',
};
</script>
