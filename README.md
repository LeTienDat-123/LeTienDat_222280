CHƯƠNG 1 : GIỚI THIỆU

1.1  Giới thiệu tổng quan tình hình đề tài	
Trong bối cảnh đô thị hóa diễn ra mạnh mẽ tại Việt Nam, đặc biệt ở các thành phố lớn như TP. Hồ Chí Minh và Hà Nội, nhà chung cư đã trở thành hình thức nhà ở phổ biến và chủ đạo đối với người dân. Theo các báo cáo gần đây, số lượng chung cư tăng nhanh chóng, tuy nhiên công tác quản lý vận hành vẫn tồn tại nhiều bất cập lớn. Các vấn đề phổ biến bao gồm: thiếu minh bạch trong thu chi quỹ bảo trì (2%), mâu thuẫn giữa cư dân - ban quản trị - chủ đầu tư, quản lý hợp đồng thuê và hóa đơn thủ công dễ sai sót, theo dõi bảo trì căn hộ chậm trễ, khó khăn trong việc thống kê tình trạng sử dụng căn hộ, doanh thu và báo cáo tổng quan.
Hiện nay, phần lớn các chung cư vẫn áp dụng phương pháp quản lý truyền thống (sổ sách giấy, Excel riêng lẻ hoặc phần mềm nước ngoài đắt đỏ, không tùy chỉnh phù hợp). Điều này dẫn đến hiệu quả thấp, mất thời gian, dễ phát sinh tranh chấp pháp lý và ảnh hưởng đến chất lượng cuộc sống của cư dân. Việc ứng dụng công nghệ thông tin, đặc biệt là phát triển phần mềm quản lý chuyên dụng, được xem là giải pháp cần thiết để số hóa quy trình, tăng tính minh bạch, giảm thiểu sai sót và nâng cao hiệu quả quản lý.
Đề tài “Xây dựng hệ thống quản lý chung cư bằng Java Swing” ra đời nhằm giải quyết một phần các hạn chế trên. Hệ thống được thiết kế với giao diện thân thiện, dễ sử dụng, tập trung vào các chức năng cốt lõi như quản lý tòa nhà - căn hộ - cư dân - hợp đồng - hóa đơn - bảo trì, thống kê báo cáo và theo dõi hoạt động người dùng. Sử dụng công nghệ Java Swing kết hợp cơ sở dữ liệu (thường là MySQL hoặc SQLite), hệ thống hướng đến đối tượng là ban quản lý chung cư quy mô vừa và nhỏ, giúp tự động hóa quy trình, hỗ trợ ra quyết định nhanh chóng dựa trên dữ liệu thực tế.


1.2 Mục tiêu đề tài
Mục tiêu tổng quát của đề tài là xây dựng một hệ thống phần mềm ứng dụng desktop (sử dụng ngôn ngữ lập trình Java với giao diện Swing) hỗ trợ tự động hóa các quy trình quản lý hành chính, tài chính và cư dân tại các chung cư quy mô vừa và nhỏ, từ đó nâng cao hiệu quả vận hành, tăng tính minh bạch, giảm thiểu tranh chấp và hỗ trợ ban quản lý ra quyết định nhanh chóng dựa trên dữ liệu thực tế.
Mục tiêu cụ thể bao gồm các nội dung sau:
1.	Tự động hóa quy trình quản lý cốt lõi: Phát triển các chức năng chính như đăng nhập/đăng ký/quên mật khẩu, quản lý tòa nhà - tầng - căn hộ, quản lý thông tin gia đình cư dân, quản lý hợp đồng thuê (tạo, xem, chấm dứt), quản lý hóa đơn dịch vụ, quản lý yêu cầu bảo trì, quản lý thẻ an ninh và quản lý người dùng hệ thống. Hệ thống giúp thay thế các phương pháp thủ công (sổ sách, Excel) bằng quy trình số hóa, đảm bảo dữ liệu nhất quán và giảm lỗi nhập liệu.
2.	Cung cấp giao diện trực quan và dễ sử dụng: Thiết kế dashboard chính hiển thị phân cấp tòa nhà → tầng → căn hộ với trạng thái realtime (trống/đang thuê/bảo trì), kết hợp thống kê nhanh (tỷ lệ lấp đầy, hóa đơn chưa thanh toán, yêu cầu bảo trì chờ xử lý). Giao diện Swing thân thiện, hỗ trợ tiếng Việt đầy đủ, phù hợp với nhân viên ban quản lý không chuyên về công nghệ thông tin.
3.	Hỗ trợ thống kê, báo cáo và theo dõi hoạt động: Xây dựng các báo cáo chi tiết về tình trạng căn hộ, hợp đồng, hóa đơn, doanh thu tổng quan và lịch sử hoạt động (log) của người dùng. Mục tiêu là cung cấp công cụ phân tích dữ liệu giúp ban quản lý nắm bắt tình hình nhanh chóng, dự báo xu hướng (ví dụ: căn hộ trống nhiều → điều chỉnh chính sách) và tăng tính minh bạch trong quản lý.
4.	Đảm bảo tính bảo mật, toàn vẹn dữ liệu và dễ bảo trì: Áp dụng phân quyền người dùng, mã hóa mật khẩu, ghi log hoạt động, cùng với thiết kế cơ sở dữ liệu quan hệ (MySQL/SQLite) đạt chuẩn 3NF và các ràng buộc toàn vẹn để bảo vệ thông tin cá nhân cư dân, hợp đồng và tài chính. Hệ thống được xây dựng theo kiến trúc phân lớp (MVC hoặc 3-layer), dễ mở rộng và bảo trì trong tương lai.
5.	Đóng góp vào việc ứng dụng công nghệ thông tin trong quản lý chung cư: Đề tài hướng đến việc cung cấp một giải pháp phần mềm mã nguồn mở (hoặc tùy chỉnh) chi phí thấp, phù hợp với các chung cư quy mô vừa và nhỏ tại Việt Nam, góp phần cải thiện chất lượng quản lý vận hành theo hướng hiện đại, chuyên nghiệp, đồng thời làm nền tảng cho các phát triển tiếp theo (web hóa, tích hợp mobile).


1.3  Đối tượng và phạm vi của đề tài

1.3.1 Đối tượng 
Đối tượng chính của đề tài là hệ thống quản lý và vận hành nhà chung cư (Apartment Management System), tập trung vào các hoạt động quản lý nội bộ của ban quản lý (BQL) chung cư. Cụ thể bao gồm:
•	Các thực thể chính: tòa nhà, tầng, căn hộ, cư dân (người thuê/đại diện gia đình), hợp đồng thuê, hóa đơn dịch vụ, yêu cầu bảo trì, thẻ an ninh.
•	Các quy trình nghiệp vụ: đăng nhập/đăng ký người dùng, tạo/chấm dứt hợp đồng, theo dõi bảo trì, tính toán hóa đơn, thống kê mức độ sử dụng căn hộ, doanh thu, báo cáo tổng quan.
•	Người sử dụng hệ thống: chủ yếu là nhân viên ban quản lý (quản trị viên, nhân viên hành chính, kế toán), và một phần hỗ trợ xem thông tin cá nhân/lịch sử hoạt động cho cư dân (nếu đăng nhập).
Đề tài không tập trung vào hệ thống quản lý kỹ thuật tòa nhà (BMS - Building Management System) như điều khiển thang máy, điện nước, PCCC, mà chủ yếu giải quyết khía cạnh quản lý hành chính - tài chính - cư dân.

1.3.2 Phạm vi
Phạm vi nghiên cứu và triển khai của đề tài được giới hạn như sau:
•	Phạm vi chức năng: Hệ thống hỗ trợ đầy đủ các chức năng chính sau (dựa trên yêu cầu đề tài):
- Đăng nhập / đăng ký / quên mật khẩu.
- Màn hình chính hiển thị danh sách tòa nhà, tầng và căn hộ thuộc tầng được chọn.
- Thao tác trên căn hộ: tạo hợp đồng, ghi nhận bảo trì, xem hóa đơn, chấm dứt hợp đồng.
- Hiển thị thống kê mức độ sử dụng căn hộ (tỷ lệ lấp đầy, trống…) ngay tại dashboard chính.
- Thống kê tổng quan: số lượng căn hộ, hóa đơn, hợp đồng, doanh thu.
- Quản lý tòa nhà, quản lý căn hộ, quản lý người dùng (nhân viên BQL), quản lý thông tin gia đình cư dân.
- Quản lý hợp đồng thuê, quản lý thẻ an ninh, quản lý yêu cầu bảo trì.
- Các báo cáo: báo cáo căn hộ (tình trạng sử dụng), hóa đơn, hợp đồng.
- Hiển thị thông tin cá nhân người dùng và lịch sử hoạt động (log) trong hệ thống.
•	Phạm vi công nghệ: Sử dụng ngôn ngữ lập trình Java với giao diện đồ họa Swing, kết hợp cơ sở dữ liệu quan hệ (MySQL/SQLite), không hỗ trợ web hoặc mobile (chỉ ứng dụng desktop).
•	Phạm vi đối tượng áp dụng: Phù hợp với các chung cư quy mô vừa và nhỏ (dưới 500 căn hộ), do một ban quản lý duy nhất vận hành. Không bao gồm tích hợp thanh toán online, ứng dụng cư dân di động, hay quản lý nhiều tòa nhà phân tán ở các tỉnh thành khác nhau.
•	Giới hạn: Không triển khai tính năng quản lý tài sản chung (quỹ bảo trì 2%), tích hợp camera/an ninh vật lý, hay xử lý tranh chấp pháp lý.

1.4 Các bước thực hiện 
Để hoàn thành đồ án, các bước thực hiện được tiến hành theo quy trình phát triển phần mềm cơ bản, bao gồm:
1.	Nghiên cứu và phân tích yêu cầu:
1.	Tìm hiểu thực trạng quản lý chung cư tại Việt Nam.
2.	Thu thập yêu cầu từ danh sách chức năng, phỏng vấn người dùng tiềm năng (nếu có).
3.	Xây dựng mô hình Use Case, mô hình dữ liệu (ERD), phân tích chức năng chi tiết.
2.	Thiết kế hệ thống :
1.	Thiết kế cơ sở dữ liệu (bảng Tòa nhà, Căn hộ, Cư dân, Hợp đồng, Hóa đơn, Bảo trì, Người dùng…).
2.	Thiết kế giao diện người dùng (UI) bằng Java Swing (các màn hình chính, form nhập liệu, bảng hiển thị, dashboard).
3.	Thiết kế kiến trúc phần mềm (MVC hoặc 3-layer: Presentation – Business – Data).
3.	Triển khai và lập trình:
1.	Xây dựng module đăng nhập/đăng ký/quên mật khẩu.
2.	Phát triển màn hình chính (dashboard) với thống kê và danh sách tòa-tầng-căn hộ.
3.	Lập trình các module quản lý: tòa nhà, căn hộ, người dùng, hợp đồng, bảo trì, thẻ an ninh, hóa đơn.
4.	Xây dựng chức năng báo cáo và thống kê (sử dụng bảng, biểu đồ đơn giản bằng JFreeChart nếu có).
5.	Xử lý lịch sử hoạt động (log) và thông tin cá nhân.
4.	Kiểm thử và hoàn thiện:
1.	Kiểm thử đơn vị (unit test), kiểm thử tích hợp và kiểm thử hệ thống.
2.	Sửa lỗi, tối ưu giao diện, hiệu suất.
3.	Viết tài liệu hướng dẫn sử dụng, báo cáo đồ án.

5.	Đánh giá và kết luận:
1.	Đánh giá ưu nhược điểm hệ thống.
2.	Đề xuất hướng phát triển tiếp theo (web hóa, tích hợp mobile, thêm tính năng nâng cao).



