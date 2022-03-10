package com.example.familybenefitstown.repository;

import com.example.familybenefitstown.FamilyBenefitsTownApplication;
import com.example.familybenefitstown.dto.entity.UserEntity;
import com.example.familybenefitstown.dto.repository.RoleRepository;
import com.example.familybenefitstown.dto.repository.UserRepository;
import com.example.familybenefitstown.resources.R;
import com.example.familybenefitstown.resources.RDB;
import com.example.familybenefitstown.security.generator.RandomValue;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.HashSet;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {FamilyBenefitsTownApplication.class})
public class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;
  @Autowired
  private RoleRepository roleRepository;

  @Before
  public void createUserEntity_TestAdmin() {

    userRepository.saveAndFlush(UserEntity
                                    .builder()
                                    .id(RandomValue.randomString(R.ID_LENGTH))
                                    .email("testAdmin@mail.com")
                                    .name("testAdmin")
                                    .roleEntitySet(new HashSet<>())
                                    .childEntitySet(new HashSet<>())
                                    .build());

    AssertionsForClassTypes.assertThat(userRepository.findAll().size()).isEqualTo(2);
  }

  @Test
  public void updateByUserEntity_TestAdmin() {

    UserEntity testAdmin = userRepository.getByEmail("testAdmin@mail.com");
    testAdmin.setName("testAdmin_new");
    AssertionsForClassTypes.assertThat(userRepository.getByEmail("testAdmin@mail.com").getName()).isEqualTo("testAdmin_new");
  }

  @Test
  public void updateByUserRepository_TestAdmin() {

    UserEntity testAdmin = userRepository.getByEmail("testAdmin@mail.com");
    testAdmin.setName("testAdmin_old");
    userRepository.saveAndFlush(testAdmin);
    AssertionsForClassTypes.assertThat(userRepository.getByEmail("testAdmin@mail.com").getName()).isEqualTo("testAdmin_old");
  }

  @Test
  public void addRoleAdminByName_TestAdmin() {

    UserEntity testAdmin = userRepository.getByEmail("testAdmin@mail.com");
    AssertionsForClassTypes.assertThat(testAdmin.getRoleEntitySet()).isEqualTo(Collections.EMPTY_SET);
    testAdmin.addRole(RDB.ROLE_ADMIN);
    userRepository.saveAndFlush(testAdmin);
    AssertionsForClassTypes.assertThat(userRepository.getByEmail("testAdmin@mail.com").getRoleEntitySet().size()).isEqualTo(1);
  }

  @Test
  public void addRoleAdminByEntity_TestAdmin() {

    UserEntity testAdmin = userRepository.getByEmail("testAdmin@mail.com");
    AssertionsForClassTypes.assertThat(testAdmin.getRoleEntitySet()).isEqualTo(Collections.EMPTY_SET);
    testAdmin.addRole(RDB.ROLE_ADMIN);
    testAdmin.addRole(RDB.ROLE_USER);
    userRepository.saveAndFlush(testAdmin);
    AssertionsForClassTypes.assertThat(userRepository.getByEmail("testAdmin@mail.com").getRoleEntitySet().size()).isEqualTo(2);
  }

  @After
  public void deleteUserEntity_TestAdmin() {

    userRepository.delete(userRepository.getByEmail("testAdmin@mail.com"));
  }
}
