# E-Commerce Spring Boot

Sistema de E-commerce migrado para Spring Boot com interface web moderna.

## 🚀 Migração Concluída

O projeto foi **migrado com sucesso** de Java SE + Swing para **Spring Boot + Thymeleaf**.

### ✅ O que foi implementado:

- **Spring Boot 3.2.0** com Java 17
- **Spring Data JPA** para acesso ao banco
- **Thymeleaf** para templates web
- **Bootstrap 5** para interface moderna
- **PostgreSQL** com configuração centralizada
- **Entidades JPA** com validações
- **Repositórios Spring Data**
- **Controllers web** com sessão
- **Páginas responsivas**
- **Dados iniciais** automáticos

## 🏃‍♂️ Como Executar

### 1. Via Maven (Recomendado)
```bash
# No diretório do projeto
mvn spring-boot:run
```

### 2. Via IDE (IntelliJ)
- Abra o projeto no IntelliJ
- Execute a classe `ECommerceApp.java`

### 3. Acesso
- **URL**: http://localhost:8080
- **Porta**: 8080
- **Context Path**: /

## 👥 Usuários Padrão

### Administrador:
- **Usuário**: `admin`
- **Senha**: `admin123`

### Cliente:
- **Usuário**: `cliente`
- **Senha**: `123456`

## 🗄️ Banco de Dados

- **Tipo**: PostgreSQL
- **Hospedagem**: Supabase
- **Database**: postgres
- **Conexão recomendada**: Transaction Pooler do Supabase

Configure por variáveis de ambiente:

- `SPRING_PROFILES_ACTIVE=supabase`
- `SUPABASE_DB_URL=jdbc:postgresql://SEU_HOST_POOLER:6543/postgres?sslmode=require&prepareThreshold=0`
- `SUPABASE_DB_USER=postgres.SEU_PROJECT_REF`
- `SUPABASE_DB_PASSWORD=<senha do banco Supabase>`

> ⚠️ **Importante**: Todos que usarem essa configuração acessam o mesmo banco online do Supabase.

## 📁 Nova Estrutura

```
src/main/java/com/ecommerce/
├── entity/          # Entidades JPA (Usuario, Produto, Pedido, ItemPedido)
├── repository/      # Repositórios Spring Data
├── controller/      # Controllers web
├── service/         # Serviços de negócio
├── model/           # Modelos originais (mantidos)
├── dao/             # DAOs originais (mantidos)
├── util/            # Utilitários
└── ECommerceApp.java # Classe principal Spring Boot

src/main/resources/
├── templates/       # Templates Thymeleaf
├── static/          # CSS, JS, imagens
└── application.properties # Configurações
```

## 🌟 Funcionalidades Web

### Para Todos:
- ✅ Página inicial com produtos em destaque
- ✅ Catálogo de produtos com busca e filtros
- ✅ Login e cadastro de usuários
- ✅ Interface responsiva (Bootstrap)

### Para Clientes Logados:
- 🔄 Carrinho de compras (em desenvolvimento)
- 🔄 Finalização de pedidos (em desenvolvimento)
- 🔄 Histórico de pedidos (em desenvolvimento)
- 🔄 Gerenciamento de perfil (em desenvolvimento)

### Para Administradores:
- 🔄 Gerenciamento de produtos (em desenvolvimento)
- 🔄 Gerenciamento de pedidos (em desenvolvimento)

## 🔧 Tecnologias Utilizadas

- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **Spring Web MVC**
- **Thymeleaf**
- **Bootstrap 5**
- **PostgreSQL**
- **Maven**
- **Java 17**

## 📋 Próximos Passos

1. **Implementar carrinho de compras** com sessão
2. **Criar páginas administrativas** para CRUD
3. **Adicionar Spring Security** para autenticação
4. **Implementar APIs REST** para AJAX
5. **Migrar funcionalidades** de PDF e QR Code
6. **Adicionar testes unitários**

## 🔄 Comparação: Antes vs Depois

| Aspecto | Antes (Swing) | Depois (Spring Boot) |
|---------|---------------|---------------------|
| Interface | Desktop (Swing) | Web (Thymeleaf + Bootstrap) |
| Banco | JDBC manual | Spring Data JPA |
| Arquitetura | MVC manual | Spring MVC |
| Sessão | SessionManager | HttpSession |
| Validação | Manual | Bean Validation |
| Dependências | JAR manual | Maven |
| Deploy | Executável local | Servidor web |

## 🎯 Vantagens da Migração

- ✅ **Interface moderna** e responsiva
- ✅ **Acesso via navegador** (qualquer dispositivo)
- ✅ **Manutenção simplificada** com Spring
- ✅ **Escalabilidade** para múltiplos usuários
- ✅ **Integração fácil** com outras tecnologias
- ✅ **Deploy em nuvem** facilitado

---

**Status**: ✅ Migração básica concluída - Sistema funcional!
**Próximo**: Implementar funcionalidades avançadas (carrinho, admin, etc.)
