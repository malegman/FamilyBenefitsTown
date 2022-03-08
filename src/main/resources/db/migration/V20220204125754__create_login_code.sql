CREATE TABLE family_benefit_town.login_code (

  "id_user" TEXT NOT NULL,
  "code" INT NOT NULL,
  "date_expiration" TIMESTAMP NOT NULL,

  CONSTRAINT login_code_pk PRIMARY KEY ("id_user"),
  CONSTRAINT login_code_uniq_token UNIQUE ("code"),
  CONSTRAINT login_code_fk_user FOREIGN KEY ("id_user")
    REFERENCES family_benefit_town.user("id")
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

COMMENT ON COLUMN family_benefit_town.login_code.id_user IS 'ID пользователя';
COMMENT ON COLUMN family_benefit_town.login_code.code IS 'Код для входа в систему';
COMMENT ON COLUMN family_benefit_town.login_code.date_expiration IS 'Время истечения срока кода';
