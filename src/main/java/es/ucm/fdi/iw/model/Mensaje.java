package es.ucm.fdi.iw.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import lombok.Data;
import lombok.Getter;
import lombok.AllArgsConstructor;

/**
 * A message that users can send each other.
 *
 */
@Entity
@NamedQueries({
	@NamedQuery(name="Mensaje.countUnread",
	query="SELECT COUNT(m) FROM Mensaje m "
			+ "WHERE m.recipient.id = :userId AND m.dateRead = null"),
	
	@NamedQuery(name="Mensaje.reports", 
	query="SELECT m FROM Mensaje m WHERE m.isReport = TRUE"),

	@NamedQuery(name="Mensaje.noLeidos", 
	query="SELECT m FROM Mensaje m WHERE m.dateRead = null")
})
@Data
public class Mensaje implements Transferable<Mensaje.Transfer> {
	
	private static Logger log = LogManager.getLogger(Mensaje.class);	
	
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen")
    @SequenceGenerator(name = "gen", sequenceName = "gen")
	private long id;
	@ManyToOne
	private User sender;
	@ManyToOne
	private User recipient;

	@ManyToOne
	private Partido partido;
	private String texto;

	private boolean isReport;
	
	private LocalDateTime dateSent;
	private LocalDateTime dateRead;
	
	/**
	 * Objeto para persistir a/de JSON
	 * @author mfreire
	 */
    @Getter
    @AllArgsConstructor
	public static class Transfer {
		private String from;
		private String to;
		private String sent;
		private String received;
		private String text;
		long id;
		public Transfer(Mensaje m) {
			this.from = m.getSender().getUsername();
			this.to = "Partido #".concat(Long.toString(m.getPartido().getId()));
			this.sent = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(m.getDateSent());
			this.received = m.getDateRead() == null ?
					null : DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(m.getDateRead());
			this.text = m.getTexto();
			this.id = m.getId();
		}
	}

	@Override
	public Transfer toTransfer() {
		return new Transfer(sender.getUsername(), Long.toString(partido.getId()), 
			DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(dateSent),
			dateRead == null ? null : DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(dateRead),
			texto, id
        );
    }
}