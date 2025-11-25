-- Creates a database exactly like it would have looked like if Week1.sql is run once

CREATE TABLE People (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    FirstName VARCHAR(50) NOT NULL,
    LastName VARCHAR(50) NOT NULL
);

CREATE TABLE Skills (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    SkillName VARCHAR(50) NOT NULL
);

CREATE TABLE Projects (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    ProjectName VARCHAR(50) NOT NULL
);

CREATE TABLE PersonSkills (
    PersonID INT NOT NULL,
    SkillID INT NOT NULL,
    PRIMARY KEY (PersonID, SkillID),
    FOREIGN KEY (PersonID) REFERENCES People(ID),
    FOREIGN KEY (SkillID) REFERENCES Skills(ID)
);

CREATE TABLE PersonProjects (
    PersonID INT NOT NULL,
    ProjectID INT NOT NULL,
    PRIMARY KEY (PersonID, ProjectID),
    FOREIGN KEY (PersonID) REFERENCES People(ID),
    FOREIGN KEY (ProjectID) REFERENCES Projects(ID)
);

CREATE TABLE ProjectSkills (
    ProjectID INT NOT NULL,
    SkillID INT NOT NULL,
    PRIMARY KEY (ProjectID, SkillID),
    FOREIGN KEY (ProjectID) REFERENCES Projects(ID),
    FOREIGN KEY (SkillID) REFERENCES Skills(ID)
);
