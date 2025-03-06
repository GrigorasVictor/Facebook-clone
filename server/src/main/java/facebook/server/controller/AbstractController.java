package facebook.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

public abstract class AbstractController<T, R extends JpaRepository<T, Long>>{

    @Autowired
    R repository; // merge chiar daca arata eroare

    @PostMapping
    public ResponseEntity<T> add(@RequestBody T newEntry) {
        System.out.println(newEntry);
        repository.save(newEntry);
        return new ResponseEntity<>(newEntry, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<T> delete(@PathVariable Long id){
        repository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<T>> getAll(){
        return new ResponseEntity<>(repository.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<T>> get(@PathVariable Long id){
        return new ResponseEntity<>(repository.findById(id), HttpStatus.OK);
    }
}