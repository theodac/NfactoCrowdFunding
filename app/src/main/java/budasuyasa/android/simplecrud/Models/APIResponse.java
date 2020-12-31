package budasuyasa.android.simplecrud.Models;

/**
 * Class untuk memetakan response API
 */
public class APIResponse {
    String status;
    String message;
    User data;


    public User getUser() {
        return data;
    }

    public void setUser(User user) {
        this.data = user;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
