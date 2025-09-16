<template>
  <v-container>
    <h1 class="text-h4 mb-6">{{ pageTitle }}</h1>

    <div v-if="isLoading" class="text-center">
      <v-progress-circular indeterminate color="primary"></v-progress-circular>
    </div>

    <v-alert v-else-if="error" type="error" variant="tonal">
      {{ error }}
    </v-alert>

    <PostForm
        v-else
        :initial-data="postData"
        :loading="isSubmitting"
        @submit="handleFormSubmit"
        @cancel="goBack"
    />
  </v-container>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import * as postService from '@/services/postService';
import type { PostCreationPayload } from '@/services/postService';
import { useUiStore } from '@/stores/ui';
import PostForm from '@/components/posts/PostForm.vue';

const route = useRoute();
const router = useRouter();
const uiStore = useUiStore();

const postId = ref<number | null>(null);
const postData = ref<PostCreationPayload | undefined>(undefined);
const isLoading = ref(false);
const isSubmitting = ref(false);
const error = ref<string | null>(null);

const isEditMode = computed(() => !!postId.value);
const pageTitle = computed(() => isEditMode.value ? 'Editar Post' : 'Criar Novo Post');

onMounted(() => {
  const id = Number(route.params.id);
  if (!isNaN(id)) {
    postId.value = id;
    fetchPostData(id);
  }
});

const fetchPostData = async (id: number) => {
  isLoading.value = true;
  error.value = null;
  try {
    const post = await postService.getPostById(id);
    postData.value = { title: post.title, content: post.content };
  } catch (err) {
    console.error('Erro ao buscar dados do post:', err);
    error.value = 'Não foi possível carregar os dados do post para edição.';
  } finally {
    isLoading.value = false;
  }
};

const handleFormSubmit = async (payload: PostCreationPayload) => {
  isSubmitting.value = true;
  try {
    let savedPost;
    if (isEditMode.value && postId.value) {
      savedPost = await postService.updatePost(postId.value, payload);
      uiStore.showAlert({ message: 'Post atualizado com sucesso!', type: 'success' });
    } else {
      savedPost = await postService.createPost(payload);
      uiStore.showAlert({ message: 'Post criado com sucesso!', type: 'success' });
    }
    await router.push({name: 'post-detail', params: {id: savedPost.id}});
  } catch (err) {
    console.error('Erro ao salvar o post:', err);
    uiStore.showAlert({ message: 'Ocorreu um erro ao salvar o post.', type: 'error' });
  } finally {
    isSubmitting.value = false;
  }
};

const goBack = () => {
  router.back();
};
</script>