package fr.fabien.escalade.webapp.controller;

import fr.fabien.escalade.business.UtilisateurManagement;
import fr.fabien.escalade.model.topo.Utilisateur;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class ConnexionController {
    @Autowired
    UtilisateurManagement utilisateurManagement;

    @GetMapping ("/connexion")
    public String connexion(Model model) {
        model.addAttribute("utilisateur", new Utilisateur());
        return "connexion";
    }

    @PostMapping("/connexion")
    public String connexionSubmit(@ModelAttribute Utilisateur utilisateur, BindingResult result, Model model) {
        System.out.println(result);
        model.addAttribute(utilisateur);
        return "result";
    }

    @GetMapping ("/inscription")
    public String inscription(Model model) {
        model.addAttribute("utilisateur", new Utilisateur());
        return "inscription";
    }

    @PostMapping("/inscription")
    public String inscriptionSubmit(@ModelAttribute Utilisateur utilisateur, BindingResult result, Model model){
        if (result.hasErrors()) {
            return "error";
        }
        utilisateurManagement.inscription(utilisateur);
        model.addAttribute("utilisateur", utilisateur);
        return "result";
    }
}
