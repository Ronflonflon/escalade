package fr.fabien.escalade.business;

import fr.fabien.escalade.consumer.topo.SecteurRepository;
import fr.fabien.escalade.model.topo.Secteur;
import fr.fabien.escalade.model.topo.Site;
import fr.fabien.escalade.model.topo.Voie;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class SecteurManagement extends CrudManager<Secteur, SecteurRepository> {
    @Autowired
    SiteManagement siteManagement;
    @Autowired
    SecteurManagement secteurManagement;

    public SecteurManagement(SecteurRepository repository) {
        super(repository);
    }

    public List<Secteur> findSecteursBySite_id(Long id) {
        return repository.findSecteursBySite_id(id);
    }

    public Secteur findSecteurById(Long id) {
        return repository.findSecteurById(id);
    }

    public void updateMinMax(Secteur secteur) {
        Integer minValue = 0;
        Integer maxValue = 0;

        for (int i = 0; i < secteur.getVoies().size(); i++) {
            if (i == 0) {
                if (secteur.getVoies().get(i).getLongueur() != null) {
                    minValue = secteur.getVoies().get(i).getLongueur();
                    maxValue = secteur.getVoies().get(i).getLongueur();
                }
            } else {
                if (secteur.getVoies().get(i).getLongueur() < minValue) {
                    minValue = secteur.getVoies().get(i).getLongueur();
                }
                if (secteur.getVoies().get(i).getLongueur() > maxValue) {
                    maxValue = secteur.getVoies().get(i).getLongueur();
                }
            }
        }
        secteur.setHauteurMin(minValue);
        secteur.setHauteurMax(maxValue);

        minValue = 0;
        maxValue = 0;

        for (
                int i = 0; i < secteur.getVoies().

                size();

                i++) {
            if (i == 0) {
                if (secteur.getVoies().get(i).getCotation() != null) {
                    minValue = secteur.getVoies().get(i).getCotation();
                    maxValue = secteur.getVoies().get(i).getCotation();
                }
            } else {
                if (secteur.getVoies().get(i).getCotation() < minValue) {
                    minValue = secteur.getVoies().get(i).getCotation();
                }
                if (secteur.getVoies().get(i).getCotation() > maxValue) {
                    maxValue = secteur.getVoies().get(i).getCotation();
                }

            }
        }
        secteur.setCotationMin(minValue);
        secteur.setCotationMax(maxValue);

        Site site = secteur.getSite();
        site.getSecteurs().

                add(secteur);
        siteManagement.save(site);
        siteManagement.updateMinMax(site);
    }

    @Override
    public void deleteById(Long id) {
        Optional<Secteur> secteur = secteurManagement.findById(id);
        if (secteur.isPresent()) {
            Site site = secteur.get().getSite();
            site.getSecteurs().remove(secteur.get());
            siteManagement.save(site);
            siteManagement.updateMinMax(site);
            repository.delete(secteur.get());
        }
    }
}
