package com.example.personalityquest.DAO;
import com.example.personalityquest.Model.User;

public interface IUserDAO {
    public void createUser(User user);
    public void updateUser(User user);
    public void deleteUser(User user);
    public User getUser(int id);
}
