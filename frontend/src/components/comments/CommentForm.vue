<template>
  <v-card class="mt-6" flat border>
    <v-card-text>
      <h3 class="text-h6 mb-4">Deixe um comentário</h3>
      <v-form @submit.prevent="submitComment">
        <v-textarea
            v-model="commentContent"
            label="Seu comentário"
            rows="3"
            variant="outlined"
            :disabled="!authStore.isAuthenticated"
            :placeholder="authStore.isAuthenticated ? '' : 'Você precisa estar logado para comentar.'"
            required
        ></v-textarea>
        <v-btn
            type="submit"
            color="primary"
            class="mt-2"
            :loading="uiStore.isLoading"
            :disabled="!authStore.isAuthenticated || uiStore.isLoading"
        >
          Enviar Comentário
        </v-btn>
      </v-form>
    </v-card-text>
  </v-card>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useAuthStore } from '@/stores/auth';
import { useUiStore } from '@/stores/ui';
import * as commentService from '@/services/commentService';

const props = defineProps<{
  postId: number;
}>();

const emit = defineEmits(['comment-added']);

const commentContent = ref('');
const authStore = useAuthStore();
const uiStore = useUiStore();

const submitComment = async () => {
  if (!commentContent.value.trim()) {
    uiStore.showAlert({ message: 'O comentário não pode estar vazio.', type: 'warning' });
    return;
  }
  uiStore.setLoading(true);
  try {
    await commentService.createComment(props.postId, {
      content: commentContent.value,
    });
    commentContent.value = '';
    uiStore.showAlert({ message: 'Comentário adicionado com sucesso!', type: 'success' });
    emit('comment-added'); // Notifica o componente pai para recarregar a lista
  } catch (error) {
    console.error('Erro ao adicionar comentário:', error);
    uiStore.showAlert({ message: 'Não foi possível enviar o comentário.', type: 'error' });
  } finally {
    uiStore.setLoading(false);
  }
};
</script>