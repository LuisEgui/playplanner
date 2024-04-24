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
            query="SELECT p FROM Partido p "),

	@NamedQuery(name="Partido.conflicto",
	query="SELECT p FROM Partido p WHERE p.pista.id = :courtId " +
    "AND  ((:fechaInicio >= p.inicio AND :fechaInicio < p.fin) " +
    "OR (:fechaFin > p.inicio AND :fechaFin <= p.fin) " +
    "OR (:fechaInicio <= p.inicio AND :fechaFin >= p.fin))"),
})

public class Partido {

    public enum Estado {
        PREPARANDO,
        CERRADO,
        TERMINADO
    }

    public static final int DURACION_PARTIDOS_HORAS = 2;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen")
    @SequenceGenerator(name = "gen", sequenceName = "gen")
	private long id;

    //@NotNull
    @ManyToOne
    private Court pista;

    @OneToMany
    @JoinColumn(name = "partido_id")
    private List<Juega> juega;

    @OneToMany
    @JoinColumn(name = "partido_id")
    private List<Mensaje> mensajes;

    //@NotNull
    //@FutureOrPresent
    private LocalDateTime inicio;

    //@NotNull
    //@FutureOrPresent
    private LocalDateTime fin;

    @Column(nullable = true)
    private String result;

    private boolean isPrivate;

    private String chatToken;

    private Estado estado = Estado.PREPARANDO;

    public Partido() {
        this.juega = new ArrayList<Juega>();
    }

    public Partido(Court pista, LocalDateTime inicio, LocalDateTime fin, boolean isPrivate) {
        this.pista = pista;
        this.inicio = inicio;
        this.fin = fin;
        this.isPrivate = isPrivate;
        this.juega = new ArrayList<Juega>();
        this.mensajes = new ArrayList<Mensaje>();
    }

    public void addJuega(Juega juega) {
        this.juega.add(juega);
        juega.setPartido(this);
    }

    public void removeJuega(Juega juega) {
        juega.setPartido(null);
        this.juega.remove(juega);
    }

    public void addMensaje(Mensaje mensaje) {
        this.mensajes.add(mensaje);
        mensaje.setPartido(this);
    }

    public void removeMensaje(Mensaje mensaje) {
        mensaje.setPartido(null);
        this.mensajes.remove(mensaje);
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public Juega getJuega(User jugador) {
        Juega resultado = null;
        for(Juega j : juega) {
            if(j.getUser().getId() == jugador.getId()) resultado = j;
        }
        return resultado;
    }
}