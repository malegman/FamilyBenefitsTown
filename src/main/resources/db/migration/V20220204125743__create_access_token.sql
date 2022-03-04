CREATE TABLE family_benefit_town.access_token (

  id_user TEXT NOT NULL,
  "token" TEXT NOT NULL,

  CONSTRAINT access_token_pk PRIMARY KEY (id_user),
  CONSTRAINT access_token_uniq_token UNIQUE ("token"),
  CONSTRAINT access_token_fk_user FOREIGN KEY (id_user)
    REFERENCES family_benefit_town.user("id")
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

COMMENT ON COLUMN family_benefit_town.access_token.id_user IS 'ID пользователя';
COMMENT ON COLUMN family_benefit_town.access_token.token IS 'Токен доступа';
