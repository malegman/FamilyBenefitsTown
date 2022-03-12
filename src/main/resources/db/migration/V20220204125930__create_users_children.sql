CREATE TABLE family_benefit_town.users_children (

  "id_user" TEXT NOT NULL,
  "id_child" TEXT NOT NULL,

  CONSTRAINT users_children_pk PRIMARY KEY ("id_user", "id_child"),
  CONSTRAINT users_children_fk_user FOREIGN KEY ("id_user")
    REFERENCES family_benefit_town.user("id")
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT users_children_fk_child FOREIGN KEY ("id_child")
    REFERENCES family_benefit_town.child("id")
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

COMMENT ON COLUMN family_benefit_town.users_children.id_user IS 'ID пользователя';
COMMENT ON COLUMN family_benefit_town.users_children.id_child IS 'ID ребенка';
