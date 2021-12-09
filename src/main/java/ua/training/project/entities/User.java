package ua.training.project.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Collection;
import java.util.Collections;

import static ua.training.project.utils.PatternsHolder.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
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

    @DecimalMin(value = MINIMAL_AGE)
    private Integer age;

    private Boolean isStudent = true;

    @NotNull
    private String password;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

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
