package fr.fabien.escalade.business;

import fr.fabien.escalade.consumer.commentaire.CommentaireRepository;
import fr.fabien.escalade.model.topo.Commentaire;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class CommentaireManagement extends CrudManager<Commentaire, CommentaireRepository> {

    public CommentaireManagement(CommentaireRepository repository) {
        super(repository);
    }

    public Optional<Commentaire> findById(Long id) {
        return repository.findById(id);
    }

    public Iterable<Commentaire> findAll() {
        return repository.findAll();
    }

    public List<Commentaire> findCommentairesByUtilisateur_id(Long id) {
        return repository.findCommentairesByUtilisateur_id(id);
    }

    public List<Commentaire> findCommentairesBySiteId(Long id) {
        return repository.findCommentairesBySiteId(id);
    }

    public List<Commentaire> findCommentairesByTopoId(Long id) {
        return repository.findCommentairesByTopoId(id);
    }

    public void deleteCommentairesByTopoId (Long id) {
        repository.deleteCommentairesByTopoId(id);
    }

    public void deleteCommentairesBySiteId (Long id) {
        repository.deleteCommentairesBySiteId(id);
    }

}
