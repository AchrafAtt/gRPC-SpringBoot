package ma.projet.grpc.repositorires;

import ma.projet.grpc.entities.Compte;

import org.springframework.data.jpa.repository.JpaRepository;





public interface CompteRepository extends JpaRepository<Compte, String> {



}
