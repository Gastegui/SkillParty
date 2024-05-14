package com.example.securingweb.Controladores;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

public class CustomErrorController implements ErrorController 
{
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request)  
    {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        
        if (status != null) 
        {
            int statusCode = Integer.valueOf(status.toString());
        
            if (statusCode == HttpStatus.NOT_FOUND.value()) 
                return "error/404";
            else if (statusCode == HttpStatus.FORBIDDEN.value())
                return "error/403";
        }
        return "error";
    }    
}
