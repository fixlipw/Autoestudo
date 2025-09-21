<template>
  <v-container class="py-8">
    <!-- Hero com CTA e Busca -->
    <v-row align="stretch" class="mb-6" justify="space-between">
      <v-col cols="12" md="7">
        <div class="d-flex align-center ga-3 mb-3">
          <img src="/bloguevi.svg" alt="BlogueVi" style="height:56px;width:56px" />
          <div>
            <h1 class="text-h4 mb-1">BlogueVi</h1>
            <p class="text-body-1 mb-0">
              Descubra os últimos posts da comunidade, compartilhe experiências e participe das discussões.
            </p>
          </div>
        </div>
      </v-col>
    </v-row>

    <!-- Sobre o Projeto -->
    <v-row class="mt-5" justify="center">
      <v-col cols="12" md="12" lg="11">
        <v-row>
          <v-col cols="12" md="6">
            <v-card elevation="1" class="mb-4">
              <v-card-title class="text-h6 d-flex align-center">
                <v-icon start color="primary">mdi-information</v-icon>
                Sobre o projeto
              </v-card-title>
              <v-card-text>
                <div class="text-body-1 mb-4">
                  Plataforma <strong>full-stack</strong> com backend <strong>Spring Boot</strong> e frontend <strong>Vue
                    3 + TypeScript</strong>.<br>
                  Demonstra autenticação, gerenciamento de posts/comentários e painel administrativo.
                </div>
                <v-divider class="mb-4"></v-divider>
                <div class="text-h6 mb-2 d-flex align-center">
                  <v-icon start color="primary">mdi-layers-triple</v-icon>
                  Stack Tecnológica
                </div>
                <div class="text-subtitle-2 mb-2">Backend</div>
                <div class="d-flex flex-wrap ga-2 mb-3">
                  <v-chip size="small" color="primary" variant="tonal">Spring Boot 3</v-chip>
                  <v-chip size="small" color="primary" variant="tonal">Spring Security (JWT)</v-chip>
                  <v-chip size="small" color="primary" variant="tonal">JPA / Hibernate</v-chip>
                  <v-chip size="small" color="primary" variant="tonal">PostgreSQL / H2</v-chip>
                  <v-chip size="small" color="primary" variant="tonal">Lombok</v-chip>
                </div>
                <div class="text-subtitle-2 mb-2">Frontend</div>
                <div class="d-flex flex-wrap ga-2">
                  <v-chip size="small" color="secondary" variant="tonal">Vue 3 + TS</v-chip>
                  <v-chip size="small" color="secondary" variant="tonal">Vite</v-chip>
                  <v-chip size="small" color="secondary" variant="tonal">Vuetify</v-chip>
                  <v-chip size="small" color="secondary" variant="tonal">Pinia</v-chip>
                  <v-chip size="small" color="secondary" variant="tonal">Axios</v-chip>
                </div>
              </v-card-text>
            </v-card>
          </v-col>
          <v-col cols="12" md="6">
            <v-card elevation="1">
              <v-card-title class="text-h6 d-flex align-center">
                <v-icon start color="primary">mdi-play-circle</v-icon>
                Como Rodar Localmente
              </v-card-title>
              <v-card-text>
                <div class="text-subtitle-2 mb-2">Backend</div>
                <v-sheet class="background rounded mb-3 pa-2" elevation="0">
                  <pre class="mb-0"><code>
        # Banco de dados (Docker)
        cd backend
        docker compose up -d

        # Aplicação (escolha uma opção)
        mvn spring-boot:run
        # ou
        java -jar target/backend-1.0-SNAPSHOT.jar
                </code></pre>
                </v-sheet>
                <div class="text-subtitle-2 mb-2">Frontend</div>
                <v-sheet class="background rounded pa-2" elevation="0">
                  <pre class="mb-0"><code>
        cd frontend
        npm install
        npm run dev
                </code></pre>
                </v-sheet>
              </v-card-text>
            </v-card>
          </v-col>
        </v-row>
      </v-col>
    </v-row>
  </v-container>
</template>