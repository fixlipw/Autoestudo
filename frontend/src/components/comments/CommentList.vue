<template>
  <div class="mt-8">
    <h2 class="text-h5 mb-4">Comentários</h2>

    <div v-if="uiStore.isLoading && comments.length === 0" class="text-center">
      <v-progress-circular indeterminate color="primary"></v-progress-circular>
    </div>

    <v-alert v-else-if="comments.length === 0" type="info" variant="tonal" class="mb-4">
      Nenhum comentário ainda. Seja o primeiro a comentar!
    </v-alert>

    <div v-else>
      <v-card v-for="comment in comments" :key="comment.id" class="mb-4" flat border>
        <v-card-text>
          <p class="body-1">{{ comment.content }}</p>
        </v-card-text>
        <v-card-subtitle class="d-flex justify-space-between align-center">
          <span><strong>{{ comment.author.fullName }}</strong></span>
          <span class="caption">
            {{
              new Date(comment.created_at).toLocaleString('pt-BR')
            }}
          </span>
        </v-card-subtitle>
      </v-card>

      <v-pagination
          v-if="pageInfo.totalPages > 1"
          v-model="pageInfo.currentPage"
          :length="pageInfo.totalPages"
          @update:model-value="loadPage"
          class="mt-4"
      ></v-pagination>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch } from 'vue';
import * as commentService from '@/services/commentService';
import { useUiStore } from '@/stores/ui';
import type { Comment } from '@/types/Comment';

const props = defineProps<{
  postId: number;
  refreshKey: number;
}>();

const uiStore = useUiStore();
const comments = ref<Comment[]>([]);

const pageInfo = reactive({
  currentPage: 1,
  totalPages: 0,
  totalElements: 0,
});

const fetchComments = async (page: number = 0) => {
  uiStore.setLoading(true);
  try {
    const response = await commentService.getCommentsByPost(props.postId, page);
    comments.value = response.content;
    pageInfo.totalPages = response.page.total_pages;
    pageInfo.totalElements = response.page.total_elements;
    pageInfo.currentPage = response.page.number + 1;
  } catch (error) {
    console.error('Erro ao buscar comentários:', error);
    uiStore.showAlert({ message: 'Não foi possível carregar os comentários.', type: 'error' });
  } finally {
    uiStore.setLoading(false);
  }
};

const loadPage = (page: number) => {
  fetchComments(page - 1);
};

onMounted(() => {
  fetchComments();
});

watch(() => props.refreshKey, () => {
  fetchComments();
});
</script>