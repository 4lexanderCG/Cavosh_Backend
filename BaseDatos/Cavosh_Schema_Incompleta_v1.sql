DROP DATABASE IF EXISTS cavosh_cafe_db;
CREATE DATABASE cavosh_cafe_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE cavosh_cafe_db;

-- ============================================================
-- 1. TABLAS PRINCIPALES
-- ============================================================

-- Roles de usuario
CREATE TABLE roles (
    id          TINYINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    nombre      VARCHAR(30) NOT NULL UNIQUE
) ENGINE=InnoDB;
INSERT INTO roles (nombre) VALUES ('cliente'), ('barista'), ('admin');

-- Clientes y Personal
CREATE TABLE usuarios (
    id                     INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    rol_id                 TINYINT UNSIGNED NOT NULL DEFAULT 1,
    nombre                 VARCHAR(100) NOT NULL,
    email                  VARCHAR(150) NOT NULL UNIQUE,
    telefono               VARCHAR(20),
    password               VARCHAR(255) NOT NULL,
    puntos_acumulados      INT UNSIGNED DEFAULT 0,
    notificaciones_activas BOOLEAN DEFAULT TRUE,
    creado_en              TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    actualizado_en         TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_usuarios_rol FOREIGN KEY (rol_id) REFERENCES roles(id) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- Direcciones de Entrega
CREATE TABLE direcciones (
    id            INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    usuario_id    INT UNSIGNED NOT NULL,
    alias         VARCHAR(50) NOT NULL, -- ej: 'Casa', 'Oficina'
    direccion     VARCHAR(255) NOT NULL,
    referencia    VARCHAR(255),
    es_principal  BOOLEAN DEFAULT FALSE,
    creado_en     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_direcciones_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    INDEX idx_direcciones_usuario (usuario_id)
) ENGINE=InnoDB;

-- Categorías
CREATE TABLE categorias (
    id          INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    nombre      VARCHAR(50) NOT NULL UNIQUE,
    tipo        ENUM('Bebidas', 'Comida') NOT NULL DEFAULT 'Bebidas',
    activo      BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB;

-- Menú de Productos
CREATE TABLE productos (
    id              INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    categoria_id    INT UNSIGNED NOT NULL,
    nombre          VARCHAR(150) NOT NULL,
    descripcion     VARCHAR(255),
    precio          DECIMAL(10,2) NOT NULL CHECK (precio >= 0),
    imagen_url      VARCHAR(255),
    disponible      BOOLEAN NOT NULL DEFAULT TRUE,
    creado_en       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    actualizado_en  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_productos_categoria FOREIGN KEY (categoria_id) REFERENCES categorias(id) ON DELETE RESTRICT,
    INDEX idx_productos_categoria (categoria_id),
    INDEX idx_productos_disponible (disponible)
) ENGINE=InnoDB;

-- Pedidos
CREATE TABLE pedidos (
    id              INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    usuario_id      INT UNSIGNED NOT NULL,
    direccion_id    INT UNSIGNED NULL,
    tipo_entrega    ENUM('delivery', 'para_llevar', 'en_local') NOT NULL DEFAULT 'para_llevar',
    estado          ENUM('pendiente', 'en_preparacion', 'listo', 'en_camino', 'entregado', 'cancelado') NOT NULL DEFAULT 'pendiente',
    metodo_pago     ENUM('efectivo', 'tarjeta', 'billetera_digital') NOT NULL,
    costo_envio     DECIMAL(10,2) DEFAULT 0.00 CHECK (costo_envio >= 0),
    subtotal        DECIMAL(10,2) NOT NULL CHECK (subtotal >= 0),
    total           DECIMAL(10,2) NOT NULL CHECK (total >= 0),
    creado_en       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    actualizado_en  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_pedidos_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE RESTRICT,
    CONSTRAINT fk_pedidos_direccion FOREIGN KEY (direccion_id) REFERENCES direcciones(id) ON DELETE RESTRICT,
    INDEX idx_pedidos_usuario (usuario_id),
    INDEX idx_pedidos_estado (estado),
    INDEX idx_pedidos_creado (creado_en)
) ENGINE=InnoDB;

-- Detalle de Pedidos
CREATE TABLE pedido_items (
    id              INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    pedido_id       INT UNSIGNED NOT NULL,
    producto_id     INT UNSIGNED NOT NULL,
    cantidad        SMALLINT UNSIGNED NOT NULL DEFAULT 1 CHECK (cantidad > 0),
    precio_unitario DECIMAL(10,2) NOT NULL CHECK (precio_unitario >= 0),
    notas           VARCHAR(255),
    CONSTRAINT fk_items_pedido FOREIGN KEY (pedido_id) REFERENCES pedidos(id) ON DELETE CASCADE,
    CONSTRAINT fk_items_producto FOREIGN KEY (producto_id) REFERENCES productos(id) ON DELETE RESTRICT,
    INDEX idx_items_pedido (pedido_id),
    INDEX idx_items_producto (producto_id)
) ENGINE=InnoDB;

-- Productos Favoritos
CREATE TABLE favoritos (
    usuario_id      INT UNSIGNED NOT NULL,
    producto_id     INT UNSIGNED NOT NULL,
    creado_en       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (usuario_id, producto_id),
    CONSTRAINT fk_favoritos_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    CONSTRAINT fk_favoritos_producto FOREIGN KEY (producto_id) REFERENCES productos(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Carrito de Compras
CREATE TABLE carrito (
    id              INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    usuario_id      INT UNSIGNED NOT NULL,
    producto_id     INT UNSIGNED NOT NULL,
    cantidad        SMALLINT UNSIGNED NOT NULL DEFAULT 1 CHECK (cantidad > 0),
    notas           VARCHAR(255),
    creado_en       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_carrito_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    CONSTRAINT fk_carrito_producto FOREIGN KEY (producto_id) REFERENCES productos(id) ON DELETE CASCADE,
    INDEX idx_carrito_usuario (usuario_id)
) ENGINE=InnoDB;

-- ============================================================
-- 2. PROCEDIMIENTOS ALMACENADOS
-- ============================================================

DELIMITER $$

-- ------------------------------------------------------------
-- MÓDULO: AUTENTICACIÓN Y PERFIL DE USUARIO
-- ------------------------------------------------------------

-- Registro de usuario
DROP PROCEDURE IF EXISTS sp_registrar_usuario$$
CREATE PROCEDURE sp_registrar_usuario(
    IN  p_nombre          VARCHAR(100),
    IN  p_email           VARCHAR(150),
    IN  p_telefono        VARCHAR(20),
    IN  p_password        VARCHAR(255),
    OUT p_usuario_id      INT UNSIGNED
)
BEGIN
    IF EXISTS (SELECT 1 FROM usuarios WHERE email = p_email) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El correo electrónico ya se encuentra registrado';
    ELSE
        INSERT INTO usuarios (nombre, email, telefono, password, rol_id)
        VALUES (p_nombre, p_email, p_telefono, p_password, 1);

        SET p_usuario_id = LAST_INSERT_ID();
    END IF;
END$$

-- Iniciar sesion de un usuario
DROP PROCEDURE IF EXISTS sp_iniciar_sesion$$
CREATE PROCEDURE sp_iniciar_sesion(
    IN p_email VARCHAR(150)
)
BEGIN
    SELECT 
        u.id,
        u.nombre,
        u.email,
        u.telefono,
        u.password,
        r.nombre AS rol,
        u.puntos_acumulados,
        u.notificaciones_activas
    FROM usuarios u
    JOIN roles r ON r.id = u.rol_id
    WHERE u.email = p_email;
END$$

-- Recuperar Contraseña
DROP PROCEDURE IF EXISTS sp_recuperar_password$$
CREATE PROCEDURE sp_recuperar_password(
    IN p_email          VARCHAR(150),
    IN p_nuevo_password VARCHAR(255)
)
BEGIN
    IF EXISTS (SELECT 1 FROM usuarios WHERE email = p_email) THEN
        UPDATE usuarios 
        SET password = p_nuevo_password
        WHERE email = p_email;
    ELSE
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'No existe una cuenta registrada con este correo electrónico';
    END IF;
END$$

-- Actualizar Perfil del usuario
DROP PROCEDURE IF EXISTS sp_actualizar_perfil$$
CREATE PROCEDURE sp_actualizar_perfil(
    IN p_usuario_id INT UNSIGNED,
    IN p_nombre     VARCHAR(100),
    IN p_telefono   VARCHAR(20)
)
BEGIN
    UPDATE usuarios 
    SET nombre = p_nombre,
        telefono = p_telefono
    WHERE id = p_usuario_id;
END$$

-- Obtener el perfil del usuario
DROP PROCEDURE IF EXISTS sp_obtener_perfil_usuario$$
CREATE PROCEDURE sp_obtener_perfil_usuario(
    IN p_usuario_id INT UNSIGNED
)
BEGIN
    SELECT 
        u.id,
        u.nombre,
        u.email,
        u.telefono,
        r.nombre AS rol,
        u.puntos_acumulados,
        u.notificaciones_activas,
        u.creado_en AS fecha_registro
    FROM usuarios u
    JOIN roles r ON r.id = u.rol_id
    WHERE u.id = p_usuario_id;
END$$

-- ------------------------------------------------------------
-- MÓDULO B: DIRECCIONES (LOCATION)
-- ------------------------------------------------------------

-- Agrega un nuevo direccion
DROP PROCEDURE IF EXISTS sp_agregar_direccion$$
CREATE PROCEDURE sp_agregar_direccion(
    IN p_usuario_id INT UNSIGNED,
    IN p_alias      VARCHAR(50),
    IN p_direccion  VARCHAR(255),
    IN p_referencia VARCHAR(255),
    IN p_principal  BOOLEAN
)
BEGIN
    IF p_principal = TRUE THEN
        UPDATE direcciones SET es_principal = FALSE WHERE usuario_id = p_usuario_id;
    END IF;

    INSERT INTO direcciones (usuario_id, alias, direccion, referencia, es_principal)
    VALUES (p_usuario_id, p_alias, p_direccion, p_referencia, p_principal);
END$$

-- ------------------------------------------------------------
-- MÓDULO C: CARRITO Y FAVORITOS
-- ------------------------------------------------------------

-- Agregar productos al carritos
DROP PROCEDURE IF EXISTS sp_agregar_al_carrito$$
CREATE PROCEDURE sp_agregar_al_carrito(
    IN p_usuario_id  INT UNSIGNED,
    IN p_producto_id INT UNSIGNED,
    IN p_cantidad    SMALLINT UNSIGNED,
    IN p_notas       VARCHAR(255)
)
BEGIN
    DECLARE v_disponible BOOLEAN;
    DECLARE v_carrito_id INT UNSIGNED;

    SELECT disponible INTO v_disponible FROM productos WHERE id = p_producto_id;

    IF v_disponible IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El producto no existe';
    ELSEIF v_disponible = FALSE THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El producto no está disponible';
    END IF;

    SELECT id INTO v_carrito_id
    FROM carrito
    WHERE usuario_id = p_usuario_id
      AND producto_id = p_producto_id
      AND notas <=> p_notas
    LIMIT 1;

    IF v_carrito_id IS NOT NULL THEN
        UPDATE carrito SET cantidad = cantidad + p_cantidad WHERE id = v_carrito_id;
    ELSE
        INSERT INTO carrito (usuario_id, producto_id, cantidad, notas)
        VALUES (p_usuario_id, p_producto_id, p_cantidad, p_notas);
    END IF;
END$$

-- Obtener el carrito del usuario
DROP PROCEDURE IF EXISTS sp_obtener_carrito_usuario$$
CREATE PROCEDURE sp_obtener_carrito_usuario(
    IN p_usuario_id INT UNSIGNED
)
BEGIN
    SELECT 
        c.id AS carrito_id,
        p.id AS producto_id,
        p.nombre,
        p.precio AS precio_unitario,
        c.cantidad,
        (c.cantidad * p.precio) AS subtotal,
        c.notas,
        p.imagen_url
    FROM carrito c
    JOIN productos p ON p.id = c.producto_id
    WHERE c.usuario_id = p_usuario_id;
END$$

-- Poder gestionar los favoritos del usuario
DROP PROCEDURE IF EXISTS sp_gestionar_favorito$$
CREATE PROCEDURE sp_gestionar_favorito(
    IN p_usuario_id  INT UNSIGNED,
    IN p_producto_id INT UNSIGNED
)
BEGIN
    IF EXISTS (SELECT 1 FROM favoritos WHERE usuario_id = p_usuario_id AND producto_id = p_producto_id) THEN
        DELETE FROM favoritos WHERE usuario_id = p_usuario_id AND producto_id = p_producto_id;
    ELSE
        INSERT INTO favoritos (usuario_id, producto_id) VALUES (p_usuario_id, p_producto_id);
    END IF;
END$$

-- ------------------------------------------------------------
-- MÓDULO D: PEDIDOS
-- ------------------------------------------------------------

-- Crear pedido de un producto
DROP PROCEDURE IF EXISTS sp_crear_pedido$$
CREATE PROCEDURE sp_crear_pedido(
    IN  p_usuario_id   INT UNSIGNED,
    IN  p_direccion_id INT UNSIGNED,
    IN  p_tipo_entrega ENUM('delivery', 'para_llevar', 'en_local'),
    IN  p_metodo_pago  ENUM('efectivo', 'tarjeta', 'billetera_digital'),
    IN  p_costo_envio  DECIMAL(10,2),
    OUT p_pedido_id    INT UNSIGNED
)
BEGIN
    DECLARE v_subtotal DECIMAL(10,2);
    DECLARE v_total DECIMAL(10,2);

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    START TRANSACTION;

    SELECT COALESCE(SUM(c.cantidad * p.precio), 0) INTO v_subtotal
    FROM carrito c
    JOIN productos p ON p.id = c.producto_id
    WHERE c.usuario_id = p_usuario_id AND p.disponible = TRUE;

    IF v_subtotal = 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El carrito está vacío o contiene productos agotados';
    END IF;

    IF p_tipo_entrega = 'delivery' AND p_direccion_id IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Debe indicar una dirección para la entrega';
    END IF;

    SET v_total = v_subtotal + IFNULL(p_costo_envio, 0.00);

    INSERT INTO pedidos (usuario_id, direccion_id, tipo_entrega, estado, metodo_pago, costo_envio, subtotal, total)
    VALUES (p_usuario_id, p_direccion_id, p_tipo_entrega, 'pendiente', p_metodo_pago, IFNULL(p_costo_envio, 0.00), v_subtotal, v_total);

    SET p_pedido_id = LAST_INSERT_ID();

    INSERT INTO pedido_items (pedido_id, producto_id, cantidad, precio_unitario, notas)
    SELECT p_pedido_id, c.producto_id, c.cantidad, p.precio, c.notas
    FROM carrito c
    JOIN productos p ON p.id = c.producto_id
    WHERE c.usuario_id = p_usuario_id AND p.disponible = TRUE;

    DELETE FROM carrito WHERE usuario_id = p_usuario_id;

    COMMIT;
END$$

-- Actualizar el estado de un pedido
DROP PROCEDURE IF EXISTS sp_actualizar_estado_pedido$$
CREATE PROCEDURE sp_actualizar_estado_pedido(
    IN p_pedido_id    INT UNSIGNED,
    IN p_nuevo_estado ENUM('pendiente', 'en_preparacion', 'listo', 'en_camino', 'entregado', 'cancelado')
)
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pedidos WHERE id = p_pedido_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El pedido especificado no existe';
    ELSE
        UPDATE pedidos SET estado = p_nuevo_estado WHERE id = p_pedido_id;
    END IF;
END$$

DELIMITER ;

-- ============================================================
-- 3. DATOS INICIALES DE EJEMPLO
-- ============================================================

INSERT INTO categorias (nombre, tipo) VALUES 
('Café Caliente', 'Bebidas'),
('Café Frío', 'Bebidas'),
('Té e Infusiones', 'Bebidas'),
('Pastelería', 'Comida'),
('Sándwiches', 'Comida');

INSERT INTO productos (categoria_id, nombre, descripcion, precio, disponible) VALUES 
(1, 'Espresso', 'Shot concentrado de café arábica', 2.50, TRUE),
(1, 'Cappuccino', 'Espresso con leche vaporizada y crema', 3.50, TRUE),
(2, 'Cold Brew', 'Extracción en frío durante 12 horas', 3.80, TRUE),
(4, 'Croissant de Mantequilla', 'Croissant horneado estilo francés', 2.20, TRUE);

-- ============================================================
-- SCRIPT DE INSERCIÓN PARA UN SOLO USUARIO DE PRUEBA
-- ============================================================

-- Insertar el Cliente
INSERT INTO usuarios (nombre, email, telefono, password, rol_id, puntos_acumulados) 
VALUES ('Ana Torres', 'ana.torres@gmail.com', '998877665', '$2y$10$hashEjemploPass123', 1, 50);

-- Capturar el ID del usuario recién creado
SET @usuario_id = LAST_INSERT_ID();

-- Direcciones del Usuario
INSERT INTO direcciones (usuario_id, alias, direccion, referencia, es_principal) 
VALUES (@usuario_id, 'Casa', 'Av. Javier Prado Este 1234, Dpto 402', 'Frente al parque San Martín', TRUE);

-- Capturar el ID de la dirección creada
SET @direccion_id = LAST_INSERT_ID();

-- Producto Favorito del Usuario (Cappuccino)
INSERT INTO favoritos (usuario_id, producto_id) 
VALUES (@usuario_id, 2);

-- Producto actualmente en el Carrito del Usuario (Cold Brew)
INSERT INTO carrito (usuario_id, producto_id, cantidad, notas) 
VALUES (@usuario_id, 3, 1, 'Poco hielo y sin azúcar');

-- Registro de un Pedido Histórico del Usuario
INSERT INTO pedidos (usuario_id, direccion_id, tipo_entrega, estado, metodo_pago, costo_envio, subtotal, total) 
VALUES (@usuario_id, @direccion_id, 'delivery', 'entregado', 'tarjeta', 2.50, 5.70, 8.20);

-- Capturar el ID del pedido generado
SET @pedido_id = LAST_INSERT_ID();

-- Detalle de los artículos comprados en ese Pedido
INSERT INTO pedido_items (pedido_id, producto_id, cantidad, precio_unitario, notas) VALUES 
(@pedido_id, 2, 1, 3.50, 'Con leche de almendras'),
(@pedido_id, 4, 1, 2.20, 'Servir tibio');