package pe.edu.upeu.geovisionBackEnd.repository.atencionPacientesConsulta;

import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.parser.PartTree;
import pe.edu.upeu.geovisionBackEnd.entity.atencionPacientesConsulta.*;
import static org.junit.jupiter.api.Assertions.*;

class PatientRepositoryMappingTest {
    @Test void explicitHqlAndDerivedPropertyPathsAreValidWithoutDatabase() {
        var registry = new StandardServiceRegistryBuilder()
                .applySetting("hibernate.dialect", "org.hibernate.dialect.OracleDialect")
                .applySetting("hibernate.boot.allow_jdbc_metadata_access", false).build();
        Class<?>[] entities = {Persona.class, Paciente.class, Documento.class, Examen.class, Medida.class,
                MedidaOjo.class, Receta.class, PacienteLog.class, TipoDocumento.class, TipoMedida.class, TipoOjo.class};
        Class<?>[] repositories = {PersonaRepository.class, PacienteRepository.class, DocumentoRepository.class,
                ExamenRepository.class, MedidaRepository.class, MedidaOjoRepository.class, RecetaRepository.class,
                PacienteLogRepository.class, TipoDocumentoRepository.class, TipoMedidaRepository.class, TipoOjoRepository.class};
        try {
            var sources = new MetadataSources(registry);
            for (var entity : entities) sources.addAnnotatedClass(entity);
            try (var factory = sources.buildMetadata().buildSessionFactory(); var session = factory.openSession()) {
                for (int i = 0; i < repositories.length; i++) {
                    for (var method : repositories[i].getDeclaredMethods()) {
                        var query = method.getAnnotation(Query.class);
                        if (query == null) {
                            assertNotNull(new PartTree(method.getName(), entities[i]));
                        } else {
                            assertNotNull(session.createQuery(query.value(), entities[i]));
                        }
                    }
                }
            }
        } finally { StandardServiceRegistryBuilder.destroy(registry); }
    }
}

