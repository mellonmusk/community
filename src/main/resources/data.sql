INSERT INTO member(email, nickname, password, profile_image_url) VALUES('student@gmail.com', '학생1', '$2a$10$mi01col2/HAfZGNwc4oUEOpytQ5aqbQr7og2t43CTuLmlX0A1p51C', 1);
INSERT INTO member(email, nickname, password, profile_image_url) VALUES('ta@gmail.com', '조교1', '$2a$10$mPMJoL13hbEGfBUKjGQdTuZ8XKOgiDi3Se.TalzO0FvfbILKFvpZ.', 2);
INSERT INTO member(email, nickname, password, profile_image_url) VALUES('professor@gmail.com', '교수1', '$2a$10$LCoZGBBDgVe/Tkl67gM79eFi0IO4Wkfgf2ivioWXcHfYGIpPmRxOa', 3);

INSERT INTO post(content, created_at, title, views, author_id) VALUES('질문 질문', '2025-03-20 14:53:27.099725', '질문 있습니다', 1, 1);
INSERT INTO post(content, created_at, title, views, author_id) VALUES('오늘 개교기념일인데 2교시 수업 있나요?', '2025-03-20 14:53:43.719722', '오늘 수업 하나요?', 1, 1);

INSERT INTO images(file_name, file_path, post_id) VALUES('1742449797539_개.png', '/uploads/1742449797539_개.png', null);
INSERT INTO images(file_name, file_path, post_id) VALUES('1742449832290_고양이.png', '/uploads/1742449832290_고양이.png', null);
INSERT INTO images(file_name, file_path, post_id) VALUES('1742449932913_스크린샷 2025-03-20 오후 2.51.46.png', '/uploads/1742449932913_스크린샷 2025-03-20 오후 2.51.46.png', 1);
INSERT INTO images(file_name, file_path, post_id) VALUES('1742450007137_개.png', '/uploads/1742450007137_개.png', null);

INSERT INTO comment(body, created_at, post_id, author_id) VALUES('답변내용입니다.', '2025-03-20 14:54:20.915289', 1, 2);

INSERT INTO likes(post_id,user_id) VALUES(1,2);
