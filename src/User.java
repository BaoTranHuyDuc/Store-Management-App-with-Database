import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("ID")
    private int userID;
    
    @SerializedName("UserName")
    private String username;

    @SerializedName("Password")
    private String password;

    @SerializedName("DisplayName")
    private String fullName;

    @SerializedName("Role")
    private String role;

    @SerializedName("AccountNumber")
    private String accountNumber;

    @SerializedName("Bank")
    private String bank;

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }
}
