insert into courses (mamh, tenmh, sotc, lt, th, loaimonhoc, is_active)
values
('SE101', 'Nhap mon Cong nghe phan mem', 3, 3, 0, 'Bat buoc', true),
('IT007', 'He dieu hanh', 4, 3, 1, 'Bat buoc', true)
on conflict (mamh) do nothing;

insert into admin_users (username, password_hash, active)
values ('admin', '$2b$12$re5JteMv1LPVXIXV5Bas3eRL4oXPExPMsQmQFVDu60OVGo/lytw9i', true)
on conflict (username) do nothing;
