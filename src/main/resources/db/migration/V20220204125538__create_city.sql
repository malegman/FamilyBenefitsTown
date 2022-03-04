CREATE TABLE family_benefit_town.city (

  "id" TEXT NOT NULL DEFAULT family_benefit_town.generate_id(20),
  "name" TEXT NOT NULL,
  "info" TEXT NULL,

  CONSTRAINT city_pk PRIMARY KEY ("id"),
  CONSTRAINT city_uniq_name UNIQUE ("name")
);

COMMENT ON COLUMN family_benefit_town.city.id IS 'ID города';
COMMENT ON COLUMN family_benefit_town.city.name IS 'Название города';
COMMENT ON COLUMN family_benefit_town.city.info IS 'Информация города';


