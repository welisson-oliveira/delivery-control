package com.acert.deliverycontrol.domain.client;

import com.acert.deliverycontrol.domain.delivery.Delivery;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Table(name = "client")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
@AllArgsConstructor
public class Client implements UserDetails {

    @Id
    @SequenceGenerator(name = "client_seq", sequenceName = "client_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "client_seq")
    private Long id;
    private String name;
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;
    private String address;
    private String password;

    @OneToMany(mappedBy = "client")
    private final List<Delivery> deliveries = new ArrayList<>();

    @ManyToMany(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    @JoinTable(
            name = "client_role",
            joinColumns = @JoinColumn(name = "client_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private final List<Role> roles = new ArrayList<>();

    public void updateClient(final Client updatedClient) {
        this.name = updatedClient.getName();
        this.email = updatedClient.getEmail();
        this.phoneNumber = updatedClient.getPhoneNumber();
        this.address = updatedClient.getAddress();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles;
    }

    public List<String> getRoles() {
        final List<String> authorities = new ArrayList<>();
        for (final GrantedAuthority authority : this.roles) {
            authorities.add(authority.getAuthority());
        }
        return authorities;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}

