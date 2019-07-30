package authentication.model;

public class BankAccount {
    private Integer accountNumber;
    private String username;
    private String password;

    public BankAccount() {
    }

    public BankAccount(Integer accountNumber, String username, String password) {
        this.accountNumber = accountNumber;
        this.username = username;
        this.password = password;
    }

    public Integer getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(Integer accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
