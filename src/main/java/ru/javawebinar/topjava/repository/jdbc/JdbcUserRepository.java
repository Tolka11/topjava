package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Transactional(readOnly = true)
public class JdbcUserRepository implements UserRepository {

    private final JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final SimpleJdbcInsert insertUser;

    private final UserExtractor userExtractor;

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();


    @Autowired
    public JdbcUserRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.insertUser = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");

        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.userExtractor = new UserExtractor();
    }

    @Override
    @Transactional
    public User save(User user) {
        BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(user);

        userValidation(List.of(user));

        if (user.isNew()) {
            Number newKey = insertUser.executeAndReturnKey(parameterSource);
            user.setId(newKey.intValue());
        } else if (namedParameterJdbcTemplate.update("""
                   UPDATE users SET name=:name, email=:email, password=:password, 
                   registered=:registered, enabled=:enabled, calories_per_day=:caloriesPerDay WHERE id=:id
                """, parameterSource) == 0) {
            return null;
        }
        jdbcTemplate.update("DELETE FROM user_roles WHERE user_id=?", user.getId());
//        for (Role role : user.getRoles()) {
//            jdbcTemplate.update("INSERT INTO user_roles (user_id, role) VALUES(?,?)", user.getId(), role.toString());
//        }
        List<Role> roles = new ArrayList<>(user.getRoles());
        jdbcTemplate.batchUpdate(
                "INSERT INTO user_roles (user_id, role) VALUES(?,?)",
                new BatchPreparedStatementSetter() {

                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setInt(1, user.getId());
                        ps.setString(2, roles.get(i).toString());
                    }

                    public int getBatchSize() {
                        return roles.size();
                    }

                });

        return user;
    }

    @Override
    @Transactional
    public boolean delete(int id) {
        int deletedRows = jdbcTemplate.update("DELETE FROM users WHERE id=?", id);
        if (deletedRows != 0) {
            jdbcTemplate.update("DELETE FROM user_roles WHERE user_id=?", id);
        }
        return deletedRows != 0;
    }

    @Override
    public User get(int id) {
        List<User> users = jdbcTemplate.query(
                "SELECT DISTINCT u.*, ur.role AS roles FROM users u LEFT JOIN user_roles ur ON u.id = ur.user_id WHERE id=?",
                userExtractor, id);
        if (users.size() != 0) {
            userValidation(users);
            return users.get(0);
        }
        return null;
    }

    @Override
    public User getByEmail(String email) {
        List<User> users = jdbcTemplate.query(
                "SELECT DISTINCT u.*, ur.role AS roles FROM users u LEFT JOIN user_roles ur ON u.id = ur.user_id WHERE email=?",
                userExtractor, email);
        if (users.size() != 0) {
            userValidation(users);
            return users.get(0);
        }
        return null;
    }

    @Override
    public List<User> getAll() {
        List<User> users = jdbcTemplate.query(
                "SELECT DISTINCT u.*, ur.role AS roles FROM users u LEFT JOIN user_roles ur ON u.id = ur.user_id",
                userExtractor);
        userValidation(users);
        return users;
    }

    private void userValidation(Collection<User> users) {
        Set<ConstraintViolation<User>> allUsersViolations = new HashSet<>();
        for (User user : users) {
            allUsersViolations.addAll(validator.validate(user));
        }
        if (allUsersViolations.size() != 0) {
            throw new ConstraintViolationException("Not valid User object. ", allUsersViolations);
        }
    }

    // My User extractor to list
    private class UserExtractor implements ResultSetExtractor<List<User>> {
        @Override
        public List<User> extractData(ResultSet rs)
                throws SQLException, DataAccessException {
            Map<Integer, User> userMap = new LinkedHashMap<>();
            while (rs.next()) {
                Integer id = rs.getInt("id");
                userMap.putIfAbsent(id, new User(id, rs.getString("name"), rs.getString("email"), rs.getString("password"),
                        rs.getInt("calories_per_day"), rs.getBoolean("enabled"), rs.getDate("registered"), new HashSet<>()));
                String roleName = rs.getString("roles");
                if (roleName != null) {
                    Role role = Role.valueOf(roleName);
                    userMap.get(id).getRoles().add(role);
                }
            }
            List<User> users = userMap.values().stream().collect(Collectors.toList());
            // Sort by id in descent order for stable test result
            return users.stream().sorted((u1, u2) -> u2.getId().compareTo(u1.getId())).collect(Collectors.toList());
        }
    }
}
