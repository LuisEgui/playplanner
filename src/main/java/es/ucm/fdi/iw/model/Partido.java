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
    @JoinColumn(name = "juega_id")
    private List<Juega> juega;

    private LocalDateTime inicio;
    private LocalDateTime fin;

    @Column(nullable = true)
    private String result;

    private boolean isPrivate;

    public Partido() {
        this.juega = new ArrayList<Juega>();
    }
}