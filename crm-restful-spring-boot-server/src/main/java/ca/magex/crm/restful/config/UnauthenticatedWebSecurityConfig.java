package ca.magex.crm.restful.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.spring.security.jwt.JwtRequestFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
@Profile(MagexCrmProfiles.CRM_NO_AUTH_EMBEDDED)
public class UnauthenticatedWebSecurityConfig extends WebSecurityConfigurerAdapter {

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
		httpSecurity.csrf().disable()
				/* get the list of public resources */
				.authorizeRequests().antMatchers(
						"/authenticate",
						"/",
						"/api/**",
						"/crm.yaml",
						"/swagger-ui-bundle.js",
						"/swagger-ui.css",
						"/favicon.ico").permitAll()												
				/* user details needs to be protected */
					.antMatchers("/validate").hasRole("AUTH_REQUEST")
				/* actuator needs to be protected */
					.antMatchers("/actuator/shutdown").hasRole("SYS_ADMIN")
					.antMatchers("/actuator/**").hasAnyRole("SYS_ADMIN", "APP_ADMIN")
					/* any other requests require authentication */
					.anyRequest().authenticated()
				.and().exceptionHandling().authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
				.and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and().addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
	}
}