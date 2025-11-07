# Bài Tập 01
Một ứng dụng Android giới thiệu thông tin sinh viên và các chức năng xử lý chuỗi/ số cơ bản.

## Tính năng

- **Hiển thị hồ sơ sinh viên**: Hiển thị tên, MSSV, ngành học, khoa, trường đại học và ảnh đại diện
- **Xử lý số**: Nhập các số cách nhau bằng dấu phẩy và tách thành danh sách số chẵn và số lẻ
- **Đảo ngược chuỗi**: Đảo ngược thứ tự các từ trong chuỗi nhập vào và chuyển thành chữ hoa
- **Hành động nhanh**: Liên kết trực tiếp đến GitHub và chức năng sao chép email

## 📸 Ảnh chụp

<img src="https://github.com/nv08-8/Mobile/blob/main/BaiTap01/img.png?raw=true" alt="Hình minh họa" width="300">

## 🛠️ Công nghệ sử dụng

- **Ngôn ngữ**: Java
- **Hệ thống build**: Gradle (Kotlin DSL)
- **SDK tối thiểu**: API 23 (Android 6.0)
- **SDK mục tiêu**: API 36 (Android 16)
- **Kiến trúc**: MVVM với View Binding
- **Thành phần UI**:
  - ConstraintLayout
  - CircleImageView
  - Material Design Components

## 🚀 Cài đặt & Thiết lập

1. **Clone repository**:
   ```bash
   git clone <repository-url>
   cd BaiTap01
   ```

2. **Mở trong Android Studio**:
   - Khởi chạy Android Studio
   - Chọn "Open an existing Android Studio project"
   - Điều hướng đến thư mục đã clone và chọn nó

3. **Build và chạy**:
   - Chờ Gradle sync hoàn thành
   - Kết nối thiết bị Android hoặc khởi động emulator
   - Nhấn "Run" (nút play màu xanh) hoặc nhấn Shift+F10

## 📖 Cách sử dụng

### Thông tin sinh viên
- Xem chi tiết sinh viên bao gồm tên, MSSV, ngành, khoa và trường
- Ảnh đại diện hiển thị với viền tròn

### Xử lý số
1. Nhập các số cách nhau bằng dấu phẩy vào ô nhập
2. Nhấn nút "Lọc số"
3. Xem số chẵn và số lẻ hiển thị bên dưới
4. Kiểm tra log Android để xem chi tiết

### Đảo ngược chuỗi
1. Nhập văn bản vào ô nhập
2. Nhấn nút "Đảo ngược"
3. Xem chuỗi đã đảo ngược và chuyển thành chữ hoa
4. Văn bản cũng hiển thị trong thông báo Toast

### Hành động nhanh
- **Nút GitHub**: Mở trang GitHub của sinh viên trong trình duyệt
- **Nút Email**: Sao chép email sinh viên vào clipboard với thông báo xác nhận

## 👨‍🎓 Thông tin sinh viên

- **Họ tên**: Võ Nguyễn Quỳnh Như
- **MSSV**: 23162074
- **Ngành**: An Toàn Thông Tin
- **Khoa**: Công Nghệ Thông Tin
- **Trường**: Đại học Sư Phạm Kỹ Thuật TP.HCM
- **Email**: 23162074@student.hcmute.edu.vn
- **GitHub**: [nv08-8](https://github.com/nv08-8)

## 📄 Cấu trúc dự án

```
app/
├── src/main/
│   ├── java/com/example/bai1/
│   │   └── MainActivity.java          # Activity chính chứa tất cả chức năng
│   ├── res/
│   │   ├── layout/
│   │   │   └── activity_main.xml      # Layout UI chính
│   │   ├── values/
│   │   │   ├── strings.xml            # Tài nguyên chuỗi
│   │   │   ├── colors.xml             # Tài nguyên màu
│   │   │   └── themes.xml             # Tài nguyên theme
│   │   └── drawable/                  # Hình ảnh và nền
│   └── AndroidManifest.xml            # Manifest ứng dụng
├── build.gradle.kts                   # Cấu hình build cấp ứng dụng
```

## 🔧 Cấu hình build

- **Compile SDK**: 36
- **Phiên bản Java**: 11
- **View Binding**: Đã bật
- **ProGuard**: Tắt cho build debug

## 📝 Notes

- Ứng dụng sử dụng tiếng Việt cho các thành phần UI
- Ảnh đại diện và nền được bao gồm trong tài nguyên drawable

