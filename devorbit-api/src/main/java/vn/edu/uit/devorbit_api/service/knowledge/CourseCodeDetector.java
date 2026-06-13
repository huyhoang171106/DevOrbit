package vn.edu.uit.devorbit_api.service.knowledge;

import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Detects course codes (e.g., IT003, SE104) from user messages.
 */
@Component
public class CourseCodeDetector {

    private static final Pattern COURSE_CODE_PATTERN = Pattern.compile("\\b([A-Z]{2,4}\\d{2,4})\\b");

    /**
     * Extract the first course code found in the message.
     */
    public Optional<String> detect(String message) {
        if (message == null || message.isBlank()) {
            return Optional.empty();
        }
        Matcher matcher = COURSE_CODE_PATTERN.matcher(message);
        return matcher.find() ? Optional.of(matcher.group(1)) : Optional.empty();
    }
}
