package vn.iotstar.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Định nghĩa PasswordEncoder sử dụng BCrypt
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Cấu hình bảo mật
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
        .csrf().disable()
        .authorizeRequests()
            .anyRequest().permitAll()  // Tất cả các request đều được phép mà không cần xác thực
        .and()
        .formLogin().disable()  // Tắt trang login mặc định của Spring Security
        .httpBasic().disable()  // Tắt xác thực HTTP Basic
        .logout()
            .logoutUrl("/logout")  // Đảm bảo đường dẫn logout đúng
            .logoutSuccessUrl("/")  // Chuyển hướng về trang chủ sau khi đăng xuất
            .invalidateHttpSession(true)  // Xóa session khi logout
            .deleteCookies("JSESSIONID");  // Xóa cookie session khi logout

        return http.build();
    }
}
