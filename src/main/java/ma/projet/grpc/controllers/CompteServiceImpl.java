package ma.projet.grpc.controllers;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import ma.projet.grpc.services.CompteService;
import ma.projet.grpc.stubs.*;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@GrpcService
public class CompteServiceImpl extends CompteServiceGrpc.CompteServiceImplBase {

    // Simulating an in-memory database
    private final Map<String, Compte> compteDB = new ConcurrentHashMap<>();

    @Autowired
    private CompteService compteService;

    // Convert gRPC Compte to Entity
    public ma.projet.grpc.entities.Compte toEntity(Compte compteGrpc) {
        ma.projet.grpc.entities.Compte entity = new ma.projet.grpc.entities.Compte();
        entity.setId(compteGrpc.getId());
        entity.setSolde(compteGrpc.getSolde());
        entity.setType(compteGrpc.getType()); // Assuming you're using Enum name
        entity.setDateCreation(compteGrpc.getDateCreation());
        return entity;
    }

    // Convert Entity back to gRPC Compte
    public Compte toGrpc(ma.projet.grpc.entities.Compte entity) {
        return Compte.newBuilder()
                .setId(entity.getId())
                .setSolde(entity.getSolde())
                .setType(entity.getType()) // Convert string to enum
                .setDateCreation(entity.getDateCreation())
                .build();
    }

    @Override
    public void allComptes(GetAllComptesRequest request, StreamObserver<GetAllComptesResponse> responseObserver) {
        GetAllComptesResponse.Builder responseBuilder = GetAllComptesResponse.newBuilder();
        responseBuilder.addAllComptes(compteDB.values());
        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void compteById(GetCompteByIdRequest request, StreamObserver<GetCompteByIdResponse> responseObserver) {
        Compte compte = compteDB.get(request.getId());
        if (compte != null) {
            responseObserver.onNext(GetCompteByIdResponse.newBuilder().setCompte(compte).build());
        } else {
            responseObserver.onError(Status.NOT_FOUND.withDescription("Compte not found").asRuntimeException());
        }
        responseObserver.onCompleted();
    }

    @Override
    public void totalSolde(GetTotalSoldeRequest request, StreamObserver<GetTotalSoldeResponse> responseObserver) {
        int count = compteDB.size();
        float sum = 0;
        for (Compte compte : compteDB.values()) {
            sum += compte.getSolde();
        }
        float average = count > 0 ? sum / count : 0;

        SoldeStats stats = SoldeStats.newBuilder()
                .setCount(count)
                .setSum(sum)
                .setAverage(average)
                .build();

        responseObserver.onNext(GetTotalSoldeResponse.newBuilder().setStats(stats).build());
        responseObserver.onCompleted();
    }

    @Override
    public void saveCompte(SaveCompteRequest request, StreamObserver<SaveCompteResponse> responseObserver) {
        try {
            // Extract compte details from the request
            CompteRequest compteReq = request.getCompte();
            String id = UUID.randomUUID().toString(); // Generate a new UUID for the account

            // Save the compte to the database
            ma.projet.grpc.entities.Compte entity = new ma.projet.grpc.entities.Compte();
            entity.setId(id);
            entity.setSolde(compteReq.getSolde());
            entity.setType(compteReq.getType());
            entity.setDateCreation(compteReq.getDateCreation());
            compteService.saveCompte(entity);


            // create grpc response

            responseObserver.onNext(SaveCompteResponse.newBuilder().setCompte(toGrpc(entity)).build());
            responseObserver.onCompleted();



        } catch (ObjectOptimisticLockingFailureException ex) {
            // Catch the optimistic locking failure and return an error response
            responseObserver.onError(Status.ABORTED.withDescription("Optimistic Locking Failure: " + ex.getMessage())
                    .asRuntimeException());
        } catch (Exception ex) {
            // Catch any other unexpected exceptions
            responseObserver.onError(Status.INTERNAL.withDescription("Internal Server Error: " + ex.getMessage())
                    .asRuntimeException());
        }
    }
}
