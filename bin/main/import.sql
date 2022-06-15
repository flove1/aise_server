INSERT INTO users(email, password, name, role) values('admin', 'aiseadmin', 'Kirill Shaforostov', 'ADMIN');
INSERT INTO users(email, name, role) values('nurbol@gmail.com', 'Nurbol Seitzhanov', 'STUDENT');
INSERT INTO users(email, name, role) values('dinara@gmail.com', 'Dinara Dandibaeva', 'STUDENT');
INSERT INTO users(email, name, role) values('kirill@gmail.com', 'Kirill Shaforostov', 'STUDENT');
INSERT INTO users(email, name, role) values('olzhas@gmail.com', 'Olzhas Aimukhambetov', 'TEACHER');

INSERT INTO groups(group_name) values("SE-2108");
INSERT INTO groups(group_name) values("CS-2107");
INSERT INTO groups(group_name) values("IT-2102");

INSERT INTO groups_participants(group_id, participant_id) values(1, 2);
INSERT INTO groups_participants(group_id, participant_id) values(1, 3);
INSERT INTO groups_participants(group_id, participant_id) values(1, 4);

INSERT INTO courses(course_name, group_id, lecturer_id, practicant_id, finished) values('OOP', 1, 5, 5, 0);
INSERT INTO courses(course_name, group_id, lecturer_id, practicant_id, finished) values('English', 1, 1, 1, 0);
INSERT INTO courses(course_name, group_id, lecturer_id, practicant_id, finished) values('ADS', 1, 5, 1, 0);
INSERT INTO courses(course_name, group_id, lecturer_id, practicant_id, finished) values('Coding lab', 1, 5, 5, 0);

INSERT INTO rooms(room) values("C1.123");
INSERT INTO rooms(room) values("C1.134");
INSERT INTO rooms(room) values("C1.122");
INSERT INTO rooms(room) values("C1.145");

INSERT INTO rooms(room) values("C2.123");
INSERT INTO rooms(room) values("C2.134");
INSERT INTO rooms(room) values("C2.122");
INSERT INTO rooms(room) values("C2.145");

INSERT INTO rooms(room) values("C3.123");
INSERT INTO rooms(room) values("C3.134");
INSERT INTO rooms(room) values("C3.122");
INSERT INTO rooms(room) values("C3.145");

INSERT INTO rooms(room) values("Online");

INSERT INTO lessons(start_time, end_time, type, week_day, course_id, room) values('08:00', '12:00', 'PRACTICE', 'MONDAY', '1', 'C3.123');
INSERT INTO lessons(start_time, end_time, type, week_day, course_id, room) values('12:00', '18:00', 'PRACTICE', 'MONDAY', '3', 'Online');
INSERT INTO lessons(start_time, end_time, type, week_day, course_id, room) values('18:00', '23:59', 'PRACTICE', 'MONDAY', '4', 'Online');

INSERT INTO lessons(start_time, end_time, type, week_day, course_id, room) values('08:00', '12:00', 'PRACTICE', 'TUESDAY', '2', 'Online');
INSERT INTO lessons(start_time, end_time, type, week_day, course_id, room) values('12:00', '18:00', 'PRACTICE', 'TUESDAY', '1', 'C3.122');
INSERT INTO lessons(start_time, end_time, type, week_day, course_id, room) values('18:00', '23:59', 'PRACTICE', 'TUESDAY', '3', 'Online');

INSERT INTO lessons(start_time, end_time, type, week_day, course_id, room) values('08:00', '12:00', 'PRACTICE', 'WEDNESDAY', '3', 'Online');
INSERT INTO lessons(start_time, end_time, type, week_day, course_id, room) values('12:00', '18:00', 'PRACTICE', 'WEDNESDAY', '2', 'C2.134');
INSERT INTO lessons(start_time, end_time, type, week_day, course_id, room) values('18:00', '23:59', 'PRACTICE', 'WEDNESDAY', '4', 'Online');

INSERT INTO lessons(start_time, end_time, type, week_day, course_id, room) values('08:00', '12:00', 'PRACTICE', 'THURSDAY', '2', 'Online');
INSERT INTO lessons(start_time, end_time, type, week_day, course_id, room) values('12:00', '18:00', 'PRACTICE', 'THURSDAY', '1', 'Online');
INSERT INTO lessons(start_time, end_time, type, week_day, course_id, room) values('18:00', '23:59', 'PRACTICE', 'THURSDAY', '1', 'Online');

INSERT INTO lessons(start_time, end_time, type, week_day, course_id, room) values('08:00', '12:00', 'PRACTICE', 'FRIDAY', '1', 'Online');
INSERT INTO lessons(start_time, end_time, type, week_day, course_id, room) values('12:00', '18:00', 'PRACTICE', 'FRIDAY', '1', 'C1.122');
INSERT INTO lessons(start_time, end_time, type, week_day, course_id, room) values('18:00', '23:59', 'PRACTICE', 'FRIDAY', '1', 'Online');

INSERT INTO lessons(start_time, end_time, type, week_day, course_id, room) values('08:00', '12:00', 'PRACTICE', 'SATURDAY', '2', 'C1.123');
INSERT INTO lessons(start_time, end_time, type, week_day, course_id, room) values('12:00', '18:00', 'PRACTICE', 'SATURDAY', '2', 'Online');
INSERT INTO lessons(start_time, end_time, type, week_day, course_id, room) values('18:00', '23:59', 'PRACTICE', 'SATURDAY', '2', 'Online');