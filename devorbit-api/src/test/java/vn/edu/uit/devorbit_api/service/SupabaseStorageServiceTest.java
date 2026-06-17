package vn.edu.uit.devorbit_api.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SupabaseStorageServiceTest {

    @Test
    void safeFileNameStripsPathSegmentsAndUnsafeCharacters() {
        assertThat(SupabaseStorageService.safeFileName("../avatars/../../evil avatar;.png"))
                .isEqualTo("evil_avatar_.png");
    }

    @Test
    void safeFileNameFallsBackForBlankOrDotNames() {
        assertThat(SupabaseStorageService.safeFileName(null)).isEqualTo("upload");
        assertThat(SupabaseStorageService.safeFileName("..")).isEqualTo("upload");
    }
}
