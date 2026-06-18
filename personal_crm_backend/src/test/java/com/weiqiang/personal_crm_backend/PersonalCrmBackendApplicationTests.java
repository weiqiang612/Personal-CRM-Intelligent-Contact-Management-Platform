package com.weiqiang.personal_crm_backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
class PersonalCrmBackendApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void generatePassword() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hash = "$2a$10$z94Fnk1mXqA42RabilFEwu29iGVNVe15pMYLv9BaxBeHwTUDbbLI2";
        boolean match = encoder.matches("123456", hash);
        System.out.println("BCRYPT_HASH_FOR_123456: " + encoder.encode("123456"));
        System.out.println("SEED_HASH_MATCHES_123456: " + match);
        org.junit.jupiter.api.Assertions.assertTrue(match, "Seeded hash does not match 123456!");
    }

}

