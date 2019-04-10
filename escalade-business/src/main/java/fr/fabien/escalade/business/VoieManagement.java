package fr.fabien.escalade.business;

import fr.fabien.escalade.consumer.topo.VoieRepository;
import fr.fabien.escalade.model.topo.Voie;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class VoieManagement {
    @Autowired private final VoieRepository repository;

    public Iterable<Voie> findAll() {
        return repository.findAll();
    }

    public void ajout (Voie voie) {
        Voie testVoie = repository.findFirstByNom(
                voie.getNom()
        );

        if (testVoie != null) {
            System.out.println("Erreur - Ce site existe déjà, id = " + testVoie.getId());
        } else {
            repository.save(voie);
        }
    }
    public Voie findVoieById (Long id) {
        return repository.findVoieById(id);
    }
}
