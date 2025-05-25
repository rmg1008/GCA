INSERT INTO "app_user" (id, name, email, password) VALUES (2, 'admin', 'admin@testubu.es', '1234');
INSERT INTO "role" (id, name) VALUES (1, 'Admin'), (2, 'Gestor');
INSERT INTO "operating_system" (id, name) VALUES (1, 'Windows');
INSERT INTO "user_roles" VALUES (2, 2);
INSERT INTO "device_group" (id, name, parent, template) VALUES (1, 'UBU', NULL, NULL);
