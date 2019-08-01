import authentication.controller.CustomerController;
import authentication.model.Account;
import authentication.model.BankAccount;
import authentication.model.BankAccountRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class CustomerControllerTest {
    @Mock
    private BankAccountRepository repository;


    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetAccountForExistingAccount() {
        CustomerController customerController = new CustomerController(repository);
        BankAccount bankAccount = Mockito.mock(BankAccount.class);
        bankAccount.setAccountNumber(Long.valueOf("77853449"));
        bankAccount.setPassword("123456");
        bankAccount.setUsername("username");

        Optional<BankAccount> bankAccount1 = Optional.of(bankAccount);

        when(repository.findById(Long.valueOf("77853449"))).thenReturn(bankAccount1);
        ResponseEntity<Account> account = customerController.getAccount("77853449");


        assertEquals("", account.getBody().getOwnerId());
        assertEquals("", account.getBody().getIban());
    }

    @Test(expected = Exception.class)
    public void testGetAccountForNonExistingAccount() {
        CustomerController customerController = new CustomerController(repository);
        ResponseEntity<Account> account = customerController.getAccount("77853449");
    }
}
