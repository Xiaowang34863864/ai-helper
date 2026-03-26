package com.example.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "name不能为空")
  @Size(max = 100, message = "name长度不能超过100")
  @Column(nullable = false, length = 100)
  private String name;

  @NotBlank(message = "email不能为空")
  @Email(message = "email格式不正确")
  @Size(max = 150, message = "email长度不能超过150")
  @Column(nullable = false, unique = true, length = 150)
  private String email;

  @NotBlank(message = "role不能为空")
  @Size(max = 50, message = "role长度不能超过50")
  @Column(nullable = false, length = 50)
  private String role;

  @NotBlank(message = "status不能为空")
  @Size(max = 20, message = "status长度不能超过20")
  @Column(nullable = false, length = 20)
  private String status;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}
