<template>
  <v-app-bar app color="primary" dark elevation="2">
    <v-app-bar-nav-icon
        v-if="$vuetify.display.mobile"
        @click="drawer = !drawer"
    ></v-app-bar-nav-icon>

    <v-toolbar-title class="font-weight-bold">
      <router-link
          class="text-decoration-none text-white d-flex align-center"
          to="/"
      >
        <img alt="BlogueVi Logo" class="mr-2" src="/bloguevi.svg"
             style="height:32px;width:32px;vertical-align:middle;"/>
        <span>BlogueVi</span>
      </router-link>
    </v-toolbar-title>

    <v-spacer></v-spacer>

    <template v-if="!$vuetify.display.mobile">
      <template v-if="!authStore.isAuthenticated">
        <v-btn class="mr-2" to="/login" variant="text">
          <v-icon left>mdi-login</v-icon>
          Login
        </v-btn>
        <v-btn to="/register" variant="outlined">
          <v-icon left>mdi-account-plus</v-icon>
          Registrar-se
        </v-btn>
      </template>
      <template v-else>
        <v-btn class="mr-2" to="/posts/new" variant="text">
          <v-icon left>mdi-plus-box</v-icon>
          <span class="hidden-sm-and-down">Novo Post</span>
        </v-btn>
        <v-btn
            v-if="authStore.isAdmin"
            class="mr-2"
            to="/admin/users"
            variant="text"
        >
          <v-icon left>mdi-shield-account</v-icon>
          <span class="hidden-sm-and-down">Administração</span>
        </v-btn>
        <v-menu offset-y>
          <template v-slot:activator="{ props }">
            <v-btn class="text-none" v-bind="props" variant="text">
              <v-avatar class="mr-2" size="32">
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
      v-if="$vuetify.display.mobile"
      v-model="drawer"
      location="left"
      temporary
  >
    <v-list>
      <v-list-item
          v-if="!authStore.isAuthenticated"
          prepend-icon="mdi-login"
          to="/login"
      >
        <v-list-item-title>Login</v-list-item-title>
      </v-list-item>
      <v-list-item
          v-if="!authStore.isAuthenticated"
          prepend-icon="mdi-account-plus"
          to="/register"
      >
        <v-list-item-title>Registrar-se</v-list-item-title>
      </v-list-item>
      <template v-if="authStore.isAuthenticated">
        <v-list-item prepend-icon="mdi-home" to="/">
          <v-list-item-title>Início</v-list-item-title>
        </v-list-item>
        <v-list-item prepend-icon="mdi-plus-box" to="/posts/new">
          <v-list-item-title>Novo Post</v-list-item-title>
        </v-list-item>
        <v-list-item prepend-icon="mdi-account" to="/profile">
          <v-list-item-title>Meu Perfil</v-list-item-title>
        </v-list-item>
        <v-list-item
            v-if="authStore.isAdmin"
            prepend-icon="mdi-shield-account"
            to="/admin/users"
        >
          <v-list-item-title>Administração</v-list-item-title>
        </v-list-item>
        <v-divider class="my-2"></v-divider>
        <v-list-item prepend-icon="mdi-logout" @click="handleLogout">
          <v-list-item-title>Logout</v-list-item-title>
        </v-list-item>
      </template>
    </v-list>
  </v-navigation-drawer>
</template>

<script lang="ts" setup>
import {ref, watch} from 'vue';
import {useRoute} from 'vue-router';
import {useAuthStore} from '@/stores/auth';

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
