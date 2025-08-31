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