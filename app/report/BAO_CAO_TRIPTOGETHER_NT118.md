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

> Bản Markdown đầy đủ được xuất từ báo cáo LaTeX có cấu trúc theo mẫu NT118. Nội dung đối chiếu mã nguồn tại revision `633e50d`; cập nhật định dạng Markdown ngày 30/10/2026. Ảnh render/fixture phục vụ minh họa UI, không thay cho nghiệm thu Firebase và Drive thật. Định dạng Markdown dành cho xem trên GitHub; dùng PDF A4 local để in với bố cục đã kiểm tra.

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

<a id="muc-1"></a>

## Chương 1. Giới thiệu.

<a id="muc-1-1"></a>

### 1.1. Lý do chọn đề tài.

Khi tổ chức chuyến đi theo nhóm, thông tin thường nằm rải rác trong đoạn chat, ghi chú cá nhân và các thư mục ảnh. Thành viên khó biết lịch trình mới nhất, danh sách người tham gia hoặc vị trí của tài liệu cần dùng. TripTogether gom những thông tin này theo từng chuyến đi, giúp chủ chuyến đi quản lý nhóm và các thành viên theo dõi kế hoạch chung trên điện thoại.

Đề tài phù hợp với môn Phát triển ứng dụng trên thiết bị di động vì kết hợp bố cục Android, vòng đời Activity, điều hướng bằng Intent, xử lý bất đồng bộ, xác thực và dữ liệu mạng. Phần chia sẻ nội dung còn đặt ra bài toán phân biệt dữ liệu cấu trúc với file nhị phân và phối hợp quyền truy cập giữa hai dịch vụ.

<a id="muc-1-2"></a>

### 1.2. Mục tiêu.

Mục tiêu thứ nhất là hoàn thiện luồng từ xác thực đến tạo và mở chuyến đi. Mục tiêu thứ hai là hỗ trợ cộng tác qua lịch trình theo ngày, quản lý thành viên, chat và thư viện nội dung. Mục tiêu thứ ba là trình bày dữ liệu bằng một hệ thống giao diện thống nhất, có phản hồi khi tải, khi danh sách rỗng và khi thao tác thất bại.

Về kỹ thuật, nhóm tách Activity khỏi thao tác truy cập dữ liệu thông qua ViewModel và Repository, duy trì chỉ mục chuyến đi theo người dùng và sử dụng rules kiểm tra UID. Việc đánh giá tập trung vào hành vi có bằng chứng trong code và kiểm thử, đồng thời nêu rõ những giới hạn chưa được nghiệm thu trên cloud.

**Đối tượng sử dụng và phạm vi.**

Đối tượng sử dụng là người tổ chức và thành viên của một nhóm du lịch nhỏ. Chủ chuyến đi tạo thông tin, mời người đã có tài khoản và thay đổi thành viên. Thành viên được cấp quyền có thể xem chuyến đi, chỉnh hoạt động, trò chuyện và thêm nội dung chia sẻ. Vai trò hiển thị trong hồ sơ cá nhân không phải quyền quản trị hệ thống.

**Bảng 1. Phạm vi chức năng**

| Trong phiên bản cơ bản | Ngoài phạm vi hiện tại |
| --- | --- |
| Email và Google sign in, đăng ký, reset qua email | OTP nhập trong app, đăng nhập Apple hoặc Facebook |
| Trip, thành viên, lịch trình và tìm kiếm | Đặt khách sạn, giỏ hàng, thanh toán và blog |
| Chat, ảnh, PDF và thông báo chưa đọc trong app | FCM push, nhắc lịch nền và phân trang lịch sử cũ |
| Hồ sơ và tùy chọn giao diện sáng tối | Xóa toàn bộ tài khoản hoặc chuyến đi có dọn dữ liệu liên dịch vụ |

**Phương pháp phát triển.**

Quá trình triển khai đi từ kiểm tra code, đối chiếu thiết kế, thống nhất schema và rules đến kiểm thử cục bộ. Figma được dùng để tham khảo cấu trúc, màu sắc, thẻ và nhận diện. Những khái niệm như sản phẩm khách sạn hoặc thanh toán trong thiết kế không được biến thành tính năng giả khi backend chưa hỗ trợ. Bộ báo cáo mô tả sản phẩm đã có, sau đó mới trình bày hướng phát triển.

**Tổng quan sản phẩm và phiên bản báo cáo.**

TripTogether là ứng dụng Android hỗ trợ một nhóm người cùng lập kế hoạch chuyến đi, quản lý thành viên, trao đổi tin nhắn và chia sẻ ảnh hoặc tài liệu. Nhóm xây dựng sản phẩm bằng Kotlin và giao diện XML, sử dụng Firebase Authentication cho danh tính, Firebase Realtime Database cho dữ liệu nghiệp vụ và Google Drive cho nội dung file. Trọng tâm là một ứng dụng lập kế hoạch du lịch cơ bản có luồng sử dụng rõ ràng và cấu trúc dữ liệu phù hợp với quyền thành viên.

Phiên bản được mô tả có 24 Activity khai báo trong manifest. Người dùng bắt đầu ở màn Welcome, chọn đăng nhập hoặc đăng ký, sau đó truy cập Home, My Trips, Favorites và Account. Các màn lịch trình, thành viên, chat, ảnh, tài liệu, bản đồ, trợ giúp và thông báo phục vụ từng chuyến đi. Thiết kế tham khảo bản Figma cuối và được chuyển thành các thành phần Android native thay vì dùng ảnh chụp làm giao diện.

Kết quả cục bộ đã lưu gồm 6 unit test và 7 bài kiểm thử Android không có lỗi. Bộ 6 unit test gồm 5 ca kiểm tra chức năng và 1 ca kiểm tra mẫu. Kiểm thử Android xác nhận điều hướng, validation, recreation, layout và chế độ tối; các kết quả này không thay thế nghiệm thu đa tài khoản trên Firebase và Google Drive thật. Những hạng mục cloud cần xác minh và giới hạn còn lại được trình bày ở phần kiểm thử, đánh giá và định hướng trong mục 3.2.

**Bảng 2. Thông tin kỹ thuật của phiên bản báo cáo**

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

**Bảng 3. Phiên bản trích từ cấu hình dự án**

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

Đây là phiên bản khai báo trong dự án, không phải khẳng định tất cả đều là phiên bản mới nhất. Alias firebase-storage còn tồn tại trong catalog nhưng không được thêm vào dependencies của app. Phương án đang chạy không dùng Firebase Storage, Firestore hoặc Cloud Functions cho lưu file; các file hỗ trợ phương án cũ vẫn nằm trong thư mục firebase nhưng không được triển khai bởi cấu hình active.

<a id="muc-2-2"></a>

### 2.2. Các tính năng của ứng dụng.

**Bảng 4. Danh mục yêu cầu chức năng**

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

Giao diện cần có vùng chạm đủ lớn, form cuộn được khi bàn phím xuất hiện và nội dung không bị ép thành thanh ngang khi chữ lớn. Lỗi mạng hoặc permission phải được phản hồi thay vì báo thành công trước khi backend xác nhận. Không ghi mật khẩu hay access token vào database. Những dữ liệu dùng làm fixture phục vụ kiểm tra layout chỉ nằm trong source debug và không phải dữ liệu nghiệp vụ của release.

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

Điều kiện trước là người dùng đã đăng nhập; nếu chọn ảnh bìa, tài khoản Google còn phải có quyền Drive phù hợp. App kiểm tra ngày hợp lệ và thứ tự ngày, tìm UID theo email mời đã chuẩn hóa, tạo tripId và gọi geocoding. Sau khi upload ảnh bìa tùy chọn, TripRepository thực hiện một updateChildren ghi trip, tripAccess và userTrips cho mọi thành viên. Chỉ khi thao tác RTDB được xác nhận mới hoàn tất ca sử dụng [3].

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

**Bảng 5. Màn hình và thao tác chính**

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

*Hình 1. Màn chọn xác thực và Home với dữ liệu minh họa*

Welcome có hai hành động chính giúp người mới phân biệt đăng nhập và tạo tài khoản. Home ưu tiên tìm kiếm, bộ lọc thời gian và các card có ảnh. My Trips dùng lại layout chung nhưng đổi ngữ cảnh sang danh sách trip của người dùng. Thẻ không hiển thị giá hay nút đặt phòng vì dữ liệu hiện tại là chuyến đi, không phải catalog bán dịch vụ.

<p align="center">
  <img src="assets/ui/activity_favorites.png" width="280" alt="Yêu thích và tổng quan chuyến đi — ảnh 1" />
  <img src="assets/ui/activity_trip_summary.png" width="280" alt="Yêu thích và tổng quan chuyến đi — ảnh 2" />
</p>

*Hình 2. Yêu thích và tổng quan chuyến đi*

Favorites dùng thẻ gọn với ảnh bên trái và thông tin màu kem trên nền xanh. Trip summary ưu tiên ảnh bìa, thông tin ngày đi và các lối vào kế hoạch hoặc nội dung của trip. Các khối trạng thái lỗi, rỗng và thử lại được đặt trong vùng có thể cuộn để không ép nút retry khi màn hình thấp.

**Tài khoản và hỗ trợ.**

<p align="center">
  <img src="assets/ui/activity_account.png" width="280" alt="Account và FAQ khi chạy HelpSupportActivity — ảnh 1" />
  <img src="assets/ui/activity_help_support_live.png" width="280" alt="Account và FAQ khi chạy HelpSupportActivity — ảnh 2" />
</p>

*Hình 3. Account và FAQ khi chạy HelpSupportActivity*

Account giữ các mục Profile, Settings, My Trips, Help và Logout. Nút kết nối Drive là chức năng bổ sung phục vụ lưu file, không phải quyền được cấp tự động bởi Firebase. Edit Profile lưu tên, họ, role mô tả, email và avatar; các trường điện thoại, địa chỉ, thành phố và khu vực trong một số frame Figma chưa được thêm vào model. Settings có light, dark và system; chưa có cơ chế đổi tiền tệ hoặc đa ngôn ngữ hoàn chỉnh.

**Chế độ tối và sai khác có chủ đích.**

<p align="center">
  <img src="assets/ui/activity_main_dark_large.png" width="280" alt="Kiểm tra night mode với fontScale 1.3 — ảnh 1" />
  <img src="assets/ui/activity_settings_dark_large.png" width="280" alt="Kiểm tra night mode với fontScale 1.3 — ảnh 2" />
</p>

*Hình 4. Kiểm tra night mode với fontScale 1.3*

Các ảnh night mode đồng thời dùng fontScale 1.3 để quan sát khả năng xuống dòng. Grid Home giữ chiều cao card thống nhất thay cho masonry của catalog Figma. Những màn giỏ hàng, thanh toán, blog, OTP và carousel onboarding chưa có chức năng tương ứng. Bản triển khai không đưa các nút không có nghiệp vụ vào luồng người dùng chỉ để giống ảnh thiết kế.

**Giao diện tạo chuyến đi và lịch trình.**

<p align="center">
  <img src="assets/ui/activity_add_trip.png" width="280" alt="Giao diện tạo chuyến đi và lịch trình; ảnh render cục bộ — ảnh 1" />
  <img src="assets/ui/activity_trip_plan.png" width="280" alt="Giao diện tạo chuyến đi và lịch trình; ảnh render cục bộ — ảnh 2" />
</p>

*Hình 5. Giao diện tạo chuyến đi và lịch trình; ảnh render cục bộ*

**Giao diện quản lý thành viên.**

<p align="center">
  <img src="assets/ui/activity_friends.png" width="280" alt="Giao diện quản lý thành viên; ảnh render cục bộ — ảnh 1" />
  <img src="assets/ui/activity_edit_friends.png" width="280" alt="Giao diện quản lý thành viên; ảnh render cục bộ — ảnh 2" />
</p>

*Hình 6. Giao diện quản lý thành viên; ảnh render cục bộ*

**Giao diện chat và thư viện nội dung.**

<p align="center">
  <img src="assets/ui/activity_group_chat.png" width="280" alt="Giao diện chat và thư viện nội dung; ảnh render cục bộ — ảnh 1" />
  <img src="assets/ui/activity_documents_home.png" width="280" alt="Giao diện chat và thư viện nội dung; ảnh render cục bộ — ảnh 2" />
</p>

*Hình 7. Giao diện chat và thư viện nội dung; ảnh render cục bộ*

**Giao diện ảnh và tài liệu PDF.**

<p align="center">
  <img src="assets/ui/activity_images_list.png" width="280" alt="Giao diện ảnh và tài liệu PDF; ảnh render cục bộ — ảnh 1" />
  <img src="assets/ui/activity_documents.png" width="280" alt="Giao diện ảnh và tài liệu PDF; ảnh render cục bộ — ảnh 2" />
</p>

*Hình 8. Giao diện ảnh và tài liệu PDF; ảnh render cục bộ*

**Giao diện xem ảnh và bản đồ.**

<p align="center">
  <img src="assets/ui/activity_image_preview.png" width="280" alt="Giao diện xem ảnh và bản đồ; ảnh render cục bộ — ảnh 1" />
  <img src="assets/ui/activity_trip_map.png" width="280" alt="Giao diện xem ảnh và bản đồ; ảnh render cục bộ — ảnh 2" />
</p>

*Hình 9. Giao diện xem ảnh và bản đồ; ảnh render cục bộ*

**Giao diện hồ sơ và tìm kiếm.**

<p align="center">
  <img src="assets/ui/activity_edit_profile.png" width="280" alt="Giao diện hồ sơ và tìm kiếm; ảnh render cục bộ — ảnh 1" />
  <img src="assets/ui/activity_search_results.png" width="280" alt="Giao diện hồ sơ và tìm kiếm; ảnh render cục bộ — ảnh 2" />
</p>

*Hình 10. Giao diện hồ sơ và tìm kiếm; ảnh render cục bộ*

**Giao diện cài đặt ban ngày và thông báo.**

<p align="center">
  <img src="assets/ui/activity_settings.png" width="280" alt="Giao diện cài đặt ban ngày và thông báo; ảnh render cục bộ — ảnh 1" />
  <img src="assets/ui/activity_notifications.png" width="280" alt="Giao diện cài đặt ban ngày và thông báo; ảnh render cục bộ — ảnh 2" />
</p>

*Hình 11. Giao diện cài đặt ban ngày và thông báo; ảnh render cục bộ*

**Giao diện đăng nhập và khôi phục mật khẩu.**

<p align="center">
  <img src="assets/ui/activity_login.png" width="280" alt="Giao diện đăng nhập và khôi phục mật khẩu; ảnh render cục bộ — ảnh 1" />
  <img src="assets/ui/activity_forgot_password.png" width="280" alt="Giao diện đăng nhập và khôi phục mật khẩu; ảnh render cục bộ — ảnh 2" />
</p>

*Hình 12. Giao diện đăng nhập và khôi phục mật khẩu; ảnh render cục bộ*

Form đăng ký được triển khai bằng RegisterActivity, dùng chung AuthFormActivity với màn đăng nhập nhưng có tên người dùng và xác nhận mật khẩu. Bộ ảnh hiện có chưa có ảnh Register riêng nên báo cáo mô tả luồng từ code; hình đăng nhập không được gắn nhãn thành hình đăng ký. Các trường runtime trống trong ảnh render chỉ phản ánh bộ fixture, không chứng minh dữ liệu cloud bị mất.

<a id="muc-3"></a>

## Chương 3. Phân tích thiết kế hệ thống.

<a id="muc-3-1"></a>

### 3.1. Sơ đồ phân rã chức năng.

<p align="center">
  <img src="assets/diagrams/functions.png" width="760" alt="Các nhóm chức năng của TripTogether — ảnh 1" />
</p>

*Hình 13. Các nhóm chức năng của TripTogether*

**Kiến trúc ứng dụng.**

<p align="center">
  <img src="assets/diagrams/architecture.png" width="760" alt="Kiến trúc ứng dụng và các dịch vụ bên ngoài — ảnh 1" />
</p>

*Hình 14. Kiến trúc ứng dụng và các dịch vụ bên ngoài*

Lớp giao diện gồm Activity, layout XML và adapter RecyclerView. Activity tiếp nhận thao tác, điều hướng và hiển thị trạng thái. ViewModel giữ trạng thái và chạy công việc trong viewModelScope. Repository triển khai truy vấn hoặc thao tác ghi với Firebase và Drive. Phần backend là dịch vụ được quản lý kết hợp Security Rules; không có máy chủ REST riêng đang vận hành cho phiên bản này.

Các contract trong DataSources.kt mô tả khả năng của từng repository. UiState biểu diễn Loading, Content và Error; danh sách rỗng là Content với tập dữ liệu rỗng chứ không phải một enum Empty riêng. ActionState gồm Idle, Running, Done và Error. ActionViewModel chặn thao tác ghi lặp khi đang Running và giữ công việc qua Activity recreation, nhưng không bảo đảm tiếp tục sau khi tiến trình bị hệ điều hành kết thúc.

**Bảng 6. Các lớp chính và trách nhiệm**

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

Realtime Database lưu cây JSON; các node không phải bảng quan hệ có khóa ngoại tự động. Firebase Auth quản lý mật khẩu và UID, còn database lưu profile cùng dữ liệu trip. Quan hệ nhiều người dùng tham gia nhiều trip được biểu diễn bằng tripAccess và userTrips. Sơ đồ dưới là quan hệ nghiệp vụ; tính nhất quán do code ghi nhiều nhánh và Security Rules duy trì.

<p align="center">
  <img src="assets/diagrams/data-relations.png" width="760" alt="Quan hệ logic giữa tài khoản chuyến đi và nội dung — ảnh 1" />
</p>

*Hình 15. Quan hệ logic giữa tài khoản chuyến đi và nội dung*

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

userTrips giúp app biết đường dẫn creatorUid để đọc từng trip được cấp quyền thay vì tải toàn bộ /trips. favorites lưu tham chiếu creatorUid thay cho bản sao trip, nên nội dung hiển thị được đọc lại từ nguồn chính. Một số nhánh đọc vẫn dung nạp index boolean hoặc snapshot cũ; điều này không thay thế migration đầy đủ cho người được mời.

**Từ điển dữ liệu người dùng và chuyến đi.**

**Bảng 7. Profile dưới users uid**

| Trường | Kiểu | Ý nghĩa |
| --- | --- | --- |
| email | string | Email hiện tại được đồng bộ từ Auth |
| emailNormalized | string | Email viết thường để query bằng equality |
| name | string | Tên hiển thị, rules giới hạn 150 ký tự |
| firstName và lastName | string tùy chọn | Tên và họ được chỉnh trong hồ sơ |
| role | string tùy chọn | Mô tả cá nhân, không phải ACL |
| photoUrl | string tùy chọn | Ảnh từ provider hoặc profile ban đầu |
| imageUrl | string tùy chọn | Ảnh đã chỉnh, ưu tiên khi hiển thị |

**Bảng 8. Trip dưới trips creatorUid tripId**

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

**Bảng 9. Các node quyền và tham chiếu**

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

**Bảng 10. Các trường dùng chung cho chat và file**

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

**Bảng 11. Quyền theo vai trò của một trip**

| Tác vụ | Owner | Thành viên | Ngoài trip |
| --- | --- | --- | --- |
| Đọc trip và nội dung | Có | Có | Không |
| Thêm hoặc gỡ thành viên | Có | Không | Không |
| Sửa lịch trình | Có | Có | Không |
| Gửi chat và thêm metadata file | Có | Có | Không |
| Yêu thích trip đang được cấp quyền | Có | Có | Không |
| Gỡ owner khỏi membership | Không | Không | Không |
| Xóa toàn trip hoặc ACL qua UI | Chưa hỗ trợ | Chưa hỗ trợ | Không |

Node gốc đặt .read và .write là false. Quyền được mở tại đúng path theo UID hoặc membership. Rules không tự lọc các node con cho một lần đọc node cha; do đó app đọc theo index và từng trip, không mở quyền đọc toàn cây để chữa lỗi tải dữ liệu [4]. Hồ sơ users/uid hiện đọc được bởi tài khoản đã xác thực; truy vấn cả users chỉ được cho phép với emailNormalized bằng một giá trị và limitToFirst bằng 1. Đây là danh bạ có xác thực, chưa phải hồ sơ riêng tư chỉ chia sẻ trong trip.

Một quyền ghi được cấp ở node cha có thể áp dụng tới node con. Vì owner được ghi ở cấp trip, không nên mô tả validation phía client là bảo đảm backend tuyệt đối cho mọi trường con. Code có validation giờ và ngày, rules kiểm tra định dạng và một số chiều dài; mức bao phủ với client độc hại cần được kiểm thử và siết tiếp. Báo cáo tách rõ kiểm tra UI khỏi ràng buộc bảo mật máy chủ.

**Migration và tương thích dữ liệu cũ.**

Công cụ migrate.cjs và migration-plan.cjs chuẩn bị chuyển boolean userTrips sang creator reference, chuẩn hóa email, dựng tripAccess từ dữ liệu cũ khi chưa có ACL, và cập nhật favorites. Không khôi phục thành viên đã bị thu hồi nếu ACL mới đã tồn tại. Script dry-run mặc định; chế độ apply cần quyền quản trị và lưu backup trước update. Code không xác nhận công cụ này đã chạy trên project cloud; báo cáo không xem dữ liệu thực đã được chuyển đổi xong.

**Lý do tách database và file.**

Database phù hợp với trường dữ liệu nhỏ, truy vấn và đồng bộ trạng thái. Ảnh và PDF cần nơi lưu bytes riêng; đưa nội dung nhị phân dạng base64 vào RTDB sẽ làm tăng kích thước và băng thông. Phiên bản thử nghiệm chọn Google Drive để đáp ứng cách vận hành đã thống nhất của dự án. Lựa chọn này không đồng nghĩa các giới hạn dung lượng, quota hoặc chính sách OAuth biến mất.

**Luồng upload và lưu metadata.**

<p align="center">
  <img src="assets/diagrams/upload-flow.png" width="760" alt="Các bước lưu file và nhánh phục hồi khi metadata lỗi — ảnh 1" />
</p>

*Hình 16. Các bước lưu file và nhánh phục hồi khi metadata lỗi*

DriveApi kiểm tra folder đúng kiểu, tài khoản có canAddChildren và toàn bộ các trang permissions không có anyone hoặc domain. File được đọc vào cache riêng với giới hạn kích thước; sau đó app khởi tạo resumable upload và gửi bytes bằng một PUT. Session URL phải có origin Google API hợp lệ, redirect tự động bị tắt và access token chỉ gửi tới origin được kiểm tra. File staging được xóa trong finally khi thao tác kết thúc.

Khi upload thành công, FilesRepository ghi metadata vào tripImages hoặc tripDocuments. Nếu ghi database lỗi, code thử xóa file vừa tạo trong một tác vụ NonCancellable có timeout 5 giây, rồi ném lại lỗi. Rollback này là best effort và không tạo một transaction chung giữa Drive với RTDB. Với cover và avatar, nhánh upload riêng chưa có cleanup đầy đủ khi bước ghi hồ sơ hoặc trip thất bại.

**Token và tải ảnh.**

DriveSession giữ access token trong RAM, gắn với UID hiện tại và đặt thời hạn nội bộ 50 phút. Khi đổi UID, hết hạn hoặc logout, token bị xóa; đây là giới hạn quản lý phiên của app, không phải lời khẳng định thời hạn token Google luôn bằng 50 phút. Process death làm mất phiên này và người dùng cần kết nối lại. Token không được lưu trong RTDB hoặc URL metadata.

DriveLinks chỉ nhận URL HTTPS có host và path media đúng dạng Drive API. DriveImages dùng Authorization cho URL hợp lệ và tắt cache ảnh Drive trong Glide. PDF được mở qua link Drive riêng tư bằng ứng dụng hoặc trình duyệt thích hợp. Tài khoản đang đăng nhập ở trình duyệt có thể khác tài khoản đã cấp OAuth trong app; cần kiểm tra khi nghiệm thu mở PDF.

**Giới hạn quyền Drive trong phương án thử nghiệm.**

Thư mục Drive dùng chung không tạo ACL tách biệt tự động cho mỗi trip. Thành viên bị gỡ khỏi trip sẽ mất quyền đọc metadata RTDB theo rules, nhưng quyền Drive do Google quản lý không tự bị thu hồi. Nếu họ vẫn có quyền thư mục hoặc đã tải file, dữ liệu có thể tiếp tục truy cập được. Bản thử nghiệm yêu cầu folder Restricted và chia sẻ theo email cho tài khoản thử nghiệm; không dùng quyền anyone with the link [6].

Scope đang xin là <https://www.googleapis.com/auth/drive>, rộng hơn nhu cầu tối thiểu của nhiều sản phẩm. Đây là cấu hình thử nghiệm hiện có, cần thu hẹp qua phương án chọn file/folder và đánh giá lại OAuth trước phát hành. Không đưa folder ID thật, access token hoặc file credential vào báo cáo; các ví dụ dùng placeholder.

**Chuẩn bị môi trường Android.**

```text
# From the repository root
bash app/run-project.sh check
bash app/run-project.sh run

# Core verification tasks
bash gradlew :app:assembleDebug :app:testDebugUnitTest :app:lintDebug
bash gradlew :app:connectedDebugAndroidTest
```

Mở repository root trong Android Studio, chọn Gradle JDK 17 hoặc 21, cài Android SDK Platform 35 và tạo emulator có Google Play services để thử Google sign in hoặc Maps. Thiết bị tối thiểu theo cấu hình là API 26. Kiểm tra SDK path trong local.properties. Lệnh run build, cài APK và mở WelcomeActivity, không mở thẳng form Login cũ.

**Thiết lập Firebase cho project.**

Bước 1 là tạo hoặc chọn Firebase project, thêm ứng dụng Android đúng package com.example.triptogether và đăng ký SHA của key ký thực tế. Dùng signingReport để lấy fingerprint, không đổi applicationId thành tên hiển thị TripTogether. Bước 2 là bật Email/Password và Google trong Authentication rồi tải google-services.json mới về app. Trong cấu hình đã đọc của phiên bản này, project ID là triptogetherv1 và RTDB URL thuộc khu vực asia-southeast1 [1][2].

Bước 3 là tạo Realtime Database, đối chiếu database URL trong file cấu hình và backup dữ liệu/rules trước thay đổi. Bước 4 là kiểm tra schema hiện có; dữ liệu legacy cần dry-run migration trước khi apply. Bước 5 là chạy emulator test và deploy riêng Database theo firebase.json đang active. Không triển khai Storage, Firestore hay Functions chỉ vì các file cũ còn tồn tại trong thư mục.

```text
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

```text
# app/drive.local.properties  local only
DRIVE_FOLDER_ID=<restricted_test_folder_id>

# local.properties or Gradle properties  local only
sdk.dir=<android_sdk_path>
MAPS_API_KEY=<restricted_android_maps_key>
GEOCODING_API_KEY=<appropriate_geocoding_key>
```

Maps và geocoding là hai luồng dùng key khác nhau trong cấu hình. Không có Maps key thì UI báo thiếu key; geocoding thất bại có thể để tọa độ 0,0 và màn bản đồ không đặt marker. Cần bật API tương ứng và kiểm tra giới hạn của từng key cho đúng loại request, đồng thời không ghi giá trị thật vào tài liệu được chia sẻ.

**Dữ liệu lưu ở đâu.**

**Bảng 12. Phân bố dữ liệu và thời gian tồn tại**

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

Báo cáo phân biệt bằng chứng tự động, kiểm tra layout và nghiệm thu cloud. Các test XML lưu trong build có timestamp ngày 15 tháng 10 năm 2026 theo giờ Việt Nam, không phải một lần chạy cloud ngày lập báo cáo. Lịch commit đã được đặt lại phục vụ tổ chức mã nguồn và không được dùng làm thời điểm thực hiện kiểm thử. Việc code có tính năng không đồng nghĩa toàn bộ dịch vụ thực đã được cấu hình và xác minh thành công.

**Bảng 13. Kết quả có bằng chứng cục bộ**

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

**Bảng 14. Ma trận nghiệm thu ba tài khoản**

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

Ưu điểm của phiên bản là luồng sử dụng đã được tách rõ, nhiều thành phần UI dùng chung, dữ liệu trip không cần đọc toàn cây và việc upload không báo thành công trước bước ghi metadata. Danh tính dựa trên UID và timestamp máy chủ giúp hạn chế các sai lệch khi client tự ghi thông tin. Bộ test cục bộ tạo nền cho việc sửa giao diện mà vẫn kiểm tra recreation.

Giới hạn quan trọng nhất là ACL Drive chưa đồng bộ với membership trip. Các giới hạn khác gồm thiếu phân trang cũ, thiếu cleanup hoàn chỉnh cho cover/avatar, chưa phục hồi upload sau process death và chưa có UI xóa toàn trip/tài khoản. Lookup email được giới hạn query nhưng chưa có rate limit chống dò. Không có số liệu khảo sát người dùng hoặc đo hiệu năng tải lớn để suy rộng tính sẵn sàng phát hành.

**Kết quả và định hướng hoàn thiện.**

**Kết quả đạt được.**

TripTogether đã hình thành một ứng dụng Android cơ bản phục vụ lập kế hoạch du lịch theo nhóm. Phiên bản kết nối các màn xác thực, danh sách trip, lịch trình, thành viên và chia sẻ nội dung trên một kiến trúc Activity, ViewModel và Repository. Figma đóng vai trò định hướng trình bày trong khi dữ liệu và nghiệp vụ bám theo phần code thực tế.

Thiết kế database làm rõ owner, membership chuẩn, chỉ mục và metadata. Phương án lưu file đã chuyển sang Drive và không yêu cầu bật toàn bộ phương án Storage cũ để chạy luồng active. Bản báo cáo này có thể dùng cho trình bày môn học sau khi điền thông tin nhóm, hoàn tất nghiệm thu cloud và bổ sung ảnh có dữ liệu thực đã được phép sử dụng.

**Thứ tự hoàn thiện trước khi nộp.**

Ưu tiên đầu tiên là chạy kịch bản ba tài khoản và ghi nhận rules/schema thực trên Firebase, kết quả upload và quyền Drive. Sau đó kiểm tra các tình huống mất mạng, token hết hạn, gỡ thành viên và rollback. Khi có bằng chứng, cập nhật cột kết quả thực tế trong ma trận E01 đến E12; không thay trạng thái bằng nhận định dựa trên ảnh UI.

Ưu tiên tiếp theo là thu hẹp scope Drive và chọn mô hình ACL phù hợp nếu muốn triển khai ngoài nhóm thử nghiệm. Các cải tiến kỹ thuật gồm cleanup file mồ côi, upload có thể tiếp tục, phân trang chat/files, xử lý vị trí chưa xác định và siết validation ở backend. Với UI, thống nhất tên TripTogether và FindTrip trước khi phát hành, sau đó mới bổ sung tính năng mới ngoài phạm vi cơ bản.

**Hướng phát triển dài hạn.**

Các khả năng như lịch nhắc nền, thông báo push, danh sách việc chuẩn bị và đồng bộ offline có thể được xem xét ở giai đoạn sau. Đặt phòng hoặc thanh toán đòi hỏi mô hình dữ liệu, nhà cung cấp và luồng giao dịch riêng, vì vậy không ghép vào báo cáo như phần đã hoàn thành. Mọi mở rộng cần có tiêu chí nghiệm thu và ranh giới quyền rõ ràng trước khi thêm giao diện.

**Đối chiếu thiết kế với mã nguồn.**

**Bảng 15. File kiểm chứng theo chủ đề**

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

[1] Firebase. Add Firebase to your Android project. <https://firebase.google.com/docs/android/setup>. Truy cập ngày 29 tháng 10 năm 2026.

[2] Firebase. Authenticate with Google on Android. <https://firebase.google.com/docs/auth/android/google-signin>. Dùng để đối chiếu cấu hình provider; dự án hiện dùng FirebaseUI cho luồng Google.

[3] Firebase. Read and Write Data on Android. <https://firebase.google.com/docs/database/android/read-and-write>. Tham khảo thao tác đọc ghi và update nhiều nhánh.

[4] Firebase. Learn the core syntax of the Realtime Database Security Rules language. <https://firebase.google.com/docs/database/security/core-syntax>. Tham khảo phạm vi quyền đọc ghi và kế thừa rules.

[5] Google Developers. Upload file data. <https://developers.google.com/workspace/drive/api/guides/manage-uploads>. Đối chiếu việc khởi tạo upload và gửi bytes.

[6] Google Developers. Share files folders and drives. <https://developers.google.com/workspace/drive/api/guides/manage-sharing>. Đối chiếu permissions của Drive. Các nguồn [2] đến [6] được kiểm tra ngày 29 tháng 10 năm 2026.

[7] Tài liệu môn học được cung cấp. Báo cáo tiến độ đồ án NT118 về ứng dụng ghi chú, bản DOCX và PDF trong thư mục ĐỒ ÁN của gói tài liệu môn học. Tham khảo bố cục bìa, mục lục, giới thiệu, công nghệ, giao diện và thiết kế hệ thống; không sử dụng thông tin nhóm mẫu như tác giả của TripTogether.

[8] Mã nguồn TripTogether, revision 633e50d. Bằng chứng phiên bản gồm manifest, Gradle catalog, repositories, model, rules, test source và test XML local. Bộ ảnh UI review là render có fixture; bản Figma cuối được dùng làm tham chiếu trình bày.

**HẾT**
