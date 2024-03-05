package es.ucm.fdi.iw.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Data
@Table(name = "IWMatch")
public class Match {
    public enum Estado {
        PREPARANDO,
        CERRADO,   
        TERMINADO
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen")
    @SequenceGenerator(name = "gen", sequenceName = "gen")
	private long id;

    /*@OneToMany
    private Court pista;*/

    private Timestamp start;
    private Timestamp end;

    @Column(nullable = true)
    private String result;

    @Column(nullable = false)
    private boolean isPrivate;
}