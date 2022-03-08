INSERT INTO family_benefit_town.users_roles
SELECT family_benefit_town.user.id, family_benefit_town.role.id
FROM family_benefit_town.user, family_benefit_town.role
WHERE family_benefit_town.user.email = 'smegovic@gmail.com' AND
      (family_benefit_town.role.name = 'ROLE_SUPER_ADMIN' OR
       family_benefit_town.role.name = 'ROLE_ADMIN');
