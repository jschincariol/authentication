package authentication.model;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class BankAccount {
    @Id
    private Long accountNumber;
    private String username;
    private String password;
    private String iban;
    private String ownerId;

    public BankAccount() {
    }

    public BankAccount(Long accountNumber, String username, String password) {
        this.accountNumber = accountNumber;
        this.username = username;
        this.password = password;
        this.iban = "NL24INGB" + accountNumber.toString() + "09";
        this.ownerId = username;
    }

    public Long getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(Long accountNumber) {
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

    public String getIban() {
        return iban;
    }

    public String getOwnerId() {
        return ownerId;
    }

}
