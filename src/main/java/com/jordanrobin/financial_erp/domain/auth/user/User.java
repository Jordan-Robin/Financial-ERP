package com.jordanrobin.financial_erp.domain.auth.user;

import com.jordanrobin.financial_erp.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter @Getter
@ToString(of = {"email", "superAdmin"})
@Builder
@Entity
@Table(name = "users", schema = "public")
public class User extends BaseEntity {

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column()
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private UserStatus status;

    @Builder.Default
    @Column(name = "is_super_admin", nullable = false)
    private boolean superAdmin = false;
}
