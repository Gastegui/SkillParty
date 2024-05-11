package com.example.securingweb.ORM;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String contraseña;
    private String rol;
    
    public Long getId(){return id;}
    public String getNombre(){return nombre;}
    public String getContraseña(){return contraseña;}
    public String getRol(){return rol;}

    public void setId(Long i){id=i;}
    public void setNombre(String n){nombre=n;}
    public void setContraseña(String c){contraseña=c;}
    public void setRol(String r){rol=r;}
}