package authentication.controller;

import authentication.config.JwtTokenUtil;
import authentication.model.*;
import authentication.service.JWTUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class CustomerController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private JWTUserDetailsService userDetailsService;

    @Autowired
    private BankAccountRepository repository;


    public CustomerController(BankAccountRepository repository, AuthenticationManager authenticationManager, JWTUserDetailsService userDetailsService, JwtTokenUtil jwtTokenUtil) {
        this.repository = repository;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @RequestMapping(value="/createAccount",method= RequestMethod.POST,produces="application/json")
    public ResponseEntity<BankAccount> createBankAccount(@RequestBody BankAccount inputBankAccount){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(inputBankAccount.getPassword());
        BankAccount bankAccount = new BankAccount(inputBankAccount.getAccountNumber(), inputBankAccount.getUsername(), hashedPassword);
        ResponseEntity<Account> account = getAccount(String.valueOf(bankAccount.getAccountNumber()));

        if(bankAccount.isLocked()) {
            return new ResponseEntity<BankAccount>(new BankAccount(), HttpStatus.LOCKED);
        }

        if(account.getBody().getIban() == null) {
            if(inputBankAccount.getPassword().length() < 6) {
                return new ResponseEntity<BankAccount>(new BankAccount(), HttpStatus.NOT_ACCEPTABLE);
            }
            repository.save(bankAccount);
            return new ResponseEntity<BankAccount>(bankAccount, HttpStatus.OK);
        } else {
            if(account.getBody().getOwnerId().equals(inputBankAccount.getUsername())) {
                return new ResponseEntity<BankAccount>(new BankAccount(), HttpStatus.NOT_ACCEPTABLE);
            } else if(inputBankAccount.getPassword().length() < 6) {
                return new ResponseEntity<BankAccount>(new BankAccount(), HttpStatus.NOT_ACCEPTABLE);
            }
            repository.save(bankAccount);
            return new ResponseEntity<BankAccount>(bankAccount, HttpStatus.OK);
        }
    }

    @RequestMapping(value="/accounts/{account}",method= RequestMethod.GET,produces="application/json")
    public ResponseEntity<Account> getAccount(@PathVariable("account") String account){
        Optional<BankAccount> bankAccount = repository
                .findById(Long.valueOf(account));
        BankAccount bankAccount1 = new BankAccount();
        if(bankAccount.isPresent()) {
            bankAccount1 = bankAccount.get();
            Account acc = new Account(bankAccount1.getIban(), bankAccount1.getOwnerId());
            if(bankAccount1.isLocked()) {
                return new ResponseEntity<Account>(acc, HttpStatus.LOCKED);
            }
            if(bankAccount1.isLocked()) {
                return new ResponseEntity<Account>(acc, HttpStatus.LOCKED);
            } else {
                return new ResponseEntity<Account>(acc, HttpStatus.OK);
            }
        }
        return new ResponseEntity<Account>(new Account(), HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value="/authenticate",method= RequestMethod.POST,produces="application/json")
    public ResponseEntity<JwtResponse> authenticate(@RequestBody UserAuthentication userAuthentication) throws Exception {
        Long accountNumber = getAccountNumber(userAuthentication.getUsername());
        Optional<BankAccount> bankAccount = repository
                .findById(Long.valueOf(accountNumber));
        BankAccount bankAccount1 = bankAccount.get();
        if(bankAccount1.isLocked()) {
            return new ResponseEntity<JwtResponse>(new JwtResponse(""), HttpStatus.LOCKED);
        }
        authenticate(accountNumber.toString(), userAuthentication.getPassword(), bankAccount1);
        final UserDetails userDetails = userDetailsService.loadUserByUsername(accountNumber.toString());
        final String token = jwtTokenUtil.generateToken(userDetails);
        return ResponseEntity.ok(new JwtResponse(token));
    }

    private Long getAccountNumber(String username) {
        Long accountNumber = Long.valueOf(0);
        Iterable<BankAccount> all = repository.findAll();
        for (BankAccount ba: all) {
            if(ba.getUsername().equals(username)) {
                accountNumber = ba.getAccountNumber();
            }
        }
        return accountNumber;
    }

    private void authenticate(String username, String password,BankAccount bankAccount) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            bankAccount.setWrongAttempts(0);
        } catch (DisabledException e) {
            incrementWrongAttempts(bankAccount);
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            incrementWrongAttempts(bankAccount);
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }

    private void incrementWrongAttempts(BankAccount bankAccount) {
        bankAccount.setWrongAttempts(bankAccount.getWrongAttempts() + 1);
        if(bankAccount.getWrongAttempts() > 2) {
            bankAccount.setLocked(true);
        }
        repository.save(bankAccount);
    }
}
