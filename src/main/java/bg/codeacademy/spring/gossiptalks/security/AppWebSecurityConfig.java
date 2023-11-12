package bg.codeacademy.spring.gossiptalks.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, jsr250Enabled = true, securedEnabled = true)
public class AppWebSecurityConfig extends WebSecurityConfigurerAdapter
{
  @Autowired
  private UserDetailsService userDetailsService;

  @Autowired
  private CustomAuthenticationEntryPoint entryPoint;

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception
  {
    auth.userDetailsService(userDetailsService);
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception
  {
//    disable session creation - store no state
    http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    http.headers().frameOptions().disable();

    http.httpBasic().and()
        .authorizeRequests()
        .antMatchers(HttpMethod.GET, "/api/v1/users").authenticated()
        .antMatchers(HttpMethod.POST, "/api/v1/users").permitAll()
        .antMatchers(HttpMethod.GET, "/api/v1/users/me").authenticated()
        .antMatchers(HttpMethod.POST, "/api/v1/users/me").authenticated()
        .antMatchers(HttpMethod.POST, "/api/v1/users/{username}/follow").authenticated()

        .antMatchers(HttpMethod.GET, "/api/v1/users/{username}/gossips").authenticated()
        .antMatchers(HttpMethod.GET, "/api/v1/gossips").authenticated()
        .antMatchers(HttpMethod.POST, "/api/v1/gossips").authenticated()
        .antMatchers("/h2").permitAll()
        .anyRequest().permitAll().and()
        .exceptionHandling().authenticationEntryPoint(entryPoint).and()
        .csrf().disable();
  }

  @Bean
  public BCryptPasswordEncoder getEncoder()
  {
    return new BCryptPasswordEncoder();
  }
}
