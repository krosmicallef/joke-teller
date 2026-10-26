CREATE TABLE subscriber (
    id INT NOT NULL AUTO_INCREMENT,
    email VARCHAR(255) NOT NULL,
    CONSTRAINT pk_subscriber PRIMARY KEY (id),
    CONSTRAINT uk_subscriber_email UNIQUE (email)
);
