<template>
  <v-container>
    <h1 class="text-h4 mb-6">Últimas Publicações</h1>

    <div v-if="uiStore.isLoading" class="text-center">
      <v-progress-circular indeterminate color="primary" size="64"></v-progress-circular>
    </div>

    <div v-else-if="posts.length > 0">
      <PostCard
          v-for="post in posts"
          :key="post.id"
          :post="post"
      />
    </div>

    <div v-else>
      <v-alert type="info" variant="tonal">
        Nenhuma publicação encontrada no momento.
      </v-alert>
    </div>

    <v-pagination
        v-if="pageInfo.totalPages > 1"
        v-model="pageInfo.currentPage"
        :length="pageInfo.totalPages"
        :total-visible="7"
        @update:model-value="loadPage"
        class="mt-8"
    ></v-pagination>
  </v-container>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue';
import * as postService from '@/services/postService';
import { useUiStore } from '@/stores/ui';
import type { Post } from '@/types/Post';
import PostCard from '@/components/posts/PostCard.vue';

const uiStore = useUiStore();
const posts = ref<Post[]>([]);

const pageInfo = reactive({
  currentPage: 1,
  totalPages: 0,
  totalElements: 0,
});

const fetchPosts = async (page: number = 0) => {
  uiStore.setLoading(true);
  try {
    const response = await postService.getPublishedPosts(page, 10);
    posts.value = response.content;
    pageInfo.totalPages = response.page.total_pages;
    pageInfo.totalElements = response.page.total_elements;
    pageInfo.currentPage = response.page.number + 1;
  } catch (error) {
    console.error("Erro ao buscar posts:", error);
    uiStore.showAlert({ message: 'Não foi possível carregar as publicações.', type: 'error' });
  } finally {
    uiStore.setLoading(false);
  }
};

const loadPage = (page: number) => {
  fetchPosts(page - 1);
};

onMounted(() => {
  fetchPosts();
});
</script>