package br.edu.umfg.teste.spring.controllers;

import br.edu.umfg.teste.spring.security.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Value("${app.login}") private String supremoLogin;
    @Value("${app.senha}") private String supremoSenha;

    private final JwtUtil jwtUtil;

    public AuthController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/auth/login")
    public String loginForm(
            @RequestParam String username,
            @RequestParam String password,
            HttpServletResponse response
    ) {
        logger.info("Tentativa de login para o usuário '{}'", username);

        if (supremoLogin.equals(username) && supremoSenha.equals(password)) {
            String token = jwtUtil.gerarToken(username);
            Cookie cookie = new Cookie("JWT_TOKEN", token);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60);
            response.addCookie(cookie);

            logger.info("Login bem-sucedido para o usuário '{}'", username);
            return "redirect:/index";
        }

        logger.warn("Falha no login para o usuário '{}'", username);
        return "redirect:/login?error";
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            logger.warn("Acesso à página de login com erro de autenticação.");
            model.addAttribute("error", "Usuário ou senha inválidos");
        }
        return "login";
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("JWT_TOKEN", "");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        logger.info("Logout realizado. Cookie JWT removido.");
        return ResponseEntity.ok().build();
    }

    @GetMapping("/")
    public String redirecionarRaiz() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            return "redirect:/login";
        }

        return "redirect:/index";
    }
}
