<template>
  <v-card flat border>
    <v-card-title>Gerenciar Meus Posts</v-card-title>
    <v-card-text>
      <v-data-table-server
          v-model:items-per-page="itemsPerPage"
          :headers="headers"
          :items="posts"
          :items-length="totalPosts"
          :loading="isLoading"
          class="elevation-1"
          item-value="id"
          @update:options="loadPosts"
      >

        <template v-slot:item.author="{ item }">
          {{ item.author.username }}
        </template>

        <template v-slot:item.status="{ item }">
          <v-chip :color="postStatusColor(item.status)" size="small">
            {{ item.status }}
          </v-chip>
        </template>

        <template v-slot:item.title="{ item }">
          <router-link :to="`/posts/${item.id}`" class="text--primary">
            {{ item.title }}
          </router-link>
        </template>

        <template v-slot:item.created_at="{ item }">
          {{ new Date(item.created_at).toLocaleString('pt-BR') }}
        </template>

        <template v-slot:item.updated_at="{ item }">
          {{ new Date(item.updated_at).toLocaleString('pt-BR') }}
        </template>

        <template v-slot:item.actions="{ item }">
          <v-menu>
            <template v-slot:activator="{ props }">
              <v-btn icon="mdi-cog" variant="text" v-bind="props"></v-btn>
            </template>
            <v-list>
              <v-list-item @click="changePostStatus(item, 'PUBLISHED' as PostStatus)">
                <v-list-item-title>Publicar</v-list-item-title>
              </v-list-item>
              <v-list-item @click="changePostStatus(item, 'DRAFT' as PostStatus)">
                <v-list-item-title>Rascunho</v-list-item-title>
              </v-list-item>
              <v-list-item @click="changePostStatus(item, 'ARCHIVED' as PostStatus)">
                <v-list-item-title>Arquivar</v-list-item-title>
              </v-list-item>
            </v-list>
          </v-menu>
          <v-btn icon="mdi-pencil" variant="text" :to="`/posts/edit/${item.id}`"></v-btn>
          <v-btn icon="mdi-delete" variant="text" color="error" @click="deletePost(item)"></v-btn>
        </template>
      </v-data-table-server>

    </v-card-text>
  </v-card>
</template>

<script setup lang="ts">
import {ref} from 'vue';
import {useAuthStore} from '@/stores/auth';
import {useUiStore} from '@/stores/ui';
import * as postService from '@/services/postService';
import type {Post} from '@/types/Post';
import type {PostStatus} from '@/types/enums';

const authStore = useAuthStore();
const uiStore = useUiStore();

const posts = ref<Post[]>([]);
const isLoading = ref(true);
const totalPosts = ref(0);
const itemsPerPage = ref(5);

const headers = [
  {title: 'ID', key: 'id', align: 'start'},
  {title: 'Título', key: 'title'},
  {title: 'Data de Criação', key: 'created_at'},
  {title: 'Última Atualização', key: 'updated_at'},
  {title: 'Status', key: 'status', sortable: false},
  {title: 'Ações', key: 'actions', sortable: false, align: 'center'},
] as const;

const loadPosts = async ({page, itemsPerPage: size}: { page: number; itemsPerPage: number }) => {
  if (!authStore.user) return;
  isLoading.value = true;
  try {
    const response = await postService.getPostsByAuthor(authStore.user.id, page - 1, size);
    posts.value = response.content;
    totalPosts.value = response.page.total_elements;
  } catch (error) {
    uiStore.showAlert({message: 'Não foi possível carregar seus posts.', type: 'error'});
  } finally {
    isLoading.value = false;
  }
};

const deletePost = async (post: Post) => {
  const confirmed = await uiStore.showConfirmDialog({
    title: 'Excluir Post',
    message: `Esta ação é irreversível. Tem certeza que deseja excluir o post "${post.title}"?`
  });
  if (!confirmed) return;

  try {
    await postService.deletePost(post.id);
    uiStore.showAlert({ message: 'Post excluído com sucesso!', type: 'success' });
    await loadPosts({page: 1, itemsPerPage: itemsPerPage.value});
  } catch (error) {
    console.error('Erro ao excluir post:', error);
    uiStore.showAlert({ message: 'Não foi possível excluir o post.', type: 'error' });
  }
};

const changePostStatus = async (post: Post, status: PostStatus) => {
  const confirmed = await uiStore.showConfirmDialog({
    title: 'Mudar Status do Post',
    message: `Tem certeza que deseja alterar o status do post "${post.title}" para ${statusPost[status]}?`
  });
  if (!confirmed) return;

  try {
    await postService.updatePostStatus(post.id, status);
    uiStore.showAlert({ message: 'Status do post atualizado com sucesso!', type: 'success' });
    await loadPosts({page: 1, itemsPerPage: itemsPerPage.value});
  } catch (error) {
    console.error('Erro ao mudar status do post:', error);
    uiStore.showAlert({ message: 'Não foi possível alterar o status do post.', type: 'error' });
  }
};

const postStatusColor = (status: PostStatus) => {
  switch (status) {
    case 'PUBLISHED': return 'green';
    case 'DRAFT': return 'orange';
    case 'ARCHIVED': return 'red';
    default: return 'default';
  }
};

const statusPost: Record<PostStatus, string> = {
  PUBLISHED: 'Publicado',
  DRAFT: 'Rascunho',
  ARCHIVED: 'Arquivado'
};
</script>