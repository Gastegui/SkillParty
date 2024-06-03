package com.example.securingweb.ORM.ficheros;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.securingweb.ORM.servicios.servicio.Servicio;
import com.example.securingweb.ORM.usuarios.usuario.Usuario;

@Service
public class FicheroService 
{

    public boolean isVid(MultipartFile file)
    {
        return getExtension(file.getOriginalFilename()).equals(".mp4");
    }

    public boolean isImg(MultipartFile file)
    {
        String extension = getExtension(file.getOriginalFilename());
        return extension.equals(".png") || extension.equals(".jpg");
    }

    /**
     * Borra el fichero indicado, y si es el último fichero en la carpeta, también borra la carpeta
     */
    public void borrarFichero(Fichero fichero) throws IOException
    {
        deleteFile(fichero.getDireccion());
    }

    /**
     * Crea la foto de perfil del usuario
     */
    public void crearPerfil(MultipartFile file, Usuario usuario) throws IOException
    {
        String extension = getExtension(file.getOriginalFilename());
        String filePath = buildFilePathProfile(System.getProperty("user.home"), usuario.getId().toString(), extension);

        Path path = Paths.get(filePath);

        createDirectoriesIfNotExist(path);

        saveFile(file, path);

        Fichero nuevo = new Fichero();
        nuevo.setDireccion(filePath);
        nuevo.setExtension(extension);
        usuario.setImagen(nuevo);
    }

    /**
     * Cambia la foto de perfil del usuario
     */
    public void cambiarPerfil(MultipartFile file, Usuario usuario, Fichero fichero) throws IOException
    {
        String extension = getExtension(file.getOriginalFilename());
        String path = buildFilePathProfile(System.getProperty("user.home"), usuario.getId().toString(), extension);

        Files.delete(Paths.get(fichero.getDireccion()));
        saveFile(file, Paths.get(path));

        fichero.setDireccion(path);
        fichero.setExtension(extension);

    }

    /**
     * Crea cualquier tipo de fichero que usan los servicios. El nombre tiene que ser: portada, muestra1, muestra2...
     * Returnea el fichero creado
     */
    public Fichero crearFicheroServicio(MultipartFile file, Servicio servicio, String nombre) throws IOException
    {
        String extension = getExtension(file.getOriginalFilename());

        String userHome = System.getProperty("user.home");
        String pathStr = buildFilePathServiceOrCourse(userHome, servicio.getCreador().getId().toString(), servicio.getId().toString(), nombre, extension, false);

        Path path = Paths.get(pathStr);

        createDirectoriesIfNotExist(path);

        saveFile(file, path);

        Fichero nuevo = new Fichero();
        nuevo.setDireccion(pathStr);
        nuevo.setExtension(extension);
        return nuevo;
    }

    /**
     * Crea cualquier tipo de fichero que usan los cursos. El nombre tiene que ser: portada, elemento1, elemento2...
     */
    /*
    public Fichero crearFicheroCurso(MultipartFile file, Curso curso, String nombre) throws IOException
    {
        String extension = getExtension(file.getOriginalFilename());

        String userHome = System.getProperty("user.home");
        String pathStr = buildFilePathServiceOrCourse(userHome, curso.getCreador().getId().toString(), curso.getId().toString(), nombre, extension, true);

        Path path = Paths.get(pathStr);

        createDirectoriesIfNotExist(path);

        saveFile(file, path);

        Fichero nuevo = new Fichero();
        nuevo.setDireccion(pathStr);
        nuevo.setExtension(extension);
        return nuevo;
    }
    */

    /**
     * Borrar el fichero guardado, y guardar el nuevo (solo servicios)
     */
    public void cambiarFicheroServicio(MultipartFile file, Servicio servicio, String nombre, Fichero fichero) throws IOException
    {
        String extension = getExtension(file.getOriginalFilename());
        String userHome = System.getProperty("user.home");
        String newPathStr = buildFilePathServiceOrCourse(userHome, servicio.getCreador().getId().toString(), servicio.getId().toString(), nombre, extension, false);

        Path newPath = Paths.get(newPathStr);

        deleteFile(fichero.getDireccion());

        saveFile(file, newPath);

        fichero.setDireccion(newPathStr);
        fichero.setExtension(extension);
    }

    /**
     * Borrar el fichero guardado, y guardar el nuevo (solo cursos)
     */
    /*
    public void cambiarFicheroCurso(MultipartFile file, Curso curso, String nombre, Fichero fichero) throws IOException
    {
        String extension = getExtension(file.getOriginalFilename());
        String userHome = System.getProperty("user.home");
        String newPathStr = buildFilePathServiceOrCourse(userHome, curso.getCreador().getId().toString(), curso.getId().toString(), nombre, extension, true);

        Path newPath = Paths.get(newPathStr);

        deleteOldFileIfExists(fichero.getDireccion());

        saveFile(file, newPath);

        fichero.setDireccion(newPathStr);
        fichero.setExtension(extension);
    }
    */
    /**
     * Le cambia el nombre al fichero (no lo mueve de carpeta)
     * Hace return del fichero con el nombre cambiado
     */
    public Fichero cambiarNombreFichero(Fichero fichero, String nuevo) throws IOException
    {
        Path oldPath = Paths.get(fichero.getDireccion());
        Path newPath = Paths.get(fichero.getDireccion().substring(0, fichero.getDireccion().lastIndexOf(File.separator))+File.separator+nuevo+fichero.getExtension());

        fichero.setDireccion(newPath.toString());

        Files.move(oldPath, newPath);
        return fichero;
    }

    private String getExtension(String filename) 
    {
        int i = filename.lastIndexOf('.');
        return (i > 0) ? filename.substring(i) : "";
    }

    private String buildFilePathProfile(String userHome, String usuario, String extension) 
    {
        return userHome + File.separator + "SkillPartyFiles" + File.separator + usuario + File.separator + "profile" + extension;
    }

    private String buildFilePathServiceOrCourse(String userHome, String usuario, String titulo, String nombre, String extension, boolean isCourse)
    {
        return userHome + File.separator + "SkillPartyFiles" + File.separator + usuario + File.separator + (isCourse ? "courses" : "services") + File.separator + titulo + File.separator + nombre + extension;
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

    private void deleteFile(String path) throws IOException 
    {
        Path oldPath = Paths.get(path);

        if (Files.exists(oldPath))
        {
            Files.delete(oldPath);
            if(isDirEmpty(oldPath.getParent()))     //Si no hay más archivos en la carpeta,
                Files.delete(oldPath.getParent());  //Borrarla
        }
    }

    private boolean isDirEmpty(Path dir) throws IOException
    {
        return !Files.newDirectoryStream(dir).iterator().hasNext();
    }
}