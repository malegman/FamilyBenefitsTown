CREATE TABLE family_benefit_town.child_birth (

  "id" TEXT NOT NULL DEFAULT family_benefit_town.generate_id(20),
  "date_birth" DATE NOT NULL,

  CONSTRAINT child_birth_pk PRIMARY KEY ("id"),
  CONSTRAINT child_birth_uniq_birth UNIQUE ("date_birth")
);

COMMENT ON COLUMN family_benefit_town.child_birth.id IS 'ID ребенка';
COMMENT ON COLUMN family_benefit_town.child_birth.date_birth IS 'Дата рождения ребенка';
