INSERT INTO member(email, image_url, password, nickname) VALUES('user@example.com', '/uploads/1742197880712_개.png', 'User@1234' '개');
INSERT INTO member(email, image_url, password, nickname) VALUES('hobbit@gmail.com', '', 'User@567', '호빗');
INSERT INTO member(email, image_url, password, nickname) VALUES('student@gmail.com', '', 'User@1111', '학생1');

INSERT INTO post(title,content) VALUES('title1', 'content1');
INSERT INTO post(title,content) VALUES('title2', 'content2');
INSERT INTO post(title,content) VALUES('title3', 'content3');

INSERT INTO post(title,content) VALUES('당신의 인생영화는?', '댓글로 작성해주세요');
INSERT INTO post(title,content) VALUES('당신의 소울 푸드는?', '제곧내');
INSERT INTO post(title,content) VALUES('당신의 취미는?', '댓글 ㄱㄱ');

INSERT INTO comment(post_id,author_id, body) VALUES(4, 1, 'john wick4');
INSERT INTO comment(post_id,author_id, body) VALUES(4, 2, 'top gun');
INSERT INTO comment(post_id,author_id, body) VALUES(4, 3, 'mission impossible');


INSERT INTO comment(post_id,author_id, body) VALUES(5, 3, 'chicken');
INSERT INTO comment(post_id,author_id, body) VALUES(5, 2, 'noodle');
INSERT INTO comment(post_id,author_id, body) VALUES(5, 1, 'barbeque');


INSERT INTO comment(post_id,author_id, body) VALUES(6, 2, 'watch movie on netflix');
INSERT INTO comment(post_id,author_id, body) VALUES(6, 2, 'listen to music');
INSERT INTO comment(post_id,author_id, body) VALUES(6, 1, 'yoga');

