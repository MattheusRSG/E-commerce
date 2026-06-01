# 🚀 Como Rodar o E-Commerce Java

## 📋 Pré-requisitos

### 1. Java Development Kit (JDK)
- **JDK 8 ou superior** instalado
- Verificar: `java -version` e `javac -version`
- Download: https://www.oracle.com/java/technologies/downloads/

### 2. PostgreSQL
- **PostgreSQL 12 ou superior** instalado e rodando
- Porta padrão: **5432**
- Download: https://www.postgresql.org/download/

## ⚙️ Configuração do Banco de Dados

### 1. Criar o Banco
```sql
-- Conectar no PostgreSQL como superuser
CREATE DATABASE estilo_feminino;
```

### 2. Configurar Usuário (se necessário)
```sql
-- Criar usuário (opcional)
CREATE USER postgres WITH PASSWORD '010203040506070809';
GRANT ALL PRIVILEGES ON DATABASE estilo_feminino TO postgres;
```

### 3. Verificar Configurações
No arquivo `src/main/resources/application.properties`, as configurações padrão são:
- **URL:** `jdbc:postgresql://localhost:5432/estilo_feminino`
- **Usuário:** `postgres`
- **Senha:** `010203040506070809`

Também é possível sobrescrever por variável de ambiente:
- `DB_URL`
- `DB_USER`
- `DB_PASSWORD`

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
1. Verificar se PostgreSQL está rodando
2. Confirmar usuário/senha no `application.properties` ou variáveis `DB_*`
3. Verificar se o banco `estilo_feminino` existe

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
