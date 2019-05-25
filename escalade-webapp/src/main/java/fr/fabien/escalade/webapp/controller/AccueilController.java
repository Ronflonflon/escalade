package fr.fabien.escalade.webapp.controller;

import fr.fabien.escalade.business.ReservationManagement;
import fr.fabien.escalade.business.UtilisateurManagement;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static fr.fabien.escalade.business.Departements.departements;

@Controller
@RequiredArgsConstructor
public class AccueilController {
    @Autowired
    UtilisateurManagement utilisateurManagement;
    @Autowired
    ReservationManagement reservationManagement;

    @GetMapping("/")
    public String accueil(Model model, HttpServletRequest request, HttpSession session) {
        session.setAttribute("user", utilisateurManagement.findByRequest(request));
        model.addAttribute("departements", departements);
        return "accueil";
    }
}
