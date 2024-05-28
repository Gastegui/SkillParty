package com.example.securingweb.ORM.ficheros;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.securingweb.ORM.servicios.servicio.Servicio;

@Service
public class FicheroService 
{
    // Repositorios necesarios
    //private FicheroRepository ficheroRepository;
    public void crearFichero (MultipartFile file, Servicio guardar, String directory) throws IOException
    {
        // Obtener la extensión del archivo subido
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        int i = originalFilename.lastIndexOf('.');
        if (i > 0) {
            extension = originalFilename.substring(i);
        }

        String userHome = System.getProperty("user.home");
        StringBuilder builder = new StringBuilder();
        builder.append(userHome);
        builder.append(File.separator);
        builder.append("ficheros"); 
        builder.append(File.separator);
        builder.append(directory);
        builder.append(File.separator);
        builder.append("portada"); // Aquí se asegura que el nombre del archivo siempre sea "portada"
        builder.append(extension);

        Path path = Paths.get(builder.toString());

        // Crear los directorios si no existen
        if (!Files.exists(path.getParent())) {
            Files.createDirectories(path.getParent());
        }

        byte[] fileBytes = file.getBytes();
        Files.write(path, fileBytes);

        // Asigna la dirección de la portada al servicio
        Fichero portNueva = new Fichero();
        portNueva.setDireccion(builder.toString());
        portNueva.setExtension(extension);
        guardar.setPortada(portNueva);
    }

    public void editFichero (MultipartFile file, Servicio guardar, Servicio nuevo, String tituloViejo, Fichero ficheroGuardado) throws IOException
    {
        // Obtener la extensión del archivo subido
    String originalFilename = file.getOriginalFilename();
    String extension = "";
    int i = originalFilename.lastIndexOf('.');
    if (i > 0) {
        extension = originalFilename.substring(i);
    }

        // Guardar la nueva imagen
        String userHome = System.getProperty("user.home");
        StringBuilder builder = new StringBuilder();
        builder.append(userHome);
        builder.append(File.separator);
        builder.append("ficheros");
        builder.append(File.separator);
        builder.append(nuevo.getTitulo());
        builder.append(File.separator);
        builder.append("portada"); // Nombre de la nueva portada
        builder.append(extension);

        Path newPath = Paths.get(builder.toString());

        // Crear los directorios si no existen
        if (!Files.exists(newPath.getParent())) {
            Files.createDirectories(newPath.getParent());
        }

        byte[] fileBytes = file.getBytes();
        Files.write(newPath, fileBytes);

        Path oldPath = Paths.get(ficheroGuardado.getDireccion());
        Path parentPath = oldPath.getParent();
            
        // Eliminar el archivo del sistema de archivos
        if (Files.exists(oldPath)) 
        {
            Files.delete(oldPath);
            if (!nuevo.getTitulo().equals(tituloViejo))
            {
                if (Files.exists(parentPath)) 
                {
                    Files.delete(parentPath);
                }
            }
        }

        ficheroGuardado.setDireccion(newPath.toString());
        ficheroGuardado.setExtension(extension);
    }
}