package com.ecommerce.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name = "usuario")
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;
    
    @NotBlank(message = "Nome é obrigatório")
    @Column(nullable = false)
    private String nome;
    
    @Email(message = "Email deve ser válido")
    @NotBlank(message = "Email é obrigatório")
    @Column(unique = true, nullable = false)
    private String email;
    
    @NotBlank(message = "Login é obrigatório")
    @Column(unique = true, nullable = false)
    private String login;
    
    @NotBlank(message = "Senha é obrigatória")
    @Column(nullable = false)
    private String senha; // Hash da senha
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_usuario", nullable = false)
    private TipoUsuario tipoUsuario = TipoUsuario.CLIENTE;
    
    @Pattern(regexp = "\\d{11}", message = "CPF deve ter 11 dígitos")
    @Column(unique = true)
    private String cpf;
    
    private String telefone;
    
    // Endereço
    private String logradouro;
    private String cidade;
    private String estado;
    private String cep;
    private String numero;
    private String complemento;
    
    public enum TipoUsuario {
        ADMIN, CLIENTE
    }
    
    // Constructors
    public Usuario() {}
    
    public Usuario(String nome, String email, String login, String senha) {
        this.nome = nome;
        this.email = email;
        this.login = login;
        this.senha = senha;
    }
    
    // Getters and Setters
    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }
    
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    
    public TipoUsuario getTipoUsuario() { return tipoUsuario; }
    public void setTipoUsuario(TipoUsuario tipoUsuario) { this.tipoUsuario = tipoUsuario; }
    
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    
    public String getLogradouro() { return logradouro; }
    public void setLogradouro(String logradouro) { this.logradouro = logradouro; }
    
    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }
    
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    
    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }
    
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    
    public String getComplemento() { return complemento; }
    public void setComplemento(String complemento) { this.complemento = complemento; }
    
    public boolean isAdmin() { return tipoUsuario == TipoUsuario.ADMIN; }
    
    public boolean hasCompleteAddress() {
        return logradouro != null && !logradouro.trim().isEmpty() &&
               cidade != null && !cidade.trim().isEmpty() &&
               estado != null && !estado.trim().isEmpty() &&
               cep != null && !cep.trim().isEmpty() &&
               numero != null && !numero.trim().isEmpty();
    }
}