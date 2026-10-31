# BÁO CÁO TIẾN ĐỘ ĐỒ ÁN NT118

**ĐẠI HỌC QUỐC GIA THÀNH PHỐ HỒ CHÍ MINH**

**TRƯỜNG ĐẠI HỌC CÔNG NGHỆ THÔNG TIN**

**KHOA MẠNG MÁY TÍNH VÀ TRUYỀN THÔNG**

<p align="center"><img src="assets/logo-uit.jpg" width="180" alt="Logo Trường Đại học Công nghệ Thông tin" /></p>

**TRIPTOGETHER — ỨNG DỤNG LẬP KẾ HOẠCH DU LỊCH NHÓM**

- Môn học: Phát triển ứng dụng trên thiết bị di động.
- GVHD: [Điền họ tên giảng viên].
- Lớp: [Điền mã lớp NT118].
- Nhóm: [Điền số nhóm].

| STT | Họ và tên | MSSV |
| --- | --- | --- |
| 1 | [Họ và tên thành viên] | [MSSV] |

TPHCM, tháng 10 năm 2026.

> Báo cáo trình bày phiên bản TripTogether tại mã nguồn `633e50d`, theo cấu trúc mẫu NT118 được cung cấp [[7]](#tai-lieu-7). Các ảnh minh họa được lấy từ bộ kiểm tra giao diện cục bộ; kết quả kiểm thử và những phần cần kiểm chứng thêm được nêu trong mục 3.2. Bản Markdown được biên tập ngày 30/10/2026 để đọc trên GitHub; bản PDF A4 được lưu riêng để in.

> **Quy ước trình bày:** Bảng và hình được đánh số theo chương, chẳng hạn Bảng 2.1 là bảng thứ nhất của Chương 2. Ký hiệu [n] dẫn đến tài liệu tham khảo ở cuối báo cáo; FR01–FR10 là mã yêu cầu chức năng, E01–E12 là mã kịch bản nghiệm thu.

## MỤC LỤC

- [Chương 1. Giới thiệu.](#muc-1)
  - [1.1. Lý do chọn đề tài.](#muc-1-1)
  - [1.2. Mục tiêu.](#muc-1-2)
- [Chương 2. Cơ sở lý thuyết.](#muc-2)
  - [2.1. Ngôn ngữ và phần mềm sử dụng.](#muc-2-1)
    - [2.1.1. Ngôn ngữ sử dụng.](#muc-2-1-1)
    - [2.1.2. Phần mềm sử dụng.](#muc-2-1-2)
  - [2.2. Các tính năng của ứng dụng.](#muc-2-2)
  - [2.3. Giao diện của Ứng dụng TripTogether.](#muc-2-3)
- [Chương 3. Phân tích thiết kế hệ thống.](#muc-3)
  - [3.1. Sơ đồ phân rã chức năng.](#muc-3-1)
  - [3.2. Cơ sở dữ liệu.](#muc-3-2)

<details>
<summary>Danh mục bảng và hình</summary>

**Danh mục bảng**

- [Bảng 1.1. Phạm vi chức năng](#bang-1-1)
- [Bảng 1.2. Thông tin kỹ thuật của phiên bản báo cáo](#bang-1-2)
- [Bảng 2.1. Phiên bản trích từ cấu hình dự án](#bang-2-1)
- [Bảng 2.2. Danh mục yêu cầu chức năng](#bang-2-2)
- [Bảng 2.3. Màn hình và thao tác chính](#bang-2-3)
- [Bảng 3.1. Các lớp chính và trách nhiệm](#bang-3-1)
- [Bảng 3.2. Hồ sơ người dùng tại `users/{uid}`](#bang-3-2)
- [Bảng 3.3. Chuyến đi tại `trips/{creatorUid}/{tripId}`](#bang-3-3)
- [Bảng 3.4. Các node quyền và tham chiếu](#bang-3-4)
- [Bảng 3.5. Các trường dùng chung cho chat và file](#bang-3-5)
- [Bảng 3.6. Quyền theo vai trò của một trip](#bang-3-6)
- [Bảng 3.7. Phân bố dữ liệu và thời gian tồn tại](#bang-3-7)
- [Bảng 3.8. Kết quả có bằng chứng cục bộ](#bang-3-8)
- [Bảng 3.9. Ma trận nghiệm thu ba tài khoản](#bang-3-9)
- [Bảng 3.10. File kiểm chứng theo chủ đề](#bang-3-10)

**Danh mục hình**

- [Hình 2.1. Màn chọn xác thực và Home với dữ liệu minh họa](#hinh-2-1)
- [Hình 2.2. Yêu thích và tổng quan chuyến đi](#hinh-2-2)
- [Hình 2.3. Account và FAQ khi chạy HelpSupportActivity](#hinh-2-3)
- [Hình 2.4. Kiểm tra night mode với fontScale 1.3](#hinh-2-4)
- [Hình 2.5. Giao diện tạo chuyến đi và lịch trình; ảnh render cục bộ](#hinh-2-5)
- [Hình 2.6. Giao diện quản lý thành viên; ảnh render cục bộ](#hinh-2-6)
- [Hình 2.7. Giao diện chat và thư viện nội dung; ảnh render cục bộ](#hinh-2-7)
- [Hình 2.8. Giao diện ảnh và tài liệu PDF; ảnh render cục bộ](#hinh-2-8)
- [Hình 2.9. Giao diện xem ảnh và bản đồ; ảnh render cục bộ](#hinh-2-9)
- [Hình 2.10. Giao diện hồ sơ và tìm kiếm; ảnh render cục bộ](#hinh-2-10)
- [Hình 2.11. Giao diện cài đặt ban ngày và thông báo; ảnh render cục bộ](#hinh-2-11)
- [Hình 2.12. Giao diện đăng nhập và khôi phục mật khẩu; ảnh render cục bộ](#hinh-2-12)
- [Hình 3.1. Các nhóm chức năng của TripTogether](#hinh-3-1)
- [Hình 3.2. Kiến trúc ứng dụng và các dịch vụ bên ngoài](#hinh-3-2)
- [Hình 3.3. Quan hệ logic giữa tài khoản chuyến đi và nội dung](#hinh-3-3)
- [Hình 3.4. Các bước lưu file và nhánh phục hồi khi metadata lỗi](#hinh-3-4)

</details>

<a id="muc-1"></a>

## Chương 1. Giới thiệu.

<a id="muc-1-1"></a>

### 1.1. Lý do chọn đề tài.

Khi chuẩn bị một chuyến đi theo nhóm, mọi người thường trao đổi lịch trình qua tin nhắn, ghi chi phí trong ghi chú và lưu ảnh hoặc tài liệu ở nhiều nơi. Cách làm này khá thuận tiện lúc đầu, nhưng khi kế hoạch thay đổi, các thành viên dễ theo dõi nhầm thông tin hoặc bỏ sót nội dung cần thiết. Từ nhu cầu đó, nhóm lựa chọn xây dựng TripTogether để tập trung thông tin của từng chuyến đi và giúp các thành viên cùng theo dõi trên điện thoại.

Thông qua đề tài, nhóm vận dụng kiến thức của môn Phát triển ứng dụng trên thiết bị di động: xây dựng giao diện Android, điều hướng giữa các màn hình, xử lý vòng đời Activity và làm việc với dữ liệu qua mạng. Việc chia sẻ ảnh, tài liệu cũng đặt ra một yêu cầu cụ thể: dữ liệu chuyến đi và nội dung file phải được lưu đúng nơi, đồng thời kiểm soát được ai có quyền truy cập.

<a id="muc-1-2"></a>

### 1.2. Mục tiêu.

Trong phạm vi đồ án, nhóm tập trung vào ba mục tiêu:

1. Hoàn thiện luồng sử dụng từ đăng ký, đăng nhập đến tạo và theo dõi chuyến đi.
2. Hỗ trợ các thành viên cùng xây dựng lịch trình, trao đổi tin nhắn và chia sẻ ảnh, tài liệu.
3. Xây dựng giao diện thống nhất, dễ theo dõi và có thông báo rõ ràng khi đang tải, chưa có dữ liệu hoặc thao tác thất bại.

Về kỹ thuật, nhóm tổ chức phần giao diện, quản lý trạng thái và truy cập dữ liệu thành các lớp Activity, ViewModel và Repository. Dữ liệu chuyến đi được tra cứu theo tài khoản; quyền truy cập được kiểm tra bằng UID và Security Rules. Báo cáo trình bày những gì đã triển khai trong mã nguồn, kết quả kiểm thử đã lưu và các hạng mục còn cần kiểm chứng khi chạy với dịch vụ thực tế.

**Đối tượng sử dụng và phạm vi.**

Đối tượng sử dụng là người tổ chức và thành viên của một nhóm du lịch nhỏ. Chủ chuyến đi tạo thông tin, mời người đã có tài khoản và thay đổi thành viên. Thành viên được cấp quyền có thể xem chuyến đi, chỉnh hoạt động, trò chuyện và thêm nội dung chia sẻ. Vai trò hiển thị trong hồ sơ cá nhân không phải quyền quản trị hệ thống.

<a id="bang-1-1"></a>

**Bảng 1.1. Phạm vi chức năng**

| Trong phiên bản cơ bản | Ngoài phạm vi hiện tại |
| --- | --- |
| Email và Google sign in, đăng ký, reset qua email | OTP nhập trong app, đăng nhập Apple hoặc Facebook |
| Trip, thành viên, lịch trình và tìm kiếm | Đặt khách sạn, giỏ hàng, thanh toán và blog |
| Chat, ảnh, PDF và thông báo chưa đọc trong app | FCM push, nhắc lịch nền và phân trang lịch sử cũ |
| Hồ sơ và tùy chọn giao diện sáng tối | Xóa toàn bộ tài khoản hoặc chuyến đi có dọn dữ liệu liên dịch vụ |

**Phương pháp phát triển.**

Nhóm sử dụng Figma làm tài liệu tham khảo về bố cục, màu sắc và cách trình bày các màn hình. Khi đưa vào ứng dụng, giao diện được điều chỉnh theo các chức năng đã có trong mã nguồn. Vì vậy, một số nội dung trong thiết kế như đặt phòng, giỏ hàng hoặc thanh toán được để lại cho hướng phát triển sau. Phần triển khai hiện tại tập trung vào lập kế hoạch chuyến đi và cộng tác giữa các thành viên.

**Tổng quan sản phẩm và phiên bản báo cáo.**

TripTogether được xây dựng bằng Kotlin và giao diện XML. Firebase Authentication phụ trách xác thực tài khoản, Firebase Realtime Database lưu thông tin chuyến đi và dữ liệu cộng tác, còn Google Drive lưu ảnh và tài liệu. Cách phân chia này được sử dụng xuyên suốt trong các luồng đăng nhập, tạo chuyến đi và chia sẻ nội dung.

Phiên bản được đối chiếu có 24 Activity khai báo trong AndroidManifest.xml. Người dùng bắt đầu tại màn hình Welcome, chọn đăng nhập hoặc đăng ký, sau đó sử dụng bốn mục chính: Home, My Trips, Favorites và Account. Từ mỗi chuyến đi, người dùng có thể mở lịch trình, danh sách thành viên, chat, ảnh, tài liệu và bản đồ. Các màn hình được triển khai bằng thành phần Android native dựa trên bản Figma tham khảo.

Các kết quả kiểm thử cục bộ đã lưu gồm 6 unit test và 7 ca kiểm thử Android, đều không ghi nhận lỗi. Trong 6 unit test có 5 ca kiểm tra chức năng và 1 ca kiểm tra mẫu. Các ca Android kiểm tra điều hướng, dữ liệu nhập, phục hồi màn hình, bố cục và chế độ tối. Kết quả này là cơ sở đánh giá phần ứng dụng; luồng Firebase và Google Drive vẫn cần được nghiệm thu riêng bằng nhiều tài khoản như mô tả ở mục 3.2.

<a id="bang-1-2"></a>

**Bảng 1.2. Thông tin kỹ thuật của phiên bản báo cáo**

| Thuộc tính | Giá trị |
| --- | --- |
| Tên sản phẩm | TripTogether |
| Android package | com.example.triptogether |
| Mã nguồn đối chiếu | 633e50d trên nhánh main |
| Ngày lập báo cáo | 29 tháng 10 năm 2026 |
| Nền tảng | Android 8.0 trở lên theo minSdk 26 |
| Dữ liệu và file | Realtime Database và Google Drive |
| Phạm vi | Ứng dụng lập kế hoạch chuyến đi nhóm cơ bản |

<a id="muc-2"></a>

## Chương 2. Cơ sở lý thuyết.

<a id="muc-2-1"></a>

### 2.1. Ngôn ngữ và phần mềm sử dụng.

<a id="muc-2-1-1"></a>

#### 2.1.1. Ngôn ngữ sử dụng.

TripTogether sử dụng Kotlin để triển khai ứng dụng Android và XML để mô tả giao diện native. Kotlin có kiểu dữ liệu nullable rõ ràng, coroutines cho thao tác bất đồng bộ và khả năng tương tác với thư viện Java. XML kết hợp ViewBinding giúp tham chiếu view theo kiểu dữ liệu, còn JSON biểu diễn dữ liệu Realtime Database và Security Rules. Các công cụ kiểm tra, migration của Firebase dùng JavaScript trong các file .cjs; đây là công cụ hỗ trợ, không phải backend REST riêng của ứng dụng.

<a id="muc-2-1-2"></a>

#### 2.1.2. Phần mềm sử dụng.

Kotlin là ngôn ngữ triển khai chính; XML mô tả layout và ViewBinding tạo tham chiếu có kiểu tới view. Android Studio là môi trường phát triển, Gradle quản lý build và thư viện. Đối với bản này, Java và Kotlin target là 17; JDK dùng để chạy Gradle có thể là 17 hoặc 21 theo script của dự án. Không đồng nhất JDK chạy Gradle với mức API thiết bị.

<a id="bang-2-1"></a>

**Bảng 2.1. Phiên bản trích từ cấu hình dự án**

| Thành phần | Phiên bản hoặc giá trị | Vai trò |
| --- | --- | --- |
| Android Gradle Plugin | 8.13.2 | Build ứng dụng |
| Gradle wrapper | 8.13 | Điều phối build |
| Kotlin | 2.0.21 | Ngôn ngữ và compiler |
| SDK | min 26, target 35, compile 35 | Phạm vi nền tảng |
| Firebase BOM | 33.15.0 | Căn chỉnh Firebase SDK |
| FirebaseUI Auth | 9.0.0 | Giao diện provider Google |
| Material Components | 1.12.0 | Button, input, dialog |
| Lifecycle | 2.9.4 | ViewModel và vòng đời |
| Coroutines | 1.9.0 | Công việc bất đồng bộ |
| Glide | 4.16.0 | Hiển thị ảnh |
| Google Maps SDK | 19.2.0 | Bản đồ chuyến đi |
| Spotless và ktfmt | 7.2.1 và 0.54 | Định dạng code |

Các phiên bản thư viện được lấy từ cấu hình dự án tại thời điểm đối chiếu. Trong danh mục thư viện vẫn còn khai báo Firebase Storage, nhưng thư viện này không được thêm vào dependencies của ứng dụng. Luồng lưu file hiện tại sử dụng Google Drive. Những file cấu hình Storage, Firestore hoặc Cloud Functions còn lại thuộc phương án trước và không được triển khai bởi cấu hình đang sử dụng.

<a id="muc-2-2"></a>

### 2.2. Các tính năng của ứng dụng.

Các chức năng được nhóm thành mười yêu cầu trong [Bảng 2.2](#bang-2-2). Mỗi mã FR được dùng để đối chiếu chức năng với luồng sử dụng và các kịch bản kiểm thử ở phần thiết kế hệ thống.

<a id="bang-2-2"></a>

**Bảng 2.2. Danh mục yêu cầu chức năng**

| Mã | Yêu cầu | Tiêu chí chính |
| --- | --- | --- |
| FR01 | Xác thực | Tách Welcome, Login, Register; reset bằng email |
| FR02 | Tạo chuyến đi | Tên, nơi đến, ngày hợp lệ; lưu trip cùng ACL và index |
| FR03 | Theo dõi trip | Hiển thị trip sở hữu và trip được mời |
| FR04 | Tìm kiếm và yêu thích | Lọc dữ liệu được cấp quyền; lưu favorite theo người dùng |
| FR05 | Quản lý thành viên | Owner mời hoặc gỡ; không gỡ owner |
| FR06 | Lịch trình | CRUD hoạt động dưới từng ngày của trip |
| FR07 | Trò chuyện | Tin có sender UID và timestamp máy chủ |
| FR08 | Ảnh và PDF | Upload Drive riêng tư, metadata trong RTDB |
| FR09 | Hồ sơ và giao diện | Sửa hồ sơ; light, dark hoặc system |
| FR10 | Trợ giúp và thông báo | FAQ và danh sách trip có chat chưa đọc |

**Yêu cầu phi chức năng.**

Bên cạnh các chức năng chính, ứng dụng cần dễ thao tác trên màn hình nhỏ: vùng chạm đủ lớn, biểu mẫu cuộn được khi bàn phím mở và nội dung có thể xuống dòng khi tăng cỡ chữ. Với thao tác qua mạng, người dùng cần biết khi nào dữ liệu đang được xử lý và khi nào cần thử lại. Mật khẩu và access token không được ghi vào database; dữ liệu mẫu phục vụ kiểm tra giao diện chỉ nằm trong bản debug.

Ứng dụng hiện giới hạn listener chat và file ở 100 bản ghi gần nhất để hạn chế lượng tải. Đây là giới hạn truy vấn, không phải cơ chế phân trang hoàn chỉnh. Chưa có số liệu đo độ trễ, mức tiêu thụ bộ nhớ hoặc thông lượng nhiều người dùng; vì vậy không đưa ra cam kết hiệu năng định lượng trong báo cáo.

**Luồng đăng nhập và đăng ký.**

```text
WelcomeActivity
  +-- Sign in --------> LoginActivity
  |                       +-- ForgotPasswordActivity
  +-- Create account -> RegisterActivity

Form -> local validation -> Firebase Auth -> sync user profile
     -> MainActivity and clear authentication back stack
Logout -> clear Drive session -> WelcomeActivity
```

LoginActivity và RegisterActivity có mode cố định và dùng chung AuthFormActivity. Đăng ký yêu cầu tên, email, mật khẩu tối thiểu 6 ký tự và xác nhận trùng mật khẩu; đăng nhập dùng email và mật khẩu. Khi đăng ký thành công, Firebase tạo tài khoản rồi app cập nhật display name và profile. Reset gửi email liên kết đổi mật khẩu, không tạo màn OTP tự xác nhận. Người đã có phiên Auth được đưa qua bước đồng bộ hồ sơ trước khi vào Main.

**Ca sử dụng tạo chuyến đi.**

Điều kiện trước là người dùng đã đăng nhập; nếu chọn ảnh bìa, tài khoản Google còn phải có quyền Drive phù hợp. App kiểm tra ngày hợp lệ và thứ tự ngày, tìm UID theo email mời đã chuẩn hóa, tạo tripId và gọi geocoding. Sau khi upload ảnh bìa tùy chọn, TripRepository thực hiện một updateChildren ghi trip, tripAccess và userTrips cho mọi thành viên. Chỉ khi thao tác RTDB được xác nhận mới hoàn tất ca sử dụng [[3]](#tai-lieu-3).

Nếu email chưa có tài khoản, app thông báo lỗi người được mời chưa đăng ký. Nếu geocoding không trả tọa độ, code dùng 0,0; màn bản đồ coi cặp giá trị này là chưa có vị trí. Nếu database từ chối sau upload ảnh bìa, ảnh có thể còn trên Drive vì cleanup cover chưa đầy đủ. Đây là ranh giới giữa thao tác database nguyên tử và tác vụ liên dịch vụ.

**Ca sử dụng thay đổi thành viên.**

Owner gửi cập nhật theo từng UID dưới tripAccess, tăng revision bằng giá trị máy chủ và thay đổi userTrips. Khi gỡ thành viên, cùng một bản cập nhật còn xóa favorites và lastSeenChat của người bị gỡ. App không sửa toàn bộ danh sách membership sau một lần đọc để tránh ghi đè các UID khác. Trip.members bên trong bản ghi trip là dữ liệu tương thích; quyền và danh sách hiển thị sau khi đọc dựa trên tripAccess.

**Ca sử dụng trao đổi và chia sẻ nội dung.**

ChatRepository lấy 100 tin gần nhất, sắp xếp theo timestamp và ghi tin mới kèm senderUid, email và ServerValue.TIMESTAMP. NotificationsViewModel tổng hợp trip có timestamp tin mới nhất lớn hơn mốc đã đọc. Đây là thông báo trong ứng dụng, không phải push notification hoặc dịch vụ chạy nền. Code kiểm tra unread hiện không loại riêng tin do chính mình gửi, nên cần nghiệm thu thêm để tránh gắn nhãn như thông báo chỉ dành cho tin nhận.

Với ảnh hoặc PDF, người dùng chọn URI, app kiểm tra loại và kích thước, upload file rồi lưu metadata. Danh sách dùng metadata để hiển thị tên, người upload và thời điểm. Nếu file bị xóa trực tiếp trên Drive, metadata không tự biến mất; app có thể gặp lỗi khi mở và cần quy trình dọn dữ liệu bổ sung.

<a id="muc-2-3"></a>

### 2.3. Giao diện của Ứng dụng TripTogether.

**Nguyên tắc thiết kế.**

Thiết kế sử dụng nền kem, xanh đậm và panel xanh nhạt. Màu chính ở chế độ sáng là #125642 và nền #FFF8EF; chế độ tối có nền #0A2C22 và chữ màu kem. Header đặt logo giữa, nút quay lại bên trái và chuông bên phải ở những màn có dùng thành phần chung. Bottom navigation gồm Home, Favorites, My Trips và Account. Các màn tác vụ và xác thực giữ điều hướng phù hợp với ngữ cảnh.

Logo được chuyển từ asset SVG FT và FindTrip thành VectorDrawable để hiển thị rõ ở nhiều mật độ. Tên ứng dụng và package vẫn là TripTogether; chữ FindTrip trên logo là một khác biệt thương hiệu cần thống nhất trước phát hành. Các PNG Figma cũ còn logo Q nên báo cáo ưu tiên bộ asset logo cuối được cung cấp. Font giao diện là Android sans-serif do không có file font thiết kế gốc đi kèm.

**Bản đồ màn hình.**

<a id="bang-2-3"></a>

**Bảng 2.3. Màn hình và thao tác chính**

| Nhóm | Activity | Thao tác |
| --- | --- | --- |
| Bắt đầu | Welcome, Login, Register, ForgotPassword | Chọn luồng, nhập form, Google và reset email |
| Khám phá trip | Main, MyTrips, Favorite, SearchResults | Tìm, lọc, mở, tạo và yêu thích |
| Chi tiết | AddTrip, TripSummary, TripMap | Tạo trip, xem tổng quan và vị trí |
| Kế hoạch | TripPlan, Friends, EditFriends | Lịch trình theo ngày và thành viên |
| Cộng tác | GroupChat, DocumentsHome, Documents | Tin nhắn, chọn ảnh hoặc tài liệu |
| Ảnh | ImagesList, ImagePreview | Danh sách và xem ảnh |
| Cá nhân | Account, EditProfile, Settings | Hồ sơ, Drive, theme và logout |
| Hỗ trợ | HelpSupport, Notifications | FAQ và chat chưa đọc |

**Minh họa màn mở đầu và danh sách.**

Các hình dưới đây là ảnh render cục bộ của layout Android với fixture hoặc trường runtime còn trống. Chúng dùng để đánh giá bố cục, không phải bằng chứng đã đăng nhập, đọc database cloud hay upload Drive thành công. Màn Help trong phần hỗ trợ được chụp khi chạy Activity thật với FAQ mở. Toàn bộ ảnh gốc phục vụ chỉnh sửa nằm trong thư mục assets của bộ báo cáo.

<p align="center">
  <img src="assets/ui/activity_welcome.png" width="280" alt="Màn chọn xác thực và Home với dữ liệu minh họa — ảnh 1" />
  <img src="assets/ui/activity_main.png" width="280" alt="Màn chọn xác thực và Home với dữ liệu minh họa — ảnh 2" />
</p>

<a id="hinh-2-1"></a>

*Hình 2.1. Màn chọn xác thực và Home với dữ liệu minh họa*

Nguồn: Bộ ảnh kiểm tra giao diện cục bộ của dự án [[8]](#tai-lieu-8).

Màn hình Welcome đặt hai lựa chọn đăng nhập và tạo tài khoản ở vị trí dễ nhận biết. Sau khi đăng nhập, Home ưu tiên phần tìm kiếm, bộ lọc thời gian và các thẻ chuyến đi có ảnh. My Trips sử dụng lại cách trình bày này cho danh sách chuyến đi của tài khoản. Vì ứng dụng phục vụ lập kế hoạch nhóm, các thẻ chỉ hiển thị thông tin chuyến đi, không có giá phòng hoặc nút đặt dịch vụ.

<p align="center">
  <img src="assets/ui/activity_favorites.png" width="280" alt="Yêu thích và tổng quan chuyến đi — ảnh 1" />
  <img src="assets/ui/activity_trip_summary.png" width="280" alt="Yêu thích và tổng quan chuyến đi — ảnh 2" />
</p>

<a id="hinh-2-2"></a>

*Hình 2.2. Yêu thích và tổng quan chuyến đi*

Nguồn: Bộ ảnh kiểm tra giao diện cục bộ của dự án [[8]](#tai-lieu-8).

Ở Favorites, mỗi chuyến đi được trình bày bằng thẻ ngang, với ảnh bên trái và thông tin trên nền xanh. Màn hình tổng quan ưu tiên ảnh bìa, ngày đi và các lối vào lịch trình, thành viên hoặc nội dung chia sẻ. Các thông báo lỗi và nút thử lại được đặt trong vùng có thể cuộn để người dùng vẫn thao tác được trên màn hình thấp.

**Tài khoản và hỗ trợ.**

<p align="center">
  <img src="assets/ui/activity_account.png" width="280" alt="Account và FAQ khi chạy HelpSupportActivity — ảnh 1" />
  <img src="assets/ui/activity_help_support_live.png" width="280" alt="Account và FAQ khi chạy HelpSupportActivity — ảnh 2" />
</p>

<a id="hinh-2-3"></a>

*Hình 2.3. Account và FAQ khi chạy HelpSupportActivity*

Nguồn: Bộ ảnh kiểm tra giao diện cục bộ của dự án [[8]](#tai-lieu-8).

Account giữ các mục Profile, Settings, My Trips, Help và Logout. Nút kết nối Drive là chức năng bổ sung phục vụ lưu file, không phải quyền được cấp tự động bởi Firebase. Edit Profile lưu tên, họ, role mô tả, email và avatar; các trường điện thoại, địa chỉ, thành phố và khu vực trong một số frame Figma chưa được thêm vào model. Settings có light, dark và system; chưa có cơ chế đổi tiền tệ hoặc đa ngôn ngữ hoàn chỉnh.

**Chế độ tối và sai khác có chủ đích.**

<p align="center">
  <img src="assets/ui/activity_main_dark_large.png" width="280" alt="Kiểm tra night mode với fontScale 1.3 — ảnh 1" />
  <img src="assets/ui/activity_settings_dark_large.png" width="280" alt="Kiểm tra night mode với fontScale 1.3 — ảnh 2" />
</p>

<a id="hinh-2-4"></a>

*Hình 2.4. Kiểm tra night mode với fontScale 1.3*

Nguồn: Bộ ảnh kiểm tra giao diện cục bộ của dự án [[8]](#tai-lieu-8).

Nhóm kiểm tra chế độ tối kết hợp cỡ chữ `fontScale = 1.3` để quan sát khả năng xuống dòng. Lưới chuyến đi ở Home dùng các thẻ có chiều cao thống nhất, thay cho bố cục so le trong một số màn Figma. Các màn giỏ hàng, thanh toán, blog, OTP và giới thiệu dạng carousel chưa nằm trong phạm vi chức năng của phiên bản này.

**Giao diện tạo chuyến đi và lịch trình.**

<p align="center">
  <img src="assets/ui/activity_add_trip.png" width="280" alt="Giao diện tạo chuyến đi và lịch trình; ảnh render cục bộ — ảnh 1" />
  <img src="assets/ui/activity_trip_plan.png" width="280" alt="Giao diện tạo chuyến đi và lịch trình; ảnh render cục bộ — ảnh 2" />
</p>

<a id="hinh-2-5"></a>

*Hình 2.5. Giao diện tạo chuyến đi và lịch trình; ảnh render cục bộ*

Nguồn: Bộ ảnh kiểm tra giao diện cục bộ của dự án [[8]](#tai-lieu-8).

Màn tạo chuyến đi thu thập thông tin cơ bản như tên, nơi đến, ngày đi và ảnh bìa. Sau khi tạo, người dùng chuyển sang lịch trình để thêm hoạt động theo từng ngày; các thao tác sửa và xóa được đặt cùng nhóm để dễ tìm.

**Giao diện quản lý thành viên.**

<p align="center">
  <img src="assets/ui/activity_friends.png" width="280" alt="Giao diện quản lý thành viên; ảnh render cục bộ — ảnh 1" />
  <img src="assets/ui/activity_edit_friends.png" width="280" alt="Giao diện quản lý thành viên; ảnh render cục bộ — ảnh 2" />
</p>

<a id="hinh-2-6"></a>

*Hình 2.6. Giao diện quản lý thành viên; ảnh render cục bộ*

Nguồn: Bộ ảnh kiểm tra giao diện cục bộ của dự án [[8]](#tai-lieu-8).

Danh sách thành viên cho biết những người tham gia chuyến đi. Chủ chuyến đi mở màn quản lý để mời tài khoản đã đăng ký hoặc gỡ thành viên; quyền thực hiện được kiểm tra cùng dữ liệu membership.

**Giao diện chat và thư viện nội dung.**

<p align="center">
  <img src="assets/ui/activity_group_chat.png" width="280" alt="Giao diện chat và thư viện nội dung; ảnh render cục bộ — ảnh 1" />
  <img src="assets/ui/activity_documents_home.png" width="280" alt="Giao diện chat và thư viện nội dung; ảnh render cục bộ — ảnh 2" />
</p>

<a id="hinh-2-7"></a>

*Hình 2.7. Giao diện chat và thư viện nội dung; ảnh render cục bộ*

Nguồn: Bộ ảnh kiểm tra giao diện cục bộ của dự án [[8]](#tai-lieu-8).

Màn chat phục vụ trao đổi trong từng chuyến đi. Màn thư viện nội dung chia các file thành hai nhóm ảnh và tài liệu, đồng thời cung cấp thao tác tải lên để người dùng bổ sung nội dung chung.

**Giao diện ảnh và tài liệu PDF.**

<p align="center">
  <img src="assets/ui/activity_images_list.png" width="280" alt="Giao diện ảnh và tài liệu PDF; ảnh render cục bộ — ảnh 1" />
  <img src="assets/ui/activity_documents.png" width="280" alt="Giao diện ảnh và tài liệu PDF; ảnh render cục bộ — ảnh 2" />
</p>

<a id="hinh-2-8"></a>

*Hình 2.8. Giao diện ảnh và tài liệu PDF; ảnh render cục bộ*

Nguồn: Bộ ảnh kiểm tra giao diện cục bộ của dự án [[8]](#tai-lieu-8).

Ảnh và tài liệu PDF được hiển thị ở hai danh sách riêng. Nội dung các danh sách được lấy từ metadata trong Realtime Database, còn file gốc nằm trên Drive. Các ảnh minh họa này được render khi chưa nạp dữ liệu động.

**Giao diện xem ảnh và bản đồ.**

<p align="center">
  <img src="assets/ui/activity_image_preview.png" width="280" alt="Giao diện xem ảnh và bản đồ; ảnh render cục bộ — ảnh 1" />
  <img src="assets/ui/activity_trip_map.png" width="280" alt="Giao diện xem ảnh và bản đồ; ảnh render cục bộ — ảnh 2" />
</p>

<a id="hinh-2-9"></a>

*Hình 2.9. Giao diện xem ảnh và bản đồ; ảnh render cục bộ*

Nguồn: Bộ ảnh kiểm tra giao diện cục bộ của dự án [[8]](#tai-lieu-8).

Màn xem ảnh tập trung vào nội dung ảnh được chọn. Màn bản đồ sử dụng vị trí của chuyến đi; trong ảnh render cục bộ, vùng ảnh và bản đồ chưa được nạp nên chỉ thể hiện phần bố cục bao quanh.

**Giao diện hồ sơ và tìm kiếm.**

<p align="center">
  <img src="assets/ui/activity_edit_profile.png" width="280" alt="Giao diện hồ sơ và tìm kiếm; ảnh render cục bộ — ảnh 1" />
  <img src="assets/ui/activity_search_results.png" width="280" alt="Giao diện hồ sơ và tìm kiếm; ảnh render cục bộ — ảnh 2" />
</p>

<a id="hinh-2-10"></a>

*Hình 2.10. Giao diện hồ sơ và tìm kiếm; ảnh render cục bộ*

Nguồn: Bộ ảnh kiểm tra giao diện cục bộ của dự án [[8]](#tai-lieu-8).

Màn hồ sơ cho phép sửa thông tin cá nhân và ảnh đại diện. Màn tìm kiếm hỗ trợ nhập từ khóa và chọn bộ lọc cho danh sách chuyến đi mà tài khoản có quyền truy cập.

**Giao diện cài đặt ban ngày và thông báo.**

<p align="center">
  <img src="assets/ui/activity_settings.png" width="280" alt="Giao diện cài đặt ban ngày và thông báo; ảnh render cục bộ — ảnh 1" />
  <img src="assets/ui/activity_notifications.png" width="280" alt="Giao diện cài đặt ban ngày và thông báo; ảnh render cục bộ — ảnh 2" />
</p>

<a id="hinh-2-11"></a>

*Hình 2.11. Giao diện cài đặt ban ngày và thông báo; ảnh render cục bộ*

Nguồn: Bộ ảnh kiểm tra giao diện cục bộ của dự án [[8]](#tai-lieu-8).

Cài đặt giao diện gồm ba lựa chọn: theo thiết bị, sáng và tối. Danh sách thông báo trong phiên bản này tổng hợp các chuyến đi có tin nhắn chưa đọc; đây là thông báo trong ứng dụng.

**Giao diện đăng nhập và khôi phục mật khẩu.**

<p align="center">
  <img src="assets/ui/activity_login.png" width="280" alt="Giao diện đăng nhập và khôi phục mật khẩu; ảnh render cục bộ — ảnh 1" />
  <img src="assets/ui/activity_forgot_password.png" width="280" alt="Giao diện đăng nhập và khôi phục mật khẩu; ảnh render cục bộ — ảnh 2" />
</p>

<a id="hinh-2-12"></a>

*Hình 2.12. Giao diện đăng nhập và khôi phục mật khẩu; ảnh render cục bộ*

Nguồn: Bộ ảnh kiểm tra giao diện cục bộ của dự án [[8]](#tai-lieu-8).

Màn đăng ký được triển khai trong `RegisterActivity`, dùng chung `AuthFormActivity` với màn đăng nhập và bổ sung tên người dùng, xác nhận mật khẩu. Bộ ảnh hiện có chưa có ảnh riêng của màn đăng ký, nên phần này được mô tả dựa trên mã nguồn. Một số ảnh render còn trống các vùng dữ liệu động; chúng chỉ minh họa bố cục và không phản ánh trạng thái dữ liệu trên Firebase.

<a id="muc-3"></a>

## Chương 3. Phân tích thiết kế hệ thống.

<a id="muc-3-1"></a>

### 3.1. Sơ đồ phân rã chức năng.

[Hình 3.1](#hinh-3-1) chia ứng dụng thành bốn nhóm chức năng. Từ đó, [Hình 3.2](#hinh-3-2) thể hiện cách các màn hình kết nối với lớp quản lý trạng thái, repository và dịch vụ bên ngoài.

<p align="center">
  <img src="assets/diagrams/functions.png" width="760" alt="Các nhóm chức năng của TripTogether — ảnh 1" />
</p>

<a id="hinh-3-1"></a>

*Hình 3.1. Các nhóm chức năng của TripTogether*

Nguồn: Sơ đồ tổng hợp từ mã nguồn dự án [[8]](#tai-lieu-8).

**Kiến trúc ứng dụng.**

<p align="center">
  <img src="assets/diagrams/architecture.png" width="760" alt="Kiến trúc ứng dụng và các dịch vụ bên ngoài — ảnh 1" />
</p>

<a id="hinh-3-2"></a>

*Hình 3.2. Kiến trúc ứng dụng và các dịch vụ bên ngoài*

Nguồn: Sơ đồ tổng hợp từ mã nguồn dự án [[8]](#tai-lieu-8).

Lớp giao diện gồm Activity, layout XML và adapter RecyclerView. Activity tiếp nhận thao tác, điều hướng và hiển thị trạng thái. ViewModel giữ trạng thái và chạy công việc trong viewModelScope. Repository triển khai truy vấn hoặc thao tác ghi với Firebase và Drive. Phần backend là dịch vụ được quản lý kết hợp Security Rules; không có máy chủ REST riêng đang vận hành cho phiên bản này.

Các interface trong `DataSources.kt` quy định thao tác mà từng repository cung cấp. `UiState` có ba trạng thái `Loading`, `Content` và `Error`; danh sách rỗng được biểu diễn bằng `Content` với tập dữ liệu rỗng. `ActionState` gồm `Idle`, `Running`, `Done` và `Error`. Khi một thao tác đang chạy, `ActionViewModel` ngăn gửi lại yêu cầu ghi và giữ công việc khi Activity được tạo lại. Nếu tiến trình ứng dụng bị hệ điều hành kết thúc, công việc chưa được bảo đảm tiếp tục.

<a id="bang-3-1"></a>

**Bảng 3.1. Các lớp chính và trách nhiệm**

| Nhóm | Thành phần | Trách nhiệm |
| --- | --- | --- |
| Xác thực | AuthRepository, AuthViewModel | Đăng nhập, đăng ký, reset email và đồng bộ hồ sơ |
| Chuyến đi | TripRepository, CreateTripRepository | Đọc trip theo index, tạo trip và cập nhật thành viên |
| Cộng tác | PlanRepository, ChatRepository | Lịch trình theo ngày, tin nhắn và mốc đã đọc |
| Nội dung | FilesRepository, DriveApi | Upload file và lưu metadata |
| Giao diện chung | PlannerActivity | Insets, thanh hệ thống, điều hướng và kết nối Drive |
| Phiên Drive | DriveSession, DriveImages, DriveLinks | Token trong RAM và đường dẫn media được kiểm tra |

**Vòng đời và quản lý trạng thái.**

Công việc bất đồng bộ không giữ tham chiếu Activity trong repository. Các dialog thêm, sửa và xóa hoạt động dùng arguments và fragment results để có thể tạo lại. Khi mở lại màn hình, Activity quan sát trạng thái thay vì gửi thao tác ghi trùng. Người dùng nhận được trạng thái đang xử lý, lỗi có thể thử lại và dữ liệu đã tải. Riêng upload chưa có WorkManager hoặc lưu phiên upload để phục hồi sau process death.

**Danh tính và phân quyền tách biệt.**

Firebase Authentication cấp UID sau khi đăng nhập; UID là khóa liên kết dữ liệu nghiệp vụ. Google Drive authorization cấp access token để gọi Drive API. Hai bước này phục vụ hai miền quyền khác nhau: đăng nhập Firebase thành công không đồng nghĩa đã có quyền đọc hoặc thêm file vào thư mục Drive. Trong app, việc kết nối Drive được đặt tại Account để người dùng chủ động đồng ý quyền.

<a id="muc-3-2"></a>

### 3.2. Cơ sở dữ liệu.

**Mô hình dữ liệu tổng quát.**

Dữ liệu được tổ chức thành cây JSON trong Realtime Database. Firebase Authentication quản lý thông tin xác thực và cấp UID cho tài khoản; database lưu hồ sơ cùng dữ liệu chuyến đi. Một người có thể tham gia nhiều chuyến đi và mỗi chuyến đi có nhiều thành viên. Quan hệ này được biểu diễn qua `tripAccess` và `userTrips`. Sơ đồ bên dưới mô tả quan hệ nghiệp vụ; tính nhất quán giữa các nhánh được duy trì bằng thao tác ghi nhiều vị trí và Security Rules, thay vì khóa ngoại như trong cơ sở dữ liệu quan hệ.

<p align="center">
  <img src="assets/diagrams/data-relations.png" width="760" alt="Quan hệ logic giữa tài khoản chuyến đi và nội dung — ảnh 1" />
</p>

<a id="hinh-3-3"></a>

*Hình 3.3. Quan hệ logic giữa tài khoản chuyến đi và nội dung*

Nguồn: Sơ đồ tổng hợp từ mã nguồn dự án [[8]](#tai-lieu-8).

```text
Realtime Database
|-- users/{uid}
|-- trips/{creatorUid}/{tripId}
|   |-- metadata and legacy members list
|   `-- plan/dayN/{activityId}
|-- tripAccess/{tripId}
|   |-- creatorUid, updatedAt, revision
|   `-- members/{uid}: true
|-- userTrips/{uid}/{tripId}: creatorUid
|-- favorites/{uid}/{tripId}: creatorUid
|-- chats/{tripId}/{messageId}
|-- lastSeenChat/{uid}/{tripId}: timestamp
|-- tripImages/{tripId}/{itemId}
`-- tripDocuments/{tripId}/{itemId}
```

`userTrips` lưu tham chiếu để ứng dụng tìm đúng đường dẫn `creatorUid` của từng chuyến đi mà tài khoản được phép đọc (xem [Bảng 3.4](#bang-3-4)). Nhờ đó, ứng dụng không phải tải toàn bộ nhánh `/trips`. Tương tự, `favorites` lưu tham chiếu thay cho bản sao chuyến đi, nên thông tin hiển thị vẫn được đọc từ bản ghi chính. Một số nhánh đọc còn hỗ trợ chỉ mục boolean hoặc snapshot cũ; dữ liệu cũ vẫn cần được chuyển đổi đầy đủ để các tài khoản được mời truy cập nhất quán.

**Từ điển dữ liệu người dùng và chuyến đi.**

<a id="bang-3-2"></a>

**Bảng 3.2. Hồ sơ người dùng tại `users/{uid}`**

| Trường | Kiểu | Ý nghĩa |
| --- | --- | --- |
| email | string | Email hiện tại được đồng bộ từ Auth |
| emailNormalized | string | Email viết thường để query bằng equality |
| name | string | Tên hiển thị, rules giới hạn 150 ký tự |
| firstName và lastName | string tùy chọn | Tên và họ được chỉnh trong hồ sơ |
| role | string tùy chọn | Mô tả cá nhân, không phải ACL |
| photoUrl | string tùy chọn | Ảnh từ provider hoặc profile ban đầu |
| imageUrl | string tùy chọn | Ảnh đã chỉnh, ưu tiên khi hiển thị |

<a id="bang-3-3"></a>

**Bảng 3.3. Chuyến đi tại `trips/{creatorUid}/{tripId}`**

| Trường | Kiểu | Quy ước |
| --- | --- | --- |
| id và createdBy | string | Khớp tripId và creatorUid của path |
| name | string | Tên bắt buộc, tối đa 150 ký tự trong rules |
| country | string | Nơi đến, tối đa 200 ký tự trong rules |
| startDate và endDate | string | dd/MM/yyyy; app kiểm tra ngày và thứ tự |
| members | list UID | Dữ liệu tương thích khi tạo; không là nguồn quyền |
| latitude và longitude | number | 0,0 biểu thị không có tọa độ theo xử lý hiện tại |
| imageUrl | string tùy chọn | URL media Drive không kèm token |
| isFavorite | boolean | Trường model còn tồn tại; UI lấy theo người dùng |
| plan/dayN/activityId | object | Ngày N bắt đầu từ 0; time, emoji, description |

**Quyền thành viên và chỉ mục.**

<a id="bang-3-4"></a>

**Bảng 3.4. Các node quyền và tham chiếu**

| Path hoặc trường | Giá trị | Ý nghĩa |
| --- | --- | --- |
| tripAccess/tripId/creatorUid | UID | Owner bất biến |
| tripAccess/tripId/members/uid | true | Nguồn membership chuẩn |
| tripAccess/tripId/revision | number | Tạo là 0, thay đổi tăng 1 |
| tripAccess/tripId/updatedAt | server timestamp | Giờ máy chủ khi đổi quyền |
| userTrips/uid/tripId | creatorUid | Index tra cứu chuyến đi |
| favorites/uid/tripId | creatorUid | Yêu thích của từng người |
| lastSeenChat/uid/tripId | timestamp | Mốc đọc không giảm và không vượt now |

Khi load trip, TripRepository lấy members từ tripAccess nếu node này tồn tại; nếu không mới dùng list cũ. Tạo trip thêm creator vào membership và ghi các index trong một updateChildren. Membership được sửa từng UID, không ghi lại toàn bộ trip. Revision giúp mô tả thứ tự thay đổi ACL và hỗ trợ tính nhất quán của các công cụ dữ liệu đã chuẩn bị.

**Tin nhắn và metadata file.**

<a id="bang-3-5"></a>

**Bảng 3.5. Các trường dùng chung cho chat và file**

| Bản ghi | Trường | Quy ước |
| --- | --- | --- |
| Chat | senderUid và sender | UID bằng auth.uid; email bằng token email |
| Chat | message | Chuỗi không rỗng, tối đa 4000 ký tự theo rules |
| Chat | timestamp | ServerValue.TIMESTAMP; listener 100 tin gần nhất |
| File | name và url | Tên gốc; URL Drive API không chứa token |
| File | uploadedByUid và uploadedBy | UID và email người upload |
| File | provider và driveFileId | provider là drive; file ID hợp lệ |
| File | contentType và sizeBytes | MIME và kích thước số dương |
| File | timestamp | Thời gian máy chủ |
| SharedItem cũ | storagePath | Trường tương thích; không ghi trong upload Drive mới |

Rules ảnh giới hạn MIME image/ và tối đa 10 MiB; tài liệu chỉ chấp nhận application/pdf, tối đa 20 MiB. MIME được lấy từ content resolver và metadata do client gửi, chưa có backend đọc magic bytes để kiểm định nội dung. URI chọn file không được dùng làm nơi lưu lâu dài; bytes được đẩy lên Drive, còn database chỉ giữ tham chiếu và thuộc tính.

**Ma trận phân quyền.**

<a id="bang-3-6"></a>

**Bảng 3.6. Quyền theo vai trò của một trip**

| Tác vụ | Owner | Thành viên | Ngoài trip |
| --- | --- | --- | --- |
| Đọc trip và nội dung | Có | Có | Không |
| Thêm hoặc gỡ thành viên | Có | Không | Không |
| Sửa lịch trình | Có | Có | Không |
| Gửi chat và thêm metadata file | Có | Có | Không |
| Yêu thích trip đang được cấp quyền | Có | Có | Không |
| Gỡ owner khỏi membership | Không | Không | Không |
| Xóa toàn trip hoặc ACL qua UI | Chưa hỗ trợ | Chưa hỗ trợ | Không |

Node gốc đặt .read và .write là false. Quyền được mở tại đúng path theo UID hoặc membership. Rules không tự lọc các node con cho một lần đọc node cha; do đó app đọc theo index và từng trip, không mở quyền đọc toàn cây để chữa lỗi tải dữ liệu [[4]](#tai-lieu-4). Hồ sơ users/uid hiện đọc được bởi tài khoản đã xác thực; truy vấn cả users chỉ được cho phép với emailNormalized bằng một giá trị và limitToFirst bằng 1. Đây là danh bạ có xác thực, chưa phải hồ sơ riêng tư chỉ chia sẻ trong trip.

Một quyền ghi được cấp ở node cha có thể áp dụng tới node con. Vì owner được ghi ở cấp trip, không nên mô tả validation phía client là bảo đảm backend tuyệt đối cho mọi trường con. Code có validation giờ và ngày, rules kiểm tra định dạng và một số chiều dài; mức bao phủ với client độc hại cần được kiểm thử và siết tiếp. Báo cáo tách rõ kiểm tra UI khỏi ràng buộc bảo mật máy chủ.

**Migration và tương thích dữ liệu cũ.**

Công cụ migrate.cjs và migration-plan.cjs chuẩn bị chuyển boolean userTrips sang creator reference, chuẩn hóa email, dựng tripAccess từ dữ liệu cũ khi chưa có ACL, và cập nhật favorites. Không khôi phục thành viên đã bị thu hồi nếu ACL mới đã tồn tại. Script dry-run mặc định; chế độ apply cần quyền quản trị và lưu backup trước update. Code không xác nhận công cụ này đã chạy trên project cloud; báo cáo không xem dữ liệu thực đã được chuyển đổi xong.

**Lý do tách database và file.**

Ảnh và tài liệu thường có dung lượng lớn hơn nhiều so với thông tin chuyến đi. Vì vậy, nhóm lưu nội dung file trên Google Drive, còn Realtime Database chỉ giữ đường dẫn và các thuộc tính cần để hiển thị. Cách làm này tránh đưa dữ liệu nhị phân dạng base64 vào database. Google Drive được chọn cho phạm vi thử nghiệm của đồ án; dung lượng, hạn mức API và yêu cầu OAuth vẫn cần được tính đến khi mở rộng.

**Luồng upload và lưu metadata.**

<p align="center">
  <img src="assets/diagrams/upload-flow.png" width="760" alt="Các bước lưu file và nhánh phục hồi khi metadata lỗi — ảnh 1" />
</p>

<a id="hinh-3-4"></a>

*Hình 3.4. Các bước lưu file và nhánh phục hồi khi metadata lỗi*

Nguồn: Sơ đồ tổng hợp từ mã nguồn dự án [[8]](#tai-lieu-8).

DriveApi kiểm tra folder đúng kiểu, tài khoản có canAddChildren và toàn bộ các trang permissions không có anyone hoặc domain. File được đọc vào cache riêng với giới hạn kích thước; sau đó app khởi tạo resumable upload và gửi bytes bằng một PUT. Session URL phải có origin Google API hợp lệ, redirect tự động bị tắt và access token chỉ gửi tới origin được kiểm tra. File staging được xóa trong `finally` khi thao tác kết thúc. Quy trình tải lên được đối chiếu với tài liệu Drive API [[5]](#tai-lieu-5).

Sau khi tải file lên Drive, `FilesRepository` ghi thông tin vào `tripImages` hoặc `tripDocuments`. Nếu bước ghi database thất bại, ứng dụng thử xóa file vừa tạo trong tác vụ `NonCancellable`, với thời gian chờ tối đa 5 giây, rồi trả lỗi về giao diện. Việc xóa bù này chỉ được thực hiện trong khả năng cho phép, vì Drive và Realtime Database không dùng chung một giao dịch (xem [Hình 3.4](#hinh-3-4)). Luồng ảnh bìa và ảnh đại diện còn cần bổ sung xử lý dọn file khi bước ghi chuyến đi hoặc hồ sơ thất bại.

**Token và tải ảnh.**

DriveSession giữ access token trong RAM, gắn với UID hiện tại và đặt thời hạn nội bộ 50 phút. Khi đổi UID, hết hạn hoặc logout, token bị xóa; đây là giới hạn quản lý phiên của app, không phải lời khẳng định thời hạn token Google luôn bằng 50 phút. Process death làm mất phiên này và người dùng cần kết nối lại. Token không được lưu trong RTDB hoặc URL metadata.

DriveLinks chỉ nhận URL HTTPS có host và path media đúng dạng Drive API. DriveImages dùng Authorization cho URL hợp lệ và tắt cache ảnh Drive trong Glide. PDF được mở qua link Drive riêng tư bằng ứng dụng hoặc trình duyệt thích hợp. Tài khoản đang đăng nhập ở trình duyệt có thể khác tài khoản đã cấp OAuth trong app; cần kiểm tra khi nghiệm thu mở PDF.

**Giới hạn quyền Drive trong phương án thử nghiệm.**

Thư mục Drive dùng chung không tạo ACL tách biệt tự động cho mỗi trip. Thành viên bị gỡ khỏi trip sẽ mất quyền đọc metadata RTDB theo rules, nhưng quyền Drive do Google quản lý không tự bị thu hồi. Nếu họ vẫn có quyền thư mục hoặc đã tải file, dữ liệu có thể tiếp tục truy cập được. Bản thử nghiệm yêu cầu folder Restricted và chia sẻ theo email cho tài khoản thử nghiệm; không dùng quyền anyone with the link [[6]](#tai-lieu-6).

Scope đang xin là <https://www.googleapis.com/auth/drive>, rộng hơn nhu cầu tối thiểu của nhiều sản phẩm. Đây là cấu hình thử nghiệm hiện có, cần thu hẹp qua phương án chọn file/folder và đánh giá lại OAuth trước phát hành. Không đưa folder ID thật, access token hoặc file credential vào báo cáo; các ví dụ dùng placeholder.

**Chuẩn bị môi trường Android.**

```bash
# From the repository root
bash app/run-project.sh check
bash app/run-project.sh run

# Core verification tasks
bash gradlew :app:assembleDebug :app:testDebugUnitTest :app:lintDebug
bash gradlew :app:connectedDebugAndroidTest
```

Mở repository root trong Android Studio, chọn Gradle JDK 17 hoặc 21, cài Android SDK Platform 35 và tạo emulator có Google Play services để thử Google sign in hoặc Maps. Thiết bị tối thiểu theo cấu hình là API 26. Kiểm tra SDK path trong local.properties. Lệnh run build, cài APK và mở WelcomeActivity, không mở thẳng form Login cũ.

**Thiết lập Firebase cho project.**

Các bước cấu hình Firebase được thực hiện theo thứ tự sau:

1. Tạo hoặc chọn project, thêm ứng dụng Android với package `com.example.triptogether`, rồi đăng ký SHA của khóa ký thực tế. Dùng `signingReport` để lấy fingerprint; tên hiển thị TripTogether không thay thế `applicationId`.
2. Bật Email/Password và Google trong Authentication, tải `google-services.json` và đặt trong thư mục `app`. Ở phiên bản đối chiếu, project ID là `triptogetherv1` và database thuộc khu vực `asia-southeast1` [[1]](#tai-lieu-1), [[2]](#tai-lieu-2).

3. Tạo Realtime Database, đối chiếu URL với cấu hình ứng dụng và sao lưu dữ liệu cùng rules trước khi thay đổi.
4. Kiểm tra cấu trúc dữ liệu đang có. Với dữ liệu cũ, chạy migration ở chế độ xem trước (dry-run) và kiểm tra kết quả trước khi áp dụng.
5. Chạy bộ kiểm thử emulator, sau đó triển khai riêng Database theo `firebase.json` đang sử dụng. Các file Storage, Firestore hoặc Functions cũ không phải căn cứ để triển khai thêm dịch vụ.

```bash
# From firebase/
npm ci
npm test
npx firebase login
npx firebase use triptogetherv1
npx firebase deploy --only database --project triptogetherv1

# Migration is a separate administrative step
node functions/migrate.cjs
# Review the dry run and backup before using --apply
```

Lệnh deploy và migration là quy trình vận hành được mô tả, không phải xác nhận đã được chạy trong lần viết báo cáo này. Không cần nới rules thành public để bỏ qua lỗi permission. Nếu Auth thành công mà profile hoặc trip lỗi, kiểm tra path, UID, index và rules đã publish thay vì chỉ xem trạng thái đăng nhập.

**Thiết lập Drive và Maps.**

Với Drive, bật Google Drive API trong Google Cloud project liên quan, cấu hình Google Auth Platform và thêm tài khoản thử nghiệm theo trạng thái ứng dụng. Đặt folder Restricted, cấp quyền Editor cho đúng tài khoản thử nghiệm và lưu folder ID trong app/drive.local.properties. Trong app mở Account rồi Connect test Google Drive để đồng ý scope. Việc đã đăng nhập Google qua Firebase không thay thế bước authorization này.

```properties
# app/drive.local.properties  local only
DRIVE_FOLDER_ID=<restricted_test_folder_id>

# local.properties or Gradle properties  local only
sdk.dir=<android_sdk_path>
MAPS_API_KEY=<restricted_android_maps_key>
GEOCODING_API_KEY=<appropriate_geocoding_key>
```

Maps và geocoding là hai luồng dùng key khác nhau trong cấu hình. Không có Maps key thì UI báo thiếu key; geocoding thất bại có thể để tọa độ 0,0 và màn bản đồ không đặt marker. Cần bật API tương ứng và kiểm tra giới hạn của từng key cho đúng loại request, đồng thời không ghi giá trị thật vào tài liệu được chia sẻ.

**Dữ liệu lưu ở đâu.**

<a id="bang-3-7"></a>

**Bảng 3.7. Phân bố dữ liệu và thời gian tồn tại**

| Nơi lưu | Nội dung | Vòng đời |
| --- | --- | --- |
| Firebase Auth | UID, provider và thông tin xác thực | Do dịch vụ Auth quản lý |
| Realtime Database | Profile, trip, ACL, chat và metadata | Tồn tại đến khi được sửa hoặc dọn |
| Google Drive | Bytes ảnh và PDF | Độc lập với RTDB và membership |
| Bộ nhớ RAM | Drive access token và state đang chạy | Mất khi process chết hoặc clear |
| Cache riêng Android | File staging tạm khi upload | Xóa sau thao tác; có rủi ro tồn dư nếu process chết |
| SharedPreferences | Chế độ light, dark hoặc system | Giữ cục bộ trên thiết bị |
| File cấu hình local | SDK path, key và folder ID | Không đưa vào Git hoặc báo cáo công khai |

**Kiểm thử và đánh giá hệ thống.**

**Phân tầng kiểm thử.**

Kết quả dưới đây được tổng hợp từ các file kết quả kiểm thử đã lưu, có thời gian ngày 15/10/2026 theo giờ Việt Nam. Nhóm phân biệt ba phạm vi: kiểm thử tự động, kiểm tra giao diện và nghiệm thu với dịch vụ cloud. Thời gian commit không được dùng thay cho thời điểm chạy kiểm thử. Những tính năng đã có trong mã nguồn vẫn cần được kiểm tra trên Firebase và Google Drive thực tế trước khi xác nhận hoàn thành.

<a id="bang-3-8"></a>

**Bảng 3.8. Kết quả có bằng chứng cục bộ**

| Nhóm | Số ca | Kết quả và giới hạn |
| --- | --- | --- |
| JVM unit | 6 | 0 lỗi; gồm 3 DriveLinks, 2 TripFilters và 1 kiểm tra mẫu |
| Android connected | 7 | 0 lỗi; điều hướng, form, recreation, layout và FAQ |
| Layout review | 22 layout trong một test | Inflate và recreate; không phải 22 ca E2E |
| Dark và large text | 3 layout trong một test | Night mode, fontScale 1.3 |
| Build và lint | Quy trình check đã ghi nhận đạt | Không đo độ phủ hoặc chất lượng cloud |
| Cloud Firebase và Drive | Chưa có biên bản E2E | Cần thực hiện kịch bản đa tài khoản |

DriveLinksTest kiểm tra chống chèn path, không authorize host khác và nhận biết media URL riêng tư. TripFiltersTest kiểm tra ngày kết thúc hôm nay vẫn là trip đang hoạt động, từ chối ngày sai và bảo toàn ký tự tiếng Việt trong tìm kiếm. ExampleUnitTest chỉ kiểm tra phép cộng mẫu, vì vậy không tính là một kiểm chứng nghiệp vụ quan trọng.

Bảy ca Android gồm dialog lịch trình phục hồi arguments; app context; Welcome mở hai form; các layout inflate và recreate; validation và phục hồi form đăng ký; FAQ mở và phục hồi; night mode với chữ lớn. reviewAuth chỉ hoạt động trong debug để kiểm tra form mà không tạo tài khoản hoặc gửi yêu cầu Firebase thật. Không dùng fixture để khẳng định Google sign in, reset email hoặc upload cloud đã chạy thành công.

**Kiểm thử Database và migration.**

firebase/package.json hiện chạy Database Emulator cùng database.test.cjs và migration.test.cjs. Các source test đã chuẩn bị tình huống owner, member, outsider, fan-out, chống giả mạo index và người gửi, gỡ thành viên, tìm email có giới hạn và metadata Drive. Kết quả ghi trong các tài liệu cũ của phương án Storage không được cộng vào kết quả phương án Drive; cần chạy npm test trên cấu hình hiện hành để lập biên bản nghiệm thu mới.

**Kịch bản nghiệm thu cloud đề xuất.**

Nhóm sử dụng ba tài khoản: A là chủ chuyến đi, B là thành viên được mời, C là người ngoài chuyến đi. [Bảng 3.9](#bang-3-9) mô tả các tình huống E01–E12 và kết quả mong đợi; các dòng này chưa được xem là kết quả nghiệm thu thực tế.

<a id="bang-3-9"></a>

**Bảng 3.9. Ma trận nghiệm thu ba tài khoản**

| Mã | Thao tác | Kết quả mong đợi |
| --- | --- | --- |
| E01 | A đăng ký, đăng nhập và logout | Profile theo UID; logout về Welcome |
| E02 | A tạo trip mời B đã có tài khoản | Trip, tripAccess và index A B nhất quán |
| E03 | B mở My Trips và chi tiết | Đọc được trip được mời; không cần đọc toàn cây |
| E04 | C ngoài trip truy cập trực tiếp dữ liệu | Rules từ chối trip, chat và file metadata |
| E05 | B sửa lịch trình và gửi chat | Đúng path ngày, UID và timestamp server |
| E06 | A hoặc B upload ảnh và PDF | File Drive tồn tại; metadata RTDB khớp |
| E07 | Upload thành công nhưng ghi metadata bị từ chối | App báo lỗi; quan sát rollback best effort |
| E08 | A gỡ B | Index, favorite và mốc đọc B được dọn; RTDB bị thu hồi |
| E09 | B thử mở file Drive sau khi bị gỡ | Đánh giá quyền Drive riêng, không kỳ vọng tự thu hồi |
| E10 | Mất mạng, token hết hạn hoặc file bị xóa ngoài Drive | Thông báo lỗi và có hướng kết nối hoặc thử lại |
| E11 | Xoay màn hình và tăng cỡ chữ | Không ghi trùng, form và dialog phục hồi |
| E12 | Cố gửi sai MIME, UID, ngày hoặc kích thước | Đối chiếu riêng validation app và rules backend |

**Đánh giá ưu điểm và giới hạn.**

Phiên bản hiện tại đã có luồng sử dụng từ xác thực đến quản lý chuyến đi và chia sẻ nội dung. Các thành phần giao diện được dùng lại giữa nhiều màn hình, dữ liệu chuyến đi được đọc theo chỉ mục của tài khoản, và thao tác lưu file chỉ báo thành công sau khi ghi metadata. UID và thời gian máy chủ giúp kiểm tra danh tính người gửi và sắp xếp dữ liệu nhất quán hơn. Bộ kiểm thử cục bộ hỗ trợ phát hiện lỗi khi chỉnh bố cục hoặc khi Activity được tạo lại.

Vấn đề cần ưu tiên là quyền truy cập file trên Drive chưa tự đồng bộ với danh sách thành viên của chuyến đi. Ngoài ra, ứng dụng chưa có phân trang lịch sử chat/file, chưa dọn đầy đủ ảnh bìa và ảnh đại diện khi lỗi, chưa phục hồi upload sau khi tiến trình bị kết thúc và chưa có giao diện xóa toàn bộ chuyến đi hoặc tài khoản. Truy vấn email đã được giới hạn nhưng chưa có cơ chế giới hạn tần suất để chống dò tìm. Do chưa có khảo sát người dùng hoặc phép đo tải lớn, báo cáo chỉ đánh giá trong phạm vi thử nghiệm hiện có.

**Kết quả và định hướng hoàn thiện.**

**Kết quả đạt được.**

Qua quá trình triển khai, nhóm đã xây dựng được các thành phần chính của một ứng dụng lập kế hoạch du lịch nhóm: tài khoản, chuyến đi, lịch trình, thành viên, trò chuyện và chia sẻ nội dung. Các màn hình kết nối qua kiến trúc Activity, ViewModel và Repository. Figma được dùng làm định hướng giao diện, còn chức năng và dữ liệu được triển khai theo khả năng thực tế của mã nguồn.

Phần thiết kế dữ liệu xác định được chủ chuyến đi, danh sách thành viên, chỉ mục tra cứu và metadata của file. Phương án lưu trữ sử dụng Google Drive thay cho luồng Firebase Storage trước đây. Trước khi dùng báo cáo để nghiệm thu, nhóm cần điền đủ thông tin thành viên, chạy các kịch bản cloud và bổ sung hình chụp với dữ liệu thử nghiệm đã được phép sử dụng.

**Thứ tự hoàn thiện trước khi nộp.**

Ưu tiên đầu tiên là chạy kịch bản ba tài khoản và ghi nhận rules/schema thực trên Firebase, kết quả upload và quyền Drive. Sau đó kiểm tra các tình huống mất mạng, token hết hạn, gỡ thành viên và rollback. Khi có bằng chứng, cập nhật cột kết quả thực tế trong ma trận E01 đến E12; không thay trạng thái bằng nhận định dựa trên ảnh UI.

Ưu tiên tiếp theo là thu hẹp scope Drive và chọn mô hình ACL phù hợp nếu muốn triển khai ngoài nhóm thử nghiệm. Các cải tiến kỹ thuật gồm cleanup file mồ côi, upload có thể tiếp tục, phân trang chat/files, xử lý vị trí chưa xác định và siết validation ở backend. Với UI, thống nhất tên TripTogether và FindTrip trước khi phát hành, sau đó mới bổ sung tính năng mới ngoài phạm vi cơ bản.

**Hướng phát triển dài hạn.**

Sau khi hoàn thiện các luồng hiện tại, nhóm có thể phát triển lịch nhắc, thông báo đẩy, danh sách công việc chuẩn bị và hỗ trợ sử dụng khi mất mạng. Những chức năng như đặt phòng hoặc thanh toán cần được thiết kế thành luồng riêng, có mô hình dữ liệu và cơ chế giao dịch phù hợp. Báo cáo xem đây là hướng phát triển, chưa phải kết quả đã đạt được.

**Đối chiếu thiết kế với mã nguồn.**

<a id="bang-3-10"></a>

**Bảng 3.10. File kiểm chứng theo chủ đề**

| Chủ đề | Đường dẫn trong repository |
| --- | --- |
| Build và phiên bản | app/build.gradle.kts; gradle/libs.versions.toml |
| Điểm vào | app/src/main/AndroidManifest.xml; WelcomeActivity.kt |
| Xác thực | AuthFormActivity.kt; data/AuthRepository.kt; ui/state/AuthViewModel.kt |
| Trip và quyền | data/TripRepository.kt; data/CreateTripRepository.kt |
| Profile | data/UserRepository.kt; model/UserProfile.kt |
| Lịch trình và chat | data/PlanRepository.kt; data/ChatRepository.kt |
| File | data/FilesRepository.kt; drive/DriveApi.kt |
| Phiên và ảnh | drive/DriveSession.kt; DriveLinks.kt; DriveImages.kt |
| Rules và migration | app/firebase/database.rules.json; functions/migration-plan.cjs |
| Kiểm thử | app/src/test; app/src/androidTest; app/firebase/tests |

Các đường dẫn Kotlin rút gọn trong bảng nằm dưới `app/src/main/java/com/example/triptogether`. Thư mục app/docs là tài liệu local và được gitignore. Bản Markdown này được xuất từ báo cáo LaTeX và lưu tại app/report để xem trên repository; PDF và nguồn LaTeX đầy đủ được lưu riêng trong bộ NT118_REPORT local.

**Kịch bản trình diễn.**

Chuẩn bị trước hai tài khoản A và B cùng một tài khoản C để thử từ chối quyền; dữ liệu chỉ chứa tên và nội dung thử nghiệm. Mở Welcome, trình bày hai form và reset email. A tạo trip không ảnh trước để tách lỗi database khỏi lỗi Drive, mời B, sau đó cho B mở My Trips. Thêm một hoạt động và gửi chat để quan sát dữ liệu đồng bộ.

Tiếp theo kết nối Drive với đúng tài khoản, tải một ảnh nhỏ và một PDF thử nghiệm, đối chiếu fileId và metadata. Mở Favorites, Settings và FAQ để minh họa luồng phụ. Cuối cùng A gỡ B và kiểm tra lại RTDB, đồng thời giải thích rằng Drive có lớp quyền riêng. Nếu một bước chưa đạt, ghi lỗi và giới hạn quan sát thay vì dùng ảnh fixture để thay kết quả thực.

**Nguồn đối chiếu và tài liệu tham khảo.**

<a id="tai-lieu-1"></a>

[1] Firebase, “Add Firebase to your Android project.” Tài liệu trực tuyến: [Firebase Android Setup](https://firebase.google.com/docs/android/setup). Truy cập ngày 29/10/2026.

<a id="tai-lieu-2"></a>

[2] Firebase, “Authenticate with Google on Android.” Tài liệu trực tuyến: [Google Sign-In](https://firebase.google.com/docs/auth/android/google-signin). Truy cập ngày 29/10/2026. Nguồn đối chiếu cấu hình Google; dự án hiện dùng FirebaseUI cho luồng đăng nhập này.

<a id="tai-lieu-3"></a>

[3] Firebase, “Read and Write Data on Android.” Tài liệu trực tuyến: [Realtime Database: Read and Write](https://firebase.google.com/docs/database/android/read-and-write). Truy cập ngày 29/10/2026. Tham khảo thao tác đọc, ghi và cập nhật nhiều nhánh.

<a id="tai-lieu-4"></a>

[4] Firebase, “Learn the core syntax of the Realtime Database Security Rules language.” Tài liệu trực tuyến: [Security Rules: Core Syntax](https://firebase.google.com/docs/database/security/core-syntax). Truy cập ngày 29/10/2026. Tham khảo phạm vi và sự kế thừa quyền truy cập.

<a id="tai-lieu-5"></a>

[5] Google for Developers, “Upload file data.” Tài liệu trực tuyến: [Google Drive API: Manage Uploads](https://developers.google.com/workspace/drive/api/guides/manage-uploads). Truy cập ngày 29/10/2026. Tham khảo quy trình khởi tạo phiên tải lên và gửi nội dung file.

<a id="tai-lieu-6"></a>

[6] Google for Developers, “Share files, folders and drives.” Tài liệu trực tuyến: [Google Drive API: Manage Sharing](https://developers.google.com/workspace/drive/api/guides/manage-sharing). Truy cập ngày 29/10/2026. Tham khảo cách quản lý quyền truy cập file và thư mục.

<a id="tai-lieu-7"></a>

[7] “Báo cáo tiến độ đồ án NT118 — Ứng dụng ghi chú,” bản DOCX và PDF trong thư mục ĐỒ ÁN của bộ tài liệu môn học được cung cấp. Tài liệu tham chiếu cho bìa, mục lục và cấu trúc ba chương; thông tin thành viên của nhóm mẫu không được sử dụng làm thông tin tác giả báo cáo này.

<a id="tai-lieu-8"></a>

[8] “TripTogether — mã nguồn dự án,” phiên bản `633e50d`. Trực tuyến: [repository tại phiên bản đối chiếu](https://github.com/khanhbinh-ngo/NT118-UIT-1001DateIdeas-TripTogetherVer1/tree/633e50d). Nguồn đối chiếu gồm AndroidManifest, cấu hình Gradle, repository, model, rules và mã kiểm thử. Kết quả kiểm thử XML và bộ ảnh kiểm tra giao diện được lưu cục bộ; ảnh có dữ liệu mẫu được sử dụng để minh họa bố cục.

**HẾT**
