package com.alpsahan.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import com.alpsahan.entity.AlertCondition;
import com.alpsahan.entity.PriceAlert;
import com.alpsahan.entity.User;
import com.alpsahan.specification.PriceAlertSpecification;

@Testcontainers
@DataJpaTest(properties = "spring.jpa.hibernate.ddl-auto=create")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)

public class PriceAlertRepositoryIntegrationTest {

    @Container
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {

        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password",postgres::getPassword);
    }

    @Autowired
    private PriceAlertRepository priceAlertRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldFindOnlyActiveAlerts() {

        User user = new User();
        user.setEmail("alp@test.com");
        user.setPassword("fake-password");

        User savedUser = userRepository.save(user);

        PriceAlert activeAlert = new PriceAlert();
        activeAlert.setSymbol("BTCUSDT");
        activeAlert.setTargetPrice(new BigDecimal("100000"));
        activeAlert.setCondition(AlertCondition.ABOVE);
        activeAlert.setActive(true);
        activeAlert.setUser(savedUser);

        PriceAlert inactiveAlert = new PriceAlert();
        
        inactiveAlert.setSymbol("ETHUSDT");
        inactiveAlert.setTargetPrice(new BigDecimal("5000"));
        inactiveAlert.setCondition(AlertCondition.ABOVE);
        inactiveAlert.setActive(false);
        inactiveAlert.setUser(savedUser);

        priceAlertRepository.save(activeAlert);
        priceAlertRepository.save(inactiveAlert);

        List<PriceAlert> result = priceAlertRepository.findByActiveTrue();

        assertEquals(1, result.size());

        assertEquals(
                "BTCUSDT",
                result.get(0).getSymbol()
        );
    }
    
    
    
    @Test
    void shouldFilterAlertsByUserSymbolActiveAndCondition() {

        // USER 1
        User alp = new User();
        alp.setEmail("alp@test.com");
        alp.setPassword("fake-password");
        alp = userRepository.save(alp);

        // USER 2
        User mert = new User();
        mert.setEmail("mert@test.com");
        mert.setPassword("fake-password");
        mert = userRepository.save(mert);


        // Alp - BTC - ABOVE - ACTIVE
        PriceAlert alert1 = new PriceAlert();
        alert1.setSymbol("BTCUSDT");
        alert1.setTargetPrice(new BigDecimal("100000"));
        alert1.setCondition(AlertCondition.ABOVE);
        alert1.setActive(true);
        alert1.setUser(alp);


        // Alp - BTC - BELOW - ACTIVE
        PriceAlert alert2 = new PriceAlert();
        alert2.setSymbol("BTCUSDT");
        alert2.setTargetPrice(new BigDecimal("80000"));
        alert2.setCondition(AlertCondition.BELOW);
        alert2.setActive(true);
        alert2.setUser(alp);


        // Alp - ETH - ABOVE - ACTIVE
        PriceAlert alert3 = new PriceAlert();
        alert3.setSymbol("ETHUSDT");
        alert3.setTargetPrice(new BigDecimal("5000"));
        alert3.setCondition(AlertCondition.ABOVE);
        alert3.setActive(true);
        alert3.setUser(alp);


        // Mert - BTC - ABOVE - ACTIVE
        PriceAlert alert4 = new PriceAlert();
        alert4.setSymbol("BTCUSDT");
        alert4.setTargetPrice(new BigDecimal("120000"));
        alert4.setCondition(AlertCondition.ABOVE);
        alert4.setActive(true);
        alert4.setUser(mert);


        // Alp - BTC - ABOVE - INACTIVE
        PriceAlert alert5 = new PriceAlert();
        alert5.setSymbol("BTCUSDT");
        alert5.setTargetPrice(new BigDecimal("150000"));
        alert5.setCondition(AlertCondition.ABOVE);
        alert5.setActive(false);
        alert5.setUser(alp);


        priceAlertRepository.saveAll(
                List.of(alert1,alert2,alert3,alert4,alert5));


        Specification<PriceAlert> spec = PriceAlertSpecification
                        .belongsToUser("alp@test.com")
                        .and(
                                PriceAlertSpecification
                                        .hasSymbol("BTCUSDT"))
                        .and(
                                PriceAlertSpecification
                                        .hasActive(true))
                        .and(
                                PriceAlertSpecification
                                        .hasCondition(AlertCondition.ABOVE));


        List<PriceAlert> result = priceAlertRepository.findAll(spec);


        assertEquals(1, result.size());

        PriceAlert resultAlert = result.get(0);

        assertEquals("BTCUSDT", resultAlert.getSymbol());
        assertEquals(AlertCondition.ABOVE, resultAlert.getCondition());
        assertEquals(true, resultAlert.isActive());
        assertEquals(
                "alp@test.com",
                resultAlert.getUser().getEmail());
    }
    
    
    
    @Test
    void shouldReturnPaginatedAndSortedAlerts() {

        User user = new User();
        user.setEmail("alp@test.com");
        user.setPassword("fake-password");
        user = userRepository.save(user);

        PriceAlert alert1 = new PriceAlert();
        alert1.setSymbol("BTCUSDT");
        alert1.setTargetPrice(new BigDecimal("80000"));
        alert1.setCondition(AlertCondition.ABOVE);
        alert1.setActive(true);
        alert1.setUser(user);

        PriceAlert alert2 = new PriceAlert();
        alert2.setSymbol("BTCUSDT");
        alert2.setTargetPrice(new BigDecimal("120000"));
        alert2.setCondition(AlertCondition.ABOVE);
        alert2.setActive(true);
        alert2.setUser(user);

        PriceAlert alert3 = new PriceAlert();
        alert3.setSymbol("BTCUSDT");
        alert3.setTargetPrice(new BigDecimal("100000"));
        alert3.setCondition(AlertCondition.ABOVE);
        alert3.setActive(true);
        alert3.setUser(user);

        PriceAlert alert4 = new PriceAlert();
        alert4.setSymbol("BTCUSDT");
        alert4.setTargetPrice(new BigDecimal("150000"));
        alert4.setCondition(AlertCondition.ABOVE);
        alert4.setActive(true);
        alert4.setUser(user);

        priceAlertRepository.saveAll(
                List.of(alert1, alert2, alert3, alert4));

        Specification<PriceAlert> spec = PriceAlertSpecification
                        .belongsToUser("alp@test.com")
                        .and(
                                PriceAlertSpecification
                                        .hasSymbol("BTCUSDT"));

        Pageable pageable = PageRequest.of(
                0,
                2,
                Sort.by("targetPrice").descending());

        Page<PriceAlert> result = priceAlertRepository.findAll(
                        spec,
                        pageable);
        

        assertEquals(2, result.getContent().size());

        assertEquals(4, result.getTotalElements());

        assertEquals(2, result.getTotalPages());

        assertEquals(0, result.getNumber());

        assertEquals(
                0,
                new BigDecimal("150000")
                        .compareTo(
                                result.getContent()
                                        .get(0)
                                        .getTargetPrice()));

        assertEquals(
                0,
                new BigDecimal("120000")
                        .compareTo(
                                result.getContent()
                                        .get(1)
                                        .getTargetPrice()));
    
    
    }
    
}