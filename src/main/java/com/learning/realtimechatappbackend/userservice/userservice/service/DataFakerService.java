package com.learning.realtimechatappbackend.userservice.userservice.service;

import com.learning.realtimechatappbackend.userservice.userservice.model.UserAccount;
import com.learning.realtimechatappbackend.userservice.userservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataFakerService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final Faker faker = new Faker();

    @Transactional
    public void seedUsers(int count, int batchSize) {
        log.info("Starting to seed {} user accounts with DataFaker", count);
        String password = passwordEncoder.encode("Password123");

        AtomicInteger counter = new AtomicInteger(0);
        int totalBatches = (int) Math.ceil((double) count / batchSize);

        for (int batch = 0; batch < totalBatches; batch++) {
            List<UserAccount> users = new ArrayList<>(batchSize);

            int start = batch * batchSize;
            int end = Math.min(start + batchSize, count);

            for (int i = start; i < end; i++) {
                UserAccount user = new UserAccount();
                user.setUserId(UUID.randomUUID().toString());

                // Generate realistic data using DataFaker
                String firstName = faker.name().firstName();
                String lastName = faker.name().lastName();
                String email = firstName.toLowerCase() + "." + lastName.toLowerCase() +
                        i + "@" + faker.internet().domainName();

                user.setEmail(email);
                user.setPassword(password);
                user.setFullName(firstName + " " + lastName);
                user.setRole("USER");
                user.setCreatedAt(Instant.now().minusSeconds(faker.number().numberBetween(0, 31536000))); // Random date within last year
                user.setUpdatedAt(Instant.now());
                users.add(user);

                if (counter.incrementAndGet() % 10000 == 0) {
                    log.info("Created {} users out of {}", counter.get(), count);
                }
            }

            accountRepository.saveAll(users);
            log.info("Saved batch {}/{}", (batch + 1), totalBatches);
        }

        log.info("Finished seeding {} user accounts", count);
    }
}
