-- NON-DESTRUCTIVE UPDATE
-- Only truncate relationships as they are derived from courses
TRUNCATE TABLE course_relationships CASCADE;

INSERT INTO courses (mamh, tenmh, tenmh_en, sotc, lt, th, loaimonhoc, semester, equivalent_mh, prerequisite_mh, previous_mh, management_unit, description) VALUES
('MA006', 'Giải tích', 'Calculus', 4, 4, 0, 'ĐC', 1, NULL, NULL, NULL, 'BMTL', 'Môn Giải tích là môn học ở giai đoạn kiến thức đại cương, là môn học bắt buộc đối với tất cả sinh viên. Môn học này giúp cho SV có kiến thức cơ bản về phép tính vi phân hàm nhiều biến; phép tính tích phân hàm nhiều biến (tích phân bội); tích phân đường, tích phân mặt; cũng như là kỹ năng khảo sát chuỗi số, chuỗi hàm, tích phân suy rộng,…cùng với việc nhận dạng và giải quyết một số phương trình vi phân cấp một, cấp cao,…để từ đó SV có thể tiếp tục học tập những môn chuyên ngành, hay phục vụ cho quá trình làm khóa luận tốt nghiệp.'),
('IT002', 'Lập trình hướng đối tượng', 'Object-Oriented Programming', 4, 3, 1, 'CSNN', 2, NULL, NULL, 'IT001', 'CNPM', 'Môn học trang bị cho sinh viên kiến thức và kỹ năng về lập trình hướng đối tượng, các nguyên lý cơ bản của thiết kế hướng đối tượng, các vấn đề căn bản và một số vấn đề nâng cao trong việc cài đặt các lớp và phương thức. Các quan niệm nằm sau cây thừa kế, đa hình, các tính chất của đối tượng, thừa kế và phân lớp. Cách thức trao đổi và truyền thông giữa các đối tượng.'),
('IT004', 'Cơ sở dữ liệu', 'Databases', 4, 3, 1, 'CSNN', 3, NULL, NULL, NULL, 'HTTT', 'Môn học trình bày về sự cần thiết của cơ sở dữ liệu trong doanh nghiệp và trong các loại hình tổ chức khác. Cung cấp sự hiểu biết về nguyên lý của các hệ thống cơ sở dữ liệu, tập trung trên CSDL quan hệ (mô hình dữ liệu quan hệ, các ngôn ngữ truy vấn).
Sinh viên có khả năng sử dụng các kỹ thuật, công cụ để có thể thiết kế, thao tác với
một CSDL quan hệ thông qua hệ quản trị CSDL cụ thể (MS SQL Server), phục vụ cho nhiều môn học nâng cao về CSDL trong những học kỳ kế tiếp.'),
('SE104', 'Nhập môn Công nghệ phần mềm', 'Introduction to Software Engineering', 4, 3, 1, 'CSN', 4, NULL, NULL, 'IT002
IT004', 'CNPM', 'Môn học này nhằm cung cấp cho các sinh viên các kiến thức cơ sở liên quan đến các đối tượng chính yếu trong lĩnh vực công nghệ phần mềm (qui trình công nghệ, phương pháp kỹ thuật thực hiện, phương pháp tổ chức quản lý, công cụ và môi trường triển khai phần mềm, …). Giúp sinh viên hiểu và biết tiến hành xây dựng phần mềm một cách có hệ thống, có phương pháp. Trong quá trình học, sinh viên sẽ được giới thiệu nhiều phương pháp khác nhau để có được góc nhìn tổng quan về các phương pháp. Và để minh họa cụ thể hơn, phương pháp OMT (Object Modeling Technique) được chọn để trình bày (với một sự lược giản để thích hợp với tính chất nhập môn của môn học).'),
('SS009', 'Chủ nghĩa xã hội khoa học', 'Scientific Socialism', 2, 2, 0, 'LLCT', 5, NULL, NULL, NULL, 'PĐTĐH', 'Môn học trang bị cho sinh viên những kiến thức cơ bản về Chủ nghĩa xã hội khoa học'),
('SE502', 'Thực tập', 'Internship', 2, 2, 0, 'TTTN', 6, NULL, NULL, NULL, 'CNPM', NULL),
('SS006', 'Pháp luật đại cương', 'Introduction to Law', 2, 2, 0, 'ĐC', 6, NULL, NULL, NULL, 'P.ĐTĐH', 'Môn học nằm trong khối kiến thức đại cương trong chương trình đào tạo. Môn học hướng đến việc trang bị những kiến thức nền tảng về nhà nước và pháp luật cho người học. Qua đó, người học có những kiến thức cơ bản về nhà nước và pháp luật hướng đến việc hình thành kỹ năng sống và thái độ sống chuẩn mực theo hiến pháp và pháp luật.'),
('ACCT3603', 'Hệ thống thông tin kế toán', 'Accounting Information Systems', 3, 3, 0, 'CN', NULL, NULL, NULL, NULL, 'HTTT', 'Trình bày các kiến thức về công tác kế toán, chu trình nghiệp vụ kế toán, tố chức và xây dựng hệ thống thông tin kế toán, thiết kế và tin học hóa công tác kế toán.'),
('ACCT5123', 'Hoạch định nguồn lực doanh nghiệp', 'Enterprise Resource Planning', 3, 3, 0, 'CSN', NULL, NULL, NULL, NULL, 'HTTT', 'Cung cấp cho sinh viên các kiến thức về một hệ thống quản lý nguồn lực ERP trong một tổ chức và nhiệm vụ đầy thách thức của quản lý HTTT (IS). Các thành phần chính của ERP cũng được giới thiệu trong môn học này. Thông qua đó, sinh viên có thể nhận thấy được tầm quan trọng của ERP, điều kiện để triển khai về nền tảng công nghệ cũng như về qui mô và phạm vi hoạt động của các tổ chức/doanh nghiệp. Sinh viên được thực hành các hệ thống SAP, ECC, Odo. SAP là nhà cung cấp hàng đầu thế giới của các doanh nghiệp phần mềm được thiết kế để tích hợp tất cả các khía cạnh của hoạt động của công ty, giúp các công ty sử dụng công nghệ để duy trì lợi thế cạnh tranh của họ trên thị trường.'),
('ADENG1', 'Tiếng Anh tăng cường 1', 'Intensive English 1', 0, 0, 0, 'ĐC', NULL, 'ENG04', NULL, NULL, 'HTTT', NULL),
('ADENG2', 'Tiếng Anh tăng cường 2', 'Intensive English 2', 0, 0, 0, 'ĐC', NULL, NULL, NULL, NULL, 'HTTT', NULL),
('ADENG3', 'Tiếng Anh tăng cường 3', 'Intensive English 3', 0, 0, 0, 'ĐC', NULL, 'ENG05', 'ADENG2', NULL, 'HTTT', NULL),
('ADENG4', 'Tiếng Anh tăng cường 4', 'Intensive English 4', 0, 0, 0, 'ĐC', NULL, 'ENG03', 'ADENG3', NULL, 'HTTT', NULL),
('AI001', 'Giới thiệu ngành Trí tuệ nhân tạo', 'Introduction to Artificial Intelligence', 1, 1, 0, 'ĐC', NULL, 'CS005
IE005
IS005
SE005
CE005
NT005
NT015
EC005
DS005', NULL, NULL, 'KHMT', NULL),
('AI002', 'Tư duy Trí tuệ nhân tạo', 'Artificial Intelligence Thinking', 4, 3, 1, 'CSN', NULL, NULL, NULL, NULL, 'KHMT', NULL),
('AI301', 'Khởi nghiệp và sáng tạo', 'Entrepreneurship and Innovation', 2, 2, 0, 'CN', NULL, NULL, NULL, NULL, 'KHMT', NULL),
('AI302', 'Kỹ thuật viết báo cáo và trình bày', 'Technical Report Writing and Presentation Skills', 2, 2, 0, 'CN', NULL, NULL, NULL, NULL, 'KHMT', NULL),
('AI503', 'Đồ án tốt nghiệp', 'Capstone Project', 6, 6, 0, 'TN', NULL, NULL, NULL, NULL, 'KHMT', NULL),
('AI504', 'Đồ án tốt nghiệp tại doanh nghiệp', 'Industry Capstone Project', 10, 10, 0, 'TN', NULL, NULL, NULL, NULL, 'KHMT', NULL),
('AI505', 'Khoá luận tốt nghiệp', 'Thesis', 10, 10, 0, 'KLTN', NULL, NULL, NULL, NULL, 'KHMT', NULL),
('BCH058', 'Kỹ năng truyền thông giao tiếp', 'Communications Techniques', 2, 1, 1, 'CN', NULL, NULL, NULL, NULL, 'PĐTĐH', NULL),
('BOQC1', 'Nhập môn máy tính lượng tử', 'Basics of Quantum Computing', 0, 0, 0, 'BT', NULL, NULL, NULL, NULL, 'BMTL', NULL),
('BUS1125', 'Khởi nghiệp kinh doanh', 'Entrepreneurship', 3, 2, 1, 'CN', NULL, NULL, NULL, NULL, 'PĐTĐH', NULL),
('CARC1', 'Kiến trúc máy tính', 'Computer Architecture', 3, 3, 0, 'ĐC', NULL, 'IT006', NULL, NULL, 'KTMT', NULL),
('CE005', 'Giới thiệu ngành Kỹ Thuật Máy tính', 'Introduction to Computer Engineering', 1, 1, 0, 'ĐC', NULL, 'CS005
IE005
IS005
SE005
NT005
NT015
EC005
DS005
AI001', NULL, NULL, 'KTMT', 'Hiều biết rõ về ngành học và định hướng nghề nghiệp là một trong những yếu tố quan trọng giúp sinh viên củng cố đam mê và xây dựng kết hoạch học tập phù hợp. Môn học được thiết kế để cung cấp cho sinh viên cái nhìn tổng quan về ngành Kỹ Thuật Máy Tính trong bối cảnh của sự phát triển mạnh mẽ của ngành Công nghệ thông tin và Truyền thông nói chung, sự bùng nổ của công nghệ IoT nói riêng. Môn học mang đến những thông tin cập nhật, giới thiệu đầy đủ những nhóm ngành, yêu cầu và tương lai phát triển; đồng thời giúp sinh viên nắm bắt được cơ hội, gắn bó với ngành học và xây dựng được kế hoạch học tập đúng đắn.'),
('CE006', 'Giới thiệu ngành Thiết kế vi mạch', 'Introduction to Integrated Circuit Design', 1, 1, 0, 'ĐC', NULL, NULL, NULL, NULL, 'KTMT', NULL),
('CE101', 'Lý thuyết mạch điện', 'Theory of electrical circuits', 4, 4, 0, 'CSN', NULL, 'CE121', NULL, NULL, 'KTMT', NULL),
('CE102', 'Hệ thống số', 'Digital systems', 4, 3, 1, 'CN', NULL, 'PH002', NULL, NULL, 'KTMT', NULL),
('CE103', 'Vi xử lý-vi điều khiển', 'Microprocessors and Microcontrollers', 4, 3, 1, 'CSN', NULL, NULL, NULL, 'IT006', 'KTMT', '▪ Môn học sẽ cung cấp các kiến thức về khái niệm, kiến trúc và nguyên lý hoạt động của một bộ vi xử lý, kiến thức về bộ vi xử lý x86 và các phương thức điều khiển dữ liệu ra vào bộ vi xử lý. Đồng thời giới thiệu về các bộ vi xử lý hiện đại.
▪ Môn học cũng cung cấp các kiến thức một bộ vi điều khiển trong đó bộ vi điều khiển X51 sẽ được nghiên cứu sâu về giao tiếp với thiết bị và lập trình'),
('CE104', 'Các thiết bị và mạch điện tử', 'Electronic devices and circuits', 3, 3, 0, 'CSN', NULL, NULL, NULL, 'PH001
CE101', 'KTMT', NULL),
('CE105', 'Xử lý tín hiệu số', 'Digital Signal Processing', 4, 3, 1, 'CSN', NULL, 'CE205', NULL, 'MA001
MA002
MA003', 'KTMT', 'Môn học Xử lý tín hiệu số nhằm cung cấp các khái niệm và kỹ thuật xử lý tín hiệu hiện đại, nền tảng hệ thống từ quân sự chuyên môn hóa cao đến các ứng dụng công nghiệp điện tử tiêu dùng. Môn học sẽ hướng dẫn sinh viên tìm hiểu chi tiết về:
\- Tín hiệu và hệ thống rời rạc theo thời gian, biểu diễn hệ thống bằng phương trình vi phân, và phân tích hệ thống sử dụng biến đổi Fourier và biến đổi Z.
\- Lý thuyết lấy mẫu tín hiệu liên tục theo thời gian, phân tích các hệ thống tuyến tính bất biến theo thời gian.
\- Biến đổi Fourier rời rạc (DFT) và thuật toán FFT để tính nhanh DFT sẽ được tìm hiểu cùng với các phương pháp phân tích phổ tín hiệu rời rạc theo thời gian.
\- Các phương pháp chính để thiết kế các bộ lọc FIR và IIR.'),
('CE106', 'Thiết kế vi mạch với HDL', 'Digital Circuit Design with HDL', 4, 3, 1, 'CN', NULL, 'CE221', NULL, NULL, 'KTMT', NULL),
('CE107', 'Hệ thống nhúng', 'Embedded systems', 4, 3, 1, 'CSN', NULL, 'CE224', NULL, 'IT001
IT006
CE103', 'KTMT', NULL),
('CE108', 'Hệ điều hành nâng cao', 'Advanced operating systems', 3, 3, 0, 'CN', NULL, NULL, NULL, NULL, 'KTMT', NULL),
('CE109', 'Lập trình nhúng căn bản', 'Introduction to embedded-system programming', 3, 2, 1, 'CN', NULL, 'CE211', NULL, NULL, 'KTMT', NULL),
('CE110', 'Lập trình hệ thống với Java', 'System Programming with Java', 4, 3, 1, 'CN', NULL, 'CE315', NULL, NULL, 'KTMT', NULL),
('CE111', 'Kiến trúc máy tính nâng cao', 'Advanced computer architecture', 3, 2, 1, 'CĐTN', NULL, NULL, NULL, 'IT006', 'KTMT', NULL),
('CE112', 'Đồ án môn học thiết kế mạch', 'Circuit design project', 2, 0, 2, 'ĐA', NULL, 'CE201', NULL, NULL, 'KTMT', NULL),
('CE113', 'Điều khiển tự động', 'Automatic Control', 3, 3, 0, 'CN', NULL, 'CE212', NULL, NULL, 'KTMT', NULL),
('CE114', 'Lập trình trên thiết bị di động', 'Mobile device application development', 3, 2, 1, 'CN', NULL, 'NT118', NULL, NULL, 'KTMT', NULL),
('CE115', 'Thiết kế mạng', 'Network design', 4, 3, 1, 'CN', NULL, 'NT113', NULL, NULL, 'KTMT', NULL),
('CE116', 'Đồ án môn học ngành KTMT', 'Information engineering project', 2, 0, 2, 'ĐA', NULL, 'CE206', NULL, NULL, 'KTMT', NULL),
('CE117', 'Thực hành điện- điện tử', 'Basic electrical and electronic laboratory', 1, 0, 1, 'CSN', NULL, NULL, NULL, 'PH001
CE101', 'KTMT', NULL),
('CE118', 'Thiết kế luận lý số', 'Digital Logic Design', 4, 3, 1, 'CSN', NULL, NULL, NULL, 'PH002', 'KTMT', 'Môn học này trình bày các kiến thức tiếp theo của môn Nhập môn mạch số, bao gồm các nội dung đi sâu hơn và chưa học trong môn học trước. Các nội dung chính bao gồm 4 chương sau:
\- Chương I: Mạch tuần tự
\- Chương II: Các thành phần lưu trữ
\- Chương III: Register transfer design
\- Chương IV: Processor design'),
('CE119', 'Thực hành kiến trúc máy tính', 'Computer Architecture Laboratory', 1, 0, 1, 'CSN', NULL, NULL, NULL, 'PH002', 'KTMT', 'Môn học này cung cấp các bài tập thực hành cho môn Kiến trúc Máy tính bao gồm
▪ Xây dựng một hệ thống máy tính trên FPGA dựa vào lõi xử lý mềm Nios II, Kit DE2 và phần mềm Quartus được hỗ trợ bởi Altera.
▪ Dựa trên hệ thống máy tính xây dựng được, các vấn đề cơ bản về kiến trúc máy tính như: lập trình ngôn ngữ assembly, kỹ thuật xuất nhập, cấu trúc bus,... được đưa vào thực hành'),
('CE121', 'Lý thuyết mạch điện', 'Theory of Electrical Circuits', 4, 3, 1, 'CSN', NULL, 'CE101
CE122
CE125', NULL, NULL, 'KTMT', '▪ Môn học này trình bày các khái niệm cơ bản về mạch điện; các loại mạch điện và phép biến đổi tương đương; mối tương quan giữa dòng áp trên các phần tử mạch điện;
▪ Môn học cũng giới thiệu phương pháp phân tích và giải mạch ở miền tần số, miền thời gian.
▪ Cụ thể sẽ gồm các nội dung sau:
o Các khái niệm cơ bản về mạch điện
o Phân tích mạch ở chế độ xác lập điều hòa
o Các phương pháp phân tích mạch
o Phân tích mạch miền thời gian
o Phân tích mạch miền tần số
o Thực hành phân tích một số mạch điện cơ bản'),
('CE122', 'Phân tích mạch kỹ thuật', 'Engineering Circuit Analysis', 4, 3, 1, 'CN', NULL, 'CE125', NULL, NULL, 'KTMT', NULL),
('CE124', 'Các thiết bị và mạch điện tử', 'Electronic Devices and Circuits', 4, 3, 1, 'CSN', NULL, 'CE104', NULL, 'CE121', 'KTMT', '▪ Môn học này trình bày các khái niệm cơ bản về mạch điện tử; các loại mạch khuếch đại, mạch lọc, mạch so sánh, mạch tạo dao động và các phép biến đổi tương đương mạch;
▪ Môn học cũng giới thiệu các đặc tuyến của từng loại linh kiện, các ảnh hưởng của từng phân tử trong chế độ DC, AC.
▪ Về thực hành, môn học này:
o Giới thiệu cách lắp ghép các linh kiện điện tử với nhau để tạo thành các mạch điện tử cơ bản và mạch điện tử ứng dụng.
o Thực hành thiết kế và mô phỏng mạch điện tử
o Thực hành thiết kế PCB và làm mạch in
o Tìm hiểu các công cụ hỗ trợ về điện – điện tử như VOM, DMM, Oscilloscope'),
('CE125', 'Kỹ thuật phân tích mạch', 'Introduction to Circuit Analysis', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'KTMT', NULL),
('CE126', 'Vật lý bán dẫn và ứng dụng', 'Semiconductor Physics and Applications', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'KTMT', NULL),
('CE201', 'Đồ án 1', 'Project 1', 2, 0, 2, 'ĐA', NULL, NULL, NULL, 'PH002
CE103
CE121', 'KTMT', 'Cung cấp các kỹ năng thiết kế các mạch điện-điện tử hay vi mạch cơ bản cho sinh viên cũng như các kỹ năng phát triển các phần mềm nhúng, phần mềm phát triển vi mạch trên các mạch mà sinh viên thiết kế'),
('CE202', 'An toàn mạng máy tính', 'Network security', 3, 3, 0, 'CN', NULL, 'NT101', NULL, NULL, 'KTMT', NULL),
('CE203', 'Điều khiển tự động nâng cao', 'Advanced automatic control', 3, 3, 0, 'CĐTN', NULL, 'CE317', NULL, NULL, 'KTMT', NULL),
('CE204', 'Thiết kế và lập trình Web', 'Web design and programming', 3, 3, 0, 'CN', NULL, NULL, NULL, NULL, 'KTMT', NULL),
('CE205', 'Xử lý tín hiệu số', 'Digital signal processing', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'KTMT', NULL),
('CE206', 'Đồ án 2', 'Project 2', 2, 0, 2, 'ĐA', NULL, 'CE116', NULL, 'PH002
CE103
CE121', 'KTMT', 'Cung cấp các kỹ năng thiết kế hệ thống các mạch điện-điện tử hay vi mạch tiếp theo môn đồ án 1 cho sinh viên cũng như các kỹ năng phát triển các phần mềm nhúng, phần mềm phát triển vi mạch trên các mạch mà sinh viên thiết kế'),
('CE207', 'Đồ án thiết kế vi mạch 1', 'Project on Integrated Circuit Design 1', 2, 0, 2, 'CN', NULL, NULL, NULL, 'CE226
CE436
CE433', 'KTMT', NULL),
('CE208', 'Đồ án thiết kế vi mạch 2', 'Project on Integrated Circuit Design 2', 2, 0, 2, 'CN', NULL, NULL, NULL, 'CE207', 'KTMT', NULL),
('CE211', 'Lập trình nhúng căn bản', 'Introduction to embedded-system programming', 4, 3, 1, 'CN', NULL, 'CE232', NULL, 'CE107', 'KTMT', NULL),
('CE212', 'Điều khiển tự động', 'Automatic Control', 4, 3, 1, 'CN', NULL, NULL, NULL, 'MA003
MA006', 'KTMT', 'Môn học giới thiệu đến SV các mô hình vật lý, trang bị các kiến thức về đặc tính động học và hướng dẫn thực hành quá trình phân tích, thiết kế, và xét các tính chất ổn định của hệ thống điều khiển tự động cơ bản theo điều kiện yêu cầu cụ thể cho trước'),
('CE213', 'Thiết kế hệ thống số với HDL', 'Digital System Design with HDL', 4, 3, 1, 'CSN', NULL, NULL, NULL, 'PH002
CE118', 'KTMT', 'Giới thiệu các khái niệm tổng quan về thiết kế mạch logic, các phương pháp thiết kế vi mạch fron-end, về ngôn ngữ mô tả phần cứng VHDL&Verilog'),
('CE219', 'Tương tác người - máy', 'Human-computer interaction', 3, 3, 0, 'CN', NULL, 'CE405', NULL, NULL, 'KTMT', NULL),
('CE221', 'Thiết kế vi mạch với HDL', 'Digital circuit design with HDL', 4, 3, 1, 'CN', NULL, 'CE213', NULL, 'PH002
CE118', 'KTMT', NULL),
('CE222', 'Thiết kế vi mạch số', 'Digital Integrated Circuit Design', 4, 3, 1, 'CN', NULL, 'CE302', NULL, 'CE118', 'KTMT', 'Nội dung môn học này cung cấp cho sinh viên những kiến thức cơ bản về thiết kế vi mạch. Cung cấp kiến thức chuyên sâu về công nghệ CMOS, công nghệ chủ đạo trong thiết kế vi mạch ngày nay. Bên cạnh đó, phương pháp phân tích chức năng, định thời, mô hình hóa và tối ưu hóa thiết kế cũng sẽ được trang bị cho sinh viên'),
('CE224', 'Thiết kế hệ thống nhúng', 'Embedded Systems Design', 4, 3, 1, 'CN', NULL, NULL, NULL, 'CE103', 'KTMT', '▪ Giới thiệu các khái niệm chung về hệ thống nhúng, bộ nhớ và ngoại vi.
▪ Giới thiệu các phương pháp tích hợp phát triển phần mềm (chủ yếu) và phần cứng (phần nhỏ) cho các hệ thống nhúng được xây dựng trên một họ vi điều khiển và họ vi xử lý.
▪ Cung cấp kiến thức phân tích và thiết kế một hệ thống nhúng đơn giản, cung câp ngôn ngữ lập trình C/C++ cho hệ thống nhúng.
▪ Sinh viên được thực hành và tự thiết kế một hệ thống nhúng ứng dụng đơn giản trong thực tiễn'),
('CE226', 'Thiết kế VLSI', 'VLSI Design', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'KTMT', NULL),
('CE232', 'Thiết kế hệ thống nhúng không dây', 'Wireless Embedded Systems Design', 4, 3, 1, 'CN', NULL, NULL, NULL, 'CE224', 'KTMT', '▪ Giới thiệu kiến trúc hệ thống nhúng tích hợp ngoại vi. Trong đó giới thiệu kiến trúc của một end node trong hệ thống Internet of Things.
▪ Mô hình tích hợp phần cứng và phần mềm trên hệ thống nhúng tích hợp ngoại vi: bootloader, kernel, OS, driver…
▪ Giới thiệu các phương pháp thiết kế hệ thống nhúng tích hợp ngoại vi.
▪ Cung cấp kiến thức phân tích và thiết kế một hệ thống nhúng phức tạp có tích hợp bộ nhớ, module ngoại vi và module giao tiếp mạng/module giao tiếp không dây.
▪ Sinh viên biết được thực hành và tự thiết kế một hệ thống nhúng ứng dụng trong một giải pháp Internet of Things'),
('CE233', 'Kỹ thuật Robot', 'Robot Principles and Design', 4, 3, 1, 'CN', NULL, NULL, NULL, 'MA003', 'KTMT', NULL),
('CE301', 'Hệ thống chứng thực số', 'Digital identification systems', 3, 3, 0, 'CN', NULL, NULL, NULL, NULL, 'KTMT', NULL),
('CE302', 'Thiết kế vi mạch', 'Circuits design', 3, 2, 1, 'CN', NULL, 'CE222', NULL, NULL, 'KTMT', NULL),
('CE303', 'Robot công nghiệp', 'Industrial robotics', 3, 3, 0, 'CN', NULL, NULL, NULL, 'MA001
MA002
CE101
CE212
CE107', 'KTMT', NULL),
('CE3031', 'Công nghệ cảm biến', 'Sensor Technologies', 4, 3, 1, 'CN', NULL, NULL, NULL, 'MA006
MA003
CE121', 'KTMT', NULL),
('CE304', 'Robot công nghiệp', 'Industrial Robotics', 4, 3, 1, 'CNTC', NULL, NULL, NULL, 'CE121
CE224
MA001
MA002', 'KTMT', 'Môn này giới thiệu cho SV khái niệm thiết kế máy Robot trong các hệ tự động hóa. Sinh viên được trang bị những kiến thức cơ bản về robot công nghiệp; nguyên lý cấu tạo cũng như cấu trúc cơ bản thông thường, phạm vi ứng dụng và sử dụng phổ biến trong thực tế, những yêu cầu cơ bản trong thiết kế và sử dụng robot công nghiệp.'),
('CE306', 'Thị giác máy tính', 'Computer vision', 3, 3, 0, 'CN', NULL, NULL, NULL, NULL, 'KTMT', NULL),
('CE312', 'Hệ thống thời gian thực', 'Real-time systems', 3, 3, 0, 'CN', NULL, NULL, NULL, 'IT006
IT007
CE103
CE211', 'KTMT', NULL),
('CE313', 'Xử lý song song và hệ thống phân tán', 'Parallel Processing and Distributed Systems', 3, 3, 0, 'CN', NULL, NULL, NULL, 'IT001
IT006', 'KTMT', 'Môn học này trình bày các khái niệm và phương pháp xây dựng hệ thống xử lí song song và hệ thống phân bố'),
('CE314', 'Trình biên dịch', 'Principles of compiler design', 3, 3, 0, 'CN', NULL, NULL, NULL, 'CE103
MA004', 'KTMT', NULL),
('CE315', 'Lập trình hệ thống với Java', 'System Programming with Java', 3, 2, 1, 'CN', NULL, NULL, NULL, NULL, 'KTMT', 'Môn học này trình bày các khái niệm cơ bản ngôn ngữ Java như các kiểu dữ liệu, các cấu trúc lặp điều khiển, các khái niệm về hướng đối tượng như đối tượng, thể hiện, lớp, thừa kế, giao diện, đa hình. Các khái niệm về lập trình giao diện như applet, swing GUI. Các khái niệm về lập trình hệ thống như mô hình client-server, socket, TCP, UDP.'),
('CE316', 'Logic mờ và ứng dụng', 'Fuzzy logic and applications', 3, 3, 0, 'CN', NULL, 'CS405', NULL, 'MA004', 'KTMT', NULL),
('CE317', 'Điều khiển tự động nâng cao', 'Advanced Automatic Control', 3, 3, 0, 'CN', NULL, NULL, NULL, 'MA001
MA002
MA003
MA004
CE101
CE104
CE212', 'KTMT', 'Nội dung của môn học đề cập đến các phương pháp thiết kế bộ điều khiển cho hệ thống tự động nhằm đảm bảo độ dự trữ , tính ổn định bền vững khi vận hành và chất lượng khi tối ưu hóa thiết kế của hệ thống trong điều kiện ràng buộc của chế độ làm việc được đặt ra.'),
('CE318', 'Trình biên dịch', 'Compiler', 4, 3, 1, 'CNTC', NULL, 'CE314', NULL, 'IT006
IT007', 'KTMT', 'Môn học Trình biên dịch bao gồm các nghiên cứu về các nguyên lý hoạt động của trình biên dịch, các kỹ thuật được sử dụng để thiết kế một trình biên dịch và các công cụ như Lex, Yacc làm thuận tiện việc cài đặt một trình biên dịch.'),
('CE319', 'Logic mờ và ứng dụng', 'Fuzzy Logic and Its Applications', 4, 3, 1, 'CNTC', NULL, NULL, NULL, 'MA004', 'KTMT', 'Môn học này trình bày các khái niệm về tập mờ, logic mờ. Đạo hàm và phương trình vi phân mờ. Bài toán tối ưu hóa mờ. Hệ chuyên gia mờ và hệ trợ giúp quyết định mờ. Phương pháp điều khiển mờ'),
('CE320', 'Logic mờ cho ứng dụng hệ thống nhúng', 'Fuzzy Logic for Embedded Systems Applications', 4, 3, 1, 'CN', NULL, NULL, NULL, 'MA004', 'KTMT', NULL),
('CE321', 'Kỹ thuật chế tạo vi mạch', 'Integrated circuit fabrication', 3, 3, 0, 'CN', NULL, NULL, NULL, 'CE222', 'KTMT', NULL),
('CE322', 'Thiết kế vi mạch hỗn hợp', 'Mixed-signal integrated circuit design', 3, 2, 1, 'CN', NULL, NULL, NULL, 'CE222', 'KTMT', NULL),
('CE323', 'Kỹ thuật thiết kế mạch in', 'Printed Circuit Board Design', 3, 2, 1, 'CN', NULL, NULL, NULL, 'CE101
CE104
CE103', 'KTMT', 'Phương pháp thiết kế schematic, netlist.
\- Phương pháp đặt thiết bị và kết nối ( Placing and routing ) cho những mạch điện tử đơn giản.
\- Phương pháp đặt thiết bị và kết nối ( Placing and routing ) cho những mạch điện tử phức tạp.
\- Một số Phương pháp thiết kế mạch in trong thương mại.
\- thiết kế mạch in.
\- Lập kế hoạch cho Một thiết kế.
\- Thực hành thiết kế mạch in cho mạch điên tử cụ thể.'),
('CE324', 'Thiết kế vi mạch tương tự', 'Analog integrated circuit design', 3, 2, 1, 'CN', NULL, NULL, NULL, 'CE104
CE221', 'KTMT', NULL),
('CE325', 'Thiết kế dựa trên vi xử lý', 'Microprocessor-based system design', 3, 2, 1, 'CN', NULL, 'CE335', NULL, 'IT001
IT006
IT003', 'KTMT', NULL),
('CE326', 'Tự động hóa thiết kế vi mạch', 'Circuit-design automation', 3, 2, 1, 'CN', NULL, NULL, NULL, 'CE221', 'KTMT', NULL),
('CE327', 'Tối ưu hóa dựa trên FPGA', 'FPGA-based optimization', 3, 3, 0, 'CN', NULL, 'CE337', NULL, 'CE118
IT006
CE221', 'KTMT', NULL),
('CE331', 'Kỹ thuật chế tạo vi mạch', 'Integrated Circuit Fabrication', 4, 3, 1, 'CNTC', NULL, 'CE321', NULL, 'CE222', 'KTMT', 'Nội dung môn học này cung cấp cho sinh viên những kiến thức cơ bản về lịch sử phát triển ngành công nghiệp chế tạo vi mạch. Nội dung môn học trang bị kiến thức về qui trình sản xuất Wafer, qui trình chế tạo công nghệ bán dẫn cũng như các phương pháp kiểm tra và đóng gói chip.'),
('CE332', 'Thiết kế vi mạch hỗn hợp', 'Mixed-Signal Integrated Circuit Design', 4, 3, 1, 'CNTC', NULL, 'CE322', NULL, 'CE222', 'KTMT', 'Môn học này bao gồm những nội dung sau
▪ Trình bày những kiến thức cơ bản về thiết kế vi mạch hỗn hợp:
o Cấu trúc cơ bản của một hệ thống vi mạch hỗn hợp.
o Các thành phần cơ bản của hệ thống: Opamps, D/A converters, S/H circuit, Analog Switches, Comparator, PLL, ...
▪ Những cách thức/phương pháp để phân tích, thiết kế, mô phỏng và layout các thành phần hoặc các mạch hỗn hợp ở mức CMOS.
▪ Các bài thực hành bám sát nội dung lý thuyết để giúp cho sinh viên hiểu rõ hơn lý thuyết và có cái nhìn thực tế về thiết kế vi mạch hỗn hợp.'),
('CE333', 'Tiếng Anh chuyên ngành Kỹ thuật máy tính', 'Technical English for Computer Engineering', 4, 4, 0, 'CN', NULL, NULL, NULL, 'ENG03
IT007', 'KTMT', NULL),
('CE334', 'Thiết kế vi mạch tương tự', 'Analog Integrated Circuit Design', 4, 3, 1, 'CNTC', NULL, 'CE324', NULL, 'CE124
CE213', 'KTMT', 'Môn học này bao gồm những nội dung sau
▪ Trình bày những kiến thức cơ bản về thiết kế vi mạch tương tự.
▪ Những cách thức/phương pháp để phân tích, thiết kế, mô phỏng và layout các thành phần hoặc các mạch tương tự ở mức CMOS.
▪ Các bài thực hành bám sát nội dung lý thuyết để giúp cho sinh viên hiểu rõ hơn lý thuyết và có cái nhìn thực tế về thiết kế vi mạch tương tự'),
('CE335', 'Thiết kế dựa trên vi xử lý', 'Microprocessor-Based Design', 4, 3, 1, 'CNTC', NULL, 'CE325', NULL, 'IT006
IT003
IT001', 'KTMT', 'Nội dung môn học có thể chia làm 2 phần chính. (1) ôn lại các thành phần cơ bản của 1 hệ vi xử lý (vi mạch vi xử lý, vi mạch bộ nhớ, vi mạch I/O); sau đó mở rộng giới thiệu về nguyên lý hoạt động cùng với mạch giao tiếp của phím và nút nhấn, của các bộ cảm biến và chuyển đổi A/D D/A, các bộ điều khiển động cơ, các thiết bị thông tin liên lạc, các thiết bị hiển thị, máy in ; (2) qui trình thiết kế và biến đổi một giải thuật vận hành, điều khiển ứng với một thiết bị cụ thể thành chương trình chạy trên các hệ vi xử lý; tương quan về độ phức tạp thiết kế giữa phần cứng và phần mềm.'),
('CE336', 'Tự động hóa thiết kế vi mạch', 'Electronic Design Automation', 4, 3, 1, 'CNTC', NULL, 'CE326', NULL, 'CE118
CE213', 'KTMT', 'Tự động hóa thiết kế vi mạch là môn học chuyên ngành trong hướng nghiên cứu vi mạch. Nó cung cấp các kiến thức nền tảng trong việc nghiên cứu chuyên sâu về thiết kế vi mạch'),
('CE337', 'Tối ưu hóa dựa trên FPGA', 'FPGA-based Optimization', 4, 3, 1, 'CNTC', NULL, 'CE327', NULL, 'IT006
CE118
CE213', 'KTMT', 'Môn học này giới thiệu những ứng dụng dùng thiết bị có khả năng tái lập cấu hình. Chủ đề được chia thành ba phần chính: kiến trúc, phương pháp thiết kế và ứng dụng. Phần kiến trúc bao gồm những giới thiệu về thiết bị lập trình tái cấu hình, đặc biệt là thiết bị thương mại hóa cao Field Programmable Gate Arrays (FPGAs), và các hệ thống có khả năng tái cấu hình. Phần phương pháp thiết kế bao gồm cách thiết kế cho FPGAs với sự trợ giúp của máy tính, biên dịch từ ngôn ngữ lập trình, thiết kế mức hệ thống: chẳng hạn như phân hoạch, kết nối, và tái cầu hình từng phần. Trong phần cuối cùng, những ứng dụng chính yếu của kỹ thuật tái cấu hình được thảo luận, từ ứng dụng công nghệ tin sinh học, bảo mật mạng cho đến các vấn đề về xử lý tín hiệu số và xử lý ảnh'),
('CE338', 'Hệ thống thời gian thực', 'Real-Time Systems', 4, 3, 1, 'CNTC', NULL, NULL, NULL, 'IT006
IT007', 'KTMT', 'Cung cấp những kiến thức cơ bản về hệ thống số bao gồm:
\- Giới thiệu hệ thống thời gian thực
\- Định thời trong hệ thời gian thực
\- Thiết kế hệ thời gian thực'),
('CE339', 'Công nghệ IoT và ứng dụng', 'IoT Technology and Applications', 4, 3, 1, 'CN', NULL, NULL, NULL, 'CE103
CE224', 'KTMT', NULL),
('CE340', 'Trí tuệ nhân tạo cho hệ thống nhúng', 'Artificial Intelligence for Embedded Systems', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'KTMT', NULL),
('CE341', 'Lập trình nhúng trên thiết bị di động', 'Embedded Programming on Mobile Devices', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'KTMT', 'Sinh viên được cung cấp các kiến thức liên quan đến lập trình nhúng trên các thiết bị di động nhằm giải quyết các vấn đề liên quan đến công suất tiêu thụ, tối ưu bộ nhớ xử lý, tiết kiệm bộ nhớ lưu trữ, thiết lập kết nối truyền thông và bảo mật thông tin. Môn học này cung cấp cho sinh viên kỹ năng phân tích, thiết kế và hiện thực được các ứng dụng trên các thiết bị di động nhỏ gọn mà áp dụng kỹ thuật lập trình nhúng. Các nội dung bao gồm
▪ Giới thiệu về tính toán di động khắp mọi nơi, tính toán cảm ngữ cảnh.
▪ Các phương pháp lập trình nhúng nâng cao: đa luồng, đa hành vi, kết nối cơ sở dữ liệu nhúng
▪ Phân tích và Đánh giá được các giải thuật nhúng trên các thiết bị di động liên quan đến các yêu cầu thực tiễn của ứng dụng về: công suất tiêu thụ, tối ưu bộ nhớ xử lý, tiết kiệm bộ nhớ lưu trữ, thiết lập kết nối truyền thông và bảo mật thông tin
▪ Thực hành thiết kế một ứng dụng nhúng trên một thiết bị di động sử dụng các lõi phần cứng như: ARM, DSP, Tiny SoC...'),
('CE342', 'Hệ thống thông minh', 'Smart Systems', 4, 3, 1, 'CN', NULL, NULL, NULL, 'CE224', 'KTMT', NULL),
('CE343', 'Trí tuệ nhân tạo cho xe tự hành', 'Artificial Intelligence for Autonomous Vehicles', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'KTMT', NULL),
('CE344', 'Trí tuệ nhân tạo cho IoT', 'Artificial Intelligence for IoT', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'KTMT', NULL),
('CE345', 'Kiến trúc IoT: giao thức mạng và bảo mật', 'IoT Architecture: Network Protocols and Security', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'KTMT', NULL),
('CE346', 'Thiết kế Ăng-ten tích hợp cho thiết bị IoT', 'Antenna Design for IoT Devices', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'KTMT', NULL),
('CE347', 'Điều khiển thông minh cho robot', 'Intelligent Control in Robotics', 4, 3, 1, 'CN', NULL, NULL, NULL, 'MA003', 'KTMT', NULL),
('CE348', 'Công nghệ cảm biến trong IoT', 'Sensor Technologies for IoT', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'KTMT', NULL),
('CE349', 'Hệ thống nhúng trên SoC', 'Embedded Systems Design on SoC', 4, 3, 1, 'CN', NULL, NULL, NULL, 'CE103', 'KTMT', NULL),
('CE350', 'Xử lý ảnh hướng ASIC', 'Image Processing with ASIC', 4, 3, 1, 'CN', NULL, NULL, NULL, 'CE436', 'KTMT', NULL),
('CE351', 'Thiết kế bộ tăng tốc phần cứng', 'Hardware Accelerator Design', 4, 3, 1, 'CN', NULL, NULL, NULL, 'CE433', 'KTMT', NULL),
('CE352', 'Xử lý tín hiệu số trên FPGA', 'Digital Signal Processing on FPGA', 4, 3, 1, 'CN', NULL, NULL, NULL, 'CE213
CE436', 'KTMT', NULL),
('CE353', 'Thiết kế vật lý vi mạch', 'Physical Design', 4, 3, 1, 'CN', NULL, NULL, NULL, 'CE226', 'KTMT', NULL),
('CE401', 'Kỹ thuật hệ thống máy tính', 'Computer system engineering', 3, 3, 0, 'CĐTN', NULL, NULL, NULL, 'IT006
IT007', 'KTMT', NULL),
('CE402', 'Các hệ điều hành nhúng', 'Embedded operating systems', 4, 3, 1, 'CĐTN', NULL, NULL, NULL, 'IT007
IT006
CE107', 'KTMT', NULL),
('CE403', 'Thiết kế số', 'Digital design', 4, 3, 1, 'CĐTN', NULL, NULL, NULL, 'PH002
IT006
CE221', 'KTMT', NULL),
('CE404', 'Kỹ thuật chế tạo vi mạch', 'Integrated Circuit Fabrication', 3, 3, 0, 'CĐTN', NULL, 'CE321', NULL, NULL, 'KTMT', NULL),
('CE405', 'Tương tác người-máy', 'Human-Computer Interaction', 3, 3, 0, 'CĐTN', NULL, NULL, NULL, NULL, 'KTMT', NULL),
('CE406', 'Tương tác người-máy', 'Human-Computer Interaction', 4, 3, 1, 'CNTC', NULL, NULL, NULL, 'CE103', 'KTMT', '▪ Cung cấp các định nghĩa về HCI, các đối tượng tham gia giao tiếp và các vấn đề liên quan.
▪ Giới thiệu các kỹ thuật giao tiếp truyền thống: giao tiếp dòng lệnh, menu, văn bản, các kỹ thuật hiện đại, giao tiếp đồ họa GUI, giao tiếp trực tiếp WIMP.
▪ Trình bày các chuẩn và các mô hình dùng trong thiết kế và các phương pháp thiết kế.
▪ Giới thiệu các kỹ thuật đánh giá giao tiếp người dùng sử dụng trong quá trình thiết kế cũng như đánh giá sản phẩm'),
('CE407', 'Đồ án chuyên ngành hệ thống nhúng và robot', 'Major Project on Embedded Systems and Robot Design', 2, 0, 2, 'CĐTN', NULL, 'CE412', NULL, 'IT006', 'KTMT', 'Môn học này dành cho sinh viên học chuyên đề tốt nghiệp. Đồ án này cung cấp các kỹ năng về thiết kế hệ thống nhúng và robot. Sinh viên hoàn thành môn học này nắm bắt được:
\- Kiến thức tổng hợp hoặc của chuyên ngành hệ thống nhúng và robot.
\- Kỹ năng tìm hiểu, nghiên cứu và giải quyết bài toán của ngành hệ thống nhúng, ngành
Kỹ thuật máy tính và các kỹ năng về thuyết trình, giao tiếp và làm việc nhóm.
\- Thái độ làm việc tích cực trong ngành hệ thống nhúng.'),
('CE408', 'Đồ án chuyên ngành thiết kế vi mạch và phần cứng', 'Major Project on Integrated Circuit and Computer Hardware Design', 2, 0, 2, 'CĐTN', NULL, NULL, NULL, 'CE201
CE206
CE213', 'KTMT', 'Môn học này dành cho sinh viên học chuyên đề tốt nghiệp. Đồ án này cung cấp các kỹ năng về thiết kế vi mạch và phần cứng máy tính, ngành Kỹ thuật máy tính. Sinh viên hoàn thành môn học này nắm bắt được
▪ Kiến thức tổng hợp hoặc của chuyên ngành thiết kế vi mạch và phần cứng, ngành kỹ thuật máy tính.
▪ Kỹ năng tìm hiểu, nghiên cứu và giải quyết bài toán của ngành thiết kế vi mạch, ngành KTMT và các kỹ năng về thuyết trình, giao tiếp và làm việc nhóm.
▪ Thái độ làm việc tích cực trong ngành KTMT'),
('CE409', 'Kỹ thuật thiết kế kiểm tra', 'Design Verification', 4, 3, 1, 'CN', NULL, NULL, NULL, 'CE118
CE213', 'KTMT', 'Môn học này cung cấp cho sinh viên các kiến thức liên quan đến quy trình thiết kế và phát triển vi mạch số từ ý tưởng đến thiết kế, hiện thực và kiểm thử trên FPGA, trên SoC theo hướng ASIC. Sinh viên hiểu và nắm được các kiến thức và kỹ năng sau
▪ Kiến thức về các module ngoại vi, bộ xử lý…
▪ Kiến thức về thiết kế mạch kiểm tra tích hợp bằng ngôn ngữ phần cứng hoặc ngôn ngữ cấp cao (C/C++) để kiểm tra hoạt động của vi mạch ngoại vi, vi mạch xử lý…
▪ Có kỹ năng phân tích, đánh giá và kiểm tra mẫu thiết kế trên chip FPGA
▪ Có kỹ năng phân tích và đánh giá các chức năng của các vi mạch thiết kế dựa trên kết quả mô phỏng'),
('CE410', 'Kỹ thuật hệ thống máy tính', 'Computer System Engineering', 4, 3, 1, 'CĐTN', NULL, NULL, NULL, 'CE118
CE224', 'KTMT', 'Môn học này trình những kiến thức cơ bản về kỹ thuật hệ thống máy tính:
\- Các thành phần cơ bản của hệ thống máy tính
\- Phân tích, thiết kế một hệ thống máy tính hoàn chỉnh
\- Xây dựng quy trình kiểm thử, bảo trì, và quản lý dự ánh cho một hệ thống máy tính
\- Hiện thực một hệ thống máy tính hoàn chỉnh'),
('CE411', 'Chuyên đề hệ thống nhúng và robot', 'Special Topics on Embedded Systems and Robotics', 4, 3, 1, 'CĐTN', NULL, NULL, NULL, 'CE107', 'KTMT', 'Mục tiêu chính là cung cấp cho sinh viên khả năng phân tích thiết kế một hệ thống
nhúng hay một robot cụ thể nào đó. Từ các lý thuyết liên quan đến các board nhúng, các robot cần sử dụng. Các qui trình và cách hiện thực để làm ra các sản phẩm nhúng thực tế gồm cả phần cứng lẫn phần mềm hoặc nhúng các phần mềm cần thiết vào một board mạch, một robot hay một hệ thống có sẵn'),
('CE412', 'Đồ án chuyên ngành hệ thống nhúng và IoT', 'Major Project on Embedded System and IoT Design', 2, 0, 2, 'CN', NULL, NULL, NULL, 'CE224', 'KTMT', NULL),
('CE413', 'Đồ án chuyên ngành Robotics và AI', 'Major Project on Robotics and AI', 2, 0, 2, 'CN', NULL, NULL, NULL, 'CE103
CE224', 'KTMT', NULL),
('CE421', 'Chuyên đề thiết kế vi mạch và phần cứng', 'Special Topics on Integrated Circuit and Computer Hardware Designs', 4, 3, 1, 'CĐTN', NULL, NULL, NULL, 'CE118
CE221
CE222', 'KTMT', 'Môn học này trình bày:
\- Giới thiệu toàn bộ các giai đoạn để thiết kế một lõi IP hay một chip xử lý theo hướng
FPGA hoặc ASIC.
\- Đưa ra một ví dụ thiết kế cụ thể, trình bày cách tiến hành từng giai đoạn thiết kế.
\- Dùng phần mềm chuyên dụng để thực hiện thiết kế trên trong từng giai đoạn ở trên giúp sinh viên có cái nhìn thực tế qui trình thiết kế trong một công ty.'),
('CE430', 'Lập trình hệ thống', 'System programming', 4, 3, 1, 'CNTC', NULL, NULL, NULL, NULL, 'KTMT', '▪ Môn học này trình bày các khái niệm cơ bản ngôn ngữ Java như các kiểu dữ liệu, các cấu trúc lặp điều khiển, các khái niệm về hướng đối tượng như đối tượng, thể hiện, lớp, thừa kế, giao diện, đa hình. Các khái niệm về lập trình giao diện như applet, swing GUI. Các khái niệm về lập trình hệ thống như mô hình client-server, socket, TCP, UDP.
▪ Tìm hiểu và thực hành lập trình cơ bản, lập trình hướng đối tượng, lập trình giao diện, lập trình hệ thống qua các bài toán cụ thể.'),
('CE432', 'Thiết kế vi mạch hướng ASIC', 'ASIC Design', 4, 3, 1, 'CNTC', NULL, NULL, NULL, 'CE118
CE213', 'KTMT', 'Môn học bao gồm các nội dung sau:
\- Trình bày tổng quan về luồng thiết kế vi mạch hướng ASIC đang được sử dụng trong ngành
công nghiệp vi mạch thế giới hiện nay.
\- Cung cấp các kiến thức nền tảng thực hiện các khâu chính trong luồng thiết kế.
\- Phân tích quy trình thực hiện Front-End thiết kế.
\- Phân tích quy trình thực hiện Back-End thiết kế.'),
('CE433', 'Thiết kế hệ thống SoC', 'SoC Design', 4, 3, 1, 'CNTC', NULL, NULL, NULL, 'CE118
IT006', 'KTMT', 'Môn học này giới thiệu về phương pháp thiết kế SoC. Cung cấp cho sinh viên quy trình thiết kế SoC và những kiến thức cơ bản về các vi mạch SoC'),
('CE434', 'Chuyên đề thiết kế hệ vi mạch 1', 'Special Topics on Integrated Circuit Design 1', 4, 3, 1, 'CNTC', NULL, NULL, NULL, 'CE213', 'KTMT', 'Mục tiêu môn học này nhằm kết hợp với Doanh nghiệp trong đào tạo công nghệ mới đáp ứng yêu cầu thị trường lao động. Do đó, nội dung chi tiết môn học sẽ được ban hành cụ thể tùy theo từng học kỳ đào tạo và có định dạng giống theo đề cương môn học này.
Tuy nhiên nội dung chính dự kiến bao gồm:
\- Giới thiệu toàn bộ các giai đoạn để thiết kế một lõi IP hay một chip xử lý theo
hướng FPGA hoặc ASIC.
\- Đưa ra một ví dụ thiết kế cụ thể, trình bày cách tiến hành từng giai đoạn thiết kế
\- Dùng các phần mềm chuyên dụng để hiện thực thiết kế trên trong từng giai đoạn ở trên giúp sinh viên có cái nhìn thực tế qui trình thiết kế trong một công ty.'),
('CE435', 'Chuyên đề thiết kế hệ vi mạch 2', 'Special Topics on Integrated Circuit Design 2', 4, 3, 1, 'CNTC', NULL, NULL, NULL, 'CE213', 'KTMT', 'Mục tiêu môn học này nhằm kết hợp với Doanh nghiệp trong đào tạo công nghệ mới đáp ứng yêu cầu thị trường lao động. Do đó, nội dung chi tiết môn học sẽ được ban hành cụ thể tùy theo từng học kỳ đào tạo và có định dạng giống theo đề cương môn học này.
Tuy nhiên nội dung chính dự kiến bao gồm:
\- Giới thiệu và cập nhật các công nghệ vi mạch có tính thời đại.
\- Giới thiệu công nghệ thiết kế, hiện thực và kiểm tra một lõi IP hoặc một IC theo
định hướng VLSI
\- Sinh viên cần hiện thực một lõi IP, hoặc một IC ứng dụng công nghệ mới nhằm
nắm bắt qui trình thiết kế, hiện thực và kiểm tra vi mạch thiết kế như tại doanh
nghiệp.'),
('CE436', 'Xử lý tín hiệu số và ứng dụng', 'Digital Signal Processing and Applications', 4, 3, 1, 'CNTC', NULL, NULL, NULL, 'MA003
MA006', 'KTMT', 'Môn học Xử lý tín hiệu số nhằm cung cấp các khái niệm và kỹ thuật xử lý tín hiệu hiện đại, nền tảng hệ thống từ quân sự chuyên môn hóa cao đến các ứng dụng công nghiệp điện tử tiêu dùng. Môn học sẽ hướng dẫn sinh viên tìm hiểu chi tiết về
▪ Tín hiệu và hệ thống rời rạc theo thời gian, biểu diễn hệ thống bằng phương trình vi phân, và phân tích hệ thống sử dụng biến đổi Fourier và biến đổi Z.
▪ Lý thuyết lấy mẫu tín hiệu liên tục theo thời gian, phân tích các hệ thống tuyến tính bất biến theo thời gian.
▪ Biến đổi Fourier rời rạc (DFT) và thuật toán FFT để tính nhanh DFT sẽ được tìm hiểu cùng với các phương pháp phân tích phổ tín hiệu rời rạc theo thời gian.
▪ Các phương pháp chính để thiết kế các bộ lọc FIR và IIR.
▪ Ứng dụng thiết kế các mạch xử lý tín hiệu, thiết kế các thuật toán xử lý tín hiệu trên hệ thống SoC, hệ thống nhúng hoặc hệ thống mô phỏng Matlab. Sinh viên có thể làm đồ án môn học ứng dụng tuỳ thuộc vào chuyên ngành mong muốn như: thiết kế vi mạch hoặc thiết kế hệ thống nhúng'),
('CE437', 'Chuyên đề thiết kế hệ thống nhúng 1', 'Special Topics on Embedded Systems 1', 4, 3, 1, 'CNTC', NULL, NULL, NULL, 'CE103', 'KTMT', 'Nội dung chi tiết môn học này sẽ được ban hành cụ thể tùy theo nhu cầu của thị trường lao động, nhưng nội dung chính có thể bao gồm
▪ Giới thiệu toàn bộ các giai đoạn để thiết kế, hiện thực một hệ thống nhúng cả trên phương diện phần cứng lẫn phần mềm.
▪ Đưa ra một ví dụ thiết kế cụ thể, trình bày cách tiến hành từng giai đoạn thiết kế
▪ Dùng các công cụ hoặc phần mềm chuyên dụng để hiện thực thiết kế trên trong từng giai đoạn ở trên -> giúp sinh viên có cái nhìn thực tế qui trình thiết kế, cài đặt và hiện thực một hệ thống nhúng như thế nào trong một công ty'),
('CE438', 'Chuyên đề thiết kế hệ thống nhúng 2', 'Special Topics on Embedded Systems 2', 4, 3, 1, 'CNTC', NULL, NULL, NULL, 'CE103', 'KTMT', 'Mục tiêu môn học này nhằm kết hợp với Doanh nghiệp trong đào tạo công nghệ mới đáp ứng yêu cầu thị trường lao động. Do đó, nội dung chi tiết môn học sẽ được ban hành cụ thể tùy theo từng học kỳ đào tạo. Tuy nhiên nội dung chính dự kiến bao gồm
▪ Giới thiệu công nghệ mới trong thiết kế, hiện thực và kiểm tra một hệ thống nhúng từ phần cứng đến phần mềm.
▪ Cung cấp các kiến thức về quy trình thiết kế hoặc phương pháp hiện thực, phương pháp đánh giá hiệu suất đối với hệ thống nhúng có tính mới, có tính cập nhật
▪ Sinh viên cần hiện thực một ứng dụng có tính thực tiễn nhằm nắm bắt qui trình thiết kế, cài đặt và hiện thực một hệ thống nhúng cho các ứng dụng tại doanh nghiệp'),
('CE439', 'Lập trình song song và hệ thống phân tán', 'Parallel Programming and Distributed Systems', 4, 3, 1, 'CNTC', NULL, NULL, NULL, 'IT001
IT006', 'KTMT', 'Môn học này trình bày các khái niệm và phương pháp xây dựng hệ thống xử lí song song và hệ thống phân bố
Giới thiệu về các mô hình, kiến trúc máy tính song song, tổ chức các bộ xử lí. Giới thiệu cách tính speed up, các mô hình lập trình song song. Tìm hiểu một số giải thuật song song cơ bản trên các bài toán cụ thể: nhân ma trận. Giới thiệu và thực hành xây dựng một số chương trình song song trên thư viện MPI và OpenMP.
Giới thiệu hệ thống phân tán, cách truyền thông trong hệ thống phân tán. Tìm hiểu một số hệ thống phân tán thông dụng như: hệ thống file phân tán, dịch vụ web, hệ thống mạng ngang hàng. Tìm hiểu về thời gian và trạng thái toàn cục của hệ thống phân tán. Tìm hiểu giải thuật phân tán trên các bài toán cụ thể: leader election, đường đi ngắn nhất.'),
('CE440', 'Hệ thống định vị với ứng dụng Artificial Intelligence', 'Indoor and Outdoor Localization Systems with Artificial Intelligence Applications', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'KTMT', NULL),
('CE441', 'Chuyên đề thiết kế Robotics và Artificial Intelligence 1', 'Special Topics on Robotics and Artificial Intelligence 1', 4, 3, 1, 'CN', NULL, NULL, NULL, 'CE103
CE224', 'KTMT', NULL),
('CE442', 'Chuyên đề thiết kế Robotics và Artificial Intelligence 2', 'Special Topics on Robotics and Artificial Intelligence 2', 4, 3, 1, 'CN', NULL, NULL, NULL, 'CE103
CE224', 'KTMT', NULL),
('CE501', 'Thực tập doanh nghiệp', 'Internship', 3, 0, 3, 'TTTN', NULL, NULL, NULL, NULL, 'KTMT', NULL),
('CE502', 'Thực tập doanh nghiệp', 'Internship', 2, 2, 0, 'TTTN', NULL, 'CE501', NULL, NULL, 'KTMT', 'Cung cấp cho sinh viên các kỹ năng thực hiện giải quyết các bài toán thực tế hoặc tham gia nghiên cứu, thiết kế hoặc thực hiện các ứng dụng cụ thể từ doanh nghiệp nhằm phát triển các kỹ năng giao tiếp, làm việc nhóm, thuyết trình trong môi trường thực tế tại các doanh nghiệp. Bên cạnh đó, môn học này giúp cho sinh viên có thái độ tích cực trong ngành Kỹ thuật máy tính và định hướng khả năng phát triển chuyên môn trong tương lai'),
('CE505', 'Khóa luận tốt nghiệp', 'Thesis', 10, 10, 0, 'KLTN', NULL, NULL, NULL, 'CE206', 'KTMT', 'Môn học này giành cho sinh viên làm Khoá luận tốt nghiệp. Khoá luận này cung cấp các kỹ năng về thiết kế hệ thống nhúng và IoT hoặc thiết kế vi mạch và phần cứng máy tính. Sinh viên hoàn thành môn học này nắm bắt được
▪ Kiến thức tổng hợp hoặc của chuyên ngành hệ thống nhúng và IoT hoặc chuyên ngành phần cứng và thiết kế vi mạch.
▪ Kỹ năng tìm hiểu, nghiên cứu và giải quyết bài toán của ngành KTMT và các kỹ năng về thuyết trình, giao tiếp và làm việc nhóm.
▪ Thái độ làm việc tích cực trong ngành KTMT'),
('CE506', 'Luận văn chuyên sâu đặc thù', 'Thesis', 14, 0, 14, 'CN', NULL, NULL, NULL, NULL, 'KTMT', NULL),
('CE507', 'Đồ án tốt nghiệp tại doanh nghiệp', 'Industry Capstone Project', 10, 10, 0, 'TN', NULL, NULL, NULL, NULL, 'KTMT', NULL),
('CE508', 'Đồ án tốt nghiệp', 'Capstone Project', 6, 0, 6, 'TN', NULL, NULL, NULL, NULL, 'KTMT', NULL),
('CE510', 'Chuyên đề tốt nghiệp định hướng hệ thống nhúng và IoT', 'Special Project on Embedded Systems and IoT', 4, 3, 1, 'CĐTN', NULL, NULL, NULL, NULL, 'KTMT', NULL),
('CE511', 'Chuyên đề tốt nghiệp định hướng Robotic và trí tuệ nhân tạo', 'Special Project on Robotics and Artificial Intelligence', 4, 3, 1, 'CĐTN', NULL, NULL, NULL, NULL, 'KTMT', NULL),
('CE512', 'Chuyên đề tốt nghiệp định hướng thiết kế vi mạch', 'Special Project on Integrated Circuit Design', 4, 3, 1, 'TN', NULL, NULL, NULL, NULL, 'KTMT', NULL),
('CM101', 'Quản lý giao tiếp', 'Communication management', 3, 3, 0, 'ĐC', NULL, NULL, NULL, NULL, 'HTTT', NULL),
('CNBU001', 'Mạng máy tính', 'Networking', 4, 3, 1, 'CN', NULL, 'CSBU105', NULL, NULL, 'MMT&TT', NULL),
('CNBU002', 'Bảo mật', 'Security', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'MMT&TT', NULL),
('CNBU003', 'Dự án nghiên cứu', 'Computing Research Project', 8, 4, 4, 'CN', NULL, 'CSBU009', NULL, NULL, 'MMT&TT', NULL),
('CNBU004', 'Thiết kế và phát triển website', 'Website Design and Development', 4, 3, 1, 'CN', NULL, 'CSBU103', NULL, NULL, 'MMT&TT', NULL),
('CNBU005', 'Internet of Things', 'Internet of Things', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'MMT&TT', NULL),
('CNBU006', 'An toàn mạng máy tính', 'Network Security', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'MMT&TT', NULL),
('CNBU007', 'Pháp chứng kỹ thuật số', 'Forensics', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'MMT&TT', NULL),
('CNBU008', 'Quản lý an toàn thông tin', 'Information Security Management', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'MMT&TT', NULL),
('CNBU009', 'Thực tập', 'Internship', 0, 0, 0, 'TTTN', NULL, NULL, NULL, NULL, 'MMT&TT', NULL),
('CNBU101', 'Toán cho tin học', 'Maths for Computing', 65, 40, 25, 'CN', NULL, NULL, NULL, NULL, 'MMT&TT', NULL),
('CNBU102', 'Công nghệ mạng máy tính', 'Networking Technologies', 65, 40, 25, 'CN', NULL, NULL, NULL, NULL, 'MMT&TT', NULL),
('CNBU103', 'Lập trình cho kỹ sư mạng máy tính', 'Programming for Network Engineers', 65, 40, 25, 'CN', NULL, NULL, NULL, NULL, 'MMT&TT', NULL),
('CNBU104', 'Hệ thống Servers', 'Server Systems', 65, 40, 25, 'CN', NULL, NULL, NULL, NULL, 'MMT&TT', NULL),
('CNBU105', 'Hệ thống mạng doanh nghiệp', 'Enterprise Network Systems', 65, 40, 25, 'CN', NULL, NULL, NULL, NULL, 'MMT&TT', NULL),
('CNBU106', 'Hoạt động an ninh mạng', 'Cyber Security Operations', 65, 40, 25, 'CN', NULL, NULL, NULL, NULL, 'MMT&TT', NULL),
('CNBU107', 'Dự án chuyên ngành', 'Enterprise Practice Project', 65, 40, 25, 'CN', NULL, NULL, NULL, NULL, 'MMT&TT', NULL),
('CNBU108', 'Hệ điều hành', 'Applied Operating Systems', 69, 44, 25, 'CN', NULL, NULL, NULL, NULL, 'MMT&TT', NULL),
('CNBU201', 'Công nghệ mạng không dây', 'Wireless Networking Technologies', 65, 40, 25, 'CN', NULL, NULL, NULL, NULL, 'MMT', NULL),
('CNBU202', 'Hệ thống tường lửa nâng cao', 'Advanced Firewall Systems', 65, 40, 25, 'CN', NULL, NULL, NULL, NULL, 'MMT', NULL),
('CNBU203', 'An toàn mạng máy tính', 'Network Security', 65, 40, 25, 'CN', NULL, NULL, NULL, NULL, 'MMT', NULL),
('CNBU204', 'Ethical Hacking', 'Ethical Hacking', 65, 40, 25, 'CN', NULL, NULL, NULL, NULL, 'MMT', NULL),
('CNBU205', 'Dự án cá nhân', 'Individual Honours Project', 65, 40, 25, 'CN', NULL, 'CSBU205', NULL, NULL, 'MMT', NULL),
('CNET1', 'Mạng máy tính', 'Computer networks', 4, 3, 1, 'ĐC', NULL, 'IT005', NULL, NULL, 'MMT&TT', NULL),
('CS003', 'Máy học nâng cao', 'Advanced machine learning', 3, 3, 0, 'CN', NULL, 'CS315', NULL, NULL, 'KHMT', NULL),
('CS004', 'Máy học trong xử lý ngôn ngữ tự nhiên', 'Machine learning in NLP', 3, 3, 0, 'CN', NULL, NULL, NULL, NULL, 'KHMT', NULL),
('CS005', 'Giới thiệu ngành Khoa học Máy tính', 'Introduction to Computer Science', 1, 1, 0, 'ĐC', NULL, 'IE005
IS005
SE005
CE005
NT005
NT015
EC005
DS005
AI001', NULL, NULL, 'KHMT', 'Môn học cung cấp các kiến thức giới thiệu về ngành CNTT nói chung và Khoa học Máy tính. Trong đó cung cấp cho sinh viên thông tin và kiến thức nền tảng về ngành học, chương trình đào tạo, chuẩn đầu ra, cơ hội nghề nghiệp, mối liên hệ với các ngành khác…'),
('CS010', 'Các công cụ của trí tuệ nhân tạo', 'Các công cụ của trí tuệ nhân tạo', 3, 3, 0, 'CHUA_PHAN_LOAI', NULL, NULL, NULL, NULL, 'Chưa rõ', NULL),
('CS013', 'Máy học nâng cao', 'Advanced machine learning', 4, 3, 1, 'CN', NULL, 'CS315', NULL, NULL, 'KHMT', NULL),
('CS014', 'Máy học trong xử lý ngôn ngữ tự nhiên', 'Machine learning in NLP', 4, 3, 1, 'CN', NULL, 'CS324', NULL, NULL, 'KHMT', NULL),
('CS019', 'Chuyên đề ứng dụng Trí tuệ nhân tạo', 'Applied artificial intelligence', 4, 4, 0, 'CN', NULL, NULL, NULL, NULL, 'KHMT', NULL),
('CS101', 'Nguyên lý và phương pháp lập trình', 'Principles and Techniques of Programming', 3, 3, 0, 'CN', NULL, 'CS111', NULL, NULL, 'KHMT', NULL),
('CS102', 'Phân tích & thiết kế thuật toán', 'Design and analysis of algorithms', 3, 3, 0, 'CN', NULL, 'CS112', NULL, NULL, 'KHMT', NULL),
('CS103', 'Cơ sở lập trình', 'Introduction to programming', 4, 4, 0, 'CN', NULL, 'CS111
CS511
SE330', NULL, NULL, 'KHMT', NULL),
('CS104', 'Nhập môn công nghệ phần mềm', 'Software engineering', 3, 3, 0, 'CN', NULL, 'SE104', NULL, NULL, 'KHMT', NULL),
('CS105', 'Đồ họa máy tính', 'Computer Graphics', 4, 3, 1, 'CSN', NULL, 'CS113', NULL, NULL, 'KHMT', 'Môn học này giới thiệu các nội dung căn bản trong lĩnh vực đồ hoạ máy tính bao gồm các kiến thức cơ bản về đồ hoạ 2D và 3D. Cụ thể là các nội dung vẽ hình, biến đổi, phép chiếu, cách hiển thị hình ảnh trên máy tính.'),
('CS106', 'Trí tuệ nhân tạo', 'Artificial Intelligence', 4, 3, 1, 'CSN', NULL, NULL, NULL, 'IT003', 'KHMT', 'Môn học cung cấp một số kiến thức cơ bản của khoa học Trí tuệ nhân tạo. Nội dung chính gồm: Lịch sử hình thành và phát triển của Trí tuệ nhân tạo, các hướng nghiên cứu và ứng dụng của Trí tuệ nhân tạo trong đời sống; Các phương pháp giải quyết vấn đề và các áp dụng, đặc biệt nhấn mạnh thuật giải heuristic và các chiến lược tìm kiếm; Một số phương pháp biểu diễn tri thức cơ bản và kỹ thuật suy diễn tự động.'),
('CS107', 'Các hệ cơ sở tri thức', 'Knowledge-Based Systems', 4, 4, 0, 'CN', NULL, 'CS217', NULL, NULL, 'KHMT', NULL),
('CS108', 'Lý thuyết thông tin', 'Information theory', 3, 3, 0, 'CN', NULL, 'NT104', NULL, NULL, 'KHMT', NULL),
('CS109', 'Máy học', 'Machine learning', 4, 4, 0, 'CN', NULL, 'CS110', NULL, NULL, 'KHMT', NULL),
('CS110', 'Nhập môn công nghệ tri thức và máy học', 'Introduction to Knowledge Engineering and Machine Learning', 4, 3, 1, 'CSN', NULL, 'CS114', NULL, NULL, 'KHMT', 'Môn học cung cấp kiến thức cơ bản về công nghệ tri thức và máy học. Nội dung chính bao gồm: Khái niệm và quy trình của công nghệ tri thức; Các ứng dụng của công nghệ tri thức; Khái niệm về máy học và một số vấn đề, phương pháp và kỹ thuật cơ bản trong máy học.'),
('CS111', 'Nguyên lý và phương pháp lập trình', 'Principles and Techniques of Programming', 4, 3, 1, 'CSN', NULL, NULL, NULL, 'IT003', 'KHMT', 'Môn học cung cấp cho sinh viên những kiến thức nền tảng về ngôn ngữ lập trình; nguyên lý, phương pháp và kỹ thuật xây dựng nên các ngôn ngữ lập trình, dòng ngôn ngữ lập trình dưới góc độ người thiết kế ngôn ngữ lập trình. Đồng thời cung cấp kiến thức giúp sinh viên hiểu rõ các cơ chế hoạt động, xử lý của các thành phần cấu thành nên một ngôn ngữ lập trình cụ thể, các mô thức lập trình và vấn đề chọn lựa mô thức lập trình phù hợp để giải quyết một cách hiệu quả các bài toán trên máy tính.'),
('CS1113', 'Khoa học máy tính 1', 'Computer Science 1', 4, 3, 1, 'ĐC', NULL, 'CS2134', NULL, NULL, 'HTTT', 'Giới thiệu về khoa học máy tính dùng ngôn ngữ máy tính cấp cao có cấu trúc khối…, bao gồm các chương trình con, mảng, bản ghi và các loại dữ liệu trừu tượng. Nguyên lý giải quyết các vấn đề, thực hành lập trình, khai báo biến, kiểu dữ liệu… Các phương pháp tìm kiếm và sắp xếp cơ bản. Sử dụng các lệnh và công cụ của hệ điều hành.'),
('CS112', 'Phân tích và thiết kế thuật toán', 'Design and Analysis of Algorithms', 4, 3, 1, 'CSN', NULL, NULL, NULL, 'IT001
IT003', 'KHMT', 'Môn học cung cấp một số kiến thức trong việc thiết kế các thuật toán và đánh giá độ phức tạp của chúng. Nội dung chính gồm:
o Tổng quan về thuật toán và độ phức tạp của thuật toán.
o Trình bày các cơ sở toán học cho việc đánh giá độ phức tạp của thuật toán và sử dụng các kiến thức toán sơ cấp để đánh giá thuật toán.
o Sử dụng hàm sinh, định lý Master trong việc đánh giá độ phức tạp các thuật toán
o Nhóm hoán vị và ứng dụng.
o Trình bày một số vấn đề mở rộng và nâng cao.'),
('CS113', 'Đồ họa máy tính và xử lý ảnh', 'Computer Graphics and Digital Image Processing', 4, 3, 1, 'CSN', NULL, 'CS105', NULL, 'IT001
IT003', 'KHMT', 'Môn học trang bị cho sinh viên kiến thức liên quan đến hiển thị hình ảnh trên máy tính như: quy trình hiển thị, các thuật toán vẽ những hình cơ bản, các phương pháp mô hình hóa đối tượng 3D, các phép biến đổi, kỹ thuật xén hình, kỹ thuật dựng hình, phối cảnh, kỹ thuật về chiếu sáng, kĩ thuật làm animation cơ bản, các phép biến đổi trên ảnh và các phương pháp xử lý ảnh.'),
('CS114', 'Máy học', 'Machine Learning', 4, 3, 1, 'CN', NULL, 'DS102', NULL, 'IT001
MA003
MA004', 'KHMT', 'Môn học cung cấp một số kiến thức cơ bản về Máy học. Nội dung chính bao gồm:
o Giới thiệu tổng quan về máy học với các nội dung như máy học là gì, các khái niệm cơ bản, lịch sử hình thành và phát triển của Máy học, các hướng nghiên cứu và ứng dụng của Máy học trong đời sống, giới thiệu một số công cụ, công nghệ và các thách thức hiện nay;
o Những cách khác nhau dùng máy học để giải quyết vấn đề, một số bài toán tiêu biểu trong máy học như hồi quy, phân lớp, gom cụm cùng với các phương pháp cơ bản để giải quyết;
o Áp dụng các phương pháp và kỹ thuật máy học cơ bản để giải quyết một số bài toán trong thực tế.'),
('CS115', 'Toán cho khoa học máy tính', 'Mathematics for Computer Science', 4, 4, 0, 'CN', NULL, NULL, NULL, 'IT001', 'KHMT', 'Môn học cung cấp kiến thức Toán ứng dụng trong các lĩnh vực máy học, trí tuệ nhân tạo, khai phá dữ liệu và xử lý tín hiệu số.'),
('CS116', 'Lập trình Python cho máy học', 'Python for Machine Learning', 4, 3, 1, 'CN', NULL, NULL, NULL, 'IT001
IT002', 'KHMT', 'Môn học cung cấp cho sinh viên kiến thức và kĩ năng lập trình bằng ngôn ngữ Python, khai thác các công cụ, thư viện, nền tảng tính toán hiện đại dựa trên Python, nhằm phát triển và ứng dụng các phương pháp máy học (machine learning) một cách hiệu quả.'),
('CS117', 'Tư duy tính toán', 'Computational Thinking', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'KHMT', 'Môn học cung cấp kiến thức nền tảng và kĩ năng vận dụng cơ bản các phương pháp tư duy, suy luận logic trong giải quyết bài toán, bao gồm: tư duy thuật toán (algorithmic thinking), tư duy phân rã (decomposition), tư duy khái quát hóa (generalization), tư duy trừu tượng (abstraction), và tư duy đánh giá định lượng (evaluation).'),
('CS124', 'Nhập môn công nghệ phần mềm', 'Nhập môn công nghệ phần mềm', 3, 3, 0, 'CHUA_PHAN_LOAI', NULL, NULL, NULL, NULL, 'Chưa rõ', NULL),
('CS210', 'Xử lý ngôn ngữ tự nhiên nâng cao', 'Advanced natural language processing', 4, 4, 0, 'CN', NULL, NULL, NULL, NULL, 'KHMT', NULL),
('CS211', 'Trí tuệ nhân tạo nâng cao', 'Advanced Artificial Intelligence', 4, 3, 1, 'CN', NULL, NULL, NULL, 'CS106', 'KHMT', 'Môn học cung cấp một số kiến thức cơ bản của khoa học trí tuệ nhân tạo. Nội dung chính gồm:
o Phương pháp tiếp cận hiện đại cho việc biểu diễn tri thức sử dụng ontology.
o Tìm hiểu tổng quan về khái niệm tác tử và hệ thống đa tác tử
o Thiết kế thuật giải di truyền và các thuật toán trong mạng neural.
o Áp dụng xây dựng các ứng dụng thực tế.'),
('CS212', 'Xử lý ngôn ngữ tự nhiên', 'Natural Language Processing', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'KHMT', NULL),
('CS213', 'Ngôn ngữ học máy tính', 'Computational linguistics', 4, 4, 0, 'CN', NULL, 'CS226', NULL, NULL, 'Chưa rõ', NULL),
('CS2133', 'Khoa học máy tính 2', 'Computer Science 2', 4, 3, 1, 'ĐC', NULL, 'CS2134', NULL, 'CS1113', 'HTTT', 'Giới thiệu các thuật toán đệ quy, các phương pháp tìm kiếm và sắp xếp thông dụng, tổ chức chương trình, lập trình bộ nhớ động, danh sách liên kết đơn, kế thừa, đa hình, xử lý ngoại lệ, stack, queue, vectors, cây... Phân tích toán học về độ phức tạp không gian và thời gian, tình huống xấu nhất, và tình huống trung bình'),
('CS2134', 'Khoa học máy tính', 'Computer Science', 4, 3, 1, 'ĐC', NULL, NULL, NULL, NULL, 'HTTT', NULL),
('CS214', 'Biểu diễn tri thức và suy luận', 'Knowledge Representation and Reasoning', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'KHMT', 'Cung cấp cho sinh viên các phương pháp biểu diễn tri thức và suy luận rộng hơn và sâu hơn, trong đó đặc biệt là phương pháp tiếp cận BDTT dựa trên các ontology, từ đó có thể tiếp cận để giải quyết các vấn đề thực tế. Vận dụng các phương pháp kĩ thuật trong một số các ứng dụng cụ thể.'),
('CS217', 'Các hệ cơ sở tri thức', 'Knowledge-Based Systems', 4, 3, 1, 'CN', NULL, NULL, NULL, 'IT002
IT003', 'KHMT', 'Môn học cung cấp kiến thức về hệ cơ sở tri thức và hệ chuyên gia, phương pháp thiết kế và các ứng dụng. Nội dung chính gồm:
o Khái niệm, cấu trúc và quy trình xây dựng hệ thống.
o Phương pháp thiết kế các thành phần trung tâm của hệ thống là cơ sở tri thức và bộ suy diễn cùng các kỹ thuật liên quan khác.
o Thiết kế và xây ứng dụng cụ thể.'),
('CS221', 'Xử lý ngôn ngữ tự nhiên', 'Natural Language Processing', 4, 3, 1, 'CN', NULL, NULL, NULL, 'IT001
MA003
MA004', 'KHMT', 'Môn học nhằm cung cấp cho sinh viên một số kiến thức nhập môn của chuyên ngành xử lý ngôn ngữ tự nhiên, bao gồm những nội dung chính về: văn phạm phi ngữ cảnh CFG (Context-Free Grammar), văn phạm DCG (Definite Clause Grammar), cài đặt và giải thích cơ chế xử lý văn phạm DCG trên Prolog, FSA (Finite State Automata). Trên cơ sở những kiến thức nền tảng này sinh viên có thể học tiếp môn chuyên ngành tự chọn “Xử lý ngôn ngữ tự nhiên nâng cao”.'),
('CS222', 'Xử lý ngôn ngữ tự nhiên nâng cao', 'Advanced Natural Language Processing', 4, 3, 1, 'CN', NULL, NULL, NULL, 'CS221', 'KHMT', 'Nội dung môn học bao gồm những nội dung chính: Probabilistic Context-Free Grammar, Unification-Based Grammar, Lexicalized Probabilistic Context-Free Grammar,...
\- Đối với hệ Cử nhân Tài năng: Sinh viên được tăng cường các bài tập lý thuyết và bài tập thực hành nâng cao về các phương pháp và kỹ thuật xử lý cú pháp trên nhiều mô hình lý thuyết ngữ pháp.'),
('CS223', 'Máy học nâng cao', 'Advanced machine learning', 4, 3, 1, 'CN', NULL, 'CS315', NULL, NULL, 'KHMT', NULL),
('CS224', 'Máy học xử lý ngôn ngữ tự nhiên', 'Machine learning in NLP', 4, 3, 1, 'CN', NULL, 'CS324', NULL, NULL, 'KHMT', NULL),
('CS225', 'Lập trình symbolic trong trí tuệ nhân tạo', 'Symbolic Programming in Artificial Intelligence', 4, 4, 0, 'CN', NULL, 'CS314', NULL, NULL, 'KHMT', NULL),
('CS226', 'Ngôn ngữ học máy tính', 'Computational Linguistics', 4, 4, 0, 'CN', NULL, NULL, NULL, NULL, 'KHMT', 'Môn học nhằm mục tiêu giảng dạy cho sinh viên những kiến thức nền tảng trong lĩnh vực Ngôn ngữ học máy tính, bao gồm các các mô hình và phương pháp xử lý văn phạm hình thức, các chiến lược và thuật toán phân tích cú pháp trên máy tính.
\- Đối với hệ Cử nhân Tài năng: Sinh viên được tăng cường các bài tập lý thuyết và bài tập thực hành nâng cao của môn Ngôn ngữ học máy tính.'),
('CS227', 'Khai thác dữ liệu và ứng dụng', 'Data Mining and Applications', 4, 3, 1, 'CN', NULL, 'CS313', NULL, NULL, 'KHMT', NULL),
('CS228', 'Máy học và ứng dụng', 'Machine learning and applications', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'KHMT', NULL),
('CS229', 'Ngữ nghĩa học tính toán', 'Computational Semantics', 4, 3, 1, 'CN', NULL, NULL, NULL, 'CS221', 'KHMT', 'Môn học nhằm mục tiêu cung cấp cho sinh viên những kiến thức và kỹ năng trong việc sử dụng các phương pháp, kỹ thuật phân tích và tính toán ngữ nghĩa. Nội dung môn học tập trung vào vấn đề xử lý ngữ nghĩa của các câu và văn bản.
\- Đối với hệ Cử nhân Tài năng: Sinh viên được tăng cường các bài tập lý thuyết và bài tập thực hành về các phương pháp và kỹ thuật nâng cao trong xử lý ngữ nghĩa...'),
('CS231', 'Nhập môn thị giác máy tính', 'Introduction to Computer Vision', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'KHMT', 'Môn học này giới thiệu các nội dung căn bản trong ngành Thị giác máy tính, bao gồm các chủ đề về low-level computer vision và mid-level computer vision. Các chủ đề cụ thể gồm: rút trích và khai thác thông tin trên ảnh, các loại đặc trưng thị giác cấp thấp và phương pháp biểu diễn đặc trưng thị giác cấp thấp, các kĩ thuật so khớp ảnh, các kĩ thuật phân đoạn ảnh, phương pháp theo vết (tracking).'),
('CS232', 'Tính toán đa phương tiện', 'Introduction to Multimedia Computing', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'KHMT', 'Môn học này cung cấp kiến thức nền tảng về tính toán, xử lý dữ liệu đa phương tiện (hình ảnh, video, âm thanh) và các ứng dụng, công nghệ đa phương tiện. Các chủ đề chính bao gồm: media characteristics, multimedia representation, data formats, compression, multimedia technology, multimedia computing applications.'),
('CS233', 'Nhận dạng Thị giác', 'Visual Pattern Recognition', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'KHMT', NULL),
('CS2433', 'Lập trình C/C++', 'C/C++ Programming', 4, 3, 1, 'CSN', NULL, 'MSIS2433', NULL, NULL, 'HTTT', 'This course presents basic programming concepts using the C++ programming language. Standard I/O classes are emphasized. Structured and object oriented programming techniques are presented and used to design and implement a variety of programming problems.'),
('CS301', 'Chuyên đề nghiên cứu khoa học', 'Scientific Research Seminar', 4, 4, 0, 'CN', NULL, NULL, NULL, NULL, 'KHMT', NULL),
('CS302', 'Seminar', 'Seminar', 3, 3, 0, 'CN', NULL, NULL, NULL, NULL, 'KHMT', NULL),
('CS311', 'Kỹ thuật lập trình trí tuệ nhân tạo', 'Artificial Intelligence Programming Techniques', 4, 3, 1, 'CN', NULL, NULL, NULL, 'IT003', 'KHMT', NULL),
('CS312', 'Hệ thống đa tác tử', 'Multi-Agent Systems', 4, 3, 1, 'CN', NULL, NULL, NULL, 'CS110', 'KHMT', 'Môn học cung cấp một số kiến thức về Công nghệ đa tác tử. Nội dung chính gồm:
o Các khái niệm về tác tử và hệ thống đa tác tử.
o Các hoạt động trong hệ thống đa tác tử.
o Công nghệ về hệ thống đa tác tử - JADE.
o Ứng dụng của tác tử trong một số lĩnh vực.'),
('CS313', 'Khai thác dữ liệu và ứng dụng', 'Data Mining and Applications', 4, 3, 1, 'CN', NULL, NULL, NULL, 'IT001
IT003', 'KHMT', 'Học phần cung cấp cho sinh viên các kiến thức và kỹ thuật khai thác dữ liệu để rút trích các tri thức quí báu từ các kho dữ liệu. Mối quan hệ giữa tri thức rút trích và tiến trình ra quyết định, hoạch địch chính sách sẽ được thảo luận với nhiều ứng dụng thực tế. Trong học phần này, sinh viên sẽ tìm hiểu các chủ đề: vai trò của khai thác dữ liệu, chuẩn bị dữ liệu, dự đoán/mô tả dữ liệu và ứng dụng đi kèm, các vấn đề đang được quan tâm giải quyết.'),
('CS314', 'Lập trình symbolic trong trí tuệ nhân tạo', 'Symbolic Programming in Artificial Intelligence', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'KHMT', 'Môn học này giới thiệu về lập trình tính toán hình thức thông qua ngôn ngữ lập trình Maple; Giới thiệu về các lệnh thường dùng các kiểu cấu trúc dữ liệu trong Maple; Ứng dụng lập trình tính toán trong việc trong việc thiết kế các thuật giải heuristic, xây dựng mạng tính toán trong Trí tuệ nhân tạo.'),
('CS315', 'Máy học nâng cao', 'Advanced Machine Learning', 4, 3, 1, 'CN', NULL, 'CS223', NULL, 'CS110', 'KHMT', 'Môn học cung cấp một số kiến thức nâng cao của kiến thức về Máy học. Nội dung chính gồm: Giới thiệu một số thuật toán máy học nâng cao hiện đang được nghiên cứu và sử dụng như: thuật toán SVM (Support Vector Machine), PageRank (Weight PageRank, LpageRank, …), mô hình Markov ẩn.'),
('CS316', 'Các hệ giải bài toán thông minh', 'Intelligent Problem-Solving Systems', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'KHMT', 'Trình bày về tiêu chuẩn và cấu trúc của hệ giải bài toán thông minh như là một lớp hệ chuyên gia, phương pháp và kỹ thuật dùng thiết kế hệ giải bài toán thông minh. Khảo sát những vấn đề kĩ thuật đặc thù của hệ IPS. Vận dụng lý thuyết để xây dựng các hệ IPS thực tế. Môn học cũng giới thiệu một số ứng dụng cụ thể.'),
('CS317', 'Phát triển và vận hành hệ thống máy học', 'Machine Learning Operations', 4, 3, 1, 'CN', NULL, NULL, NULL, 'CS114', 'KHMT', NULL),
('CS321', 'Ngôn ngữ học ngữ liệu', 'Corpus Linguistics', 4, 3, 1, 'CN', NULL, NULL, NULL, 'IT001', 'KHMT', 'Ngữ liệu là tập hợp dữ liệu ngôn ngữ được lưu trữ trên máy tính nhằm phục vụ cho các nghiên cứ về xử lý ngôn ngữ tự nhiên trên máy tính. Mục tiêu của ngôn ngữ học ngữ liệu là giới thiệu các khía cạnh nghiên cứu của ngữ liệu nhằm phục vụ cho các bài toán trong xử lý ngôn ngữ tự nhiên cũng như các lĩnh vực liên quan như nghiên cứu ngôn ngữ, giảng dạy ngoại ngữ.'),
('CS322', 'Biểu diễn tri thức và ứng dụng', 'Knowledge representation and applications', 4, 4, 0, 'CN', NULL, 'CS214', NULL, 'CS212', 'KHMT', NULL),
('CS323', 'Các hệ thống hỏi-đáp', 'Question-Answering Systems', 4, 3, 1, 'CN', NULL, NULL, NULL, 'CS221', 'KHMT', 'Môn học cung cấp kiến thức cơ bản thuộc hướng nghiên cứu Question Answering trong xử lý ngôn ngữ tự nhiên gồm: phương pháp phân tích câu hỏi, phương pháp phân tích tài liệu văn bản đề xác định câu trả lời, mô hình các hệ thống hỏi-đáp, phương pháp đánh giá một hệ thống hỏi-đáp.'),
('CS324', 'Máy học trong xử lý ngôn ngữ tự nhiên', 'Machine Learning for Natural Language Processing', 4, 3, 1, 'CN', NULL, 'CS224', NULL, 'CS221', 'KHMT', 'Máy học là một trong những công cụ quan trọng thường được sử dụng trong các nghiên cứu về xử lý ngôn ngữ tự nhiên. Do đó, môn học này nhằm cung cấp cho sinh viên một số kiến thức nền tảng về các phương pháp máy học thường được sử dụng trong xử lý ngôn ngữ tự nhiên.'),
('CS325', 'Dịch máy', 'Machine Translation', 4, 3, 1, 'CN', NULL, NULL, NULL, 'CS221', 'KHMT', 'Dịch máy, là một lĩnh vực thuộc ngôn ngữ học máy tính nghiên cứu về phần mềm máy tính dịch văn bản hoặc tiếng nói từ một ngôn ngữ tự nhiên sang một ngôn ngữ khác. Ở mức độ căn bản, MT chỉ đơn thuần thay thế các từ trong một ngôn ngữ tự nhiên sang các từ thuộc ngôn ngữ khác. Với kỹ thuật ngữ liệu thì chúng ta có thể dịch được những văn bản phức tạp hơn, cho phép xử lý tốt hơn với các loại hình ngôn ngữ khác nhau, nhận dạng cụm từ, thành ngữ.'),
('CS326', 'Các kĩ thuật trong xử lý ngôn ngữ tự nhiên', 'Advanced Techniques in Natural Language Processing', 4, 3, 1, 'CN', NULL, NULL, NULL, 'CS221', 'KHMT', 'Môn học giới thiệu các phương pháp xử lý ngữ nghĩa hình thức câu và văn bản
trên máy tính. Ngoài ra, môn học cũng giới thiệu một số công cụ phổ biến
thường được sử dụng trong xử lý ngôn ngữ tự nhiên'),
('CS331', 'Thị giác máy tính nâng cao', 'Advanced Computer Vision', 4, 3, 1, 'CN', NULL, NULL, NULL, 'CS231', 'KHMT', 'Môn học này cung cấp khối kiến thức nâng cao trong chuyên ngành Thị giác máy tính, tập trung vào các vấn đề khai thác nội dung ảnh và video. Chủ đề được giới thiệu bao gồm: các phương pháp khai thác đặc trưng cấp cao, đặc trưng ngữ nghĩa, khai thác thông tin ngữ cảnh và mối liên hệ về không gian trong ảnh và video. Các bài toán chính bao gồm: video/image classification, image annotation, object detection, object recognition.'),
('CS332', 'Máy học trong thị giác máy tính', 'Machine Learning in Computer Vision', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'KHMT', 'Môn học này tập trung vào việc giới thiệu các giải pháp kĩ thuật nhằm ứng dụng các phương pháp máy học vào một số bài toán quan trọng trong ngành Thị giác máy tính, như: object detection (face detection, pedestrian detection), recognition (object categorization, fine-graned recognition), semantic analysis and indexing.'),
('CS333', 'Đồ họa game', 'Computer Graphics in Game', 3, 2, 1, 'CN', NULL, NULL, NULL, NULL, 'KHMT', 'Môn học này giới thiệu thiệu các kỹ thuật, phương pháp và cách thức sử dụng phần mềm, công cụ lập trình đồ họa cho video game. Sinh viên được truyền đạt các kiến thức cập nhật và hiện đại nhất nhằm tiếp cận nhu cầu nhân lực của các đơn vị sản xuất video game.'),
('CS334', 'Lập trình tính toán hình thức', 'Programming for formal computing', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'KHMT', NULL),
('CS335', 'Tìm kiếm ảnh và video', 'Image and Video Search', 4, 3, 1, 'CNTC', NULL, NULL, NULL, 'IT001', 'KHMT', NULL),
('CS336', 'Truy vấn thông tin đa phương tiện', 'Multimedia Information Retrieval', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'KHMT', 'Môn học này chú trọng truyền đạt các kiến thức nền tảng và các kĩ thuật nâng cao sử dụng trong các hệ thống tìm kiếm dựa trên văn bản, hình ảnh và video. Các vấn đề được giới thiệu bao gồm: các phương pháp rút trích và biểu diễn đặc trưng, các phương pháp ước lượng độ tương tự, các kĩ thuật đánh chỉ mục, kết hợp đa đặc trưng trong tìm kiếm, các kĩ thuật tìm kiếm trên cơ sở dữ liệu lớn.'),
('CS3363', 'Tổ chức ngôn ngữ lập trình', 'Organization of Programming Languages', 4, 3, 1, 'ĐC', NULL, 'MSIS207', NULL, NULL, 'HTTT', 'Xây dựng ngôn ngữ lập trình, các kiểu thực thi chương trình. Cấu trúc định nghĩa ngôn ngữ lập trình. Các cấu trúc điều khiển và các mô hình lập trình luồng dữ liệu.'),
('CS337', 'Xử lý âm thanh và tiếng nói', 'Speech and Audio Signal Processing', 4, 3, 1, 'CN', NULL, NULL, NULL, 'IT003', 'KHMT', 'Môn học này giới thiệu kiến thức nền tảng về xử lý tín hiệu số, trong đó đặc biệt là tín hiệu âm thanh và tiếng nói. Các chủ đề bao gồm: acoustic modeling, linear prediction, homomorphic processing, Time-Frequency analysis, auditory, fundamental speech recognition and synthesis.'),
('CS3373', 'Lập trình hướng đối tượng nâng cao cho môi trường windows', 'Advanced Object-Oriented Programming for Windows', 4, 3, 1, 'CSN', NULL, 'IEM5723', NULL, NULL, 'HTTT', 'Áp dụng mô hình tính toán hướng đối tượng vào thiết kế và phát triển các phần mềm trên môi trường windows. Sử dụng hiệu quả giao diện GUI, internet, các nguyên lý trao đổi dữ liệu và các chủ đề liên quan.'),
('CS338', 'Nhận dạng', 'Pattern Recognition', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'KHMT', 'Môn này cung cấp khối kiến thức cơ bản trong lĩnh vực Nhận dạng bao gồm các thuật toán nhận dạng có tham số và phi tham số như: SVM, Mạng Neural Network, Mô Hình Markov Ẩn, Maximum Likelihood, K-Nearest Neighbor, K-Mean. Sinh viên có thể cài đặt được một số thuật toán cho bài toán nhận dạng với dữ liệu đặc trưng rút trích từ các dữ liệu thực tế.'),
('CS339', 'Xử lý văn bản y khoa', 'Natural Language Processing with Health Focus', 4, 3, 1, 'CNTC', NULL, NULL, NULL, NULL, 'KHMT', 'This course covers natural language processing with a focus on health applications.
Some NLP theory is covered. The main emphasis is on practice and application of free NLP tool sets in the real world. Course topics: introduce health NLP; review health NLP and health data science basic concepts; introduce the CLAMP toolset; experiment with the CLAMP NLP pipeline on real-world texts; introduce selected open-source NLP toolsets; introduce machine learning (ML) in clinical NLP; study and apply the System Development Life Cycle approach to software engineering; learn how to measure the performance of your NLP system; and learn how to manage machine and human annotation.'),
('CS3423', 'Cấu trúc tập tin', 'File Structures', 4, 3, 1, 'CSN', NULL, 'CS4153', NULL, NULL, 'HTTT', 'Trình bày các đặc điểm vật lý cơ bả của các thiết bị lưu trữ. CÁc phương pháp xử lý và tổ chức tập tin cho các tập tin tuần tự, trực tiếp, lập chỉ mục, cấu trúc cây, nghịch đảo. Ứng dụng của các khái niệm cấu trúc dữ liệu vào việc tổ chức tập tin vật lý và logic. Phân tích hiệu năng. Các thành tố của các hệ CSDL nâng cao.'),
('CS3443', 'Hệ thống máy tính', 'Computer Systems', 3, 3, 0, 'CSN', NULL, NULL, NULL, NULL, 'HTTT', 'Mô tả mức thanh ghi và chức năng của hệ thống máy tính, các cấu trúc máy tính, kỹ thuật truy tìm địa chỉ, macro, liên kết, toán tử nhập – xuất. Giới thiệu về các phép toán xử lý tập tin và các thiết bị lưu trữ phụ. Lập trình bằng ngôn ngữ assembly.'),
('CS351', 'Chuyên đề NCKH 1', 'Scientific Research 1', 4, 4, 0, 'CN', NULL, NULL, NULL, NULL, 'KHMT', NULL),
('CS3513', 'Phương pháp số cho máy tính kỹ thuật số', 'Numerical methods for digital computers', 3, 3, 0, 'CNTC', NULL, NULL, NULL, NULL, 'HTTT', NULL),
('CS352', 'Chuyên đề NCKH 2', 'Scientific Research 2', 4, 4, 0, 'CN', NULL, NULL, NULL, NULL, 'KHMT', NULL),
('CS3613', 'Cơ sở tính toán', 'Theoretical Foundations of Computing', 4, 3, 1, 'CNCS', NULL, 'STAT3013', NULL, NULL, 'HTTT', 'Giới thiệu lý thuyết cổ điển về khoa học máy tính. Các máy tuần tự và ứng dụng vào các thiết bị, tiến trình và lập trình. Các mô hình tính toán: automat hữu hạn, automat đẩy xuống, máy Turing. Vài trò của không tất định. Các giới hạn của máy tính số. Giải được và không giải được.'),
('CS3653', 'Toán rời rạc', 'Discrete Mathematics', 3, 3, 0, 'ĐC', NULL, NULL, NULL, 'MATH2154', 'HTTT', 'Giới thiệu các thuật toán đệ quy, các phương pháp tìm kiếm và sắp xếp thông dụng, tổ chức chương trình, lập trình bộ nhớ động, danh sách liên kết đơn, kế thừa, đa hình, xử lý ngoại lệ, stack, queue, vectors, cây... Phân tích toán học về độ phức tạp không gian và thời gian, tình huống xấu nhất, và tình huống trung bình'),
('CS371', 'Seminar chuyên đề 1', 'Seminar 1', 2, 2, 0, 'CN', NULL, NULL, NULL, NULL, 'KHMT', NULL),
('CS372', 'Seminar chuyên đề 2', 'Seminar 2', 2, 2, 0, 'CN', NULL, NULL, NULL, NULL, 'KHMT', NULL),
('CS401', 'Công nghệ Java', 'Java technology', 4, 3, 1, 'CĐTN', NULL, NULL, NULL, NULL, 'KHMT', NULL),
('CS402', 'Phân tích thiết kế HTTT quản lý', 'Management-information system analysis and design', 3, 3, 0, 'CN', NULL, NULL, NULL, NULL, 'KHMT', NULL),
('CS403', 'Các dịch vụ web', 'Web services', 3, 3, 0, 'CN', NULL, NULL, NULL, NULL, 'KHMT', NULL),
('CS404', 'Công nghệ đa tác tử (Muli-Agent)', 'Multi-agent technology', 4, 4, 0, 'CĐTN', NULL, 'CS312', NULL, NULL, 'KHMT', NULL),
('CS405', 'Logic mờ và ứng dụng', 'Fuzzy Logic and Applications', 4, 3, 1, 'CĐTN', NULL, NULL, NULL, NULL, 'KHMT', 'Lý thuyết mờ là một công cụ quan trọng trong giải quyết các vấn để thuộc về tính toán mềm trong ngành Khoa học Máy tính và các chuyên ngành kỹ thuật khác. Môn học có 2 mục tiêu: trang bị các kiến thức cơ bản của Lý thuyết mờ và một số ứng dụng lý thuyết mờ.'),
('CS406', 'Xử lý ảnh và ứng dụng', 'Image Processing and Applications', 4, 3, 1, 'CĐTN', NULL, NULL, NULL, NULL, 'KHMT', 'Môn học này nhằm cung cấp cho sinh viên những kiến thức nền tảng của xử lý ảnh cũng như kĩ thuật để xây dựng những hệ thống thông tin dựa trên ảnh.'),
('CS407', 'Các kỹ thuật trong xử lý NNTN', 'Advanced Techniques in Natural Language Processing', 4, 4, 0, 'CĐTN', NULL, 'CS326', NULL, NULL, 'KHMT', NULL),
('CS408', 'Các hệ giải toán thông minh', 'Intelligent Problem-Solving Systems', 4, 4, 0, 'CĐTN', NULL, 'CS316', NULL, NULL, 'KHMT', NULL),
('CS409', 'Hệ suy diễn mờ', 'Fuzzy Inference Systems', 4, 3, 1, 'CĐTN', NULL, NULL, NULL, NULL, 'KHMT', 'Môn học có nội dung bao gồm 2 phần. Phần lý thuyết trình bày các công cụ của Lý thuyết mờ như lý thuyết tập mờ, logic mờ. Phần ứng dụng bao gồm các ứng dụng Logic mờ trong matlab, Dư báo chuỗi thời gian, Ontology mờ, Điều khiển mờ.'),
('CS410', 'Mạng neural và thuật giải di truyền', 'Neural Networks and Genetic Algorithms', 4, 3, 1, 'CĐTN', NULL, NULL, 'IT003', NULL, 'KHMT', 'môn học cung cấp kiến thức nền tảng chuyên sâu về mạng nơ-ron và thuật giải di truyền. Bên cạnh đó, giới thiệu các ứng dụng quan trọng của mạng nơ-ron và thuật giải di truyền.'),
('CS411', 'Dịch máy', 'Machine Translation', 4, 4, 0, 'CĐTN', NULL, 'CS325', NULL, NULL, 'KHMT', NULL),
('CS412', 'Web ngữ nghĩa', 'Semantic Web', 4, 3, 1, 'CĐTN', NULL, NULL, NULL, 'CS221', 'KHMT', 'web ngữ nghĩa là một vấn đề trong lĩnh vực xử lý ngôn ngữ tự nhiên nói riêng và chuyên ngành trong lĩnh vực ứng dụng trí tuệ nhân tạo nói chung. Web ngữ nghĩa chuyên về xây dựng các hệ thống web với mức độ tri thức cao trên nền tảng các hệ cơ sở tri thức (ontology) và dữ liệu có cấu trúc, dữ liệu liên kết (linked data), ngôn ngữ truy vấn cơ sở dữ liệu liên kết.'),
('CS414', 'Lý thuyết automat và ứng dụng', 'Automata Theory and Applications', 4, 3, 1, 'CĐTN', NULL, NULL, NULL, 'MA004', 'KHMT', 'Trang bị cho sinh viên những khái niệm cơ bản về Lý thuyết, từ đó phát triển các ứng dụng xây dựng ngôn ngữ lập trình và các trình dịch (Bộ phân tích từ vựng và cú pháp trong các trình biên dịch) và các ứng dụng trong Thiết kế các hệ thống số gồm:
– Mạch tuần tự
– Mạch đếm
– Máy trạng thái
– Controller'),
('CS4143', 'Đồ họa máy tính', 'Computer Graphics', 3, 3, 0, 'CNTC', NULL, NULL, NULL, NULL, 'HTTT', 'Lập trình đồ họa tương tác, phần cứng đồ họa, phép biến đổi hình học, các cấu trúc dữ liệu cho việc biểu diễn đồ thị, tổng quan 3D, biểu diễn hình 3D, thuật toán ẩn cạnh và ẩn bề mặt cắt, mô hình tô bóng.'),
('CS415', 'Mã hóa thông tin', 'Encoding information', 4, 3, 1, 'CĐTN', NULL, NULL, NULL, NULL, 'KHMT', NULL),
('CS4153', 'Phát triển ứng dụng trên di động', 'Mobile Application Development', 3, 3, 0, 'CN', NULL, NULL, NULL, 'CS2133', 'HTTT', 'Giới thiệu về tính toán di động khắp mọi nơi, tính toán cảm ngữ cảnh, giới thiệu hệ điều hành Android và các phương pháp lập trình trên Android. Các phương pháp lập trình nâng cao: đa luồng, đa hành vi, kết nối SQLite, Web Services. Khái niệm cross platform (PhoneGap) thiết kế ứng dụng cho nhiều loại thiết bị di động khác nhau trên đa hệ điều hành như iOs, Android,...'),
('CS417', 'Nhận dạng', 'Pattern recognition', 3, 2, 1, 'CN', NULL, NULL, NULL, NULL, 'KHMT', NULL),
('CS418', 'Trực quan máy tính', 'Visual computing', 4, 3, 1, 'CĐTN', NULL, NULL, NULL, NULL, 'KHMT', NULL),
('CS419', 'Truy xuất thông tin', 'Information Retrieval', 4, 3, 1, 'CĐTN', NULL, NULL, NULL, 'CS115', 'KHMT', 'Môn học giới thiệu những kiến thức căn bản trong lĩnh vực truy xuất thông tin, bao gồm: các mô hình truy xuất thông tin, các phương pháp lập chỉ mục, mô hình không gian véc-tơ và phương pháp đánh giá mô hình truy xuất thông tin.Ngoài ra, sinh viên được hướng dẫn để thực hiện một đồán môn học. Mục đích: Môn học cung cấp cho sinh viên những kiến thức cơ sở trong lĩnh vực truy xuất thông tn, từ đó sinh viên có khả năng triển khai, dưới sự hướng dẫn của giảng viên, một đồán môn học dựa trên những kiến thức đã được học.'),
('CS420', 'Các vấn đề chọn lọc trong thị giác máy tính', 'Selected Topics in Computer Vision', 4, 3, 1, 'CĐTN', NULL, NULL, NULL, 'CS231', 'KHMT', 'Môn học này có nội dung linh hoạt, chủ yếu tập trung vào các chủ đề, bài toán mới nhất trong lĩnh vực Thị giác máy tính.'),
('CS421', 'Khai thác dữ liệu đa phương tiện', 'Khai thác dữ liệu đa phương tiện', 4, 3, 1, 'CĐTN', NULL, NULL, NULL, NULL, 'KHMT', NULL),
('CS4243', 'Thuật toán và tiến trình trong an toàn máy tính', 'Algorithms and Processes in Computer Security', 3, 3, 0, 'CSN', NULL, NULL, NULL, 'CS4283', 'HTTT', 'Tổng quan về các thành phần của máy tính và an toàn mạng. Thảo luận về những tiến trình bên ngoài được yêu cầu trong các hệ thống bảo mật, bảo đảm thông tin, sao lưu, khôi phục giao dịch. Phân tích chi tiết về mã hoá bảo mật, giao thức, hashing, phân quyền và chứng thực.'),
('CS4273', 'Nhập môn Công nghệ phần mềm', 'Introduction to Software Engineering', 4, 3, 1, 'CNCS', NULL, NULL, NULL, NULL, 'HTTT', 'Trình bày đặc điểm cơ bản của chu trình sống phần mềm. Các công cụ, phương pháp kỹ thuật, và quản lí kiểm soát cho việc phát triển và duy trì các hệ thống phần mềm lớn. Các mô hình và công cụ đo đạc. Nhân tố con người và kiểm định hệ thống.'),
('CS4283', 'Mạng máy tính', 'Computer Networks', 3, 3, 0, 'ĐC', NULL, NULL, NULL, NULL, 'HTTT', 'Cung cấp kiến thức về mạng máy tính, các hệ thống phân phối và thiết kế mạng tính hệ thống của chúng. Giới thiệu việc sử dụng, cấu trúc, và kiến trúc mạng máy tính. Các thí nghiệm mạng để mô tả cấu trúc mạng.'),
('CS431', 'Các kĩ thuật học sâu và ứng dụng', 'Deep Learning and Applications', 3, 2, 1, 'CĐTN', NULL, NULL, NULL, 'IT001
IT002', 'KHMT', 'Môn này cung cấp các kiến thức cơ bản trong lĩnh vực máy học đồng thời tiếp cận các hướng tiếp cận máy học hiện đại như thuật toán học sâu (Deep Learning). Qua môn học này sinh viên có thể cài đặt được thuật toán huấn luyện Gradient Descend để huấn luyện mạng Neural Network(NN), hiểu được kiến trúc mạng Convolutional Neural Network(CNN) cho bài toán phân loại đối tượng, huấn luyện lại mạng CNN cho dữ liệu mới và một số ứng dụng của mạng CNN trong một số bài toán như nhận dạng gương mặt, phát hiện đối tượng, truy vấn đối tượng.'),
('CS4323', 'Hệ điều hành', 'Operating Systems', 3, 3, 0, 'ĐC', NULL, NULL, NULL, NULL, 'HTTT', 'Kích hoạt tiến trình và khóa ngữ cảnh tiến trình.Xử lý theo lô, hệ điều hành chia sẻ thời gian, nhiều chương trình, Quản lý tiến trình, quản lý bộ nhớ và đồng bộ hóa. Ngăn ngừa, tránh và loại bế tắc….'),
('CS4343', 'Cấu trúc dữ liệu và giải thuật', 'Data Structures and Algorithms', 4, 3, 1, 'ĐC', NULL, NULL, NULL, 'CS2134', 'HTTT', 'Tổ chức lưu trữ, các cấu trúc, các cấu trúc dữ liệu và thông tin, xử lý danh sách, xử lý cây, xử lý đồ thị, tìm kiếm, sắp xếp.'),
('CS4344', 'An ninh mạng', 'Cyber Security', 3, 3, 0, 'CSN', NULL, NULL, NULL, NULL, 'HTTT', NULL),
('CS4793', 'Trí tuệ nhân tạo', 'Artificial Intelligence', 4, 3, 1, 'CNCS', NULL, 'MKTG5883', NULL, NULL, 'HTTT', 'Đề cập đến nhiều chủ đề chính của trí tuệ nhân tạo, bao gồm giải quyết vấn đề tìm kiếm định hướng,suy diễn logic và biểu diễn tri thức, ngôn ngữ trí tuệ nhân tạo, lịch sử, và triết lý về trí tuệ nhân tạo.'),
('CS4883', 'Các vấn đề xã hội trong tính toán', 'Social issues in computing', 3, 3, 0, 'CNTC', NULL, NULL, NULL, NULL, 'HTTT', NULL),
('CS5000', 'Luận văn', 'Thesis', 10, 10, 0, 'KLTN', NULL, NULL, NULL, NULL, 'HTTT', NULL),
('CS501', 'Khóa luận tốt nghiệp', 'Thesis', 10, 10, 0, 'KLTN', NULL, NULL, NULL, NULL, 'KHMT', NULL),
('CS502', 'Các công nghệ web và ứng dụng', 'Web technology and applications', 2, 2, 0, 'CN', NULL, 'SE341', NULL, NULL, 'KHMT', NULL),
('CS503', 'Môn tốt nghiệp KHMT 2', 'Computer science', 3, 3, 0, 'CN', NULL, NULL, NULL, 'IT003', 'KHMT', NULL),
('CS5030', 'Thực tập tốt nghiệp', 'Internship', 3, 3, 0, 'TTTN', NULL, NULL, NULL, NULL, 'HTTT', NULL),
('CS5031', 'Thực tập doanh nghiệp', 'Internship', 2, 2, 0, 'TN', NULL, NULL, NULL, NULL, 'HTTT', NULL),
('CS504', 'Công nghệ .NET', '.Net technology', 4, 3, 1, 'CN', NULL, 'SE310', NULL, NULL, 'KHMT', NULL),
('CS505', 'Khoá luận tốt nghiệp', 'Thesis', 10, 10, 0, 'KLTN', NULL, NULL, NULL, NULL, 'KHMT', NULL),
('CS506', 'Chuyên đề J2EE', 'J2EE', 4, 3, 1, 'CN', NULL, 'SE325', NULL, NULL, 'KHMT', NULL),
('CS507', 'Hệ điều hành Linux', 'Linux operating systems', 4, 3, 1, 'CN', NULL, 'NT103', NULL, NULL, 'KHMT', NULL),
('CS508', 'Lập trình cơ sở dữ liệu', 'Database programming', 4, 3, 1, 'CN', NULL, 'IS203', NULL, NULL, 'KHMT', NULL),
('CS510', 'Lý thuyết thông tin', 'Information theory', 3, 3, 0, 'CN', NULL, 'NT104', NULL, NULL, 'KHMT', NULL),
('CS511', 'Ngôn ngữ lập trình C#', 'C# Programming Language', 4, 3, 1, 'CNTC', NULL, NULL, NULL, 'IT002
IT003
IT001', 'KHMT', 'Nắm được ngôn ngữ lập trình C#
Hiểu được kiến trúc .NET
Nắm được phương pháp lập trình trên môi trường Window
o Lập trình giao diện
o Lập trình đồ họa GDI+
o Xử lý tập tin và thư mục
o Kết nối cơ sở dữ liệu
o Lập trình đồng hành
\- Xây dựng ứng dụng trên nền .NET Framework'),
('CS513', 'Ngôn ngữ lập trình Java', 'Java programming language', 4, 3, 1, 'CN', NULL, 'SE330', NULL, NULL, 'KHMT', NULL),
('CS515', 'Phân tích thiết kế hệ thống thông tin', 'Information system analysis and design', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'KHMT', NULL),
('CS516', 'Phân tích thiết kế hướng đối tượng với UML', 'Object-oriented analysis and design with UML', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'KHMT', NULL),
('CS517', 'Quản lý dự án', 'Project management', 3, 3, 0, 'CN', NULL, 'IS208', NULL, NULL, 'KHMT', NULL),
('CS518', 'Xây dựng phần mềm hướng đối tượng', 'Object-oriented software development', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'KHMT', NULL),
('CS519', 'Phương pháp luận nghiên cứu khoa học', 'Scientific Research Methodology', 3, 3, 0, 'CNTC', NULL, NULL, NULL, NULL, 'KHMT', 'Môn học hướng về việc trang bị cho sinh viên những kiến thức cơ bản về khoa học và nghiên cứu khoa học nhằm mục đích nâng cao chất lượng của các đề cương và các luận văn tốt nghiệp. Qua các thí dụ cụ thể, các tiến trình trong nghiên cứu khoa học được bổ sung bởi những mô hình và lý thuyết để bồi dưỡng cho sinh viên về phương pháp thực dụng trong nghiên cứu khoa học: cách đặt vấn đề khoa học; phương pháp giải quyết các bài toán trong tin học; áp dụng các thủ thuật sáng tạo trong các bài toán tin học; các phương pháp suy luận, tư duy giả thuyết khoa học; cách viết bài báo khoa học, và sau cùng là các vấn đề liên quan đến đạo đức của người làm khoa học.'),
('CS521', 'Toán rời rạc nâng cao', 'Advanced Discrete Mathematics', 4, 4, 0, 'CNTC', NULL, NULL, NULL, 'IT003
MA004', 'KHMT', 'Môn học này trình bày những kiến thức nâng cao về lý thuyết đồ thị, trong đó sẽ
làm rõ một số tính chất của đồ thị như: đồ thị có chu trình Euler, Hamilton, các tính
chất của cây, cây đỏ đen, cây Huffman, …
\- Ứng dụng để cài đặt các thuật toán trên đồ thị lên trên máy tính
\- Giới thiệu bài toán ghép đôi, lý thuyết mã'),
('CS522', 'Đại số máy tính', 'Computer Algebra', 4, 3, 1, 'CĐTN', NULL, NULL, NULL, 'IT003
MA004', 'KHMT', 'Môn học này trình bày những kiến thức nâng cao về lý thuyết đồ thị, trong đó sẽ
làm rõ một số tính chất của đồ thị như: đồ thị có chu trình Euler, Hamilton, các tính
chất của cây, cây đỏ đen, cây Huffman, …
\- Ứng dụng để cài đặt các thuật toán trên đồ thị lên trên máy tính
\- Giới thiệu bài toán ghép đôi, lý thuyết mã'),
('CS523', 'Cấu trúc dữ liệu và giải thuật nâng cao', 'Advanced Data Structures and Algorithms', 4, 3, 1, 'CNTC', NULL, NULL, NULL, 'IT003
IT001', 'KHMT', 'Trình bày các phương pháp tổ chức và những thao tác cơ sở trên các cấu trúc dữ liệu phức tạp, được xây dựng trên nền các cấu trúc dữ liệu cơ sở. Các giải thuật kết hợp với các cấu trúc dữ liệu để hình thành nên chương trình máy tính. Ngon ngữ lập trình được sử dụng là các ngôn ngữ lập trình cấp cao như: C/C++, Python, ...'),
('CS524', 'Một số ứng dụng của xử lý ngôn ngữ tự nhiên', 'Applied Natural Language Processing', 4, 3, 1, 'CNTC', NULL, NULL, NULL, 'CS221', 'KHMT', 'Giới thiệu một số vấn đề thực tế trong xử lý thông tin văn bản và giải pháp của
của nó cùng với việc đề xuất mô hình và xây dựng ứng dụng phù hợp. Trong quá
trình tìm giải pháp, sinh viên sẽ áp dụng các bài toán trong xử lý ngôn ngữ tự
nhiên đã học vào từng vấn đề cụ thể. Đến giai đoạn thiết kế mô hình hệ thống và
xây dựng ứng dụng, sinh viên có thể hoặc sử dụng các thư viện phần mềm về xử
lý ngôn ngữ tự nhiên để phát triển một phần mềm hoàn chỉnh có quy mô nhỏ,
phục vụ cho một vấn đề được chọn trong việc xử lý thông tin văn bản.'),
('CS525', 'Thị giác máy tính trong tương tác người – máy', 'Computer Vision in Human-Computer Interaction', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'KHMT', NULL),
('CS526', 'Phát triển ứng dụng đa phương tiện trên thiết bị di động', 'Multimedia Mobile Applications', 4, 3, 1, 'CNTC', NULL, NULL, NULL, 'IT002
IT003', 'KHMT', NULL),
('CS527', 'Thực tại ảo', 'Virtual Reality', 4, 3, 1, 'CNTC', NULL, NULL, NULL, NULL, 'KHMT', 'Môn học này giới thiệu các khái niệm và mô hình cơ bản của ứng dụng thực tại ảo, các giải pháp tích hợp thông ảo, các công cụ phát triển ứng dụng thực tại ảo.'),
('CS528', 'Trực quan hóa thông tin', 'Information Visualization', 4, 3, 1, 'CNTC', NULL, NULL, NULL, NULL, 'KHMT', 'Môn học này giới thiệu kiến thức nền tảng và ứng dụng của trực quan hóa thông tin. Các chủ đề bao gồm: visual encoding, data and task abstraction, visual representation, dimensionality reduction, tabular data, trees and graphs presentation.'),
('CS529', 'Các vấn đề nghiên cứu và ứng dụng trong khoa học máy tính', 'Selected Topics for Researching and Application in Computer Science', 4, 4, 0, 'CNTC', NULL, NULL, NULL, NULL, 'KHMT', NULL),
('CS530', 'Đồ án chuyên ngành', 'Undergraduate Research Project', 3, 3, 0, 'CNTC', NULL, NULL, NULL, NULL, 'KHMT', NULL),
('CS531', 'Đồ họa trong video game', 'Computer Graphics in Video Games', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'KHMT', NULL),
('CS532', 'Thị giác máy tính trong tương tác người-máy', 'Computer Vision in Human-Computer Interaction', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'KHMT', 'Môn học này giới thiệu các hướng tiếp cận về tương tác người-máy dựa trên các phương pháp, kĩ thuật thị giác máy tính. Trong đó, tập trung vàocác phương pháp phân tích, nhận dạng cử chỉ, hành động, biểu cảm khuôn mặt của người điều khiển thông qua hình ảnh và video (bao gồm cả video 2D thông thường và video độ sâu).'),
('CS534', 'Lập trình Javascript và ứng dụng', 'JavaScript Programming and Applications', 4, 3, 1, 'CNTC', NULL, NULL, NULL, NULL, 'KHMT', NULL),
('CS535', 'Tổng hợp tiếng nói', 'Speech Synthesis', 4, 3, 1, 'CNTC', NULL, NULL, NULL, 'CS221
CS114', 'KHMT', 'Tiếng nói là phương tiện giao tiếp tự nhiên nhất trong trao đổi và tiếp nhận thông tin hàng ngày giữa người với người. Trong môn học này, các kỹ thuật và mô hình học trong xử lý ngôn ngữ nói được trình bày thông qua các lý thuyết nền tảng về nhận dạng tiếng nói (Speech Recognition), tổng hợp tiếng nói (Speech Synthesis) và các hệ thống giao tiếp giữa người và máy qua tiếng nói (Dialog).'),
('CS5423', 'Nguyên lý các hệ cơ sở dữ liệu', 'Principles of Database Systems', 4, 3, 1, 'CSN', NULL, NULL, NULL, NULL, 'HTTT', 'Khái quát về các hệ quản trị cơ sở dữ liệu, mô hình thực thể mối kết hợp, ngôn nữ truy vấn có cấu trúc, đại số quan hệ, thiết kế CSDL quan hệ với các định lý về chuẩn hóa, các ràng buộc toàn vẹn CSDL và nguyên lý các hệ CSDL với internet.'),
('CS5433', 'Các hệ cơ sở dữ liệu phân tán', 'Distributed Database Systems', 3, 3, 0, 'CSN', NULL, NULL, NULL, 'CS5423
CS4283
MSIS4013', 'HTTT', 'Khái quát về các hệ quản trị CSDL, kiến trúc hệ quản trị CSDL phân tán, thiết kế CSDL phân tán, khái quát về xử lý truy vấn, giới thiệu về quản lý giao dịch, kiểm soát đồng thời phân tán và SQL server.'),
('CS551', 'Thực tập', 'Internship', 2, 2, 0, 'TTTN', NULL, NULL, NULL, NULL, 'KHMT', NULL),
('CS553', 'Đồ án tốt nghiệp', 'Capstone Project', 6, 6, 0, 'TN', NULL, NULL, NULL, NULL, 'KHMT', NULL),
('CS554', 'Đồ án tốt nghiệp tại doanh nghiệp', 'Industry Capstone Project', 10, 10, 0, 'TN', NULL, NULL, NULL, NULL, 'KHMT', NULL),
('CSBU001', 'Lập trình', 'Programming', 4, 3, 1, 'CN', NULL, 'CSBU101', NULL, NULL, 'KHMT', NULL),
('CSBU002', 'Mạng máy tính', 'Networking', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'KHMT', NULL),
('CSBU003', 'Thực hành nghề nghiệp', 'Professional Practice', 4, 4, 0, 'CN', NULL, NULL, NULL, NULL, 'KHMT', NULL),
('CSBU004', 'Toán cho tin học', 'Maths for Computing', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'KHMT', NULL),
('CSBU005', 'Bảo mật', 'Security', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'KHMT', NULL),
('CSBU006', 'Quản lý dự án máy tính thành công', 'Managing a Successful Computing Project', 4, 4, 0, 'CN', NULL, NULL, NULL, NULL, 'KHMT', NULL),
('CSBU007', 'Thiết kế và phát triển cơ sở dữ liệu', 'Database Design and Development', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'KHMT', NULL),
('CSBU008', 'Kiến trúc máy tính', 'Computer Systems Architecture', 4, 3, 1, 'CN', NULL, 'CSBU102', NULL, NULL, 'KHMT', NULL),
('CSBU009', 'Dự án nghiên cứu', 'Computing Research Project', 8, 4, 4, 'CN', NULL, NULL, NULL, NULL, 'KHMT', NULL),
('CSBU010', 'Công nghệ kinh doanh thông minh', 'Business Intelligence', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'KHMT', NULL),
('CSBU011', 'Toán rời rạc', 'Discrete Maths', 4, 4, 0, 'CN', NULL, 'CSBU110', NULL, NULL, 'KHMT', NULL),
('CSBU012', 'Cấu trúc dữ liệu và giải thuật', 'Data Structures and Algorithms', 4, 3, 1, 'CN', NULL, 'CSBU104', NULL, NULL, 'KHMT', NULL),
('CSBU013', 'Lập trình nâng cao', 'Advanced Programming', 4, 3, 1, 'CN', NULL, 'CSBU107', NULL, NULL, 'KHMT', NULL),
('CSBU014', 'Máy học', 'Machine Learning', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'KHMT', NULL),
('CSBU015', 'Điện toán đám mây', 'Cloud Computing', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'KHMT', NULL),
('CSBU016', 'Thực tập', 'Internship', 0, 0, 0, 'TTTN', NULL, NULL, NULL, NULL, 'KHMT', NULL),
('CSBU101', 'Lập trình máy tính', 'Computer Programming', 65, 40, 25, 'CN', NULL, NULL, NULL, NULL, 'KHMT', NULL),
('CSBU102', 'Hệ thống máy tính', 'Computer Systems', 65, 40, 25, 'CN', NULL, NULL, NULL, NULL, 'KHMT', NULL),
('CSBU103', 'Phát triển và thiết kế web', 'Website Design and Development', 65, 40, 25, 'CN', NULL, NULL, NULL, NULL, 'KHMT', NULL),
('CSBU104', 'Cấu trúc dữ liệu và giải thuật', 'Data Structures and Algorithms', 65, 40, 25, 'CN', NULL, NULL, NULL, NULL, 'KHMT', NULL),
('CSBU105', 'Mạng máy tính căn bản', 'Network Fundamentals', 65, 40, 25, 'CN', NULL, NULL, NULL, NULL, 'KHMT', NULL),
('CSBU106', 'Đồ án đổi mới sáng tạo', 'Innovation Project', 80, 48, 32, 'CN', NULL, NULL, NULL, NULL, 'KHMT', NULL),
('CSBU107', 'Lập trình hướng đối tượng', 'Object-Oriented Programming', 65, 40, 25, 'CN', NULL, NULL, NULL, NULL, 'KHMT', NULL),
('CSBU108', 'Hệ điều hành', 'Operating Systems', 65, 40, 25, 'CN', NULL, NULL, NULL, NULL, 'KHMT', NULL),
('CSBU109', 'Phát triển ứng dụng web và cơ sở dữ liệu', 'Database and Web Application Development', 65, 40, 25, 'CN', NULL, NULL, NULL, NULL, 'KHMT', NULL),
('CSBU110', 'Toán rời rạc và lập trình khai báo', 'Discrete Mathematics and Declarative Programming', 65, 40, 25, 'CN', NULL, NULL, NULL, NULL, 'KHMT', NULL),
('CSBU111', 'An ninh mạng', 'Cyber Security', 65, 40, 25, 'CN', NULL, NULL, NULL, NULL, 'KHMT', NULL),
('CSBU112', 'Thiết kế phần mềm', 'Software Design', 65, 40, 25, 'CN', NULL, NULL, NULL, NULL, 'KHMT', NULL),
('CSBU201', 'Thiết kế trải nghiệm người dùng', 'User Experience Design', 65, 40, 25, 'CN', NULL, NULL, NULL, NULL, 'KHMT', NULL),
('CSBU202', 'Phát triển ứng dụng cho thiết bị di động và thiết bị đeo', 'Mobile and Wearable Application Development', 80, 48, 32, 'CN', NULL, NULL, NULL, NULL, 'KHMT', NULL),
('CSBU203', 'Điện toán đám mây', 'Cloud Computing', 65, 40, 25, 'CN', NULL, NULL, NULL, NULL, 'KHMT', NULL),
('CSBU204', 'Trí tuệ nhân tạo và máy học', 'Artificial Intelligence and Machine Learning', 65, 40, 25, 'CN', NULL, NULL, NULL, NULL, 'KHMT', NULL),
('CSBU205', 'Dự án cá nhân', 'Individual Honours Project', 80, 48, 32, 'CN', NULL, NULL, NULL, NULL, 'KHMT', NULL),
('CSC01', 'Tin học đại cương', 'Introduction to informatics', 4, 3, 1, 'CN', NULL, 'IT001', NULL, NULL, 'KHMT', NULL),
('CSC11', 'Khoa học máy tính I', 'Computer science I', 4, 4, 0, 'CN', NULL, NULL, NULL, NULL, 'KHMT', NULL),
('CSC12', 'Khoa học máy tính II', 'Computer science II', 4, 4, 0, 'CN', NULL, NULL, NULL, NULL, 'KHMT', NULL),
('CSC21', 'Tin học đại cương (TE)', 'Introduction to informatics', 5, 5, 0, 'CN', NULL, 'IT001
CSC01', NULL, NULL, 'KHMT', NULL),
('CSKI1', 'Kỹ năng truyền thông làm việc nhóm', 'Teamwork and communication skills', 4, 4, 0, 'CN', NULL, NULL, NULL, NULL, 'KHMT', NULL),
('CU001', 'Văn hóa doanh nghiệp Nhật', 'Japanese Corporate Culture', 2, 2, 0, 'CN', NULL, NULL, NULL, NULL, 'KTTT', NULL),
('DAI015', 'Thực hành văn bản Tiếng Việt', 'Vietnamese Language Practice', 2, 2, 0, 'CN', NULL, NULL, NULL, NULL, 'PĐTĐH', NULL),
('DBSS0', 'Cơ sở dữ liệu', 'Cơ sở dữ liệu', 3, 3, 0, 'CHUA_PHAN_LOAI', NULL, NULL, NULL, NULL, 'Chưa rõ', NULL),
('DBSS1', 'Cơ sở dữ liệu', 'Database', 4, 3, 1, 'ĐC', NULL, 'IT004', NULL, NULL, 'TTNN', NULL),
('DS005', 'Giới thiệu ngành Khoa học Dữ liệu', 'Introduction to Data Science', 1, 1, 0, 'ĐC', NULL, 'CS005
IE005
IS005
SE005
CE005
NT005
NT015
EC005
AI001', NULL, NULL, 'KTTT', 'Môn học trình bày về sự cần thiết của Khoa học Dữ liệu (KHDL) trong các doanh nghiệp, tổ chức; Phân biệt ngành KHDL và các chuyên ngành, ứng dụng của KHDL trong thực tiễn và tầm ảnh hưởng của KHDL; Vị trí và cơ hội nghề nghiệp; Định hướng phát triển KHDL trong tương lai.'),
('DS101', 'Thống kê và xác suất chuyên sâu', 'Advanced Statistics and Probability', 3, 2, 1, 'CN', NULL, NULL, 'MA005', 'MA006
MA003', 'KTTT', 'Môn học cung cấp cho sinh viên những kiến thức cơ sở và chuyên sâu về xác suất và thống kê Toán học. Sinh viên có khả năng sử dụng các nguyên lý thống kê kết hợp với các định đề về xác suất để giải quyết các bài toán từ thực tế: các bài toán phân tích và dự báo về các đại lượng ngẫu nhiên và quá trình ngẫu nhiên.'),
('DS102', 'Học máy thống kê', 'Statistical Machine Learning', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'KTTT', 'Môn học nhằm cung cấp cho sinh viên những kiến thức nền tảng và nâng cao về học máy, đặc biệt là học máy thống kê, học từ dữ liệu như: phân tích dữ liệu thống kê, các phương pháp chọn mẫu dữ liệu, hồi qui tuyến tính, logistics, các phương pháp học có giám sát, không giám sát. Thêm vào đó, người học cũng được trang bị kiến thức liên quan những khó khăn khi học từ dữ liệu: không cân bằng dữ liệu, quá khớp, nhiễu. Thông qua những ví dụ thực tế, người học dễ dàng nắm bắt những kiến thức về học máy thống kê và dễ dàng áp dụng những phương pháp học máy thống kê vào những lĩnh vực ứng dụng khác nhau.'),
('DS103', 'Thu thập và tiền xử lý dữ liệu', 'Getting and Cleaning Data', 4, 3, 1, 'CN', NULL, 'DS108', NULL, NULL, 'KTTT', 'Môn học cung cấp những cách thức để thu thập và xử lý dữ liệu nhằm phục vụ cho những mục đích sử dụng dữ liệu về sau. Môn học trình bày những điều cơ bản cần thiết để thu thập dữ liệu bao gồm lấy dữ liệu thô từ web, API, cơ sở dữ liệu và từ các nguồn khác nhau ở những định dạng khác nhau, làm sạch dữ liệu và làm cho dữ liệu mang tính sẵn sàng để chia sẻ. Dữ liệu sẵn sàng sẽ đem lại hiệu quả đáng kể trong phân tích dữ liệu.'),
('DS104', 'Tính toán song song và phân tán', 'Parallel and Distributed Computing', 4, 3, 1, 'CN', NULL, NULL, NULL, 'IT004', 'KTTT', 'Môn học giới thiệu tổng quan về khái niệm, mô hình và những thách thức của hệ thống xử lý song song và xử lý phân bố. Môn học đề cập đến một số phương pháp và nền tảng cụ thể hỗ trợ giải quyết các bài toán dữ liệu lớn trên mô hình xử lý song song và xử lý phân bố.'),
('DS105', 'Phân tích và trực quan dữ liệu', 'Data Analytics and Visulization', 4, 3, 1, 'CN', NULL, 'DS111', NULL, 'MA005
IT001
DS103', 'KTTT', 'Môn học nhằm cung cấp cho sinh viên những kiến thức nền tảng về quy trình thu thập và xử lý dữ liệu cùng các kỹ thuật, công cụ phân tích, trực quan hoá dữ liệu.'),
('DS106', 'Tối ưu hóa và ứng dụng', 'Optimization and Applications', 3, 2, 1, 'CN', NULL, NULL, NULL, 'MA003', 'KTTT', 'Môn học cung cấp cho sinh viên những kiến thức cơ sở và nâng cao về tối ưu hóa và ứng dụng. Sinh viên có khả năng sử dụng các nguyên lý của lý thuyết về tối ưu kết hợp với các phương pháp xử lý bằng máy tính để giải quyết các bài toán các bài toán ứng dụng thực tế. Bên cạnh việc trang bị cho sinh viên kiến thức về Tối ưu và ứng dụng, sinh viên cũng sẽ được làm quen với các công cụ như phần mềm MATLAB và MAPLE để có thể giải quyết các bài toán phong phú của thực tế.'),
('DS107', 'Tư duy tính toán cho khoa học dữ liệu', 'Computational Thinking for Data Science', 4, 3, 1, 'CN', NULL, NULL, NULL, 'IT002', 'KTTT', NULL),
('DS108', 'Tiền xử lý và xây dựng bộ dữ liệu', 'Preprocessing and Constructing Datasets', 4, 3, 1, 'CN', NULL, NULL, NULL, 'IT001
IT004', 'KTTT', NULL),
('DS111', 'Phân tích dữ liệu', 'Data Analysis', 4, 3, 1, 'CSN', NULL, NULL, NULL, 'IT001
MA005', 'KTTT', NULL),
('DS200', 'Phân tích dữ liệu lớn', 'Big Data Analysis', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'KTTT', 'Môn học nhằm cung cấp cho sinh viên những kiến thức nền tảng, một bức tranh tổng quan về lĩnh vực dữ liệu lớn. Môn học là nền tảng cho khoa học và công nghệ dữ liệu. Bên cạnh đó, môn học giúp sinh viên bước đầu hiểu, tư duy về việc tạo ra những ứng dụng, bài toán thực tế về dữ liệu lớn. Đồng thời, giúp sinh viên làm quen với Hadoop framework, một nền tảng cơ bản giúp xử lý, phân tích dữ liệu lớn một cách dễ dàng.'),
('DS201', 'Deep Learning trong khoa học dữ liệu', 'Deep Learning for Data Science', 4, 3, 1, 'CN', NULL, NULL, NULL, 'DS102', 'KTTT', 'Môn học cung cấp các kiến thức vé ứng dụng học sâu trong khoa học dữ liệu. Môn học trìng bày khái quát về học sâu và ứng dụng học sâu trong khoa học dữ liệu. Cách dùng học sâu đề khám phá biểu diễn các loại dữ liệu có cấu trúc và phi cấu trúc như văn bản, hình ảnh, dữ liệu chuỗi thời. Các kiến thức về học sâu có giám sát, các phương pháp học sâu: convolutional networks and recurrent networks. Lý thuyết và kinh nghiệm để huấn luyện các mô hình học sâu bao gồm tối ưu hóa bằng cách sử dụng gradient giảm. Các ví dụ ứng dụng về học sâu cho khoa học dữ liệu trong các lĩnh vực phân tích văn bản, dự báo chuỗi thời gian, xử lý dữ liệu ảnh.'),
('DS202', 'Đồ án khoa học dữ liệu và ứng dụng 1', 'Data Science Project and Application 1', 2, 0, 2, 'CN', NULL, NULL, NULL, NULL, 'KTTT', 'Môn học Đồ án Khoa học Dữ liệu và Ứng dụng 1 là môn học tổ chức trong các
học kỳ chính, nhằm trang bị cho sinh viên những bước bắt đầu để có thể hoàn
thành một dự án khoa học dữ liệu và ứng dụng. Các kỹ năng được trang bị cho
sinh viên bao gồm khả năng phân tích và giải quyết vấn đề/bài toán; kỹ năng
thiết kế; kỹ năng thực hành và hiện thực thiết kế xây dựng dữ liệu phục vụ cho
một dự án hoặc ứng dụng; khả năng thực nghiệm và đánh giá các mô hình trên
dữ liệu đã xây dựng; kỹ năng giao tiếp, trao đổi và làm việc nhóm; kỹ năng
thuyết trình.'),
('DS203', 'Đồ án khoa học dữ liệu và ứng dụng 2', 'Data Science Project and Application 2', 2, 0, 2, 'CN', NULL, NULL, NULL, 'DS202', 'KTTT', 'Môn học Đồ án Khoa học Dữ liệu và Ứng dụng 2 là môn học tổ chức trong các
học kỳ chính, nhằm trang bị cho sinh viên những bước bắt đầu để có thể hoàn
thành một dự án khoa học dữ liệu và ứng dụng nâng cao. Các kỹ năng được trang
bị cho sinh viên bao gồm khả năng phân tích và giải quyết vấn đề/bài toán; kỹ
năng thiết kế; phân tích, đánh giá và so sánh trên nhiều nguồn dữ liệu khác nhau
với nhiều phương pháp; kỹ năng giao tiếp, trao đổi và làm việc nhóm; kỹ năng
viết và thuyết trình.'),
('DS204', 'Đồ án khoa học dữ liệu và ứng dụng', 'Data Science Project and Application', 2, 0, 2, 'ĐA', NULL, NULL, NULL, NULL, 'KTTT', 'Mục tiêu môn học là tìm hiểu về yêu cầu, quy trình triển khai thu thập và lưu trữ dữ liệu phục vụ cho xử lý dữ liệu lớn. Bên cạnh đó, sinh viên được tìm hiểu các kỹ thuật để thu thập và rút trích dữ liệu từ mạng Internet.'),
('DS207', 'Đồ án', 'Project', 2, 0, 2, 'CN', NULL, NULL, NULL, NULL, 'KTTT', NULL),
('DS300', 'Hệ khuyến nghị', 'Recommender Systems', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'KTTT', 'Môn học nhằm cung cấp cho sinh viên/học viên những kiến thức nền tảng về hệ khuyến nghị như: tầm quan trọng, phạm vi ứng dụng của hệ khuyến nghị trong thực tế; những khái niệm, thuật toán cơ bản thực hiện khuyến nghị (Lọc nội dung, lọc cộng tác); phương pháp đánh giá hệ khuyến nghị (online, offline), những thư viện lập trình, công cụ hỗ trợ xây dựng hệ khuyến nghị. Bên cạnh đó, môn học cũng giới thiệu người học những phương pháp tiếp cận mới và xu hướng của hệ khuyến nghị.'),
('DS301', 'Các giải thuật khai phá dữ liệu lớn', 'Mining of Massive Datasets', 4, 3, 1, 'CN', NULL, NULL, NULL, 'IT007', 'KTTT', 'Môn học cung cấp các khái niệm cũng như các giải thuật liên quan đến khai phá dữ liệu lớn, khả năng phân tích, thiết kế các ứng dụng khai phá dữ liệu lớn trong quản lý, trang bị khả năng phát triển các giải thuật khai phá dữ liệu lớn bằng kỹ thuật song song và phân tán.'),
('DS302', 'Phân tích thống kê đa biến', 'Multivariate Statistical Analysis', 3, 2, 1, 'CN', NULL, NULL, 'MA005', NULL, 'KTTT', 'Môn học dựa trên nền tảng của thống kê kinh điển trình bày về sự mở rộng củaToán thống kê sang trường hợp đa biến. Cung cấp sự hiểu biết về nguyên lý xử lý dữ liệu và cách triển khai thực hiện. Sinh viên có khả năng sử dụng các kỹ thuật, công cụ để có thể thiết kế, thao tác trên các bài toán cụ thể (phân tích phương sai, phân tích nhân tố, phân tích chùm) phục vụ cho nhiều môn học nâng cao về xác suất và thống kê (thống kê Bayes, mô phỏng số Monter – Carlo, học máy thống kê-Statistical Learning), trong những học kỳ kế tiếp. Trong quá trình học và thực hành về môn này sinh viên sẽ được trang bị những định hướng mới về thống kê đa biến và những phần mềm chuyên dùng như SPSS cùng các phần mềm mã nguồn mở như R.'),
('DS303', 'Thống kê Bayes', 'Bayesian Statistics', 3, 2, 1, 'CN', NULL, NULL, 'MA005', NULL, 'KTTT', 'Môn học cung cấp cho sinh viên những kiến thức cơ sở về xác suất và thống kê Bayes. Sinh viên có khả năng sử dụng các nguyên lý thống kê Bayes kết hợp với các định đề về xác suất để giải quyết các bài toán từ thực tế: các bài toán phân tích và dự báo về các đại lượng ngẫu nhiên và quá trình ngẫu nhiên.'),
('DS304', 'Thiết kế và phân tích thực nghiệm', 'Design and Analysis of Experiments', 3, 3, 0, 'CN', NULL, NULL, NULL, 'MA005', 'KTTT', 'Môn học cung cấp các kiến thức và phương pháp thiết kế thống kê thực nghiệm (statistical design of experiments - DOE) để thiết kế các thí nghiệm, phân tích đúng đắn kết quả thu được qua thí nghiệm và trình bày rõ ràng kết quả thí nghiệm. Phương pháp thống kê thí nghiệm được dùng phổ biến trong nghiên cứu học thuật và trong công nghiệp. Khóa học sử dụng Excel và các phần mềm chuyên dụng như SPSS hoặc R để thực hiện phân tích dữ liệu theo yêu cầu của các bài tập trong môn học.'),
('DS305', 'Phân tích dữ liệu chuỗi thời gian và ứng dụng', 'Applied Time Series Analysis', 3, 2, 1, 'CN', NULL, NULL, 'MA005', NULL, 'KTTT', 'Môn học cung cấp cho sinh viên những kiến thức cơ sở về quá trình ngẫu nhiên với thời gian rời rạc – chuỗi thời gian. Sinh viên có khả năng sử dụng các nguyên lý của xác suất thống kê kết hợp với các kiến thức toán khác như giải tích, đại số tuyến tính để giải quyết các bài toán từ thực tế: Các bài toán phân tích và dự báo về chuỗi thời gian trong công nghệ và trong kinh tế.'),
('DS306', 'Phân tích dữ liệu lớn trong tài chính', 'Big Data Analytics in Finance', 3, 3, 0, 'CN', NULL, NULL, 'DS200', NULL, 'KTTT', 'Môn học cung cấp các kiến thức để triển khai ứng dụng thực tế về phân tích dữ liệu lớn trong lĩnh vực tài chính nhằm hỗ trợ các quyết định đầu tư. Môn học tập trung vào các ứng dụng các mô hình phân tích dữ liệu lớn để giải quyết các bài toán trong lĩnh vực tài chính như dư báo, phân tích khách hàng, phân tích gian lận, phân tích hồi qui, phân tích rủi ro, ...
Môn học sẽ kết hợp của Lý thuyết và thực tiễn với các trường hợp dữ liệu tài chính như các báo cáo tài chính của các công ty được niêm yết trên thị trường chứng khoán, dự báo giá cổ phiếu, phát hiện các gian lận trong bào cáo tài chính, ... Bên cạnh đó các kỹ thuật trực quan hóa được áp dụng để nêu bật kết quả phân tích. Môn học có sự tham gia của các giảng viên từ các khoa kế toán-tài chính nhắm giúp sinh viên có các hiểu biết cụ thể về ứng dụng phân tích dữ liệu lớn trong lĩnh vực tài chính.'),
('DS307', 'Phân tích dữ liệu truyền thông xã hội', 'Social Media Data Analytics', 3, 3, 0, 'CN', NULL, NULL, NULL, 'IT004', 'KTTT', 'Môn học nhằm cung cấp cho sinh viên những kiến thức về thu thập, phân tích, xử lý dữ liệu trong lĩnh vực truyền thông xã hội. Bên cạnh đó, sinh viên được tiếp cận với một số bài toán trong lĩnh vực truyền thông xã hội như: bài toán phân tích cảm xúc (Sentiment Analysis), khai thác quan điểm công đồng (Opinion Mining), bài toán nhận biết tin nóng, sự kiện thời sự nhất từ mạng xã hội (Top Story Detection).'),
('DS308', 'Mô hình đồ thị xác suất', 'Probabilistic Graphical Models', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'KTTT', 'Môn học nhằm cung cấp cho sinh viên những kiến thức nền tảng về các mô hình đồ thị xác suất (PGM) và ứng dụng của nó: biểu diễn và suy luận và học trên các mô hình đồ thị xác suất. Các bài toán từ thế giới thực sẽ được biểu diễn thông qua các PGM như thế nào dùng Bayesian Networks dựa trên đồ thị có hướng và Markov Networks dựa trên đồ thị vô hướng. Việc suy diễn dựa trên các mô hình PGM như thế nào và làm thế nào có thể học được các tham số mô hình PGM từ một tập dữ liệu huấn luyện.'),
('DS309', 'Thực tập doanh nghiệp', 'Internship', 2, 2, 0, 'TTTN', NULL, NULL, NULL, NULL, 'KTTT', NULL),
('DS310', 'Xử lý ngôn ngữ tự nhiên cho khoa học dữ liệu', 'Natural Language Processing for Data Science', 4, 3, 1, 'CN', NULL, NULL, NULL, 'DS102', 'KTTT', NULL),
('DS311', 'Kỹ năng nghiên cứu và viết bài báo khoa học', 'Scientific Research and Writing Skills', 3, 3, 0, 'CN', NULL, NULL, NULL, NULL, 'KTTT', NULL),
('DS312', 'Xử lý ảnh y khoa', 'Medical Image Processing', 3, 3, 0, 'CN', NULL, NULL, NULL, NULL, 'KTTT', NULL),
('DS313', 'Xử lý thông tin giọng nói', 'Speech Information Processing', 4, 3, 1, 'CN', NULL, NULL, NULL, 'DS102', 'KTTT', NULL),
('DS314', 'Rút trích và truy vấn thông tin', 'Information Extraction and Retrieval', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'KTTT', NULL),
('DS315', 'Phân tích kho dữ liệu', 'Data Warehouse Analysis', 4, 3, 1, 'CN', NULL, NULL, NULL, 'IT004', 'KTTT', NULL),
('DS316', 'Xây dựng ứng dụng thông minh', 'Intelligent Applications', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'KTTT', NULL),
('DS317', 'Khai phá dữ liệu trong doanh nghiệp', 'Data Mining for Enterprise', 4, 3, 1, 'CN', NULL, NULL, NULL, 'IT002
IT003', 'KTTT', NULL),
('DS318', 'Đạo đức trong trí tuệ nhân tạo và khoa học dữ liệu', 'Ethics in Artificial Intelligence and Data Science', 3, 3, 0, 'CN', NULL, NULL, NULL, NULL, 'KTTT', NULL),
('DS319', 'Mô hình ngôn ngữ lớn', 'Large Language Models', 4, 3, 1, 'CN', NULL, NULL, NULL, 'IT003
IT002
DS102', 'KTTT', NULL),
('DS320', 'Học đa thể thức', 'Multimodal Learning', 4, 3, 1, 'CN', NULL, NULL, NULL, 'IT002
IT003
DS102', 'KTTT', NULL),
('DS321', 'Khoa học dữ liệu cho an toàn thông tin', 'Data Science for Cybersecurity', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'KTTT', NULL),
('DS322', 'Thiết kế hệ thống học máy', 'Machine Learning Systems Design', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'KTTT', NULL),
('DS323', 'Viết báo cáo kỹ thuật và thuyết trình', 'Technical Report Writing and Presentation', 3, 3, 0, 'CN', NULL, NULL, NULL, NULL, 'KTTT', NULL),
('DS324', 'Khai thác dữ liệu ảnh số', 'Digital Image Data Mining', 4, 3, 1, 'CN', NULL, NULL, NULL, 'MA003
IT003', 'KTTT', NULL),
('DS325', 'Thiết kế ứng dụng với dữ liệu chuyên sâu', 'Designing Data-Intensive Applications', 4, 3, 1, 'CN', NULL, NULL, 'DS108', NULL, 'KTTT', NULL),
('DS326', 'Khai phá dữ liệu đa phương tiện và ứng dụng', 'Multimedia Mining and Applications', 4, 3, 1, 'CN', NULL, NULL, NULL, 'IT003
DS102', 'KTTT', NULL),
('DS327', 'Các mô hình nền tảng', 'Foundation Models', 4, 3, 1, 'CN', NULL, NULL, NULL, 'IT003
IT002
DS102', 'KTTT', NULL),
('DS400', 'Chuyên đề tốt nghiệp khoa học dữ liệu', 'Data Science Graduate Seminars', 4, 4, 0, 'TN', NULL, NULL, NULL, NULL, 'KTTT', NULL),
('DS501', 'Đồ án tốt nghiệp', 'Capstone Project', 6, 6, 0, 'ĐC', NULL, NULL, NULL, 'DS400', 'KTTT', NULL),
('DS502', 'Đồ án tốt nghiệp tại doanh nghiệp', 'Industry Capstone Project', 10, 10, 0, 'TN', NULL, NULL, NULL, NULL, 'KTTT', NULL),
('DS505', 'Khóa luận tốt nghiệp', 'Thesis', 10, 10, 0, 'KLTN', NULL, NULL, NULL, 'DS207', 'KTTT', 'Giúp sinh viên có kỹ năng đọc tài liệu thành thạo; có kỹ năng tiến hành nghiên cứu, giải quyết vấn đề; có kỹ năng thực hiện thí nghiệm, đánh giá; có kỹ năng viết luận văn; có kỹ năng trình bày. Sinh viên năm cuối đủ điều kiện sẽ được làm khóa luận tốt nghiệp dưới sự hướng dẫn của giảng viên. Theo đó sinh viên cần vận dụng các kiến thức và kỹ năng đã tích lũy được để giải quyết một vấn đề nghiên cứu cơ bản hoặc giải pháp thực tiễn thuộc lĩnh vực CNTT. Việc thực hiện khóa luận tốt nghiệp giúp sinh viên củng cố hoặc có thêm kiến thức và kỹ năng trong hoạt động chuyên môn như: đọc tài liệu, phát triển ý tưởng, lập trình, thực hiện thí nghiệm, đánh giá, viết luận văn, trình bày báo cáo, v.v.'),
('DSAL0', 'Cấu trúc dữ liệu và giải thuật', 'Cấu trúc dữ liệu và giải thuật', 5, 5, 0, 'CHUA_PHAN_LOAI', NULL, NULL, NULL, NULL, 'Chưa rõ', NULL),
('DSAL1', 'Cấu trúc dữ liệu và giải thuật', 'Data Structures and Algorithms', 4, 3, 1, 'ĐC', NULL, 'IT003', NULL, NULL, 'TTNN', NULL),
('DSAL2', 'Cấu trúc dữ liệu & giải thuật nâng cao', 'Advanced data structures and algorithms', 4, 3, 1, 'ĐC', NULL, 'CS523', NULL, NULL, 'KHMT', NULL),
('DTH039', 'Đô thị học đại cương', 'Introduction to Urban Studies', 3, 3, 0, 'CN', NULL, NULL, NULL, NULL, 'PĐTĐH', NULL),
('EC001', 'Kinh tế học đại cương', 'Principles of Economics', 4, 4, 0, 'CSN', NULL, NULL, NULL, NULL, 'HTTT', 'Cung cấp cho sinh viên những kiến thức tổng quan về kinh tế vi mô và kinh tế vĩ mô.'),
('EC002', 'Quản trị doanh nghiệp', 'Business Management', 3, 3, 0, 'CNTC', NULL, NULL, NULL, NULL, 'HTTT', 'Cung cấp cho sinh viên các lý thuyết cơ sở về quản trị học, các hoạt động của doanh nghiệp bao gồm quản trị tài chính, kinh doanh, nhân sự, sản xuất, nguồn cung ứng.'),
('EC003', 'Tiếp thị căn bản', 'Basic Marketing', 3, 3, 0, 'CN', NULL, 'EC101', NULL, NULL, 'HTTT', 'Giới thiệu cho sinh viên những khái niệm, tầm quan trọng, bản chất, các chức năng cơ bản của Marketing đối với các doanh nghiệp. Cung cấp kiến thức về môi trường Marketing, hành vi của người tiêu dùng và doanh nghiệp, cách phân khúc, lựa chọn và định vị thị trường, nằm được chiến lược sản phẩm, chiến lược giá, chiến lược phân phối, chiến lược xúc tiến, lập kế hoạch , tổ chức, thực hiện và kiểm soát Marketing. Cách thức hình thành và phát triển (một bước) năng lực thu thập thông tin, kỹ năng tổng hợp, hệ thống hóa các vấn đề trong mối quan hệ tổng thể; kỹ năng so sánh, phân tích, bình luận, đánh giá một kế hoạch Marketing.'),
('EC005', 'Giới thiệu ngành Thương mại Điện tử', 'Introduction to E-Commerce', 1, 1, 0, 'ĐC', NULL, 'CS005
IE005
IS005
SE005
CE005
NT005
NT015
DS005
AI001', NULL, NULL, 'HTTT', 'Môn học giúp sinh viên hiểu rõ ngành Thương mại điện tử và cách tiếp cận với môi trường tác nghiệp, chức năng, nhiệm vụ của người Cử nhân ngành Thương mại điện tử. Môn học giới thiệu về CNTT tổng quát, chuyên sâu ngành và những yêu cầu của cấp độ đào tạo Cử nhân ngành TMĐT.Trên cơ sở phương pháp luận tiếp cận hệ thống, nội dung môn học hướng sinh viên tới việc chủ động thực hiện quá trình tự đào tạo chính mình để trở thành một Cử nhân có phẩm chất đạo đức, có kiến thức và kỹ năng chuyên môn đáp ứng yêu cầu của xã hội.'),
('EC101', 'Marketing căn bản', 'Basic Marketing', 3, 3, 0, 'CN', NULL, NULL, NULL, NULL, 'HTTT', NULL),
('EC201', 'Phân tích thiết kế quy trình nghiệp vụ doanh nghiệp', 'Business Process Analysis and Design', 4, 3, 1, 'CSN', NULL, NULL, NULL, NULL, 'HTTT', 'Cung cấp kiến thức về các qui trình nghiệp vụ trong doanh nghiệp. Mô hình hóa các qui trình nghiệp vụ, tối ưu hóa qui trình và áp dụng vào các hệ thống ERP. Kiến thức về quản trị sự thay đổi nhằm áp dụng vào tái cơ cấu hệ thống doanh nghiệp.'),
('EC202', 'Nhập môn quản trị chuỗi cung ứng', 'Supply Chain Management', 4, 3, 1, 'CNTC', NULL, 'EC214', NULL, NULL, 'HTTT', 'Cung cấp Kiến thức về quản trị Logisitics, làm nền tảng cho quản trị chuỗi cung ứng. Các hệ thống ERP hỗ trợ cho công tác hoạch định, tổ chức, thực thi, kiểm tra chuỗi cung ứng trong doanh nghiệp.'),
('EC203', 'Quản trị quan hệ khách hàng và nhà cung cấp', 'Customer Relationship Management and Supplier Relationship Management', 4, 3, 1, 'CN', NULL, 'EC213', NULL, NULL, 'HTTT', NULL),
('EC204', 'Marketing điện tử', 'E-Marketing', 3, 2, 1, 'CN', NULL, NULL, NULL, NULL, 'HTTT', NULL),
('EC208', 'Quản trị dự án thương mại điện tử', 'E-Commerce Project Management', 3, 3, 0, 'CN', NULL, NULL, NULL, NULL, 'HTTT', 'Trình bày các khía cạnh quan trọng để triển khai thành công một đề án TMĐT bao gồm hành vi, chiến lược, kỹ thuật, định lượng, truyền thông. Cung cấp cho sinh viên các kiến thức liên quan đến việc quản lý và thực hiện dự án TMĐT đồng thời đề cập tới những yêu cầu kỹ năng của người quản lý dự án so với yêu cầu quản lý kỹ thuật.'),
('EC212', 'Thực tập doanh nghiệp', 'Industry Internship Program', 3, 3, 0, 'TTTN', NULL, 'EC222', NULL, NULL, 'HTTT', 'Sinh viên bắt buộc phải đi thực tập thực tế tại các doanh nghiệp và thực hiện báo cáo thực tập nộp về Khoa'),
('EC213', 'Quản trị quan hệ khách hàng và nhà cung cấp', 'Customer and Supplier Relationship Management', 3, 2, 1, 'CN', NULL, NULL, NULL, NULL, 'HTTT', 'Kiến thức về cách thức kết nối với khách hàng và nhà cung cấp bằng các phương tiện Internet (Email, website, forum, Chat, CRM, SRM..). Giải pháp và kỹ thuật cốt yếu để giữ gìn mối quan hệ với khách hàng và nhà cung cấp. Đảm bảo sự xuyên suốt của dòng thông tin cũng như sản phẩm từ nhà cung cấp, tới nhà sản xuất, nhà phân phối và người tiêu dùng.'),
('EC214', 'Nhập môn quản trị chuỗi cung ứng', 'Introduction to Supply Chain Management', 3, 3, 0, 'CNTC', NULL, NULL, NULL, NULL, 'HTTT', 'Cung cấp Kiến thức về quản trị Logisitics, làm nền tảng cho quản trị chuỗi cung ứng. Các hệ thống ERP hỗ trợ cho công tác hoạch định, tổ chức, thực thi, kiểm tra chuỗi cung ứng trong doanh nghiệp.'),
('EC219', 'Pháp luật trong thương mại điện tử', 'Law in E-Commerce', 3, 3, 0, 'CN', NULL, 'EC229', NULL, NULL, 'HTTT', NULL),
('EC222', 'Thực tập doanh nghiệp', 'Internship', 2, 2, 0, 'TTTN', NULL, NULL, NULL, NULL, 'HTTT', NULL),
('EC229', 'Pháp luật trong thương mại điện tử', 'Law in E-Commerce', 2, 2, 0, 'CN', NULL, NULL, NULL, NULL, 'HTTT', 'Môn học cung cấp các kiến thức về pháp luật, chính sách thương mại điện tử trong nước và trên thế giới, cập nhật hệ thống pháp luật điều chỉnh riêng cho hoạt động mua bán trên mạng, cung cấp các quy định về thông tin cá nhân, bản quyền, … và các vấn đề liên quan trên môi trường Internet.'),
('EC232', 'Nguyên lý kế toán', 'Principles of Accounting', 3, 3, 0, 'CNTC', NULL, NULL, NULL, NULL, 'HTTT', 'Cung cấp cho sinh viên những kiến thức cơ bản về Kế toán: nguyên tắc kế toán, phân loại kế toán yêu cầu của kế toán, giới thiệu hệ thống các quy định và hướng dẫn thực hiện của Luật Kế toán và Thông tư của Bộ tài chính, các phương pháp của kế toán: tổng hợp và cân đối kế toán, tài khoản kế toán, Chứng từ kế toán, Sổ sách và hình thức kế toán. Đồng thời hướng dẫn sinh viên cách hạch toán các nghiệp vụ kinh tế chủ yếu.'),
('EC301', 'Tiếp thị trực tuyến (E-Marketing)', 'E-Marketing', 4, 3, 1, 'CN', NULL, 'EC311', NULL, NULL, 'HTTT', NULL),
('EC302', 'Thiết kế Hệ thống Thương mại điện tử', 'E-Commerce System Design', 4, 3, 1, 'CN', NULL, 'EC312', NULL, NULL, 'HTTT', NULL),
('EC304', 'Tối ưu hóa công cụ tìm kiếm trong thương mại điện tử', 'Search Engine Optimization in E-Commerce', 3, 3, 0, 'CN', NULL, NULL, NULL, NULL, 'HTTT', NULL),
('EC311', 'Tiếp thị trực tuyến', 'E-Marketing', 3, 2, 1, 'CN', NULL, 'EC204', NULL, NULL, 'HTTT', 'Cung cấp những kiến thức Marketing hiện đại, bên cạnh việc sử dụng Internet để làm công cụ đưa sản phẩm ra thị trường. Kiến thức về lập kế hoạch E-Marketing (E-Marketing Mix)sử dụng chiến lược giá trực tuyến, các quảng cáo trực tyến, khuyến mãi, và các kênh phân phối internet để chiếm thị phần, hỗ trợ hoạt động kinh doanh.'),
('EC312', 'Thiết kế hệ thống thương mại điện tử', 'E-Commerce Systems Design', 3, 2, 1, 'CN', NULL, NULL, NULL, 'IS207', 'HTTT', 'Cung cấp các kiến thức về việc phân tích thiết kế các mô hình kinh doanh điện tử. Kiến thức về hệ thống bán hàng online, thanh toán và an toàn mạng.'),
('EC331', 'Quản trị chiến lược kinh doanh điện tử', 'E-Business Strategic Management', 3, 3, 0, 'CNTC', NULL, NULL, NULL, NULL, 'HTTT', 'Môn học trình bày các khái niệm về chiến lược và quản trị chiến lược bằng công cụ thẻ điểm cân bằng, bảng đồ chiến lược. Cách thức hoạch định, tổ chức, thực thi và kiểm tra việc vận hành doanh nghiệp theo chiến lược điện tử.'),
('EC332', 'Quản trị sản xuất', 'Production and Manufacturing Management', 3, 3, 0, 'CNTC', NULL, NULL, NULL, NULL, 'HTTT', 'Môn học trình bày các khái niệm, mô hình sản xuất và quản trị sản xuất theo Kanban, Lean, 6 Sigma, hoạch định nguồn lực bằng ERP.'),
('EC333', 'Quản trị tài chính doanh nghiệp', 'Financial Management', 3, 3, 0, 'CNTC', NULL, NULL, NULL, NULL, 'HTTT', 'Môn học này trang bị cho sinh viên những khái niệm, nguyên tắc và kỹ thuật cơ bản của quản trị tài chính và ứng dụng những nguyên tắc này trong việc ra quyết định của giám đốc tài chính: quyết định đầu tư, tài trợ và cổ tức. Sinh viên được làm quen với những vấn đề chính mà một giám đốc tài chính phải đối diện trong công ty. Những chủ đề chính bao gồm: sự bất cân xứng về thông tin, ra quyết định đầu tư trong điều kiện có rủi ro, cấu trúc vốn, phân chia cổ tức, phân tích báo cáo tài chính, dự báo và lên kế hoạch tài chính, phân tích các dự án để ra quyết định đầu tư dài hạn đến phân tích các nguồn tài trợ sẵn có để quyết định phương án huy động vốn.'),
('EC334', 'Quản trị kênh phân phối', 'Distribution Channel Management', 3, 3, 0, 'CNTC', NULL, NULL, NULL, NULL, 'HTTT', 'Giới thiệu các kiến thức cơ bản về việc tổ chức và điều hành hệ thống phân phối sản phẩm (chuỗi các nhà phân phối, đại lý, cửa hàng) được áp dụng trong các doanh nghiệp. Cụ thể, môn học này trang bị cho sinh viên các kiến thức cơ bản chính: vai trò, tầm quan trọng của hệ thống phân phối đối với doanh nghiệp trong việc thực hiện các mục tiêu của doanh nghiệp đề ra, việc thiết kế kênh phân phối được tiến hành ra sao?, chọn lựa và đánh giá các thành viên trong hệ thống phân phối như thế nào?, chính sách, biện pháp để kích thích các thành viên trong hệ thống phân phối hoạt động mang lại hiệu quả cao nhất theo mục tiêu đề ra… Bên cạnh việc cung cấp lý thuyết, các tình huống thực tế của một số doanh nghiệp lớn trên thị trường Việt Nam sẽ được giảng viên đưa ra để sinh viên thảo luận nhằm để đánh giá các tình huống.'),
('EC335', 'An toàn và bảo mật thương mại điện tử', 'Safety and Security in E-Commerce', 3, 3, 0, 'CN', NULL, NULL, NULL, 'IT005
IS207', 'HTTT', 'Môn học tập trung vào hai phần chính là an toàn hệ thống và bảo mật dữ liệu trong Thương mại điện tử. Trong phần bảo mật dữ liệu, học viên được cung cấp kiến thức về mã hóa thông tin và ứng dụng. Trong phần an toàn dữ liệu, học viên sẽ được cung cấp kiến thực về an toàn thông tin và cách dùng chúng để bảo vệ hệ điều hành, hệ thống mạng, hệ thống phần mềm.'),
('EC336', 'Quản trị nhân lực', 'Human Resources', 3, 3, 0, 'CNTC', NULL, NULL, NULL, NULL, 'HTTT', 'Môn học giới thiệu các chức năng quản trị nhân sự nhằm cung cấp cho sinh viên các kiến thức cần thiết để quản lý con người trong doanh nghiệp hiệu quả, bao gồm bốn lĩnh vực chủ yếu: hoạch định nguồn nhân lực, thu hút và tuyển chọn, đào tạo và phát triển, và duy trì – quản lý. Bên cạnh đó, sinh viên được trao dồi các kỹ năng quản lý như phân tích công việc, đánh giá kết quả làm việc, phỏng vấn ứng viên… Hoàn tất môn học, sinh viên sẽ có đủ khả năng dự đoán và giải quyết các vấn đề liên quan đến sử dụng lao động trong một tổ chức.'),
('EC337', 'Hệ thống thanh toán trực tuyến', 'E-Payment Systems', 3, 3, 0, 'CN', NULL, NULL, NULL, NULL, 'HTTT', 'Môn học giới thiệu các mô hình thanh toán truyền thống và hiện đại. Các vấn đề về an toàn và bảo mật thông tin thanh toán. Tích hợp các phương thức thanh toán trực tuyến vào hệ thống quản trị bán hàng và tài chính doanh nghiệp.'),
('EC338', 'Quản trị bán hàng', 'Sales Management', 3, 3, 0, 'CNTC', NULL, NULL, NULL, NULL, 'HTTT', 'Giúp cho học viên có được kiến thức và kỹ năng cơ bản về quản trị bán hàng, nhận thức về ngưới quản lý bán hàng và nhân viên bán hàng trong công ty, hiểu và vận dụng tốt các kỹ năng đối với nhà quản trị bán hàng, nắm được cách thức xây dựng và quản trị đội ngũ bán hàng, cuối cùng là nhận thức được tương lai phát triển của quản trị bán hàng.'),
('EC401', 'Khóa luận tốt nghiệp', 'Thesis', 10, 10, 0, 'KLTN', NULL, NULL, NULL, NULL, 'HTTT', NULL),
('EC402', 'Phát triển ứng dụng thương mại di động', 'Mobile Commerce Application Development', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'HTTT', NULL),
('EC403', 'Thương mại xã hội', 'Social Commerce', 3, 3, 0, 'CN', NULL, NULL, NULL, NULL, 'HTTT', NULL),
('EC404', 'Đồ án tốt nghiệp', 'Capstone Project', 6, 6, 0, 'TN', NULL, NULL, NULL, NULL, 'HTTT', NULL),
('EC405', 'Đồ án tốt nghiệp tại doanh nghiệp', 'Industry Capstone Project', 10, 10, 0, 'TN', NULL, NULL, NULL, NULL, 'HTTT', NULL),
('ECE02', 'Mạch số', 'Digital circuit', 4, 3, 1, 'ĐC', NULL, NULL, NULL, NULL, 'BMTL', NULL),
('ECON3313', 'Kinh tế tiền tệ', 'Monetary economics', 3, 3, 0, 'CNTC', NULL, NULL, NULL, NULL, 'HTTT', NULL),
('EN001', 'Anh văn 1', 'English 1', 4, 4, 0, 'ĐC', NULL, NULL, 'ENBT', NULL, 'BMAV', NULL),
('EN001.CO', 'English for Communication 1', 'English for Communication 1', 0, 0, 0, 'BT', NULL, NULL, NULL, NULL, 'HTTT', NULL),
('EN001.GE', 'General English', 'General English', 0, 0, 0, 'BT', NULL, NULL, NULL, NULL, 'HTTT', NULL),
('EN002', 'Anh văn 2', 'English 2', 4, 4, 0, 'ĐC', NULL, 'EN004
ENG01', 'EN001', NULL, 'BMAV', NULL),
('EN002.CO', 'English for Communication 1', 'English for Communication 1', 0, 0, 0, 'BT', NULL, NULL, NULL, NULL, 'HTTT', NULL),
('EN002.GE', 'General English', 'General English', 0, 0, 0, 'BT', NULL, NULL, NULL, NULL, 'HTTT', NULL),
('EN003', 'Anh văn 3', 'English 3', 4, 4, 0, 'ĐC', NULL, 'EN006
EN005
ENG02', 'EN002', NULL, 'BMAV', NULL),
('EN004', 'Anh văn 1', 'English 1', 4, 4, 0, 'ĐC', NULL, 'ENG01', NULL, NULL, 'BMAV', NULL),
('EN005', 'Anh văn 2', 'English 2', 4, 4, 0, 'ĐC', NULL, 'ENG02', NULL, NULL, 'BMAV', NULL),
('EN006', 'Anh văn 3', 'English 3', 4, 4, 0, 'ĐC', NULL, 'ENG03', NULL, NULL, 'BMAV', NULL),
('ENBT', 'Anh văn Bổ túc', 'English 0', 0, 0, 0, 'BT', NULL, NULL, NULL, NULL, 'BMAV', NULL),
('ENG00', 'Anh văn 0', 'English 0', 0, 0, 0, 'ĐC', NULL, NULL, NULL, NULL, 'TTNN', NULL),
('ENG01', 'Anh văn 1', 'English 1', 4, 4, 0, 'ĐC', NULL, 'INT001
EN004
EN002', NULL, NULL, 'TTNN', 'Môn Anh văn 1 là môn học đầu tiên trong các môn học Anh văn bắt buộc dành cho sinh viên chương trình đào tạo chính quy. Môn học nhằm trang bị kiến thức và kỹ năng sử dụng tiếng Anh trong môi trường làm việc nhầm giúp sinh viên đạt được trình độ tiếng Anh tương đương từ 30 đến dưới 42 điểm theo thang điểm đánh giá trình độ tiếng Anh toàn cầu GSE (The Global Scale of English) tương đương trình độ tiền trung cấp (pre-intermediate A2-B1).'),
('ENG02', 'Anh văn 2', 'English 2', 4, 4, 0, 'ĐC', NULL, 'INT003
EN005
EN003', 'ENG01', NULL, 'TTNN', 'Sau khi hoàn thành môn học này sinh viên có trình độ, kỹ năng tiếng Anh tương
đương TOEIC-400, sinh viên có thể:
\- Củng cố nền tảng căn bản về tiếng Anh tổng quát; thiết lập kỹ năng đọc hiểu;
hiểu và sử dụng các cấu trúc ngữ pháp đơn giản; củng cố kỹ năng giao tiếp căn
bản trong cuộc sống hàng ngày.
\- Xây dựng kỹ năng nghe nói, đọc hiểu và có được vốn từ vựng về các chủ đề
trong công sở, công việc hằng ngày: công ty, điện thoại, sản phẩm, sản xuất,
ngân hàng, tiền tệ, luật pháp và kinh doanh.
\- Rèn luyện kỹ năng viết email , báo cáo ngắn, bài miêu tả nhận xét sản phẩm,
thư phàn nàn, và thư xác nhận đặt hàng.'),
('ENG03', 'Anh văn 3', 'English 3', 4, 4, 0, 'ĐC', NULL, 'INT004
EN006', 'ENG02', NULL, 'TTNN', 'Môn học gồm 12 bài học (Units 01 – 12). Bài học bao gồm các hướng dẫn từng phần được kiểm tra trong bài test TOEIC như các điểm ngữ pháp, từ vựng, kỹ năng nghe và đọc nói và viết. Sinh viên cũng được cung cấp bài thi TOEIC thử cũng như các chiến thuật làm bài.'),
('ENG04', 'Anh văn 4', 'English 4', 4, 4, 0, 'ĐC', NULL, 'INT005', 'ENG03', NULL, 'TTNN', 'Môn học gồm 06 bài học của giáo trình Collins English for Exams: Skills for the TOEIC Test: Speaking and Writing, được chia ra trong 60 tiết học lý thuyết, thời gian học là 12 tuần.
\- Môn học cung cấp sinh viên kiến thức về kĩ năng làm bài hai kỹ năng Nói và Viết trong bài thi TOEIC,
\- Môn học bao gồm các bài học hướng dẫn các kỹ năng và kiến thức hỗ trợ cho hai kỹ năng Nói và Viết, như : từ vựng, ngữ pháp, phát âm, cấu trúc câu. Môn học còn cung cấp các chiến thuật làm bài thi TOEIC Nói và Viết, cũng như tạo cơ hội cho sinh viên rèn luyện kỹ năng làm bài thi.'),
('ENG05', 'Anh văn 5', 'English 5', 4, 4, 0, 'ĐC', NULL, 'INT006', 'ENG04', NULL, 'TTNN', 'Môn học gồm 03 bài học của giáo trình Collins English for Exams: Skills for the TOEIC Test: Speaking and Writing, được chia ra trong 60 tiết học lý thuyết, thời gian học là 12 tuần.
\- Môn học cung cấp cho sinh viên kiến thức chuyên sâu về kĩ năng làm bài hai kỹ năng Nói và Viết trong bài thi TOEIC.
\- Môn học bao gồm các bài học hướng dẫn các kỹ năng và kiến thức hỗ trợ cho hai kỹ năng Nói và Viết, như: từ vựng, ngữ pháp, phát âm, cấu trúc câu, tư duy phản biện trong trình bày câu trả lời phần Nói và Viết bài luận. Môn học còn cung cấp các chiến thuật làm bài thi TOEIC Nói và Viết, cũng như'),
('ENG06', 'Kỹ năng thuyết trình tiếng Anh', 'Effective Presentation', 4, 4, 0, 'CN', NULL, NULL, 'ENG03', NULL, 'TTNN', NULL),
('ENG07', 'Kỹ năng viết luận', 'Academic Writing', 4, 4, 0, 'CN', NULL, NULL, 'ENG03', NULL, 'TTNN', NULL),
('ENG11', 'Tiếng anh tăng cường I', 'Intensive English I', 0, 0, 0, 'ĐC', NULL, NULL, NULL, NULL, 'TTNN', NULL),
('ENG12', 'Tiếng anh tăng cường II', 'Intensive English II', 0, 0, 0, 'ĐC', NULL, NULL, NULL, NULL, 'TTNN', NULL),
('ENG13', 'Tiếng Anh I', 'English Composition I', 0, 0, 0, 'ĐC', NULL, NULL, NULL, NULL, 'TTNN', NULL),
('ENG14', 'Tiếng Anh II', 'English Composition II', 0, 0, 0, 'ĐC', NULL, NULL, NULL, NULL, 'TTNN', NULL),
('ENG15', 'Tiếng Anh chuyên ngành CNTT', 'English for computer science', 0, 0, 0, 'ĐC', NULL, NULL, NULL, NULL, 'TTNN', NULL),
('ENGA1', 'Anh văn sơ cấp 1', 'Anh văn sơ cấp 1', 0, 0, 0, 'BT', NULL, NULL, NULL, NULL, 'TTNN', NULL),
('ENGA2', 'Anh văn sơ cấp 2', 'Anh văn sơ cấp 2', 0, 0, 0, 'BT', NULL, NULL, NULL, NULL, 'TTNN', NULL),
('ENGBT', 'Anh văn bổ túc', 'English 0', 0, 0, 0, 'ĐC', NULL, NULL, NULL, NULL, 'TTNN', NULL),
('ENGL1113', 'Tiếng Anh 1', 'English Composition 1', 3, 3, 0, 'ĐC', NULL, 'ENG06', NULL, NULL, 'HTTT', 'Cung cấp những kiến thức và kỹ năng Tiếng Anh cơ bản làm nền tảng vững chắc giúp sinh viên có thể tiếp thu thuận lợi những bài học ở cấp độ cao hơn bằng tiếng Anh.'),
('ENGL1213', 'Tiếng Anh 2', 'English Composition 2', 3, 3, 0, 'ĐC', NULL, 'ENG07', 'ENGL1113', NULL, 'HTTT', 'Cung cấp những kiến thức và kỹ năng Tiếng Anh nâng cao làm nền tảng vững chắc giúp sinh viên có thể tiếp thu thuận lợi những bài học ở cấp độ cao bằng tiếng Anh.'),
('ENLS1', 'Nâng cao kỹ năng nghe, nói tiếng Anh 1', 'Nâng cao kỹ năng nghe, nói tiếng Anh 1', 0, 0, 0, 'ĐC', NULL, NULL, NULL, NULL, 'TTNN', NULL),
('ENLS2', 'Nâng cao kỹ năng nghe, nói tiếng Anh 2', 'Nâng cao kỹ năng nghe, nói tiếng Anh 2', 0, 0, 0, 'ĐC', NULL, NULL, NULL, NULL, 'TTNN', NULL),
('ENRW1', 'Nâng cao kỹ năng đọc, viết tiếng Anh 1', 'Nâng cao kỹ năng đọc, viết tiếng Anh 1', 0, 0, 0, 'ĐC', NULL, NULL, NULL, NULL, 'TTNN', NULL),
('ENRW2', 'Nâng cao kỹ năng đọc, viết tiếng Anh 2', 'Nâng cao kỹ năng đọc, viết tiếng Anh 2', 0, 0, 0, 'ĐC', NULL, NULL, NULL, NULL, 'TTNN', NULL),
('GDH075', 'Tâm lý học giao tiếp', 'Communication Psychology', 2, 1, 1, 'CN', NULL, NULL, NULL, NULL, 'PĐTĐH', NULL),
('HCMT1', 'Tư tưởng Hồ Chí Minh', 'Ho Chi Minh''s Ideology', 2, 2, 0, 'ĐC', NULL, 'SS003', NULL, NULL, 'PĐTĐH', NULL),
('HCMT2', 'Tư tưởng Hồ Chí Minh', 'Tư tưởng Hồ Chí Minh', 3, 3, 0, 'CHUA_PHAN_LOAI', NULL, NULL, NULL, NULL, 'Chưa rõ', NULL),
('IE005', 'Giới thiệu ngành Công nghệ Thông tin', 'Introduction to Information Technology', 1, 1, 0, 'ĐC', NULL, 'CS005
IS005
SE005
CE005
NT005
NT015
EC005
DS005
AI001', NULL, NULL, 'KTTT', 'Môn học trình bày về sự cần thiết của ngành CNTT trong các doanh nghiệp, tổ chức; Phân biệt ngành CNTT và các chuyên ngành khác trong thực tiễn. Vị trí và cơ hội nghề nghiệp; Định hướng phát triển CNTT trong tương lai.'),
('IE101', 'Cơ sở hạ tầng công nghệ thông tin', 'Information Technology Infrastructure', 3, 2, 1, 'CSN', NULL, NULL, NULL, NULL, 'KTTT', 'Môn học cung cấp một cái nhìn tổng quan về ngành Công nghệ Thông tin, các chủ đề phổ biến trong CNTT. Môn học mô tả mối quan hệ giữa CNTT với các ngành liên quan, môn học có tính khai tâm và giúp sinh viên nhận thức về CNTT. Ngoài ra môn học giúp sinh viên hiểu biết về các bối cảnh đa dạng mà trong đó CNTT sẽ được ứng dụng.'),
('IE102', 'Các công nghệ nền', 'Platform Technologies', 3, 2, 1, 'CSN', NULL, NULL, NULL, NULL, 'KTTT', 'Môn học trình bày những tri thức nền của phần cứng, phần mềm và các cách thức tích hợp giữa chúng để tạo nên những thành phần cần thiết của các hệ thống CNTT. Các tri thức này giúp các chuyên gia CNTT trong việc: chọn lựa, triển khai, tích hợp và quản trị những kĩ thuật để hỗ trợ cho các cơ sở hạ tầng CNTT.'),
('IE103', 'Quản lý thông tin', 'Information Management', 4, 3, 1, 'CSN', NULL, NULL, NULL, 'IT004', 'KTTT', 'Môn học trình bày các kiến thức liên quan đến việc: thu thập, tổ chức, mô hình, chuyển đổi, trình bày, an toàn và an ninh của dữ liệu và thông tin. Các kiến thức này giúp các chuyên gia CNTT trong việc quản lý, tích hợp, phát triển dữ liệu và thông tin cho các tổ chức.'),
('IE104', 'Internet và công nghệ web', 'Internet and Web Technology', 4, 3, 1, 'CSN', NULL, NULL, NULL, 'IT001
IT005', 'KTTT', 'Môn học này cung cấp kiến thức về công nghệ Web thông qua việc giới thiệu về hệ thống, tổ chức, xây dựng, và sử dụng các hệ thống và ứng dụng trên Web. Hai mảng kiến thức chính là: kiến thức chung về Internet và Web, và kiến thức và kỹ năng xây dựng ứng dụng Web. Các chủ đề chính bao gồm: giao thức HTTP, Web markups, lập trình client và server, Web services; XHTML, XML, SVG, CSS, Javascript; hosting, sử dụng và chia sẻ thông tin trên Internet, tìm kiếm và hỗ trợ nghiên cứu thông qua Web, vấn đề bản quyền và sử dụng Web an toàn, cùng một số chủ đề mở rộng như các dạng dữ liệu đa phương tiện trên Web, giao diện người dùng Web, Web 2.0 và Web ngữ nghĩa (Web 3.0).'),
('IE105', 'Nhập môn bảo đảm và an ninh thông tin', 'Introduction to Information Assurance and Security', 4, 3, 1, 'CSN', NULL, NULL, NULL, NULL, 'KTTT', 'Môn Nhập môn bảo đảm và an ninh thông tin gồm các nội dung cơ bản như sau: Tổng quan về các nguyên tắc an ninh Mạng máy tính, xây dựng một tổ chức an toàn, quyền truy cập và kiểm soát truy cập, phương thức ngăn chặn việc tấn công hệ thống, bảo vệ chống lại việc tấn công Botnet, các vấn đề bảo vệ hệ thống mạng chống phần mềm độc hại, an ninh mạng Windows và Unix / Linux, bảo mật mạng truyền dẫn; bảo mật mạng LAN, mạng không dây và mạng di động.'),
('IE106', 'Thiết kế giao diện người dùng', 'User Interface Design', 4, 3, 1, 'CSN', NULL, NULL, NULL, 'IT001', 'KTTT', 'Môn học này cung cấp các kiến thức cơ bản như: vấn đề khả dụng của các hệ thống tương tác, các nguyên tắc quan trọng của thiết kế giao diện người dùng, quá trình thiết kế giao diện, đánh giá thiết kế giao diện; bên cạnh việc giới thiệu một số dạng kỹ thuật thiết kế liên quan đến thực đơn, form fill-in, hộp hội thoại, tài liệu người dùng, giao diện tìm kiếm, mối liên hệ giữa giao diện người dùng với trực quan thông tin; cũng như giới thiệu sơ về các mảng công nghệ mới.'),
('IE107', 'Thiết kế giao diện người dùng', 'Thiết kế giao diện người dùng', 4, 3, 1, 'CSN', NULL, NULL, NULL, NULL, 'KTTT', NULL),
('IE108', 'Phân tích thiết kế phần mềm', 'Software Analysis and Design', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'KTTT', NULL),
('IE201', 'Xử lý dữ liệu thống kê', 'Analysis of Statistical Data', 3, 3, 0, 'CN', NULL, NULL, NULL, 'MA005
MA006', 'KTTT', 'Môn học nhằm trình bày những phương pháp sơ cấp cơ bản để xử lý, mô tả, phân tích, phán đoán, các dữ liệu thống kê, cùng với cơ sở lí luận xác suất của các phương pháp đó. Môn học gồm 4 chương: Mô tả đầy đủ các phân phối; Mô tả đặc trưng các phân phối; Phân tích tương quan và hồi qui; Phương pháp kiểm định.'),
('IE202', 'Quản trị doanh nghiệp', 'Business management', 3, 3, 0, 'CN', NULL, NULL, NULL, NULL, 'KTTT', 'Nội dung môn học bao gồm những kiến thức cơ bản về quản trị kinh doanh; một số các bộ môn cơ bản của lĩnh vực quản trị học; và kiến thức cơ bản về áp dụng HTTT trong quản lý doanh nghiệp.'),
('IE203', 'Hệ thống quản trị qui trình nghiệp vụ', 'Business Process Management Systems', 4, 3, 1, 'CĐTN', NULL, NULL, NULL, 'IT001
IT004', 'KTTT', 'Giải pháp BPM có mối quan hệ chặt chẽ đến 2 lĩnh vực: Công nghệ thông tin và Quản lý. Xét về mặt quản lý, đây là cách tiếp cận có hệ thống nhằm giúp các tổ chức tiêu chuẩn hóa và tối ưu hóa các quy trình hoạt động với mục đích giảm chi phí, tăng chất lượng hoạt động nhằm đạt được các mục tiêu cần thiết. Về mặt công nghệ, BPM là một bộ công cụ giúp các tổ chức thiết kế, mô hình hóa, triển khai, giám sát, vận hành và cải tiến các quy trình nghiệp vụ một cách linh hoạt.
Môn học trang bị các kiến thức cơ bản về quy trình nghiệp vụ, mô hình hóa quy trình, Business Process Modeling Notation - BPMN; kỹ năng mô hình hoá quy trình; kỹ năng xây dựng ứng dụng quản lý điều hành theo các quy trình đã được mô hình hóa cho các tổ chức, doanh nghiệp.'),
('IE204', 'Tối ưu hóa công cụ tìm kiếm', 'Search Engine Optimization', 4, 3, 1, 'CĐTN', NULL, NULL, NULL, 'IE104', 'KTTT', 'Tối ưu hóa công cụ tìm kiếm là một tập hợp các phương pháp nhằm nâng cao thứ hạng của một website trong các trang kết quả của các công cụ tìm kiếm và được coi là một lĩnh vực nhỏ của tiếp thị số. Bao gồm các phương pháp :
\- Tối ưu hóa website: tác động mã nguồn HTML, cấu trúc, layout và nội dung website bao gồm text, ảnh, video hay đa phương tiện khác trên web mà người dùng nhìn thấy hay tương tác được.
\- Xây dựng các liên kết hữu ích bên trong website (Internal links) và từ các trang uy tín bên ngoài (Inbound links) đến trang để các công cụ tìm kiếm chọn lựa trang web phù hợp nhất phục vụ người tìm kiếm trên Internet ứng với một từ khóa cụ thể được người dùng truy vấn và đặt được yêu cầu cần tìm.'),
('IE205', 'Xử lý ảnh vệ tinh', 'Satellite Image Processing', 3, 3, 0, 'CN', NULL, NULL, NULL, 'IS351', 'KTTT', 'Tổng quát về quá trình thu thập, xử lý dữ liệu và hệ thống xử lý dữ liệu ảnh vệ tinh; khảo sát các thành phần chủ yếu trong xử lý ảnh số bao gồm hiệu chỉnh bức xạ, nắn chỉnh hình học, tăng cường chất lượng ảnh, phân loại ảnh; và ứng dụng các kỹ thuật xử lý ảnh trong phát hiện biến động.'),
('IE206', 'Đồ án chuẩn bị tốt nghiệp', 'Preparing for the Graduation Project', 2, 0, 2, 'ĐA', NULL, NULL, NULL, NULL, 'KTTT', NULL),
('IE207', 'Đồ án', 'Project', 2, 0, 2, 'ĐA', NULL, NULL, NULL, 'IT001
IT004', 'KTTT', 'Môn học Đồ án là môn học tổ chức trong các học kỳ chính, nhằm trang bị cho sinh viên những bước bắt đầu để có thể hoàn thành khóa luận tốt nghiệp (bắt buộc) hoặc thực hiện các môn chuyên đề (không bắt buộc). Các kỹ năng được trang bị cho sinh viên bao gồm khả năng phân tích và giải quyết vấn đề/bài toán; kỹ năng thiết kế; kỹ năng thực hành và hiện thực thiết kế; khả năng thực nghiệm, mô phỏng và đánh giá kết quả; kỹ năng trình bày, viết báo cáo theo từng giai đoạn của một dự án; kỹ năng giao tiếp và làm việc nhóm; kỹ năng thuyết trình. Môn học được thực hiện trong từ 10 tuần đến 15 tuần trong một học kỳ dưới sự hướng dẫn của một giảng viên do nhóm sinh viên đăng ký.'),
('IE208', 'Kiến trúc và tích hợp hệ thống', 'System Integration and Architecture', 3, 3, 0, 'CNTC', NULL, NULL, NULL, NULL, 'KTTT', NULL),
('IE209', 'Công nghệ Java', 'Java Technology', 4, 3, 1, 'CNTC', NULL, 'IE303', NULL, 'IT002', 'KTTT', NULL),
('IE210', 'Hệ thống định vị toàn cầu (GPS)', 'Global Positioning System', 3, 3, 0, 'CNTC', NULL, NULL, NULL, NULL, 'KTTT', NULL),
('IE211', 'Tin học môi trường', 'Environmental Informatics', 2, 2, 0, 'CNTC', NULL, NULL, NULL, NULL, 'KTTT', NULL),
('IE212', 'Công nghệ dữ liệu lớn', 'Big Data Technology', 4, 3, 1, 'CN', NULL, NULL, NULL, 'IT007', 'KTTT', 'Môn học giới thiệu tổng quan về khái niệm, đặc trưng cũng như những thách thức của Big data (Khả năng phân tích, dự đoán nhằm trích xuất một giá trị lớn hơn từ dữ liệu). Giới thiệu một số phương pháp và công cụ phổ biến để khai thác và quản lý Big data (Hadoop, MapReduce và Spark).'),
('IE213', 'Kỹ thuật phát triển hệ thống web', 'Web System Development Techniques', 4, 3, 1, 'CN', NULL, NULL, NULL, 'IE104', 'KTTT', 'Môn học cung cấp các kiến thức,kỹ năng xây dựng, triển khai một website trong thực tế, cung câp các kiến thức về ngôn ngữ lập trình web như PHP, ASP.NET, nodejs, các web frameword trong việc xây dựng một website.'),
('IE216', 'Các chủ đề toán học cho khoa học dữ liệu', 'Topics in Mathematics of Data Science', 3, 3, 0, 'CNTC', NULL, NULL, NULL, NULL, 'KTTT', NULL),
('IE217', 'Máy học', 'Máy học', 4, 3, 1, 'CNTC', NULL, 'DS102', NULL, NULL, 'KTTT', NULL),
('IE218', 'Xử lý dữ liệu lớn', 'Big Data Processing', 4, 3, 1, 'CNTC', NULL, NULL, NULL, NULL, 'KTTT', NULL),
('IE221', 'Kỹ thuật lập trình Python', 'Python Programming Techniques', 4, 3, 1, 'CN', NULL, NULL, NULL, 'IT001', 'KTTT', 'Kỹ thuật lập trình Python là một môn học tự chọn quan trọng để hỗ trợ cho định hướng Dữ liệu lớn và Khoa học dữ liệu thuộc ngành Công nghệ thông tin. Môn học gồm các nội dung chính như sau: (1) Giới thiệu khái quát lịch sử ngôn ngữ lập trình Python và vai trò của nó trong cách mạng công nghiệp 4.0. (2) Ngữ nghĩa cú pháp ngôn ngữ lập trình Python. (3) Nguyên lý hướng đối tượng trong Python. (4) Các thư viện phổ biến nhất hỗ trợ lập trình trong Python. (5) Xây dựng các ứng dụng bằng Python.
Bên cạnh đó, môn học trang bị thêm một số kỹ năng hướng dẫn đọc tài liệu thành thạo, kỹ năng tiến hành nghiên cứu, kỹ năng viết báo cáo, trình bày thuyết minh đề tài và đặc biệt làm việc nhóm, phối hợp với nhau để hoàn thành thuyết minh đề tài.'),
('IE222', 'Phân tích dữ liệu bằng Python', 'Phân tích dữ liệu bằng Python', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'KTTT', NULL),
('IE223', 'Phân tích dữ liệu bằng Python và R', 'Data Analysis with Python and R', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'KTTT', NULL),
('IE224', 'Phân tích dữ liệu', 'Data Analysis', 4, 3, 1, 'CN', NULL, 'IE313', NULL, NULL, 'KTTT', 'Môn học giới thiệu các khái niệm về phân tích dữ liệu, các mô hình phân tích dữ liệu hiện tại, cách chọn mô hình phân tích dữ liệu thích hợp với nguồn dữ liệu. Cung cấp các kiến thức nâng cao để người học có thể tự thiết kế các mô hình nghiên cứu trong phân tích dữ liệu. Môn học còn trang bị các kiến thức toán cơ bản thống kê trong phân tích dữ liệu. Bên cạnh đó, môn học cũng cung cấp cách trực quan hóa dữ liệu sau khi phân tích. Trong môn học này, Python đóng vai trò chính trong quá trình phân tích dữ liệu. Python chủ yếu tập trung vào các mô-đun/package sau: NumPy, Pandas, MatlotLib.
Ngoài ra, môn học trang bị thêm một số kỹ năng hướng dẫn đọc tài liệu thành thạo (đọc hiểu các project requirements document), kỹ năng tiến hành nghiên cứu, kỹ năng viết báo cáo, trình bày thuyết minh xây dựng đề tài và đặc biệt làm việc nhóm, phối hợp với nhau để hoàn thành thuyết minh đề tài.'),
('IE225', 'Mạng kết nối', 'Interconnection Networks', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'KTTT', 'Mạng kết nối là một lĩnh vực nghiên cứu về sự kết nối giữa các thành phần khác nhau của hệ thống máy tính. Mạng kết nối diễn ra trên nhiều phạm vi từ on-chip networks (OCN)/Networks-on-Chip (NoCs) đến việc điều chỉnh các mạng tốc độ cao trong các máy tính hiệu năng cao, đến các mạng cáp quang trong trung tâm dữ liệu. Sự phát triển mạnh ngày càng tăng về tính song song, tính toán phân tán và hiệu quả năng lượng trên tất cả các hệ thống này làm cho việc thiết kế kết cấu truyền thông trở nên quan trọng đối với cả hiệu suất cao và tiêu thụ điện năng thấp. Khóa học này kiểm tra kiến trúc, phương pháp thiết kế và ưu nhược điểm của các mạng kết nối. Trọng tâm tổng thể của khóa học sẽ là các kiến trúc mạng kết nối được sử dụng trong các hệ thống đa bộ xử lý và nhiều lõi, và thiết kế cho các yêu cầu giao tiếp của các kiến trúc song song khác nhau và các cơ chế kết hợp bộ đệm.'),
('IE226', 'Đồ họa và trực quan hóa máy tính', 'Computer Graphics and Visualization', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'KTTT', 'Sinh viên được khám phá nhiều chủ đề trong dựng (rendering), trực quan hóa thông tin và mô hình hóa các đối tượng 3D. Mục tiêu của môn học này là phát triển các thuật toán để tạo, biểu diễn, giải thích, hiển thị và tương tác với dữ liệu. Đặc biệt, các bộ dữ liệu phức tạp và lớn nằm trong trọng tâm của môn học, vì chúng đóng vai trò ngày càng quan trọng trong nhiều ứng dụng khoa học, y tế và kỹ thuật.'),
('IE227', 'Xử lý tín hiệu số cho mạng', 'Signal Processing over Networks', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'KTTT', 'Môn học này cung cấp cho sinh viên các kiến thức và kỹ thuật tổng quan về xử lý tín hiệu số trên các miền tín hiệu khác nhau như không gian (spaces) và thời gian (times). Đối với xử lý tín hiệu số thông qua các mạng (networks), kiến trúc của mạng sẽ ảnh hưởng đến cấu trúc và ràng buộc của tín hiệu thông tin. Các nội dung chính bao gồm: các thuật toán lọc, phát hiện và tối ưu truyền thông tin, cách thiết kế mô hình mạng, phương pháp đánh giá độ lợi thông tin thu được.'),
('IE228', 'Tương tác người-máy Việt Nhật', 'Human-Computer Interaction', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'KTTT', 'Môn học cung cấp các kiến thức cơ bản về hoạch định, thiết kế và khai thác sự giao tiếp giữa con người và máy tính. Các nội chính bao gồm: các nguyên lý tương tác, các phương pháp và công cụ thiết kế giao diện, các mẫu thiết kế phổ biến và phương pháp đánh giá chất lượng của hệ thống tương tác người - máy.'),
('IE229', 'Trí tuệ nhân tạo Việt Nhật', 'Artificial Intelligence', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'KTTT', 'Môn học cung cấp các kiến thức tổng quan về trí tuệ nhân tạo, các vấn đề và phương pháp giải quyết được nghiên cứu trong lĩnh vực trí tuệ nhân tạo. Nội dung chính mà môn học cung cấp bao gồm: các khái niệm cơ bản về thuật giải, thuật toán, các phương pháp tìm kiếm lời giải cho bài toán, các phương pháp heuristic. Ngoài ra, môn học cũng giới thiệu sơ lược về máy học (machine learning), các mô hình máy học cơ bản (học có giám sát, học không giám sát, ...) và các phương pháp đánh giá mô hình máy học.'),
('IE230', 'Viết báo cáo kỹ thuật bằng tiếng Nhật', 'Japanese Technical Writing', 2, 0, 2, 'CN', NULL, NULL, NULL, NULL, 'KTTT', 'Môn học trang bị các kiến thức, kỹ năng để giúp người học có cái nhìn tổng thể về báo cáo kỹ thuật trong ngành CNTT. Chúng được chia làm nhiều chủ đề: (1) Giới thiệu báo cáo kỹ thuật, tầm quan trọng của một báo cáo kỹ thuật, trình bày các báo cáo kỹ thuật mẫu. (2) Phương pháp viết một bài báo cáo kỹ thuật đúng chuẩn, phân loại các báo cáo kỹ thuật. (3) Kỹ năng thuyết minh đề tài để làm nổi bật sản phẩm/ứng dụng/nghiên cứu đang triển khai. (4) Áp dụng các kiến thức đã học để xây dựng một báo cáo kỹ thuật đúng chuẩn.'),
('IE231', 'Quản trị doanh nghiệp công nghệ thông tin', 'Information Technology Enterprise Management', 3, 3, 0, 'CN', NULL, NULL, NULL, NULL, 'KTTT', NULL),
('IE232', 'Nhập môn trí tuệ nhân tạo', 'Introduction to Artificial Intelligence', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'KTTT', NULL),
('IE233', 'Phân tích và mô hình mạng xã hội', 'Analysis and Modeling of Social Networks', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'KTTT', NULL),
('IE301', 'Quản trị quan hệ khách hàng', 'Customer Relationship Management', 3, 3, 0, 'CNTC', NULL, NULL, NULL, NULL, 'KTTT', 'Môn học này giúp người học nghiên cứu tổng thể toàn bộ các khía cạnh của quản trị quan hệ khách hàng (CRM): chiến lược, tổ chức, marketing và công nghệ thông tin; nó còn giúp người học biết và hiểu các kiến thức cơ bản về việc tổ chức và điều hành các hoạt động chăm sóc khách hàng. Cụ thể, môn học này trang bị cho người học các kiến thức cơ bản về vai trò và tầm quan trọng của quản trị quan hệ khách hàng, chiến lược quan hệ khách hàng, quản trị cơ sở dữ liệu khách hàng, quản trị xung đột và duy trì sự hài lòng của khách hàng. Môn học cũng cung cấp kiến thức về kiểm tra đánh giá và điều chỉnh hoạt động quản trị quan hệ khách hàng.'),
('IE302', 'Kiến trúc và tích hợp hệ thống', 'System Integration and Architecture', 3, 3, 0, 'CNTC', NULL, NULL, NULL, NULL, 'KTTT', 'Môn học trình bày các kiến thức liên quan đến các kỹ năng thu thập yêu cầu, tìm nguồn cung ứng, đánh giá và tích hợp các thành phần vào một hệ thống duy nhất. Nó cũng bao gồm các nguyên tắc cơ bản của quản lý dự án và sự tương tác giữa các ứng dụng CNTT với các quy trình của tổ chức. Môn học trình bày các tri thức như: quản lý dự án; kiểm định và đảm bảo chất lượng; tích hợp và phát triển hệ thống; tìm nguồn cung ứng.'),
('IE303', 'Công nghệ Java', 'Java Technology', 4, 3, 1, 'CĐTN', NULL, NULL, NULL, 'IT002', 'KTTT', 'Môn học được thiết kế đặc biệt chú trọng vào việc giúp sinh viên hiểu biết nắm vững kiến thức nền tảng về Công nghệ Java. Đồng thời làm chủ các công nghệ như: Java SE, Servlet, JSP, Enterprise Java Beans…. Để hướng đến mục tiêu xây dựng những Enterprise applications một cách chuyên nghiệp.'),
('IE304', 'Hệ thống định vị toàn cầu', 'Global Positioning System', 3, 3, 0, 'CNTC', NULL, NULL, NULL, 'IS251', 'KTTT', 'Môn học này cung cấp một cái nhìn tổng quan toàn diện từ các chức năng hệ thống GPS đến các ứng dụng. Môn học trang bị cho sinh viên các kiến thức về các hệ thống vệ tinh định vị với sự chú trọng trên GPS từ cơ sở toạ độ sử dụng, kiến thức về quĩ đạo vệ tinh, tín hiệu truyền từ vệ tinh, các thiết bị phần cứng - phần mềm GPS, và sự tác động của môi trường truyền sóng vào độ chính xác định vị.'),
('IE305', 'Tin học môi trường', 'Environmental Informatics', 2, 2, 0, 'CNTC', NULL, NULL, NULL, 'IE101
IS251', 'KTTT', 'Để xây dựng được hệ thống quản lý rủi ro môi trường, sinh viên sẽ được trang bị những kiến thức cơ bản về rủi ro môi trường và giải pháp công nghệ trong việc xây dựng hệ thống quản lý dữ liệu môi trường như thành lập các bản đồ rủi ro môi trường, xây dựng phần mềm tích hợp GIS (Application GIS, Web GIS, Mobile GIS), phân tích và khai thác dữ liệu không gian phục vụ việc ra quyết định của các nhà quản lý.'),
('IE307', 'Công nghệ lập trình đa nền tảng cho ứng dụng di động', 'Cross-Platform Mobile Development', 4, 3, 1, 'CNTC', NULL, NULL, NULL, NULL, 'KTTT', 'Môn học trình bày nguyên lý cơ bản của các Framework về lập trình di động đa nền tảng (React Native, PhoneGap, Xamarin...) và đặc biệt là Xamarin Framework. Cung cấp các Controls cơ bản của Xamarin, và áp dụng để xây dựng ứng dụng đa nền tảng: Label, Entry, Button, Image, Switch, ListView, DatePicker, TimePicker. Bên cạnh đó, môn học còn cung cấp thêm các vấn đề nâng cao của Xamarin, để tiếp tục tự nghiên cứu sử dụng về sau của Camera, Notification, Google Map APIs, Grial, RESTful API, Syncfusion... Môn học trang bị kỹ năng làm việc nhóm theo môi trường doanh nghiệp, đọc hiểu Requirement của khách hàng về ứng dụng di động, có khả năng Phân tích & Thiết kế các ứng dụng di động để xây dựng một ứng dụng di động đa nền tảng cơ bản chạy trên iOS, Android & Windows Phone theo yêu cầu.'),
('IE309', 'Thực tập doanh nghiệp', 'Internship', 2, 2, 0, 'TTTN', NULL, NULL, NULL, NULL, 'KTTT', 'Các doanh nghiệp hướng dẫn sinh viên tham gia, thực hiện các dự án CNTT liên quan để trải nghiệm môi trường nghề nghiệp thực tế. Sinh viên tham gia nghiêm túc và thực hiện báo cáo thực tập nộp về đơn vị chuyên môn.'),
('IE310', 'Tư duy thiết kế', 'Design Thinking', 3, 3, 0, 'CN', NULL, NULL, NULL, NULL, 'KTTT', NULL),
('IE313', 'Phân tích và trực quan dữ liệu', 'Data Analysis and Visualization', 4, 3, 1, 'CN', NULL, NULL, NULL, 'IT001', 'KTTT', NULL),
('IE400', 'Chuyên đề tốt nghiệp', 'Graduate Seminars', 4, 4, 0, 'TN', NULL, NULL, NULL, NULL, 'KTTT', NULL),
('IE401', 'Tin-Sinh học', 'Bioinformatics', 3, 3, 0, 'CĐTN', NULL, NULL, NULL, 'IT003', 'KTTT', 'Tin sinh học là một lĩnh vực khoa học sử dụng các công nghệ của các ngành: toán học ứng dụng, tin học, thống kê, khoa học máy tính, trí tuệ nhân tạo, hóa học và hóa sinh để giải quyết các vấn đề sinh học.
Nội dung Tin sinh học bao gồm: Sinh học phân tử. Giới thiệu một số ngân hàng cơ sở dữ liệu sinh học trên Internet. Thuật toán giải một số bài toán cơ bản trong Tin sinh học. Xây dựng phần mềm Tin sinh học'),
('IE402', 'Hệ thống thông tin địa lý 3 chiều', 'Three-Dimensional Geographic Information Systems', 4, 3, 1, 'CĐTN', NULL, NULL, NULL, 'IT004', 'KTTT', 'Môn học cung cấp lịch sử, mục đích, các khái niệm cơ sở của GIS 3D, cách phân nhóm cho mô hình dữ liệu 3D và mô tả chi tiết cho mỗi mô hình dữ liệu GIS 3D. Môn học cũng cung cấp cho sinh viên các ưu điểm, hạn chế của mô hình trên một số tiêu chí khi triển khai vào các bài toán thực tiễn.'),
('IE403', 'Khai thác dữ liệu truyền thông xã hội', 'Social Media Mining', 3, 3, 0, 'CĐTN', NULL, NULL, NULL, NULL, 'KTTT', 'Môn học này sẽ trình bày các khái niệm cơ bản, các vấn đề đang nổi lên, và các thuật toán để phân tích hiệu quả mạng và khai thác dữ liệu. Chương trình cung cấp các kiến thức về Social Media Mining và có các bài tập đi kèm mỗi chương với độ khó khác nhau để nâng cao sự hiểu biết và giúp áp dụng được các khái niệm, nguyên tắc, phương pháp trong các bài toán khai thác dữ liệu truyền thông xã hội.'),
('IE404', 'Khai phá truyền thông xã hội', 'Social Media Mining', 3, 3, 0, 'CĐTN', NULL, NULL, NULL, NULL, 'KTTT', NULL),
('IE405', 'Công nghệ phân tích dữ liệu lớn', 'Big Data Analysis Technologies', 4, 3, 1, 'CĐTN', NULL, NULL, NULL, NULL, 'KTTT', 'Môn học giới thiệu các kiến thức và các công nghệ phân tích dữ liệu lớn nhằm tìm kiếm tri thức từ dữ liệu lớn hỗ trợ tiến trình ra quyết định.
Môn học cung cấp khái niệm về dữ liệu lớn bao gồm 5 đặc điểm được viết tắt là 5V và các công cụ, kỹ thuật để lưu trữ và phân tích dữ liệu lớn như HDFS, MapReduce, Apache Spark, Mahout, hệ cơ sở dữ liệu NoSQL. Môn học còn giới thiệu cách dùng ngôn ngữ Python, Java, Scala để phân tích dữ liệu lớn. Cuối cùng, môn học giới thiệu vài ứng dụng của big data trong thực tiễn.'),
('IE406', 'Nhập môn ẩn thông tin và ứng dụng', 'Data Hiding Fundamentals and Applications', 3, 3, 0, 'TN', NULL, NULL, NULL, NULL, 'KTTT', 'Hiện nay, khi các vấn đề liên quan đến an toàn và bảo mật thông tin ở và Việt Nam và thế giới đang được quan tâm, phương pháp ẩn thông tin là một lĩnh vực nghiên cứu nhằm quyết hiệu quả hơn các vấn đề liên quan đến an toàn và bảo mật thông tin trên các dữ liệu dạng số hóa. Học phần này trang bị các kiến thức cơ bản và các vấn đề liên quan về lĩnh vực nghiên cứu ẩn thông tin trên dữ liệu số. Các nội dung chi tiết gồm phương pháp nhúng và trích thông tin cơ bản, giới thiệu kỹ thuật phân tích và phát hiện thông tin các ứng dụng kỹ thuật ẩn thông tin trong thực tế. Từ đó giới thiệu các định hướng nghiên cứu và ứng dụng ẩn thông tin như: bảo vệ bản quyền, xác thực nội dung, dò vết, theo dõi phát sóng, kiểm soát sao chép, ...'),
('IE501', 'Đồ án tốt nghiệp', 'Capstone Project', 6, 6, 0, 'TN', NULL, NULL, NULL, 'IE400', 'KTTT', NULL),
('IE502', 'Đồ án tốt nghiệp tại doanh nghiệp', 'Industry Capstone Project', 10, 10, 0, 'TN', NULL, NULL, NULL, NULL, 'KTTT', NULL),
('IE505', 'Khóa luận tốt nghiệp', 'Thesis', 10, 10, 0, 'KLTN', NULL, NULL, NULL, NULL, 'KTTT', 'Giúp sinh viên có kỹ năng đọc tài liệu thành thạo; có kỹ năng tiến hành nghiên cứu, giải quyết vấn đề; có kỹ năng thực hiện thí nghiệm, đánh giá; có kỹ năng viết luận văn; có kỹ năng trình bày. Sinh viên năm cuối đủ điều kiện sẽ được làm khóa luận tốt nghiệp dưới sự hướng dẫn của giảng viên. Theo đó sinh viên cần vận dụng các kiến thức và kỹ năng đã tích lũy được để giải quyết một vấn đề nghiên cứu cơ bản hoặc giải pháp thực tiễn thuộc lĩnh vực CNTT. Việc thực hiện khóa luận tốt nghiệp giúp sinh viên củng cố hoặc có thêm kiến thức và kỹ năng trong hoạt động chuyên môn như: đọc tài liệu, phát triển ý tưởng, lập trình, thực hiện thí nghiệm, đánh giá, viết luận văn, trình bày báo cáo, v.v.'),
('IEM4733', 'Tái cấu trúc quy trình doanh nghiệp', 'Re-engineering Business Processes', 3, 3, 0, 'CN', NULL, NULL, NULL, 'MSIS3303', 'HTTT', 'Khóa học cung cấp các khái niệm, phương pháp tiếp cận và ứng dụng khác nhau để tái cấu trúc quy trình nghiệp vụ, các biện pháp để có những bước tiến khổng lồ để đạt được sự thống lĩnh thị trường kinh doanh năng động. Khóa học cho thấy tác động của sự tự động hóa các quy trình được thiết kế lại có thể làm tăng lợi thế cạnh tranh cho công ty. Sử dụng một số case study phổ biến ở các công ty với việc sắp xếp hợp lý các quy trình của họ đã làm giảm chi phí hoạt động đáng kể, tạo ra sự vượt trội và làm tăng giá trị cho tất cả các bên liên quan.'),
('IEM5723', 'Mô hình hóa dữ liệu, quy trình và đối tượng', 'Data, Process and Object Modeling', 3, 3, 0, 'CSN', NULL, NULL, NULL, NULL, 'HTTT', 'Cung cấp cho các sinh viên phương pháp mô hình hóa, công cụ để nắm bắt và biểu diễn các yêu cầu, thiết kế, đề xuất các giải pháp cho các hệ thống phần mềm. Phương pháp mô hình hóa sử dụng ngôn ngữ mô hình hóa hợp nhất UML dùng để biểu diễn sơ đồ use case, sơ đồ lớp và sơ đồ trình tự. Trình bày những chủ đề quan trọng liên quan đến phương pháp phân tích thiết kế sử dụng các lớp và các đối tượng, kế thừa, các nguyên tắc có thể dùng lại, phân tích nhu cầu ứng dụng,...'),
('INI01', 'Thực tập quốc tế', 'International Internship', 2, 2, 0, 'CN', NULL, NULL, NULL, NULL, 'PĐTĐH', NULL),
('INT001', 'Tiếng Anh tổng quát', 'General English', 0, 0, 0, 'BT', NULL, 'ENG01', NULL, NULL, 'HTTT', NULL),
('INT002', 'Toeic 1', 'TOEIC 1', 0, 0, 0, 'BT', NULL, NULL, NULL, NULL, 'HTTT', NULL),
('INT003', 'Tiếng Anh tổng quát 2', 'General English 2', 0, 0, 0, 'BT', NULL, 'ENG02', NULL, NULL, 'HTTT', NULL),
('INT004', 'Toeic 2', 'TOEIC 2', 0, 0, 0, 'BT', NULL, 'ENG03', NULL, NULL, 'HTTT', NULL),
('INT005', 'Tiếng Anh giao tiếp', 'Communication', 0, 0, 0, 'ĐC', NULL, 'ENG04', NULL, NULL, 'HTTT', NULL),
('INT006', 'Toeic 3', 'TOEIC 3', 0, 0, 0, 'ĐC', NULL, 'ENG05', NULL, NULL, 'HTTT', NULL),
('IS005', 'Giới thiệu ngành Hệ thống Thông tin', 'Introduction to Information Systems Discipline', 1, 1, 0, 'ĐC', NULL, 'CS005
IE005
SE005
CE005
NT005
NT015
EC005
DS005
AI001', NULL, NULL, 'HTTT', NULL),
('IS101', 'Thiết kế cơ sở dữ liệu', 'Database design', 4, 3, 1, 'CN', NULL, 'IS214', NULL, NULL, 'HTTT', NULL),
('IS102', 'Các hệ cơ sở tri thức', 'Knowledge-based systems', 3, 3, 0, 'CN', NULL, 'CS217', NULL, NULL, 'HTTT', NULL),
('IS103', 'Hệ quản trị cơ sở dữ liệu', 'Database Management Systems', 4, 3, 1, 'CN', NULL, 'IS210', NULL, NULL, 'HTTT', NULL),
('IS104', 'Cơ sở dữ liệu phân tán', 'Distributed Databases', 4, 3, 1, 'CN', NULL, 'IS211', NULL, NULL, 'HTTT', NULL),
('IS105', 'Hệ quản trị cơ sở dữ liệu Oracle', 'Oracle Database Management System', 4, 3, 1, 'CN', NULL, NULL, NULL, 'IT004', 'HTTT', 'Môn học cung cấp kiến thức nâng cao về Hệ quản trị CSDL, các khái niệm và kỹ thuật liên quan đến quản trị và lập trình bao gồm ngôn ngữ SQL, ngôn ngữ thủ tục PL/SQL, kiến trúc hệ quản trị Oracle, quản lý instance, quản lý cấu hình, lưu trữ, phân quyền, giao tác, sử dụng RMAN; cơ sở dữ liệu hướng đối tượng, XML trong Oracle. Môn học giúp sinh viên nắm vững và vận dụng kiến thức hệ quản trị để tổ chức, lưu trữ, truy vấn dữ liệu, có khả năng sử dụng các công cụ iSQL plus, SQL Developer…để kết nối, thao tác dữ liệu và sao lưu phục hồi khi cần thiết.'),
('IS106', 'Khai thác dữ liệu', 'Data Mining', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'HTTT', NULL),
('IS107', 'Hệ thống thông tin kế toán', 'Accounting Information Systems', 5, 5, 0, 'CN', NULL, 'IS232', NULL, NULL, 'HTTT', NULL),
('IS201', 'Phân tích thiết kế hệ thống', 'Systems Analysis and Design', 4, 3, 1, 'CSN', NULL, NULL, NULL, 'IT004
IT002', 'HTTT', 'Môn học trình bày các khái niệm và phương pháp luận để phân tích thiết kế một hệ thống thông tin (HTTT). Sinh viên được trang bị kiến thức nguyên lý hoạt động của một HTTT bao gồm 2 thành phần chính: thành phần dữ liệu (khía cạnh tĩnh của HTTT) và thành phần xử lý (khía cạnh động). Cách tiếp cận dữ liệu theo mô hình dữ liệu quan niệm/mô hình hướng đối tượng làm rõ hơn cho thành phần dữ liệu sinh viên đã được học trong môn học trước đó là cơ sở dữ liệu. Ngoài ra, sinh viên được trang bị kỹ năng phân tích, thiết kế HTTT theo mô hình dòng dữ liệu DFD hoặc mô hình hướng đối tượng (ngôn ngữ đặc tả UML) và vận dụng để giải quyết các bài toán thực tế; sử dụng được một số công cụ hỗ trợ phân tích, thiết kế. Đồ án môn học dựa trên một bài toán thực tế sẽ đ¬ược thực hiện theo nhóm 2 sinh viên trở lên nhằm rèn luyện các kỹ năng phân tích, thiết kế, kỹ năng cá nhân như giao tiếp và làm việc nhóm.'),
('IS202', 'Nhập môn công nghệ phần mềm', 'Software engineering', 4, 3, 1, 'CN', NULL, 'SE104', NULL, NULL, 'HTTT', NULL),
('IS203', 'Lập trình cơ sở dữ liệu', 'Database programming', 4, 3, 1, 'CN', NULL, NULL, NULL, 'IT004', 'HTTT', NULL),
('IS204', 'Nhập môn hệ thống thông tin địa lý', 'Introduction to Geographic Information Systems', 4, 3, 1, 'CN', NULL, 'IS251', NULL, NULL, 'HTTT', NULL),
('IS205', 'PTTK hướng đối tượng với UML', 'Object-oriented Analysis and Design with UML', 4, 3, 1, 'CN', NULL, 'IS215', NULL, NULL, 'HTTT', NULL),
('IS206', 'Lập trình ứng dụng Web với Java', 'Web application programming in Java', 4, 3, 1, 'CN', NULL, 'IS216', NULL, 'IT004', 'HTTT', NULL),
('IS207', 'Phát triển ứng dụng web', 'Web Application Development', 4, 3, 1, 'CN', NULL, NULL, NULL, 'IT004', 'HTTT', 'Cung cấp cho sinh viên những kiến thức, kỹ năng, phương pháp lập trình web động với ngôn ngữ lập trình PHP và trình quản trị cơ sở dữ liệu MySQL (ngôn ngữ lập trình mã nguồn mở được sử dụng rộng rãi trên thế giới); kỹ thuật lập trình Ajax trong PHP; giới thiệu một số Framework hỗ trợ viết web bằng PHP. Thiết kế và triển khai các ứng dụng web trong thực tế bằng ngôn ngữ lập trình web PHP, vận hành và bảo trì website.'),
('IS208', 'Quản lý dự án công nghệ thông tin', 'Information Technology Project Management', 4, 3, 1, 'CSN', NULL, 'SE340', NULL, NULL, 'HTTT', 'Môn học trình bày các khái niệm và kiến thức cơ bản liên quan đến việc quản lý một dự án công nghệ thông tin, bao gồm: quản lý phạm vi, quản lý thời gian, chi phí, chất lượng, nguồn nhân lực, rủi ro, truyền thông, và quản lý tích hợp, mô tả những yêu cầu về kỹ năng và kỹ thuật đối với người quản lý dự án, đồng thời cung cấp một số phương pháp và các phần mềm hỗ trợ quản lý dự án CNTT.'),
('IS210', 'Hệ quản trị cơ sở dữ liệu', 'Database Management Systems', 4, 3, 1, 'CSN', NULL, NULL, NULL, 'IT004', 'HTTT', 'Môn học trình bày các khái niệm cơ bản về các hệ quản trị cơ sở dữ liệu (HQTCSDL): kiến trúc hệ quản trị, quản lý instance, quản lý cấu hình, tổ chức, lưu trữ, phân quyền, giao tác, các khái niệm và kỹ thuật liên quan đến quản trị và lập trình, các cơ chế quản lý truy xuất đồng thời, an toàn và khôi phục dữ liệu sau sự cố, tối ưu hoá câu truy vấn. Mỗi nội dung trình bày giải pháp cài đặt cụ thể của chúng trên HQTCSDL thương mại MS SQL Server, DB2, Oracle, MySQL,…'),
('IS211', 'Cơ sở dữ liệu phân tán', 'Distributed Databases', 4, 3, 1, 'CN', NULL, 'IE103', NULL, 'IT004
IT005', 'HTTT', 'Môn học cung cấp các kiến thức về nguyên lý thiết kế cơ sở dữ liệu phân tán, quản lý giao tác, điều khiển tương tranh và phục hồi dữ liệu... Trên cơ sở này, người học có thể nắm vững phương pháp thiết kế cơ sở dữ liệu phân tán, giải quyết được vấn đề về quản lý giao dịch, đặc trưng và các tính chất giao dịch. Cũng như, hiểu được các thuật toán điều khiển tương tranh, phục hồi dữ liệu nhằm ứng dụng vào thực tế và nghiên cứu. Đồng thời vận dụng được kỹ thuật xử lý phân tán và cách triển khai CSDL phân tán bằng Oracle/MS SQL Server.'),
('IS212', 'Thực tập tốt nghiệp', 'Internship', 3, 3, 0, 'TTTN', NULL, NULL, NULL, NULL, 'HTTT', 'Sinh viên bắt buộc phải đi thực tập thực tế tại các doanh nghiệp và thực hiện báo cáo thực tập nộp về Khoa.'),
('IS213', 'Đồ án xây dựng một hệ thống thông tin', 'Information system development', 3, 3, 0, 'ĐA', NULL, NULL, NULL, NULL, 'HTTT', NULL),
('IS214', 'Thiết kế cơ sở dữ liệu', 'Database design', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'HTTT', NULL),
('IS215', 'Thiết kế hướng đối tượng với UML', 'Object-Oriented Analysis and Design with UML', 4, 3, 1, 'CSN', NULL, NULL, NULL, 'IT002
IT004', 'HTTT', 'Môn học trình bày các kiến thức về việc phân tích thiết kế hệ thống thông tin theo
hướng đối tượng. Nội dung chính gồm:
\- Các nguyên lý nền tảng và các khái niệm cơ bản về hướng đối tượng: sự trừu
tượng, tính bao bọc, tính kế thừa và tính đa hình.
\- Tổng quan về ngôn ngữ mô hình hóa thống nhất (UML).
\- Phương pháp phân tích thiết kế hệ thống theo hướng đối tượng: Phát triển hệ thống
từ các mô hình use case được xem như là một mô hình phân tích nhằm biểu diễn
đầy đủ yêu cầu hệ thống.'),
('IS216', 'Lập trình Java', 'Programming with Java', 4, 3, 1, 'CSN', NULL, NULL, NULL, 'IT004', 'HTTT', 'Giới thiệu những khái niệm cơ bản của ngôn ngữ Java, sử dụng các công nghệ Java trong việc lập trình ứng dụng, trong đó chủ yếu tập trung vào công nghệ Java phía server. Nội dung chính của môn học bao gồm các khái niệm cơ bản trong lập trình Java, Giới thiệu về nguyên lý lập trình (cách trao đổi thông tin) giữa Client và Server trong java, ngôn ngữ lập trình web động java với trình quản trị CSDL SQL Server hoặc MySQL, kỹ thuật lập trình Ajax trong Java; một số Framework hỗ trợ viết web bằng Java. Cách thức thiết kế, lập trình và triển khai các ứng dụng cơ sở dữ liệu dùng web động và mô hình lập trình MVC.'),
('IS217', 'Kho dữ liệu và OLAP', 'Data Warehouse and OLAP', 3, 3, 0, 'CN', NULL, 'IS304', NULL, 'IT004', 'HTTT', 'Môn học trang bị kiến thức cơ sở, nâng cao về kho dữ liệu và các phương pháp phân tích, thiết kế kho dữ liệu, các mô hình dữ liệu đa chiều, ngôn ngữ truy vấn cơ sở dữ liệu đa chiều để xây dựng các ứng dụng thực tế cho doanh nghiệp. Bên cạnh đó, sinh viên còn được trang bị các kỹ năng mô phỏng CSDL dạng khối, kỹ năng phân tích dữ liệu đa chiều, khai phá dữ liệu, kỹ năng trích xuất, biến đổi và nạp dữ liệu vào kho, vận dụng công cụ BI thành thạo và ngôn ngữ truy vấn dữ liệu đa chiều.'),
('IS218', 'Kỹ năng tư vấn', 'Kỹ năng tư vấn', 2, 2, 0, 'CNTC', NULL, NULL, NULL, NULL, 'HTTT', NULL),
('IS219', 'Pháp luật trong Thương mại điện tử', 'Pháp luật trong Thương mại điện tử', 3, 3, 0, 'CNTC', NULL, NULL, NULL, NULL, 'HTTT', NULL),
('IS220', 'Xây dựng hệ thống thông tin trên các framework', 'Information Systems Development with Frameworks', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'HTTT', 'Môn học này trình bày các kiến trúc cơ bản của các framework, ứng dụng và đặc trưng của các công nghệ framework. Ngoài ra học phần còn cung cấp cho sinh viên các hướng tiếp cận chuyên sâu trong xây dựng các ứng dụng HTTT dựa trên các framework này.'),
('IS225', 'Khai thác dữ liệu và ứng dụng', 'Data mining and applications', 4, 4, 0, 'CN', NULL, NULL, NULL, NULL, 'Chưa rõ', NULL),
('IS232', 'Hệ thống thông tin kế toán', 'Accounting Information Systems', 4, 4, 0, 'CN', NULL, NULL, NULL, 'IS336', 'HTTT', 'Môn học trình bày các kiến thức về công tác kế toán, chu trình nghiệp vụ kế toán, tố chức và xây dựng hệ thống thông tin kế toán, thiết kế và tin học hóa công tác kế toán.'),
('IS251', 'Nhập môn hệ thống thông tin địa lý', 'Introduction to Geographic Information Systems', 4, 3, 1, 'CN', NULL, 'IS204', NULL, 'IT004', 'HTTT', 'Cung cấp những khái niệm, mô hình của một hệ thống thông tin địa lý, tiến trình hình thành và phát triển khoa học thông tin địa lý, phương pháp luận biểu diễn đối tượng không gian như là một thành phần của dữ liệu GIS liên kết với thành phần khác trong hệ cơ sở dữ liệu GIS là dữ liệu thuộc tính. Môn học cũng xác định các hướng hoạt động của GIS là nghiên cứu phát triển hệ thống thông tin địa lý GIS và phát triển những ứng dụng sử dụng hệ thống thông tin địa lý (GIS) trong các lĩnh vực quản lý tài nguyên, môi trường, sử dụng đất, cơ sở hạ tầng kỹ thuật, kinh tế – xã hội'),
('IS252', 'Khai thác dữ liệu', 'Data Mining', 4, 3, 1, 'CN', NULL, NULL, NULL, 'MA005
IT004', 'HTTT', 'Cung cấp các kiến thức về việc khai thác tri thức tiềm ẩn trong các CSDL. Học viên được học các kiến thức về quy trình khai thác tri thức, bài toán tập phổ biến và luật kết hợp, bài toán chuỗi tuần tự, bài toán phân lớp, bài toán gom cụm và các ứng dụng của khai thác dữ liệu vào thực tiễn.'),
('IS253', 'Lập trình ứng dụng trên thiết bị di động', 'Mobile application programming', 3, 2, 1, 'CN', NULL, 'NT118', NULL, 'IT002', 'HTTT', NULL),
('IS254', 'Hệ hỗ trợ quyết định', 'Decision Support Systems', 3, 3, 0, 'CN', NULL, NULL, NULL, NULL, 'HTTT', 'Môn học này dùng cho sinh viên chuyên ngành Hệ thống thông tin quản lý (MIS) và Thương mại điện tử (EC). Môn học nhằm cung cấp những khái niệm, kiến thức, kỹ năng để xây dựng và thi công hệ hỗ trợ ra quyết định. Môn học này là sự kết nối tổng hợp của nhiều môn học khác nhau: mô hình toán, cơ sở dữ liệu, hệ chuyên gia, hệ nơ- ron, xử lý ngôn ngữ tự nhiên, khoa học về quản lý, giao diện người dùng, kỹ thuật đồ họa, kỹ thuật lập trình và một số ngành khoa học nghiên cứu về tâm lý và thái độ của nhà quản lý.'),
('IS301', 'Thương mại điện tử', 'E-commerce', 3, 3, 0, 'CN', NULL, 'IS334', NULL, NULL, 'HTTT', NULL),
('IS302', 'Phân tích không gian', 'Spatial analysis', 4, 3, 1, 'CN', NULL, 'IS351', NULL, NULL, 'HTTT', NULL),
('IS303', 'Hệ cơ sở dữ liệu không gian', 'Spatial Database Systems', 4, 3, 1, 'CN', NULL, 'IS352', NULL, NULL, 'HTTT', NULL),
('IS3033', 'Quản lý dự án hệ thống thông tin', 'IS-Project Management', 4, 4, 0, 'CN', NULL, NULL, NULL, NULL, 'HTTT', NULL),
('IS304', 'Kho dữ liệu và OLAP', 'Data warehouse and OLAP', 4, 3, 1, 'CN', NULL, 'IS404
IS217', NULL, NULL, 'HTTT', NULL),
('IS305', 'An toàn và bảo mật HTTT', 'Information systems security & protection', 3, 3, 0, 'CN', NULL, 'IS335', NULL, NULL, 'HTTT', NULL),
('IS306', 'Hệ thống thông tin quản lý', 'Accounting information systems', 3, 3, 0, 'CN', NULL, 'IS332', NULL, NULL, 'HTTT', NULL),
('IS311', 'Đồ án hệ thống thông tin', 'Information Systems Project', 3, 3, 0, 'ĐA', NULL, 'IS213', NULL, NULL, 'HTTT', NULL),
('IS3303', 'Phân tích thiết kế hệ thống', 'System Analysis and Design', 4, 4, 0, 'CN', NULL, NULL, NULL, NULL, 'HTTT', NULL),
('IS332', 'Hệ thống thông tin quản lý', 'Management Information Systems', 3, 3, 0, 'CN', NULL, NULL, NULL, 'IS201', 'HTTT', 'Cung cấp những khái niệm cơ sở về hệ thống thông tin quản lý, các yếu tố cấu thành, vai trò, vị trí và tầm quan trọng của hệ thống trong tổ chức; những phương thức mà hệ thống trợ giúp các hoạt động kinh doanh, hỗ trợ việc ra quyết định và tạo ra lợi thế cạnh tranh; quy trình tổ chức và phương pháp tiến hành giải quyết những vấn đề kinh doanh bằng hệ thống thông tin dựa trên cơ sở công nghệ thông tin; nghiên cứu một vài hệ thống thông tin tiêu biểu dưới dạng nghiên cứu tình huống (Case Study)'),
('IS334', 'Thương mại điện tử', 'E-Commerce', 3, 3, 0, 'CN', NULL, NULL, NULL, NULL, 'HTTT', 'Cung cấp các khái niệm cơ bản về thương mại điện tử và việc sử dụng CNTT để phát triển các ứng dụng thương mại điện tử, cách thức hoạch định kế hoạch kinh doanh TMĐT bao gồm mô hình kinh doanh và chiến lược kinh doanh điện tử, kiểm soát thực thi kế hoạch này. Sinh viên nắm được các công cụ để triển khai TMĐT như xây dựng website thương mại điện tử, thanh toán điện tử, công cụ làm Marketing trực tuyến.'),
('IS335', 'An toàn và bảo mật hệ thống thông tin', 'Information Systems Security', 3, 3, 0, 'CN', NULL, NULL, NULL, 'IT005
IT004', 'HTTT', 'Môn học tập trung vào hai phần chính là an toàn hệ thống và bảo mật dữ liệu. Trong phần bảo mật dữ liệu, học viên được cung cấp kiến thức về mã hóa thông tin và ứng dụng. Trong phần an tòan dữ liệu, học viên sẽ được cung cấp kiến thực về an toàn thông tin và cách dùng chúng để bảo vệ các hệ điều hành, hệ thống mạng, hệ thống phần mềm.'),
('IS336', 'Hoạch định nguồn lực doanh nghiệp', 'Enterprise Resource Planning', 4, 3, 1, 'CSN', NULL, NULL, NULL, NULL, 'HTTT', 'Cung cấp cho sinh viên các quy trình chuẩn của một hệ thống ERP trong doanh nghiệp liên quan đến bán hàng, sản xuất, phân phối, vật tư, mua hàng, kế toán. Sinh viên có khả năng thao tác trên hệ thống hoạch định nguồn lực ERP cụ thể, hiểu được tầm quan trọng của ERP, điều kiện triển khai về nền tảng công nghệ, qui mô và phạm vi hoạt động của các tổ chức/doanh nghiệp và các chức năng mà một hệ thống ERP hỗ trợ cho nhà quản trị trong việc điều hành doanh nghiệp, hỗ trợ người dùng thực thi các nghiệp vụ kinh doanh.'),
('IS337', 'Cơ sở dữ liệu nâng cao', 'Advanced databases', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'HTTT', NULL),
('IS338', 'Dự báo kinh doanh', 'Markets analysis', 3, 3, 0, 'CN', NULL, NULL, NULL, 'IS336', 'HTTT', NULL),
('IS339', 'Sinh tin học', 'Bioinformatics', 3, 3, 0, 'CNTC', NULL, NULL, NULL, NULL, 'HTTT', NULL),
('IS340', 'Thị trường chứng khoán', 'Stock Market', 3, 3, 0, 'CNTC', NULL, NULL, NULL, NULL, 'HTTT', NULL),
('IS341', 'Khởi nghiệp', 'Entrepreneurship', 3, 3, 0, 'CNTC', NULL, NULL, NULL, NULL, 'HTTT', NULL),
('IS342', 'Chính phủ điện tử', 'E-Government', 3, 3, 0, 'CNTC', NULL, NULL, NULL, NULL, 'HTTT', NULL),
('IS343', 'Luật CNTT', 'Luật CNTT', 3, 3, 0, 'CNTC', NULL, NULL, NULL, NULL, 'HTTT', NULL),
('IS344', 'Quản trị nguồn lực y tế', 'Healthcare Resource Management', 3, 2, 1, 'CN', NULL, NULL, NULL, NULL, 'HTTT', NULL),
('IS345', 'AI trong y tế', 'AI in Healthcare', 3, 3, 0, 'CN', NULL, NULL, NULL, NULL, 'HTTT', NULL),
('IS346', 'Quản lý dự án công nghệ thông tin y tế', 'Healthcare Information Technology Project Management', 3, 2, 1, 'CN', NULL, NULL, NULL, NULL, 'HTTT', NULL),
('IS347', 'Thống kê y học', 'Medical Statistics', 3, 3, 0, 'CN', NULL, NULL, NULL, NULL, 'HTTT', NULL),
('IS348', 'Dịch tễ học', 'Epidemiology', 3, 2, 1, 'CN', NULL, NULL, NULL, NULL, 'HTTT', NULL),
('IS349', 'Hệ thống y tế', 'Health Systems', 3, 3, 0, 'CN', NULL, NULL, NULL, 'IT004', 'HTTT', NULL),
('IS351', 'Phân tích không gian', 'Spatial Analysis', 4, 3, 1, 'CN', NULL, NULL, NULL, 'IS251', 'HTTT', 'Phân tích không gian là một chức năng quan trọng của GIS trong tiến trình khai thác hệ thống thông tin địa lý. Môn học sẽ cung cấp những thuật toán xử lý phân tích dữ liệu của các đối tượng, các hiện tượng, các sự kiện theo không gian và thời gian. Những thuật toán phân tích không gian có thể áp dụng trên một lớp dữ liệu, hoặc trên nhiều lớp dữ liệu tích hợp, bao gồm những thuật toán phân tích các đối tượng điểm, phân tích theo bề mặt, hoặc phân tích mạng trong cấu trúc dữ liệu vector. Ngoài ra môn học cũng cung cấp những thuật toán xử lý dữ liệu có cấu trúc raster gọi là phân tích lưới và giới thiệu các phương pháp nội suy khác nhau.'),
('IS352', 'Hệ cơ sở dữ liệu không gian', 'Spatial Database Systems', 4, 3, 1, 'CN', NULL, NULL, NULL, 'IT004
IS251', 'HTTT', 'Cung cấp các mô hình quản trị cơ sở dữ liệu không gian truyền thống như data files trong các hệ thống GIS, mô hình cơ sở dữ liệu geodatabase, nghiên cứu kiến trúc cơ sở dữ liệu 3 tầng (3-tier) trong các hệ thống thông tin địa lý nhiều người dùng (collaborative GIS), hệ thống chuyên nghiệp (enterprise GIS), kiến trúc mạng GIS'),
('IS353', 'Mạng xã hội', 'Social Networks', 3, 3, 0, 'CĐTN', NULL, NULL, NULL, 'IT004
MA004', 'HTTT', 'Khóa học nhằm mục đích giới thiệu sinh viên phân tích mạng xã hội trên cả hai mạng lưới tĩnh và động. Nửa đầu của khóa học sẽ giới thiệu các sinh viên làm thế nào để phân tích một mạng tĩnh bằng cách sử dụng số liệu và ý nghĩa của các kết quả thu được dựa trên sự phân tích này. Nửa sau của khóa học sẽ tập trung vào phân tích mạng lưới động. Mô hình mạng lưới ngẫu nhiên và các số liệu thống kê sẽ được nêu rõ. Việc hình thành mạng lưới chiến lược cũng sẽ được giới thiệu. Mạng được hình thành là một trong những đề tài nghiên cứu phổ biến nhất trong phân tích mạng xã hội (social network analysis - SNA). Hình thành lý thuyết trò chơi (mạng) để giải quyết các vấn đề như cân bằng, ổn định, thương lượng, chuyển giao, phối hợp lựa chọn và thích ứng với những thay đổi mạng. Sử dụng phần mềm Pajek giúp các sinh viên đo và hiển thị dữ liệu mạng. Sinh viên sẽ thực hành sử dụng phần mềm này qua các bài tập.'),
('IS354', 'Công nghệ tài chính căn bản Fintech', 'Introduction to Financial Technology', 3, 3, 0, 'CN', NULL, NULL, NULL, NULL, 'HTTT', NULL),
('IS355', 'Công nghệ Blockchain', 'Blockchain Technology', 4, 3, 1, 'CN', NULL, NULL, NULL, 'IT005
IT004', 'HTTT', NULL),
('IS356', 'Agile IT với DevOps', 'Agile IT with DevOps', 3, 3, 0, 'CN', NULL, NULL, NULL, NULL, 'HTTT', NULL),
('IS357', 'Kiến trúc hướng dịch vụ', 'Service Oriented Architecture', 3, 3, 0, 'CN', NULL, NULL, NULL, NULL, 'HTTT', NULL),
('IS358', 'Kiểm soát nhiễm khuẩn bệnh viện', 'Hospital Infection Control', 3, 3, 0, 'CN', NULL, NULL, NULL, NULL, 'HTTT', NULL),
('IS360', 'Quản lý chăm sóc và điều trị', 'Care Management and Treatment', 3, 3, 0, 'CN', NULL, NULL, NULL, NULL, 'HTTT', NULL),
('IS361', 'Quản lý chuỗi cung ứng dược và thiết bị y tế', 'Supply Management of Drugs and Medical Equipment', 3, 3, 0, 'CN', NULL, NULL, NULL, NULL, 'HTTT', NULL),
('IS362', 'Quản trị tài chính và bảo hiểm y tế', 'Hospital Financial Management and Health Insurance', 3, 3, 0, 'CN', NULL, NULL, NULL, NULL, 'HTTT', NULL),
('IS363', 'Pháp luật trong lĩnh vực y tế', 'Law on Health and Medical', 2, 2, 0, 'CN', NULL, NULL, NULL, NULL, 'HTTT', NULL),
('IS364', 'Mã tiêu chuẩn dùng chung trong y tế', 'Medical Coding', 3, 3, 0, 'CN', NULL, NULL, NULL, NULL, 'HTTT', NULL),
('IS401', 'Khóa luận tốt nghiệp', 'Thesis', 10, 10, 0, 'KLTN', NULL, NULL, NULL, NULL, 'HTTT', NULL),
('IS4013', 'Thiết kế, quản lý và quản trị hệ CSDL', 'DBM design, management & administration', 3, 3, 0, 'CN', NULL, NULL, NULL, NULL, 'HTTT', NULL),
('IS402', 'Điện toán đám mây', 'Cloud Computing', 3, 3, 0, 'CĐTN', NULL, NULL, NULL, 'IT005
IT007', 'HTTT', 'Môn học trình bày các khái niệm cơ bản của điện toán đám mây như: khái niệm, mô hình triển khai, mô hình dịch vụ, đặc trưng của các dịch vụ điện toán đám mây, ảo hóa và các thách thức đối với điện toán đám mây; Nguyên lý hoạt động của ảo hóa; nguyên lý xử lý phân tán, minh họa trên một trong số công nghệ nền tảng đám mây.
Sinh viên có khả năng phân tích và tư vấn cho doanh nghiệp mô hình dịch vụ điện toán đám mây phù hợp với thông tin doanh nghiệp và kỹ năng quản lý đám mây qua phần mềm mô phỏng, sử dụng các dịch vụ điện toán đám mây của các nhà cung cấp, lập trình trên nền tảng xử lý phân tán.'),
('IS403', 'Phân tích dữ liệu kinh doanh', 'Data Analysis in Business', 3, 3, 0, 'CN', NULL, NULL, NULL, 'MA005
IT004', 'HTTT', 'Môn học nhằm cung cấp các kỹ thuật phân tích dữ liệu cần thiết cho việc thực hiện phân tích dữ liệu trong nghiên cứu, các dữ liệu trong kinh doanh. Sinh viên được trang bị kiến thức kiến thức nền tảng của các công thức toán học cần thiết, từ các tình huống gần gũi trong thực tế, dưới dạng các ứng dụng trong kinh doanh,thực hành trên các phần mềm EVIEWS, SPSS…'),
('IS404', 'Kho dữ liệu và OLAP', 'Data Warehouse and OLAP', 4, 3, 1, 'CĐTN', NULL, 'IS217', NULL, NULL, 'HTTT', NULL),
('IS405', 'Dữ liệu lớn', 'Big Data', 4, 3, 1, 'CĐTN', NULL, NULL, NULL, 'IT004', 'HTTT', 'Môn học giới thiệu tổng quan thế nào là dữ liệu lớn và những thách thức của dữ liệu lớn (khả năng phân tích, xử lý). Giới thiệu những kỹ thuật R statistics, Hadoop và Map reduce để trực quan hóa và phân tích dữ liệu lớn và tạo ra các mô hình thống kê.'),
('IS406', 'Điện toán đám mây và dữ liệu lớn', 'Cloud Computing and Big Data', 3, 3, 0, 'CN', NULL, NULL, NULL, NULL, 'HTTT', NULL),
('IS407', 'Đồ án tốt nghiệp', 'Capstone Project', 6, 6, 0, 'CN', NULL, NULL, NULL, NULL, 'HTTT', NULL),
('IS4133', 'Công nghệ thông tin cho thương mại điện tử', 'Information Technologies for e-commerce', 3, 3, 0, 'CN', NULL, NULL, NULL, NULL, 'HTTT', NULL),
('IS4263', 'Các ứng dụng thông minh và hỗ trợ ra quyết định', 'Decision Support and Busi.Intelligence Applications', 3, 3, 0, 'CN', NULL, NULL, NULL, NULL, 'HTTT', NULL),
('IS4523', 'Hệ truyền thông dữ liệu', 'Data Communication Systems', 3, 3, 0, 'CN', NULL, NULL, NULL, NULL, 'HTTT', NULL),
('IS501', 'Thực tập cuối khóa', 'Internship', 3, 3, 0, 'TTTN', NULL, 'IS212', NULL, NULL, 'HTTT', NULL),
('IS502', 'Thực tập doanh nghiệp', 'Internship', 2, 2, 0, 'TTTN', NULL, NULL, NULL, NULL, 'HTTT', NULL),
('IS503', 'Đồ án tốt nghiệp tại doanh nghiệp', 'Industry Capstone Project', 10, 10, 0, 'CN', NULL, NULL, NULL, NULL, 'HTTT', NULL),
('IS505', 'Khóa luận tốt nghiệp', 'Thesis', 10, 10, 0, 'KLTN', NULL, 'IS401', NULL, NULL, 'HTTT', NULL),
('IS5100', 'Thực tập cuối khóa', 'Internship', 4, 4, 0, 'TT', NULL, NULL, NULL, NULL, 'HTTT', NULL),
('IS6301', 'Phân tích thiết kế hệ thống thông tin nâng cao', 'Advanced Information Systems Analysis and Design', 7, 4, 3, 'CN', NULL, NULL, NULL, NULL, 'HTTT', NULL),
('IT001', 'Nhập môn lập trình', 'Introduction to Programming', 4, 3, 1, 'ĐC', NULL, 'IT011', NULL, NULL, 'KHMT', 'Môn học sẽ cung cấp các kiến thức nền tảng về máy tính, tư duy và các kỹ năng
căn bản lập trình cho tất cả sinh viên các ngành Công nghệ thông tin.
Đối với hệ tài năng: sinh viên sẽ được trang bị các kiến thức nâng cao về tư duy
và các kỹ năng lập trình thông qua một số bài toán có độ phức tạp cao.'),
('IT003', 'Cấu trúc dữ liệu và giải thuật', 'Data Structures and Algorithms', 4, 3, 1, 'CSNN', NULL, 'IT013', NULL, 'IT001', 'KHMT', 'Môn học giúp sinh viên hiểu tầm quan trọng của giải thuật và cách tổ chức dữ
liệu, là hai thành tố quan trọng nhất cho một chương trình. Nắm bắt, áp dụng
được các giải thuật, cấu trúc dữ liệu thường được áp dụng trong việc giải quyết
bài toán trong tin học. Giúp củng cố và phát triển kỹ năng lập trình vừa được học
trong môn học trước.'),
('IT005', 'Nhập môn mạng máy tính', 'Introduction to Computer Networks', 4, 3, 1, 'CSNN', NULL, NULL, NULL, NULL, 'MMT&TT', 'Cung cấp cho sinh viên những khái niệm cơ bản về mạng máy tính và truyền dữ liệu trên mạng; các dịch vụ mạng cơ bản, kỹ thuật mạng không dây.'),
('IT006', 'Kiến trúc máy tính', 'Computer Architecture', 3, 3, 0, 'CSNN', NULL, NULL, NULL, 'PH002
IT001', 'KTMT', 'Môn học này trình bày kiến thức cơ bản về kiến trúc máy tính bao gồm: lịch sử hình thành máy tính và các công nghệ liên quan đến phát triển máy tính; các khái niệm chính trong kiến trúc máy tính như thành phần cấu tạo, quy tắc hoạt động, kiến trúc tập lệnh và hiệu suất của một máy tính. Môn học cũng cung cấp kiến thức liên quan đến lập trình hợp ngữ và các vấn đề liên quan tới CPU như thiết kế datapath cơ bản và cơ chế pipeline.'),
('IT007', 'Hệ điều hành', 'Operating Systems', 4, 3, 1, 'CSNN', NULL, NULL, NULL, 'IT006', 'KTMT', 'Giới thiệu các khái niệm, các nguyên lý hoạt động cơ bản trong hệ điều hành đi theo trình tự từ đơn giản đến phức tạp. Môn học gồm có 8 chương ứng với các khối kiến thức sau: tổng quan về hệ điều hành, cấu trúc hệ điều hành, quản lý tiến trình, định thời CPU, đồng bộ hóa tiến trình, tắc nghẽn (deadlocks), quản lý bộ nhớ và bộ nhớ ảo. Kết thúc phần lý thuyết của từng khối kiến thức sẽ là các bài thực hành trong phòng lab để có cái nhìn thực tế hơn về các khái niệm, các giải thuật đã được giới thiệu.'),
('IT008', 'Lập trình trực quan', 'Visual Programming', 4, 3, 1, 'CN', NULL, 'WINP1', NULL, 'IT001
IT002', 'CNPM', 'Môn học này trình bày các khái niệm và phương pháp lập trình trực quan trên môi trường Windows, cách trình bày các cách thức, quy trình tạo một ứng dụng trên Windows, cách cách thức xử lý thông điệp, các giao diện điều khiển, cơ chế quản lý bộ nhớ, thư viện liên kết động, lập trình đa nhiệm…'),
('IT009', 'Giới thiệu ngành', 'Introduction to Information Technology Disciplines', 2, 2, 0, 'CSNN', NULL, 'CS005
IE005
IS005
SE005
CE005
NT005
NT015
EC005
DS005', NULL, NULL, 'PĐTĐH', NULL),
('IT010', 'Tổ chức và cấu trúc máy tính', 'Computer Organization and Architecture', 2, 2, 0, 'CSN', NULL, NULL, NULL, NULL, 'KTMT', 'Môn học nhằm mục đích giới thiệu cho sinh viên:
\- Các hệ thống số cơ bản và sự chuyển đổi qua lại giữa các hệ thống số này
\- Đại số Boolean
\- Giới thiệu tổ chức của CPU
\- Giới thiệu tổ chức của bộ nhớ: bộ nhớ trong, bộ nhớ ngoài và bộ nhớ cache
\- Các thiết bị Input & Output'),
('IT011', 'Nhập môn lập trình thi đấu', 'Introduction to Competitive Programming', 4, 3, 1, 'ĐC', NULL, 'IT001', NULL, NULL, 'KHMT', 'Dành riêng cho các sinh viên đã có kiến thức cơ bản về lập trình, đã từng tham gia các kỳ thi lập trình.
Trang bị cho sinh viên những kiến thức, kỹ năng và thái độ cần thiết để giải quyết các bài toán khi tham dự các kỳ thi lập trình cấp quốc gia và quốc tế.
Hướng dẫn cách vận dụng kỹ thuật lập trình và sử dụng cấu trúc dữ liệu để tấn công những thử thách tính toán. Các dạng thuật toán được giới thiệu bao gồm: vét cạn, chia để trị, tham lam, qui hoạch động, … Sinh viên còn được hướng dẫn sử dụng các thư viện của ngôn ngữ lập trình (C/C++, Java, Python, …) để lập trình giải bài toán trên máy tính.'),
('IT012', 'Tổ chức và cấu trúc máy tính 2', 'Computer Structure and Organization 2', 4, 3, 1, 'CSNN', NULL, NULL, NULL, NULL, 'KTMT', 'Môn học này trình bày kiến thức cơ bản về kiến trúc máy tính bao gồm:
\- Lịch sử hình thành và các công nghệ liên quan đến phát triển máy tính.
\- Chức năng và nguyên lý hoạt động của các bộ phận trong máy tính.
\- Cách biểu diễn dữ liệu, tính toán trong máy tính.
\- Cách phân tích các mạch số cơ bản.
\- Kiến trúc bộ lệnh, lập trình hợp ngữ.
 -Các vấn đề liên quan tới nguyên lý hoạt động của bộ xử lý.'),
('IT013', 'Cấu trúc dữ liệu cho lập trình thi đấu', 'Data Structures and Algorithms for Competitive Programming', 4, 3, 1, 'ĐC', NULL, 'IT003', NULL, NULL, 'KHMT', NULL),
('ITEM1', 'Nhập môn Quản trị doanh nghiệp', 'Initiation to Business Administration', 2, 2, 0, 'ĐC', NULL, 'SE349', NULL, NULL, 'PĐTĐH', NULL),
('ITEW1', 'Nhập môn công tác kỹ sư', 'Introduction to Tasks of an Engineer', 2, 2, 0, 'ĐC', NULL, 'IT009
SS004', NULL, NULL, 'TTNN', NULL),
('ITNT005', 'Communication', 'Communication', 0, 0, 0, 'BT', NULL, NULL, NULL, NULL, 'HTTT', NULL),
('JAN01', 'Tiếng Nhật 1', 'Japanese 1', 5, 2, 3, 'NN', NULL, NULL, NULL, NULL, 'PĐTĐH', 'Môn học cung cấp cho sinh viên các kiến thức về tiếng Nhật sơ cấp: làm quen với hệ chữ khác hệ chữ La Tinh, ngữ pháp (ngữ pháp tiếng Nhật sơ cấp; các thì, thể của động từ; trợ từ, giới từ; lượng từ vựng tương ứng), phát âm,… các kỹ năng nghe, nói, đọc, viết sơ cấp.'),
('JAN02', 'Tiếng Nhật 2', 'Japanese 2', 5, 2, 3, 'NN', NULL, NULL, 'JAN01', NULL, 'PĐTĐH', 'Môn học cung cấp cho sinh viên các kiến thức về tiếng Nhật ở trình độ sơ cấp 2 (tương đương N5) các kỹ năng nghe, nói, đọc, viết với trình độ tương đương. Môn học cung cấp lượng kiến thức về Hán tự sơ cấp cho sinh viên.'),
('JAN03', 'Tiếng Nhật 3', 'Japanese 3', 5, 2, 3, 'NN', NULL, NULL, 'JAN02', NULL, 'PĐTĐH', 'Môn học cung cấp cho sinh viên các kiến thức về tiếng Nhật ở trình độ sơ cấp 3 (tương đương đầu N4) các kỹ năng nghe, nói, đọc, viết với trình độ tương đương. Bắt đầu tập trung nâng cao khả năng giao tiếp.'),
('JAN04', 'Tiếng Nhật 4', 'Japanese 4', 5, 2, 3, 'NN', NULL, NULL, 'JAN03', NULL, 'PĐTĐH', 'Môn học cung cấp cho sinh viên các kiến thức về tiếng Nhật ở trình độ sơ cấp 4 (tương đương N4) các kỹ năng nghe, nói, đọc, viết với trình độ tương đương. Bắt đầu tập trung nâng cao khả năng giao tiếp.'),
('JAN05', 'Tiếng Nhật 5', 'Japanese 5', 5, 2, 3, 'NN', NULL, NULL, 'JAN04', NULL, 'PĐTĐH', 'Môn học cung cấp cho sinh viên các kiến thức về tiếng Nhật ở trình độ Trung cấp 1 (tương đương đầu N3) các kỹ năng nghe, nói, đọc, viết với trình độ tương đương. Nâng cao khả năng vận dụng ngôn ngữ vào giao tiếp.'),
('JAN06', 'Tiếng Nhật 6', 'Japanese 6', 3, 1, 2, 'NN', NULL, NULL, 'JAN05', NULL, 'PĐTĐH', 'Môn học cung cấp cho sinh viên các kiến thức về tiếng Nhật ở trình độ Trung cấp 2 (tương đương giữa N3) các kỹ năng nghe, nói, đọc, viết với trình độ tương đương. Nâng cao khả năng vận dụng ngôn ngữ vào giao tiếp.'),
('JAN07', 'Tiếng Nhật 7', 'Japanese 7', 3, 1, 2, 'NN', NULL, NULL, 'JAN06', NULL, 'PĐTĐH', 'Môn học cung cấp cho sinh viên các kiến thức về tiếng Nhật ở trình độ Trung cấp 3 (tương đương N3) các kỹ năng nghe, nói, đọc, viết với trình độ tương đương. Nâng cao khả năng vận dụng ngôn ngữ vào giao tiếp.'),
('JAN08', 'Tiếng Nhật 8', 'Japanese 8', 3, 1, 2, 'NN', NULL, NULL, 'JAN07', NULL, 'PĐTĐH', 'Môn học cung cấp cho sinh viên các kiến thức về tiếng Nhật dùng trong môi trường công sở, các quy tắc, ứng xử phù hợp với môi trường làm việc văn phòng.'),
('JANHU', 'Tiếng Nhật miễn phí do Huredee tài trợ', 'Japanese language (sponsored by Huredee)', 40, 40, 0, 'ĐC', NULL, NULL, NULL, NULL, 'P.ĐTĐH', NULL),
('LIA01', 'Đại số tuyến tính', 'Linear Algebra', 3, 3, 0, 'ĐC', NULL, 'MA003', NULL, NULL, 'BMTL', NULL),
('LIA11', 'Đại số tuyến tính', 'Linear Algebra', 3, 3, 0, 'ĐC', NULL, NULL, NULL, NULL, 'BMTL', NULL),
('MA001', 'Giải tích 1', 'Calculus 1', 3, 3, 0, 'ĐC', NULL, 'MA006', NULL, NULL, 'BMTL', NULL),
('MA002', 'Giải tích 2', 'Calculus 2', 3, 3, 0, 'ĐC', NULL, 'MA006', NULL, 'MA001', 'BMTL', NULL),
('MA003', 'Đại số tuyến tính', 'Linear Algebra', 3, 3, 0, 'ĐC', NULL, NULL, NULL, NULL, 'BMTL', 'Đại số tuyến tính là môn học ở giai đoạn kiến thức đại cương, là môn học bắt buộc đối với tất cả sinh viên. Môn học này giúp cho sinh viên nắm được khái niệm và làm được các phép toán về: ma trận, hạng, định thức, hệ phương trình tuyến tính; cách giải hệ phương trình tuyến tính bằng phương pháp Cramer, phương pháp Gauss, phương pháp Gauss-Jordan; về không gian vector, sự phụ thuộc, độc lập tuyến tính, tập sinh, cơ sở và số chiều của không gian vector; ma trận chéo hóa và ý nghĩa của việc chéo hóa ma trận; về ánh xạ tuyến tính, toán tử tuyến tính, dạng toàn phương và phép đưa dạng toàn phương về dạng chính tắc; để từ đó SV có thể tiếp tục học tập những môn chuyên ngành, hay phục vụ cho quá trình làm khóa luận tốt nghiệp.'),
('MA004', 'Cấu trúc rời rạc', 'Discrete Structures', 4, 4, 0, 'ĐC', NULL, NULL, NULL, NULL, 'BMTL', 'Cấu trúc rời rạc là môn học ở giai đoạn kiến thức đại cương, là môn học bắt buộc đối với tất cả sinh viên. Đây là một trong những môn thi tuyển sinh đầu vào ở bậc Sau đại học ngành công nghệ thông tin. Môn học này giúp cho sinh viên có kiến thức, có kỹ năng giải quyết được những bài toán liên quan đến Toán rời rạc (cơ sở logic, các phương pháp đếm, quan hệ, đại số Bool và hàm Bool), và Lý thuyết đồ thị (các khái niệm cơ bản về lý thuyết đồ thị, đường đi, chu trình và cây).'),
('MA005', 'Xác suất thống kê', 'Probability and Statistics', 3, 3, 0, 'ĐC', NULL, NULL, NULL, 'MA006', 'BMTL', 'Xác suất thống kê là môn học bắt buộc (hoặc tự chọn) của sinh viên một số ngành thuộc lĩnh vực công nghệ thông tin. Đây là một trong những môn thi tuyển sinh đầu vào ở bậc Sau đại học ngành Khoa học máy tính. Môn học này trình bày các khái niệm và phương pháp về: Lý thuyết xác suất (Không gian xác suất; Biến ngẫu nhiên; Hàm đăc trưng; Dãy các biến ngẫu nhiên; Các quy luật phân phối xác suất; Các định lý giới hạn phân phối xác suất) và Thống kê (Mẫu ngẫu nhiên; Ước lượng điểm và ước lượng khoảng; Kiểm định các giả thiết thống kê; Phân tích tương quan và hồi quy; Một số vấn đề về quá trình ngẫu nhiên). Ngoài ra, môn học này còn giới thiệu về cách thức nhận diện, phân tích và xử lý một vấn đề thực tế; xử lý các số liệu thống kê; để từ đó giúp cho người dùng đưa ra các suy luận phù hợp (nhằm hỗ trợ cho quá trình ra quyết định).'),
('MAT01', 'Toán cao cấp A1', 'Advanced mathematics A1', 3, 3, 0, 'ĐC', NULL, 'MA001', NULL, NULL, 'BMTL', NULL),
('MAT02', 'Toán cao cấp A2', 'Advanced mathematics A2', 4, 4, 0, 'ĐC', NULL, 'MA002', NULL, NULL, 'BMTL', NULL),
('MAT04', 'Cấu trúc rời rạc', 'Discrete Structures', 4, 4, 0, 'ĐC', NULL, 'MA004', NULL, NULL, 'BMTL', NULL),
('MAT11', 'Giải tích 1', 'Calculus 1', 4, 4, 0, 'ĐC', NULL, NULL, NULL, NULL, 'BMTL', NULL),
('MAT12', 'Giải tích 2', 'Calculus 2', 3, 3, 0, 'ĐC', NULL, NULL, NULL, NULL, 'BMTL', NULL),
('MAT14', 'Toán rời rạc cho máy tính', 'Discrete mathematics for computer', 3, 3, 0, 'ĐC', NULL, NULL, NULL, NULL, 'BMTL', NULL),
('MAT21', 'Toán cao cấp A1 (TE)', 'Advanced Mathematics A1', 4, 4, 0, 'ĐC', NULL, 'MA001
MAT01', NULL, NULL, 'BMTL', NULL),
('MAT22', 'Toán cao cấp A2 (TE)', 'Advanced Mathematics A2', 4, 4, 0, 'ĐC', NULL, 'MA002
MAT02', NULL, NULL, 'BMTL', NULL),
('MAT23', 'Đại số tuyến tính', 'Linear Algebra', 4, 4, 0, 'ĐC', NULL, 'LIA01', NULL, NULL, 'BMTL', NULL),
('MAT24', 'Cấu trúc rời rạc (TE)', 'Discrete structures', 4, 4, 0, 'ĐC', NULL, NULL, NULL, NULL, 'BMTL', NULL),
('MATH2144', 'Giải tích 1', 'Calculus 1', 4, 4, 0, 'ĐC', NULL, 'MATH2154', NULL, NULL, 'HTTT', 'Đây là học phần giải tích đầu tiên, nhằm cung cấp các khái niệm cơ bản về các phương trình trong hệ tọa độ Đêcác cũng như trong hệ tọa độ cực. Bao gồm các kỹ thuật dựng đồ thị hàm số; các kỹ thuật vi phân và tích phân cùng các ứng dụng; vi phân từng phần và ứng dụng cho các hàm nhiều biến.'),
('MATH2153', 'Giải tích 2', 'Calculus 2', 3, 3, 0, 'ĐC', NULL, 'MATH2154', NULL, 'MATH2144', 'HTTT', 'Cung cấp các kiến thức về tích phân bội: tích phân 2 lớp, tích phân 2 lớp trong hệ tọa độ cực, tích phân 3 lớp, tích phân 3 lớp trong hệ tọa độ trụ và hệ tọa độ cầu; tích phân đường và tích phân mặt cùng các ứng dụng khác nhau trong trường vectơ
Nội dung: Tích phân xác định và các ứng dụng tính diện tích, thể tích, công, các phương trình vi phân. Dãy và chuỗi. Vectơ và hình giải tích trong không gian hai và ba chiều. Hệ tọa độ cực. Phương trình tham số.'),
('MATH2154', 'Giải tích', 'Calculus', 4, 4, 0, 'ĐC', NULL, NULL, NULL, NULL, 'HTTT', NULL),
('MATH3013', 'Đại số tuyến tính', 'Linear Algebra', 3, 3, 0, 'ĐC', NULL, NULL, NULL, NULL, 'HTTT', 'Cung cấp cho sinh viên các ngành kỹ thuật lý thuyết và ứng dụng của ma trận và hệ các phương trình tuyến tính, biến đổi tuyến tính, giá trị riêng, vectơ riêng.
Nội dung: Logic, Tập. Ma trận, hệ phương trình tuyến tính. Không gian vectơ, hạng và ma trận nghịch đảo. Biến đổi tuyến tính. Giá trị riêng và vectơ riêng. Không gian Euclid, trực giao.'),
('ME001', 'Giáo dục quốc phòng', 'Military Education', 0, 0, 0, 'ĐC', NULL, NULL, NULL, NULL, 'P.ĐTĐH', 'Môn học trang bị cho học sinh, sinh viên những hiểu biết cơ bản về nền quốc phòng toàn dân, an ninh nhân dân, quan điểm của Đảng, chính sách, pháp luật của nhà nước về quốc phòng và an ninh; truyền thống chống ngoại xâm của dân tộc, lực lượng vũ trang nhân dân và nghệ thuật quân sự Việt Nam; xây dựng nền quốc phòng toàn dân, an ninh nhân dân, lực lượng vũ trang nhân dân; có kiến thức cơ bản, cần thiết về phòng thủ dân sự, kỹ năng quân sự; sẵn sàng thực hiện nghĩa vụ quân sự bảo vệ Tổ quốc.'),
('MEDU1', 'Giáo dục quốc phòng', 'Military Education', 0, 0, 0, 'QP', NULL, 'ME001', NULL, NULL, 'GDQP', NULL),
('MKTG4223', 'Quản trị chuỗi cung ứng', 'Supply Chain Management', 3, 3, 0, 'CN', NULL, NULL, NULL, NULL, 'HTTT', 'Cung cấp kiến thức về quản trị Logisitics làm nền tảng cho quản trị chuỗi cung ứng. Các hệ thống ERP hỗ trợ cho công tác hoạch định, tổ chức, thực thi, kiểm tra chuỗi cung ứng trong doanh nghiệp.'),
('MKTG5883', 'Khai phá dữ liệu và ứng dụng', 'Data Mining and Applications', 4, 3, 1, 'CN', NULL, NULL, NULL, 'CS5423', 'HTTT', 'Cung cấp các kiến thức về việc khai thác tri thức tiềm ẩn trong các ứng dụng CSDL. Người học được học các kiến thức về quy trình khai thác tri thức, bài toán tập phổ biến và luật kết hợp, bài toán chuỗi tuần tự, bài toán phân lớp, bài toán gom cụm và các ứng dụng của khai thác dữ liệu vào thực tiễn.'),
('MLPE1', 'Kinh tế chính trị Mác-Lênin (TE)', 'Marxism–Leninism political economy', 5, 5, 0, 'ĐC', NULL, 'MLPE2', NULL, NULL, 'P.ĐTĐH', NULL),
('MLPE2', 'Kinh tế chính trị Mác-Lênin (TE1)', 'Marxism–Leninism political economy', 4, 4, 0, 'ĐC', NULL, NULL, NULL, NULL, 'P.ĐTĐH', NULL),
('MM001', 'Kỹ năng truyền thông cho người làm công nghệ thông tin', 'Communication Skills for Information Technology Professionals', 3, 2, 1, 'CN', NULL, NULL, NULL, NULL, 'PĐTĐH', NULL),
('MM002', 'Truyền thông kỹ thuật số', 'Digital Communication', 3, 2, 1, 'CN', NULL, NULL, NULL, NULL, 'PĐTĐH', NULL),
('MM003', 'Quản trị sự kiện', 'Event Management', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'CNPM', NULL),
('MM004', 'Nguyên lý thiết kế đồ hoạ', 'Principles of Graphic Design', 2, 0, 2, 'CN', NULL, NULL, NULL, NULL, 'CNPM', NULL),
('MM005', 'Nhập môn marketing', 'Introduction to Marketing', 2, 2, 0, 'ĐC', NULL, NULL, NULL, NULL, 'CNPM', NULL),
('MM006', 'Tâm lý học đại cương', 'Introduction to Psychology', 2, 2, 0, 'ĐC', NULL, NULL, NULL, NULL, 'CNPM', NULL),
('MM007', 'Tư duy sáng tạo và xu hướng thiết kế truyền thông', 'Creative Thinking and Media Design Trends', 2, 2, 0, 'ĐC', NULL, NULL, NULL, NULL, 'CNPM', NULL),
('MM008', 'Kỹ năng truyền thông ứng dụng', 'Applied Communication Skills', 2, 2, 0, 'ĐC', NULL, NULL, NULL, NULL, 'CNPM', NULL),
('MM101', 'Giới thiệu ngành Truyền thông đa phương tiện', 'Introduction to Multimedia', 1, 1, 0, 'CSN', NULL, NULL, NULL, NULL, 'CNPM', NULL),
('MM102', 'Lý luận truyền thông đại chúng', 'Foundations of Mass Communication', 3, 3, 0, 'CSN', NULL, NULL, NULL, NULL, 'CNPM', NULL),
('MM103', 'Cơ sở tạo hình và nguyên lý thị giác', 'Basics of Form Creation and Visual Principles', 2, 2, 0, 'CSN', NULL, NULL, NULL, NULL, 'CNPM', NULL),
('MM104', 'Viết nội dung đa phương tiện', 'Strategic Multimedia Writing', 3, 3, 0, 'CSN', NULL, NULL, NULL, NULL, 'CNPM', NULL),
('MM105', 'Nhập môn kỹ thuật sản xuất nội dung đa phương tiện', 'Introduction to Multimedia Production', 3, 2, 1, 'CSN', NULL, NULL, NULL, NULL, 'CNPM', NULL),
('MM106', 'Thu thập và phân tích khám phá dữ liệu truyền thông đa phương tiện', 'Data Collection and Exploratory Analysis for Multimedia', 3, 2, 1, 'CSN', NULL, NULL, NULL, 'IT001', 'CNPM', NULL),
('MM107', 'Học máy ứng dụng trong truyền thông đa phương tiện', 'Applied Machine Learning for Multimedia', 3, 2, 1, 'CSN', NULL, NULL, NULL, 'IT001', 'CNPM', NULL),
('MM108', 'Tiếp thị số', 'Digital Marketing', 3, 2, 1, 'CSN', NULL, NULL, NULL, NULL, 'CNPM', NULL),
('MM109', 'Thiết kế đồ họa', 'Graphic Design Principles', 4, 3, 1, 'CSN', NULL, NULL, NULL, NULL, 'CNPM', NULL),
('MM110', 'Màu sắc và tâm lý thị giác trong thiết kế truyền thông', 'Color and Visual Perception Psychology in Media Design', 2, 2, 0, 'CSN', NULL, NULL, NULL, NULL, 'CNPM', NULL),
('MM201', 'Truyền thông và dư luận xã hội', 'Media and Public Discourse', 4, 3, 1, 'CN', NULL, NULL, NULL, 'IT001', 'CNPM', NULL),
('MM202', 'Học sâu ứng dụng trong truyền thông đa phương tiện', 'Applied Deep Learning for Multimedia', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'CNPM', NULL),
('MM203', 'Xử lý ngôn ngữ tự nhiên cho truyền thông đa phương tiện', 'Natural Language Processing for Multimedia', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'CNPM', NULL),
('MM204', 'Xử lý ảnh số và video trong truyền thông đa phương tiện', 'Digital Image and Video Processing for Multimedia', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'CNPM', NULL),
('MM205', 'Phân tích và hiểu nội dung đa phương thức', 'Multimodal Data Analysis and Understanding', 4, 3, 1, 'CĐTN', NULL, NULL, NULL, NULL, 'CNPM', NULL),
('MM206', 'Dữ liệu lớn ứng dụng trong truyền thông đa phương tiện', 'Big Data Applied in Multimedia', 4, 3, 1, 'CN', NULL, NULL, NULL, 'IT007', 'CNPM', NULL),
('MM207', 'Hệ thống khai phá dữ liệu mạng xã hội', 'Developing Social Network Data Mining Systems', 4, 3, 1, 'CĐTN', NULL, NULL, NULL, NULL, 'CNPM', NULL),
('MM208', 'Thiết kế và sản xuất ấn phẩm', 'Publication Design and Production', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'CNPM', NULL),
('MM209', 'Nghiệp vụ truyền thông và báo chí', 'Press Communication Profession', 4, 3, 1, 'CN', NULL, NULL, NULL, 'MM104', 'CNPM', NULL),
('MM210', 'Kỹ thuật quay phim biên kịch và hậu kỳ', 'Video Recording, Screen Writing and Post Processing Techniques', 4, 3, 1, 'CĐTN', NULL, NULL, NULL, NULL, 'CNPM', NULL),
('MM211', 'Thực tế ảo và thực tế tăng cường', 'Virtual and Augmented Reality', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'CNPM', NULL),
('MM212', 'Hoạt hình', 'Animation', 3, 2, 1, 'CN', NULL, NULL, NULL, NULL, 'CNPM', NULL),
('MM213', 'Quản lý dự án truyền thông đa phương tiện', 'Multimedia Project Management', 4, 3, 1, 'CĐTN', NULL, NULL, NULL, NULL, 'CNPM', NULL),
('MM214', 'Chiến lược phát triển thương hiệu', 'Brand Development Strategy', 3, 3, 0, 'CN', NULL, NULL, NULL, NULL, 'CNPM', NULL),
('MM215', 'Quan hệ công chúng trong marketing', 'Public Relations for Marketing', 3, 3, 0, 'CN', NULL, NULL, NULL, NULL, 'CNPM', NULL),
('MM216', 'Tối ưu hóa và tiếp thị trên công cụ tìm kiếm', 'Search Engine Optimization and Marketing', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'CNPM', NULL),
('MM217', 'Tiếp thị cho sản phẩm dịch vụ', 'Marketing for Service Products', 3, 3, 0, 'CN', NULL, NULL, NULL, NULL, 'CNPM', NULL),
('MM218', 'Xây dựng kênh tiếp thị trực tuyến', 'Building Online Marketing Channels', 3, 3, 0, 'CN', NULL, NULL, NULL, NULL, 'CNPM', NULL),
('MM219', 'Quản trị mối quan hệ khách hàng định hướng dữ liệu', 'Data Driven Customer Relationship Management', 4, 3, 1, 'CĐTN', NULL, NULL, NULL, NULL, 'CNPM', NULL),
('MM220', 'Phân tích dữ liệu truyền thông số', 'Digital Media Data Analytics', 4, 3, 1, 'CĐTN', NULL, NULL, NULL, NULL, 'CNPM', NULL),
('MM221', 'Chuyên đề các vấn đề hiện đại trong truyền thông đa phương tiện', 'Seminar on Modern Topics in Multimedia', 3, 3, 0, 'CN', NULL, NULL, NULL, NULL, 'CNPM', NULL),
('MM222', 'An ninh thông tin trong truyền thông đa phương tiện', 'Information Security in Multimedia', 4, 3, 1, 'CN', NULL, NULL, NULL, 'IT007', 'CNPM', NULL),
('MM223', 'Kể chuyện tương tác', 'Interactive Storytelling', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'CNPM', NULL),
('MM224', 'Hình họa cơ bản', 'Basic Drawing and Visual Observation', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'CNPM', NULL),
('MM301', 'Đồ án truyền thông đa phương tiện', 'Multimedia Projects', 2, 0, 2, 'CN', NULL, NULL, NULL, NULL, 'CNPM', NULL),
('MM302', 'Thực tập', 'Internship', 2, 0, 2, 'CN', NULL, NULL, NULL, NULL, 'CNPM', NULL),
('MM304', 'Khởi nghiệp ngành Truyền thông đa phương tiện', 'Startup in Multimedia', 2, 2, 0, 'CN', NULL, NULL, NULL, NULL, 'CNPM', NULL),
('MM504', 'Đồ án tốt nghiệp', 'Capstone Project', 6, 0, 6, 'DATN', NULL, NULL, NULL, NULL, 'CNPM', NULL),
('MM505', 'Khóa luận tốt nghiệp', 'Thesis', 10, 0, 10, 'KLTN', NULL, NULL, NULL, NULL, 'CNPM', NULL),
('MM506', 'Đồ án tốt nghiệp tại doanh nghiệp', 'Industry Capstone Project', 10, 0, 10, 'DATNDN', NULL, NULL, NULL, NULL, 'CNPM', NULL),
('MSIS207', 'Phát triển ứng dụng web', 'Web Application Development', 4, 3, 1, 'CN', NULL, NULL, NULL, 'CS5423
CS2134', 'HTTT', 'Cung cấp cho sinh viên những kiến thức, kỹ năng, phương pháp lập trình web động với ngôn ngữ lập trình PHP (ngôn ngữ lập trình mã nguồn mở được sử dụng rộng rãi trên thế giới); kỹ thuật lập trình Ajax trong PHP; một số Framework hỗ trợ viết web bằng PHP. Thiết kế và triển khai các ứng dụng web trong thực tế bằng ngôn ngữ lập trình web PHP, vận hành và bảo trì website.'),
('MSIS2433', 'Lập trình hướng đối tượng', 'Object-Oriented Programming', 3, 3, 0, 'CSN', NULL, NULL, NULL, 'CS2134', 'HTTT', 'Giới thiệu về lập trình hướng đối tượng, nhấn mạnh vào các nguyên tắc cơ bản của thiết kế có cấu trúc, phát triển, thử nghiệm. Bao gồm cú pháp ngôn ngữ, áp dụng mô hình hướng đối tượng vào thiết kế và phát triển các phần mềm, các nguyên lý trao đổi dữ liệu và các chủ đề liên quan. Nhấn mạnh lập trình hướng đối tượng với đóng gói (tạo ra các class và instance), thừa kế (xác định các class kế thừa và các methods từ các class có sẵn), và đa hình.'),
('MSIS3033', 'Quản lý dự án hệ thống thông tin', 'Information Systems Project Management', 3, 3, 0, 'CSN', NULL, NULL, NULL, NULL, 'HTTT', 'trình bày các khía cạnh quan trọng để quản lý, triển khai thành công một dự án hệ thống thông tin bao gồm hành vi, chiến lược, kỹ thuật, điều hành, định lượng, giao tiếp, những rủi ro,…'),
('MSIS3233', 'Khoa học quản lý', 'Management Science', 3, 3, 0, 'CN', NULL, NULL, NULL, 'MATH2154', 'HTTT', 'Khóa học đề cập đến việc đưa ra quyết định trong ngữ cảnh quản lý, bao gồm một số phương pháp tiếp cận có hệ thống để đưa ra quyết định trong các vấn đề thường gặp phải bởi các nhà quản lý. Trong thế giới hiện đại, trực giác trong việc ra quyết định có thể là một hướng dẫn không an toàn. Tính năng đặc biệt của khoa học quản lý là xây dựng một mô hình rõ ràng, đơn giản hóa các khía cạnh liên quan của tiến trình ra quyết định, các mô hình như vậy thường dựa trên các phương pháp toán học định lượng.'),
('MSIS3242', 'Quản lý chất lượng phần mềm', 'Software Quality Management', 3, 3, 0, 'CN', NULL, NULL, NULL, NULL, 'HTTT', 'Hiểu quá trình tạo ra các hệ thống phần mềm lớn, bao gồm thiết kế hệ thống, phát triển, bảo trì, kiểm thử và các tài liệu mô tả hệ thống, cách thức thực tế làm giảm chi phí phần mềm và tăng độ tin cậy và khả năng thay đổi. Môn học giới thiệu các khái niệm lý thuyết và những công cụ thực hành đảm bảo và đo lường chất lượng phần mềm, nội dung tập trung trên các chủ đề sau: Đảm bảo chất lượng, phòng ngừa và loại bỏ khiếm khuyết, kỹ thuật kiểm tra, giám sát, các chỉ định và phương pháp xác minh, cải tiến chất lượng phần mềm.'),
('MSIS3243', 'Lý thuyết quyết định quản lý', 'Managerial Decision Theory', 3, 3, 0, 'CN', NULL, NULL, NULL, 'MATH2153', 'HTTT', 'Khóa học cung cấp cho sinh viên kiến thức nền tảng của quá trình ra quyết định quản lý, ra quyết định dưới điều kiện rủi ro và không chắc chắn, các tiện ích, ứng dụng lý thuyết xác suất trong việc ra quyết định, các công cụ và kỹ thuật để hỗ trợ quá trình ra quyết định, phương pháp đa mục tiêu để đưa ra quyết định thông qua các case study. Sử dụng các mô hình ra quyết định kinh doanh với kết quả được điều chỉnh bởi các phân bố xác suất. Phân tích quyết định Bayes, các phép đo lường, mô phỏng và các mô hình kiểm kê,…'),
('MSIS3303', 'Phân tích thiết kế hệ thống', 'Systems Analysis and Design', 4, 3, 1, 'CN', NULL, NULL, NULL, 'CS5423', 'HTTT', 'Khóa học cung cấp các khái niệm, phương pháp tiếp cận và ứng dụng khác nhau để tái cấu trúc quy trình nghiệp vụ, các biện pháp để có những bước tiến khổng lồ để đạt được sự thống lĩnh thị trường kinh doanh năng động. Khóa học cho thấy tác động của sự tự động hóa các quy trình được thiết kế lại có thể làm tăng lợi thế cạnh tranh cho công ty. Sử dụng một số case study phổ biến ở các công ty với việc sắp xếp hợp lý các quy trình của họ đã làm giảm chi phí hoạt động đáng kể, tạo ra sự vượt trội và làm tăng giá trị cho tất cả các bên liên quan.'),
('MSIS4013', 'Thiết kế, quản lý và quản trị hệ cơ sở dữ liệu', 'Database Systems Design, Management and Administration', 3, 3, 0, 'CN', NULL, NULL, NULL, 'CS5423', 'HTTT', 'Các khía cạnh lý thuyết và nghiệp vụ của các mô hình dữ liệu và CSDL. An toàn dữ liệu, duy trì tính toàn vẹn CSDL và quản trị CSDL trong môi trường phân tán, mạng và dùng chung. Các khái niệm liên quan đến CSDL bao gồm CSDL hướng đối tượng và phát triển CSDL Web. Phân tích, thiết kế và hiện thực hệ CSDL dùng các công cụ CSDL và các ngôn ngữ cấp cao để đọc và xử lý dữ liệu.'),
('MSIS402', 'Điện toán đám mây', 'Cloud Computing', 3, 3, 0, 'CN', NULL, NULL, NULL, NULL, 'HTTT', 'Trình bày các khái niệm cơ bản của điện toán đám mây như: khái niệm, mô hình triển khai, mô hình dịch vụ, đặc trưng của các dịch vụ điện toán đám mây, ảo hóa và các thách thức đối với điện toán đám mây; Nguyên lý hoạt động của ảo hóa; nguyên lý xử lý phân tán, minh họa trên một trong số công nghệ nền tảng đám mây. Phân tích và lựa chọn mô hình dịch vụ điện toán đám mây phù hợp với nhu cầu của tổ chức, doanh nghiệp; Kỹ năng cài đặt một số thuật toán xử lý phân tán đơn giản trên một trong số các công nghệ nền tảng đám mây.'),
('MSIS405', 'Dữ liệu lớn', 'Big Data', 3, 3, 0, 'CN', NULL, NULL, NULL, NULL, 'HTTT', 'Giới thiệu tổng quan dữ liệu lớn và những thách thức của dữ liệu lớn (khả năng phân tích, xử lý). Giới thiệu những kỹ thuật R statistics, Hadoop và Map reduce để trực quan hóa và phân tích dữ liệu lớn và tạo ra các mô hình thống kê.'),
('MSIS406', 'Dữ liệu lớn trên nền điện toán đám mây', 'Big Data in Cloud Computing', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'HTTT', NULL),
('MSIS4133', 'Công nghệ thông tin trong thương mại điện tử', 'Information Technologies for E-Commerce', 3, 3, 0, 'CN', NULL, NULL, NULL, NULL, 'HTTT', 'Sử dụng các kỹ thuật, hệ thống và các ứng dụng Web trên Internet cho phép các tổ chức vượt qua rào cản thời gian, khoảng cách địa lý trong thương mại. Những ứng dụng bao gồm mã mở và ngôn ngữ đánh dấu (scripting and markup languages), các công cụ lập trình Web, và các kỹ thuật kết nối trong quá trình thiết kế và phát triển các hệ thống ứng dụng thương mại điện tử.'),
('MSIS4243', 'Điều khiển và giám sát hệ thống thông tin', 'Control and Audit of Information Systems', 3, 3, 0, 'CN', NULL, NULL, NULL, NULL, 'HTTT', 'Khóa học cung cấp quy trình xác thực, lưu trữ và trích xuất các minh chứng (bằng chứng) điện tử. Giám sát và điều tra về xâm nhập hệ thống mạng và máy chủ, phân tích thông tin thu thập được và chuẩn bị các chứng cứ xác thực dựa trên nhu cầu. Các công cụ pháp lý và tài nguyên cho những người quản trị hệ thống và văn phòng an ninh hệ thống thông tin. Đạo đức, luật, chính sách và tiêu chuẩn liên quan đến minh chứng kỹ thuật số.'),
('MSIS4263', 'Các ứng dụng thông minh và hỗ trợ ra quyết định', 'Decision Support and Intelligent Applications', 3, 3, 0, 'CN', NULL, NULL, NULL, NULL, 'HTTT', 'Trình bày các kỹ thuật và công cụ quản lý tri thức áp dụng để hỗ trợ ra quyết định, các hệ hỗ trợ ra quyết định, các kỹ thuật khai phá dữ liệu'),
('MSIS4363', 'Các chủ đề nâng cao trong phát triển hệ thống', 'Advanced topics in systems development', 3, 3, 0, 'CNTC', NULL, NULL, NULL, NULL, 'HTTT', NULL),
('MSIS4443', 'Các hệ thống mô phỏng trên máy tính', 'Computer-based Simulation Systems', 3, 3, 0, 'CN', NULL, NULL, NULL, NULL, 'HTTT', 'Mô phỏng các hệ thống dựa trên các sự kiện diễn ra. Một số hệ thống được mô phỏng như: hệ thống lưu trữ, quản lý tài chính, giao tiếp dữ liệu, các vấn đề về hệ thống thông tin hoặc các trạng thái hàng đợi. Thu thập và ước lượng các dữ liệu liên kết, sinh viên cần hiểu rõ sự mô phỏng là công cụ hữu ích trong khoa học quản lý cũng như trong các hệ thống thông tin.'),
('MSIS4523', 'Hệ truyền thông dữ liệu', 'Data Communication Systems', 3, 3, 0, 'CN', NULL, 'ACCT5123', NULL, NULL, 'HTTT', 'Bao quát các loại mạng và giao thức mạng được sử dụng để điều khiển các giọng nói khác nhau, hình ảnh, và những nhu cầu dữ liệu thương mại ngày nay. Hiểu các thuật ngữ dùng trong mạng và chức năng vận hành của các thành phần viễn thông.'),
('MSIS4800', 'Hệ thống thông tin tính toán', 'Accounting information system', 3, 3, 0, 'CNTC', NULL, NULL, NULL, NULL, 'HTTT', NULL),
('MSIS4801', 'Quản lý thông tin địa lý', 'Geospatial information management', 4, 3, 1, 'CNTC', NULL, NULL, NULL, NULL, 'HTTT', NULL),
('MSIS5723', 'Phân tích thiết kế hệ thống thông tin', 'Information Systems Analysis and Design', 4, 3, 1, 'CN', NULL, NULL, NULL, 'IT004', 'HTTT', NULL),
('Mã MH', 'Tên MH (Tiếng Việt)', 'Tên MH (Tiếng Anh)', 0, 0, 0, 'Loại MH', NULL, 'Mã môn học tương đương', 'Mã môn học tiên quyết', 'Mã môn học trước', 'Đơn vị quản lý chuyên môn', NULL),
('NHJP1', 'Tiếng Nhật Sơ cấp 1', 'Japanese for Beginners 1', 0, 0, 0, 'BT', NULL, NULL, NULL, NULL, 'PĐTĐH', NULL),
('NHJP2', 'Tiếng Nhật Sơ cấp 2', 'Japanese for Beginners 2', 0, 0, 0, 'BT', NULL, NULL, NULL, NULL, 'PĐTĐH', NULL),
('NNH050', 'Ngôn ngữ quảng cáo', 'The Language of Advertising', 2, 2, 0, 'CN', NULL, NULL, NULL, NULL, 'PĐTĐH', NULL),
('NT005', 'Giới thiệu ngành Mạng máy tính và Truyền thông dữ liệu', 'Introduction to Computer Networks and Data Communication', 1, 1, 0, 'ĐC', NULL, 'CS005
IE005
IS005
SE005
CE005
NT015
EC005
DS005
AI001', NULL, NULL, 'MMT&TT', 'Môn học cung cấp các kiến thức giới thiệu về ngành CNTT nói chung và các chuyên ngành sâu nói riêng, cung cấp cho sinh viên biết trong ngành sẽ học những gì và ra trường sẽ làm được gì, làm ở đâu.'),
('NT008', 'Mạng truyền thông và di động', 'Mạng truyền thông và di động', 4, 4, 0, 'CHUA_PHAN_LOAI', NULL, NULL, NULL, NULL, 'Chưa rõ', NULL),
('NT009', 'Lập trình ứng dụng Mạng', 'Lập trình ứng dụng Mạng', 4, 4, 0, 'CHUA_PHAN_LOAI', NULL, NULL, NULL, NULL, 'Chưa rõ', NULL),
('NT015', 'Giới thiệu ngành An toàn Thông tin', 'Introduction to Information Security Programs', 1, 1, 0, 'ĐC', NULL, 'CS005
IE005
IS005
SE005
CE005
NT005
EC005
DS005
AI001', NULL, NULL, 'MMT&TT', 'Môn học cung cấp các kiến thức giới thiệu về ngành CNTT nói chung và các chuyên ngành sâu nói riêng gồm: Khoa học máy tính, Kỹ thuật máy tính, Kỹ thuật phần mềm, Mạng máy tính & truyền thông và Hệ thống thông tin. Trong đó cung cấp cho sinh viên biết trong mỗi ngành sẽ học những gì và ra trường sẽ làm được gì, làm ở đâu, đặc biệt là ngành An toàn thông tin.'),
('NT101', 'An toàn mạng máy tính', 'Network Security', 4, 3, 1, 'CSN', NULL, 'NT140', NULL, 'IT005', 'MMT&TT', 'Tổng quan về các nguyên tắc an ninh Mạng máy tính; Xây dựng một tổ chức an toàn, quyền truy cập và kiểm soát truy cập; Phương thức ngăn chặn việc tấn công hệ thống, bảo vệ chống lại việc tấn công Botnet, các vấn đề bảo vệ hệ thống mạng chống phần mềm độc hại, an ninh mạng Windows và Unix / Linux, bảo mật mạng truyền dẫn; bảo mật mạng LAN, mạng không dây và mạng di động.'),
('NT102', 'Điện tử cho công nghệ thông tin', 'Electronics for information technology', 4, 3, 1, 'CSN', NULL, 'PH002', NULL, NULL, 'MMT&TT', NULL),
('NT103', 'Hệ điều hành Linux', 'Linux Operating Systems', 4, 3, 1, 'CNTC', NULL, 'NT131', NULL, NULL, 'MMT&TT', 'Môn “Hệ điều hành Linux” cung cấp cho sinh viên những kiến thức tổng quan về hệ điều hành Linux, cách thức cài đặt, giao diện sử dụng và các thủ tục làm việc cơ bản với Linux; tổ chức hệ thống tập tin, hệ thống tài khoản, và các thủ tục quản lý hệ thống tập tin, tài khoản; quản trị hệ thống bao gồm quản lý tiến trình, quản lý đĩa, biến môi trường …; shell script, cách viết một script. Cấu hình các dịch vụ trên linux.'),
('NT104', 'Lý thuyết thông tin', 'Information Theory', 3, 3, 0, 'CSN', NULL, NULL, NULL, 'IT001', 'MMT&TT', 'Môn học trình bày các vấn đề lý thuyết thông tin rời rạc như lượng tin, entropy, nguồn rời rạc và kênh rời rạc, các phương pháp mã hoá và giải mã, mã hoá tối ưu về kích thước, mã hoá chống nhiễu thông tin trên đường truyền, mã hoá bảo mật thông tin'),
('NT105', 'Truyền dữ liệu', 'Data Communications', 4, 3, 1, 'CSN', NULL, NULL, NULL, 'IT005', 'MMT&TT', 'Môn “Truyền dữ liệu” cung cấp cho sinh viên những kiến thức tổng quan về kỹ thuật truyền dữ liệu, các phương pháp mã hóa và điều chế; các kỹ thuật truyền dữ liệu số và các chuẩn giao tiếp vật lý; các cơ chế điều khiển liên kết dữ liệu, kỹ thuật ghép kênh trong truyền dữ liệu và các mạng truyền dữ liệu.'),
('NT106', 'Lập trình mạng căn bản', 'Basic Network Programming', 3, 2, 1, 'CSN', NULL, NULL, NULL, 'IT005
IT001', 'MMT&TT', 'Môn học này dành cho sinh viên khoa Mạng máy tính và viễn thông, nội dung của môn học nhằm trang bị cho sinh viên những kiến thức về:
• Kiến thức cơ bản về ứng dụng mạng, giao thức
• Thiết kế và lập trình chương trình Client Server
• Phát triển các ứng dụng unicast/multicast/broadcast
• Kỹ thuật phân tán ứng dụng: Web services, .NET remoting (hoặc RMI, CORBA)'),
('NT107', 'Xử lý tín hiệu trong truyển thông', 'Signal Processing for Communications', 4, 3, 1, 'CN', NULL, 'NT332', NULL, NULL, 'MMT&TT', NULL),
('NT108', 'Mạng truyền thông và di động', 'Communication and mobile networks', 3, 3, 0, 'CN', NULL, 'NT132', NULL, NULL, 'MMT&TT', NULL),
('NT109', 'Lập trình ứng dụng mạng', 'Network-Based Application Development', 3, 2, 1, 'CSN', NULL, NULL, NULL, 'IT004
NT106', 'MMT&TT', '• Kiến thức cơ bản về ứng dụng web và mô hình phát triển ứng dụng
• Công nghệ J2EE
• MVC framework và các framework thông dụng: Struts/Spring
• An toàn ứng dụng mạng'),
('NT110', 'Tín hiệu và mạch', 'Signals and circuits', 3, 3, 0, 'CN', NULL, 'PH001', NULL, 'IT002', 'MMT&TT', NULL),
('NT111', 'Thiết bị mạng và truyền thông ĐPT', 'Network appliances and multimedia communications', 4, 3, 1, 'CNTC', NULL, NULL, NULL, 'IT005', 'MMT&TT', NULL),
('NT112', 'Công nghệ mạng viễn thông', 'Communications technology', 3, 2, 1, 'CN', NULL, 'NT402', NULL, 'IT005', 'MMT&TT', NULL),
('NT113', 'Thiết kế mạng', 'Network Design', 3, 2, 1, 'CSN', NULL, NULL, NULL, 'NT132', 'MMT&TT', 'Các nội dung bao gồm: Phân tích các mục tiêu thiết kế mạng, các đặc trưng hệ thống mạng. Thiết kế một cấu trúc liên kết mạng, lựa chọn thiết bị và phần mềm. Các chiến lược bảo mật và quản lý mạng. Công nghệ và thiết bị cho mạng Campus, mạng doanh nghiệp. Kiểm tra thiết kế mạng và tối ưu hóa thiết kế mạng. Xây dựng tài liệu thiết kế mạng.'),
('NT114', 'Đồ án chuyên ngành', 'Specialized Project', 2, 0, 2, 'ĐA', NULL, NULL, NULL, NULL, 'MMT&TT', 'Đồ án chuyên ngành yêu cầu sinh viên áp dụng tất cả những kiến thức và kỹ năng đã học để giải quyết một vấn đề thực tế của chuyên ngành mà sinh viên lựa chọn dưới sự hướng dẫn của giảng viên.'),
('NT115', 'Thực tập doanh nghiệp', 'Internship', 3, 3, 0, 'TTTN', NULL, 'NT215', NULL, NULL, 'MMT&TT', 'Trong chương trình thực tập cuối khóa sinh viên phải đến thực tập tại các công ty, đơn vị sự nghiệp trong lĩnh vực để làm quen với môi trường thực tế của nghề nghiệp; nắm bắt các công việc; học hỏi kinh nghiệm trong quá trình tác nghiệp tại các đơn vị thực tập, xử lý các tình huống phát sinh liên quan đến lĩnh vực chuyên môn mà sinh viên đã lựa chọn.'),
('NT116', 'Kỹ năng mềm', 'Soft skills', 2, 0, 2, 'CN', NULL, 'SS004', NULL, NULL, 'MMT&TT', NULL),
('NT117', 'Đồ án môn học lập trình ứng dụng mạng', 'Network-Based Application Programming Project', 2, 0, 2, 'ĐA', NULL, NULL, NULL, NULL, 'MMT&TT', NULL),
('NT118', 'Phát triển ứng dụng trên thiết bị di động', 'Mobile Application Development', 3, 2, 1, 'CSN', NULL, NULL, NULL, 'IT002', 'MMT&TT', 'Trang bị cho sinh viên những kiến thức và kỹ năng sau đây:
• Môn học bao gồm các mô hình phát triển ứng dụng di động trên Google Android. Sinh viên sẽ được học việc triển khai thiết kế giao diện hiệu quả cho các thiết bị di động hiện đại. Sinh viên sẽ bắt đầu bằng cách sử dụng mô phỏng trước khi cài đặt đến các thiết bị thực tế. Sinh viên được cung cấp kiến thức để xây dựng ứng dụng Native app lẫn cross platform app. Trong ứng dụng native app, sinh viên sử dụng ngôn ngữ lập trình Java để thể hiện chương trình trên Android.
• Trong ứng dụng Native app, sinh viên sử dụng HTML và CSS để xây dựng ứng dụng đáp ứng trên mọi thiết bị, sử dụng JavaScript để tạo ra một ứng dụng chuyển tiếp, liên lạc và swipe, hình ảnh động. Trong khóa học, sinh viên sẽ được khuyến khích để tích hợp các dịch vụ web hiện có từ Google và Amazon như là một phần của ứng dụng của họ.
• Thông qua lý thuyết và thực hành, sinh viên có khả năng thu thập chứng cứ tại máy tính cá nhân cũng như tại nơi xảy ra sự cố bằng cách sử dụng các công cụ thu thập dữ liệu.
Các nội dung bao gồm:
• Giới thiệu về tính toán di động khắp mọi nơi, tính toán cảm ngữ cảnh, giới thiệu hệ điều hành Android và các phương pháp lập trình trên Android. Các phương pháp lập trình nâng cao: đa luồng, đa hành vi, kết nối SQLite, Web Services.
• Khái niệm cross platform, thiết kế web di động, ứng dụng cho Điện thoại di động. Đánh dấu cho điện thoại di động. Web Apps di động và tính năng thiết bị từ Web Apps. Giới thiệu PhoneGap. Bản địa hóa ứng dụng.'),
('NT119', 'Mật mã học', 'Cryptography', 4, 3, 1, 'CSN', NULL, 'NT219', NULL, 'MA001
MA002', 'MMT&TT', NULL),
('NT121', 'Thiết bị mạng và truyền thông đa phương tiện', 'Network Appliances and Multimedia Communications', 3, 2, 1, 'CĐTN', NULL, 'NT111', NULL, NULL, 'MMT&TT', '• Cấu tạo và cách cấu hình các thiết bị như switch, router…
• Các giải thuật định tuyến thông dụng như: RIP, OSPF…
• Mạng cục bộ ảo (VLAN)
• Cách cấu hình danh sách điều khiển truy cập
• NAT, PAT
• Một số phương pháp kết nối mạng WAN
• Công nghệ đa phương tiện và các chuẩn
• Một số ứng dụng của truyền thông đa phương tiện'),
('NT130', 'Cơ chế hoạt động của mã độc', 'Malware modus operandi', 4, 3, 1, 'CSN', NULL, 'NT230', NULL, 'IT007
NT101', 'MMT&TT', NULL),
('NT131', 'Hệ thống nhúng mạng không dây', 'Wireless Embedded Network Systems', 4, 3, 1, 'CSN', NULL, NULL, NULL, 'IT007', 'MMT&TT', 'Cung cấp những khái niệm tổng quan và kiến thức nền tảng về hệ thống nhúng và mạng không dây. Môn học cũng trình bày chi tiết kiến trúc các thiết bị nhúng đầu cuối sử dụng giao tiếp mạng không dây dựa trên nền tảng các hệ điều hành mã nguồn mở như Linux OSes, TinyOS, ContikiOS.'),
('NT132', 'Quản trị mạng và hệ thống', 'Networks and Systems Administration', 4, 3, 1, 'CSN', NULL, NULL, NULL, 'IT005', 'MMT&TT', 'Trang bị cho sinh viên những kiến thức và kỹ năng chuyên về thiết kế, cài đặt và quản trị hệ thống mạng trên nền hệ điều hành Windows, Linux, cũng như cấu hình và quản trị hạ tầng mạng, thiết bị mạng. Cụ thể hơn, môn học hướng dẫn kỹ thuật: i) thiết kế, cài đặt và cấu hình mạng; ii) quản trị tài khoản, người dùng, nhóm người dùng, máy tính, chính sách...; iii) quản trị cơ sở hạ tầng mạng: dịch vụ, cấu hình địa chỉ IP động, tên miền, cấp chứng nhận số và mạng riêng ảo,…; iv) quản trị dịch vụ WWW, truyền tập tin, thư điện tử, chia sẻ tập tin và máy in; v) các cơ chế và công cụ bảo mật hệ thống và hạ tầng mạng; vi) công cụ quản trị mạng, hệ thống và SNMP.'),
('NT133', 'An toàn kiến trúc hệ thống', 'Systems Architecture Security', 3, 2, 1, 'CN', NULL, NULL, NULL, 'IT006
IT007', 'MMT&TT', 'Để tạo ra được một hệ thống an toàn có nhiều mô hình đã được nghiên cứu. Một số trong các mô hình đã được hiện thực hóa trong phần cứng, hệ điều hành. Môn học này nhằm giới thiệu các mô hình an toàn hệ thống như state machine, Bell-LaPadula, Biba,…Ngoài ra, các kiểu vận hành an toàn (như dedicated system mode, system high security mode), các chuẩn đánh giá hệ thống cũng được đề cập, các mô hình an toàn thông tin trong hệ thống phân tán, điện toán đám mây.'),
('NT137', 'Kỹ thuật phân tích mã độc', 'Malware Analysis Techniques', 3, 2, 1, 'CN', NULL, NULL, NULL, NULL, 'MMT&TT', 'Số lượng mã độc ngày càng lớn, việc nắm vững cơ chế phân tích tìm hiểu về mã độc là thực sự cần thiết. Môn học này trang bị cho sinh viên các kiến thức, kĩ thuật cập nhật nhất về phân tích mã độc. Hai kĩ thuật phần tích chính là phân tích tĩnh và phân tích động được trình bày'),
('NT140', 'An toàn mạng', 'Network Security', 4, 3, 1, 'CN', NULL, NULL, NULL, 'IT005', 'MMT&TT', NULL),
('NT201', 'Phân tích thiết kế hệ thống truyền thông và mạng', 'Communication and Network Systems Analysis and Design', 3, 3, 0, 'CNTC', NULL, 'NT532', NULL, NULL, 'MMT&TT', 'Chương trình được chia làm hai phần chính:
• Phần một bắt đầu từ chương 1 đến chương 6 đề cập đến việc phân tích hệ thống: những khái niệm cơ bản, kiến trúc, nhiệm vụ của hệ thống, các giai đoạn và trạng thái hoạt động của hệ thống.
• Phần hai từ chương 7 đến chương 12 đề cập đến việc thiết kế và phát triển hệ thống, bao gồm: chiến lược phát triển hệ thống, đặc tả hệ thống, vấn đề thực hiện phát triển hệ thống, phân tích các hỗ trợ ra quyết định và các thủ tục kiểm tra và phê chuẩn.'),
('NT202', 'Đồ án môn Lập trình ứng dụng mạng', 'Project on Nerwork-Application Programming', 2, 0, 2, 'ĐA', NULL, 'NT117', NULL, NULL, 'MMT&TT', NULL),
('NT203', 'Đồ án chuyên ngành', 'Specialized Project', 2, 0, 2, 'ĐA', NULL, 'NT114', NULL, NULL, 'MMT&TT', NULL),
('NT204', 'Hệ thống tìm kiếm, phát hiện và ngăn ngừa xâm nhập', 'Intrusion Detection and Prevention Systems', 3, 2, 1, 'CN', NULL, NULL, NULL, NULL, 'MMT&TT', 'Tổng quan về các Hệ thống tìm kiếm, phát hiện và ngăn ngừa xâm nhập. Các phương thức ngăn chặn tấn công, đóng lỗ hổng. Các hệ thống cảnh báo tấn công và thu thập thông tin về các cuộc tấn công mạng. Cách thức thu thập chứng cứ pháp lý và hoành thiện báo cáo đầy đủ. Các tính năng không an toàn như tin nhắn được mã hóa và đường hầm VPN trong các IDS và khả năng hạn chế hoạt động hacker'),
('NT205', 'Tấn công mạng', 'Network Offences', 3, 2, 1, 'CNTC', NULL, NULL, NULL, 'NT101', 'MMT&TT', 'Tóm tắt nội dung:
\- Kiến thức lý thuyết về những lỗ hổng bảo mật phổ biến tồn tại trong hệ thống mạng, hệ điều hành, ứng dụng.
\- Các phương pháp tấn công dựa vào các lỗ hổng đã phát hiện.
\- Các bước thực hiện tấn công chiếm quyền điều khiển hệ thống, thay đổi dữ liệu hay từ chối dịch vụ…
\- Xây dựng hệ thống phòng thủ ngăn chặn các cuộc tấn công
Đối với hệ Cử nhân tài năng:
\- Trình bày chuyên sâu hơn về các giao thức mạng và việc tận dụng các lỗ hổng trong giao thức để tấn công; cách thức tấn công trên webserver cấu hình mạnh; các phương pháp tấn công ứng dụng web; cách thức tấn công và phòng chống lại các cuộc tấn công mạng trong tương lai.
\- Bổ sung các bài tập nâng cao về việc sử dụng các công cụ crack password phức tạp và leo thang đặc quyền, xoá dấu vết, tấn công DDoS, cách thức điều khiển các zombie và xây dựng các mạng BotNet.
\- Sinh viên thực hiện seminar chuyên đề theo định hướng của chương trình tài năng, trong đó sinh viên được chọn đề tài, tự tìm tài liệu và thực hiện báo cáo theo yêu cầu của giảng viên.'),
('NT206', 'Quản trị hệ thống mạng', 'Computer network administration', 3, 2, 1, 'CN', NULL, 'NT531', NULL, 'IT005', 'MMT&TT', NULL),
('NT207', 'Quản lý rủi ro và an toàn thông tin trong doanh nghiệp', 'Risk Management and Information Security in Enterprise', 3, 2, 1, 'CN', NULL, NULL, NULL, 'NT101', 'MMT&TT', 'Các loại rủi ro của công ty và các phương tiện thiết thực bảo vệ chống rủi ro. Vị trí an ninh thông tin trong các cơ quan của chính phủ, các tổ chức thương mại và công nghiệp. Xây dựng chương trình quản lý rủi ro hiệu quả, đánh giá hiệu năng chống rủi ro của một chương trình bảo mật thông tin.'),
('NT208', 'Lập trình ứng dụng web', 'Web-Based Application Development', 3, 2, 1, 'CNTC', NULL, 'NT507
NT307
NT533', NULL, 'IT002
IT004', 'MMT&TT', '• Kiến thức về phát triển ứng dụng Web và nền tảng mã nguồn mở
• Kỹ thuật client-side / server-side
• Phát triển ứng dụng web dựa trên framework mã nguồn mở
• Căn bản về Web service và phát triển ứng dụng với Web service
• Căn bản về yêu cầu bất đồng bộ và Ajax
• Căn bản về RSS và kỹ thuật liên quan'),
('NT209', 'Lập trình hệ thống', 'Computer System Programming', 3, 2, 1, 'CSN', NULL, NULL, NULL, 'IT006
IT001', 'MMT&TT', 'Cung cấp cho sinh viên những khái niệm cơ bản về lập trình hệ thống máy tính ở dạng ngôn ngữ Asssembly, cách chuyển đổi ngôn ngữ cấp cao sang mã máy và ngược lại. Môn học cung cấp kiến thức và kỹ năng tối ưu hóa chương trình, những khái niệm về stack, pointer, cache và kiến trúc máy tính để từ đó xây dựng được chương trình an toàn hơn, hiệu quả hơn và có tầm nhìn hệ thống hơn. Đồng thời, kiến thức của môn này còn phục vụ cho các kỹ thuật dịch ngược, debug và kiểm lỗi phần mềm.'),
('NT210', 'Thương mại điện tử và triển khai ứng dụng', 'E-Commerce and Applications', 3, 2, 1, 'CĐTN', NULL, NULL, NULL, 'IT005
NT106', 'MMT&TT', 'Các nội dung bao gồm: Hoạt động thương mại và Thương mại Điện tử. Các mô hình Thương mại Điện tử. E-Marketing, M-commerce. Công nghệ cho Website, Web động, Web tĩnh, PHP và MySQL. Các nguy cơ về an ninh trong Thương mại Điện tử. Bảo mật thông tin, chứng thực số và chữ ký điện tử. Giao dịch điện tử trong Thương mại Điện tử. Quản trị Doanh nghiệp trong Thương mại Điện tử. Quản trị tài nguyên doanh nghiệp (ERP), quản lý quan hệ khách hàng (CRM), quản lý chuỗi cung ứng (SCM). Sinh viên được thực tập triển khai phần mềm thương mại điện tử mã nguồn mở EcShop và phần mềm quản lý quan hệ khách hàng Vtiger CRM.'),
('NT211', 'An ninh nhân sự, định danh và chứng thực', 'Personnel Security, Identification, and Authentication', 3, 2, 1, 'CNTC', NULL, NULL, NULL, NULL, 'MMT&TT', 'Môn học đề cập tới những khái niệm căn bản về định danh, xác thực và ứng dụng của chúng trong quản lý truy cập. Các công nghệ hiện đại trong định danh và xác thực được đề cập trong lý thuyết cũng như qua các bài thực hành dưới dạng các trường hợp sử dụng thực (use case).
Môn học trang bị cho sinh viên ngành an ninh thông tin:
\- Khái niệm nền tảng về an ninh liên quan tới con người,
\- Kiến thức về định danh cùng các công nghệ định danh hiện đại
\- Kiến thức về xác thực và những công nghệ liên quan đến xác thực
\- Ứng dụng định danh và xác thực trong hệ thống CNTT
Đối với hệ Cử nhân tài năng:
\- Trình bày chuyên sâu hơn các nội dung Sinh trắc và các phương pháp chính; Quản lý tài khoản với Token; Quản lý tài khoản liên hợp;Tấn công thẻ thông minh.
\- Bổ sung các bài tập nâng cao ở các nội dung trình bày chuyên sâu trên và các nội dung Bẻ mật khẩu phức tạp; Thiết kế, xây dựng và triển khai hệ thống cấp chứng chỉ số ở quy mô lớn.
\- Sinh viên thực hiện seminar chuyên đề theo định hướng của chương trình tài năng, trong đó sinh viên được chọn đề tài, tự tìm tài liệu và thực hiện báo cáo theo yêu cầu của giảng viên.'),
('NT212', 'An toàn dữ liệu, khôi phục thông tin sau sự cố', 'Data Integrity and Disaster Recovery', 3, 2, 1, 'CNTC', NULL, NULL, NULL, 'IT003
IT006
IT007', 'MMT&TT', 'Tổng quan về quy trình, phương pháp quy hoạch, và các nguyên tắc khắc phục sau sự cố đối với một doanh nghiệp.
\- Phương pháp triển khai khắc phục sự cố, đánh giá kết quả của một sự cố, và làm thế nào để bảo vệ thông tin thiết yếu.
\- Các nguyên tắc triển khai một kế hoạch khắc phục sự cố, các thử nghiệm liên quan đến khắc phục sự cố, hiệu lực kiểm soát thông tin trong một sự cố, và ghi nhận các đánh giá từ việc thực hiện chức năng khôi phục thông tin.
Đối với hệ cử nhân tài năng:
\- Trình bày chuyên sâu hơn các nội dung như đánh giá rủi ro của các sự cố có liên quan đến dữ liệu và phân tích tác động của chúng đối với hoạt động của tổ chức, hệ thống các biện pháp đề phòng và phục hồi sau sự cố,
\- Bổ sung bài tập nâng cao về các kỹ thuật sao lưu dữ liệu của tổ chức trên máy cá nhân và trên mạng của tổ chức, các công cụ phục hồi cứu hộ dữ liệu, xây dựng quy trình quản lý dữ liệu và giảm thiểu rủi ro khi có sự cố.
\- Sinh viên thực hiện seminar chuyên đề theo định hướng của chương trình tài năng, trong đó sinh viên được chọn đề tài, tự tìm tài liệu và thực hiện báo cáo theo yêu cầu của giảng viên.'),
('NT213', 'Bảo mật web và ứng dụng', 'Web and Application Security', 3, 2, 1, 'CN', NULL, NULL, NULL, 'NT208', 'MMT&TT', 'Môn học này cung cấp kiến thức tổng quan về hack ứng dụng Web. Bên cạnh đó, sinh viên sẽ nắm bắt được các kỹ thuật bảo mật như: thu thập thông tin, xác nhận đầu vào tại server side, bảo mật cho client-side framework. Ngoài ra, môn học cũng cung cấp kiến thức về Malware trên nền web'),
('NT215', 'Thực tập doanh nghiệp', 'Internship', 2, 2, 0, 'TTTN', NULL, NULL, NULL, NULL, 'MMT&TT', 'Trong chương trình thực tập cuối khóa sinh viên phải đến thực tập tại các công ty, đơn vị sự nghiệp trong lĩnh vực để làm quen với môi trường thực tế của nghề nghiệp; nắm bắt các công việc; học hỏi kinh nghiệm trong quá trình tác nghiệp tại các đơn vị thực tập, xử lý các tình huống phát sinh liên quan đến lĩnh vực chuyên môn mà sinh viên đã lựa chọn.'),
('NT216', 'Bảo mật hệ thống dữ liệu', 'Data System Security', 3, 2, 1, 'CNTC', NULL, NULL, NULL, NULL, 'MMT&TT', NULL),
('NT219', 'Mật mã học', 'Cryptography', 3, 2, 1, 'CN', NULL, 'NT119', NULL, NULL, 'MMT&TT', '• Lược sử mã hóa.
• Các khái niệm cơ bản trong lý thuyết thông tin.
• Khóa bí mật; mã hóa (DES, thám mã sai phân) và mã chứng thực thông điệp.
• Khóa công khai; mã hóa và chữ ký (RSA, Elgamal, Rabin).
• Hàm băm một-chiều và tính kháng đụng độ.
• Định nghĩa và chứng minh hình thức (dựa trên trò chơi) các tính chất an ninh.
• Lược đồ định danh và tri thức trị không.
• Hạ tầng khóa công khai'),
('NT230', 'Cơ chế hoạt động của mã độc', 'Malware: Modes of Operation', 3, 2, 1, 'CSN', NULL, 'NT130', NULL, 'NT209', 'MMT&TT', 'Mã độc là chủ đề quan trọng trong an toàn thông tin. Việc hiểu được cơ chế hoạt động của mã độc sẽ giúp ích trong việc xây dựng được các hệ thống phát hiện, ngăn chặn chúng. Do đó, môn học này có mục tiêu trang bị cho sinh viên kiến thức cả lý thuyết và thực hành về hoạt động của các mã độc thông dụng. Cụ thể môn học trình bày cơ chế vận hành, các kĩ thuật được sử dụng của các mã độc thông dụng như virus, sâu, botnet, rootkit, ...'),
('NT301', 'Quản trị hệ thống mạng', 'Computer network administration', 3, 2, 1, 'CN', NULL, 'NT206
NT531', NULL, NULL, 'MMT&TT', NULL),
('NT302', 'Xây dựng chuẩn chính sách an toàn thông tin trong doanh nghiệp', 'Enterprise information security policy', 3, 2, 1, 'CN', NULL, 'NT331', NULL, NULL, 'MMT&TT', NULL),
('NT303', 'Công nghệ thoại IP', 'VoIP Phone Technology', 3, 2, 1, 'CN', NULL, 'NT536', NULL, 'NT105', 'MMT&TT', 'Nội dung môn học bao gồm tổng quan về xu thế phát triển của Internet và công nghệ thoại IP, các giao thức báo hiệu và xử lý cuộc gọi. Môn học cũng đề cập đến các kỹ thuật nén tín hiệu thoại bao gồm nguyên lý chung đến các chuẩn nén. Các cách thức ghi địa chỉ, đánh số, phương pháp định tuyến giữa mạng điện thoại truyền thống (PSTN) với mạng điện thoại IP và các vấn đề đo kiểm chất lượng dịch vụ thoại IP, các thiết bị, phần mềm đầu cuối và triển khai mạng điện thoại IP. Môn học cũng đề cập các công nghệ VoIP của Cisco và mã nguồn mở Asterisk trong đó trình bầy chuyên sâu về lập trình một mạng thoại với nguồn mở Asterisk.'),
('NT304', 'Ứng dụng truyền thông và an ninh thông tin', 'Applications of communications and information security', 3, 2, 1, 'CN', NULL, NULL, NULL, NULL, 'MMT&TT', NULL),
('NT305', 'Phát triển ứng dụng trên thiết bị di động', 'Mobile Application Development', 3, 2, 1, 'CN', NULL, 'NT118', NULL, NULL, 'MMT&TT', NULL),
('NT306', 'Kỹ thuật lập trình mạng trên Linux', 'Network programming techniques on Linux', 3, 3, 0, 'CN', NULL, NULL, NULL, NULL, 'MMT&TT', NULL),
('NT307', 'Xây dựng ứng dụng web', 'Web application development', 3, 2, 1, 'CN', NULL, 'NT208
NT533', NULL, NULL, 'MMT&TT', NULL),
('NT309', 'Lập trình trên Linux', 'Programming on Linux', 3, 2, 1, 'CN', NULL, NULL, NULL, NULL, 'MMT&TT', NULL),
('NT310', 'Pháp chứng mạng di động', 'Digital Forensics', 3, 2, 1, 'CNTC', NULL, NULL, NULL, 'NT101', 'MMT&TT', 'Môn học bao gồm các kỹ thuật pháp chứng di động cơ bản, cung cấp cái nhìn tổng
quan về các loại pháp chứng di động , kỹ thuật, các bằng chứng điện tử và cách thức
thu thập.
Khóa học cũng cung cấp cho sinh viên với một cách tiếp cận có hệ thống khi tiến
hành một điều tra pháp chứng di động (cả hai loại điều tra công quyền và điều tra
công ty), các yêu cầu của một phòng thí nghiệm pháp chứng di động bao gồm cả
thiết bị phục hồi dữ liệu, phần cứng và phần mềm cần thiết để xác nhận pháp chứng kỹ thuật số trong phòng thí nghiệm.
Thông qua lý thuyết và thực hành, sinh viên có khả năng thu thập chứng cứ tại điện
thoại di động cũng như tại nơi xảy ra sự cố bằng cách sử dụng các công cụ thu thập
dữ liệu.'),
('NT311', 'Công nghệ tường lửa và bảo vệ mạng ngoại vi', 'Firewall Technology and Perimeter Protection', 3, 2, 1, 'CNTC', NULL, NULL, NULL, 'NT101', 'MMT&TT', 'Các chủ đề chính của môn học bao gồm:
\- Tổng quan về cơ sở hạ tầng mạng an toàn
\- Nhu cầu bảo mật của doanh nghiệp
\- Công nghệ tường lửa
\- Mạng ngoại vi
\- Lọc gói tin và máy chủ Proxy
\- Các hệ thống chính và hệ thống giả lập'),
('NT312', 'Bảo mật với smartcard và NFC', 'Smart Card and NFC Security', 3, 2, 1, 'CNTC', NULL, NULL, NULL, 'NT101', 'MMT&TT', 'Môn học cung cấp cho sinh viên những kiến thức cơ bản về thẻ thông minh cũng
như kiến thức chuyên sâu về bảo mật thẻ thông minh. Nội dung môn học không chỉ tập trung vào các ứng dụng chủ yếu trong lĩnh vực truyền thông di động và ngân
hàng mà còn phân tích khả năng ứng dụng của thẻ thông minh và các công nghệ mới như NFC. Nội dung môn học đi sâu vào các vấn đề bảo mật với thẻ thông minh, các tiêu chuẩn thiết kế hình thức cho hệ thống thẻ thông minh an toàn, các khả năng và nguy cơ tấn công, các giải pháp bảo mật khi phát triển sản phẩm thương mại với thẻ thông minh. Ngoài các kiến thức lý thuyết, nội dung môn học còn trang bị cho sinh viên kỹ năng để phát triển ứng dụng thực tế với thẻ thông minh và những công nghệ mới như NFC.'),
('NT320', 'Công nghệ vệ tinh', 'Satelite technology', 3, 3, 0, 'CN', NULL, NULL, NULL, NULL, 'MMT&TT', NULL),
('NT321', 'Hệ thống tìm kiếm, phát hiện và ngăn ngừa xâm nhập', 'Intrusion Detection and Prevention System', 3, 2, 1, 'CN', NULL, 'NT204', NULL, NULL, 'MMT&TT', NULL),
('NT330', 'An toàn mạng không dây và di động', 'Wireless Network and Mobile Security', 3, 2, 1, 'CN', NULL, 'NT108', NULL, 'NT101', 'MMT&TT', 'Các khái niệm bảo mật cơ bản và các kiến thức cần thiết cho đánh giá các vấn đề an ninh. Các vấn đề an ninh và các giải pháp bảo mật công nghệ không dây và điện thoại di động như Bluetooth, WiFi, WiMax, 2G và 3G. Các kỹ thuật bảo mật được sử dụng để bảo vệ các ứng dụng tải về các thiết bị di động thông qua mạng điện thoại di động. Các vấn đề an ninh và giải pháp trong các công nghệ không dây và điện thoại di động như mạng cảm biến, di động 4G và mạng IMS.'),
('NT331', 'Xây dựng chuẩn chính sách an toàn thông tin trong doanh nghiệp', 'Building Enterprise Information Security Policies', 3, 2, 1, 'CN', NULL, NULL, NULL, 'IT005
NT101', 'MMT&TT', 'Luật pháp, điều tra và các vấn đề đạo đức nghề nghiệp. Chuẩn chính sách an toàn thông tin. Các kỹ thuật tăng cường bảo mật hệ thống cơ bản. Xác định các nguy cơ tiềm ẩn đối với hệ thống. Xây dựng chuẩn phù hợp quy mô, cơ sở hạ tầng và đặc thù doanh nghiệp.'),
('NT332', 'Xử lý tín hiệu trong truyền thông', 'Signal Processing in Communications', 4, 3, 1, 'CĐTN', NULL, NULL, NULL, 'IT005', 'MMT&TT', 'Môn học này cung cấp cho sinh viên những kiến thức về tín hiệu số và tín hiệu tuần tự, phổ của tín hiệu, chuyển đổi tín hiệu A/D và D/A. Sinh viên được học về các phép biến đổi trong xử lý các tín hiệu số như phép biến đổi Z, Fourier; thiết kế các bộ lọc số FIR, IIR; các kênh truyền thông và thiết kế các hệ thống truyền thông số. Môn học cũng trình bầy những vấn đề liên quan đến xử lý tín hiệu trong Viễn thông, Truyền thông di động và không dây. Sinh viên được học và thực hành với phần mềm Matlab.'),
('NT333', 'Tính toán lưới', 'Grid Computing', 3, 2, 1, 'CNTC', NULL, NULL, NULL, 'IT005
NT106', 'MMT&TT', 'Môn “Tính toán lưới” cung cấp cho sinh viên những kiến thức về tính toán lưới, kiến trúc và các thành phấn của hệ thống tính toán lưới; các chuẩn hỗ trợ tính toán lưới: OGSI, OGSA; phát triển hệ thống tính toán lưới, cơ sở về quản lý và phát triển lưới, an ninh lưới; hệ thống truyền thông lưới'),
('NT334', 'Pháp chứng kỹ thuật số', 'Digital Forensics', 3, 2, 1, 'CN', NULL, NULL, NULL, NULL, 'MMT&TT', 'Nội dung môn học bao gồm: Tổng quan về pháp chứng kỹ thuật số; Phương thức thu thập lưu lượng mạng và bằng chứng khác; Phương thức giải mã một Header TCP sử dụng công cụ Snort, sử dụng ứng dụng NetFlow, sử dụng công cụ SilentRunner NetWitness và AccessData điều tra pháp chứng số trên máy tính; Kết hợp pháp chứng số vào kế hoạch ứng phó sự cố; pháp chứng Internet, pháp chứng điện toán đám mây và mối quan hệ của chúng; kỹ năng điều tra pháp chứng mạng và kỹ năng pháp chứng mạng theo chu kỳ'),
('NT395', 'Phát triển ứng dụng trên thiết bị di động', 'Mobile application development', 3, 2, 1, 'CN', NULL, NULL, NULL, NULL, 'MMT&TT', NULL),
('NT400', 'An toàn mạng nâng cao', 'An toàn mạng nâng cao', 4, 4, 0, 'CHUA_PHAN_LOAI', NULL, NULL, NULL, NULL, 'Chưa rõ', NULL),
('NT401', 'An toàn mạng nâng cao', 'Advanced network security', 3, 2, 1, 'CN', NULL, NULL, NULL, NULL, 'MMT&TT', NULL),
('NT402', 'Công nghệ mạng viễn thông', 'Telecommunications Technology', 3, 2, 1, 'CN', NULL, 'NT404', NULL, 'NT105', 'MMT&TT', 'Môn học này dành cho sinh viên khoa Mạng máy tính và viễn thông, nội dung của môn học nhằm trang bị cho sinh viên những kiến thức về hạ tầng viễn thông sử dụng mạng thông tin di động (3G/4G/5G), mạng đồng trục cáp quang, và các hạ tầng mạng viễn thông tiên tiến khác hiện nay.'),
('NT403', 'Tính toán lưới', 'Net Computing', 3, 2, 1, 'CN', NULL, 'NT333', NULL, NULL, 'MMT&TT', NULL),
('NT404', 'Khóa luận tốt nghiệp', 'Thesis', 10, 10, 0, 'KLTN', NULL, NULL, NULL, NULL, 'MMT&TT', NULL),
('NT405', 'Bảo mật Internet', 'Internet Security', 3, 2, 1, 'CĐTN', NULL, NULL, NULL, 'NT101
IT005', 'MMT&TT', 'Các chủ đề chính của môn học bao gồm:
• Tổng quan về bảo mật Internet
• Động cơ của kẻ tấn công
• Sự thăm dò, quét mạng và liệt kê
• Tấn công hệ thống
• Các phần mềm gây hại (Virus, Worm, Trojan, Backdoor, Rootkit)
• Nghe lén
• Các kỹ thuật lừa đảo
• Tấn công từ chối dịch vụ
• Tường lửa và hệ thống phát hiện xâm nhập
• Các kỹ thuật kiểm thử hệ thống và biện pháp đối phó'),
('NT406', 'Đồ án tốt nghiệp', 'Graduation Project', 4, 4, 0, 'CĐTN', NULL, NULL, NULL, NULL, 'MMT&TT', NULL),
('NT407', 'Pháp chứng kỹ thuật số', 'Digital Forensics', 3, 2, 1, 'CN', NULL, 'NT334', NULL, NULL, 'MMT&TT', NULL),
('NT408', 'Bảo mật trên Internet', 'Cyber security', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'MMT&TT', NULL),
('NT501', 'Thực tập doanh nghiệp', 'Internship', 3, 3, 0, 'TTTN', NULL, 'NT115', NULL, NULL, 'MMT&TT', NULL),
('NT502', 'Thương mại Điện tử và Triển khai ứng dụng', 'E-commerce and applications', 3, 2, 1, 'CĐTN', NULL, 'NT210', NULL, 'IT005
NT101
NT109', 'MMT&TT', NULL),
('NT503', 'Bảo mật Internet', 'Cyber security', 3, 2, 1, 'CĐTN', NULL, 'NT405', NULL, 'IT005
NT101', 'MMT&TT', NULL),
('NT504', 'Tiểu luận tốt nghiệp', 'Graduation Project', 4, 4, 0, 'CĐTN', NULL, 'NT406', NULL, 'NT114
NT117', 'MMT&TT', NULL),
('NT505', 'Khóa luận tốt nghiệp', 'Thesis', 10, 10, 0, 'KLTN', NULL, NULL, NULL, NULL, 'MMT&TT', 'Khóa luận tốt nghiệp được xem là một công trình nghiên cứu khoa học dành cho sinh viên. Đề tài khóa luận tốt nghiệp là một đề tài được nghiên cứu và triển khai chuyên sâu cho thấy khả năng làm việc độc lập nhất định của sinh viên. Trong khóa luận, sinh viên nêu rõ những vấn đề do sinh viên thực hiện được dưới sự hướng dẫn của giảng viên như: ứng dụng, quy trình hoạt động, hệ thống triển khai. Ngoài ra khóa luận cần có những đánh giá, phương hướng phát triển tiếp theo của đề tài. Trong khóa luận nêu rõ kết quả thực hiện của sinh viên, đây là thành phần quan trọng nhất của khóa luận.
Đối với hệ tài năng:
Đây là môn học tài năng, sinh viên thực hiện dưới sự hướng dẫn của các giảng viên có trình độ Tiến sỹ trở lên với các đề tài nghiên cứu chuyên sâu. Đề tài khóa luận tốt nghiệp là một đề tài được nghiên cứu và triển khai chuyên sâu gắn với yêu cầu thực tế cho thấy khả năng nghiên cứu và làm việc độc lập của sinh viên. Khóa luận tốt nghiệp được xem là một công trình nghiên cứu khoa học dành cho những sinh viên trong chương trình đào tạo cử nhân tài năng chuyên ngành An ninh Thông tin.
Trong khóa luận tốt nghiệp, cần xác định rõ những vấn đề do sinh viên thực hiện được dưới sự hướng dẫn của giảng viên như: ứng dụng, quy trình hoạt động, hệ thống triển khai, tính mới của nghiên cứu. Ngoài ra khóa luận cần có những đánh giá, phương hướng phát triển tiếp theo của đề tài. Trong khóa luận cần nêu rõ kết quả nghiên cứu của sinh viên, đây là thành phần quan trọng nhất của khóa luận.'),
('NT506', 'Đồ án tốt nghiệp tại doanh nghiệp', 'Industry Capstone Project', 10, 10, 0, 'TN', NULL, NULL, NULL, NULL, 'MMT', NULL),
('NT507', 'Xây dựng ứng dụng web', 'Web application development', 3, 2, 1, 'CĐTN', NULL, 'NT208', NULL, NULL, 'MMT&TT', NULL),
('NT508', 'Đồ án tốt nghiệp', 'Capstone Project', 6, 6, 0, 'TN', NULL, NULL, NULL, NULL, 'MMT', NULL),
('NT509', 'Hệ thống đa tác tử di động thông minh', 'Intelligent mobile multi-agent systems', 3, 2, 1, 'CĐTN', NULL, NULL, NULL, NULL, 'MMT&TT', NULL),
('NT521', 'Lập trình an toàn và khai thác lỗ hổng phần mềm', 'Secure Programming and Exploiting Vulnerabilities', 4, 3, 1, 'CNTC', NULL, NULL, NULL, 'IT002
NT209', 'MMT&TT', NULL),
('NT522', 'Phương pháp học máy trong an toàn thông tin', 'Machine Learning for Information Security', 3, 2, 1, 'CNTC', NULL, NULL, NULL, NULL, 'MMT&TT', NULL),
('NT523', 'An toàn thông tin trong kỷ nguyên máy tính lượng tử', 'Cybersecurity in the Quantum Era', 3, 2, 1, 'CNTC', NULL, NULL, NULL, 'MA003', 'MMT&TT', NULL),
('NT524', 'Kiến trúc và bảo mật điện toán đám mây', 'Cloud Architecture and Security', 4, 3, 1, 'CN', NULL, NULL, NULL, 'IT005', 'MMT&TT', NULL),
('NT531', 'Đánh giá hiệu năng hệ thống mạng máy tính', 'Modeling and Performance Evaluation of Network and Computer Systems', 3, 2, 1, 'CN', NULL, NULL, NULL, 'NT132', 'MMT&TT', 'Môn học trình bày các mô hình đánh giá hiệu năng mạng, trình bày các đặc trưng của các kiểu kiến trúc mạng; các khái niệm và phương pháp liên quan đến đo hiệu năng mạng. Ngoài ra, các công cụ được sử dụng để đánh giá hiệu năng mạng cũng sẽ được giới thiệu. Cụ thể hơn, môn học giới thiệu những kỹ thuật mô hình hóa dựa trên phân tích giúp dự đoán hiệu suất của những hệ thống máy tính và mạng. Những kỹ thuật này cũng được dùng để xác nhận những tiêu chí thiết kế đã được đề ra trước đó. Những nội dung chính của môn học là: giới thiệu về ứng dụng của mô hình hóa hiệu suất; mô hình hóa phân tích và mô hình hóa mô phỏng; quá trình ngẫu nhiên; lý thuyết hàng cơ bản: ứng dụng vào hệ thống máy tính và mạng; phương pháp giải cho những mô hình phân tích về hiệu suất.'),
('NT532', 'Công nghệ Internet of things hiện đại', 'Advanced Internet of Things Technologies', 3, 2, 1, 'CNTC', NULL, NULL, NULL, NULL, 'MMT&TT', 'Trình bày các bộ giao thức mạng IoTs hiện đại như IEEE 802.15.4 WPAN/ZigBee, IEEE 802.15.1/Bluetooth, RF4CE/RFID, 6LoWPAN, uIP/uIPv6,…Môn học cũng trình bày các kiến trúc mạng hiện đại khác được sử dụng trong việc phát triển hạ tầng mạng, topo mạng IoTs hiện đại bao gồm Star, Tree, Clustering, Bus, Ring, Chain, Sweep, Tributaries-Delta, Mesh, Grid. Thông qua môn học, sinh viên nhận được những nền tảng cần thiết để có thể phát triển các ứng dụng và giải pháp IoTs thông minh nhằm phục vụ tốt hơn, tiện nghi hơn cuộc sống của con người (Ambient-Assisted Living).'),
('NT533', 'Hệ tính toán phân bố', 'Distributed Computing Systems', 3, 2, 1, 'CN', NULL, NULL, NULL, 'IT005', 'MMT&TT', 'Môn học giới thiệu hệ phân bố; các dịch vụ trên hệ phân bố như chia sẻ file; giới thiệu các hướng mới trong tính toán hiệu năng cao: cluster, Grid computing, cloud computing. Cụ thể hơn, môn học giới thiệu các khái niệm và kỹ thuật cơ bản về thiết kế và kỹ thuật của các hệ thống tính toán phân tán. Chủ đề kỹ thuật bao gồm trong khóa học này bao gồm thông tin liên lạc interprocess, gọi trình từ xa, hệ thống tập tin phân phối, kiểm soát đồng thời,... Các loại hệ thống được thảo luận trong môn học này bao gồm đám mây điện toán, điện toán lưới, hệ thống lưu trữ, mạng peer-to-peer và các dịch vụ Web.'),
('NT534', 'An toàn mạng máy tính nâng cao', 'Advanced Network Security', 3, 2, 1, 'CSN', NULL, NULL, NULL, 'NT101', 'MMT&TT', 'Trong khi môn học an toàn mạng máy tính đề cập các chủ đề căn bản của an toàn mạng. Môn học này đề cập đến các vấn đề chuyên sâu hơn ví dụ như là làm thế nào để phòng chống tấn công từ chối dịch vụ, các hoạt động ngầm trên Internet, bàn luận về các giải pháp kĩ thuật trong việc ngăn chặn cũng như đối phó với ngăn chặn trong việc quản lý truy cập trên Internet. Ngoài ra, môn này cũng đề cập các nguy cơ từ các loại mã độc tinh vi đối với an toàn mạng.
Đối với hệ tài năng:
Môn an toàn mạng đề cập các chủ đề căn bản của an toàn mạng. Môn này đề cập đến các vấn đề chuyên sâu hơn ví dụ như là làm thế nào để phòng chống tấn công từ chối dịch vụ, các hoạt động ngầm trên Internet, bàn luận về các giải pháp kĩ thuật trong việc ngăn chặn cũng như đối phó với ngăn chặn trong việc quản lý truy cập trên Internet. Ngoài ra, môn này cũng đề cập các nguy cơ từ các loại mã độc tinh vi đối với an toàn mạng. Cuối cùng, các kỹ thuật client side, server-side honeypot cũng được giới thiệu để nghiên cứu, thu thập mã độc.'),
('NT535', 'Bảo mật Internet of things', 'Security and Privacy in Internet of Things', 3, 2, 1, 'CNTC', NULL, NULL, NULL, 'NT101', 'MMT&TT', 'Phân tích tổng quan đặc điểm các thiết bị IoTs đầu cuối, các bộ giao thức mạng và kiến trúc mạng IoTs hiện đại. Trình bày các điểm yếu, lỗ hổng bảo mật, các phương thức tấn công thường gặp và các giải pháp bảo mật tương ứng. Các vấn đề pháp lý về tính riêng tư, và các giải pháp kỹ thuật tương ứng.'),
('NT536', 'Công nghệ truyền thông đa phương tiện', 'Multimedia Communication Technologies', 3, 2, 1, 'CN', NULL, NULL, NULL, NULL, 'MMT&TT', 'Môn học cung cấp kiến thức và kỹ năng về các công nghệ truyền thông đa phương tiện tiên tiến hiện nay cho các dữ liệu và tín hiệu như audio, video,…dùng trong các ứng dụng truyền thông phổ dụng hiện nay như IPTV, VoIP, digital multimedia.'),
('NT537', 'Truyền thông xã hội và kinh doanh', 'Social Media and Business Practices', 3, 3, 0, 'CN', NULL, NULL, NULL, NULL, 'MMT&TT', 'Trong môn học này, sinh viên sẽ được học về khái niệm truyền thông xã hội và ứng dụng của nó trong kinh doanh. Rất nhiều công ty và cá nhân đã và đang sử dụng truyền thông xã hôi như 1 công cụ chính hoặc trợ giúp cho việc kinh doanh bao gồm:
• Marketing, Customer Relationship Management & Customer Service (B2C & C2C)
• Managing Business Partners and Suppliers (B2B)
• Connected Company (Collaboration, engaging workforce)
• Crowdsourcing/Research & Product Development
Sinh viên sẽ được học cách dùng social media để đạt được mục tiêu kinh doanh. Lớp học sẽ thảo luận những case studies mà các công ty đang sử dụng bằng nhiều cách khác nhau để giúp đỡ cho công việc kinh doanh. Lớp học sẽ giới thiệu framework để quản lý dự án social media: khởi tạo dự án, hướng dự án để đạt được nhu cầu kinh doanh, đo lường kết quả kinh doanh xã hội và quản lý sự thay đổi. Sẽ có những lời khuyên, kĩ năng giúp học viên tránh những sai lầm và vượt qua khó khăn khi thực hiện dự án truyền thông xã hội.'),
('NT538', 'Giải thuật xử lý song song và phân bố', 'Algorithms in Parallel and Distributed Computing', 3, 2, 1, 'CN', NULL, NULL, NULL, 'IT005', 'MMT&TT', 'Trong môn học này, sinh viên sẽ được trang bị kiến thức nền tảng về các giải thuật song song và phân bố. Cụ thể hơn, môn học trình bày về kiến trúc hệ thống cũng như cơ chế vận hành của các giải thuật song song và phân bố phổ biến nhất hiện nay. Trong suốt quá trình học, các khái niệm này sẽ được mô tả thông qua các ví dụ và bài toán thực tế.'),
('NT539', 'AI ứng dụng trong mạng và truyền thông', 'Artificial Intelligence for Networking and Communications', 4, 3, 1, 'TN', NULL, NULL, NULL, 'IT005', 'MMT&TT', 'Môn học cung cấp những khái niệm tổng quan và kiến thức nền tảng về trí tuệ nhân tạo (AI). Môn học cũng trình bày chi tiết về ứng dụng của AI vào trong mạng máy tính và truyền thông thông qua các bài toán triển khai trong thực tế hiện nay. Trong suốt quá trình học, các kiến thức lý thuyết luôn đi song song với các bài thực hành dưới dạng các trường hợp thực tế (use-cases )'),
('NT540', 'Mạng không dây thế hệ mới', 'Next-Generation Wireless Networks', 3, 2, 1, 'CN', NULL, NULL, NULL, 'NT105', 'MMT&TT', NULL),
('NT541', 'Công nghệ mạng khả lập trình', 'Reprogramming Network Technologies', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'MMT&TT', NULL),
('NT542', 'Lập trình kịch bản tự động hóa cho quản trị và bảo mật mạng', 'Scripting for Automation in Administration and Security', 3, 2, 1, 'CN', NULL, NULL, NULL, 'NT132', 'MMT&TT', NULL),
('NT543', 'Tín hiệu và hệ thống thông tin', 'Signals and Communication Systems', 3, 2, 1, 'CN', NULL, NULL, NULL, 'NT105', 'MMT&TT', NULL),
('NT544', 'Ăng ten và truyền thông vô tuyến', 'Antenna and Telecommunications', 4, 3, 1, 'CN', NULL, NULL, NULL, 'NT105', 'MMT&TT', NULL),
('NT545', 'Thiết kế hệ thống viễn thông', 'Fundamental Techniques in Telecommunication System Design', 3, 2, 1, 'CN', NULL, NULL, NULL, 'NT402', 'MMT&TT', NULL),
('NT546', 'Thiết kế và triển khai mạng tốc độ cao', 'Design and Implementation of High-Speed Networks', 3, 2, 1, 'CN', NULL, NULL, NULL, 'NT113', 'MMT&TT', NULL),
('NT547', 'Blockchain: Nền tảng, ứng dụng và bảo mật', 'Blockchain: Fundamentals, Applications and Security', 3, 2, 1, 'CN', NULL, NULL, NULL, NULL, 'MMT&TT', NULL),
('NT548', 'Công nghệ DevOps và ứng dụng', 'DevOps Technology and Its Applications', 4, 3, 1, 'CĐTN', NULL, NULL, NULL, 'IT005', 'MMT&TT', NULL),
('NT549', 'Học máy tăng cường cho các hệ thống mạng', 'Reinforcement Learning for Networked Systems', 4, 3, 1, 'CN', NULL, NULL, NULL, 'IT005
IT002', 'MMT&TT', NULL),
('OOPT1', 'Lập trình hướng đối tượng', 'Object-Oriented Programming', 4, 3, 1, 'ĐC', NULL, 'IT002', NULL, NULL, 'CNPM', NULL),
('OOPT2', 'Lập trình hướng đối tượng', 'Object-oriented programming', 5, 4, 1, 'CSNN', NULL, NULL, NULL, NULL, 'CNPM', NULL),
('OSYS1', 'Hệ điều hành', 'Operating Systems', 3, 3, 0, 'ĐC', NULL, 'IT007', NULL, NULL, 'MMT&TT', NULL),
('OSYS2', 'Hệ điều hành', 'Operating systems', 3, 3, 0, 'ĐC', NULL, 'IT007', NULL, NULL, 'MMT&TT', NULL),
('OSYS3', 'Hệ điều hành', 'Hệ điều hành', 4, 4, 0, 'CHUA_PHAN_LOAI', NULL, NULL, NULL, NULL, 'Chưa rõ', NULL),
('PE001', 'Giáo dục thể chất 1', 'Physical education 1', 0, 0, 0, 'TC', NULL, 'PE012', NULL, NULL, 'PĐTĐH', 'Chương trình môn học giáo dục thể chất (GDTC) nhằm trang bị kiến thức, kỹ năng vận động cơ bản, hình thành thói quen tập luyện thể dục thể thao (TDTT) để nâng cao sức khỏe, phát triển thể lực, tầm vóc, hoàn thiện nhân cách, nâng cao khả năng họctập, kỹ năng hoạt động xã hội với tinh thần, thái độ tích cực cho sinh viên, góp phần thực hiện mục tiêu giáo dục toàn diện.'),
('PE002', 'Giáo dục thể chất 2', 'Physical education 2', 0, 0, 0, 'TC', NULL, 'PE012', NULL, NULL, 'PĐTĐH', 'Chương trình môn học giáo dục thể chất (GDTC) nhằm trang bị kiến thức, kỹ năng vận động cơ bản, hình thành thói quen tập luyện thể dục thể thao (TDTT) để nâng cao sức khỏe, phát triển thể lực, tầm vóc, hoàn thiện nhân cách, nâng cao khả năng họctập, kỹ năng hoạt động xã hội với tinh thần, thái độ tích cực cho sinh viên, góp phần thực hiện mục tiêu giáo dục toàn diện.'),
('PE003', 'Giáo dục thể chất 3', 'Physical education 3', 0, 0, 0, 'TC', NULL, NULL, NULL, NULL, 'TTNN', NULL),
('PE012', 'Giáo dục thể chất', 'Physical education', 0, 0, 0, 'TC', NULL, 'PE231
PE232', NULL, NULL, 'PĐTĐH', 'Chương trình môn học giáo dục thể chất (GDTC) nhằm trang bị kiến thức, kỹ năng vận động cơ bản, hình thành thói quen tập luyện thể dục thể thao (TDTT) để nâng cao sức khỏe, phát triển thể lực, tầm vóc, hoàn thiện nhân cách, nâng cao khả năng họctập, kỹ năng hoạt động xã hội với tinh thần, thái độ tích cực cho sinh viên, góp phần thực hiện mục tiêu giáo dục toàn diện.'),
('PE231', 'Giáo dục thể chất 1', 'Physical Education 1', 0, 0, 0, 'TC', NULL, NULL, NULL, NULL, 'PĐTĐH', NULL),
('PE232', 'Giáo dục thể chất 2', 'Physical Education 2', 0, 0, 0, 'TC', NULL, NULL, NULL, 'PE231', 'PĐTĐH', NULL),
('PEDU1', 'Giáo dục thể chất 1', 'Physical Education 1', 0, 0, 0, 'TC', NULL, 'PE001', NULL, NULL, 'GDTC', NULL),
('PEDU2', 'Giáo dục thể chất 2', 'Physical Education 2', 0, 0, 0, 'TC', NULL, 'PE002', NULL, NULL, 'PĐTĐH', NULL),
('PH001', 'Nhập môn điện tử', 'Introduction to Electrical Engineering', 3, 3, 0, 'ĐC', NULL, NULL, NULL, NULL, 'BMTL', 'Đây là môn hoc ̣ ở giai đoan ki ̣ ến thức đai cương. Môn học này trình bày các khái niệm và ̣phương pháp cơ bản về điện tử. Giới thiệu về nguyên lý hoạt động của của các linh kiện điện tử cơ bản (điện trở, tụ điện, nguồn điện, transistor,….). Ứng dụng các linh kiện điện tử này vào các mạch điện thực tế.'),
('PH002', 'Nhập môn mạch số', 'Introduction to Digital Circuits', 4, 3, 1, 'ĐC', NULL, 'CE102
NT102', NULL, NULL, 'KTMT', 'Môn học nhằm mục đích giới thiệu cho sinh viên các:
\- Hiểu kiến thức nền tảng về thiết kế các mạch số trong máy tính
\- Các hệ thống số cơ bản và sự chuyển đổi qua lại giữa các hệ thống số này
\- Đại số Boolean
\- Phương pháp bìa Karnaugh
\- Các phương pháp tối ưu mạch logic khác
\- Thiết kế và phân tích mạch tổ hợp
\- Thiết kế và phân tích mạch tuần tự
\- Thiết kế các bộ đếm'),
('PH003', 'Vật lý kỹ thuật', 'Technical physics', 4, 4, 0, 'ĐC', NULL, 'PHY02
PH002', NULL, NULL, 'BMTL', NULL),
('PHIL1', 'Những NLCB của chủ nghĩa Mác-Lênin', 'Fundamental Principles of Marxism-Leninism', 5, 5, 0, 'ĐC', NULL, 'SS001', NULL, NULL, 'BMAV', NULL),
('PHIL2', 'Triết học Mác-Lênin', 'Marxist-Leninist philosophy', 5, 5, 0, 'ĐC', NULL, NULL, NULL, NULL, 'P.ĐTĐH', NULL),
('PHY01', 'Vật lý đại cương A1', 'General physics A1', 3, 3, 0, 'ĐC', NULL, 'PH001', NULL, NULL, 'BMTL', NULL),
('PHY02', 'Vật lý đại cương A2', 'General physics A2', 3, 3, 0, 'ĐC', NULL, 'PH002', NULL, NULL, 'BMTL', NULL),
('PHY03', 'Vật lý đại cương A3', 'General physics A3', 2, 2, 0, 'ĐC', NULL, NULL, NULL, NULL, 'BMTL', NULL),
('PHY11', 'General Physics 1', 'General Physics 1', 4, 4, 0, 'ĐC', NULL, NULL, NULL, NULL, 'BMTL', NULL),
('PHY12', 'General Physics 2', 'General Physics 2', 4, 4, 0, 'ĐC', NULL, NULL, NULL, NULL, 'BMTL', NULL),
('PHY22', 'Vật lý đại cương A2 (TE1)', 'General physics A2', 4, 4, 0, 'ĐC', NULL, NULL, NULL, NULL, 'BMTL', NULL),
('PHYS1114', 'Vật lý đại cương 1', 'General Physics 1', 4, 4, 0, 'ĐC', NULL, 'PHYS1215', NULL, NULL, 'HTTT', 'Cung cấp cho sinh viên các kiến thức về các định luật cơ bản của cơ học cổ điển, các định luật bảo toàn, sóng cơ học và dao động, một số khái niệm về thuyết tương đối, các phương pháp phân tích giải quyết các vấn đề liên quan.'),
('PHYS1214', 'Vật lý đại cương 2', 'General Physics 2', 4, 4, 0, 'ĐC', NULL, 'PHYS1215', NULL, 'PHYS1114', 'HTTT', 'Cung cấp cho sinh viên các kiến thức về các định luật điện từ cơ bản, cách thức mô tả điện trường, từ trường cũng như tương tác của chúng với vật chất, phương pháp phân tích và giải quyết các bài toán liên quan. Ba định luật nhiệt động
Nội dung: Điện trường tĩnh. Vật cách ly. Vật dẫn và tụ điện. Từ trường. Cảm ứng điện từ. Vật liệu từ. Dao động và sóng điện từ. Điện từ trường.'),
('PHYS1215', 'Vật lý đại cương', 'General Physics', 3, 3, 0, 'ĐC', NULL, NULL, NULL, NULL, 'HTTT', NULL),
('QTE111', 'Văn hóa giao tiếp', 'Intercultural Communication', 2, 2, 0, 'CN', NULL, NULL, NULL, NULL, 'PĐTĐH', NULL),
('SC203', 'Phương pháp nghiên cứu khoa học', 'Scientific Research Methodology', 3, 3, 0, 'CNTC', NULL, NULL, NULL, NULL, 'HTTT', NULL),
('SE005', 'Giới thiệu ngành Kỹ thuật Phần mềm', 'Introduction to Software Engineering', 1, 1, 0, 'ĐC', NULL, 'CS005
IE005
IS005
CE005
NT005
NT015
EC005
DS005
AI001', NULL, NULL, 'CNPM', 'Môn Giới thiệu ngành Công Nghệ Phần Mềm (CNPM) được thiết kế để giúp sinh
viên năm thứ nhất tiếp cận các kiến thức liên quan định hướng, kỹ năng nghề
nghiệp của cử nhân/kỹ sư CNPM. Cụ thể là:
\- Giới thiệu những thách thức hiện tại đối với kỹ sư/cử nhân CNPM.
\- Vai trò của CNPM trong tổng thể nhóm ngành CNTT và trong nền kinh tế tri
thức.
\- Lược sử các xu hướng chính và các xu hướng tương lai của ngành CNPM.
\- Định nghĩa và đặc điểm của sản phẩm phần mềm.
\- Các khối kiến thức tổng quan về CNPM và phương pháp giải quyết vấn đề
trong lĩnh vực phần mềm.
\- Thực hành các kỹ năng làm việc nhóm, viết báo cáo và thuyết trình.'),
('SE100', 'Phương pháp phát triển phần mềm hướng đối tượng', 'Object-Oriented Software Development Methodology', 4, 3, 1, 'CSN', NULL, NULL, NULL, 'SE104
IT008', 'CNPM', 'Môn học này trình bày về phân tích và thiết kế hệ thống phần mềm theo hướng đối tượng. Nội dung môn học trình từ cơ bản tới chuyên sâu các thao tác trong quá trình phát triển phần mềm. Chương 1 trình bày sơ lược về quy trình phát triển phần mềm. Chương 2 trình bày về các khái niệm cơ bản về hướng đối tượng. Chương 3 và 4 trình bày về mô hình hóa các yêu cầu. Chương 5 trình bày về phân tích phần mềm hướng đối tượng. Chương 6 trình bày về thiết kế phần mềm hướng đối tượng. Chương 7 thảo luận một số vấn đề khác trong phát triển phần mềm hướng đối tượng.'),
('SE101', 'Phương pháp mô hình hóa', 'Modeling Methods', 3, 3, 0, 'CSN', NULL, NULL, NULL, 'IT001
IT002
IT003', 'CNPM', 'Trình bày các kiến trúc, nền tảng về các phương pháp mô hình hóa thông tin, tri thức, biểu diễn vấn đề và lời giải, mô hình hóa hệ thống. Sinh viên tiếp cận với các các phương pháp mô hình hóa và biểu diễn vấn đề như mô hình hóa và biểu diễn dữ liệu, mô hình hóa và biểu diễn quan hệ, mô hình hóa và biểu diễn tiến trình, mô hình hóa và biểu diễn tri thức như phương pháp SDLC, JSD, SSM, OOA...Sinh viên làm quen với các công cụ đùn biểu diễn mô hình như công cụ CASE (upper và lower), các ngôn ngữ mô phỏng mô hình hóa như ngôn ngữ UML, VRML..nhằm hiện thực hóa một hệ thống. Học phần là sự kết hợp giữa các bài giảng, thuyết trình, bài tập nhỏ, tự nghiên cứu tài liệu và báo cáo đồ án kết thúc môn học. Học phần được chia làm 2 phần: phần 1 dẫn nhập và giới thiệu những khái niệm về các mô hình đặc trung hiện nay, phần 2 là giới thiệu về phương pháp luận dùng cho mô hình hóa, và phần 3 giới thiệu cụ thể về các mô hình biểu diễn thông tin, dữ liệu, thời gian thực.'),
('SE102', 'Nhập môn phát triển game', 'Introduction to Game Development', 3, 2, 1, 'CSN', NULL, NULL, NULL, 'IT001', 'CNPM', 'Môn học giới thiệu cho Sinh viên những khái niệm, thông tin cơ bản trong ngành game và đi sâu vào kỹ thuật lập trình DirectX để xây dựng các game 2D đơn giản như Tetris, Battle City, Mario, Contras... Chương 1 giới thiệu tổng quan về ngành game. Chương 2 giới thiệu về kỹ thuật lập trình Windows dùng C++ và Windows SDK. Chương 3 giới thiệu kỹ thuật làm chuyển động và kỹ thuật lập trình DirectX cơ bản. Chương 4 cung cấp kỹ thuật làm việc với Sprite và xử lý thiết bị nhập. Chương 5 thảo luận về các kỹ thuật hỗ trợ khác như phép biến đổi, lập trình DirectSound, hiển thị chữ ... Chương 6 bàn luận về Game Engine và cách xây dựng một game engine đơn giản.'),
('SE103', 'Các phương pháp lập trình', 'Programming Paradigms', 3, 2, 1, 'CN', NULL, 'SE334', NULL, NULL, 'CNPM', NULL),
('SE105', 'Lập trình nhúng căn bản', 'Embedded-system Programming', 3, 2, 1, 'CN', NULL, 'CE211', NULL, NULL, 'CNPM', NULL),
('SE106', 'Đặc tả hình thức', 'Formal Specification', 4, 4, 0, 'CSN', NULL, NULL, NULL, 'IT001
IT003
SE104
IT002', 'CNPM', 'Trình bày các kiến trúc, nền tảng về đặc tả hình thức, là một trong các cách tiếp cận xây dựng môn học. Thông qua các ngôn ngữ đặc tả hình thức là ngôn ngữ VDM và ngôn ngữ Z, sinh viên có thể dễ dàng nắm bắt được quy trình và các phương pháp hệ thống riêng biệt từ đặc tả, thiết kế đến thực hiện chương trình. Học phần là sự kết hợp giữa các bài giảng, thuyết trình, bài tập nhỏ, tự nghiên cứu tài liệu và kiểm tra cuối kỳ. Học phần được chia làm 2 phần: phần 1 dẫn nhập và giới thiệu những khái niệm cơ sở của đặc tả hình thức được minh họa bằng ngôn ngữ VDM, phần 2 là giới thiệu về ngôn ngữ đặc tả Z.'),
('SE107', 'Phân tích thiết kế hệ thống', 'System analysis and design', 4, 3, 1, 'CN', NULL, 'IS201', NULL, 'IT002
IT004
SE104', 'CNPM', NULL),
('SE108', 'Kiểm chứng phần mềm', 'Software testing', 3, 2, 1, 'CSN', NULL, 'SE113', NULL, 'IT001
SE104', 'CNPM', NULL),
('SE109', 'Phát triển, vận hành, bảo trì phần mềm', 'Software Development, Deployment and Maintenance', 3, 3, 0, 'CSN', NULL, NULL, NULL, 'SE104', 'CNPM', 'Môn học cung cấp cho sinh viên những kiến thức để giải quyết các vấn đề phát sinh trong quá trình bảo trì, thay đổi phần mềm đặc biệt là các dự án lớn, sao cho việc quản lý, thực thi quá trình bảo trì nâng cấp phần mềm được hiệu quả. Môn học cung cấp các khái niệm cơ bản về bảo trì, nâng cấp phần mềm. Các lý thuyết cơ bản cho các kỹ năng cần thiết để quản lý hiệu quả những thay đổi nhằm mục đích nâng cấp phần mềm theo những thay đổi của yêu cầu thực tế.'),
('SE110', 'Phương pháp Phát triển phần mềm hướng đối tượng', 'Phương pháp Phát triển phần mềm hướng đối tượng', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'CNPM', NULL),
('SE111', 'Đồ án mã nguồn mở', 'Open Source Project', 2, 2, 0, 'CSN', NULL, 'SE121', NULL, 'IT001
IT002
IT003
IT004
SE104', 'CNPM', 'Đồ án môn học mã nguồn mở nhằm giúp sinh viên:
\- Vận dụng lại các kiến thức đã được học trong nhà trường và tìm hiểu qua các tài liệu báo chí, sách, đài, tivi...như kiến thức về nhập môn công nghệ phần mềm, kiến thức về lập trình, kiến thức về tổ chức dữ liệu, kiến thức về ngôn ngữ và các phương pháp lập trình...nhằm ứng dụng cụ thể vào đồ án môn học mã nguồn mở.
\- Nghiên cứu các thuật toán/các công nghệ/ngôn ngữ lập trình/các ứng dụng được sử dụng rộng rãi trong cộng đồng mã nguồn mở phục vụ cho đồ án môn học mã nguồn mở.
\- Nghiên cứu các quy định, luật chơi được sử dụng khi xây dựng phần mềm mã nguồn mở và tham gia vào cộng đỗng mã nguồn mở.
Nghiên cứu, tìm hiểu, xây dựng và triển khai phần mềm mã nguồn mở được ứng dụng thực tế cho đồ án môn học mã nguồn mở.'),
('SE112', 'Đồ án chuyên ngành', 'Specialized Project', 2, 2, 0, 'CSN', NULL, 'SE122', NULL, 'IT002
IT003
IT004
SE104', 'CNPM', 'Đồ án môn học nhằm giúp sinh viên:
§ Vận dụng lại các kiến thức đã được học trong nhà trường và tìm hiểu qua các tài liệu báo chí, sách, đài, tivi...như kiến thức về nhập môn công nghệ phần mềm, kiến thức về lập trình, kiến thức về tổ chức dữ liệu, kiến thức về ngôn ngữ và các phương pháp lập trình...nhằm ứng dụng cụ thể vào đồ án môn học chuyên ngành.
§ Nghiên cứu các thuật toán, các công nghệ và ngôn ngữ lập trình mới (nếu cần thiết) phục vụ cho đồ án môn học chuyên ngành.
§ Ứng dụng quy trình và các phương pháp luận xây dựng và triển khai phần mềm ứng dụng thực tế cho đồ án môn học chuyên ngành.'),
('SE113', 'Kiểm chứng phần mềm', 'Software Testing', 4, 3, 1, 'CNTC', NULL, NULL, NULL, 'SE104', 'CNPM', 'Môn học này trình bày về các kiến thức cơ bản về kiểm chức phần mềm và các kỹ thuật liên quan; và là học phần bắt buộc cho sinh viên công nghệ thông tin trong một học kỳ. Học phần được phân làm 4 phần: phần 1 là các khái niệm liên quan tới kiểm chứng phần mềm; phần 2 là các kĩ thuật kiểm chứng phần mềm; phần 3 là các chiến lược kiểm chứng phần mềm; phần 4 là các vấn đề nâng cao.'),
('SE114', 'Nhập môn ứng dụng di động', 'Mobile Application Overview', 3, 2, 1, 'CSN', NULL, NULL, NULL, 'IT001
IT002', 'CNPM', 'Cung cấp cho sinh viên những kiến thức cơ bản về hệ thống nhúng, phần mềm nhúng, công cụ và môi trường phát triển ứng dụng trên các hệ thống nhúng, mạch số. Mục tiêu của môn học là giúp sinh viên tiếp cận việc thiết kế phần mềm cho các ứng dụng nhúng với một bộ vi xử lý đơn lẻ dựa trên các bộ vi điều khiển chuẩn nhỏ. Nâng cao kỹ năng thực thi các thiết kế ứng dụng nhúng sử dụng ngôn ngữ lập trình cấp cao.'),
('SE115', 'Phát triển game với Unity', 'Game Development with Unity', 3, 2, 1, 'CSN', NULL, NULL, NULL, 'IT001
IT002', 'CNPM', NULL),
('SE116', 'Phát triển kỹ năng lập trình game ứng dụng trong thực tế', 'Skill Development in Game Programming for Real Projects', 4, 3, 1, 'CSN', NULL, NULL, NULL, NULL, 'CNPM', NULL),
('SE117', 'Kỹ thuật lập trình', 'Programming Techniques', 4, 3, 1, 'CN', NULL, NULL, NULL, 'IT001', 'CNPM', NULL),
('SE121', 'Đồ án 1', 'Project 1', 2, 2, 0, 'CSN', NULL, NULL, NULL, 'SE104', 'CNPM', NULL),
('SE122', 'Đồ án 2', 'Project 2', 2, 2, 0, 'CSN', NULL, NULL, NULL, 'SE121', 'CNPM', NULL),
('SE207', 'Phân tích thiết kế hệ thống', 'System Analysis and Design', 4, 3, 1, 'CN', NULL, 'SE107', NULL, NULL, 'CNPM', NULL),
('SE208', 'Kiểm chứng phần mềm', 'Software Testing', 3, 2, 1, 'CN', NULL, 'SE108', NULL, NULL, 'CNPM', NULL),
('SE209', 'Phát triển, vận hành, bảo trì phần mềm', 'Software Development, Deployment and Maintenance', 3, 3, 0, 'CN', NULL, 'SE109', NULL, NULL, 'CNPM', NULL),
('SE210', 'Quản lý dự án công nghệ thông tin', 'Information-Technology Project Management', 4, 3, 1, 'CN', NULL, 'IS208', NULL, NULL, 'CNPM', NULL),
('SE211', 'Phát triển phần mềm hướng đối tượng', 'Object-oriented Software Development Methodology', 4, 3, 1, 'CN', NULL, 'SE100', NULL, NULL, 'CNPM', NULL),
('SE212', 'Phát triển phần mềm mã nguồn mở', 'Open-source Software Development', 3, 2, 1, 'CN', NULL, 'SE331', NULL, NULL, 'CNPM', NULL),
('SE213', 'Xử lý phân bố', 'Distributed Computing', 3, 2, 1, 'CN', NULL, 'SE339', NULL, NULL, 'CNPM', NULL),
('SE214', 'Công nghệ phần mềm chuyên sâu', 'Advanced Software Engineering', 4, 3, 1, 'CN', NULL, NULL, NULL, 'SE104
IT001
IT003', 'CNPM', 'Học phần này trình bày các kiến thức chuyên sâu về các phương pháp, qui trình phát triển phần mềm mới, tiên tiến như RUP, Agile, XP, Scrum. Trang bị các kiến thức chuyên sâu về đặc tả và cộng nghệ yêu cầu, cũng như các kiến thức liên quan đến quản lý và triển khai dựa án phần mềm. Môn học giúp sinh viên nắm vững và có khả năng áp dụng các qui trình tiên tiến trong công nghệ phần mềm, có khả năng thiết lập. quản lý, triển khai một dự án phần mềm một cách chuyên nghiệp.'),
('SE215', 'Giao tiếp người máy', 'Human-Computer Interaction', 4, 3, 1, 'CN', NULL, NULL, NULL, 'SE104', 'CNPM', 'Môn học cung cấp cho sinh viên các kiến thức, nguyên lý thiết kế tương tác, các phương pháp làm nguyên mẫu, đánh giá chất lượng giao diện, các nguyên tắc thiết kế nhận thức. Chương 1 giới thiệu các kiến thức tổng quan. Chương 2 đi vào phân tích vai trò, cách thức tương tác. Chương 3 giới thiệu một số quy trình. Chương 4 nói về cách thiết kế tập trung vào vai trò người dùng. Chương 5 là các mẫu thiết kế.'),
('SE220', 'Thiết kế game', 'Game Design', 3, 3, 0, 'CN', NULL, NULL, NULL, NULL, 'CNPM', 'Môn học giới thiệu cho Sinh viên những kiến thức, kỹ năng cơ bản nhất trong lĩnh vực thiết kế game. Chương 1 cung cấp lý thuyết nền tảng về tâm lý con người, bản chất của game là gì, tại sao game hấp dẫn, diễn biến tâm lý người chơi khi chơi game. Chương 2 cung cấp các gợi mở về kỹ thuật thiết kế game, các bài học lịch sử trong thiết kế game, các tiêu chí thiết kế. Chương 3 tập trung vào thiết kế giao diện game như cách xây dựng menu, bố trí các thành phần giao diện, biểu tượng, thiết kế HUD. Chương 4 bàn về thiết kế cảnh chơi như cách đặt thử thách, xây dựng bối cảnh, tạo hồn cho cảnh chơi...'),
('SE221', 'Lập trình game nâng cao', 'Advanced Game Programming', 4, 3, 1, 'CN', NULL, NULL, NULL, 'SE102', 'CNPM', 'Đây là môn học chuyên ngành nhằm trang bị cho sinh viên những kiến thức và kỹ
năng sau:
• Môn học cung cấp cho sinh viên những kiến thức cơ bản về lập trình game
chơi qua mạng như các kiến trúc game peer-to-peer, client/server, cách thức
xử lý các vấn đề phát sinh trong môi trường mạng như lag, lost package.
• Kết thúc khóa học, sinh viên sẽ có khả năng tự xây dựng những game có sự
tương tác giữa nhiều người chơi trong môi trường mạng.'),
('SE301', 'Phát triển phần mềm mã nguồn mở', 'Open Source Software Development', 3, 3, 0, 'CN', NULL, NULL, NULL, 'IT003
IT004
IT002
SE104
SE109
IT001', 'CNPM', 'Môn học giới thiệu tổng quan về sự phát triển của phần mềm mã nguồn mở, các khái niệm liên quan về bản quyền trong các phần mềm mã nguồn mở. Môn học cũng giới thiệu các phương pháp xây dựng phần mềm mã nguồn mở, ứng dụng SVN để xây dựng phần mềm mã nguồn mở.'),
('SE310', 'Công nghệ .NET', '.NET Technology', 4, 3, 1, 'CNTC', NULL, NULL, NULL, 'IT008', 'CNPM', 'Học phần này trình bày các kiến trúc, nền tảng về công nghệ .Net, các kỹ năng và phương pháp lập trình hướng đối tượng trong .Net. Ứng dụng tích hợp việc sử dụng công nghệ (C#) và hệ quản trị CSDL trong việc xây dựng một hệ thống quản lý. Ngoài ra học phần còn cung cấp cho sinh viên các hướng tiếp cận chuyên sâu trong xây dựng các ứng dụng bằng công nghệ .Net.'),
('SE311', 'Ngôn ngữ lập trình Java', 'Java Programming Language', 4, 3, 1, 'CN', NULL, 'SE330', NULL, NULL, 'CNPM', NULL),
('SE312', 'Công nghệ .NET', '.Net Technology', 4, 3, 1, 'CN', NULL, 'SE310', NULL, 'IT004
SE104
SE347', 'CNPM', NULL),
('SE313', 'Một số thuật toán thông minh', 'Intelligent Algorithms', 2, 2, 0, 'CNTC', NULL, NULL, NULL, 'IT002
IT003
IT001', 'CNPM', 'Môn học trình bày cho sinh viến các kiến thức về thuật toán, và đưa ra các kiến thức về một số thuật toán thông minh hiện nay để giải một số bài toán cơ bản.'),
('SE314', 'Công nghệ game 3D', '3D Game Technology', 3, 2, 1, 'CN', NULL, NULL, NULL, NULL, 'CNPM', NULL),
('SE315', 'Công nghệ game online', 'Technology for Online Game Development', 4, 3, 1, 'CN', NULL, NULL, NULL, 'IT002
IT004', 'CNPM', NULL),
('SE316', 'Phát triển game đa nền tảng', 'Cross-Platform Game Development', 3, 2, 1, 'CN', NULL, NULL, NULL, 'SE115', 'CNPM', NULL),
('SE317', 'Công nghệ tiên tiến trong phát triển game', 'Advanced Technologies in Game Development', 3, 2, 1, 'CN', NULL, NULL, NULL, NULL, 'CNPM', NULL),
('SE320', 'Lập trình đồ họa 3 chiều với Direct3D', '3D Programming with Direct3D', 4, 3, 1, 'CNTC', NULL, NULL, NULL, 'SE102', 'CNPM', 'Môn học trình bày các kiến thức nền tảng về lập trình ứng dụng đồ họa 3 chiều và hướng dẫn sử dụng bộ thư viện đồ họa tiêu chuẩn của Microsoft là DirectX để xây dựng ứng dụng. Chương trình tổng quan bao gồm 4 chương trong đó: chương 1 trình bày về cơ sở toán học ứng dụng trong đồ họa 3 chiều và quy trình dựng hình 3 chiều, chương 2 và 3 sẽ trình bày về Direct3D bao gồm các vấn đề đi từ cơ bản đến nâng cao, chương 4 sẽ hướng dẫn sinh viên ứng dụng các kiến thức đã học vào xây dựng trò chơi Tetris 3D. Kết thúc khóa học, sinh viên sẽ có khả năng tự thiết kế và lập trình ứng dụng đồ họa 3 chiều đơn giản trên môi trường Windows.'),
('SE321', 'Lập trình trên thiết bị di động', 'Mobile Device Application Development', 4, 4, 0, 'CN', NULL, 'SE346', NULL, NULL, 'CNPM', NULL),
('SE322', 'Công nghệ Web và ứng dụng', 'Web Technology and Applications', 2, 2, 0, 'CN', NULL, 'SE341', NULL, NULL, 'CNPM', NULL),
('SE323', 'Thiết kế Game', 'Introduction to game design', 4, 4, 0, 'CN', NULL, 'SE220', NULL, NULL, 'CNPM', NULL),
('SE324', 'Nhập môn lập trình 3D game', '3D game engine design', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'CNPM', NULL),
('SE325', 'Chuyên đề J2EE', 'J2EE', 4, 3, 1, 'CNTC', NULL, NULL, NULL, 'IT002
IT003
IT004', 'CNPM', 'Môn học giới thiệu cáckiến thức cơ bản thành phần của J2EE, lập trình web với servlet và JSP, Kiến trúc MVC với Struts, Spring. Sinh viên có thể dùng các kiến thức đã học để có thể phân tích, thiết kế một hệ thống J2EE hoàn chỉnh'),
('SE326', 'Cơ sở dữ liệu nâng cao', 'Advanced Databases', 2, 2, 0, 'CN', NULL, 'IS337', NULL, NULL, 'CNPM', NULL),
('SE327', 'Phát triển và vận hành game', 'Game Development and Operations', 3, 3, 0, 'CNTC', NULL, NULL, NULL, 'SE102', 'CNPM', 'Môn học cung cấp cho sinh viên những kiến thức về quy trình phát triển và vận
hành một game online theo các thể loại khác nhau từ casual, action, SLG cho đến
MMORPG độc lập hoặc trên nền mạng xã hội.'),
('SE328', 'Lập trình trí tuệ nhân tạo trong game', 'Artificial Intelligent in Game Development', 4, 3, 1, 'CNTC', NULL, NULL, NULL, 'IT002
IT003
SE102', 'CNPM', 'Việc tạo ra trí tuệ nhân tạo thiết thực là một trong những thử thách lớn nhất trong lập trình game, việc thành công của những game thương mại ngày nay phụ thuộc rất nhiều vào chất lượng của AI. Môn học này trình bày về những kỹ thuật xây dựng những sinh vật nhân tạo có khả năng chuyển vùng đặc biệt, tạo các quyết định chiến thuật dựa trên hành vi đã học được theo các hướng tiếp cận chuyên sâu bắt đầu bằng những thuật toán thường được sử dụng bao gồm thuật toán tìm đường A\*, suy luận dựa trên luật hay cây quyết định, hệ thống đối thoại, biểu diễn tri thức. Bên cạnh đó môn học còn giới thiệu về ngôn ngữ lập trình Python, quy trình phát triển toàn diện từ bắt đầu đến kết thúc để hiện thực AI trong game.'),
('SE329', 'Thiết kế 3D Game Engine', '3D Game Engine Design', 4, 3, 1, 'CNTC', NULL, NULL, NULL, 'SE102', 'CNPM', 'Học phần này trình bày kiến trúc của 3D Game Engine, các thuật toán cho đồ họa 3D. Từ đó sinh viên có thể tự thiết kế và xây dựng một 3D Engine phục vụ cho các game 3D tương đối phức tạp. Học phần là sự kết hợp giữa các bài giảng, thuyết trình, bài tập nhỏ, tự nghiên cứu tài liệu và báo cáo đồ án kết thúc môn học. Học phần được chia làm 3 phần: phần 1 giới thiệu về kiến trúc của 3D Game Engine, phần 2 là giới thiệu về các thuật toán cho đồ họa 3D, phần 3 làcách thức thiết kế và xây dựng một 3D Game Engine.'),
('SE330', 'Ngôn ngữ lập trình Java', 'Java Programming Language', 4, 3, 1, 'CSN', NULL, NULL, NULL, 'IT001
IT002', 'CNPM', 'Môn học cung cấp các kiến thức cơ bản ngôn ngữ Java, lập trình giao diện với AWT - Abstract Window Toolkit, lập trình đa luồng - Multithreading, lập trình cở sở dữ liệu. Môn học cũng cấp các kiến thức giúp sinh viên làm quen với các công cụ sử dụng trong ngôn ngữ lập trình Java.'),
('SE331', 'Chuyên đề E-commerce', 'E-Commerce', 2, 2, 0, 'CNTC', NULL, NULL, NULL, 'IT004
SE104', 'CNPM', 'Học phần này trình bày các thức tổng quan về thương mại điện tử, các xu thế phát triển thương mại điện tử hiện tại và trong tương lai, các lĩnh vực ngành nghề phù hợp đặc biệt đối với việc áp dụng thương mại điện tử và giá trị của thương mại điện tử mang lại cho sự phát triển kinh tế, xã hội. Tiếp theo, học phần này sẽ cung cấp các kiến thức về các mô hình thương mại điện tử phù hợp theo từng đối tượng tương tác, các phương thức thanh toán phổ biến được sử dụng trong thương mại điện tử hiện tại và các dịch vụ hỗ trợ thanh toán hiện có trên thị trường và đặc biệt là vấn đề bảo mật trong các giao dịch thương mại điện tử. Tiếp theo, phần trọng tâm của môn học là giới thiệu các công nghệ, kỹ thuật và quy trình phát triển một website thương mại điện tử và các kiến thức, kỹ năng liên quan đến vận hành website thương mại điện tử.'),
('SE332', 'Chuyên đề cơ sở dữ liệu nâng cao', 'Advanced Database', 2, 2, 0, 'CN', NULL, NULL, NULL, 'IT004', 'CNPM', 'Môn học cung cấp cho sinh viên những kiến thức bổ sung về cơ sở dữ liệu bao gồm quy trình xây dựng một cơ sở dữ liệu thực tiễn, việc lưu giữ cơ sở dữ liệu trên bộ nhớ ngoài, việc thực hiện và tối ưu các truy vấn, kiểm tra cạnh tranh'),
('SE333', 'Chuyên đề E-Government', 'E-Government', 2, 2, 0, 'CN', NULL, 'SE404', NULL, NULL, 'CNPM', NULL),
('SE334', 'Các phương pháp lập trình', 'Programming Paradigms', 3, 2, 1, 'CN', NULL, NULL, NULL, 'IT001', 'CNPM', 'Học phần này trình bày các kiến trúc, nền tảng về các phương pháp, kỹ thuật lập trình thường dùng khi thiết kế và xây dựng một chương trình máy tính. Sinh viên được tiếp cận với các các phương pháp, kỹ thuật lập trình như: kỹ thuật lập trình đệ qui, kỹ thuật tối ưu mã chương trình, phương pháp lập trình cấu trúc, lập trình hướng đối tượng, lập trình đa nhiệm, song song. Sinh viên được làm quen với các ngôn ngữ lập trình trong các ví dụ minh họa như: ngôn ngữ C++, Java, các thư viện hỗ trợ trong lập trình song song. Học phần cung cấp các kiến thức cơ bản về cách đặt tên biến, hàm, lớp... trong lập trình cũng như kỹ thuật thiết kế kiến trúc và giao diện chương trình. Học phần là sự kết hợp giữa các bài giảng, thuyết trình, tự nghiên cứu tài liệu và báo cáo đồ án kết thúc môn học. Học phần được chia làm 3 phần: phần 1 giới thiệu các kỹ thuật và các nguyên lý cơ bản của lập trình, phần 2 là giới thiệu cụ thể về các phương pháp và kỹ thuật lập trình như: lập trình đệ qui, lập trình cấu trúc, lập trình hướng đối tượng và lập trình song song, phần 3 giới thiệu kỹ thuật thiết kế kiến trúc và giao diện chương trình'),
('SE335', 'Công nghệ XML và ứng dụng', 'XML technology and applications', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'CNPM', NULL),
('SE336', 'Phương pháp luận sáng tạo KH-CN', 'Scientific methodology and innovation', 2, 2, 0, 'CN', NULL, 'CS519', NULL, NULL, 'CNPM', NULL),
('SE337', 'Các thuật toán thông minh', 'Intelligent Algorithms', 1, 1, 0, 'CN', NULL, 'SE313', NULL, NULL, 'CNPM', NULL),
('SE338', 'Logic mờ', 'Fuzzy logic', 2, 2, 0, 'CN', NULL, 'CS405', NULL, NULL, 'CNPM', NULL),
('SE339', 'Xử lý phân bổ', 'Distributed computing', 3, 2, 1, 'CN', NULL, NULL, NULL, NULL, 'CNPM', NULL),
('SE340', 'Quản lý dự án công nghệ thông tin', 'Information-technology project management', 4, 3, 1, 'CN', NULL, 'IS208', NULL, 'IT001', 'CNPM', 'Môn học này trình bày kiến trúc về quản lý dự án nói chung và dự án công nghệ thông tin nói riêng và là học phần chuyên ngành cho sinh viên công nghệ thông tin trong một học kỳ giúp sinh viên trang bị kỹ năng triển khai hoạch định và tổ chức công việc của người quản trị dự án so với yêu cầu quản trị kỹ thuật. Chương 1 trình bày về tổng quan về quản lý dự án khung làm việc của quản trị dự án, những định hướng phát triển hiện tại và tương lai. Chương 2 giới thiệu kiến thức cơ bản về quản trị phạm vi dự án, sơ lược các phương pháp chọn lựa dự án và mô tả tài liệu dự án trong giai đoạn khởi đầu. Chương 3 trình bày về quản trị thời gian, các kỹ thuật triển khai lập kế hoạch ước lượng thực hiện dự án. Chương 4 trình bày về chi phí dự án, kỹ thuật ước lượng và phân bổ ngân sách. Chương 5 và các chương còn lại trình bày kiến thức và bước hỗ trợ nâng cao kiến thức tổ chức nhân sự, chất lượng, rủi ro, mua sắm, tích hợp dự án.'),
('SE341', 'Công nghệ Web và ứng dụng', 'Web technology and applications', 2, 2, 0, 'CN', NULL, NULL, NULL, 'IT001
IT002
IT004', 'CNPM', NULL),
('SE342', 'Logic mờ', 'Fuzzy logic', 2, 2, 0, 'CN', NULL, NULL, NULL, NULL, 'CNPM', NULL),
('SE343', 'Công nghệ Portal', 'Portal Technology', 3, 3, 0, 'CNTC', NULL, NULL, NULL, 'SE347', 'CNPM', 'Môn học này trình bày về công nghệ Portal, tìm hiểu và phát triển một hệ thống Portal mã nguồn mở (GateIn); và là học phần tự chọn cho sinh viên công nghệ thông tin trong một học kỳ, thích hợp cho sinh viên có hướng phát triển về xây dựng ứng dụng Web. Học phần được phân làm 2 phần chính: phần 1 là các khái niệm liên quan tới Portal, so sánh các hệ thống Portal hiện có trên thế giới; phần 2 tập trung tìm hiểu sâu về hệ thống GateIn và xây dựng ứng dụng trên hệ thống này'),
('SE344', 'Lập trình game trong các thiết bị di động', 'Mobile Game Programming', 4, 3, 1, 'CNTC', NULL, NULL, NULL, 'IT001
SE346', 'CNPM', 'Môn học cung cấp cho sinh viên những kiến thức cần thiết để có thể xây dựng game trên các thiết bị cầm tay như điện thoại di động, PocketPC, … Sau khi hoàn tất môn học, sinh viên sẽ nắm vững những đặc điểm của các thiết bị di động cũng như các giới hạn của loại thiết bị này trong việc thực thi các chương trình Game; sinh viên cũng nắm vững nguyên lý của các bộ công cụ phát triển và phương pháp chuyển đổi một Game từ máy tính sang thiết bị di động.'),
('SE345', 'Kỹ thuật lập trình nhúng', 'Embedded programming techniques', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'CNPM', NULL),
('SE346', 'Lập trình trên thiết bị di động', 'Mobile Device Application Development', 4, 3, 1, 'CN', NULL, NULL, NULL, 'IT001
IT008', 'CNPM', 'Học phần này trình bày các kiến trúc, nền tảng của thiết bị di động, các kỹ năng và các hướng tiếp cận chuyên sâu trong xây dựng các ứng dụng trên thiết bị di động và là học phần tự chọn cho sinh viên công nghệ thông tin trong một học kỳ. Học phần là việc kết hợp giữa các bài giảng, thuyết trình, bài tập nhỏ tại lớp và thực hiện đồ án môn học vào cuối kỳ. Học phần được phân làm 3 phần chính sau: phần 1 là các chuyên đề lập trình trên nền tảng .Net và Window Phone, phần 2 là các chuyên đề lập trình trên nền tảng Android, và phần 3 là các chủ đề tìm hiểu.'),
('SE347', 'Công nghệ web và ứng dụng', 'Web Technologies and Applications', 4, 3, 1, 'CNTC', NULL, 'SE341', NULL, 'IT001
IT002
IT004', 'CNPM', 'Môn học cung cấp cho sinh viên cả lý thuyết lẫn kiến thức cơ bản về công nghệ Web. Môn học giới thiệu một trong những mô hình ứng dụng lập trình trên web giúp sinh viên xây dựng các ứng dụng trên Web.'),
('SE348', 'Chuyên đề M-commerce', 'M-Commerce', 2, 2, 0, 'ĐC', NULL, NULL, NULL, 'SE331', 'CNPM', 'Qua môn học này sinh viên có thể làm quen với một vài hoạt động sơ khai của m-commerce. Sinh viên sẽ học các kỹ năng cần thiết, với các kinh nghiệm thực hành cần thiết để sinh viên hiện thực hoặc chỉ đạo triển khai trên các thiết bị di động không dây dẫn (vô tuyến). Sinh viên sẽ nghiên cứu các công nghệ di động và ứng dụng vào trong thương mại di động (M- Commerce), đưa ra những lợi ích, ưu điểm, nhược điểm và ứng dụng của thương mại di động. Đồng thời chỉ ra được điểm khác biệt giữa thương mại di động với thương mại điện tử (E-Commerce)..'),
('SE349', 'Nhập môn Quản trị doanh nghiệp', 'Introduction to business administration', 2, 2, 0, 'CN', NULL, 'IE202', NULL, NULL, 'CNPM', NULL),
('SE350', 'Chuyên đề E-learning', 'E-Learning', 2, 2, 0, 'ĐC', NULL, NULL, NULL, NULL, 'CNPM', 'Môn học này trình bày giới thiệu chung về E-Learning, mô hình và công cụ cho E-Learning. Từ đó, hướng dẫn cách xây dựng và triển khai hệ thống E-Learning. Bên cạnh đó, nội dung liên quan đến quyền sở hữu trí tuệ cũng được đề cập.'),
('SE351', 'Xử lý song song', 'Parallel Processing', 4, 3, 1, 'CN', NULL, NULL, NULL, 'IT001
IT002
IT003', 'CNPM', 'Khóa học trang bị cho học viên kiến thức để thiết kế các thuật toán song song hiệu quả như Thiết kế các thuật toán song song, Phân tích hiệu năng của chương trình song song, Lập trình đa tuyến với POSIX, Lập trình với OpenMP và ứng dựng các kỹ thuật lập trình song song để giải quyết các bài toán khoa học'),
('SE352', 'Phát triển ứng dụng VR', 'VR App Development', 3, 2, 1, 'CNTC', NULL, NULL, NULL, 'SE102', 'CNPM', 'Môn hoc này giúp các sinh viên nắm các khái niệm về ứng dụng VR(Virtual Reality)
và cách xây dụng ứng dụng VR dựa trên Unity 3D. Qua việc học lập trình với VR các
sinh viên có thể phát triển ứng dụng Game, kiến trúc, giả lập..'),
('SE354', 'Chuyên đề các quy trình phát triển phần mềm hiện đại', 'Special Topics in Modern Software Development Processes', 3, 2, 1, 'CNTC', NULL, NULL, NULL, NULL, 'CNPM', 'Môn học nhằm cung cấp cho các sinh viên các kiến thức liên quan đến qui trình phát triển phần mềm hiện đại, các đối tượng chính yếu trong lĩnh vực công nghệ phần mềm (qui trình công nghệ, phương pháp kỹ thuật thực hiện, phương pháp tổ chức quản lý, công cụ và môi trường triển khai phần mềm,…).
\- Cung cấp cho sinh viên các lý thuyết về kiến trúc phần mềm kinh điển và hiện đại
\- Giúp sinh viên hiểu và biết tiến hành xây dựng phần mềm một cách có hệ thống, có phương pháp theo các qui trình phát triển phần mềm hiện đại'),
('SE355', 'Máy học và các công cụ', 'Machine Learning and Tools', 3, 2, 1, 'CN', NULL, NULL, NULL, 'IT003', 'CNPM', NULL),
('SE356', 'Kiến trúc phần mềm', 'Software Architecture', 4, 3, 1, 'CNTC', NULL, NULL, NULL, 'SE104
SE100', 'CNPM', 'Giúp sinh viên hiểu các kiến trúc phần mềm hiện có, các đặc điểm của chúng và qua, giúp sinh viên có khả năng chọn lực kiến trúc thích hợp theo yêu cầu đặt ra
Giới thiệu các kiến trúc phần mềm hiện có như kiến trúc phân lớp, thành phần, dựa vào sự kiện, khung thức... Các phương pháp để mô tả, chọn lựa kiến trúc phần mềm thích hợp.'),
('SE357', 'Kỹ thuật phân tích yêu cầu', 'Requirements Engineering', 3, 2, 1, 'CNTC', NULL, NULL, NULL, 'SE104', 'CNPM', 'Đây là môn học chuyên ngành Công Nghệ Phần Mềm nhằm trang bị cho sinh viên:
\- Kiến thức cơ bản về yêu cầu phần mềm và ảnh hưởng của yêu cầu tới toàn bộ dự án phát triển phần mềm.
\- Kỹ thuật khai phá và thu thập yêu cầu phần mềm.
\- Quy trình phân tích yêu cầu phần mềm và đánh giá chất lượng yêu cầu.
\- Thực hành việc khai thác và thu thập yêu cầu cho dự án công nghệ phần mềm'),
('SE358', 'Quản lý dự án phát triển phần mềm', 'Software Development Project Management', 4, 3, 1, 'CN', NULL, NULL, NULL, 'SE104', 'CNPM', NULL),
('SE359', 'DevOps trong phát triển phần mềm', 'DevOps in Software Developing', 3, 2, 1, 'CSN', NULL, NULL, NULL, NULL, 'CNPM', NULL),
('SE360', 'Điện toán đám mây và phát triển ứng dụng hướng dịch vụ', 'Cloud Computing and Building Cloud-Native Applications', 4, 3, 1, 'CSN', NULL, NULL, NULL, NULL, 'CNPM', NULL),
('SE361', 'Phát triển phần mềm theo kiến trúc Microservices', 'Microservices Software Development', 4, 3, 1, 'CSN', NULL, NULL, NULL, 'IT002
IT004', 'CNPM', NULL),
('SE362', 'An toàn phần mềm và hệ thống', 'Software and Systems Security', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'CNPM', NULL),
('SE363', 'Phát triển ứng dụng trên nền tảng dữ liệu lớn', 'Application Development on Big Data Platforms', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'CNPM', NULL),
('SE364', 'Thiết kế giao diện và trải nghiệm người dùng', 'UI/UX Design', 4, 3, 1, 'CN', NULL, NULL, NULL, 'IT001', 'CNPM', NULL),
('SE365', 'Học sâu ứng dụng trong phát triển phần mềm', 'Applied Deep Learning in Software Engineering', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'CNPM', NULL),
('SE400', 'Seminar các vấn đề hiện đại của công nghệ phần mềm', 'Seminar on Modern Software Engineering', 4, 4, 0, 'CĐTN', NULL, NULL, NULL, 'SE100
SE104', 'CNPM', 'Môn học có thể cung cấp cho người học cái nhìn tổng quan về các vấn đề hiện đại của
lĩnh vực phát triển phần mềm trong giai đoạn hiện nay. Sinh viên sau khi hoàn thành
môn học có thể:
\- Có khả năng tìm hiểu một vấn đề mới
\- Có khả năng viết bào cáo, trình bày vấn đê tìm hiểu…'),
('SE401', 'Mẫu thiết kế', 'Design Patterns', 3, 3, 0, 'CĐTN', NULL, NULL, NULL, 'SE100', 'CNPM', 'Môn học trình bày các mẫu thiết kế hiện đang được sử dụng trong phát triển hệ thống phần mềm, đưa ra các kiến trúc để có thể sử dụng linh hoạt các mẫu thiệt kế vào việc phát triển phân mềm với các giải pháp khác nhau'),
('SE402', 'Điện toán đám mây', 'Cloud computing', 2, 2, 0, 'CĐTN', NULL, 'IS402', NULL, 'IT005
IT008', 'CNPM', NULL),
('SE403', 'Nguyên lý thiết kế thế giới ảo', 'Virtual World Design', 4, 4, 0, 'CĐTN', NULL, NULL, NULL, NULL, 'CNPM', 'Môn học này trình bày cho sinh viên các kiến thức và nguyên lý để từ đó thiết kế thế giới ảo trong công nghệ thông tin.'),
('SE404', 'Chuyên đề E-Government', 'E-Government', 2, 2, 0, 'CĐTN', NULL, NULL, NULL, NULL, 'CNPM', 'Học phần này trình bày về các khái niệm và kiến trúc của Chính phủ điện tử, vai trò và lợi ích của Chính phủ trong việc phát triển kinh tế xã hội. Môn học cung cấp kiến thức về quá trình xây dựng Chính phủ điện tử ở Việt Nam và một số nước trên thế giới cũng như vai trò cốt yếu của công nghệ thông tin nói chung và công nghệ phần mềm nói riêng trong việc xây dựng Chính phủ điện tử.'),
('SE405', 'Chuyên đề mobile and pervasive computing', 'Mobile Pervasive Computing', 3, 3, 0, 'CĐTN', NULL, NULL, NULL, 'SE114', 'CNPM', 'Môn học nhằm cung cấp một nền tảng các khái niệm cơ bản trong lĩnh vực tính toán di động. Giới thiệu các công nghệ, ứng dụng mới và quy trình xây dựng ứng dụng trên thiết bị di động.'),
('SE406', 'Mẫu thiết kế hướng đối tượng', 'Object-Oriented Design Patterns', 4, 4, 0, 'CĐTN', NULL, NULL, NULL, 'SE100', 'CNPM', NULL),
('SE407', 'Chuyên đề pervasive and mobile computing', 'Pervasive Mobile Computing', 4, 4, 0, 'CĐTN', NULL, NULL, NULL, 'SE114', 'CNPM', NULL),
('SE408', 'Phát triển game với blockchain', 'Blockchain-Based Game Development', 4, 3, 1, 'CĐTN', NULL, NULL, NULL, NULL, 'CNPM', NULL),
('SE409', 'Phát triển dự án game', 'Game Project Development', 4, 3, 1, 'CN', NULL, NULL, NULL, NULL, 'CNPM', NULL),
('SE417', 'Đồ án môn học mã nguồn mở', 'Open Source Project', 2, 2, 0, 'ĐA', NULL, 'SE111', NULL, NULL, 'CNPM', 'Đồ án môn học mã nguồn mở nhằm giúp sinh viên:
\- Vận dụng lại các kiến thức đã được học trong nhà trường và tìm hiểu qua các tài liệu báo chí, sách, đài, tivi...như kiến thức về nhập môn công nghệ phần mềm, kiến thức về lập trình, kiến thức về tổ chức dữ liệu, kiến thức về ngôn ngữ và các phương pháp lập trình...nhằm ứng dụng cụ thể vào đồ án môn học mã nguồn mở.
\- Nghiên cứu các thuật toán/các công nghệ/ngôn ngữ lập trình/các ứng dụng được sử dụng rộng rãi trong cộng đồng mã nguồn mở phục vụ cho đồ án môn học mã nguồn mở.
\- Nghiên cứu các quy định, luật chơi được sử dụng khi xây dựng phần mềm mã nguồn mở và tham gia vào cộng đỗng mã nguồn mở.
Nghiên cứu, tìm hiểu, xây dựng và triển khai phần mềm mã nguồn mở được ứng dụng thực tế cho đồ án môn học mã nguồn mở.'),
('SE418', 'Đồ án môn học chuyên ngành', 'Specialized Project', 3, 3, 0, 'ĐA', NULL, 'SE112', NULL, NULL, 'CNPM', NULL),
('SE501', 'Thực tập tốt nghiệp', 'Internship', 2, 2, 0, 'TTTN', NULL, NULL, NULL, NULL, 'CNPM', 'Trong chương trình thực tập cuối khóa sinh viên phải đến thực tập tại các công ty phần mềm, các công ty về CNTT, cơ quan quản lý nhà nước về CNTT, trường học…để làm quen với môi trường thực tế của nghề nghiệp; nắm bắt các công việc; học hỏi kinh nghiệm trong quá trình tác nghiệp tại các đơn vị thực tập, xử lý các tình huống phát sinh liên quan đến lĩnh vực chuyên môn mà sinh viên đã lựa chọn.'),
('SE503', 'Đồ án', 'Major Project', 2, 2, 0, 'CN', NULL, NULL, NULL, NULL, 'CNPM', NULL),
('SE505', 'Khóa luận tốt nghiệp', 'Thesis', 10, 10, 0, 'KLTN', NULL, NULL, NULL, 'SE122', 'CNPM', 'Để tốt nghiệp, sinh viên cần phải hoặc hoàn thành Khóa luận tốt nghiệp hoặc thi 3 môn chuyên đề tốt nghiệp. Với khóa luận tốt nghiệp, sinh viên phải làm một khóa luận phần mềm trong các chuyên ngành là kỹ thuật phần mềm, lập trình nhúng hoặc lập trình game dưới sự hướng dẫn của giảng viên hướng dẫn và phải bảo vệ thành công khóa luận của mình trước hội đồng. Với 3 môn chuyên đề, sinh viên phải học và phải thi 3 môn chuyên đề do khoa đưa ra.'),
('SE506', 'Đồ án tốt nghiệp tại doanh nghiệp', 'Industry Capstone Project', 10, 10, 0, 'KLTN', NULL, NULL, NULL, 'SE122', 'CNPM', NULL),
('SE507', 'Đồ án tốt nghiệp', 'Capstone Project', 6, 6, 0, 'ĐA', NULL, NULL, NULL, 'SE122', 'CNPM', NULL),
('SMET1', 'Phương pháp NCKH trong tin học', 'Scientific Research Methodology in Informatics', 2, 2, 0, 'ĐC', NULL, 'CS519
SS005', NULL, NULL, 'PĐTĐH', NULL),
('SMET2', 'Phương pháp luận sáng tạo KH-CN', 'Scientific Method and Innovation', 2, 2, 0, 'ĐC', NULL, 'CS519
SS005', NULL, NULL, 'PĐTĐH', NULL),
('SOCI1', 'Chủ nghĩa xã hội khoa học', 'Scientific socialism', 3, 3, 0, 'ĐC', NULL, NULL, NULL, NULL, 'P.ĐTĐH', NULL),
('SP3724', 'Kỹ năng giao tiếp', 'Communication Skills', 3, 3, 0, 'ĐC', NULL, NULL, NULL, NULL, 'HTTT', NULL),
('SPCH2713', 'Kỹ năng giao tiếp', 'Introduction to Speech Communication', 2, 2, 0, 'ĐC', NULL, NULL, NULL, NULL, 'HTTT', 'Tăng cường cho sinh viên các kỹ năng trong môi trường làm việc. Lý thuyết và thực tế lập kế hoạch, chuẩn bị và kỹ năng trình bày, kỹ năng báo cáo, đề xuất, hợp tác làm việc,…'),
('SPCH3723', 'Tiếng Anh chuyên ngành công nghệ thông tin', 'English for Computer Science', 3, 3, 0, 'ĐC', NULL, NULL, NULL, NULL, 'HTTT', 'Cung cấp những kiến thức và thuật ngữ Tiếng Anh chuyên ngành máy tính làm nền tảng vững chắc giúp sinh viên tiếp cận kiến thức chuyên ngành máy tính dễ dàng thông qua các tài liệu chuyên ngành và rèn luyện những kỹ năng trình bày các báo cáo chuyên ngành.'),
('SPCH3724', 'Kỹ năng giao tiếp', 'Communication skills', 3, 3, 0, 'ĐC', NULL, 'SPCH2713', NULL, NULL, 'HTTT', 'Tăng cường cho sinh viên các kỹ năng giao tiếp trong môi trường kỹ thuật, khoa học và công nghệ. Lý thuyết và thực tế lập kế hoạch, chuẩn bị và đánh giá các bản báo cáo, đề xuất, hướng dẫn, định hướng nghiên cứu, hợp tác làm việc, trình bày báo cáo.'),
('SS001', 'Những nguyên lý cơ bản của chủ nghĩa Mác Lênin', 'Fundamental principles of Marxism-Leninism', 5, 5, 0, 'ĐC', NULL, NULL, NULL, NULL, 'PĐTĐH', 'Môn học trang bị cho sinh viên những kiến thức cơ bản về triết học Mác-Lênin, kinh tế chính trị Mác-Lênin, chủ nghĩa xã hội khoa học và sự vận dụng chúng vào cách mạng Việt Nam'),
('SS002', 'Đường lối cách mạng của Đảng CS Việt Nam', 'The revolutionary way of the communist party of Vietnam', 3, 3, 0, 'ĐC', NULL, 'SS010', NULL, NULL, 'PĐTĐH', 'Nội dung chủ yếu của môn học là cung cấp cho sinh viên những hiểu biết cơ bản có hệ thống về quá trình thành lập Đảng Cộng sản Việt Nam và đƣờng lối cách mạng của Đảng Cộng sản Việt Nam, đặc biệt là đƣờng lối
trong thời kỳ đổi mới.'),
('SS003', 'Tư tưởng Hồ Chí Minh', 'Ho Chi Minh Thought', 2, 2, 0, 'LLCT', NULL, NULL, NULL, NULL, 'PĐTĐH', 'Môn học trang bị cho sinh viên tư tưởng Hồ Chí Minh về các vấn đề cơ bản của cách mạng Việt Nam, từ cách mạng dân tộc dân chủ nhân dân đến cách mạng xã hội chủ nghĩa như: Tư tưởng Hồ Chí Minh về vấn đề dân tộc và cách mạng giải phóng dân tộc; về chủ nghĩa xã hội và con đường quá độ lên chủ nghĩa xã hội; về Đảng Cộng sản Việt Nam; về đại đoàn kết dân tộc và đoàn kết quốc tế; về xây dựng nhà nước của dân, do dân và vì dân; về văn hóa, đạo đức và xây dựng con người mới.'),
('SS004', 'Kỹ năng nghề nghiệp', 'Professional Skills', 2, 2, 0, 'ĐC', NULL, NULL, NULL, NULL, 'PĐTĐH', 'Môn học cung cấp các kiến thức về các kỹ năng hỗ trợ trong lĩnh vực CNTT. Các kỹ năng này gồm kỹ năng giao tiếp, kỹ năng làm việc nhóm, kỹ năng tư duy, kỹ năng quản lý thời gian và kỹ năng thuyết trình. Nội dung môn học hướng sinh viên tới việc tìm hiểu và thực hành các kỹ năng này. Sinh viên sẽ có những kiến thức và kỹ năng ở mức độ cơ bản để có thể đáp ứng các yêu cầu học tập và làm việc trong ngành CNTT'),
('SS005', 'Phương pháp luận sáng tạo KH-CN', 'Scientific methodology and innovation', 2, 2, 0, 'ĐC', NULL, 'SMET1
 SMET2
CS519', NULL, NULL, 'PĐTĐH', NULL),
('SS007', 'Triết học Mác – Lênin', 'Philosophy of Marxism and Leninism', 3, 3, 0, 'LLCT', NULL, NULL, NULL, NULL, 'PĐTĐH', 'Môn học trang bị cho sinh viên những kiến thức cơ bản về Triết học Mác – Lênin'),
('SS008', 'Kinh tế chính trị Mác – Lênin', 'Political Economics of Marxism and Leninism', 2, 2, 0, 'LLCT', NULL, NULL, NULL, NULL, 'PĐTĐH', 'Môn học trang bị cho sinh viên những kiến thức cơ bản về Kinh tế chính trị Mác – Lênin'),
('SS010', 'Lịch sử Đảng Cộng sản Việt Nam', 'History of Vietnamese Communist Party', 2, 2, 0, 'LLCT', NULL, NULL, NULL, NULL, 'PĐTĐH', 'Môn học trang bị cho sinh viên những kiến thức cơ bản về Lịch sử Đảng Cộng sản Việt Nam'),
('SSKL1', 'Kỹ năng mềm', 'Soft skills', 2, 2, 0, 'ĐC', NULL, NULL, NULL, NULL, 'P.ĐTĐH', NULL),
('STA01', 'Xác suất thống kê', 'Statistics', 3, 3, 0, 'ĐC', NULL, 'MA005', NULL, NULL, 'BMAV', NULL),
('STAT11', 'Xác xuất thống kê', 'Statistics', 3, 3, 0, 'ĐC', NULL, NULL, NULL, NULL, 'HTTT', NULL),
('STAT3013', 'Phân tích thống kê', 'Intermediate Statistical Analysis', 3, 3, 0, 'CS', NULL, NULL, NULL, 'STAT4033', 'HTTT', 'Cung cấp cho sinh viên các ngành kỹ thuật lý thuyết và ứng dụng của ma trận và hệ các phương trình tuyến tính, biến đổi tuyến tính, giá trị riêng, vectơ riêng.
Nội dung: Logic, Tập. Ma trận, hệ phương trình tuyến tính. Không gian vectơ, hạng và ma trận nghịch đảo. Biến đổi tuyến tính. Giá trị riêng và vectơ riêng. Không gian Euclid, trực giao.'),
('STAT4033', 'Thống kê', 'Statistics', 3, 3, 0, 'ĐC', NULL, NULL, NULL, 'MATH3013', 'HTTT', 'Các lý thuyết toán học về xác suất và thống kê cho chúng ta những công cụ cơ bản để xây dựng và phân tích các mô hình toán học cho các sự kiện ngẫu nhiên. Khóa học giúp sinh viên nghiên cứu các hiện tượng ngẫu nhiên và kiến thức nền tảng về thống kê, giả thuyết và kiểm tra giả thuyết, áp dụng xác suất và thống kê cho các lĩnh vực của chuyên môn của họ.'),
('THU086', 'Đào tạo năng lực thông tin', 'Information Literacy Training', 2, 2, 0, 'CN', NULL, NULL, NULL, NULL, 'PĐTĐH', NULL),
('THU107', 'Truyền thông xã hội trong các tổ chức', 'Social Media in Organizations', 2, 2, 0, 'CN', NULL, NULL, NULL, NULL, 'PĐTĐH', NULL),
('TLH025', 'Tâm lý học nhân cách', 'Personality Psychology', 3, 3, 0, 'CN', NULL, NULL, NULL, NULL, 'PĐTĐH', NULL),
('TOEIC 450', 'TOEIC 450', 'TOEIC 450', 0, 0, 0, 'ĐC', NULL, NULL, NULL, NULL, 'TTNN', NULL),
('TOEIC450', 'TOEIC 450', 'TOEIC 450', 0, 0, 0, 'ĐC', NULL, NULL, NULL, NULL, 'TTNN', NULL),
('VCPH1', 'Lịch sử Đảng CSVN', 'History of the Communist Party of Vietnam', 3, 3, 0, 'ĐC', NULL, NULL, NULL, NULL, 'P.ĐTĐH', NULL),
('VCPL1', 'Đường lối cách mạng của Đảng CSVN', 'Revolution Directions of Vietnam Communist Party', 3, 3, 0, 'ĐC', NULL, 'SS002', NULL, NULL, 'TTNN', NULL),
('WINP1', 'Lập trình trên Windows', 'Windows Programming', 4, 3, 1, 'ĐC', NULL, 'IT008', NULL, NULL, 'CNPM', NULL)
ON CONFLICT (mamh) DO UPDATE SET
    tenmh = EXCLUDED.tenmh,
    tenmh_en = EXCLUDED.tenmh_en,
    sotc = EXCLUDED.sotc,
    lt = EXCLUDED.lt,
    th = EXCLUDED.th,
    loaimonhoc = EXCLUDED.loaimonhoc,
    semester = EXCLUDED.semester,
    equivalent_mh = EXCLUDED.equivalent_mh,
    prerequisite_mh = EXCLUDED.prerequisite_mh,
    previous_mh = EXCLUDED.previous_mh,
    management_unit = EXCLUDED.management_unit,
    description = EXCLUDED.description;


INSERT INTO course_relationships (course_id, related_course_id, relation_type) VALUES
((SELECT stt FROM courses WHERE mamh = 'ADENG3'), (SELECT stt FROM courses WHERE mamh = 'ADENG2'), 'PREREQUISITE'),
((SELECT stt FROM courses WHERE mamh = 'ADENG4'), (SELECT stt FROM courses WHERE mamh = 'ADENG3'), 'PREREQUISITE'),
((SELECT stt FROM courses WHERE mamh = 'CE103'), (SELECT stt FROM courses WHERE mamh = 'IT006'), 'COREQUISITE'),
((SELECT stt FROM courses WHERE mamh = 'CE104'), (SELECT stt FROM courses WHERE mamh = 'PH001'), 'COREQUISITE'),
((SELECT stt FROM courses WHERE mamh = 'CE104'), (SELECT stt FROM courses WHERE mamh = 'CE101'), 'COREQUISITE'),
((SELECT stt FROM courses WHERE mamh = 'CE105'), (SELECT stt FROM courses WHERE mamh = 'MA001'), 'COREQUISITE'),
((SELECT stt FROM courses WHERE mamh = 'CE105'), (SELECT stt FROM courses WHERE mamh = 'MA002'), 'COREQUISITE'),
((SELECT stt FROM courses WHERE mamh = 'CE105'), (SELECT stt FROM courses WHERE mamh = 'MA003'), 'COREQUISITE'),
((SELECT stt FROM courses WHERE mamh = 'CE107'), (SELECT stt FROM courses WHERE mamh = 'IT001'), 'COREQUISITE'),
((SELECT stt FROM courses WHERE mamh = 'CE107'), (SELECT stt FROM courses WHERE mamh = 'IT006'), 'COREQUISITE'),
((SELECT stt FROM courses WHERE mamh = 'CE107'), (SELECT stt FROM courses WHERE mamh = 'CE103'), 'COREQUISITE'),
((SELECT stt FROM courses WHERE mamh = 'CE111'), (SELECT stt FROM courses WHERE mamh = 'IT006'), 'COREQUISITE'),
((SELECT stt FROM courses WHERE mamh = 'CE117'), (SELECT stt FROM courses WHERE mamh = 'PH001'), 'COREQUISITE'),
((SELECT stt FROM courses WHERE mamh = 'CE117'), (SELECT stt FROM courses WHERE mamh = 'CE101'), 'COREQUISITE'),
((SELECT stt FROM courses WHERE mamh = 'CE118'), (SELECT stt FROM courses WHERE mamh = 'PH002'), 'COREQUISITE'),
((SELECT stt FROM courses WHERE mamh = 'CE119'), (SELECT stt FROM courses WHERE mamh = 'PH002'), 'COREQUISITE'),
((SELECT stt FROM courses WHERE mamh = 'CE124'), (SELECT stt FROM courses WHERE mamh = 'CE121'), 'COREQUISITE'),
((SELECT stt FROM courses WHERE mamh = 'CE201'), (SELECT stt FROM courses WHERE mamh = 'PH002'), 'COREQUISITE')
ON CONFLICT DO NOTHING;

INSERT INTO courses (mamh, tenmh, sotc, lt, th, loaimonhoc, management_unit, is_open) VALUES
('SE359', 'DevOps trong Phát triển Phần mềm', 3, 2, 1, 'TU_CHON', 'CNPM', true),
('SE102', 'Nhậm môn phát triển game', 3, 2, 1, 'TU_CHON', 'CNPM', true),
('SE115', 'Phát triển game with Unity', 3, 2, 1, 'TU_CHON', 'CNPM', true),
('SE116', 'Phát triển kỹ năng lập trình Game ứng dụng trong thực tế', 4, 3, 1, 'TU_CHON', 'CNPM', true),
('SE114', 'Nhập môn ứng dụng di động', 3, 2, 1, 'TU_CHON', 'CNPM', true),
('SE360', 'Điện toán đám mây và phát triển ứng dụng hướng dịch vụ', 4, 3, 1, 'TU_CHON', 'CNPM', true),
('SE361', 'Phát triển phần mềm theo kiến trúc Microservices', 4, 3, 1, 'TU_CHON', 'CNPM', true),
('SE301', 'Phát triển phần mềm mã nguồn mở', 3, 3, 0, 'TU_CHON', 'CNPM', true),
('SE215', 'Giao tiếp người máy', 4, 3, 1, 'TU_CHON', 'CNPM', true),
('SE113', 'Kiểm chứng phần mềm', 4, 3, 1, 'TU_CHON', 'CNPM', true),
('SE358', 'Quản lý dự án phát triển phần mềm', 4, 3, 1, 'TU_CHON', 'CNPM', true),
('SE117', 'Kỹ thuật lập trình', 4, 3, 1, 'TU_CHON', 'CNPM', true),
('SE330', 'Ngôn ngữ lập trình Java', 4, 3, 1, 'TU_CHON', 'CNPM', true),
('SE332', 'Chuyên đề CSDL nâng cao', 2, 2, 0, 'TU_CHON', 'CNPM', true),
('SE334', 'Các phương pháp lập trình', 3, 2, 1, 'TU_CHON', 'CNPM', true),
('SE347', 'Công nghệ Web và ứng dụng', 4, 3, 1, 'TU_CHON', 'CNPM', true),
('SE350', 'Chuyên đề E-learning', 2, 2, 0, 'TU_CHON', 'CNPM', true),
('SE343', 'Công nghệ Portal', 3, 3, 0, 'TU_CHON', 'CNPM', true),
('SE355', 'Máy học và các công cụ', 3, 2, 1, 'TU_CHON', 'CNPM', true),
('SE310', 'Công nghệ .NET', 4, 3, 1, 'TU_CHON', 'CNPM', true),
('SE346', 'Lập trình trên thiết bị di động', 4, 3, 1, 'TU_CHON', 'CNPM', true),
('SE404', 'Chuyên đề E-Government', 2, 2, 0, 'TU_CHON', 'CNPM', true),
('SE331', 'Chuyên đề E-Commerce', 2, 2, 0, 'TU_CHON', 'CNPM', true),
('SE313', 'Một số thuật toán thông minh', 2, 2, 0, 'TU_CHON', 'CNPM', true),
('SE352', 'Phát triển ứng dụng VR', 3, 2, 1, 'TU_CHON', 'CNPM', true),
('SE109', 'Phát triển, vận hành, bảo trì phần mềm', 3, 3, 0, 'TU_CHON', 'CNPM', true),
('SE356', 'Kiến trúc phần mềm', 4, 3, 1, 'TU_CHON', 'CNPM', true),
('SE357', 'Kỹ thuật phân tích yêu cầu', 3, 2, 1, 'TU_CHON', 'CNPM', true),
('SE325', 'Chuyên đề J2EE', 4, 3, 1, 'TU_CHON', 'CNPM', true),
('SE101', 'Phương pháp mô hình hóa', 3, 3, 0, 'TU_CHON', 'CNPM', true),
('SE106', 'Đặc tả hình thức', 4, 4, 0, 'TU_CHON', 'CNPM', true),
('SE214', 'Công nghệ phần mềm chuyên sâu', 4, 3, 1, 'TU_CHON', 'CNPM', true),
('SE362', 'An toàn phần mềm và hệ thống', 4, 3, 1, 'TU_CHON', 'CNPM', true),
('SE363', 'Phát triển ứng dụng trên nền tảng dữ liệu lớn', 4, 3, 1, 'TU_CHON', 'CNPM', true),
('SE364', 'Thiết kế giao diện và trải nghiệm người dùng', 4, 3, 1, 'TU_CHON', 'CNPM', true),
('SE365', 'Học sâu ứng dụng trong phát triển phần mềm', 4, 3, 1, 'TU_CHON', 'CNPM', true),
('SE221', 'Lập trình game nâng cao', 4, 3, 1, 'TU_CHON', 'CNPM', true),
('SE220', 'Thiết kế game', 4, 3, 1, 'TU_CHON', 'CNPM', true),
('SE320', 'Lập trình đồ họa 3 chiều với Direct 3D', 4, 3, 1, 'TU_CHON', 'CNPM', true),
('SE327', 'Phát triển và vận hành game', 4, 3, 1, 'TU_CHON', 'CNPM', true),
('SE328', 'Lập trình TTNT trong game', 4, 3, 1, 'TU_CHON', 'CNPM', true),
('SE344', 'Lập trình game trên các thiết bị di động', 4, 3, 1, 'TU_CHON', 'CNPM', true),
('SE314', 'Công nghệ game 3D', 3, 2, 1, 'TU_CHON', 'CNPM', true),
('SE315', 'Công nghệ Game Online', 4, 3, 1, 'TU_CHON', 'CNPM', true),
('SE316', 'Phát triển Game đa nền tảng', 3, 2, 1, 'TU_CHON', 'CNPM', true),
('SE317', 'Công nghệ tiên tiến trong phát triển game', 3, 2, 1, 'TU_CHON', 'CNPM', true)
ON CONFLICT (mamh) DO UPDATE SET 
    loaimonhoc = 'TU_CHON',
    is_open = true;

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

INSERT INTO "public"."github_repos" ("id", "repo_name", "description", "github_url", "tech_stack", "subject_id", "display_name", "primary_language", "is_active", "course_id", "stars") VALUES (1, 'DSA-W4-18052023', 'Repository cho các bài tập môn Cấu trúc dữ liệu và giải thuật - phần Cây của thầy Trần Doãn Thuyên', 'https://github.com/UIT-KTPM-2022-3-22521474/DSA-W4-18052023', null, 'IT003', 'DSA-W4-18052023', 'C++', true, (SELECT stt FROM courses WHERE mamh = 'IT003'), 0), 
(2, 'GizmoGlobe-Retailer_side', 'Repository cho nền tảng retailer cho phần báo cáo Đồ án 1 - Đồ án 2 - KLTN của sinh viên Tô Vĩnh Tiến & Đỗ Hồng Quân - KTPM2022 UIT.', 'https://github.com/GizmoGlobe-SE121-P11-UIT/GizmoGlobe-Retailer_side', null, 'SE503', 'GizmoGlobe-Retailer_side', 'Dart', true, (SELECT stt FROM courses WHERE mamh = 'SE503'), 1), 
(3, 'GizmoGlobe-Client_side', 'Repository cho nền tảng client cho phần báo cáo Đồ án 1 - Đồ án 2 - KLTN của sinh viên Tô Vĩnh Tiến & Đỗ Hồng Quân - KTPM2022 UIT.', 'https://github.com/GizmoGlobe-SE121-P11-UIT/GizmoGlobe-Client_side', null, 'SE503', 'GizmoGlobe-Client_side', 'Dart', true, (SELECT stt FROM courses WHERE mamh = 'SE503'), 1), 
(4, 'NET-SE310.P12-MotorStore', 'Repository cho phần bài thực hành 2 - ASP.NET & MVC môn Công nghệ .NET - SE310.P12 của thầy Nguyễn Trịnh Đông và cô Huỳnh Hồ Thị Mộng Trinh', 'https://github.com/UIT-KTPM-2022-3-22521474/NET-SE310.P12-MotorStore', null, 'SE310', 'NET-SE310.P12-MotorStore', 'CSS', true, (SELECT stt FROM courses WHERE mamh = 'SE310'), 0), 
(5, 'Java-BaiTapChuong3', 'Repository cho phần bài tập chương 3 môn Ngôn ngữ lập trình Java - SE330.O21 của thầy Lê Thanh Trọng', 'https://github.com/UIT-KTPM-2022-3-22521474/Java-BaiTapChuong3', null, 'SE330', 'Java-BaiTapChuong3', 'Java', true, (SELECT stt FROM courses WHERE mamh = 'SE330'), 0), 
(6, 'NET-SE310.P12-Farm-Management', 'Repository cho phần bài tập 1 ADO.NET - Quản lý trang trại môn Công nghệ .NET - SE310.P12 của thầy Nguyễn Trịnh Đông và cô Huỳnh Hồ Thị Mộng Trinh', 'https://github.com/UIT-KTPM-2022-3-22521474/NET-SE310.P12-Farm-Management', null, 'SE310', 'NET-SE310.P12-Farm-Management', 'C#', true, (SELECT stt FROM courses WHERE mamh = 'SE310'), 0), 
(7, 'ProgMethods-SE334.P21-Recipe', 'Repository cho phần ứng dụng bài tập thực hành 2 - môn Các phương pháp lập trình - SE334.P21 của thầy Nguyễn Duy Khánh.', 'https://github.com/FiveM-UIT/ProgMethods-SE334.P21-Recipe', null, 'SE334', 'ProgMethods-SE334.P21-Recipe', 'JavaScript', true, (SELECT stt FROM courses WHERE mamh = 'SE334'), 0), 
(8, 'MobileProgramming-MoneyTracking', 'Repository cho đồ án ứng dụng quản lý chi tiêu - Money Tracking của môn Lập trình trên thiết bị di động - SE346.O21 của thầy Huỳnh Tuấn Anh', 'https://github.com/UIT-KTPM-2022-3-22521474/MobileProgramming-MoneyTracking', null, 'SE346', 'MobileProgramming-MoneyTracking', null, true, (SELECT stt FROM courses WHERE mamh = 'SE346'), 0), 
(9, 'WebDev-SE347.P12-SSE', 'Repository cho phần bài tập SSE- môn Công nghệ Web và ứng dụng - SE347.P12 của thầy Nguyễn Tấn Toàn.', 'https://github.com/UIT-KTPM-2022-3-22521474/WebDev-SE347.P12-SSE', null, 'SE347', 'WebDev-SE347.P12-SSE', 'JavaScript', true, (SELECT stt FROM courses WHERE mamh = 'SE347'), 0), 
(10, 'WebDev-SE347.P12-Yahoo', 'Repository cho phần bài tập 1 môn Công nghệ Web và ứng dụng - SE347.P12 của thầy Nguyễn Tấn Toàn và thầy Lê Văn Tuấn.', 'https://github.com/UIT-KTPM-2022-3-22521474/WebDev-SE347.P12-Yahoo', null, 'SE347', 'WebDev-SE347.P12-Yahoo', 'HTML', true, (SELECT stt FROM courses WHERE mamh = 'SE347'), 0), 
(11, 'ProgMethods-SE334.P21-CinemaBooking', 'Repository cho phần ứng dụng bài tập thực hành 1 - môn Các phương pháp lập trình - SE334.P21 của thầy Nguyễn Duy Khánh.', 'https://github.com/FiveM-UIT/ProgMethods-SE334.P21-CinemaBooking', null, 'SE334', 'ProgMethods-SE334.P21-CinemaBooking', 'TypeScript', true, (SELECT stt FROM courses WHERE mamh = 'SE334'), 0), 
(12, 'UIT_IT001', '', 'https://github.com/ThanhTrucT/UIT_IT001', null, 'IT001', 'UIT_IT001', 'C++', true, (SELECT stt FROM courses WHERE mamh = 'IT001'), 0), 
(13, 'DSA-W2-06042023', 'Repository cho các bài tập thực hành môn Cấu trúc Dữ liệu và Giải thuật của thầy Trần Doãn Thuyên', 'https://github.com/UIT-KTPM-2022-3-22521474/DSA-W2-06042023', null, 'IT003', 'DSA-W2-06042023', 'C++', true, (SELECT stt FROM courses WHERE mamh = 'IT003'), 0), 
(14, 'IT007', 'Hệ Điều Hành - Operating System (OS)', 'https://github.com/kennywalker1205/IT007', null, 'IT007', 'IT007', 'C', true, (SELECT stt FROM courses WHERE mamh = 'IT007'), 0), 
(15, 'it007-lab6', 'homebrew shell :)', 'https://github.com/doqin/it007-lab6', null, 'IT007', 'it007-lab6', 'C', true, (SELECT stt FROM courses WHERE mamh = 'IT007'), 1), 
(16, 'IT007.N12.KHCL', 'Hệ điều hành - Lab Report', 'https://github.com/gkietle/IT007.N12.KHCL', null, 'IT007', 'IT007.N12.KHCL', 'C', true, (SELECT stt FROM courses WHERE mamh = 'IT007'), 1), 
(17, 'Lab04-IT007', 'Our team''s works for a lab in Operating System ', 'https://github.com/uyenbhku/Lab04-IT007', null, 'IT007', 'Lab04-IT007', 'C++', true, (SELECT stt FROM courses WHERE mamh = 'IT007'), 0), 
(18, 'IT007_Lab6_2024', 'Customize shell writen in C lang', 'https://github.com/HarryLee02/IT007_Lab6_2024', null, 'IT007', 'IT007_Lab6_2024', 'C', true, (SELECT stt FROM courses WHERE mamh = 'IT007'), 0), 
(19, 'NET-SE310-P12-HotelManagement', 'Repository cho phần đồ án cuối kỳ - ứng dụng quản lý khách sạn môn Công nghệ .NET - SE310.P12 của thầy Nguyễn Trịnh Đông và cô Huỳnh Hồ Thị Mộng Trinh', 'https://github.com/NET-SE310-P12-UIT2024HK5/NET-SE310-P12-HotelManagement', null, 'SE310', 'NET-SE310-P12-HotelManagement', 'JavaScript', true, (SELECT stt FROM courses WHERE mamh = 'SE310'), 0), 
(20, 'IT007-Operating-Systems', 'Tổng hợp các hướng dẫn làm Lab môn Hệ điều hành', 'https://github.com/ngocnhhu12/IT007-Operating-Systems', null, 'IT007', 'IT007-Operating-Systems', null, true, (SELECT stt FROM courses WHERE mamh = 'IT007'), 0), 
(21, 'NET-SE310.P12-ASP.NET-MVC', 'Repository cho phần bài tập 4 - ASP.NET & MVC môn Công nghệ .NET - SE310.P12 của thầy Nguyễn Trịnh Đông và cô Huỳnh Hồ Thị Mộng Trinh', 'https://github.com/UIT-KTPM-2022-3-22521474/NET-SE310.P12-ASP.NET-MVC', null, 'SE310', 'NET-SE310.P12-ASP.NET-MVC', 'JavaScript', true, (SELECT stt FROM courses WHERE mamh = 'SE310'), 0), 
(22, 'Java-BaiThucHanh2', 'Repository cho phần bài tập thực hành 2 môn Ngôn ngữ lập trình Java - SE330.O21 của thầy Lê Thanh Trọng', 'https://github.com/UIT-KTPM-2022-3-22521474/Java-BaiThucHanh2', null, 'SE330', 'Java-BaiThucHanh2', 'Java', true, (SELECT stt FROM courses WHERE mamh = 'SE330'), 0), 
(24, 'IT002_Object-Oriented-Programming_FinalExam', 'Solving OOP Examination at UIT from Sugar (anhkiet1227)', 'https://github.com/anhkiet1227/IT002_Object-Oriented-Programming_FinalExam', null, 'IT002', 'IT002_Object-Oriented-Programming_FinalExam', 'C++', true, (SELECT stt FROM courses WHERE mamh = 'IT002'), 5), 
(25, 'DSA-W6-16062023', 'Repository cho các bài tập thực hành tuần 6 môn Cấu trúc dữ liệu và Giải thuật của thầy Trần Doãn Thuyên', 'https://github.com/UIT-KTPM-2022-3-22521474/DSA-W6-16062023', null, 'IT003', 'DSA-W6-16062023', 'C++', true, (SELECT stt FROM courses WHERE mamh = 'IT003'), 0), 
(27, 'IT002OfDuong', 'Lập trình hướng đối tượng UIT', 'https://github.com/duonguwu/IT002OfDuong', null, 'IT002', 'IT002OfDuong', 'C++', true, (SELECT stt FROM courses WHERE mamh = 'IT002'), 1), 
(28, 'IT002-ObjectOrientedProgramming-UIT', 'A repository for storing all learning resources related to Object Oriented Programming at UIT', 'https://github.com/solivaquaant/IT002-ObjectOrientedProgramming-UIT', null, 'IT002', 'IT002-ObjectOrientedProgramming-UIT', 'C++', true, (SELECT stt FROM courses WHERE mamh = 'IT002'), 0), 
(29, 'Java-BaiTap5C2', 'Repository cho phần bài tập 5 cuối chương 2 môn Ngôn ngữ lập trình Java - SE330.O21 của thầy Lê Thanh Trọng', 'https://github.com/UIT-KTPM-2022-3-22521474/Java-BaiTap5C2', null, 'SE330', 'Java-BaiTap5C2', 'Java', true, (SELECT stt FROM courses WHERE mamh = 'SE330'), 0), 
(30, 'WebDev-SE347.P12-TodoApp', 'Repository cho phần bài tập 4 - Todo WebApp - Vite-ReactJS-TailwindCSS - môn Công nghệ Web và ứng dụng - SE347.P12 của thầy Nguyễn Tấn Toàn..', 'https://github.com/UIT-KTPM-2022-3-22521474/WebDev-SE347.P12-TodoApp', null, 'SE347', 'WebDev-SE347.P12-TodoApp', 'JavaScript', true, (SELECT stt FROM courses WHERE mamh = 'SE347'), 0), 
(31, 'NguyenHoangKhang_22718451_kientrucphanmem', 'đây là repository cho thực hành môn kiến trúc phần mềm', 'https://github.com/nguyenhoangkhq7/NguyenHoangKhang_22718451_kientrucphanmem', null, 'SE356', 'NguyenHoangKhang_22718451_kientrucphanmem', 'Java', true, (SELECT stt FROM courses WHERE mamh = 'SE356'), 0), 
(32, 'UIT-IT007', 'Hệ điều hành', 'https://github.com/alttleb0y/UIT-IT007', null, 'IT007', 'UIT-IT007', 'Shell', true, (SELECT stt FROM courses WHERE mamh = 'IT007'), 0), 
(33, 'IT007-OS', 'Hệ điều hành - Đại học Công nghệ Thông tin (UIT) ', 'https://github.com/doxuantu110/IT007-OS', null, 'IT007', 'IT007-OS', null, true, (SELECT stt FROM courses WHERE mamh = 'IT007'), 0), 
(34, 'UIT-VNU_IT007_LAB-06', '', 'https://github.com/fredle09/UIT-VNU_IT007_LAB-06', null, 'IT007', 'UIT-VNU_IT007_LAB-06', 'C', true, (SELECT stt FROM courses WHERE mamh = 'IT007'), 0), 
(35, 'OOP', 'This repository contains the source code for exercises from my IT002 - Object-Oriented Programming class at the University of Information Technology (UIT)', 'https://github.com/tnhi1821/OOP', null, 'IT002', 'OOP', 'Jupyter Notebook', true, (SELECT stt FROM courses WHERE mamh = 'IT002'), 0), 
(36, 'IT007_HDH', 'LAB FOR IT007', 'https://github.com/MinhisMinh/IT007_HDH', null, 'IT007', 'IT007_HDH', 'C', true, (SELECT stt FROM courses WHERE mamh = 'IT007'), 0), 
(37, 'UIT_IT003', null, 'https://github.com/cngpc43/UIT_IT003', null, 'IT003', 'UIT_IT003', 'C++', true, (SELECT stt FROM courses WHERE mamh = 'IT003'), 0), 
(38, 'Game_2048_DSA-', 'ĐỒ ÁN MÔN IT003 - UIT', 'https://github.com/daknulak/Game_2048_DSA-', null, 'IT003', 'Game_2048_DSA-', 'Python', true, (SELECT stt FROM courses WHERE mamh = 'IT003'), 0), 
(39, 'IT004', 'Database - IT004.Q111 - UIT', 'https://github.com/HDungg925/IT004', null, 'IT004', 'IT004', 'TSQL', true, (SELECT stt FROM courses WHERE mamh = 'IT004'), 0), 
(40, 'IT004', 'Cơ sở dữ liệu - Database', 'https://github.com/kennywalker1205/IT004', null, 'IT004', 'IT004', 'TSQL', true, (SELECT stt FROM courses WHERE mamh = 'IT004'), 0), 
(41, 'IT004', 'Tài liệu môn Cơ sở Dữ liệu - UIT', 'https://github.com/h2okisme57/IT004', null, 'IT004', 'IT004', null, true, (SELECT stt FROM courses WHERE mamh = 'IT004'), 0), 
(42, 'IT004_DATABASE_SQL', 'Just a place where i can submit my homework ', 'https://github.com/Lanh-ngwah/IT004_DATABASE_SQL', null, 'IT004', 'IT004_DATABASE_SQL', 'TSQL', true, (SELECT stt FROM courses WHERE mamh = 'IT004'), 0), 
(43, 'IT004-Database-UIT', 'A repository for storing all learning resources related to IDatabase at UIT', 'https://github.com/solivaquaant/IT004-Database-UIT', null, 'IT004', 'IT004-Database-UIT', 'TSQL', true, (SELECT stt FROM courses WHERE mamh = 'IT004'), 0), 
(44, 'IT004_UIT_Database', 'A repository about doing lab in database which is a subject at University of Information Technology', 'https://github.com/AlyThien/IT004_UIT_Database', null, 'IT004', 'IT004_UIT_Database', 'TSQL', true, (SELECT stt FROM courses WHERE mamh = 'IT004'), 0), 
(45, 'SE334.P21-E-book-Reader', 'Repository cho phần ứng dụng đồ án cuối kì E-book Reader - môn Các phương pháp lập trình - SE334.P21 của thầy Nguyễn Duy Khánh.', 'https://github.com/SE334-P21-ProgrammingMethods/SE334.P21-E-book-Reader', null, 'SE503', 'SE334.P21-E-book-Reader', 'Dart', true, (SELECT stt FROM courses WHERE mamh = 'SE503'), 2), 
(47, 'SE215.P11-Last_mile_delivery', 'Repository cho phần đồ án front-end cuối kì LastMileDelivery GiaoHangXanh - môn Giao tiếp người - máy - SE215.P11 của thầy Nguyễn Công Hoan.', 'https://github.com/FiveM-UIT/SE215.P11-Last_mile_delivery', null, 'SE215', 'SE215.P11-Last_mile_delivery', 'JavaScript', true, (SELECT stt FROM courses WHERE mamh = 'SE215'), 1), 
(48, 'it008_15_Puzzle_Game', 'A 15 puzzle game make in wpf aka a project for it008 in UIT', 'https://github.com/bighousevn/it008_15_Puzzle_Game', null, 'IT008', 'it008_15_Puzzle_Game', 'C#', true, (SELECT stt FROM courses WHERE mamh = 'IT008'), 0), 
(49, 'hci2025_19130143', 'Repository môn giao tiêp người máy 2025', 'https://github.com/NajooDev/hci2025_19130143', null, 'SE215', 'hci2025_19130143', 'HTML', true, (SELECT stt FROM courses WHERE mamh = 'SE215'), 0), 
(50, 'Java-BaiThucHanh3', 'Repository cho các bài tập trong Bài thực hành 3 JDBC - môn Ngôn ngữ lập trình Java SE330.O21 của thầy Lê Thanh Trọng', 'https://github.com/UIT-KTPM-2022-3-22521474/Java-BaiThucHanh3', null, 'SE330', 'Java-BaiThucHanh3', 'Java', true, (SELECT stt FROM courses WHERE mamh = 'SE330'), 0), 
(51, '24521736_Lab3_IT008', 'Bài tập thực hành Lab 3 Lập trình trực quan', 'https://github.com/DonThuanUIT-24521736/24521736_Lab3_IT008', null, 'IT008', '24521736_Lab3_IT008', 'C#', true, (SELECT stt FROM courses WHERE mamh = 'IT008'), 0), 
(52, 'UIT_IT007', 'Operating System - IT007', 'https://github.com/anhhuyluong/UIT_IT007', null, 'IT007', 'UIT_IT007', 'C', true, (SELECT stt FROM courses WHERE mamh = 'IT007'), 0), 
(53, 'UIT_IT007', 'Operating System - IT007', 'https://github.com/anhhuyluong/UIT_IT007', null, 'IT007', 'UIT_IT007', 'C', true, (SELECT stt FROM courses WHERE mamh = 'IT007'), 0), 
(54, 'IT007', 'UIT Operating System - IT0007', 'https://github.com/phatthanh69/IT007', null, 'IT007', 'IT007', 'C++', true, (SELECT stt FROM courses WHERE mamh = 'IT007'), 0), 
(55, 'IT012-LAB04-21520093', '21520093- Võ Minh Quân-9', 'https://github.com/omlgg/IT012-LAB04-21520093', null, 'IT012', 'IT012-LAB04-21520093', 'Assembly', true, (SELECT stt FROM courses WHERE mamh = 'IT012'), 0), 
(56, 'LAB04', 'Thực hành IT012.M21.KHTN - LAB04', 'https://github.com/nv259/LAB04', null, 'IT012', 'LAB04', 'Assembly', true, (SELECT stt FROM courses WHERE mamh = 'IT012'), 1), 
(58, 'UITCanteen', 'SE104.N11 project', 'https://github.com/TNB142/UITCanteen', null, 'SE104', 'UITCanteen', 'TypeScript', true, (SELECT stt FROM courses WHERE mamh = 'SE104'), 1), 
(59, 'SE104.L27.KHCL', 'Nhập môn công nghệ phần mềm', 'https://github.com/Karhdo/SE104.L27.KHCL', null, 'SE104', 'SE104.L27.KHCL', 'C#', true, (SELECT stt FROM courses WHERE mamh = 'SE104'), 5), 
(60, 'UIT-IT007.P11.CTTN-Operating_Systems', '', 'https://github.com/ckha1410/UIT-IT007.P11.CTTN-Operating_Systems', null, 'IT007', 'UIT-IT007.P11.CTTN-Operating_Systems', 'C', true, (SELECT stt FROM courses WHERE mamh = 'IT007'), 0), 
(61, 'IT007-LAB', '', 'https://github.com/Darklul03/IT007-LAB', null, 'IT007', 'IT007-LAB', 'C', true, (SELECT stt FROM courses WHERE mamh = 'IT007'), 0), 
(62, 'StudentManagementWinApp', 'This Windows-based student management application is my final project for the SE104 Software Engineering course at the University of Information Technology (UIT) ', 'https://github.com/tnhi1821/StudentManagementWinApp', null, 'SE104', 'StudentManagementWinApp', 'C#', true, (SELECT stt FROM courses WHERE mamh = 'SE104'), 0), 
(63, 'IT007_LAB4', '', 'https://github.com/Khanh23-code/IT007_LAB4', null, 'IT007', 'IT007_LAB4', 'C', true, (SELECT stt FROM courses WHERE mamh = 'IT007'), 0), 
(64, 'Desktop-UIT-25730129', 'Bài tập Git - SS004', 'https://github.com/25730129-TranHuynhUyenNhi/Desktop-UIT-25730129', null, 'SS004', 'Desktop-UIT-25730129', 'C++', true, (SELECT stt FROM courses WHERE mamh = 'SS004'), 0), 
(65, 'advanced-inglish-front-end', 'A front-end repository for SE505 (KLTN) at UIT.', 'https://github.com/vthphc/advanced-inglish-front-end', null, 'SE505', 'advanced-inglish-front-end', 'TypeScript', true, (SELECT stt FROM courses WHERE mamh = 'SE505'), 1), 
(66, 'UIT-SS004', '', 'https://github.com/w4ngg/UIT-SS004', null, 'SS004', 'UIT-SS004', 'Python', true, (SELECT stt FROM courses WHERE mamh = 'SS004'), 0), 
(67, 'SS004-Job-skills', 'This is a course project for Job Skills. The selected topic is Building an online lecture on game programming using Godot Engine.', 'https://github.com/PhuocSang16/SS004-Job-skills', null, 'SS004', 'SS004-Job-skills', null, true, (SELECT stt FROM courses WHERE mamh = 'SS004'), 1), 
(68, 'Game-FrameWork', 'Framework for [this project](https://github.com/loia5tqd001/SE102-UIT-Game-Captain-America-and-The-Avengers)', 'https://github.com/loia5tqd001/Game-FrameWork', null, 'SE102', 'Game-FrameWork', 'C++', true, (SELECT stt FROM courses WHERE mamh = 'SE102'), 3), 
(69, 'SE102-UIT-Game-Captain-America-and-The-Avengers', 'A school project remaking 1 NES game with C++ and DirectX 9', 'https://github.com/loia5tqd001/SE102-UIT-Game-Captain-America-and-The-Avengers', null, 'SE102', 'SE102-UIT-Game-Captain-America-and-The-Avengers', 'C++', true, (SELECT stt FROM courses WHERE mamh = 'SE102'), 4), 
(70, 'Castlevania', 'SE102- Game UIT Project: A remake of Castlevania NES :video_game:  ', 'https://github.com/viettiennguyen029/Castlevania', null, 'SE102', 'Castlevania', 'C++', true, (SELECT stt FROM courses WHERE mamh = 'SE102'), 5), 
(71, 'SS004.Q215-Tetris-Group07-InfoSys5', 'Đồ án cuối kỳ môn SS004: Xây dựng game Tetris bằng C++ - Trường ĐH CNTT (UIT)', 'https://github.com/25520267/SS004.Q215-Tetris-Group07-InfoSys5', null, 'SS004', 'SS004.Q215-Tetris-Group07-InfoSys5', 'C++', true, (SELECT stt FROM courses WHERE mamh = 'SS004'), 2), 
(72, 'SE102-intro-game-development', 'Course assignments and the final project will be a complete Super Mario 3', 'https://github.com/dxv2k/SE102-intro-game-development', null, 'SE102', 'SE102-intro-game-development', 'C++', true, (SELECT stt FROM courses WHERE mamh = 'SE102'), 25), 
(73, 'super-mario-bros-3-se102', 'A mock of the famous videogame SMB3 for my UIT SE102 course', 'https://github.com/comaybay/super-mario-bros-3-se102', null, 'SE102', 'super-mario-bros-3-se102', 'C++', true, (SELECT stt FROM courses WHERE mamh = 'SE102'), 0), 
(74, 'KNNN_Snake_game', 'Final project SS004.CLC UIT team - Snake game ', 'https://github.com/VanNhatMinh/KNNN_Snake_game', null, 'SS004', 'KNNN_Snake_game', null, true, (SELECT stt FROM courses WHERE mamh = 'SS004'), 0), 
(75, 'UIT-SE100-PPPTPMOOP', 'Basic App manage residents information', 'https://github.com/yang020501/UIT-SE100-PPPTPMOOP', null, 'SE100', 'UIT-SE100-PPPTPMOOP', 'C#', true, (SELECT stt FROM courses WHERE mamh = 'SE100'), 0), 
(76, 'Lab06-IT007', null, 'https://github.com/uyenbhku/Lab06-IT007', null, 'IT007', 'Lab06-IT007', 'C++', true, (SELECT stt FROM courses WHERE mamh = 'IT007'), 0), 
(77, 'IT007_LAB5', null, 'https://github.com/khongphaidpduy/IT007_LAB5', null, 'IT007', 'IT007_LAB5', 'C', true, (SELECT stt FROM courses WHERE mamh = 'IT007'), 0), 
(78, 'cinemaphile', 'UIT SE114''s final project - Mobile programming', 'https://github.com/quoclien/cinemaphile', null, 'SE114', 'cinemaphile', 'Java', true, (SELECT stt FROM courses WHERE mamh = 'SE114'), 0), 
(79, 'IT003-DataStructuresAndAlgorithms-UIT', 'A repository for storing all learning resources related to Data Structures and Algorithms at UIT', 'https://github.com/solivaquaant/IT003-DataStructuresAndAlgorithms-UIT', null, 'IT003', 'IT003-DataStructuresAndAlgorithms-UIT', 'C++', true, (SELECT stt FROM courses WHERE mamh = 'IT003'), 0), 
(80, 'UIT-IT002-OOP', null, 'https://github.com/kiet252/UIT-IT002-OOP', null, 'IT002', 'UIT-IT002-OOP', 'C++', true, (SELECT stt FROM courses WHERE mamh = 'IT002'), 0), 
(81, 'IT002-Object-oriented-programming', 'This is some basic assignment of OOP using C++', 'https://github.com/PhuocSang16/IT002-Object-oriented-programming', null, 'IT002', 'IT002-Object-oriented-programming', 'C++', true, (SELECT stt FROM courses WHERE mamh = 'IT002'), 1), 
(82, 'IT002-Object-oriented-programming', 'This is some basic assignment of OOP using C++', 'https://github.com/PhuocSang16/IT002-Object-oriented-programming', null, 'IT002', 'IT002-Object-oriented-programming', 'C++', true, (SELECT stt FROM courses WHERE mamh = 'IT002'), 1), 
(83, 'OOP-OnThiCuoiKy', 'Repository hỗ trợ ôn thi cuối kỳ môn OOP - Lập trình hướng đối tượng', 'https://github.com/UIT-KTPM-2022-3-22521474/OOP-OnThiCuoiKy', null, 'IT002', 'OOP-OnThiCuoiKy', 'C++', true, (SELECT stt FROM courses WHERE mamh = 'IT002'), 0), 
(84, 'DSA-W5-01062023', 'Repository cho các bài tập về Hash - Cấu trúc dữ liệu và giải thuật của thầy Trần Doãn Thuyên', 'https://github.com/UIT-KTPM-2022-3-22521474/DSA-W5-01062023', null, 'IT003', 'DSA-W5-01062023', 'C++', true, (SELECT stt FROM courses WHERE mamh = 'IT003'), 0), 
(85, 'DSA-LT-Sort-27030304', 'Cấu trúc dữ liệu và giải thuật IT003.N213 - Repository cho các bài tập về các thuật toán sắp xếp của thầy Ngô Tuấn Kiệt', 'https://github.com/UIT-KTPM-2022-3-22521474/DSA-LT-Sort-27030304', null, 'IT003', 'DSA-LT-Sort-27030304', 'C++', true, (SELECT stt FROM courses WHERE mamh = 'IT003'), 0), 
(86, 'ThucHanh_SE310.P11', 'Công nghệ .NET UIT SE310.P11', 'https://github.com/minhphan46/ThucHanh_SE310.P11', null, 'SE310', 'ThucHanh_SE310.P11', 'CSS', true, (SELECT stt FROM courses WHERE mamh = 'SE310'), 0), 
(87, 'IT007.Q17-Operating-Systems-UIT', null, 'https://github.com/Naathan404/IT007.Q17-Operating-Systems-UIT', null, 'IT007', 'IT007.Q17-Operating-Systems-UIT', 'C', true, (SELECT stt FROM courses WHERE mamh = 'IT007'), 0), 
(88, 'DSA-W3-01052023', 'Repository cho các bài tập Cấu trúc dữ liệu và Giải thuật - Danh sách liên kết đơn của thầy Trần Doãn Thuyên', 'https://github.com/UIT-KTPM-2022-3-22521474/DSA-W3-01052023', null, 'IT003', 'DSA-W3-01052023', 'C++', true, (SELECT stt FROM courses WHERE mamh = 'IT003'), 0), 
(89, 'WebDev-SE347.P12-GraphQL', 'Repository cho phần bài tập GraphQL - môn Công nghệ Web và ứng dụng - SE347.P12 của thầy Nguyễn Tấn Toàn.', 'https://github.com/UIT-KTPM-2022-3-22521474/WebDev-SE347.P12-GraphQL', null, 'SE347', 'WebDev-SE347.P12-GraphQL', 'JavaScript', true, (SELECT stt FROM courses WHERE mamh = 'SE347'), 0), 
(90, 'WebDev-SE347.P12-WebSocket', 'Repository cho phần bài tập WebSocket - môn Công nghệ Web và ứng dụng - SE347.P12 của thầy Nguyễn Tấn Toàn.', 'https://github.com/UIT-KTPM-2022-3-22521474/WebDev-SE347.P12-WebSocket', null, 'SE347', 'WebDev-SE347.P12-WebSocket', 'HTML', true, (SELECT stt FROM courses WHERE mamh = 'SE347'), 0), 
(91, 'java_se330.i21', 'Java project SE330.I21 UIT 2018', 'https://github.com/danhthanh418/java_se330.i21', null, 'SE330', 'java_se330.i21', null, true, (SELECT stt FROM courses WHERE mamh = 'SE330'), 0), 
(92, 'OrderUp-Food-Delivery-And-Ordering-Website', 'UIT - SE330.P21 - Final Project', 'https://github.com/Linh0512/OrderUp-Food-Delivery-And-Ordering-Website', null, 'SE330', 'OrderUp-Food-Delivery-And-Ordering-Website', 'Java', true, (SELECT stt FROM courses WHERE mamh = 'SE330'), 1), 
(93, 'UIT-SE330-NNLTJava', 'Java NetBeans GUI with minimax ', 'https://github.com/yang020501/UIT-SE330-NNLTJava', null, 'SE330', 'UIT-SE330-NNLTJava', 'Java', true, (SELECT stt FROM courses WHERE mamh = 'SE330'), 1), 
(94, 'IT008.O14-VisualProgramming-HealthApp', 'Repository cho phần ứng dụng đồ án cuối kì HealthApp - môn Lập trình trực quan - IT008.O14 của thầy Nguyễn Tấn Toàn.', 'https://github.com/UIT-KTPM-2022-3-22521474/IT008.O14-VisualProgramming-HealthApp', null, 'IT008', 'IT008.O14-VisualProgramming-HealthApp', null, true, (SELECT stt FROM courses WHERE mamh = 'IT008'), 0), 
(95, 'NET-SE310.P12-Seminar_LinQ', 'Repository cho phần seminar LinQ môn Công nghệ .NET - SE310.P12 của thầy Nguyễn Trịnh Đông và cô Huỳnh Hồ Thị Mộng Trinh', 'https://github.com/UIT-KTPM-2022-3-22521474/NET-SE310.P12-Seminar_LinQ', null, 'SE310', 'NET-SE310.P12-Seminar_LinQ', 'C#', true, (SELECT stt FROM courses WHERE mamh = 'SE310'), 0), 
(98, 'TicketSellingWebsite', 'Final_Semester_Project_SE347', 'https://github.com/anhkhoatqt11/TicketSellingWebsite', null, 'SE347', 'TicketSellingWebsite', 'TypeScript', true, (SELECT stt FROM courses WHERE mamh = 'SE347'), 6), 
(99, 'TITHotelier', 'SE347.P11 - Công nghệ Web và Ứng dụng - UIT ', 'https://github.com/NguyenNhuTu09/TITHotelier', null, 'SE347', 'TITHotelier', 'JavaScript', true, (SELECT stt FROM courses WHERE mamh = 'SE347'), 0), 
(100, 'Flutter-styLLe', 'A project for SE346 - UIT', 'https://github.com/nhlinhseuit/Flutter-styLLe', null, 'SE346', 'Flutter-styLLe', 'Dart', true, (SELECT stt FROM courses WHERE mamh = 'SE346'), 1), 
(101, 'NET-SE310.P12-BaoMoi', 'Repository cho phần bài tập 2 - UI/UX Báo mới môn Công nghệ .NET - SE310.P12 của thầy Nguyễn Trịnh Đông và cô Huỳnh Hồ Thị Mộng Trinh', 'https://github.com/UIT-KTPM-2022-3-22521474/NET-SE310.P12-BaoMoi', null, 'SE310', 'NET-SE310.P12-BaoMoi', 'HTML', true, (SELECT stt FROM courses WHERE mamh = 'SE310'), 0), 
(102, 'NET-SE310.P12-ProductManagement', 'Repository cho phần bài tập 3 - LinQ môn Công nghệ .NET - SE310.P12 của thầy Nguyễn Trịnh Đông và cô Huỳnh Hồ Thị Mộng Trinh', 'https://github.com/UIT-KTPM-2022-3-22521474/NET-SE310.P12-ProductManagement', null, 'SE310', 'NET-SE310.P12-ProductManagement', 'C#', true, (SELECT stt FROM courses WHERE mamh = 'SE310'), 0), 
(103, 'Java-BaiThucHanh1', 'Repository cho các bài tập trong Bài thực hành 1 - môn Ngôn ngữ lập trình Java SE330.O21 của thầy Lê Thanh Trọng', 'https://github.com/UIT-KTPM-2022-3-22521474/Java-BaiThucHanh1', null, 'SE330', 'Java-BaiThucHanh1', 'Java', true, (SELECT stt FROM courses WHERE mamh = 'SE330'), 0), 
(104, 'SE334-Lab01', null, 'https://github.com/tathuythanh-vn/SE334-Lab01', null, 'SE334', 'SE334-Lab01', 'Java', true, (SELECT stt FROM courses WHERE mamh = 'SE334'), 0), 
(105, 'NET-SE310.P12-LapStore-ThiTH', 'Repository cho phần bài thi thực hành - ASP.NET & MVC môn Công nghệ .NET - SE310.P12 của thầy Nguyễn Trịnh Đông và cô Huỳnh Hồ Thị Mộng Trinh', 'https://github.com/UIT-KTPM-2022-3-22521474/NET-SE310.P12-LapStore-ThiTH', null, 'SE310', 'NET-SE310.P12-LapStore-ThiTH', 'CSS', true, (SELECT stt FROM courses WHERE mamh = 'SE310'), 0), 
(106, 'lab5_se332_taiwindcss', null, 'https://github.com/Ury479/lab5_se332_taiwindcss', null, 'SE332', 'lab5_se332_taiwindcss', 'Vue', true, (SELECT stt FROM courses WHERE mamh = 'SE332'), 0), 
(107, 'SE346_AppProject', 'Đây là repository đồ án của môn SE246_Lập trình trên thiết bị di động', 'https://github.com/thuanhuy2006/SE346_AppProject', null, 'SE346', 'SE346_AppProject', 'Dart', true, (SELECT stt FROM courses WHERE mamh = 'SE346'), 0), 
(108, 'IT003.P22-Data-Structures-Algorithms-UIT', 'i hate dsa', 'https://github.com/Naathan404/IT003.P22-Data-Structures-Algorithms-UIT', null, 'IT003', 'IT003.P22-Data-Structures-Algorithms-UIT', 'C++', true, (SELECT stt FROM courses WHERE mamh = 'IT003'), 0), 
(109, 'IT002.P26-Object-Oriented-Programming-UIT', 'store my documents for subject OOP at university', 'https://github.com/Naathan404/IT002.P26-Object-Oriented-Programming-UIT', null, 'IT002', 'IT002.P26-Object-Oriented-Programming-UIT', 'C++', true, (SELECT stt FROM courses WHERE mamh = 'IT002'), 0), 
(110, 'IT005.Q112-Introduction-to-Computer-Network-UIT', null, 'https://github.com/Naathan404/IT005.Q112-Introduction-to-Computer-Network-UIT', null, 'IT005', 'IT005.Q112-Introduction-to-Computer-Network-UIT', null, true, (SELECT stt FROM courses WHERE mamh = 'IT005'), 0), 
(111, 'IT004.Q18-Database-UIT', null, 'https://github.com/Naathan404/IT004.Q18-Database-UIT', null, 'IT004', 'IT004.Q18-Database-UIT', 'TSQL', true, (SELECT stt FROM courses WHERE mamh = 'IT004'), 0), 
(112, 'IT008.Q14-Visual-Programming-UIT', null, 'https://github.com/Naathan404/IT008.Q14-Visual-Programming-UIT', null, 'IT008', 'IT008.Q14-Visual-Programming-UIT', 'C#', true, (SELECT stt FROM courses WHERE mamh = 'IT008'), 0), 
(113, 'WebDev-SE347.P12-Bootstrap-JQuery', 'Repository cho phần bài tập 3 môn Công nghệ Web và ứng dụng - SE347.P12 của thầy Nguyễn Tấn Toàn và thầy Lê Văn Tuấn', 'https://github.com/UIT-KTPM-2022-3-22521474/WebDev-SE347.P12-Bootstrap-JQuery', null, 'SE347', 'WebDev-SE347.P12-Bootstrap-JQuery', 'HTML', true, (SELECT stt FROM courses WHERE mamh = 'SE347'), 0), 
(114, 'WebDev-SE347.P12-RestfulAPI', 'Repository cho phần bài tập 2 môn Công nghệ Web và ứng dụng - SE347.P12 của thầy Nguyễn Tấn Toàn và thầy Lê Văn Tuấn.', 'https://github.com/UIT-KTPM-2022-3-22521474/WebDev-SE347.P12-RestfulAPI', null, 'SE347', 'WebDev-SE347.P12-RestfulAPI', 'JavaScript', true, (SELECT stt FROM courses WHERE mamh = 'SE347'), 0), 
(115, 'Medicare', 'Repository cho phần ứng dụng đồ án cuối kì Medicare - môn Công nghệ Web và ứng dụng - SE347.P12 của thầy Nguyễn Tấn Toàn.', 'https://github.com/UIT-KTPM-2022-3-22521474/Medicare', null, 'SE347', 'Medicare', null, true, (SELECT stt FROM courses WHERE mamh = 'SE347'), 0), 
(116, 'Medicare', 'Repository cho phần ứng dụng đồ án cuối kì Medicare - môn Công nghệ Web và ứng dụng - SE347.P12 của thầy Nguyễn Tấn Toàn.', 'https://github.com/UIT-KTPM-2022-3-22521474/Medicare', null, 'SE347', 'Medicare', null, true, (SELECT stt FROM courses WHERE mamh = 'SE347'), 0), 
(117, 'Lab3-IT007', null, 'https://github.com/HarryLee02/Lab3-IT007', null, 'IT007', 'Lab3-IT007', 'C', true, (SELECT stt FROM courses WHERE mamh = 'IT007'), 0), 
(119, 'IT007-Lab-02', null, 'https://github.com/zerizennie/IT007-Lab-02', null, 'IT007', 'IT007-Lab-02', null, true, (SELECT stt FROM courses WHERE mamh = 'IT007'), 0), 
(120, 'IT007_O215_Lab', 'This repository contain all lab', 'https://github.com/TCkien/IT007_O215_Lab', null, 'IT007', 'IT007_O215_Lab', 'HTML', true, (SELECT stt FROM courses WHERE mamh = 'IT007'), 0), 
(121, 'UIT-SE356.M11-PurchaseManagement', 'Course project - UIT | Codebase for website managing purchasing products for retailers', 'https://github.com/ylantt/UIT-SE356.M11-PurchaseManagement', null, 'SE356', 'UIT-SE356.M11-PurchaseManagement', 'JavaScript', true, (SELECT stt FROM courses WHERE mamh = 'SE356'), 0), 
(122, 'UIT-IT003-DSA', null, 'https://github.com/Cao-Quang-Minh/UIT-IT003-DSA', null, 'IT003', 'UIT-IT003-DSA', 'C++', true, (SELECT stt FROM courses WHERE mamh = 'IT003'), 0), 
(123, 'IT003-DSA', 'Cấu trúc dữ liệu và giải thuật - Đại học Công nghệ Thông tin (UIT)', 'https://github.com/doxuantu110/IT003-DSA', null, 'IT003', 'IT003-DSA', null, true, (SELECT stt FROM courses WHERE mamh = 'IT003'), 1), 
(124, 'IT003-DSA', 'Cấu trúc dữ liệu và giải thuật - Đại học Công nghệ Thông tin (UIT)', 'https://github.com/doxuantu110/IT003-DSA', null, 'IT003', 'IT003-DSA', null, true, (SELECT stt FROM courses WHERE mamh = 'IT003'), 1), 
(125, 'IT002', 'OOP UIT', 'https://github.com/namphuong11/IT002', null, 'IT002', 'IT002', 'C++', true, (SELECT stt FROM courses WHERE mamh = 'IT002'), 0), 
(126, 'IT002', 'OOP UIT', 'https://github.com/namphuong11/IT002', null, 'IT002', 'IT002', 'C++', true, (SELECT stt FROM courses WHERE mamh = 'IT002'), 0), 
(128, 'dx9gf', 'Simple Directx9 Game framework for learning (SE102)', 'https://github.com/doqin/dx9gf', null, 'SE102', 'dx9gf', 'C++', true, (SELECT stt FROM courses WHERE mamh = 'SE102'), 1), 
(129, 'IT002-OOP-UIT', 'Lập trình hướng đối tượng (OOP) - UIT VNUHCM', 'https://github.com/normalman159/IT002-OOP-UIT', null, 'IT002', 'IT002-OOP-UIT', 'C++', true, (SELECT stt FROM courses WHERE mamh = 'IT002'), 1), 
(130, 'IT003.Q21.CTTN-W1-SortingReport', 'A report for an exercise in Data structure and Algorithm course at UIT', 'https://github.com/lftroq/IT003.Q21.CTTN-W1-SortingReport', null, 'IT003', 'IT003.Q21.CTTN-W1-SortingReport', 'Python', true, (SELECT stt FROM courses WHERE mamh = 'IT003'), 1), 
(131, 'Megaman-X3---UIT--SE102', 'Đồ án NMPT GAME - UIT - SE102 - Megaman X3 - HK5 ', 'https://github.com/txbac98/Megaman-X3---UIT--SE102', null, 'SE102', 'Megaman-X3---UIT--SE102', 'C++', true, (SELECT stt FROM courses WHERE mamh = 'SE102'), 1), 
(132, 'IT002-Object-Oriented-Programming-UIT-', null, 'https://github.com/quangcrazymen/IT002-Object-Oriented-Programming-UIT-', null, 'IT002', 'IT002-Object-Oriented-Programming-UIT-', 'C++', true, (SELECT stt FROM courses WHERE mamh = 'IT002'), 0), 
(133, 'IT002-Object-Oriented-Programming-UIT-', null, 'https://github.com/quangcrazymen/IT002-Object-Oriented-Programming-UIT-', null, 'IT002', 'IT002-Object-Oriented-Programming-UIT-', 'C++', true, (SELECT stt FROM courses WHERE mamh = 'IT002'), 0), 
(134, 'uit-go-se360', null, 'https://github.com/dieuxuanhien/uit-go-se360', null, 'SE360', 'uit-go-se360', 'TypeScript', true, (SELECT stt FROM courses WHERE mamh = 'SE360'), 0), 
(135, 'UIT-DataBase-IT004.O117-LAB', 'Thực Hành Cơ Sở Dữ Liệu UIT', 'https://github.com/Trtinssss/UIT-DataBase-IT004.O117-LAB', null, 'IT004', 'UIT-DataBase-IT004.O117-LAB', 'TSQL', true, (SELECT stt FROM courses WHERE mamh = 'IT004'), 0), 
(136, 'IT003', 'Nơi tập hợp deadline IT003 UIT', 'https://github.com/minhmap123/IT003', null, 'IT003', 'IT003', 'C++', true, (SELECT stt FROM courses WHERE mamh = 'IT003'), 0), 
(137, 'IT002.P11.CTTN', 'Tập hợp bài tập cho môn học Lập trình hướng đối tượng của UIT', 'https://github.com/minhmap123/IT002.P11.CTTN', null, 'IT002', 'IT002.P11.CTTN', 'C++', true, (SELECT stt FROM courses WHERE mamh = 'IT002'), 0), 
(140, 'IT003-Data-Structures-Algorithms', 'Toàn Bộ Bài Tập Thực Hành - Môn Cấu Trúc Dữ Liệu Và Giải Thuật (IT003) - Trường Đại Học Công Nghệ Thông Tin - ĐHQG.TPHCM (UIT).', 'https://github.com/quocthai912/IT003-Data-Structures-Algorithms', null, 'IT003', 'IT003-Data-Structures-Algorithms', 'C++', true, (SELECT stt FROM courses WHERE mamh = 'IT003'), 3), 
(142, 'UIT.IT008.Notepad', 'Đồ án môn học lập trình trực quan', 'https://github.com/hotrungnhan/UIT.IT008.Notepad', null, 'IT008', 'UIT.IT008.Notepad', 'C#', true, (SELECT stt FROM courses WHERE mamh = 'IT008'), 0), 
(143, 'it003-assignment-6', null, 'https://github.com/hoangnnh/it003-assignment-6', null, 'IT003', 'it003-assignment-6', 'C++', true, (SELECT stt FROM courses WHERE mamh = 'IT003'), 0), 
(144, 'IT003.Q21.CTTN_LAB_REPORT_1', 'báo cáo cách bài thực hành của lớp IT003.Q21.CTTN', 'https://github.com/BetterCuckChuck/IT003.Q21.CTTN_LAB_REPORT_1', null, 'IT003', 'IT003.Q21.CTTN_LAB_REPORT_1', 'Python', true, (SELECT stt FROM courses WHERE mamh = 'IT003'), 0), 
(145, 'UIT-IT004.P11.CTTN-Database', null, 'https://github.com/ckha1410/UIT-IT004.P11.CTTN-Database', null, 'IT004', 'UIT-IT004.P11.CTTN-Database', null, true, (SELECT stt FROM courses WHERE mamh = 'IT004'), 0), 
(146, 'MA004-CTRR', 'Cấu trúc rời rạc - Đại học Công nghệ Thông tin (UIT)', 'https://github.com/doxuantu110/MA004-CTRR', null, 'MA004', 'MA004-CTRR', null, true, (SELECT stt FROM courses WHERE mamh = 'MA004'), 0), 
(147, 'IT003', 'Tài liệu DSA của 24520557_UIT', 'https://github.com/h2okisme57/IT003', null, 'IT003', 'IT003', 'C++', true, (SELECT stt FROM courses WHERE mamh = 'IT003'), 0), 
(148, 'IT004_Database_Excercise_Teaching-Management', 'This is the code for SQL homework for the subject Database at UIT - enjoy it =)))', 'https://github.com/anhkiet1227/IT004_Database_Excercise_Teaching-Management', null, 'IT004', 'IT004_Database_Excercise_Teaching-Management', 'TSQL', true, (SELECT stt FROM courses WHERE mamh = 'IT004'), 2), 
(149, 'uit-eng03-healthy-breakfast', '[Presentation] | The Importance of Having a Healthy Breakfast', 'https://github.com/uit-25730067-chithanh/uit-eng03-healthy-breakfast', null, 'ENG03', 'uit-eng03-healthy-breakfast', 'Vue', true, (SELECT stt FROM courses WHERE mamh = 'ENG03'), 0), 
(150, 'UIT-SE347-WEB', 'Clothes E-commerce Web with Admin + User sides.', 'https://github.com/yang020501/UIT-SE347-WEB', null, 'SE347', 'UIT-SE347-WEB', 'JavaScript', true, (SELECT stt FROM courses WHERE mamh = 'SE347'), 0), 
(151, 'uit-limousine', 'A project for UIT SE214 Advanced Software Engineering', 'https://github.com/Huangphoux/uit-limousine', null, 'SE214', 'uit-limousine', 'JavaScript', true, (SELECT stt FROM courses WHERE mamh = 'SE214'), 0), 
(152, 'UIT-SE106-DTHT', 'Chuyển đổi ngôn ngữ đặc tả (formal specification) sang C# hoặc C++  ', 'https://github.com/yang020501/UIT-SE106-DTHT', null, 'SE106', 'UIT-SE106-DTHT', 'C#', true, (SELECT stt FROM courses WHERE mamh = 'SE106'), 1), 
(153, 'SE113.Lab3', 'Lab3', 'https://github.com/Winder510/SE113.Lab3', null, 'SE113', 'SE113.Lab3', 'JavaScript', true, (SELECT stt FROM courses WHERE mamh = 'SE113'), 0), 
(154, 'FurnitureStore', 'FurnitureStore for UIT.SE310.O12', 'https://github.com/trungkien2003ntk/FurnitureStore', null, 'SE310', 'FurnitureStore', 'C#', true, (SELECT stt FROM courses WHERE mamh = 'SE310'), 0), 
(155, 'Mobile-Assignment', 'Ninh Đức Quang Huy 24520688 Bài tập thực hành mobile lớp SE114.Q21', 'https://github.com/quanghuy-newbie/Mobile-Assignment', null, 'SE114', 'Mobile-Assignment', null, true, (SELECT stt FROM courses WHERE mamh = 'SE114'), 0), 
(157, 'SE347_Lab', null, 'https://github.com/Lghthien/SE347_Lab', null, 'SE347', 'SE347_Lab', 'JavaScript', true, (SELECT stt FROM courses WHERE mamh = 'SE347'), 0), 
(158, 'UIT-SE104-NMPTPM', 'E-Metro system application', 'https://github.com/yang020501/UIT-SE104-NMPTPM', null, 'SE104', 'UIT-SE104-NMPTPM', 'C#', true, (SELECT stt FROM courses WHERE mamh = 'SE104'), 0), 
(159, 'studMin', 'A Windows application used for high school management', 'https://github.com/phanxuanquang/studMin', null, 'SE104', 'studMin', 'C#', true, (SELECT stt FROM courses WHERE mamh = 'SE104'), 2), 
(160, 'se104-survey-system', 'A survey system for UIT SE104 class', 'https://github.com/thaiminh2022/se104-survey-system', null, 'SE104', 'se104-survey-system', 'TypeScript', true, (SELECT stt FROM courses WHERE mamh = 'SE104'), 0), 
(161, 'SE104.O29', 'Môn Nhập môn công nghệ phần mềm HK2 (2024) - UIT - VNUHCM', 'https://github.com/votrung654/SE104.O29', null, 'SE104', 'SE104.O29', null, true, (SELECT stt FROM courses WHERE mamh = 'SE104'), 0), 
(162, 'Chatbot_for_admissions', 'Final project of SE104 (Introduction to Software Engineering) of UIT', 'https://github.com/imneit06/Chatbot_for_admissions', null, 'SE104', 'Chatbot_for_admissions', 'HTML', true, (SELECT stt FROM courses WHERE mamh = 'SE104'), 0), 
(163, 'SE104-QLVMB-5', 'Airline Ticket Management Software: Streamlining Sales and Revenue for Airlines with Windows Forms (C#)', 'https://github.com/nv259/SE104-QLVMB-5', null, 'SE104', 'SE104-QLVMB-5', 'C#', true, (SELECT stt FROM courses WHERE mamh = 'SE104'), 2), 
(164, 'UiT.iT012.Assembly', 'Assembly language', 'https://github.com/katharsis7769/UiT.iT012.Assembly', null, 'IT012', 'UiT.iT012.Assembly', 'Assembly', true, (SELECT stt FROM courses WHERE mamh = 'IT012'), 0), 
(165, 'UIT-Go-Backend', 'For SE360 year end project', 'https://github.com/OneKeyCoder/UIT-Go-Backend', null, 'SE360', 'UIT-Go-Backend', 'Go', true, (SELECT stt FROM courses WHERE mamh = 'SE360'), 0), 
(166, 'MA005-XSTK', 'Xác suất thống kê - Đại học Công nghệ Thông tin (UIT)', 'https://github.com/doxuantu110/MA005-XSTK', null, 'MA005', 'MA005-XSTK', null, true, (SELECT stt FROM courses WHERE mamh = 'MA005'), 0), 
(167, 'UIT-IT002.Q26.2', null, 'https://github.com/hyugai/UIT-IT002.Q26.2', null, 'IT002', 'UIT-IT002.Q26.2', 'C++', true, (SELECT stt FROM courses WHERE mamh = 'IT002'), 0), 
(168, 'IT004---CSDL--UIT', 'Danh sách đề thi tham khảo - đáp án tham khảo của đề thi - đáp án tham khảo bài tập Lab', 'https://github.com/ThisIsHHanh/IT004---CSDL--UIT', null, 'IT004', 'IT004---CSDL--UIT', 'TSQL', true, (SELECT stt FROM courses WHERE mamh = 'IT004'), 0), 
(169, 'Java-QuanLyHocPhanOOP', 'Bài tập OOP Chương II - Quản lý học phần môn Ngôn ngữ lập trình Java - SE330.O21 của thầy Lê Thanh Trọng', 'https://github.com/UIT-KTPM-2022-3-22521474/Java-QuanLyHocPhanOOP', null, 'SE330', 'Java-QuanLyHocPhanOOP', 'Java', true, (SELECT stt FROM courses WHERE mamh = 'SE330'), 0), 
(171, 'IT007', 'UIT', 'https://github.com/dducktai/IT007', null, 'IT007', 'IT007', 'C++', true, (SELECT stt FROM courses WHERE mamh = 'IT007'), 5), 
(172, 'IT003', 'DSA - UIT', 'https://github.com/dducktai/IT003', null, 'IT003', 'IT003', 'C++', true, (SELECT stt FROM courses WHERE mamh = 'IT003'), 2), 
(173, 'IT003', 'DSA - UIT', 'https://github.com/dducktai/IT003', null, 'IT003', 'IT003', 'C++', true, (SELECT stt FROM courses WHERE mamh = 'IT003'), 2), 
(174, 'IT002-Object-Oriented-Programming-UIT', null, 'https://github.com/tvyyyy/IT002-Object-Oriented-Programming-UIT', null, 'IT002', 'IT002-Object-Oriented-Programming-UIT', 'C++', true, (SELECT stt FROM courses WHERE mamh = 'IT002'), 3), 
(175, 'UIT_IT004', 'Database - IT004', 'https://github.com/anhhuyluong/UIT_IT004', null, 'IT004', 'UIT_IT004', 'TSQL', true, (SELECT stt FROM courses WHERE mamh = 'IT004'), 0), 
(176, 'code_HDH_UIT', 'Lab IT007', 'https://github.com/huenguyentran/code_HDH_UIT', null, 'IT007', 'code_HDH_UIT', null, true, (SELECT stt FROM courses WHERE mamh = 'IT007'), 0), 
(178, 'IT007-Lab-05', null, 'https://github.com/zerizennie/IT007-Lab-05', null, 'IT007', 'IT007-Lab-05', null, true, (SELECT stt FROM courses WHERE mamh = 'IT007'), 0), 
(179, 'IT007-He_dieu_hanh', 'practice lab', 'https://github.com/luannd4869/IT007-He_dieu_hanh', null, 'IT007', 'IT007-He_dieu_hanh', null, true, (SELECT stt FROM courses WHERE mamh = 'IT007'), 0), 
(180, 'IT001-Of-Duong', 'Lưu code của nhập môn lập trình IT001 - UIT', 'https://github.com/duonguwu/IT001-Of-Duong', null, 'IT001', 'IT001-Of-Duong', 'C++', true, (SELECT stt FROM courses WHERE mamh = 'IT001'), 0), 
(181, 'IT001', 'Nhập môn lập trình - UIT', 'https://github.com/truong11062002/IT001', null, 'IT001', 'IT001', 'C++', true, (SELECT stt FROM courses WHERE mamh = 'IT001'), 0), 
(182, 'UIT_IT008_Visual_Programming', null, 'https://github.com/karasuma-2401/UIT_IT008_Visual_Programming', null, 'IT008', 'UIT_IT008_Visual_Programming', 'C#', true, (SELECT stt FROM courses WHERE mamh = 'IT008'), 0), 
(183, 'color-oop-project', 'A Final Project in IT002 (Object-oriented Programming) subject at UIT.', 'https://github.com/tannd-ds/color-oop-project', null, 'IT002', 'color-oop-project', 'C++', true, (SELECT stt FROM courses WHERE mamh = 'IT002'), 0), 
(184, 'uit.it001.snake-game', 'IT001 Introduction to Programming Project', 'https://github.com/uit-anhvuk13/uit.it001.snake-game', null, 'IT001', 'uit.it001.snake-game', 'C++', true, (SELECT stt FROM courses WHERE mamh = 'IT001'), 0), 
(185, 'Lab1-IT008', 'Bài tập thực hành Lab 1 Lập trình trực quan - UIT', 'https://github.com/DonThuanUIT-24521736/Lab1-IT008', null, 'IT008', 'Lab1-IT008', 'C#', true, (SELECT stt FROM courses WHERE mamh = 'IT008'), 0), 
(186, 'UIT-IT002.P11.CTTN-Object_Oriented_Programming', null, 'https://github.com/ckha1410/UIT-IT002.P11.CTTN-Object_Oriented_Programming', null, 'IT002', 'UIT-IT002.P11.CTTN-Object_Oriented_Programming', 'C++', true, (SELECT stt FROM courses WHERE mamh = 'IT002'), 0), 
(187, 'IT004', 'CSDL UIT', 'https://github.com/namphuong11/IT004', null, 'IT004', 'IT004', 'TSQL', true, (SELECT stt FROM courses WHERE mamh = 'IT004'), 0), 
(188, 'UIT-IT004', 'Practical exercises for subject IT004 at UIT', 'https://github.com/nqhhoang2002/UIT-IT004', null, 'IT004', 'UIT-IT004', null, true, (SELECT stt FROM courses WHERE mamh = 'IT004'), 0), 
(189, 'IT003-DSAA', 'it003 UIT exercises', 'https://github.com/boy2407/IT003-DSAA', null, 'IT003', 'IT003-DSAA', 'C++', true, (SELECT stt FROM courses WHERE mamh = 'IT003'), 0), 
(190, 'Bai5Lab2-UIT-IT002', 'Doing homework from UIT', 'https://github.com/OopsNooob/Bai5Lab2-UIT-IT002', null, 'IT002', 'Bai5Lab2-UIT-IT002', 'C++', true, (SELECT stt FROM courses WHERE mamh = 'IT002'), 0), 
(191, 'IT004_Lab', null, 'https://github.com/hoangnxm/IT004_Lab', null, 'IT004', 'IT004_Lab', 'TSQL', true, (SELECT stt FROM courses WHERE mamh = 'IT004'), 0), 
(192, 'IT004-CS115-UIT', null, 'https://github.com/NTGNguyen/IT004-CS115-UIT', null, 'IT004', 'IT004-CS115-UIT', 'TSQL', true, (SELECT stt FROM courses WHERE mamh = 'IT004'), 5), 
(194, 'Networking-LAB', 'This is all my IT005''s LAB project.', 'https://github.com/UITxWoodyNguyen/Networking-LAB', null, 'IT005', 'Networking-LAB', 'HTML', true, (SELECT stt FROM courses WHERE mamh = 'IT005'), 0), 
(195, 'se350_UITE-Learning_BE', null, 'https://github.com/troqphuc147/se350_UITE-Learning_BE', null, 'SE350', 'se350_UITE-Learning_BE', 'JavaScript', true, (SELECT stt FROM courses WHERE mamh = 'SE350'), 0), 
(196, 'QL_Giai_BD_QG_SE104_UIT', null, 'https://github.com/truonghoangkhiem/QL_Giai_BD_QG_SE104_UIT', null, 'SE104', 'QL_Giai_BD_QG_SE104_UIT', 'JavaScript', true, (SELECT stt FROM courses WHERE mamh = 'SE104'), 0), 
(197, 'SE347.O12.PMCL-LAB', null, 'https://github.com/ninehcobra/SE347.O12.PMCL-LAB', null, 'SE347', 'SE347.O12.PMCL-LAB', 'HTML', true, (SELECT stt FROM courses WHERE mamh = 'SE347'), 0), 
(198, 'uit_shield_demo', 'Simple scam/phishing email demo for my SS004', 'https://github.com/1zuki/uit_shield_demo', null, 'SS004', 'uit_shield_demo', 'Python', true, (SELECT stt FROM courses WHERE mamh = 'SS004'), 0), 
(200, 'SE347.L11_LAB3_18521314', null, 'https://github.com/tuongluong2000/SE347.L11_LAB3_18521314', null, 'SE347', 'SE347.L11_LAB3_18521314', null, true, (SELECT stt FROM courses WHERE mamh = 'SE347'), 0), 
(201, 'SE350M21_UITE-learning', 'E-learning', 'https://github.com/TranHoangdbz/SE350M21_UITE-learning', null, 'SE350', 'SE350M21_UITE-learning', 'JavaScript', true, (SELECT stt FROM courses WHERE mamh = 'SE350'), 0), 
(202, 'Lab-.NET', '[SE310] Công nghệ .NET - UIT', 'https://github.com/huynhanx03/Lab-.NET', null, 'SE310', 'Lab-.NET', 'C#', true, (SELECT stt FROM courses WHERE mamh = 'SE310'), 0), 
(203, 'ASP.NET-Core-Seminar', 'ASP.NET Core Seminar For UIT SE347', 'https://github.com/comaybay/ASP.NET-Core-Seminar', null, 'SE347', 'ASP.NET-Core-Seminar', 'C#', true, (SELECT stt FROM courses WHERE mamh = 'SE347'), 0), 
(204, 'Bai-Tap-Cong-Nghe-Web-Uit', 'SE347 - UIT', 'https://github.com/loia5tqd001/Bai-Tap-Cong-Nghe-Web-Uit', null, 'SE347', 'Bai-Tap-Cong-Nghe-Web-Uit', 'HTML', true, (SELECT stt FROM courses WHERE mamh = 'SE347'), 1), 
(205, 'UIT-SE350-CDE-Learning', 'ReactJs-NodeJs website E-Learning ', 'https://github.com/yang020501/UIT-SE350-CDE-Learning', null, 'SE350', 'UIT-SE350-CDE-Learning', 'JavaScript', true, (SELECT stt FROM courses WHERE mamh = 'SE350'), 0), 
(206, 'uit-SE357_requirement', null, 'https://github.com/Huangphoux/uit-SE357_requirement', null, 'SE357', 'uit-SE357_requirement', 'TypeScript', true, (SELECT stt FROM courses WHERE mamh = 'SE357'), 0), 
(207, 'uit-SE357_requirement', null, 'https://github.com/Huangphoux/uit-SE357_requirement', null, 'SE357', 'uit-SE357_requirement', 'TypeScript', true, (SELECT stt FROM courses WHERE mamh = 'SE357'), 0), 
(208, 'Java-BaiTapChuong2', 'Bài tập chương II OOP môn Ngôn ngữ lập trình Java - SE330.O21 của thầy Lê Thanh Trọng', 'https://github.com/UIT-KTPM-2022-3-22521474/Java-BaiTapChuong2', null, 'SE330', 'Java-BaiTapChuong2', 'Java', true, (SELECT stt FROM courses WHERE mamh = 'SE330'), 0), 
(209, 'Java-BaiTapChuong2', 'Bài tập chương II OOP môn Ngôn ngữ lập trình Java - SE330.O21 của thầy Lê Thanh Trọng', 'https://github.com/UIT-KTPM-2022-3-22521474/Java-BaiTapChuong2', null, 'SE330', 'Java-BaiTapChuong2', 'Java', true, (SELECT stt FROM courses WHERE mamh = 'SE330'), 0), 
(211, 'Cryptography-Assignment', 'A cryptohack challenge a day keeps bad grades away - IT003.O21.CTTN', 'https://github.com/san601/Cryptography-Assignment', null, 'IT003', 'Cryptography-Assignment', 'Python', true, (SELECT stt FROM courses WHERE mamh = 'IT003'), 0), 
(213, 'IT001-NMLT-UIT-2025', 'cho sinh vien UIT nam nhat', 'https://github.com/Psgekkouga/IT001-NMLT-UIT-2025', null, 'IT001', 'IT001-NMLT-UIT-2025', null, true, (SELECT stt FROM courses WHERE mamh = 'IT001'), 0), 
(214, 'uit-assignment', 'Archive of assignments when studying at UIT', 'https://github.com/tuilakhanh-s-Stuff/uit-assignment', null, 'IT001', 'uit-assignment', 'C++', true, (SELECT stt FROM courses WHERE mamh = 'IT001'), 6), 
(215, 'IT004.Q17', 'IT004.Q17 - Cơ sở dữ liệu - UIT - VNUHCM', 'https://github.com/AIVIETNAM-AIO-dthphuc/IT004.Q17', null, 'IT004', 'IT004.Q17', null, true, (SELECT stt FROM courses WHERE mamh = 'IT004'), 0), 
(216, 'NMLT_UIT_wecode', 'Wecode assignment IT001', 'https://github.com/yunaLee21/NMLT_UIT_wecode', null, 'IT001', 'NMLT_UIT_wecode', 'C++', true, (SELECT stt FROM courses WHERE mamh = 'IT001'), 0), 
(217, 'IT005-NMM', 'Nhập môn Mạng máy tính - Đại học Công nghệ Thông tin (UIT) ', 'https://github.com/doxuantu110/IT005-NMM', null, 'IT005', 'IT005-NMM', null, true, (SELECT stt FROM courses WHERE mamh = 'IT005'), 0), 
(219, 'IT008-LAB02', null, 'https://github.com/UHUUITINHT/IT008-LAB02', null, 'IT008', 'IT008-LAB02', 'C#', true, (SELECT stt FROM courses WHERE mamh = 'IT008'), 0), 
(220, 'UIT-SE330.O21-practice-java', null, 'https://github.com/th1enlm02/UIT-SE330.O21-practice-java', null, 'SE330', 'UIT-SE330.O21-practice-java', 'Java', true, (SELECT stt FROM courses WHERE mamh = 'SE330'), 0), 
(221, 'IT005', 'Nhap mon mang may tinh UIT', 'https://github.com/locluclak/IT005', null, 'IT005', 'IT005', 'HTML', true, (SELECT stt FROM courses WHERE mamh = 'IT005'), 0), 
(222, 'UIT-Thuc-Hanh-IT004', 'Hướng dẫn giải bài tập thực hành csdl(lời giải chỉ mang tính chất tham khảo nếu phát hiện sai sót có thể nhắn tin giúp mình chỉnh lại nha)', 'https://github.com/SonRobo/UIT-Thuc-Hanh-IT004', null, 'IT004', 'UIT-Thuc-Hanh-IT004', null, true, (SELECT stt FROM courses WHERE mamh = 'IT004'), 0), 
(223, 'IT004_LAB', 'Những bài lab của môn CSDL', 'https://github.com/TrThuyTien/IT004_LAB', null, 'IT004', 'IT004_LAB', 'TSQL', true, (SELECT stt FROM courses WHERE mamh = 'IT004'), 0), 
(224, 'UIT_NhapMonMangMayTinh-IT005.O123-LAB', 'Nhập môn mạng máy tính', 'https://github.com/Trtinssss/UIT_NhapMonMangMayTinh-IT005.O123-LAB', null, 'IT005', 'UIT_NhapMonMangMayTinh-IT005.O123-LAB', null, true, (SELECT stt FROM courses WHERE mamh = 'IT005'), 0), 
(226, 'SE347_Lab', null, 'https://github.com/XuanThao03/SE347_Lab', null, 'SE347', 'SE347_Lab', 'HTML', true, (SELECT stt FROM courses WHERE mamh = 'SE347'), 0), 
(229, 'UIT-IT008-LTTQ', 'Phần mềm quản lý Nhân - Hộ Khẩu', 'https://github.com/yang020501/UIT-IT008-LTTQ', null, 'IT008', 'UIT-IT008-LTTQ', 'C#', true, (SELECT stt FROM courses WHERE mamh = 'IT008'), 0), 
(230, 'UIT_SE346', null, 'https://github.com/thienngann/UIT_SE346', null, 'SE346', 'UIT_SE346', 'JavaScript', true, (SELECT stt FROM courses WHERE mamh = 'SE346'), 0), 
(231, 'UIT_SE346', null, 'https://github.com/thienngann/UIT_SE346', null, 'SE346', 'UIT_SE346', 'JavaScript', true, (SELECT stt FROM courses WHERE mamh = 'SE346'), 0), 
(233, 'SE346_Lab', 'SE346 exercises', 'https://github.com/nbminh24/SE346_Lab', null, 'SE346', 'SE346_Lab', 'JavaScript', true, (SELECT stt FROM courses WHERE mamh = 'SE346'), 0), 
(234, 'SE346_Lab', 'SE346 exercises', 'https://github.com/nbminh24/SE346_Lab', null, 'SE346', 'SE346_Lab', 'JavaScript', true, (SELECT stt FROM courses WHERE mamh = 'SE346'), 0), 
(235, 'UIT.SE114.Company-Management', null, 'https://github.com/hotrungnhan/UIT.SE114.Company-Management', null, 'SE114', 'UIT.SE114.Company-Management', 'Kotlin', true, (SELECT stt FROM courses WHERE mamh = 'SE114'), 0), 
(236, 'SE347_LAB_7_8', null, 'https://github.com/kazei1211/SE347_LAB_7_8', null, 'SE347', 'SE347_LAB_7_8', 'HTML', true, (SELECT stt FROM courses WHERE mamh = 'SE347'), 1), 
(237, 'SE347_LAB_7_8', null, 'https://github.com/kazei1211/SE347_LAB_7_8', null, 'SE347', 'SE347_LAB_7_8', 'HTML', true, (SELECT stt FROM courses WHERE mamh = 'SE347'), 1), 
(238, 'UIT_OCM', 'UIT SE114 Final Project - Group: 3Guyz', 'https://github.com/khaph/UIT_OCM', null, 'SE114', 'UIT_OCM', 'Java', true, (SELECT stt FROM courses WHERE mamh = 'SE114'), 0), 
(240, 'UIT-SE301.M11-BlockCommerce', null, 'https://github.com/Thomg102/UIT-SE301.M11-BlockCommerce', null, 'SE301', 'UIT-SE301.M11-BlockCommerce', 'JavaScript', true, (SELECT stt FROM courses WHERE mamh = 'SE301'), 0), 
(241, 'IT007_UIT', 'Course note for IT007 at UIT', 'https://github.com/chisphung/IT007_UIT', null, 'IT007', 'IT007_UIT', 'TypeScript', true, (SELECT stt FROM courses WHERE mamh = 'IT007'), 2), 
(242, 'Dungeon_Escape_Game', 'A game project for UIT class SE114.L22.PMCL', 'https://github.com/Moonbanner/Dungeon_Escape_Game', null, 'SE114', 'Dungeon_Escape_Game', 'ASP.NET', true, (SELECT stt FROM courses WHERE mamh = 'SE114'), 0), 
(244, 'IT001_NhapMonLapTrinhUIT', null, 'https://github.com/Mercury9999/IT001_NhapMonLapTrinhUIT', null, 'IT001', 'IT001_NhapMonLapTrinhUIT', 'C++', true, (SELECT stt FROM courses WHERE mamh = 'IT001'), 0), 
(245, 'IT001_NhapMonLapTrinhUIT', null, 'https://github.com/Mercury9999/IT001_NhapMonLapTrinhUIT', null, 'IT001', 'IT001_NhapMonLapTrinhUIT', 'C++', true, (SELECT stt FROM courses WHERE mamh = 'IT001'), 0), 
(247, 'IT001', 'Lab06.01 UIT IT001', 'https://github.com/duyyeubongchuyen/IT001', null, 'IT001', 'IT001', 'C++', true, (SELECT stt FROM courses WHERE mamh = 'IT001'), 0), 
(248, 'SE360-UIT_Go', null, 'https://github.com/khoilv2005/SE360-UIT_Go', null, 'SE360', 'SE360-UIT_Go', 'JavaScript', true, (SELECT stt FROM courses WHERE mamh = 'SE360'), 1), 
(249, 'IT008TH', 'Mini Project Thực hành cuối kỳ Lập trình trực quan IT008 UIT', 'https://github.com/PhuThien2005/IT008TH', null, 'IT008', 'IT008TH', null, true, (SELECT stt FROM courses WHERE mamh = 'IT008'), 0), 
(250, 'MasterLibrary', 'Đồ án của môn Lập trình trực quan - IT008 - UIT - Quản lý thư viện', 'https://github.com/HuynhNhan0330/MasterLibrary', null, 'IT008', 'MasterLibrary', 'C#', true, (SELECT stt FROM courses WHERE mamh = 'IT008'), 1), 
(251, 'IT001_UIT', 'Code for my college', 'https://github.com/LowTechTurtle/IT001_UIT', null, 'IT001', 'IT001_UIT', 'C++', true, (SELECT stt FROM courses WHERE mamh = 'IT001'), 0), 
(252, 'Snake-Game', '[UIT-SS004.P12] Đồ án môn học SS004.P12 được thực hiện bởi Nhóm xx', 'https://github.com/ckietlam/Snake-Game', null, 'SS004', 'Snake-Game', null, true, (SELECT stt FROM courses WHERE mamh = 'SS004'), 0), 
(253, 'Snake', 'Bài tập SS004.9 môn Kĩ năng Nghề nghiệp UIT', 'https://github.com/25520509-25521798-25521416/Snake', null, 'SS004', 'Snake', 'C++', true, (SELECT stt FROM courses WHERE mamh = 'SS004'), 0), 
(254, 'se102-mario', 'A Mario clone using DirectX for class (SE102 VNUHCM-UIT) ', 'https://github.com/thaiminh2022/se102-mario', null, 'SE102', 'se102-mario', 'C++', true, (SELECT stt FROM courses WHERE mamh = 'SE102'), 2), 
(255, 'SS004_Tetris_Project_Nhom9', 'Đồ án cuối kì môn Kỹ năng nghề nghiệp - Tetris game (SS004 - UIT)', 'https://github.com/23730050TranHuynhThien/SS004_Tetris_Project_Nhom9', null, 'SS004', 'SS004_Tetris_Project_Nhom9', 'C++', true, (SELECT stt FROM courses WHERE mamh = 'SS004'), 0), 
(256, '-SE104.L23', 'A project for [SE104.L23]Software engineering - UIT', 'https://github.com/Anhvu210103/-SE104.L23', null, 'SE104', '-SE104.L23', null, true, (SELECT stt FROM courses WHERE mamh = 'SE104'), 1), 
(257, 'IT005---NHAP-MON-MANG-MAY-TINH---UIT', null, 'https://github.com/ThisIsHHanh/IT005---NHAP-MON-MANG-MAY-TINH---UIT', null, 'IT005', 'IT005---NHAP-MON-MANG-MAY-TINH---UIT', 'HTML', true, (SELECT stt FROM courses WHERE mamh = 'IT005'), 0), 
(259, 'uit-se104-project-frontend', 'Đồ án môn học Nhập môn công nghệ phần mềm - Frontend', 'https://github.com/phuctinh542001/uit-se104-project-frontend', null, 'SE104', 'uit-se104-project-frontend', 'TypeScript', true, (SELECT stt FROM courses WHERE mamh = 'SE104'), 0), 
(260, 'UIT_IT001_Introduction_to_Programming', 'Some my codes do at UIT', 'https://github.com/karasuma-2401/UIT_IT001_Introduction_to_Programming', null, 'IT001', 'UIT_IT001_Introduction_to_Programming', 'C++', true, (SELECT stt FROM courses WHERE mamh = 'IT001'), 1), 
(261, 'IT003-DSA-Lab', 'Store DSA assignments.', 'https://github.com/SangHq005/IT003-DSA-Lab', null, 'IT003', 'IT003-DSA-Lab', null, true, (SELECT stt FROM courses WHERE mamh = 'IT003'), 1), 
(262, 'functional-dependency', 'A small web app for the IT004-Database course in UIT-VNHCM', 'https://github.com/thuanvonb/functional-dependency', null, 'IT004', 'functional-dependency', 'JavaScript', true, (SELECT stt FROM courses WHERE mamh = 'IT004'), 0), 
(263, 'ENG03', 'ENG03 - Anh văn 3 (cô Thy & cô Tiên)', 'https://github.com/KevMi-UIT/ENG03', null, 'ENG03', 'ENG03', 'HTML', true, (SELECT stt FROM courses WHERE mamh = 'ENG03'), 0), 
(264, '24521736_Lab5_IT008', 'Bài tập thực hành Lab 5 Lập trình trực quan', 'https://github.com/DonThuanUIT-24521736/24521736_Lab5_IT008', null, 'IT008', '24521736_Lab5_IT008', 'C#', true, (SELECT stt FROM courses WHERE mamh = 'IT008'), 0), 
(265, 'IT012', 'To chuc cau truc may tinh UIT', 'https://github.com/locluclak/IT012', null, 'IT012', 'IT012', 'Assembly', true, (SELECT stt FROM courses WHERE mamh = 'IT012'), 0), 
(266, 'IT002-OOP', 'Lập trình hướng đối tượng - Đại học Công nghệ Thông tin (UIT)', 'https://github.com/doxuantu110/IT002-OOP', null, 'IT002', 'IT002-OOP', 'C++', true, (SELECT stt FROM courses WHERE mamh = 'IT002'), 0) ON CONFLICT (id) DO NOTHING;