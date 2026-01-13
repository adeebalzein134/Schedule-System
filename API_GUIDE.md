# دليل واجهات API للنظام

- جميع المسارات تبدأ تحت `/api`
- المصادقة تعتمد على JWT عبر ترويسة `Authorization: Bearer <token>`
- الأدوار المستخدمة: `ADMIN`, `STAFF`, `CUSTOMER`

## المصادقة (AuthController)
- الملف: [AuthController.java](file:///c:/Users/USER/Documents/trae_projects/Final%20system/src/main/java/com/example/smartschedule/controller/AuthController.java)
- المسار الأساسي: `/api/auth`

- POST `/api/auth/register`
  - الدور: عام (بدون توثيق)
  - Body (JSON):
    - `username` (string)
    - `password` (string, ≥4)
    - `email` (string, بريد صالح)
    - `role` (string, اختياري؛ إن لم يُرسل يُفترض CUSTOMER)
  - مثال:
  
    ```json
    {
      "username": "ali",
      "password": "secret123",
      "email": "ali@example.com",
      "role": "CUSTOMER"
    }
    ```

- POST `/api/auth/login`
  - الدور: عام (بدون توثيق)
  - Body (JSON):
    - `username` (string)
    - `password` (string)
  - مثال:
  
    ```json
    {
      "username": "ali",
      "password": "secret123"
    }
    ```
  - استجابة:
  
    ```json
    {
      "token": "jwt-token-here"
    }
    ```

## الخدمات (ServiceController)
- الملف: [ServiceController.java](file:///c:/Users/USER/Documents/trae_projects/Final%20system/src/main/java/com/example/smartschedule/controller/ServiceController.java)
- الكيان: [Service.java](file:///c:/Users/USER/Documents/trae_projects/Final%20system/src/main/java/com/example/smartschedule/entity/Service.java)
- المسار الأساسي: `/api/services`

- POST `/api/services`
  - الدور: ADMIN
  - Body (JSON):
    - `name` (string)
    - `duration` (number, بالدقائق)
    - `price` (number)
    - `assignedStaff` (object اختياري يحتوي على `id` لموظف موجود)
  - مثال:
  
    ```json
    {
      "name": "Teeth Cleaning",
      "duration": 30,
      "price": 25.0,
      "assignedStaff": { "id": 7 }
    }
    ```

- GET `/api/services`
  - الدور: يتطلب توثيق (أي مستخدم مسجل)
  - لا يوجد Body

## المواعيد (AppointmentController)
- الملف: [AppointmentController.java](file:///c:/Users/USER/Documents/trae_projects/Final%20system/src/main/java/com/example/smartschedule/controller/AppointmentController.java)
- DTO الطلب: [AppointmentRequest.java](file:///c:/Users/USER/Documents/trae_projects/Final%20system/src/main/java/com/example/smartschedule/dto/AppointmentRequest.java)
- الكيانات: [Appointment.java](file:///c:/Users/USER/Documents/trae_projects/Final%20system/src/main/java/com/example/smartschedule/entity/Appointment.java), الحالة: [AppointmentStatus.java](file:///c:/Users/USER/Documents/trae_projects/Final%20system/src/main/java/com/example/smartschedule/entity/AppointmentStatus.java)
- المسار الأساسي: `/api/appointments`

- POST `/api/appointments`
  - الدور: CUSTOMER
  - Body (JSON):
    - `serviceId` (number)
    - `startTime` (string بصيغة ISO مثل `2026-01-13T09:00:00`)
  - مثال:
  
    ```json
    {
      "serviceId": 1,
      "startTime": "2026-01-13T09:00:00"
    }
    ```

- GET `/api/appointments`
  - الدور: يتطلب توثيق
  - يعيد قائمة مواعيد المستخدم الحالي

- GET `/api/appointments/availability`
  - الدور: يتطلب توثيق
  - Query Params:
    - `serviceId` (number، مطلوب)
    - `date` (string بصيغة تاريخ ISO `YYYY-MM-DD`، مطلوب)
  - مثال: `/api/appointments/availability?serviceId=1&date=2026-01-13`
  - استجابة نموذجية:
  
    ```json
    [
      { "start": "2026-01-13T09:00:00", "end": "2026-01-13T09:20:00" },
      { "start": "2026-01-13T09:20:00", "end": "2026-01-13T09:40:00" }
    ]
    ```

- PUT `/api/appointments/{id}/status`
  - الدور: ADMIN أو STAFF
  - Path Param:
    - `id` (number)
  - Query Param:
    - `status` (string من القيم: `PENDING`, `APPROVED`, `CANCELLED`, `COMPLETED`)
  - مثال: `/api/appointments/123/status?status=APPROVED`

## الإدارة (AdminController)
- الملف: [AdminController.java](file:///c:/Users/USER/Documents/trae_projects/Final%20system/src/main/java/com/example/smartschedule/controller/AdminController.java)
- DTO التحديث: [UserUpdateRequest.java](file:///c:/Users/USER/Documents/trae_projects/Final%20system/src/main/java/com/example/smartschedule/dto/UserUpdateRequest.java)
- المسار الأساسي: `/api/admin`
- الدور على مستوى الـ Controller: ADMIN

- GET `/api/admin/users`
  - يعرض جميع المستخدمين

- GET `/api/admin/users/{id}`
  - يعرض مستخدمًا حسب المعرّف

- PUT `/api/admin/users/{id}`
  - Body (JSON):
    - `username` (string)
    - `email` (string)
    - `role` (string: `ADMIN`/`STAFF`/`CUSTOMER`)
    - `password` (string)
  - مثال:
  
    ```json
    {
      "username": "ali",
      "email": "ali@example.com",
      "role": "STAFF",
      "password": "newSecret"
    }
    ```

- DELETE `/api/admin/users/{id}`
  - يحذف مستخدمًا حسب المعرّف

## ملاحظات إضافية
- استخدم ترويسة `Authorization` مع قيمة Bearer JWT لجميع المسارات التي تتطلب التوثيق.
- قيم `role` يجب أن تطابق التعداد المعمول به في النظام.

