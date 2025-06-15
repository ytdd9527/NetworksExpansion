package com.ytdd9527.networksexpansion.utils.databases;

public class DataTables {

    public static final String CONTAINER = "Container";
    public static final String ITEM_STACK = "ItemStack";
    public static final String ITEM_STORED = "ItemStored";
    public static final String ENVIRONMENT = "Environment";

    public static final String CONTAINER_CREATION =
            "CREATE TABLE IF NOT EXISTS Container (" + "ContainerID CHAR(8) PRIMARY KEY NOT NULL,"
                    + "PlayerUUID CHAR(36) NOT NULL,"
                    + "SizeType TINYINT NOT NULL,"
                    + "IsPlaced TINYINT NOT NULL,"
                    + "LastLocation VARCHAR(64) NOT NULL);";

    public static final String ITEM_STACK_CREATION =
            "CREATE TABLE IF NOT EXISTS ItemStack (" + "ItemID CHAR(8) PRIMARY KEY NOT NULL," + "Item TEXT NOT NULL);";

    public static final String ITEM_STORED_CREATION =
            "CREATE TABLE IF NOT EXISTS ItemStored (" + "ContainerID CHAR(8) NOT NULL,"
                    + "ItemID CHAR(8) NOT NULL,"
                    + "Amount CHAR(8) NOT NULL);";

    public static final String ENVIRONMENT_CREATION = "CREATE TABLE IF NOT EXISTS Environment ("
            + "VarName CHAR(8) PRIMARY KEY NOT NULL," + "VarValue CHAR(128) NOT NULL);";
}
