package ml.raketeufo.thiunofficial.data.model;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser {

    private String userId;
    private String displayName;
    private String accessToken;

    public LoggedInUser(String userId, String displayName, String accessToken) {
        this.userId = userId;
        this.displayName = displayName;
        this.accessToken = accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getUserId() {
        return userId;
    }

    public String getDisplayName() {
        return displayName;
    }
}