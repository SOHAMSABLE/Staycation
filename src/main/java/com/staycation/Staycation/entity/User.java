package com.staycation.Staycation.entity;
import com.staycation.Staycation.entity.enums.Gender;
import com.staycation.Staycation.entity.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@Table(name = "App_user")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String name;

    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_"+role.name()))
                .collect(Collectors.toSet());
    }

    @Override
    public String getUsername() {
        return email;
    }

//    @Override
//    public boolean equals(Object o) {
//        if (!(o instanceof User user)) return false;
//        return Objects.equals(id, user.id);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hashCode(id);
//    }
@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null) return false;

    // Handle Hibernate proxy classes correctly
    if (org.hibernate.Hibernate.getClass(this) != org.hibernate.Hibernate.getClass(o)) {
        return false;
    }

    User user = (User) o;
    return id != null && id.equals(user.id);
}

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }


}
