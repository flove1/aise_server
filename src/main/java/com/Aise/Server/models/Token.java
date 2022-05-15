package com.Aise.Server.models;

import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Entity
@Table(name="tokens")
public class Token {
  @Column @Id private String token;

  @ManyToOne(fetch = FetchType.LAZY, optional = false, targetEntity = User.class)
  @JoinColumn(name = "user_id", referencedColumnName = "id") 
  private User user;

  @Column private Date expiryDate;
  
  public Token() {
  }

  public Token(User user) {
		String secretKey = "aise";
		List<GrantedAuthority> grantedAuthorities = AuthorityUtils
				.commaSeparatedStringToAuthorityList("ROLE_USER");
    this.user = user;
    this.expiryDate = new Date(System.currentTimeMillis() + 600000);
		String token = Jwts
				.builder()
				.setId("aiseJWT")
				.setSubject(user.getEmail())
				.claim("authorities",
						grantedAuthorities.stream()
								.map(GrantedAuthority::getAuthority)
								.collect(Collectors.toList()))
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + 600000))
				.signWith(SignatureAlgorithm.HS256,
						secretKey.getBytes()).compact();
    this.token = token;
  }
  
  public String getToken() {
    return token;
  }
  public User getUser() {
    return user;
  }
  public String getexpiryDate() {
    return expiryDate.toString();
  }

  public void setToken(String token) {
    this.token = token;
  }
  public void setExpiryDate(Date expiryDate) {
    this.expiryDate = expiryDate;
  }
}