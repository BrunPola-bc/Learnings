CREATE TABLE test_table (
  id INT AUTO_INCREMENT PRIMARY KEY,
  message VARCHAR(255)
);

INSERT INTO test_table(message)
VALUES ("First message from init.sql");

CREATE TABLE counters (
  name VARCHAR(50) PRIMARY KEY,
  value INT NOT NULL
);

INSERT INTO counters(name, value)
VALUES ('db_writer_channel_counter', 0);
