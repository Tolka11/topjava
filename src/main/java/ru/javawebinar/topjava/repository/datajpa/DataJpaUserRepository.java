package ru.javawebinar.topjava.repository.datajpa;

import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;

import java.util.List;

@Repository
@Profile("datajpa")
public class DataJpaUserRepository implements UserRepository {
    private static final Sort SORT_NAME_EMAIL = Sort.by(Sort.Direction.ASC, "name", "email");

    private final CrudUserRepository crudUserRepository;
    private final CrudMealRepository crudMealRepository;

    public DataJpaUserRepository(CrudUserRepository crudUserRepository, CrudMealRepository crudMealRepository) {
        this.crudUserRepository = crudUserRepository;
        this.crudMealRepository = crudMealRepository;
    }

    @Override
    public User save(User user) {
        return crudUserRepository.save(user);
    }

    @Override
    public boolean delete(int id) {
        return crudUserRepository.delete(id) != 0;
    }

    @Override
    public User get(int id) {
        return crudUserRepository.findById(id).orElse(null);
    }

    @Override
    public User getByEmail(String email) {
        return crudUserRepository.getByEmail(email);
    }

    @Override
    public List<User> getAll() {
        return crudUserRepository.findAll(SORT_NAME_EMAIL);
    }

    @Override
    public User getWithMeals(int id) {
        User user = crudUserRepository.findById(id).orElse(null);
        if (user != null) {
            user.setMeals(crudMealRepository.findAllByUserIdOrderByDateTimeDesc(id));
        }
        return user;
    }
}
