package com.Aise.Server.repositories;

import java.sql.Date;

import com.Aise.Server.models.Token;

import org.springframework.data.jpa.repository.JpaRepository;


public interface TokenRepository extends JpaRepository<Token, String> {  
  Token getByToken(String token);
  Boolean existsByToken(String token);
  void deleteByExpiryDateLessThan(Date date);
}
