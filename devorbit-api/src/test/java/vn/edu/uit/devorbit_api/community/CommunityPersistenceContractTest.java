package vn.edu.uit.devorbit_api.community;

import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.uit.devorbit_api.entity.ChatChannel;
import vn.edu.uit.devorbit_api.entity.ChatChannelType;
import vn.edu.uit.devorbit_api.entity.CommunityMessage;
import vn.edu.uit.devorbit_api.entity.CourseReview;
import vn.edu.uit.devorbit_api.entity.RepoReview;
import vn.edu.uit.devorbit_api.entity.RepoVote;
import vn.edu.uit.devorbit_api.repository.ChatChannelRepository;
import vn.edu.uit.devorbit_api.repository.CommunityMessageRepository;
import vn.edu.uit.devorbit_api.repository.CourseReviewRepository;
import vn.edu.uit.devorbit_api.repository.RepoReviewRepository;
import vn.edu.uit.devorbit_api.repository.RepoVoteRepository;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class CommunityPersistenceContractTest {

    @Test
    void migrationCreatesCommunityTablesAndConstraints() throws Exception {
        String migration = Files.readString(Path.of("src/main/resources/db/migration/V006__create_community_features.sql"));

        assertThat(migration).contains(
                "CREATE TABLE IF NOT EXISTS chat_channels",
                "CREATE TABLE IF NOT EXISTS community_messages",
                "CREATE TABLE IF NOT EXISTS repo_reviews",
                "CREATE TABLE IF NOT EXISTS repo_votes",
                "CREATE TABLE IF NOT EXISTS course_reviews",
                "CONSTRAINT uk_repo_review_student UNIQUE (repo_id, student_id)",
                "CONSTRAINT uk_repo_vote_student UNIQUE (repo_id, student_id)",
                "CONSTRAINT uk_course_review_student UNIQUE (course_id, student_id)"
        );
    }

    @Test
    void entitiesMapCommunityTablesAndLazyRelationships() throws Exception {
        assertTable(ChatChannel.class, "chat_channels");
        assertTable(CommunityMessage.class, "community_messages");
        assertTable(RepoReview.class, "repo_reviews");
        assertTable(RepoVote.class, "repo_votes");
        assertTable(CourseReview.class, "course_reviews");

        assertLazyManyToOne(CommunityMessage.class, "channel");
        assertLazyManyToOne(CommunityMessage.class, "student");
        assertLazyManyToOne(RepoReview.class, "repo");
        assertLazyManyToOne(RepoReview.class, "student");
        assertLazyManyToOne(RepoVote.class, "repo");
        assertLazyManyToOne(RepoVote.class, "student");
        assertLazyManyToOne(CourseReview.class, "course");
        assertLazyManyToOne(CourseReview.class, "student");

        assertThat(ChatChannelType.values())
                .containsExactly(ChatChannelType.GENERAL, ChatChannelType.COURSE, ChatChannelType.TECH_STACK);
    }

    @Test
    void repositoriesExposeJpaAccessForCommunityEntities() {
        assertThat(JpaRepository.class).isAssignableFrom(ChatChannelRepository.class);
        assertThat(JpaRepository.class).isAssignableFrom(CommunityMessageRepository.class);
        assertThat(JpaRepository.class).isAssignableFrom(RepoReviewRepository.class);
        assertThat(JpaRepository.class).isAssignableFrom(RepoVoteRepository.class);
        assertThat(JpaRepository.class).isAssignableFrom(CourseReviewRepository.class);
    }

    private static void assertTable(Class<?> entityClass, String tableName) {
        assertThat(entityClass.getAnnotation(Table.class).name()).isEqualTo(tableName);
    }

    private static void assertLazyManyToOne(Class<?> entityClass, String fieldName) throws Exception {
        ManyToOne relation = entityClass.getDeclaredField(fieldName).getAnnotation(ManyToOne.class);
        assertThat(relation).isNotNull();
        assertThat(relation.fetch()).isEqualTo(FetchType.LAZY);
    }
}
