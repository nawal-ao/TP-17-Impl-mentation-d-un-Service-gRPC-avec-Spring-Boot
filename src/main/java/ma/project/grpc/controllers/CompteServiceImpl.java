package ma.project.grpc.controllers;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import ma.project.grpc.services.CompteService;
import ma.project.grpc.stubs.*;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.stream.Collectors;

@GrpcService
public class CompteServiceImpl extends CompteServiceGrpc.CompteServiceImplBase {

    private final CompteService compteService;

    public CompteServiceImpl(CompteService compteService) {
        this.compteService = compteService;
    }

    private Compte convertCompteEntityToGrpc(ma.project.grpc.entities.Compte entity) {
        return Compte.newBuilder()
                .setId(entity.getId())
                .setSolde(entity.getSolde())
                .setDateCreation(entity.getDateCreation())
                .setType(TypeCompte.valueOf(entity.getType()))
                .build();
    }

    @Override
    public void allComptes(GetAllComptesRequest request, StreamObserver<GetAllComptesResponse> responseObserver) {
        var comptes = compteService.findAllComptes().stream()
                .map(this::convertCompteEntityToGrpc)
                .collect(Collectors.toList());

        responseObserver.onNext(GetAllComptesResponse.newBuilder()
                .addAllComptes(comptes).build());
        responseObserver.onCompleted();
    }

    @Override
    public void compteById(GetCompteByIdRequest request, StreamObserver<GetCompteByIdResponse> responseObserver) {
        ma.project.grpc.entities.Compte entity = compteService.findCompteById(request.getId());

        if (entity != null) {
            Compte grpcCompte = convertCompteEntityToGrpc(entity);
            responseObserver.onNext(GetCompteByIdResponse.newBuilder()
                    .setCompte(grpcCompte).build());
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("Compte non trouvé avec l'ID: " + request.getId())
                    .asRuntimeException());
        }
    }

    @Override
    public void totalSolde(GetTotalSoldeRequest request, StreamObserver<GetTotalSoldeResponse> responseObserver) {
        SoldeStats stats = compteService.getSoldeStats();

        responseObserver.onNext(GetTotalSoldeResponse.newBuilder()
                .setStats(stats).build());
        responseObserver.onCompleted();
    }

    @Override
    public void saveCompte(SaveCompteRequest request, StreamObserver<SaveCompteResponse> responseObserver) {
        var compteReq = request.getCompte();

        var compteEntity = new ma.project.grpc.entities.Compte();
        compteEntity.setSolde(compteReq.getSolde());
        compteEntity.setDateCreation(compteReq.getDateCreation());
        compteEntity.setType(compteReq.getType().name());

        var savedCompte = compteService.saveCompte(compteEntity);

        var grpcCompte = convertCompteEntityToGrpc(savedCompte);

        responseObserver.onNext(SaveCompteResponse.newBuilder()
                .setCompte(grpcCompte).build());
        responseObserver.onCompleted();
    }
}