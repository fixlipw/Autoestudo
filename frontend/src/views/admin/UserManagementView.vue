<template>
  <v-container>
    <h1 class="text-h4 mb-6">Gerenciamento de Usuários</h1>

   <v-row class="mb-6">
     <v-col
       v-for="status in statusList"
       :key="status"
       cols="12"
       sm="6"
       md="3"
     >
       <v-card flat border class="mb-2">
         <v-card-text>
           <div class="text-center">
             <div class="text-h4 text-primary font-weight-bold">
               {{ statusCounts[status] || 0 }}
             </div>
             <div class="text-body-2">{{ statusUsuario[status] }}</div>
           </div>
         </v-card-text>
       </v-card>
     </v-col>
   </v-row>

    <!-- Filtros por status -->
    <v-row class="mb-4">
      <v-col cols="12">
        <v-chip-group v-model="selectedStatus" row>
          <v-chip value="ALL">Todos</v-chip>
          <v-chip v-for="status in statusList" :key="status" :value="status">
            {{ statusUsuario[status] }}
          </v-chip>
        </v-chip-group>
      </v-col>
    </v-row>

<!--    &lt;!&ndash; Pedidos de aceite de cadastro &ndash;&gt;-->
<!--    <v-card class="mb-6" v-if="pendingUsers.length">-->
<!--      <v-card-title>Pedidos de Aceite de Cadastro</v-card-title>-->
<!--      <v-data-table-server-->
<!--        :headers="pendingHeaders"-->
<!--        :items="pendingUsers"-->
<!--        class="elevation-1"-->
<!--        item-value="id"-->
<!--        hide-default-footer-->
<!--      >-->
<!--        <template v-slot:item.actions="{ item }">-->
<!--          <v-btn color="success" icon="mdi-check" @click="approveUser(item)"></v-btn>-->
<!--          <v-btn color="error" icon="mdi-close" @click="rejectUser(item)"></v-btn>-->
<!--        </template>-->
<!--      </v-data-table-server>-->
<!--    </v-card>-->

    <!-- Tabela principal de usuários -->
    <v-card>
      <v-card-title>
        <v-text-field
            v-model="search"
            append-inner-icon="mdi-magnify"
            label="Buscar usuário (em breve)"
            single-line
            hide-details
            disabled
        ></v-text-field>
      </v-card-title>

      <v-data-table-server
          v-model:items-per-page="itemsPerPage"
          :headers="userHeaders"
          :items="users"
          :items-length="totalUsers"
          :loading="isLoading"
          :search="search"
          class="elevation-1"
          item-value="id"
          @update:options="loadUsers"
      >
        <template v-slot:item.role="{ item }">
          <v-chip :color="item.role === 'ADMIN' ? 'blue' : 'grey'" size="small">
            {{ item.role }}
          </v-chip>
        </template>

        <template v-slot:item.status="{ item }">
          <v-chip :color="userStatusColor(item.status)" size="small">
            {{ item.status }}
          </v-chip>
        </template>

        <template v-slot:item.actions="{ item }">
          <v-menu>
            <template v-slot:activator="{ props }">
              <v-btn icon="mdi-cog" variant="text" v-bind="props"></v-btn>
            </template>
            <v-list>
              <v-list-item @click="changeUserStatus(item, 'ACTIVE' as UserStatus)">
                <v-list-item-title>Ativar</v-list-item-title>
              </v-list-item>
              <v-list-item @click="changeUserStatus(item, 'SUSPENDED' as UserStatus)">
                <v-list-item-title>Suspender</v-list-item-title>
              </v-list-item>
              <v-list-item @click="changeUserStatus(item, 'INACTIVE' as UserStatus)">
                <v-list-item-title>Desativar</v-list-item-title>
              </v-list-item>
            </v-list>
          </v-menu>
          <v-btn icon="mdi-delete" variant="text" color="error" @click="deleteUser(item)"></v-btn>
        </template>
      </v-data-table-server>
    </v-card>
  </v-container>
</template>

<script setup lang="ts">
import {onMounted, ref, watch} from 'vue';
import * as userService from '@/services/userService';
import {useUiStore} from '@/stores/ui';
import type {User} from '@/types/User';
import {UserStatus} from '@/types/enums';

const uiStore = useUiStore();

const users = ref<User[]>([]);
const allUsers = ref<User[]>([]);
const isLoading = ref(true);
const totalUsers = ref(0);
const itemsPerPage = ref(10);
const search = ref('');
const selectedStatus = ref<'ALL' | UserStatus>('ALL');
const pendingUsers = ref<User[]>([]);
const statusCounts = ref<Record<UserStatus, number>>({ ACTIVE: 0, SUSPENDED: 0, INACTIVE: 0, PENDING: 0 });
const statusList = [ 'ACTIVE', 'SUSPENDED', 'INACTIVE', 'PENDING' ] as const;

const userHeaders = [
  { title: 'Nome Completo', key: 'fullName' },
  { title: 'Usuário', key: 'username' },
  { title: 'E-mail', key: 'email' },
  { title: 'Role', key: 'role', sortable: false },
  { title: 'Status', key: 'status', sortable: false },
  { title: 'Ações', key: 'actions', sortable: false, align: 'center' },
] as const;

const loadUsers = async ({ page, itemsPerPage: size }: { page: number; itemsPerPage: number }) => {
  isLoading.value = true;
  try {
    const response = await userService.getAllUsers();
    allUsers.value = response.content;

    const counts: Record<UserStatus, number> = { ACTIVE: 0, SUSPENDED: 0, INACTIVE: 0, PENDING: 0 };
    for (const u of allUsers.value) {
      counts[u.status] = (counts[u.status] || 0) + 1;
    }
    statusCounts.value = counts;

    let filtered = allUsers.value;
    if (selectedStatus.value !== 'ALL') {
      filtered = filtered.filter(u => u.status === selectedStatus.value);
    }

    pendingUsers.value = allUsers.value.filter(u => u.status === 'PENDING');

    totalUsers.value = filtered.length;
    const start = (page - 1) * size;
    const end = start + size;
    users.value = filtered.slice(start, end);
  } catch (error) {
    console.error("Erro ao carregar usuários:", error);
    uiStore.showAlert({ message: 'Não foi possível carregar os usuários.', type: 'error' });
  } finally {
    isLoading.value = false;
  }
};

onMounted(() => {
  loadUsers({ page: 1, itemsPerPage: itemsPerPage.value });
});

watch(selectedStatus, () => {
  loadUsers({ page: 1, itemsPerPage: itemsPerPage.value });
});

const changeUserStatus = async (user: User, status: UserStatus) => {
  const confirmed = await uiStore.showConfirmDialog({
    title: 'Mudar Status',
    message: `Tem certeza que deseja alterar o status de "${user.username}" para ${statusUsuario[status]}?`
  });
  if (!confirmed) return;

  try {
    await userService.updateUserStatus(user.id, status);
    uiStore.showAlert({ message: 'Status do usuário atualizado com sucesso!', type: 'success' });
    await loadUsers({page: 1, itemsPerPage: itemsPerPage.value});
  } catch (error) {
    console.error('Erro ao mudar status:', error);
    uiStore.showAlert({ message: 'Não foi possível alterar o status.', type: 'error' });
  }
};

const deleteUser = async (user: User) => {
  const confirmed = await uiStore.showConfirmDialog({
    title: 'Excluir Usuário',
    message: `Esta ação é irreversível. Tem certeza que deseja excluir o usuário "${user.username}"?`
  });
  if (!confirmed) return;

  try {
    await userService.deleteUser(user.id);
    uiStore.showAlert({ message: 'Usuário excluído com sucesso!', type: 'success' });
    await loadUsers({page: 1, itemsPerPage: itemsPerPage.value});
  } catch (error) {
    console.error('Erro ao excluir usuário:', error);
    uiStore.showAlert({ message: 'Não foi possível excluir o usuário.', type: 'error' });
  }
};

const userStatusColor = (status: UserStatus | 'ALL') => {
  switch (status) {
    case 'ACTIVE': return 'green';
    case 'SUSPENDED': return 'orange';
    case 'INACTIVE': return 'red';
    case 'PENDING': return 'grey';
    default: return 'primary';
  }
};

const statusUsuario: Record<UserStatus, string> = {
  ACTIVE: 'Ativo',
  SUSPENDED: 'Suspenso',
  INACTIVE: 'Inativo',
  PENDING: 'Pendente'
};
</script>
