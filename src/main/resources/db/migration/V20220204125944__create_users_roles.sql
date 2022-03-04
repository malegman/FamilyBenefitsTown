CREATE TABLE family_benefit_town.users_roles (

  "id_user" TEXT NOT NULL,
  "id_role" TEXT NOT NULL,

  CONSTRAINT users_roles_pk PRIMARY KEY ("id_user", "id_role"),
  CONSTRAINT users_roles_fk_user FOREIGN KEY ("id_user")
    REFERENCES family_benefit_town.user("id")
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT users_roles_fk_role FOREIGN KEY ("id_role")
    REFERENCES family_benefit_town.role("id")
    ON DELETE RESTRICT
    ON UPDATE CASCADE
);

COMMENT ON COLUMN family_benefit_town.users_roles.id_user IS 'ID пользователя';
COMMENT ON COLUMN family_benefit_town.users_roles.id_role IS 'ID роли';
