package pl.rentathing.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.rentathing.auth.dto.RegisterRequest;

@Service
@RequiredArgsConstructor
public class AuthService {

    @Transactional
    public void register(RegisterRequest request){
        // TODO: Implement user.existByEmail()
        // TODO: implement user.save()
    }
}
