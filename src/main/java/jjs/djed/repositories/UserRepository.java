package jjs.djed.repositories;

import jjs.djed.model.User;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

import static jjs.djed.jooq.Tables.USERS;

@Repository
public class UserRepository {

    private final DSLContext dsl;

    public UserRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    public User insert(User user) {
        return dsl.insertInto(USERS)
                .set(USERS.USER_ID, user.getUserId())
                .set(USERS.DATE_CREATED, user.getDateCreated())
                .set(USERS.NAME, user.getUsername())
                .returning()
                .fetchOne(record -> new User(
                        record.getUserId(),
                        record.getName(),
                        record.getDateCreated(),
                        record.getDisplayId()
                ));
    }

    public User update(User user) {
        return dsl.update(USERS)
                .set(USERS.NAME, user.getUsername())
                .where(USERS.USER_ID.eq(user.getUserId()))
                .returning()
                .fetchOne(record -> new User(
                        record.getUserId(),
                        record.getName(),
                        record.getDateCreated(),
                        record.getDisplayId()
                ));
    }

    public Optional<User> findById(UUID id) {
        return dsl.selectFrom(USERS)
                .where(USERS.USER_ID.eq(id))
                .fetchOptional()
                .map(record -> new User(
                        record.getUserId(),
                        record.getName(),
                        record.getDateCreated(),
                        record.getDisplayId()
                ));
    }
}
