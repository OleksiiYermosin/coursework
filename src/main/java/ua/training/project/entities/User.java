package ua.training.project.entities;

import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import static ua.training.project.utils.PatternsHolder.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
@DynamicUpdate
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Pattern(regexp = NAME_PATTERN)
    private String name;

    @Pattern(regexp = SURNAME_PATTERN)
    private String surname;

    @Pattern(regexp = USERNAME_PATTERN)
    private String username;

    @Min(18)
    @Max(100)
    private Integer age;

    private Boolean isStudent = true;

    @DecimalMin(value = "0.00")
    @Digits(integer = 999,fraction = 2)
    private BigDecimal balance;

    @NotNull
    @NotEmpty
    private String password;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "user")
    private Set<Attendance> attendances;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(getRole());
    }

    @Transient
    private final boolean isAccountNonExpired = true;

    @Transient
    private final boolean isAccountNonLocked = true;

    @Transient
    private final boolean isCredentialsNonExpired = true;

    @Transient
    private final boolean isEnabled = true;
}
