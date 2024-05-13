package com.example.securingweb.Controladores;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SnakeController 
{
    
    @GetMapping("/snake")
    public String snake(@RequestParam(name="elementos", required = false) String numero,Model modelo)
    {
        if(numero == null)
            modelo.addAttribute("elementos", 15);
        else
        {
            try
            {
                int tamaño = Integer.parseInt(numero);
                if(tamaño > 100)
                    tamaño = 100;
                modelo.addAttribute("elementos", tamaño);
            }
            catch (Exception e)
            {
                modelo.addAttribute("elementos", 15);
            }
        }
        return "snake";
    }

}
