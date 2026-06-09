# 🚀 Como Rodar o E-Commerce Java

## 📋 Pré-requisitos

### 1. Java Development Kit (JDK)
- **JDK 8 ou superior** instalado
- Verificar: `java -version` e `javac -version`
- Download: https://www.oracle.com/java/technologies/downloads/

### 2. Banco de dados
- O projeto agora usa o PostgreSQL hospedado no **Supabase**
- Não é necessário instalar PostgreSQL local para rodar a versão Spring Boot

## ⚙️ Configuração do Banco de Dados

### 1. Verificar Configurações
O profile padrão é `supabase`, então configure as variáveis abaixo antes de rodar com o banco remoto.
Pegue a URL em **Supabase > Project Settings > Database > Connection string > Transaction pooler**.

- `SUPABASE_DB_URL`
- `SUPABASE_DB_USER`
- `SUPABASE_DB_PASSWORD`

Para testar sem Supabase, rode com `SPRING_PROFILES_ACTIVE=local`.

## 🔧 Executar o Projeto

### Opção 1: Scripts Automáticos (Windows)
```bash
# 1. Compilar
compile.bat

# 2. Executar
run.bat
```

### Opção 2: Comandos Manuais
```bash
# 1. Compilar
javac -d bin -cp "src;lib\postgresql-42.7.3.jar" src\main\java\com\ecommerce\**\*.java

# 2. Executar
java -cp "bin;lib\postgresql-42.7.3.jar" com.ecommerce.ECommerceApp
```

### Opção 3: Linux/Mac
```bash
# 1. Compilar
javac -d bin -cp "src:lib/postgresql-42.7.3.jar" src/main/java/com/ecommerce/**/*.java

# 2. Executar
java -cp "bin:lib/postgresql-42.7.3.jar" com.ecommerce.ECommerceApp
```

## 👥 Usuários Padrão

### Administrador
- **Usuário:** `admin`
- **Senha:** `admin123`

### Cliente
- **Usuário:** `cliente`
- **Senha:** `123456`

## 📁 Estrutura do Projeto
```
Projeto-e-commerce/
├── src/main/java/com/ecommerce/    # Código fonte
├── lib/                            # Bibliotecas (PostgreSQL)
├── bin/                            # Classes compiladas
├── pdfs/                           # Notas fiscais geradas
├── compile.bat                     # Script de compilação
├── run.bat                         # Script de execução
└── README.md                       # Documentação
```

## 🐛 Solução de Problemas

### Erro de Conexão com Banco
1. Confirmar se o projeto do Supabase está ativo
2. Confirmar usuário/senha no `application.properties`
3. Verificar se a URL termina com `?sslmode=require`

### Erro de Compilação
1. Verificar se JDK está instalado
2. Confirmar se `postgresql-42.7.3.jar` está na pasta `lib/`
3. Verificar sintaxe dos comandos

### Erro ao Executar
1. Compilar primeiro com `compile.bat`
2. Verificar se pasta `bin/` foi criada
3. Confirmar classpath nos comandos

## 📞 Suporte
Se houver problemas:
1. Verificar logs no console
2. Confirmar pré-requisitos
3. Testar conexão com banco separadamente

## 🎯 Funcionalidades
- ✅ Login de usuários (Admin/Cliente)
- ✅ Catálogo de produtos
- ✅ Carrinho de compras
- ✅ Finalização de pedidos
- ✅ Geração de nota fiscal PDF
- ✅ Pagamento PIX
- ✅ Histórico de pedidos
- ✅ Gerenciamento administrativo
