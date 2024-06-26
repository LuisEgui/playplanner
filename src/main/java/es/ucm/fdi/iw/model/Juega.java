package es.ucm.fdi.iw.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "IWJuega")
public class Juega {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen")
    @SequenceGenerator(name = "gen", sequenceName = "gen")
	private long id;

    @ManyToOne
    private Partido partido;

    @ManyToOne
    private User user;

    private LocalDateTime ultimoAcceso = LocalDateTime.now();

    @OneToMany
    @JoinColumn(name = "rating_id")
    private List<Rating> ratings = new ArrayList<>();

    public Juega() {
        this.ratings = new ArrayList<Rating>();
    }
}
