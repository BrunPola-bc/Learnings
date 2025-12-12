DROP DATABASE IF EXISTS week1database;

CREATE DATABASE week1database;
USE week1database;

CREATE TABLE People (
    ID BIGINT PRIMARY KEY AUTO_INCREMENT,
    FirstName VARCHAR(50) NOT NULL,
    LastName VARCHAR(50) NOT NULL
);

CREATE TABLE Skills (
    ID BIGINT PRIMARY KEY AUTO_INCREMENT,
    SkillName VARCHAR(50) NOT NULL
);

CREATE TABLE Projects (
    ID BIGINT PRIMARY KEY AUTO_INCREMENT,
    ProjectName VARCHAR(50) NOT NULL
);

CREATE TABLE PersonSkills (
    PersonID BIGINT,
    SkillID BIGINT,
    PRIMARY KEY (PersonID, SkillID),
    FOREIGN KEY (PersonID) REFERENCES People(ID),
    FOREIGN KEY (SkillID) REFERENCES Skills(ID)
);

CREATE TABLE PersonProjects (
    PersonID BIGINT,
    ProjectID BIGINT,
    PRIMARY KEY (PersonID, ProjectID),
    FOREIGN KEY (PersonID) REFERENCES People(ID),
    FOREIGN KEY (ProjectID) REFERENCES Projects(ID)
);

CREATE TABLE ProjectSkills (
    ProjectID BIGINT,
    SkillID BIGINT,
    PRIMARY KEY (ProjectID, SkillID),
    FOREIGN KEY (ProjectID) REFERENCES Projects(ID),
    FOREIGN KEY (SkillID) REFERENCES Skills(ID)
);

INSERT INTO People (FirstName, LastName) 
VALUES
('John', 'Doe'),
('Jane', 'Smith'),
('Alice', 'Johnson'),
('Bob', 'Brown'),
('Charlie', 'Davis'),
('Emily', 'Clark'),
('Michael', 'Scott');

INSERT INTO Skills (SkillName) 
VALUES
('Web Development'),
('SQL'),
('Basic MS Office'),
('Advanced Excel'),
('CSS/HTML'),
('JavaScript');

INSERT INTO Projects (ProjectName) 
VALUES
('Website Redesign'),
('Database Migration'),
('Marketing Campaign'),
('Product Launch'),
('Customer Feedback Analysis'),
('Mobile App Development');

INSERT INTO PersonSkills (PersonID, SkillID) 
VALUES
(1, 1), (1, 2),
(2, 3), (2, 4),
(3, 1), (3, 5),
(4, 2), (4, 3),
(5, 4);

INSERT INTO PersonProjects (PersonID, ProjectID) 
VALUES
(1, 1), (1, 2),
(2, 3),
(3, 1), (3, 4),
(4, 2), (4, 5),
(5, 3);

INSERT INTO ProjectSkills (ProjectID, SkillID) 
VALUES
(1, 1), (1,3), (1, 5),
(2, 2),
(3, 3), (3, 4),
(4, 1), (4, 4),
(5, 2), (5, 3);

CREATE USER	IF NOT EXISTS 'TestingUser'@'localhost' IDENTIFIED BY 'TestingUserPass';
GRANT ALL PRIVILEGES ON week1database.* TO 'TestingUser'@'localhost';
FLUSH PRIVILEGES;
