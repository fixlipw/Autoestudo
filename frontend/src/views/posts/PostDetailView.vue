<template>
  <v-container>
    <div v-if="uiStore.isLoading && !post" class="text-center py-10">
      <v-progress-circular indeterminate color="primary" size="64"></v-progress-circular>
    </div>

    <v-alert v-else-if="error" type="error" variant="tonal">
      {{ error }}
    </v-alert>

    <article v-else-if="post">
      <header class="mb-8">
        <h1 class="text-h3 font-weight-bold mb-2">{{ post.title }}</h1>
        <div class="text-subtitle-1">
          <span>Por {{ post.author.username }}</span> &bull;
          <span>{{ new Date(post.created_at).toLocaleDateString('pt-BR') }}</span>
        </div>
      </header>

      <section class="post-content" v-html="post.content"></section>

      <v-divider class="my-8"></v-divider>

      <section>
        <CommentForm :post-id="post.id" @comment-added="refreshComments" />
        <CommentList :post-id="post.id" :refresh-key="commentRefreshKey" />
      </section>
    </article>
  </v-container>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import * as postService from '@/services/postService';
import { useUiStore } from '@/stores/ui';
import type { Post } from '@/types/Post';
import CommentList from '@/components/comments/CommentList.vue';
import CommentForm from '@/components/comments/CommentForm.vue';

const route = useRoute();
const uiStore = useUiStore();

const post = ref<Post | null>(null);
const error = ref<string | null>(null);
const commentRefreshKey = ref(0);

const fetchPostDetails = async () => {
  uiStore.setLoading(true);
  error.value = null;
  try {
    const postId = Number(route.params.id);
    if (isNaN(postId)) {
      throw new Error("ID do post é inválido.");
    }
    post.value = await postService.getPostById(postId);
  } catch (err) {
    console.error('Erro ao buscar detalhes do post:', err);
    error.value = 'Não foi possível encontrar a publicação solicitada.';
  } finally {
    uiStore.setLoading(false);
  }
};

const refreshComments = () => {
  commentRefreshKey.value++;
};

onMounted(() => {
  fetchPostDetails();
});
</script>

<style>
.post-content {
  line-height: 1.7;
  font-size: 1.1rem;
}
.post-content h2,
.post-content h3 {
  margin-top: 1.5em;
  margin-bottom: 0.5em;
}
</style>