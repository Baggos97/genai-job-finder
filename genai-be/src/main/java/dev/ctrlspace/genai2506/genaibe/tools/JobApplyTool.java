package dev.ctrlspace.genai2506.genaibe.tools;

import dev.ctrlspace.genai2506.genaibe.models.dtos.completions.MessageDTO;
import dev.ctrlspace.genai2506.genaibe.models.entities.Agent;
import dev.ctrlspace.genai2506.genaibe.models.entities.Application;
import dev.ctrlspace.genai2506.genaibe.models.entities.ChatMessage;
import dev.ctrlspace.genai2506.genaibe.models.entities.Document;
import dev.ctrlspace.genai2506.genaibe.models.entities.User;
import dev.ctrlspace.genai2506.genaibe.repositories.ApplicationRepository;
import dev.ctrlspace.genai2506.genaibe.repositories.DocumentRepository;
import dev.ctrlspace.genai2506.genaibe.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

/**
 * JobApplyTool — το tool που καλεί ο agent για να κάνει apply εκ μέρους του χρήστη.
 *
 * Ο agent το καλεί με:
 *   job_id         — το UUID της θέσης (document)
 *   user_id        — το UUID του χρήστη
 *   motivation_text — το κείμενο motivation που δημιουργήθηκε ή δόθηκε από τον χρήστη
 *
 * Επιστρέφει:
 *   application_id + status (submitted)
 */
@Component
public class JobApplyTool implements Tool {

    private static final Logger logger = LoggerFactory.getLogger(JobApplyTool.class);

    private final ApplicationRepository applicationRepository;
    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public JobApplyTool(ApplicationRepository applicationRepository,
                        DocumentRepository documentRepository,
                        UserRepository userRepository,
                        ObjectMapper objectMapper) {
        this.applicationRepository = applicationRepository;
        this.documentRepository = documentRepository;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public String getName() {
        return "job_apply";
    }

    @Override
    public MessageDTO execute(MessageDTO.ToolCall toolCall, Agent agent, ChatMessage originalMessage) throws Exception {
        // Διαβάζουμε τα arguments που έστειλε ο agent
        JsonNode args = objectMapper.readTree(toolCall.getFunction().getArguments());

        String jobId         = args.get("job_id").asText();
        String motivationText = args.get("motivation_text").asText();

        // Ο user_id έρχεται από το originalMessage (ποιος μιλάει στο chat)
        // αν δεν υπάρχει user στο message, ψάχνουμε από args
        UUID userId;
        if (originalMessage.getUser() != null) {
            userId = originalMessage.getUser().getId();
        } else {
            userId = UUID.fromString(args.get("user_id").asText());
        }

        logger.info("JobApplyTool: user {} applying to job {}", userId, jobId);

        // Φέρνουμε την θέση από τη βάση
        Document job = documentRepository.findById(UUID.fromString(jobId))
                .orElseThrow(() -> new RuntimeException("Job not found with id: " + jobId));

        // Φέρνουμε τον χρήστη από τη βάση
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // Δημιουργούμε και αποθηκεύουμε την αίτηση
        Application application = new Application();
        application.setAccount(originalMessage.getAccount());
        application.setUser(user);
        application.setDocument(job);
        application.setMotivationText(motivationText);
        application.setStatus("submitted");

        application = applicationRepository.save(application);

        logger.info("Application saved with id: {}", application.getId());

        // Επιστρέφουμε επιβεβαίωση στον agent
        String result = String.format(
            "Application submitted successfully!\n" +
            "application_id: %s\n" +
            "job: %s\n" +
            "status: submitted",
            application.getId(),
            job.getTitle()
        );

        return MessageDTO.builder()
                .role("tool")
                .content(result)
                .build();
    }
}
