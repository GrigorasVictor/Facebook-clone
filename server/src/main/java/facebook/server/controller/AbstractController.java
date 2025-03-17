package facebook.server.controller;

import facebook.server.repository.AbstractRepository;
import facebook.server.service.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

public abstract class AbstractController<T, S extends AbstractService<T, ? extends AbstractRepository<T>>> {

    @Autowired
    S service; // merge chiar daca arata eroare

    @PostMapping
    public ResponseEntity<T> add(@RequestBody T newEntry) {
        System.out.println(newEntry);
        service.getRepository().save(newEntry);
        return new ResponseEntity<>(newEntry, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<T> delete(@PathVariable Long id){
        service.getRepository().deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<T>> getAll(){
        return new ResponseEntity<>(service.getRepository().findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<T>> get(@PathVariable Long id){
        return new ResponseEntity<>(service.getRepository().findById(id), HttpStatus.OK);
    }
}