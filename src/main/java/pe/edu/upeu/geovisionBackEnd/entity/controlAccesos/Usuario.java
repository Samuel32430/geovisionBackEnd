package pe.edu.upeu.geovisionBackEnd.entity.controlAccesos;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "USUARIO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;

    @Column(name = "nombre", nullable = false, unique = true, length = 200)
    private String nombre;

    @Column(name = "password", nullable = false, length = 500)
    private String password;

    @Column(name = "correo", nullable = false, unique = true, length = 200)
    private String correo;

    @Column(name = "estado", nullable = false)
    @Builder.Default
    private Integer estado = 1;

    @PrePersist
    public void prePersist() {
        if (this.estado == null) {
            this.estado = 1;
        }
    }
}
