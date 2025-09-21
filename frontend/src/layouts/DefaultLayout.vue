<template>
  <v-app>
    <NavBar/>
    <v-main>
      <v-container class="pa-0" fluid>
        <v-breadcrumbs
            v-if="breadcrumbs.length > 1"
            :items="breadcrumbs"
            class="px-4 py-2"
        >
          <template v-slot:item="{ item }">
            <v-breadcrumbs-item
                :disabled="item.disabled"
                :to="item.to"
            >
              {{ item.title }}
            </v-breadcrumbs-item>
          </template>
        </v-breadcrumbs>
        <router-view/>
        <v-fab
            class="ma-4"
            icon="mdi-theme-light-dark"
            location="bottom right"
            size="40"
            style="position: fixed; bottom: 45px; right: 0; z-index: 1000;"
            variant="outlined"
            @click="$vuetify.theme.change(
                $vuetify.theme.global.name === 'myCustomLightTheme' ? 'myCustomDarkTheme' : 'myCustomLightTheme')"
        ></v-fab>
      </v-container>
    </v-main>
    <Footer/>
  </v-app>
</template>

<script lang="ts" setup>
import {computed} from 'vue';
import {useRoute} from 'vue-router';
import NavBar from '@/components/layout/TheNavbar.vue';
import Footer from '@/components/layout/TheFooter.vue';

const route = useRoute();

const breadcrumbs = computed(() => {
  const crumbs = [{title: 'Início', to: '/', disabled: false}];
  if (route.name === 'home') return [];
  if (route.path.includes('/admin')) {
    crumbs.push({title: 'Administração', to: '/admin/users', disabled: false});
  }
  if (route.name === 'profile') {
    crumbs.push({title: 'Meu Perfil', to: '/profile', disabled: true});
  }
  if (route.path.includes('/posts/new')) {
    crumbs.push({title: 'Novo Post', to: '/posts/new', disabled: true});
  }
  if (route.path.includes('/posts/') && route.params.id) {
    crumbs.push({title: 'Post', to: route.path, disabled: true});
  }
  return crumbs;
});
</script>
