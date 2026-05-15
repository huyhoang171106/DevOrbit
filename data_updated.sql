-- CLEANUP OLD DATA
TRUNCATE TABLE course_relationships CASCADE;
TRUNCATE TABLE github_repos CASCADE;
TRUNCATE TABLE courses CASCADE;
TRUNCATE TABLE repo_candidates CASCADE;
TRUNCATE TABLE repo_tech_stacks CASCADE;

-- INSERT NEW COURSES
INSERT INTO courses (mamh, tenmh, tenmh_en, sotc, lt, th, loaimonhoc, semester, management_unit, description, is_open) VALUES
('ENG01', 'Anh văn 1', NULL, 4, 4, 0, 'ĐC', 1, 'CNPM', 'Môn Anh văn 1 là môn học đầu tiên trong các môn học Anh văn bắt buộc dành cho sinh viên chương trình đào tạo chính quy. Môn học nhằm trang bị kiến thức và kỹ năng sử dụng tiếng Anh trong môi trường làm việc nhầm giúp sinh viên đạt được trình độ tiếng Anh tương đương từ 30 đến dưới 42 điểm theo thang điểm đánh giá trình độ tiếng Anh toàn cầu GSE (The Global Scale of English) tương đương trình độ tiền trung cấp (pre-intermediate A2-B1).', true),
('IT001', 'Nhập môn Lập trình', NULL, 4, 3, 1, 'ĐC', 1, 'CNPM', 'Môn học sẽ cung cấp các kiến thức nền tảng về máy tính, tư duy và các kỹ năng
căn bản lập trình cho tất cả sinh viên các ngành Công nghệ thông tin.
Đối với hệ tài năng: sinh viên sẽ được trang bị các kiến thức nâng cao về tư duy
và các kỹ năng lập trình thông qua một số bài toán có độ phức tạp cao.', true),
('IT007', 'Hệ điều hành', NULL, 4, 3, 1, 'CSN', 1, 'CNPM', 'Giới thiệu các khái niệm, các nguyên lý hoạt động cơ bản trong hệ điều hành đi theo trình tự từ đơn giản đến phức tạp. Môn học gồm có 8 chương ứng với các khối kiến thức sau: tổng quan về hệ điều hành, cấu trúc hệ điều hành, quản lý tiến trình, định thời CPU, đồng bộ hóa tiến trình, tắc nghẽn (deadlocks), quản lý bộ nhớ và bộ nhớ ảo. Kết thúc phần lý thuyết của từng khối kiến thức sẽ là các bài thực hành trong phòng lab để có cái nhìn thực tế hơn về các khái niệm, các giải thuật đã được giới thiệu.', true),
('MA003', 'Đại số tuyến tính', NULL, 3, 3, 0, 'ĐC', 1, 'CNPM', 'Đại số tuyến tính là môn học ở giai đoạn kiến thức đại cương, là môn học bắt buộc đối với tất cả sinh viên. Môn học này giúp cho sinh viên nắm được khái niệm và làm được các phép toán về: ma trận, hạng, định thức, hệ phương trình tuyến tính; cách giải hệ phương trình tuyến tính bằng phương pháp Cramer, phương pháp Gauss, phương pháp Gauss-Jordan; về không gian vector, sự phụ thuộc, độc lập tuyến tính, tập sinh, cơ sở và số chiều của không gian vector; ma trận chéo hóa và ý nghĩa của việc chéo hóa ma trận; về ánh xạ tuyến tính, toán tử tuyến tính, dạng toàn phương và phép đưa dạng toàn phương về dạng chính tắc; để từ đó SV có thể tiếp tục học tập những môn chuyên ngành, hay phục vụ cho quá trình làm khóa luận tốt nghiệp.', true),
('MA006', 'Giải tích', NULL, 4, 4, 0, 'TT', 1, 'CNPM', 'Môn Giải tích là môn học ở giai đoạn kiến thức đại cương, là môn học bắt buộc đối với tất cả sinh viên. Môn học này giúp cho SV có kiến thức cơ bản về phép tính vi phân hàm nhiều biến; phép tính tích phân hàm nhiều biến (tích phân bội); tích phân đường, tích phân mặt; cũng như là kỹ năng khảo sát chuỗi số, chuỗi hàm, tích phân suy rộng,…cùng với việc nhận dạng và giải quyết một số phương trình vi phân cấp một, cấp cao,…để từ đó SV có thể tiếp tục học tập những môn chuyên ngành, hay phục vụ cho quá trình làm khóa luận tốt nghiệp.', true),
('SE005', 'Giới thiệu ngành Kỹ thuật Phần mềm', NULL, 1, 1, 0, 'CSN', 1, 'CNPM', 'Môn Giới thiệu ngành Công Nghệ Phần Mềm (CNPM) được thiết kế để giúp sinh
viên năm thứ nhất tiếp cận các kiến thức liên quan định hướng, kỹ năng nghề
nghiệp của cử nhân/kỹ sư CNPM. Cụ thể là:
- Giới thiệu những thách thức hiện tại đối với kỹ sư/cử nhân CNPM.
- Vai trò của CNPM trong tổng thể nhóm ngành CNTT và trong nền kinh tế tri
thức.
- Lược sử các xu hướng chính và các xu hướng tương lai của ngành CNPM.
- Định nghĩa và đặc điểm của sản phẩm phần mềm.
- Các khối kiến thức tổng quan về CNPM và phương pháp giải quyết vấn đề
trong lĩnh vực phần mềm.
- Thực hành các kỹ năng làm việc nhóm, viết báo cáo và thuyết trình.', true),
('ENG02', 'Anh văn 2', NULL, 4, 4, 0, 'ĐC', 2, 'CNPM', 'Sau khi hoàn thành môn học này sinh viên có trình độ, kỹ năng tiếng Anh tương
đương TOEIC-400, sinh viên có thể:
- Củng cố nền tảng căn bản về tiếng Anh tổng quát; thiết lập kỹ năng đọc hiểu;
hiểu và sử dụng các cấu trúc ngữ pháp đơn giản; củng cố kỹ năng giao tiếp căn
bản trong cuộc sống hàng ngày.
- Xây dựng kỹ năng nghe nói, đọc hiểu và có được vốn từ vựng về các chủ đề
trong công sở, công việc hằng ngày: công ty, điện thoại, sản phẩm, sản xuất,
ngân hàng, tiền tệ, luật pháp và kinh doanh.
- Rèn luyện kỹ năng viết email , báo cáo ngắn, bài miêu tả nhận xét sản phẩm,
thư phàn nàn, và thư xác nhận đặt hàng.', true),
('IT002', 'Lập trình hướng đối tượng', NULL, 4, 3, 1, 'TT', 2, 'CNPM', 'Môn học trang bị cho sinh viên kiến thức và kỹ năng về lập trình hướng đối tượng, các nguyên lý cơ bản của thiết kế hướng đối tượng, các vấn đề căn bản và một số vấn đề nâng cao trong việc cài đặt các lớp và phương thức. Các quan niệm nằm sau cây thừa kế, đa hình, các tính chất của đối tượng, thừa kế và phân lớp. Cách thức trao đổi và truyền thông giữa các đối tượng.', true),
('IT003', 'Cấu trúc dữ liệu và giải thuật', NULL, 4, 3, 1, 'CSN', 2, 'CNPM', 'Môn học giúp sinh viên hiểu tầm quan trọng của giải thuật và cách tổ chức dữ
liệu, là hai thành tố quan trọng nhất cho một chương trình. Nắm bắt, áp dụng
được các giải thuật, cấu trúc dữ liệu thường được áp dụng trong việc giải quyết
bài toán trong tin học. Giúp củng cố và phát triển kỹ năng lập trình vừa được học
trong môn học trước.', true),
('MA004', 'Cấu trúc rời rạc', NULL, 4, 4, 0, 'ĐC', 2, 'CNPM', 'Cấu trúc rời rạc là môn học ở giai đoạn kiến thức đại cương, là môn học bắt buộc đối với tất cả sinh viên. Đây là một trong những môn thi tuyển sinh đầu vào ở bậc Sau đại học ngành công nghệ thông tin. Môn học này giúp cho sinh viên có kiến thức, có kỹ năng giải quyết được những bài toán liên quan đến Toán rời rạc (cơ sở logic, các phương pháp đếm, quan hệ, đại số Bool và hàm Bool), và Lý thuyết đồ thị (các khái niệm cơ bản về lý thuyết đồ thị, đường đi, chu trình và cây).', true),
('MA005', 'Xác suất thống kê', NULL, 3, 3, 0, 'ĐC', 2, 'CNPM', 'Xác suất thống kê là môn học bắt buộc (hoặc tự chọn) của sinh viên một số ngành thuộc lĩnh vực công nghệ thông tin. Đây là một trong những môn thi tuyển sinh đầu vào ở bậc Sau đại học ngành Khoa học máy tính. Môn học này trình bày các khái niệm và phương pháp về: Lý thuyết xác suất (Không gian xác suất; Biến ngẫu nhiên; Hàm đăc trưng; Dãy các biến ngẫu nhiên; Các quy luật phân phối xác suất; Các định lý giới hạn phân phối xác suất) và Thống kê (Mẫu ngẫu nhiên; Ước lượng điểm và ước lượng khoảng; Kiểm định các giả thiết thống kê; Phân tích tương quan và hồi quy; Một số vấn đề về quá trình ngẫu nhiên). Ngoài ra, môn học này còn giới thiệu về cách thức nhận diện, phân tích và xử lý một vấn đề thực tế; xử lý các số liệu thống kê; để từ đó giúp cho người dùng đưa ra các suy luận phù hợp (nhằm hỗ trợ cho quá trình ra quyết định).', true),
('ENG03', 'Anh văn 3', NULL, 4, 4, 0, 'ĐC', 3, 'CNPM', 'Môn học gồm 12 bài học (Units 01 – 12). Bài học bao gồm các hướng dẫn từng phần được kiểm tra trong bài test TOEIC như các điểm ngữ pháp, từ vựng, kỹ năng nghe và đọc nói và viết. Sinh viên cũng được cung cấp bài thi TOEIC thử cũng như các chiến thuật làm bài.', true),
('IT004', 'Cơ sở dữ liệu', NULL, 4, 3, 1, 'TT', 3, 'CNPM', 'Môn học trình bày về sự cần thiết của cơ sở dữ liệu trong doanh nghiệp và trong các loại hình tổ chức khác. Cung cấp sự hiểu biết về nguyên lý của các hệ thống cơ sở dữ liệu, tập trung trên CSDL quan hệ (mô hình dữ liệu quan hệ, các ngôn ngữ truy vấn).
Sinh viên có khả năng sử dụng các kỹ thuật, công cụ để có thể thiết kế, thao tác với
một CSDL quan hệ thông qua hệ quản trị CSDL cụ thể (MS SQL Server), phục vụ cho nhiều môn học nâng cao về CSDL trong những học kỳ kế tiếp.', true),
('IT005', 'Nhập môn mạng máy tính', NULL, 4, 3, 1, 'CSN', 3, 'CNPM', 'Cung cấp cho sinh viên những khái niệm cơ bản về mạng máy tính và truyền dữ liệu trên mạng; các dịch vụ mạng cơ bản, kỹ thuật mạng không dây.', true),
('IT008', 'Lập trình trực quan', NULL, 4, 3, 1, 'CSN', 3, 'CNPM', 'Môn học này trình bày các khái niệm và phương pháp lập trình trực quan trên môi trường Windows, cách trình bày các cách thức, quy trình tạo một ứng dụng trên Windows, cách cách thức xử lý thông điệp, các giao diện điều khiển, cơ chế quản lý bộ nhớ, thư viện liên kết động, lập trình đa nhiệm…', true),
('IT012', 'Tổ chức và cấu trúc máy tính II', NULL, 4, 3, 1, 'CSN', 3, 'CNPM', 'Môn học này trình bày kiến thức cơ bản về kiến trúc máy tính bao gồm:
- Lịch sử hình thành và các công nghệ liên quan đến phát triển máy tính.
- Chức năng và nguyên lý hoạt động của các bộ phận trong máy tính.
- Cách biểu diễn dữ liệu, tính toán trong máy tính.
- Cách phân tích các mạch số cơ bản.
- Kiến trúc bộ lệnh, lập trình hợp ngữ.
 -Các vấn đề liên quan tới nguyên lý hoạt động của bộ xử lý.', true),
('SE104', 'Nhập môn công nghệ phần mềm', NULL, 4, 3, 1, 'TT', 4, 'CNPM', 'Môn học này nhằm cung cấp cho các sinh viên các kiến thức cơ sở liên quan đến các đối tượng chính yếu trong lĩnh vực công nghệ phần mềm (qui trình công nghệ, phương pháp kỹ thuật thực hiện, phương pháp tổ chức quản lý, công cụ và môi trường triển khai phần mềm, …). Giúp sinh viên hiểu và biết tiến hành xây dựng phần mềm một cách có hệ thống, có phương pháp. Trong quá trình học, sinh viên sẽ được giới thiệu nhiều phương pháp khác nhau để có được góc nhìn tổng quan về các phương pháp. Và để minh họa cụ thể hơn, phương pháp OMT (Object Modeling Technique) được chọn để trình bày (với một sự lược giản để thích hợp với tính chất nhập môn của môn học).', true),
('SS004', 'Kỹ năng nghề nghiệp', NULL, 2, 2, 0, 'KHÁC', 4, 'CNPM', 'Môn học cung cấp các kiến thức về các kỹ năng hỗ trợ trong lĩnh vực CNTT. Các kỹ năng này gồm kỹ năng giao tiếp, kỹ năng làm việc nhóm, kỹ năng tư duy, kỹ năng quản lý thời gian và kỹ năng thuyết trình. Nội dung môn học hướng sinh viên tới việc tìm hiểu và thực hành các kỹ năng này. Sinh viên sẽ có những kiến thức và kỹ năng ở mức độ cơ bản để có thể đáp ứng các yêu cầu học tập và làm việc trong ngành CNTT', true),
('SS007', 'Triết học Mác – Lênin', NULL, 3, 3, 0, 'ĐC', 4, 'CNPM', 'Môn học trang bị cho sinh viên những kiến thức cơ bản về Triết học Mác – Lênin', true),
('SS008', 'Kinh tế chính trị Mác – Lênin', NULL, 2, 2, 0, 'ĐC', 4, 'CNPM', 'Môn học trang bị cho sinh viên những kiến thức cơ bản về Kinh tế chính trị Mác – Lênin', true),
('SE100', 'Phương pháp phát triển phần mềm hướng đối tượng', NULL, 4, 3, 1, 'CSN', 5, 'CNPM', 'Môn học này trình bày về phân tích và thiết kế hệ thống phần mềm theo hướng đối tượng. Nội dung môn học trình từ cơ bản tới chuyên sâu các thao tác trong quá trình phát triển phần mềm. Chương 1 trình bày sơ lược về quy trình phát triển phần mềm. Chương 2 trình bày về các khái niệm cơ bản về hướng đối tượng. Chương 3 và 4 trình bày về mô hình hóa các yêu cầu. Chương 5 trình bày về phân tích phần mềm hướng đối tượng. Chương 6 trình bày về thiết kế phần mềm hướng đối tượng. Chương 7 thảo luận một số vấn đề khác trong phát triển phần mềm hướng đối tượng.', true),
('SS009', 'Chủ nghĩa xã hội khoa học', NULL, 2, 2, 0, 'TT', 5, 'CNPM', 'Môn học trang bị cho sinh viên những kiến thức cơ bản về Chủ nghĩa xã hội khoa học', true),
('SS010', 'Lịch sử Đảng Cộng sản Việt Nam', NULL, 2, 2, 0, 'ĐC', 5, 'CNPM', 'Môn học trang bị cho sinh viên những kiến thức cơ bản về Lịch sử Đảng Cộng sản Việt Nam', true),
('SE503', 'Đồ án', NULL, 2, 2, 0, 'TT', 6, 'CNPM', NULL, true),
('SS003', 'Tư tưởng Hồ Chí Minh', NULL, 2, 2, 0, 'ĐC', 6, 'CNPM', 'Môn học trang bị cho sinh viên tư tưởng Hồ Chí Minh về các vấn đề cơ bản của cách mạng Việt Nam, từ cách mạng dân tộc dân chủ nhân dân đến cách mạng xã hội chủ nghĩa như: Tư tưởng Hồ Chí Minh về vấn đề dân tộc và cách mạng giải phóng dân tộc; về chủ nghĩa xã hội và con đường quá độ lên chủ nghĩa xã hội; về Đảng Cộng sản Việt Nam; về đại đoàn kết dân tộc và đoàn kết quốc tế; về xây dựng nhà nước của dân, do dân và vì dân; về văn hóa, đạo đức và xây dựng con người mới.', true),
('SS006', 'Pháp luật đại cương', NULL, 2, 2, 0, 'TT', 6, 'CNPM', 'Môn học nằm trong khối kiến thức đại cương trong chương trình đào tạo. Môn học hướng đến việc trang bị những kiến thức nền tảng về nhà nước và pháp luật cho người học. Qua đó, người học có những kiến thức cơ bản về nhà nước và pháp luật hướng đến việc hình thành kỹ năng sống và thái độ sống chuẩn mực theo hiến pháp và pháp luật.', true),
('SE502', 'Thực tập', NULL, 2, 2, 0, 'TT', 7, 'CNPM', NULL, true),
('SE505', 'Khóa luận tốt nghiệp', NULL, 10, 10, 0, 'TT', 8, 'CNPM', 'Để tốt nghiệp, sinh viên cần phải hoặc hoàn thành Khóa luận tốt nghiệp hoặc thi 3 môn chuyên đề tốt nghiệp. Với khóa luận tốt nghiệp, sinh viên phải làm một khóa luận phần mềm trong các chuyên ngành là kỹ thuật phần mềm, lập trình nhúng hoặc lập trình game dưới sự hướng dẫn của giảng viên hướng dẫn và phải bảo vệ thành công khóa luận của mình trước hội đồng. Với 3 môn chuyên đề, sinh viên phải học và phải thi 3 môn chuyên đề do khoa đưa ra.', true),
('SE506', 'Đồ án tốt nghiệp tại doanh nghiệp', NULL, 10, 10, 0, 'TT', 8, 'CNPM', NULL, true),
('SE101', 'Phương pháp mô hình hóa', NULL, 3, 3, 0, 'CN', NULL, 'CNPM', 'Trình bày các kiến trúc, nền tảng về các phương pháp mô hình hóa thông tin, tri thức, biểu diễn vấn đề và lời giải, mô hình hóa hệ thống. Sinh viên tiếp cận với các các phương pháp mô hình hóa và biểu diễn vấn đề như mô hình hóa và biểu diễn dữ liệu, mô hình hóa và biểu diễn quan hệ, mô hình hóa và biểu diễn tiến trình, mô hình hóa và biểu diễn tri thức như phương pháp SDLC, JSD, SSM, OOA...Sinh viên làm quen với các công cụ đùn biểu diễn mô hình như công cụ CASE (upper và lower), các ngôn ngữ mô phỏng mô hình hóa như ngôn ngữ UML, VRML..nhằm hiện thực hóa một hệ thống. Học phần là sự kết hợp giữa các bài giảng, thuyết trình, bài tập nhỏ, tự nghiên cứu tài liệu và báo cáo đồ án kết thúc môn học. Học phần được chia làm 2 phần: phần 1 dẫn nhập và giới thiệu những khái niệm về các mô hình đặc trung hiện nay, phần 2 là giới thiệu về phương pháp luận dùng cho mô hình hóa, và phần 3 giới thiệu cụ thể về các mô hình biểu diễn thông tin, dữ liệu, thời gian thực.', true),
('SE102', 'Nhập môn phát triển game', NULL, 3, 2, 1, 'CSN', NULL, 'CNPM', 'Môn học giới thiệu cho Sinh viên những khái niệm, thông tin cơ bản trong ngành game và đi sâu vào kỹ thuật lập trình DirectX để xây dựng các game 2D đơn giản như Tetris, Battle City, Mario, Contras... Chương 1 giới thiệu tổng quan về ngành game. Chương 2 giới thiệu về kỹ thuật lập trình Windows dùng C++ và Windows SDK. Chương 3 giới thiệu kỹ thuật làm chuyển động và kỹ thuật lập trình DirectX cơ bản. Chương 4 cung cấp kỹ thuật làm việc với Sprite và xử lý thiết bị nhập. Chương 5 thảo luận về các kỹ thuật hỗ trợ khác như phép biến đổi, lập trình DirectSound, hiển thị chữ ... Chương 6 bàn luận về Game Engine và cách xây dựng một game engine đơn giản.', true),
('SE106', 'Đặc tả hình thức', NULL, 4, 4, 0, 'CN', NULL, 'CNPM', 'Trình bày các kiến trúc, nền tảng về đặc tả hình thức, là một trong các cách tiếp cận xây dựng môn học. Thông qua các ngôn ngữ đặc tả hình thức là ngôn ngữ VDM và ngôn ngữ Z, sinh viên có thể dễ dàng nắm bắt được quy trình và các phương pháp hệ thống riêng biệt từ đặc tả, thiết kế đến thực hiện chương trình. Học phần là sự kết hợp giữa các bài giảng, thuyết trình, bài tập nhỏ, tự nghiên cứu tài liệu và kiểm tra cuối kỳ. Học phần được chia làm 2 phần: phần 1 dẫn nhập và giới thiệu những khái niệm cơ sở của đặc tả hình thức được minh họa bằng ngôn ngữ VDM, phần 2 là giới thiệu về ngôn ngữ đặc tả Z.', true),
('SE109', 'Phát triển, vận hành, bảo trì phần mềm', NULL, 3, 3, 0, 'CN', NULL, 'CNPM', 'Môn học cung cấp cho sinh viên những kiến thức để giải quyết các vấn đề phát sinh trong quá trình bảo trì, thay đổi phần mềm đặc biệt là các dự án lớn, sao cho việc quản lý, thực thi quá trình bảo trì nâng cấp phần mềm được hiệu quả. Môn học cung cấp các khái niệm cơ bản về bảo trì, nâng cấp phần mềm. Các lý thuyết cơ bản cho các kỹ năng cần thiết để quản lý hiệu quả những thay đổi nhằm mục đích nâng cấp phần mềm theo những thay đổi của yêu cầu thực tế.', true),
('SE113', 'Kiểm chứng phần mềm', NULL, 4, 3, 1, 'CSN', NULL, 'CNPM', 'Môn học này trình bày về các kiến thức cơ bản về kiểm chức phần mềm và các kỹ thuật liên quan; và là học phần bắt buộc cho sinh viên công nghệ thông tin trong một học kỳ. Học phần được phân làm 4 phần: phần 1 là các khái niệm liên quan tới kiểm chứng phần mềm; phần 2 là các kĩ thuật kiểm chứng phần mềm; phần 3 là các chiến lược kiểm chứng phần mềm; phần 4 là các vấn đề nâng cao.', true),
('SE114', 'Nhập môn ứng dụng di động', NULL, 3, 2, 1, 'CSN', NULL, 'CNPM', 'Cung cấp cho sinh viên những kiến thức cơ bản về hệ thống nhúng, phần mềm nhúng, công cụ và môi trường phát triển ứng dụng trên các hệ thống nhúng, mạch số. Mục tiêu của môn học là giúp sinh viên tiếp cận việc thiết kế phần mềm cho các ứng dụng nhúng với một bộ vi xử lý đơn lẻ dựa trên các bộ vi điều khiển chuẩn nhỏ. Nâng cao kỹ năng thực thi các thiết kế ứng dụng nhúng sử dụng ngôn ngữ lập trình cấp cao.', true),
('SE115', 'Phát triển game với Unity', NULL, 3, 2, 1, 'CSN', NULL, 'CNPM', NULL, true),
('SE116', 'Phát triển kỹ năng lập trình Game ứng dụng trong thực tế', NULL, 4, 3, 1, 'CSN', NULL, 'CNPM', NULL, true),
('SE117', 'Kỹ thuật lập trình', NULL, 4, 3, 1, 'CSN', NULL, 'CNPM', NULL, true),
('SE214', 'Công nghệ phần mềm chuyên sâu', NULL, 4, 3, 1, 'CN', NULL, 'CNPM', 'Học phần này trình bày các kiến thức chuyên sâu về các phương pháp, qui trình phát triển phần mềm mới, tiên tiến như RUP, Agile, XP, Scrum. Trang bị các kiến thức chuyên sâu về đặc tả và cộng nghệ yêu cầu, cũng như các kiến thức liên quan đến quản lý và triển khai dựa án phần mềm. Môn học giúp sinh viên nắm vững và có khả năng áp dụng các qui trình tiên tiến trong công nghệ phần mềm, có khả năng thiết lập. quản lý, triển khai một dự án phần mềm một cách chuyên nghiệp.', true),
('SE215', 'Giao tiếp người máy', NULL, 4, 3, 1, 'CSN', NULL, 'CNPM', 'Môn học cung cấp cho sinh viên các kiến thức, nguyên lý thiết kế tương tác, các phương pháp làm nguyên mẫu, đánh giá chất lượng giao diện, các nguyên tắc thiết kế nhận thức. Chương 1 giới thiệu các kiến thức tổng quan. Chương 2 đi vào phân tích vai trò, cách thức tương tác. Chương 3 giới thiệu một số quy trình. Chương 4 nói về cách thiết kế tập trung vào vai trò người dùng. Chương 5 là các mẫu thiết kế.', true),
('SE220', 'Thiết kế game', NULL, 4, 3, 1, 'CN', NULL, 'CNPM', 'Môn học giới thiệu cho Sinh viên những kiến thức, kỹ năng cơ bản nhất trong lĩnh vực thiết kế game. Chương 1 cung cấp lý thuyết nền tảng về tâm lý con người, bản chất của game là gì, tại sao game hấp dẫn, diễn biến tâm lý người chơi khi chơi game. Chương 2 cung cấp các gợi mở về kỹ thuật thiết kế game, các bài học lịch sử trong thiết kế game, các tiêu chí thiết kế. Chương 3 tập trung vào thiết kế giao diện game như cách xây dựng menu, bố trí các thành phần giao diện, biểu tượng, thiết kế HUD. Chương 4 bàn về thiết kế cảnh chơi như cách đặt thử thách, xây dựng bối cảnh, tạo hồn cho cảnh chơi...', true),
('SE221', 'Lập trình game nâng cao', NULL, 4, 3, 1, 'CN', NULL, 'CNPM', 'Đây là môn học chuyên ngành nhằm trang bị cho sinh viên những kiến thức và kỹ
năng sau:
• Môn học cung cấp cho sinh viên những kiến thức cơ bản về lập trình game
chơi qua mạng như các kiến trúc game peer-to-peer, client/server, cách thức
xử lý các vấn đề phát sinh trong môi trường mạng như lag, lost package.
• Kết thúc khóa học, sinh viên sẽ có khả năng tự xây dựng những game có sự
tương tác giữa nhiều người chơi trong môi trường mạng.', true),
('SE301', 'Phát triển phần mềm mã nguồn mở', NULL, 3, 3, 0, 'CSN', NULL, 'CNPM', 'Môn học giới thiệu tổng quan về sự phát triển của phần mềm mã nguồn mở, các khái niệm liên quan về bản quyền trong các phần mềm mã nguồn mở. Môn học cũng giới thiệu các phương pháp xây dựng phần mềm mã nguồn mở, ứng dụng SVN để xây dựng phần mềm mã nguồn mở.', true),
('SE310', 'Công nghệ .NET', NULL, 4, 3, 1, 'CN', NULL, 'CNPM', 'Học phần này trình bày các kiến trúc, nền tảng về công nghệ .Net, các kỹ năng và phương pháp lập trình hướng đối tượng trong .Net. Ứng dụng tích hợp việc sử dụng công nghệ (C#) và hệ quản trị CSDL trong việc xây dựng một hệ thống quản lý. Ngoài ra học phần còn cung cấp cho sinh viên các hướng tiếp cận chuyên sâu trong xây dựng các ứng dụng bằng công nghệ .Net.', true),
('SE313', 'Một số thuật toán thông minh', NULL, 2, 2, 0, 'CN', NULL, 'CNPM', 'Môn học trình bày cho sinh viến các kiến thức về thuật toán, và đưa ra các kiến thức về một số thuật toán thông minh hiện nay để giải một số bài toán cơ bản.', true),
('SE314', 'Công nghệ game 3D', NULL, 3, 2, 1, 'CN', NULL, 'CNPM', NULL, true),
('SE315', 'Công nghệ Game Online', NULL, 4, 3, 1, 'CN', NULL, 'CNPM', NULL, true),
('SE316', 'Phát triển Game đa nền tảng', NULL, 3, 2, 1, 'CN', NULL, 'CNPM', NULL, true),
('SE317', 'Công nghệ tiên tiến trong phát triển game', NULL, 3, 2, 1, 'CN', NULL, 'CNPM', NULL, true),
('SE320', 'Lập trình đồ họa 3 chiều với Direct 3D', NULL, 4, 3, 1, 'CN', NULL, 'CNPM', 'Môn học trình bày các kiến thức nền tảng về lập trình ứng dụng đồ họa 3 chiều và hướng dẫn sử dụng bộ thư viện đồ họa tiêu chuẩn của Microsoft là DirectX để xây dựng ứng dụng. Chương trình tổng quan bao gồm 4 chương trong đó: chương 1 trình bày về cơ sở toán học ứng dụng trong đồ họa 3 chiều và quy trình dựng hình 3 chiều, chương 2 và 3 sẽ trình bày về Direct3D bao gồm các vấn đề đi từ cơ bản đến nâng cao, chương 4 sẽ hướng dẫn sinh viên ứng dụng các kiến thức đã học vào xây dựng trò chơi Tetris 3D. Kết thúc khóa học, sinh viên sẽ có khả năng tự thiết kế và lập trình ứng dụng đồ họa 3 chiều đơn giản trên môi trường Windows.', true),
('SE325', 'Chuyên đề J2EE', NULL, 4, 3, 1, 'CN', NULL, 'CNPM', 'Môn học giới thiệu cáckiến thức cơ bản thành phần của J2EE, lập trình web với servlet và JSP, Kiến trúc MVC với Struts, Spring. Sinh viên có thể dùng các kiến thức đã học để có thể phân tích, thiết kế một hệ thống J2EE hoàn chỉnh', true),
('SE327', 'Phát triển và vận hành game', NULL, 4, 3, 1, 'CN', NULL, 'CNPM', 'Môn học cung cấp cho sinh viên những kiến thức về quy trình phát triển và vận
hành một game online theo các thể loại khác nhau từ casual, action, SLG cho đến
MMORPG độc lập hoặc trên nền mạng xã hội.', true),
('SE328', 'Lập trình TTNT trong game', NULL, 4, 3, 1, 'CN', NULL, 'CNPM', 'Việc tạo ra trí tuệ nhân tạo thiết thực là một trong những thử thách lớn nhất trong lập trình game, việc thành công của những game thương mại ngày nay phụ thuộc rất nhiều vào chất lượng của AI. Môn học này trình bày về những kỹ thuật xây dựng những sinh vật nhân tạo có khả năng chuyển vùng đặc biệt, tạo các quyết định chiến thuật dựa trên hành vi đã học được theo các hướng tiếp cận chuyên sâu bắt đầu bằng những thuật toán thường được sử dụng bao gồm thuật toán tìm đường A*, suy luận dựa trên luật hay cây quyết định, hệ thống đối thoại, biểu diễn tri thức. Bên cạnh đó môn học còn giới thiệu về ngôn ngữ lập trình Python, quy trình phát triển toàn diện từ bắt đầu đến kết thúc để hiện thực AI trong game.', true),
('SE330', 'Ngôn ngữ lập trình Java', NULL, 4, 3, 1, 'CN', NULL, 'CNPM', 'Môn học cung cấp các kiến thức cơ bản ngôn ngữ Java, lập trình giao diện với AWT - Abstract Window Toolkit, lập trình đa luồng - Multithreading, lập trình cở sở dữ liệu. Môn học cũng cấp các kiến thức giúp sinh viên làm quen với các công cụ sử dụng trong ngôn ngữ lập trình Java.', true),
('SE331', 'Chuyên đề E-Commerce', NULL, 2, 2, 0, 'CN', NULL, 'CNPM', 'Học phần này trình bày các thức tổng quan về thương mại điện tử, các xu thế phát triển thương mại điện tử hiện tại và trong tương lai, các lĩnh vực ngành nghề phù hợp đặc biệt đối với việc áp dụng thương mại điện tử và giá trị của thương mại điện tử mang lại cho sự phát triển kinh tế, xã hội. Tiếp theo, học phần này sẽ cung cấp các kiến thức về các mô hình thương mại điện tử phù hợp theo từng đối tượng tương tác, các phương thức thanh toán phổ biến được sử dụng trong thương mại điện tử hiện tại và các dịch vụ hỗ trợ thanh toán hiện có trên thị trường và đặc biệt là vấn đề bảo mật trong các giao dịch thương mại điện tử. Tiếp theo, phần trọng tâm của môn học là giới thiệu các công nghệ, kỹ thuật và quy trình phát triển một website thương mại điện tử và các kiến thức, kỹ năng liên quan đến vận hành website thương mại điện tử.', true),
('SE332', 'Chuyên đề CSDL nâng cao', NULL, 2, 2, 0, 'CN', NULL, 'CNPM', 'Môn học cung cấp cho sinh viên những kiến thức bổ sung về cơ sở dữ liệu bao gồm quy trình xây dựng một cơ sở dữ liệu thực tiễn, việc lưu giữ cơ sở dữ liệu trên bộ nhớ ngoài, việc thực hiện và tối ưu các truy vấn, kiểm tra cạnh tranh', true),
('SE334', 'Các phương pháp lập trình', NULL, 3, 2, 1, 'CN', NULL, 'CNPM', 'Học phần này trình bày các kiến trúc, nền tảng về các phương pháp, kỹ thuật lập trình thường dùng khi thiết kế và xây dựng một chương trình máy tính. Sinh viên được tiếp cận với các các phương pháp, kỹ thuật lập trình như: kỹ thuật lập trình đệ qui, kỹ thuật tối ưu mã chương trình, phương pháp lập trình cấu trúc, lập trình hướng đối tượng, lập trình đa nhiệm, song song. Sinh viên được làm quen với các ngôn ngữ lập trình trong các ví dụ minh họa như: ngôn ngữ C++, Java, các thư viện hỗ trợ trong lập trình song song. Học phần cung cấp các kiến thức cơ bản về cách đặt tên biến, hàm, lớp... trong lập trình cũng như kỹ thuật thiết kế kiến trúc và giao diện chương trình. Học phần là sự kết hợp giữa các bài giảng, thuyết trình, tự nghiên cứu tài liệu và báo cáo đồ án kết thúc môn học. Học phần được chia làm 3 phần: phần 1 giới thiệu các kỹ thuật và các nguyên lý cơ bản của lập trình, phần 2 là giới thiệu cụ thể về các phương pháp và kỹ thuật lập trình như: lập trình đệ qui, lập trình cấu trúc, lập trình hướng đối tượng và lập trình song song, phần 3 giới thiệu kỹ thuật thiết kế kiến trúc và giao diện chương trình', true),
('SE343', 'Công nghệ Portal', NULL, 3, 3, 0, 'CN', NULL, 'CNPM', 'Môn học này trình bày về công nghệ Portal, tìm hiểu và phát triển một hệ thống Portal mã nguồn mở (GateIn); và là học phần tự chọn cho sinh viên công nghệ thông tin trong một học kỳ, thích hợp cho sinh viên có hướng phát triển về xây dựng ứng dụng Web. Học phần được phân làm 2 phần chính: phần 1 là các khái niệm liên quan tới Portal, so sánh các hệ thống Portal hiện có trên thế giới; phần 2 tập trung tìm hiểu sâu về hệ thống GateIn và xây dựng ứng dụng trên hệ thống này', true),
('SE344', 'Lập trình game trên các thiết bị di động', NULL, 4, 3, 1, 'CN', NULL, 'CNPM', 'Môn học cung cấp cho sinh viên những kiến thức cần thiết để có thể xây dựng game trên các thiết bị cầm tay như điện thoại di động, PocketPC, … Sau khi hoàn tất môn học, sinh viên sẽ nắm vững những đặc điểm của các thiết bị di động cũng như các giới hạn của loại thiết bị này trong việc thực thi các chương trình Game; sinh viên cũng nắm vững nguyên lý của các bộ công cụ phát triển và phương pháp chuyển đổi một Game từ máy tính sang thiết bị di động.', true),
('SE346', 'Lập trình trên thiết bị di động', NULL, 4, 3, 1, 'CN', NULL, 'CNPM', 'Học phần này trình bày các kiến trúc, nền tảng của thiết bị di động, các kỹ năng và các hướng tiếp cận chuyên sâu trong xây dựng các ứng dụng trên thiết bị di động và là học phần tự chọn cho sinh viên công nghệ thông tin trong một học kỳ. Học phần là việc kết hợp giữa các bài giảng, thuyết trình, bài tập nhỏ tại lớp và thực hiện đồ án môn học vào cuối kỳ. Học phần được phân làm 3 phần chính sau: phần 1 là các chuyên đề lập trình trên nền tảng .Net và Window Phone, phần 2 là các chuyên đề lập trình trên nền tảng Android, và phần 3 là các chủ đề tìm hiểu.', true),
('SE347', 'Công nghệ Web và ứng dụng', NULL, 4, 3, 1, 'CN', NULL, 'CNPM', 'Môn học cung cấp cho sinh viên cả lý thuyết lẫn kiến thức cơ bản về công nghệ Web. Môn học giới thiệu một trong những mô hình ứng dụng lập trình trên web giúp sinh viên xây dựng các ứng dụng trên Web.', true),
('SE350', 'Chuyên đề E-learning', NULL, 2, 2, 0, 'CN', NULL, 'CNPM', 'Môn học này trình bày giới thiệu chung về E-Learning, mô hình và công cụ cho E-Learning. Từ đó, hướng dẫn cách xây dựng và triển khai hệ thống E-Learning. Bên cạnh đó, nội dung liên quan đến quyền sở hữu trí tuệ cũng được đề cập.', true),
('SE352', 'Phát triển ứng dụng VR', NULL, 3, 2, 1, 'CN', NULL, 'CNPM', 'Môn hoc này giúp các sinh viên nắm các khái niệm về ứng dụng VR(Virtual Reality)
và cách xây dụng ứng dụng VR dựa trên Unity 3D. Qua việc học lập trình với VR các
sinh viên có thể phát triển ứng dụng Game, kiến trúc, giả lập..', true),
('SE355', 'Máy học và các công cụ', NULL, 3, 2, 1, 'CN', NULL, 'CNPM', NULL, true),
('SE356', 'Kiến trúc phần mềm', NULL, 4, 3, 1, 'CN', NULL, 'CNPM', 'Giúp sinh viên hiểu các kiến trúc phần mềm hiện có, các đặc điểm của chúng và qua, giúp sinh viên có khả năng chọn lực kiến trúc thích hợp theo yêu cầu đặt ra
Giới thiệu các kiến trúc phần mềm hiện có như kiến trúc phân lớp, thành phần, dựa vào sự kiện, khung thức... Các phương pháp để mô tả, chọn lựa kiến trúc phần mềm thích hợp.', true),
('SE357', 'Kỹ thuật phân tích yêu cầu', NULL, 3, 2, 1, 'CN', NULL, 'CNPM', 'Đây là môn học chuyên ngành Công Nghệ Phần Mềm nhằm trang bị cho sinh viên:
- Kiến thức cơ bản về yêu cầu phần mềm và ảnh hưởng của yêu cầu tới toàn bộ dự án phát triển phần mềm.
- Kỹ thuật khai phá và thu thập yêu cầu phần mềm.
- Quy trình phân tích yêu cầu phần mềm và đánh giá chất lượng yêu cầu.
- Thực hành việc khai thác và thu thập yêu cầu cho dự án công nghệ phần mềm', true),
('SE358', 'Quản lý dự án phát triển phần mềm', NULL, 4, 3, 1, 'CSN', NULL, 'CNPM', NULL, true),
('SE359', 'DevOps trong Phát triển Phần mềm', NULL, 3, 2, 1, 'CSN', NULL, 'CNPM', NULL, true),
('SE360', 'Điện toán đám mây và phát triển ứng dụng hướng dịch vụ', NULL, 4, 3, 1, 'CSN', NULL, 'CNPM', NULL, true),
('SE361', 'Phát triển phần mềm theo kiến trúc Microservices', NULL, 4, 3, 1, 'CSN', NULL, 'CNPM', NULL, true),
('SE362', 'An toàn phần mềm và hệ thống', NULL, 4, 3, 1, 'CN', NULL, 'CNPM', NULL, true),
('SE363', 'Phát triển ứng dụng trên nền tảng dữ liệu lớn', NULL, 4, 3, 1, 'CN', NULL, 'CNPM', NULL, true),
('SE364', 'Thiết kế giao diện và trải nghiệm người dùng', NULL, 4, 3, 1, 'CN', NULL, 'CNPM', NULL, true),
('SE365', 'Học sâu ứng dụng trong phát triển phần mềm', NULL, 4, 3, 1, 'CN', NULL, 'CNPM', NULL, true),
('SE400', 'Seminar các vấn đề hiện đại của Công nghệ Phần mềm', NULL, 4, 4, 0, 'TT', NULL, 'CNPM', 'Môn học có thể cung cấp cho người học cái nhìn tổng quan về các vấn đề hiện đại của
lĩnh vực phát triển phần mềm trong giai đoạn hiện nay. Sinh viên sau khi hoàn thành
môn học có thể:
- Có khả năng tìm hiểu một vấn đề mới
- Có khả năng viết bào cáo, trình bày vấn đê tìm hiểu…', true),
('SE403', 'Nguyên lý thiết kế thế giới ảo', NULL, 4, 4, 0, 'TT', NULL, 'CNPM', 'Môn học này trình bày cho sinh viên các kiến thức và nguyên lý để từ đó thiết kế thế giới ảo trong công nghệ thông tin.', true),
('SE404', 'Chuyên đề E-Government', NULL, 2, 2, 0, 'CN', NULL, 'CNPM', 'Học phần này trình bày về các khái niệm và kiến trúc của Chính phủ điện tử, vai trò và lợi ích của Chính phủ trong việc phát triển kinh tế xã hội. Môn học cung cấp kiến thức về quá trình xây dựng Chính phủ điện tử ở Việt Nam và một số nước trên thế giới cũng như vai trò cốt yếu của công nghệ thông tin nói chung và công nghệ phần mềm nói riêng trong việc xây dựng Chính phủ điện tử.', true),
('SE406', 'Mẫu thiết kế hướng đối tượng', NULL, 4, 4, 0, 'TT', NULL, 'CNPM', NULL, true),
('SE407', 'Chuyên đề Pervasive and Mobile Computing', NULL, 4, 4, 0, 'TT', NULL, 'CNPM', NULL, true),
('SE408', 'Phát triển game với Blockchain', NULL, 4, 3, 1, 'TT', NULL, 'CNPM', NULL, true),
('SE409', 'Phát triển dự án Game', NULL, 4, 3, 1, 'TT', NULL, 'CNPM', NULL, true),
('SE507', 'Đồ án tốt nghiệp', NULL, 6, 6, 0, 'TT', NULL, 'CNPM', NULL, true);

-- INSERT RELATIONSHIPS
INSERT INTO course_relationships (course_id, related_course_id, relation_type) VALUES
((SELECT stt FROM courses WHERE mamh = 'ENG02'), (SELECT stt FROM courses WHERE mamh = 'ENG01'), 'PREREQUISITE'),
((SELECT stt FROM courses WHERE mamh = 'ENG03'), (SELECT stt FROM courses WHERE mamh = 'ENG02'), 'PREREQUISITE'),
((SELECT stt FROM courses WHERE mamh = 'IT002'), (SELECT stt FROM courses WHERE mamh = 'IT001'), 'PREREQUISITE'),
((SELECT stt FROM courses WHERE mamh = 'IT003'), (SELECT stt FROM courses WHERE mamh = 'IT001'), 'PREREQUISITE'),
((SELECT stt FROM courses WHERE mamh = 'IT004'), (SELECT stt FROM courses WHERE mamh = 'IT001'), 'PREREQUISITE'),
((SELECT stt FROM courses WHERE mamh = 'IT005'), (SELECT stt FROM courses WHERE mamh = 'IT001'), 'PREREQUISITE'),
((SELECT stt FROM courses WHERE mamh = 'IT007'), (SELECT stt FROM courses WHERE mamh = 'IT001'), 'PREREQUISITE'),
((SELECT stt FROM courses WHERE mamh = 'IT008'), (SELECT stt FROM courses WHERE mamh = 'IT001'), 'PREREQUISITE'),
((SELECT stt FROM courses WHERE mamh = 'MA005'), (SELECT stt FROM courses WHERE mamh = 'MA006'), 'PREREQUISITE'),
((SELECT stt FROM courses WHERE mamh = 'SE104'), (SELECT stt FROM courses WHERE mamh = 'IT002'), 'PREREQUISITE'),
((SELECT stt FROM courses WHERE mamh = 'SE100'), (SELECT stt FROM courses WHERE mamh = 'SE104'), 'PREREQUISITE'),
((SELECT stt FROM courses WHERE mamh = 'SE503'), (SELECT stt FROM courses WHERE mamh = 'SE104'), 'PREREQUISITE'),
((SELECT stt FROM courses WHERE mamh = 'SE502'), (SELECT stt FROM courses WHERE mamh = 'SE104'), 'PREREQUISITE'),
((SELECT stt FROM courses WHERE mamh = 'SE505'), (SELECT stt FROM courses WHERE mamh = 'SE502'), 'PREREQUISITE'),
((SELECT stt FROM courses WHERE mamh = 'SE506'), (SELECT stt FROM courses WHERE mamh = 'SE502'), 'PREREQUISITE'),
((SELECT stt FROM courses WHERE mamh = 'SE507'), (SELECT stt FROM courses WHERE mamh = 'SE502'), 'PREREQUISITE'),
((SELECT stt FROM courses WHERE mamh = 'SE102'), (SELECT stt FROM courses WHERE mamh = 'SE221'), 'PREREQUISITE'),
((SELECT stt FROM courses WHERE mamh = 'SE114'), (SELECT stt FROM courses WHERE mamh = 'SE346'), 'PREREQUISITE'),
((SELECT stt FROM courses WHERE mamh = 'SE114'), (SELECT stt FROM courses WHERE mamh = 'SE215'), 'PREREQUISITE'),
((SELECT stt FROM courses WHERE mamh = 'SE330'), (SELECT stt FROM courses WHERE mamh = 'SE347'), 'PREREQUISITE'),
((SELECT stt FROM courses WHERE mamh = 'SE332'), (SELECT stt FROM courses WHERE mamh = 'SE343'), 'PREREQUISITE'),
((SELECT stt FROM courses WHERE mamh = 'SE334'), (SELECT stt FROM courses WHERE mamh = 'SE355'), 'PREREQUISITE'),
((SELECT stt FROM courses WHERE mamh = 'SE350'), (SELECT stt FROM courses WHERE mamh = 'SE310'), 'PREREQUISITE'),
((SELECT stt FROM courses WHERE mamh = 'SE346'), (SELECT stt FROM courses WHERE mamh = 'SE313'), 'PREREQUISITE'),
((SELECT stt FROM courses WHERE mamh = 'SE404'), (SELECT stt FROM courses WHERE mamh = 'SE313'), 'PREREQUISITE'),
((SELECT stt FROM courses WHERE mamh = 'SE331'), (SELECT stt FROM courses WHERE mamh = 'SE352'), 'PREREQUISITE'),
((SELECT stt FROM courses WHERE mamh = 'SE109'), (SELECT stt FROM courses WHERE mamh = 'SE356'), 'PREREQUISITE'),
((SELECT stt FROM courses WHERE mamh = 'SE221'), (SELECT stt FROM courses WHERE mamh = 'SE328'), 'PREREQUISITE'),
((SELECT stt FROM courses WHERE mamh = 'SE220'), (SELECT stt FROM courses WHERE mamh = 'SE328'), 'PREREQUISITE'),
((SELECT stt FROM courses WHERE mamh = 'SE320'), (SELECT stt FROM courses WHERE mamh = 'SE328'), 'PREREQUISITE'),
((SELECT stt FROM courses WHERE mamh = 'SE327'), (SELECT stt FROM courses WHERE mamh = 'SE328'), 'PREREQUISITE'),
((SELECT stt FROM courses WHERE mamh = 'SE221'), (SELECT stt FROM courses WHERE mamh = 'SE344'), 'PREREQUISITE'),
((SELECT stt FROM courses WHERE mamh = 'SE220'), (SELECT stt FROM courses WHERE mamh = 'SE344'), 'PREREQUISITE'),
((SELECT stt FROM courses WHERE mamh = 'SE320'), (SELECT stt FROM courses WHERE mamh = 'SE344'), 'PREREQUISITE'),
((SELECT stt FROM courses WHERE mamh = 'SE327'), (SELECT stt FROM courses WHERE mamh = 'SE344'), 'PREREQUISITE');

-- UPDATE ACTUAL ELECTIVE SUBJECTS TO 'TU_CHON'
UPDATE courses 
SET loaimonhoc = 'TU_CHON' 
WHERE mamh IN (
    'SE359', 'SE102', 'SE115', 'SE116', 'SE114', 'SE360', 'SE361', 'SE301', 'SE215', 'SE113', 'SE358', 'SE117',
    'SE330', 'SE332', 'SE334', 'SE347', 'SE350', 'SE343', 'SE355', 'SE310', 'SE346', 'SE404', 'SE331', 'SE313', 'SE352', 
    'SE109', 'SE356', 'SE357', 'SE325', 'SE101', 'SE106', 'SE214', 'SE362', 'SE363', 'SE364', 'SE365', 
    'SE221', 'SE220', 'SE320', 'SE327', 'SE328', 'SE344', 'SE314', 'SE315', 'SE316', 'SE317'
);

-- INSERT PLACEHOLDER NODES INTO THE ROADMAP
INSERT INTO courses (mamh, tenmh, sotc, lt, th, loaimonhoc, semester, management_unit, description, is_open) VALUES
('TC_CSN_4',   'Tự chọn Cơ sở ngành', 4, 3, 1, 'CSN', 4, 'CNPM', 'Sinh viên chọn môn tự chọn trong danh sách Cơ sở ngành.', true),
('TC_CSN_5_1', 'Tự chọn Cơ sở ngành', 4, 3, 1, 'CSN', 5, 'CNPM', 'Sinh viên chọn môn tự chọn trong danh sách Cơ sở ngành.', true),
('TC_CSN_5_2', 'Tự chọn Cơ sở ngành', 4, 3, 1, 'CSN', 5, 'CNPM', 'Sinh viên chọn môn tự chọn trong danh sách Cơ sở ngành.', true),
('TC_CN_6_1',  'Tự chọn Chuyên ngành', 4, 3, 1, 'CN',  6, 'CNPM', 'Sinh viên chọn môn tự chọn trong danh sách Chuyên ngành.', true),
('TC_CN_6_2',  'Tự chọn Chuyên ngành', 4, 3, 1, 'CN',  6, 'CNPM', 'Sinh viên chọn môn tự chọn trong danh sách Chuyên ngành.', true),
('TC_CN_7_1',  'Tự chọn Chuyên ngành', 4, 3, 1, 'CN',  7, 'CNPM', 'Sinh viên chọn môn tự chọn trong danh sách Chuyên ngành.', true),
('TC_CN_7_2',  'Tự chọn Chuyên ngành', 4, 3, 1, 'CN',  7, 'CNPM', 'Sinh viên chọn môn tự chọn trong danh sách Chuyên ngành.', true),
('TC_TD_7',    'Tự chọn tự do',        6, 6, 0, 'ĐC',  7, 'CNPM', 'Sinh viên chọn môn tự chọn tự do.', true)
ON CONFLICT (mamh) DO UPDATE SET 
    tenmh = EXCLUDED.tenmh,
    semester = EXCLUDED.semester,
    loaimonhoc = EXCLUDED.loaimonhoc;
