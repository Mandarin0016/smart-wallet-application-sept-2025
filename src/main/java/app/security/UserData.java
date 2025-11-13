package app.security;

import app.user.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

// Основни данни за аутентикирания потребител = принципъл
// Това е моя пинципъл обект
// Single Responsibility =
// Principle = object that stores Security data for authenticated user
@Data
@AllArgsConstructor
public class UserData implements UserDetails {

    private UUID userId;
    private String username;
    private String password;
    private UserRole role;
    private String email;
//    private List<String> permissions;
    private boolean isAccountActive;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

//        SimpleGrantedAuthority role = new SimpleGrantedAuthority("ROLE_" + "ADMIN");
//        SimpleGrantedAuthority permission1 = new SimpleGrantedAuthority("read_all_products");
//        SimpleGrantedAuthority permission2 = new SimpleGrantedAuthority("do_transfer");
//        SimpleGrantedAuthority permission3 = new SimpleGrantedAuthority("open_new_wallet");

        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role.name());
//        List<SimpleGrantedAuthority> list = permissions.stream().map(permission -> new SimpleGrantedAuthority(permission)).toList();

        return List.of(authority);
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.isAccountActive;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.isAccountActive;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.isAccountActive;
    }

    @Override
    public boolean isEnabled() {
        return this.isAccountActive;
    }
}
