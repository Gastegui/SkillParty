package com.example.securingweb.ORM.servicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ServicioService 
{
    private ServicioRepository servicioRepository;

    @Autowired
    public ServicioService(ServicioRepository servicioRepository) 
    {
        this.servicioRepository = servicioRepository;
    }

    public List<Servicio> obtenerTodosLosServicios() 
    {
        return servicioRepository.findAll();
    }

    public Servicio guardarServicio(Servicio servicio) 
    {
        return servicioRepository.save(servicio);
    }
}
