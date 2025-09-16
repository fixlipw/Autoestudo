-- Inserindo 5 posts para o author_id = 1
INSERT INTO posts (title, content, summary, status, author_id, created_at, updated_at)
VALUES ('Primeiro Post do Usuário 1', 'Conteúdo detalhado do primeiro post.', 'Resumo do primeiro post.', 'PUBLISHED',
        1, '2025-09-11 20:23:00', '2025-09-11 20:23:00'),
       ('Segunda Publicação do Usuário 1', 'Conteúdo sobre a segunda publicação.', 'Resumo da segunda publicação.',
        'PUBLISHED', 1, '2025-09-11 20:23:00', '2025-09-11 20:23:00'),
       ('Terceiro Tópico do Usuário 1', 'Conteúdo aprofundado sobre o terceiro tópico.', 'Resumo do terceiro tópico.',
        'PUBLISHED', 1, '2025-09-11 20:23:00', '2025-09-11 20:23:00'),
       ('Quarta Ideia do Usuário 1', 'Explorando a quarta ideia em detalhes.', 'Resumo da quarta ideia.', 'PUBLISHED',
        1, '2025-09-11 20:23:00', '2025-09-11 20:23:00'),
       ('Quinto Artigo do Usuário 1', 'Artigo completo sobre o quinto assunto.', 'Resumo do quinto artigo.',
        'PUBLISHED', 1, '2025-09-11 20:23:00', '2025-09-11 20:23:00');

-- Inserindo 5 posts para o author_id = 2
INSERT INTO posts (title, content, summary, status, author_id, created_at, updated_at)
VALUES ('Primeiro Post do Usuário 2', 'Conteúdo detalhado do primeiro post do usuário 2.',
        'Resumo do primeiro post do usuário 2.', 'PUBLISHED', 2, '2025-09-11 20:23:00', '2025-09-11 20:23:00'),
       ('Segunda Publicação do Usuário 2', 'Conteúdo sobre a segunda publicação do usuário 2.',
        'Resumo da segunda publicação do usuário 2.', 'PUBLISHED', 2, '2025-09-11 20:23:00', '2025-09-11 20:23:00'),
       ('Terceiro Tópico do Usuário 2', 'Conteúdo aprofundado sobre o terceiro tópico do usuário 2.',
        'Resumo do terceiro tópico do usuário 2.', 'PUBLISHED', 2, '2025-09-11 20:23:00', '2025-09-11 20:23:00'),
       ('Quarta Ideia do Usuário 2', 'Explorando a quarta ideia em detalhes do usuário 2.',
        'Resumo da quarta ideia do usuário 2.', 'PUBLISHED', 2, '2025-09-11 20:23:00', '2025-09-11 20:23:00'),
       ('Quinto Artigo do Usuário 2', 'Artigo completo sobre o quinto assunto do usuário 2.',
        'Resumo do quinto artigo do usuário 2.', 'PUBLISHED', 2, '2025-09-11 20:23:00', '2025-09-11 20:23:00');

-- Inserindo 5 posts para o author_id = 3
INSERT INTO posts (title, content, summary, status, author_id, created_at, updated_at)
VALUES ('Primeiro Post do Usuário 3', 'Conteúdo detalhado do primeiro post do usuário 3.',
        'Resumo do primeiro post do usuário 3.', 'PUBLISHED', 3, '2025-09-11 20:23:00', '2025-09-11 20:23:00'),
       ('Segunda Publicação do Usuário 3', 'Conteúdo sobre a segunda publicação do usuário 3.',
        'Resumo da segunda publicação do usuário 3.', 'PUBLISHED', 3, '2025-09-11 20:23:00', '2025-09-11 20:23:00'),
       ('Terceiro Tópico do Usuário 3', 'Conteúdo aprofundado sobre o terceiro tópico do usuário 3.',
        'Resumo do terceiro tópico do usuário 3.', 'PUBLISHED', 3, '2025-09-11 20:23:00', '2025-09-11 20:23:00'),
       ('Quarta Ideia do Usuário 3', 'Explorando a quarta ideia em detalhes do usuário 3.',
        'Resumo da quarta ideia do usuário 3.', 'PUBLISHED', 3, '2025-09-11 20:23:00', '2025-09-11 20:23:00'),
       ('Quinto Artigo do Usuário 3', 'Artigo completo sobre o quinto assunto do usuário 3.',
        'Resumo do quinto artigo do usuário 3.', 'PUBLISHED', 3, '2025-09-11 20:23:00', '2025-09-11 20:23:00');

-- Inserindo 3 comentários para cada um dos 15 posts
-- Comentários para o Post 1 (author_id: 1)
INSERT INTO comments (content, active, author_id, post_id, created_at, updated_at)
VALUES ('Ótimo post, muito informativo!', TRUE, 2, 1, '2025-09-11 20:23:00', '2025-09-11 20:23:00'),
       ('Gostei da perspectiva, obrigado por compartilhar.', TRUE, 3, 1, '2025-09-11 20:23:00', '2025-09-11 20:23:00'),
       ('Concordo plenamente com o que foi dito.', TRUE, 1, 1, '2025-09-11 20:23:00', '2025-09-11 20:23:00');

-- Comentários para o Post 2 (author_id: 1)
INSERT INTO comments (content, active, author_id, post_id, created_at, updated_at)
VALUES ('Interessante, nunca tinha pensado nisso.', TRUE, 3, 2, '2025-09-11 20:23:00', '2025-09-11 20:23:00'),
       ('Você poderia elaborar mais sobre o segundo parágrafo?', TRUE, 2, 2, '2025-09-11 20:23:00',
        '2025-09-11 20:23:00'),
       ('Excelente artigo!', TRUE, 1, 2, '2025-09-11 20:23:00', '2025-09-11 20:23:00');

-- Comentários para o Post 3 (author_id: 1)
INSERT INTO comments (content, active, author_id, post_id, created_at, updated_at)
VALUES ('Muito bem escrito!', TRUE, 2, 3, '2025-09-11 20:23:00', '2025-09-11 20:23:00'),
       ('Adorei a leitura, parabéns.', TRUE, 3, 3, '2025-09-11 20:23:00', '2025-09-11 20:23:00'),
       ('Vou compartilhar com meus amigos.', TRUE, 1, 3, '2025-09-11 20:23:00', '2025-09-11 20:23:00');

-- Comentários para o Post 4 (author_id: 1)
INSERT INTO comments (content, active, author_id, post_id, created_at, updated_at)
VALUES ('Não concordo com tudo, mas é um bom ponto de partida.', TRUE, 3, 4, '2025-09-11 20:23:00',
        '2025-09-11 20:23:00'),
       ('Obrigado pelas informações.', TRUE, 2, 4, '2025-09-11 20:23:00', '2025-09-11 20:23:00'),
       ('Bom trabalho!', TRUE, 1, 4, '2025-09-11 20:23:00', '2025-09-11 20:23:00');

-- Comentários para o Post 5 (author_id: 1)
INSERT INTO comments (content, active, author_id, post_id, created_at, updated_at)
VALUES ('Fantástico! Continue assim.', TRUE, 2, 5, '2025-09-11 20:23:00', '2025-09-11 20:23:00'),
       ('Gostei muito do resumo.', TRUE, 3, 5, '2025-09-11 20:23:00', '2025-09-11 20:23:00'),
       ('Conteúdo de alta qualidade.', TRUE, 1, 5, '2025-09-11 20:23:00', '2025-09-11 20:23:00');

-- Comentários para o Post 6 (author_id: 2)
INSERT INTO comments (content, active, author_id, post_id, created_at, updated_at)
VALUES ('Excelente ponto de vista.', TRUE, 1, 6, '2025-09-11 20:23:00', '2025-09-11 20:23:00'),
       ('Esse post me ajudou bastante, obrigado!', TRUE, 3, 6, '2025-09-11 20:23:00', '2025-09-11 20:23:00'),
       ('Parabéns pelo conteúdo.', TRUE, 2, 6, '2025-09-11 20:23:00', '2025-09-11 20:23:00');

-- Comentários para o Post 7 (author_id: 2)
INSERT INTO comments (content, active, author_id, post_id, created_at, updated_at)
VALUES ('Muito claro e objetivo.', TRUE, 3, 7, '2025-09-11 20:23:00', '2025-09-11 20:23:00'),
       ('Adicionaria um ponto sobre X, mas no geral está ótimo.', TRUE, 1, 7, '2025-09-11 20:23:00',
        '2025-09-11 20:23:00'),
       ('Gostei muito!', TRUE, 2, 7, '2025-09-11 20:23:00', '2025-09-11 20:23:00');

-- Comentários para o Post 8 (author_id: 2)
INSERT INTO comments (content, active, author_id, post_id, created_at, updated_at)
VALUES ('Incrível, como sempre!', TRUE, 1, 8, '2025-09-11 20:23:00', '2025-09-11 20:23:00'),
       ('Compartilhando agora mesmo.', TRUE, 3, 8, '2025-09-11 20:23:00', '2025-09-11 20:23:00'),
       ('Ótima análise.', TRUE, 2, 8, '2025-09-11 20:23:00', '2025-09-11 20:23:00');

-- Comentários para o Post 9 (author_id: 2)
INSERT INTO comments (content, active, author_id, post_id, created_at, updated_at)
VALUES ('Reflexão muito necessária.', TRUE, 1, 9, '2025-09-11 20:23:00', '2025-09-11 20:23:00'),
       ('Obrigado por trazer esse assunto à tona.', TRUE, 3, 9, '2025-09-11 20:23:00', '2025-09-11 20:23:00'),
       ('Continue com o excelente trabalho.', TRUE, 2, 9, '2025-09-11 20:23:00', '2025-09-11 20:23:00');

-- Comentários para o Post 10 (author_id: 2)
INSERT INTO comments (content, active, author_id, post_id, created_at, updated_at)
VALUES ('Post muito bem estruturado.', TRUE, 3, 10, '2025-09-11 20:23:00', '2025-09-11 20:23:00'),
       ('Concordo com a conclusão.', TRUE, 1, 10, '2025-09-11 20:23:00', '2025-09-11 20:23:00'),
       ('Perfeito!', TRUE, 2, 10, '2025-09-11 20:23:00', '2025-09-11 20:23:00');

-- Comentários para o Post 11 (author_id: 3)
INSERT INTO comments (content, active, author_id, post_id, created_at, updated_at)
VALUES ('Que ótima abordagem!', TRUE, 1, 11, '2025-09-11 20:23:00', '2025-09-11 20:23:00'),
       ('Não poderia concordar mais.', TRUE, 2, 11, '2025-09-11 20:23:00', '2025-09-11 20:23:00'),
       ('Estava precisando ler algo assim.', TRUE, 3, 11, '2025-09-11 20:23:00', '2025-09-11 20:23:00');

-- Comentários para o Post 12 (author_id: 3)
INSERT INTO comments (content, active, author_id, post_id, created_at, updated_at)
VALUES ('Simplesmente brilhante.', TRUE, 2, 12, '2025-09-11 20:23:00', '2025-09-11 20:23:00'),
       ('Você tem alguma fonte para aprofundar no assunto?', TRUE, 1, 12, '2025-09-11 20:23:00', '2025-09-11 20:23:00'),
       ('Parabéns!', TRUE, 3, 12, '2025-09-11 20:23:00', '2025-09-11 20:23:00');

-- Comentários para o Post 13 (author_id: 3)
INSERT INTO comments (content, active, author_id, post_id, created_at, updated_at)
VALUES ('Muito obrigado por este post.', TRUE, 1, 13, '2025-09-11 20:23:00', '2025-09-11 20:23:00'),
       ('Amei a forma como você explicou.', TRUE, 2, 13, '2025-09-11 20:23:00', '2025-09-11 20:23:00'),
       ('Conteúdo valioso.', TRUE, 3, 13, '2025-09-11 20:23:00', '2025-09-11 20:23:00');

-- Comentários para o Post 14 (author_id: 3)
INSERT INTO comments (content, active, author_id, post_id, created_at, updated_at)
VALUES ('Faz todo o sentido.', TRUE, 2, 14, '2025-09-11 20:23:00', '2025-09-11 20:23:00'),
       ('Vou salvar para ler novamente mais tarde.', TRUE, 1, 14, '2025-09-11 20:23:00', '2025-09-11 20:23:00'),
       ('Muito bom!', TRUE, 3, 14, '2025-09-11 20:23:00', '2025-09-11 20:23:00');

-- Comentários para o Post 15 (author_id: 3)
INSERT INTO comments (content, active, author_id, post_id, created_at, updated_at)
VALUES ('Uau, que post incrível!', TRUE, 1, 15, '2025-09-11 20:23:00', '2025-09-11 20:23:00'),
       ('Obrigado, isso foi muito útil.', TRUE, 2, 15, '2025-09-11 20:23:00', '2025-09-11 20:23:00'),
       ('Finalizando a série com chave de ouro!', TRUE, 3, 15, '2025-09-11 20:23:00', '2025-09-11 20:23:00');

