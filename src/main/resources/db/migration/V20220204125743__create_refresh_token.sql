CREATE TABLE family_benefit_town.refresh_token(

  "id_user" TEXT NOT NULL,
  "token" TEXT NOT NULL,
  "date_expiration" TIMESTAMP NOT NULL,

  CONSTRAINT refresh_token_pk PRIMARY KEY ("id_user"),
  CONSTRAINT refresh_token_uniq_token UNIQUE ("token"),
  CONSTRAINT refresh_token_fk_user FOREIGN KEY ("id_user")
    REFERENCES family_benefit_town.user("id")
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

COMMENT ON COLUMN family_benefit_town.refresh_token.id_user IS 'ID пользователя';
COMMENT ON COLUMN family_benefit_town.refresh_token.token IS 'Токен восстановления';
COMMENT ON COLUMN family_benefit_town.refresh_token.date_expiration IS 'Время истечения срока токена';
