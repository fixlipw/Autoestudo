package com.ufc.blog.controller;

import com.ufc.blog.entity.User;
import com.ufc.blog.entity.UserStatus;
import com.ufc.blog.exception.BadRequestException;
import com.ufc.blog.exception.ResourceNotFoundException;
import com.ufc.blog.repository.UserRepository;
import com.ufc.blog.util.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;
    private final PasswordEncoder passwordEncoder;

    // ===== CRUD Operations =====

    /**
     * Retorna uma página contendo todos os usuários cadastrados.
     * Apenas usuários com papel ADMIN podem acessar este endpoint.
     *
     * @param page número da página (padrão: 0)
     * @param size quantidade de usuários por página (padrão: 10)
     * @return ResponseEntity contendo uma página de usuários
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<Page<User>> getAllUsers(@RequestParam(defaultValue = "0", name = "page") int page, @RequestParam(defaultValue = "10", name = "size") int size) {
        log.info("Listando todos os usuários (ADMIN)");
        return ResponseEntity.ok(userRepository.findAll(Pageable.ofSize(size).withPage(page)));
    }

    /**
     * Retorna os detalhes de um usuário específico pelo ID.
     * Usuários com papel ADMIN podem acessar qualquer usuário.
     * Usuários com papel USER só podem acessar seus próprios dados.
     *
     * @param id ID do usuário a ser buscado
     * @return ResponseEntity contendo os detalhes do usuário
     * @throws ResourceNotFoundException    se o usuário não for encontrado
     * @throws AuthorizationDeniedException se o usuário não tiver permissão
     */
    @PreAuthorize("permitAll()")
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") Long id) {
        log.info("Buscando usuário por ID {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", id));
        return ResponseEntity.ok(user);
    }

    /**
     * Atualiza os dados de um usuário existente.
     * Usuários com papel ADMIN podem atualizar qualquer usuário.
     * Usuários com papel USER só podem atualizar seus próprios dados.
     *
     * @param id             ID do usuário a ser atualizado
     * @param request        dados do usuário a serem atualizados
     * @param authentication informações de autenticação do usuário requisitante
     * @return ResponseEntity contendo os detalhes do usuário atualizado
     * @throws ResourceNotFoundException    se o usuário não for encontrado
     * @throws BadRequestException          se o username ou email já estiverem em uso
     * @throws AuthorizationDeniedException se o usuário não tiver permissão
     */
    @PreAuthorize("permitAll()")
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable("id") Long id, @Valid @RequestBody User request, Authentication authentication) {
        log.info("Atualizando usuário ID {}", id);
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", id));
        securityUtils.checkOwnershipOrAdmin(authentication, user);

        if (request.getUsername() != null) {
            validateUserUniqueness(request.getUsername(), null, id);
            user.setUsername(request.getUsername());
        }
        if (request.getEmail() != null) {
            validateUserUniqueness(null, request.getEmail(), id);
            user.setEmail(request.getEmail());
        }
        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getRole() != null) user.setRole(request.getRole());

        return ResponseEntity.ok(userRepository.save(user));
    }

    /**
     * Deleta um usuário pelo ID.
     * Usuários com papel ADMIN podem deletar qualquer usuário.
     * Usuários com papel USER só podem deletar seus próprios dados.
     *
     * @param id             ID do usuário a ser deletado
     * @param authentication informações de autenticação do usuário requisitante
     * @return ResponseEntity com status 204 (No Content) se a deleção for bem-sucedida
     * @throws ResourceNotFoundException    se o usuário não for encontrado
     * @throws AuthorizationDeniedException se o usuário não tiver permissão
     */
    @PreAuthorize("permitAll()")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id, Authentication authentication) {
        log.info("Deletando usuário ID {}", id);
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", id));
        securityUtils.checkOwnershipOrAdmin(authentication, user);
        userRepository.delete(user);
        return ResponseEntity.noContent().build();
    }

    // ===== Query Operations =====

    /**
     * Busca um usuário pelo username ou email.
     * Usuários com papel ADMIN podem buscar qualquer usuário.
     * Usuários com papel USER só podem buscar seus próprios dados.
     *
     * @param username       username do usuário a ser buscado (opcional)
     * @param email          email do usuário a ser buscado (opcional)
     * @param authentication informações de autenticação do usuário requisitante
     * @return ResponseEntity contendo os detalhes do usuário encontrado
     * @throws BadRequestException          se nenhum parâmetro for fornecido
     * @throws ResourceNotFoundException    se o usuário não for encontrado
     * @throws AuthorizationDeniedException se o usuário não tiver permissão
     */
    @PreAuthorize("permitAll()")
    @GetMapping("/search")
    public ResponseEntity<User> getUser(@RequestParam(name = "username", required = false) String username, @RequestParam(name = "email", required = false) String email, Authentication authentication) {
        log.info("Buscando usuário por username/email");
        if (username == null && email == null) {
            throw new BadRequestException("Parâmetro 'username' ou 'email' deve ser fornecido.");
        }

        User user = (username != null) ?
                userRepository.findByUsername(username)
                        .orElseThrow(() -> new ResourceNotFoundException("Usuário", "username", username)) :
                userRepository.findByEmail(email)
                        .orElseThrow(() -> new ResourceNotFoundException("Usuário", "email", email));

        securityUtils.checkOwnershipOrAdmin(authentication, user);
        return ResponseEntity.ok(user);
    }


    /**
     * Retorna uma página de usuários filtrados pelo status.
     * Apenas usuários com papel ADMIN podem acessar este endpoint.
     *
     * @param status status dos usuários a serem buscados
     * @param page   número da página (padrão: 0)
     * @param size   quantidade de usuários por página (padrão: 10)
     * @return ResponseEntity contendo uma página de usuários com o status especificado
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/search/status/{status}")
    public ResponseEntity<Page<User>> getUsersByStatus(@PathVariable(name = "status") UserStatus status, @RequestParam(defaultValue = "0", name = "page") int page, @RequestParam(defaultValue = "10", name = "size") int size) {
        log.info("Buscando usuários por status {}", status);
        return ResponseEntity.ok(userRepository.findByStatus(status, Pageable.ofSize(size).withPage(page)));
    }

    // ===== Status Management =====

    /**
     * Atualiza o status de um usuário.
     * Apenas usuários com papel ADMIN podem acessar este endpoint.
     *
     * @param id     ID do usuário cujo status será atualizado
     * @param status novo status do usuário
     * @return ResponseEntity contendo os detalhes do usuário com o status atualizado
     * @throws ResourceNotFoundException se o usuário não for encontrado
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<User> updateUserStatus(@PathVariable(name = "id") Long id, @RequestParam(name = "status") UserStatus status) {
        log.info("Atualizando status do usuário ID {} para {}", id, status);
        return ResponseEntity.ok(changeUserStatus(id, status));
    }

    /**
     * Ativa um usuário (define o status como ACTIVE).
     * Apenas usuários com papel ADMIN podem acessar este endpoint.
     *
     * @param id ID do usuário a ser ativado
     * @return ResponseEntity contendo os detalhes do usuário ativado
     * @throws ResourceNotFoundException se o usuário não for encontrado
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping("/{id}/activate")
    public ResponseEntity<User> activateUser(@PathVariable(name = "id") Long id) {
        log.info("Ativando usuário ID {}", id);
        return ResponseEntity.ok(changeUserStatus(id, UserStatus.ACTIVE));
    }

    /**
     * Suspende um usuário (define o status como SUSPENDED).
     * Apenas usuários com papel ADMIN podem acessar este endpoint.
     *
     * @param id ID do usuário a ser suspenso
     * @return ResponseEntity contendo os detalhes do usuário suspenso
     * @throws ResourceNotFoundException se o usuário não for encontrado
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping("/{id}/suspend")
    public ResponseEntity<User> suspendUser(@PathVariable(name = "id") Long id) {
        log.info("Suspendendo usuário ID {}", id);
        return ResponseEntity.ok(changeUserStatus(id, UserStatus.SUSPENDED));
    }

    /**
     * Desativa um usuário (define o status como INACTIVE).
     * Apenas usuários com papel ADMIN podem acessar este endpoint.
     *
     * @param id ID do usuário a ser desativado
     * @return ResponseEntity contendo os detalhes do usuário desativado
     * @throws ResourceNotFoundException se o usuário não for encontrado
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<User> deactivateUser(@PathVariable(name = "id") Long id) {
        log.info("Desativando usuário ID {}", id);
        return ResponseEntity.ok(changeUserStatus(id, UserStatus.INACTIVE));
    }

    // ===== Password Management =====

    /**
     * Atualiza a senha de um usuário.
     * Usuários com papel ADMIN podem atualizar a senha de qualquer usuário.
     * Usuários com papel USER só podem atualizar sua própria senha.
     *
     * @param id             ID do usuário cuja senha será atualizada
     * @param passwords      array contendo a senha atual e a nova senha
     * @param authentication informações de autenticação do usuário requisitante
     * @return ResponseEntity com status 204 (No Content) se a atualização for bem-sucedida
     * @throws BadRequestException          se o corpo da requisição estiver inválido ou a senha atual estiver incorreta
     * @throws ResourceNotFoundException    se o usuário não for encontrado
     * @throws AuthorizationDeniedException se o usuário não tiver permissão
     */
    @PreAuthorize("permitAll()")
    @PatchMapping("/{id}/password")
    public ResponseEntity<Void> updateUserPassword(@PathVariable(name = "id") Long id, @RequestBody String[] passwords, Authentication authentication) {
        log.info("Atualizando senha do usuário ID {}", id);
        if (passwords.length != 2) {
            throw new BadRequestException("O corpo da requisição deve conter a senha atual e a nova senha.");
        }

        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", id));
        securityUtils.checkOwnershipOrAdmin(authentication, user);

        String currentPassword = passwords[0];
        String newPassword = passwords[1];

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new BadRequestException("Senha antiga está incorreta.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return ResponseEntity.noContent().build();
    }

    /**
     * Verifica se um usuário existe pelo username ou email.
     * Este endpoint é público e não requer autenticação.
     *
     * @param username username do usuário a ser verificado (opcional)
     * @param email    email do usuário a ser verificado (opcional)
     * @return ResponseEntity contendo true se o usuário existir, false caso contrário
     * @throws BadRequestException se nenhum parâmetro for fornecido
     */
    @GetMapping("/validation/exists")
    public ResponseEntity<Boolean> userExists(@RequestParam(name = "username", required = false) String username, @RequestParam(name = "email", required = false) String email) {
        log.info("Verificando existência de usuário por username/email");
        boolean exists = false;
        if (username != null) {
            exists = userRepository.existsByUsername(username);
        } else if (email != null) {
            exists = userRepository.existsByEmail(email);
        }
        return ResponseEntity.ok(exists);
    }

    // ===== Utility =====

    /**
     * Valida a unicidade do username e email.
     *
     * @param username  username a ser validado (pode ser null)
     * @param email     email a ser validado (pode ser null)
     * @param excludeId ID do usuário a ser excluído da verificação (usado em atualizações)
     * @throws ResponseStatusException com status 400 se o username ou email já estiverem em uso
     */
    private void validateUserUniqueness(String username, String email, Long excludeId) {
        if (username != null) {
            userRepository.findByUsername(username).filter(u -> !u.getId().equals(excludeId)).ifPresent(u -> {
                throw new BadRequestException("Username já está em uso");
            });
        }
        if (email != null) {
            userRepository.findByEmail(email).filter(u -> !u.getId().equals(excludeId)).ifPresent(u -> {
                throw new BadRequestException("Email já está em uso");
            });
        }
    }

    /**
     * Altera o status de um usuário.
     *
     * @param id     ID do usuário cujo status será alterado
     * @param status novo status do usuário
     * @return usuário com o status atualizado
     * @throws ResponseStatusException com status 404 se o usuário não for encontrado
     */
    private User changeUserStatus(Long id, UserStatus status) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", id));
        user.setStatus(status);
        return userRepository.save(user);
    }

}
