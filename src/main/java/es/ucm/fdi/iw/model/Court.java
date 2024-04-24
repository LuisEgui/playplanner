package es.ucm.fdi.iw.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.persistence.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import lombok.Data;
import lombok.Getter;
import lombok.AllArgsConstructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Entity
@Data
@Table(name="IWCourt")
public class Court {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen")
    @SequenceGenerator(name = "gen", sequenceName = "gen")
	private long id;

    private String nombre;
    private String tipo;
    private String localizacion;
    private String localidad;

    private int apertura;
    private int cierre;

    private int maxp;

    //@OneToMany(mappedBy = "court")
    @OneToMany
    @JoinColumn(name = "partido_id")
    List<Partido> partido = new ArrayList<>();

    public Court() {
        this.partido = new ArrayList<Partido>();
    }
}