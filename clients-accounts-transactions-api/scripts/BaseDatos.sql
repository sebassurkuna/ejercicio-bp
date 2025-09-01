-- Habilitar extension para generar UUIDs (si no está ya habilitada)
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Crear esquema
CREATE SCHEMA IF NOT EXISTS bank;
SET search_path = bank, public;

CREATE TABLE bank.persona (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  nombre VARCHAR(200) NOT NULL,
  apellido VARCHAR(200),
  genero VARCHAR(50),
  fecha_nacimiento DATE,
  identificacion VARCHAR(100) UNIQUE,
  telefono VARCHAR(50),
  direccion TEXT,
  estado boolean NOT NULL DEFAULT true, 
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE bank.cliente (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  persona_id uuid NOT NULL REFERENCES bank.persona(id) ON DELETE RESTRICT,
  username VARCHAR(100) UNIQUE,
  password VARCHAR(512),
  estado boolean NOT NULL DEFAULT true,
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE INDEX idx_cliente_persona ON bank.cliente(persona_id);

CREATE TABLE bank.cuenta (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  cliente_id uuid NOT NULL REFERENCES bank.cliente(id) ON DELETE CASCADE,
  numero_cuenta BIGSERIAL NOT NULL UNIQUE,
  tipo VARCHAR(50) NOT NULL DEFAULT 'AHORROS',
  saldo_inicial numeric(18,2) NOT NULL DEFAULT 0.00,
  saldo_actual numeric(18,2) NOT NULL DEFAULT 0.00, 
  estado boolean NOT NULL DEFAULT true,
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE INDEX idx_cuenta_cliente ON bank.cuenta(cliente_id);

CREATE TABLE bank.movimiento (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  cuenta_id uuid NOT NULL REFERENCES bank.cuenta(id) ON DELETE CASCADE,
  fecha timestamptz NOT NULL DEFAULT now(),
  tipo VARCHAR(50) NOT NULL,
  valor numeric(18,2) NOT NULL,
  saldo_post_movimiento numeric(18,2) NOT NULL,
  created_at timestamptz NOT NULL DEFAULT now()
);

CREATE INDEX idx_movimiento_cuenta_fecha ON bank.movimiento(cuenta_id, fecha DESC);
CREATE INDEX idx_movimiento_fecha ON bank.movimiento(fecha);

-- ======================================================================
-- DATOS DE EJEMPLO
-- ======================================================================

-- Insertar personas de ejemplo
INSERT INTO bank.persona (id, nombre, apellido, genero, fecha_nacimiento, identificacion, telefono, direccion, estado) VALUES
('550e8400-e29b-41d4-a716-446655440001', 'José', 'Lema', 'Masculino', '1985-03-15', '1723456789', '098254785', 'Otavalo sn y principal', true),
('550e8400-e29b-41d4-a716-446655440002', 'Marianela', 'Montalvo', 'Femenino', '1990-07-22', '1723456790', '097548965', 'Amazonas y NNUU', true);

-- Insertar clientes de ejemplo
INSERT INTO bank.cliente (id, persona_id, username, password, estado) VALUES
('660e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440001', 'jlema', '$2a$10$X5wFWxnvLRnl.XdKWnA/LOpOH.RfzOqJpzAOp1qOr8JfUzq1YHuHy', true),
('660e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440002', 'mmontalvo', '$2a$10$Y6xGXyoqLSom.YeLWoB/MOqQI.SgzPrKqaBA1r2Ps9KgVaq2ZIvIz', true);

-- Insertar cuentas de ejemplo
INSERT INTO bank.cuenta (id, cliente_id, numero_cuenta, tipo, saldo_inicial, saldo_actual, estado) VALUES
('770e8400-e29b-41d4-a716-446655440001', '660e8400-e29b-41d4-a716-446655440001', 478758, 'AHORROS', 2000.00, 1425.00, true),
('770e8400-e29b-41d4-a716-446655440002', '660e8400-e29b-41d4-a716-446655440002', 225487, 'CORRIENTE', 100.00, 700.00, true);

-- Insertar movimientos de ejemplo para José Lema (Cuenta 478758)
INSERT INTO bank.movimiento (id, cuenta_id, fecha, tipo, valor, saldo_post_movimiento) VALUES
('880e8400-e29b-41d4-a716-446655440001', '770e8400-e29b-41d4-a716-446655440001', '2024-01-10 09:00:00', 'DEBITO', -575.00, 1425.00),
('880e8400-e29b-41d4-a716-446655440002', '770e8400-e29b-41d4-a716-446655440001', '2024-01-15 14:30:00', 'CREDITO', 0.00, 1425.00);

-- Insertar movimientos de ejemplo para Marianela Montalvo (Cuenta 225487)
INSERT INTO bank.movimiento (id, cuenta_id, fecha, tipo, valor, saldo_post_movimiento) VALUES
('880e8400-e29b-41d4-a716-446655440003', '770e8400-e29b-41d4-a716-446655440002', '2024-01-12 11:15:00', 'CREDITO', 600.00, 700.00),
('880e8400-e29b-41d4-a716-446655440004', '770e8400-e29b-41d4-a716-446655440002', '2024-01-18 16:45:00', 'DEBITO', 0.00, 700.00);
