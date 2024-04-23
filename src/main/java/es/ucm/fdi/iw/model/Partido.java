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
            query="SELECT p FROM Partido p ")
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

    private String chatToken;

    public Partido() {
        this.juega = new ArrayList<Juega>();
    }
}