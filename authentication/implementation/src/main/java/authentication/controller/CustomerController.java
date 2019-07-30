package authentication.controller;

import authentication.config.JwtTokenUtil;
import authentication.model.BankAccount;
import authentication.model.JwtResponse;
import authentication.model.UserAuthentication;
import authentication.service.JWTUserDetailsService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private JWTUserDetailsService userDetailsService;

    @ApiOperation(value="get customer",response=BankAccount.class)
    @ApiResponses(value={
            @ApiResponse(code=200,message="Customer Details Retrieved",response=BankAccount.class),
            @ApiResponse(code=500,message="Internal Server Error"),
            @ApiResponse(code=404,message="Customer not found")
    })
    @RequestMapping(value="/createAccount",method= RequestMethod.POST,produces="application/json")
    public ResponseEntity<BankAccount> createBankAccount(@RequestBody BankAccount inputBankAccount){
        BankAccount cust = new BankAccount();
        cust.setAccountNumber(inputBankAccount.getAccountNumber());
        cust.setUsername(inputBankAccount.getUsername());
        cust.setPassword(inputBankAccount.getPassword());
        return new ResponseEntity<BankAccount>(cust, HttpStatus.OK);
    }

    @RequestMapping(value="/authenticate",method= RequestMethod.POST,produces="application/json")
    public ResponseEntity<JwtResponse> authenticate(@RequestBody UserAuthentication userAuthentication) throws Exception {
        authenticate(userAuthentication.getUsername(), userAuthentication.getPassword());
        final UserDetails userDetails = userDetailsService.loadUserByUsername(userAuthentication.getUsername());
        final String token = jwtTokenUtil.generateToken(userDetails);
        return ResponseEntity.ok(new JwtResponse(token));
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }
}
