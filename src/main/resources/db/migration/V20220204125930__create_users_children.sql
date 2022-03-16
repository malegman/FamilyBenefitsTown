CREATE TABLE family_benefit_town.users_children (

  "id_user" TEXT NOT NULL,
  "id_child_birth" TEXT NOT NULL,

  CONSTRAINT users_children_pk PRIMARY KEY ("id_user", "id_child_birth"),
  CONSTRAINT users_children_fk_user FOREIGN KEY ("id_user")
    REFERENCES family_benefit_town.user("id")
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT users_children_fk_child FOREIGN KEY ("id_child_birth")
    REFERENCES family_benefit_town.child_birth("id")
    ON DELETE RESTRICT
    ON UPDATE CASCADE
);

COMMENT ON COLUMN family_benefit_town.users_children.id_user IS 'ID пользователя';
COMMENT ON COLUMN family_benefit_town.users_children.id_child_birth IS 'ID рождения ребенка';
