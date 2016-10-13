package com.de6elinggmail.todo.db;

/**
 * Created by debeling on 9/14/16.
 */

import android.provider.BaseColumns;

public class TaskContract {
    public static final String DB_NAME = "com.de6elinggmail.todo.db";
    public static final int DB_VERSION = 2;

    public class TaskEntry implements BaseColumns {
        public static final String TABLE = "tasks";

        public static final String COL_TASK_TITLE = "title";
        public static final String COL_TASK_ARCHIVE = "status";
    }
}
