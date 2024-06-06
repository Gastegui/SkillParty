package com.example.securingweb.ORM.contactos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ContactoService {

    @Autowired
    private ContactosRepository contactoRepository;

    public List<Contactos> getAllContactos() {
        return contactoRepository.findAll();
    }

    public Optional<Contactos> getContactoById(Long id) {
        return contactoRepository.findById(id);
    }

    public Contactos saveContacto(Contactos contacto) {
        return contactoRepository.save(contacto);
    }

    public void deleteContacto(Long id) {
        contactoRepository.deleteById(id);
    }
}
