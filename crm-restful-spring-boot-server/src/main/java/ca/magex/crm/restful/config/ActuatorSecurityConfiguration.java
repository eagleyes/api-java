package ca.magex.crm.restful.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import ca.magex.crm.spring.security.jwt.JwtRequestFilter;

@Configuration
@Order(1)
@Description("Defines the Roles required for accessing the actuator endpoints")
public class ActuatorSecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired private UserDetailsService jwtUserDetailsService;
	@Autowired private UserDetailsPasswordService jwtUserDetailsPasswordService;
	@Autowired private JwtRequestFilter jwtRequestFilter;
	@Autowired private PasswordEncoder passwordEncoder;

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(jwtUserDetailsService)
				.passwordEncoder(passwordEncoder)
				.userDetailsPasswordManager(jwtUserDetailsPasswordService);
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		/*
		 * actuator endpoints
		 * @formatter:off
		 */
		httpSecurity.authorizeRequests()
				.antMatchers("/actuator/shutdown").hasRole("SYS_ADMIN")
				.antMatchers("/actuator/*").hasAnyRole("SYS_ADMIN", "APP_ADMIN")		
			.and().exceptionHandling().authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
			.and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and().addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
			.csrf().disable();
//			.csrf().csrfTokenRepository(new CookieCsrfTokenRepository());
	}
}