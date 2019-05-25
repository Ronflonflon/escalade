package fr.fabien.escalade.webapp.controller;

import fr.fabien.escalade.business.*;
import fr.fabien.escalade.model.topo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static fr.fabien.escalade.business.Cotations.cotations;
import static fr.fabien.escalade.business.Departements.departements;

@Controller
@RequiredArgsConstructor
public class TopoController {
    @Autowired
    TopoManagement topoManagement;
    @Autowired
    SiteManagement siteManagement;
    @Autowired
    SecteurManagement secteurManagement;
    @Autowired
    VoieManagement voieManagement;
    @Autowired
    UtilisateurManagement utilisateurManagement;
    @Autowired
    CommentaireManagement commentaireManagement;
    @Autowired
    ReservationManagement reservationManagement;
    @Autowired
    ValidationModel validationModel = new ValidationModel();


    @GetMapping("/topo")
    public String topo(Model model, HttpServletRequest request, @RequestParam(value = "errors", required = false) List<String> errors) {
        Utilisateur utilisateur = utilisateurManagement.findByRequest(request);
        Topo topo = new Topo();
        topo.setUtilisateur(utilisateur);
        model.addAttribute("user", utilisateur);
        model.addAttribute("topo", topo);
        model.addAttribute("errors", errors);
        return "topo_creation";
    }

    @PostMapping("/topo/save")
    public String creation_topo(Topo topo, Model model, HttpServletRequest request, RedirectAttributes ra) {
        List<String> errors = validationModel.verifyValidity(topo);

        if (errors.size() == 0) {
            Utilisateur utilisateur = utilisateurManagement.findByRequest(request);
            topoManagement.save(topo);
            model.addAttribute("utilisateur", utilisateur);
            model.addAttribute("topo", topo);
            model.addAttribute("object_type", "topo");
            return "redirect:/profile";
        }
        ra.addAttribute("errors", errors);
        return "redirect:/topo";
    }

    @GetMapping("/topo/{id}")
    public String listMesTopos(Model model, HttpServletRequest request, @PathVariable String id,
                               @RequestParam(value = "errors", required = false) List<String> errors) {
        Long long_id = Long.parseLong(id);
        if (topoManagement.findById(long_id).isPresent()) {
            Topo topo = topoManagement.findById(long_id).get();
            Commentaire commentaire = new Commentaire();
            List<Site> sites = siteManagement.findAllByTopos_Id(topo.getId());
            commentaire.setUtilisateur(utilisateurManagement.findByRequest(request));
            commentaire.setTopo(topo);

            if (reservationManagement.actualyReserved(topo).size() == 0 ||
                    reservationManagement.isReservedByUser(utilisateurManagement.findByRequest(request))) {
                model.addAttribute("topo", topo);
            }
            if (utilisateurManagement.findByRequest(request) != null) {
                if ((topo.getUtilisateur().getId().equals(utilisateurManagement.findByRequest(request).getId()))) {
                    model.addAttribute("topo", topo);
                }
            }
            model.addAttribute("commentaire_write", commentaire);
            model.addAttribute("commentaires", commentaireManagement.findCommentairesByTopoId(long_id));
            model.addAttribute("redirectionId", id);
            model.addAttribute("dates", new Dates().getThisWeek());
            model.addAttribute("sites", sites);
            return "topo_show";
        } else {
            return "erreur";
        }
    }

    @GetMapping("/topo/{id}/edit")
    public String topo_edit(Model model, @PathVariable String id) {
        Optional<Topo> topo = topoManagement.findById(Long.parseLong(id));
        if (topo.isPresent()) {
            model.addAttribute(topo.get());
            model.addAttribute("departements", departements);
            return "topo_creation";
        }
        return "erreur";
    }

    @PostMapping("/topo/{id}/reservation")
    public String reservation(@PathVariable String id, @RequestParam(value = "dateFin") String dateFin,
                              RedirectAttributes ra, HttpServletRequest request) throws ParseException {
        List<String> errors = new ArrayList<>();
        DateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
        Date dateConv = format.parse(dateFin);
        Reservation reservation = new Reservation();
        reservation.setDateDebut(new Date());
        reservation.setDateFin(dateConv);
        reservation.setUtilisateur(utilisateurManagement.findByRequest(request));
        Long long_id = Long.parseLong(id);
        reservation.setTopo(topoManagement.findById(long_id).get());
        System.out.println("Libre ? " + reservationManagement.isFree(reservation));
        if (reservationManagement.isFree(reservation)) {
            reservationManagement.save(reservation);
        } else {
            ra.addAttribute("errors", errors);
        }

        return "redirect:/topo/{id}";
    }

    @GetMapping("/topo/{id}/delete")
    public String topo_delete(@PathVariable String id, HttpServletRequest request) {
        Long long_id = Long.parseLong(id);
        Utilisateur utilisateur = utilisateurManagement.findByRequest(request);
        Optional<Topo> topo = topoManagement.findById(long_id);
        if (topo.isPresent()) {
            if (utilisateur.getId().equals(topo.get().getUtilisateur().getId())) {
                if (topo.get().getCommentaires() != null)
                    commentaireManagement.deleteCommentairesByTopoId(topo.get().getId());
                topoManagement.deleteById(long_id);
            }
        }
        return "redirect:/profile";
    }

    @PostMapping("/topo/{id}/link")
    public String topoLinkSite(@PathVariable String id, @RequestParam("stringSite") String stringSite, HttpServletRequest request) {
        Site site = siteManagement.findSiteByNom(stringSite);
        Optional<Topo> topo = topoManagement.findById(Long.parseLong(id));

        if (topo.isPresent() && site != null) {
            if (!topoManagement.isAlreadyLinkWithTopo(topo.get(), site)) {
                List<Topo> topos = site.getTopos();
                topos.add(topo.get());
                site.setTopos(topos);
                siteManagement.save(site);

                List<Site> sites = topo.get().getSites();
                sites.add(site);
                topo.get().setSites(sites);
                topoManagement.save(topo.get());
            }
        }
        return "redirect:/topo/{id}";
    }

    @GetMapping("/site")
    public String site(Model model, HttpServletRequest request, @RequestParam(value = "errors", required = false) List<String> errors) {
        Site site = new Site();
        site.setCotationMin(0);
        site.setCotationMax(0);
        site.setHauteurMin(0);
        site.setHauteurMax(0);
        site.setDate(new Date(System.currentTimeMillis()));
        Utilisateur utilisateur = utilisateurManagement.findByRequest(request);
        site.setUtilisateur(utilisateur);
        model.addAttribute("utilisateur", utilisateur);
        model.addAttribute("site", site);
        model.addAttribute("errors", errors);
        model.addAttribute("departements", departements);
        return "site_creation";
    }

    @PostMapping("/site/save")
    public String creation_site(@ModelAttribute Site site, Model model, HttpServletRequest request, RedirectAttributes ra) {
        Utilisateur utilisateur = utilisateurManagement.findByRequest(request);
        List<String> errors = validationModel.verifyValidity(site);

        errors.addAll(siteManagement.validDepartement(site));

        if (errors.size() == 0) {
            site.setUtilisateur(utilisateur);
            siteManagement.save(site);
            String object_type = "site";
            model.addAttribute("utilisateur", utilisateur);
            model.addAttribute("site", site);
            model.addAttribute("object_type", object_type);
            return "redirect:/profile";
        }
        ra.addAttribute("errors", errors);
        return "redirect:/site";
    }

    @GetMapping("/site/{id}")
    public String listMesSites(Model model,
                               HttpServletRequest request, @PathVariable String id) {
        Long long_id = Long.parseLong(id);
        Site site = siteManagement.findById(long_id).get();
        Commentaire commentaire = new Commentaire();
        commentaire.setUtilisateur(utilisateurManagement.findByRequest(request));
        commentaire.setSite(site);

        model.addAttribute("site", site);
        model.addAttribute("commentaire_write", commentaire);
        model.addAttribute("utilisateur_show", site.getUtilisateur());
        model.addAttribute("commentaires", commentaireManagement.findCommentairesBySiteId(long_id));
        model.addAttribute("redirectionId", id);
        model.addAttribute("cotations", new Cotations());
        return "site_show";
    }

    @GetMapping("/site/{id}/edit")
    public String site_edit(Model model, @PathVariable String id) {
        Optional<Site> site = siteManagement.findById(Long.parseLong(id));
        if (site.isPresent()) {
            model.addAttribute(site.get());
            return "site_creation";
        }
        return "erreur";
    }

    @GetMapping("/site/{id}/delete")
    public String site_delete(@PathVariable String id, HttpServletRequest request) {
        Long long_id = Long.parseLong(id);
        Utilisateur utilisateur = utilisateurManagement.findByRequest(request);
        Optional<Site> site = siteManagement.findById(long_id);
        if (site.isPresent()) {
            if (site.get().getCommentaires() != null)
                commentaireManagement.deleteCommentairesBySiteId(site.get().getId());
            siteManagement.deleteById(long_id);
        }
        return "redirect:/profile";
    }

    @GetMapping("/site/{id}/reservation")
    public String site_reservation(@PathVariable String id, HttpServletRequest request) {

        return "redirect:/site/{id}";
    }

    @GetMapping("/site/{id}/secteur")
    public String creation_secteur(Model model, @PathVariable String id, @RequestParam(value = "errors", required = false) List<String> errors) {
        Secteur secteur = new Secteur();
        secteur.setDate(new Date(System.currentTimeMillis()));
        secteur.setSite(siteManagement.findById(Long.parseLong(id)).get());
        model.addAttribute("secteur", secteur);
        model.addAttribute("errors", errors);
        return "secteur_creation";
    }

    @GetMapping("/secteur/{id}")
    public String secteur(Model model, @PathVariable String id) {
        long long_id = Long.parseLong(id);
        model.addAttribute("secteur", secteurManagement.findSecteurById(long_id));
        model.addAttribute("cotations", new Cotations());
        return "secteur_show";
    }

    @PostMapping("/secteur/save")
    public String creation_secteur(@ModelAttribute Secteur secteur, Model model, HttpServletRequest request, RedirectAttributes ra) {
        Utilisateur utilisateur = utilisateurManagement.findByRequest(request);
        List<String> errors = validationModel.verifyValidity(secteur);

        if (errors.size() == 0) {
            Date date = new Date(System.currentTimeMillis());
            secteur.setDate(date);
            secteurManagement.save(secteur);

            String object_type = "secteur";
            model.addAttribute("utilisateur", utilisateur);
            model.addAttribute("secteur", secteur);
            model.addAttribute("object_type", object_type);
            return "redirect:/site/" + secteur.getSite().getId();
        }
        ra.addAttribute("errors", errors);
        return "redirect:/site/" + secteur.getSite().getId() + "/secteur";

    }

    @GetMapping("/secteur/{id}/edit")
    public String secteur_edit(Model model, @PathVariable String id) {
        Optional<Secteur> secteur = secteurManagement.findById(Long.parseLong(id));
        if (secteur.isPresent()) {
            model.addAttribute(secteur.get());
            return "secteur_creation";
        }
        return "erreur";
    }

    @GetMapping("/secteur/{id}/delete")
    public String secteur_delete(@PathVariable String id, HttpServletRequest request) {
        Long long_id = Long.parseLong(id);
        Utilisateur utilisateur = utilisateurManagement.findByRequest(request);
        Optional<Secteur> secteur = secteurManagement.findById(long_id);
        if (secteur.isPresent()) {
            if (utilisateur.getId().equals(secteur.get().getSite().getUtilisateur().getId())) {
                secteurManagement.deleteById(long_id);
            }
            return "redirect:/site/" + secteur.get().getSite().getId();
        }
        return "redirect:/erreur";
    }

    @GetMapping("/secteur/{id}/voie")
    public String creation_voie(Model model, @PathVariable String id, @RequestParam(value = "errors", required = false) List<String> errors) {
        Voie voie = new Voie();
        voie.setDate(new Date(System.currentTimeMillis()));
        voie.setSecteur(secteurManagement.findSecteurById(Long.parseLong(id)));
        model.addAttribute("voie", voie);
        model.addAttribute("cotations", cotations);
        model.addAttribute("errors", errors);
        return "voie_creation";
    }

    @GetMapping("/voie/{id}")
    public String voie(Model model, HttpServletRequest request, HttpSession session, @PathVariable String id) {
        long long_id = Long.parseLong(id);
        session.setAttribute("user", utilisateurManagement.findByRequest(request));
        model.addAttribute("voie", voieManagement.findVoieById(long_id));
        model.addAttribute("cotations", new Cotations());
        return "voie_show";
    }

    @PostMapping("/voie/save")
    public String creation_voie(@ModelAttribute Voie voie, Model model, HttpServletRequest request, RedirectAttributes ra) {
        List<String> errors = validationModel.verifyValidity(voie);
        String redirect = "redirect:/secteur/" + voie.getSecteur().getId() + "/voie";

        if (errors.size() == 0) {
            Utilisateur utilisateur = utilisateurManagement.findByRequest(request);
            Date date = new Date(System.currentTimeMillis());
            voie.setDate(date);
            voieManagement.save(voie);

            String object_type = "voie";
            model.addAttribute("utilisateur", utilisateur);
            model.addAttribute("voie", voie);
            model.addAttribute("object_type", object_type);
            return "redirect:/profile";
        }
        ra.addAttribute("errors", errors);
        return redirect;
    }

    @GetMapping("/voie/{id}/edit")
    public String voie_edit(Model model, @PathVariable String id) {
        Optional<Voie> voie = voieManagement.findById(Long.parseLong(id));
        if (voie.isPresent()) {
            model.addAttribute(voie.get());
            return "voie_creation";
        }
        return "erreur";
    }

    @GetMapping("/voie/{id}/delete")
    public String voie_delete(@PathVariable String id) {
        Long long_id = Long.parseLong(id);
        Optional<Voie> voie = voieManagement.findById(long_id);
        if (voie.isPresent()) {
            voieManagement.deleteById(long_id);
        }
        return "redirect:/profile";
    }

    @PostMapping("/comment/{id}")
    public String comment_add(@ModelAttribute Commentaire commentaire, @PathVariable String id) {
        commentaire.setDate(new Date(System.currentTimeMillis()));
        commentaireManagement.save(commentaire);

        System.out.println(commentaire.getUtilisateur());

        String redirection = "redirect:/";
        if (commentaire.getSite() != null) {
            redirection += "site";
        } else if (commentaire.getTopo() != null) {
            redirection += "topo";
        } else {
            redirection += "error";
        }
        redirection += "/";
        redirection += id;
        return redirection;
    }

    @PostMapping("/comment/{id}/delete")
    public String comment_delete(@PathVariable String id, HttpServletRequest request) {
        Long long_id = Long.parseLong(id);
        String id_redirect = "";
        String redirection = "redirect:/";
        Commentaire commentaire = commentaireManagement.findById(long_id).get();
        if (commentaire.getSite() != null) {
            redirection += "site";
            id_redirect = Long.toString(commentaire.getSite().getId());
        } else if (commentaire.getTopo() != null) {
            redirection += "topo";
            id_redirect = Long.toString(commentaire.getTopo().getId());
        } else {
            redirection += "error";
        }
        redirection += "/";
        redirection += id_redirect;

        Utilisateur utilisateur = utilisateurManagement.findByRequest(request);
        if (commentaire.getUtilisateur().getId().equals(utilisateur.getId()))
            commentaireManagement.deleteById(long_id);
        return redirection;
    }
}
