import authentication.config.JwtTokenUtil;
import authentication.controller.CustomerController;
import authentication.model.*;
import authentication.service.JWTUserDetailsService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.ArgumentMatchers;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class CustomerControllerTest {
    @Mock
    private BankAccountRepository repository;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JWTUserDetailsService userDetailsService;
    @Mock
    private JwtTokenUtil jwtTokenUtil;

    private final String USER_NAME = "c3629d83-95f7-4966-9b67-76b13fe2cd5a";
    private final String PASSWORD = "123456";
    private final Long ACCOUNT_NUMBER = 77853449L;
    CustomerController customerController;


    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        customerController = new CustomerController(repository, authenticationManager, userDetailsService, jwtTokenUtil);
    }

    @Test
    public void testCreateBankAccount() {
        BankAccount bankAccount = new BankAccount(ACCOUNT_NUMBER, USER_NAME, PASSWORD);
        ResponseEntity<BankAccount> bankAccount1 = customerController.createBankAccount(bankAccount);
        bankAccount1.getBody().getAccountNumber();
        assertEquals(bankAccount1.getBody().getAccountNumber(), bankAccount.getAccountNumber());
        assertEquals(bankAccount1.getBody().getIban(), bankAccount.getIban());
        assertEquals(bankAccount1.getBody().getOwnerId(), bankAccount.getOwnerId());
        assertEquals(bankAccount1.getBody().getWrongAttempts(), bankAccount.getWrongAttempts());
        assertEquals(bankAccount1.getBody().getUsername(), bankAccount.getUsername());
    }

    @Test
    public void testCreateBankAccountForExistingBankAccount() {
        BankAccount bankAccount = new BankAccount(ACCOUNT_NUMBER, USER_NAME, PASSWORD);
        Optional<BankAccount> bankAccount2 = Optional.of(bankAccount);
        Mockito.<Optional<BankAccount>>when(repository.findById(Long.valueOf(ACCOUNT_NUMBER.toString()))).thenReturn(bankAccount2);
        ResponseEntity<BankAccount> bankAccount1 = customerController.createBankAccount(bankAccount);
        assertEquals(HttpStatus.NOT_ACCEPTABLE, bankAccount1.getStatusCode());
    }

    @Test
    public void testAuthenticateForCorrectAuthentication() throws Exception {
        UserAuthentication userAuthentication = new UserAuthentication(USER_NAME, PASSWORD);
        BankAccount bankAccount = new BankAccount(ACCOUNT_NUMBER, USER_NAME, PASSWORD);
        Optional<BankAccount> bankAccount1 = Optional.of(bankAccount);
        List<BankAccount> bas = new ArrayList<>();
        bas.add(bankAccount);
        Iterable<BankAccount> iBankAccount = bas;

        when(repository.findAll()).thenReturn(iBankAccount);
        Mockito.<Optional<BankAccount>>when(repository.findById(Long.valueOf(ACCOUNT_NUMBER))).thenReturn(bankAccount1);
        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(userDetailsService.loadUserByUsername(ACCOUNT_NUMBER.toString())).thenReturn(null);
        when(jwtTokenUtil.generateToken(null)).thenReturn("123");
        ResponseEntity<JwtResponse> authenticate = customerController.authenticate(userAuthentication);

        assertNotNull("123", authenticate.getBody().getToken());
    }

    @Test
    public void testGetAccountForExistingAccount() {
        BankAccount bankAccount = new BankAccount(ACCOUNT_NUMBER, USER_NAME, PASSWORD);
        Optional<BankAccount> bankAccount1 = Optional.of(bankAccount);
        Mockito.<Optional<BankAccount>>when(repository.findById(Long.valueOf("77853449"))).thenReturn(bankAccount1);
        ResponseEntity<Account> account = customerController.getAccount("77853449");

        assertEquals(USER_NAME, account.getBody().getOwnerId());
        assertEquals("NL24INGB" + ACCOUNT_NUMBER + "09", account.getBody().getIban());
    }

    @Test
    public void testGetAccountForNonExistingAccount() {
        BankAccount bankAccount = new BankAccount(ACCOUNT_NUMBER, USER_NAME, PASSWORD);
        Optional<BankAccount> bankAccount1 = Optional.of(bankAccount);
        Mockito.<Optional<BankAccount>>when(repository.findById(Long.valueOf(ACCOUNT_NUMBER))).thenReturn(bankAccount1);
        ResponseEntity<Account> account = customerController.getAccount("123456789");

        assertEquals(HttpStatus.NO_CONTENT, account.getStatusCode());
    }
}
