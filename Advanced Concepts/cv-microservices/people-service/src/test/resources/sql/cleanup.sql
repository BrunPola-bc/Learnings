DELETE FROM person_project_ids;
DELETE FROM person_skill_ids;
DELETE FROM ps_people;

ALTER TABLE ps_people ALTER COLUMN id RESTART WITH 1;