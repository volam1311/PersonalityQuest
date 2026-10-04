package com.example.personalityquest.DAO.auth;
import com.example.personalityquest.Model.auth.User;

/** Defines persistence operations for user records */
public interface IUserDAO {
    /** Creates a user record
     * @param user the user to store
     */
    public void createUser(User user);
    /** Updates a stored user record
     * @param user the user with updated details
     */
    public void updateUser(User user);
    /** Deletes a stored user record
     * @param user the user to delete
     */
    public void deleteUser(User user);
    /** Retrieves a user by ID
     * @param id the user ID to find
     * @return the matching user, or null if no user is found
     */
    public User getUser(int id);
}
