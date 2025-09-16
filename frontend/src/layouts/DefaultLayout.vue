<template>
  <v-app>
    <NavBar />
    <v-main>
      <v-container fluid class="pa-0">
        <v-breadcrumbs
            v-if="breadcrumbs.length > 1"
            :items="breadcrumbs"
            class="px-4 py-2 bg-grey-lighten-5"
        >
          <template v-slot:item="{ item }">
            <v-breadcrumbs-item
                :to="item.to"
                :disabled="item.disabled"
            >
              {{ item.title }}
            </v-breadcrumbs-item>
          </template>
        </v-breadcrumbs>
        <router-view />
      </v-container>
    </v-main>
    <Footer />
  </v-app>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useRoute } from 'vue-router';
import NavBar from '@/components/layout/TheNavbar.vue';
import Footer from '@/components/layout/TheFooter.vue';

const route = useRoute();

const breadcrumbs = computed(() => {
  const crumbs = [{ title: 'Início', to: '/', disabled: false }];
  if (route.name === 'home') return [];
  if (route.path.includes('/admin')) {
    crumbs.push({ title: 'Administração', to: '/admin/users', disabled: false });
  }
  if (route.name === 'profile') {
    crumbs.push({ title: 'Meu Perfil', to: '/profile', disabled: true });
  }
  if (route.path.includes('/posts/new')) {
    crumbs.push({ title: 'Novo Post', to: '/posts/new', disabled: true });
  }
  if (route.path.includes('/posts/') && route.params.id) {
    crumbs.push({ title: 'Post', to: route.path, disabled: true });
  }
  return crumbs;
});
</script>
