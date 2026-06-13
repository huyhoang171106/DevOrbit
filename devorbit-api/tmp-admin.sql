INSERT INTO admin_users (username, password_hash, active)
VALUES ('thaian', '$2a$12$KQPsSr66IHC0B81dcVfut.k8ccMNVV7NfxmDYp5wstM9p0gutfa0G', true)
ON CONFLICT (username) DO NOTHING;
