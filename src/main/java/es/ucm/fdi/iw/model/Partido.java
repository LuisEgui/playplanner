package es.ucm.fdi.iw.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Data
@NamedQueries({
    @NamedQuery(name="Partido.horasOcupadas",
            query="SELECT HOUR(p.inicio) FROM Partido p "
            + "WHERE p.pista.id = :pistaId "
            + "AND p.inicio >= :fecha "
            + "AND p.inicio < :fechaMasUnDia"),
    @NamedQuery(name="Partido.allPartidos",
            query="SELECT p FROM Partido p "
            + "WHERE p.estado = 'PREPARANDO'"),
    @NamedQuery(name="Partido.filtrar",
            query="SELECT p FROM Partido p "
            + "WHERE (:deporte IS NULL OR p.pista.tipo = :deporte) "
            + "AND (:localizacion IS NULL OR p.pista.localizacion = :localizacion) "
            + "AND (:fechaInicio IS NULL OR p.inicio >= :fechaInicio) "
            + "AND (:fechaFin IS NULL OR p.fin <= :fechaFin) "
            + "AND (p.estado = 'PREPARANDO')"),
    @NamedQuery(name="Partido.ganados",
            query="SELECT COUNT(p) FROM Partido p "
            + "JOIN p.juega j "
            + "JOIN j.user u "
            + "WHERE p.result = 'GANADO' "
            + "AND u.id = :idUser"),
    @NamedQuery(name="Partido.perdidos",
            query="SELECT COUNT(p) FROM Partido p "
            + "JOIN p.juega j "
            + "JOIN j.user u "
            + "WHERE p.result = 'PERDIDO' "
            + "AND u.id = :idUser"),
    @NamedQuery(name="Partido.byUser",
            query="SELECT p FROM Partido p "
            + "JOIN p.juega j "
            + "JOIN j.user u "
            + "WHERE p.estado = 'TERMINADO' "
            + "AND u.id = :idUser")
})
public class Partido {
    
    public enum Estado {
        PREPARANDO,
        CERRADO,   
        TERMINADO
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen")
    @SequenceGenerator(name = "gen", sequenceName = "gen")
	private long id;

    @ManyToOne
    private Court pista;

    @OneToMany
    @JoinColumn(name = "partido_id")
    private List<Juega> juega;

    @OneToMany
    @JoinColumn(name = "partido_id")
    private List<Mensaje> mensajes;

    private LocalDateTime inicio;
    private LocalDateTime fin;

    @Column(nullable = true)
    private String result;

    private boolean isPrivate;

    private String estado;

    private String chatToken;

    public Partido() {
        this.juega = new ArrayList<Juega>();
    }
}