-- insert admin (username a, password aa)
INSERT INTO IWUser (id, enabled, roles, username, password)
VALUES (1, TRUE, 'ADMIN,USER', 'a',
    '{bcrypt}$2a$10$2BpNTbrsarbHjNsUWgzfNubJqBRf.0Vz9924nRSHBqlbPKerkgX.W');
INSERT INTO IWUser (id, enabled, roles, username, password)
VALUES (2, TRUE, 'USER', 'b',
    '{bcrypt}$2a$10$2BpNTbrsarbHjNsUWgzfNubJqBRf.0Vz9924nRSHBqlbPKerkgX.W');

INSERT INTO IWCourt (id, nombre, localizacion, apertura, cierre, tipo)
VALUES (1, 'Pista prueba 1', 'Calle agjsdksdn', '08:00', '20:00', 'Baloncesto');

INSERT INTO Partido (id, fin, inicio, is_private, pista_id, chat_token) VALUES (978, '2024-11-11T18:00', '2024-11-11T19:00', false, 1, 'XhbWy-uPBI1N81IT');

INSERT INTO IWJUEGA (id, partido_id, user_id) VALUES (1, 978, 2);
INSERT INTO IWJUEGA (id, partido_id, user_id) VALUES (2, 978, 1);
-- start id numbering from a value that is larger than any assigned above
ALTER SEQUENCE "PUBLIC"."GEN" RESTART WITH 1024;
