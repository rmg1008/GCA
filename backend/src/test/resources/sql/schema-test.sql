SET REFERENTIAL_INTEGRITY FALSE;
drop table if exists device;
drop table if exists device_group;
drop table if exists template_command;
drop table if exists command;
drop table if exists template;
drop table if exists operating_system;
drop table if exists user_roles;
drop table if exists app_user;
drop table if exists role;
SET REFERENTIAL_INTEGRITY TRUE;

CREATE TABLE role (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE app_user (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(255) NOT NULL,
                        email VARCHAR(255) NOT NULL UNIQUE,
                        password VARCHAR(255) NOT NULL
);


CREATE TABLE user_roles (
                              user_id BIGINT NOT NULL,
                              role_id BIGINT NOT NULL,
                              PRIMARY KEY (user_id, role_id),
                              FOREIGN KEY (user_id) REFERENCES app_user(id),
                              FOREIGN KEY (role_id) REFERENCES role(id)
);

CREATE TABLE operating_system (
                                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE template (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             name VARCHAR(255),
                             description VARCHAR(255),
                             os_id BIGINT NOT NULL,
                             updated_at TIMESTAMP,
                             FOREIGN KEY (os_id) REFERENCES operating_system(id)
);

CREATE TABLE command (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           name VARCHAR(255),
                           description VARCHAR(255),
                           command_value VARCHAR(255)
);

CREATE TABLE template_command (
                                    template_id BIGINT NOT NULL,
                                    command_id BIGINT NOT NULL,
                                    execution_order INT NOT NULL,
                                    parameter_values TEXT NULL,
                                    PRIMARY KEY (template_id, command_id),
                                    FOREIGN KEY (template_id) REFERENCES template(id),
                                    FOREIGN KEY (command_id) REFERENCES command(id)
);

CREATE TABLE device_group (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                name VARCHAR(255) NOT NULL,
                                parent BIGINT,
                                template BIGINT,
                                FOREIGN KEY (parent) REFERENCES device_group(id),
                                FOREIGN KEY (template) REFERENCES template(id)
);

CREATE TABLE device (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          finger_print VARCHAR(255) NOT NULL UNIQUE,
                          finger_print_hash VARCHAR(255) NOT NULL UNIQUE,
                          name VARCHAR(255) NOT NULL UNIQUE,
                          created_at TIMESTAMP,
                          group_id BIGINT NOT NULL,
                          os_id BIGINT NOT NULL,
                          template INT,
                          FOREIGN KEY (group_id) REFERENCES device_group(id),
                          FOREIGN KEY (os_id) REFERENCES operating_system(id),
                          FOREIGN KEY (template) REFERENCES template(id)
);