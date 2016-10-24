package aca.com.hris.HelperClass;

import android.app.Activity;

import java.util.HashMap;

import aca.com.hris.Util.UtilDate;

/**
 * Created by Marsel on 30/11/2015.
 */
public class SysAuditLog {

    private Activity activity;
    private int auditArray, emptyAuditArray, start, length;
    private HashMap<String, String> map;

    private String LogDateTime,
            MenuId,
            ActionType,
            TableName,
            PrimaryKeyData,
            LogData,
            UserId,
            HostName;


    public SysAuditLog() {
        LogDateTime = "";
        MenuId = "";
        ActionType = "";
        TableName = "";
        PrimaryKeyData = "";
        LogData = "";
        UserId = "";
        HostName = "";
    }

    public SysAuditLog(String logDateTime, String menuId, String actionType, String tableName, String primaryKeyData, String logData, String userId, String hostName) {
        LogDateTime = logDateTime;
        MenuId = menuId;
        ActionType = actionType;
        TableName = tableName;
        PrimaryKeyData = primaryKeyData;
        LogData = logData;
        UserId = userId;
        HostName = hostName;

    }

    public HashMap<String, String> create(Activity activity,
                         int auditLogArray,
                         int length,
                         HashMap<String, String> map) {

        this.activity = activity;
        this.auditArray = auditLogArray;
//        this.emptyAuditArray = emptyAuditLogArray;
        this.start = start;
        this.length = length;
        this.map = map;


        LogData =  createEmptyLog() + createAuditLog();
        fill();

        return map;
    }


    public String createAuditLog() {
        String[] params = activity.getResources().getStringArray(auditArray);
        String key = "";
        String value = "";
        String hashString = "";
        String logData = "";

        for (int i = length; i < params.length; i++) {
            key = params[i];
            value = map.get(key);
            hashString = "[" + key + "|" + value + "]";

            logData += hashString;
        }

        return logData;
    }

    public String createEmptyLog() {
        String[] params = activity.getResources().getStringArray(auditArray);
        String key = "";
        String value = "";
        String hashString = "";
        String logData = "";

        for (int i = 0; i < length; i++) {
            key = params[i];
            value = "#" + key + "#";
            hashString = "[" + key + "|" + value + "]";

            logData += hashString;
        }

        return logData;
    }

    public String update(Activity activity, int arrayName, int start, HashMap<String, String> map) {
        String[] params = activity.getResources().getStringArray(arrayName);
        String key = "";
        String value = "";
        String hashString = "";
        String logData = "";

        for (int i = start; i < params.length; i++) {
            key = params[i];
            value = map.get(key);
            hashString = "[" + key + "|" + value + "]";

            logData += hashString;
        }

        return logData;
    }

    public void fill() {
        map.put("sLogDateTime", LogDateTime);
        map.put("sMenuId", MenuId);
        map.put("sActionType", ActionType);
        map.put("sTableName", TableName);
        map.put("sPrimaryKeyData", PrimaryKeyData);
        map.put("sLogData", LogData);
        map.put("sUserId", UserId);
        map.put("sHostName", HostName);
    }

    public String getLogDateTime() {
        return LogDateTime;
    }

    public void setLogDateTime(String logDateTime) {
        LogDateTime = logDateTime;
    }

    public String getMenuId() {
        return MenuId;
    }

    public void setMenuId(String menuId) {
        MenuId = menuId;
    }

    public String getActionType() {
        return ActionType;
    }

    public void setActionType(String actionType) {
        ActionType = actionType;
    }

    public String getTableName() {
        return TableName;
    }

    public void setTableName(String tableName) {
        TableName = tableName;
    }

    public String getPrimaryKeyData() {
        return PrimaryKeyData;
    }

    public void setPrimaryKeyData(String primaryKeyData) {
        PrimaryKeyData = primaryKeyData;
    }

    public String getLogData() {
        return LogData;
    }

    public void setLogData(String logData) {
        LogData = logData;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getHostName() {
        return HostName;
    }

    public void setHostName(String hostName) {
        HostName = hostName;
    }
}
