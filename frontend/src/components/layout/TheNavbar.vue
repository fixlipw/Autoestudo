<template>
  <v-app-bar app color="primary" dark elevation="2">
    <v-app-bar-nav-icon
        v-if="$vuetify.display.mobile"
        @click="drawer = !drawer"
    ></v-app-bar-nav-icon>

    <v-toolbar-title class="font-weight-bold">
      <router-link
          to="/"
          class="text-decoration-none text-white d-flex align-center"
      >
        <v-icon class="mr-2">mdi-post</v-icon>
        <span class="hidden-sm-and-down">BlogueVi</span>
      </router-link>
    </v-toolbar-title>

    <v-spacer></v-spacer>

    <template v-if="!$vuetify.display.mobile">
      <template v-if="!authStore.isAuthenticated">
        <v-btn variant="text" to="/login" class="mr-2">
          <v-icon left>mdi-login</v-icon>
          Login
        </v-btn>
        <v-btn variant="outlined" to="/register">
          <v-icon left>mdi-account-plus</v-icon>
          Registrar-se
        </v-btn>
      </template>
      <template v-else>
        <v-btn variant="text" to="/posts/new" class="mr-2">
          <v-icon left>mdi-plus-box</v-icon>
          <span class="hidden-sm-and-down">Novo Post</span>
        </v-btn>
        <v-btn
            v-if="authStore.isAdmin"
            variant="text"
            to="/admin/users"
            class="mr-2"
        >
          <v-icon left>mdi-shield-account</v-icon>
          <span class="hidden-sm-and-down">Admin</span>
        </v-btn>
        <v-menu offset-y>
          <template v-slot:activator="{ props }">
            <v-btn variant="text" v-bind="props" class="text-none">
              <v-avatar size="32" class="mr-2">
                <v-icon>mdi-account-circle</v-icon>
              </v-avatar>
              <span class="hidden-sm-and-down">
                {{ authStore.user?.fullName || authStore.user?.username }}
              </span>
              <v-icon right>mdi-menu-down</v-icon>
            </v-btn>
          </template>
          <v-list>
            <v-list-item to="/profile">
              <template v-slot:prepend>
                <v-icon>mdi-account</v-icon>
              </template>
              <v-list-item-title>Meu Perfil</v-list-item-title>
            </v-list-item>
            <v-divider></v-divider>
            <v-list-item @click="handleLogout">
              <template v-slot:prepend>
                <v-icon>mdi-logout</v-icon>
              </template>
              <v-list-item-title>Logout</v-list-item-title>
            </v-list-item>
          </v-list>
        </v-menu>
      </template>
    </template>
  </v-app-bar>

  <v-navigation-drawer
      v-model="drawer"
      temporary
      location="left"
      v-if="$vuetify.display.mobile"
  >
    <v-list>
      <v-list-item
          v-if="!authStore.isAuthenticated"
          to="/login"
          prepend-icon="mdi-login"
      >
        <v-list-item-title>Login</v-list-item-title>
      </v-list-item>
      <v-list-item
          v-if="!authStore.isAuthenticated"
          to="/register"
          prepend-icon="mdi-account-plus"
      >
        <v-list-item-title>Registrar-se</v-list-item-title>
      </v-list-item>
      <template v-if="authStore.isAuthenticated">
        <v-list-item to="/" prepend-icon="mdi-home">
          <v-list-item-title>Início</v-list-item-title>
        </v-list-item>
        <v-list-item to="/posts/new" prepend-icon="mdi-plus-box">
          <v-list-item-title>Novo Post</v-list-item-title>
        </v-list-item>
        <v-list-item to="/profile" prepend-icon="mdi-account">
          <v-list-item-title>Meu Perfil</v-list-item-title>
        </v-list-item>
        <v-list-item
            v-if="authStore.isAdmin"
            to="/admin/users"
            prepend-icon="mdi-shield-account"
        >
          <v-list-item-title>Administração</v-list-item-title>
        </v-list-item>
        <v-divider class="my-2"></v-divider>
        <v-list-item @click="handleLogout" prepend-icon="mdi-logout">
          <v-list-item-title>Logout</v-list-item-title>
        </v-list-item>
      </template>
    </v-list>
  </v-navigation-drawer>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';
import { useRoute } from 'vue-router';
import { useAuthStore } from '@/stores/auth';

const route = useRoute();
const authStore = useAuthStore();
const drawer = ref(false);

const handleLogout = () => {
  authStore.handleLogout();
  drawer.value = false;
};

watch(() => route.path, () => {
  drawer.value = false;
});
</script>

<style scoped>
.v-app-bar .v-toolbar-title a {
  text-decoration: none !important;
}
.hidden-sm-and-down {
  display: inline;
}
@media (max-width: 960px) {
  .hidden-sm-and-down {
    display: none !important;
  }
}
</style>
