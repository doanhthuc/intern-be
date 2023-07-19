ALTER TABLE attachments RENAME COLUMN content TO image_data;

ALTER TABLE attachments ALTER COLUMN image_data SET NOT NULL;

ALTER TABLE attachments ADD COLUMN source_code TEXT;

ALTER TABLE attachments ADD COLUMN kahoot_url VARCHAR(255);

ALTER TABLE attachments DROP COLUMN attachment_type;
