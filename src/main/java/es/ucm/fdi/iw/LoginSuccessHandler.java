package es.ucm.fdi.iw;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import es.ucm.fdi.iw.model.Juega;
import es.ucm.fdi.iw.model.Rating;
import es.ucm.fdi.iw.model.User;
import es.ucm.fdi.iw.model.User.Role;

/**
 * Called when a user is first authenticated (via login).
 * Called from SecurityConfig; see https://stackoverflow.com/a/53353324
 * 
 * Adds a "u" variable to the session when a user is first authenticated.
 * Important: the user is retrieved from the database, but is not refreshed at each request. 
 * You should refresh the user's information if anything important changes; for example, after
 * updating the user's profile.
 */
@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired 
    private HttpSession session;
    
    @Autowired
    private EntityManager entityManager;    
    
	private static Logger log = LogManager.getLogger(LoginSuccessHandler.class);
	
    /**
     * Called whenever a user authenticates correctly.
     */
    @Override
	@Transactional
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
	   
		/* 
		  Avoids following warning: 
		  Cookie “JSESSIONID” will be soon rejected because it has the “SameSite” 
		  attribute set to “None” or an invalid value, without the “secure” attribute. 
		  To know more about the “SameSite“ attribute, read 
		  https://developer.mozilla.org/docs/Web/HTTP/Headers/Set-Cookie/SameSite
		*/
		addSameSiteCookieAttribute(response);

		String username = ((org.springframework.security.core.userdetails.User)
				authentication.getPrincipal()).getUsername();
	    
	    // add a 'u' session variable, accessible from thymeleaf via ${session.u}
	    log.info("Storing user info for {} in session {}", username, session.getId());
		User u = entityManager.createNamedQuery("User.byUsername", User.class)
		        .setParameter("username", username)
		        .getSingleResult();		
		session.setAttribute("u", u);

		// add 'url' and 'ws' session variables
		// example URLS:                            Root URL
		//     http://localhost:8080/               //localhost:8080/
		//     http://localhost:8080/abc/           //localhost:8080/abc/
		//     https://vmXY.containers.fdi.ucm.es/  //vmXY.containers.fdi.ucm.es/
		//
		String url = request.getRequestURL().toString()
			.replaceFirst("/[^/]*$", "")        // ...foo/bar        => ...foo/
			.replaceFirst("[^/]*", "");         // http[s]://...foo/ => //...foo/
		String ws = "ws:" + url + "/ws";        // //...foo/         => ws://...foo/ws
		if (url.contains("ucm.es")) {
			ws = ws.replace("ws:", "wss:"); // for deployment in containers
		}
		session.setAttribute("url", url);
		session.setAttribute("ws", ws);

		// redirects to 'admin' or 'user/{id}', depending on the user
		String nextUrl = u.hasRole(User.Role.ADMIN) ? 
			"admin/" :
			"user/" + u.getId();

		log.info("LOG IN: {} (id {}) -- session is {}, websocket is {} -- redirected to {}",
			u.getUsername(), u.getId(), session.getId(), ws, nextUrl);

		u = entityManager.find(User.class, u.getId());
		ArrayList<String> topics = new ArrayList<>();
		for (Juega j : u.getJuega()) {
			log.info("\t interesado en {}", j.getPartido().getChatToken());
			topics.add("/topic/" + j.getPartido().getChatToken());
		}
		String intereses = String.join(",", topics);

		log.info("\t => interesado en {}", intereses);
		session.setAttribute("topics", intereses);


		// note that this is a 302, and will result in a new request
		response.sendRedirect(nextUrl);
	}

	/**
	 * Set samesite cookie - see https://stackoverflow.com/a/58996747/15472
	 * @param response
	 */
	private void addSameSiteCookieAttribute(HttpServletResponse response) {
		Collection<String> headers = response.getHeaders(HttpHeaders.SET_COOKIE);
		boolean firstHeader = true;
		// there can be multiple Set-Cookie attributes
		for (String header : headers) {
			if (firstHeader) {
				response.setHeader(HttpHeaders.SET_COOKIE,
						String.format("%s; %s", header, "SameSite=Strict"));
				firstHeader = false;
				continue;
			}
			response.addHeader(HttpHeaders.SET_COOKIE,
					String.format("%s; %s", header, "SameSite=Strict"));
		}
	}
}
