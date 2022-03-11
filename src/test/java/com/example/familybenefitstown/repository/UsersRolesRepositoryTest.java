package com.example.familybenefitstown.repository;

import com.example.familybenefitstown.FamilyBenefitsTownApplication;
import com.example.familybenefitstown.dto.repositories.strong.RoleRepository;
import com.example.familybenefitstown.dto.repositories.strong.UserRepository;
import com.example.familybenefitstown.dto.repositories.weak.UsersRolesRepository;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {FamilyBenefitsTownApplication.class})
public class UsersRolesRepositoryTest {

  @Autowired
  private RoleRepository roleRepository;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private UsersRolesRepository usersRolesRepository;

  @Test
  public void getAllRoles() {

    AssertionsForClassTypes.assertThat(roleRepository.findAll().size()).isEqualTo(3);
  }

  @Test
  public void getRolesOfDefaultSuperAdmin() {

    AssertionsForClassTypes.assertThat(usersRolesRepository.findAll().size()).isEqualTo(2);

    AssertionsForClassTypes.assertThat(
        usersRolesRepository.findAllByUserEntity(
        userRepository.getSuperAdmin())
            .size()).isEqualTo(2);
  }
}
