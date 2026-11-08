-- Sprint 0 — Esquema inicial del módulo Gestión Maestra + auditoría
-- Aplicar con: npx supabase db push  (o pegarlo en el SQL Editor de Supabase)

create extension if not exists pgcrypto;

-- ---------------------------------------------------------------------------
-- Funciones transversales
-- ---------------------------------------------------------------------------

create or replace function public.set_updated_at()
returns trigger
language plpgsql
as $$
begin
  new.updated_at = now();
  return new;
end;
$$;

create or replace function public.audit_row()
returns trigger
language plpgsql
security definer
as $$
declare
  v_usuario uuid;
  v_trace   text;
begin
  begin
    v_usuario := nullif(current_setting('app.user_id', true), '')::uuid;
  exception when others then
    v_usuario := null;
  end;
  v_trace := current_setting('app.trace_id', true);

  if tg_op = 'INSERT' then
    insert into public.auditoria
      (tabla_origen, registro_id, usuario_id, accion, valores_nuevos, trace_id)
    values
      (tg_table_name, new.id, v_usuario, 'CREATE', to_jsonb(new), v_trace);
    return new;
  elsif tg_op = 'UPDATE' then
    insert into public.auditoria
      (tabla_origen, registro_id, usuario_id, accion, valores_anteriores, valores_nuevos, trace_id)
    values
      (tg_table_name, new.id, v_usuario, 'UPDATE', to_jsonb(old), to_jsonb(new), v_trace);
    return new;
  elsif tg_op = 'DELETE' then
    insert into public.auditoria
      (tabla_origen, registro_id, usuario_id, accion, valores_anteriores, trace_id)
    values
      (tg_table_name, old.id, v_usuario, 'DELETE', to_jsonb(old), v_trace);
    return old;
  end if;
  return null;
end;
$$;

-- ---------------------------------------------------------------------------
-- Auditoría
-- ---------------------------------------------------------------------------

create table public.auditoria (
  id                 bigint generated always as identity primary key,
  tabla_origen       text        not null,
  registro_id        uuid        not null,
  usuario_id         uuid,
  accion             text        not null check (accion in ('CREATE','UPDATE','DELETE','APPROVE','LOGIN','EXPORT')),
  fecha              timestamptz not null default now(),
  valores_anteriores jsonb,
  valores_nuevos     jsonb,
  trace_id           text
);

create index idx_auditoria_tabla_registro on public.auditoria (tabla_origen, registro_id, fecha desc);
create index idx_auditoria_fecha          on public.auditoria (fecha desc);
create index idx_auditoria_usuario        on public.auditoria (usuario_id, fecha desc);

-- ---------------------------------------------------------------------------
-- Gestión Maestra
-- ---------------------------------------------------------------------------

create table public.proveedores (
  id               uuid primary key default gen_random_uuid(),
  nombre           text not null,
  contacto         text,
  email            text,
  telefono         text,
  condiciones_pago text,
  evaluacion       smallint check (evaluacion between 1 and 5),
  activo           boolean not null default true,
  version          integer not null default 1,
  created_at       timestamptz not null default now(),
  updated_at       timestamptz not null default now()
);

create table public.productos (
  id                    uuid primary key default gen_random_uuid(),
  sku                   text not null unique,
  nombre                text not null,
  categoria             text not null,
  linea                 text not null,
  precio                numeric(14,2) not null check (precio >= 0),
  punto_reorden         integer not null default 0 check (punto_reorden >= 0),
  proveedor_principal_id uuid references public.proveedores(id),
  activo                boolean not null default true,
  version               integer not null default 1,
  created_at            timestamptz not null default now(),
  updated_at            timestamptz not null default now()
);

create index idx_productos_nombre    on public.productos (nombre);
create index idx_productos_categoria on public.productos (categoria);
create index idx_productos_activo    on public.productos (activo) where activo;

create table public.clientes (
  id             uuid primary key default gen_random_uuid(),
  nombre         text not null,
  contacto       text,
  email          text,
  telefono       text,
  direccion      text,
  limite_credito numeric(14,2) not null default 0 check (limite_credito >= 0),
  saldo          numeric(14,2) not null default 0 check (saldo >= 0),
  zona_entrega   text,
  activo         boolean not null default true,
  version        integer not null default 1,
  created_at     timestamptz not null default now(),
  updated_at     timestamptz not null default now()
);

create index idx_clientes_nombre on public.clientes (nombre);
create index idx_clientes_activo on public.clientes (activo) where activo;

create table public.empleados (
  id           uuid primary key default gen_random_uuid(),
  nombre       text not null,
  email        text unique,
  puesto       text not null,
  departamento text not null,
  salario      numeric(14,2) not null check (salario > 0),
  activo       boolean not null default true,
  version      integer not null default 1,
  created_at   timestamptz not null default now(),
  updated_at   timestamptz not null default now()
);

create table public.almacenes (
  id         uuid primary key default gen_random_uuid(),
  codigo     text not null unique,
  nombre     text not null,
  ubicacion  text,
  capacidad  integer not null default 0 check (capacidad >= 0),
  activo     boolean not null default true,
  version    integer not null default 1,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create table public.rutas_entrega (
  id            uuid primary key default gen_random_uuid(),
  nombre        text not null,
  transportista text not null,
  zona          text,
  activo        boolean not null default true,
  version       integer not null default 1,
  created_at    timestamptz not null default now(),
  updated_at    timestamptz not null default now()
);

-- ---------------------------------------------------------------------------
-- Triggers de updated_at y auditoría para todas las tablas maestras
-- ---------------------------------------------------------------------------

do $$
declare
  t text;
begin
  foreach t in array array[
    'proveedores', 'productos', 'clientes', 'empleados', 'almacenes', 'rutas_entrega'
  ] loop
    execute format(
      'create trigger %I before update on public.%I for each row execute function public.set_updated_at()',
      t || '_updated_at', t
    );
    execute format(
      'create trigger %I after insert or update or delete on public.%I for each row execute function public.audit_row()',
      t || '_audit', t
    );
  end loop;
end;
$$;

-- ---------------------------------------------------------------------------
-- RLS: lectura para usuarios autenticados; escritura solo vía API (service_role)
-- ---------------------------------------------------------------------------

do $$
declare
  t text;
begin
  foreach t in array array[
    'proveedores', 'productos', 'clientes', 'empleados', 'almacenes', 'rutas_entrega'
  ] loop
    execute format('alter table public.%I enable row level security', t);
    execute format(
      'create policy %I on public.%I for select to authenticated using (true)',
      t || '_select', t
    );
  end loop;
end;
$$;

alter table public.auditoria enable row level security;
