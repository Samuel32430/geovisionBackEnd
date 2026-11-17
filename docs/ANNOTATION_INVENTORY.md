# Annotation inventory — GeoVision

Snapshot: 2026-11-08, after the comment, Lombok, and Spring convention passes. This records current usage; agreed rules are in JAVA_STYLE.md.

**90 distinct annotations**, normalized by simple name: 68 appear in production and 30 in tests. Shared annotations are counted once in the total. Java source references use imported simple names.

The inventory was extracted from Java code after excluding comments and string literals. Email addresses, Javadoc tags, and annotation names mentioned in strings are not counted.

Persistence follow-up: `@UniqueConstraint` was removed; existing column `unique`
and `nullable` flags remain. Each converter now belongs to its enum and is named
`PersistenceConverter`; its annotation uses the imported name `@Converter`.
The entity retains `@Convert`. Composite test constraints are declared in test SQL.
Service transactions are declared on methods only; class defaults were copied
to affected methods while retaining existing method-specific overrides.

## Proposed discussion sequence

Lombok and Spring bean conventions are agreed. Discuss JPA mapping next, followed by validation, controllers/error handling, repository queries/transactions, security/JSON, and test annotations. Annotation ordering remains open.

## Inventory

### 1. Lombok

| Annotation | Scope | Purpose | Example |
|---|---|---|---|
| `@Getter` | Production | Generates getters. | [TipoLogEnum.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/main/java/pe/edu/upeu/geovision_backend/enums/TipoLogEnum.java:8>) |
| `@Setter` | Production | Generates setters. | [Lente.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/main/java/pe/edu/upeu/geovision_backend/entity/atencion/Lente.java:31>) |
| `@NoArgsConstructor` | Production | Generates a constructor without arguments. | [Lente.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/main/java/pe/edu/upeu/geovision_backend/entity/atencion/Lente.java:32>) |
| `@AllArgsConstructor` | Production | Generates a constructor for the instance fields. | [Lente.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/main/java/pe/edu/upeu/geovision_backend/entity/atencion/Lente.java:33>) |
| `@RequiredArgsConstructor` | Production, Tests | Generates a constructor for required fields, including uninitialized final fields. | [JwtAuthenticationFilter.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/main/java/pe/edu/upeu/geovision_backend/security/JwtAuthenticationFilter.java:26>) |
| `@NonNull` | Production | Lombok non-null metadata/check generation where supported; distinct from Bean Validation. | [GlobalExceptionHandler.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/main/java/pe/edu/upeu/geovision_backend/exception/GlobalExceptionHandler.java:35>) |

### 2. Application and Spring beans

| Annotation | Scope | Purpose | Example |
|---|---|---|---|
| `@SpringBootApplication` | Production | Application entry point and Spring Boot configuration. | [GeovisionBackEndApplication.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/main/java/pe/edu/upeu/geovision_backend/GeovisionBackEndApplication.java:6>) |
| `@Configuration` | Production | Declares Spring configuration. | [JacksonConfig.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/main/java/pe/edu/upeu/geovision_backend/config/JacksonConfig.java:9>) |
| `@Bean` | Production | Registers a method result as a Spring bean. | [JacksonConfig.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/main/java/pe/edu/upeu/geovision_backend/config/JacksonConfig.java:12>) |
| `@Component` | Production | Registers a general component. | [RestAccessDeniedHandler.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/main/java/pe/edu/upeu/geovision_backend/security/RestAccessDeniedHandler.java:12>) |
| `@Service` | Production | Registers a service component. | [JwtService.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/main/java/pe/edu/upeu/geovision_backend/security/service/JwtService.java:22>) |
| `@Repository` | Production | Declares a persistence component/repository. | [LenteRepository.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/main/java/pe/edu/upeu/geovision_backend/repository/atencion/LenteRepository.java:17>) |
| `@Value` | Production, Tests | Injects a configured value. | [PoliticaCalidad.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/main/java/pe/edu/upeu/geovision_backend/config/gestion_mantenimiento/PoliticaCalidad.java:17>) |
| `@Profile` | Production | Restricts bean registration to profiles. | [SecurityDataInitializer.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/main/java/pe/edu/upeu/geovision_backend/config/security/SecurityDataInitializer.java:22>) |
| `@PostConstruct` | Production | Runs initialization after dependency injection. | [JwtService.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/main/java/pe/edu/upeu/geovision_backend/security/service/JwtService.java:104>) |

### 3. HTTP controllers and binding

| Annotation | Scope | Purpose | Example |
|---|---|---|---|
| `@RestController` | Production, Tests | Declares a controller with response-body handling. | [CatalogoOpticoController.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/main/java/pe/edu/upeu/geovision_backend/controller/atencion/CatalogoOpticoController.java:23>) |
| `@RequestMapping` | Production | Declares route mappings, usually their shared prefix. | [CatalogoOpticoController.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/main/java/pe/edu/upeu/geovision_backend/controller/atencion/CatalogoOpticoController.java:24>) |
| `@GetMapping` | Production, Tests | Maps HTTP GET. | [CatalogoOpticoController.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/main/java/pe/edu/upeu/geovision_backend/controller/atencion/CatalogoOpticoController.java:33>) |
| `@PostMapping` | Production | Maps HTTP POST. | [PacienteController.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/main/java/pe/edu/upeu/geovision_backend/controller/atencion/PacienteController.java:69>) |
| `@PutMapping` | Production | Maps HTTP PUT. | [PacienteController.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/main/java/pe/edu/upeu/geovision_backend/controller/atencion/PacienteController.java:79>) |
| `@PatchMapping` | Production | Maps HTTP PATCH. | [PacienteController.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/main/java/pe/edu/upeu/geovision_backend/controller/atencion/PacienteController.java:88>) |
| `@PathVariable` | Production | Binds a route variable. | [PacienteController.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/main/java/pe/edu/upeu/geovision_backend/controller/atencion/PacienteController.java:58>) |
| `@RequestParam` | Production, Tests | Binds a query parameter. | [CatalogoOpticoController.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/main/java/pe/edu/upeu/geovision_backend/controller/atencion/CatalogoOpticoController.java:45>) |
| `@RequestBody` | Production | Binds the request body. | [PacienteController.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/main/java/pe/edu/upeu/geovision_backend/controller/atencion/PacienteController.java:72>) |
| `@ResponseStatus` | Production | Declares an HTTP response status. | [OrdenTrabajoController.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/main/java/pe/edu/upeu/geovision_backend/controller/orden_trabajo/OrdenTrabajoController.java:65>) |
| `@DateTimeFormat` | Production | Controls date/time binding. | [VentaHistorialController.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/main/java/pe/edu/upeu/geovision_backend/controller/ventas/VentaHistorialController.java:42>) |
| `@PageableDefault` | Production | Declares default pagination/sorting. | [VentaHistorialController.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/main/java/pe/edu/upeu/geovision_backend/controller/ventas/VentaHistorialController.java:45>) |

### 4. Error responses

| Annotation | Scope | Purpose | Example |
|---|---|---|---|
| `@RestControllerAdvice` | Production | Applies shared controller advice and response-body handling. | [GlobalExceptionHandler.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/main/java/pe/edu/upeu/geovision_backend/exception/GlobalExceptionHandler.java:27>) |
| `@ExceptionHandler` | Production | Selects a method to handle matching exceptions. | [GlobalExceptionHandler.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/main/java/pe/edu/upeu/geovision_backend/exception/GlobalExceptionHandler.java:34>) |

### 5. Validation

| Annotation | Scope | Purpose | Example |
|---|---|---|---|
| `@Valid` | Production | Cascades validation into the annotated value. | [PacienteController.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/main/java/pe/edu/upeu/geovision_backend/controller/atencion/PacienteController.java:72>) |
| `@Validated` | Production | Enables Spring validation integration, including method validation where applicable. | [PacienteServiceImpl.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/main/java/pe/edu/upeu/geovision_backend/service/atencion/impl/PacienteServiceImpl.java:33>) |
| `@NotNull` | Production | Rejects null. | [LenteRequestDTO.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/main/java/pe/edu/upeu/geovision_backend/dto/atencion/LenteRequestDTO.java:12>) |
| `@NotBlank` | Production | Rejects null, empty, or whitespace-only text. | [PersonaRequestDTO.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/main/java/pe/edu/upeu/geovision_backend/dto/atencion/PersonaRequestDTO.java:12>) |
| `@NotEmpty` | Production | Rejects null or empty values. | [RecetaRequestDTO.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/main/java/pe/edu/upeu/geovision_backend/dto/atencion/RecetaRequestDTO.java:25>) |
| `@Positive` | Production | Requires a value above zero. | [PacienteController.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/main/java/pe/edu/upeu/geovision_backend/controller/atencion/PacienteController.java:58>) |
| `@PositiveOrZero` | Production | Requires a nonnegative value. | [LenteRequestDTO.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/main/java/pe/edu/upeu/geovision_backend/dto/atencion/LenteRequestDTO.java:21>) |
| `@Min` | Production, Tests | Declares a minimum numeric value. | [CatalogoOpticoController.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/main/java/pe/edu/upeu/geovision_backend/controller/atencion/CatalogoOpticoController.java:45>) |
| `@Max` | Production | Declares a maximum numeric value. | [CatalogoOpticoController.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/main/java/pe/edu/upeu/geovision_backend/controller/atencion/CatalogoOpticoController.java:46>) |
| `@DecimalMin` | Production | Declares a decimal minimum. | [MedidaRequestDTO.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/main/java/pe/edu/upeu/geovision_backend/dto/atencion/MedidaRequestDTO.java:17>) |
| `@Digits` | Production | Limits integer and fractional digits. | [LenteRequestDTO.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/main/java/pe/edu/upeu/geovision_backend/dto/atencion/LenteRequestDTO.java:22>) |
| `@Size` | Production | Limits length or collection size. | [OrdenTrabajoController.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/main/java/pe/edu/upeu/geovision_backend/controller/orden_trabajo/OrdenTrabajoController.java:36>) |
| `@Pattern` | Production | Checks text against a regular expression. | [ConfiguracionEmpresaDTO.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/main/java/pe/edu/upeu/geovision_backend/dto/gestion_configuracion/ConfiguracionEmpresaDTO.java:16>) |
| `@Email` | Production | Checks email format. | [ConfiguracionEmpresaDTO.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/main/java/pe/edu/upeu/geovision_backend/dto/gestion_configuracion/ConfiguracionEmpresaDTO.java:22>) |

### 6. JPA entity mapping

| Annotation | Scope | Purpose | Example |
|---|---|---|---|
| `@Entity` | Production | Declares a persistent entity. | [Lente.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/main/java/pe/edu/upeu/geovision_backend/entity/atencion/Lente.java:28>) |
| `@Table` | Production | Declares table mapping. | [Lente.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/main/java/pe/edu/upeu/geovision_backend/entity/atencion/Lente.java:29>) |
| `@Id` | Production | Identifies the primary-key attribute. | [Lente.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/main/java/pe/edu/upeu/geovision_backend/entity/atencion/Lente.java:36>) |
| `@GeneratedValue` | Production | Declares identifier generation. | [Lente.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/main/java/pe/edu/upeu/geovision_backend/entity/atencion/Lente.java:37>) |
| `@Column` | Production | Declares column mapping. | [Lente.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/main/java/pe/edu/upeu/geovision_backend/entity/atencion/Lente.java:38>) |
| `@ManyToOne` | Production | Maps a many-to-one association. | [Lente.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/main/java/pe/edu/upeu/geovision_backend/entity/atencion/Lente.java:44>) |
| `@OneToOne` | Production | Maps a one-to-one association. | [Paciente.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/main/java/pe/edu/upeu/geovision_backend/entity/atencion/Paciente.java:35>) |
| `@ManyToMany` | Production | Maps a many-to-many association. | [Lente.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/main/java/pe/edu/upeu/geovision_backend/entity/atencion/Lente.java:56>) |
| `@JoinColumn` | Production | Declares an association join column. | [Lente.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/main/java/pe/edu/upeu/geovision_backend/entity/atencion/Lente.java:45>) |
| `@JoinTable` | Production | Declares an association join table. | [Lente.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/main/java/pe/edu/upeu/geovision_backend/entity/atencion/Lente.java:57>) |
| `@MapsId` | Production | Shares an entity identifier with an association. | [Paciente.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/main/java/pe/edu/upeu/geovision_backend/entity/atencion/Paciente.java:36>) |
| `@OrderBy` | Production | Orders an entity collection when loaded. | [Lente.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/main/java/pe/edu/upeu/geovision_backend/entity/atencion/Lente.java:61>) |
| `@Convert` | Production | Selects a persistent attribute converter. | [Medida.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/main/java/pe/edu/upeu/geovision_backend/entity/atencion/Medida.java:38>) |
| `@Converter` | Production | Declares an attribute converter. | [TipoLogEnum.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/main/java/pe/edu/upeu/geovision_backend/enums/TipoLogEnum.java:35>) |
| `@PrePersist` | Production | Runs an entity callback before persistence. | [Medida.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/main/java/pe/edu/upeu/geovision_backend/entity/atencion/Medida.java:68>) |

### 7. Repository queries

| Annotation | Scope | Purpose | Example |
|---|---|---|---|
| `@Query` | Production | Declares repository query text. | [LenteRepository.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/main/java/pe/edu/upeu/geovision_backend/repository/atencion/LenteRepository.java:34>) |
| `@Param` | Production | Names a query parameter. | [LenteRepository.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/main/java/pe/edu/upeu/geovision_backend/repository/atencion/LenteRepository.java:35>) |
| `@Lock` | Production | Selects a locking mode for a repository operation. | [LenteRepository.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/main/java/pe/edu/upeu/geovision_backend/repository/atencion/LenteRepository.java:38>) |
| `@EntityGraph` | Production | Selects associations to fetch. | [LenteRepository.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/main/java/pe/edu/upeu/geovision_backend/repository/atencion/LenteRepository.java:23>) |

### 8. Transactions

| Annotation | Scope | Purpose | Example |
|---|---|---|---|
| `@Transactional` | Production, Tests | Declares transaction semantics, such as propagation or read-only behavior. | [SecurityDataInitializer.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/main/java/pe/edu/upeu/geovision_backend/config/security/SecurityDataInitializer.java:36>) |

### 9. Security

| Annotation | Scope | Purpose | Example |
|---|---|---|---|
| `@EnableWebSecurity` | Production | Enables Spring web security integration. | [SecurityConfig.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/main/java/pe/edu/upeu/geovision_backend/config/security/SecurityConfig.java:25>) |
| `@EnableMethodSecurity` | Production | Enables method authorization annotations. | [SecurityConfig.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/main/java/pe/edu/upeu/geovision_backend/config/security/SecurityConfig.java:26>) |
| `@PreAuthorize` | Production | Checks an authorization expression before invocation. | [CatalogoOpticoController.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/main/java/pe/edu/upeu/geovision_backend/controller/atencion/CatalogoOpticoController.java:25>) |

### 10. JSON

| Annotation | Scope | Purpose | Example |
|---|---|---|---|
| `@JsonProperty` | Production | Declares a JSON property; current occurrences expose calculated response properties. | [PersonaUsuarioResumenDTO.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/main/java/pe/edu/upeu/geovision_backend/dto/security/PersonaUsuarioResumenDTO.java:19>) |

### 11. Java language

| Annotation | Scope | Purpose | Example |
|---|---|---|---|
| `@Override` | Production, Tests | Checks that a method overrides/implements another method. | [TipoLogEnum.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/main/java/pe/edu/upeu/geovision_backend/enums/TipoLogEnum.java:37>) |
| `@FunctionalInterface` | Tests | Checks the single-abstract-method interface contract. | [DtoRecordJsonContractTest.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/test/java/pe/edu/upeu/geovision_backend/dto/DtoRecordJsonContractTest.java:29>) |

### 12. JUnit and parameterized tests

| Annotation | Scope | Purpose | Example |
|---|---|---|---|
| `@Test` | Tests | Declares a test. | [GeovisionBackEndApplicationTests.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/test/java/pe/edu/upeu/geovision_backend/GeovisionBackEndApplicationTests.java:11>) |
| `@BeforeEach` | Tests | Runs setup before each test. | [AtencionPacienteIntegrationTest.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/test/java/pe/edu/upeu/geovision_backend/controller/atencion/AtencionPacienteIntegrationTest.java:108>) |
| `@BeforeAll` | Tests | Runs setup before the test class. | [ConfiguracionEmpresaValidationTest.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/test/java/pe/edu/upeu/geovision_backend/dto/gestion_configuracion/ConfiguracionEmpresaValidationTest.java:22>) |
| `@AfterAll` | Tests | Runs cleanup after the test class. | [PatientFoundationValidationTest.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/test/java/pe/edu/upeu/geovision_backend/dto/atencion/PatientFoundationValidationTest.java:20>) |
| `@DisplayName` | Tests | Sets a human-readable test name. | [InventarioStockControllerTest.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/test/java/pe/edu/upeu/geovision_backend/controller/inventario/InventarioStockControllerTest.java:79>) |
| `@ExtendWith` | Tests | Registers a JUnit extension. | [InventarioStockControllerTest.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/test/java/pe/edu/upeu/geovision_backend/controller/inventario/InventarioStockControllerTest.java:42>) |
| `@ParameterizedTest` | Tests | Runs a test with multiple argument sets. | [DtoRecordJsonContractTest.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/test/java/pe/edu/upeu/geovision_backend/dto/DtoRecordJsonContractTest.java:58>) |
| `@MethodSource` | Tests | Provides arguments from methods. | [DtoRecordJsonContractTest.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/test/java/pe/edu/upeu/geovision_backend/dto/DtoRecordJsonContractTest.java:59>) |
| `@CsvSource` | Tests | Provides CSV arguments. | [PatientFoundationValidationTest.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/test/java/pe/edu/upeu/geovision_backend/dto/atencion/PatientFoundationValidationTest.java:36>) |
| `@ValueSource` | Tests | Provides simple argument values. | [AtencionPacienteIntegrationTest.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/test/java/pe/edu/upeu/geovision_backend/controller/atencion/AtencionPacienteIntegrationTest.java:305>) |
| `@NullAndEmptySource` | Tests | Provides null/empty argument cases. | [ConfiguracionEmpresaValidationTest.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/test/java/pe/edu/upeu/geovision_backend/dto/gestion_configuracion/ConfiguracionEmpresaValidationTest.java:42>) |

### 13. Spring integration tests

| Annotation | Scope | Purpose | Example |
|---|---|---|---|
| `@SpringBootTest` | Tests | Loads a Spring Boot test context. | [GeovisionBackEndApplicationTests.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/test/java/pe/edu/upeu/geovision_backend/GeovisionBackEndApplicationTests.java:7>) |
| `@ActiveProfiles` | Tests | Selects test profiles. | [GeovisionBackEndApplicationTests.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/test/java/pe/edu/upeu/geovision_backend/GeovisionBackEndApplicationTests.java:8>) |
| `@AutoConfigureMockMvc` | Tests | Configures MockMvc integration. | [AtencionPacienteIntegrationTest.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/test/java/pe/edu/upeu/geovision_backend/controller/atencion/AtencionPacienteIntegrationTest.java:71>) |
| `@AfterTransaction` | Tests | Runs after a test transaction. | [AtencionPacienteIntegrationTest.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/test/java/pe/edu/upeu/geovision_backend/controller/atencion/AtencionPacienteIntegrationTest.java:132>) |
| `@MockitoBean` | Tests | Overrides a Spring bean with a Mockito mock. | [ConfiguracionEmpresaControllerTest.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/test/java/pe/edu/upeu/geovision_backend/controller/gestion_configuracion/ConfiguracionEmpresaControllerTest.java:47>) |
| `@MockitoSpyBean` | Tests | Overrides/wraps a Spring bean with a Mockito spy. | [ConfiguracionLenteControllerTest.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/test/java/pe/edu/upeu/geovision_backend/controller/atencion/ConfiguracionLenteControllerTest.java:89>) |

### 14. Mockito unit tests

| Annotation | Scope | Purpose | Example |
|---|---|---|---|
| `@Mock` | Tests | Creates a mock for unit tests. | [InventarioStockControllerTest.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/test/java/pe/edu/upeu/geovision_backend/controller/inventario/InventarioStockControllerTest.java:49>) |
| `@InjectMocks` | Tests | Injects test doubles into the object under test. | [InventarioStockControllerTest.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/test/java/pe/edu/upeu/geovision_backend/controller/inventario/InventarioStockControllerTest.java:55>) |

### 15. Security test identities

| Annotation | Scope | Purpose | Example |
|---|---|---|---|
| `@WithMockUser` | Tests | Provides a mock authenticated identity. | [ConfiguracionLenteControllerTest.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/test/java/pe/edu/upeu/geovision_backend/controller/atencion/ConfiguracionLenteControllerTest.java:69>) |
| `@WithAnonymousUser` | Tests | Provides an anonymous test identity. | [ConfiguracionLenteControllerTest.java](<C:/Users/DRIZA/Documents/UNI/(IV - Project) GeoVision/src/test/java/pe/edu/upeu/geovision_backend/controller/atencion/ConfiguracionLenteControllerTest.java:341>) |

## Distinctions for the discussion

- `@NonNull`, `@NotNull`, and `@Column(nullable = false)` belong to different mechanisms. They do not substitute for one another.
- `@Valid` cascades validation; it does not itself require a value to be present.
- `@Transactional` is a behavioral boundary. Annotation order is a style decision; invocation through the relevant Spring infrastructure is a separate runtime concern.
- `@RequiredArgsConstructor` constructs objects; it does not register a Spring bean. The root `lombok.config` currently copies Spring `@Value` annotations to generated parameters.
- `@Column`, `@JoinColumn`, and `@UniqueConstraint` describe mappings/schema generation. With `ddl-auto: validate`, the application does not automatically add missing constraints to an existing Oracle schema.
- Enums such as `FetchType`, `GenerationType`, `Propagation`, and `LockModeType` are annotation argument values, not additional annotations.
- `@Autowired`, `@PersistenceContext`, `@Slf4j`, `@Builder`, and the removed custom DTO deserializer annotations are not part of the current source inventory.
- Integration tests use generated constructors with Spring test constructor autowiring enabled in junit-platform.properties. Mock/spy bean overrides remain test-managed fields.
- `@NoArgsConstructor` and `@AllArgsConstructor` occur only on entities; the DTO records have implicit canonical constructors. Enums have ordinary value constructors.
- Choosing annotation ordering and reviewing the remaining groups are subsequent discussions.
