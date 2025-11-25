INSERT INTO People (FirstName, LastName) VALUES
('John', 'Doe'),
('Jane', 'Smith'),
('Alice', 'Johnson'),
('Bob', 'Brown'),
('Charlie', 'Davis'),
('Emily', 'Clark'),
('Michael', 'Scott');

INSERT INTO Skills (SkillName) VALUES
('Web Development'),
('SQL'),
('Basic MS Office'),
('Advanced Excel'),
('CSS/HTML'),
('JavaScript');

INSERT INTO Projects (ProjectName) VALUES
('Website Redesign'),
('Database Migration'),
('Marketing Campaign'),
('Product Launch'),
('Customer Feedback Analysis'),
('Mobile App Development');

INSERT INTO PersonSkills (PersonID, SkillID) VALUES
(1, 1), (1, 2),
(2, 3), (2, 4),
(3, 1), (3, 5),
(4, 2), (4, 3),
(5, 4);

INSERT INTO PersonProjects (PersonID, ProjectID) VALUES
(1, 1), (1, 2),
(2, 3),
(3, 1), (3, 4),
(4, 2), (4, 5),
(5, 3);

INSERT INTO ProjectSkills (ProjectID, SkillID) VALUES
(1, 1), (1, 3), (1, 5),
(2, 2),
(3, 3), (3, 4),
(4, 1), (4, 4),
(5, 2), (5, 3);
