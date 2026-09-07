INSERT INTO organization (id, legal_name, trade_name, active) VALUES
  ('10000000-0000-0000-0000-000000000001', 'Consorcio Imperio Perú SAC', 'Imperio Perú', true),
  ('10000000-0000-0000-0000-000000000002', 'AMOLCA', 'AMOLCA', true);

INSERT INTO organization_role (organization_id, role) VALUES
  ('10000000-0000-0000-0000-000000000001', 'DISTRIBUTOR'),
  ('10000000-0000-0000-0000-000000000001', 'SELLER'),
  ('10000000-0000-0000-0000-000000000002', 'PUBLISHER'),
  ('10000000-0000-0000-0000-000000000002', 'CATALOG_SOURCE');

INSERT INTO publisher (id, organization_id, slug, description, active) VALUES
  ('20000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000002', 'amolca', 'Editorial de libros médicos y odontológicos.', true);

INSERT INTO catalog_source (id, organization_id, code, display_name, source_type, active) VALUES
  ('30000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000002', 'AMOLCA_FILE', 'Archivo autorizado AMOLCA', 'FILE', true);

INSERT INTO distribution_relationship (publisher_organization_id, distributor_organization_id, notes, active) VALUES
  ('10000000-0000-0000-0000-000000000002', '10000000-0000-0000-0000-000000000001', 'Relación conceptual registrada; no implica derechos automáticos de reutilización de medios o textos.', true);

INSERT INTO field_ownership_policy (catalog_source_id, field_name, ownership, sync_policy) VALUES
  ('30000000-0000-0000-0000-000000000001', 'isbn13', 'BIBLIOGRAPHIC_SOURCE', 'PROPOSE_UPDATE'),
  ('30000000-0000-0000-0000-000000000001', 'title', 'BIBLIOGRAPHIC_SOURCE', 'PROPOSE_UPDATE'),
  ('30000000-0000-0000-0000-000000000001', 'subtitle', 'BIBLIOGRAPHIC_SOURCE', 'PROPOSE_UPDATE'),
  ('30000000-0000-0000-0000-000000000001', 'authors', 'BIBLIOGRAPHIC_SOURCE', 'PROPOSE_UPDATE'),
  ('30000000-0000-0000-0000-000000000001', 'edition', 'BIBLIOGRAPHIC_SOURCE', 'PROPOSE_UPDATE'),
  ('30000000-0000-0000-0000-000000000001', 'pages', 'BIBLIOGRAPHIC_SOURCE', 'PROPOSE_UPDATE'),
  ('30000000-0000-0000-0000-000000000001', 'sku', 'IMPERIO_COMMERCIAL', 'ADMIN_APPROVAL'),
  ('30000000-0000-0000-0000-000000000001', 'slug', 'IMPERIO_COMMERCIAL', 'ADMIN_APPROVAL'),
  ('30000000-0000-0000-0000-000000000001', 'regularPrice', 'IMPERIO_COMMERCIAL', 'ADMIN_APPROVAL'),
  ('30000000-0000-0000-0000-000000000001', 'promotionalPrice', 'IMPERIO_COMMERCIAL', 'ADMIN_APPROVAL'),
  ('30000000-0000-0000-0000-000000000001', 'stock', 'IMPERIO_COMMERCIAL', 'ADMIN_APPROVAL'),
  ('30000000-0000-0000-0000-000000000001', 'commercialDescription', 'IMPERIO_COMMERCIAL', 'ADMIN_APPROVAL'),
  ('30000000-0000-0000-0000-000000000001', 'seo', 'IMPERIO_COMMERCIAL', 'ADMIN_APPROVAL'),
  ('30000000-0000-0000-0000-000000000001', 'categories', 'IMPERIO_COMMERCIAL', 'ADMIN_APPROVAL'),
  ('30000000-0000-0000-0000-000000000001', 'status', 'IMPERIO_COMMERCIAL', 'ADMIN_APPROVAL');

INSERT INTO category (id, parent_id, root_type, code, display_name, slug_candidate, sort_order, active, visible, show_in_menu) VALUES
  ('40000000-0000-0000-0000-000000000001', null, 'MEDICINE', 'medicina', 'Medicina', 'medicina', 10, true, true, true),
  ('40000000-0000-0000-0000-000000000002', null, 'DENTISTRY', 'odontologia', 'Odontología', 'odontologia', 20, true, true, true);

INSERT INTO category (parent_id, root_type, code, display_name, slug_candidate, sort_order, active, visible, show_in_menu) VALUES
  ('40000000-0000-0000-0000-000000000001', 'MEDICINE', 'anatomia', 'Anatomía', 'anatomia', 10, true, true, true),
  ('40000000-0000-0000-0000-000000000001', 'MEDICINE', 'anestesiologia', 'Anestesiología', 'anestesiologia', 20, true, true, true),
  ('40000000-0000-0000-0000-000000000001', 'MEDICINE', 'cardiologia', 'Cardiología', 'cardiologia', 30, true, true, true),
  ('40000000-0000-0000-0000-000000000001', 'MEDICINE', 'cirugia', 'Cirugía', 'cirugia', 40, true, true, true),
  ('40000000-0000-0000-0000-000000000001', 'MEDICINE', 'cirugia-plastica-reconstructiva', 'Cirugía Plástica y Reconstructiva', 'cirugia-plastica-reconstructiva', 50, true, true, true),
  ('40000000-0000-0000-0000-000000000001', 'MEDICINE', 'cirugia-vascular', 'Cirugía Vascular', 'cirugia-vascular', 60, true, true, true),
  ('40000000-0000-0000-0000-000000000001', 'MEDICINE', 'cuidados-intensivos', 'Cuidados Intensivos', 'cuidados-intensivos', 70, true, true, true),
  ('40000000-0000-0000-0000-000000000001', 'MEDICINE', 'dermatologia', 'Dermatología', 'dermatologia', 80, true, true, true),
  ('40000000-0000-0000-0000-000000000001', 'MEDICINE', 'endocrinologia', 'Endocrinología', 'endocrinologia', 90, true, true, true),
  ('40000000-0000-0000-0000-000000000001', 'MEDICINE', 'enfermeria', 'Enfermería', 'enfermeria', 100, true, true, true),
  ('40000000-0000-0000-0000-000000000001', 'MEDICINE', 'fisioterapia', 'Fisioterapia', 'fisioterapia', 110, true, true, true),
  ('40000000-0000-0000-0000-000000000001', 'MEDICINE', 'gastroenterologia', 'Gastroenterología', 'gastroenterologia', 120, true, true, true),
  ('40000000-0000-0000-0000-000000000001', 'MEDICINE', 'ginecologia-obstetricia', 'Ginecología y Obstetricia', 'ginecologia-obstetricia', 130, true, true, true),
  ('40000000-0000-0000-0000-000000000001', 'MEDICINE', 'laboratorio-clinico', 'Laboratorio Clínico', 'laboratorio-clinico', 140, true, true, true),
  ('40000000-0000-0000-0000-000000000001', 'MEDICINE', 'medicina-estetica', 'Medicina Estética', 'medicina-estetica', 150, true, true, true),
  ('40000000-0000-0000-0000-000000000001', 'MEDICINE', 'medicina-general', 'Medicina General', 'medicina-general', 160, true, true, true),
  ('40000000-0000-0000-0000-000000000001', 'MEDICINE', 'medicina-interna', 'Medicina Interna', 'medicina-interna', 170, true, true, true),
  ('40000000-0000-0000-0000-000000000001', 'MEDICINE', 'microbiologia', 'Microbiología', 'microbiologia', 180, true, true, true),
  ('40000000-0000-0000-0000-000000000001', 'MEDICINE', 'nefrologia', 'Nefrología', 'nefrologia', 190, true, true, true),
  ('40000000-0000-0000-0000-000000000001', 'MEDICINE', 'neumologia', 'Neumología', 'neumologia', 200, true, true, true),
  ('40000000-0000-0000-0000-000000000001', 'MEDICINE', 'neurocirugia', 'Neurocirugía', 'neurocirugia', 210, true, true, true),
  ('40000000-0000-0000-0000-000000000001', 'MEDICINE', 'neurologia', 'Neurología', 'neurologia', 220, true, true, true),
  ('40000000-0000-0000-0000-000000000001', 'MEDICINE', 'oftalmologia', 'Oftalmología', 'oftalmologia', 230, true, true, true),
  ('40000000-0000-0000-0000-000000000001', 'MEDICINE', 'oncologia', 'Oncología', 'oncologia', 240, true, true, true),
  ('40000000-0000-0000-0000-000000000001', 'MEDICINE', 'ortopedia-traumatologia', 'Ortopedia y Traumatología', 'ortopedia-traumatologia', 250, true, true, true),
  ('40000000-0000-0000-0000-000000000001', 'MEDICINE', 'otorrinolaringologia', 'Otorrinolaringología', 'otorrinolaringologia', 260, true, true, true),
  ('40000000-0000-0000-0000-000000000001', 'MEDICINE', 'patologia', 'Patología', 'patologia', 270, true, true, true),
  ('40000000-0000-0000-0000-000000000001', 'MEDICINE', 'pediatria', 'Pediatría', 'pediatria', 280, true, true, true),
  ('40000000-0000-0000-0000-000000000001', 'MEDICINE', 'psiquiatria-psicologia', 'Psiquiatría y Psicología', 'psiquiatria-psicologia', 290, true, true, true),
  ('40000000-0000-0000-0000-000000000001', 'MEDICINE', 'radiologia-imagenologia', 'Radiología e Imagenología', 'radiologia-imagenologia', 300, true, true, true),
  ('40000000-0000-0000-0000-000000000001', 'MEDICINE', 'reumatologia', 'Reumatología', 'reumatologia', 310, true, true, true),
  ('40000000-0000-0000-0000-000000000001', 'MEDICINE', 'urologia', 'Urología', 'urologia', 320, true, true, true),
  ('40000000-0000-0000-0000-000000000002', 'DENTISTRY', 'cirugia-oral-maxilofacial', 'Cirugía Oral y Maxilofacial', 'cirugia-oral-maxilofacial', 10, true, true, true),
  ('40000000-0000-0000-0000-000000000002', 'DENTISTRY', 'endodoncia', 'Endodoncia', 'endodoncia', 20, true, true, true),
  ('40000000-0000-0000-0000-000000000002', 'DENTISTRY', 'estetica-dental', 'Estética Dental', 'estetica-dental', 30, true, true, true),
  ('40000000-0000-0000-0000-000000000002', 'DENTISTRY', 'estetica-orofacial', 'Estética Orofacial', 'estetica-orofacial', 40, true, true, true),
  ('40000000-0000-0000-0000-000000000002', 'DENTISTRY', 'implantologia', 'Implantología', 'implantologia', 50, true, true, true),
  ('40000000-0000-0000-0000-000000000002', 'DENTISTRY', 'laboratorio-dental', 'Laboratorio Dental', 'laboratorio-dental', 60, true, true, true),
  ('40000000-0000-0000-0000-000000000002', 'DENTISTRY', 'oclusion', 'Oclusión', 'oclusion', 70, true, true, true),
  ('40000000-0000-0000-0000-000000000002', 'DENTISTRY', 'odontologia-general', 'Odontología General', 'odontologia-general', 80, true, true, true),
  ('40000000-0000-0000-0000-000000000002', 'DENTISTRY', 'odontopediatria', 'Odontopediatría', 'odontopediatria', 90, true, true, true),
  ('40000000-0000-0000-0000-000000000002', 'DENTISTRY', 'ortodoncia-ortopedia-maxilar', 'Ortodoncia y Ortopedia Maxilar', 'ortodoncia-ortopedia-maxilar', 100, true, true, true),
  ('40000000-0000-0000-0000-000000000002', 'DENTISTRY', 'patologia-bucal', 'Patología Bucal', 'patologia-bucal', 110, true, true, true),
  ('40000000-0000-0000-0000-000000000002', 'DENTISTRY', 'periodoncia', 'Periodoncia', 'periodoncia', 120, true, true, true),
  ('40000000-0000-0000-0000-000000000002', 'DENTISTRY', 'prostodoncia', 'Prostodoncia', 'prostodoncia', 130, true, true, true),
  ('40000000-0000-0000-0000-000000000002', 'DENTISTRY', 'radiologia-dental', 'Radiología Dental', 'radiologia-dental', 140, true, true, true),
  ('40000000-0000-0000-0000-000000000002', 'DENTISTRY', 'rehabilitacion-oral', 'Rehabilitación Oral', 'rehabilitacion-oral', 150, true, true, true);

