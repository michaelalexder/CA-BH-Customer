package com.bhca.customer.db;

import com.bhca.common.AbstractBaseEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
public class Customer extends AbstractBaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String surname;

    @Column(nullable = false)
    private BigDecimal balance;

    @Column(nullable = false)
    @CreationTimestamp
    private Instant createdAt;
}
