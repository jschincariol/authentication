import authentication.controller.CustomerController;
import authentication.model.Account;
import authentication.model.BankAccount;
import authentication.model.BankAccountRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

public class CustomerControllerTest {
    @Mock
    private BankAccountRepository repository;
    private final String USER_NAME = "c3629d83-95f7-4966-9b67-76b13fe2cd5a";
    private final String PASSWORD = "123456";
    private final Long ACCOUNT_NUMBER = 77853449L;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateBankAccount() {

    }

    @Test
    public void testCreateBankAccountForExistingBankAccount() {

    }

    @Test
    public void testAuthenticateForCorrectAuthentication() {

    }

    @Test
    public void testAuthenticateForWrongAuthentication() {

    }

    @Test
    public void testGetAccountForExistingAccount() {
        CustomerController customerController = new CustomerController(repository);
        BankAccount bankAccount = new BankAccount(ACCOUNT_NUMBER, USER_NAME, PASSWORD);
        Optional<BankAccount> bankAccount1 = Optional.of(bankAccount);
        Mockito.<Optional<BankAccount>>when(repository.findById(Long.valueOf("77853449"))).thenReturn(bankAccount1);
        ResponseEntity<Account> account = customerController.getAccount("77853449");

        assertEquals(USER_NAME, account.getBody().getOwnerId());
        assertEquals("NL24INGB" + ACCOUNT_NUMBER + "09", account.getBody().getIban());
    }

    @Test
    public void testGetAccountForNonExistingAccount() {
        CustomerController customerController = new CustomerController(repository);
        BankAccount bankAccount = new BankAccount(ACCOUNT_NUMBER, USER_NAME, PASSWORD);
        Optional<BankAccount> bankAccount1 = Optional.of(bankAccount);
        Mockito.<Optional<BankAccount>>when(repository.findById(Long.valueOf(ACCOUNT_NUMBER))).thenReturn(bankAccount1);
        ResponseEntity<Account> account = customerController.getAccount("123456789");

        assertEquals(HttpStatus.NO_CONTENT, account.getStatusCode());
    }
}
