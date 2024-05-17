package com.example.securingweb.ORM.servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.securingweb.ORM.ficheros.Fichero;
import com.example.securingweb.ORM.ficheros.FicheroRepository;
import com.example.securingweb.ORM.servicios.categoria.Categoria;
import com.example.securingweb.ORM.servicios.categoria.CategoriaRepository;
import com.example.securingweb.ORM.servicios.servicio.Servicio;
import com.example.securingweb.ORM.servicios.servicio.ServicioRepository;
import com.example.securingweb.ORM.usuario.Usuario;
import com.example.securingweb.ORM.usuario.UsuarioRepository;
import java.util.List;
import java.util.Optional;

@Service
public class ServicioService 
{
    private ServicioRepository servicioRepository;
    private CategoriaRepository categoriaRepository;
    private FicheroRepository ficheroRepository;
    private UsuarioRepository usuarioRepository;

    @Autowired
    public ServicioService(ServicioRepository servicioRepository, CategoriaRepository categoriaRepository, FicheroRepository ficheroRepository, UsuarioRepository usuarioRepository) 
    {
        this.servicioRepository = servicioRepository;
        this.categoriaRepository = categoriaRepository;
        this.ficheroRepository = ficheroRepository;
    }

    public List<Servicio> obtenerTodosLosServicios() 
    {
        return servicioRepository.findAll();
    }

    public Servicio guardarServicio(Servicio servicio) 
    {

        Optional<Fichero> ficheroExistente = ficheroRepository.findById(servicio.getPortada().getId());
        Fichero portada;
        if(ficheroExistente.isEmpty())
            portada = ficheroRepository.save(ficheroExistente.get());
        else
            portada = ficheroExistente.get();

        Optional<Categoria> categoriaExistente = categoriaRepository.findById(servicio.getCategoria().getId());
        Categoria categoria;
        if(categoriaExistente.isEmpty())
            categoria = categoriaRepository.save(categoriaExistente.get());
        else 
            categoria = categoriaExistente.get();
    
        Optional<Usuario> creadorExistente = usuarioRepository.findById(servicio.getCreador().getId());
        if(creadorExistente.isEmpty())
            return null;

        servicio.setCreador(creadorExistente.get());
        servicio.setCategoria(categoria);
        servicio.setPortada(portada);
        return servicioRepository.save(servicio);
    }
}
