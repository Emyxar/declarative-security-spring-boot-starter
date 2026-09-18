# Декларативный Security Starter

Spring Boot автоконфигурация для декларативной настройки Spring Security через `application.yml`: правила доступа к эндпоинтам, CORS и формат JSON-ответов на ошибки `401`/`403` задаются конфигом, без написания `SecurityFilterChain` в каждом сервисе.

## Что делает стартер

При подключении зависимости и наличии на classpath web-стека автоконфигурация:

- строит `SecurityFilterChain` со STATELESS-сессиями, отключёнными CSRF, `formLogin` и `httpBasic`;
- транслирует список `security.config.rules` в `authorizeHttpRequests` (открытые эндпоинты / требующие любой аутентификации / требующие конкретную роль);
- настраивает CORS из `security.config.allowed-origins` / `allowed-methods` / `allowed-headers` / `allow-credentials`;
- отдаёт единый JSON на `401` (`AuthenticationEntryPoint`) и `403` (`AccessDeniedHandler`) с текстом сообщений из конфига;
- добавляет свой фильтр `StarterTokenFilter` перед `UsernamePasswordAuthenticationFilter` — точку расширения для проверки токена.

Все бины регистрируются как `@ConditionalOnMissingBean`, поэтому любой из них можно переопределить в приложении-потребителе своим `@Bean` того же типа.

## Подключение

```xml
<dependency>
    <groupId>ru.emyxar</groupId>
    <artifactId>declarative-security-spring-boot-starter</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

Требования у потребителя:

- Java 21+;
- `spring-boot-starter-web` на classpath (автоконфигурация — сервлетная, для WebFlux не сработает);
- Spring Boot 4.1.x.

После подключения выполните `mvn clean install` в модуле стартера — иначе потребитель продолжит использовать старую версию jar из локального `.m2`.

## Конфигурация

Весь конфиг живёт под префиксом `security.config`.

### Правила доступа

```yaml
security:
  config:
    rules:
      # Открытый эндпоинт
      - endpoint: /hello
        authenticated: false

      # Нужна любая аутентификация
      - endpoint: /api/profile/**
        authenticated: true

      # Нужна конкретная роль
      - endpoint: /api/admin/**
        role: ADMIN
```

Правила применяются в порядке объявления. Всё, что не попало ни под одно правило, по умолчанию требует аутентификации (`anyRequest().authenticated()`).

Формат `role`: без префикса `ROLE_` — например `ADMIN`, а не `ROLE_ADMIN`. Значение проверяется regex-паттерном `^(?!ROLE_)[A-Z0-9_]+$`; префикс добавляет сам Spring Security при вызове `hasRole()`.

### Сообщения об ошибках

```yaml
security:
  config:
    errors:
      access-denied-message: "Недостаточно прав для выполнения операции"
      unauthorized-message: "Требуется авторизация"
```

Если не заданы — используются значения по умолчанию: «Доступ запрещён» и «Требуется аутентификация».

Формат ответа:

```json
{
  "status": "FORBIDDEN",
  "message": "Недостаточно прав для выполнения операции"
}
```

### CORS

```yaml
security:
  config:
    allowed-origins:
      - "https://example.com"
    allowed-methods:
      - GET
      - POST
      - PUT
      - PATCH
      - DELETE
      - OPTIONS
    allowed-headers:
      - Authorization
      - Content-Type
      - X-Requested-With
    allow-credentials: true
```

**Важно:** если `allow-credentials: true`, `allowed-origins` не может содержать `"*"` — это проверяется валидацией на старте (`@AssertTrue`) и дополнительно упадёт в рантайме при первом кросс-доменном запросе, так как `CorsConfiguration` не допускает такую комбинацию. Для случая с credentials перечисляйте домены явно.

## Точки расширения

### Формат ответа об ошибке

По умолчанию используется `DefaultSecurityErrorResponse`, отдающий `{status, message}`. Чтобы поменять формат — например, добавить `path` и `timestamp`, — задайте свой бин:

```java
@Bean
public StarterSecurityErrorResponseFactory securityErrorResponseFactory() {
    return (status, message, request, exception) -> Map.of(
            "status", status,
            "message", message,
            "path", request.getRequestURI(),
            "timestamp", Instant.now().toString()
    );
}

// Или если есть готовый стурктурированный DTO класс
@Bean
StarterSecurityErrorResponseFactory securityErrorResponseFactory() {
    return (status, message, request, exception) ->
            new UnauthorizedResponse(false, null, new ApiError(status, message), OffsetDateTime.now());
}
```

Он будет использован вместо стандартного благодаря `@ConditionalOnMissingBean`.

### Проверка токена

`StarterTokenFilter` в текущей версии — заглушка, пропускающая все запросы дальше по цепочке. Чтобы подключить реальную проверку (JWT и т.п.), задайте свой бин `StarterTokenFilter` (или наследника) в приложении-потребителе — он заменит стандартный фильтр в цепочке.

### Полная замена цепочки безопасности

Если нужно собрать `SecurityFilterChain` самостоятельно — просто объявите свой бин этого типа; автоконфигурация не станет создавать свой благодаря `@ConditionalOnMissingBean(SecurityFilterChain.class)`.

## Проверка, что стартер подключился

1. В логе при старте должна появиться строка `Security starter properties loaded` — она выводится из `@PostConstruct` в `SecurityProperties` и подтверждает, что конфигурация была забиндена.
2. Запрос к защищённому эндпоинту без токена должен возвращать JSON-тело вида `{"status": "SC_UNAUTHORIZED", ...}`, а не стандартную форму логина Spring Boot.

Если ни того ни другого не происходит — автоконфигурация не подхватилась. Проверьте:

- наличие файла `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` с полным именем класса `SecurityAutoConfiguration` внутри jar стартера;
- что `mvn install` для стартера выполнялся после последних изменений в коде.

## Известные ограничения

- Работает только со servlet-стеком (`spring-mvc`), для WebFlux не подходит.
- `StarterTokenFilter` не содержит логики проверки токена «из коробки» — это точка расширения, а не готовое решение.
- Правила проверяются в порядке объявления списком, без приоритетов и без поддержки нескольких ролей на один паттерн одновременно.