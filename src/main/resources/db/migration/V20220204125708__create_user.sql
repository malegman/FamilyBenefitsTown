CREATE TABLE family_benefit_town.user (

  "id" TEXT NOT NULL DEFAULT family_benefit_town.generate_id(20),
  "name" TEXT NOT NULL,
  "email" TEXT NOT NULL,
  "date_birth" DATE NULL,
  "id_city" TEXT NULL,

  CONSTRAINT user_pk PRIMARY KEY ("id"),
  CONSTRAINT user_uniq_email UNIQUE ("email"),
  CONSTRAINT user_fk_city FOREIGN KEY ("id_city")
    REFERENCES family_benefit_town.city("id")
    ON DELETE RESTRICT
    ON UPDATE CASCADE
);

COMMENT ON COLUMN family_benefit_town.user.id IS 'ID пользователя';
COMMENT ON COLUMN family_benefit_town.user.name IS 'Имя пользователя';
COMMENT ON COLUMN family_benefit_town.user.email IS 'Эл. почта пользователя';
COMMENT ON COLUMN family_benefit_town.user.date_birth IS 'Дата рождения пользователя';
COMMENT ON COLUMN family_benefit_town.user.id_city IS 'ID города пользователя';
