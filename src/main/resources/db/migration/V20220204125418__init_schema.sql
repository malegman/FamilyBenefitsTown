CREATE SCHEMA IF NOT EXISTS family_benefit_town;

GRANT USAGE ON SCHEMA family_benefit_town TO familyben;
GRANT SELECT, INSERT, UPDATE, DELETE, REFERENCES ON ALL TABLES IN SCHEMA family_benefit_town TO familyben;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA family_benefit_town TO familyben;
