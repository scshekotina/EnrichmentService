package org.example.user;

public interface UserStorage {
    User get(String msidsn);
    void add(User user);
    User update (String msidsn, User user);
    void delete (User user);

}
