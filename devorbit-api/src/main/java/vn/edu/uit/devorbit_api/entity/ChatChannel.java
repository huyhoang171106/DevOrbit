package vn.edu.uit.devorbit_api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_channels")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatChannel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "channel_id", nullable = false, unique = true, length = 100)
    private String channelId;

    @NotBlank
    @Column(nullable = false, length = 255)
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ChatChannelType type;

    @Column(name = "reference_id", length = 50)
    private String referenceId;

    @Builder.Default
    @Column(nullable = false)
    private boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
