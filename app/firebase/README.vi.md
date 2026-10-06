# Đồng bộ Firebase với code TripTogether

**Phương án đang dùng đã chuyển sang Google Drive + Realtime Database.** Không triển khai phần Storage/Firestore/Functions ở hướng dẫn cũ bên dưới. Xem tài liệu Google Drive setup để biết cấu hình OAuth và metadata mới.


Các file trong thư mục này là bản triển khai được chuẩn bị cho project `triptogetherv1`. Chưa publish lên cloud, chưa chạy migration cloud và chưa kiểm thử E2E hai tài khoản trên cloud. Không dùng rules cũ chuyến đi cá nhân với bản code mới này.

## Schema thực thi

```text
users/{uid}                       profile + emailNormalized
trips/{creatorUid}/{tripId}        trip metadata + legacy members list + plan/dayN
tripAccess/{tripId}
  creatorUid                      immutable
  members/{uid}: true             canonical membership
  updatedAt                       server timestamp
  revision                        0 on creation, atomic increment on changes
userTrips/{uid}/{tripId}: creatorUid
favorites/{uid}/{tripId}: creatorUid
chats/{tripId}/{messageId}         senderUid + sender email + server timestamp
lastSeenChat/{uid}/{tripId}        monotonic timestamp
tripImages|tripDocuments/{tripId}/{itemId}
  uploadedByUid + storagePath + contentType + sizeBytes + server timestamp
```

`Trip.members` khi đọc được lấy từ tripAccess; list cũ bên trong trip chỉ phục vụ tương thích, không cấp quyền và không được dùng cho migration nếu tripAccess đã có. Membership cập nhật theo UID riêng, không đọc rồi ghi lại cả list. Index userTrips phục vụ truy vấn; tripAccess là nguồn quyền. Owner không được xóa khỏi membership. Không đọc toàn /trips hoặc /users; lookup email phải query chính xác, limitToFirst(1).

Profiles được coi là danh bạ người dùng đã đăng nhập: đọc từng UID được phép, đọc toàn danh bạ chỉ cho query email chính xác. Không có số điện thoại/tài liệu riêng tư trong profile hiện tại. Đây không phải cơ chế chống dò email: nếu sản phẩm cần riêng tư mạnh hơn, chuyển lookup sang backend có rate limit và chỉ trả hồ sơ của người cùng trip.

## Luồng đã chỉnh

- Tạo trip và ACL/index cho mọi thành viên trong một updateChildren.
- My Trips/Search tải từng trip bằng creator reference, gồm trip được mời.
- Favorites lưu reference, đọc nguồn mới nhất, bỏ qua trip mất quyền; không hiển thị bản sao cũ như nguồn thật.
- Chủ trip thêm/xóa thành viên; revoke dọn index, favorite và mốc đọc của người bị xóa trong cùng update.
- Thành viên đọc trip, sửa lịch trình, gửi chat, chia sẻ ảnh/PDF; người ngoài bị từ chối.
- Chat kiểm tra senderUid/email, server timestamp; listener giới hạn 100 tin gần nhất. Chưa có nút tải lịch sử cũ.
- File metadata lưu uploader UID/path/MIME/size; listener giới hạn 100 file gần nhất. Chưa có trang tải các file cũ hơn.
- Upload metadata thất bại có rollback file best-effort. Process chết hoặc mất mạng có thể để file mồ côi; chưa có job cleanup định kỳ.
- Create Trip phân biệt lỗi quyền Database và Storage; không báo lưu thành công nếu update thất bại.

## Storage: vì sao cần Firestore và Functions

Storage rules không đọc membership RTDB trực tiếp. `functions/index.js` có RTDB trigger `syncTripAccess`, copy ACL sang Firestore `tripAccess/{tripId}`. App không được đọc/ghi các document ACL này; Admin SDK cập nhật. Revision ngăn event cũ ghi đè ACL mới. Storage rules kiểm tra ACL bằng firestore.get.

Cần tạo Firestore database **(default)**, bật billing phù hợp để deploy Functions/Storage và cấp quyền liên kết Storage–Firestore theo yêu cầu Console. Đây là thành phần mới của phương án triển khai; không phải thay đổi tự động trên project.

Đồng bộ ACL có độ trễ. Vừa tạo/mời/xóa thành viên, quyền SDK Storage chỉ cập nhật sau trigger. Nếu upload bị từ chối ngay sau tạo trip, đợi đồng bộ rồi thử lại. Không có đảm bảo thu hồi tức thời liên dịch vụ.

Cover mới upload vào `tripDraftCovers/{ownerUid}/{tripId}.jpg` trước khi trip tồn tại; chỉ owner upload/read SDK. App chia sẻ cover qua download URL lưu trong trip. Ảnh/profile/document mở qua download URL có bearer token: người đã có URL có thể tiếp tục dùng URL sau khi mất membership. Rules thu hồi SDK access không tự thu hồi token. Nếu yêu cầu thu hồi cả URL, cần backend phục vụ link ngắn hạn hoặc thu hồi token và thay cơ chế mở ảnh/file; bản này không cam kết điều đó.

MIME và size được kiểm tra, không kiểm chứng magic bytes/nội dung file. Các đường dẫn cover cũ `tripProfileImages/{tripId}.jpg` bị đóng trong rules mới; dữ liệu cũ qua URL vẫn có thể hoạt động, không tự di chuyển file binary.

## Thứ tự triển khai

1. Sao lưu RTDB và ghi lại rules cũ; không chạy app cũ trong lúc chuyển đổi dữ liệu.
2. Xác nhận Email/Password + Google hoạt động, JSON app đúng project và URL.
3. Tạo Firestore (default), xác nhận billing/bucket. Cung cấp rules Storage đang dùng để đối chiếu trước khi thay.
4. Từ thư mục này, dùng Node 22 (máy hiện tại là Node 26; các kiểm thử đã chạy nhưng npm có cảnh báo engine):

```bash
npm ci
npm --prefix functions ci
npx firebase login
npx firebase use triptogetherv1
```

5. Deploy Firestore ACL deny-client rules và hàm đồng bộ trước:

```bash
npx firebase deploy --only firestore:rules,functions:syncTripAccess --project triptogetherv1
```

6. Migration được chuẩn bị tại `functions/migrate.cjs`. Dùng Application Default Credentials có quyền project; không đặt service-account key trong app/Git. Script luôn dry-run mặc định, và khi --apply sẽ ghi backup cục bộ trước update. Backup có dữ liệu cá nhân, đã được gitignore.

```bash
node functions/migrate.cjs
node functions/migrate.cjs --apply
```

Chỉ chạy --apply sau khi xem báo cáo dry-run, có backup và xác nhận không có dữ liệu bị ghi đồng thời. Script chuyển boolean index thành creator reference, dựng membership từ dữ liệu cũ khi chưa có ACL, chuẩn hóa email lookup, chuyển favorites và dọn stale indexes. Không xóa lịch trình/chat/files. Tin nhắn/metadata cũ được giữ nguyên để đọc; rules mới chỉ yêu cầu UID cho lần ghi mới. Script chưa chạy trên cloud.

7. Kiểm tra Firestore ACL đã xuất hiện và revision khớp RTDB. Sau đó triển khai Database + Storage rules và chạy APK mới:

```bash
npx firebase deploy --only database,storage --project triptogetherv1
```

`database.rules.json` có thể copy vào RTDB Console nếu không dùng CLI, nhưng vẫn phải chuyển dữ liệu và triển khai phần Storage theo thứ tự trên. Không chỉ publish database rules rồi bỏ qua schema/index.

## Kiểm thử

```bash
npm test
```

Lệnh dùng project demo, Database/Firestore/Storage Emulator, không truy cập dữ liệu cloud. Tests gồm owner/member/outsider, update nhiều nhánh, chống spoof index/sender/uploader, revoke, bounded email lookup, plan validation và MIME/Storage access. Test Storage seed ACL trực tiếp bằng môi trường privileged; chưa kiểm thử live trigger và độ trễ đồng bộ trên cloud. Functions đã kiểm tra cú pháp; migration đã kiểm tra cú pháp nhưng chưa thực thi.

Kiểm thử cloud bắt buộc sau deploy: A/B tạo trip có/không cover, mời/xóa, login lại, My Trips/Search/Favorites, lịch trình, chat/unread, ảnh/PDF, MIME sai, mất mạng, xoay màn hình. C là outsider phải bị từ chối. Xác minh cả metadata và binary Storage, không chỉ toast.

## Giới hạn còn lại

Không có UI xóa toàn trip/tài khoản, upload phục hồi qua process death, backend chống dò email, phân trang lịch sử hay cơ chế thu hồi download token. Các tính năng này không nên báo cáo là đã triển khai. Validation ngày hiện kiểm tra định dạng ở rules, tính hợp lệ/order ở app; nếu cần chống client độc hại gửi ngày sai lịch, cần backend validation bổ sung. Quyền xóa trip/ACL trực tiếp bị đóng nhằm tránh orphan/index sai.

Nguồn: https://firebase.google.com/docs/database/security/rules-conditions ; https://firebase.google.com/docs/storage/security/rules-conditions

## Kết quả xác nhận cục bộ

- 14/14 tests Database/Firestore/Storage Emulator đạt.
- 3/3 tests chuyển đổi dữ liệu đạt: giữ lịch trình, chuyển index/yêu thích, không khôi phục thành viên đã bị thu hồi, phát hiện owner sai.
- Build debug, unit tests Android JVM và lint debug đạt; không xác nhận cloud E2E.
- Function handler tải được cùng dependencies; script migration kiểm tra cú pháp đạt.

Chưa deploy services/rules, chưa ghi migration lên project thực, chưa thay rules Storage hiện tại (người dùng chưa cung cấp). Đây là kết quả kiểm thử bộ cấu hình được chuẩn bị, không phải kết quả Firebase đang chạy trên cloud.
