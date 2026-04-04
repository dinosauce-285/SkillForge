# Feature Checklist

Legend: ✅ = có, ⏳ = chưa / chưa hoàn tất

| Chức năng | Backend | Frontend | Integrate |
|---|---|---|---|
| Auth email login/register | ✅ | ✅ | ✅ |
| Xác thực token JWT | ✅ | ✅ | ✅ |
| OAuth Google/Facebook | ⏳ | ✅ | ⏳ |
| Logout | ⏳ | ✅ | ⏳ |
| User profile xem/cập nhật | ✅ | ⏳ | ⏳ |
| Home dashboard | ⏳ | ✅ | ⏳ |
| Course listing | ✅ | ✅ | ✅ |
| Course details | ✅ | ✅ | ✅ |
| Search/filter course theo tên/category/level | ✅ | ✅ | ✅ |
| Favorites/Wishlist | ✅ | ✅ | ✅ |
| Course CRUD cho instructor | ✅ | ✅ | ✅ |
| Curriculum management: chapter/lesson CRUD | ✅ | ✅ | ✅ |
| Upload tài liệu / Supabase Storage | ⏳ | ✅ | ⏳ |
| Lesson content API | ✅ | ✅ | ✅ |
| Video player | ⏳ | ✅ | ⏳ |
| PDF viewer / mở PDF | ⏳ | ✅ | ⏳ |
| Text-based lesson UI | ⏳ | ✅ | ⏳ |
| Progress tracking | ⏳ | ✅ | ⏳ |
| Cart | ⏳ | ⏳ | ⏳ |
| Checkout / tạo order | ✅ | ✅ | ⏳ |
| Order history / transaction history | ✅ | ⏳ | ⏳ |
| Receipts | ⏳ | ⏳ | ⏳ |
| Coupons | ⏳ | ✅ | ⏳ |
| Quiz system | ⏳ | ⏳ | ⏳ |
| Q&A / Discussion | ⏳ | ✅ | ⏳ |
| Reviews / Ratings | ⏳ | ⏳ | ⏳ |
| Notifications | ⏳ | ✅ | ⏳ |
| Certificate | ⏳ | ⏳ | ⏳ |
| Recommendation | ⏳ | ⏳ | ⏳ |
| Instructor analytics dashboard | ⏳ | ✅ | ⏳ |
| Student/class management | ⏳ | ⏳ | ⏳ |
| Admin features | ⏳ | ⏳ | ⏳ |

## Quy ước Integrate

- Tick `Integrate` khi client đang gọi API/repository thật ở luồng chính và không còn render bằng mockdata cho chức năng đó.
- Những mục chỉ có UI demo, dữ liệu hard-code, hoặc mới có backend/schema nhưng chưa nối end-to-end thì chưa tick.
