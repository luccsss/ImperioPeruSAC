ALTER TABLE book_offer
  ALTER COLUMN regular_price_currency TYPE varchar(3)
    USING trim(regular_price_currency),
  ALTER COLUMN promotional_price_currency TYPE varchar(3)
    USING trim(promotional_price_currency);
