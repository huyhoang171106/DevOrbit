package vn.edu.uit.devorbit_api.service.ai;

import org.springframework.stereotype.Service;
import vn.edu.uit.devorbit_api.dto.publicapi.WebSearchResponse;
import vn.edu.uit.devorbit_api.dto.publicapi.WebSearchResponse.WebSearchResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Service to search the web for supplementary context.
 * Performs simulated search responses for known academic queries to support offline/local testing.
 */
@Service
public class WebSearchService {

    public WebSearchResponse search(String query) {
        String normalized = query.toLowerCase();
        
        List<WebSearchResult> results = new ArrayList<>();
        
        if (normalized.contains("giải tích") || normalized.contains("giaitich") || normalized.contains("calculus")) {
            results.add(new WebSearchResult(
                "https://www.reddit.com/r/learnmath/comments/umy1qb/how_to_selfstudy_everything_i_need_to_know_before/?tl=vi",
                "Cách tự học mọi thứ tôi cần biết trước khi học giải tích đại học - Reddit",
                "Khan Academy (dùng để học nội dung và hướng dẫn) · KutaSoftware (dùng để làm thêm bài tập) · Microsoft Math Solver (một ứng dụng web tính toán, nó ...",
                1
            ));
            results.add(new WebSearchResult(
                "https://svuit.org/mmtt/docs/MonHocDaiCuong/MA006.html",
                "MA006 - Giải tích | SVUIT-MMTT",
                "Chương 1: Phép tính vi tích phân của hàm một biến. · Chương 2: Chuỗi hàm và chuỗi số. · Chương 3: Phép tính vi tích phân của hàm nhiều biến.",
                2
            ));
            results.add(new WebSearchResult(
                "https://forum.uit.edu.vn/t/giai-tich-2-tai-lieu-nao-dung-hoc-tot/132278",
                "giải tích 2-tài liệu nào dùng học tốt? - UIT - Forum",
                "Có một cuốn mà mình thấy học rất tốt là cuốn “Giải tích 2” do thầy Đỗ Công Khanh bên Bách Khoa chủ biên, rất nhiều bài tập giải sẵn để cho bạn ...",
                3
            ));
            results.add(new WebSearchResult(
                "https://giasuplus.kesug.com/de-thi-uit-tai-lieu-dai-cuong-tong-hop/",
                "Đề Thi UIT Miễn Phí Cho Sinh Viên (Đại Cương) - Giasuplus",
                "Đề thi UIT tổng hợp + tài liệu các môn đại cương UIT: toán, triết, xác suất, lập trình cơ bản. Hỗ trợ học tập và ôn thi hiệu quả.",
                4
            ));
        } else {
            // General fallback
            results.add(new WebSearchResult(
                "https://forum.uit.edu.vn",
                "Diễn đàn Sinh viên trường Đại học Công nghệ Thông tin - ĐHQG-HCM",
                "Cổng thông tin, thảo luận trao đổi kinh nghiệm học tập, tài liệu học tập của sinh viên UIT.",
                1
            ));
            results.add(new WebSearchResult(
                "https://daa.uit.edu.vn",
                "Phòng Đào tạo Đại học - Trường Đại học Công nghệ Thông tin",
                "Thông báo học vụ, lịch thi, đăng ký môn học và chương trình đào tạo chính thức tại UIT.",
                2
            ));
        }

        return new WebSearchResponse("success", results);
    }
}
