-- insert admin (username a, password aa)
INSERT INTO IWUser (id, enabled, roles, username, password)
VALUES (1, TRUE, 'ADMIN,USER', 'a',
    '{bcrypt}$2a$10$2BpNTbrsarbHjNsUWgzfNubJqBRf.0Vz9924nRSHBqlbPKerkgX.W');
INSERT INTO IWUser (id, enabled, roles, username, password)
VALUES (2, TRUE, 'USER', 'b',
    '{bcrypt}$2a$10$2BpNTbrsarbHjNsUWgzfNubJqBRf.0Vz9924nRSHBqlbPKerkgX.W');

INSERT INTO IWUser (id, enabled, roles, username, password)
VALUES (3, TRUE, 'USER', 'c',
    '{bcrypt}$2a$10$2BpNTbrsarbHjNsUWgzfNubJqBRf.0Vz9924nRSHBqlbPKerkgX.W');

INSERT INTO IWUser (id, enabled, roles, username, password)
VALUES (4, TRUE, 'USER', 'd',
    '{bcrypt}$2a$10$2BpNTbrsarbHjNsUWgzfNubJqBRf.0Vz9924nRSHBqlbPKerkgX.W');

INSERT INTO IWUser (id, enabled, roles, username, password)
VALUES (5, TRUE, 'USER', 'e',
    '{bcrypt}$2a$10$2BpNTbrsarbHjNsUWgzfNubJqBRf.0Vz9924nRSHBqlbPKerkgX.W');


INSERT INTO IWCourt (id, nombre, localidad, localizacion, apertura, cierre, tipo, maxp)
VALUES (1, 'Pista prueba 1', 'Alcorcón', 'Calle agjsdksdn', '8', '20', 'Baloncesto', 10);

INSERT INTO Partido (id, inicio, fin, is_private, pista_id, chat_token, maxp, estado) VALUES (1, '2024-11-11T18:00', '2024-11-11T19:00', false, 1, 'XhbWy-uPBI1N81IT', 8, 0);
INSERT INTO Partido (id, inicio, fin, is_private, pista_id, chat_token, maxp, estado) VALUES (2, '2024-11-11T16:00', '2024-11-11T17:00', false, 1, 'XhbWy-uPBI1N81IO', 10, 0);

--Participaciones en partido
INSERT INTO IWJUEGA (id, partido_id, user_id) VALUES (1, 1, 2);
INSERT INTO IWJUEGA (id, partido_id, user_id) VALUES (2, 1, 3);
INSERT INTO IWJUEGA (id, partido_id, user_id) VALUES (3, 1, 4);

-- Mensajes de chat
INSERT INTO Mensaje (id, date_read, date_sent, is_report, texto, partido_id, recipient_id, sender_id)
VALUES (1, '2024-11-11T19:00', '2024-11-11T18:00', false, 'Hola a todos!', 1, null, 2);

INSERT INTO Mensaje (id, date_read, date_sent, is_report, texto, partido_id, recipient_id, sender_id)
VALUES (2, '2024-11-11T20:00', '2024-11-11T19:00', false, 'Holaa!', 1, null, 4);

-- Insertar reportes
INSERT INTO Mensaje (id, date_read, date_sent, is_report, texto, partido_id, recipient_id, sender_id)
VALUES (3, '2024-11-11T19:00', '2024-11-11T18:00', true, 'El usuario pepe esta spameando el chat', 1, null, 2);

INSERT INTO Mensaje (id, date_read, date_sent, is_report, texto, partido_id, recipient_id, sender_id)
VALUES (4, '2024-11-11T20:00', '2024-11-11T19:00', true, 'Esto es otro reporte', 1, null, 3);

-- start id numbering from a value that is larger than any assigned above
ALTER SEQUENCE "PUBLIC"."GEN" RESTART WITH 1024;
