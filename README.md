# Smart Schedule — Backend

نظام خلفي لجدولة المواعيد يعمل بـ Spring Boot 3.x مع JWT وWebSocket (STOMP).

## التقنيات المستخدمة
- Java 17+ (يعمل أيضًا مع Java 21)
- Spring Boot 3.2.x
- Spring Web, Spring Data JPA, Spring Security
- JWT (jjwt-api, jjwt-impl, jjwt-jackson)
- WebSocket + STOMP (Simple Broker)
- H2 Database (in-memory) لأغراض التطوير
- Lombok
- Maven
- HTTPS مفعّل (TLS) على المنفذ 8443

## خطوات التشغيل
1) المتطلبات:
   - تثبيت Java (17 أو أعلى) وMaven
   - أدوات اختبار: Postman ومتصفح

2) البناء والتشغيل:
   - بناء:
     ```bash
     mvn clean install
     ```
   - تشغيل:
     ```bash
     mvn spring-boot:run
     ```
   - المنفذ: 8443 مع HTTPS

3) قاعدة البيانات H2:
   - فتح الكونسول:
     - https://localhost:8443/h2-console
   - بيانات الاتصال:
     - Driver: org.h2.Driver
     - JDBC URL: jdbc:h2:mem:smartschedule
     - User: sa
     - Password: فارغ
   - السماح بالوصول تم في [SecurityConfig.java](/config/SecurityConfig.java#L35-L41)

4) الإشعارات (WebSocket/STOMP):
   - نقاط الاتصال:
     - أصلية: wss://localhost:8443/ws
     - SockJS: https://localhost:8443/ws-sockjs
   - صفحة اختبار جاهزة:
     - https://localhost:8443/ws-test.html
     - الملف: [ws-test.html](/Final%20system/src/main/resources/static/ws-test.html)
   - التدفق:
     - اشترك في `/topic/notifications/{username}`
     - غيّر حالة موعد عبر REST، تصل رسالة فورًا للمشتركين
   - مثال تغيير حالة موعد:
     1) تسجيل الدخول للحصول على JWT:
        ```bash
        curl -k -X POST https://localhost:8443/api/auth/login \
          -H "Content-Type: application/json" \
          -d "{\"username\":\"customer\",\"password\":\"customer123\"}"
        ```
     2) الحصول على قائمة المواعيد:
        ```bash
        curl -k https://localhost:8443/api/appointments \
          -H "Authorization: Bearer <TOKEN>"
        ```
     3) تغيير الحالة (يرسل إشعارًا إلى `/topic/notifications/customer`):
        ```bash
        curl -k -X PUT "https://localhost:8443/api/appointments/<ID>/status?status=CONFIRMED" \
          -H "Authorization: Bearer <TOKEN>"
        ```

## بنية النظام وتقسيمات الكود
- نقطة الانطلاق:
  - [SmartScheduleApplication.java](/smartschedule/SmartScheduleApplication.java)

- الإعدادات:
  - الأمان: [SecurityConfig.java](/config/SecurityConfig.java)
    - تعطيل CSRF
    - السماح لمسارات auth وws وh2 والموارد الثابتة
    - Stateless + فلتر JWT قبل UsernamePasswordAuthenticationFilter
  - WebSocket/STOMP: [WebSocketConfig.java](/config/WebSocketConfig.java)
    - enableSimpleBroker("/topic")
    - setApplicationDestinationPrefixes("/app")
    - نقاط الاتصال: `/ws` و`/ws-sockjs`
  - بيانات أولية: [DataInitializer.java](/config/DataInitializer.java)
    - إنشاء مستخدمين admin/staff/customer
    - خدمات تجريبية
    - جدول دوام عام لكل أيام الأسبوع (الجمعة عطلة)

- الكيانات:
  - المستخدم: [User.java](/entity/User.java)
  - الخدمة: [Service.java](/entity/Service.java)
  - الموعد: [Appointment.java](/entity/Appointment.java)
  - حالة الموعد: [AppointmentStatus.java](/entity/AppointmentStatus.java)
  - جدول الدوام العام: [WorkingSchedule.java](/entity/WorkingSchedule.java)

- المستودعات:
  - المستخدمين: [UserRepository.java](/repository/UserRepository.java)
  - الخدمات: [ServiceRepository.java](/repository/ServiceRepository.java)
  - المواعيد: [AppointmentRepository.java](/repository/AppointmentRepository.java)
  - جدول الدوام: [WorkingScheduleRepository.java](/repository/WorkingScheduleRepository.java)

- الخدمات (منطق الأعمال):
  - المستخدمين: [UserService.java](/service/UserService.java)
  - الخدمات: [ServiceService.java](/service/ServiceService.java)
  - المواعيد: [AppointmentService.java](/service/AppointmentService.java)
    - تحقق من ساعات الدوام العامة
    - تحقق من التعارضات حسب الموظف المعيّن للخدمة إن وجد
    - إرسال إشعار عبر `SimpMessagingTemplate` إلى `/topic/notifications/{username}`
  - جدول الدوام: [WorkingScheduleService.java](/service/WorkingScheduleService.java)

- الكنترولرز (واجهات REST):
  - المصادقة: [AuthController.java](/controller/AuthController.java)
    - register, login
  - الأدمن: [AdminController.java](/controller/AdminController.java)
    - إدارة المستخدمين: عرض، تعديل، حذف
  - الخدمات: [ServiceController.java](/controller/ServiceController.java)
    - إنشاء خدمة (ADMIN)، عرض الخدمات (مستخدم موثّق)
  - المواعيد: [AppointmentController.java](/controller/AppointmentController.java)
    - إنشاء موعد (CUSTOMER)
    - عرض المواعيد حسب الدور
    - تحديث حالة الموعد (ADMIN/STAFF)

- الأمن وJWT:
  - فلتر JWT: [JwtFilter.java](/security/JwtFilter.java)
  - أدوات JWT: [JwtUtil.java](/security/JwtUtil.java)
  - إعدادات JWT: [JwtConfig.java](/config/JwtConfig.java)

## حسابات تجريبية
- ADMIN: admin / admin123
- STAFF: staff / staff123
- CUSTOMER: customer / customer123


## ملاحظات
- المنصة تعمل على HTTPS، استخدم wss:// لاتصالات WebSocket.
- جدول الدوام عام للنظام: السبت–الخميس من 09:00 إلى 17:00، الجمعة عطلة.


### قاعدة البيانات H2 (للتطوير)
- Console: https://localhost:8443/h2-console
- JDBC URL: `jdbc:h2:mem:smartschedule` — User: `sa` — Password: فارغ

### WebSocket/Notifications
- Native WebSocket: `wss://localhost:8443/ws`
- SockJS: `https://localhost:8443/ws-sockjs`
- صفحة اختبار: https://localhost:8443/ws-test.html
- اشترك في: `/topic/notifications/{username}`
- يتفعّل الإشعار عند تغيير حالة الموعد عبر REST


### Api Documentation
تم شرحها وتفصيلها بشكل كامل في ملف API_GUIDE.md

### تقسيم العمل
المهام:
إعداد الـ Controller (محمد عمر العبيد)
إعداد الـ Services (أديب عبد الرزاق الزين)
إعداد الـ Entity&Reprositries (محمد عامر هيثم الشعار)
إعداد الـ HTTPS (أديب عبد الرزاق الزين)
إعداد الـ AOP (Logging) (مفيد موفق طعمه)
إعداد الـ JWT&Security (قاسم محمد الخلف + محمد عمر العبيد)
إعداد الـ Websocket وإرسال الإشعار عند تغيير الموعد (محمد عامر هيثم الشعار)
التجربة والاختبار (Testing Logic) & اختبار التحمل (JMeter) (الجميع)

