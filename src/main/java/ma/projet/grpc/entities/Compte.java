package ma.projet.grpc.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import ma.projet.grpc.stubs.TypeCompte;  // Import the enum
import lombok.Data;

@Entity
@Data
public class Compte {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;

    private double solde;

    // Use @Enumerated(EnumType.ORDINAL) to store the ordinal of the enum
    @Enumerated(EnumType.ORDINAL)
    private TypeCompte type;

    private String dateCreation;
}


