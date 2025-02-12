package com.example.breaxcare.model;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.breaxcare.model.Biiling.BillingHistoryItem;
import com.example.breaxcare.model.Biiling.Card;
import com.example.breaxcare.model.treatments.Task;
import com.example.breaxcare.model.treatments.TreatmentPlan;

import org.mindrot.jbcrypt.BCrypt;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DBhelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "breaxcare.db";
    private static final int DATABASE_VERSION = 4;

    // Table names
    private static final String TABLE_USERS = "users";
    private static final String TABLE_TREATMENT_PLAN = "treatment_plan"; // Fixed typo in table name
    private static final String TABLE_IMAGES = "images"; // New table for images
    private static final String TABLE_PREMIUM_SUBSCRIPTION = "premium_subscription";
    private static final String TABLE_PROFILE_IMAGES = "profile_images";
    private static final String TABLE_MEDICAL_HISTORY = "Medical_History";
    private static final String COLUMN_IMAGE = "image";
    private static final String COLUMN_IMAGESTATUS = "memo_Status";
    private static final String COLUMN_IMAGEDATE = "memo_date";
    private static final String TABLE_CHECKUP = "checktup";


    private static final String TABLE_ONGOING_PAYMENT_CARD = "ongoing_payment_card";
    // Column names for users table
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_FIRST_NAME = "first_name";
    private static final String COLUMN_LAST_NAME = "last_name";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_DOB = "dob";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_USER_TYPE = "user_type";
    private static final String COLUMN_ACCOUNT_TYPE = "account_type"; // New column for account type

    // Column names for treatment plan table
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_TASK_TYPE = "task_type";
    public static final String COLUMN_DESCRIPTION = "description";

    // Column names for images table
    private static final String COLUMN_IMAGE_ID = "image_id";
    private static final String COLUMN_USER_ID = "user_id"; // Foreign key to users table
    private static final String COLUMN_IMAGE_PATH = "image_path"; // Path to the image file
    private static final String COLUMN_IMAGE_NAME = "image_name"; // Name of the image

    private static final String COLUMN_RESULT = "result";
    private static final String COLUMN_ROUNDNESS_COLOR = "roundness_color";
    private static final String COLUMN_WHITE_DOTS_COUNT = "white_dots_count";
    private static final String COLUMN_WHITE_DOTS_PERCENTAGE = "white_dots_percentage";
    private static final String COLUMN_RISK_LEVEL = "risk_level";

    // SQL query to create the users table
    private static final String CREATE_TABLE_USERS =
            "CREATE TABLE " + TABLE_USERS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_FIRST_NAME + " TEXT, " +
                    COLUMN_LAST_NAME + " TEXT, " +
                    COLUMN_USERNAME + " TEXT , " +
                    COLUMN_EMAIL + " TEXT, " +
                    COLUMN_DOB + " TEXT, " +
                    COLUMN_PASSWORD + " TEXT, " +
                    COLUMN_USER_TYPE + " TEXT, " +
                    COLUMN_ACCOUNT_TYPE + " TEXT DEFAULT 'Non-premium'" + // Default value for account type
                    ");";


    private static final String CREATE_TABLE_TREATMENT_PLAN =
            "CREATE TABLE " + TABLE_TREATMENT_PLAN + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TITLE + " TEXT, " +
                    COLUMN_DATE + " TEXT, " +
                    COLUMN_TIME + " TEXT, " +
                    COLUMN_TASK_TYPE + " TEXT, " +
                    COLUMN_DESCRIPTION + " TEXT, " +
                    "user_id INTEGER, " +  // Add a foreign key reference to users table
                    "FOREIGN KEY (user_id) REFERENCES " + TABLE_USERS + " (" + COLUMN_ID + ") ON DELETE CASCADE" +
                    ");";

    private static final String CREATE_TABLE_PREMIUM_SUBSCRIPTION =
            "CREATE TABLE " + TABLE_PREMIUM_SUBSCRIPTION + " (" +
                    "userid INTEGER, " +
                    "Subscription_Type TEXT, " +
                    "CardType TEXT, " +
                    "CardHolder_Name TEXT, " +
                    "Card_No TEXT, " +
                    "ExpiryDate TEXT, " +
                    "CVV TEXT, " +
                    "PaymentDate TEXT, " +
                    "FOREIGN KEY(userid) REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + ")" +
                    ");";

    private static final String CREATE_TABLE_ONGOING_PAYMENT_CARD =
            "CREATE TABLE " + TABLE_ONGOING_PAYMENT_CARD + " (" +
                    "userid INTEGER, " +
                    "Card_No TEXT, " +
                    "FOREIGN KEY(userid) REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + ")" +
                    ");";

    private static final String CREATE_TABLE_PROFILE_IMAGES =
            "CREATE TABLE " + TABLE_PROFILE_IMAGES + " (" +
                    COLUMN_IMAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_USER_ID + " INTEGER, " +
                    COLUMN_IMAGE + " BLOB, " + // Column for storing the image as a BLOB
                    COLUMN_IMAGE_NAME + " TEXT, " +
                    "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + ")" +
                    ");";

    private static final String CREATE_TABLE_CHECKUP =
            "CREATE TABLE " + TABLE_CHECKUP + " (" +
                    COLUMN_ID + " INTEGER, " +
                    COLUMN_IMAGESTATUS + " TEXT, " +
                    COLUMN_IMAGEDATE + " TEXT);";

    private static final String CREATE_MEDICAL_HISTORY_TABLE =
            "CREATE TABLE " + TABLE_MEDICAL_HISTORY + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_USER_ID + " INTEGER, " +
            COLUMN_RESULT + " TEXT, " +
            COLUMN_ROUNDNESS_COLOR + " TEXT, " +
            COLUMN_WHITE_DOTS_COUNT + " INTEGER, " +
            COLUMN_WHITE_DOTS_PERCENTAGE + " REAL, " +
            COLUMN_RISK_LEVEL + " TEXT, " +
            COLUMN_DATE + " TEXT" +
            ");";

    public DBhelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Execute the SQL statement to create the users table
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_TREATMENT_PLAN);
        db.execSQL(CREATE_TABLE_PREMIUM_SUBSCRIPTION);
        db.execSQL(CREATE_TABLE_ONGOING_PAYMENT_CARD);
        db.execSQL(CREATE_TABLE_PROFILE_IMAGES);
        db.execSQL(CREATE_TABLE_CHECKUP);
        db.execSQL(CREATE_MEDICAL_HISTORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop the old table and create a new one
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFILE_IMAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TREATMENT_PLAN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PREMIUM_SUBSCRIPTION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ONGOING_PAYMENT_CARD);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHECKUP);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEDICAL_HISTORY);
        onCreate(db);
    }

    public boolean checkUserExists(String username, String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " +
                COLUMN_USERNAME + " = ? OR " + COLUMN_EMAIL + " = ?", new String[]{username, email});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Insert user data into the users table
    public boolean insertUser(String firstName, String lastName, String username, String email, String dob, String password, String userType) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Set the account type based on username
        String accountType = username.startsWith("Admin") ? "Admin" : "Non-premium";

        // Check if username or email already exists
        if (checkUserExists(username, email)) {
            db.close();
            return false; // Return false if user already exists
        }

        // Hash the password before saving it
        String hashedPassword = hashPassword(password);

        ContentValues values = new ContentValues();
        values.put(COLUMN_FIRST_NAME, firstName);
        values.put(COLUMN_LAST_NAME, lastName);
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_DOB, dob);
        values.put(COLUMN_PASSWORD, hashedPassword); // Save the hashed password
        values.put(COLUMN_USER_TYPE, userType);
        values.put(COLUMN_ACCOUNT_TYPE, accountType); // Insert account type

        // Insert the row and return true if insertion is successful
        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1; // If result is -1, the insertion failed
    }

    // Hash password using bcrypt
    public String hashPassword(String password) {
        // Generate a salt (the "work factor" can be increased for more security)
        String salt = BCrypt.gensalt(12); // 12 is the log_rounds (work factor)

        // Hash the password with the salt
        return BCrypt.hashpw(password, salt);
    }

    // Verify password using bcrypt
    public boolean checkPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword); // Check if the entered password matches the hashed password
    }

    // Method to check if the email exists in the database
    public boolean checkUserEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_EMAIL + " = ?", new String[]{email});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    public String getAccountTypeByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String accountType = "Non-premium"; // Default value
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("SELECT " + COLUMN_ACCOUNT_TYPE + " FROM " + TABLE_USERS + " WHERE " + COLUMN_EMAIL + " = ?", new String[]{email});
            if (cursor != null && cursor.moveToFirst()) {
                accountType = cursor.getString(cursor.getColumnIndex(COLUMN_ACCOUNT_TYPE));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return accountType;
    }


    // Method to check if the given password matches the one stored for the given email
    public boolean checkPasswordForUser(String email, String plainPassword) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_EMAIL + " = ?", new String[]{email});
        boolean isValidUser = false;

        if (cursor.moveToFirst()) {
            // Get the stored hashed password from the database
            @SuppressLint("Range") String storedHashedPassword = cursor.getString(cursor.getColumnIndex(COLUMN_PASSWORD));

            // Compare the plain password with the stored hashed password
            isValidUser = BCrypt.checkpw(plainPassword, storedHashedPassword);
        }

        cursor.close();
        db.close();
        return isValidUser;
    }

    // Method to update the password for the given email
    public boolean updatePassword(String email, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Hash the new password before saving it
        String hashedPassword = hashPassword(newPassword);

        ContentValues values = new ContentValues();
        values.put(COLUMN_PASSWORD, hashedPassword); // Update the password

        // Update the password where email = ?
        int rowsUpdated = db.update(TABLE_USERS, values, COLUMN_EMAIL + " = ?", new String[]{email});
        db.close();

        return rowsUpdated > 0; // If rowsUpdated is greater than 0, the password was updated successfully
    }

    // Modify the checkUser method to compare the hashed password
    public boolean checkUser(String usernameOrEmail, String plainPassword) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM users WHERE username = ? OR email = ?";
        Cursor cursor = db.rawQuery(query, new String[]{usernameOrEmail, usernameOrEmail});

        boolean isValidUser = false;
        if (cursor.moveToFirst()) {
            // Get the stored hashed password from the database
            @SuppressLint("Range") String storedHashedPassword = cursor.getString(cursor.getColumnIndex(COLUMN_PASSWORD));

            // Compare the plain password with the stored hashed password
            isValidUser = BCrypt.checkpw(plainPassword, storedHashedPassword);
        }
        cursor.close();
        db.close();
        return isValidUser;
    }

    public String getUsernameByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_USERNAME + " FROM " + TABLE_USERS + " WHERE " + COLUMN_EMAIL + " = ?", new String[]{email});

        String username = null;
        if (cursor.moveToFirst()) {
            username = cursor.getString(cursor.getColumnIndex(COLUMN_USERNAME));
        }

        cursor.close();
        db.close();
        return username;
    }

    // Method to retrieve user data by email
    public String[] getUserDataByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_FIRST_NAME + ", " + COLUMN_LAST_NAME + ", " + COLUMN_USERNAME + ", " + COLUMN_EMAIL + ", " + COLUMN_USER_TYPE + " FROM " + TABLE_USERS + " WHERE " + COLUMN_EMAIL + " = ?", new String[]{email});

        String[] userData = null;
        if (cursor.moveToFirst()) {
            userData = new String[5];
            userData[0] = cursor.getString(cursor.getColumnIndex(COLUMN_FIRST_NAME));
            userData[1] = cursor.getString(cursor.getColumnIndex(COLUMN_LAST_NAME));
            userData[2] = cursor.getString(cursor.getColumnIndex(COLUMN_USERNAME));
            userData[3] = cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL));
            userData[4] = cursor.getString(cursor.getColumnIndex(COLUMN_USER_TYPE));
        }

        cursor.close();
        db.close();
        return userData;
    }

    public boolean updateUserProfile(String email, String firstName, String lastName, String username, String userType) {
        // Get writable database
        SQLiteDatabase db = this.getWritableDatabase();

        // Create a content values object to store the updated data
        ContentValues contentValues = new ContentValues();
        contentValues.put("first_name", firstName); // Match database column names
        contentValues.put("last_name", lastName);  // Match database column names
        contentValues.put("username", username);
        contentValues.put("user_type", userType);

        // Define the where clause (updating the user based on email)
        String whereClause = "email = ?";
        String[] whereArgs = new String[]{email};

        // Perform the update and check if rows are affected (i.e., update success)
        int rowsAffected = db.update("users", contentValues, whereClause, whereArgs);

        // Close the database
        db.close();

        // Return true if rows were updated, otherwise return false
        return rowsAffected > 0;
    }


    // In DBhelper.java
    public int getUserIdByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        int userId = -1;

        try {
            String query = "SELECT " + COLUMN_ID + " FROM " + TABLE_USERS + " WHERE " + COLUMN_EMAIL + " = ?";
            cursor = db.rawQuery(query, new String[]{email});

            if (cursor != null && cursor.moveToFirst()) {
                userId = cursor.getInt(cursor.getColumnIndex(COLUMN_ID)); // Use COLUMN_ID instead of COLUMN_USER_ID
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return userId;
    }



    public boolean deleteUser(String email) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Delete user where email = ?
        int rowsDeleted = db.delete(TABLE_USERS, COLUMN_EMAIL + " = ?", new String[]{email});
        db.close();

        return rowsDeleted > 0; // If rowsDeleted is greater than 0, the data was deleted successfully
    }
    private byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public boolean saveUserImage(int userId, Bitmap bitmap) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_IMAGE, getBitmapAsByteArray(bitmap)); // Convert bitmap to byte array

        // Check if the user already has a profile image
        int rowsUpdated = db.update(TABLE_PROFILE_IMAGES, values, COLUMN_USER_ID + "=?", new String[]{String.valueOf(userId)});

        if (rowsUpdated == 0) {
            // No existing profile image found, so insert a new one
            long result = db.insert(TABLE_PROFILE_IMAGES, null, values);
            db.close();
            return result != -1; // Return true if the image was inserted successfully
        }

        db.close();
        return rowsUpdated > 0; // Return true if the image was updated successfully
    }


    public Bitmap getUserImage(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_IMAGE + " FROM " + TABLE_PROFILE_IMAGES + " WHERE " + COLUMN_USER_ID + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            byte[] imageBytes = cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_IMAGE));
            cursor.close();
            db.close();
            return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        }

        cursor.close();
        db.close();
        return null; // Return null if no image is found
    }

    public long addTreatmentPlan(String title, String date, String time, String taskType, String description, long userId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_TIME, time);
        values.put(COLUMN_TASK_TYPE, taskType);
        values.put(COLUMN_DESCRIPTION, description);
        values.put("user_id", userId);  // Add the user_id to associate the treatment plan with the user

        long result = db.insert(TABLE_TREATMENT_PLAN, null, values);
        db.close();
        return result; // Return the row ID of the newly inserted treatment plan
    }


    public List<String> getTitlesByUserId(int userId) {
        List<String> titles = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            String query = "SELECT " + COLUMN_TITLE + " FROM " + TABLE_TREATMENT_PLAN +
                    " WHERE " + COLUMN_USER_ID + " = ?";
            cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    titles.add(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return titles;
    }

    // In DBhelper.java
    public HashMap<String, String> getDataByTitleAndUserId(String title, int userId) {
        HashMap<String, String> data = new HashMap<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            String query = "SELECT * FROM " + TABLE_TREATMENT_PLAN +
                    " WHERE " + COLUMN_TITLE + " = ? AND " + COLUMN_USER_ID + " = ?";
            cursor = db.rawQuery(query, new String[]{title, String.valueOf(userId)});

            if (cursor != null && cursor.moveToFirst()) {
                data.put(COLUMN_DATE, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)));
                data.put(COLUMN_TIME, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIME)));
                data.put(COLUMN_TASK_TYPE, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TASK_TYPE)));
                data.put(COLUMN_DESCRIPTION, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return data;
    }

    public long updateTreatmentPlanByUserIdAndTitle(String title, String date, String time, String taskType, String description, long userId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_TIME, time);
        values.put(COLUMN_TASK_TYPE, taskType);
        values.put(COLUMN_DESCRIPTION, description);

        // The condition to update based on userId and title
        String selection = "user_id = ? AND title = ?";
        String[] selectionArgs = { String.valueOf(userId), title };

        // Update the treatment plan record in the database
        long result = db.update(TABLE_TREATMENT_PLAN, values, selection, selectionArgs);
        db.close();
        return result; // Return the number of rows affected (or -1 for error)
    }

    public List<Task> getTasksByUserId(long userId) {
        List<Task> taskList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Query to fetch tasks for the given userId
        Cursor cursor = db.query(
                TABLE_TREATMENT_PLAN, // Table name
                null, // Return all columns
                "user_id = ?", // Where clause
                new String[]{String.valueOf(userId)}, // Where clause arguments (userId)
                null, // Group by
                null, // Having
                null // Order by (optional)
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Retrieve data from the cursor
                String title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE));
                String date = cursor.getString(cursor.getColumnIndex(COLUMN_DATE));
                String time = cursor.getString(cursor.getColumnIndex(COLUMN_TIME));
                String taskType = cursor.getString(cursor.getColumnIndex(COLUMN_TASK_TYPE));
                String description = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION));
                long taskId = cursor.getLong(cursor.getColumnIndex(COLUMN_ID)); // Assuming there's an ID column

                // Create a Task object and add it to the list
                Task task = new Task(taskId, title, date, time, taskType, description, userId);
                taskList.add(task);
            } while (cursor.moveToNext());
            cursor.close();
        }

        db.close();
        return taskList;
    }

    public List<TreatmentPlan> getTasksForUserAndDate(int userId, String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<TreatmentPlan> tasks = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_TREATMENT_PLAN +
                " WHERE " + COLUMN_USER_ID + " = ? AND " + COLUMN_DATE + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId), date});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE));
                String time = cursor.getString(cursor.getColumnIndex(COLUMN_TIME));
                String description = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION));
                String taskType = cursor.getString(cursor.getColumnIndex(COLUMN_TASK_TYPE));

                TreatmentPlan task = new TreatmentPlan(title, time, description, taskType);
                tasks.add(task);
            } while (cursor.moveToNext());

            cursor.close();
        }

        return tasks;
    }
    public boolean deleteTask(long taskId) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Delete the task with the given taskId
        int rowsAffected = db.delete(
                TABLE_TREATMENT_PLAN, // Table name
                "id = ?", // Where clause
                new String[]{String.valueOf(taskId)} // Where clause arguments (taskId)
        );

        db.close();

        // If rowsAffected is greater than 0, the delete was successful
        return rowsAffected > 0;
    }

    public boolean insertPremiumSubscription(int userId, String subscriptionType, String cardType, String cardHolderName, String cardNo, String expireDate, String cvv, String paymentDate) {
        // Get writable database
        SQLiteDatabase db = this.getWritableDatabase(); // Initialize db here

        ContentValues contentValues = new ContentValues();
        contentValues.put("userid", userId);
        contentValues.put("Subscription_Type", subscriptionType);
        contentValues.put("CardType", cardType);
        contentValues.put("CardHolder_Name", cardHolderName);
        contentValues.put("Card_No", cardNo);
        contentValues.put("ExpiryDate", expireDate);
        contentValues.put("CVV", cvv);
        contentValues.put("PaymentDate", paymentDate);

        long result = db.insert(TABLE_PREMIUM_SUBSCRIPTION, null, contentValues);
        db.close(); // Close the database after the operation
        return result != -1; // Return true if insertion was successful
    }

    public String getUsernameByUserId(int userId) {
        String username = null;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_USERNAME + " FROM " + TABLE_USERS + " WHERE " + COLUMN_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            username = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return username;
    }

    public String getPaymentDateByUserId(int userId) {
        String paymentDate = null;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT PaymentDate FROM " + TABLE_PREMIUM_SUBSCRIPTION + " WHERE userid = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            paymentDate = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return paymentDate;
    }

    public boolean updateAccountType(int userId, String accountType) {
        // Get writable database
        SQLiteDatabase db = this.getWritableDatabase(); // Initialize db here

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ACCOUNT_TYPE, accountType);

        int result = db.update(TABLE_USERS, contentValues, COLUMN_ID + " = ?", new String[]{String.valueOf(userId)});
        db.close(); // Close the database after the operation
        return result > 0; // Return true if update was successful
    }


    public List<Card> getCardsByUserId(int userId) {
        List<Card> cards = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Update the SQL query to select CardHolder_Name and CVV as well
        String query = "SELECT CardType, Card_No, ExpiryDate, CardHolder_Name, CVV FROM " + TABLE_PREMIUM_SUBSCRIPTION + " WHERE userid = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        while (cursor.moveToNext()) {
            // Get the values for all the columns including cardHolderName and cvv
            String cardType = cursor.getString(0);
            String cardNo = cursor.getString(1);
            String expiryDate = cursor.getString(2);
            String cardHolderName = cursor.getString(3); // Added
            String cvv = cursor.getString(4); // Added

            // Create a new Card object with all the required fields
            cards.add(new Card(cardType, cardNo, expiryDate, cardHolderName, cvv));
        }

        cursor.close();
        return cards;
    }

    public boolean insertCard(int userId, String cardType, String cardHolderName, String cardNo, String expiryDate, String cvv) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("userid", userId);
        values.put("Subscription_Type", "Premium"); // Assuming a default subscription type
        values.put("CardType", cardType);
        values.put("CardHolder_Name", cardHolderName);
        values.put("Card_No", cardNo);
        values.put("ExpiryDate", expiryDate);
        values.put("CVV", cvv);
        values.put("PaymentDate", String.valueOf(System.currentTimeMillis())); // Store current timestamp as PaymentDate

        long result = db.insert(TABLE_PREMIUM_SUBSCRIPTION, null, values);
        db.close();
        return result != -1;
    }

    public boolean insertAlterCard(int userId, String cardType, String cardHolderName, String cardNo, String expiryDate, String cvv) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("userid", userId);
        values.put("Subscription_Type", "Premium"); // Assuming a default subscription type
        values.put("CardType", cardType);
        values.put("CardHolder_Name", cardHolderName);
        values.put("Card_No", cardNo);
        values.put("ExpiryDate", expiryDate);
        values.put("CVV", cvv);

        long result = db.insert(TABLE_PREMIUM_SUBSCRIPTION, null, values);
        db.close();
        return result != -1;
    }
    public boolean updateCardDetails(String cardHolderName, String cardNo, String expiryDate, String cvv, String cardType) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("CardHolder_Name", cardHolderName);
        values.put("Card_No", cardNo);
        values.put("ExpiryDate", expiryDate);
        values.put("CVV", cvv);
        values.put("CardType", cardType);

        // Update the record based on cardNo (or any unique identifier for the card)
        String whereClause = "Card_No = ?";
        String[] whereArgs = {cardNo};

        int rowsAffected = db.update(TABLE_PREMIUM_SUBSCRIPTION, values, whereClause, whereArgs);

        // Return true if rows are updated, otherwise false
        return rowsAffected > 0;
    }

    public String getSubscriptionTypeByUserId(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String subscriptionType = null;
        Cursor cursor = null;

        try {
            String query = "SELECT Subscription_Type FROM " + TABLE_PREMIUM_SUBSCRIPTION + " WHERE userid = ?";
            cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

            if (cursor != null && cursor.moveToFirst()) {
                subscriptionType = cursor.getString(cursor.getColumnIndex("Subscription_Type"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return subscriptionType;
    }

    public boolean updateSubscriptionType(int userId, String newSubscriptionType) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("Subscription_Type", newSubscriptionType);

        int rowsAffected = db.update(TABLE_PREMIUM_SUBSCRIPTION, contentValues, "userid = ?", new String[]{String.valueOf(userId)});

        db.close();
        return rowsAffected > 0; // Returns true if at least one row is updated
    }

    public void setOngoingPaymentCard(int userId, String cardNo) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Clear any existing data in TABLE_ONGOING_PAYMENT_CARD
        db.execSQL("DELETE FROM " + TABLE_ONGOING_PAYMENT_CARD);

        // Insert the new ongoing payment card
        ContentValues values = new ContentValues();
        values.put("userid", userId);
        values.put("Card_No", cardNo);

        db.insert(TABLE_ONGOING_PAYMENT_CARD, null, values);
    }

    public String getDefaultCardForUser(int userId) {
        String defaultCardNo = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ONGOING_PAYMENT_CARD,
                new String[]{"Card_No"},
                "userid=?",
                new String[]{String.valueOf(userId)},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            defaultCardNo = cursor.getString(cursor.getColumnIndex("Card_No"));
            cursor.close();
        }
        db.close();
        return defaultCardNo;
    }

    public List<BillingHistoryItem> getBillingHistoryByUserId(int userId) {
        List<BillingHistoryItem> billingHistory = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Modify the query to only select records where PaymentDate is not null
        Cursor cursor = db.query(
                TABLE_PREMIUM_SUBSCRIPTION,
                new String[]{"PaymentDate", "Card_No", "CardType"},
                "userid=? AND PaymentDate IS NOT NULL",  // Added condition to check if PaymentDate is not null
                new String[]{String.valueOf(userId)},
                null,
                null,
                null
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String paymentDate = cursor.getString(cursor.getColumnIndex("PaymentDate"));
                String cardNo = cursor.getString(cursor.getColumnIndex("Card_No"));
                String cardType = cursor.getString(cursor.getColumnIndex("CardType"));
                billingHistory.add(new BillingHistoryItem(paymentDate, cardNo, cardType));
            }
            cursor.close();
        }
        db.close();

        return billingHistory;
    }
    public boolean insertcheckup(String userid, String Status, String date)
    {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values  = new ContentValues();
        values.put("id",userid);
        values.put("memo_Status",Status);
        values.put("memo_date",date);
        long result = db.insert(TABLE_CHECKUP, null, values);
        db.close();
        if (result != -1)
        {
            return true;
        }else {
            return false;
        }
    }
    public String[] getCheckupDetails(String userId) {
        SQLiteDatabase dbhelper = getReadableDatabase(); // Use getReadableDatabase() if no writes are needed.
        String[] checkupDetails = new String[3];

        // Correct SQL query with proper spacing
        Cursor cursor = dbhelper.rawQuery(
                "SELECT * FROM " + TABLE_CHECKUP + " WHERE " + COLUMN_ID + " = ?",
                new String[]{userId}
        );

        if (cursor.moveToFirst()) {
            try {
                checkupDetails[0] = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGESTATUS));
                checkupDetails[1] = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGEDATE));
            } finally {
                cursor.close(); // Always close the cursor in a finally block
            }
        } else {
            checkupDetails = null; // Return null if no data found
        }

        dbhelper.close();
        return checkupDetails;
    }
    public boolean detelecheckup(String userid){
        SQLiteDatabase db = this.getWritableDatabase();

        // Delete user where email = ?
        int rowsDeleted = db.delete(TABLE_CHECKUP, COLUMN_ID + " = ?", new String[]{userid});
        db.close();
        if(rowsDeleted != -1){
            return true;
        }else{
            return false;
        }

    }

    public boolean insertMedicalHistory(int userId, String result, String roundnessColor, int whiteDotsCount,
                                        double whiteDotsPercentage, String riskLevel, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_RESULT, result);
        values.put(COLUMN_ROUNDNESS_COLOR, roundnessColor);
        values.put(COLUMN_WHITE_DOTS_COUNT, whiteDotsCount);
        values.put(COLUMN_WHITE_DOTS_PERCENTAGE, whiteDotsPercentage);
        values.put(COLUMN_RISK_LEVEL, riskLevel);
        values.put(COLUMN_DATE, date);

        long resultId = db.insert(TABLE_MEDICAL_HISTORY, null, values);
        db.close();

        return resultId != -1;  // Return true if insertion was successful
    }

    public List<MedicalHistory> getMedicalHistoryForUser(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<MedicalHistory> historyList = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_MEDICAL_HISTORY +
                " WHERE " + COLUMN_USER_ID + " = ?" +
                " ORDER BY " + COLUMN_DATE + " DESC";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                MedicalHistory history = new MedicalHistory(
                        cursor.getInt(cursor.getColumnIndex(COLUMN_USER_ID)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_RESULT)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_ROUNDNESS_COLOR)),
                        cursor.getInt(cursor.getColumnIndex(COLUMN_WHITE_DOTS_COUNT)),
                        cursor.getDouble(cursor.getColumnIndex(COLUMN_WHITE_DOTS_PERCENTAGE)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_RISK_LEVEL)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_DATE))
                );
                historyList.add(history);
            } while (cursor.moveToNext());

            cursor.close();
        }
        db.close();
        return historyList;
    }

}
