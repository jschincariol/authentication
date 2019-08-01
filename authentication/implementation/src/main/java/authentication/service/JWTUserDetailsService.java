package authentication.service;

import java.util.ArrayList;
import java.util.Optional;

import authentication.model.BankAccount;
import authentication.model.BankAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
@Service
public class JWTUserDetailsService implements UserDetailsService {
    @Autowired
    private BankAccountRepository repository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<BankAccount> bankAccount = repository
                .findById(Long.valueOf(username));
        BankAccount bankAccount1 = new BankAccount();
        if(bankAccount.isPresent()) {
            bankAccount1 = bankAccount.get();
        }

        if (bankAccount1.getUsername().toString().equals(bankAccount1.getUsername())) {
            User user = new User(bankAccount1.getUsername(), bankAccount1.getPassword(), new ArrayList<>());
            UserDetails userDetails = user;
            return userDetails;
        } else {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
    }
}