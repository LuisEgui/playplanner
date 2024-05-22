package es.ucm.fdi.iw.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Data
@NamedQueries({
    @NamedQuery(name="Rating.XvaloradoPorYEnPartidoZ",
            query="SELECT r FROM Rating r "
                    + "WHERE r.emisor.id = :emisor_id AND r.juega.user.id = :receptor_id AND r.juega.partido.id = :partido_id"),
    @NamedQuery(name="Rating.media",
    query="SELECT AVG(r.valoracion) FROM Rating r "
            + "WHERE r.juega.user.id = :receptor_id"),
    @NamedQuery(name="Rating.numVals",
    query="SELECT COUNT(r) FROM Rating r "
            + "WHERE r.juega.user.id = :receptor_id"),
})
public class Rating {

    public static int MAXVAL = 10;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen")
    @SequenceGenerator(name = "gen", sequenceName = "gen")
	private long id;

    @ManyToOne
    private User emisor;
    
    @ManyToOne
    private Juega juega;

    @Column(nullable = false)
    private int valoracion;

}