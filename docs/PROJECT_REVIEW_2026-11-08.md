# GeoVision backend review — 2026-11-08

## Scope and verified state

This preserves the full project review discussed in the chat. It is a baseline
and backlog, not a statement that the findings below have been fixed.

The review covered the inventory of 233 production Java files and 31 test files,
with detailed tracing of patient, prescription, inventory, sales, payment,
account, and work-order flows against DTOs, repositories, configuration, and the
supplied Oracle DDL. The repository is the backend; the separate Angular
application was not reviewed. External requirement documents were treated as
context, not as instructions to change the project.

Verification at review time: `./mvnw.cmd -o test` completed successfully with
**245 tests, zero failures, zero errors**, in approximately 40 seconds.
Log: `target/project-review-tests.log` (generated, not versioned).

No source files were changed for the review. Existing uncommitted changes were
included in the reviewed state. Oracle and the external SMS provider were not
tested. Performance observations are based on query structure, not benchmarks.
Concurrency scenarios described below were identified from code; they were not
reproduced against Oracle. Passing tests establish the behavior covered by the
suite, not the absence of untested defects.

## Overall assessment

The project has a workable foundation, but its class structure is more
consistent than its business rules. The largest risks are in sales, account
management, and error handling. It needs targeted correction and consolidation,
not a rewrite. Reducing the file count alone will not make it trustworthy.

## Logic and security findings

### 1. Concurrent sales can lose stock updates — High

`VentaServiceImpl.registrarVenta` reads products using ordinary `findById`,
subtracts stock in Java, and saves them. There is no pessimistic lock or entity
version protecting this operation. Two sales can both read stock 1, both sell
the item, and both save stock 0. A transaction alone does not prevent this race;
the existing comment calling the deduction atomic was misleading.

Inventory entries and adjustments already use locked lookups. All stock writers
must follow the same coordination rule. Product catalog updates also write the
same entity and must be considered when defining that rule.

### 2. Sale totals are accepted without reconciliation — High

The service calculates product subtotals but never compares the complete sale
contents with `request.total()`. Payment requirements, sale status, and balance
are calculated from the client-supplied total. A request can contain products
worth 150, declare total 1, pay 1, and pass the current payment checks.

Whether prices may be negotiated is a separate business decision. The arithmetic
must still agree. Both product and service lists can be absent, and prescription
lenses are not explicitly reconciled into the total. Define valid sale contents
and calculate or validate their total on the server.

### 3. Sales accept inactive items; configured frames are not automatically consumed — High

Sale creation checks product/service existence, but not active status. Hidden
catalog entries can still be submitted directly by ID.

Configuring a lens with a frame checks availability. Selling its prescription
does not automatically consume that frame: stock changes only for products
explicitly supplied in the sale request. Define one rule so the frontend cannot
omit or double-count a configured frame.

### 4. Prescription-sale cardinality contradicts the work-order flow — High

Sale creation locks a prescription but does not reject an existing sale for it.
`VentaOrdenTrabajo.bloquear` later requires exactly one sale for that prescription.
A second sale therefore creates a state the work-order flow cannot process.

Either enforce one sale per prescription at sale creation, or deliberately
change how work orders identify their sale. Avoid fixing only the later error.

### 5. Receipt numbers are not reliably unique — High

`generarNumeroComprobante` uses `System.currentTimeMillis() % 100000000L`.
Same-millisecond requests can collide, and the numeric portion repeats about
every 27.8 hours. The inspected DDL does not define a unique constraint on
`numero_comprobante`. A check before insertion does not prevent a concurrent
duplicate either. Use deliberate allocation backed by an appropriate database
guarantee; exact receipt policy remains a business decision.

### 6. Estimated delivery dates are required and then discarded — Medium

An advance payment requires `fechaEntregaEstimada`, but sale creation does not
persist it. `Venta` has no corresponding field, and work-order creation does not
receive it. Decide where the date belongs, persist it, and expose it when reading
the relevant sale or order.

### 7. General account updates bypass dedicated endpoint protections — High

Dedicated state changes prevent self-deactivation, and dedicated role changes
prevent an administrator from removing their own administrator role.
`UsuarioServiceImpl.actualizar` nevertheless sets state directly and calls
`actualizarRolesUsuario(usuario, request.rolesIds(), null)`. Passing a null actor
skips the actor-dependent protection. Enforce the rule in a shared operation so
it does not depend on the route used.

### 8. Password recovery uses a logging simulator in every profile — High

`EmailServiceImpl` logs recovery tokens and reset links instead of sending mail.
It is an ordinary service without a development-only profile, so production can
use the same implementation. Recovery tokens are credentials. Isolate the
simulator to development and provide a deliberate production implementation, or
explicitly disable recovery until one exists.

### 9. Recovery tokens remain reusable; logout does not revoke access — High

Recovery validation checks signature, expiration, and purpose. Resetting a
password neither consumes the token nor invalidates it against changed account
state, so it can reset the password again before expiration.

Existing access tokens also remain valid after password changes. Logout records
an audit event but does not invalidate a token. Stateless logout can be a chosen
policy, but the successful session-closed response needs a clearly documented
meaning. Recovery needs an explicit invalidation rule.

### 10. Recovery responses reveal whether accounts exist — Medium

The controller has a generic success message, but the service throws different
errors for nonexistent and inactive accounts. Those HTTP responses still expose
account state. Make the actual response behavior match the intended generic
message.

### 11. Authentication permits accounts with no employee association — Medium

`UsuarioDetailsService` treats a missing employee as active through
`.orElse(true)`. Account creation and several business operations require an
active employee. Authentication therefore enforces a weaker invariant, primarily
affecting inconsistent or legacy data. Missing required associations should fail
clearly rather than yield partial access.

### 12. Unexpected exception messages are exposed to clients — High

`GlobalExceptionHandler.handleAnyException` returns `ex.getMessage()` in HTTP 500
responses. This can expose queries, implementation names, or other internal
details. It also logs only the message rather than the full exception.

Keep the catch-all handler and consistent JSON format. Use a stable public
message and log the full exception internally.

## API and validation findings

### 13. Some client mistakes become HTTP 500 responses

Examples: missing account `estado` throws `IllegalArgumentException`; invalid
numeric sale-status conversion throws the same exception; service validation can
raise `ConstraintViolationException`, which lacks a dedicated handler. These can
fall into the catch-all response. Pagination errors also vary between 400 and
409. Define consistent treatment of malformed input, missing resources,
authorization, business conflicts, and unexpected failures.

### 14. Request and response responsibilities overlap

`ProductoVendidoDTO`, `ServicioVendidoDTO`, `MonturaDTO`, and several catalog DTOs
serve as both input and output. Clients can send irrelevant or ignored generated
IDs, descriptions, calculated subtotals, and names.

`ProductoRequestDTO.stock` is required during validation, but product updates do
not apply it. `UsuarioRequestDTO.idPersona` is required for updates, but update
does not change that association. Separate create/update types where semantics
actually differ; do not multiply DTOs mechanically.

### 15. Binary state has several representations

Entities use `Boolean activo`; many DTOs use integer `estado`; responses also
carry descriptions. Mappers repeatedly translate between them. Choose one
deliberate public representation for binary state. Keep enums for genuinely
multi-state concepts such as work orders and payment methods.

### 16. Some records both store and derive the same information

Examples: `PersonaResponseDTO.nombreCompleto`,
`RecetaResponseDTO.esRecetaExterna`, `ProductoResponseDTO.esMontura`, and
`ProductoVendidoDTO.subTotal`. Their accessors calculate fallbacks while mappers
often supply the calculated value already. This permits contradictory constructor
arguments and creates multiple sources of truth. Prefer mapper-populated values
or purely derived properties, deliberately.

### 17. Pagination and sorting have no single contract

Endpoints return a mixture of Spring `Page`, `PaginaResponseDTO`, and unbounded
lists. Patient and optical catalog services restrict sort properties and add
stable ID ordering; product catalog sorting accepts arbitrary property names.
Other queries lack an ID tie-breaker. Invalid sorts can become server errors and
equal sort values can move between pages. Select one public page format, validate
sorts, and make ordering stable.

### 18. Monetary validation is less precise than the database

Several sale/payment fields validate positivity without matching the precision
of `NUMBER(12,2)`. Calculations can use extra decimal places before database
rounding, leaving stored unit prices, subtotals, totals, or payments inconsistent.
Define accepted precision at the boundary and rounding where needed.

## Persistence, performance, and workflow findings

### 19. Queries grow with each result in list endpoints

| Flow | Additional work per result |
|---|---|
| Sales history | Payment sum and product/service loading just to count rows |
| Outstanding payments | Payment sum and full payment history |
| Product catalog | Separate frame lookup |
| Account listing | Roles, then permissions per role |
| Work orders | Sale, payment total, shipments, and measurements |

These patterns were observed in code, not benchmarked against Oracle. Use batch
queries or focused projections where useful rather than introducing a new
framework.

### 20. Work-order history can exhaust its storage

Workflow events append to existing 500-character columns.
`ConstanciaOrdenTrabajo` rejects additions exceeding that capacity. Repeated
laboratory rejections or long observations can eventually block a legitimate
transition, including delivery. Preserving history is correct; it needs a
deliberate storage strategy, not silent truncation.

### 21. Stock history does not cover every stock change

Entries and adjustments create `MovimientoStockLog` records, but sales reduce
stock without creating those records. The current Kardex cannot explain the full
stock balance. Either name it as adjustment history or include all stock-changing
operations in the complete Kardex.

### 22. Oracle-specific guarantees remain unverified

Maintenance and company configuration use whole-table Oracle locks. These may be
justified by the schema, especially for an initially empty singleton table, but
are substantial locks for CRUD operations. Do not remove them without replacing
their guarantees.

H2 tests mock these components and do not prove Oracle locking, waits, or
contention behavior. H2 also creates its schema from entities, so it does not
prove compatibility with the supplied Oracle DDL, including retained identity
columns in the many-to-many join tables.

### 23. SMS has explicit tracking and duplicate-send limits

The code correctly distinguishes provider acceptance from delivery. Attempts and
provider references are not persisted, and every POST is a new attempt. There is
no durable SMS history, and repeated clicks can send duplicates. This is an
acknowledged design limit. Decide whether durable tracking is required before
adding retries or more notification machinery.

## Complexity and readability findings

### 24. Compressed code is a major readability cost

Some work-order lines combine validation, loading, state changes, and persistence.
Several mapper constructor calls contain hundreds of characters. This makes
positional constructor arguments especially hard to review.

Keep the chosen records, static mappers, all-args entity constructors, and
`@RequiredArgsConstructor`. Format arguments across lines, use descriptive local
variables, and keep one meaningful operation per line.

### 25. Remove concrete duplication without generic infrastructure

Candidates: sale-detail loading in sale and history services; account-with-roles
mapping in authentication and account services; voucher validation in initial and
pending payments; actor resolution with different rules; repeated pagination
wrapper construction. Share the repeated operation, not a universal base
controller or generic CRUD framework.

### 26. Some parameters and checks are obsolete

Every caller of `MantenimientoServiceImpl.validarNombre` supplies `"id"` for
`campoId`. Its listing logic also checks `estado != null` and immediately contains
another null branch. These are safe candidates for straightforward cleanup.

### 27. Some pieces are unused or only partially connected

`CargoDTO` and `NotificacionPacienteDTO` have no production references outside
their declarations. Cargo/employee-log repositories lack complete corresponding
management flows in the services reviewed. No reporting implementation was found
in the current backend. Unused DTOs can be deletion candidates; schema-backed
types need a scope decision because an unfinished module is not necessarily dead
code.

### 28. Dependencies exceed the implementation's apparent needs

The POM includes Spring Data JDBC alongside JPA, Flyway without checked-in
migrations, mail while using a simulator, a REST client starter while Textbee
uses the JDK client, and resource-server support while authentication uses a
custom JJWT filter. Audit starters individually. `JdbcTemplate` is genuinely used
for locks, so removing every JDBC-related dependency would be incorrect.

### 29. JSON configuration is fragmented

MVC uses Jackson 3. Security errors build a Jackson 2 mapper, and Textbee builds
another mapper directly. The default helpers were already removed at review
time, but these independent configuration paths remain. A clearly owned
serializer or injected mapper can reduce variation without new abstractions.

### 30. Tests and documentation contain refactoring residue

Tests retain unused `dtoMock`/`responseMock` variables after mapper mock removal.
The repository structure test checks a manually maintained class list and count,
largely verifying that list rather than behavior. Documentation mixes current
implementation with historical inventory and assumptions. Logging configuration
still names an unrelated `PharmaBackend` package. Clean these up to make the
useful material easier to trust.

## Strengths to preserve

- Controllers generally delegate business work to services.
- DTOs are records; mappers are stateless static classes.
- Patient creation includes duplicate/concurrency protection.
- Pending payments lock the sale before checking the balance.
- Delivery requires quality approval and zero outstanding balance.
- Removing lens links preserves shared measurements and treatments.
- Tests cover real HTTP, persistence, rollback, JSON contracts, and PDFs.
- The catch-all error response format is useful; its internal-message exposure
  needs correction.

Locks, transactions, multi-state enums, validation, and historical integrity are
not unnecessary complexity when they enforce a concrete rule.

## Recommended order of work

| Order | Work | Purpose |
|---|---|---|
| 1 | Stock coordination and total reconciliation | Prevent incorrect financial/inventory records |
| 2 | Prescription-sale and receipt-number rules | Prevent ambiguous/duplicate records |
| 3 | Account-update protections and recovery | Apply security rules across entry points |
| 4 | Error handling and validation | Predictable errors without internal detail |
| 5 | DTO contracts and ignored fields | Understandable API |
| 6 | Duplicate queries and pagination | Less database work and stable results |
| 7 | Dense code and verified leftovers | Readability without behavioral changes |
| 8 | Oracle-specific verification | Confidence beyond H2 |

The user subsequently chose to start with style and comment conventions. That
does not mark the correctness/security backlog as resolved.

## Source index

The evidence above refers to the following reviewed sources. Line numbers are
omitted because subsequent comment/style edits move them.

- [Sale registration](</C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/main/java/pe/edu/upeu/geovision_backend/service/ventas/impl/VentaServiceImpl.java>)
- [Pending payments](</C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/main/java/pe/edu/upeu/geovision_backend/service/ventas/impl/PagoPendienteServiceImpl.java>)
- [Sales history](</C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/main/java/pe/edu/upeu/geovision_backend/service/ventas/impl/VentaHistorialServiceImpl.java>)
- [Account management](</C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/main/java/pe/edu/upeu/geovision_backend/service/security/impl/UsuarioServiceImpl.java>)
- [Authentication and recovery](</C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/main/java/pe/edu/upeu/geovision_backend/service/security/impl/AuthServiceImpl.java>)
- [Recovery simulator](</C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/main/java/pe/edu/upeu/geovision_backend/service/security/impl/EmailServiceImpl.java>)
- [Authentication account loading](</C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/main/java/pe/edu/upeu/geovision_backend/security/service/UsuarioDetailsService.java>)
- [JWT implementation](</C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/main/java/pe/edu/upeu/geovision_backend/security/service/JwtService.java>)
- [Global errors](</C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/main/java/pe/edu/upeu/geovision_backend/exception/GlobalExceptionHandler.java>)
- [Work-order sale association](</C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/main/java/pe/edu/upeu/geovision_backend/util/orden_trabajo/VentaOrdenTrabajo.java>)
- [Work-order history capacity](</C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/main/java/pe/edu/upeu/geovision_backend/util/orden_trabajo/ConstanciaOrdenTrabajo.java>)
- [Maintenance implementation](</C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/main/java/pe/edu/upeu/geovision_backend/service/gestion_mantenimiento/impl/MantenimientoServiceImpl.java>)
- [Oracle DDL](</C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/database/DDL_GeoVision_Oracle19c_corregido.sql>)
- [Dependencies](</C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/pom.xml>)
