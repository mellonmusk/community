INSERT INTO member(email, image_id, password, nickname) VALUES('user@example.com', 2, 'User@1234' '개');
INSERT INTO member(email, image_id, password, nickname) VALUES('hobbit@gmail.com', 3, 'User@567', '호빗');
INSERT INTO member(email, image_id, password, nickname) VALUES('student@gmail.com', null, 'User@1111', '학생1');

INSERT INTO post(title,content, likes, title, views, author_id, created_at) VALUES('질문 있습니다.', '오늘 수업하나요?', 1, 8, 1, '2025-03-17 01:33:45.222341');
INSERT INTO post(title,content, likes, title, views, author_id, created_at) VALUES('cors란 무엇인가요?', 'cors와 csrf에 대해 설명해주세요.', 2, 0, 2, '2025-03-17 10:08:57.530307');

INSERT INTO comment(post_id,author_id, body) VALUES(1, 2, '');
INSERT INTO comment(post_id,author_id, body) VALUES(4, 2, 'top gun');
INSERT INTO comment(post_id,author_id, body) VALUES(4, 3, 'mission impossible');

INSERT INTO comment(post_id,author_id, body) VALUES(6, 2, 'watch movie on netflix');
INSERT INTO comment(post_id,author_id, body) VALUES(6, 2, 'listen to music');
INSERT INTO comment(post_id,author_id, body) VALUES(6, 1, 'yoga');

