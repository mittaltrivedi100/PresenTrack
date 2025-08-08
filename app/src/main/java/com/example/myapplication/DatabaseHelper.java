package com.example.myapplication;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.myapplication.models.AttendanceEntry;
import com.example.myapplication.models.AttendanceLectureModel;
import com.example.myapplication.models.AttendanceRecord;
import com.example.myapplication.models.ClassModel;
import com.example.myapplication.models.LectureModel;
import com.example.myapplication.models.StudentModel;
import com.example.myapplication.models.TeacherModel;
import com.example.myapplication.models.TimetableModel;
import com.example.myapplication.models.ViewAttendanceLectureModel;
import com.example.myapplication.models.ViewAttendanceStudentModel;
import java.util.ArrayList;
import java.util.List;
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "PresenTrack.db";
    public static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {


        // Create Class Table
        db.execSQL("CREATE TABLE Class (" +
                "id TEXT PRIMARY KEY NOT NULL, " +
                "name TEXT NOT NULL, " +
                "section TEXT NOT NULL)");

        // Create Admin Table
        db.execSQL("CREATE TABLE Admin (" +
                "id TEXT PRIMARY KEY NOT NULL, " +
                "firstName TEXT NOT NULL, " +
                "lastName TEXT NOT NULL, " +
                "email TEXT NOT NULL UNIQUE, " +
                "mobile TEXT NOT NULL CHECK(length(mobile) >= 10), " +
                "password TEXT NOT NULL)");

        // Create Teacher Table
        db.execSQL("CREATE TABLE Teacher (" +
                "id TEXT PRIMARY KEY NOT NULL, " +
                "designation TEXT NOT NULL, " +
                "firstName TEXT NOT NULL, " +
                "lastName TEXT NOT NULL, " +
                "fatherName TEXT NOT NULL, " +
                "dob DATE NOT NULL, " +
                "qualification TEXT NOT NULL, " +
                "experience TEXT NOT NULL, " +
                "phone TEXT NOT NULL CHECK(length(phone) >= 10), " +
                "email TEXT NOT NULL UNIQUE, " +
                "address TEXT NOT NULL, " +
                "joiningDate DATE NOT NULL, " +
                "password TEXT NOT NULL)");

        // Create Student Table
        db.execSQL("CREATE TABLE Student (" +
                "id TEXT PRIMARY KEY NOT NULL, " +
                "firstName TEXT NOT NULL, " +
                "lastName TEXT NOT NULL, " +
                "fatherName TEXT NOT NULL, " +
                "dob DATE NOT NULL, " +
                "section TEXT NOT NULL, " +
                "phone TEXT NOT NULL CHECK(length(phone) >= 10), " +
                "email TEXT NOT NULL UNIQUE, " +
                "address TEXT NOT NULL, " +
                "password TEXT NOT NULL)");

        db.execSQL("CREATE TABLE Timetable (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL)");

        // Create Lectures Table
        db.execSQL("CREATE TABLE Lecture (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "timetableName TEXT," +
                "sectionName TEXT NOT NULL, " +
                "teacherName TEXT NOT NULL, " +
                "day TEXT NOT NULL, " +
                "subject TEXT NOT NULL, " +
                "startTimeHour INTEGER NOT NULL, " +
                "startTimeMinute INTEGER NOT NULL, " +
                "endTimeHour INTEGER NOT NULL, " +
                "endTimeMinute INTEGER NOT NULL)");


        db.execSQL("CREATE TABLE Attendance (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "studentId TEXT, " +
                "lectureId INTEGER, " +
                "status INTEGER, " +  // 1 for present, 0 for absent
                "date TEXT, " +
                "FOREIGN KEY(studentId) REFERENCES Student(id) ON DELETE CASCADE, " +
                "FOREIGN KEY(lectureId) REFERENCES Lecture(id) ON DELETE CASCADE, " +
                "UNIQUE(studentId, lectureId, date)" +  // ensure no duplicate attendance for same student, lecture, and date
                ")");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Admin");
        db.execSQL("DROP TABLE IF EXISTS Teacher");
        db.execSQL("DROP TABLE IF EXISTS Student");
        db.execSQL("DROP TABLE IF EXISTS Class");
        db.execSQL("DROP TABLE IF EXISTS Timetable");
        db.execSQL("DROP TABLE IF EXISTS Lecture");
        db.execSQL("DROP TABLE IF EXISTS Attendance");
        onCreate(db);
    }


//login/registration methods----------------------------------------------------------------
public boolean checkAdminLogin(String email, String password) {
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor cursor = db.rawQuery("SELECT * FROM Admin WHERE email=? AND password=?", new String[]{email, password});
    boolean isValid = cursor.moveToFirst();
    cursor.close();
    return isValid;
}
    public String getAdminIdByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String adminId = null;

        Cursor cursor = db.rawQuery("SELECT id FROM Admin WHERE email = ?", new String[]{email});
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                adminId = cursor.getString(0); // ID is in first column
            }
            cursor.close();
        }
        return adminId;
    }
    public boolean isAdminIdExists(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Admin WHERE id = ?", new String[]{id});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }
    public boolean insertAdmin(String id, String firstName, String lastName, String email, String mobile, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", id);
        values.put("firstName", firstName);
        values.put("lastName", lastName);
        values.put("email", email);
        values.put("mobile", mobile);
        values.put("password", password);
        long result = db.insert("Admin", null, values);
        return result != -1;
    }
    public boolean checkTeacherLogin(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Teacher WHERE email=? AND password=?", new String[]{email, password});
        boolean isValid = cursor.moveToFirst();
        cursor.close();
        return isValid;
    }
    public String getTeacherIdByEmail(String email) {
        String teacherId = "";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM teacher WHERE email = ?", new String[]{email});

        if (cursor != null && cursor.moveToFirst()) {
            teacherId = cursor.getString(0);
            cursor.close();
        }

        return teacherId;
    }
    public boolean isTeacherIdExists(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Teacher WHERE id = ?", new String[]{id});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }
    public long insertTeacher(String id, String designation, String firstName, String lastName, String fatherName,
                              String dob, String qualification, String experience, String phone,
                              String email, String address, String joiningDate, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", Integer.parseInt(id));
        values.put("designation", designation);
        values.put("firstName", firstName);
        values.put("lastName", lastName);
        values.put("fatherName", fatherName);
        values.put("dob", dob);
        values.put("qualification", qualification);
        values.put("experience", experience);
        values.put("phone", phone);
        values.put("email", email);
        values.put("address", address);
        values.put("joiningDate", joiningDate);
        values.put("password", password);

        return db.insert("Teacher", null, values);
    }
    public boolean checkStudentLogin(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Student WHERE email = ? AND password = ?", new String[]{email, password});
        boolean isValid = cursor.moveToFirst();
        cursor.close();
        return isValid;
    }
    public String getStudentIdByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM Student WHERE email = ?", new String[]{email});
        String studentId = null;

        if (cursor.moveToFirst()) {
            studentId = cursor.getString(cursor.getColumnIndex("id"));
        }

        cursor.close();
        return studentId;
    }
    //class mehtods-------------------------------------------------
    public boolean isClassExists(String name, String section) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM class WHERE name = ? AND section = ?", new String[]{name, section});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }
    public boolean insertClass(String id, String name, String section) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", id);
        values.put("name", name);
        values.put("section", section);
        long result = db.insert("Class", null, values);
        return result != -1;
    }
    public ArrayList<StudentModel> getAllStudents() {
        ArrayList<StudentModel> studentList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Student", null);

        if (cursor.moveToFirst()) {
            do {
                String id = cursor.getString(cursor.getColumnIndex("id"));
                String firstName = cursor.getString(cursor.getColumnIndex("firstName"));
                String lastName = cursor.getString(cursor.getColumnIndex("lastName"));
                String fatherName = cursor.getString(cursor.getColumnIndex("fatherName"));
                String dob = cursor.getString(cursor.getColumnIndex("dob"));
                String section = cursor.getString(cursor.getColumnIndex("section"));
                String phone = cursor.getString(cursor.getColumnIndex("phone"));
                String email = cursor.getString(cursor.getColumnIndex("email"));
                String address = cursor.getString(cursor.getColumnIndex("address"));
                String password = cursor.getString(cursor.getColumnIndex("password"));

                StudentModel model = new StudentModel(id, firstName, lastName, fatherName, dob, section, phone, email, address, password);
                studentList.add(model);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return studentList;
    }

    public boolean deleteTeacher(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete("Teacher", "id=?", new String[]{id});
        return result != -1;
    }
    public boolean updateTeacher(TeacherModel teacher) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("designation", teacher.getDesignation());
        values.put("firstName", teacher.getFirstName());
        values.put("lastName", teacher.getLastName());
        values.put("fatherName", teacher.getFatherName());
        values.put("dob", teacher.getDob());
        values.put("qualification", teacher.getQualification());
        values.put("experience", teacher.getExperience());
        values.put("phone", teacher.getPhone());
        values.put("email", teacher.getEmail());
        values.put("address", teacher.getAddress());
        values.put("joiningDate", teacher.getJoiningDate());
        values.put("password", teacher.getPassword());

        int rows = db.update("Teacher", values, "id = ?", new String[]{teacher.getId()});
        db.close();
        return rows > 0;
    }

    public ArrayList<TeacherModel> getAllTeachers() {
        ArrayList<TeacherModel> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Teacher", null);

        if (cursor.moveToFirst()) {
            do {
                String id = cursor.getString(cursor.getColumnIndexOrThrow("id"));
                String designation = cursor.getString(cursor.getColumnIndexOrThrow("designation"));
                String firstName = cursor.getString(cursor.getColumnIndexOrThrow("firstName"));
                String lastName = cursor.getString(cursor.getColumnIndexOrThrow("lastName"));
                String fatherName = cursor.getString(cursor.getColumnIndexOrThrow("fatherName"));
                String dob = cursor.getString(cursor.getColumnIndexOrThrow("dob"));
                String qualification = cursor.getString(cursor.getColumnIndexOrThrow("qualification"));
                String experience = cursor.getString(cursor.getColumnIndexOrThrow("experience"));
                String phone = cursor.getString(cursor.getColumnIndexOrThrow("phone"));
                String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
                String address = cursor.getString(cursor.getColumnIndexOrThrow("address"));
                String joiningDate = cursor.getString(cursor.getColumnIndexOrThrow("joiningDate"));
                String password = cursor.getString(cursor.getColumnIndexOrThrow("password"));

                TeacherModel model = new TeacherModel(id, designation, firstName, lastName, fatherName,
                        dob, qualification, experience, phone, email, address, joiningDate, password);

                list.add(model);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return list;
    }

    public boolean isTimetableNameExists(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Timetable WHERE name = ?", new String[]{name});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }
    public boolean isLectureDuplicate(String timetableName, String sectionName, String teacherName,
                                      String day, String subject, int startHour, int startMinute, int endHour, int endMinute) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM Lecture WHERE timetableName = ? AND sectionName = ? AND teacherName = ? AND day = ? AND subject = ? " +
                        "AND startTimeHour = ? AND startTimeMinute = ? AND endTimeHour = ? AND endTimeMinute = ?",
                new String[]{timetableName, sectionName, teacherName, day, subject,
                        String.valueOf(startHour), String.valueOf(startMinute),
                        String.valueOf(endHour), String.valueOf(endMinute)}
        );

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }




    public boolean isStudentEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM Student WHERE email = ?", new String[]{email});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public boolean isStudentIdExists(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM Student WHERE id = ?", new String[]{id});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public List<String> getAllSections() {
        List<String> sections = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Query to get distinct sections from the Class table (or wherever your sections are stored)
        Cursor cursor = db.rawQuery("SELECT DISTINCT section FROM Class", null);

        if (cursor.moveToFirst()) {
            do {
                String section = cursor.getString(cursor.getColumnIndexOrThrow("section"));
                sections.add(section);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return sections;
    }

    public List<AttendanceLectureModel> getLecturesForTeacherAndDay(String teacherName, String day) {
        List<AttendanceLectureModel> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Lecture WHERE teacherName=? AND day=?", new String[]{teacherName, day});

        if (cursor.moveToFirst()) {
            do {
                AttendanceLectureModel model = new AttendanceLectureModel();
                model.id = cursor.getInt(cursor.getColumnIndex("id"));
                model.subject = cursor.getString(cursor.getColumnIndex("subject"));
              //model.sectionName = cursor.getString(cursor.getColumnIndex("sectionName"));
                model.startTimeHour = cursor.getInt(cursor.getColumnIndex("startTimeHour"));
                model.startTimeMinute = cursor.getInt(cursor.getColumnIndex("startTimeMinute"));
                model.endTimeHour = cursor.getInt(cursor.getColumnIndex("endTimeHour"));
                model.endTimeMinute = cursor.getInt(cursor.getColumnIndex("endTimeMinute"));
                list.add(model);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public ArrayList<ClassModel> getAllClasses() {
        ArrayList<ClassModel> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Class", null);

        if (cursor.moveToFirst()) {
            do {
                String id = cursor.getString(0);
                String name = cursor.getString(1);
                String section = cursor.getString(2);
                list.add(new ClassModel(id, name, section));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return list;
    }

    public boolean updateClass(String id, String name, String section) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("section", section);
        long result = db.update("Class", values, "id=?", new String[]{id});
        return result != -1;
    }

    public boolean deleteClass(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete("Class", "id=?", new String[]{id});
        return result != -1;
    }
    //insert student
    public long insertStudent(String id, String firstName, String lastName, String fatherName,
                              String dob, String section, String phone, String email, String address, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", id);
        values.put("firstName", firstName);
        values.put("lastName", lastName);
        values.put("fatherName", fatherName);
        values.put("dob", dob);
        values.put("section", section);
        values.put("phone", phone);
        values.put("email", email);
        values.put("address", address);
        values.put("password", password);

        return db.insert("Student", null, values);
    }

public int updateStudent(String id, String firstName, String lastName, String fatherName,
                         String dob, String section, String phone, String email, String address, String password) {
    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put("firstName", firstName);
    values.put("lastName", lastName);
    values.put("fatherName", fatherName);
    values.put("dob", dob);
    values.put("section", section);
    values.put("phone", phone);
    values.put("email", email);
    values.put("address", address);
    values.put("password", password);

    return db.update("Student", values, "id = ?", new String[]{id});
}

public int deleteStudent(String id) {
    SQLiteDatabase db = this.getWritableDatabase();
    return db.delete("Student", "id = ?", new String[]{id});
}

public ArrayList<String> getSections() {
    ArrayList<String> sectionList = new ArrayList<>();
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor cursor = db.rawQuery("SELECT DISTINCT section FROM Class", null); // from Class table

    if (cursor.moveToFirst()) {
        do {
            sectionList.add(cursor.getString(cursor.getColumnIndexOrThrow("section")));
        } while (cursor.moveToNext());
    }
    cursor.close();
    return sectionList;
}

public ArrayList<StudentModel> getStudentsBySection(String section) {
    ArrayList<StudentModel> list = new ArrayList<>();
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor cursor = db.rawQuery("SELECT * FROM Student WHERE section=?", new String[]{section});

    if (cursor.moveToFirst()) {
        do {
            String id = cursor.getString(cursor.getColumnIndexOrThrow("id"));
            String firstName = cursor.getString(cursor.getColumnIndexOrThrow("firstName"));
            String lastName = cursor.getString(cursor.getColumnIndexOrThrow("lastName"));
            String fatherName = cursor.getString(cursor.getColumnIndexOrThrow("fatherName"));
            String dob = cursor.getString(cursor.getColumnIndexOrThrow("dob"));
            String phone = cursor.getString(cursor.getColumnIndexOrThrow("phone"));
            String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
            String address = cursor.getString(cursor.getColumnIndexOrThrow("address"));
            String password = cursor.getString(cursor.getColumnIndexOrThrow("password"));

            list.add(new StudentModel(id, firstName, lastName, fatherName, dob, section, phone, email, address, password));
        } while (cursor.moveToNext());
    }

    cursor.close();
    return list;
}

    public boolean insertTimetable(TimetableModel model) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", model.getName()); // assuming the column is "name"
        long result = db.insert("timetable", null, values);
        return result != -1;
    }

    public boolean deleteTimetable(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Step 1: Delete associated lectures
        db.delete("Lecture", "timetableName = (SELECT name FROM Timetable WHERE id=?)", new String[]{String.valueOf(id)});

        // Step 2: Delete the timetable
        int result = db.delete("Timetable", "id=?", new String[]{String.valueOf(id)});

        return result > 0;
    }


    public boolean updateTimetable(TimetableModel timetable) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", timetable.getName());

        int result = db.update("timetable", values, "id = ?", new String[]{String.valueOf(timetable.getId())});
        return result > 0;
    }

    public List<String> getAllTeachersName() {
        List<String> teacherNames = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT firstName, lastName FROM Teacher", null);

        if (cursor.moveToFirst()) {
            do {
                String fullName = cursor.getString(0) + " " + cursor.getString(1); // Combine first + last name
                teacherNames.add(fullName);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return teacherNames;
    }
    public List<LectureModel> getLecturesForTimetable(String timetableName) {
        List<LectureModel> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Lecture WHERE timetableName=?", new String[]{timetableName});

        if (cursor.moveToFirst()) {
            do {
                String id = cursor.getString(cursor.getColumnIndexOrThrow("id"));
                String sectionName = cursor.getString(cursor.getColumnIndexOrThrow("sectionName"));
                String teacherName = cursor.getString(cursor.getColumnIndexOrThrow("teacherName"));
                String day = cursor.getString(cursor.getColumnIndexOrThrow("day"));
                String subject = cursor.getString(cursor.getColumnIndexOrThrow("subject"));
                int startHour = cursor.getInt(cursor.getColumnIndexOrThrow("startTimeHour"));
                int startMinute = cursor.getInt(cursor.getColumnIndexOrThrow("startTimeMinute"));
                int endHour = cursor.getInt(cursor.getColumnIndexOrThrow("endTimeHour"));
                int endMinute = cursor.getInt(cursor.getColumnIndexOrThrow("endTimeMinute"));

                list.add(new LectureModel(id, timetableName, sectionName, teacherName, day, subject,
                        startHour, startMinute, endHour, endMinute));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public List<String> getAllTimetableNames() {
        List<String> names = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT name FROM Timetable", null);

        if (cursor.moveToFirst()) {
            do {
                names.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return names;
    }



public List<ViewAttendanceLectureModel> getLecturesByTimetableName(String timetableName) {
    List<ViewAttendanceLectureModel> lectures = new ArrayList<>();
    SQLiteDatabase db = this.getReadableDatabase();

    String query = "SELECT id, subject FROM Lecture WHERE timetableName = ?";
    Cursor cursor = db.rawQuery(query, new String[] { timetableName });

    if (cursor.moveToFirst()) {
        do {
            String id = cursor.getString(0);
            String subject = cursor.getString(1);
            lectures.add(new ViewAttendanceLectureModel(id, subject));
        } while (cursor.moveToNext());
    }

    cursor.close();
    db.close();
    return lectures;
}



    public List<AttendanceEntry> filterAttendance(String studentId, String subject, String day, String fromDate, String toDate) {
        List<AttendanceEntry> records = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        StringBuilder query = new StringBuilder();
        query.append("SELECT A.date, L.subject, A.status, L.day FROM Attendance A ");
        query.append("JOIN Lecture L ON A.lectureId = L.id ");
        query.append("WHERE A.studentId = ? ");

        List<String> argsList = new ArrayList<>();
        argsList.add(studentId);

        if (!subject.equalsIgnoreCase("All")) {
            query.append("AND L.subject = ? ");
            argsList.add(subject);
        }
        if (!day.equalsIgnoreCase("All")) {
            query.append("AND L.day = ? ");
            argsList.add(day);
        }
        if (!fromDate.isEmpty()) {
            query.append("AND A.date >= ? ");
            argsList.add(fromDate);
        }
        if (!toDate.isEmpty()) {
            query.append("AND A.date <= ? ");
            argsList.add(toDate);
        }

        query.append("ORDER BY A.date DESC");

        Cursor cursor = db.rawQuery(query.toString(), argsList.toArray(new String[0]));

        if (cursor.moveToFirst()) {
            do {
                String date = cursor.getString(0);
                String subj = cursor.getString(1);
                int statusInt = cursor.getInt(2);
                // int dayIndex = cursor.getString(3); // day if needed
                boolean isPresent = (statusInt == 1);
                records.add(new AttendanceEntry(date, subj, isPresent));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return records;
    }
    public List<String> getSubjectsForStudent(String studentId) {
        List<String> subjects = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT DISTINCT L.subject FROM Attendance A " +
                        "JOIN Lecture L ON A.lectureId = L.id " +
                        "WHERE A.studentId = ?", new String[]{studentId}
        );

        if (cursor.moveToFirst()) {
            do {
                subjects.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return subjects;
    }

    public String getStudentNameById(String studentId) {
        String name = "Student";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT firstName, lastName FROM Student WHERE id = ?", new String[]{studentId});
        if (cursor.moveToFirst()) {
            name = cursor.getString(0) + " " + cursor.getString(1);
        }
        cursor.close();
        return name;
    }

    public List<ViewAttendanceStudentModel> getStudentsByLecture(int lectureId, String date) {
        List<ViewAttendanceStudentModel> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT s.id, (s.firstName || ' ' || s.lastName) AS fullName, a.status " +
                        "FROM Attendance a " +
                        "INNER JOIN Student s ON a.studentId = s.id " +
                        "WHERE a.lectureId = ? AND a.date = ?",
                new String[]{String.valueOf(lectureId), date}
        );

        if (cursor.moveToFirst()) {
            do {
                String id = cursor.getString(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("fullName"));
                int status = cursor.getInt(cursor.getColumnIndexOrThrow("status"));
                list.add(new ViewAttendanceStudentModel(id, name, String.valueOf(status)));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return list;
    }
    public Integer getAttendanceStatus(int studentId, int lectureId, String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT status FROM Attendance WHERE studentId = ? AND lectureId = ? AND date = ?",
                new String[]{String.valueOf(studentId), String.valueOf(lectureId), date});
        if (cursor.moveToFirst()) {
            int status = cursor.getInt(cursor.getColumnIndex("status"));
            cursor.close();
            return status;
        }
        cursor.close();
        return null; // no record found
    }

    public List<LectureModel> getLectureModelsByTimetable(String timetableName) {
        List<LectureModel> lectureList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Query to select all lectures matching the timetableName
        String query = "SELECT * FROM Lecture WHERE timetableName = ?";
        Cursor cursor = db.rawQuery(query, new String[] { timetableName });

        if (cursor.moveToFirst()) {
            do {
                String id = cursor.getString(cursor.getColumnIndexOrThrow("id"));
                String timetable = cursor.getString(cursor.getColumnIndexOrThrow("timetableName"));
                String section = cursor.getString(cursor.getColumnIndexOrThrow("sectionName"));
                String teacherName = cursor.getString(cursor.getColumnIndexOrThrow("teacherName"));
                String day = cursor.getString(cursor.getColumnIndexOrThrow("day"));
                String subject = cursor.getString(cursor.getColumnIndexOrThrow("subject"));
                int startTimeHour = cursor.getInt(cursor.getColumnIndexOrThrow("startTimeHour"));
                int startTimeMinute = cursor.getInt(cursor.getColumnIndexOrThrow("startTimeMinute"));
                int endTimeHour = cursor.getInt(cursor.getColumnIndexOrThrow("endTimeHour"));
                int endTimeMinute = cursor.getInt(cursor.getColumnIndexOrThrow("endTimeMinute"));

                LectureModel lecture = new LectureModel(
                        id,
                        timetable,
                        section,
                        teacherName,
                        day,
                        subject,
                        startTimeHour,
                        startTimeMinute,
                        endTimeHour,
                        endTimeMinute
                );

                lectureList.add(lecture);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return lectureList;
    }
    public List<TimetableModel> getAllTimetables() {
        List<TimetableModel> timetableList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Timetable", null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String name = cursor.getString(cursor.getColumnIndex("name"));

                TimetableModel model = new TimetableModel(id, name);
                timetableList.add(model);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return timetableList;
    }
    public List<AttendanceRecord> getAttendanceForAllStudents(int lectureId, String fromDate, String toDate) {
        List<AttendanceRecord> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT S.firstName, S.lastName, A.date, A.status, L.subject " +
                        "FROM Attendance A " +
                        "JOIN Student S ON A.studentId = S.id " +
                        "JOIN Lecture L ON A.lectureId = L.id " +
                        "WHERE A.lectureId = ? AND A.date BETWEEN ? AND ? " +
                        "ORDER BY A.date ASC",
                new String[]{String.valueOf(lectureId), fromDate, toDate}
        );

        while (cursor.moveToNext()) {
            String name = cursor.getString(0) + " " + cursor.getString(1);
            String date = cursor.getString(2);
            boolean present = cursor.getInt(3) == 1;
            String subject = cursor.getString(4);

            list.add(new AttendanceRecord(name, subject, date, present));
        }
        cursor.close();
        return list;
    }



    public List<AttendanceRecord> getAttendanceForSingleStudent(String lectureId, String studentId, String fromDate, String toDate) {
        List<AttendanceRecord> records = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT A.date, A.status, L.subject " +
                        "FROM Attendance A " +
                        "JOIN Lecture L ON A.lectureId = L.id " +
                        "WHERE A.lectureId = ? AND A.studentId = ? AND A.date BETWEEN ? AND ? " +
                        "ORDER BY A.date ASC",
                new String[]{lectureId, studentId, fromDate, toDate}
        );

        while (cursor.moveToNext()) {
            String date = cursor.getString(0);
            boolean isPresent = cursor.getInt(1) == 1;
            String subject = cursor.getString(2);
            records.add(new AttendanceRecord("You", subject, date, isPresent));
        }
        cursor.close();
        return records;
    }










    public boolean updateLecture(String id, String sectionName, String teacherName, String day, String timetableName, String subject,
                                 int startTimeHour, int startTimeMinute, int endTimeHour, int endTimeMinute) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("sectionName", sectionName);
        values.put("teacherName", teacherName);
        values.put("day", day);
        values.put("timetableName", timetableName);
        values.put("subject", subject);
        values.put("startTimeHour", startTimeHour);
        values.put("startTimeMinute", startTimeMinute);
        values.put("endTimeHour", endTimeHour);
        values.put("endTimeMinute", endTimeMinute);

        int rows = db.update("Lecture", values, "id = ?", new String[]{id});
        return rows > 0;
    }
    public boolean deleteLecture(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete("Lecture", "id=?", new String[]{id});
        return result != -1;
    }
    public long insertLecture(String timetableName, String sectionName, String teacherName, String day,
                              String subject, int startHour, int startMinute, int endHour, int endMinute) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("timetableName", timetableName);
        values.put("sectionName", sectionName);
        values.put("teacherName", teacherName);
        values.put("day", day);
        values.put("subject", subject);
        values.put("startTimeHour", startHour);
        values.put("startTimeMinute", startMinute);
        values.put("endTimeHour", endHour);
        values.put("endTimeMinute", endMinute);

        return db.insert("Lecture", null, values); // Return inserted row ID
    }

}
