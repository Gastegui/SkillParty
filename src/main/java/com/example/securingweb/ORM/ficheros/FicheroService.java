package com.example.securingweb.ORM.ficheros;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.securingweb.ORM.servicios.servicio.Servicio;

import jakarta.transaction.Transactional;

@Service
public class FicheroService 
{
    @Transactional
    public void crearFichero (MultipartFile file, Servicio guardar, String directory) throws IOException
    {
        String originalFilename = file.getOriginalFilename();
        String extension = getExtension(originalFilename);

        String userHome = System.getProperty("user.home");
        String filePath = buildFilePath(userHome, directory, extension);

        Path path = Paths.get(filePath);

        createDirectoriesIfNotExist(path);

        saveFile(file, path);

        Fichero portNueva = new Fichero();
        portNueva.setDireccion(filePath);
        portNueva.setExtension(extension);
        guardar.setPortada(portNueva);
    }

    @Transactional
    public void editFichero (MultipartFile file, Servicio guardar, Servicio nuevo, String tituloViejo, Fichero ficheroGuardado) throws IOException
    {
        String originalFilename = file.getOriginalFilename();
        String extension = getExtension(originalFilename);

        String userHome = System.getProperty("user.home");
        String newFilePath = buildFilePath(userHome, nuevo.getTitulo(), extension);

        Path newPath = Paths.get(newFilePath);

        createDirectoriesIfNotExist(newPath);

        saveFile(file, newPath);

        deleteOldFileIfExists(ficheroGuardado.getDireccion(), nuevo.getTitulo(), tituloViejo);

        ficheroGuardado.setDireccion(newFilePath);
        ficheroGuardado.setExtension(extension);
    }

    private String getExtension(String filename) 
    {
        int i = filename.lastIndexOf('.');
        return (i > 0) ? filename.substring(i) : "";
    }

    private String buildFilePath(String userHome, String directory, String extension) 
    {
        return userHome + File.separator + "ficheros" + File.separator + directory + File.separator + "portada" + extension;
    }

    private void createDirectoriesIfNotExist(Path path) throws IOException 
    {
        if (!Files.exists(path.getParent())) 
        {
            Files.createDirectories(path.getParent());
        }
    }

    private void saveFile(MultipartFile file, Path path) throws IOException 
    {
        byte[] fileBytes = file.getBytes();
        Files.write(path, fileBytes);
    }

    private void deleteOldFileIfExists(String oldFilePath, String newTitle, String oldTitle) throws IOException 
    {
        Path oldPath = Paths.get(oldFilePath);
        Path parentPath = oldPath.getParent();

        if (Files.exists(oldPath)) 
        {
            Files.delete(oldPath);
            if (!newTitle.equals(oldTitle) && Files.exists(parentPath)) 
            {
                Files.delete(parentPath);
            }
        }
    }
}