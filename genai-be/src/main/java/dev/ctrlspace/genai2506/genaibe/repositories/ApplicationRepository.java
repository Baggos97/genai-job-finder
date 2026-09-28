package dev.ctrlspace.genai2506.genaibe.repositories;

import dev.ctrlspace.genai2506.genaibe.models.entities.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, UUID> {

    // Βρες όλες τις αιτήσεις ενός χρήστη
    List<Application> findByUserId(UUID userId);

    // Βρες όλες τις αιτήσεις για μια θέση (για τον Admin)
    List<Application> findByDocumentId(UUID documentId);
}
