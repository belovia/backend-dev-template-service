# TEST_SKILL — Java Test Writing Guide

Use this skill when adding or reviewing **unit** and **slice/integration** tests in this repository (Java 21, Spring Boot 3, Maven).

## Test naming convention

Name every test method using:

```text
WHEN_<methodUnderTest>_<actionWithData>_THEN_<expectedOutcome>
```

| Segment | Meaning | Examples |
|---------|---------|----------|
| `WHEN` | Fixed prefix | `WHEN_` |
| `<methodUnderTest>` | Method or use-case under test | `getCurrentWeather`, `login`, `register` |
| `<actionWithData>` | Input / scenario (camelCase, concise) | `validCoordinates`, `blankCity`, `latitudeOutOfRange` |
| `THEN` | Fixed separator | `THEN_` |
| `<expectedOutcome>` | Result or behavior | `returnsWeather`, `throwsIllegalArgument`, `returns401` |

**Examples**

```java
@Test
void WHEN_getCurrentWeather_validCoordinates_THEN_returnsWeather() { ... }

@Test
void WHEN_getCurrentWeather_latitudeOutOfRange_THEN_throwsIllegalArgument() { ... }

@Test
void WHEN_register_blankPassword_THEN_throwsValidationError() { ... }
```

Avoid generic names like `test1`, `shouldWork`, or `testGetWeather`.

---

## Test structure (mandatory sections)

Every test method must be organized with three comment blocks in this order:

```java
@Test
void WHEN_someMethod_someScenario_THEN_someOutcome() {
    // Arrange
    // ... setup data, mocks, test context

    // Action
    // ... invoke the method under test (single logical action)

    // Asserts
    // ... assertions on result, exceptions, interactions
}
```

Rules:

- **Arrange** — only preparation (mocks, fixtures, `given`, building requests). No calls to the system under test.
- **Action** — one clear invocation (or one HTTP call in slice tests).
- **Asserts** — all verifications (`assert*`, `verify`, `assertThrows`, status codes).

Keep each section focused; do not mix arrangement with assertions.

---

## Test stack (preferred order)

### Default: unit / slice tests (preferred)

Use this combination for most tests:

| Annotation / extension | Role |
|------------------------|------|
| `@ExtendWith(SpringExtension.class)` | Spring TestContext (JUnit 5) |
| `@ContextConfiguration(classes = { ... })` | Minimal Spring context (only required `@Configuration` / `@Bean` classes) |
| `@TestPropertySource(properties = { ... })` or `locations = "..."` | Override config for the test |
| `@ExtendWith(MockitoExtension.class)` | Mockito `@Mock` / `@InjectMocks` when not using full Spring for the unit |

**Example (unit with Mockito only)**

```java
@ExtendWith(MockitoExtension.class)
class WeatherServiceTest {

    @Mock
    private WeatherClient weatherClient;

    @InjectMocks
    private WeatherService weatherService;

    @Test
    void WHEN_getCurrentWeather_validCoordinates_THEN_returnsWeather() {
        // Arrange
        CurrentWeather expected = new CurrentWeather(/* ... */);
        when(weatherClient.getCurrentWeather(55.75, 37.62)).thenReturn(Optional.of(expected));

        // Action
        CurrentWeather actual = weatherService.getCurrentWeather(55.75, 37.62);

        // Asserts
        assertEquals(expected, actual);
    }
}
```

**Example (slice with Spring + properties)**

```java
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { AppConfig.class, SecurityConfig.class })
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:test",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class AuthServiceSliceTest {
    // ...
}
```

Prefer **narrow** `@ContextConfiguration` — include only classes needed for the test, not the full application.

### Integration tests (use sparingly)

Use `@SpringBootTest` only when you need:

- Full application context bootstrap
- Real auto-configuration wiring end-to-end
- Random port / `TestRestTemplate` / full stack

For this repository, **default to** `@ExtendWith(SpringExtension.class)` + `@ContextConfiguration` + `@TestPropertySource` instead of `@SpringBootTest`.

When `@SpringBootTest` is required:

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = { "..." })
class ApplicationIntegrationTest {
    // ...
}
```

Tag heavy tests (e.g. `@Tag("integration")`) and keep them few.

---

## What to test (coverage by business logic)

Do **not** write only happy-path tests. Derive cases from **requirements, validation rules, and branches** in the code under test.

### 1. Positive (happy path)

- Valid inputs → expected return value / side effect
- Boundary values that are **allowed** (e.g. min/max latitude -90/90, forecast `days = 1` and `days = 16`)

### 2. Empty / missing data

- `null` arguments where applicable
- Blank strings (`""`, `"   "`)
- Empty collections / optional empty
- Missing required fields on DTOs (validation layer)

### 3. Invalid data (constraints)

- Out-of-range numbers (e.g. latitude `100`, longitude `200`)
- Wrong types or formats if validated
- Business rules: “city not found”, “user already exists”, unauthorized access

### 4. Error / edge behavior

- Dependencies return empty (`Optional.empty()`)
- External API failure (mock to return empty or throw)
- Security: unauthenticated vs authenticated (`/api/v1/auth/me`)

### 5. Interactions (when using mocks)

- `verify(mock, times(1)).save(...)` when persistence is expected
- `verify(mock, never()).save(...)` when validation fails early

### Checklist per public method

Before finishing a test class, confirm:

- [ ] At least one **positive** case
- [ ] **Empty/null/blank** inputs if the method accepts strings or objects
- [ ] **Invalid** inputs for each documented constraint
- [ ] **Not-found / failure** path if the method uses `Optional` or throws
- [ ] Naming follows `WHEN_..._THEN_...`
- [ ] Sections `// Arrange`, `// Action`, `// Asserts` are present

---

## Assertions and libraries

- JUnit 5 (`org.junit.jupiter.api.*`)
- AssertJ or JUnit assertions (`assertEquals`, `assertThrows`, `assertThat`)
- Mockito: `when`, `verify`, `@Mock`, `@InjectMocks`
- Spring Test: `MockMvc` for controller slice tests when appropriate

```java
// Asserts
assertThrows(IllegalArgumentException.class, () -> service.getCurrentWeather(100, 0));
verify(userRepository, never()).save(any());
```

---

## Module layout

| Module | Test location | Notes |
|--------|---------------|--------|
| `service` | `service/src/test/java/...` | Controllers, security, JPA — prefer slice + mocks |
| `weather-mcp-server` | `weather-mcp-server/src/test/java/...` | `WeatherService` unit tests; MCP tools via mocked `WeatherService` |

Mirror package structure of `src/main/java` under `src/test/java`.

---

## Anti-patterns (avoid)

- Testing only `main` or trivial getters with no logic
- `@SpringBootTest` for every test class
- Multiple unrelated actions in one test
- Missing `THEN_` outcome in the method name
- Assertions in the Arrange section
- Ignoring validation and error branches in business services

---

## Quick template (copy-paste)

```java
@ExtendWith(MockitoExtension.class)
class MyServiceTest {

    @Mock
    private MyPort myPort;

    @InjectMocks
    private MyService myService;

    @Test
    void WHEN_doSomething_validInput_THEN_returnsExpected() {
        // Arrange
        // ...

        // Action
        // var result = myService.doSomething(...);

        // Asserts
        // assertEquals(...);
    }

    @Test
    void WHEN_doSomething_invalidInput_THEN_throwsException() {
        // Arrange
        // ...

        // Action & Asserts
        // assertThrows(SomeException.class, () -> myService.doSomething(...));
    }
}
```

```java
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { /* minimal config */ })
@TestPropertySource(properties = { /* overrides */ })
class MySliceTest {
    // @Autowired beans; MockMvc if needed
}
```

---

## References in this repo

- Example unit test: `weather-mcp-server/src/test/java/.../WeatherServiceTest.java`
- Run tests: `mvn test` or `mvn -pl <module> test`
