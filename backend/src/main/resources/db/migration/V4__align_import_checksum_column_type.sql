ALTER TABLE import_batch
  ALTER COLUMN checksum_sha256 TYPE varchar(64)
    USING trim(checksum_sha256);
