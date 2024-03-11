-- insert admin (username a, password aa)
INSERT INTO IWUser (id, enabled, roles, username, password)
VALUES (1, TRUE, 'ADMIN,USER', 'a',
    '{bcrypt}$2a$10$2BpNTbrsarbHjNsUWgzfNubJqBRf.0Vz9924nRSHBqlbPKerkgX.W');
INSERT INTO IWUser (id, enabled, roles, username, password)
VALUES (2, TRUE, 'USER', 'b',
    '{bcrypt}$2a$10$2BpNTbrsarbHjNsUWgzfNubJqBRf.0Vz9924nRSHBqlbPKerkgX.W');

INSERT INTO IWCourt (id, nombre, localizacion, apertura, cierre, tipo)
VALUES (1, 'Pista prueba 1', 'Calle agjsdksdn', '08:00', '20:00', 'Baloncesto');

-- start id numbering from a value that is larger than any assigned above
ALTER SEQUENCE "PUBLIC"."GEN" RESTART WITH 1024;
