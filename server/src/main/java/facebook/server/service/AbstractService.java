package facebook.server.service;

import facebook.server.repository.AbstractRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;


@Getter
public abstract class AbstractService<T, R extends AbstractRepository<T>> {
    @Autowired
    protected R repository;
}
