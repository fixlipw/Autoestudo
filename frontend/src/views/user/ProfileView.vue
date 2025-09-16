<template>
  <v-container>
    <div v-if="uiStore.isLoading && !userData" class="text-center">
      <v-progress-circular indeterminate color="primary" size="64"></v-progress-circular>
    </div>

    <div v-else-if="userData">
      <user-card :user="userData"></user-card>

      <div class="mt-8">
        <h2 class="text-h5 mb-4">Publicações de {{ userData.fullName || userData.username }}</h2>

        <div v-if="uiStore.isLoading && posts.length === 0" class="text-center">
          <v-progress-circular indeterminate color="primary"></v-progress-circular>
        </div>

        <div v-else-if="posts.length === 0" class="mt-4">
          <v-alert type="info" variant="tonal">
            Nenhuma publicação encontrada para este usuário.
          </v-alert>
        </div>

        <div v-else>
          <post-card
              v-for="post in posts"
              :key="post.id"
              :post="post"
              class="mb-4"
          />

          <v-pagination
              v-if="pageInfo.totalPages > 1"
              v-model="pageInfo.currentPage"
              :length="pageInfo.totalPages"
              :total-visible="7"
              @update:model-value="loadPage"
              class="mt-8"
          />
        </div>
      </div>
    </div>

    <v-alert v-else type="error" variant="tonal" class="mt-4">
      Usuário não encontrado.
    </v-alert>
  </v-container>
</template>

<script setup lang="ts">
import * as userService from "@/services/userService";
import * as postService from "@/services/postService";
import type { User } from "@/types/User";
import type { Post } from "@/types/Post";
import { useUiStore } from "@/stores/ui";
import { onMounted, reactive, ref, watch } from "vue";
import UserCard from "@/components/user/UserCard.vue";
import PostCard from "@/components/posts/PostCard.vue";
import { useRoute, useRouter } from "vue-router";

const route = useRoute();
const router = useRouter();
const uiStore = useUiStore();

const userData = ref<User | null>(null);
const posts = ref<Post[]>([]);
const isLoadingPosts = ref(false);

const pageInfo = reactive({
  currentPage: 1,
  totalPages: 0,
  totalElements: 0,
});

const fetchUser = async (userId: number) => {
  if (isNaN(userId)) {
    console.error("ID do usuário é inválido:", userId);
    uiStore.showAlert({
      message: 'ID do usuário é inválido.',
      type: 'error'
    });
    await router.push({name: 'home'});
    return;
  }

  uiStore.setLoading(true);
  try {
    userData.value = await userService.getUserById(userId);
  } catch (error) {
    console.error("Erro ao buscar usuário:", error);
    uiStore.showAlert({
      message: 'Não foi possível carregar os dados do usuário.',
      type: 'error'
    });
    await router.push({name: 'home'});
  } finally {
    uiStore.setLoading(false);
  }
};

const fetchPosts = async (page: number = 0) => {
  if (!userData.value) {
    console.warn("fetchPosts chamado sem userData carregado");
    return;
  }
  isLoadingPosts.value = true;

  try {
    const response = await postService.getPostsByAuthor(userData.value.id, page, 10);
    posts.value = response.content;
    pageInfo.totalPages = response.page.total_pages;
    pageInfo.totalElements = response.page.total_elements;
    pageInfo.currentPage = response.page.number + 1;

  } catch (error) {
    console.error("Erro ao buscar posts:", error);
    uiStore.showAlert({
      message: 'Não foi possível carregar as publicações do usuário.',
      type: 'error'
    });
  } finally {
    isLoadingPosts.value = false;
  }
};

const loadPage = (page: number) => {
  fetchPosts(page - 1);
};

// Watch para mudanças na rota
watch(
    () => route.params.id,
    async (newId) => {
      if (newId) {
        const userId = Number(newId);
        userData.value = null;
        posts.value = [];
        pageInfo.currentPage = 1;
        pageInfo.totalPages = 0;
        pageInfo.totalElements = 0;

        await fetchUser(userId);
      }
    },
    { immediate: true }
);

onMounted(async () => {
  const userId = Number(route.params.id);
  await fetchUser(userId);
  if (userData.value) {
    await fetchPosts();
  }
});
</script>

<style scoped>
.text-center {
  text-align: center;
}
</style>