import bcrypt
password = b"admin123"
hash = b"$2b$12$3IpxxJtwI/zV8hYwQc8W9eWRkf.l0yocc3Di5FvA65J0QiJ395WSe"
try:
    print(bcrypt.checkpw(password, hash))
except Exception as e:
    print(f"Error: {e}")
