# THÔNG TIN DỰ ÁN SKILLFORGE (ANDROID CLIENT)
- **Domain:** Nền tảng học trực tuyến (E-learning platform) tương tự Coursera, edX. Các thực thể chính bao gồm: Course, Module, Lesson, Instructor, Student, Progress, Enrollment.
- **Ngôn ngữ:** Kotlin.
- **UI Framework:** 100% Jetpack Compose (Tuyệt đối không sử dụng XML, ViewBinding hay DataBinding).
- **Kiến trúc:** Clean Architecture kết hợp Feature-based Modularization. 
- **Cấu trúc Layer:** `core` (dùng chung), `data` (xử lý dữ liệu/API/DB), `domain` (business logic, models, use cases), `feature` (UI và ViewModel theo từng chức năng).

## 1. QUY TẮC UI & COMPONENT (JETPACK COMPOSE)
- Đặt tất cả UI tại `feature/<tên_feature>/ui`. 
- Chia nhỏ các màn hình phức tạp thành các component tái sử dụng trong thư mục `components` của feature đó.
- Sử dụng chuẩn `State Hoisting`: UI Component chỉ nhận data (state) và emit events (callbacks) lên trên, không tự chứa logic gọi data.
- Luôn sử dụng `core/designsystem` (Color.kt, Theme.kt, GlobalStyles.kt) cho màu sắc, typography và spacing. Không hardcode UI values.
- Mọi chuyển trang (Navigation) sử dụng **Jetpack Navigation Compose**, định nghĩa tập trung tại `core/navigation/AppRoute.kt`.

## 2. QUY TẮC STATE MANAGEMENT & VIEWMODEL
- **Quản lý trạng thái UI:** Bắt buộc sử dụng `sealed class` hoặc `data class` để định nghĩa UI State (VD: `Idle`, `Loading`, `Success`, `Error`).
- **Luồng dữ liệu:** Sử dụng `MutableStateFlow` (private) và `StateFlow` (public) trong ViewModel. Trong Compose UI, sử dụng `collectAsStateWithLifecycle()` hoặc `collectAsState()` để observe.
- ViewModel chỉ tương tác với **UseCase**, tuyệt đối không gọi thẳng tới Repository.

## 3. QUY TẮC KIẾN TRÚC CLEAN ARCHITECTURE & LUỒNG DỮ LIỆU
Luồng chuẩn: UI -> ViewModel -> UseCase (`domain/usecase`) -> Repository Interface (`domain/repository`) -> Repository Impl (`data/repository`) -> Data Source (Remote/Local).
- **Domain Layer:** Pure Kotlin. Chỉ chứa Models, Repository Interfaces và UseCases. Không chứa bất kỳ thư viện Android nào. 
- **Data Layer:** Nơi implement Repository Interfaces. Đảm nhận việc mapping data từ DTO (Network/DB) sang Domain Models trước khi trả về cho UseCase.

## 4. QUY TẮC TIÊM PHỤ THUỘC (DEPENDENCY INJECTION)
- **CẤM** sử dụng Hilt, Dagger hoặc Koin.
- Dự án sử dụng **Manual DI (Tiêm phụ thuộc thủ công)**.
- Quản lý các dependencies (Network, Database, Repositories) tập trung tại `core/di/AppContainer.kt`.
- Luôn tạo các lớp `*ViewModelFactory.kt` (VD: `LoginViewModelFactory.kt`) để cung cấp UseCase/Repository vào ViewModel.

## 5. QUY TẮC NETWORK & LOCAL DATABASE (DATA LAYER)
- **Network (Remote):** Sử dụng **Retrofit** kết hợp Kotlin Coroutines (Suspend functions). Khai báo các API Interfaces tại `data/remote`.
- **Local Storage:**
  - Sử dụng **Preferences DataStore** để lưu trữ các trạng thái đơn giản (Auth Token, User Settings).
  - Sử dụng **Room Database** để lưu trữ các dữ liệu có cấu trúc phục vụ **Offline Learning** (Danh sách Course, tiến độ học, metadata video tải xuống). Khai báo Entity và DAO tại `data/local`.
- Luôn wrap kết quả trả về từ Repository bằng `Result<T>` hoặc custom Wrapper class để xử lý Success/Error.

## 6. MOCK DATA & TESTING
- Khi xây dựng UI mà API chưa sẵn sàng, bắt buộc tạo Mock Data tại thư mục `mock` của feature (VD: `feature/home/ui/mock/HomeMockData.kt`).
- Đảm bảo Unit Test cho ViewModel và UseCase bao phủ được các luồng Success, Error và Loading.