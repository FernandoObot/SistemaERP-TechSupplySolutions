-- Sprint 2 — Compras: órdenes, recepción, inventario básico y cuentas por pagar

create sequence if not exists public.oc_folio_seq;

create or replace function public.next_oc_folio()
returns text
language sql
as $$
  select 'OC-' || to_char(now(), 'YYYYMMDD') || '-' ||
         lpad(nextval('public.oc_folio_seq')::text, 4, '0');
$$;

-- ---------------------------------------------------------------------------
-- Órdenes de compra
-- ---------------------------------------------------------------------------

create table public.ordenes_compra (
  id            uuid primary key default gen_random_uuid(),
  folio         text not null unique default public.next_oc_folio(),
  proveedor_id  uuid not null references public.proveedores(id),
  fecha         timestamptz not null default now(),
  estado        text not null default 'ENVIADA'
                check (estado in ('BORRADOR','ENVIADA','PARCIAL','RECIBIDA','CANCELADA')),
  total         numeric(14,2) not null default 0 check (total >= 0),
  observaciones text,
  version       integer not null default 1,
  created_at    timestamptz not null default now(),
  updated_at    timestamptz not null default now()
);

create index idx_oc_proveedor on public.ordenes_compra (proveedor_id, fecha desc);
create index idx_oc_estado    on public.ordenes_compra (estado);
create index idx_oc_fecha     on public.ordenes_compra (fecha desc);

create table public.detalles_oc (
  id               uuid primary key default gen_random_uuid(),
  orden_compra_id  uuid not null references public.ordenes_compra(id) on delete cascade,
  producto_id      uuid not null references public.productos(id),
  cantidad         integer not null check (cantidad > 0),
  precio_unitario  numeric(14,2) not null check (precio_unitario >= 0),
  subtotal         numeric(14,2) not null check (subtotal >= 0),
  created_at       timestamptz not null default now()
);

create index idx_detalles_oc_orden on public.detalles_oc (orden_compra_id);
create index idx_detalles_oc_prod  on public.detalles_oc (producto_id);

-- ---------------------------------------------------------------------------
-- Recepción de bienes
-- ---------------------------------------------------------------------------

create table public.recepciones (
  id               uuid primary key default gen_random_uuid(),
  orden_compra_id  uuid not null references public.ordenes_compra(id),
  almacen_id       uuid not null references public.almacenes(id),
  fecha            timestamptz not null default now(),
  estado           text not null default 'RECIBIDA'
                   check (estado in ('RECIBIDA','PARCIAL')),
  observaciones    text,
  created_at       timestamptz not null default now(),
  updated_at       timestamptz not null default now()
);

create index idx_recepciones_orden on public.recepciones (orden_compra_id, fecha desc);

create table public.detalles_recepcion (
  id                uuid primary key default gen_random_uuid(),
  recepcion_id      uuid not null references public.recepciones(id) on delete cascade,
  detalle_oc_id     uuid not null references public.detalles_oc(id),
  producto_id       uuid not null references public.productos(id),
  cantidad_recibida integer not null check (cantidad_recibida > 0),
  created_at        timestamptz not null default now()
);

create index idx_detalles_recepcion_rec  on public.detalles_recepcion (recepcion_id);
create index idx_detalles_recepcion_det  on public.detalles_recepcion (detalle_oc_id);

-- ---------------------------------------------------------------------------
-- Inventario básico (entradas por recepción)
-- ---------------------------------------------------------------------------

create table public.movimientos_inventario (
  id          uuid primary key default gen_random_uuid(),
  almacen_id  uuid not null references public.almacenes(id),
  producto_id uuid not null references public.productos(id),
  tipo        text not null check (tipo in ('ENTRADA','SALIDA','AJUSTE')),
  cantidad    integer not null check (cantidad > 0),
  referencia  text,
  fecha       timestamptz not null default now(),
  usuario_id  uuid
);

create index idx_mov_inv_producto on public.movimientos_inventario (producto_id, fecha desc);
create index idx_mov_inv_almacen  on public.movimientos_inventario (almacen_id, fecha desc);

create table public.stock (
  producto_id uuid not null references public.productos(id),
  almacen_id  uuid not null references public.almacenes(id),
  cantidad    integer not null default 0 check (cantidad >= 0),
  updated_at  timestamptz not null default now(),
  primary key (producto_id, almacen_id)
);

-- ---------------------------------------------------------------------------
-- Cuentas por pagar generadas por recepción
-- ---------------------------------------------------------------------------

create table public.cuentas_por_pagar (
  id                uuid primary key default gen_random_uuid(),
  recepcion_id      uuid not null unique references public.recepciones(id),
  orden_compra_id   uuid not null references public.ordenes_compra(id),
  proveedor_id      uuid not null references public.proveedores(id),
  monto             numeric(14,2) not null check (monto >= 0),
  saldo             numeric(14,2) not null check (saldo >= 0),
  estado            text not null default 'PENDIENTE'
                    check (estado in ('PENDIENTE','PAGADA','ANULADA')),
  fecha_vencimiento date,
  created_at        timestamptz not null default now(),
  updated_at        timestamptz not null default now()
);

create index idx_cxp_proveedor on public.cuentas_por_pagar (proveedor_id, estado);

-- ---------------------------------------------------------------------------
-- Triggers
-- ---------------------------------------------------------------------------

do $$
declare
  t text;
begin
  foreach t in array array['ordenes_compra', 'recepciones', 'cuentas_por_pagar'] loop
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
-- RLS: lectura autenticada; escritura vía API (service_role)
-- ---------------------------------------------------------------------------

do $$
declare
  t text;
begin
  foreach t in array array[
    'ordenes_compra', 'detalles_oc', 'recepciones', 'detalles_recepcion',
    'movimientos_inventario', 'stock', 'cuentas_por_pagar'
  ] loop
    execute format('alter table public.%I enable row level security', t);
    execute format(
      'create policy %I on public.%I for select to authenticated using (true)',
      t || '_select', t
    );
  end loop;
end;
$$;
