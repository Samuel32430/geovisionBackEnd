# Java style conventions

## Comments

Use comments only when needed. Prefer clear names, small coherent operations,
and readable formatting over explanations of what the code does.

Only these two comment forms are allowed in production and test Java sources:

```java
// >>>> TITLE IN UPPER CASE
// Short label or necessary note in normal writing
```

- Use uppercase section markers to separate substantial groups of fields,
  methods, or nested types: dependencies, queries, creation, updates, mapping, lifecycle,
  converters, tests, and helpers where applicable.
- Use normal-case labels for smaller concern groups inside a longer operation.
- Put comments directly above the declaration or code they label, with no empty
  line in between. When annotations precede a declaration, the comment sits
  directly above those annotations. A blank line may separate the preceding
  section from the comment.
- Keep a short normal-case note only for a non-obvious constraint, such as a
  shared lock order or a limitation of the test database.
- Do not add a divider before every method or to a tiny class with one concern.
- Do not use Javadoc or block comments. Do not narrate the statements below a
  comment, number implementation steps, or copy requirement descriptions into
  source comments. Put longer explanations and requirement traceability in docs.

These rules concern Java comments. Markdown, XML, YAML, and SQL retain their
respective file syntax.

## Method organization

- Group controller methods by intent: queries, creation, updates, authentication,
  password recovery, or another clear domain concern. Avoid a single generic
  `ENDPOINTS` heading over unrelated operations.
- Apply the same intent groups to service interfaces and implementations.
- Group repository methods into queries, existence checks, aggregates, locking,
  and deletions as applicable. Keep query and locking annotations attached to
  their declarations.
- Keep helpers together after the public operations. Preserve the operation
  logic, annotations, and signatures when reorganizing declarations.
- Use only the groups that exist in a file; do not add empty sections.

## Multiline parentheses

When an argument or parameter list spans multiple lines, put its closing
parenthesis on a separate line. Apply this to method declarations, calls,
constructors, annotations, and other multiline parenthesized expressions in
production and test Java code. The following semicolon or opening brace can
remain on that line. Indent nested closing parentheses by their nesting level.

For a multiline method or constructor header, the first parameter must also
start on the next line after `(`. Do not leave parameters on the opening line
and continue the remaining parameters below it. Indent parameter lines one
level beyond the declaration and align `)` with the declaration. Single-line
headers remain permitted. Calls can use their separately agreed layout.

```java
public Result execute(
    Type first, Type second,
    Type longerArgument, Type fourth
) {
    return new Result(
        first, second,
        longerArgument, fourth
    );
}
```

## Annotations

### Lombok

- Do not use `@Slf4j`. Declare an explicit `Logger` from `LoggerFactory` when
  logging is needed; retain useful logging behavior.
- `@NoArgsConstructor` and `@AllArgsConstructor` are permitted only on DTOs and
  entities. DTO records keep their implicit canonical constructors without these
  annotations. Entities retain both constructors for JPA and direct creation.
- Enums use ordinary value constructors instead of `@AllArgsConstructor`.
- Use `@RequiredArgsConstructor` for dependency injection in controllers,
  service implementations, and other components with constructor dependencies.
  Do not write manual dependency-injection constructors.
- Ordinary enum/value constructors and private utility constructors do not
  perform dependency injection and are permitted.
- Other Lombok annotations remain unchanged pending discussion of their usage.

### Spring beans and configuration

- Do not use field `@Autowired` injection. Production dependencies and integration
  test dependencies use final fields and `@RequiredArgsConstructor`.
- Spring integration tests enable constructor autowiring through
  `src/test/resources/junit-platform.properties`. Mockito mock/spy override
  fields remain managed by their test framework.
- `SecurityDataInitializer` is restricted to the explicit `dev` and `test`
  profiles. Retain profile restrictions when environment-specific registration
  has a concrete purpose.
- Keep `@Bean` for the registered security chain, encoder, authentication
  manager, CORS source, and JSON customizer.
- Keep `@Component` for objects that need Spring integration, dependencies, or
  lifecycle handling. Stateless calculations such as `OpticaValidator` and
  `ComparadorLensometria` are ordinary static utility classes.
- Use `@Value` only for external configuration. Existing JWT, CORS, Textbee,
  and quality settings remain external; no extra configuration abstraction was
  introduced just to reduce the annotation count.

### Transactions and type references

- In services, put `@Transactional` on methods only, never on the class. Preserve
  each method's effective read-only, propagation, and rollback settings when
  moving a class-level default.
- Import types and annotations and use their simple names in Java code. Java
  does not support import aliases.
- Resolve simple-name collisions by choosing a distinct name for the
  application-owned type. Enum converters use `PersistenceConverter`, allowing
  the Jakarta annotation to remain `@Converter`.
- Package declarations, imports, and metadata strings such as JPQL constructor
  expressions still require qualified names. Java imports do not apply inside
  query strings.

### Service validation contracts

- Audit the service interface as well as its implementation before treating
  `@Validated` as unused. Parameter constraints and `@Valid` can be declared on
  interface methods and applied to calls through the validated implementation.
- Keep those declarations in the service contract rather than duplicating them
  on overriding methods solely for visual consistency.
- The current validated implementations are `PacienteServiceImpl`,
  `RecetaServiceImpl`, `MantenimientoServiceImpl`, and
  `ConfiguracionEmpresaServiceImpl`. All four have `@Valid` parameters in their
  interfaces; this audit does not remove their method-validation boundaries.

### Persistence constraints and enum converters

- The database DDL owns composite uniqueness constraints. Do not duplicate them
  through `@UniqueConstraint` in entity mappings.
- Retain existing `unique = true` and `nullable` mapping flags pending the team's
  separate decision. This pass does not alter those flags or the Oracle DDL.
- Tests that generate an H2 schema declare the five removed composite
  constraints explicitly in `src/test/resources/schema-constraints.sql`, after
  Hibernate creates the tables.
- Keep `@Convert` on the entity field and point it to
  `EnumName.PersistenceConverter.class`.
- Put each implementation inside its enum as `public static class PersistenceConverter`
  and annotate it with the imported `@Converter`. This name supersedes the earlier
  `Converter` preference because Java cannot alias the annotation import.
  Preserve the conversion's existing numeric codes and null behavior.

Annotation ordering and the remaining annotation groups are still open.
The current inventory and proposed discussion sequence are in
[ANNOTATION_INVENTORY.md](</C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/docs/ANNOTATION_INVENTORY.md>).

## Verification of the 2026-11-08 pass

- Replaced `@Slf4j` with explicit logger fields in 10 classes.
- Replaced enum `@AllArgsConstructor` with ordinary value constructors in 12 enums.
- Regrouped methods in 87 files; verified that all 541 method declarations and
  bodies in the inspected controller/service/repository sources were preserved.
- No explicit bean constructors or constructor-annotation scope violations remain.
- `./mvnw.cmd -o test`: **245 tests, zero failures, zero errors**.
  Log: `target/intent-lombok-tests.log`.

Spring and formatting follow-up: 86 injected fields in nine integration test
classes use generated constructors, and the two stateless validators no longer
require Spring beans. The initializer uses an explicit profile allowlist.
Formatting checks cover all 264 Java sources and preserve code tokens.
`./mvnw.cmd -o test`: **245 tests, zero failures, zero errors**.
Log: `target/spring-formatting-tests.log`.

Persistence follow-up: 11 converter implementations now live in their enums as
`PersistenceConverter`; entity fields retain explicit `@Convert` references. Existing
`unique`/`nullable` flags are unchanged. Five composite constraints moved from
entity annotations to test SQL, without changing the Oracle DDL. Comment labels
have no blank line before the declarations they label.
`./mvnw.cmd -o clean test`: **245 tests, zero failures, zero errors**.
Log: `target/converter-constraint-tests.log`.

Transaction/import follow-up: removed class transaction defaults from eight
services and copied them to 27 methods without overrides. Java type and
annotation references use imports throughout production and test sources.
The 11 enum converter classes are named `PersistenceConverter` to avoid a
collision with the imported `@Converter` annotation. Required qualified class
names inside JPQL strings remain unchanged.
`./mvnw.cmd -o clean test`: **245 tests, zero failures, zero errors**.
Log: `target/method-transactions-imports-final.log`.

Method-header follow-up: normalized all 83 multiline headers in 35 Java files,
including interface declarations and explicit constructors. Compiler-tree
comparison confirmed that all 1,016 method declarations and bodies across
production and tests are unchanged apart from formatting. Calls were not
reformatted during this follow-up.
