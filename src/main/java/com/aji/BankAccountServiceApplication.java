package com.aji;

import com.aji.domain.Account;
import com.aji.repository.AccountRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@EnableJpaRepositories
@SpringBootApplication
@EnableScheduling
public class BankAccountServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankAccountServiceApplication.class, args);
    }

    @Bean
    public CommandLineRunner accountRunner(AccountRepo accountRepo) {
        return args -> {
            Account account = Account.builder().balance(0).version(0).createdDate(LocalDateTime.now()).lastModifiedDate(LocalDateTime.now()).build();
            accountRepo.save(account);
        };
    }

    @Bean
    public ScheduledExecutorService scheduledExecutorService() {
        int poolSize = 2;
        return Executors.newScheduledThreadPool(poolSize);
    }
}
