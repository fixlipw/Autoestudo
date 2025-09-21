package com.ufc.blog.controller;

import com.ufc.blog.entity.Comment;
import com.ufc.blog.entity.Post;
import com.ufc.blog.entity.PostStatus;
import com.ufc.blog.entity.User;
import com.ufc.blog.exception.BadRequestException;
import com.ufc.blog.exception.ResourceNotFoundException;
import com.ufc.blog.repository.CommentRepository;
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
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentController {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final SecurityUtils securityUtils;

    // ===== CRUD Operations =====

    /**
     * Cria um novo comentário em um post específico.
     * Apenas usuários autenticados podem criar comentários.
     * Comentários só podem ser adicionados a posts publicados.
     *
     * @param postId         ID do post onde o comentário será adicionado
     * @param request        Detalhes do comentário a ser criado (apenas o campo content é considerado)
     * @param authentication Informações de autenticação do usuário requisitante
     * @return ResponseEntity contendo o comentário criado
     * @throws ResourceNotFoundException se o post não for encontrado
     * @throws BadRequestException       se o post não estiver publicado
     */
    @PreAuthorize("permitAll()")
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<Comment> createComment(@PathVariable("postId") Long postId, @Valid @RequestBody Comment request, Authentication authentication) {
        User author = securityUtils.getAuthenticatedUser(authentication);
        Post post = postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));

        if (post.getStatus() != PostStatus.PUBLISHED) {
            throw new BadRequestException("Não é possível comentar em posts não publicados");
        }

        log.info("Usuário {} criando comentário no post {}", author.getUsername(), postId);

        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setAuthor(author);
        comment.setPost(post);

        return ResponseEntity.status(HttpStatus.CREATED).body(commentRepository.save(comment));
    }

    /**
     * Recupera um comentário pelo ID.
     * Comentários de posts publicados são acessíveis a todos.
     * Comentários de posts não publicados são acessíveis apenas ao autor do post, administradores ou ao autor do comentário.
     *
     * @param id             ID do comentário a ser buscado
     * @param authentication Informações de autenticação do usuário requisitante
     * @return ResponseEntity contendo o comentário encontrado
     * @throws ResourceNotFoundException    se o comentário não for encontrado
     * @throws AuthorizationDeniedException se o usuário não tiver permissão para acessar o comentário
     */
    @GetMapping("/comments/{id}")
    public ResponseEntity<Comment> getCommentById(@PathVariable("id") Long id, Authentication authentication) {
        log.info("Buscando comentário ID {}", id);

        Comment comment = commentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Comentário", "id", id));

        Post post = comment.getPost();

        if (post.getStatus() == PostStatus.PUBLISHED || securityUtils.isAdmin(authentication) || securityUtils.isSelf(authentication, comment.getAuthor().getId())) {
            return ResponseEntity.ok(comment);
        }

        throw new AuthorizationDeniedException("Acesso negado");
    }

    /**
     * Apaga um comentário pelo ID.
     * Apenas o autor do comentário, administradores ou o autor do post (se não publicado) podem apagar comentários.
     *
     * @param id             ID do comentário a ser apagado
     * @param authentication Informações de autenticação do usuário requisitante
     * @return ResponseEntity com status 204 (No Content) se a deleção for bem-sucedida
     * @throws ResourceNotFoundException    se o comentário não for encontrado
     * @throws AuthorizationDeniedException se o usuário não tiver permissão para apagar o comentário
     */
    @PreAuthorize("permitAll()")
    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable("id") Long id, Authentication authentication) {
        User user = securityUtils.getAuthenticatedUser(authentication);

        Comment comment = commentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Comentário", "id", id));

        securityUtils.checkOwnershipOrAdmin(authentication, comment.getAuthor());

        log.info("Usuário {} deletando comentário ID {}", user.getUsername(), id);

        commentRepository.delete(comment);
        return ResponseEntity.noContent().build();
    }

    // ===== Query & Search Operations =====

    /**
     * Lista comentários de um post específico.
     * Comentários de posts publicados são acessíveis a todos.
     * Comentários de posts não publicados são acessíveis apenas ao autor do post e administradores.
     *
     * @param postId         ID do post cujos comentários serão listados
     * @param page           Número da página para paginação (padrão: 0)
     * @param size           Tamanho da página para paginação (padrão: 10)
     * @param authentication Informações de autenticação do usuário requisitante
     * @return ResponseEntity contendo uma página de comentários
     * @throws ResourceNotFoundException    se o post não for encontrado
     * @throws AuthorizationDeniedException se o usuário não tiver permissão para acessar os comentários
     */
    @PreAuthorize("permitAll()")
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<Page<Comment>> getCommentsByPost(@PathVariable("postId") Long postId, @RequestParam(defaultValue = "0", name = "page") int page, @RequestParam(defaultValue = "10", name = "size") int size, Authentication authentication) {
        log.info("Listando comentários do post {}", postId);

        Post post = postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));

        if (post.getStatus() == PostStatus.PUBLISHED || securityUtils.isAdmin(authentication) || securityUtils.isSelf(authentication, post.getAuthor().getId())) {
            return ResponseEntity.ok(commentRepository.findByPostId(postId, Pageable.ofSize(size).withPage(page)));
        }

        throw new AuthorizationDeniedException("Acesso negado");
    }

    /**
     * Lista comentários feitos por um autor específico.
     * Comentários de posts publicados são acessíveis a todos.
     * Comentários de posts não publicados são acessíveis apenas ao autor do comentário e administradores.
     *
     * @param authorId       ID do autor cujos comentários serão listados
     * @param page           Número da página para paginação (padrão: 0)
     * @param size           Tamanho da página para paginação (padrão: 10)
     * @param authentication Informações de autenticação do usuário requisitante
     * @return ResponseEntity contendo uma página de comentários
     */
    @PreAuthorize("permitAll()")
    @GetMapping("/users/{authorId}/comments")
    public ResponseEntity<Page<Comment>> getCommentsByAuthor(@PathVariable Long authorId, @RequestParam(defaultValue = "0", name = "page") int page, @RequestParam(defaultValue = "10", name = "size") int size, Authentication authentication) {
        log.info("Listando comentários do autor ID {}", authorId);

        if (securityUtils.isAdmin(authentication) || securityUtils.isSelf(authentication, authorId)) {
            return ResponseEntity.ok(commentRepository.findByAuthorId(authorId, Pageable.ofSize(size).withPage(page)));
        }

        return ResponseEntity.ok(commentRepository.findPublishedCommentsByAuthorId(authorId, Pageable.ofSize(size).withPage(page)));
    }

}
