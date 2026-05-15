package vn.edu.uit.devorbit_api.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.context.annotation.Configuration;

import java.util.*;

@Getter
@Configuration
public class ElectiveGroupConfig {

    private Map<String, ElectiveGroupDef> groups;

    @PostConstruct
    void init() {
        groups = new LinkedHashMap<>();

        // Tự chọn Cơ sở ngành — HK 4-5
        groups.put("TC_CSN", new ElectiveGroupDef(
            "TC_CSN", "Tự chọn Cơ sở ngành",
            "Các môn tự chọn thuộc khối kiến thức cơ sở ngành, tích lũy tối thiểu 12 tín chỉ.",
            12, null,
            List.of("SE359", "SE102", "SE115", "SE116", "SE114", "SE360",
                    "SE361", "SE301", "SE215", "SE113", "SE358", "SE117")
        ));

        // Tự chọn Chuyên ngành (General) — HK 6-7
        groups.put("TC_CN", new ElectiveGroupDef(
            "TC_CN", "Tự chọn Chuyên ngành",
            "Các môn tự chọn thuộc khối kiến thức chuyên ngành, tích lũy tối thiểu 16 tín chỉ.",
            16, null,
            List.of("SE330", "SE332", "SE334", "SE347", "SE350", "SE343",
                    "SE355", "SE310", "SE346", "SE404", "SE331", "SE313",
                    "SE352", "SE101", "SE106", "SE214", "SE362", "SE363",
                    "SE364", "SE365")
        ));

        // Định hướng Phát triển Phần mềm (sub-group của TC_CN)
        groups.put("TC_CN_PM", new ElectiveGroupDef(
            "TC_CN_PM", "Định hướng Phát triển Phần mềm",
            "Các môn định hướng Phát triển Phần mềm, nằm trong 16 TC tự chọn chuyên ngành.",
            0, "TC_CN",
            List.of("SE109", "SE356", "SE357", "SE325")
        ));

        // Định hướng Môi trường ảo & Game (sub-group của TC_CN)
        groups.put("TC_CN_GAME", new ElectiveGroupDef(
            "TC_CN_GAME", "Định hướng Môi trường ảo & Game",
            "Các môn định hướng Môi trường ảo và Game, nằm trong 16 TC tự chọn chuyên ngành.",
            0, "TC_CN",
            List.of("SE221", "SE220", "SE320", "SE327", "SE328", "SE344",
                    "SE314", "SE315", "SE316", "SE317")
        ));

        // Chuyên đề Tốt nghiệp — HK 8
        groups.put("TC_TN", new ElectiveGroupDef(
            "TC_TN", "Chuyên đề Tốt nghiệp",
            "Các môn chuyên đề tốt nghiệp, chọn tối thiểu 1 môn (4 TC) kết hợp với Đồ án tốt nghiệp.",
            4, null,
            List.of("SE400", "SE406", "SE403", "SE407", "SE408", "SE409")
        ));
    }

    public record ElectiveGroupDef(
        String code,
        String name,
        String description,
        int minCredits,
        String parentGroupCode,
        List<String> courseCodes
    ) {}
}
