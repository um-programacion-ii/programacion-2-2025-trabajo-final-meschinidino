package com.eventos.sistemaeventos.presentation.controller;

import com.eventos.sistemaeventos.application.service.UsuarioService;
import com.eventos.sistemaeventos.presentation.dto.LoginRequestDTO;
import com.eventos.sistemaeventos.presentation.dto.RegisterRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Endpoints para registro, login y gestión de sesión de usuarios")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UsuarioService usuarioService;

    @Operation(
        summary = "Iniciar sesión",
        description = "Autentica un usuario con username y password. Retorna información del usuario y establece sesión."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login exitoso"),
        @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()
                )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            usuarioService.actualizarUltimoLogin(request.getUsername());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("username", request.getUsername());
            response.put("message", "Login exitoso");

            log.info("Login exitoso para usuario: {}", request.getUsername());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error en login: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Credenciales inválidas");
            return ResponseEntity.status(401).body(response);
        }
    }

    @Operation(
        summary = "Registrar nuevo usuario",
        description = "Crea una nueva cuenta de usuario en el sistema. El username y email deben ser únicos."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario registrado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Usuario o email ya existe")
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO request) {
        try {
            var usuario = usuarioService.registrarUsuario(
                request.getUsername(),
                request.getPassword(),
                request.getEmail(),
                request.getFirstName(),
                request.getLastName()
            );

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("username", usuario.getUsername());
            response.put("message", "Usuario registrado exitosamente");

            log.info("Registro exitoso para usuario: {}", request.getUsername());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error en registro: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "Cerrar sesión", description = "Cierra la sesión del usuario actual")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Logout exitoso")
    })
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        SecurityContextHolder.clearContext();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Logout exitoso");

        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Obtener usuario actual",
        description = "Retorna la información del usuario autenticado actualmente"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario obtenido"),
        @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }

        String username = authentication.getName();
        var usuario = usuarioService.obtenerUsuario(username);

        Map<String, Object> response = new HashMap<>();
        response.put("username", usuario.getUsername());
        response.put("email", usuario.getEmail());
        response.put("firstName", usuario.getFirstName());
        response.put("lastName", usuario.getLastName());

        return ResponseEntity.ok(response);
    }
}
