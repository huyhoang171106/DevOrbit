package vn.edu.uit.devorbit_api.dto.knowledge;

import java.util.UUID;

/**
 * DTO representing a citation to a source document.
 * Used inside {@link TutorResponse} to show which source materials informed the answer.
 *
 * @param sourceId     UUID of the knowledge source.
 * @param fileName     Original file name (e.g. "CS106_Syllabus.pdf").
 * @param url          Source URL if the content came from a web page.
 * @param sectionTitle Title of the section within the source where the information was found.
 * @param pageFrom     Starting page number (for PDFs).
 * @param pageTo       Ending page number (for PDFs).
 * @param chunkIndex   Index of the text chunk within the source.
 */
public record Citation(
    UUID sourceId,
    String fileName,
    String url,
    String sectionTitle,
    Integer pageFrom,
    Integer pageTo,
    Integer chunkIndex
) {}
