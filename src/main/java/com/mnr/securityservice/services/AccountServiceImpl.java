package com.mnr.securityservice.services;



import com.mnr.securityservice.entities.AppRole;
import com.mnr.securityservice.entities.AppUser;
import com.mnr.securityservice.repositories.AppRoleRepository;
import com.mnr.securityservice.repositories.AppUserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional

//autowirer les injections via le constructor
//@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {


    private AppUserRepository appUserRepository;
    private AppRoleRepository appRoleRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public AccountServiceImpl(AppUserRepository appUserRepository, AppRoleRepository appRoleRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.appUserRepository = appUserRepository;
        this.appRoleRepository = appRoleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public AppUser saveUser(String username, String password, String confirmedPassword) {
        //add user
        AppUser user= appUserRepository.findByUsername(username);
        if(user!=null) throw new  RuntimeException("User already exist");
        if(!password.equals(confirmedPassword)) throw new RuntimeException("Please confirme your password");

        //creer user s'il n existe pas
        AppUser appUser= new AppUser();
        appUser.setUsername(username);
        appUser.setActived(true);
        appUser.setPassword(bCryptPasswordEncoder.encode(password));
        appUserRepository.save(appUser);

        //donner un role by default a l'user
        addRoleToUser(username,"USER");
        return appUser;
    }

    @Override
    public AppRole save(AppRole role) {
        return appRoleRepository.save(role);
    }

    @Override
    public AppUser loadUserByUsername(String username) {
        return appUserRepository.findByUsername(username);
    }

    @Override
    public void addRoleToUser(String username, String rolename) {
        AppUser appUser= appUserRepository.findByUsername(username);
        AppRole appRole= appRoleRepository.findByRoleName(rolename);

        appUser.getRoles().add(appRole);

    }
}
