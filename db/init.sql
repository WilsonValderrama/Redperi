--  Redperi - Esquema + Procedimientos + Seed


------------ TABLAS ----------
CREATE TABLE IF NOT EXISTS users (
    id               BIGSERIAL PRIMARY KEY,
    username         VARCHAR(50)  NOT NULL UNIQUE,
    password         VARCHAR(255) NOT NULL,          -- hash BCrypt
    nombres          VARCHAR(100) NOT NULL,
    apellidos        VARCHAR(100) NOT NULL,
    fecha_nacimiento DATE         NOT NULL,
    alias            VARCHAR(50)  NOT NULL,
    created_at       TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS posts (
    id                BIGSERIAL PRIMARY KEY,
    user_id           BIGINT    NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    mensaje           TEXT      NOT NULL,
    fecha_publicacion TIMESTAMP NOT NULL DEFAULT NOW(),
    created_at        TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS likes (
    id         BIGSERIAL PRIMARY KEY,
    user_id    BIGINT    NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    post_id    BIGINT    NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_like UNIQUE (user_id, post_id)
);

CREATE INDEX IF NOT EXISTS idx_posts_user ON posts(user_id);
CREATE INDEX IF NOT EXISTS idx_likes_post ON likes(post_id);



--  PROCEDURE 1: dar quitar like y devolver el total
CREATE OR REPLACE PROCEDURE sp_toggle_like(
    IN    p_user_id     BIGINT,
    IN    p_post_id     BIGINT,
    INOUT p_total_likes BIGINT DEFAULT NULL
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_exists BOOLEAN;
BEGIN
    SELECT EXISTS(
        SELECT 1 FROM likes WHERE user_id = p_user_id AND post_id = p_post_id
    ) INTO v_exists;

    IF v_exists THEN
        DELETE FROM likes WHERE user_id = p_user_id AND post_id = p_post_id;
    ELSE
        INSERT INTO likes(user_id, post_id) VALUES (p_user_id, p_post_id);
    END IF;

    SELECT COUNT(*) INTO p_total_likes FROM likes WHERE post_id = p_post_id;
END;
$$;


--  PROCEDURE 2: crear una publicación y devolver su id
CREATE OR REPLACE PROCEDURE sp_create_post(
    IN    p_user_id     BIGINT,
    IN    p_mensaje     TEXT,
    INOUT p_new_post_id BIGINT DEFAULT NULL
)
LANGUAGE plpgsql
AS $$
BEGIN
    INSERT INTO posts(user_id, mensaje)
    VALUES (p_user_id, p_mensaje)
    RETURNING id INTO p_new_post_id;
END;
$$;


--  FUNCION: feed de otros usuarios con conteo de likes
--  y si el usuario actual ya dio like. Alimenta la pantalla principal.
CREATE OR REPLACE FUNCTION fn_get_feed(p_current_user_id BIGINT)
RETURNS TABLE (
    post_id           BIGINT,
    mensaje           TEXT,
    fecha_publicacion TIMESTAMP,
    autor_id          BIGINT,
    autor_alias       VARCHAR,
    total_likes       BIGINT,
    liked_by_me       BOOLEAN
)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
    SELECT p.id, p.mensaje, p.fecha_publicacion,
           u.id, u.alias,
           COUNT(l.id),
           COALESCE(BOOL_OR(l.user_id = p_current_user_id), FALSE)
    FROM posts p
    JOIN users u ON u.id = p.user_id
    LEFT JOIN likes l ON l.post_id = p.id
    WHERE p.user_id <> p_current_user_id        -- solo publicaciones de OTROS
    GROUP BY p.id, u.id
    ORDER BY p.fecha_publicacion DESC;
END;
$$;


--  SEED: usuarios de prueba (password = "Password123")
INSERT INTO users (username, password, nombres, apellidos, fecha_nacimiento, alias) VALUES
('wvalderrama', '$2b$10$F1SzRqJXTCVMtv4rhLIUZeG8G5akY/TRMrkjsfVvth6rLTz6qDjUy', 'Wilson', 'Valderrama', '1996-11-10', 'wilson'),
('Rosalba',        '$2b$10$F1SzRqJXTCVMtv4rhLIUZeG8G5akY/TRMrkjsfVvth6rLTz6qDjUy', 'Rosalba',   'bejarano',        '1988-11-02', 'Rosa'),
('Aleja',      '$2b$10$F1SzRqJXTCVMtv4rhLIUZeG8G5akY/TRMrkjsfVvth6rLTz6qDjUy', 'Alejandra',  'Gomez',      '1995-03-21', 'Alej')
ON CONFLICT (username) DO NOTHING;

-- una publicación por usuario 
INSERT INTO posts (user_id, mensaje)
SELECT id, 'Hola, soy ' || alias || ' y esta es mi primera publicacion'
FROM users u
WHERE NOT EXISTS (SELECT 1 FROM posts p WHERE p.user_id = u.id);