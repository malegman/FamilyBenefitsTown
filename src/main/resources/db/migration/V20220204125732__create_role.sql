CREATE TABLE family_benefit_town.role (

  "id" TEXT NOT NULL DEFAULT family_benefit_town.generate_id(20),
  "name" TEXT NOT NULL,

  CONSTRAINT role_pk PRIMARY KEY ("id"),
  CONSTRAINT role_uniq_name UNIQUE ("name")
);

COMMENT ON COLUMN family_benefit_town.role.id IS 'ID роли';
COMMENT ON COLUMN family_benefit_town.role.name IS 'Название роли';
