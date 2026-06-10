# Autenticação JWT — Guia Didático

> Branch: `feature/auth-jwt`

---

## O que é JWT?

JWT (JSON Web Token) é um padrão para transmitir informações de forma segura entre sistemas.
O token é uma string dividida em 3 partes separadas por ponto (`.`):

```
HEADER.PAYLOAD.SIGNATURE
eyJhbGci...  .  eyJzdWIi...  .  SflKxwRJSMeKKF...
```

- **Header**: algoritmo de assinatura (ex: HS256)
- **Payload**: dados do usuário (username, expiração)
- **Signature**: garante que o token não foi alterado

---

## Fluxo de Autenticação (Login)

```mermaid
sequenceDiagram
    actor Usuario
    participant Frontend
    participant AuthController
    participant AuthenticationManager
    participant JwtUtil
    participant API

    Usuario->>Frontend: Informa usuário e senha
    Frontend->>AuthController: POST /api/auth/login
    AuthController->>AuthenticationManager: Valida credenciais
    AuthenticationManager-->>AuthController: Autenticado
    AuthController->>JwtUtil: generateToken(username)
    JwtUtil-->>AuthController: token JWT
    AuthController-->>Frontend: { "token": "eyJ..." }

    Note over Frontend: Armazena o token

    Frontend->>API: GET /api/pessoas (Authorization: Bearer token)
    API->>JwtAuthFilter: Intercepta a requisição
    JwtAuthFilter->>JwtUtil: isTokenValid(token, username)
    JwtUtil-->>JwtAuthFilter: válido
    JwtAuthFilter->>API: Libera acesso
    API-->>Frontend: Dados da resposta
```

---

## Fluxo de Registro

```mermaid
sequenceDiagram
    actor Usuario
    participant Frontend
    participant AuthController
    participant UsuarioService
    participant BancoDeDados

    Usuario->>Frontend: Informa usuário, senha e perfil
    Frontend->>AuthController: POST /api/auth/register
    AuthController->>UsuarioService: salvarUsuario(username, password, role)
    UsuarioService->>UsuarioService: BCrypt.encode(password)
    UsuarioService->>BancoDeDados: INSERT usuario
    BancoDeDados-->>UsuarioService: Usuario salvo
    UsuarioService-->>AuthController: Usuario
    AuthController-->>Frontend: { "message": "Usuário registrado com sucesso" }
```

---

## Diagrama de Classes

```mermaid
classDiagram
    class Usuario {
        +Long id
        +String username
        +String password
        +String role
    }

    class UsuarioRepository {
        +findByUsername(username) Optional~Usuario~
    }

    class UsuarioService {
        +loadUserByUsername(username) UserDetails
        +salvarUsuario(username, password, role) Usuario
    }

    class JwtUtil {
        -SecretKey key
        -long EXPIRATION
        +generateToken(username) String
        +extractUsername(token) String
        +isTokenValid(token, username) boolean
        -isTokenExpired(token) boolean
    }

    class JwtAuthFilter {
        +doFilterInternal(request, response, chain)
    }

    class SecurityConfig {
        +filterChain(http) SecurityFilterChain
        +passwordEncoder() PasswordEncoder
        +authenticationManager() AuthenticationManager
    }

    class AuthController {
        +login(body) Map
        +register(body) Map
    }

    UsuarioService --> UsuarioRepository
    UsuarioService --> Usuario
    JwtAuthFilter --> JwtUtil
    JwtAuthFilter --> UsuarioService
    AuthController --> JwtUtil
    AuthController --> UsuarioService
    SecurityConfig --> JwtAuthFilter
```

---

## Explicação linha a linha de cada arquivo

---

### pom.xml — Dependências

```xml
<dependency>spring-boot-starter-security</dependency>
```
Ativa o Spring Security no projeto — responsável por interceptar todas as requisições e aplicar regras de acesso.

```xml
<dependency>jjwt-api 0.12.5</dependency>
```
Interface pública da biblioteca JWT — contém as classes usadas no código (Jwts, Claims, etc).

```xml
<dependency>jjwt-impl 0.12.5 (runtime)</dependency>
```
Implementação interna do JWT — só é carregada em tempo de execução, não precisa estar no classpath de compilação.

```xml
<dependency>jjwt-jackson 0.12.5 (runtime)</dependency>
```
Permite que o JWT serialize/desserialize o payload usando a biblioteca Jackson (JSON).

---

### SecurityConfig.java

```java
@Configuration
```
Marca a classe como fonte de configurações do Spring. Os métodos anotados com `@Bean` serão gerenciados pelo Spring.

```java
.csrf(csrf -> csrf.disable())
```
Desabilita a proteção CSRF. Em APIs REST que usam JWT, não é necessária, pois não há cookies de sessão.

```java
.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
```
Define que o servidor **não guarda sessão**. Cada requisição deve trazer o token JWT — isso é chamado de arquitetura "stateless" (sem estado).

```java
.requestMatchers("/api/auth/**").permitAll()
```
Libera os endpoints de login e registro sem exigir autenticação — caso contrário, ninguém conseguiria nem se registrar.

```java
.anyRequest().authenticated()
```
Exige que **qualquer outra rota** só seja acessada por usuários autenticados com token válido.

```java
.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
```
Registra o filtro JWT para ser executado **antes** do filtro padrão de autenticação do Spring. Assim, cada requisição passa pelo filtro que valida o token.

```java
new BCryptPasswordEncoder()
```
BCrypt é um algoritmo de hash seguro para senhas. Ele adiciona "sal" aleatório e é propositalmente lento para dificultar ataques de força bruta.

---

### JwtUtil.java

```java
private final SecretKey key = Keys.hmacShaKeyFor("minha-chave-secreta-super-segura-32bytes!!".getBytes());
```
Gera a chave secreta usada para **assinar** e **verificar** tokens. Deve ter no mínimo 32 bytes para HS256. Em produção, essa chave deve vir de variável de ambiente.

```java
Jwts.builder()
    .subject(username)
    .issuedAt(new Date())
    .expiration(new Date(System.currentTimeMillis() + EXPIRATION))
    .signWith(key)
    .compact();
```
Constrói o token JWT:
- `subject`: identifica o usuário (quem é o dono do token)
- `issuedAt`: quando o token foi gerado
- `expiration`: quando o token expira (1 hora neste caso)
- `signWith`: assina o token com a chave secreta
- `compact`: gera a string final do token

```java
Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload()
```
Analisa e valida o token: verifica a assinatura com a chave secreta e retorna o payload (dados internos do token).

---

### JwtAuthFilter.java

```java
extends OncePerRequestFilter
```
Garante que o filtro seja executado **exatamente uma vez** por requisição HTTP.

```java
String authHeader = request.getHeader("Authorization");
```
Lê o cabeçalho `Authorization` da requisição. O valor esperado é `Bearer eyJ...`.

```java
jwt = authHeader.substring(7);
```
Remove o prefixo `"Bearer "` (7 caracteres) para obter somente o token.

```java
if (username != null && SecurityContextHolder.getContext().getAuthentication() == null)
```
Só tenta autenticar se: (1) o token tinha um username válido E (2) o usuário ainda não está autenticado no contexto atual.

```java
SecurityContextHolder.getContext().setAuthentication(authToken);
```
Registra o usuário como autenticado no contexto de segurança do Spring para esta requisição. A partir daqui, o Spring sabe quem está fazendo a chamada.

```java
filterChain.doFilter(request, response);
```
Passa a requisição para o próximo filtro da cadeia (obrigatório, caso contrário a requisição é bloqueada).

---

### UsuarioService.java

```java
implements UserDetailsService
```
Interface do Spring Security que obriga a implementar `loadUserByUsername`. O Spring chama esse método para buscar o usuário durante o processo de autenticação.

```java
User.builder()
    .username(usuario.getUsername())
    .password(usuario.getPassword())
    .roles(usuario.getRole())
    .build();
```
Cria o objeto `UserDetails` que o Spring Security entende — contém username, senha (já criptografada) e as permissões do usuário.

```java
passwordEncoder.encode(password)
```
Sempre que salvar um usuário, a senha é criptografada com BCrypt **antes** de ir ao banco. Jamais armazene senhas em texto puro.

---

### AuthController.java

```java
@PostMapping("/login")
```
Endpoint público de login. Recebe `username` e `password` no corpo da requisição.

```java
authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password))
```
Delega ao Spring Security a validação das credenciais — ele busca o usuário no banco (via `UsuarioService`) e compara as senhas.

```java
Map.of("token", token)
```
Retorna apenas o token JWT para o cliente. O frontend deve armazenar e enviar esse token em todas as requisições futuras.

```java
body.getOrDefault("role", "USER")
```
Se o campo `role` não for enviado, o usuário recebe o perfil padrão `USER`. Outros perfis possíveis: `ADMIN`, `PROFESSOR`, etc.

---

## Como testar

### 1. Registrar um usuário
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "joao",
  "password": "123456",
  "role": "USER"
}
```

### 2. Fazer login e obter o token
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "joao",
  "password": "123456"
}
```
Resposta:
```json
{ "token": "eyJhbGciOiJIUzI1NiJ9..." }
```

### 3. Acessar endpoint protegido
```http
GET /api/pessoas
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```
