package com.example.securingweb.ORM.usuarios.usuario;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.example.securingweb.ORM.ficheros.Fichero;
import com.example.securingweb.ORM.servicios.comprarServicios.ComprarServicio;
import com.example.securingweb.ORM.servicios.servicio.Servicio;
import com.example.securingweb.ORM.servicios.valorarServicios.ValorarServicios;
import com.example.securingweb.ORM.usuarios.autoridad.Autoridad;
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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

// Clase de entidad para la tabla "usuario"
@Entity
@Table(name="usuarios")
public class Usuario implements UserDetails
{
    // Atributos de la entidad Usuario
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;
    @Column(unique=true)
    private String username;
    private String password;

    // Relación muchos a muchos con la entidad Autoridad
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "usuarios_autoridades",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "autoridad_id"))

    private List<Autoridad> autoridades = new ArrayList<>();

    private boolean enabled;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;

    private String nombre;
    private String apellidos;

    @Column(name="fecha_de_nacimiento")
    private Date fechaDeNacimiento;
    private String telefono;
    private String email;
    private BigDecimal saldo;
    @Column(name="por_cobrar")
    private BigDecimal porCobrar;
    private Long puntuacion;

    @ManyToOne
    @JoinColumn(name = "imagen_id")
    private Fichero imagen;

    @OneToMany(mappedBy="creador", cascade=CascadeType.ALL, orphanRemoval=true)
    private List<Servicio> servicios;
    
    @OneToMany(mappedBy="usuario", cascade=CascadeType.ALL, orphanRemoval=true)
    private List<ComprarServicio> serviciosComprados;

    @OneToMany(mappedBy="usuario", cascade=CascadeType.ALL, orphanRemoval=true)
    private List<ValorarServicios> serviciosValorados;
    // Métodos getter y setter para los atributos de la clase

    public Long getId()
    {
        return id;
    }
    public String getUsername()
    {
        return username;
    }
    public String getPassword()
    {
        return password;
    }
    public List<GrantedAuthority> getAuthorities() 
    {
        List<GrantedAuthority> ret = new ArrayList<>();
        for(Autoridad a : autoridades)
        {
            ret.add(new SimpleGrantedAuthority(a.getAutoridad()));
        }
        return ret;
    }
    public List<Autoridad> getAuthoritiesEntity() 
    {
        return autoridades;
    }
    public boolean isEnabled()
    {
        return enabled;
    }
    public boolean isAccountNonExpired()
    {
        return accountNonExpired;
    }
    public boolean isAccountNonLocked()
    {
        return accountNonLocked;
    }
    public boolean isCredentialsNonExpired()
    {
        return credentialsNonExpired;
    }
    public String getNombre()
    {
        return nombre;
    }
    public String getApellidos()
    
    {
        return apellidos;
    }
    public Date getFechaDeNacimiento()
    {
        return fechaDeNacimiento;
    }
    public String getTelefono()
    {
        return telefono;
    }
    public String getEmail()
    {
        return email;
    }
    public List<Servicio> getServiciosCreados()
    {
        return servicios;
    }
    public List<ComprarServicio> getServiciosComprados()
    {
        return serviciosComprados;
    }
    public List<ValorarServicios> getServiciosValorados()
    {
        return serviciosValorados;
    }
    public BigDecimal getSaldo()
    {
        return saldo;
    }
    public BigDecimal getPorCobrar()
    {
        return porCobrar;
    }
    public boolean isAdmin()
    {
        for(Autoridad a : autoridades)
        {
            if(a.getAutoridad().equals("ADMIN"))
                return true;
        }
        return false;
    }
    public boolean isPro()
    {
        if(isAdmin())
            return true;
            
        for(Autoridad a : autoridades)
        {
            if(a.getAutoridad().equals("USER_PRO"))
                return true;
        }
        return false;
    }
    public boolean isCreatorAll()
    {
        if(isAdmin())
            return true;
            
        for(Autoridad a : autoridades)
        {
            if(a.getAutoridad().equals("CREATE_ALL"))
                return true;
        }
        return false;
    }
    public boolean isCreatorService()
    {
        if(isAdmin())
            return true;

        for(Autoridad a : autoridades)
        {
            if(a.getAutoridad().equals("CREATE_ALL") || a.getAutoridad().equals("CREATE_SERVICE"))
                return true;
        }
        return false;
    }
    public boolean isCreatorCourse()
    {
        if(isAdmin())
            return true;

        for(Autoridad a : autoridades)
        {
            if(a.getAutoridad().equals("CREATE_ALL") || a.getAutoridad().equals("CREATE_COURSE"))
                return true;
        }
        return false;
    }
    public boolean isCreatorAny()
    {
        if(isAdmin())
            return true;
            
        for(Autoridad a : autoridades)
        {
            if(a.getAutoridad().equals("CREATE_ALL") || a.getAutoridad().equals("CREATE_SERVICE") || a.getAutoridad().equals("CREATE_COURSE"))
                return true;
        }
        return false;
    }
    public Long getPuntuacion()
    {
        return puntuacion;
    }
    public Fichero getImagen()
    {
        return imagen;
    }

    public void setId(Long i)
    {
        id=i;
    }
    public void setUsername(String n)
    {
        username=n;
    }
    public void setPassword(String c)
    {
        password=c;
    }
    public void setEnabled(boolean e)
    {
        enabled=e;
    }
    public void setAccountNonExpired(boolean a)
    {
        accountNonExpired=a;
    }
    public void setAccountNonLocked(boolean a)
    {
        accountNonLocked=a;
    }
    public void setCredentialsNonExpired(boolean c)
    {
        credentialsNonExpired=c;
    }
    public void setAutoridadesEntity(List<Autoridad> a)
    {
        autoridades=a;
    }
    public void setAutoridad(String s)
    {
        Autoridad nuevo = new Autoridad();
        nuevo.setAutoridad(s);
        autoridades.add(nuevo);
    }
    public void setNombre(String n)
    {
        nombre = n;
    }
    public void setApellidos(String a)
    {
        apellidos = a;
    }
    public void setFechaDeNacimiento(Date f)
    {
        fechaDeNacimiento = f;
    }
    public void setTelefono(String t)
    {
        telefono = t;
    }
    public void setEmail(String e)
    {
        email = e;
    }
    public void setSaldo(BigDecimal s)
    {
        saldo = s;
    }
    public void setPorCobrar(BigDecimal p)
    {
        porCobrar = p;
    }
    public void setPuntuacion(Long p)
    {
        puntuacion = p;
    }
    public void setImagen(Fichero i)
    {
        imagen = i;
    }

    @Override
    public boolean equals(Object o)
    {
        if(o == null)
        {
            return false;
        }
        if(o.getClass() != this.getClass())
        {
            return false;
        }
        return ((Usuario)o).getUsername().equals(this.getUsername());
        
    }
}