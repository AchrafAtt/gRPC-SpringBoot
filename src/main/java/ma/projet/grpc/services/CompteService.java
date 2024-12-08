package ma.projet.grpc.services;

import ma.projet.grpc.repositorires.CompteRepository;
import ma.projet.grpc.entities.Compte;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class CompteService {
    private final CompteRepository compteRepository;


    public CompteService(CompteRepository compteRepository) {
        this.compteRepository = compteRepository;
    }
//
//    public List<Compte> findAllCopmtes() {
//        return compteRepository.findAll();
//    }
    public Compte saveCompte( Compte compte) {

        return compteRepository.save(compte);
    }



//    public Compte findCompteById(String id) {
//        return compteRepository.findById(id).orElse(null);
//    }

    //converte the grpc message to entity

}
