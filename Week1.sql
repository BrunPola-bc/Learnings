DROP DATABASE IF EXISTS week1database;

CREATE DATABASE week1database;
USE week1database;

CREATE TABLE People (
    ID BIGINT PRIMARY KEY AUTO_INCREMENT,
    FirstName VARCHAR(50) NOT NULL,
    LastName VARCHAR(50) NOT NULL
    -- ,Email VARCHAR(100),
    -- PhoneNumber VARCHAR(15)
);

CREATE TABLE Skills (
    ID BIGINT PRIMARY KEY AUTO_INCREMENT,
    SkillName VARCHAR(50) NOT NULL
    -- ,Descript VARCHAR(255)
);

CREATE TABLE Projects (
    ID BIGINT PRIMARY KEY AUTO_INCREMENT,
    ProjectName VARCHAR(50) NOT NULL
    -- ,Descript VARCHAR(255),
    -- StartDate DATE,
    -- EndDate DATE
);

-- Many-to-Many relationship between People and Skills
-- Who has which skills
CREATE TABLE PersonSkills (
    PersonID BIGINT,
    SkillID BIGINT,
    -- ProficiencyLevel ENUM('Beginner', 'Intermediate', 'Advanced', 'Expert'),
    PRIMARY KEY (PersonID, SkillID),
    FOREIGN KEY (PersonID) REFERENCES People(ID),
    FOREIGN KEY (SkillID) REFERENCES Skills(ID)
);

-- Many-to-Many relationship between People and Projects
-- Who is working on which projects
CREATE TABLE PersonProjects (
    PersonID BIGINT,
    ProjectID BIGINT,
    -- Role VARCHAR(50),
    PRIMARY KEY (PersonID, ProjectID),
    FOREIGN KEY (PersonID) REFERENCES People(ID),
    FOREIGN KEY (ProjectID) REFERENCES Projects(ID)
);

-- Many-to-Many relationship between Skills and Projects
-- Which skills are required for which projects
CREATE TABLE ProjectSkills (
    ProjectID BIGINT,
    SkillID BIGINT,
    PRIMARY KEY (ProjectID, SkillID),
    FOREIGN KEY (ProjectID) REFERENCES Projects(ID),
    FOREIGN KEY (SkillID) REFERENCES Skills(ID)
);

-- BP?? Is it better to have a composite or a surrogate primary key in the junction tables?

-- Filling in some sample data
INSERT INTO People (FirstName, LastName) 
VALUES
('John', 'Doe'),
('Jane', 'Smith'),
('Alice', 'Johnson'),
('Bob', 'Brown'),
('Charlie', 'Davis');

INSERT INTO Skills (SkillName) 
VALUES
('Web Development'),
('SQL'),
('Basic MS Office'),
('Advanced Excel'),
('CSS/HTML');

INSERT INTO Projects (ProjectName) 
VALUES
('Website Redesign'),
('Database Migration'),
('Marketing Campaign'),
('Product Launch'),
('Customer Feedback Analysis');

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

-- 1. Add new people, skills and projects to the database.
INSERT INTO People (FirstName, LastName) VALUES ('Emily', 'Clark');

SELECT * FROM people;

DELIMITER $$
CREATE PROCEDURE AddPerson (IN p_FName VARCHAR(50), IN p_LName VARCHAR(50))
BEGIN
    INSERT INTO People (FirstName, LastName) VALUES (p_FName, p_LName);
END$$
DELIMITER ;

CALL AddPerson('Michael', 'Scott');

INSERT INTO Skills (SkillName) VALUES ('JavaScript');
INSERT INTO Projects (ProjectName) VALUES ('Mobile App Development');

-- 2. Generate a list of all people that have a specific skill.
DELIMITER $$
CREATE PROCEDURE GetPeopleBySkill (IN p_SkillName VARCHAR(50))
BEGIN
	SELECT p.FirstName, p.LastName
	FROM People p
	JOIN PersonSkills ps ON p.ID = ps.PersonID
	JOIN Skills s ON ps.SkillID = s.ID
	WHERE s.SkillName = p_SkillName;
END$$
DELIMITER ;

CALL GetPeopleBySkill ('SQL');

-- 3. Search for people by skill or project.
DELIMITER $$
CREATE PROCEDURE SearchPeople (p_SearchTerm VARCHAR(50))
BEGIN
	SELECT p.FirstName, p.LastName, s.SkillName AS Term
	FROM People p
	JOIN PersonSkills ps ON p.ID = ps.PersonID
	JOIN Skills s ON ps.SkillID = s.ID
	WHERE s.SkillName LIKE CONCAT('%', p_SearchTerm, '%')
	UNION
	SELECT p.FirstName, p.LastName, pr.ProjectName
	FROM People p
	JOIN PersonProjects pp ON p.ID = pp.PersonID
	JOIN Projects pr ON pp.ProjectID = pr.ID
	WHERE pr.ProjectName LIKE CONCAT('%', p_SearchTerm, '%');
END$$
DELIMITER ;

CALL SearchPeople ('Web');

-- 4. What skills aren't being accounted for in a specific project?

SELECT DISTINCT s.ID, s.SkillName
FROM Skills s
JOIN ProjectSkills prs ON s.ID = prs.SkillID
-- Filter skills by project name
WHERE prs.ProjectID = (
    SELECT ID FROM Projects
    WHERE ProjectName = "Website Redesign" -- Variable if using a procedure
)
-- Then remove
AND s.ID NOT IN (
    -- Skills that people working on the project have
    SELECT ps.SkillID
    FROM PersonSkills ps
    JOIN PersonProjects pp ON ps.PersonID = pp.PersonID
    WHERE pp.ProjectID = prs.ProjectID 
);

CREATE USER	IF NOT EXISTS 'TestingUser'@'localhost' IDENTIFIED BY 'TestingUserPass';
GRANT ALL PRIVILEGES ON week1database.* TO 'TestingUser'@'localhost';
FLUSH PRIVILEGES;
