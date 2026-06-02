package com.ecommerce.controller;

import com.ecommerce.entity.Usuario;
import com.ecommerce.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class UserController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new CadastroUsuarioForm());
        }
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("user") CadastroUsuarioForm form,
                          RedirectAttributes redirectAttributes) {
        String login = limpar(form.getNomeUsuario());
        String nome = limpar(form.getNomeCompleto());
        String email = limpar(form.getEmail());
        String senha = form.getSenha();

        if (isBlank(login) || isBlank(nome) || isBlank(email) || isBlank(senha)) {
            redirectAttributes.addFlashAttribute("erro", "Preencha os campos obrigatórios.");
            redirectAttributes.addFlashAttribute("user", form);
            return "redirect:/register";
        }

        if (usuarioRepository.existsByLogin(login)) {
            redirectAttributes.addFlashAttribute("erro", "Nome de usuário já cadastrado.");
            redirectAttributes.addFlashAttribute("user", form);
            return "redirect:/register";
        }

        if (usuarioRepository.existsByEmail(email)) {
            redirectAttributes.addFlashAttribute("erro", "Email já cadastrado.");
            redirectAttributes.addFlashAttribute("user", form);
            return "redirect:/register";
        }

        Usuario usuario = new Usuario(nome, email, login, senha);
        usuario.setTipoUsuario(Usuario.TipoUsuario.CLIENTE);
        usuario.setTelefone(limpar(form.getTelefone()));
        usuario.setLogradouro(limpar(form.getEndereco()));

        usuarioRepository.save(usuario);

        redirectAttributes.addFlashAttribute("sucesso", "Cadastro realizado com sucesso. Faça login para continuar.");
        return "redirect:/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                       @RequestParam String password,
                       @RequestParam(required = false) String redirect,
                       HttpSession session,
                       RedirectAttributes redirectAttributes) {
        
        Optional<Usuario> usuarioOpt = usuarioRepository.findByLogin(username);
        
        if (usuarioOpt.isPresent() && usuarioOpt.get().getSenha().equals(password)) {
            session.setAttribute("usuario", usuarioOpt.get());
            
            if (redirect != null && !redirect.isEmpty() && !redirect.equals("null")) {
                return "redirect:" + redirect;
            }
            
            return "redirect:/";
        } else {
            redirectAttributes.addFlashAttribute("erro", "Usuário ou senha inválidos!");
            return "redirect:/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    private static String limpar(String valor) {
        return valor == null ? null : valor.trim();
    }

    private static boolean isBlank(String valor) {
        return valor == null || valor.trim().isEmpty();
    }

    public static class CadastroUsuarioForm {
        private String nomeUsuario;
        private String email;
        private String nomeCompleto;
        private String senha;
        private String endereco;
        private String telefone;

        public String getNomeUsuario() { return nomeUsuario; }
        public void setNomeUsuario(String nomeUsuario) { this.nomeUsuario = nomeUsuario; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getNomeCompleto() { return nomeCompleto; }
        public void setNomeCompleto(String nomeCompleto) { this.nomeCompleto = nomeCompleto; }

        public String getSenha() { return senha; }
        public void setSenha(String senha) { this.senha = senha; }

        public String getEndereco() { return endereco; }
        public void setEndereco(String endereco) { this.endereco = endereco; }

        public String getTelefone() { return telefone; }
        public void setTelefone(String telefone) { this.telefone = telefone; }
    }
}
