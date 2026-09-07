CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE organization (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  legal_name varchar(200) NOT NULL,
  trade_name varchar(200),
  tax_id varchar(40),
  active boolean NOT NULL DEFAULT true,
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now(),
  CONSTRAINT uk_organization_legal_name UNIQUE (legal_name)
);

CREATE TABLE organization_role (
  organization_id uuid NOT NULL REFERENCES organization(id),
  role varchar(40) NOT NULL,
  PRIMARY KEY (organization_id, role)
);

CREATE TABLE publisher (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  organization_id uuid NOT NULL UNIQUE REFERENCES organization(id),
  slug varchar(180) NOT NULL UNIQUE,
  description text,
  active boolean NOT NULL DEFAULT true,
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE catalog_source (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  organization_id uuid REFERENCES organization(id),
  code varchar(80) NOT NULL UNIQUE,
  display_name varchar(180) NOT NULL,
  source_type varchar(30) NOT NULL,
  active boolean NOT NULL DEFAULT true,
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE distribution_relationship (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  publisher_organization_id uuid NOT NULL REFERENCES organization(id),
  distributor_organization_id uuid NOT NULL REFERENCES organization(id),
  starts_on date,
  ends_on date,
  notes text,
  active boolean NOT NULL DEFAULT true,
  CONSTRAINT uk_distribution_relationship UNIQUE (publisher_organization_id, distributor_organization_id)
);

CREATE TABLE seo_metadata (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  seo_title varchar(180),
  meta_description varchar(320),
  canonical_url varchar(500),
  robots_policy varchar(30) NOT NULL DEFAULT 'NOINDEX',
  og_title varchar(180),
  og_description varchar(320),
  og_image_url varchar(500),
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE author (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  name varchar(220) NOT NULL,
  slug varchar(220) NOT NULL UNIQUE,
  biography text,
  active boolean NOT NULL DEFAULT true,
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE book (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  isbn13 varchar(13),
  isbn10 varchar(10),
  title varchar(300) NOT NULL,
  subtitle varchar(300),
  edition varchar(100),
  publication_year integer,
  pages integer,
  language varchar(30),
  binding_format varchar(60),
  bibliographic_description text,
  publisher_id uuid REFERENCES publisher(id),
  seo_metadata_id uuid UNIQUE REFERENCES seo_metadata(id),
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now(),
  created_by varchar(180),
  updated_by varchar(180),
  CONSTRAINT uk_book_isbn13 UNIQUE (isbn13),
  CONSTRAINT uk_book_isbn10 UNIQUE (isbn10),
  CONSTRAINT ck_book_isbn13_length CHECK (isbn13 IS NULL OR length(isbn13) = 13),
  CONSTRAINT ck_book_isbn10_length CHECK (isbn10 IS NULL OR length(isbn10) = 10),
  CONSTRAINT ck_book_pages CHECK (pages IS NULL OR pages > 0)
);

CREATE TABLE book_author (
  book_id uuid NOT NULL REFERENCES book(id) ON DELETE CASCADE,
  author_id uuid NOT NULL REFERENCES author(id),
  sort_order integer NOT NULL DEFAULT 0,
  PRIMARY KEY (book_id, author_id)
);

CREATE TABLE book_offer (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  book_id uuid NOT NULL UNIQUE REFERENCES book(id),
  seller_organization_id uuid NOT NULL REFERENCES organization(id),
  supplier_organization_id uuid REFERENCES organization(id),
  sku varchar(100) NOT NULL UNIQUE,
  slug varchar(240) NOT NULL UNIQUE,
  regular_price_amount numeric(19,2) NOT NULL,
  regular_price_currency char(3) NOT NULL,
  promotional_price_amount numeric(19,2),
  promotional_price_currency char(3),
  commercial_description text,
  status varchar(40) NOT NULL,
  featured boolean NOT NULL DEFAULT false,
  new_arrival boolean NOT NULL DEFAULT false,
  published_at timestamptz,
  archived_at timestamptz,
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now(),
  created_by varchar(180),
  updated_by varchar(180),
  CONSTRAINT ck_book_offer_currency CHECK (regular_price_currency = 'USD'),
  CONSTRAINT ck_book_offer_amount CHECK (regular_price_amount >= 0),
  CONSTRAINT ck_book_offer_promo_pair CHECK (
    (promotional_price_amount IS NULL AND promotional_price_currency IS NULL)
    OR (promotional_price_amount IS NOT NULL AND promotional_price_currency = regular_price_currency AND promotional_price_amount >= 0)
  )
);

CREATE TABLE category (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  parent_id uuid REFERENCES category(id),
  root_type varchar(30) NOT NULL,
  code varchar(120) NOT NULL UNIQUE,
  display_name varchar(220) NOT NULL,
  slug_candidate varchar(220),
  public_path varchar(500),
  description text,
  sort_order integer NOT NULL DEFAULT 0,
  active boolean NOT NULL DEFAULT true,
  visible boolean NOT NULL DEFAULT true,
  show_in_menu boolean NOT NULL DEFAULT true,
  seo_metadata_id uuid UNIQUE REFERENCES seo_metadata(id),
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now(),
  created_by varchar(180),
  updated_by varchar(180),
  CONSTRAINT ck_category_no_self_parent CHECK (parent_id IS NULL OR parent_id <> id)
);

CREATE TABLE item_category (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  book_id uuid NOT NULL REFERENCES book(id) ON DELETE CASCADE,
  category_id uuid NOT NULL REFERENCES category(id),
  is_primary boolean NOT NULL DEFAULT false,
  sort_order integer NOT NULL DEFAULT 0,
  active boolean NOT NULL DEFAULT true,
  created_at timestamptz NOT NULL DEFAULT now(),
  CONSTRAINT uk_item_category UNIQUE (book_id, category_id)
);
CREATE UNIQUE INDEX uk_item_category_primary ON item_category(book_id) WHERE is_primary AND active;

CREATE TABLE book_image (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  book_id uuid NOT NULL REFERENCES book(id) ON DELETE CASCADE,
  image_type varchar(30) NOT NULL,
  storage_key varchar(500) NOT NULL,
  alt_text varchar(240) NOT NULL,
  media_type varchar(80) NOT NULL,
  width integer,
  height integer,
  sort_order integer NOT NULL DEFAULT 0,
  authorized boolean NOT NULL DEFAULT false,
  source_organization_id uuid REFERENCES organization(id),
  created_at timestamptz NOT NULL DEFAULT now(),
  CONSTRAINT ck_book_image_dimensions CHECK ((width IS NULL AND height IS NULL) OR (width > 0 AND height > 0))
);

CREATE TABLE inventory_item (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  book_offer_id uuid NOT NULL UNIQUE REFERENCES book_offer(id),
  on_hand integer NOT NULL DEFAULT 0,
  reserved integer NOT NULL DEFAULT 0,
  minimum_stock integer NOT NULL DEFAULT 0,
  version bigint NOT NULL DEFAULT 0,
  updated_at timestamptz NOT NULL DEFAULT now(),
  CONSTRAINT ck_inventory_non_negative CHECK (on_hand >= 0 AND reserved >= 0 AND minimum_stock >= 0),
  CONSTRAINT ck_inventory_reserved CHECK (reserved <= on_hand)
);

CREATE TABLE inventory_movement (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  inventory_item_id uuid NOT NULL REFERENCES inventory_item(id),
  movement_type varchar(40) NOT NULL,
  quantity integer NOT NULL,
  on_hand_after integer NOT NULL,
  reserved_after integer NOT NULL,
  reason varchar(300) NOT NULL,
  reference_type varchar(60),
  reference_id varchar(120),
  actor varchar(180) NOT NULL,
  occurred_at timestamptz NOT NULL DEFAULT now(),
  CONSTRAINT ck_inventory_movement_quantity CHECK (quantity <> 0)
);

CREATE TABLE redirect_rule (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  source_path varchar(500) NOT NULL UNIQUE,
  destination_path varchar(500) NOT NULL,
  status_code integer NOT NULL DEFAULT 301,
  active boolean NOT NULL DEFAULT true,
  reason varchar(300),
  created_at timestamptz NOT NULL DEFAULT now(),
  created_by varchar(180),
  CONSTRAINT ck_redirect_status CHECK (status_code IN (301, 302, 307, 308)),
  CONSTRAINT ck_redirect_not_self CHECK (source_path <> destination_path)
);

CREATE TABLE field_ownership_policy (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  catalog_source_id uuid NOT NULL REFERENCES catalog_source(id),
  field_name varchar(120) NOT NULL,
  ownership varchar(40) NOT NULL,
  sync_policy varchar(40) NOT NULL,
  active boolean NOT NULL DEFAULT true,
  CONSTRAINT uk_field_ownership UNIQUE (catalog_source_id, field_name)
);

CREATE TABLE import_batch (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  catalog_source_id uuid NOT NULL REFERENCES catalog_source(id),
  original_filename varchar(255) NOT NULL,
  content_type varchar(100) NOT NULL,
  checksum_sha256 char(64) NOT NULL,
  status varchar(40) NOT NULL,
  total_rows integer NOT NULL DEFAULT 0,
  valid_rows integer NOT NULL DEFAULT 0,
  error_rows integer NOT NULL DEFAULT 0,
  created_by varchar(180) NOT NULL,
  created_at timestamptz NOT NULL DEFAULT now(),
  applied_at timestamptz
);

CREATE TABLE import_row (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  import_batch_id uuid NOT NULL REFERENCES import_batch(id) ON DELETE CASCADE,
  row_number integer NOT NULL,
  source_payload jsonb NOT NULL,
  match_type varchar(40),
  matched_book_id uuid REFERENCES book(id),
  disposition varchar(40) NOT NULL,
  validation_messages jsonb NOT NULL DEFAULT '[]'::jsonb,
  proposed_changes jsonb NOT NULL DEFAULT '{}'::jsonb,
  decision_by varchar(180),
  decision_at timestamptz,
  CONSTRAINT uk_import_row_number UNIQUE (import_batch_id, row_number)
);

CREATE TABLE app_user (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  email varchar(254) NOT NULL UNIQUE,
  password_hash varchar(100) NOT NULL,
  display_name varchar(180) NOT NULL,
  enabled boolean NOT NULL DEFAULT true,
  token_version integer NOT NULL DEFAULT 0,
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE user_role (
  user_id uuid NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
  role varchar(40) NOT NULL,
  PRIMARY KEY (user_id, role)
);

CREATE TABLE user_permission (
  user_id uuid NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
  permission varchar(80) NOT NULL,
  PRIMARY KEY (user_id, permission)
);

CREATE TABLE audit_event (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  actor varchar(180) NOT NULL,
  action varchar(80) NOT NULL,
  entity_type varchar(100) NOT NULL,
  entity_id varchar(120) NOT NULL,
  before_data jsonb,
  after_data jsonb,
  request_id varchar(120),
  occurred_at timestamptz NOT NULL DEFAULT now()
);

CREATE INDEX ix_book_title ON book USING gin (to_tsvector('spanish', title));
CREATE INDEX ix_book_offer_status ON book_offer(status);
CREATE INDEX ix_category_navigation ON category(root_type, parent_id, active, visible, show_in_menu, sort_order);
CREATE INDEX ix_inventory_movement_item_time ON inventory_movement(inventory_item_id, occurred_at DESC);
CREATE INDEX ix_audit_event_entity ON audit_event(entity_type, entity_id, occurred_at DESC);

