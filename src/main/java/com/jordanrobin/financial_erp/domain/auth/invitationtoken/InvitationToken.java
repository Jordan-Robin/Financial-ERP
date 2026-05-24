package com.jordanrobin.financial_erp.domain.auth.invitationtoken;

import com.jordanrobin.financial_erp.domain.BaseEntity;
import com.jordanrobin.financial_erp.domain.auth.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@ToString(of = {"tokenHash", "expiresAt", "usedAt"})
@Builder
@Entity
@Table(name = "invitation_tokens", schema = "public")
public class InvitationToken extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String tokenHash;

    @Column(nullable = false)
    private Instant expiresAt;

    @Column()
    private Instant usedAt;
}
