package es.ucm.fdi.iw.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.persistence.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.lang.Nullable;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Asocia un mensaje con un usuario indicando la fecha en la que lo leyó (si lo leyó).
 * Usado para chat grupal en el que no se puede asociar directamente mensaje y fecha leido.
 */

/* @Entity 
@Data
@NoArgsConstructor
public class Leido {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen")
    @SequenceGenerator(name = "gen", sequenceName = "gen")
	private long id;

    @ManyToOne
    private User user;
    
    @ManyToOne
    private Mensaje mensaje; 

    private LocalDateTime fecha;
} */
