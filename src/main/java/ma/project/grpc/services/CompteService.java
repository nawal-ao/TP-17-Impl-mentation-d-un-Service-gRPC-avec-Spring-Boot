package ma.project.grpc.services;

import ma.project.grpc.entities.Compte;
import ma.project.grpc.repositories.CompteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Service
@Transactional
public class CompteService {
    private final CompteRepository compteRepository;

    public CompteService(CompteRepository compteRepository) {
        this.compteRepository = compteRepository;
    }

    public List<Compte> findAllComptes() {
        return compteRepository.findAll();
    }

    public Compte findCompteById(String id) {
        return compteRepository.findById(id).orElse(null);
    }

    public Compte saveCompte(Compte compte) {
        return compteRepository.save(compte);
    }

    // Méthode pour calculer les statistiques
    public ma.project.grpc.stubs.SoldeStats getSoldeStats() {
        int count = (int) compteRepository.count();
        float sum = compteRepository.getSumSolde();
        float average = count > 0 ? sum / count : 0;

        return ma.project.grpc.stubs.SoldeStats.newBuilder()
                .setCount(count)
                .setSum(sum)
                .setAverage(average)
                .build();
    }
}