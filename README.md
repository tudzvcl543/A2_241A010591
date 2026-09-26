# Lab A2 – Đồng hồ bấm giờ

**Sinh viên:** Trần Tuấn Tú · **MSSV:** 241A010591  
**Lớp:** Lập trình di động – INT4211  
**Ngôn ngữ:** Java, XML · **Min SDK:** 24

## Mở và chạy

Mở thư mục này bằng Android Studio, đồng bộ Gradle, chạy module `app` trên máy ảo hoặc điện thoại. Bấm **Bắt đầu**, **Tạm dừng**, **Đặt lại**. Đồng hồ tính thời gian từ `SystemClock.elapsedRealtime()`, cập nhật hiển thị bằng `Handler` mỗi 100 ms. Thử xoay máy trong lúc chạy để kiểm tra việc khôi phục `Bundle`.

## Vòng đời và lưu trạng thái

- `onResume()` bật lại ticker nếu đồng hồ đang chạy; `onPause()` gỡ ticker để không cập nhật UI khi ứng dụng ra nền.
- `onSaveInstanceState()` lưu trạng thái chạy, tổng thời gian, mốc bắt đầu, số lần tạo lại, danh sách vòng và tuỳ chọn dừng khi ra nền. `onCreate()` khôi phục trước khi cập nhật UI.
- `onDestroy()` gỡ callback của `Handler`. Ticker không chạy chồng vì `startTicking()` luôn xoá callback cũ.
- Nút Back thoát Activity rồi mở lại sẽ bắt đầu từ 0; đồng hồ không lưu bền vững vào CSDL.

## Bài nâng cao

1. **NC1 – Ghi vòng:** nút **Ghi vòng** ghi mốc hiện tại, hiện các vòng mới nhất ở trên; danh sách được lưu vào `Bundle` khi xoay màn hình.
2. **NC2 – Dừng khi ra nền:** bật ô chọn để `onStop()` tạm dừng đồng hồ lúc rời ứng dụng. Chế độ này bỏ qua lần `onStop()` do xoay màn hình.
3. **NC4 – Bố cục ngang:** `res/layout-land/activity_main.xml` đặt phần điều khiển và danh sách vòng bên phải đồng hồ.

## Kiểm tra và nộp

Trong Logcat, lọc `tag:A2_241A010591`. Chạy 5 kịch bản trong tài liệu lab với ô **Dừng khi ra nền** tắt, chụp ảnh màn hình kịch bản 4 và tab Git Log, sau đó điền kết quả thực tế vào báo cáo. Quay video thật ≤ 2 phút: giới thiệu họ tên/MSSV, bắt đầu đồng hồ, xoay màn hình, chỉ ra thời gian vẫn tiếp tục. Nếu bài nộp yêu cầu repo riêng, để repo **Private** và mời tài khoản GitHub giảng viên theo tài khoản do thầy cung cấp.

Không đưa `.gradle/`, `build/`, `local.properties` lên GitHub.
