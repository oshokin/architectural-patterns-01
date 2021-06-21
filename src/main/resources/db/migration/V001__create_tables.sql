CREATE TABLE IF NOT EXISTS movies (
    id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(200) NOT NULL,
    duration SMALLINT NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS prices (
  date_time DATETIME NOT NULL,
  movie_id INT NOT NULL,
  price DECIMAL(15, 2) NOT NULL,
  INDEX fk_prices_movies_movie_id_id_idx (movie_id ASC) VISIBLE,
  CONSTRAINT fk_prices_movies_movie_id_id
    FOREIGN KEY (movie_id)
    REFERENCES movies (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE);

CREATE TABLE IF NOT EXISTS sessions (
  id BIGINT NOT NULL AUTO_INCREMENT,
  movie_id INT NOT NULL,
  start_date DATETIME NOT NULL,
  tickets_amount SMALLINT NOT NULL,
  price DECIMAL(15, 2) NOT NULL,
  PRIMARY KEY (id),
  INDEX schedule_movie_id (movie_id ASC, start_date ASC) VISIBLE,
  CONSTRAINT fk_schedule_movies_movie_id_id
    FOREIGN KEY (movie_id)
    REFERENCES movies (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE);

CREATE TABLE IF NOT EXISTS clients (
  id INT NOT NULL AUTO_INCREMENT,
  phone_number VARCHAR(16) NOT NULL,
  first_name VARCHAR(100) NOT NULL,
  last_name VARCHAR(100) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS tickets (
    uuid VARCHAR(36) NOT NULL,
    client_id INT NOT NULL,
    session_id BIGINT NOT NULL,
    seat_row TINYINT NOT NULL,
    seat_number TINYINT NOT NULL,
    PRIMARY KEY (uuid),
    KEY idx_tickets_session_id (session_id),
    KEY idx_tickets_client_id (client_id),
    CONSTRAINT fk_tickets_sessions_client_id_id FOREIGN KEY (client_id)
        REFERENCES clients (id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_tickets_sessions_session_id_id FOREIGN KEY (session_id)
        REFERENCES sessions (id)
        ON DELETE CASCADE ON UPDATE CASCADE
)