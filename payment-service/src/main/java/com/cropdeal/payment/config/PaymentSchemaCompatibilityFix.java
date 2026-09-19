package com.cropdeal.payment.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class PaymentSchemaCompatibilityFix implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    public PaymentSchemaCompatibilityFix(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!exists("information_schema.tables", "table_name", "payments")) {
            return;
        }

        if (columnExists("payment_id")) {
            jdbcTemplate.execute(
                    "ALTER TABLE payments CHANGE COLUMN payment_id id BIGINT NOT NULL AUTO_INCREMENT"
            );
        } else if (!columnExists("id")) {
            jdbcTemplate.execute(
                    "ALTER TABLE payments ADD COLUMN id BIGINT NOT NULL AUTO_INCREMENT UNIQUE FIRST"
            );
        }

        try {
            jdbcTemplate.execute("ALTER TABLE payments MODIFY COLUMN payment_method VARCHAR(50)");
        } catch (Exception ignored) {
        }

        try {
            jdbcTemplate.execute("ALTER TABLE payments MODIFY COLUMN status VARCHAR(50)");
        } catch (Exception ignored) {
        }

        if (columnExists("currency")) {
            try {
                jdbcTemplate.execute("ALTER TABLE payments MODIFY COLUMN currency VARCHAR(10) NULL DEFAULT 'INR'");
            } catch (Exception ignored) {
            }
        }

        if (columnExists("updated_at")) {
            try {
                jdbcTemplate.execute("ALTER TABLE payments MODIFY COLUMN updated_at DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP");
            } catch (Exception ignored) {
            }
        }
    }

    private boolean columnExists(String columnName) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.columns "
                        + "WHERE table_schema = DATABASE() AND table_name = 'payments' AND column_name = ?",
                Integer.class,
                columnName
        );
        return count != null && count > 0;
    }

    private boolean exists(String informationSchemaTable, String column, String value) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM " + informationSchemaTable
                        + " WHERE table_schema = DATABASE() AND " + column + " = ?",
                Integer.class,
                value
        );
        return count != null && count > 0;
    }
}
