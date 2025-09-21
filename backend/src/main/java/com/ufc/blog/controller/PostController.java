package com.ufc.blog.controller;

import com.ufc.blog.entity.Post;
import com.ufc.blog.entity.PostStatus;
import com.ufc.blog.entity.User;
import com.ufc.blog.exception.ResourceNotFoundException;
import com.ufc.blog.repository.PostRepository;
import com.ufc.blog.util.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostRepository postRepository;
    private final SecurityUtils securityUtils;

    // ===== CRUD Operations =====

    /**
     * Cria um novo post. Apenas usuários autenticados podem criar posts.
     *
     * @param request        Detalhes do post a ser criado
     * @param authentication Informações de autenticação do usuário requisitante
     * @return ResponseEntity contendo o post criado
     */
    @PreAuthorize("permitAll()")
    @PostMapping
    public ResponseEntity<Post> createPost(@Valid @RequestBody Post request, Authentication authentication) {
        User author = securityUtils.getAuthenticatedUser(authentication);
        log.info("Criando novo post pelo usuário {}", author.getUsername());

        Post post = new Post();
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setAuthor(author);
        post.setStatus(PostStatus.DRAFT);

        post = postRepository.save(post);
        return ResponseEntity.status(HttpStatus.CREATED).body(post);
    }

    /**
     * Recupera uma página contendo todos os posts.
     * Administradores veem todos os posts, usuários autenticados veem apenas seus próprios posts,
     * e usuários não autenticados veem apenas posts publicados.
     *
     * @param page           número da página (padrão: 0)
     * @param size           tamanho da página (padrão: 10)
     * @param authentication informações de autenticação do usuário requisitante (pode ser null)
     * @return ResponseEntity contendo a página de posts
     */
    @PreAuthorize("permitAll()")
    @GetMapping
    public ResponseEntity<Page<Post>> getAllPosts(@RequestParam(defaultValue = "0", name = "page") int page, @RequestParam(defaultValue = "10", name = "size") int size, Authentication authentication) {
        log.info("Listando todos os posts");

        Page<Post> posts;
        if (securityUtils.isAdmin(authentication)) {
            posts = postRepository.findAll(Pageable.ofSize(size).withPage(page));
        } else if (authentication != null && authentication.isAuthenticated()) {
            User user = securityUtils.getAuthenticatedUser(authentication);
            posts = postRepository.findByAuthorId(user.getId(), Pageable.ofSize(size).withPage(page));
        } else {
            posts = postRepository.findByStatus(PostStatus.PUBLISHED, Pageable.ofSize(size).withPage(page));
        }

        return ResponseEntity.ok(posts);
    }

    /**
     * Recupera um post pelo ID.
     * Posts publicados são acessíveis a todos. Posts não publicados são acessíveis apenas ao autor ou administradores.
     *
     * @param id             ID do post a ser buscado
     * @param authentication informações de autenticação do usuário requisitante (pode ser null)
     * @return ResponseEntity contendo detalhes do post
     * @throws ResourceNotFoundException    se o post não for encontrado
     * @throws AuthorizationDeniedException se o usuário não tiver permissão para ver o post
     */
    @PreAuthorize("permitAll()")
    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable("id") Long id, Authentication authentication) {
        log.info("Buscando post por ID {}", id);
        Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));

        if (post.getStatus() == PostStatus.PUBLISHED) {
            return ResponseEntity.ok(post);
        }
        if (authentication != null) {
            securityUtils.checkOwnershipOrAdmin(authentication, post.getAuthor());
            return ResponseEntity.ok(post);
        }

        throw new AuthorizationDeniedException("Acesso negado");
    }

    /**
     * Atualiza os dados de um post existente.
     * Apenas o autor do post ou administradores podem atualizar.
     *
     * @param id             ID do post a ser atualizado
     * @param request        dados do post a serem atualizados
     * @param authentication informações de autenticação do usuário requisitante
     * @return ResponseEntity contendo detalhes do post atualizado
     * @throws ResourceNotFoundException    se o post não for encontrado
     * @throws AuthorizationDeniedException se o usuário não for o autor ou administrador
     */
    @PreAuthorize("permitAll()")
    @PutMapping("/{id}")
    public ResponseEntity<Post> updatePost(@PathVariable("id") Long id, @Valid @RequestBody Post request, Authentication authentication) {
        User author = securityUtils.getAuthenticatedUser(authentication);
        log.info("Atualizando post ID {} pelo usuário {}", id, author.getUsername());

        Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));

        securityUtils.checkOwnershipOrAdmin(authentication, post.getAuthor());

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());

        return ResponseEntity.ok(postRepository.save(post));
    }

    /**
     * Deleta um post pelo ID.
     * Apenas o autor do post ou administradores podem deletar.
     *
     * @param id             ID do post a ser deletado
     * @param authentication informações de autenticação do usuário requisitante
     * @return ResponseEntity com status 204 (No Content) se a deleção for bem-sucedida
     * @throws ResourceNotFoundException    se o post não for encontrado
     * @throws AuthorizationDeniedException se o usuário não for o autor ou administrador
     */
    @PreAuthorize("permitAll()")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable("id") Long id, Authentication authentication) {
        User author = securityUtils.getAuthenticatedUser(authentication);
        log.info("Deletando post ID {} pelo usuário {}", id, author.getUsername());

        Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));

        securityUtils.checkOwnershipOrAdmin(authentication, post.getAuthor());

        postRepository.delete(post);
        return ResponseEntity.noContent().build();
    }

    // ===== Query & Search Operations =====

    /**
     * Recupera uma página contendo todos os posts publicados.
     * Este endpoint é público e não requer autenticação.
     *
     * @param page número da página (padrão: 0)
     * @param size tamanho da página (padrão: 10)
     * @return ResponseEntity contendo a página de posts publicados
     */
    @GetMapping("/published")
    public ResponseEntity<Page<Post>> getPublishedPosts(@RequestParam(defaultValue = "0", name = "page") int page, @RequestParam(defaultValue = "10", name = "size") int size) {
        log.info("Listando posts publicados");
        return ResponseEntity.ok(postRepository.findByStatus(PostStatus.PUBLISHED, Pageable.ofSize(size).withPage(page)));
    }

    /**
     * Recupera uma página contendo todos os posts de um autor específico.
     * Administradores e o próprio autor veem todos os posts, demais usuários veem apenas publicados.
     *
     * @param authorId       ID do autor cujos posts serão buscados
     * @param page           número da página (padrão: 0)
     * @param size           tamanho da página (padrão: 10)
     * @param authentication informações de autenticação do usuário requisitante
     * @return ResponseEntity contendo a página de posts do autor
     */
    @PreAuthorize("permitAll()")
    @GetMapping("/author/{authorId}")
    public ResponseEntity<Page<Post>> getPostsByAuthor(@PathVariable("authorId") Long authorId, @RequestParam(defaultValue = "0", name = "page") int page, @RequestParam(defaultValue = "10", name = "size") int size, Authentication authentication) {
        if (securityUtils.isAdmin(authentication) || securityUtils.isSelf(authentication, authorId)) {
            return ResponseEntity.ok(postRepository.findByAuthorId(authorId, Pageable.ofSize(size).withPage(page)));
        }
        return ResponseEntity.ok(postRepository.findByAuthorIdAndStatus(authorId, PostStatus.PUBLISHED, Pageable.ofSize(size).withPage(page)));
    }

    /**
     * Recupera uma página contendo todos os posts com um status específico.
     * Apenas administradores podem acessar este endpoint.
     *
     * @param status status dos posts a serem buscados
     * @param page   número da página (padrão: 0)
     * @param size   tamanho da página (padrão: 10)
     * @return ResponseEntity contendo a página de posts com o status especificado
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/status/{status}")
    public ResponseEntity<Page<Post>> getPostsByStatus(@PathVariable("status") PostStatus status, @RequestParam(defaultValue = "0", name = "page") int page, @RequestParam(defaultValue = "10", name = "size") int size) {
        log.info("Listando posts com status {}", status);
        return ResponseEntity.ok(postRepository.findByStatus(status, Pageable.ofSize(size).withPage(page)));
    }

    /**
     * Recupera uma página contendo todos os posts de um autor específico com um status específico.
     * Apenas administradores podem acessar este endpoint.
     *
     * @param authorId ID do autor cujos posts serão buscados
     * @param status   status dos posts a serem buscados
     * @param page     número da página (padrão: 0)
     * @param size     tamanho da página (padrão: 10)
     * @return ResponseEntity contendo a página de posts do autor com o status especificado
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/author/{authorId}/status/{status}")
    public ResponseEntity<Page<Post>> getPostsByAuthorAndStatus(@PathVariable("authorId") Long authorId, @PathVariable("status") PostStatus status, @RequestParam(defaultValue = "0", name = "page") int page, @RequestParam(defaultValue = "10", name = "size") int size) {
        log.info("Listando posts do autor ID {} com status {}", authorId, status);
        return ResponseEntity.ok(postRepository.findByAuthorIdAndStatus(authorId, status, Pageable.ofSize(size).withPage(page)));
    }

    // ===== Status Management =====

    /**
     * Atualiza o status de um post (DRAFT, PUBLISHED, ARCHIVED).
     * Apenas o autor do post ou administradores podem atualizar o status.
     *
     * @param id             ID do post cujo status será atualizado
     * @param status         novo status do post
     * @param authentication informações de autenticação do usuário requisitante
     * @return ResponseEntity contendo detalhes do post com o status atualizado
     * @throws ResourceNotFoundException    se o post não for encontrado
     * @throws AuthorizationDeniedException se o usuário não for o autor ou administrador
     */
    @PreAuthorize("permitAll()")
    @PatchMapping("/{id}/status")
    public ResponseEntity<Post> updatePostStatus(@PathVariable("id") Long id, @RequestParam("status") PostStatus status, Authentication authentication) {
        User author = securityUtils.getAuthenticatedUser(authentication);
        log.info("Atualizando status do post ID {} para {} pelo usuário {}", id, status, author.getUsername());

        Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));

        securityUtils.checkOwnershipOrAdmin(authentication, post.getAuthor());

        post.setStatus(status);
        return ResponseEntity.ok(postRepository.save(post));
    }

    /**
     * Publica um post, alterando seu status para PUBLISHED.
     * Apenas o autor do post ou administradores podem publicar.
     *
     * @param id             ID do post a ser publicado
     * @param authentication informações de autenticação do usuário requisitante
     * @return ResponseEntity contendo detalhes do post publicado
     * @throws ResourceNotFoundException    se o post não for encontrado
     * @throws AuthorizationDeniedException se o usuário não for o autor ou administrador
     */
    @PreAuthorize("permitAll()")
    @PatchMapping("/{id}/publish")
    public ResponseEntity<Post> publishPost(@PathVariable("id") Long id, Authentication authentication) {
        return changePostStatus(id, PostStatus.PUBLISHED, authentication);
    }

    /**
     * Remove a publicação de um post, alterando seu status para DRAFT.
     * Apenas o autor do post ou administradores podem remover a publicação.
     *
     * @param id             ID do post cuja publicação será removida
     * @param authentication informações de autenticação do usuário requisitante
     * @return ResponseEntity contendo detalhes do post com a publicação removida
     * @throws ResourceNotFoundException    se o post não for encontrado
     * @throws AuthorizationDeniedException se o usuário não for o autor ou administrador
     */
    @PreAuthorize("permitAll()")
    @PatchMapping("/{id}/unpublish")
    public ResponseEntity<Post> unpublishPost(@PathVariable("id") Long id, Authentication authentication) {
        return changePostStatus(id, PostStatus.DRAFT, authentication);
    }

    /**
     * Arquiva um post, alterando seu status para ARCHIVED.
     * Apenas o autor do post ou administradores podem arquivar.
     *
     * @param id             ID do post a ser arquivado
     * @param authentication informações de autenticação do usuário requisitante
     * @return ResponseEntity contendo detalhes do post arquivado
     * @throws ResourceNotFoundException    se o post não for encontrado
     * @throws AuthorizationDeniedException se o usuário não for o autor ou administrador
     */
    @PreAuthorize("permitAll()")
    @PatchMapping("/{id}/archive")
    public ResponseEntity<Post> archivePost(@PathVariable("id") Long id, Authentication authentication) {
        return changePostStatus(id, PostStatus.ARCHIVED, authentication);
    }

    // ===== Utility =====

    /**
     * Método auxiliar para alterar o status de um post.
     * Apenas o autor do post ou administradores podem alterar o status.
     *
     * @param id             ID do post cujo status será alterado
     * @param status         novo status do post
     * @param authentication informações de autenticação do usuário requisitante
     * @return ResponseEntity contendo detalhes do post com o status alterado
     * @throws ResourceNotFoundException    se o post não for encontrado
     * @throws AuthorizationDeniedException se o usuário não for o autor ou administrador
     */
    private ResponseEntity<Post> changePostStatus(Long id, PostStatus status, Authentication authentication) {
        User user = securityUtils.getAuthenticatedUser(authentication);
        log.info("Alterando status do post ID {} para {} pelo usuário {}", id, status, user.getUsername());

        Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));

        securityUtils.checkOwnershipOrAdmin(authentication, post.getAuthor());

        post.setStatus(status);
        return ResponseEntity.ok(postRepository.save(post));
    }

}
