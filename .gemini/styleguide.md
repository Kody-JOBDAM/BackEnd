# Kody-JOBDAM Style Guide

This document outlines the coding standards and best practices for the Kody-JOBDAM project, focusing on Spring Boot and JPA.

## 1. General Java & Clean Code
- **Naming:** 
  - Classes: `PascalCase` (e.g., `CommonService`)
  - Methods/Variables: `camelCase` (e.g., `createReservation`)
  - Constants: `SCREAMING_SNAKE_CASE` (e.g., `MAX_RETRY_COUNT`)
- **Indentation:** Use 4 spaces for indentation.
- **Lombok:** Use `@Getter`, `@Setter`, `@RequiredArgsConstructor` to reduce boilerplate code. Avoid `@Data` if possible to prevent unintended side effects in JPA entities.

## 2. Spring Boot
- **Controllers:**
  - Use `@RestController` for API endpoints.
  - Use constructor injection via `@RequiredArgsConstructor`.
  - Prefer `ResponseEntity<?>` or a specific DTO for return types to maintain consistency in responses.
  - Keep logic in controllers minimal; delegate to services.
- **Services:**
  - Mark with `@Service`.
  - Business logic should reside here.
  - Use `ResponseStatusException` for simple error handling or define custom exceptions.
- **DTOs:**
  - Use DTOs for request and response bodies.
  - Place DTOs in a `.dto` package relative to their domain.

## 3. JPA (Java Persistence API)
- **Entities:**
  - Use `@Entity` and `@Table`.
  - Always define a primary key with `@Id` and `@GeneratedValue(strategy = GenerationType.IDENTITY)`.
  - Use `@Enumerated(EnumType.STRING)` for Enum fields to ensure readability in the database.
  - Avoid using `@Setter` on all fields; provide specific methods for state changes if needed.
- **Repositories:**
  - Extend `JpaRepository<Entity, IdType>`.
  - Use Spring Data JPA's method naming convention for simple queries (e.g., `findByEmail`).
  - Use `@Query` for complex logic that cannot be expressed via method names.
- **Transaction Management:**
  - Use `@Transactional` (from `org.springframework.transaction.annotation`) on service methods that perform write operations.

## 4. Testing
- Use JUnit 5 for unit and integration tests.
- Mock dependencies using Mockito.
- Aim for high coverage on business logic within services.
