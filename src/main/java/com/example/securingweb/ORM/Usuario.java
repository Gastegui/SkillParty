package com.example.securingweb.ORM;

import java.util.ArrayList;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name="Usuario")
public class Usuario implements UserDetails{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;
    @Column(unique=true)
    private String username;
    private String password;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "usuario_autoridad",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "autoridad_id"))
    private List<Autoridad> autoridades = new ArrayList<>();

    private boolean enabled;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    
    public Long getId(){return id;}
    public String getUsername(){return username;}
    public String getPassword(){return password;}

    public List<GrantedAuthority> getAuthorities() 
    {
        List<GrantedAuthority> ret = new ArrayList<>();
        for(Autoridad a : autoridades)
        {
            ret.add(new SimpleGrantedAuthority(a.getAutoridad()));
        }
        return ret;
    }

    public List<Autoridad> getAuthoritiesEntity() {return autoridades;}
    public boolean isEnabled(){return enabled;}
    public boolean isAccountNonExpired(){return accountNonExpired;}
    public boolean isAccountNonLocked(){return accountNonLocked;}
    public boolean isCredentialsNonExpired(){return credentialsNonExpired;}

    public void setId(Long i){id=i;}
    public void setUsername(String n){username=n;}
    public void setPassword(String c){password=c;}
    public void setEnabled(boolean e){enabled=e;}
    public void setAccountNonExpired(boolean a){accountNonExpired=a;}
    public void setAccountNonLocked(boolean a){accountNonLocked=a;}
    public void setCredentialsNonExpired(boolean c){credentialsNonExpired=c;}
    public void setAutoridadesEntity(List<Autoridad> a){autoridades=a;}
    public void setAutoridad(String s)
    {
        Autoridad nuevo = new Autoridad();
        nuevo.setAutoridad(s);
        autoridades.add(nuevo);
    }

    @Override
    public boolean equals(Object o)
    {
        if(o == null)
            return false;
        
        if(o.getClass() != this.getClass())
            return false;

        if(((Usuario)o).getUsername() != this.getUsername())
            return false;

        return true;
    }
}