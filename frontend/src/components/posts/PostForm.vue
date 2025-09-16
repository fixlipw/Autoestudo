<template>
  <v-form @submit.prevent="submit">
    <v-text-field
        v-model="editablePost.title"
        label="Título do Post"
        variant="outlined"
        :rules="[rules.required]"
        class="mb-4"
    ></v-text-field>

    <v-textarea
        v-model="editablePost.content"
        label="Conteúdo do Post"
        variant="outlined"
        rows="15"
        :rules="[rules.required]"
        auto-grow
    ></v-textarea>
    <div class="d-flex justify-end mt-4">
      <v-btn color="grey" variant="text" @click="cancel" class="mr-2">Cancelar</v-btn>
      <v-btn
          type="submit"
          color="primary"
          :loading="loading"
          :disabled="loading"
      >
        Salvar Post
      </v-btn>
    </div>
  </v-form>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';
import type { PostCreationPayload } from '@/services/postService';

const props = defineProps<{
  initialData?: PostCreationPayload;
  loading: boolean;
}>();

const emit = defineEmits<{
  (e: 'submit', payload: PostCreationPayload): void;
  (e: 'cancel'): void;
}>();

const editablePost = ref<PostCreationPayload>({ title: '', content: '' });

const rules = {
  required: (value: string) => !!value || 'Este campo é obrigatório.',
};

watch(
    () => props.initialData,
    (newData) => {
      if (newData) {
        editablePost.value = { ...newData };
      }
    },
    { immediate: true }
);

const submit = () => {
  emit('submit', editablePost.value);
};

const cancel = () => {
  emit('cancel');
};
</script>