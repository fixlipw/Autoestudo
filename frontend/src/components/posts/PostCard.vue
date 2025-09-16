<template>
  <v-card class="mb-4" outlined>
    <v-card-title class="text-h5 font-weight-bold">
      <router-link :to="postLink" class="text-decoration-none text-primary">
        {{ post.title }}
      </router-link>
    </v-card-title>

    <v-card-subtitle class="pb-2">
      <router-link :to="userLink" class="text-decoration-none">
        {{ post.author.username }}
      </router-link> &bull;
      <span>{{ formattedDate }}</span>
    </v-card-subtitle>

    <v-card-text>
      <p>{{ post.summary || truncatedContent }}</p>
    </v-card-text>

    <v-card-actions>
      <v-spacer></v-spacer>
      <v-btn :to="postLink" color="primary" variant="text">
        Ler Mais
        <v-icon right>mdi-arrow-right</v-icon>
      </v-btn>
    </v-card-actions>
  </v-card>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import type { Post } from '@/types/Post';

const props = defineProps<{
  post: Post;
}>();

const postLink = computed(() => `/posts/${props.post.id}`);
const userLink = computed(() => `/users/${props.post.author.id}`);

const formattedDate = computed(() => {
  const dateValue = props.post.created_at;
  if (!dateValue) return 'Data desconhecida';
  return new Date(dateValue).toLocaleDateString('pt-BR', {
    day: '2-digit',
    month: 'long',
    year: 'numeric',
  });
});

const truncatedContent = computed(() => {
  if (props.post.content.length > 150) {
    return props.post.content.substring(0, 150) + '...';
  }
  return props.post.content;
});
</script>

<style scoped>
.text-primary {
  color: #1976D2;
}
</style>