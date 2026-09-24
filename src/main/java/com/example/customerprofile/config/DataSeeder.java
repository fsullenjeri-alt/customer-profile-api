package com.example.customerprofile.config;

import com.example.customerprofile.customer.entity.Customers;
import com.example.customerprofile.customer.repository.CustomerRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

/**
 * Inserts 10 random customers on startup, but only when the table is empty,
 * so restarting the app does not keep adding more.
 */
@Slf4j
@Component
@AllArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private static final int CUSTOMER_COUNT = 10;

    private static final List<String> FIRST_NAMES = List.of(
            "Ann", "John", "Maria", "David", "Sara", "Luca", "Elena", "Omar",
            "Nina", "Marco", "Julia", "Adam", "Sofia", "Leo", "Emma", "Noah");

    private static final List<String> LAST_NAMES = List.of(
            "Smith", "Rossi", "Brown", "Garcia", "Muller", "Hoxha", "Wilson",
            "Martin", "Lopez", "Taylor", "Bianchi", "Clark", "Lee", "Walker");

    private static final List<String> DOMAINS = List.of(
            "example.com", "mail.com", "test.org");

    private final CustomerRepository customerRepository;

    @Override
    public void run(String... args) {
        if (customerRepository.count() > 0) {
            log.info("Customers table already has data, skipping seeding");
            return;
        }

        List<Customers> customers = IntStream.rangeClosed(1, CUSTOMER_COUNT)
                .mapToObj(this::randomCustomer)
                .toList();

        customerRepository.saveAll(customers);
        log.info("Seeded {} random customers", customers.size());
    }

    private Customers randomCustomer(int index) {
        String firstName = pick(FIRST_NAMES);
        String lastName = pick(LAST_NAMES);

        // index keeps emails unique even if the same name is picked twice
        String email = "%s.%s%d@%s".formatted(firstName, lastName, index, pick(DOMAINS))
                .toLowerCase(Locale.ROOT);
        String photo = "https://i.pravatar.cc/150?img=" + ThreadLocalRandom.current().nextInt(1, 71);

        return new Customers(null, firstName + " " + lastName, email, photo);
    }

    private static <T> T pick(List<T> values) {
        return values.get(ThreadLocalRandom.current().nextInt(values.size()));
    }
}
