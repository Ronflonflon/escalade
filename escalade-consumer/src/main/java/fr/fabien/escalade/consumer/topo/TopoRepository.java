package fr.fabien.escalade.consumer.topo;

import fr.fabien.escalade.model.topo.Topo;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TopoRepository extends CrudRepository<Topo, Long> {
}
